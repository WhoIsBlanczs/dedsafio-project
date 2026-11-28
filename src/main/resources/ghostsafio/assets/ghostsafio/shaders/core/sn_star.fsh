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
  vec2 p=vec2(uv.x*2.0-1.0, uv.y*2.0-1.0);
  float r=length(p);
  if(r>=1.0){ fragColor=vec4(0.0); return; }
  float sp=Params.y;                                        // cuanto se notan los picos (0 = redonda)
  float core=exp(-pow(r/0.11,2.0));
  float halo=exp(-pow(r/0.32,2.0))*0.5+1.0/(1.0+pow(r/0.13,2.2))*0.28;
  // cuatro picos de difraccion (cruz)
  float ax=abs(p.x); float ay=abs(p.y);
  float spikes=(exp(-ay*30.0)*exp(-ax*2.6)+exp(-ax*30.0)*exp(-ay*2.6))*sp;
  // picos diagonales mas debiles (como un telescopio)
  vec2 d=vec2(p.x+p.y,p.x-p.y)*0.7071;
  float spikes2=(exp(-abs(d.x)*40.0)*exp(-abs(d.y)*3.4)+exp(-abs(d.y)*40.0)*exp(-abs(d.x)*3.4))*sp*0.35;
  float a=clamp(core+halo+spikes+spikes2,0.0,1.0)*(1.0-smoothstep(0.75,1.0,r));
  vec3 col=mix(vcol.rgb,vec3(1.0),core);
  fragColor=vec4(col,a*vcol.a);
}
