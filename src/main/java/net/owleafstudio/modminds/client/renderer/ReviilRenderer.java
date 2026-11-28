package net.owleafstudio.modminds.client.renderer;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.owleafstudio.modminds.entity.ReviilEntity;
import net.owleafstudio.modminds.entity.layer.ReviilLayer;
import net.owleafstudio.modminds.entity.model.ReviilModel;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.renderer.GeoEntityRenderer;

public class ReviilRenderer extends GeoEntityRenderer<ReviilEntity, net.minecraft.client.renderer.entity.state.LivingEntityRenderState> {
   private static final String[] WHEEL_BONES = new String[]{"ruleta_body", "ruleta_body2"};
   private static final float WHEEL_BACK_X = 0.0F;
   private static final float WHEEL_BACK_Y = 8.0F;
   private static final float WHEEL_BACK_Z = 39.0F;
   private static final float WHEEL_GROWTH = 1.7F;

   public ReviilRenderer(EntityRendererProvider.Context renderManager) {
      super(renderManager, new ReviilModel());
      this.shadowRadius = 0.0F;
      this.withRenderLayer(new ReviilLayer(this));
   }

   public RenderType getRenderType(net.minecraft.client.renderer.entity.state.LivingEntityRenderState estado$, Identifier texture) {
      return net.minecraft.client.renderer.rendertype.RenderTypes.entityTranslucent(texture);
   }

   public void preRender(
      PoseStack poseStack,
      ReviilEntity entity,
      BakedGeoModel model,
      MultiBufferSource bufferSource,
      VertexConsumer buffer,
      boolean isReRender,
      float partialTick,
      int packedLight,
      int packedOverlay,
      int color
   ) {
      float scale = entity.getScale();
      this.scaleHeight = scale;
      this.scaleWidth = scale;
      carryWheelOnBack(entity, model);
      super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
   }

   private static void carryWheelOnBack(ReviilEntity entity, BakedGeoModel model) {
      for (String bone : WHEEL_BONES) {
         model.getBone(bone).ifPresent(wheel -> {
            if (entity.isFury()) {
               wheel.updatePosition(0.0F, 8.0F, 39.0F);
               wheel.updateScale(1.7F, 1.7F, 1.7F);
            } else {
               wheel.updatePosition(0.0F, 0.0F, 0.0F);
               wheel.updateScale(1.0F, 1.0F, 1.0F);
            }
         });
      }
   }

   protected float getDeathMaxRotation(ReviilEntity entityLivingBaseIn) {
      return 0.0F;
   }
}
