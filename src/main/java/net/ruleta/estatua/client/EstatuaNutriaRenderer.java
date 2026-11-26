package net.ruleta.estatua.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.ruleta.estatua.EstatuaNutriaBE;
import net.ruleta.estatua.EstatuaNutriaBloque;

public class EstatuaNutriaRenderer implements BlockEntityRenderer<EstatuaNutriaBE> {
   public EstatuaNutriaRenderer(Context ctx) {
   }

   public boolean shouldRenderOffScreen(EstatuaNutriaBE be) {
      return true;
   }

   public int getViewDistance() {
      return 96;
   }

   public void render(EstatuaNutriaBE be, float parcial, PoseStack ps, MultiBufferSource buf, int luz, int overlay) {
      BlockState estado = be.getBlockState();
      if (estado.getBlock() instanceof EstatuaNutriaBloque) {
         ps.pushPose();
         ps.translate(0.5, 0.0, 0.5);
         ps.mulPose(Axis.YP.rotationDegrees(180.0F - ((Direction)estado.getValue(EstatuaNutriaBloque.FACING)).toYRot()));
         dibujar(ps, buf, luz, overlay);
         ps.popPose();
      }
   }

   public static void dibujar(PoseStack ps, MultiBufferSource buf, int luz, int overlay) {
      ObjModelo modelo = ObjModelo.obtener();
      if (!modelo.grupos().isEmpty()) {
         Pose p = ps.last();

         for (ObjModelo.Grupo g : modelo.grupos()) {
            VertexConsumer vc = buf.getBuffer(RenderType.entityCutoutNoCull(g.textura()));
            float[] d = g.datos();

            for (int i = 0; i < d.length; i += 8) {
               vc.addVertex(p, d[i], d[i + 1], d[i + 2])
                  .setColor(255, 255, 255, 255)
                  .setUv(d[i + 3], d[i + 4])
                  .setOverlay(OverlayTexture.NO_OVERLAY)
                  .setLight(luz)
                  .setNormal(p, d[i + 5], d[i + 6], d[i + 7]);
            }
         }
      }
   }
}
