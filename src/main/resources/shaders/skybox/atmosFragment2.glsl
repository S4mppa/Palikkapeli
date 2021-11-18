#version 150
#define saturate(a) clamp( a, 0.0, 1.0 )

in vec2 textureCoords;

out vec4 out_Colour;

uniform sampler2D colourTexture;
uniform sampler2D depthTexture;
uniform vec3 worldSpaceCameraPos;
uniform mat4 inverseProjection;
uniform mat4 inverseView;

const vec3 sunDir = normalize(vec3(1,0.3,0));


vec3 _ScreenToWorld(vec3 pos) {
    vec4 posP = vec4(pos.xyz * 2.0 - 1.0, 1.0);
    vec4 posVS = inverseProjection * posP;
    vec4 posWS = inverseView * vec4((posVS.xyz / posVS.w), 1.0);
    return posWS.xyz;
}

float _SoftLight(float a, float b) {
    return (b < 0.5 ?
    (2.0 * a * b + a * a * (1.0 - 2.0 * b)) :
    (2.0 * a * (1.0 - b) + sqrt(a) * (2.0 * b - 1.0))
    );
}
vec3 _SoftLight(vec3 a, vec3 b) {
    return vec3(
    _SoftLight(a.x, b.x),
    _SoftLight(a.y, b.y),
    _SoftLight(a.z, b.z)
    );
}

vec3 _ApplyGroundFog(
in vec3 rgb,
float distToPoint,
float height,
in vec3 worldSpacePos,
in vec3 rayOrigin,
in vec3 rayDir,
in vec3 sunDir)
{
    vec3 up = normalize(rayOrigin);
    float skyAmt = dot(vec3(0,-1,0), rayDir) * 0.25 + 0.75;
    skyAmt = saturate(skyAmt);
    skyAmt *= skyAmt;
    vec3 DARK_BLUE = vec3(0.1, 0.2, 0.3);
    vec3 LIGHT_BLUE = vec3(0.7, 0.7, 0.7);
    vec3 DARK_ORANGE = vec3(0.7, 0.4, 0.05);
    vec3 BLUE = vec3(0.5, 0.6, 0.7);
    vec3 YELLOW = vec3(1.0, 0.9, 0.7);
    vec3 fogCol = mix(DARK_BLUE, LIGHT_BLUE, skyAmt);
    float sunAmt = max(dot(rayDir, sunDir), 0.0);
    fogCol = mix(fogCol, YELLOW, pow(sunAmt, 16.0));
    float be = 0.0025;
    float fogAmt = (1.0 - exp(-distToPoint * be));
    // Sun
    sunAmt = 0.5 * saturate(pow(sunAmt, 256.0));
    return mix(rgb, fogCol, fogAmt) + sunAmt * YELLOW;
}

void main(void){
    float depth = texture(depthTexture, textureCoords).r;
    vec3 viewRay = _ScreenToWorld(vec3(textureCoords, depth));
    vec3 camDir = normalize(viewRay - worldSpaceCameraPos);
    float dist = length(viewRay - worldSpaceCameraPos);

    vec3 rgb = texture(colourTexture, textureCoords).rgb;


    rgb = _ApplyGroundFog(rgb, dist, 0, viewRay, worldSpaceCameraPos, camDir, normalize(vec3(-1, 1, 0)));

    rgb = 1.0 - exp(-rgb);

    out_Colour.rgb = rgb;
    out_Colour.a = 1;
}