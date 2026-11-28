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
  vec2 p=vec2(uv.x*2.0-1.0, 1.0-uv.y*2.0);
  float r=length(p); float th=atan(p.y,p.x); float t=Params.x;
  float R=0.30;
  if(r>=1.0){ fragColor=vec4(0.0); return; }
  float aa=fwidth(r)*1.2;
  float disc=1.0-smoothstep(R-aa,R+aa,r);
  float q=clamp(r/R,0.0,1.0);
  float mu=sqrt(max(0.0,1.0-q*q));
  float limb=0.50+0.50*pow(mu,0.65);
  float g=fbm2(p/R*7.0+vec2(t*0.05,-t*0.03));
  float gran=0.93+0.14*g;
  vec3 dcol=mix(vec3(1.0,0.66,0.30),vec3(1.0,0.97,0.86),limb)*gran*1.35;
  float d=max(r-R,0.0);
  float flick=0.88+0.24*pn(th+t*0.05,3.0,2.0);
  float glow=exp(-d/0.030)*0.95+exp(-d/0.14)*0.42*flick+exp(-d/0.42)*0.14;
  glow*=1.0-smoothstep(0.80,1.0,r);
  vec3 gcol=mix(vec3(1.0,0.85,0.55),vec3(1.0,0.95,0.80),exp(-d/0.08));
  vec3 col=dcol*disc+gcol*glow*(1.0-disc);
  float cm=max(max(col.r,col.g),col.b);
  fragColor=vec4(col/max(cm,0.001)*vcol.rgb, clamp(cm,0.0,1.0)*vcol.a);
}
