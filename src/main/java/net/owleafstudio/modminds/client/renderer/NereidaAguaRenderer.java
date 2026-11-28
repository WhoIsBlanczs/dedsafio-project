package net.owleafstudio.modminds.client.renderer;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.owleafstudio.modminds.entity.NereidaAguaEntity;
import net.owleafstudio.modminds.entity.model.NereidaAguaModel;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.renderer.GeoEntityRenderer;

public class NereidaAguaRenderer extends GeoEntityRenderer<NereidaAguaEntity, net.minecraft.client.renderer.entity.state.LivingEntityRenderState> {
   public NereidaAguaRenderer(EntityRendererProvider.Context renderManager) {
      super(renderManager, new NereidaAguaModel());
      this.withScale(0.7F);
      this.shadowRadius = 0.0F;
   }

   public RenderType getRenderType(net.minecraft.client.renderer.entity.state.LivingEntityRenderState estado$, Identifier texture) {
      return net.minecraft.client.renderer.rendertype.RenderTypes.entityTranslucent(texture);
   }

   

   @Override
   public void adjustModelBonesForRender(com.geckolib.renderer.base.RenderPassInfo<net.minecraft.client.renderer.entity.state.LivingEntityRenderState> info, com.geckolib.renderer.base.BoneSnapshots snapshots) {
      super.adjustModelBonesForRender(info, snapshots);
      net.minecraft.client.renderer.entity.state.LivingEntityRenderState estado = info.renderState();
      snapshots.ifPresent("head", hueso -> {
         hueso.setRotX(estado.xRot * (float)(Math.PI / 180.0));
         hueso.setRotY(estado.yRot * (float)(Math.PI / 180.0));
      });
   }
}
