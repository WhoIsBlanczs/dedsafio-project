package net.owleafstudio.modminds.client.renderer;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.owleafstudio.modminds.entity.TorretaBorgEntity;
import net.owleafstudio.modminds.entity.layer.TorretaBorgLayer;
import net.owleafstudio.modminds.entity.model.TorretaBorgModel;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.renderer.GeoEntityRenderer;

public class TorretaBorgRenderer extends GeoEntityRenderer<TorretaBorgEntity, net.minecraft.client.renderer.entity.state.LivingEntityRenderState> {
   public TorretaBorgRenderer(EntityRendererProvider.Context renderManager) {
      super(renderManager, new TorretaBorgModel());
      this.shadowRadius = 0.0F;
      this.withRenderLayer(new TorretaBorgLayer(this));
   }

   public RenderType getRenderType(net.minecraft.client.renderer.entity.state.LivingEntityRenderState estado$, Identifier texture) {
      return net.minecraft.client.renderer.rendertype.RenderTypes.entityTranslucent(texture);
   }

   

   @Override
   public void adjustModelBonesForRender(com.geckolib.renderer.base.RenderPassInfo<net.minecraft.client.renderer.entity.state.LivingEntityRenderState> info, com.geckolib.renderer.base.BoneSnapshots snapshots) {
      super.adjustModelBonesForRender(info, snapshots);
      net.minecraft.client.renderer.entity.state.LivingEntityRenderState estado = info.renderState();
      snapshots.ifPresent("bone", hueso -> {
         hueso.setRotX(estado.xRot * (float)(Math.PI / 180.0));
         hueso.setRotY(estado.yRot * (float)(Math.PI / 180.0));
      });
   }
}
