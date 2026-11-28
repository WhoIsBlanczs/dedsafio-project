package net.ruleta.estatua.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import java.util.function.Consumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.resources.Identifier;
import net.ruleta.client.compat.Grabadora;
import org.joml.Vector3f;
import org.joml.Vector3fc;

/** La estatua de la nutria como ítem: en 26.3 es un "special model" (items/estatua_nutria.json). */
public class EstatuaNutriaItemRenderer implements NoDataSpecialModelRenderer {
   public static final Identifier ID = Identifier.fromNamespaceAndPath("ruletafanproject", "estatua_nutria");

   public static void registrar() {
      SpecialModelRenderers.ID_MAPPER.put(ID, Unbaked.CODEC);
   }

   @Override
   public void submit(PoseStack ps, SubmitNodeCollector colector, int luz, int overlay, boolean brillo, int contorno) {
      Grabadora g = new Grabadora(colector);
      ps.pushPose();
      ps.translate(0.5, 0.0, 0.5);
      EstatuaNutriaRenderer.dibujar(ps, g, luz, overlay);
      ps.popPose();
      g.enviar();
   }

   @Override
   public void getExtents(Consumer<Vector3fc> salida) {
      salida.accept(new Vector3f(0.0F, 0.0F, 0.0F));
      salida.accept(new Vector3f(1.0F, 2.0F, 1.0F));
   }

   public record Unbaked() implements NoDataSpecialModelRenderer.Unbaked {
      public static final MapCodec<Unbaked> CODEC = MapCodec.unit(new Unbaked());

      @Override
      public MapCodec<Unbaked> type() {
         return CODEC;
      }

      @Override
      public SpecialModelRenderer<Void> bake(SpecialModelRenderer.BakingContext contexto) {
         return new EstatuaNutriaItemRenderer();
      }
   }
}
