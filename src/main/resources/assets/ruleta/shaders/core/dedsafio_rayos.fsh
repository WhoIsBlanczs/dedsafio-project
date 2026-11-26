#version 150

// Código generado por Claude (Anthropic).
//
// Luz dinámica del sol de DEDsafío: rayos de luz en pantalla (god rays), la misma idea que la supernova. Desde cada píxel se
// camina hacia el sol sumando luz solo por donde se ve cielo (profundidad = 1), así los árboles, las rocas, el jugador y el
// terreno recortan los rayos y la luz "interactúa" con el mundo.

uniform sampler2D DepthSampler;
uniform vec4 Luz;     // xy = posición del sol en pantalla (0..1), z = relación de aspecto, w = intensidad
uniform vec4 Tinte;   // rgb = color de los rayos

in vec2 vUV;
out vec4 fragColor;

void main() {
    vec2 L = Luz.xy;
    float asp = Luz.z;
    vec2 delta = L - vUV;
    const int N = 32;
    float jit = fract(52.9829189 * fract(dot(gl_FragCoord.xy, vec2(0.06711056, 0.00583715))));
    vec2 paso = delta * (0.95 / float(N));   // 32 pasos (antes 44): igual de suave y más barato
    vec2 sp = vUV + paso * jit;
    float acum = 0.0;
    float decay = 1.0;
    for (int i = 0; i < N; i++) {
        sp += paso;
        vec2 c = clamp(sp, vec2(0.001), vec2(0.999));
        float libre = step(0.99995, texture(DepthSampler, c).r);       // 1 = cielo (nada delante)
        float aLaLuz = length((sp - L) * vec2(asp, 1.0));
        float brillo = exp(-aLaLuz * 2.2) * 0.22 + exp(-aLaLuz * 8.0) * 0.30;
        acum += libre * brillo * decay;
        decay *= 0.972;
    }
    acum /= float(N) * 0.9;
    float aMi = length((vUV - L) * vec2(asp, 1.0));
    float cerca = 0.55 + 0.45 * exp(-aMi * 1.1);
    float v = acum * cerca * 1.1;
    // con una figura formada (Tinte.a = 0..1) los rayos y la bruma solo quedan pegados al sol: no tapan ni saturan la imagen
    float lejosDelSol = smoothstep(0.05, 0.20, aMi);
    float apagar = 1.0 - Tinte.a * lejosDelSol * 0.95;
    v *= apagar;
    // bruma de luz muy tenue: en el video el sol es un disco pequeño con halo suave, no un resplandor que llena la pantalla
    v += (exp(-aMi * 3.0) * 0.04 + exp(-aMi * 9.0) * 0.05) * (1.0 - 0.35 * acum) * apagar;
    vec3 col = mix(Tinte.rgb, vec3(1.0, 0.92, 0.80), clamp(v * 0.25, 0.0, 1.0));
    fragColor = vec4(col, clamp(v * Luz.w, 0.0, 1.0));
}
