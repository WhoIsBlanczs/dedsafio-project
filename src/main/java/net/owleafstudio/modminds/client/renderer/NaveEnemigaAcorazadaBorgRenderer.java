package net.owleafstudio.modminds.client.renderer;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.owleafstudio.modminds.entity.NaveEnemigaAcorazadaBorgEntity;
import net.owleafstudio.modminds.entity.model.NaveEnemigaAcorazadaBorgModel;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.renderer.GeoEntityRenderer;

public class NaveEnemigaAcorazadaBorgRenderer extends GeoEntityRenderer<NaveEnemigaAcorazadaBorgEntity, net.minecraft.client.renderer.entity.state.LivingEntityRenderState> {
   public NaveEnemigaAcorazadaBorgRenderer(EntityRendererProvider.Context renderManager) {
      super(renderManager, new NaveEnemigaAcorazadaBorgModel());
      this.shadowRadius = 0.5F;
   }

   public RenderType getRenderType(net.minecraft.client.renderer.entity.state.LivingEntityRenderState estado$, Identifier texture) {
      return net.minecraft.client.renderer.rendertype.RenderTypes.entityTranslucent(texture);
   }

   

   protected float getDeathMaxRotation(NaveEnemigaAcorazadaBorgEntity entityLivingBaseIn) {
      return 0.0F;
   }
}
