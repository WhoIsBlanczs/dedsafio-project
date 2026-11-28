#version 150

// Código generado por Claude (Anthropic).
// El tornado como HUMO volumétrico de verdad (igual que la estela de los meteoritos): un volumen que se recorre con un rayo por cada píxel.
//  - el humo GIRA alrededor del eje: el núcleo va más rápido que el borde y abajo más rápido que arriba, así se enroscan brazos en espiral;
//  - bultos como coliflor + remolinos + detalle fino que se deshace en hilachas por los bordes, y todo sube mientras gira;
//  - abajo, una falda de polvo ancha y más clara que arranca del suelo; arriba se abre muy ancho y se funde con la nube pared del cielo
//    (tormenta_cielo.fsh baja su base justo hasta la punta del embudo);
//  - mismos colores que el cielo de la tormenta (pizarra azulada, panza oscura, tapa más clara), así no desentona;
//  - barato: pocos pasos, el rayo solo recorre el tramo que atraviesa el cuerpo, y el humo sigue con textura aunque estés dentro.
//
// Base.xyz = dónde está la base del tornado respecto a la cámara, Base.w = altura.
// Datos.x = radio, Datos.yz = hacia dónde avanza (normalizado, 0 si está quieto), Datos.w = identificador.
// Capa.x = opacidad general, Capa.w = densidad de niebla por bloque.

uniform float Tiempo;
uniform vec3 Niebla;
uniform vec4 Capa;
uniform vec4 Base;
uniform vec4 Datos;

in vec3 wp;
in float dist;
out vec4 fragColor;

const int PASOS_MAX = 64;
const float ESCALA = 1.6;   // lo que la envoltura que dibuja el cliente excede al embudo

// Ruido de valor 3D. La coordenada z (la de la altura/tiempo) es PERIÓDICA (32 celdas): así los números del hash nunca crecen aunque
// pase una hora de juego (con el tiempo creciendo sin parar, el hash perdía precisión y el humo se llenaba de rayas y píxeles).
float hash13(vec3 p) {
    p = fract(p * 0.3183099 + vec3(0.1, 0.2, 0.3));
    p *= 17.0;
    return fract(p.x * p.y * p.z * (p.x + p.y + p.z));
}

float ruido(vec3 x) {
    x.z = mod(x.z, 32.0);
    vec3 i = floor(x);
    vec3 f = fract(x);
    f = f * f * (3.0 - 2.0 * f);
    float z1 = mod(i.z + 1.0, 32.0);
    vec3 i0 = vec3(i.xy, i.z);
    vec3 i1 = vec3(i.xy, z1);
    return mix(mix(mix(hash13(i0), hash13(i0 + vec3(1, 0, 0)), f.x),
                   mix(hash13(i0 + vec3(0, 1, 0)), hash13(i0 + vec3(1, 1, 0)), f.x), f.y),
               mix(mix(hash13(i1), hash13(i1 + vec3(1, 0, 0)), f.x),
                   mix(hash13(i1 + vec3(0, 1, 0)), hash13(i1 + vec3(1, 1, 0)), f.x), f.y), f.z);
}

// fase de un giro (en radianes, siempre entre 0 y 2π): los senos y cosenos de ángulos enormes (una hora de juego son miles de radianes)
// pierden precisión en la GPU y estropean el humo; envueltos a una vuelta son exactos y el movimiento es idéntico
float fase(float vel, float desfase) {
    return mod(Tiempo * vel + desfase, 6.2831853);
}

// el mismo perfil que dibuja el cliente: un CONO INVERTIDO (punta fina en el suelo, se ensancha en línea recta y se abre arriba), con algo de polvo en la base
float radioEn(float v) {
    float R = Datos.x;
    return R * (0.06 + 1.55 * v + 1.10 * pow(v, 5.0) + 0.30 * exp(-v * 18.0));
}

// y la misma curva del eje (ondula y se echa hacia atrás al subir)
vec2 ejeEn(float v) {
    float R = Datos.x;
    float id = Datos.w;
    float v15 = pow(v, 1.5);
    float ox = sin(v * 3.1 + fase(0.55, id * 1.7)) * R * 0.30 * v15 + sin(v * 7.0 - fase(1.1, 0.0)) * R * 0.06 * v;
    float oz = cos(v * 2.7 + fase(0.47, id * 2.3)) * R * 0.30 * v15 + cos(v * 6.0 - fase(0.9, 0.0)) * R * 0.06 * v;
    float atras = Base.w * 0.14 * pow(v, 1.3);
    ox -= Datos.y * atras;
    oz -= Datos.z * atras;
    return vec2(ox, oz);
}

// densidad de humo (0..1) en un punto q respecto a la base; fino = con el detalle pequeño (hilachas)
float gT = 0.0;   // distancia de la cámara al punto que se mide (para quitar detalle fino a lo lejos)
float gN = 0.5;   // el ruido del último punto medido (para dar relieve al color)
float densidad(vec3 q, bool fino) {
    float v = q.y / Base.w;
    if (v < 0.0 || v > 1.0) {
        return 0.0;
    }
    vec2 c = q.xz - ejeEn(v);
    float rad = radioEn(v);
    float s = length(c) / rad;
    if (s > 1.8) {
        return 0.0;
    }
    float id = Datos.w;
    // GIRO: el núcleo gira más rápido que el borde (cizalla), abajo más rápido que arriba, y las capas se enroscan al subir.
    // La cizalla estira el humo en brazos que se enroscan en espiral.
    // (la cizalla está ACOTADA: si el giro del núcleo y el del borde se fueran separando sin parar, con el tiempo el humo se estiraría
    //  en hilos finísimos, que es lo que se veía como líneas y píxeles. Ahora todo gira junto y la diferencia solo "respira".)
    float dif = 1.4 - min(s, 1.4);
    float ang = fase(0.80, id) + v * 5.0 + dif * (1.5 + 1.1 * sin(fase(0.21, id)));
    float cs = cos(ang);
    float sn = sin(ang);
    vec2 cr = vec2(c.x * cs - c.y * sn, c.x * sn + c.y * cs) / rad;
    // el humo sube mientras gira
    float yy = q.y / (Datos.x * 0.65) - Tiempo * 0.45;
    // bultos grandes redondos (coliflor) y remolinos que los deforman
    float lump = ruido(vec3(cr * 1.5, yy * 0.60 + id)) * 0.65 + 0.35 * ruido(vec3(cr * 3.1 + 5.0, yy * 1.2 + id));
    vec3 pw = vec3(cr * 2.2, yy * 1.2);
    vec2 w = vec2(ruido(pw + 3.1), ruido(pw + 7.7)) - 0.5;
    float n = ruido(vec3(cr * 2.6 + w * 1.5, yy * 1.3));
    if (fino) {
        // el detalle fino se va quitando con la distancia: lo lejano sale suave (nada de granos) y lo cercano conserva hilachas
        float w2 = 1.0 - smoothstep(120.0, 420.0, gT);
        float w3 = 1.0 - smoothstep(40.0, 150.0, gT);
        float n2 = ruido(vec3(cr * 5.4 + w * 1.2, yy * 2.6 - Tiempo * 0.2));
        float n3 = ruido(vec3(cr * 10.5, yy * 4.8 - Tiempo * 0.4));
        n = n * (0.58 + 0.30 * (1.0 - w2) + 0.12 * (1.0 - w3)) + 0.30 * w2 * n2 + 0.12 * w3 * n3;
    }
    gN = n;
    // brazos en espiral: bandas claras y oscuras que siguen el giro
    float th = atan(c.y, c.x);
    float brazo = 0.5 + 0.5 * sin(th * 3.0 + v * 17.0 - fase(2.2, 0.0) + n * 2.6);
    float s2 = s / (0.58 + 0.85 * lump);
    // el borde se deshace en hilachas; más cuanto más arriba (se funde con la nube)
    float borde = s2 + (n - 0.5) * (1.45 + 0.5 * v) - (brazo - 0.5) * 0.22;
    float d = 1.0 - smoothstep(0.20, 1.15, borde);   // caída larga y suave: bordes de humo, no de recorte
    d *= 0.20 + 1.35 * smoothstep(0.15, 0.85, n);
    // se apaga del todo ANTES de llegar a la envoltura de geometría: así no se ve ningún borde recto (el cono)
    d *= 1.0 - smoothstep(0.80, 1.45, s);   // contraste: bultos densos y huecos casi vacíos, así dentro se ve el humo
    // abajo: la falda de polvo (menos densa, algo más alta y suelta); arriba: la nube pared espesa
    d *= mix(0.55, 1.0, smoothstep(0.0, 0.14, v)) * (1.0 + 0.9 * smoothstep(0.65, 0.95, v));
    // se funde en el suelo y, arriba del todo, con la base de las nubes del cielo
    d *= smoothstep(0.0, 0.02, v) * (1.0 - smoothstep(0.72, 0.99, v));
    return clamp(d, 0.0, 1.0);
}

void main() {
    vec3 o = -Base.xyz;                 // la cámara respecto a la base
    vec3 d = normalize(wp);
    float tw = length(wp);
    vec3 qf = o + wp;                   // este punto de la envoltura respecto a la base
    float vf = clamp(qf.y / Base.w, 0.0, 1.0);

    // ¿la cámara está dentro de la envoltura? (entonces se ve la cara de atrás; si no, la de delante)
    float vc = clamp(o.y / Base.w, 0.0, 1.0);
    bool dentro = length(o.xz - ejeEn(vc)) < radioEn(vc) * ESCALA * 0.985 && o.y > 0.0 && o.y < Base.w;
    vec2 fuera = qf.xz - ejeEn(vf);
    bool cerca = dot(d.xz, fuera) < 0.0;
    if (dentro == cerca) {
        discard;
    }

    // el rayo solo recorre el tramo que atraviesa el cuerpo (un cilindro alrededor del eje, a la altura de este punto)
    float t0 = dentro ? 0.0 : tw;
    float t1 = dentro ? tw : tw + radioEn(vf) * ESCALA * 3.0;
    float Rc = radioEn(min(vf + 0.30, 1.0)) * ESCALA + 1.0;   // (generoso: si se queda corto, el rayo se corta y salen bordes rectos)
    vec2 oc = o.xz - ejeEn(vf);
    float a = dot(d.xz, d.xz);
    float b = dot(oc, d.xz);
    float cc = dot(oc, oc) - Rc * Rc;
    if (a > 1e-6) {
        float disc = b * b - a * cc;
        if (disc <= 0.0) {
            discard;
        }
        float sq = sqrt(disc);
        t0 = max(t0, (-b - sq) / a);
        t1 = min(t1, (-b + sq) / a);
    } else if (cc > 0.0) {
        discard;
    }
    if (t1 <= t0) {
        discard;
    }

    // pasos pequeños cerca de la cámara (donde se nota cada capa) y más grandes lejos (donde un píxel abarca mucho humo)
    float jit = fract(52.9829189 * fract(dot(gl_FragCoord.xy, vec2(0.06711056, 0.00583715))));
    int maxPasos = dist > 260.0 ? 36 : PASOS_MAX;
    float t = t0 + (1.2 + 0.04 * t0) * (0.05 + 0.30 * jit);

    vec3 luzDir = normalize(vec3(0.25, 0.92, 0.18));
    // los colores del cielo de la tormenta
    vec3 oscuro = vec3(0.085, 0.120, 0.165);
    vec3 claro = vec3(0.50, 0.61, 0.73);
    vec3 polvoC = vec3(0.34, 0.31, 0.29);      // el polvo que levanta del suelo (gris pardo)
    float K = 6.0 / max(Datos.x * 0.6, 8.0);

    vec3 col = vec3(0.0);
    float T = 1.0;
    for (int i = 0; i < PASOS_MAX; i++) {
        if (i >= maxPasos || t >= t1) {
            break;
        }
        float dt = clamp(1.2 + 0.04 * t, 1.4, 6.0);
        vec3 q = o + d * t;
        gT = t;
        float dn = densidad(q, true);
        float relieve = gN;   // el ruido de este punto (la sombra de abajo vuelve a llamar a densidad y lo cambia)
        if (dn > 0.002) {
            float v = clamp(q.y / Base.w, 0.0, 1.0);
            float rad = radioEn(v);
            // cuánto humo queda hacia la luz (un solo punto): sombra propia, panza oscura
            float sombra = densidad(q + luzDir * rad * 0.7, false);
            float luz = exp(-sombra * 2.0);
            // el borde (poca densidad) deja pasar la luz; el centro, denso, se ve oscuro
            float ilum = clamp(luz * (0.05 + 0.32 * v) + 0.04 + 0.16 * (1.0 - dn) * luz + 0.22 * (relieve - 0.45), 0.0, 1.0);
            // arriba, el humo es la panza oscura de la nube (igual que la base del cielo), no más claro
            ilum *= 1.0 - 0.75 * smoothstep(0.45, 0.95, v);
            vec3 c = mix(oscuro, claro, ilum);
            // el polvo de abajo, más claro y pardo
            c = mix(c, polvoC * (0.7 + 0.6 * luz), (1.0 - smoothstep(0.0, 0.16, v)) * 0.65);
            c = mix(c, oscuro * 1.05, smoothstep(0.60, 0.95, v) * 0.65);
            float al = 1.0 - exp(-dn * K * min(dt, t1 - t + 0.01));
            col += T * al * c;
            T *= 1.0 - al;
            if (T < 0.04) {
                break;
            }
        }
        t += dt;
    }

    float alfa = (1.0 - T) * Capa.x;
    if (alfa < 0.004) {
        discard;
    }
    vec3 rgb = col / max(1.0 - T, 1e-3);
    float f = 1.0 - exp(-dist * Capa.w);
    rgb = mix(rgb, Niebla * 0.8, clamp(f, 0.0, 0.85));
    fragColor = vec4(rgb, clamp(alfa, 0.0, 1.0));
}
