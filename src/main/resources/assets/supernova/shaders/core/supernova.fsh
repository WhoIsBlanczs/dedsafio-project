#version 150

in vec4 vColor;
in vec2 vUV;
in vec3 vPos;

uniform sampler2D DepthSampler;   // copia de la profundidad del mundo (para los rayos de luz)
uniform float Time;
uniform float Charge;      // 0 en reposo, 0 -> 1 durante la cuenta atrás
uniform float Expl;        // 0 -> 1 en el primer segundo y medio de la explosión (sale del blanco)
uniform float Fade;        // 1 a pleno, baja a 0 al esfumarse
uniform float Debug;       // diagnóstico: 1 = color sólido magenta
uniform float Mode;        // 0 estrella, 2 explosión, 3 cielo, 4 destello, 5 chorro, 6 color plano, 7 brillo del borde, 8 luz en el suelo, 9 gas
uniform float Intensity;   // brillo general del modo (destello / chorro / borde)
uniform float SkyMix;      // opacidad del cielo de nebulosa
uniform float SkyBright;   // cielo iluminado por la estrella en la cuenta atrás
uniform float SkyDark;     // cielo apagado tras la explosión
uniform vec3 SphereCenter; // centro de la esfera en espacio de vista
uniform vec3 ViewDirW;
uniform vec3 AxX;          // eje X del mundo en espacio de vista
uniform vec3 AxY;
uniform vec3 AxZ;
uniform float Aparece;     // segundos desde que apareció la supernova (para la onda del principio)
uniform vec3 StarDirW;     // dirección (mundo) de la cámara hacia la estrella: los destellos del cielo
uniform float CamY;        // altura real (Y del mundo) de la cámara, para poner las nubes a la altura 200
uniform float Apoc;        // 0 = cielo de siempre, 1 = cielo del fin del mundo (/supernova cielo)
uniform float CloudQ;      // calidad de las nubes: 0 apagadas, 1 baja, 2 media, 3 alta (opción del cliente)
uniform float Radius;      // radio real de la explosión (para el brillo del borde)     // dirección (mundo) del centro de la esfera hacia la cámara

out vec4 fragColor;

// dirección en la esfera a partir de los ángulos (UV = longitud / colatitud): exacta en coma flotante, sin los escalones del color de 8 bits
vec3 dirUV(vec2 uv) {
    float a = uv.y * 3.14159265;
    float b = uv.x * 6.2831853;
    return vec3(sin(a) * cos(b), cos(a), sin(a) * sin(b));
}

float hash(vec3 p) {
    p = fract(p * 0.3183099 + 0.1);
    p *= 17.0;
    return fract(p.x * p.y * p.z * (p.x + p.y + p.z));
}

float noise(vec3 x) {
    vec3 i = floor(x);
    vec3 f = fract(x);
    f = f * f * (3.0 - 2.0 * f);
    return mix(
        mix(mix(hash(i + vec3(0.0, 0.0, 0.0)), hash(i + vec3(1.0, 0.0, 0.0)), f.x),
            mix(hash(i + vec3(0.0, 1.0, 0.0)), hash(i + vec3(1.0, 1.0, 0.0)), f.x), f.y),
        mix(mix(hash(i + vec3(0.0, 0.0, 1.0)), hash(i + vec3(1.0, 0.0, 1.0)), f.x),
            mix(hash(i + vec3(0.0, 1.0, 1.0)), hash(i + vec3(1.0, 1.0, 1.0)), f.x), f.y),
        f.z);
}

float fbm(vec3 p) {
    float a = 0.5;
    float s = 0.0;
    for (int i = 0; i < 5; i++) {
        s += a * noise(p);
        p = p * 2.02 + vec3(13.1, 7.7, 3.3);
        a *= 0.5;
    }
    return s;
}

float fbm3(vec3 p) {
    float a = 0.5;
    float s = 0.0;
    for (int i = 0; i < 3; i++) {
        s += a * noise(p);
        p = p * 2.02 + vec3(13.1, 7.7, 3.3);
        a *= 0.5;
    }
    return s;
}

float h13(vec3 p) {
    p = fract(p * 0.1031);
    p += dot(p, p.zyx + 31.32);
    return fract((p.x + p.y) * p.z);
}

vec3 h33(vec3 p) {
    p = fract(p * vec3(0.1031, 0.1030, 0.0973));
    p += dot(p, p.yxz + 33.33);
    return fract((p.xxy + p.yxx) * p.zyx);
}

// una capa de estrellas: el cielo se parte en celdas y en algunas hay una estrella (punto con brillo suave)
vec3 capaEstrellas(vec3 d, float escala, float densidad, float tam, float brillo) {
    vec3 p = d * escala;
    vec3 c = floor(p);
    vec3 r = h33(c);
    if (r.x > densidad) {
        return vec3(0.0);
    }
    vec3 sitio = normalize((c + 0.25 + 0.5 * h33(c + 7.13)) / escala);
    float dist = length(d - sitio) * escala;
    float t = tam * (0.5 + r.y * r.y * 1.6);
    float luz = exp(-dist * dist / (t * t)) + 0.15 * exp(-dist / (t * 2.5));
    float parpadeo = 0.75 + 0.25 * sin(Time * (1.5 + r.z * 3.0) + r.y * 40.0);
    vec3 color = mix(vec3(0.72, 0.82, 1.0), vec3(1.0, 0.97, 0.92), r.z);
    if (r.y > 0.93) {
        color = vec3(1.0, 0.85, 0.65);
    }
    return color * luz * parpadeo * brillo * (0.4 + r.y);
}

// estrellas extra, mas grandes y brillantes que el campo fino: se ven aunque la pantalla baje de resolucion (video, 1080p)
vec3 estrellasExtra(vec3 d) {
    vec3 acc = vec3(0.0);
    for (int k = 0; k < 2; k++) {
        float esc = (k == 0) ? 85.0 : 150.0;
        vec3 p = (d + float(k) * vec3(0.31, 0.17, 0.53)) * esc;
        vec3 c = floor(p);
        vec3 r = h33(c + float(k) * 17.0);
        if (r.x > 0.16) continue;
        vec3 sitio = c + 0.3 + 0.4 * h33(c + 7.13);
        float dist = length(p - sitio);
        float grande = r.y * r.y * r.y;                                 // casi todas diminutas, muy pocas más grandes
        float t = 0.075 + 0.11 * grande;
        float nucleo = exp(-dist * dist / (t * t));
        float velo = 0.10 * exp(-dist / (t * 3.0)) * smoothstep(0.55, 0.30, dist) * grande;   // un halo muy tenue solo en las más brillantes
        float parp = 0.70 + 0.30 * sin(Time * (1.2 + r.z * 3.5) + r.y * 40.0);
        vec3 color = mix(vec3(1.0, 0.72, 0.52), vec3(0.80, 0.86, 1.0), r.z);   // naranjas y azuladas, como estrellas de verdad
        acc += color * (nucleo + velo) * parp * (0.25 + 1.1 * grande);
    }
    return acc;
}

float ridge(float v) {
    return 1.0 - abs(2.0 * v - 1.0);
}

// ------------------------------------------------------------------ cielo: atardecer azul-violeta / noche magenta, con estrellas, nubes y bruma (como el video)
// nubes volumétricas: una capa gruesa sobre el jugador, se atraviesa con unos pocos pasos y se ilumina desde arriba
float densNube(vec3 P, float v, float cob) {
    // cúmulos: base plana y cimas redondeadas y abultadas (el umbral sube con la altura), con detalle fino en el borde
    float base = fbm3(P * vec3(0.9, 1.1, 0.9) + vec3(0.0, 3.0, 0.0));
    float det = fbm3(P * vec3(2.6, 3.4, 2.6) + 7.0);
    float coli = noise(P * vec3(7.0, 8.0, 7.0) + 21.0);           // coliflor: bultos pequeños en el borde (1 sola octava: barato)
    float forma = base * 0.70 + det * 0.40 + (coli - 0.5) * 0.30;
    float umbral = cob + 0.30 * v;
    float perfil = smoothstep(0.0, 0.10, v) * (1.0 - smoothstep(0.62, 1.0, v));
    float x = clamp((forma - umbral) * 4.4, 0.0, 1.0);
    return x * x * (3.0 - 2.0 * x) * perfil;
}

// versión barata para las sombras (solo la forma grande): se llama varias veces por paso, así que cada ruido cuenta
float densSombra(vec3 P, float v, float cob) {
    float base = fbm3(P * vec3(0.9, 1.1, 0.9) + vec3(0.0, 3.0, 0.0));
    float x = clamp((base * 1.10 - (cob + 0.30 * v)) * 4.4 + 0.15, 0.0, 1.0);
    return x * x * (3.0 - 2.0 * x);
}

// devuelve (rgb ya iluminado, alfa); d = dirección del rayo (mundo), t = tiempo para el viento, luzDir = hacia la luz
vec4 nubes(vec3 d, float t, vec3 luzDir, vec3 sombra, vec3 claro, float espesor, float Y0, float Y1, float esc, float cob) {
    float h = d.y;
    if (h < 0.02 || CloudQ < 0.5) return vec4(0.0);
    float t0 = Y0 / h;
    float t1 = min(Y1 / h, t0 + 6.0);                    // en rasante el rayo cruza muchas "casillas": se acorta para que no salgan rayas
    // cobertura aproximada barata: si no hay nada en el centro de la capa, no se recorre
    vec3 Pm = d * (0.5 * (t0 + t1)) + vec3(t * 0.06, 0.0, t * 0.02);
    if (fbm3(Pm * esc * vec3(0.9, 1.1, 0.9) + vec3(0.0, 3.0, 0.0)) < cob - 0.32) return vec4(0.0);
    int N = CloudQ > 2.5 ? 12 : (CloudQ > 1.5 ? 7 : 4);        // pasos por rayo según la calidad
    float dt = (t1 - t0) / float(N);
    float T = 1.0;
    vec3 acc = vec3(0.0);
    vec3 ld = normalize(luzDir);
    float fw = pow(clamp(dot(d, ld), 0.0, 1.0), 4.0);     // mirando hacia la luz: bordes plateados
    float jit = fract(52.9829189 * fract(dot(gl_FragCoord.xy, vec2(0.06711056, 0.00583715))));
    for (int i = 0; i < N; i++) {
        float tt = t0 + (float(i) + 0.25 + 0.5 * jit) * dt;
        vec3 P = d * tt + vec3(t * 0.06, 0.0, t * 0.02);
        float v = clamp((tt * h - Y0) / (Y1 - Y0), 0.0, 1.0);
        float dn = densNube(P * esc, v, cob);
        if (dn > 0.002) {
            // sombra propia: dos muestras baratas hacia la luz = cuánta nube tapa la luz
            float sh = densSombra((P + ld * 0.45) * esc, v + ld.y * 0.20, cob)
                     + densSombra((P + ld * 1.30) * esc, v + ld.y * 0.60, cob) * 1.1;
            float luz = exp(-sh * 1.7);
            float polvo = 1.0 - exp(-dn * 3.0);                    // los bordes finos se ven más claros que el centro
            // luz del cielo (suave, desde arriba) + luz directa de la estrella (con sombra propia)
            float ambiente = 0.16 + 0.40 * v;
            vec3 c = sombra + (claro - sombra) * clamp(ambiente * (0.55 + 0.45 * (1.0 - polvo)) + 0.95 * luz * (0.30 + 0.70 * (1.0 - polvo)), 0.0, 1.0);
            c += claro * fw * exp(-dn * 1.8) * 0.85 * luz;         // borde plateado hacia la luz
            c *= 0.45 + 0.85 * v;                                  // la base más oscura, la cima clara: eso da el volumen
            float a = 1.0 - exp(-dn * dt * espesor);
            acc += T * a * c;
            T *= 1.0 - a;
            if (T < 0.03) break;
        }
    }
    float alfa = 1.0 - T;
    alfa *= smoothstep(0.02, 0.20, h);                            // se difuminan en la bruma del horizonte
    vec3 rgb = alfa > 1e-4 ? acc / max(1.0 - T, 1e-4) : vec3(0.0);
    return vec4(rgb, alfa);
}

vec3 skyNormal(vec3 d, float narIn) {
    float h = d.y;
    float hc = abs(h);                       // el cielo cubre toda la esfera: debajo del horizonte es simétrico
    float noche = max(1.0 - smoothstep(0.25, 0.85, clamp(Intensity, 0.0, 1.0)), SkyDark);   // Intensity = luz del día
    vec3 sd0 = normalize(StarDirW);
    float ca0 = dot(d, sd0);
    float ang0 = acos(clamp(ca0, -1.0, 1.0));
    // atardecer azul-violeta
    vec3 colD = mix(vec3(0.82, 0.62, 0.76), vec3(0.58, 0.52, 0.82), smoothstep(0.0, 0.30, hc));
    colD = mix(colD, vec3(0.36, 0.40, 0.74), smoothstep(0.25, 0.85, hc));
    // noche magenta oscura (reposo del video)
    vec3 colN = mix(vec3(0.34, 0.09, 0.22), vec3(0.19, 0.06, 0.19), smoothstep(0.0, 0.30, hc));
    colN = mix(colN, vec3(0.06, 0.03, 0.11), smoothstep(0.25, 0.85, hc));
    vec3 col = mix(colD, colN, noche);
    float t = Time * 0.004;

    // bruma rosa en manchas grandes y suaves
    float bru = fbm3(d * 2.2 + vec3(5.0, 1.0, t));
    col += mix(vec3(0.30, 0.10, 0.22), vec3(0.50, 0.10, 0.30), noche) * smoothstep(0.38, 0.80, bru) * (1.0 - smoothstep(0.1, 0.8, hc)) * 0.9;

    // nebulosa magenta de noche: zarcillos y manchas violeta
    if (noche > 0.02) {
        vec3 w1 = vec3(fbm3(d * 2.0 + vec3(t, 0.0, 0.0)), fbm3(d * 2.0 + vec3(4.1, t, 1.7)), fbm3(d * 2.0 + vec3(8.3, 2.9, t)));
        vec3 q = d * vec3(3.0, 5.5, 3.0) + (w1 - 0.5) * 3.2;
        float f1 = fbm(q);
        float f2 = fbm(q * 2.1 + vec3(3.3, 0.0, 7.1) + (w1 - 0.5) * 2.0);
        float wisp = pow(clamp((f1 - 0.33) * 2.4, 0.0, 1.0), 2.2);
        float fil = pow(clamp((f2 - 0.40) * 2.4, 0.0, 1.0), 2.5);
        float curt = wisp * (0.45 + 0.9 * fil) * (1.0 - 0.4 * smoothstep(0.55, 0.95, h));
        vec3 fuego = mix(vec3(0.46, 0.08, 0.26), vec3(0.85, 0.22, 0.46), clamp(fil * 1.2, 0.0, 1.0));
        col += fuego * curt * 0.75 * noche;
        float vio = smoothstep(0.45, 0.85, fbm3(d * 1.7 + vec3(2.0, 9.0, 4.0)));
        col += vec3(0.20, 0.09, 0.34) * vio * smoothstep(0.1, 0.7, hc) * 0.8 * noche;
    }

    // cuenta atrás: el cielo se vuelve naranja, concentrado hacia la estrella, y granate hacia el lado opuesto
    float nar = clamp(narIn, 0.0, 1.0);
    if (nar > 0.001) {
        vec3 ore = vec3(0.30, 0.09, 0.13) + vec3(0.78, 0.36, 0.12) * exp(-ang0 * 1.15) + vec3(0.30, 0.13, 0.08) * exp(-abs(h) * 5.0);
        ore += vec3(0.20, 0.10, 0.04) * smoothstep(0.35, 0.75, bru);
        col = mix(col, ore, nar * 0.93);
    }

    // estrellas: pequeñas y nítidas por todo el cielo, brillan más cuanto más oscuro
    vec3 g = d * 210.0;
    vec3 id = floor(g);
    vec3 off = (vec3(hash(id + 1.3), hash(id + 7.1), hash(id + 3.7)) - 0.5) * 0.6;
    float hs = hash(id);
    float dd = length(fract(g) - 0.5 - off);
    float star = step(0.90, hs) * (1.0 - smoothstep(0.0, 0.20, dd)) * (0.55 + 0.45 * sin(Time * 1.6 + hs * 60.0));
    star *= (1.0 + 0.8 * noche);
    float tam = step(0.985, hs);
    col += mix(vec3(0.85, 0.88, 1.0), vec3(1.0, 0.82, 0.92), fract(hs * 31.0)) * star * (0.85 + 1.2 * tam);

    // nubes volumétricas (solo en el cielo, h > 0): ciruelas oscuras que se iluminan desde arriba
    if (h > 0.015 && Apoc < 0.5) {      // en el cielo del fin del mundo estas nubes quedarían tapadas por las suyas: se ahorra el cálculo
        vec4 nb = nubes(d, Time, normalize(sd0 + vec3(0.0, 0.95, 0.0)), vec3(0.07, 0.015, 0.08), vec3(0.98, 0.42, 0.52), 9.0, 0.55, 2.1, 0.85, 0.43);
        vec3 nc = nb.rgb * (1.0 - 0.25 * noche);
        col = mix(col, nc, nb.a * 0.95);
    }

    // estrellas grandes que parpadean (se suman a las finas)
    col += estrellasExtra(d) * (0.35 + 0.65 * noche);

    // la estrella: resplandor suave, un único haz largo y fino, y una línea de destello horizontal
    float ca = ca0;
    float ang = ang0;
    col += vec3(1.0, 0.60, 0.45) * exp(-ang * 9.0) * 0.55;
    col += vec3(0.80, 0.60, 0.80) * exp(-ang * 3.2) * 0.14;
    if (ca > 0.0) {
        vec3 sd = normalize(StarDirW);
        vec3 t1 = abs(sd.y) > 0.97 ? vec3(1.0, 0.0, 0.0) : normalize(cross(sd, vec3(0.0, 1.0, 0.0)));
        vec3 t2 = cross(sd, t1);
        vec3 pp = d - sd * ca;
        float px = dot(pp, t1);
        float py = dot(pp, t2);
        float fuerza = (0.55 + 0.75 * SkyBright + 0.25 * clamp(1.0 - Aparece / 6.0, 0.0, 1.0)) * (1.0 - 0.9 * nar);
        float aa = 1.18 + sin(Time * 0.025) * 0.10;                       // un solo haz, inclinado hacia arriba-derecha
        vec2 u = vec2(cos(aa), sin(aa));
        float perp = abs(px * u.y - py * u.x);
        float along = px * u.x + py * u.y;
        float w = 0.006 + 0.020 * max(along, 0.0);
    }
    float onda = clamp(1.0 - Aparece / 3.5, 0.0, 1.0);
    if (onda > 0.0) {
        float ra = 0.03 + Aparece * 0.45;
        float an = 0.008 + Aparece * 0.012;
        col += vec3(1.0, 0.70, 0.60) * exp(-pow((ang - ra) / an, 2.0)) * 0.7 * onda * onda;
    }
    col = mix(col, col * 1.2 + vec3(0.07, 0.06, 0.14), SkyBright);
    col *= 1.0 - 0.35 * SkyDark;
    return col;
}

// ------------------------------------------------------------------ cielo del fin del mundo (video): rojo oscuro con estrellas -> naranja ardiente -> durazno
// Charge = cuánto se ha puesto naranja (0..1), SkyBright = cuánto se aclara a durazno al final (0..1), Aparece = segundos desde que salió
vec3 skyApoc(vec3 d) {
    float h = d.y;
    float hc = abs(h);
    float nar = clamp(Charge, 0.0, 1.0);
    float pea = clamp(SkyBright, 0.0, 1.0);
    vec3 sd = normalize(StarDirW);
    float ca = dot(d, sd);
    float ang = acos(clamp(ca, -1.0, 1.0));
    float t = Time * 0.004;

    // el cielo en sí es el de siempre (nebulosa magenta / morado / rosa con estrellas, el de la noche con supernova);
    // el naranja NO es del cielo: es la luz de la estrella que lo va bañando
    vec3 col = skyNormal(d, 0.0);
    // medido del video: la neblina de la luz es naranja-rojo saturado y casi liso (arriba ~(0.66,0.23,0.09), junto a la estrella ~(0.97,0.39,0.12))
    float haz = exp(-ang * 1.35);
    vec3 luzCielo = mix(vec3(0.62, 0.10, 0.02), vec3(1.0, 0.33, 0.04), clamp(haz * 0.95 + (1.0 - smoothstep(0.0, 0.7, hc)) * 0.30, 0.0, 1.0));
    // el cielo de fondo es el de noche con supernova (magenta/morado/estrellas); la luz naranja manda cerca de la estrella
    // y, al activarse la cuenta atrás, va llenando todo el cielo
    vec3 baseCielo = col;                                     // estrellas, nebulosa y nubes: se guardan para que la luz naranja no las borre
    float llena = smoothstep(0.72, 1.0, nar);
    float mw = clamp(haz * 1.5 + 0.45 + llena, 0.0, 1.0);   // 0.45 = cuánto naranja hay lejos de la estrella (más alto = más naranja)
    col = mix(col, luzCielo, mw * clamp(nar * 1.35, 0.0, 0.98));
    col += baseCielo * 0.50 * clamp(nar * 1.35, 0.0, 1.0) * (1.0 - 0.6 * llena);        // la nebulosa y las estrellas siguen viéndose bajo la luz naranja
    col += estrellasExtra(d) * 0.75 * clamp(nar * 1.35, 0.0, 1.0);

    // nubes volumétricas rojizas: oscuras en reposo; con la cuenta atrás las ilumina la estrella (naranja del lado de la estrella)
    if (h > 0.015) {
        float cara = 0.35 + 0.65 * smoothstep(-0.2, 0.9, ca);                 // el lado que mira a la estrella recibe más luz
        vec3 sombra = mix(vec3(0.090, 0.070, 0.090), vec3(0.27, 0.125, 0.105), nar);      // grises oscuros, no blancos
        vec3 claro = mix(vec3(0.46, 0.40, 0.46), vec3(0.95, 0.52, 0.30), nar * 0.80) * cara;   // gris con el borde teñido por la luz de la estrella
        float y0 = max((200.0 - CamY) / 48.0, 0.25);      // capa de nubes en la altura 200 del mundo (1 unidad de ruido = 48 bloques)
        vec4 nb = nubes(d, Time * 1.4, normalize(sd + vec3(0.0, 0.45, 0.0)), sombra * 0.7, claro * 1.25, 6.0, y0, y0 + 3.0, 0.34, 0.38);
        col = mix(col, nb.rgb, nb.a * mix(0.96, 0.78, nar));
    }

    // la luz de la estrella baña TODO el cielo: neblina naranja que crece con la cuenta atrás y es más densa hacia la estrella
    float luz = exp(-ang * 1.2) * 0.75 + exp(-ang * 4.5) * 0.9 + exp(-ang * 14.0) * 1.2;
    float neb = nar * (0.10 + 0.90 * exp(-ang * 1.0)) * (0.70 + 0.30 * smoothstep(0.7, 0.0, hc));   // más densa cerca del horizonte
    col += vec3(1.0, 0.20, 0.03) * neb * 0.10;
    col += mix(vec3(0.55, 0.20, 0.55), vec3(1.0, 0.52, 0.18), nar) * luz * (0.06 + 0.30 * nar * nar);
    col += vec3(1.0, 0.40, 0.10) * exp(-abs(h - sd.y) * 7.0) * exp(-ang * 0.9) * 0.12 * nar * nar;

    // onda al aparecer: un anillo de fuego que cruza el cielo desde la estrella
    float onda = clamp(1.0 - Aparece / 4.5, 0.0, 1.0);
    if (onda > 0.0) {
        float ra = 0.04 + Aparece * 0.55;
        float an = 0.020 + Aparece * 0.030;
        float anillo = exp(-pow((ang - ra) / an, 2.0));
        col += vec3(1.0, 0.62, 0.32) * anillo * 1.1 * onda * onda;
        col += vec3(0.8, 0.22, 0.08) * exp(-pow((ang - ra * 0.82) / (an * 3.0), 2.0)) * 0.5 * onda * onda;
    }

    // al final de la cuenta atrás todo el cielo se aclara a durazno
    vec3 dur = mix(vec3(1.0, 0.62, 0.36), vec3(1.0, 0.82, 0.58), smoothstep(0.0, 0.8, hc)) * (0.85 + 0.25 * exp(-ang * 1.5));
    col = mix(col, dur, pea * pea);
    return col;
}

vec3 skyColor(vec3 d) {
    if (Apoc > 0.999) return skyApoc(d);
    vec3 normal = skyNormal(d, 0.0);
    if (Apoc < 0.001) return normal;
    return mix(normal, skyApoc(d), Apoc);
}

// manchas de color de un punto del disco de la explosión (q en -1..1): turquesa, rosa y blanco
vec3 manchas(vec2 q, float T) {
    float k1 = fbm3(vec3(q * 5.2, T * 0.20));
    float k2 = fbm3(vec3(q * 6.5 + 7.0, T * 0.25 + 3.0));
    float k3 = noise(vec3(q * 9.0, T * 0.35 + 11.0));
    vec3 col = vec3(0.74, 0.92, 1.0);
    col = mix(col, vec3(0.38, 0.88, 0.95), smoothstep(0.42, 0.56, k1));
    col = mix(col, vec3(1.0, 0.45, 0.62), smoothstep(0.52, 0.68, k2) * 0.70);
    col = mix(col, vec3(1.0), smoothstep(0.45, 0.72, k3) * 0.35);
    return col * (0.80 + 0.35 * k3);
}

// ------------------------------------------------------------------ destello / resplandor (billboard, UV -1..1)
vec4 flare(vec2 uv) {
    float d = length(uv);
    vec3 idleC = vec3(1.0, 0.50, 0.16);
    vec3 midC = vec3(1.0, 0.74, 0.30);          // a mitad de la cuenta: ámbar dorado
    vec3 hotC = vec3(1.0, 0.95, 0.86);          // al final: blanco cálido
    vec3 col = mix(idleC, midC, smoothstep(0.0, 0.35, Charge));
    col = mix(col, hotC, smoothstep(0.35, 0.85, Charge));

    float glow = exp(-d * 6.0) * 1.2 + exp(-d * 2.6) * 0.35;
    // reposo: corona turbulenta pegada al disco + ondas de calor que salen de la estrella como de un sol
    float rest = (1.0 - smoothstep(0.0, 0.45, Charge)) * (1.0 - Expl);
    if (rest > 0.001) {
        float rd = clamp(Radius, 0.08, 0.80);
        float ang0 = atan(uv.y, uv.x);
        float tur = fbm3(vec3(cos(ang0) * 1.7 + d * 2.0, sin(ang0) * 1.7, Time * 0.18));
        float arco = fbm3(vec3(cos(ang0) * 2.4, sin(ang0) * 2.4, Time * 0.10 + d * 1.5));    // rompe los anillos en arcos
        float s = (d - rd) / max(rd, 0.10);
        float corona = exp(-max(s, 0.0) * 3.4) * smoothstep(-0.20, 0.06, s) * (0.20 + 1.10 * tur * tur);
        float fase = d * 30.0 - Time * 2.1 + tur * 4.0;
        float onda = pow(0.5 + 0.5 * sin(fase), 2.0) * smoothstep(rd * 1.0, rd * 1.4, d) * exp(-(d - rd) * 2.4) * smoothstep(0.30, 0.65, arco);
        float bruma = exp(-d * 3.4) * (0.40 + 0.9 * tur) * 0.40;
        glow += (corona * 0.80 + onda * 0.70 + bruma) * rest;
        col = mix(col, vec3(1.0, 0.80, 0.36), clamp(corona * 0.5, 0.0, 0.6) * rest);
    }
    if (rest > 0.001) {                                         // el brillo no tapa el disco: se ve su superficie
        glow *= 1.0 - 0.80 * rest * (1.0 - smoothstep(Radius * 0.78, Radius * 1.00, d));
    }
    glow *= 1.0 - smoothstep(0.85, 1.0, d);
    float rays = 0.0;
    float rk = smoothstep(0.25, 0.80, Charge) + Expl;
    if (rk > 0.001) {
        float hS = exp(-abs(uv.y) * 90.0) * exp(-abs(uv.x) * 2.2);          // gran raya horizontal
        float vS = exp(-abs(uv.x) * 70.0) * exp(-abs(uv.y) * 3.5) * 0.6;    // raya vertical
        vec2 r = vec2(uv.x + uv.y, uv.x - uv.y) * 0.7071;
        float dS = (exp(-abs(r.y) * 80.0) * exp(-abs(r.x) * 5.0) + exp(-abs(r.x) * 80.0) * exp(-abs(r.y) * 5.0)) * 0.35;
        float ang = atan(uv.y, uv.x);
        float spokes = pow(max(0.0, noise(vec3(ang * 9.0, 0.0, Time * 0.6))), 3.0) * exp(-d * 3.0) * 0.8;
        rays = (hS + vS + dS + spokes) * rk;
    }
    // anillo fino de halo con franjas de color (rojo hacia fuera, cian hacia dentro), como el borde de la esfera del video
    float anillo = exp(-pow((d - 0.40) / 0.022, 2.0)) * 0.30 + exp(-pow((d - 0.36) / 0.016, 2.0)) * 0.14;
    glow += anillo * smoothstep(0.1, 0.7, Charge);
    float core = exp(-d * 22.0) * 2.0 * max(mix(0.10, 1.0, smoothstep(0.40, 0.95, Charge)), Expl);   // el blanco quemado llega solo al final
    float mask = 1.0 - smoothstep(0.45, 0.98, d);        // nada llega al borde: no se ve el cuadrado
    float a = (glow + rays + core) * mask * Intensity * Fade;
    vec3 outc = mix(col, vec3(1.0), clamp(core + rays * 0.5, 0.0, 1.0));
    outc = mix(outc, mix(vec3(1.0, 0.35, 0.50), vec3(0.45, 1.0, 0.95), smoothstep(0.37, 0.41, d)), clamp(anillo * 2.0, 0.0, 0.6) * smoothstep(0.1, 0.7, Charge));
    return vec4(outc, clamp(a, 0.0, 1.0));
}

// ------------------------------------------------------------------ rayo de la estrella (haz fino en la estrella, ancho hacia ti)
// línea de destello horizontal de la estrella: recta en la pantalla (no un círculo del cielo), fina y larga
vec4 lineaH(vec2 uv) {
    float x = abs(uv.x * 2.0 - 1.0);
    float y = abs(uv.y * 2.0 - 1.0);
    float core = exp(-y * y * 5.0);
    float along = pow(max(1.0 - x, 0.0), 1.6) * (0.30 + 0.70 * exp(-x * 5.0));
    vec3 col = mix(vec3(0.70, 0.76, 1.0), vec3(1.0), core * exp(-x * 4.0));
    return vec4(col, clamp(core * along * Intensity * Fade, 0.0, 1.0));
}

vec4 shaft(vec2 uv) {
    float u = uv.x;                              // 0 en la estrella, 1 en el extremo
    float v = uv.y * 2.0 - 1.0;                  // -1..1 a lo ancho
    float w = 0.03 + 0.97 * pow(u, 1.1);
    float y = v / w;
    float core = exp(-y * y * 2.4);
    float n = fbm3(vec3(u * 7.0 - Time * 0.25, v * 2.5, Time * 0.05));
    float along = smoothstep(0.0, 0.05, u) * (1.0 - smoothstep(0.80, 1.0, u));
    float inten = core * along * (0.55 + 0.55 * n) * (0.35 + 0.65 * u);
    vec3 col = mix(vec3(0.55, 0.66, 1.0), vec3(1.0, 0.97, 1.0), pow(core, 5.0) * (1.0 - u));
    return vec4(col, clamp(inten * Intensity * Fade, 0.0, 1.0));
}

// ------------------------------------------------------------------ chorro de luz (billboard a lo largo del eje)
vec4 jet(vec2 uv) {
    float a = abs(uv.x);                         // 0 centro, 1 punta
    float w = 0.15 + 0.60 * a;                   // se ensancha como un cono (más grueso en el centro: se ve dentro de la esfera)
    float y = uv.y / w;
    float core = exp(-y * y * 3.0);
    float n = fbm3(vec3(uv.x * 6.0 - Time * 0.8 * sign(uv.x), uv.y * 3.0, 0.0));
    float along = (1.0 - smoothstep(0.5, 1.0, a)) * (0.6 + 0.4 * n);
    float inten = core * along * (0.6 + 0.4 * exp(-a * 4.0));
    vec3 col = mix(vec3(0.62, 0.78, 1.0), vec3(1.0), pow(core, 4.0) * (1.0 - a));
    return vec4(col, clamp(inten * Intensity * Fade, 0.0, 1.0));
}

float hashf(float x) {
    return fract(sin(x * 91.3458) * 47453.5453);
}

// ------------------------------------------------------------------ brillo rosa alrededor de la explosión (cáscara)
// 9: CAPA DE GAS que envuelve toda la estrella (una cáscara esférica de verdad, se ve igual desde cualquier lado).
// Se recorre el rayo de vista por dentro de la cáscara (de 1 radio a GAS_FUERA radios) y en cada punto se mira la altura de gas que
// toca en esa dirección de la estrella: una ola base que se mueve alrededor + llamaradas solares que brotan por todos lados,
// suben, se curvan y se apagan. Delante de la estrella queda un velo de gas que se arremolina sobre su superficie.
// Radius = radio de la estrella. AxX/AxY/AxZ = ejes del mundo en espacio de vista (para que el gas gire con el mundo, no con la cámara)
const float GAS_FUERA = 1.9;   // la cáscara de gas llega hasta 1.9 radios de la estrella (lo usa el Java)

// 9: capa de GAS pegada a la estrella (cáscara que la rodea por todos lados). Barata: sin recorrer el volumen, mide la cuerda de la cáscara
// que cruza cada rayo y le da forma de humo con pocos ruidos. De la cáscara salen lenguas de gas (llamaradas) que brotan, suben y se apagan.
vec4 gas(vec3 n) {
    vec3 v = normalize(vPos);
    float x = length(cross(v, SphereCenter)) / max(Radius, 1e-3);   // 1 = borde de la estrella
    if (dot(v, SphereCenter) < 0.0) x = 10.0;
    if (x > GAS_FUERA) return vec4(0.0);
    float T = Time;
    float viol = 1.0 + 0.9 * smoothstep(0.0, 1.0, Charge);

    // lenguas de gas: 7 que nacen en puntos cualquiera, suben rápido, se estiran y se apagan despacio (solo cuentas, nada de ruido)
    float lenguas = 0.0;
    for (int k = 0; k < 7; k++) {
        float fk = float(k);
        float ciclo = T * (0.08 + 0.07 * hashf(fk * 3.1)) * viol + hashf(fk * 7.7);
        float vida = fract(ciclo);
        float semilla = floor(ciclo) * 5.31 + fk * 13.7;
        vec3 dir = normalize(h33(vec3(semilla, fk, semilla * 0.37)) * 2.0 - 1.0 + 1e-4);
        float sube = vida < 0.35 ? smoothstep(0.0, 0.35, vida) : smoothstep(1.0, 0.35, vida);
        float ancho = 0.04 + 0.06 * hashf(semilla + 5.1);
        float g = exp(-(1.0 - dot(n, dir)) / ancho);
        lenguas = max(lenguas, g * sube * (0.45 + 0.40 * hashf(semilla + 9.3)));
    }
    float exterior = 1.38 + 0.50 * lenguas;                           // las lenguas empujan la cáscara hacia fuera
    if (x > exterior) return vec4(0.0);

    // grosor de gas que cruza el rayo (por fuera de la estrella): cuerda de la cáscara menos la parte tapada por la estrella
    float cuerda = sqrt(max(exterior * exterior - x * x, 0.0)) - sqrt(max(1.0 - x * x, 0.0));
    float grosor = cuerda / 1.0;

    // humo: una deformación barata (1 ruido) + 3 capas de ruido que giran lento + hilos finos (1 ruido)
    float c1 = cos(T * 0.06), s1 = sin(T * 0.06);
    vec3 q = vec3(c1 * n.x - s1 * n.z, n.y, s1 * n.x + c1 * n.z);
    float w = noise(q * 2.2 + vec3(0.0, T * 0.05, 0.0));
    float humo = fbm3(q * 3.4 + w * 1.6 + vec3(T * 0.03, 0.0, -T * 0.02));
    float hilos = pow(1.0 - abs(noise(q * 7.0 - w * 2.0 + T * 0.05) * 2.0 - 1.0), 3.0);
    float d = grosor * (0.20 + 1.00 * smoothstep(0.30, 0.72, humo)) + hilos * 0.20 * grosor;
    d *= smoothstep(exterior, exterior - 0.45, x);                    // borde muy suave: se deshace como gas, sin corte
    d *= 1.0 + 0.9 * exp(-max(x - 1.0, 0.0) * 5.0);                   // más espeso pegado a la estrella
    d *= mix(1.0, 0.16, smoothstep(1.0, 0.88, x));                    // delante del disco solo un velo fino (no un manto)

    float act = clamp(SkyBright, 0.0, 1.0);
    vec3 rojo = vec3(1.0, 0.30, 0.05);
    vec3 naranja = mix(vec3(1.0, 0.42, 0.10), vec3(1.0, 0.78, 0.40), hilos);
    naranja = mix(rojo, naranja, smoothstep(1.6, 1.0, x));             // más rojo en la punta, más claro pegado a la estrella
    vec3 blanco = mix(vec3(0.80, 0.88, 1.0), vec3(1.0), hilos);
    vec3 col = mix(naranja, blanco, act);
    return vec4(col, clamp(d * 1.25 * Intensity * Fade, 0.0, 1.0));
}

vec4 rimGlow(vec3 n) {
    // distancia del rayo de vista al centro, en radios de la esfera: 1.0 = justo el borde que se ve
    vec3 v = normalize(vPos);
    float x = length(cross(v, SphereCenter)) / max(Radius, 1e-3);
    if (dot(v, SphereCenter) < 0.0) x = 10.0;
    float wob = (noise(n * 4.0 + vec3(Time * 0.25)) - 0.5) * 0.06;
    float o = max(x - 1.0 - wob, 0.0);
    float g = exp(-o * 90.0) * smoothstep(0.975, 1.0, x);   // halo rosa muy fino
    g *= 1.0 - smoothstep(1.025, 1.05, x);
    vec3 col = vec3(1.0, 0.24, 0.46);
    return vec4(col, clamp(g * 0.8 * Intensity * Fade, 0.0, 1.0));
}


// ------------------------------------------------------------------ SOL: granulación, manchas y protuberancias
const float PROM_ESC = 1.9;   // el cuadrado de las protuberancias mide PROM_ESC radios de la estrella de lado (lo usa el Java)

// celdas de plasma (Worley): 0 en el centro de cada celda, ~0.8 en las líneas entre celdas
float celdas(vec3 p) {
    vec3 i = floor(p);
    vec3 f = fract(p);
    float md = 8.0;
    for (int x = -1; x <= 1; x++) {
        for (int y = -1; y <= 1; y++) {
            for (int z = -1; z <= 1; z++) {
                vec3 g = vec3(float(x), float(y), float(z));
                vec3 o = h33(i + g);
                o = 0.5 + 0.5 * sin(Time * 0.35 + 6.2831 * o);
                vec3 r = g + o - f;
                md = min(md, dot(r, r));
            }
        }
    }
    return sqrt(md);
}


// olas de fuego: pequeñas ondulaciones suaves pegadas al borde de la estrella (nada de arcos grandes)
// uv en -1..1, el disco ocupa el radio 1/PROM_ESC
vec4 protuberancias(vec2 uv) {
    float r0 = 1.0 / PROM_ESC;
    float r = length(uv);
    if (r < r0 * 0.97 || r > 1.0) return vec4(0.0);
    float th = atan(uv.y, uv.x);
    float x = (r - r0) / r0;                       // altura sobre el borde, en radios de la estrella
    float T = Time;
    float viol = 1.0 + 0.9 * smoothstep(0.0, 1.0, Charge);

    // GAS que rodea TODA la estrella, pegado al disco: capa turbulenta de grosor irregular con llamaradas solares por todos lados
    float tur = fbm3(vec3(th * 3.0, T * 0.12 * viol, 1.0));
    float ola = 0.150
        + 0.035 * sin(th * 5.0 - T * 0.45 * viol)
        + 0.028 * sin(th * 9.0 + T * 0.70 * viol + 1.3)
        + 0.020 * sin(th * 14.0 - T * 0.95 * viol + 4.1);
    ola *= 0.70 + 0.70 * tur;
    // 18 llamaradas repartidas alrededor (cada una nace, sube, se retuerce y se apaga a su ritmo) + erupciones grandes
    float lenguas = 0.0;
    for (int k = 0; k < 18; k++) {
        float fk = float(k);
        float grande = (k < 4) ? 1.0 : 0.0;
        float ciclo = T * (0.07 + 0.06 * hashf(fk * 3.1)) * (grande > 0.5 ? 0.7 : 1.0) + hashf(fk * 7.7);
        float vida = fract(ciclo);
        float semilla = floor(ciclo) * 5.31 + fk * 13.7;
        float ang = (fk + 0.8 * (hashf(semilla + 1.7) - 0.5)) / 18.0 * 6.2831853;
        float dth = abs(mod(th - ang + 3.14159265, 6.2831853) - 3.14159265);
        float ancho = (0.045 + 0.050 * hashf(semilla + 5.1)) * (1.0 + 1.6 * grande);
        float altura = (0.10 + 0.20 * hashf(semilla + 9.3) + 0.25 * grande * hashf(semilla + 2.9)) * viol;
        float subida = sin(vida * 3.14159265);
        float retuerce = 0.06 * sin(vida * 9.0 + fk) * subida;
        float forma = exp(-pow((dth + retuerce) / (ancho * (0.55 + 0.9 * vida)), 2.0));
        lenguas = max(lenguas, forma * altura * subida);
    }
    ola += lenguas;
    ola = max(ola, 0.03);
    // gas: densidad turbulenta que se deshilacha hacia fuera (hebras de plasma que fluyen), sin borde duro
    float xt = x + 0.05 * (noise(vec3(th * 11.0, x * 4.0 - T * 0.30, T * 0.10)) - 0.5);
    float cuerpo = pow(smoothstep(ola, 0.0, xt), 1.25);
    float hebra = pow(noise(vec3(th * 30.0, x * 3.0 - T * 0.55 * viol, T * 0.12)), 1.5);
    float nube = fbm3(vec3(th * 6.0 - T * 0.10, x * 7.0, T * 0.18));
    float flujo = (0.45 + 0.85 * nube) * (0.50 + 0.90 * hebra);
    float cresta = exp(-pow((x - ola * 0.35) / (ola * 0.45 + 0.01), 2.0)) * 0.30;
    float halo = exp(-max(x, 0.0) * 5.0) * 0.20;

    float a = (cuerpo * flujo * 1.55 + cresta * flujo * 1.2 + halo) * (1.0 + 0.9 * clamp(lenguas * 4.0, 0.0, 1.0)) * smoothstep(1.0, 0.86, r) * smoothstep(-0.04, 0.01, x);
    // color por altura: blanco-amarillo junto al borde, naranja en medio, rojo en la punta de la llama
    float hh = clamp(x / max(ola, 0.02), 0.0, 1.0);
    vec3 col = mix(vec3(1.0, 0.82, 0.40), vec3(1.0, 0.45, 0.10), smoothstep(0.0, 0.45, hh));
    col = mix(col, vec3(0.95, 0.18, 0.03), smoothstep(0.45, 1.0, hh));
    col = mix(col, vec3(1.0, 0.92, 0.78), smoothstep(0.4, 1.0, Charge) * 0.5);
    return vec4(col, clamp(a * Intensity * Fade, 0.0, 1.0));
}

// ------------------------------------------------------------------ rayos de luz (como los "god rays" de los shaders de Iris/Oculus)
// Pantalla completa. SphereCenter = (x, y, proporción) de la estrella en pantalla (x, y de 0 a 1, pueden salirse). Cada píxel mira
// hacia la estrella y suma la luz del cielo que no está tapada por bloques, árboles, mobs ni jugadores: así aparecen haces entre las
// cosas, y sobre todo donde hay huecos. Charge hace la luz más blanca y fuerte. Intensity = fuerza general.
vec4 rayosLuz(vec2 uv) {
    vec2 L = SphereCenter.xy;
    float asp = SphereCenter.z;
    vec2 delta = L - uv;
    const int N = 44;
    float jit = fract(52.9829189 * fract(dot(gl_FragCoord.xy, vec2(0.06711056, 0.00583715))));
    vec2 paso = delta * (0.95 / float(N));
    vec2 sp = uv + paso * jit;
    float acum = 0.0;
    float decay = 1.0;
    for (int i = 0; i < N; i++) {
        sp += paso;
        vec2 c = clamp(sp, vec2(0.001), vec2(0.999));
        float libre = step(0.99995, texture(DepthSampler, c).r);       // 1 = cielo (nada delante)
        float fuera = (sp.x < 0.0 || sp.x > 1.0 || sp.y < 0.0 || sp.y > 1.0) ? 1.0 : 0.0;
        libre = max(libre, fuera * 0.0);
        float aLaLuz = length((sp - L) * vec2(asp, 1.0));
        float brillo = exp(-aLaLuz * 2.4) * 0.8 + exp(-aLaLuz * 9.0) * 1.6;      // el halo de la estrella
        acum += libre * brillo * decay;
        decay *= 0.972;
    }
    acum /= float(N) * 0.9;
    float aMi = length((uv - L) * vec2(asp, 1.0));
    float cerca = 0.55 + 0.45 * exp(-aMi * 1.1);                 // un poco más fuerte cuanto más cerca de la estrella
    float v = acum * cerca * 1.45;
    // bruma de luz: la luz se dispersa en el aire y envuelve a jugadores y objetos (halo naranja fuerte alrededor de las siluetas)
    float bruma = exp(-aMi * 1.15) * 0.50 + exp(-aMi * 3.2) * 0.35;
    v = v + bruma * (1.0 - 0.35 * acum) * (1.0 - 0.8 * smoothstep(0.3, 1.0, Charge));   // al explotar (luz blanca) la bruma del centro no tapa la supernova
    vec3 naranja = vec3(1.0, 0.27, 0.035);
    vec3 dorado = vec3(1.0, 0.50, 0.12);
    vec3 col = mix(naranja, dorado, clamp(v * 0.12, 0.0, 1.0));
    col = mix(col, vec3(1.0, 0.95, 0.88), smoothstep(0.3, 1.0, Charge) * 0.7);
    return vec4(col, clamp(v * Intensity * Fade, 0.0, 1.0));
}

void main() {
    if (Debug > 0.5) {
        fragColor = vec4(1.0, 0.0, 1.0, 1.0);
        return;
    }
    if (Mode > 15.5) {                                              // 16: velo blanco de pantalla: tapa del todo el cielo, pero deja ver las siluetas del terreno (y los rayos de luz que las cruzan)
        float libre = step(0.99995, texture(DepthSampler, vUV).r);
        fragColor = vec4(vColor.rgb, vColor.a * mix(0.55, 1.0, libre));
        return;
    }
    if (Mode > 14.5) {                                              // 15: destello blanco: grande y difuso al principio de la cuenta, se encoge hasta cubrir la estrella; al explotar crece con ella
        float dq = length(vUV * 2.0 - 1.0);
        float borde = 0.70 * clamp(Charge, 0.0, 1.0);               // Charge 0 = bola difusa, 1 = disco que tapa toda la estrella
        float a = pow(1.0 - smoothstep(borde, 1.0, dq), 1.0 + 1.6 * (1.0 - clamp(Charge, 0.0, 1.0))) * Intensity;
        fragColor = vec4(1.0, 0.97, 1.0, clamp(a, 0.0, 1.0));
        return;
    }
    if (Mode > 13.5) { fragColor = rayosLuz(vUV); return; }
    if (Mode > 11.5) { fragColor = protuberancias(vUV * 2.0 - 1.0); return; }
    if (Mode > 10.5) { fragColor = lineaH(vUV); return; }
    if (Mode > 9.5) { fragColor = shaft(vUV); return; }
    if (Mode > 8.5) { fragColor = gas(dirUV(vUV)); return; }
    if (Mode > 7.5) {                                               // 8: luz del chorro donde cae (mancha suave en el suelo)
        vec2 q = vUV * 2.0 - 1.0;
        float d = length(q);
        float a = exp(-d * d * 2.6) * (1.0 - smoothstep(0.8, 1.0, d));
        fragColor = vec4(vColor.rgb, vColor.a * a * Intensity * Fade);
        return;
    }
    if (Mode > 6.5) { fragColor = rimGlow(dirUV(vUV)); return; }
    if (Mode > 5.5) { fragColor = vColor; return; }                 // velo de pantalla completa
    if (Mode > 4.5) { fragColor = jet(vUV * 2.0 - 1.0); return; }
    if (Mode > 3.5) { fragColor = flare(vUV * 2.0 - 1.0); return; }

    vec3 n = dirUV(vUV);                   // dirección en la esfera, fija al mundo
    if (Mode > 2.5) {
        fragColor = vec4(skyColor(n), clamp(SkyMix, 0.0, 1.0));
        return;
    }

    vec3 nv = normalize(vPos - SphereCenter);
    float mu = clamp(abs(dot(nv, normalize(-vPos))), 0.0, 1.0);    // 1 = centro del disco, 0 = borde
    float r = sqrt(max(0.0, 1.0 - mu * mu));                        // radio en el disco que ves (0 centro, 1 borde)

    // ---------------------------------------------------------------- explosión
    if (Mode > 1.5) {
        vec3 V = normalize(ViewDirW);
        vec3 up = abs(V.y) > 0.95 ? vec3(1.0, 0.0, 0.0) : vec3(0.0, 1.0, 0.0);
        vec3 e1 = normalize(cross(V, up));
        vec3 e2 = cross(e1, V);
        vec3 p = n - V * dot(n, V);
        float ang = atan(dot(p, e2), dot(p, e1));
        vec2 cs = vec2(cos(ang), sin(ang));
        float T = Time;

        // rayas radiales (efecto de zoom, salen del centro)
        float s1 = fbm3(vec3(cs * 9.0, r * 1.4 - T * 0.35));
        float s2 = noise(vec3(cs * 28.0, r * 2.5 - T * 0.6));
        float streak = s1 * 0.65 + s2 * 0.45;

        // vetas moteadas rosa / turquesa, arrastradas hacia fuera
        vec3 q = n * 5.0 + vec3(cs * r * 2.2, -T * 0.08);
        float w = fbm3(q * 0.7 + T * 0.03);
        float m1 = fbm(q + w * 1.7);
        float m2 = fbm(q * 1.8 + w * 2.2 + 7.0);
        float vein = pow(ridge(m1), 5.0);
        float vein2 = pow(ridge(m2), 7.0);
        float which = smoothstep(0.42, 0.58, fbm3(n * 2.5 + 3.0));

        vec3 base = mix(vec3(1.0, 0.98, 1.0), vec3(0.86, 0.84, 0.90), smoothstep(0.0, 0.55, r));
        base *= 0.60 + 0.62 * streak;
        base = mix(base, vec3(1.0, 0.62, 0.78), smoothstep(0.55, 0.85, s2) * 0.30 * r);
        vec3 pinkV = vec3(1.0, 0.38, 0.60);
        vec3 tealV = vec3(0.45, 1.0, 0.92);
        vec3 col = base;
        col = mix(col, mix(pinkV, tealV, which), clamp(vein * 1.3 * smoothstep(0.05, 0.4, r), 0.0, 1.0) * 0.55);
        col = mix(col, mix(tealV, pinkV, which), clamp(vein2 * 1.5 * smoothstep(0.1, 0.5, r), 0.0, 1.0) * 0.45);

        // anillos de color hacia el borde con aberración cromática: turquesa dentro, rosa-rojo fuera
        float edgeN = (fbm3(vec3(cs * 5.0, T * 0.15)) - 0.5) * 0.08;
        float rr = r + edgeN;
        float tealRing = smoothstep(0.78, 0.90, rr) * (1.0 - smoothstep(0.91, 0.945, rr));
        col = mix(col, vec3(0.55, 1.0, 0.93) * (0.85 + 0.3 * streak), tealRing * 0.6);
        float pinkRim = smoothstep(0.945, 0.975, rr);
        col = mix(col, vec3(1.0, 0.25, 0.45) * (0.9 + 0.35 * streak), pinkRim);

        // ---- textura de gas: nubes que se hinchan, remolinos y huecos con volumen (los colores siguen siendo los de arriba)
        vec3 qg = n * 3.4 + vec3(cs * r * 1.7, -T * 0.09);
        float w1 = fbm(qg * 0.7 + vec3(0.0, T * 0.02, 0.0));
        vec3 qw = qg + (vec3(w1, fbm3(qg * 0.7 + 5.0), fbm3(qg * 0.7 + 9.0)) - 0.5) * 2.6;
        float dens = fbm(qw * 1.6);
        float bolsa = 1.0 - smoothstep(0.10, 0.70, celdas(qw * 2.3));          // bolsas redondas, como coliflor
        float nube = clamp(mix(dens, bolsa, 0.30) * 1.25 - 0.08, 0.0, 1.0);
        // sombreado falso: la luz viene de arriba-izquierda, así cada bulto tiene su lado claro y su lado oscuro
        float ee = 0.10;
        vec3 qh = qw * 1.6;
        float gx = fbm3(qh + vec3(ee, 0.0, 0.0)) - fbm3(qh - vec3(ee, 0.0, 0.0));
        float gy = fbm3(qh + vec3(0.0, ee, 0.0)) - fbm3(qh - vec3(0.0, ee, 0.0));
        float luzG = clamp(0.5 - (gx * 0.6 + gy * 0.8) * 3.2, 0.0, 1.0);
        col *= 0.80 + 0.40 * luzG;
        col = mix(col, col * vec3(0.74, 0.70, 0.92), smoothstep(0.55, 0.20, nube) * 0.30);      // huecos más fríos
        col = mix(col, col + vec3(0.18, 0.16, 0.20), smoothstep(0.62, 0.95, nube) * 0.5);       // crestas más claras
        col = mix(vec3(dot(col, vec3(0.333))), col, 1.25);                                          // que no pierda color
        // hilos finos de gas que se estiran hacia fuera
        float hilo = pow(ridge(fbm3(vec3(cs * 6.0, r * 2.0 - T * 0.25) + qw * 0.4)), 5.0);
        col = mix(col, mix(vec3(0.62, 0.95, 1.0), vec3(1.0, 0.55, 0.75), which), hilo * 0.28 * smoothstep(0.1, 0.5, r));

        // núcleo blanco ardiente
        col += vec3(1.0, 0.98, 1.0) * (exp(-r * 9.0) * 1.1 + exp(-r * 3.0) * 0.15);
        col = mix(vec3(1.0), col, smoothstep(0.0, 1.0, Expl));

        float alpha = (1.0 - smoothstep(0.985, 1.0, rr)) * mix(0.93, 0.80, smoothstep(0.3, 0.9, r));
        alpha *= 1.0 - 0.45 * smoothstep(0.55, 0.97, r) * (1.0 - nube);        // el borde se deshilacha un poco
        fragColor = vec4(col, clamp(alpha * Fade, 0.0, 1.0));
        return;
    }

    // ---------------------------------------------------------------- estrella (reposo: sol naranja; cuenta: blanca)
    // la de siempre, pero hecha de gas: el color se arrastra en remolinos suaves (doble deformación), sin celdas ni bordes duros
    float spd = 0.10 + 0.6 * Charge;
    vec3 q = n * 3.0 + vec3(0.0, Time * spd * 0.15, Time * spd * 0.1);
    float w = fbm3(q * 0.9 - Time * 0.02);
    float w2 = fbm3(q * 1.3 + w * 2.0 + vec3(5.0, Time * 0.03, 2.0));
    vec3 qd = q + (vec3(w, w2, fbm3(q * 0.8 + 9.0)) - 0.5) * 2.2;      // gas retorcido
    float f = fbm(qd);                                  // manchas grandes y blandas
    float gr = fbm(qd * 2.6 + Time * 0.04);             // grano fino de nube
    float vein = pow(ridge(fbm(qd * 1.5 + 4.0)), 5.0);

    // el color sube con la cuenta atrás: naranja ardiente -> dorado -> amarillo pálido -> blanco cálido
    float calor = smoothstep(0.0, 0.50, Charge);
    vec3 dark = mix(vec3(0.60, 0.12, 0.03), vec3(0.88, 0.34, 0.07), calor);
    vec3 orange = mix(vec3(1.0, 0.42, 0.08), vec3(1.0, 0.66, 0.20), calor);
    vec3 yellow = mix(vec3(1.0, 0.78, 0.38), vec3(1.0, 0.92, 0.62), calor);
    vec3 col = mix(dark, orange, smoothstep(0.26, 0.60, f));
    col = mix(col, yellow, smoothstep(0.50, 0.78, f * 0.6 + gr * 0.5));
    col *= 0.82 + 0.38 * gr;
    float gran = 1.0 - smoothstep(0.10, 0.70, celdas(qd * 1.7));            // granulación de plasma: celdas brillantes con bordes más oscuros
    float gran2 = 1.0 - smoothstep(0.15, 0.75, celdas(qd * 4.3 + 3.0));      // celdas más finas encima
    col *= 0.70 + 0.55 * gran * (1.0 - 0.5 * Charge) + 0.18 * gran2;
    float mancha = smoothstep(0.60, 0.74, fbm3(q * 0.8 + 3.0));              // manchas solares oscuras
    col *= 1.0 - 0.50 * mancha * (1.0 - Charge);
    col += vec3(1.0, 0.80, 0.45) * pow(gran, 3.0) * 0.30 * (1.0 - Charge);    // centros de celda muy brillantes
    col = mix(col, vec3(0.50, 0.09, 0.03), vein * 0.45);                 // vetas oscuras, más suaves
    // volumen de gas: lados claros y oscuros de cada nube
    float ee = 0.12;
    float gx = fbm3(qd * 2.0 + vec3(ee, 0.0, 0.0)) - fbm3(qd * 2.0 - vec3(ee, 0.0, 0.0));
    float gy = fbm3(qd * 2.0 + vec3(0.0, ee, 0.0)) - fbm3(qd * 2.0 - vec3(0.0, ee, 0.0));
    col *= 0.82 + 0.40 * clamp(0.5 - (gx * 0.6 + gy * 0.8) * 2.6, 0.0, 1.0);
    col *= 0.55 + 0.6 * pow(mu, 0.6);                                   // oscurecimiento al borde
    col += vec3(1.0, 0.55, 0.2) * pow(1.0 - mu, 3.0) * 0.6;             // borde encendido

    float wh = smoothstep(0.35, 0.95, Charge);
    vec3 hot = mix(vec3(1.0, 0.93, 0.80), vec3(1.0, 0.99, 0.95), pow(mu, 2.0)) * (1.2 + 0.4 * gr);
    col = mix(col, hot, wh);
    col *= 1.0 + (0.04 + 0.25 * Charge) * sin(Time * (5.0 + 40.0 * Charge));

    float edge = smoothstep(0.0, 0.16, mu + (f - 0.5) * 0.10);          // borde blando, de gas
    float fadeDisc = 1.0 - 0.9 * smoothstep(0.5, 0.95, Charge);   // al final se disuelve en el brillo, sin disco blanco
    fragColor = vec4(col, clamp(edge * Fade * fadeDisc, 0.0, 1.0));
}