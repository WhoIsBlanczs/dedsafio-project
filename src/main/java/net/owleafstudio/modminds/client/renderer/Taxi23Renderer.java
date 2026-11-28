package net.owleafstudio.modminds.client.renderer;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.owleafstudio.modminds.entity.Taxi23Entity;
import net.owleafstudio.modminds.entity.model.Taxi23Model;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.renderer.GeoEntityRenderer;

public class Taxi23Renderer extends GeoEntityRenderer<Taxi23Entity, net.minecraft.client.renderer.entity.state.LivingEntityRenderState> {
   public Taxi23Renderer(EntityRendererProvider.Context renderManager) {
      super(renderManager, new Taxi23Model());
      this.shadowRadius = 0.0F;
   }

   public RenderType getRenderType(net.minecraft.client.renderer.entity.state.LivingEntityRenderState estado$, Identifier texture) {
      return net.minecraft.client.renderer.rendertype.RenderTypes.entityTranslucent(texture);
   }

   
}
