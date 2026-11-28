package net.owleafstudio.modminds.client.renderer;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.owleafstudio.modminds.entity.PuertaBorgAzulEntity;
import net.owleafstudio.modminds.entity.model.PuertaBorgAzulModel;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.renderer.GeoEntityRenderer;

public class PuertaBorgAzulRenderer extends GeoEntityRenderer<PuertaBorgAzulEntity, net.minecraft.client.renderer.entity.state.LivingEntityRenderState> {
   public PuertaBorgAzulRenderer(EntityRendererProvider.Context renderManager) {
      super(renderManager, new PuertaBorgAzulModel());
      this.shadowRadius = 0.0F;
   }

   public RenderType getRenderType(net.minecraft.client.renderer.entity.state.LivingEntityRenderState estado$, Identifier texture) {
      return net.minecraft.client.renderer.rendertype.RenderTypes.entityTranslucent(texture);
   }

   

   protected float getDeathMaxRotation(PuertaBorgAzulEntity entityLivingBaseIn) {
      return 0.0F;
   }
}
