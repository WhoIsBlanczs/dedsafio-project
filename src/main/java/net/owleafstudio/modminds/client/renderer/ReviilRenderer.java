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

   private static final com.geckolib.constant.dataticket.DataTicket<Float> ESCALA = com.geckolib.constant.dataticket.DataTicket.create("modminds_reviil_escala", Float.class);
   private static final com.geckolib.constant.dataticket.DataTicket<Boolean> FURIA = com.geckolib.constant.dataticket.DataTicket.create("modminds_reviil_furia", Boolean.class);

   @Override
   public void captureDefaultRenderState(ReviilEntity entity, Void relacionado$, net.minecraft.client.renderer.entity.state.LivingEntityRenderState estado$, float partialTick) {
      super.captureDefaultRenderState(entity, relacionado$, estado$, partialTick);
      com.geckolib.renderer.base.GeoRenderState geo = (com.geckolib.renderer.base.GeoRenderState)estado$;
      geo.addGeckolibData(ESCALA, entity.getScale());
      geo.addGeckolibData(FURIA, entity.isFury());
   }

   @Override
   public void scaleModelForRender(com.geckolib.renderer.base.RenderPassInfo<net.minecraft.client.renderer.entity.state.LivingEntityRenderState> info, float widthScale, float heightScale) {
      float scale = info.getOrDefaultGeckolibData(ESCALA, 1.0F);
      super.scaleModelForRender(info, widthScale * scale, heightScale * scale);
   }

   @Override
   public void adjustModelBonesForRender(com.geckolib.renderer.base.RenderPassInfo<net.minecraft.client.renderer.entity.state.LivingEntityRenderState> info, com.geckolib.renderer.base.BoneSnapshots snapshots) {
      super.adjustModelBonesForRender(info, snapshots);
      boolean furia = info.getOrDefaultGeckolibData(FURIA, false);
      for (String bone : WHEEL_BONES) {
         snapshots.ifPresent(bone, wheel -> {
            if (furia) {
               wheel.setTranslation(WHEEL_BACK_X, WHEEL_BACK_Y, WHEEL_BACK_Z);
               wheel.setScale(WHEEL_GROWTH, WHEEL_GROWTH, WHEEL_GROWTH);
            } else {
               wheel.setTranslation(0.0F, 0.0F, 0.0F);
               wheel.setScale(1.0F, 1.0F, 1.0F);
            }
         });
      }
   }

   protected float getDeathMaxRotation(com.geckolib.renderer.base.GeoRenderState estado$) {
      return 0.0F;
   }
}
