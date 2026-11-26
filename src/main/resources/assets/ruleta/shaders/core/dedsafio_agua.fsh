#version 150

// Código generado por Claude (Anthropic).
//
// Superficie del agua de DEDsafío: un plano a la altura del mar que se dibuja encima del terreno cargado, con LA MISMA fórmula
// que el mar del cielo (dedsafio_cielo.fsh), así el agua de cerca y la de lejos son una sola. Sustituye al agua de Minecraft
// en la dimensión (AguaDedsafioMixin la oculta). Cerca es translúcida y deja ver la arena y las algas. Ver mar() más abajo.

uniform float Tiempo;
uniform vec4 Sol;      // xyz = dirección al sol
uniform vec4 Ajuste;   // z = brillo del sol
uniform vec4 Mundo;    // xy = posición x,z de la cámara en el mundo (para que las ondas no se muevan con uno)

in vec3 rel;
out vec4 fragColor;

const vec3 HORIZONTE = vec3(0.98, 0.72, 0.64);

float hash(vec2 p) {
    p = fract(p * vec2(123.34, 456.21));
    p += dot(p, p + 45.32);
    return fract(p.x * p.y);
}

float ruido(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    float a = hash(i);
    float b = hash(i + vec2(1.0, 0.0));
    float c = hash(i + vec2(0.0, 1.0));
    float d = hash(i + vec2(1.0, 1.0));
    return mix(mix(a, b, f.x), mix(c, d, f.x), f.y);
}

// brillo del sol según el ángulo (radianes) al sol: medido en el video
vec3 halo(float a) {
    vec3 c = vec3(1.0, 1.0, 0.98);
    c = mix(c, vec3(0.99, 0.96, 0.79), smoothstep(0.010, 0.026, a));
    c = mix(c, vec3(0.99, 0.91, 0.73), smoothstep(0.026, 0.045, a));
    c = mix(c, vec3(0.96, 0.82, 0.69), smoothstep(0.045, 0.090, a));
    c = mix(c, vec3(0.93, 0.73, 0.65), smoothstep(0.090, 0.130, a));
    c = mix(c, vec3(0.84, 0.67, 0.63), smoothstep(0.130, 0.195, a));
    c = mix(c, vec3(0.68, 0.58, 0.58), smoothstep(0.195, 0.285, a));
    c = mix(c, vec3(0.57, 0.52, 0.54), smoothstep(0.285, 0.415, a));
    return c;
}

// el cielo sin nubes en una dirección (igual que en dedsafio_cielo.fsh; el agua lo refleja)
vec3 cielo(vec3 d, vec3 sol) {
    float h = clamp(d.y, 0.0, 1.0);
    vec3 col = mix(vec3(0.92, 0.66, 0.55), vec3(0.62, 0.55, 0.58), smoothstep(0.0, 0.08, h));
    col = mix(col, vec3(0.55, 0.53, 0.63), smoothstep(0.08, 0.60, h));
    float a = acos(clamp(dot(d, sol), -1.0, 1.0));
    float w = (1.0 - smoothstep(0.30, 0.60, a)) * Ajuste.z;
    col = mix(col, halo(a), clamp(w, 0.0, 1.0));
    return col;
}

// ---------------------------------------------------------------- el mar (misma fórmula en el cielo y en el plano de agua)
// Como en el video: casi un espejo del cielo limpio (sin nubes) con ondas muy finas y alargadas, translúcido de cerca (se ve el fondo)
// y con la columna de destellos del sol. Desde arriba y de cerca se ve turquesa cristalino (se transparenta la arena y las algas).
// "cuerpo" = opacidad del agua sin contar el reflejo (0 = se ve todo el fondo, 1 = opaca). alfa = opacidad total.
vec3 mar(vec3 d, vec2 pos, float dist, vec3 sol, float cuerpo, out float alfa) {
    float f = exp(-dist * 0.010);
    float amp = 0.060 * f + 0.012;   // olitas más marcadas de cerca, como en el video
    vec2 p1 = pos * vec2(1.9, 1.3);
    vec2 p2 = pos * vec2(0.55, 0.40);
    vec2 g = vec2(
        ruido(p1 + vec2(Tiempo * 0.35, Tiempo * 0.10)) - ruido(p1 * 0.81 + vec2(5.3, 1.7) - vec2(Tiempo * 0.27, 0.0)),
        ruido(p1.yx + vec2(9.1, 3.3) - vec2(Tiempo * 0.18, Tiempo * 0.22)) - ruido(p1.yx * 1.17 + vec2(2.7, 8.1) + vec2(0.0, Tiempo * 0.30))
    );
    g += 0.7 * vec2(
        ruido(p2 + vec2(Tiempo * 0.08, 0.0)) - ruido(p2 * 0.7 + 7.7),
        ruido(p2.yx + 3.1 - Tiempo * 0.06) - ruido(p2.yx * 0.7 + 1.3)
    );
    // olitas finas en líneas alargadas (se ven de cerca; a lo lejos se funden)
    vec2 p3 = pos * vec2(6.5, 3.8);
    g += 0.55 * f * vec2(
        ruido(p3 + vec2(Tiempo * 0.6, 0.0)) - ruido(p3 * 0.77 + 4.1 - vec2(0.0, Tiempo * 0.4)),
        ruido(p3.yx + 8.3 - Tiempo * 0.5) - ruido(p3.yx * 0.83 + 2.2)
    );
    vec3 n = normalize(vec3(g.x * amp, 1.0, g.y * amp));
    vec3 r = reflect(d, n);
    r.y = abs(r.y);
    float cosT = clamp(dot(n, -d), 0.0, 1.0);
    float fres = clamp(0.025 + 0.975 * pow(1.0 - cosT, 5.0), 0.0, 1.0);

    vec3 reflejo = cielo(r, sol);
    // la luz del sol rebotando en las ondas: columna de destellos bajo el sol
    float esp = clamp(dot(r, sol), 0.0, 1.0);
    float ch = smoothstep(0.62, 0.90, ruido(pos * 3.1 + vec2(Tiempo * 1.1, -Tiempo * 0.7)));
    reflejo += vec3(1.0, 0.84, 0.62) * (pow(esp, 380.0) * 1.8 + ch * pow(esp, 70.0) * 1.1) * Ajuste.z;

    // lo que se ve a través del agua: gris verdoso suave, más oscuro y frío a lo lejos
    // y desde arriba (poca distancia, mirando hacia abajo) el agua es turquesa cristalina; a lo lejos se vuelve verde grisáceo
    float arriba = smoothstep(0.55, 0.95, cosT);
    vec3 turquesa = mix(vec3(0.30, 0.62, 0.58), vec3(0.16, 0.72, 0.62), arriba);
    vec3 fondo = mix(turquesa, vec3(0.34, 0.52, 0.54), smoothstep(10.0, 150.0, dist));
    fondo *= 0.86 + 0.14 * ruido(pos * 0.8);

    alfa = fres + (1.0 - fres) * cuerpo;
    vec3 col = (reflejo * fres + fondo * (1.0 - fres) * cuerpo) / max(alfa, 0.001);
    return mix(col, HORIZONTE, 1.0 - exp(-dist * 0.0016));
}

void main() {
    vec3 d = normalize(rel);
    vec3 sol = normalize(Sol.xyz);
    float dist = length(rel);
    vec2 pos = rel.xz + Mundo.xy;
    // cerca deja ver el fondo; lejos es opaca
    float cuerpo = mix(0.26, 0.96, smoothstep(6.0, 110.0, dist));
    float alfa;
    vec3 col = mar(d, pos, dist, sol, cuerpo, alfa);
    fragColor = vec4(col, clamp(alfa, 0.0, 1.0));
}
