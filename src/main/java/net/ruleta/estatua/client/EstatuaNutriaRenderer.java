package net.ruleta.estatua.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Object;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Axis;
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

   public void render(EstatuaNutriaBE be, float parcial, PoseStack ps, Object buf, int luz, int overlay) {
      BlockState estado = be.getBlockState();
      if (estado.getBlock() instanceof EstatuaNutriaBloque) {
         ps.pushPose();
         ps.translate(0.5, 0.0, 0.5);
         ps.mulPose(Axis.YP.rotationDegrees(180.0F - ((Direction)estado.getValue(EstatuaNutriaBloque.FACING)).toYRot()));
         dibujar(ps, buf, luz, overlay);
         ps.popPose();
      }
   }

   public static void dibujar(PoseStack ps, Object buf, int luz, int overlay) { }
}
