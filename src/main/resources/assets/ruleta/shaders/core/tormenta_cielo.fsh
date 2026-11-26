#version 150

// Código generado por Claude (Anthropic).
// Cielo de tormenta VOLUMÉTRICO: las nubes no son una imagen pegada, son una capa de verdad en el mundo (una losa de nube
// de unos 170 bloques de grosor a cierta altura) que se atraviesa con rayos desde la cámara: cada rayo da 12 pasos por la
// losa, mide la densidad de la nube en 3D, cuánta luz le llega (sombra hacia arriba) y cuánta nube hay por delante.
// Por eso tienen paralaje (si te mueves, las nubes cambian de perspectiva), se ven más gruesas y oscuras por debajo, se
// funden con la niebla a lo lejos, y sobre el tornado la base baja hasta tocar la punta del embudo y gira (nube pared).
// Nivel: opacidad de este cielo (entra y sale suave). Rayo: destello. Niebla: color del horizonte (igual que la niebla del mundo).
// CamPos: posición de la cámara. Losa: (altura de la base de la nube, grosor). Torn: (x, z, radio, fuerza) del tornado más cercano.

uniform float Tiempo;
uniform float Nivel;
uniform float Rayo;
uniform vec3 Niebla;
uniform vec3 CamPos;
uniform vec2 Losa;
uniform vec4 Torn;

in vec3 direccion;
out vec4 fragColor;

float hash3(vec3 p) {
    p = fract(p * 0.3183099 + 0.1);
    p *= 17.0;
    return fract(p.x * p.y * p.z * (p.x + p.y + p.z));
}

float ruido3(vec3 x) {
    vec3 i = floor(x);
    vec3 f = fract(x);
    f = f * f * (3.0 - 2.0 * f);
    return mix(mix(mix(hash3(i), hash3(i + vec3(1, 0, 0)), f.x),
                   mix(hash3(i + vec3(0, 1, 0)), hash3(i + vec3(1, 1, 0)), f.x), f.y),
               mix(mix(hash3(i + vec3(0, 0, 1)), hash3(i + vec3(1, 0, 1)), f.x),
                   mix(hash3(i + vec3(0, 1, 1)), hash3(i + vec3(1, 1, 1)), f.x), f.y), f.z);
}

float fbm3(vec3 p, int octavas) {
    float v = 0.0;
    float a = 0.5;
    for (int i = 0; i < 5; i++) {
        if (i >= octavas) break;
        v += a * ruido3(p);
        p = p * 2.07 + vec3(11.7, 3.1, 5.9);
        a *= 0.5;
    }
    return v;
}

// densidad de nube en el punto p del mundo (0 = aire, 1 = nube cerrada)
float densidad(vec3 p, int octavas) {
    float base = Losa.x;
    vec2 pr = p.xz - Torn.xy;
    float r = length(pr);
    // cerca del tornado: la base baja, todo gira alrededor del eje y la nube se espesa
    float w = Torn.w * exp(-r / (Torn.z * 3.5 + 60.0));
    // sin remolino: las nubes siguen su camino y solo baja y se espesa la masa que hay sobre el embudo
    vec2 xz = p.xz;
    base -= w * 70.0;

    float h = (p.y - base) / Losa.y;
    if (h < 0.0 || h > 1.15) {
        return 0.0;
    }

    // forma vertical: la base se corta de golpe (plana y oscura) y arriba se deshace en bultos
    float perfil = smoothstep(0.0, 0.10, h) * (1.0 - smoothstep(0.55, 1.1, h));
    vec3 q3 = vec3(xz.x, p.y, xz.y) * 0.0042 + vec3(Tiempo * 0.012, 0.0, Tiempo * 0.006);
    float n = fbm3(q3, octavas);
    // los bultos grandes (cobertura) y el detalle
    float cobertura = 0.40 - w * 0.12;
    float d = smoothstep(cobertura, cobertura + 0.22, n * 0.85 + (perfil - 0.5) * 0.35);
    d *= perfil;
    // la masa de nube que cuelga sobre el tornado (el embudo sale de ella): una cúpula oscura, redonda y espesa
    float cupula = Torn.w * exp(-(r * r) / (Torn.z * Torn.z * 5.0 + 1.0)) * smoothstep(-0.15, 0.25, h) * (1.0 - smoothstep(0.9, 1.15, h));
    d = max(d, cupula * smoothstep(0.30, 0.62, n + 0.25));
    return d;
}

void main() {
    vec3 d = normalize(direccion);
    float alto = d.y;

    // fondo: gris pizarra, más claro en el horizonte (por donde se cuela la luz)
    vec3 cenit = vec3(0.10, 0.145, 0.21);
    vec3 fondo = mix(Niebla * 0.95, cenit, smoothstep(0.0, 0.45, alto));
    vec3 col = fondo;

    if (alto > 0.012) {
        float t0 = (Losa.x - 70.0 - CamPos.y) / alto;
        float t1 = (Losa.x + Losa.y * 1.15 - CamPos.y) / alto;
        t0 = max(t0, 0.0);
        t1 = min(t1, 6500.0);
        if (t1 > t0) {
            const int PASOS = 12;
            float dt = (t1 - t0) / float(PASOS);
            // un poco de "ruido azul" en el inicio del rayo: sin bandas visibles entre pasos
            float j = fract(52.9829189 * fract(dot(gl_FragCoord.xy, vec2(0.06711056, 0.00583715))));
            float t = t0 + dt * j;
            float T = 1.0;
            vec3 acum = vec3(0.0);
            vec3 luzDir = normalize(vec3(0.25, 0.92, 0.18));
            for (int i = 0; i < PASOS; i++) {
                vec3 p = CamPos + d * t;
                float dens = densidad(p, 4);
                if (dens > 0.01) {
                    // cuánta luz llega aquí: mirar la nube que queda hacia arriba (hacia la luz)
                    float sombra = densidad(p + luzDir * (Losa.y * 0.30), 2) + densidad(p + luzDir * (Losa.y * 0.65), 2) * 0.7;
                    float luz = exp(-sombra * 2.4);
                    float h = clamp((p.y - Losa.x) / Losa.y, 0.0, 1.0);
                    // tapa clara (azulada) arriba, panza oscura abajo; con el tornado, aún más negro
                    vec3 oscuro = vec3(0.095, 0.135, 0.185);
                    vec3 claro = vec3(0.46, 0.57, 0.69);
                    vec3 S = mix(oscuro, claro, clamp(luz * (0.30 + 0.80 * h), 0.0, 1.0));
                    S = mix(S, vec3(0.20, 0.27, 0.35), Torn.w * 0.20);
                    // el borde de la nube deja pasar luz (se ve más claro en las partes finas)
                    S += vec3(0.10, 0.11, 0.13) * (1.0 - dens) * luz * 0.6;
                    float a = 1.0 - exp(-dens * dt * 0.050);
                    acum += T * a * S;
                    T *= 1.0 - a;
                    if (T < 0.03) break;
                }
                t += dt;
            }

            // lo lejano se funde con la niebla del mundo
            float lejos = 1.0 - exp(-t0 * 0.00030);
            vec3 nubes = mix(acum, Niebla * (1.0 - T), clamp(lejos, 0.0, 0.9));
            col = fondo * T + nubes;
            // relámpagos: iluminan la nube por dentro
            col += Rayo * vec3(0.62, 0.68, 0.85) * (1.0 - T) * 0.9;
        }
    }

    // el horizonte se funde con la niebla
    col = mix(col, Niebla, smoothstep(0.14, -0.02, alto) * 0.9);
    fragColor = vec4(col, clamp(Nivel, 0.0, 1.0));
}
