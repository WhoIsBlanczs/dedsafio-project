#version 150

in vec3 Position;   // base de la ola (y = suelo, avanzando en la dirección de Dir)
in vec2 UV0;        // x = bloques a lo largo (desde el centro), y = t (0 pie .. 1 cresta .. 2 pie de atrás)

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec4 Ola;      // x = altura, y = largo
uniform vec4 Dir;      // x,y = dirección (x,z) en la que avanza la ola

out vec3 rel;
out vec2 uv;

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
    vec2 sy = perfil(UV0.x, UV0.y, Ola.x, Ola.y);
    vec3 p = Position + vec3(0.0, sy.y, 0.0) + vec3(Dir.x, 0.0, Dir.y) * sy.x;
    rel = p;
    uv = UV0;
    gl_Position = ProjMat * ModelViewMat * vec4(p, 1.0);
}
