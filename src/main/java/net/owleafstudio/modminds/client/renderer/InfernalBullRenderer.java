package net.owleafstudio.modminds.client.renderer;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.owleafstudio.modminds.entity.InfernalBullEntity;
import net.owleafstudio.modminds.entity.model.InfernalBullModel;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.renderer.GeoEntityRenderer;

public class InfernalBullRenderer extends GeoEntityRenderer<InfernalBullEntity, net.minecraft.client.renderer.entity.state.LivingEntityRenderState> {
   public InfernalBullRenderer(EntityRendererProvider.Context renderManager) {
      super(renderManager, new InfernalBullModel());
      this.shadowRadius = 0.0F;
   }

   public RenderType getRenderType(net.minecraft.client.renderer.entity.state.LivingEntityRenderState estado$, Identifier texture) {
      return net.minecraft.client.renderer.rendertype.RenderTypes.entityTranslucent(texture);
   }

   
}
