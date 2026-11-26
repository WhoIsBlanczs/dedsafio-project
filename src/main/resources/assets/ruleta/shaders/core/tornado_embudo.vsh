#version 150

// Código generado por Claude (Anthropic).
// El embudo del tornado es un volumen de nube que se calcula por píxel (ver el .fsh). Aquí solo se dibuja un cilindro que lo
// envuelve; los vértices llegan ya relativos a la cámara (Position = dónde está ese punto respecto a la cámara).

in vec3 Position;
in vec2 UV0;
in vec4 Color;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

out vec3 wp;
out float dist;

void main() {
    vec4 v = ModelViewMat * vec4(Position, 1.0);
    dist = length(v.xyz);
    wp = Position;
    gl_Position = ProjMat * v;
}
