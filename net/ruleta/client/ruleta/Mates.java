package net.ruleta.client.ruleta;

final class Mates {
   private Mates() {
   }

   static float limitar(float v, float lo, float hi) {
      return v < lo ? lo : Math.min(v, hi);
   }

   static float suave(float u) {
      u = limitar(u, 0.0F, 1.0F);
      return u * u * (3.0F - 2.0F * u);
   }

   static float entre(float x, float a, float b) {
      return suave((x - a) / (b - a));
   }

   static float modulo(float a, float m) {
      float r = a % m;
      return r < 0.0F ? r + m : r;
   }

   static int mezclar(int a, int b, float k) {
      k = limitar(k, 0.0F, 1.0F);
      int r = Math.round((a >> 16 & 0xFF) + ((b >> 16 & 0xFF) - (a >> 16 & 0xFF)) * k);
      int g = Math.round((a >> 8 & 0xFF) + ((b >> 8 & 0xFF) - (a >> 8 & 0xFF)) * k);
      int bl = Math.round((a & 0xFF) + ((b & 0xFF) - (a & 0xFF)) * k);
      return r << 16 | g << 8 | bl;
   }

   static int argb(float alfa, int rgb) {
      return Math.round(limitar(alfa, 0.0F, 1.0F) * 255.0F) << 24 | rgb & 16777215;
   }
}
