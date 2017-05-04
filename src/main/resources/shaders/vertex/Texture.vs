#version 330

layout (location=0) in vec2 pos;
layout (location=1) in vec2 inTexCoord;

out vec2 exTexCoord;

uniform mat4 projectionMatrix;
uniform mat4 worldMatrix;

void main()
{
    gl_Position = projectionMatrix * worldMatrix * vec4(pos, 0.0, 1.0);
    exTexCoord = inTexCoord;
}