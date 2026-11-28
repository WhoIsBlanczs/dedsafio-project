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

vec3 nebRamp(float x){
  vec3 a=vec3(0.35,0.85,1.0); vec3 b=vec3(0.55,0.90,1.0); vec3 c=vec3(1.0,0.72,0.92); vec3 d=vec3(1.0,0.52,0.45); vec3 e=vec3(0.95,0.35,0.30);
  if(x<0.4) return mix(a,b,x/0.4);
  if(x<0.68) return mix(b,c,(x-0.4)/0.28);
  if(x<0.88) return mix(c,d,(x-0.68)/0.20);
  return mix(d,e,(x-0.88)/0.12);
}
void main(){
  vec2 p=vec2(uv.x*2.0-1.0, 1.0-uv.y*2.0);
  float r=length(p); float th=atan(p.y,p.x);
  float t=Params.x; float R0=Params.y;
  if(r>=1.0){ fragColor=vec4(0.0); return; }
  // la capa de gas se deforma y fluye
  float rw=r+0.09*(fbm2(p*2.6+vec2(t*0.04,0.0))-0.5)+0.045*(fbm2(p*6.0-vec2(0.0,t*0.06))-0.5);
  float ca=cos(th); float sa=sin(th);
  // filamentos: ruido muy estirado en sentido radial -> hilos finos que salen del centro
  float n1=fbm3(vec3(ca*9.0,sa*9.0,rw*1.4+t*0.015));
  float n2=fbm3(vec3(ca*14.0+5.0,sa*14.0,rw*1.8-t*0.02)+3.0);
  float f1=pow(1.0-abs(2.0*n1-1.0),9.0);
  float f2=pow(1.0-abs(2.0*n2-1.0),9.0);
  float shell=exp(-pow((rw-R0)/0.13,2.0));
  float inner=1.0-smoothstep(0.0,R0*0.95,rw);
  float cloud=fbm2(p*5.0+vec2(t*0.03,2.0));
  float knots=0.6+0.8*vn2(p*38.0+vec2(t*0.2,0.0));
  float dens=shell*(0.03+2.0*f1)*knots+0.45*inner*(0.08+1.5*f2)*cloud+0.08*shell*cloud;
  vec3 col=nebRamp(clamp(rw/0.75,0.0,1.0))*(0.8+0.5*f1);
  float edge=1.0-smoothstep(0.70,0.92,r);
  float core=exp(-pow(r/0.05,2.0));
  float a=clamp(dens,0.0,1.0)*edge;
  col=col*a+vec3(1.0)*core*0.9;
  a=max(a,core);
  vec3 hue=col/max(a,0.001);
  fragColor=vec4(clamp(hue,0.0,1.0)*vcol.rgb,pow(clamp(a,0.0,1.0),0.9)*vcol.a);
}
