package net.ruleta.client.eclipse;

import com.supernova.client.ScreenEffectHandler;
import net.minecraft.client.multiplayer.ClientLevel;
import net.ruleta.client.agujero.AgujeroCliente;
import net.ruleta.client.meteoritos.MeteoritosCliente;
import net.ruleta.client.tornado.TornadoCliente;

public final class EfectosEclipse {
   private EfectosEclipse() {
   }

   private static float oscuridadCielo() {
      return Math.max(Math.max(EclipseCliente.oscuridad(), AgujeroCliente.nivel() * 0.6F), TornadoCliente.oscuridad());
   }

   public static boolean hayOscuridad() {
      return oscuridadCielo() > 0.0F || EclipseCliente.oscuridadBloques() > 0.0F;
   }

   public static float factorLuz(ClientLevel nivel, int luzCielo, int luzBloque, float oscuroCielo) {
      if (luzCielo == 15 && luzBloque == 15) {
         return 1.0F;
      } else {
         float k = oscuridadCielo();
         float kb = EclipseCliente.oscuridadBloques();
         if (k <= 0.0F && kb <= 0.0F) {
            return 1.0F;
         } else {
            float cielo = curva(luzCielo) * oscuroCielo;
            float bloque = curva(luzBloque) * 1.5F;
            float delCielo = cielo / (cielo + bloque + 0.02F);
            return Math.max(0.0F, 1.0F - k * delCielo - kb * (1.0F - delCielo));
         }
      }
   }

   private static float curva(int nivel) {
      float x = nivel / 15.0F;
      return x / (4.0F - 3.0F * x);
   }

   public static boolean omitirCielo() {
      return AgujeroCliente.tapaCielo() || ScreenEffectHandler.skyMix > 0.02F || MeteoritosCliente.tapaCielo();
   }

   public static boolean omitirNubes() {
      return AgujeroCliente.tapaCielo()
         || TornadoCliente.tapaCielo()
         || EclipseCliente.activo()
         || ScreenEffectHandler.skyMix > 0.02F
         || MeteoritosCliente.tapaCielo();
   }
}
