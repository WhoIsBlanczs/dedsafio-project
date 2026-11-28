#version 150

// Código generado por Claude (Anthropic).
// Eclipse solar (como en el video del DEDsafío):
//  - parcial: la Luna entra por un lado y va tapando el Sol (queda una media luna brillante); el cielo se apaga poco a poco
//  - anillo de diamante: justo antes y justo después de la totalidad, un punto de luz cegador con rayos en el borde
//  - totalidad: disco negro con corona blanca y rayitos, cielo azul de noche con estrellas y un atardecer naranja
//    alrededor de TODO el horizonte (360 grados)
// Fase: x = posición de la Luna (-1..1; 0 = justo delante del Sol), y = noche (0..1), z = anillo de diamante (0..1),
//       w = opacidad de este cielo (entra y sale suave)
// Brillo: x = cuánto Sol queda sin tapar (0..1), y = corona (0..1)

uniform float Tiempo;
uniform vec3 SolDir;
uniform vec3 Lado;
uniform vec3 Cielo;
uniform vec3 Horizonte;
uniform vec4 Fase;
uniform vec2 Brillo;

in vec3 direccion;
out vec4 fragColor;

const float RADIO_SOL = 0.135;       // radio del Sol en el cielo (radianes): casi el doble que antes (0,055)
const float RADIO_LUNA = 0.141;     // la Luna un poco más grande, así en la totalidad no se ve nada del Sol
const float ESCALA = 1.82;           // tamaño del halo, la corona y el anillo (fijo, para que crecer el Sol no los infle)

float hash(vec3 p) {
    p = fract(p * 0.3183099 + 0.1);
    p *= 17.0;
    return fract(p.x * p.y * p.z * (p.x + p.y + p.z));
}

float hash2(vec2 p) {
    p = fract(p * vec2(123.34, 456.21));
    p += dot(p, p + 45.32);
    return fract(p.x * p.y);
}

float ruido2(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    return mix(mix(hash2(i), hash2(i + vec2(1, 0)), f.x), mix(hash2(i + vec2(0, 1)), hash2(i + vec2(1, 1)), f.x), f.y);
}

float fbm2(vec2 p) {
    float v = 0.0;
    float a = 0.5;
    for (int i = 0; i < 5; i++) {
        v += a * ruido2(p);
        p = p * 2.03 + vec2(5.2, 1.7);
        a *= 0.5;
    }
    return v;
}

float ang(vec3 a, vec3 b) {
    return acos(clamp(dot(a, b), -1.0, 1.0));
}

void main() {
    vec3 d = normalize(direccion);
    float alto = d.y;
    float noche = Fase.y;

    // el cielo de siempre (azul arriba, más claro en el horizonte)
    vec3 dia = mix(Horizonte, Cielo, smoothstep(-0.05, 0.45, alto));

    // el cielo del eclipse: azul de noche arriba y un atardecer naranja-rosa alrededor de todo el horizonte
    vec3 cenit = vec3(0.055, 0.075, 0.150);
    vec3 naranja = vec3(0.95, 0.52, 0.30);
    vec3 rosa = vec3(0.55, 0.36, 0.45);
    float banda = exp(-max(alto + 0.02, 0.0) / 0.07);
    vec3 eclipse = mix(cenit, rosa, exp(-max(alto, 0.0) / 0.22) * 0.8);
    eclipse = mix(eclipse, naranja, banda);
    if (alto < -0.02) {
        eclipse = mix(naranja * 0.55, vec3(0.06, 0.05, 0.07), smoothstep(-0.02, -0.25, alto));
    }
    vec3 col = mix(dia, eclipse, noche);
    // el atardecer del horizonte aparece ya durante la fase parcial
    col = mix(col, mix(col, naranja, banda * 0.5), smoothstep(0.1, 0.5, noche) * (1.0 - noche));

    // estrellas (solo cuando ya está oscuro)
    vec3 celda = floor(d * 300.0);
    float h = hash(celda);
    if (h > 0.9965 && alto > -0.02) {
        // quietas, sin parpadear (como en el video); cada una con su propio brillo fijo
        float brilloFijo = 0.55 + 0.45 * fract(h * 731.0);
        col += vec3(0.85, 0.9, 1.0) * smoothstep(0.55, 0.95, noche) * brilloFijo * (h - 0.9965) / 0.0035;
    }

    // nubes: una capa alta, suave y esponjosa (sin huecos duros); con el eclipse se vuelven azul grisáceas y, en la totalidad,
    // siluetas suaves contra el atardecer del horizonte
    if (alto > 0.015) {
        vec2 pn = d.xz / (alto + 0.15) * 0.9 + vec2(Tiempo * 0.003, Tiempo * 0.001);
        float n = fbm2(pn);
        float cob = smoothstep(0.38, 0.95, n);
        float lejos = smoothstep(0.015, 0.25, alto);
        float haciaSol = clamp(dot(d, SolDir) * 0.5 + 0.5, 0.0, 1.0);
        // un poco más claras por arriba que por abajo (relieve suave)
        float luzN = 0.55 + 0.45 * smoothstep(0.35, 0.8, n);
        vec3 diaN = mix(vec3(0.70, 0.76, 0.86), vec3(1.0), luzN);
        vec3 grisN = mix(vec3(0.34, 0.40, 0.50), vec3(0.62, 0.68, 0.76), luzN);
        vec3 noN = vec3(0.09, 0.11, 0.17) + naranja * 0.30 * exp(-alto / 0.12);
        vec3 cn = mix(diaN, grisN, smoothstep(0.0, 0.75, noche));
        cn = mix(cn, noN, smoothstep(0.70, 1.0, noche));
        cn += vec3(1.0, 0.95, 0.85) * pow(haciaSol, 10.0) * Brillo.x * 0.35;
        col = mix(col, cn, cob * lejos * 0.70);
    }

    // el Sol y la Luna
    vec3 luna = normalize(SolDir + Lado * (Fase.x * RADIO_SOL * 2.4));
    float aSol = ang(d, SolDir);
    float aLuna = ang(d, luna);
    float discoSol = smoothstep(RADIO_SOL, RADIO_SOL * 0.97, aSol);
    float discoLuna = smoothstep(RADIO_LUNA, RADIO_LUNA * 0.985, aLuna);
    float queda = Brillo.x;

    // (el resplandor del Sol se suma más abajo, por encima de todo, también de la Luna: así la Luna queda dentro del cielo)
    float halo = exp(-max(aSol - RADIO_SOL, 0.0) / (0.10 * ESCALA)) * 0.30 + exp(-max(aSol - RADIO_SOL, 0.0) / (0.030 * ESCALA)) * 0.34;

    // corona de la totalidad: luz blanca alrededor de la Luna con rayitos que se mueven muy despacio
    if (Brillo.y > 0.001 && aLuna > RADIO_LUNA * 0.98) {
        vec3 t1 = normalize(Lado);
        vec3 t2 = normalize(cross(luna, t1));
        float a = atan(dot(d, t2), dot(d, t1));
        float rayos = 0.55 + 0.25 * sin(a * 7.0 + Tiempo * 0.05) + 0.15 * sin(a * 19.0 - 1.3) + 0.12 * sin(a * 41.0 + 2.0);
        float r = aLuna - RADIO_LUNA;
        float corona = exp(-r / (0.010 * ESCALA)) * 1.1 + exp(-r / (0.035 * ESCALA * rayos)) * 0.75 + exp(-r / (0.16 * ESCALA)) * 0.18;
        col += vec3(0.95, 0.97, 1.0) * corona * Brillo.y;
    }

    // el disco del Sol (lo que la Luna no tapa) y la Luna
    col = mix(col, vec3(1.0, 0.99, 0.95) * 1.4, discoSol * (1.0 - discoLuna));
    // la Luna se ve oscura delante del Sol (y casi negra en la totalidad)
    // (en el video se ve el disco entero de la Luna, tenue, aunque aún no tape el Sol)
    float lunaVisible = discoLuna * max(max(smoothstep(0.35, 0.0, abs(Fase.x)), smoothstep(RADIO_SOL * 2.2, RADIO_SOL, aSol)), 0.32 * (1.0 - noche));   // opaca en todo lo que toca al Sol o su resplandor
    // la Luna no es un disco plano: es el cielo de detrás muy oscurecido, un poco más claro en el borde (luz dispersa)
    float rLuna = clamp(aLuna / RADIO_LUNA, 0.0, 1.0);
    vec3 colLuna = col * 0.04 + vec3(0.004, 0.005, 0.010) + vec3(0.03, 0.04, 0.06) * pow(rLuna, 6.0);   // casi negra
    col = mix(col, colLuna, lunaVisible);   // OPACA: delante del Sol no se ve nada de él a través
    // el resplandor del Sol por encima de todo (también sobre la Luna, un poco menos)
    col += vec3(1.0, 0.97, 0.9) * halo * queda * (1.0 - discoLuna * lunaVisible);   // el resplandor del Sol no entra en el disco de la Luna

    // anillo de diamante: un punto cegador en el borde por donde la Luna deja escapar el último rayo, con rayos en cruz
    if (Fase.z > 0.001) {
        vec3 punto = normalize(SolDir - Lado * sign(Fase.x + 0.0001) * RADIO_SOL * 0.98);
        vec3 t1 = normalize(Lado);
        vec3 t2 = normalize(cross(punto, t1));
        vec2 q = vec2(dot(d - punto, t1), dot(d - punto, t2));
        float dist = length(q);
        float brillo = exp(-dist / (0.012 * ESCALA)) * 2.0 + exp(-dist / (0.08 * ESCALA)) * 0.7;
        float cruz = exp(-abs(q.y) / 0.0025) * exp(-abs(q.x) / 0.35) + exp(-abs(q.x) / 0.0025) * exp(-abs(q.y) / 0.35);
        vec2 qd = vec2(q.x + q.y, q.x - q.y) * 0.7071;
        float diag = exp(-abs(qd.y) / 0.002) * exp(-abs(qd.x) / 0.18) + exp(-abs(qd.x) / 0.002) * exp(-abs(qd.y) / 0.18);
        col += vec3(1.0, 0.98, 0.95) * (brillo + cruz * 1.2 + diag * 0.6) * Fase.z;
    }

    fragColor = vec4(min(col, vec3(1.0)), Fase.w);
}
