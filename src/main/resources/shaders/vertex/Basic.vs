#version 330

layout (location=0) in vec2 pos;

uniform mat4 projectionMatrix;
uniform mat4 worldMatrix;
uniform mat4 parentMatrix;

void main()
{
    gl_Position = projectionMatrix * worldMatrix * parentMatrix * vec4(pos, 0.0, 1.0);
}