#version 150

// Código generado por Claude (Anthropic).
//
// Espuma suave donde el agua toca arena, rocas o bloques: una franja blanca que "respira" (sube y baja despacio), con encaje fino.
// uv.x = cercanía al bloque (1 pegado a él, 0 lejos), la interpolan los vértices.

uniform float Tiempo;
uniform vec4 Mundo;   // xy = cámara x,z

in vec3 rel;
in vec2 uv;
out vec4 fragColor;

float hash(vec2 p) {
    p = fract(p * vec2(123.34, 456.21));
    p += dot(p, p + 45.32);
    return fract(p.x * p.y);
}

float ruido(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    return mix(mix(hash(i), hash(i + vec2(1.0, 0.0)), f.x), mix(hash(i + vec2(0.0, 1.0)), hash(i + vec2(1.0, 1.0)), f.x), f.y);
}

void main() {
    vec2 w = rel.xz + Mundo.xy;
    float t = Tiempo;
    // la espuma sube y baja, desfasada de una zona a otra (nunca todo a la vez)
    float respira = 0.5 + 0.5 * sin(t * 1.05 + ruido(w * 0.09) * 6.2831);
    float encaje = ruido(w * 1.6 + vec2(t * 0.09, -t * 0.06)) * 0.6 + ruido(w * 4.1 - vec2(t * 0.13, 0.0)) * 0.4;
    float cerca = clamp(uv.x, 0.0, 1.0);
    float grosor = cerca * (0.55 + 0.45 * respira);
    float a = smoothstep(0.16, 0.62, grosor + (encaje - 0.5) * 0.50);
    a *= 0.80;
    a *= 1.0 - smoothstep(45.0, 75.0, length(rel.xz));   // de lejos se funde
    fragColor = vec4(0.97, 0.985, 0.975, a);
}
