package net.owleafstudio.modminds.client.renderer;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.owleafstudio.modminds.entity.WardenMounstrosityEntity;
import net.owleafstudio.modminds.entity.model.WardenMounstrosityModel;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.renderer.GeoEntityRenderer;

public class WardenMounstrosityRenderer extends GeoEntityRenderer<WardenMounstrosityEntity, net.minecraft.client.renderer.entity.state.LivingEntityRenderState> {
   public WardenMounstrosityRenderer(EntityRendererProvider.Context renderManager) {
      super(renderManager, new WardenMounstrosityModel());
      this.withScale(1.4F);
      this.shadowRadius = 0.0F;
   }

   public RenderType getRenderType(net.minecraft.client.renderer.entity.state.LivingEntityRenderState estado$, Identifier texture) {
      return net.minecraft.client.renderer.rendertype.RenderTypes.entityTranslucent(texture);
   }

   
}
