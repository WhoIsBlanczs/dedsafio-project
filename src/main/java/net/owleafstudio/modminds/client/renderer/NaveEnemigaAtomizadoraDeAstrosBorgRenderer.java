package net.owleafstudio.modminds.client.renderer;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.owleafstudio.modminds.entity.NaveEnemigaAtomizadoraDeAstrosBorgEntity;
import net.owleafstudio.modminds.entity.model.NaveEnemigaAtomizadoraDeAstrosBorgModel;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.renderer.GeoEntityRenderer;

public class NaveEnemigaAtomizadoraDeAstrosBorgRenderer extends GeoEntityRenderer<NaveEnemigaAtomizadoraDeAstrosBorgEntity, net.minecraft.client.renderer.entity.state.LivingEntityRenderState> {
   public NaveEnemigaAtomizadoraDeAstrosBorgRenderer(EntityRendererProvider.Context renderManager) {
      super(renderManager, new NaveEnemigaAtomizadoraDeAstrosBorgModel());
      this.shadowRadius = 0.5F;
   }

   public RenderType getRenderType(net.minecraft.client.renderer.entity.state.LivingEntityRenderState estado$, Identifier texture) {
      return net.minecraft.client.renderer.rendertype.RenderTypes.entityTranslucent(texture);
   }

   

   protected float getDeathMaxRotation(NaveEnemigaAtomizadoraDeAstrosBorgEntity entityLivingBaseIn) {
      return 0.0F;
   }
}
