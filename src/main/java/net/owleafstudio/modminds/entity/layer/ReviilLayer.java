package net.owleafstudio.modminds.entity.layer;

import net.minecraft.resources.Identifier;
import net.owleafstudio.modminds.entity.ReviilEntity;
import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.layer.builtin.AutoGlowingGeoLayer;

/** Capa brillante (antes RenderType.eyes); en GeckoLib 5 se usa la capa de brillo propia. */
public class ReviilLayer extends AutoGlowingGeoLayer<ReviilEntity, Void, net.minecraft.client.renderer.entity.state.LivingEntityRenderState> {
   private static final Identifier LAYER = Identifier.fromNamespaceAndPath("modminds", "textures/entities/reviil_bossglow.png");

   public ReviilLayer(GeoRenderer<ReviilEntity, Void, net.minecraft.client.renderer.entity.state.LivingEntityRenderState> entityRenderer) {
      super(entityRenderer);
   }

   @Override
   protected Identifier getTextureResource(net.minecraft.client.renderer.entity.state.LivingEntityRenderState estado) {
      return LAYER;
   }
}
