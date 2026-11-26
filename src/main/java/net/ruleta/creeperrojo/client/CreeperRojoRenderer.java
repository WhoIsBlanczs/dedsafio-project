package net.ruleta.creeperrojo.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Creeper;

public class CreeperRojoRenderer<T extends Creeper> extends MobRenderer<T, CreeperRojoModel<T>> {
   public static final Identifier TEXTURA_ROJO = Identifier.fromNamespaceAndPath("ruleta", "textures/entity/creeper_rojo.png");
   public static final float ESCALA_ROJO = 1.0F;
   private final Identifier textura;
   private final float escala;

   public CreeperRojoRenderer(Context contexto) {
      this(contexto, TEXTURA_ROJO, 1.0F);
   }

   public CreeperRojoRenderer(Context contexto, Identifier textura, float escala) {
      super(contexto, new CreeperRojoModel(contexto.bakeLayer(CreeperRojoModel.CAPA)), 0.6F * escala);
      this.textura = textura;
      this.escala = escala;
   }

   protected void scale(T entidad, PoseStack pila, float parcial) {
      float hinchado = entidad.getSwelling(parcial);
      float vibracion = 1.0F + Mth.sin(hinchado * 100.0F) * hinchado * 0.01F;
      hinchado = Mth.clamp(hinchado, 0.0F, 1.0F);
      hinchado *= hinchado;
      hinchado *= hinchado;
      float xz = (1.0F + hinchado * 0.4F) * vibracion;
      float y = (1.0F + hinchado * 0.1F) / vibracion;
      pila.scale(xz * this.escala, y * this.escala, xz * this.escala);
   }

   protected float getWhiteOverlayProgress(T entidad, float parcial) {
      float hinchado = entidad.getSwelling(parcial);
      return (int)(hinchado * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(hinchado, 0.5F, 1.0F);
   }

   public Identifier getTextureLocation(T entidad) {
      return this.textura;
   }
}
