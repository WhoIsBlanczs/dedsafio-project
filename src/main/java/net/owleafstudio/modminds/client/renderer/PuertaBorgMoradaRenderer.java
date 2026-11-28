package net.owleafstudio.modminds.client.renderer;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.owleafstudio.modminds.entity.PuertaBorgMoradaEntity;
import net.owleafstudio.modminds.entity.model.PuertaBorgMoradaModel;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.renderer.GeoEntityRenderer;

public class PuertaBorgMoradaRenderer extends GeoEntityRenderer<PuertaBorgMoradaEntity, net.minecraft.client.renderer.entity.state.LivingEntityRenderState> {
   public PuertaBorgMoradaRenderer(EntityRendererProvider.Context renderManager) {
      super(renderManager, new PuertaBorgMoradaModel());
      this.shadowRadius = 0.0F;
   }

   public RenderType getRenderType(net.minecraft.client.renderer.entity.state.LivingEntityRenderState estado$, Identifier texture) {
      return net.minecraft.client.renderer.rendertype.RenderTypes.entityTranslucent(texture);
   }

   

   protected float getDeathMaxRotation(com.geckolib.renderer.base.GeoRenderState estado$) {
      return 0.0F;
   }
}
