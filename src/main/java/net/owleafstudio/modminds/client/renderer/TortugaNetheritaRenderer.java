package net.owleafstudio.modminds.client.renderer;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.owleafstudio.modminds.entity.TortugaNetheritaEntity;
import net.owleafstudio.modminds.entity.model.TortugaNetheritaModel;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.renderer.GeoEntityRenderer;

public class TortugaNetheritaRenderer extends GeoEntityRenderer<TortugaNetheritaEntity, net.minecraft.client.renderer.entity.state.LivingEntityRenderState> {
   public TortugaNetheritaRenderer(EntityRendererProvider.Context renderManager) {
      super(renderManager, new TortugaNetheritaModel());
      this.shadowRadius = 1.3F;
   }

   public RenderType getRenderType(net.minecraft.client.renderer.entity.state.LivingEntityRenderState estado$, Identifier texture) {
      return net.minecraft.client.renderer.rendertype.RenderTypes.entityTranslucent(texture);
   }

   
}
