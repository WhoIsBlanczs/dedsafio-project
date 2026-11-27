package net.ruleta.nuclear.client;

import net.minecraft.client.renderer.entity.CreeperRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.state.CreeperRenderState;
import net.minecraft.resources.Identifier;

/** Renderer del Nuclear Creeper: modelo de creeper vanilla con su textura propia. */
public class NuclearCreeperRenderer extends CreeperRenderer {
   private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("nuclearcreeper", "textures/entity/nuclear_creeper.png");

   public NuclearCreeperRenderer(Context context) {
      super(context);
   }

   @Override
   public Identifier getTextureLocation(CreeperRenderState state) {
      return TEXTURE;
   }
}
