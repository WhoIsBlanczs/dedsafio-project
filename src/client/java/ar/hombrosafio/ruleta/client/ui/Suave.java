package ar.hombrosafio.ruleta.client.ui;

import net.minecraft.Util;

public final class Suave {
   public float v;
   private long ultimo = -1L;

   public Suave() {
   }

   public Suave(float inicial) {
      this.v = inicial;
   }

   public float hacia(float objetivo, float velocidad) {
      long ahora = Util.getMillis();
      float dt = this.ultimo < 0L ? 0.016F : Math.min(0.1F, (float)(ahora - this.ultimo) / 1000.0F);
      this.ultimo = ahora;
      this.v = this.v + (objetivo - this.v) * (1.0F - (float)Math.exp(-velocidad * dt));
      if (Math.abs(objetivo - this.v) < 0.001F) {
         this.v = objetivo;
      }

      return this.v;
   }

   public void fijar(float valor) {
      this.v = valor;
   }
}
