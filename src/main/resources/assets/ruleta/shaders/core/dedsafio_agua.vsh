#version 150

in vec3 Position;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

out vec3 rel;   // posición respecto a la cámara

void main() {
    rel = Position;
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
}
