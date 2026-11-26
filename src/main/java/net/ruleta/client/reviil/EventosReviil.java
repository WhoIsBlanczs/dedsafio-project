package net.ruleta.client.reviil;

import com.mojang.blaze3d.vertex.VertexSorting;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.material.FogType;
import net.ruleta.client.agujero.AgujeroCliente;
import net.ruleta.client.compat.ClientPlayerNetworkEvent;
import net.ruleta.client.compat.RenderLevelStageEvent;
import net.ruleta.client.compat.SubscribeEvent;
import net.ruleta.client.compat.TickEvent;
import net.ruleta.client.compat.ViewportEvent;
import org.joml.Matrix4f;

public final class EventosReviil {
   private EventosReviil() {
   }

   @SubscribeEvent
   public static void alDibujarMundo(RenderLevelStageEvent e) { }

   @SubscribeEvent
   public static void alColorearNiebla(ViewportEvent.ComputeFogColor e) {
      float[] c = CieloReviil.niebla(e.getRed(), e.getGreen(), e.getBlue());
      if (c != null) {
         e.setRed(c[0]);
         e.setGreen(c[1]);
         e.setBlue(c[2]);
      }
   }

   @SubscribeEvent
   public static void alTick(TickEvent.ClientTickEvent e) {
      if (e.phase == TickEvent.Phase.END) {
         CieloReviil.tick();
      }
   }

   @SubscribeEvent
   public static void alSalir(ClientPlayerNetworkEvent.LoggingOut e) {
      CieloReviil.limpiar();
   }
}
