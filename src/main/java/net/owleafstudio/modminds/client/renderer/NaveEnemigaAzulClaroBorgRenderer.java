package net.owleafstudio.modminds.client.renderer;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.owleafstudio.modminds.entity.NaveEnemigaAzulClaroBorgEntity;
import net.owleafstudio.modminds.entity.model.NaveEnemigaAzulClaroBorgModel;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.renderer.GeoEntityRenderer;

public class NaveEnemigaAzulClaroBorgRenderer extends GeoEntityRenderer<NaveEnemigaAzulClaroBorgEntity, net.minecraft.client.renderer.entity.state.LivingEntityRenderState> {
   public NaveEnemigaAzulClaroBorgRenderer(EntityRendererProvider.Context renderManager) {
      super(renderManager, new NaveEnemigaAzulClaroBorgModel());
      this.shadowRadius = 0.5F;
   }

   public RenderType getRenderType(net.minecraft.client.renderer.entity.state.LivingEntityRenderState estado$, Identifier texture) {
      return net.minecraft.client.renderer.rendertype.RenderTypes.entityTranslucent(texture);
   }

   

   protected float getDeathMaxRotation(NaveEnemigaAzulClaroBorgEntity entityLivingBaseIn) {
      return 0.0F;
   }
}
