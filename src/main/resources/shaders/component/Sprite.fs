#version 330

in vec2 texPosition;
out vec4 fragColor;

uniform vec2 textureDimensions;
uniform sampler2D textureSampler;
uniform vec2 textureDelta;

void main()
{
    vec2 texCoordinates = (texPosition * textureDimensions) + textureDelta;
    fragColor = texture2D(textureSampler, texCoordinates);
}