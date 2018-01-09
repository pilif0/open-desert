#version 330

layout (location=0) in vec2 pos;

out vec2 texPosition;

uniform vec2 spriteDimensions;
uniform mat4 projectionMatrix;
uniform mat4 worldMatrix;

void main()
{
    gl_Position = projectionMatrix
        * worldMatrix
        * vec4(spriteDimensions * pos, 0.0, 1.0);
    texPosition = pos + vec2(0.5, 0.5);
}