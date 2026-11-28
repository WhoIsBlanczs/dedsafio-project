#version 150

in vec2 uv;
in vec4 vcol;
uniform vec4 Params;
out vec4 fragColor;

#define PI 3.14159265
#define TAU 6.2831853
float h11(float p){ p=fract(p*.1031); p*=p+33.33; p*=p+p; return fract(p); }
float h21(vec2 p){ vec3 q=fract(vec3(p.xyx)*.1031); q+=dot(q,q.yzx+33.33); return fract((q.x+q.y)*q.z); }
float h31(vec3 p){ p=fract(p*.1031); p+=dot(p,p.zyx+31.32); return fract((p.x+p.y)*p.z); }
float vn2(vec2 x){
  vec2 i=floor(x); vec2 f=fract(x); f=f*f*(3.0-2.0*f);
  return mix(mix(h21(i),h21(i+vec2(1.0,0.0)),f.x), mix(h21(i+vec2(0.0,1.0)),h21(i+vec2(1.0,1.0)),f.x), f.y);
}
float vn3(vec3 x){
  vec3 i=floor(x); vec3 f=fract(x); f=f*f*(3.0-2.0*f);
  float a=mix(h31(i),h31(i+vec3(1.0,0.0,0.0)),f.x);
  float b=mix(h31(i+vec3(0.0,1.0,0.0)),h31(i+vec3(1.0,1.0,0.0)),f.x);
  float c=mix(h31(i+vec3(0.0,0.0,1.0)),h31(i+vec3(1.0,0.0,1.0)),f.x);
  float d=mix(h31(i+vec3(0.0,1.0,1.0)),h31(i+vec3(1.0,1.0,1.0)),f.x);
  return mix(mix(a,b,f.y),mix(c,d,f.y),f.z);
}
float fbm2(vec2 p){
  float a=0.5; float s=0.0;
  for(int i=0;i<5;i++){ s+=a*vn2(p); p=p*2.03+vec2(1.7,9.2); a*=0.5; }
  return s/0.96875;
}
float fbm3(vec3 p){
  float a=0.5; float s=0.0;
  for(int i=0;i<5;i++){ s+=a*vn3(p); p=p*2.03+vec3(1.7,9.2,4.1); a*=0.5; }
  return s/0.96875;
}
float fbm3l(vec3 p){
  float a=0.5; float s=0.0;
  for(int i=0;i<3;i++){ s+=a*vn3(p); p=p*2.03+vec3(1.7,9.2,4.1); a*=0.5; }
  return s/0.875;
}
// ruido periodico en el angulo (sin costura)
float pn(float th, float k, float seed){ return vn3(vec3(cos(th),sin(th),0.0)*k+vec3(seed,seed*1.7,seed*0.6)); }

void main(){
  float t=Params.x;
  float u=uv.x; float v=uv.y;     // u: a lo largo de la cortina, v: 0 abajo .. 1 arriba
  // rayos verticales finos que se mueven
  float r1=vn2(vec2(u*90.0,t*0.20));
  float r2=vn2(vec2(u*210.0+7.0,t*0.35+v*1.5));
  float r3=vn2(vec2(u*28.0,v*2.0+t*0.10));
  float rays=0.25+0.95*pow(r1,1.4)+0.55*pow(r2,2.0)*(1.0-v*0.5);
  rays*=0.65+0.7*r3;
  // pliegues de la cortina (sombras y luces suaves)
  float fold=0.7+0.5*sin(u*140.0+t*0.6+r3*6.0);
  // mas brillante abajo, se desvanece arriba, con un borde inferior marcado
  float vert=pow(1.0-v,0.85);
  float edge=smoothstep(0.0,0.07,v)*1.0;
  float bright=rays*(0.55+0.45*fold)*vert*edge;
  // fleco rosado/rojo en el borde de abajo
  vec3 fringe=vec3(1.0,0.25,0.45)*exp(-v/0.06)*0.5;
  vec3 col=vcol.rgb*(0.7+0.6*bright)+fringe*vcol.a;
  float a=vcol.a*clamp(bright*1.5,0.0,2.0);
  fragColor=vec4(col,a);
}
