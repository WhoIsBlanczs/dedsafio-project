#version 150

// Código generado por Claude (Anthropic).
// Los misiles de los Phora. Modo:
//  0 roca: bola de roca volcánica oscura con grietas de magma que brillan y laten (vColor.rgb = normal de la esfera)
//  1 resplandor: halo de fuego alrededor de la roca (se suma)
//  2 estela de fuego: blanca pegada a la roca, luego amarilla, naranja y roja (se suma); vUV.x = 0 en la roca, 1 en la cola
//  3 estela de humo: humo oscuro que queda detrás (se mezcla)
//  4 polvo: bocanadas del hongo de polvo (vColor.r = tono, vColor.g = semilla, vColor.b = brillo de fuego, vColor.a = cuánto queda)
//  5 color plano (destello de pantalla)
//  6 llama que envuelve la roca: como un cometa, cabeza casi blanca y lenguas de fuego naranja que se van hacia atrás
//  7 onda expansiva: anillo de polvo y aire que corre por el suelo (vUV.y = 0 dentro .. 1 fuera del anillo)
//  8 bola de luz del impacto
//  9 HONGO VOLUMÉTRICO (como el humo de los meteoritos): tallo, sombrero en anillo y falda de polvo calculados por píxel con
//    raymarching dentro de una caja. HongoA = (centro rel. cámara xyz, escala), HongoB = (edad, alto, radio del anillo, grosor del tubo),
//    HongoC = (opacidad, fuego, semilla, mitad del ancho de la caja)

uniform float Modo;
uniform float Tiempo;
uniform float Semilla;
uniform vec4 HongoA;
uniform vec4 HongoB;
uniform vec4 HongoC;

in vec4 vColor;
in vec2 vUV;
in vec3 vPos;
out vec4 fragColor;

float hash(vec3 p) {
    p = fract(p * 0.3183099 + 0.1);
    p *= 17.0;
    return fract(p.x * p.y * p.z * (p.x + p.y + p.z));
}

float ruido(vec3 x) {
    vec3 i = floor(x);
    vec3 f = fract(x);
    f = f * f * (3.0 - 2.0 * f);
    return mix(mix(mix(hash(i), hash(i + vec3(1, 0, 0)), f.x), mix(hash(i + vec3(0, 1, 0)), hash(i + vec3(1, 1, 0)), f.x), f.y),
               mix(mix(hash(i + vec3(0, 0, 1)), hash(i + vec3(1, 0, 1)), f.x), mix(hash(i + vec3(0, 1, 1)), hash(i + vec3(1, 1, 1)), f.x), f.y), f.z);
}

float fbm(vec3 p) {
    float a = 0.5, s = 0.0;
    for (int i = 0; i < 5; i++) {
        s += a * ruido(p);
        p = p * 2.07 + vec3(5.3, 9.1, 2.7);
        a *= 0.5;
    }
    return s;
}


float fbmH(vec3 p, int oct) {
    float a = 0.5, s = 0.0;
    for (int i = 0; i < 4; i++) {
        if (i >= oct) { break; }
        s += a * ruido(p);
        p = p * 2.03 + vec3(5.3, 9.1, 2.7);
        a *= 0.5;
    }
    return s;
}

float suaveMin(float a, float b, float k) {
    float h = clamp(0.5 + 0.5 * (b - a) / k, 0.0, 1.0);
    return mix(b, a, h) - k * h * (1.0 - h);
}

// Densidad del hongo en el punto p (relativo al impacto; y = 0 es el suelo). Devuelve la densidad en x y el calor en y.
vec2 densidadHongo(vec3 p, int oct) {
    float e = HongoB.x;
    float H = HongoB.y;
    float R = HongoB.z;
    float tubo = HongoB.w;
    float s = HongoA.w;
    float semi = HongoC.z;
    float rt = tubo * 2.0;
    // el humo gira despacio alrededor del eje, más rápido arriba
    float giro = e * 0.10 + p.y / max(H, 1.0) * 0.8;
    float cg = cos(giro);
    float sg = sin(giro);
    vec3 q = vec3(p.x * cg - p.z * sg, p.y, p.x * sg + p.z * cg);
    float rho = length(q.xz);
    float yn = clamp(q.y / max(H, 1.0), 0.0, 1.0);

    // tallo: más fino en medio, abre un poco al llegar al sombrero
    float rs = tubo * (1.0 - 0.22 * sin(3.14159 * yn)) * (1.0 + 0.45 * smoothstep(0.75, 1.0, yn)) * (1.0 + 0.7 * (1.0 - smoothstep(0.0, 0.22, yn)));
    float sdT = max(rho - rs, max(-q.y - 3.0 * s, q.y - H));
    // sombrero: anillo (toro) aplastado + relleno que lo tapa por arriba
    vec2 qt = vec2(rho - R, (q.y - H) / 0.7);
    float sdA = (length(qt) - rt) * 0.6;
    vec3 el = q / vec3(R * 0.95, rt * 0.85, R * 0.95) - vec3(0.0, (H + rt * 0.2) / (rt * 0.85), 0.0);
    float sdE = (length(el) - 1.0) * rt * 0.6;
    // falda de polvo pegada al suelo
    float suelo = 6.0 * s + R * 0.9 * (1.0 - exp(-e / 2.2));
    float hs = max(2.5 * s, tubo * 0.55);
    float sdK = (length(q / vec3(suelo, hs, suelo)) - 1.0) * hs;

    float sd = suaveMin(sdT, min(sdA, sdE), tubo * 0.7);
    sd = min(sd, sdK);

    // bultos de coliflor: el borde nunca es liso
    vec3 pn = q / max(tubo, 1.0) * 0.95 + vec3(semi, semi * 0.7, -semi) + vec3(0.0, -e * 0.12, 0.0);
    float n1 = fbmH(pn, oct);
    float n2 = oct > 2 ? fbmH(pn * 2.6 + 7.0, 2) : 0.5;
    sd += (0.52 - n1) * tubo * 1.4 + (0.5 - n2) * tubo * 0.35;

    float dens = 1.0 - smoothstep(-0.40 * tubo, 0.30 * tubo, sd);
    dens *= 0.35 + 1.3 * n1;
    dens *= smoothstep(-1.5 * s, 1.0 * s, p.y);        // nada bajo el suelo
    // calor: abajo del tallo y en la panza del sombrero, mientras dure el fuego
    float fuego = HongoC.y;
    float cal = fuego * (exp(-max(q.y, 0.0) / (0.55 * H + 1.0)) * 0.8 + 0.5 * smoothstep(0.0, 0.8 * rt, -qt.y * 0.5) * step(q.y, H));
    return vec2(dens, clamp(cal, 0.0, 1.0));
}

void hongoVolumen() {
    vec3 C = HongoA.xyz;
    float W = HongoC.w;
    float top = HongoB.y + HongoB.w * 4.0 + 6.0;
    vec3 d = normalize(vPos);
    float tw = length(vPos);
    // caja [-W, W] x [-2, top] x [-W, W] alrededor del impacto, en coordenadas relativas a la cámara
    vec3 bmin = C + vec3(-W, -2.0, -W);
    vec3 bmax = C + vec3(W, top, W);
    vec3 dd = mix(d, vec3(1e-5), lessThan(abs(d), vec3(1e-5)));
    vec3 inv = 1.0 / dd;
    vec3 t0 = (bmin) * inv;
    vec3 t1 = (bmax) * inv;
    vec3 tmn = min(t0, t1);
    vec3 tmx = max(t0, t1);
    float tin = max(max(tmn.x, tmn.y), tmn.z);
    float tout = min(min(tmx.x, tmx.y), tmx.z);
    if (tout <= max(tin, 0.0)) { discard; }
    // cada rayo se calcula una sola vez: en la cara por la que entra (o, si la cámara está dentro de la caja, en la de salida)
    float tol = 0.02 * tout + 0.5;
    if (tin < 0.0) {
        if (abs(tw - tout) > tol) { discard; }
        tin = 0.0;
    } else if (tw > tin + tol) { discard; }

    const int PAS = 22;
    float dt = (tout - tin) / float(PAS);
    float jit = fract(52.9829189 * fract(dot(gl_FragCoord.xy, vec2(0.06711056, 0.00583715))));
    float t = tin + dt * jit;
    vec3 luz = normalize(vec3(-0.35, 0.85, 0.45));
    float tubo = HongoB.w;
    float sigma = 2.2 / max(tubo, 1.0);
    float T = 1.0;
    vec3 col = vec3(0.0);
    vec3 claro = vec3(1.0, 0.91, 0.72);
    vec3 oscuro = vec3(0.30, 0.19, 0.11);
    for (int i = 0; i < PAS; i++) {
        vec3 p = d * t - C;
        vec2 ds = densidadHongo(p, 3);
        if (ds.x > 0.01) {
            // sombra: cuánto humo hay entre este punto y el sol
            float dl = densidadHongo(p + luz * tubo * 1.3, 2).x;
            float sombra = exp(-dl * 2.8);
            float a = 1.0 - exp(-ds.x * sigma * dt);
            vec3 c = mix(oscuro, claro, 0.18 + 0.82 * sombra) * (0.8 + 0.25 * ds.x);
            // el fuego del impacto lo ilumina por dentro
            vec3 brasa = mix(vec3(1.0, 0.45, 0.12), vec3(1.0, 0.85, 0.5), ds.y);
            c = mix(c, brasa * 1.35, clamp(ds.y * 1.2, 0.0, 0.95));
            col += T * a * c;
            T *= 1.0 - a;
            if (T < 0.02) { break; }
        }
        t += dt;
    }
    float alfa = (1.0 - T) * HongoC.x;
    if (alfa < 0.004) { discard; }
    fragColor = vec4(col / max(1.0 - T, 1e-3) + (jit - 0.5) / 255.0, alfa);
}

void main() {
    float T = Tiempo;
    if (Modo > 8.5) {
        hongoVolumen();
        return;
    }
    if (Modo < 0.5) {
        vec3 n = normalize(vColor.rgb * 2.0 - 1.0);
        vec3 p = n * 2.3 + vec3(Semilla);
        // relieve de la roca
        float roca = fbm(p * 1.4);
        // grietas: líneas finas donde el ruido cruza por la mitad (como placas de lava enfriada)
        float r1 = 1.0 - abs(fbm(p * 1.1 + 3.0) * 2.0 - 1.0);
        float r2 = 1.0 - abs(fbm(p * 2.3 + 8.0) * 2.0 - 1.0);
        float grieta = pow(r1, 14.0) * 1.2 + pow(r2, 22.0) * 0.8;
        float late = 0.75 + 0.25 * sin(T * 6.0 + roca * 9.0);
        vec3 basalto = mix(vec3(0.07, 0.045, 0.04), vec3(0.22, 0.12, 0.08), smoothstep(0.35, 0.75, roca));
        // por la cara que va delante (la que roza el aire) la roca está más caliente
        float calor = 0.35 + 0.25 * roca;
        vec3 magma = mix(vec3(1.0, 0.32, 0.04), vec3(1.0, 0.85, 0.45), clamp(grieta - 0.6, 0.0, 1.0));
        vec3 col = basalto + vec3(0.9, 0.25, 0.04) * calor * 0.35;
        col = mix(col, magma * 1.6, clamp(grieta * late, 0.0, 1.0));
        // borde encendido (la roca brilla por los lados)
        float borde = pow(1.0 - clamp(abs(vUV.y * 2.0 - 1.0), 0.0, 1.0), 0.5);
        fragColor = vec4(min(col, vec3(1.0)), 1.0);
        return;
    }

    vec2 c = vUV * 2.0 - 1.0;
    // nada llega al borde del cuadrado: así no se ve nunca la caja
    float sinCaja = smoothstep(1.0, 0.8, length(c));
    if (Modo < 1.5) {
        // la cabeza del misil: un punto de luz blanca cegadora con resplandor amarillo (como en el video, no se ve roca)
        float r = length(c);
        float nucleo = exp(-r * r * 60.0) * 1.6;
        float halo = exp(-r * r * 6.0) * 0.8 + exp(-r * 3.5) * 0.35;
        vec3 col = mix(vec3(1.0, 0.78, 0.40), vec3(1.0, 1.0, 0.96), clamp(nucleo, 0.0, 1.0));
        fragColor = vec4(col, clamp((nucleo + halo) * sinCaja * vColor.a, 0.0, 1.0));
        return;
    }

    if (Modo < 2.5) {
        // estela: blanca cegadora pegada a la cabeza y fuego naranja detrás que se va apagando en rojo
        float u = vUV.x;
        float v = vUV.y * 2.0 - 1.0;
        float tur = fbm(vec3(u * 8.0 - T * 6.0, v * 2.0, Semilla));
        float ancho = mix(0.55, 1.0, smoothstep(0.0, 0.35, u)) * mix(1.0, 0.45, smoothstep(0.4, 1.0, u)) * (0.8 + 0.4 * tur);
        float nucleo = exp(-pow(v / (0.22 * ancho), 2.0));
        float brillo = exp(-pow(v / (0.65 * ancho), 2.0)) * 0.6;
        float largo = (1.0 - smoothstep(0.35, 1.0, u)) * (0.7 + 0.5 * tur);
        float calor = clamp(nucleo * (1.0 - u * 1.6), 0.0, 1.0);
        vec3 col = mix(vec3(0.95, 0.30, 0.06), vec3(1.0, 0.58, 0.16), smoothstep(0.0, 0.5, nucleo * (1.0 - u)));
        col = mix(col, vec3(1.0, 0.85, 0.5), smoothstep(0.35, 0.7, calor));
        col = mix(col, vec3(1.0, 1.0, 0.97), smoothstep(0.65, 0.9, calor) * (1.0 - smoothstep(0.08, 0.3, u)));
        fragColor = vec4(col, clamp((nucleo + brillo) * largo * vColor.a, 0.0, 1.0));
        return;
    }

    if (Modo < 3.5) {
        float u = vUV.x;
        float v = abs(vUV.y * 2.0 - 1.0);
        float n = fbm(vec3(u * 6.0 - T * 1.5, vUV.y * 2.5, Semilla + 4.0));
        float ancho = mix(0.35, 1.0, u);
        float perfil = smoothstep(ancho, ancho * 0.2, v + (n - 0.5) * 0.5);
        vec3 col = mix(vec3(0.20, 0.12, 0.08), vec3(0.32, 0.27, 0.24), u);
        float a = perfil * smoothstep(0.02, 0.15, u) * (1.0 - smoothstep(0.6, 1.0, u)) * (0.45 + 0.5 * n);
        fragColor = vec4(col, clamp(a * 0.75 * vColor.a, 0.0, 1.0));
        return;
    }

    if (Modo < 4.5) {
        // bocanada de polvo con volumen: se sombrea como una bola (luz desde arriba, panza oscura, borde que se ilumina)
        float semilla = vColor.g * 100.0;
        float r = length(c);
        if (r > 1.0) discard;
        vec3 q = vec3(c, 0.0);
        float n = fbm(vec3(c * 1.8, semilla + T * 0.10));
        float n2 = fbm(vec3(c * 4.5 + 7.0, semilla * 1.3 - T * 0.15));
        // borde grumoso de coliflor
        float ang = atan(c.y, c.x);
        float grumo = fbm(vec3(cos(ang) * 2.5, sin(ang) * 2.5, semilla + T * 0.08));
        float borde = 0.72 + 0.28 * grumo;
        float dentro = 1.0 - r / borde;
        float a = smoothstep(0.0, 0.35, dentro) * (0.75 + 0.35 * n);
        // normal falsa de la esfera, abultada por el ruido
        float rr = clamp(r / borde, 0.0, 1.0);
        vec3 nor = normalize(vec3(c / borde, sqrt(max(0.0, 1.0 - rr * rr)) + (n2 - 0.5) * 0.8));
        float luz = clamp(dot(nor, normalize(vec3(-0.35, 0.85, 0.45))), 0.0, 1.0);
        float sombra = 0.38 + 0.62 * luz;
        vec3 claro = vec3(1.0, 0.91, 0.72);
        vec3 oscuro = vec3(0.42, 0.27, 0.15);
        vec3 col = mix(oscuro, claro, sombra) * mix(0.85, 1.08, n2);
        // pliegues anaranjados
        col = mix(col, vec3(0.86, 0.55, 0.30), smoothstep(0.55, 0.85, n2) * 0.35);
        col *= vColor.r;
        // el fuego del impacto lo ilumina por dentro y por debajo
        float desdeAbajo = clamp(0.5 - c.y * 0.5, 0.0, 1.0);
        vec3 brasa = mix(vec3(1.0, 0.52, 0.16), vec3(1.0, 0.82, 0.45), luz * n);
        // lo más caliente (la bola de fuego recién nacida) es casi blanco
        brasa = mix(brasa, vec3(1.0, 0.97, 0.86), smoothstep(0.8, 1.0, vColor.b) * (0.4 + 0.6 * luz));
        col = mix(col, brasa * (1.0 + 0.3 * n), clamp(vColor.b * (0.55 + 0.45 * desdeAbajo) * 1.15, 0.0, 0.95));
        fragColor = vec4(col, clamp(a * vColor.a, 0.0, 1.0));
        return;
    }

    if (Modo < 5.5) {
        fragColor = vColor;
        return;
    }

    if (Modo < 6.5) {
        float u = vUV.x;                        // 0 delante de la roca .. 1 punta de la cola
        float v = vUV.y * 2.0 - 1.0;            // -1 .. 1 de lado a lado
        // turbulencia que corre hacia atrás
        float t1 = fbm(vec3(u * 6.0 - T * 9.0, v * 2.5, Semilla));
        float t2 = fbm(vec3(u * 14.0 - T * 15.0, v * 5.0, Semilla + 3.0));
        // forma de gota: redonda delante, se abre un poco y se afina hacia la cola
        float ancho = sqrt(clamp(u / 0.16, 0.0, 1.0)) * mix(1.0, 0.25, smoothstep(0.2, 1.0, u)) * (0.85 + 0.35 * t1);
        float dentro = 1.0 - abs(v + (t1 - 0.5) * 0.45 * u) / max(ancho, 0.001);
        float llama = clamp(dentro, 0.0, 1.0);
        llama = pow(llama, 0.8) * (0.65 + 0.7 * t2) * (1.0 - smoothstep(0.6, 1.0, u));
        // más caliente cerca de la cabeza
        float calor = llama * mix(1.6, 0.4, smoothstep(0.0, 0.7, u));
        vec3 col = vec3(0.55, 0.06, 0.01);
        col = mix(col, vec3(1.0, 0.35, 0.05), smoothstep(0.15, 0.55, calor));
        col = mix(col, vec3(1.0, 0.75, 0.25), smoothstep(0.55, 0.95, calor));
        col = mix(col, vec3(1.0, 0.98, 0.88), smoothstep(0.95, 1.4, calor));
        fragColor = vec4(col, clamp(llama * 1.1 * vColor.a, 0.0, 1.0));
        return;
    }

    if (Modo > 7.5) {
        // 8: la bola de luz del impacto: enorme, blanca en el centro y amarilla anaranjada hacia fuera
        float r = length(c);
        float a = (exp(-r * r * 2.6) + exp(-r * 4.5) * 0.4) * sinCaja;
        vec3 col = mix(vec3(1.0, 0.62, 0.25), vec3(1.0, 0.98, 0.88), exp(-r * 2.5));
        fragColor = vec4(col, clamp(a * vColor.a, 0.0, 1.0));
        return;
    }

    // 7: onda expansiva
    float rr = vUV.y;
    float ang = vUV.x * 6.2831853;
    float n = fbm(vec3(cos(ang) * 3.0, sin(ang) * 3.0, Semilla + rr * 2.0 - T * 0.5));
    float banda = smoothstep(0.0, 0.55, rr) * smoothstep(1.0, 0.7, rr);
    vec3 col = mix(vec3(0.75, 0.58, 0.40), vec3(1.0, 0.85, 0.65), smoothstep(0.6, 0.95, rr));
    fragColor = vec4(col, clamp(banda * (0.5 + 0.7 * n) * vColor.a, 0.0, 1.0));
}
