#version 300 es

uniform mat4 uMVPMatrix;

layout(location = 4) in vec4 vPosition;
layout(location = 5) in vec2 vTexCoord;

out vec2 fTexCoord;

void main() {
    gl_Position = uMVPMatrix * vPosition;
    fTexCoord = vTexCoord;
}