package net.owleafstudio.modminds.entity.layer;

import net.minecraft.resources.Identifier;
import net.owleafstudio.modminds.entity.AlmaPerdidaEntity;
import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.layer.builtin.AutoGlowingGeoLayer;

/** Capa brillante (antes RenderType.eyes); en GeckoLib 5 se usa la capa de brillo propia. */
public class AlmaPerdidaLayer extends AutoGlowingGeoLayer<AlmaPerdidaEntity, Void, net.minecraft.client.renderer.entity.state.LivingEntityRenderState> {
   private static final Identifier LAYER = Identifier.fromNamespaceAndPath("modminds", "textures/entities/almabrillante.png");

   public AlmaPerdidaLayer(GeoRenderer<AlmaPerdidaEntity, Void, net.minecraft.client.renderer.entity.state.LivingEntityRenderState> entityRenderer) {
      super(entityRenderer);
   }

   @Override
   protected Identifier getTextureResource(net.minecraft.client.renderer.entity.state.LivingEntityRenderState estado) {
      return LAYER;
   }
}
