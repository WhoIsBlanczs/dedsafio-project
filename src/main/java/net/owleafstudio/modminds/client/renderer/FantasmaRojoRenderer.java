package net.owleafstudio.modminds.client.renderer;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.owleafstudio.modminds.entity.FantasmaRojoEntity;
import net.owleafstudio.modminds.entity.layer.FantasmaRojoLayer;
import net.owleafstudio.modminds.entity.model.FantasmaRojoModel;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.renderer.GeoEntityRenderer;

public class FantasmaRojoRenderer extends GeoEntityRenderer<FantasmaRojoEntity, net.minecraft.client.renderer.entity.state.LivingEntityRenderState> {
   public FantasmaRojoRenderer(EntityRendererProvider.Context renderManager) {
      super(renderManager, new FantasmaRojoModel());
      this.shadowRadius = 0.6F;
      this.withRenderLayer(new FantasmaRojoLayer(this));
   }

   public RenderType getRenderType(net.minecraft.client.renderer.entity.state.LivingEntityRenderState estado$, Identifier texture) {
      return net.minecraft.client.renderer.rendertype.RenderTypes.entityTranslucent(texture);
   }

   
}
