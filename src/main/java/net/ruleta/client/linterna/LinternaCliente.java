package net.ruleta.client.linterna;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.ruleta.Ajustes;
import net.ruleta.client.compat.EventPriority;
import net.ruleta.client.compat.RenderGuiEvent;
import net.ruleta.client.compat.RenderLevelStageEvent;
import net.ruleta.client.compat.SubscribeEvent;
import net.ruleta.client.compat.TickEvent;
import net.ruleta.client.eclipse.EclipseCliente;
import net.ruleta.objetos.Linterna;
import net.ruleta.objetos.Objetos;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public final class LinternaCliente {
   private static boolean esperarSoltar;
   private static long luzEn;
   private static boolean luzLista;
   private static float luzCache;
   private static long otrosEn;
   private static boolean otrosListo;
   private static float otrosCache;
   private static Matrix4f proyeccion;
   private static Matrix4f vista;
   private static Vec3 camara;
   private static float parcial;
   private static Object shaderHaz;
   private static Object shaderNegro;
   private static TextureTarget profundidad;
   private static final String[] POS = new String[]{"Luz0Pos", "Luz1Pos", "Luz2Pos", "Luz3Pos"};
   private static final String[] DIR = new String[]{"Luz0Dir", "Luz1Dir", "Luz2Dir", "Luz3Dir"};

   private LinternaCliente() {
   }

   @SubscribeEvent
   public static void alTick(TickEvent.ClientTickEvent e) {
      if (e.phase == TickEvent.Phase.END) {
         Minecraft mc = Minecraft.getInstance();
         LocalPlayer p = mc.player;
         if (p != null && mc.options.keyUse.isDown()) {
            if (p.isUsingItem() && p.getUseItem().is(Objetos.LINTERNA.get()) && p.getTicksUsingItem() >= Linterna.ticksPrender() - 1) {
               esperarSoltar = true;
            }
         } else {
            esperarSoltar = false;
         }
      }
   }

   public static boolean ignorarUso() {
      return esperarSoltar && !enMano().isEmpty();
   }

   public static ItemStack enMano() {
      LocalPlayer p = Minecraft.getInstance().player;
      if (p == null) {
         return ItemStack.EMPTY;
      } else if (p.getMainHandItem().is(Objetos.LINTERNA.get())) {
         return p.getMainHandItem();
      } else {
         return p.getOffhandItem().is(Objetos.LINTERNA.get()) ? p.getOffhandItem() : ItemStack.EMPTY;
      }
   }

   public static float luz() {
      long ahora = System.nanoTime();
      if (luzLista && ahora - luzEn < 2000000L) {
         return luzCache;
      }

      ItemStack s = enMano();
      luzCache = s.isEmpty() ? 0.0F : Linterna.intensidad(s);
      luzEn = ahora;
      luzLista = true;
      return luzCache;
   }

   @SubscribeEvent
   public static void alDibujarHud(RenderGuiEvent.Post e) { }

   private static void disco(Object b, Matrix4f m, float cx, float cy, float r, float k, int argb) {
      int seg = 48;
      int hasta = Math.round(seg * k);

      for (int i = 0; i < hasta; i++) {
         float a0 = (float)((-Math.PI / 2) + i * Math.PI * 2.0 / seg);
         float a1 = (float)((-Math.PI / 2) + (i + 1) * Math.PI * 2.0 / seg);
         v(b, m, cx, cy, argb);
         v(b, m, cx + Mth.cos(a0) * r, cy + Mth.sin(a0) * r, argb);
         v(b, m, cx + Mth.cos(a1) * r, cy + Mth.sin(a1) * r, argb);
      }
   }

   private static void v(Object b, Matrix4f m, float x, float y, int argb) {
      b.addVertex(m, x, y, 0.0F).setColor(argb >> 16 & 0xFF, argb >> 8 & 0xFF, argb & 0xFF, argb >>> 24);
   }

   public static float luzOtros() { return 0; }

   public static float luzTotal() {
      return Math.max(luz(), luzOtros());
   }

   @SubscribeEvent(priority = EventPriority.HIGHEST)
   public static void alEmpezarMundo(RenderLevelStageEvent e) { }

   private static float[] centroPropio() {
      Minecraft mc = Minecraft.getInstance();
      if (mc.player != null && proyeccion != null) {
         float yaw = mc.player.getYRot() * (float) (Math.PI / 180.0);
         Vec3 ojos = mc.player.getEyePosition();
         Vec3 p = ojos.add(-Mth.sin(yaw) * 7.0, 0.0, Mth.cos(yaw) * 7.0);
         Vector4f v = new Vector4f((float)(p.x - camara.x), (float)(p.y - camara.y), (float)(p.z - camara.z), 1.0F);
         vista.transform(v);
         proyeccion.transform(v);
         return v.w <= 0.1F ? null : new float[]{v.x / v.w, v.y / v.w};
      } else {
         return new float[]{0.0F, 0.0F};
      }
   }

   public static List<float[]> focos() {
      List<float[]> r = new ArrayList<>();
      Minecraft mc = Minecraft.getInstance();
      if (mc.level != null && proyeccion != null) {
         for (Player otro : mc.level.players()) {
            if (otro != mc.player && !otro.isSpectator()) {
               ItemStack s = otro.getMainHandItem().is(Objetos.LINTERNA.get())
                  ? otro.getMainHandItem()
                  : (otro.getOffhandItem().is(Objetos.LINTERNA.get()) ? otro.getOffhandItem() : ItemStack.EMPTY);
               float k = s.isEmpty() ? 0.0F : Linterna.intensidad(s);
               if (!(k <= 0.0F)) {
                  Vec3 ojos = otro.getEyePosition();
                  float yawOtro = otro.getYRot() * (float) (Math.PI / 180.0);
                  Vec3 fin = ojos.add(-Mth.sin(yawOtro) * 9.0, 0.0, Mth.cos(yawOtro) * 9.0);
                  HitResult hit = mc.level.clip(new ClipContext(ojos, fin, Block.VISUAL, Fluid.NONE, otro));
                  Vec3 p = hit.getLocation();
                  double largo = p.distanceTo(ojos);
                  Vector4f v = new Vector4f((float)(p.x - camara.x), (float)(p.y - camara.y), (float)(p.z - camara.z), 1.0F);
                  vista.transform(v);
                  proyeccion.transform(v);
                  if (!(v.w <= 0.1F)) {
                     float radioMundo = (float)(1.2 + largo * 0.16) * (0.6F + 0.4F * k);
                     float radio = radioMundo * proyeccion.m11() / v.w;
                     r.add(new float[]{v.x / v.w, v.y / v.w, radio, k});
                  }
               }
            }
         }

         return r;
      } else {
         return r;
      }
   }

   public static void oscurecer(float negro, float luz) {
      haz(true, 1.0F - Mth.clamp(negro, 0.0F, 0.995F));
   }

   public static void shaderHaz(Object s) {
      shaderHaz = s;
   }

   public static void shaderNegro(Object s) {
      shaderNegro = s;
   }

   private static float fuerza(Player j) {
      ItemStack s = j.getMainHandItem().is(Objetos.LINTERNA.get())
         ? j.getMainHandItem()
         : (j.getOffhandItem().is(Objetos.LINTERNA.get()) ? j.getOffhandItem() : ItemStack.EMPTY);
      return s.isEmpty() ? 0.0F : Linterna.intensidad(s);
   }

   private static List<float[]> linternas() {
      List<float[]> r = new ArrayList<>();
      Minecraft mc = Minecraft.getInstance();
      if (mc.level != null && camara != null) {
         List<Player> otros = new ArrayList<>();

         for (Player j : mc.level.players()) {
            if (!j.isSpectator() && fuerza(j) > 0.0F && j.distanceToSqr(camara) < 4096.0) {
               otros.add(j);
            }
         }

         otros.sort(Comparator.comparingDouble(jx -> jx == mc.player ? -1.0 : jx.distanceToSqr(camara)));

         for (Player j : otros) {
            if (r.size() >= 4) {
               break;
            }

            Vec3 ojos = j.getEyePosition(parcial);
            Vec3 mira = j.getViewVector(parcial);
            Vec3 lado = mira.cross(new Vec3(0.0, 1.0, 0.0));
            if (lado.lengthSqr() > 1.0E-4) {
               ojos = ojos.add(lado.normalize().scale(0.25));
            }

            ojos = ojos.add(0.0, -0.2, 0.0);
            r.add(
               new float[]{
                  (float)(ojos.x - camara.x), (float)(ojos.y - camara.y), (float)(ojos.z - camara.z), fuerza(j), (float)mira.x, (float)mira.y, (float)mira.z
               }
            );
         }

         return r;
      } else {
         return r;
      }
   }

   private static void haz(boolean negro, float fuera) { }

   private static void g(Object b, float x, float y, float k) {
      b.addVertex(x, y, 0.0F).setColor(k, k, k, 1.0F);
   }

   @SubscribeEvent
   public static void alDibujarMundo(RenderLevelStageEvent e) {
      if (e.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL && !EclipseCliente.activo()) {
         haz(false, 0.0F);
      }
   }

   private static void mancha(Object b, float cx, float cy, float r, float a, float aspecto) { }
}
