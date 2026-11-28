package net.owleafstudio.modminds.client.renderer;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.owleafstudio.modminds.entity.SpaceshipEntity;
import net.owleafstudio.modminds.entity.model.SpaceshipModel;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.renderer.GeoEntityRenderer;

public class SpaceshipRenderer extends GeoEntityRenderer<SpaceshipEntity, net.minecraft.client.renderer.entity.state.LivingEntityRenderState> {
   public SpaceshipRenderer(EntityRendererProvider.Context renderManager) {
      super(renderManager, new SpaceshipModel());
      this.shadowRadius = 0.5F;
   }

   public RenderType getRenderType(net.minecraft.client.renderer.entity.state.LivingEntityRenderState estado$, Identifier texture) {
      return net.minecraft.client.renderer.rendertype.RenderTypes.entityTranslucent(texture);
   }

   

   @Override
   public void adjustModelBonesForRender(com.geckolib.renderer.base.RenderPassInfo<net.minecraft.client.renderer.entity.state.LivingEntityRenderState> info, com.geckolib.renderer.base.BoneSnapshots snapshots) {
      super.adjustModelBonesForRender(info, snapshots);
      net.minecraft.client.renderer.entity.state.LivingEntityRenderState estado = info.renderState();
      snapshots.ifPresent("root", hueso -> {
         hueso.setRotX(estado.xRot * (float)(Math.PI / 180.0));
         hueso.setRotY(estado.yRot * (float)(Math.PI / 180.0));
      });
   }
}
