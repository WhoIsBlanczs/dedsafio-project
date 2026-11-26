#version 150

in vec3 Position;
in vec2 UV0;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

out vec3 rel;   // posición respecto a la cámara
out vec2 uv;    // x = bloques a lo largo de la ola, y = 0 en el pie .. 1 en la cresta

void main() {
    rel = Position;
    uv = UV0;
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
}
