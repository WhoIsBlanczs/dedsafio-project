package com.ghostsafio.mitomod.client.compat;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;

/**
 * Graba los vértices que dibuja un renderer de 1.21.1 (dibujo inmediato) y al terminar los envía al
 * SubmitNodeCollector de 26.3 como geometría propia, uno por cada RenderType usado. Los vértices ya
 * vienen transformados por la PoseStack del renderer, así que se reenvían sin volver a transformar.
 */
public final class Grabadora implements MultiBufferSource {
   private final Map<RenderType, Pista> pistas = new LinkedHashMap<>();
   private final SubmitNodeCollector colector;

   public Grabadora(SubmitNodeCollector colector) {
      this.colector = colector;
   }

   /** Dibuja un ítem como lo hacía ItemRenderer.renderStatic en 1.21.1. */
   public static void item(MultiBufferSource buf, net.minecraft.world.item.ItemStack pila, net.minecraft.world.item.ItemDisplayContext modo, PoseStack ps, int luz, int overlay, net.minecraft.world.level.Level nivel) {
      if (buf instanceof Grabadora g && !pila.isEmpty()) {
         net.minecraft.client.renderer.item.ItemStackRenderState estado = new net.minecraft.client.renderer.item.ItemStackRenderState();
         net.minecraft.client.Minecraft.getInstance().getItemModelResolver().updateForTopItem(estado, pila, modo, nivel, null, 0);
         estado.submit(ps, g.colector, luz, overlay, 0);
      }
   }

   /** Luz combinada (bloque y cielo) en una posición, como LevelRenderer.getLightColor de 1.21.1. */
   public static int luz(net.minecraft.world.level.Level nivel, net.minecraft.core.BlockPos pos) {
      return net.minecraft.util.LightCoordsUtil.pack(
         nivel.getBrightness(net.minecraft.world.level.LightLayer.BLOCK, pos), nivel.getBrightness(net.minecraft.world.level.LightLayer.SKY, pos)
      );
   }

   @Override
   public VertexConsumer getBuffer(RenderType tipo) {
      return this.pistas.computeIfAbsent(tipo, t -> new Pista());
   }

   public void enviar() {
      SubmitNodeCollector colector = this.colector;
      PoseStack identidad = new PoseStack();
      for (Map.Entry<RenderType, Pista> e : this.pistas.entrySet()) {
         Pista p = e.getValue();
         if (!p.vertices.isEmpty()) {
            List<float[]> copia = p.vertices;
            colector.submitCustomGeometry(identidad, e.getKey(), (pose, vc) -> {
               for (float[] v : copia) {
                  vc.addVertex(v[0], v[1], v[2])
                     .setColor((int)v[3], (int)v[4], (int)v[5], (int)v[6])
                     .setUv(v[7], v[8])
                     .setUv1((int)v[9], (int)v[10])
                     .setUv2((int)v[11], (int)v[12])
                     .setNormal(v[13], v[14], v[15]);
               }
            });
         }
      }
      this.pistas.clear();
   }

   private static final class Pista implements VertexConsumer {
      final List<float[]> vertices = new ArrayList<>();
      private float[] actual;

      @Override
      public VertexConsumer addVertex(float x, float y, float z) {
         this.actual = new float[]{x, y, z, 255, 255, 255, 255, 0, 0, 0, 10, 240, 240, 0, 1, 0};
         this.vertices.add(this.actual);
         return this;
      }

      @Override
      public VertexConsumer setColor(int r, int g, int b, int a) {
         this.actual[3] = r;
         this.actual[4] = g;
         this.actual[5] = b;
         this.actual[6] = a;
         return this;
      }

      @Override
      public VertexConsumer setColor(int argb) {
         return this.setColor(argb >> 16 & 0xFF, argb >> 8 & 0xFF, argb & 0xFF, argb >>> 24);
      }

      @Override
      public VertexConsumer setUv(float u, float v) {
         this.actual[7] = u;
         this.actual[8] = v;
         return this;
      }

      @Override
      public VertexConsumer setUv1(int u, int v) {
         this.actual[9] = u;
         this.actual[10] = v;
         return this;
      }

      @Override
      public VertexConsumer setUv2(int u, int v) {
         this.actual[11] = u;
         this.actual[12] = v;
         return this;
      }

      @Override
      public VertexConsumer setUv3(float u, float v) {
         return this;
      }

      @Override
      public VertexConsumer setNormal(float x, float y, float z) {
         this.actual[13] = x;
         this.actual[14] = y;
         this.actual[15] = z;
         return this;
      }

      @Override
      public VertexConsumer setLineWidth(float ancho) {
         return this;
      }
   }
}
