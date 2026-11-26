package net.ruleta.client.dedsafio;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexSorting;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.DimensionSpecialEffects.SkyType;
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
   public static void alDibujarMundo(RenderLevelStageEvent e) {
      if (e.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL && (DedsafioCielo.visible() || OlaCliente.activaFuera())) {
         Minecraft mc = Minecraft.getInstance();
         if (mc.level != null && mc.level.effects().skyType() == SkyType.NORMAL) {
            FogType dentro = e.getCamera().getFluidInCamera();
            if (dentro == FogType.NONE) {
               Camera camara = e.getCamera();
               Matrix4f vista = new Matrix4f()
                  .rotateX(camara.getXRot() * (float) (Math.PI / 180.0))
                  .rotateY((camara.getYRot() + 180.0F) * (float) (Math.PI / 180.0));
               Matrix4f vistaMundo = e.getModelViewMatrix();
               if (vistaMundo != null) {
                  Matrix4f m = new Matrix4f(vistaMundo).setTranslation(0.0F, 0.0F, 0.0F);
                  Vector3f a = vista.transformDirection(new Vector3f(0.0F, 0.0F, -1.0F));
                  Vector3f b = m.transformDirection(new Vector3f(0.0F, 0.0F, -1.0F));
                  if (Float.isFinite(b.x()) && Float.isFinite(b.y()) && Float.isFinite(b.z()) && Math.abs(m.determinant() - 1.0F) < 0.05F && a.dot(b) > 0.99F) {
                     vista = m;
                  }
               }

               Matrix4f proyeccion = new Matrix4f(e.getProjectionMatrix());
               float lejos = mc.options.getEffectiveRenderDistance() * 16.0F * 4.0F;
               Matrix4f proyeccionAntes = new Matrix4f(RenderSystem.getProjectionMatrix());
               VertexSorting ordenAntes = RenderSystem.getVertexSorting();
               mc.getMainRenderTarget().bindWrite(true);
               RenderSystem.setProjectionMatrix(proyeccion, VertexSorting.DISTANCE_TO_ORIGIN);
               RenderSystem.depthFunc(515);

               try {
                  if (DedsafioCielo.visible()) {
                     DedsafioCielo.camX = camara.getPosition().x;
                     DedsafioCielo.camZ = camara.getPosition().z;
                     DedsafioCielo.dibujar(vista, lejos, camara.getPosition().y);
                     AguaDedsafio.dibujar(vista, mc.options.getEffectiveRenderDistance() * 16.0F * 1.5F, camara.getPosition().y);
                     EspumaDedsafio.dibujar(vista, camara.getPosition().x, camara.getPosition().y, camara.getPosition().z);
                     OlaCliente.dibujar(vista, camara.getPosition().x, camara.getPosition().y, camara.getPosition().z);
                     RayosDedsafio.dibujar(proyeccion, vista);
                     OlaCliente.dibujarDentro();
                     return;
                  }

                  OlaCliente.dibujar(vista, camara.getPosition().x, camara.getPosition().y, camara.getPosition().z);
                  OlaCliente.dibujarDentro();
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
