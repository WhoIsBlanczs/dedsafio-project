#version 150

// Código generado por Claude (Anthropic).
//
// Cielo y mar de la dimensión DEDsafío, versión 2 (colores medidos píxel a píxel del video):
//  - cielo lila grisáceo con franja salmón en el horizonte
//  - sol con el perfil de brillo medido del video (núcleo blanco, halo durazno, caída suave hasta lila)
//  - nubes alargadas con volumen: cuerpo oscuro, borde iluminado hacia el sol, sombra propia, neblina en el horizonte
//  - 6 figuras (atlas 3x3, cada una con su tamaño y posición medidos), se funden con la densidad de las nubes
//  - mar hasta el horizonte (solo donde no llega el terreno cargado)

uniform sampler2D Figuras;
uniform float Tiempo;
uniform vec4 Sol;      // xyz = dirección hacia el sol, w = fuerza de los rayos (0..1)
uniform vec4 Escena;   // x = figura (0..6, -1 = ninguna), y = formación (0..1), z = oscuridad de la figura, w = altura de la cámara sobre el agua
uniform vec4 Ajuste;   // x = más o menos nubes (-1..1), y = tamaño de las figuras (1 = normal), z = brillo del sol
uniform vec4 Mundo;    // xy = posición x,z de la cámara en el mundo (las ondas del mar no se mueven con uno)

in vec3 direccion;
out vec4 fragColor;

const float PI = 3.14159265;
const vec3 HORIZONTE = vec3(0.98, 0.72, 0.64);   // igual que en DedsafioCielo.java (color de la niebla)

// geometría de cada figura medida en el video: ancho y alto de su celda (radianes) y posición de su centro respecto al sol
const float G_ANCHO[7] = float[7](0.8690, 0.8949, 0.5318, 0.8949, 0.5966, 0.6874, 0.8541);
const float G_ALTO[7]  = float[7](0.6096, 0.5577, 0.5707, 0.5837, 0.4929, 0.4669, 0.6406);
const float G_OFFX[7]  = float[7](0.0765, -0.0169, 0.0104, -0.0220, -0.0065, 0.0195, 0.0123);
const float G_OFFY[7]  = float[7](0.2516, 0.3035, 0.2464, 0.2672, 0.2490, 0.2309, 0.2509);

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

float ruido5(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    f = f * f * f * (f * (f * 6.0 - 15.0) + 10.0);
    float a = hash(i);
    float b = hash(i + vec2(1.0, 0.0));
    float c = hash(i + vec2(0.0, 1.0));
    float d = hash(i + vec2(1.0, 1.0));
    return mix(mix(a, b, f.x), mix(c, d, f.x), f.y);
}

// 3 octavas suaves y normalizadas (las nubes del video son grandes y borrosas, no granulosas)
float fbm3(vec2 p) {
    float v = 0.0;
    float a = 0.5;
    float s = 0.0;
    mat2 r = mat2(0.8, 0.6, -0.6, 0.8);
    for (int i = 0; i < 3; i++) {
        v += a * ruido5(p);
        s += a;
        p = r * p * 2.0 + 11.7;
        a *= 0.5;
    }
    return v / s;
}

float fbm(vec2 p) {
    float v = 0.0;
    float a = 0.5;
    mat2 r = mat2(0.8, 0.6, -0.6, 0.8);
    for (int i = 0; i < 5; i++) {
        v += a * ruido(p);
        p = r * p * 2.03 + 11.7;
        a *= 0.5;
    }
    return v;
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

float gFg = 0.0;   // cuánto está formada la figura (0 = ninguna): el halo ancho del sol se apaga para no taparla ni saturarla

// el cielo sin nubes en una dirección (también lo usa el agua para reflejarlo)
vec3 cielo(vec3 d, vec3 sol) {
    float h = clamp(d.y, 0.0, 1.0);
    vec3 col = mix(vec3(0.92, 0.66, 0.55), vec3(0.62, 0.55, 0.58), smoothstep(0.0, 0.08, h));
    col = mix(col, vec3(0.55, 0.53, 0.63), smoothstep(0.08, 0.60, h));
    float a = acos(clamp(dot(d, sol), -1.0, 1.0));
    float w = (1.0 - smoothstep(0.30, 0.60, a)) * Ajuste.z;
    w *= 1.0 - gFg * 0.90 * smoothstep(0.035, 0.14, a);   // con figura: solo el núcleo y el halo cercano; el resto del resplandor se va
    col = mix(col, halo(a), clamp(w, 0.0, 1.0));
    return col;
}


// ---------------------------------------------------------------- nubes volumétricas
// Capa de nubes 3D (base plana, cima redondeada) recorrida con marcha de rayos; la luz del sol se calcula marchando hacia él.
// Parámetros calibrados contra el video con una simulación (alturas en metros respecto a la cámara).
const float NUBE_BASE = 1500.0;
const float NUBE_TOPE = 2300.0;
const float NUBE_SIG = 0.0040;     // extinción
const float NUBE_KL = 2.4;         // cuánto oscurece la propia nube a la luz que la cruza
const vec3 SOL_COL = vec3(1.0, 0.70, 0.50);
const vec3 AMBIENTE = vec3(0.47, 0.47, 0.53);

float gFig = 0.0;      // figura formada en esta dirección: suma densidad para que se funda con las nubes
float gHalo = 0.0;     // cerca de la figura (borrosa): ahí las nubes NO se apartan, se juntan con ella
float gApartan = 0.0;  // cuánto se apartan las demás nubes para dejar ver la figura (0..1)
float gAnillo = 0.0;   // el borde del óvalo: ahí se juntan las nubes que se apartaron (0..1)
float gClaro = 0.0;    // claro alrededor del sol (0..1): las nubes no tapan el sol

float hash3(vec3 p) {
    return fract(sin(dot(p, vec3(127.1, 311.7, 74.7))) * 43758.5453);
}

float ruido3(vec3 p) {
    vec3 i = floor(p);
    vec3 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    float a = hash3(i);
    float b = hash3(i + vec3(1.0, 0.0, 0.0));
    float c = hash3(i + vec3(0.0, 1.0, 0.0));
    float d = hash3(i + vec3(1.0, 1.0, 0.0));
    float e = hash3(i + vec3(0.0, 0.0, 1.0));
    float g = hash3(i + vec3(1.0, 0.0, 1.0));
    float h = hash3(i + vec3(0.0, 1.0, 1.0));
    float k = hash3(i + vec3(1.0, 1.0, 1.0));
    return mix(mix(mix(a, b, f.x), mix(c, d, f.x), f.y), mix(mix(e, g, f.x), mix(h, k, f.x), f.y), f.z);
}

float fbm3d(vec3 p) {
    float v = 0.0;
    float a = 0.5;
    float s = 0.0;
    for (int i = 0; i < 3; i++) {
        v += a * ruido3(p);
        s += a;
        p = p * 2.03 + vec3(3.1, 1.7, 5.3);
        a *= 0.5;
    }
    return v / s;
}

// igual que fbm3d pero con 2 octavas: para las muestras de luz hacia el sol (mucho más barato, la sombra no necesita detalle fino)
float fbm3d2(vec3 p) {
    float v = 0.5 * ruido3(p);
    p = p * 2.03 + vec3(3.1, 1.7, 5.3);
    v += 0.25 * ruido3(p);
    return v / 0.75;
}

// cobertura de las nubes: más bajo = más nubes (en el video el cielo está casi cubierto de nubes largas)
const float NUBE_UMBRAL = 0.47;
const float NUBE_SUAVE = 0.20;

// densidad de nube en un punto (posición respecto a la cámara); hs = hacia el sol sobre el suelo
float densidad(vec3 pos, vec2 hs, bool fino) {
    float hf = clamp((pos.y - NUBE_BASE) / (NUBE_TOPE - NUBE_BASE), 0.0, 1.0);
    float prof = smoothstep(0.0, 0.12, hf) * (1.0 - smoothstep(0.45, 1.0, hf));   // base plana, cima redondeada
    float along = dot(pos.xz, hs);
    float perp = dot(pos.xz, vec2(hs.y, -hs.x)) + Tiempo * 8.0;
    // nubes alargadas en la dirección del sol (como en el video, donde convergen hacia él): largas a lo largo, estrechas a lo ancho
    // bandas onduladas (como en el video): la franja se curva suavemente y converge hacia el sol
    perp += 650.0 * sin(along / 2100.0 + 1.3) + 280.0 * sin(along / 760.0 + perp / 3000.0);
    vec3 q = vec3(perp / 1700.0, pos.y / 420.0, along / 6200.0);
    float base = fino ? fbm3d(q) : fbm3d2(q);
    // cuando hay una figura las demás nubes se apartan (menos las pegadas a la figura, que se funden con ella)
    // (en el video las nubes se barren hacia fuera y dejan un claro redondeado alrededor de la figura, que es una silueta gris plana)
    float n = base + Ajuste.x * 0.05 - gApartan * 0.85 - gClaro * 0.55;
    return clamp((n - NUBE_UMBRAL) / NUBE_SUAVE, 0.0, 1.0) * prof;
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

float hh(vec2 i, float s) {
    return fract(sin(dot(i, vec2(127.1, 311.7)) + s * 74.7) * 43758.5453);
}

// densidad de una capa de manchas de nube en el plano del cielo: una por celda, cada una con su forma (elipse torcida y deformada).
// y = altura del rayo: cerca del horizonte la deformación se apaga para que no se vuelva ruido.
float capa(float pp, float aa, float sx, float sy, float off, float pres, float y) {
    vec2 q = vec2(pp / sx + off, aa / sy + off * 0.7);
    vec2 c0 = floor(q);
    float amp = 0.55 * smoothstep(0.015, 0.07, y);
    vec2 w = (vec2(fbm3(q * 1.9), fbm3(q * 1.9 + vec2(7.0, 3.0))) - 0.5) * amp;
    float dens = 0.0;
    for (int ox = -1; ox <= 1; ox++) {
        for (int oy = -1; oy <= 1; oy++) {
            vec2 ic = c0 + vec2(float(ox), float(oy));
            float h1 = hh(ic, 1.0);
            float h2 = hh(ic, 2.0);
            float h3 = hh(ic, 3.0);
            float h4 = hh(ic, 4.0);
            float h5 = hh(ic, 5.0);
            float h6 = hh(ic, 6.0);
            vec2 cc = ic + 0.5 + (vec2(h1, h2) - 0.5) * 0.6;
            vec2 l = q + w - cc;
            float th = h3 - 0.5;
            float cth = cos(th);
            float sth = sin(th);
            vec2 r = vec2(cth * l.x + sth * l.y, -sth * l.x + cth * l.y);
            float ax = 0.42 + 0.22 * h4;
            float ay = 0.34 + 0.22 * h5;
            float dd = length(vec2(r.x / ax, r.y / ay));
            float m = 1.0 - clamp((dd - 0.55) / 0.45, 0.0, 1.0);
            m = m * m * (3.0 - 2.0 * m);
            m *= step(pres, h6);
            dens = max(dens, m);
        }
    }

    return dens;
}

// miles de nubes hasta el horizonte: capas grandes arriba y cada vez más pequeñas y apiladas hacia el horizonte (la perspectiva las achata sola)
float manchas(float pp, float aa, float y) {
    float d1 = capa(pp, aa, 1.30, 3.6, 0.0, 0.20, y);
    float d2 = capa(pp, aa, 0.80, 2.0, 3.3, 0.30, y);
    float d3 = capa(pp, aa, 0.55, 1.3, 7.1, 0.38, y);
    float k2 = smoothstep(0.45, 0.14, y);
    float k3 = smoothstep(0.20, 0.07, y) * smoothstep(0.012, 0.04, y);
    float r = max(d1, max(d2 * k2, d3 * k3 * 0.9));
    float e = smoothstep(0.006, 0.03, y);
    return r * e + 0.45 * (1.0 - e) * smoothstep(0.0, 0.006, y);
}

void main() {
    vec3 d = normalize(direccion);
    vec3 sol = normalize(Sol.xyz);
    float y = d.y;
    float cs = dot(d, sol);
    vec3 col;

    if (y >= 0.0) {
        gFg = Escena.x > -0.5 ? Escena.y : 0.0;
        col = cielo(d, sol);
        float aSol = acos(clamp(cs, -1.0, 1.0));
        gClaro = 0.0;   // las nubes se abren alrededor del sol

        // ---- figura: calcula antes la máscara para que sume densidad a las nubes (así se funde con ellas)
        vec2 p = d.xz / (y + 0.06);
        float t = Tiempo * 0.02;
        float figura = 0.0;
        bool hayFigura = Escena.x > -0.5 && Escena.y > 0.001;
        if (hayFigura) {
            int fi = int(clamp(floor(Escena.x + 0.5), 0.0, 6.0));
            float azSol = atan(sol.x, sol.z);
            float da = mod(atan(d.x, d.z) - azSol + PI, 2.0 * PI) - PI;
            float derecha = -da;                                  // en pantalla, a la derecha del sol (corrige el espejo)
            float elSol = asin(clamp(sol.y, -1.0, 1.0));
            float k = Ajuste.y;
            float an = G_ANCHO[fi] * k;
            float al = G_ALTO[fi] * k;
            vec2 uv = vec2(0.5 + (derecha - G_OFFX[fi] * k) / an,
                           0.5 - (asin(clamp(y, -1.0, 1.0)) - (elSol + G_OFFY[fi] * k)) / al);
            float forma = Escena.y;
            // al principio la figura es ruido (nubes sueltas) y se va concentrando en la forma; al final se deshace igual
            float fm = smoothstep(0.0, 0.75, forma);
            vec2 wf = vec2(fbm(p * 1.3 + t * 7.0), fbm(p * 1.3 + 9.1 + t * 7.0)) - 0.5;
            vec2 uvw = uv + wf * 0.16 * (1.0 - fm);
            vec2 borde = smoothstep(vec2(0.0), vec2(0.03), uvw) * smoothstep(vec2(0.0), vec2(0.03), 1.0 - uvw);
            float dentro = borde.x * borde.y;
            vec2 celda = vec2(float(fi % 3), float(fi / 3));
            vec2 cuv = clamp(uvw, 0.003, 0.997);
            float m = textureLod(Figuras, (celda + cuv) / vec2(3.0, 3.0), 0.0).r * dentro;
            float v = m * fm + (fbm(p * 2.2 + 4.0) - 0.5) * 0.5 * (1.0 - fm);
            figura = smoothstep(0.12, 0.50, v) * smoothstep(0.0, 0.04, y);
            // zona borrosa alrededor de la figura (8 muestras): las nubes pegadas a ella no se apartan
            float hal = m;
            for (int kk = 0; kk < 8; kk++) {
                float ang = float(kk) * 0.785398;
                vec2 q = clamp(uvw + vec2(cos(ang), sin(ang)) * 0.07, 0.003, 0.997);
                hal += textureLod(Figuras, (celda + q) / vec2(3.0, 3.0), 0.0).r;
            }

            gHalo = clamp(hal / 9.0 * 3.0, 0.0, 1.0);
            // claro redondeado (óvalo) alrededor de la figura: dentro no hay nubes, el borde se funde con las nubes que se apartan
            // óvalo claro alrededor de la figura (como en el video): dentro no queda nube, y las nubes empujadas se amontonan en el borde del óvalo
            float rOv = length((uv - 0.5) * vec2(1.0, 1.15) * 2.0);
            float cerca = 1.0 - smoothstep(1.35, 2.30, rOv);   // claro grande: las nubes del centro se van del todo
            gApartan = smoothstep(0.0, 0.25, forma) * cerca;
            gAnillo = smoothstep(0.0, 0.5, forma) * smoothstep(1.55, 1.95, rOv) * (1.0 - smoothstep(2.1, 2.7, rOv));
        }

        gFig = figura * (1.0 - 0.92 * smoothstep(0.55, 1.0, Escena.y));

        // ---- nubes (calculadas en código, sin imágenes): miles de manchas hasta el horizonte, cada una, cada una con su propia forma y casi del mismo tamaño, de fondo plano y bordes suaves,
        // con el borde que mira al sol iluminado de rosa. Arriba se ven grandes y hacia el horizonte se achatan en filas (perspectiva).
        vec2 hs = normalize(sol.xz + vec2(0.0001));
        float dyc = max(y, 0.015);
        vec2 Pl = d.xz / dyc;
        float aa = dot(Pl, hs);
        float pp = dot(Pl, vec2(hs.y, -hs.x)) + Tiempo * 0.012;
        float D = clamp(manchas(pp, aa, y) * (1.0 - gApartan) + gAnillo * 0.30 * smoothstep(0.0, 0.3, y) + gFig * 0.12 + Ajuste.x * 0.05, 0.0, 1.0);
        float Dt = clamp(manchas(pp, aa + 0.10, y) * (1.0 - gApartan) + gAnillo * 0.30 * smoothstep(0.0, 0.3, y) + gFig * 0.12 + Ajuste.x * 0.05, 0.0, 1.0);
        float borde = clamp((D - Dt) * 2.6, 0.0, 1.0);
        float nucleo = clamp((D - 0.5) * 2.0, 0.0, 1.0);
        float cercaSol = exp(-aSol * 3.0);
        vec3 cuerpo = mix(vec3(0.60, 0.55, 0.60), vec3(0.40, 0.40, 0.46), nucleo);
        cuerpo = mix(cuerpo, vec3(0.96, 0.80, 0.74), cercaSol * 0.5);
        cuerpo = mix(cuerpo, vec3(1.0, 0.84, 0.76), borde * 0.55);
        float ac = D * 0.92;
        // velo de cirros muy fino, rosado
        float velo = smoothstep(0.38, 0.75, fbm3(vec2(pp * 0.55, aa * 0.20) + 7.0));
        float av = velo * (0.12 + 0.25 * cercaSol) * (1.0 - 0.9 * gApartan);
        vec3 colVelo = vec3(1.0, 0.86, 0.80);
        float T = (1.0 - ac) * (1.0 - av);
        vec3 acum = cuerpo * ac * (1.0 - av) + colVelo * av;

        // neblina del horizonte: las nubes lejanas se funden con la franja salmón (y no se ven por debajo de ~1°)
        float bruma = exp(-y * 11.0);
        acum = acum * (1.0 - bruma * 0.8) + vec3(0.84, 0.62, 0.56) * bruma * 0.8 * (1.0 - T);
        float fade = smoothstep(0.012, 0.075, y);
        vec3 conNubes = col * T + acum;
        col = mix(col, conNubes, fade);

        float cobertura = (1.0 - T) * fade;

        // el sol siempre se ve: núcleo y halo cercano se pintan por encima de las nubes
        float visSol = clamp(Ajuste.z * 2.0 * (1.0 - smoothstep(0.030, 0.130, aSol)), 0.0, 1.0);
        visSol *= 0.30 + 0.70 * T;   // las nubes pueden tapar el sol en parte (como en el video)
        col = mix(col, halo(aSol), visSol);

        // la figura en el video (medido): el cuerpo es un gris cálido, menos azul y casi igual de claro que el cielo; las líneas del dibujo dejan ver el cielo
        if (hayFigura) {
            float aparece = smoothstep(0.15, 0.85, Escena.y);
            // silueta gris plana semitransparente (como en el video): oscurece y quita color al cielo y a lo que haya detrás
            float lum = dot(col, vec3(0.299, 0.587, 0.114));
            // el brillo del sol no puede tapar la figura: el gris se limita, así la silueta se ve también delante del sol
            vec3 gris = vec3(min(lum * 0.86, 0.66)) * vec3(1.02, 1.0, 0.97);
            col = mix(col, gris, 0.70 * figura * aparece);
        }

        // ---- rayos suaves alrededor del sol
        if (cs > 0.0) {
            vec3 der = normalize(cross(sol, vec3(0.0, 1.0, 0.0)));
            vec3 arr = cross(der, sol);
            vec2 s2 = vec2(dot(d, der), dot(d, arr));
            float ang = atan(s2.y, s2.x);
            float r = length(s2);
            vec2 dirA = vec2(cos(ang), sin(ang));
            float rayo = ruido(dirA * 3.2 + Tiempo * 0.03) * 0.65 + ruido(dirA * 7.5 - Tiempo * 0.05) * 0.35;
            rayo = pow(smoothstep(0.40, 0.95, rayo), 1.5);
            col += vec3(1.0, 0.82, 0.64) * rayo * exp(-r * 3.0) * 0.09 * Sol.w * (1.0 - cobertura * 0.6) * (1.0 - gFg * 0.90);
        }
    } else {
        // ---- mar hasta el horizonte (la misma fórmula que el plano de agua)
        float alt = max(Escena.w, 0.3);
        float dist = alt / (-y + 0.0007);
        float alfaMar;
        col = mar(d, d.xz * dist + Mundo.xy, dist, sol, 1.0, alfaMar);
    }

    col += (hash(gl_FragCoord.xy) - 0.5) / 255.0;
    fragColor = vec4(col, 1.0);
}
