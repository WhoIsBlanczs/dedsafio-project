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
  float t=Params.x; float L=max(Params.y,0.2); float rot=Params.z;
  float c=cos(rot), s=sin(rot);
  p=vec2(c*p.x+s*p.y, -s*p.x+c*p.y);
  float r=length(p);
  if(r>=1.0){ fragColor=vec4(0.0); return; }
  float core=exp(-pow(r/0.075,2.0));
  float halo=exp(-r/0.14)*0.60;
  float sH=exp(-pow(p.y/0.011,2.0))*exp(-abs(p.x)/(0.30*L));
  float sV=exp(-pow(p.x/0.011,2.0))*exp(-abs(p.y)/(0.30*L));
  float sD=(exp(-pow((p.x-p.y)*0.7071/0.009,2.0))+exp(-pow((p.x+p.y)*0.7071/0.009,2.0)))*exp(-r/(0.16*L))*0.55;
  float shim=0.86+0.14*sin(t*8.0+r*34.0);
  float sp=(sH+sV)*shim+sD;
  vec3 fringe=vec3(1.0, 0.86+0.14*cos(r*26.0), 0.92+0.08*cos(r*19.0+1.0));
  vec3 col=fringe*(core*1.3+halo+sp);
  col*=1.0-smoothstep(0.78,1.0,r);
  float cm=max(max(col.r,col.g),col.b);
  fragColor=vec4(col/max(cm,0.001)*vcol.rgb, clamp(cm,0.0,1.0)*vcol.a);
}
