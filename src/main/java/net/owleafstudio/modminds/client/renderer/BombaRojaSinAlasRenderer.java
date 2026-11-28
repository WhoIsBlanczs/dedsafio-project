package net.owleafstudio.modminds.client.renderer;

import net.minecraft.resources.Identifier;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.owleafstudio.modminds.client.model.Modelunknown;
import net.owleafstudio.modminds.entity.BombaRojaSinAlasEntity;

public class BombaRojaSinAlasRenderer extends MobRenderer<BombaRojaSinAlasEntity, net.minecraft.client.renderer.entity.state.LivingEntityRenderState, Modelunknown<net.minecraft.client.renderer.entity.state.LivingEntityRenderState>> {
   public BombaRojaSinAlasRenderer(EntityRendererProvider.Context context) {
      super(context, new Modelunknown<>(context.bakeLayer(Modelunknown.LAYER_LOCATION)), 0.0F);
   }

   @Override
   public net.minecraft.client.renderer.entity.state.LivingEntityRenderState createRenderState() {
      return new net.minecraft.client.renderer.entity.state.LivingEntityRenderState();
   }

   public Identifier getTextureLocation(net.minecraft.client.renderer.entity.state.LivingEntityRenderState estado$) {
      return Identifier.parse("modminds:textures/entities/bombaroja.png");
   }
}
