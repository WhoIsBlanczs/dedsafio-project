package net.owleafstudio.modminds.client.renderer;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.owleafstudio.modminds.entity.ReactorHydraEntity;
import net.owleafstudio.modminds.entity.layer.ReactorHydraLayer;
import net.owleafstudio.modminds.entity.model.ReactorHydraModel;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.renderer.GeoEntityRenderer;

public class ReactorHydraRenderer extends GeoEntityRenderer<ReactorHydraEntity, net.minecraft.client.renderer.entity.state.LivingEntityRenderState> {
   public ReactorHydraRenderer(EntityRendererProvider.Context renderManager) {
      super(renderManager, new ReactorHydraModel());
      this.shadowRadius = 0.0F;
      this.withRenderLayer(new ReactorHydraLayer(this));
   }

   public RenderType getRenderType(net.minecraft.client.renderer.entity.state.LivingEntityRenderState estado$, Identifier texture) {
      return net.minecraft.client.renderer.rendertype.RenderTypes.entityTranslucent(texture);
   }

   

   protected float getDeathMaxRotation(com.geckolib.renderer.base.GeoRenderState estado$) {
      return 0.0F;
   }
}
