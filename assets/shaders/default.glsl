#type vertex
#version 330 core

layout (location=0) in vec3 aPos;
layout (location=1) in vec4 aColor;
layout (location=2) in vec2 aTexCoords;
layout (location=3) in float aTexId;

uniform mat4 uProjMat;
uniform mat4 uViewMat;

out vec4 fColor;
out vec2 fTexCoords;
out float fTexId;

void main()
{
    fColor = aColor;
    fTexCoords = aTexCoords;
    fTexId = aTexId;
    gl_Position = uProjMat * uViewMat * vec4(aPos, 1.0);
}

#type fragment
#version 330 core

uniform sampler2D uTextures[8];

in vec4 fColor;
in vec2 fTexCoords;
in float fTexId;

out vec4 color;

void main()
{
    if (fTexId > 0) {
        color = fColor * texture(uTextures[int(fTexId)], fTexCoords);
    }
    else
    {
        color = fColor;
    }
}