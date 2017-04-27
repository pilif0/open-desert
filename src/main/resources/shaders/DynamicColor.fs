#version 330

in vec4 exColor;
out vec4 fragColor;

uniform vec4 color;

void main()
{
    fragColor = vec4(color);
}