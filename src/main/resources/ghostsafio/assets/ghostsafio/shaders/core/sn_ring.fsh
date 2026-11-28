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
  float r=length(p); float th=atan(p.y,p.x);
  float t=Params.x; float o=clamp(Params.y,0.0,1.0);     // o = cuanto se abrio (0 cerrado .. 1 abierto)
  if(r>=1.0){ fragColor=vec4(0.0); return; }
  float ca=cos(th); float sa=sin(th);
  float wob=(fbm3(vec3(ca,sa,0.0)*1.3+vec3(3.1,0.0,t*0.05))-0.5)*2.0+0.5*(pn(th,3.5,t*0.08+1.0)-0.5)*2.0;
  float Rrim=(0.58+0.12*o)*(1.0+0.07*wob);
  float NR=72.0;
  float jit=pn(th,5.0,2.3);
  float ang=th*NR/TAU+0.8*(jit-0.5)+t*0.02;
  float exo=0.7+1.6*o;
  float fine=pn(th,28.0,4.0);
  float ray=pow(abs(cos(PI*ang)),exo)*(0.6+0.4*fine);
  ray*=0.86+0.14*sin(t*3.0+ang*1.3);                          // centelleo
  float mixc=0.5+0.5*cos(PI*(ang*0.5+3.0*(jit-0.5)));          // turquesa <-> rosa
  float rin=0.03+0.50*o*Rrim;
  float inside=smoothstep(rin*0.6,rin+0.12,r)*(1.0-smoothstep(Rrim-0.03,Rrim+0.015,r));
  float cl1=fbm2(p*6.0+vec2(t*0.05,0.0)); float cl2=fbm2(p*16.0-vec2(0.0,t*0.07));
  float broken=1.0-0.85*o*smoothstep(0.35,0.75,cl1*0.55+cl2*0.45+0.2*(jit-0.5));
  float inten=ray*inside*broken;
  float fill=0.38*inside*(1.0-0.8*o);
  vec3 tur=vec3(0.30,1.0,0.95); vec3 pnk=vec3(1.0,0.45,0.78);
  vec3 rc=mix(tur,pnk,mixc)*(0.55+0.45*(1.0-smoothstep(0.0,Rrim,r)));
  float a1=clamp(inten*1.4+fill,0.0,1.0);
  vec3 C=rc*(inten*1.9+fill);
  float A=a1;
  // nucleo blanco caliente
  float coreR=0.30*(1.0-0.82*o)+0.02;
  float core=exp(-pow(r/coreR,2.0))*(1.0-0.35*o)+0.25*exp(-pow(r/(coreR*2.4),2.0));
  C+=vec3(1.0)*core; A=max(A,clamp(core,0.0,1.0));
  // borde rosado irregular con resplandor
  float rimw=0.030+0.014*o;
  float rim=exp(-pow((r-Rrim)/rimw,2.0))*(0.8+0.9*cl1)*(1.0-0.55*o*smoothstep(0.45,0.8,cl2));
  float glow=exp(-pow(max(r-Rrim,0.0)/(0.10+0.06*o),1.4))*step(Rrim-0.02,r)*0.75;
  float iglow=exp(-max(Rrim-r,0.0)/0.06)*step(r,Rrim)*0.35;
  vec3 pc=vec3(1.0,0.42,0.72);
  C+=(rim+glow*0.9+iglow*0.8)*pc;
  A=max(A,clamp(rim*1.1+glow*0.8+iglow*0.5,0.0,1.0));
  // filamentos que se escapan al abrirse
  float fo=smoothstep(0.35,0.9,o);
  float fn=fbm3(vec3(ca*11.0,sa*11.0,(r-Rrim)*2.0-t*0.05));
  float wisp=pow(1.0-abs(2.0*fn-1.0),7.0)*exp(-max(r-Rrim,0.0)/0.10)*step(Rrim,r)*fo*0.9;
  C+=wisp*vec3(1.0,0.55,0.85); A=max(A,clamp(wisp,0.0,1.0));
  A=clamp(A,0.0,1.0)*(1.0-smoothstep(0.90,1.0,r));
  vec3 hue=clamp(C/max(A,0.001),0.0,1.0);
  fragColor=vec4(hue*vcol.rgb,A*vcol.a);
}
