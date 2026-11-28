package net.owleafstudio.modminds.client.renderer;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.owleafstudio.modminds.entity.FantasmaAmarilloEntity;
import net.owleafstudio.modminds.entity.layer.FantasmaAmarilloLayer;
import net.owleafstudio.modminds.entity.model.FantasmaAmarilloModel;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.renderer.GeoEntityRenderer;

public class FantasmaAmarilloRenderer extends GeoEntityRenderer<FantasmaAmarilloEntity, net.minecraft.client.renderer.entity.state.LivingEntityRenderState> {
   public FantasmaAmarilloRenderer(EntityRendererProvider.Context renderManager) {
      super(renderManager, new FantasmaAmarilloModel());
      this.shadowRadius = 0.6F;
      this.withRenderLayer(new FantasmaAmarilloLayer(this));
   }

   public RenderType getRenderType(net.minecraft.client.renderer.entity.state.LivingEntityRenderState estado$, Identifier texture) {
      return net.minecraft.client.renderer.rendertype.RenderTypes.entityTranslucent(texture);
   }

   
}
