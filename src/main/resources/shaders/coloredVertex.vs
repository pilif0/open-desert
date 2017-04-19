#version 330

layout (location=0) in vec2 pos;
layout (location=1) in vec4 inColor;

out vec4 exColor;

uniform mat4 projectionMatrix;
uniform mat4 worldMatrix;
uniform vec4 color;

void main()
{
    gl_Position = projectionMatrix * worldMatrix * vec4(pos, 0.0, 1.0);
    exColor = color * inColor;
}