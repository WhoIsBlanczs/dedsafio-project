package net.ruleta.client.dedsafio;

import com.mojang.blaze3d.vertex.VertexSorting;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.material.FogType;
import net.ruleta.client.compat.ClientPlayerNetworkEvent;
import net.ruleta.client.compat.RenderGuiEvent;
import net.ruleta.client.compat.RenderLevelStageEvent;
import net.ruleta.client.compat.SubscribeEvent;
import net.ruleta.client.compat.TickEvent;
import net.ruleta.client.compat.ViewportEvent;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public final class EventosDedsafio {
   private EventosDedsafio() {
   }

   @SubscribeEvent
   public static void alDibujarMundo(RenderLevelStageEvent e) { }

   @SubscribeEvent
   public static void alColorearNiebla(ViewportEvent.ComputeFogColor e) {
      if (DedsafioCielo.enDimension() && e.getCamera().getFluidInCamera() == FogType.NONE) {
         e.setRed(DedsafioCielo.HORIZONTE[0]);
         e.setGreen(DedsafioCielo.HORIZONTE[1]);
         e.setBlue(DedsafioCielo.HORIZONTE[2]);
      }
   }

   @SubscribeEvent
   public static void alAngulosCamara(ViewportEvent.ComputeCameraAngles e) {
      float[] b = OlaCliente.balanceo();
      if (b != null) {
         e.setYaw(e.getYaw() + b[0]);
         e.setPitch(e.getPitch() + b[1]);
         e.setRoll(e.getRoll() + b[2]);
      }
   }

   @SubscribeEvent
   public static void alTick(TickEvent.ClientTickEvent e) {
      if (e.phase == TickEvent.Phase.END) {
         DedsafioCielo.tick();
         EscenaDedsafio.tick();
         OlaCliente.tick();
         DedsafioCielo.mirarFigura();
      }
   }

   @SubscribeEvent
   public static void alSalir(ClientPlayerNetworkEvent.LoggingOut e) {
      DedsafioCielo.limpiar();
      EscenaDedsafio.limpiar();
      OlaCliente.limpiar();
      EspumaDedsafio.limpiar();
      AudioDedsafio.pararTodo();
   }

   @SubscribeEvent
   public static void alDibujarHud(RenderGuiEvent.Post e) {
      HudDedsafio.dibujar(e.getGuiGraphics());
   }
}
