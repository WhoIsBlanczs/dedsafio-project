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

float angd(float a, float b){ return mod(a-b+PI,TAU)-PI; }
void main(){
  vec2 p=vec2(uv.x*2.0-1.0, 1.0-uv.y*2.0);
  float r=length(p); float th=atan(p.y,p.x); float t=Params.x;
  float R=0.92; float d=r-R;
  if(r>=1.0||d<-0.05){ fragColor=vec4(0.0); return; }
  float ang=pn(th,2.0,5.0);
  float band=exp(-pow(d/0.010,2.0))*(0.30+0.70*ang);
  float pro=0.0;
  for(int i=0;i<5;i++){
    float fi=float(i);
    float a0=fi*1.26+0.6+0.05*sin(t*0.2+fi);
    float da=angd(th,a0);
    float w=0.10+0.05*h11(fi*3.7);
    float h=0.020+0.050*h11(fi*9.1+1.0);
    float prof=h*cos(clamp(da/w,-1.0,1.0)*1.5708);
    float inside=(1.0-smoothstep(0.0,0.010,d-prof))*step(0.0,d)*step(abs(da),w);
    pro+=inside*(0.65+0.35*sin(t*1.7+fi*2.0));
  }
  float I=band+pro*0.85;
  vec3 col=mix(vec3(1.0,0.26,0.18),vec3(1.0,0.58,0.48),clamp(pro,0.0,1.0)*0.7)*I;
  float cm=max(max(col.r,col.g),col.b);
  fragColor=vec4(col/max(cm,0.001)*vcol.rgb, clamp(cm,0.0,1.0)*vcol.a);
}
