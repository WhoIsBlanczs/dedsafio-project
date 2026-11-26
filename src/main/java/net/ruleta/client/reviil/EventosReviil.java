package net.ruleta.client.reviil;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexSorting;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.DimensionSpecialEffects.SkyType;
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
   public static void alDibujarMundo(RenderLevelStageEvent e) {
      if (e.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL && CieloReviil.visible() && !AgujeroCliente.tapaCielo()) {
         Minecraft mc = Minecraft.getInstance();
         if (mc.level != null && mc.level.effects().skyType() == SkyType.NORMAL) {
            FogType dentro = e.getCamera().getFluidInCamera();
            if (dentro != FogType.LAVA && dentro != FogType.POWDER_SNOW) {
               Camera camara = e.getCamera();
               Matrix4f vista = new Matrix4f()
                  .rotateX(camara.getXRot() * (float) (Math.PI / 180.0))
                  .rotateY((camara.getYRot() + 180.0F) * (float) (Math.PI / 180.0));
               Matrix4f proyeccion = new Matrix4f(e.getProjectionMatrix());
               float lejos = mc.options.getEffectiveRenderDistance() * 16.0F * 4.0F;
               Matrix4f proyeccionAntes = new Matrix4f(RenderSystem.getProjectionMatrix());
               VertexSorting ordenAntes = RenderSystem.getVertexSorting();
               mc.getMainRenderTarget().bindWrite(true);
               RenderSystem.setProjectionMatrix(proyeccion, VertexSorting.DISTANCE_TO_ORIGIN);
               RenderSystem.depthFunc(515);

               try {
                  CieloReviil.dibujar(vista, lejos);
               } finally {
                  RenderSystem.depthMask(true);
                  RenderSystem.enableDepthTest();
                  RenderSystem.enableCull();
                  RenderSystem.disableBlend();
                  RenderSystem.defaultBlendFunc();
                  RenderSystem.setProjectionMatrix(proyeccionAntes, ordenAntes);
               }
            }
         }
      }
   }

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
