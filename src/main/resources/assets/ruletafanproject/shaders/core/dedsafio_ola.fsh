#version 150

in vec3 rel;
in vec2 uv;

uniform vec4 Ola;
uniform vec4 Dir;
uniform float Tiempo;
uniform vec4 Mundo;

out vec4 fragColor;

float ss_(float a, float b, float x) { float t = clamp((x - a) / (b - a), 0.0, 1.0); return t * t * (3.0 - 2.0 * t); }
float hs(vec2 p) { return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453); }
float rn(vec2 p) {
    vec2 i = floor(p); vec2 f = fract(p); f = f * f * (3.0 - 2.0 * f);
    return mix(mix(hs(i), hs(i + vec2(1.0, 0.0)), f.x), mix(hs(i + vec2(0.0, 1.0)), hs(i + vec2(1.0, 1.0)), f.x), f.y);
}
// huecos: óvalos alargados que cuelgan de la cresta (la parte hundida de la pared), uno por celda, cada uno distinto
float hueco(float u, float v) {
    float i0 = floor(u / 7.0);
    float m = 0.0;
    for (int k = -1; k <= 1; k++) {
        float i = i0 + float(k);
        float h1 = hs(vec2(i, 1.7));
        float h2 = hs(vec2(i, 5.3));
        float h3 = hs(vec2(i, 9.1));
        float cu = (i + 0.5 + (h1 - 0.5) * 0.7) * 7.0 + (v - 0.6) * 8.0 * (h3 - 0.5);
        float cv = 0.46 + 0.30 * h2;
        float wu = 1.9 + 1.7 * h3;
        float hv = 0.26 + 0.22 * h1;
        float dd = length(vec2((u - cu) / wu, (v - cv) / hv));
        m = max(m, step(0.22, hs(vec2(i, 3.3))) * (1.0 - ss_(0.30, 1.0, dd)));
    }
    return m;
}
// relieve de la cara (bloques, con signo: + hacia el jugador, - hacia dentro): lóbulos grandes (huecos) y costillas verticales finas
float relieve(float u, float v, float H) {
    float k = H / 28.0;
    float uw = u + 2.0 * sin(u * 0.11 + 1.0) + (v - 0.5) * 4.0 * sin(u * 0.07 + 0.5);
    float big = sin(uw * 0.26) + 0.6 * sin(uw * 0.53 + 1.9);
    float c1 = sin(uw * 2.10 + 3.0 * sin(uw * 0.13) + 1.5 * v);
    float c2 = sin(uw * 3.70 + 1.0 + 2.0 * v);
    float costilla = 0.6 * c1 + 0.4 * c2;
    float amp = 0.55 + 0.45 * sin(uw * 0.31 + 0.8);
    float env = ss_(0.03, 0.28, v) * (1.0 - 0.3 * ss_(0.85, 1.0, v));
    return k * env * (1.4 * big + 1.3 * amp * costilla - 3.0 * hueco(u, v));
}
// perfil de la ola: x = cuánto sale hacia el jugador, y = altura. t 0..1 = cara, 1..2 = pendiente de atrás.
vec2 perfil(float u, float t, float H, float W) {
    float punta = clamp((W * 0.5 - abs(u)) / (0.05 * W + 3.0), 0.0, 1.0);
    float redondo = sqrt(max(0.0, 1.0 - (1.0 - punta) * (1.0 - punta)));   // esquina redondeada: casi toda la altura hasta el borde y cierra en curva
    float alto = (1.00 + 0.07 * sin(u * 0.045 + 0.6) + 0.04 * sin(u * 0.13)) * redondo;
    float vc = min(t, 1.0);
    float y = H * alto * vc;
    float s = H * 0.06 * pow(vc, 2.0) + H * 0.05 * ss_(0.88, 1.0, vc);   // la cresta se echa hacia delante y se curva
    s += H * 0.03 * sin(u * 0.08 + 0.7) * ss_(0.5, 1.0, vc);              // la línea de la cresta ondula
    s += relieve(u, vc, H) * (1.0 - ss_(1.0, 1.25, t));
    if (t > 1.0) {
        float a = t - 1.0;
        y = H * alto * max(0.0, 1.0 + 0.10 * sin(min(a / 0.25, 1.0) * 3.14159) * (0.6 + 0.4 * sin(u * 0.21 + 1.0)) - pow(a, 1.2));   // baja hasta el suelo, sin dejar huecos
        s = H * 0.11 + H * 0.03 * sin(u * 0.08 + 0.7) + relieve(u, 1.0, H) * (1.0 - ss_(0.0, 0.25, a)) - a * H * 1.3;   // empalma con la cresta de la cara (sin grietas)
    }
    s -= (1.0 - punta) * (1.0 - punta) * H * 0.20;                        // en la esquina la pared se cierra un poco hacia atrás
    return vec2(s, y);
}

void main() {
    float H = Ola.x;
    float t = uv.y;
    float vc = min(t, 1.0);
    // normal real de la geometría
    // normal analítica del perfil (suave, no depende de cómo se parte la malla): se deriva el perfil en u y en t
    float W = Ola.y;
    float eu = 0.18;
    float et = 0.012;
    vec2 p0 = perfil(uv.x, t, H, W);
    vec2 pu = perfil(uv.x + eu, t, H, W);
    vec2 pt = perfil(uv.x, t + et, H, W);
    // coordenadas locales: x = a lo largo (u), y = altura, z = hacia delante (s)
    vec3 du = vec3(eu, pu.y - p0.y, pu.x - p0.x);
    vec3 dt = vec3(0.0, pt.y - p0.y, pt.x - p0.x);
    vec3 nl = normalize(cross(dt, du));
    vec3 n = normalize(nl.x * vec3(Dir.y, 0.0, -Dir.x) + nl.y * vec3(0.0, 1.0, 0.0) + nl.z * vec3(Dir.x, 0.0, Dir.y));
    vec3 toCam = normalize(-rel);
    if (dot(n, toCam) < 0.0) n = -n;
    vec3 L = normalize(vec3(-0.5, 0.6, 0.62));
    float dif = clamp(dot(n, L), 0.0, 1.0);

    // profundidad: cuánto más hundido está este punto que su entorno, y sombra de lo que sobresale encima
    float z0 = relieve(uv.x, vc, H);
    float prom = 0.0;
    for (int i = -2; i <= 2; i++) {
        for (int j = -1; j <= 1; j++) {
            prom += relieve(uv.x + float(i) * 3.0, clamp(vc + float(j) * 0.14, 0.0, 1.0), H);
        }
    }
    prom /= 15.0;
    float oc = clamp((prom - z0) / 3.0, 0.0, 1.0);
    float sup = relieve(uv.x - 1.5, clamp(vc + 0.12, 0.0, 1.0), H);
    float sh = clamp((sup - z0) / 2.5, 0.0, 1.0);
    float d = clamp(0.75 * oc + 0.45 * sh, 0.0, 1.0) * ss_(0.04, 0.3, vc) * (1.0 - ss_(1.0, 1.1, t));

    vec3 base = vec3(0.07 + 0.05 * vc, 0.42 + 0.13 * vc, 0.47 + 0.11 * vc);
    vec3 col = base * (0.78 + 0.50 * dif);
    vec3 prof = vec3(0.03, 0.26, 0.32) * (0.9 + 0.2 * dif);
    col = mix(col, prof, d);
    float hu = hueco(uv.x, vc) * ss_(0.12, 0.35, vc) * (1.0 - ss_(1.0, 1.1, t));
    col = mix(col, vec3(0.02, 0.22, 0.28) * (0.8 + 0.4 * dif), 0.88 * ss_(0.05, 0.7, hu));
    float vet = ss_(0.66, 0.95, 0.65 * rn(vec2(uv.x * 0.6 + 1.5 * sin(vc * 2.0 + uv.x * 0.1), vc * 1.2)) + 0.35 * rn(vec2(uv.x * 1.3, vc * 2.4 + 7.0))) * ss_(0.10, 0.5, vc) * (1.0 - hu);
    float ven = max(clamp((dif - 0.80) * 4.0, 0.0, 1.0) * ss_(0.1, 0.6, vc) * 0.45, vet * 0.55);
    col = mix(col, vec3(0.86, 0.98, 0.97), ven);

    // espuma: banda blanca irregular en la cresta que se deshace en la pendiente de atrás, y pie espumoso
    float rug = rn(vec2(uv.x * 0.22, t * 3.0 + Tiempo * 0.15));
    float cresta = ss_(0.85 + 0.06 * rug, 1.0, t) * (1.0 - ss_(1.10 + 0.30 * rug, 1.65, t));
    float pie = (1.0 - ss_(0.0, 0.06 + 0.05 * rug, t)) * 0.8;
    col = mix(col, vec3(0.94, 0.99, 0.99), clamp(cresta * 0.95 + pie, 0.0, 1.0));
    if (t > 1.0) col = mix(col, vec3(0.30, 0.72, 0.74) * (0.7 + 0.5 * dif), ss_(1.3, 1.8, t) * 0.8);
    // rayas pálidas largas y curvas en la pendiente de atrás (como en el video): siguen la ladera y se curvan suavemente
    if (t > 1.0) {
        float a2 = t - 1.0;
        float k = (uv.x + 9.0 * sin(a2 * 1.6 + uv.x * 0.05)) / 5.0;
        float cual = floor(k + 0.5);
        float d = abs(k - cual);
        float w = 0.05 + 0.05 * fract(sin(cual * 12.9898) * 43758.5);
        float raya = 1.0 - ss_(0.0, w, d);
        float existe = step(0.45, fract(sin(cual * 78.233) * 43758.5));
        float trozo = ss_(0.2, 0.6, rn(vec2(cual * 3.7, a2 * 1.4 - Tiempo * 0.05)));
        float zona = ss_(0.05, 0.25, a2) * (1.0 - ss_(0.7, 1.0, a2));
        col = mix(col, vec3(0.88, 0.99, 0.98), raya * existe * trozo * zona * 0.6);
    }
    fragColor = vec4(col, 1.0);
}
