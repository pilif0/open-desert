#version 330

in vec2 exTexCoord;
out vec4 fragColor;

uniform sampler2D textureSampler;
uniform vec2 textureDelta;

void main()
{
    vec2 textureCoordinates = exTexCoord + textureDelta;
    fragColor = texture(textureSampler, textureCoordinates);
}