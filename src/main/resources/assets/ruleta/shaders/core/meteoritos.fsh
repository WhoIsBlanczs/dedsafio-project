#version 150

// Código generado por Claude (Anthropic).
// Código generado por Claude (Anthropic).
// Los meteoritos y su cielo rojo volcánico. Modo:
//  0 cielo: malva -> carmesí con humo -> polvo naranja de los impactos (vPos = dirección de la vista), medido del video
//  1 cabeza del meteorito: esfera de lava (corteza negra con grietas rojas y naranjas, como la supernova pero volcánica) con borde ardiente
//  2 resplandor: núcleo blanco amarillo, halo naranja y el anillo fino brillante que se ve en los videos (se suma)
//  3 estela de fuego: blanca pegada al meteorito, luego amarilla, naranja y roja (se suma); vUV.x = 0 en la cabeza, 1 en la cola
//  4 estela de humo oscuro (se mezcla)
//  5 bocanadas del impacto (vColor.r = tono, vColor.g = semilla, vColor.b = fuego, vColor.a = cuánto queda)
//  6 color plano
//  7 onda expansiva sobre el suelo (vUV.y = 0 dentro .. 1 fuera del anillo)
//  8 bola de fuego del impacto (se suma)
//  12/13/14 MALLAS 3D: bola de roca, estela exterior (cáscara) y estela interior (embudo); la normal viaja en vColor.rgb, uv = (largo, vuelta)
//  9 humo volumétrico (estela y nube del impacto): vColor.r = calor (brasa naranja por dentro), g = semilla, b = oscuridad (0 polvo ámbar, 1 hollín), a = opacidad

uniform float Modo;
uniform vec3 CamPos;  // altura de la cámara (x, z = 0: solo importa la altura para el paralaje de las nubes)
uniform vec4 HumoA;   // modo 16: xyz = donde empieza la estela (la cabeza) respecto a la cámara, w = largo
uniform vec4 HumoB;   // xyz = hacia dónde se va la cola (unitario), w = radio del meteorito
uniform vec4 HumoC;   // x = opacidad general, y = semilla, z = edad (s) de una estela que ya no tiene meteorito
uniform float Tiempo;
uniform float Semilla;
uniform float Fuerza;
uniform float Avance;
uniform float Polvo;

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

float fbm3(vec3 p) {
    float a = 0.5, s = 0.0;
    for (int i = 0; i < 3; i++) {
        s += a * ruido(p);
        p = p * 2.03 + vec3(5.3, 9.1, 2.7);
        a *= 0.5;
    }
    return s;
}

float fbm2(vec3 p) {
    float a = 0.5, s = 0.0;
    for (int i = 0; i < 2; i++) {
        s += a * ruido(p);
        p = p * 2.03 + vec3(5.3, 9.1, 2.7);
        a *= 0.5;
    }
    return s / 0.75;
}

// ------------------------------------------------------------------ cielo con profundidad
// Fase 0 -> 1 (Avance en segundos): del cielo malva apagado al carmesí oscuro de tormenta; con los impactos (Polvo 0..1) el aire se llena de
// polvo naranja-marrón. Capas: degradado de fondo (ciruela oscuro arriba, carmesí, brasa en el horizonte), velos altos y finos, y un banco de
// nubes de tormenta bajo con luz por los bordes (se aclara el lado que mira al horizonte, como en el video), bruma del horizonte y destellos.

// Bancos de nubes VOLUMÉTRICAS: una losa de nube a unos 150-300 bloques de altura que se recorre con rayos desde la cámara
// (con paralaje real: si subes o bajas las nubes cambian de perspectiva), iluminada desde abajo por la luz del horizonte y los fuegos.
vec4 nubesVol(vec3 d, float fase, float tmax) {
    if (d.y < 0.012) { return vec4(0.0); }
    float y0 = 150.0;
    float y1 = 300.0;
    float ta = (y0 - CamPos.y) / d.y;
    float tb = (y1 - CamPos.y) / d.y;
    ta = max(ta, 0.0);
    tb = min(tb, min(4200.0, tmax));
    if (tb <= ta) { return vec4(0.0); }
    const int N = 6;
    float dt = (tb - ta) / float(N);
    float jit = fract(52.9829189 * fract(dot(gl_FragCoord.xy, vec2(0.06711056, 0.00583715))));
    float tt = ta + dt * jit;
    vec3 luzDir = normalize(vec3(0.35, -0.45, 0.25));
    vec3 acum = vec3(0.0);
    float Tr = 1.0;
    for (int i = 0; i < N; i++) {
        vec3 p = vec3(0.0, CamPos.y, 0.0) + d * tt;
        float hh = clamp((p.y - y0) / (y1 - y0), 0.0, 1.0);
        vec3 q = vec3(p.x * 0.0038 - Tiempo * 0.010, p.y * 0.0075, p.z * 0.0038 + Tiempo * 0.005);
        float n = fbm3(q);
        float perfil = smoothstep(0.0, 0.18, hh) * (1.0 - smoothstep(0.55, 1.0, hh));
        float dn = smoothstep(0.42, 0.74, n + (perfil - 0.5) * 0.30) * perfil;
        if (dn > 0.01) {
            vec3 q2 = q + luzDir * 0.07;
            float n2 = fbm3(q2);
            float d2 = smoothstep(0.42, 0.74, n2 + (perfil - 0.5) * 0.30);
            float luz = exp(-d2 * 2.0);
            vec3 sombra = vec3(0.095, 0.008, 0.012);
            vec3 claro = mix(vec3(0.82, 0.20, 0.16), vec3(1.00, 0.20, 0.07), fase);
            vec3 c = mix(sombra, claro, clamp(luz * (0.45 + 0.55 * (1.0 - hh)), 0.0, 1.0));
            c += vec3(0.30, 0.09, 0.03) * pow(luz, 2.0) * 0.6 * fase;     // borde de brasa
            float al = 1.0 - exp(-dn * dt * 0.028);
            acum += Tr * al * c;
            Tr *= 1.0 - al;
            if (Tr < 0.04) { break; }
        }
        tt += dt;
    }
    // lo lejano se funde con la bruma del horizonte
    float lejos = 1.0 - exp(-ta * 0.00055);
    return vec4(acum / max(1.0 - Tr, 1e-3), (1.0 - Tr) * (1.0 - 0.7 * lejos));
}

vec3 cielo(vec3 d) {
    float h = d.y;
    float ah = clamp(h, 0.0, 1.0);
    float t = Tiempo;
    float fase = smoothstep(0.0, 1.0, clamp((Avance - 3.0) / 11.0, 0.0, 1.0));

    // 0) degradado de fondo: no es un rojo uniforme; arriba casi ciruela-negro, en medio carmesí, abajo brasa
    vec3 zen = mix(vec3(0.34, 0.05, 0.08), vec3(0.120, 0.000, 0.020), fase);
    vec3 med = mix(vec3(0.58, 0.09, 0.10), vec3(0.520, 0.035, 0.030), fase);
    vec3 hor = mix(vec3(0.88, 0.15, 0.10), vec3(0.880, 0.090, 0.040), fase);
    vec3 base = mix(hor, med, smoothstep(0.0, 0.22, ah));
    base = mix(base, zen, smoothstep(0.18, 0.88, ah));

    // 1) velos altos: tiras finas y largas hacia el cénit, claras por abajo
    vec2 qa = d.xz / (max(h, 0.0) + 0.85) * 1.5 + vec2(t * 0.010, t * 0.004);
    float va = fbm2(vec3(qa * vec2(0.9, 2.6), 3.0));
    float velo = smoothstep(0.52, 0.80, va) * (0.25 + 0.75 * smoothstep(0.1, 0.5, ah)) * smoothstep(0.0, 0.08, h);
    base = mix(base, base * vec3(1.9, 1.5, 1.4) + vec3(0.04, 0.0, 0.01), velo * 0.22 * fase);

    // 2) humo de tormenta: bultos grandes y blandos en toda la cúpula (ruido 3D de la dirección, no se aplasta hacia el horizonte), con la luz de abajo
    vec2 qb = d.xz / (max(h, 0.0) + 0.60) * 1.25;
    vec2 mov = vec2(-t * 0.016, t * 0.007);
    vec3 p3 = d * vec3(2.1, 2.8, 2.1) + vec3(t * 0.010, t * 0.004, -t * 0.007);
    float w1 = fbm2(p3 * 0.8) - 0.5;
    vec3 wv = vec3(w1, -0.6 * w1, 0.8 * w1);
    vec3 pq = p3 + wv * 1.15;
    float nb = fbm3(pq * 0.9);
    float dt = fbm3(pq * 2.7 + 5.0);
    float cuerpo = nb * 0.70 + dt * 0.30;
    float dens = smoothstep(0.30, 0.70, cuerpo);
    // luz: se compara con el punto un poco más abajo (si ahí hay menos humo, este borde mira a la luz del horizonte)
    float nb2 = fbm3((pq + vec3(0.0, -0.16, 0.0)) * 0.9);
    float lit = clamp(0.45 + (nb - nb2) * 5.5, 0.0, 1.0);
    vec3 sombra = mix(vec3(0.22, 0.03, 0.05), vec3(0.110, 0.004, 0.010), fase);
    vec3 luz = mix(vec3(0.80, 0.14, 0.10), vec3(0.86, 0.100, 0.050), fase);
    vec3 cn = mix(sombra, luz, lit * (0.35 + 0.65 * (1.0 - ah)));
    cn += mix(vec3(0.0), vec3(0.85, 0.30, 0.10), fase) * pow(lit, 3.0) * pow(1.0 - ah, 2.0) * 0.22;   // borde de brasa cerca del horizonte
    cn *= 0.78 + 0.44 * dt;
    float cubre = dens * smoothstep(-0.01, 0.06, h) * (0.65 + 0.35 * fase);
    vec3 col = mix(base, cn, clamp(cubre, 0.0, 0.90));
    vec4 nv = nubesVol(d, fase, 1.0e9);
    col = mix(col, nv.rgb, clamp(nv.a * (0.35 + 0.65 * fase), 0.0, 0.95));

    // 3) bruma del horizonte: la luz de abajo se cuela entre las nubes
    float bruma = exp(-max(h, 0.0) * 6.5);
    float vari = 0.65 + 0.7 * fbm2(d * 2.6 + vec3(t * 0.01, 0.0, 6.0));          // la bruma no es un color liso: hay zonas más claras y más apagadas
    vec3 horN = mix(hor, vec3(0.96, 0.24, 0.10), 0.45 * fase * smoothstep(0.45, 0.85, vari));
    col = mix(col, horN * vec3(1.0, 0.92, 0.9), bruma * 0.24 * vari * (0.55 + 0.45 * fase));

    // 4) polvo de los impactos: naranja marrón, más espeso abajo
    float n2 = Polvo > 0.01 ? fbm2(vec3(qb * 1.9 + mov, 4.2)) : 0.5;
    vec3 pzen = vec3(0.38, 0.045, 0.035);
    vec3 phor = vec3(0.88, 0.17, 0.09);
    vec3 polvo = mix(phor, pzen, smoothstep(0.0, 0.75, ah)) * (0.78 + 0.45 * n2) * (0.85 + 0.3 * lit);
    float pw = Polvo * (1.0 - 0.45 * smoothstep(0.35, 1.0, ah));
    col = mix(col, polvo, clamp(pw, 0.0, 1.0));

    // por debajo del horizonte solo se oscurece; la niebla de Minecraft manda ahí
    col *= 1.0 - 0.30 * smoothstep(0.0, -0.4, h);
    // un empujón más hacia el rojo: el cielo entero se ve ensangrentado, no solo el horizonte
    col *= vec3(1.14, 0.80, 0.78);
    return col;
}


// ------------------------------------------------------------------ piezas del meteorito

// bola del meteorito (como en el video): roca mate marrón-grisácea, apagada, con manchas suaves y alguna veta pálida muy tenue.
// El borde fino crema y el resplandor los pone el modo 2; la luz viene de abajo a la izquierda (el resplandor del horizonte).
vec4 cabeza(vec2 p, float sem) {
    float r = length(p);
    if (r > 1.0) return vec4(0.0);
    float z = sqrt(1.0 - r * r);
    vec3 n = vec3(p, z);
    float g = Tiempo * 0.08 + sem;
    float cg = cos(g), sg = sin(g);
    vec3 q = vec3(n.x * cg - n.z * sg, n.y, n.x * sg + n.z * cg);
    vec3 w = vec3(fbm2(q * 1.8 + sem), fbm2(q * 1.8 + sem + 11.0), fbm2(q * 1.8 + sem + 23.0));
    vec3 qq = q * 2.0 + (w - 0.5) * 1.2;
    float f = fbm3(qq + sem);                       // manchas grandes
    float m = fbm3(q * 7.0 + sem);                  // grano fino
    vec3 roca = mix(vec3(0.30, 0.135, 0.10), vec3(0.50, 0.25, 0.17), smoothstep(0.30, 0.70, f));
    roca *= 0.85 + 0.30 * m;
    // vetas pálidas casi imperceptibles
    float h = fbm2(qq * 0.95 + 5.0);
    float vena = exp(-pow((h - 0.52) / 0.018, 2.0)) * 0.07;
    roca = mix(roca, vec3(0.85, 0.55, 0.38), vena);
    // luz suave: más clara hacia abajo a la izquierda, algo más oscura arriba a la derecha
    float luz = 0.80 + 0.35 * dot(n, normalize(vec3(-0.45, -0.55, 0.70)));
    vec3 col = roca * luz;
    // el borde recibe el calor del anillo
    col = mix(col, vec3(0.95, 0.50, 0.22), pow(r, 7.0) * 0.45);
    return vec4(col, 1.0 - smoothstep(0.975, 1.0, r));
}


void cuerpo() {
    // ================================================================== MALLAS 3D REALES (la normal viaja en vColor.rgb; vUV.x = largo 0..1, vUV.y = vuelta 0..1)
    if (Modo > 16.5) {
        // ---------------------------------------------- 17: FUNDA DE AIRE ENCENDIDO (gota: cuenco delante + tubo detrás), una cáscara gruesa y suave calculada por píxel.
        // La densidad es una campana alrededor de la superficie de la gota; se suma a lo largo del rayo, así de canto atraviesa más y la orilla brilla sin rayas.
        // HumoA: xyz = centro de la bola (rel. cámara), w = largo del tubo detrás del centro (bloques); HumoB: xyz = hacia atrás (unitario), w = radio de la bola;
        // HumoC: x = opacidad, y = semilla, z = cuánto se ha agrandado, w = largo total de la gota en radios (desde la punta).
        vec3 C = HumoA.xyz;
        float L = HumoA.w;
        vec3 T = normalize(HumoB.xyz);
        float r = HumoB.w;
        float semi = HumoC.y;
        float Ltot = HumoC.w;
        float tam = HumoC.z;
        float fw = 5.4 * r;                        // cuánto sale el volumen por delante del centro
        float Rc = r * (6.2 * tam + 3.6);          // radio del cilindro que lo contiene (igual que en Java)
        vec3 d = normalize(vPos);
        float tw = length(vPos);
        vec3 oc = -C;
        vec3 dp = d - T * dot(d, T);
        vec3 op = oc - T * dot(oc, T);
        float qa = dot(dp, dp);
        float qb = dot(op, dp);
        float qc = dot(op, op) - Rc * Rc;
        float disc = qb * qb - qa * qc;
        if (qa < 1e-8 || disc <= 0.0) { discard; }
        float sq = sqrt(disc);
        float tin = (-qb - sq) / qa;
        float tout = (-qb + sq) / qa;
        float tol = 0.03 * tout + 0.02 * Rc + 0.5;
        float dT = dot(d, T);
        float sC = dot(C, T);                       // s de un punto p: dot(p - C, T) = t*dT - sC
        float sMin = -fw;
        float sMax = L + r;
        if (abs(dT) > 1e-5) {
            float ta = (sMin + sC) / dT;
            float tb = (sMax + sC) / dT;
            tin = max(tin, min(ta, tb));
            tout = min(tout, max(ta, tb));
        } else if (-sC < sMin || -sC > sMax) { discard; }
        if (tout <= tin || tout <= 0.0) { discard; }
        if (tin < 0.0) {
            if (abs(tw - tout) > tol) { discard; }
            tin = 0.0;
        } else if (tw > tin + tol) { discard; }
        // la bola tapa lo de atrás
        {
            float bb = dot(-C, d);
            float hb = bb * bb - (dot(C, C) - r * r);
            if (hb > 0.0) {
                float tb2 = -bb - sqrt(hb);
                if (tb2 > 0.0) { tout = min(tout, tb2); }
            }
        }
        if (tout <= tin) { discard; }
        vec3 e1 = normalize(cross(T, abs(T.y) > 0.9 ? vec3(1.0, 0.0, 0.0) : vec3(0.0, 1.0, 0.0)));
        vec3 e2 = cross(T, e1);
        const int PAS = 24;
        float dt = (tout - tin) / float(PAS);
        float jit = fract(52.9829189 * fract(dot(gl_FragCoord.xy, vec2(0.06711056, 0.00583715))));
        float t = tin + dt * jit;
        float S = 0.0;
        float Hc = 0.0;
        for (int i = 0; i < PAS; i++) {
            vec3 rel = d * t - C;
            float sb = dot(rel, T);                 // bloques detrás del centro (negativo: delante)
            float s = sb / r;
            float dr = s + 3.0;                     // radios desde la punta delantera
            if (dr > 0.0) {
                vec3 perp = rel - T * sb;
                float w = length(perp) / r;
                float Rm = 6.2 * tam;
                float qd = clamp((Rm - dr) / Rm, 0.0, 1.0);
                float R = Rm * sqrt(1.0 - qd * qd) * sqrt(clamp((Ltot - dr) / 4.5, 0.0, 1.0));   // redondeada también atrás
                float ang = atan(dot(perp, e2), dot(perp, e1));
                // la cáscara se vuelve más gruesa y turbulenta hacia atrás; el frente (onda de choque) es fino y nítido
                float atrasK = smoothstep(0.0, 40.0, dr);
                float grosor = 0.36 + 0.55 * atrasK;
                float vet = fbm3(vec3(cos(ang) * 1.6 + semi, sin(ang) * 1.6, s * 0.35 - Tiempo * 1.3));
                float torb = fbm3(vec3(cos(ang) * 3.4 + semi * 1.3, sin(ang) * 3.4, s * 0.9 - Tiempo * 2.2));
                float Rw = R * (1.0 + 0.10 * atrasK * (torb - 0.5));          // la orilla ondula hacia atrás como llamas
                float dens = exp(-0.5 * (w - Rw) * (w - Rw) / grosor);
                // capa interior tenue (aire encendido dentro de la cáscara)
                dens += 0.22 * (1.0 - smoothstep(0.0, 1.0, w / max(Rw, 0.1))) * (1.0 - 0.6 * atrasK);
                float fin = smoothstep(0.0, 2.5, Ltot - dr);
                float vivo = 1.0 + 0.9 * (1.0 - smoothstep(0.0, 6.0, dr));    // frente mucho más brillante
                // lenguas de fuego que corren hacia atrás
                float lengua = smoothstep(0.55, 0.9, torb) * atrasK;
                S += dens * fin * vivo * (0.50 + 1.0 * vet + 0.8 * lengua) * dt / r;
                float cal = exp(-max(dr, 0.0) / 14.0);                        // calor: 1 junto a la bola, 0 muy atrás
                Hc += dens * fin * cal * dt / r;
            }
            t += dt;
        }
        float a = (1.0 - exp(-S * 0.17)) * HumoC.x;
        if (a < 0.003) { discard; }
        // color: blanco-amarillo caliente junto a la bola -> naranja -> rojo oscuro muy atrás
        float calor = clamp(Hc / max(S, 1e-3) * 1.6, 0.0, 1.0);
        vec3 col = mix(vec3(1.0, 0.17, 0.02), vec3(1.0, 0.48, 0.07), smoothstep(0.0, 0.5, calor));
        col = mix(col, vec3(1.0, 0.85, 0.40), smoothstep(0.45, 1.0, calor));
        fragColor = vec4(col * a * 2.6 + (jit - 0.5) / 255.0, a * 0.65);
        return;
    }
    if (Modo > 15.5) {
        // ---------------------------------------------- 16: HUMO VOLUMÉTRICO de la estela: una columna de humo de verdad, calculada por píxel
        // (bultos como coliflor, remolinos, más ancha y deshecha cuanto más vieja, iluminada por el fuego de la cabeza y por el cielo naranja)
        vec3 A = HumoA.xyz;
        float L = HumoA.w;
        vec3 T = normalize(HumoB.xyz);
        float r = HumoB.w;
        float sem = HumoC.y;
        float edadS = HumoC.z;
        vec3 d = normalize(vPos);
        float tw = length(vPos);
        float BR = Modo > 16.5 ? 1.0 : 0.0;   // 17: bruma exterior (naranja rojiza, suave, más ancha que el humo)
        float esc = 1.0 + 1.6 * BR;
        float Rm = r * 11.5 * (1.0 + 0.04 * edadS) * esc;
        vec3 oc = -A;
        vec3 dp = d - T * dot(d, T);
        vec3 op = oc - T * dot(oc, T);
        float qa = dot(dp, dp);
        float qb = dot(op, dp);
        float qc = dot(op, op) - Rm * Rm;
        float disc = qb * qb - qa * qc;
        if (qa < 1e-8 || disc <= 0.0) { discard; }
        float sq = sqrt(disc);
        float tin = (-qb - sq) / qa;
        float tout = (-qb + sq) / qa;
        if (tout <= 0.0) { discard; }
        // el volumen es un cilindro CERRADO (con tapas) que va de s=0 a s=L: se recorta con las tapas y solo vale la cara que toca la malla
        float tol = 0.03 * tout + 0.02 * Rm + 0.5;
        float dT = dot(d, T);
        float sA = dot(A, T);
        if (abs(dT) > 1e-5) {
            float ta = sA / dT;
            float tb = (L + sA) / dT;
            tin = max(tin, min(ta, tb));
            tout = min(tout, max(ta, tb));
        } else if (sA < 0.0 || sA > L) { discard; }
        if (tout <= tin || tout <= 0.0) { discard; }
        if (tin < 0.0) {
            if (abs(tw - tout) > tol) { discard; }   // la cámara está dentro: vale la cara de atrás (lateral o tapa)
            tin = 0.0;
        } else if (tw > tin + tol) { discard; }      // fuera: solo la cara de delante
        vec3 e1 = normalize(cross(T, abs(T.y) > 0.9 ? vec3(1.0, 0.0, 0.0) : vec3(0.0, 1.0, 0.0)));
        vec3 e2 = cross(T, e1);
        const int PAS = 26;
        float dt = (tout - tin) / float(PAS);
        float jit = fract(52.9829189 * fract(dot(gl_FragCoord.xy, vec2(0.06711056, 0.00583715))));
        float t = tin + dt * jit;
        vec3 colA = vec3(0.0);
        float Tr = 1.0;
        float K = (0.50 - 0.36 * BR) / r;
        for (int i = 0; i < PAS; i++) {
            vec3 p = d * t;
            vec3 rel = p - A;
            float s = dot(rel, T);
            if (s > 0.0 && s < L) {
                vec3 perp = rel - T * s;
                float u = s / L;
                float edad = clamp(s / (0.8 * L), 0.0, 1.0);
                float Rs = r * (2.9 + 7.4 * pow(edad, 0.8)) * (1.0 + 0.04 * edadS) * esc;   // rodea a la llama y se abre hacia atrás
                float sT = L - s;                      // distancia desde donde empezó (queda fija en el mundo mientras el meteorito avanza)
                vec2 pe = vec2(dot(perp, e1), dot(perp, e2)) / Rs;
                // la columna serpentea y se tuerce como el humo de verdad
                pe += (1.0 - BR) * 0.30 * vec2(sin(sT / (r * 13.0) + sem), cos(sT / (r * 17.0) + sem * 1.7)) * (0.3 + edad);
                vec3 np = vec3(pe * 1.5, sT / (r * 4.5)) + sem;
                vec2 wv = vec2(fbm3(np + 3.1), fbm3(np + 7.7)) - 0.5;
                pe += (1.0 - BR) * wv * (0.30 + 0.70 * edad);
                float bulto = fbm3(vec3(pe * 2.1, sT / (r * 3.0)) + sem * 1.7);
                float fino = fbm3(vec3(pe * 5.5, sT / (r * 1.6)) + sem * 0.7);
                float borde = length(pe) + (1.0 - 0.9 * BR) * ((bulto - 0.5) * 0.95 + (fino - 0.5) * 0.25 * edad);
                float dn = 1.0 - smoothstep(0.30 - 0.55 * BR, 1.0, borde);
                dn *= mix(0.50 + 1.0 * bulto, 0.85 + 0.30 * bulto, BR);
                dn *= smoothstep(0.0, r * 1.8, s) * (0.60 + 0.40 * smoothstep(0.0, r * 5.0, max(s - HumoC.w, 0.0))) * (1.0 - smoothstep(0.85, 1.0, u));   // cerca de la bola es más ligero para que se vea el fuego
                dn = clamp(dn, 0.0, 1.2);
                if (dn > 0.015) {
                    // luz: el cielo naranja desde arriba (con sombra propia) y el fuego de la cabeza que ilumina lo cercano
                    float sombra = 0.0;
                    {
                        vec3 q2 = p + vec3(0.0, 1.0, 0.0) * Rs * 0.7;
                        vec3 rel2 = q2 - A;
                        float s2 = dot(rel2, T);
                        vec3 pp2 = rel2 - T * s2;
                        vec2 pe2 = vec2(dot(pp2, e1), dot(pp2, e2)) / Rs;
                        float bb = fbm3(vec3(pe2 * 2.1, (L - s2) / (r * 3.0)) + sem * 1.7);
                        sombra = (1.0 - smoothstep(0.30, 1.0, length(pe2) + (bb - 0.5) * 0.95)) * step(0.0, s2) * step(s2, L);
                    }
                    float luz = exp(-sombra * 1.8);
                    vec3 oscuro = vec3(0.16, 0.095, 0.105);
                    vec3 cielo = vec3(0.68, 0.39, 0.23);
                    vec3 c = mix(oscuro, cielo, clamp(luz * (0.35 + 0.55 * dot(normalize(perp + 1e-4), vec3(0.0, 1.0, 0.0)) * 0.5 + 0.2), 0.0, 1.0));
                    float fuego = exp(-max(s - HumoC.w, 0.0) / (r * 10.0));
                    c += vec3(1.0, 0.46, 0.12) * fuego * (0.35 + 0.65 * (1.0 - length(pe) * 0.7)) * 0.9;
                    c += vec3(0.20, 0.07, 0.03) * (1.0 - dn) * luz * 0.5;
                    if (BR > 0.5) {
                        // bruma: gas caliente naranja-rojizo, sin zonas negras; más brillante cerca del fuego y más rojo al alejarse
                        float calor = clamp(fuego * 1.3 + 0.35 * luz, 0.0, 1.0);
                        c = mix(vec3(0.90, 0.46, 0.12), vec3(1.0, 0.80, 0.34), calor) * (0.85 + 0.30 * bulto);
                        dn *= 0.55;
                    }
                    float al = 1.0 - exp(-dn * K * dt);
                    colA += Tr * al * c;
                    Tr *= 1.0 - al;
                    if (Tr < 0.03) { break; }
                }
            }
            t += dt;
        }
        float alfa = (1.0 - Tr) * HumoC.x;
        if (alfa < 0.004) { discard; }
        fragColor = vec4(colA / max(1.0 - Tr, 1e-3), clamp(alfa, 0.0, 1.0));
        return;
    }
    if (Modo > 14.5) {
        // chispa incandescente: punto blanco-amarillo con borde naranja que se funde (vColor = color y brillo)
        float r = length(vUV * 2.0 - 1.0);
        float g = exp(-r * r * 7.0);
        float c = exp(-r * r * 30.0);
        fragColor = vec4(mix(vColor.rgb, vec3(1.0, 0.95, 0.75), c), clamp(g * vColor.a, 0.0, 1.0));
        return;
    }
    if (Modo > 11.5) {
        vec3 N = normalize(vColor.rgb * 2.0 - 1.0);
        if (Modo < 12.5) {
            // la bola es una esfera exacta calculada por píxel sobre un cartel: el contorno es un círculo perfecto (sin vértices)
            vec3 d0 = normalize(vPos);
            vec3 Cc = HumoA.xyz;
            float rb = HumoA.w;
            float bb = dot(-Cc, d0);
            float hb = bb * bb - (dot(Cc, Cc) - rb * rb);
            if (hb <= 0.0) { discard; }
            float th = -bb - sqrt(hb);
            N = normalize(d0 * th - Cc);
        }
        vec3 V = normalize(-vPos);                  // de este punto hacia la cámara (vPos ya es relativo a la cámara)
        if (dot(N, V) < 0.0 && Modo < 12.5) { N = -N; }   // la bola solo enseña su cara visible
        float nv = clamp(abs(dot(N, V)), 0.0, 1.0);
        float sem = Semilla;
        // ---------------------------------------------- 12: la bola de roca (esfera de verdad, gira despacio)
        if (Modo < 12.5) {
            // bola de lava: esfera lisa, manchas naranjas y rojo oscuro, grietas finas de magma crema, borde rosa-blanco que arde
            float g = Tiempo * 0.10 + sem;
            float cg = cos(g), sg = sin(g);
            vec3 q = vec3(N.x * cg - N.z * sg, N.y, N.x * sg + N.z * cg);
            vec3 w = vec3(fbm2(q * 1.6 + sem), fbm2(q * 1.6 + sem + 11.0), fbm2(q * 1.6 + sem + 23.0));
            vec3 qq = q * 1.9 + (w - 0.5) * 1.5;
            float f = fbm3(qq + sem);
            float m = fbm3(q * 4.0 + sem + 3.0);
            vec3 col = mix(vec3(0.55, 0.09, 0.02), vec3(1.0, 0.56, 0.08), smoothstep(0.25, 0.62, f));
            col = mix(col, vec3(1.0, 0.76, 0.20), smoothstep(0.55, 0.80, f) * 0.7);
            col *= 0.85 + 0.30 * m;
            // grietas: líneas finas donde el ruido cruza un valor (crema brillante con borde naranja)
            float lin = abs(fbm3(qq * 1.15 + sem + 40.0) - 0.5);
            float crack = 1.0 - smoothstep(0.012, 0.05, lin);
            float core = 1.0 - smoothstep(0.0, 0.016, lin);
            col = mix(col, vec3(1.0, 0.72, 0.38), crack * 0.85);
            col = mix(col, vec3(1.0, 0.95, 0.80), core);
            // luz suave: más claro hacia el centro, más oscuro en el limbo antes del borde ardiente
            col *= 0.80 + 0.35 * nv;
            float fr = 1.0 - nv;
            col = mix(col, vec3(1.0, 0.86, 0.80), smoothstep(0.72, 0.97, fr) * 0.8);   // borde rosa-blanco suave
            fragColor = vec4(col, smoothstep(0.0, 0.30, nv));   // el contorno se funde suave (sin escalones de píxeles)
            return;
        }
        float u = vUV.x;
        float ang = vUV.y * 6.2832;
        vec2 ca = vec2(cos(ang), sin(ang));
        float edge = 1.0 - nv;
        // ---------------------------------------------- 13: FUNDA DE AIRE ENCENDIDO en forma de gota (cuenco delante + tubo atrás), varias capas finas de una cáscara gruesa y suave.
        // De canto se atraviesa más aire: la orilla brilla. Mitad tapa, mitad suma (alfa premultiplicado: color * a, alfa a * 0.5), con vetas que corren hacia atrás.
        if (Modo < 13.5) {
            float vetas = fbm3(vec3(u * 2.0 - Tiempo * 0.9, ca * 2.2 + sem));
            float camino = clamp(0.40 / max(nv, 0.26), 0.35, 1.6);
            float s = 0.16 * camino * (0.55 + 0.9 * vetas) * vColor.a;
            float a = 1.0 - exp(-s * 1.7);
            vec3 col = vec3(1.0, 0.62, 0.32);
            fragColor = vec4(col * a * 1.5, a * 0.5);
            return;
        }
        // ---------------------------------------------- 14: CONO DE FUEGO (sale de la bola, estrecho, se abre hacia atrás y se apaga; naranja brillante)
        float lenguas = fbm3(vec3(u * 4.0 - Tiempo * 1.6, ca * 2.0 + sem));
        float llena = 0.40 + 0.60 * nv;
        float a = llena * (1.0 - smoothstep(0.0, 1.0, u)) * (0.60 + 0.80 * lenguas) * vColor.a;
        a += pow(edge, 3.0) * 0.25 * (1.0 - u) * vColor.a;
        vec3 col = mix(vec3(1.0, 0.82, 0.42), vec3(0.96, 0.40, 0.10), smoothstep(0.0, 0.75, u));
        col = mix(col, vec3(1.0, 0.92, 0.70), (1.0 - smoothstep(0.0, 0.25, u)) * nv * 0.6);
        fragColor = vec4(col, clamp(a * 0.85, 0.0, 1.0));
        return;
    }

    // ------------------------------------------------------------------ cielo
    if (Modo < 0.5) {
        fragColor = vec4(cielo(normalize(vPos)), clamp(Fuerza, 0.0, 1.0));
        return;
    }

    // ------------------------------------------------------------------ cabeza (esfera de lava, de cara a la cámara)
    if (Modo < 1.5) {
        vec2 p = vUV * 2.0 - 1.0;
        vec4 c = cabeza(p, Semilla);
        fragColor = vec4(c.rgb, c.a * vColor.a);
        return;
    }

    // ------------------------------------------------------------------ resplandor: borde fino crema pegado a la bola y un halo naranja corto
    if (Modo < 2.5) {
        // resplandor de la bola incandescente: una luz que se expande (sin anillos), blanca en el centro, naranja y roja hacia fuera
        vec2 p = vUV * 2.0 - 1.0;
        float r = length(p);
        float nucleo = exp(-pow(max(r - 0.136, 0.0) / 0.034, 2.0)) * 0.80 + 0.35 * exp(-pow(max(r - 0.136, 0.0) / 0.07, 2.0));
        float medio = exp(-r * 8.5);
        float lejos = exp(-r * 3.2);
        // remolino de polvo caliente alrededor de la cabeza (como en los videos: anillo suave naranja con vuelta irregular)
        float ang = atan(p.y, p.x);
        float vuelta = 0.65 + 0.55 * sin(ang * 2.0 + r * 9.0 - Tiempo * 1.3 + Semilla) * sin(ang * 3.0 - r * 5.0 + Semilla * 1.7);
        float remo = exp(-pow((r - 0.40) / 0.12, 2.0)) * vuelta + 0.6 * exp(-pow((r - 0.62) / 0.15, 2.0)) * (1.1 - vuelta);
        vec3 col = vec3(1.0, 0.93, 0.70) * nucleo * 0.9 + vec3(1.0, 0.58, 0.14) * medio * 0.85 + vec3(0.90, 0.22, 0.06) * lejos * 0.30 + vec3(0.95, 0.46, 0.17) * remo * 0.12;
        float a = clamp(nucleo * 0.9 + medio * 0.55 + lejos * 0.22 + remo * 0.10, 0.0, 1.0) * smoothstep(0.095, 0.136, r) * vColor.a * (0.94 + 0.06 * sin(Tiempo * 23.0 + Semilla));
        fragColor = vec4(col, a);
        return;
    }


    // ------------------------------------------------------------------ estela de fuego (se suma)
    if (Modo < 3.5) {
        float u = vUV.x;
        float v = vUV.y * 2.0 - 1.0;
        float tur = fbm3(vec3(u * 7.0 - Tiempo * 2.2, v * 2.5, Semilla));
        float ancho = 1.0 - smoothstep(0.0, 1.0, abs(v) + (tur - 0.5) * 0.7 * u);
        float lar = pow(1.0 - u, 1.3);
        vec3 col = mix(vec3(1.0, 0.95, 0.80), vec3(1.0, 0.55, 0.12), smoothstep(0.0, 0.22, u));
        col = mix(col, vec3(0.80, 0.14, 0.03), smoothstep(0.2, 0.7, u));
        float a = ancho * lar * (0.55 + 0.6 * tur) * vColor.a;
        fragColor = vec4(col * 1.15, clamp(a, 0.0, 1.0));
        return;
    }

    // ------------------------------------------------------------------ estela de humo
    if (Modo < 4.5) {
        float u = vUV.x;
        float v = vUV.y * 2.0 - 1.0;
        float tur = fbm3(vec3(u * 4.0 - Tiempo * 0.6, v * 1.8, Semilla + 3.0));
        float ancho = 1.0 - smoothstep(0.0, 1.0, abs(v) * (0.7 + 0.5 * u) + (tur - 0.5) * 0.6);
        float a = ancho * smoothstep(0.0, 0.08, u) * (1.0 - u) * 0.75 * vColor.a;
        vec3 col = mix(vec3(0.28, 0.08, 0.05), vec3(0.10, 0.07, 0.07), u);
        fragColor = vec4(col, clamp(a, 0.0, 1.0));
        return;
    }

    // ------------------------------------------------------------------ bocanadas del impacto
    if (Modo < 5.5) {
        vec2 p = vUV * 2.0 - 1.0;
        float r = length(p);
        float tono = vColor.r;
        float sem = vColor.g * 40.0;
        float fuego = vColor.b;
        float n = fbm3(vec3(p * 2.2, sem));
        float borde = 1.0 - smoothstep(0.55 + 0.25 * n, 1.0, r);
        if (borde <= 0.0) { fragColor = vec4(0.0); return; }
        float luz = 0.45 + 0.7 * n;
        vec3 polvo = mix(vec3(0.20, 0.07, 0.05), vec3(0.62, 0.30, 0.15), tono * luz);
        vec3 ardiente = mix(vec3(1.0, 0.45, 0.08), vec3(1.0, 0.92, 0.62), pow(1.0 - r, 1.5) * 0.9);
        vec3 col = mix(polvo, ardiente, clamp(fuego * (0.4 + 0.9 * (1.0 - r)), 0.0, 1.0));
        fragColor = vec4(col, borde * vColor.a * 0.9);
        return;
    }

    // ------------------------------------------------------------------ color plano
    if (Modo < 6.5) {
        fragColor = vColor;
        return;
    }

    // ------------------------------------------------------------------ onda expansiva
    if (Modo < 7.5) {
        float v = vUV.y;
        float banda = smoothstep(0.0, 0.7, v) * (1.0 - smoothstep(0.7, 1.0, v));
        float n = fbm3(vec3(vUV.x * 30.0, v * 3.0, Semilla));
        fragColor = vec4(vec3(0.55, 0.22, 0.10) * (0.7 + 0.6 * n), banda * (0.4 + 0.6 * n) * vColor.a);
        return;
    }

    // ------------------------------------------------------------------ estela interior: haz amarillo-naranja luminoso con vetas y brasas (se suma)
    if (Modo > 10.5) {
        float u = vUV.x;
        float v = vUV.y * 2.0 - 1.0;
        float sem = Semilla;
        float uu = clamp(u, 0.0, 1.0);
        // angosto junto a la bola y cada vez más ancho hacia atrás (no se cierra: se desvanece), con la punta delantera redondeada
        float cap = clamp(uu / 0.20, 0.0, 1.0);
        float w = 0.78 * (0.62 + 0.38 * pow(uu, 0.9)) * sqrt(1.0 - (1.0 - cap) * (1.0 - cap));   // punta delantera redondeada, sin corte recto
        float dd = abs(v) / max(w, 0.02);
        if (dd > 1.3 || u > 1.0) { fragColor = vec4(0.0); return; }
        float vetas = 0.80 + 0.40 * fbm3(vec3(u * 1.6 - Tiempo * 0.8, v * 9.0, sem + 3.0));
        float turb = 0.80 + 0.40 * fbm3(vec3(u * 4.0 - Tiempo * 0.5, v * 2.5, sem));
        float nucleo = exp(-pow(dd / 0.78, 2.0));
        float borde = exp(-pow((dd - 0.95) / 0.25, 2.0)) * 0.25;
        float largo = pow(1.0 - uu, 0.9);
        float cab = exp(-uu * 8.0);
        float a = (nucleo * 0.34 + borde * 0.6) * vetas * turb * largo * smoothstep(0.0, 0.02, u) + cab * nucleo * 0.16;
        a *= 1.0 - smoothstep(0.95, 1.28, dd);                     // bordes sin corte
        vec3 col = mix(vec3(0.95, 0.42, 0.17), vec3(1.0, 0.62, 0.28), clamp(cab * 1.2 + nucleo * largo * 0.3, 0.0, 1.0));
        // brasas: puntos brillantes que se van hacia atrás
        vec2 gq = vec2(uu * 26.0 - Tiempo * 2.5, v * 7.0 + sem);
        vec2 id = floor(gq);
        vec2 fr = fract(gq) - 0.5;
        float hh = hash(vec3(id, sem));
        float punto = step(0.972, hh) * exp(-dot(fr, fr) * (70.0 + 140.0 * fract(hh * 91.0))) * step(dd, 1.1);
        a += punto * 0.60 * largo;
        col = mix(col, vec3(1.0, 0.70, 0.35), clamp(punto, 0.0, 1.0));
        fragColor = vec4(col, clamp(a, 0.0, 1.0) * vColor.a);
        return;
    }


    // ------------------------------------------------------------------ la estela: dos cáscaras translúcidas en forma de gota, suaves
    if (Modo > 9.5) {
        float u = vUV.x;                       // 0 = en el meteorito, 1 = la punta de la cola
        float v = vUV.y * 2.0 - 1.0;           // a lo ancho
        float sem = Semilla;
        float n = fbm3(vec3(u * 3.0, v * 1.6, Tiempo * 0.15 + sem));
        float uu = clamp(u, 0.0, 1.0);
        // forma de huevo: elipse redondeada por los dos extremos (sin puntas), angosta junto a la bola y ancha hacia atrás
        float up = pow(uu, 0.92);
        float w = 0.78 * sqrt(max(4.0 * up * (1.0 - up), 0.0)) * (0.97 + 0.06 * n);
        float d = abs(v) / max(w, 0.02);
        if (d > 1.4 || u > 1.0) { fragColor = vec4(0.0); return; }
        // sin rayas: una niebla suave y densa en el centro, un borde difuso (más marcado hacia un lado, como en el video) y vetas de humo
        float dd = clamp(d, 0.0, 1.4);
        float cuerpo = pow(clamp(1.0 - dd * dd, 0.0, 1.0), 1.4);
        float rim = exp(-pow((dd - 0.93) / 0.17, 2.0)) * (0.55 + 0.45 * clamp(-v * sign(w) * 0.0 + 0.5 + 0.5 * sin(u * 2.0 + sem), 0.0, 1.0));
        float interior = exp(-pow(dd / 0.55, 2.0));
        float vetas = 0.75 + 0.5 * fbm3(vec3(u * 5.0, v * 2.2, Tiempo * 0.12 + sem + 5.0));
        float cola = 1.0 - smoothstep(0.88, 1.0, u);
        float cab = 1.0 - smoothstep(0.0, 0.38, abs(u - 0.15));
        float a = (cuerpo * 0.27 + rim * 0.18 + interior * cab * 0.20) * vetas * cola;
        a *= smoothstep(0.0, 0.012, u);
        vec3 col = mix(vec3(1.0, 0.45, 0.26), vec3(1.0, 0.70, 0.42), cab * 0.75 + rim * 0.2);
        fragColor = vec4(col, clamp(a, 0.0, 1.0) * vColor.a);
        return;
    }


    // ------------------------------------------------------------------ humo / polvo suave (sin bordes: neblina que se funde)
    if (Modo > 8.5) {
        if (vUV.x > 5.0) {
            // BOCANADA DE LA ESTELA: vColor.r = edad (0 nace .. 1 se va), g = semilla, a = cuánto; amarillo -> naranja -> humo marrón
            vec2 pe = (vUV - vec2(10.0, 0.0)) * 2.0 - 1.0;
            float e = vColor.r;
            float sm = vColor.g * 40.0;
            float ang = sm * 6.2832;
            vec2 q = mat2(cos(ang), -sin(ang), sin(ang), cos(ang)) * pe;
            float n = fbm2(vec3(q * 1.5 + sm, e * 1.6 + sm));
            float rr = length(pe);
            float forma = 1.0 - smoothstep(0.10 + 0.25 * e, 0.95, rr + 1.0 * (n - 0.5));
            vec3 c = mix(vec3(1.0, 0.80, 0.42), vec3(0.97, 0.50, 0.14), smoothstep(0.0, 0.07, e));
            c = mix(c, vec3(0.66, 0.28, 0.08), smoothstep(0.07, 0.25, e));
            c = mix(c, vec3(0.44, 0.18, 0.07), smoothstep(0.25, 0.55, e));
            c = mix(c, vec3(0.28, 0.11, 0.06), smoothstep(0.55, 1.0, e));
            c *= 0.60 + 0.80 * n;
            c += vec3(1.0, 0.75, 0.35) * (1.0 - smoothstep(0.0, 0.12, e)) * (1.0 - smoothstep(0.0, 0.7, rr)) * 0.30;   // joven: núcleo encendido
            float ram = mix(0.85, 0.78, smoothstep(0.0, 0.12, e));
            ram = mix(ram, 0.59, smoothstep(0.12, 0.35, e));
            ram = mix(ram, 0.34, smoothstep(0.35, 0.65, e));
            ram = mix(ram, 0.0, smoothstep(0.65, 1.0, e));
            float al = forma * ram * (0.55 + 0.9 * n) * vColor.a;
            fragColor = vec4(c, clamp(al, 0.0, 1.0));
            return;
        }
        vec2 p = vUV * 2.0 - 1.0;
        float sem = vColor.g * 40.0;
        float calor = vColor.r;
        float osc = vColor.b;
        // el contorno se deforma con ruido de baja frecuencia para que no parezca un círculo
        vec2 w = vec2(fbm2(vec3(p * 1.3, sem)), fbm2(vec3(p * 1.3 + 7.0, sem))) - 0.5;
        vec2 q = p + w * 0.55;
        float r = length(q);
        float n = fbm3(vec3(q * 1.6, sem * 1.1 + Tiempo * 0.03));
        float n2 = fbm3(vec3(q * 3.2 + 4.0, sem * 1.3));
        // caída gaussiana: densa en el centro y desvanecida hacia fuera, sin corte
        float dens = exp(-r * r * (2.6 - 0.8 * n)) * (1.0 - smoothstep(0.80, 1.0, r));
        if (dens <= 0.002) { fragColor = vec4(0.0); return; }
        // luz suave desde abajo a la izquierda (el resplandor del horizonte), sin relieve de bola
        float diff = clamp(0.55 - 0.22 * q.x - 0.28 * q.y, 0.0, 1.0);
        vec3 claro = mix(vec3(0.98, 0.50, 0.20), vec3(0.55, 0.24, 0.13), osc);
        vec3 oscuro = mix(vec3(0.60, 0.25, 0.12), vec3(0.22, 0.09, 0.07), osc);
        vec3 col = mix(oscuro, claro, diff * (0.7 + 0.5 * n2));
        vec3 brasa = mix(vec3(0.95, 0.30, 0.06), vec3(1.0, 0.75, 0.38), 1.0 - r);
        col = mix(col, brasa, clamp(calor * (0.25 + 0.7 * (1.0 - r)), 0.0, 1.0) * 0.8);
        float a = dens * (0.45 + 0.55 * n) * (0.8 + 0.4 * n2) * vColor.a;
        fragColor = vec4(col, clamp(a, 0.0, 1.0));
        return;
    }

    // ------------------------------------------------------------------ bola de fuego del impacto (se suma)
    vec2 p = vUV * 2.0 - 1.0;
    float r = length(p);
    float n = fbm3(vec3(p * 2.0, Tiempo * 0.8 + Semilla));
    float a = (1.0 - smoothstep(0.0, 1.0, r + (n - 0.5) * 0.5)) * vColor.a;
    vec3 col = mix(vec3(1.0, 0.45, 0.08), vec3(1.0, 0.95, 0.78), pow(1.0 - r, 2.2));
    fragColor = vec4(col, clamp(a, 0.0, 1.0));
}
// Las nubes del cielo tapan lo que está detrás de ellas: el rastro del meteorito se ve POR DETRÁS de las nubes.
void main() {
    cuerpo();
    if (Modo > 0.5) {
        float tf = length(vPos);
        vec4 nb = nubesVol(vPos / max(tf, 1e-4), 0.5, tf);
        float k = 1.0 - clamp(nb.a * 1.15, 0.0, 0.92);
        if (Modo > 16.5 && Modo < 17.5) { fragColor *= k; } else { fragColor.a *= k; }
    }
}
