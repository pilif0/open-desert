#version 330

layout (location=0) in vec3 pos;
layout (location=1) in vec3 inColor;

out vec3 exColor;

uniform mat4 projectionMatrix;

void main()
{
    gl_Position = projectionMatrix * vec4(pos, 1.0);
    exColor = inColor;
}