package net.ruleta.creeperrojo.client;

import net.minecraft.client.renderer.entity.CreeperRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.state.CreeperRenderState;
import net.minecraft.resources.Identifier;

/** Creeper rojo / de cesped: modelo vanilla con textura propia. */
public class CreeperRojoRenderer extends CreeperRenderer {
   public static final Identifier TEXTURA_ROJO = Identifier.fromNamespaceAndPath("ruleta", "textures/entity/creeper_rojo.png");
   public static final float ESCALA_ROJO = 1.0F;
   private final Identifier textura;

   public CreeperRojoRenderer(Context contexto) {
      this(contexto, TEXTURA_ROJO, 1.0F);
   }

   public CreeperRojoRenderer(Context contexto, Identifier textura, float escala) {
      super(contexto);
      this.textura = textura;
   }

   @Override
   public Identifier getTextureLocation(CreeperRenderState estado) {
      return this.textura;
   }
}
