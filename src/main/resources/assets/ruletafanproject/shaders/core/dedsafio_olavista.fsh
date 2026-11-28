#version 150

// Código generado por Claude (Anthropic).
//
// Lo que se ve dentro de la ola (video): el mundo se ve a través de un agua turquesa clara y translúcida, pareja en toda la pantalla (sin borde en el
// horizonte), sin rayas (las rayas del video son de la parte de atrás de la ola, no de este efecto).

uniform float Tiempo;
uniform vec4 Dentro;   // x = cuánto cubre (0..1), y = segundos dentro, z = relación de aspecto, w = cabeceo de la cámara (grados, + = mira abajo)

in vec2 vUV;
out vec4 fragColor;

float hash(vec2 p) {
    p = fract(p * vec2(123.34, 456.21));
    p += dot(p, p + 45.32);
    return fract(p.x * p.y);
}

float vr(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    return mix(mix(hash(i), hash(i + vec2(1.0, 0.0)), f.x), mix(hash(i + vec2(0.0, 1.0)), hash(i + vec2(1.0, 1.0)), f.x), f.y);
}

void main() {
    float t = Dentro.y;
    float cubre = Dentro.x;
    vec2 p = (vUV - 0.5) * 2.0;   // -1..1

    // tinte turquesa claro, apenas más profundo hacia arriba; el mundo se ve a través
    vec3 col = mix(vec3(0.18, 0.80, 0.78), vec3(0.14, 0.70, 0.72), smoothstep(-0.8, 1.0, p.y));
    float alfa = mix(0.52, 0.64, smoothstep(-1.0, 1.0, p.y));
    // manchas muy suaves de agua más honda y más clara (nada de bordes duros)
    float honda = vr(p * vec2(0.9, 0.6) + vec2(t * 0.03, -t * 0.04));
    col *= 0.93 + 0.14 * honda;

    fragColor = vec4(col, alfa * cubre);
}
