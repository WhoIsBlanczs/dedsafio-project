package net.owleafstudio.modminds.utils;

import net.minecraft.util.Mth;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import com.geckolib.cache.model.GeoBone;

public class AnimUtils {
   public static void renderPartOverBone(
      ModelPart model, GeoBone bone, PoseStack stack, VertexConsumer buffer, int packedLightIn, int packedOverlayIn, float alpha
   ) {
      renderPartOverBone(model, bone, stack, buffer, packedLightIn, packedOverlayIn, 1.0F, 1.0F, 1.0F, alpha);
   }

   public static void renderPartOverBone(
      ModelPart model, GeoBone bone, PoseStack stack, VertexConsumer buffer, int packedLightIn, int packedOverlayIn, float r, float g, float b, float a
   ) {
      setupModelFromBone(model, bone);
      model.render(stack, buffer, packedLightIn, packedOverlayIn, packColor(r, g, b, a));
   }

   private static int packColor(float r, float g, float b, float a) {
      return clampChannel(a) << 24 | clampChannel(r) << 16 | clampChannel(g) << 8 | clampChannel(b);
   }

   private static int clampChannel(float channel) {
      return (int)(Mth.clamp(channel, 0.0F, 1.0F) * 255.0F + 0.5F);
   }

   public static void setupModelFromBone(ModelPart model, GeoBone bone) {
      model.setPos(bone.getPivotX(), bone.getPivotY(), bone.getPivotZ());
      model.xRot = 0.0F;
      model.yRot = 0.0F;
      model.zRot = 0.0F;
   }
}
