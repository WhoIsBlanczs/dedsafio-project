package net.owleafstudio.modminds.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.owleafstudio.modminds.entity.SoldadoReviilEntity;
import net.owleafstudio.modminds.entity.model.SoldadoReviilModel;
import com.geckolib.renderer.GeoEntityRenderer;

public class SoldadoReviilRenderer extends GeoEntityRenderer<SoldadoReviilEntity, net.minecraft.client.renderer.entity.state.LivingEntityRenderState> {
   public SoldadoReviilRenderer(EntityRendererProvider.Context renderManager) {
      super(renderManager, new SoldadoReviilModel());
      this.shadowRadius = 0.9F;
   }
}
