package net.owleafstudio.modminds.entity.layer;

import net.minecraft.resources.Identifier;
import net.owleafstudio.modminds.entity.LimboTitanEntity;
import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.layer.builtin.AutoGlowingGeoLayer;

/** Capa brillante (antes RenderType.eyes); en GeckoLib 5 se usa la capa de brillo propia. */
public class LimboTitanLayer extends AutoGlowingGeoLayer<LimboTitanEntity, Void, net.minecraft.client.renderer.entity.state.LivingEntityRenderState> {
   private static final Identifier LAYER = Identifier.fromNamespaceAndPath("modminds", "textures/entities/limbotitanbrillante.png");

   public LimboTitanLayer(GeoRenderer<LimboTitanEntity, Void, net.minecraft.client.renderer.entity.state.LivingEntityRenderState> entityRenderer) {
      super(entityRenderer);
   }

   @Override
   protected Identifier getTextureResource(net.minecraft.client.renderer.entity.state.LivingEntityRenderState estado) {
      return LAYER;
   }
}
