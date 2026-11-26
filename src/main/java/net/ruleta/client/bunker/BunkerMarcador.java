package net.ruleta.client.bunker;

import com.mojang.blaze3d.vertex.Object;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Object;
import com.mojang.blaze3d.vertex.VertexSorting;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.ruleta.bunker.BunkerPayload;
import net.ruleta.bunker.Bunkers;
import net.ruleta.client.compat.ClientPlayerNetworkEvent;
import net.ruleta.client.compat.RenderLevelStageEvent;
import net.ruleta.client.compat.SubscribeEvent;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

public final class BunkerMarcador {
   private static final float PX = 1.0F;
   private static final float TEXTO = 0.75F;
   private static final double DIST_DIBUJO = 48.0;
   private static List<Bunkers.Bunker> bunkers = new ArrayList<>();
   private static List<Bunkers.Bunker> todos = new ArrayList<>();

   private BunkerMarcador() {
   }

   public static void recibir(BunkerPayload m) {
      if (m.todos()) {
         todos = new ArrayList<>(m.lista());
      } else {
         bunkers = new ArrayList<>(m.lista());
      }
   }

   public static List<Bunkers.Bunker> todos() {
      return todos;
   }

   public static List<Bunkers.Bunker> deAqui() {
      Minecraft mc = Minecraft.getInstance();
      List<Bunkers.Bunker> r = new ArrayList<>();
      if (mc.level == null) {
         return r;
      }

      String dim = mc.level.dimension().location().toString();

      for (Bunkers.Bunker b : bunkers) {
         if (b.dimension().equals(dim)) {
            r.add(b);
         }
      }

      return r;
   }

   @SubscribeEvent
   public static void alSalir(ClientPlayerNetworkEvent.LoggingOut e) {
      bunkers = new ArrayList<>();
      todos = new ArrayList<>();
   }

   public static String etiqueta(Bunkers.Bunker b) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.player == null) {
         return b.nombre();
      }

      double d = Math.sqrt(mc.player.distanceToSqr(b.mx(), b.my(), b.mz()));
      return b.nombre() + " (" + Math.round(d) + "m)";
   }

   public static void alDibujarMundo(RenderLevelStageEvent e) { }

   private static void dibujar(Matrix4f m, String texto) { }

   public static void dibujarPin(GuiGraphicsExtractor g, float x, float y, Bunkers.Bunker b) { }

   private static void geometria(Object bb, Matrix4f m) {
      pin(bb, m, 8.2F, -1728053248);
      pin(bb, m, 7.0F, -1);
      icono(bb, m);
   }

   private static void pin(Object b, Matrix4f m, float r, int argb) {
      float cy = -r * 2.0F;
      int seg = 24;

      for (int i = 0; i < seg; i++) {
         float a0 = (float)(i * Math.PI * 2.0 / seg);
         float a1 = (float)((i + 1) * Math.PI * 2.0 / seg);
         v(b, m, 0.0F, cy, argb);
         v(b, m, Mth.cos(a0) * r, cy + Mth.sin(a0) * r, argb);
         v(b, m, Mth.cos(a1) * r, cy + Mth.sin(a1) * r, argb);
      }

      float k = r * 0.72F;
      v(b, m, -k, cy + r * 0.69F, argb);
      v(b, m, k, cy + r * 0.69F, argb);
      v(b, m, 0.0F, 0.0F, argb);
   }

   private static void icono(Object b, Matrix4f m) {
      int gris = -13749704;
      float cy = -14.0F;
      v(b, m, -4.5F, cy - 1.0F, gris);
      v(b, m, 4.5F, cy - 1.0F, gris);
      v(b, m, 0.0F, cy - 4.5F, gris);
      rect(b, m, -3.5F, cy - 1.0F, 3.5F, cy + 3.5F, gris);
      rect(b, m, -1.2F, cy + 0.6F, 1.2F, cy + 3.5F, -1);
   }

   private static void rect(Object b, Matrix4f m, float x0, float y0, float x1, float y1, int c) {
      v(b, m, x0, y0, c);
      v(b, m, x0, y1, c);
      v(b, m, x1, y1, c);
      v(b, m, x0, y0, c);
      v(b, m, x1, y1, c);
      v(b, m, x1, y0, c);
   }

   private static void v(Object b, Matrix4f m, float x, float y, int argb) {
      b.addVertex(m, x, y, 0.0F).setColor(argb >> 16 & 0xFF, argb >> 8 & 0xFF, argb & 0xFF, argb >>> 24);
   }
}
