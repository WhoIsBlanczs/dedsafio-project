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

vec3 sunRamp(float h){
  vec3 c0=vec3(0.30,0.04,0.0); vec3 c1=vec3(0.85,0.22,0.03); vec3 c2=vec3(1.0,0.55,0.10);
  vec3 c3=vec3(1.0,0.82,0.38); vec3 c4=vec3(1.0,0.96,0.78);
  if(h<0.25) return mix(c0,c1,h/0.25);
  if(h<0.5) return mix(c1,c2,(h-0.25)/0.25);
  if(h<0.75) return mix(c2,c3,(h-0.5)/0.25);
  return mix(c3,c4,(h-0.75)/0.25);
}
void main(){
  float r2=dot(uv,uv);
  if(r2>1.0){ fragColor=vec4(0.0); return; }
  float mu=sqrt(max(0.0,1.0-r2));
  vec3 n=vec3(uv,mu);
  float t=Params.x; float sp=Params.y;
  float cs=cos(sp); float sn=sin(sp);
  vec3 p=vec3(cs*n.x+sn*n.z, n.y, -sn*n.x+cs*n.z);
  // granulacion: celdas de conveccion que hierven
  float g1=fbm3(p*13.0+vec3(0.0,0.0,t*0.12));
  float g2=fbm3(p*38.0+vec3(t*0.2,0.0,0.0)+g1*2.2);
  float gran=mix(g1,g2,0.6);
  float lanes=1.0-abs(2.0*vn3(p*24.0+vec3(0.0,t*0.08,0.0)+g1*3.0)-1.0);
  lanes=lanes*lanes*lanes;
  // manchas solares cerca del ecuador
  float sg=fbm3(p*2.6+vec3(7.0));
  float lat=1.0-smoothstep(0.30,0.80,abs(p.y));
  float spot=smoothstep(0.60,0.72,sg)*lat;
  float umbra=smoothstep(0.69,0.80,sg)*lat;
  float fac=smoothstep(0.50,0.62,sg)*(1.0-spot)*pow(1.0-mu,2.0);
  float heat=0.58+0.95*(gran-0.5)-0.22*lanes-0.45*spot-0.25*umbra+0.20*fac;
  // destellos brillantes (fulguraciones) que laten
  float fl=pow(1.0-abs(2.0*vn3(p*5.0+vec3(t*0.05,3.0,0.0))-1.0),14.0);
  heat+=0.35*fl*(0.5+0.5*sin(t*1.7+p.x*4.0));
  heat=clamp(heat,0.0,1.0);
  vec3 col=sunRamp(heat);
  // oscurecimiento del borde (limbo): mas oscuro y rojizo
  float limb=1.0-0.66*(1.0-pow(mu,0.85));
  col*=limb*mix(vec3(1.0,0.50,0.25),vec3(1.0),pow(mu,0.45));
  col*=1.12;
  col=mix(col,vec3(1.0,0.97,0.88),clamp(Params.z,0.0,1.0));
  float a=smoothstep(0.0,0.05,mu)*vcol.a;
  fragColor=vec4(col*vcol.rgb,a);
}
