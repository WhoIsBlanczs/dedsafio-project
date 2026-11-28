package net.owleafstudio.modminds.client.renderer;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.owleafstudio.modminds.entity.PuertaBorgAmarilloEntity;
import net.owleafstudio.modminds.entity.model.PuertaBorgAmarilloModel;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.renderer.GeoEntityRenderer;

public class PuertaBorgAmarilloRenderer extends GeoEntityRenderer<PuertaBorgAmarilloEntity, net.minecraft.client.renderer.entity.state.LivingEntityRenderState> {
   public PuertaBorgAmarilloRenderer(EntityRendererProvider.Context renderManager) {
      super(renderManager, new PuertaBorgAmarilloModel());
      this.shadowRadius = 0.0F;
   }

   public RenderType getRenderType(net.minecraft.client.renderer.entity.state.LivingEntityRenderState estado$, Identifier texture) {
      return net.minecraft.client.renderer.rendertype.RenderTypes.entityTranslucent(texture);
   }

   

   protected float getDeathMaxRotation(PuertaBorgAmarilloEntity entityLivingBaseIn) {
      return 0.0F;
   }
}
