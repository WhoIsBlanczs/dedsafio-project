package net.owleafstudio.modminds.client.renderer;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.world.phys.Vec3;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import com.mojang.renderpearl.api.vertex.VertexFormat;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.owleafstudio.modminds.entity.ZonaPeligroEntity;
import org.joml.Matrix4f;

public class ZonaPeligroRenderer extends EntityRenderer<ZonaPeligroEntity, net.owleafstudio.modminds.compat.Compat.EstadoEntidad> {
   private static final RenderType PINTURA = net.minecraft.client.renderer.rendertype.RenderTypes.debugQuads();
   private static final int SEGMENTOS = 40;
   private static final int JAG_PASOS = 14;
   private static final int RAYO_PASOS = 10;

   public ZonaPeligroRenderer(EntityRendererProvider.Context renderManager) {
      super(renderManager);
   }

   public Identifier getTextureLocation(ZonaPeligroEntity entity) {
      return TextureAtlas.LOCATION_BLOCKS;
   }

   public boolean shouldRender(ZonaPeligroEntity entity, Frustum camera, double camX, double camY, double camZ, float partialTick$) {
      return true;
   }

   public net.owleafstudio.modminds.compat.Compat.EstadoEntidad createRenderState() {
      return new net.owleafstudio.modminds.compat.Compat.EstadoEntidad();
   }

   public void extractRenderState(ZonaPeligroEntity entidad$, net.owleafstudio.modminds.compat.Compat.EstadoEntidad estado$, float pt$) {
      super.extractRenderState(entidad$, estado$, pt$);
      estado$.entidad = entidad$;
      estado$.pt = pt$;
   }

   public void submit(net.owleafstudio.modminds.compat.Compat.EstadoEntidad estado$, PoseStack poseStack, net.minecraft.client.renderer.SubmitNodeCollector colector$, net.minecraft.client.renderer.state.level.CameraRenderState camara$) {
      ZonaPeligroEntity zone = (ZonaPeligroEntity)estado$.entidad;
      float partialTick = estado$.pt;
      float entityYaw = zone.getYRot();
      int packedLight = estado$.lightCoords;
      int life = zone.getLife();
      if (life > 0) {
         float age = zone.tickCount + partialTick;
         double r = zone.getRadius();
         double wave = zone.getWave();
         double progress = Mth.clamp(age / life, 0.0, 1.0);
         float breathe = (float)Math.min(1.0, age / 4.0) * (float)Math.min(1.0, (life - age) / 6.0);
         int rgb = zone.getColor();
         float red = (rgb >> 16 & 0xFF) / 255.0F;
         float green = (rgb >> 8 & 0xFF) / 255.0F;
         float blue = (rgb & 0xFF) / 255.0F;
         poseStack.pushPose();
         Vec3 here = zone.paintCentre(partialTick);
         Vec3 body = zone.getPosition(partialTick);
         poseStack.translate(here.x - body.x, here.y - body.y + 0.06, here.z - body.z);
         colector$.submitCustomGeometry(poseStack, PINTURA, (pose$, buffer) -> {
            Matrix4f pose = pose$.pose();
            switch (zone.getForma()) {
               case 1:
                  crack(pose, buffer, zone, red, green, blue, breathe, progress, age);
                  break;
               case 2:
                  rayCircle(pose, buffer, zone, red, green, blue, breathe, progress, age, partialTick);
                  break;
               case 3:
                  pozo(pose, buffer, zone, red, green, blue, breathe, progress, age);
                  break;
               case 4:
                  columnas(pose, buffer, zone, red, green, blue, breathe, age);
                  break;
               case 5:
                  campo(pose, buffer, zone, red, green, blue, breathe, progress);
                  break;
               case 6:
                  surco(pose, buffer, zone, red, green, blue, breathe, age);
                  break;
               case 7:
                  avalancha(pose, buffer, zone, red, green, blue, breathe, progress);
                  break;
               default:
                  if (zone.getBeamLength() > 0.0) {
                     beam(pose, buffer, zone, red, green, blue, breathe, progress);
                  } else if (wave > 0.0) {
                     double t = Mth.clamp(age / wave, 0.0, 1.0);
                     double head = r * t;
                     double vida = (1.0 - progress) * breathe;
                     band(pose, buffer, Math.max(0.0, head * 0.62), head, red, green, blue, (float)vida * 0.85F);
                     band(pose, buffer, Math.max(0.0, head * 0.4), Math.max(0.02, head * 0.62), red, green, blue, (float)vida * 0.4F);
                     if (head > 0.4) {
                        rayas(pose, buffer, 0.0, Math.max(0.02, head * 0.4), 14, age * 0.11 + zone.getSeed(), red, green, blue, (float)vida * 0.2F);
                        band(pose, buffer, Math.max(0.0, head - 0.3), head, Math.min(1.0F, red * 1.2F), Math.min(1.0F, green * 1.15F + 0.1F), blue, (float)vida);
                        arcos(pose, buffer, head * 0.9, Math.min(r, head * 1.08), 9, 0.58, age * 0.24 + zone.getSeed(), red, green, blue, (float)vida * 0.75F);
                        band(pose, buffer, head, Math.min(r * 1.2, head + Math.max(0.6, head * 0.1)), red, green, blue, (float)vida * 0.22F);
                        empalizada(pose, buffer, head, Math.max(0.3, 2.4 * vida), 22, 0.5, age * 0.24 + zone.getSeed(), red, green, blue, (float)vida * 0.42F);
                        escombros(pose, buffer, head, red, green, blue, (float)vida);
                     }
                  } else {
                     aviso(pose, buffer, zone, red, green, blue, breathe, progress, age);
                  }
            }
         });

         poseStack.popPose();
      }
   }

   private static void aviso(
      Matrix4f pose, VertexConsumer buffer, ZonaPeligroEntity zone, float red, float green, float blue, float breathe, double progress, float age
   ) {
      float pulse = 0.85F + 0.15F * Mth.sin(age * 0.35F);
      double r = zone.getRadius();
      double fondo = zone.getDepth();
      double inner = fondo > 0.0 ? Math.max(0.0, r - fondo) : 0.0;
      double encendido = Mth.clamp((progress - 0.62) / 0.38, 0.0, 1.0);
      float hr = Mth.clamp(red + (float)encendido * 0.3F, 0.0F, 1.0F);
      float hg = Mth.clamp(green + (float)encendido * 0.22F, 0.0F, 1.0F);
      float hb = Mth.clamp(blue * (1.0F - (float)encendido * 0.4F), 0.0F, 1.0F);
      double giro = age * 0.085 + zone.getSeed() * 0.6;
      band(pose, buffer, inner, r, hr, hg, hb, 0.3F * breathe);
      rayas(pose, buffer, inner, r, (int)Mth.clamp(r * 1.6, 10.0, 34.0), giro, red, green, blue, (0.09F + 0.06F * (float)encendido) * breathe);
      band(pose, buffer, r - Math.max(0.35, r * 0.1), r, red, green, blue, 0.75F * pulse * breathe);
      if (fondo > 0.0) {
         band(pose, buffer, inner, inner + Math.max(0.35, fondo * 0.12), red, green, blue, 0.75F * pulse * breathe);
      }

      band(pose, buffer, r, r + Math.max(0.5, r * 0.06), red, green, blue, 0.28F * pulse * breathe);
      band(pose, buffer, r + Math.max(0.5, r * 0.06), r + Math.max(1.1, r * 0.12), red, green, blue, 0.12F * pulse * breathe);
      dientes(pose, buffer, r * 0.84, r * 0.97, Mth.clamp((int)(r * 1.2), 10, 28), 0.42, -giro * 1.4, red, green, blue, 0.55F * pulse * breathe);
      arcos(pose, buffer, r * 0.6, r * 0.66, 6, 0.45, giro * 0.7, red, green, blue, 0.5F * breathe);
      if (inner <= 0.01) {
         arcos(pose, buffer, r * 0.3, r * 0.35, 3, 0.55, -giro, red, green, blue, 0.42F * breathe);
         mirilla(pose, buffer, r, giro, red, green, blue, 0.8F * breathe);
      }

      double head = r - (r - inner) * progress;
      double edge = Math.max(0.1, (r - inner) * 0.16 + 0.3);
      band(pose, buffer, Math.max(inner, head - edge), head, red, green, blue, 0.9F * breathe);
      band(pose, buffer, Math.max(inner, head - 0.18), head, hr, hg, hb, 1.0F * breathe);
      dientes(pose, buffer, Math.max(0.0, head - edge), head, 12, 0.5, giro * 2.0, red, green, blue, 0.75F * breathe);
      double altura = Math.min(2.3, 0.45 + r * 0.1);
      empalizada(
         pose,
         buffer,
         r,
         altura * (0.35 + 0.65 * encendido),
         Mth.clamp((int)(r * 1.5), 14, 32),
         0.58,
         giro * 0.35,
         red,
         green,
         blue,
         (0.26F + 0.3F * (float)encendido) * breathe
      );
      if (fondo > 0.0) {
         empalizada(
            pose,
            buffer,
            inner,
            altura * 0.5 * (0.35 + 0.65 * encendido),
            Mth.clamp((int)(inner * 1.2), 10, 26),
            0.5,
            -giro * 0.5,
            red,
            green,
            blue,
            0.2F * breathe
         );
      }

      if (head > 0.3 && head < r - 0.3) {
         empalizada(
            pose,
            buffer,
            head,
            0.55 + 0.85 * (float)progress,
            Mth.clamp((int)(head * 1.4), 12, 30),
            0.5,
            giro,
            hr,
            hg,
            hb,
            (0.34F + 0.26F * (float)progress) * breathe
         );
      }
   }

   private static void rayas(Matrix4f pose, VertexConsumer buffer, double inner, double outer, int cantidad, double giro, float r, float g, float b, float a) {
      int total = Math.max(4, cantidad);

      for (int i = 0; i < total; i += 2) {
         double a0 = giro + (Math.PI * 2) * i / total;
         sector(pose, buffer, inner, outer, a0, a0 + (Math.PI * 2) / total, r, g, b, a);
      }
   }

   private static void dientes(
      Matrix4f pose, VertexConsumer buffer, double inner, double outer, int total, double frac, double giro, float r, float g, float b, float a
   ) {
      int count = Math.max(4, total);

      for (int i = 0; i < count; i++) {
         double a0 = giro + (Math.PI * 2) * i / count;
         double a1 = a0 + (Math.PI * 2) * frac / count;
         double paso = Math.max(0.017, (a1 - a0) / 4.0);

         for (int s = 0; s < 4; s++) {
            double s0 = a0 + paso * s;
            double s1 = a0 + paso * (s + 1);
            corner(pose, buffer, inner * Math.cos(s0), inner * Math.sin(s0), r, g, b, a);
            corner(pose, buffer, outer * Math.cos(s0), outer * Math.sin(s0), r, g, b, a);
            corner(pose, buffer, outer * Math.cos(s1), outer * Math.sin(s1), r, g, b, a);
            corner(pose, buffer, inner * Math.cos(s1), inner * Math.sin(s1), r, g, b, a);
         }
      }
   }

   private static void arcos(
      Matrix4f pose, VertexConsumer buffer, double inner, double outer, int total, double frac, double giro, float r, float g, float b, float a
   ) {
      int count = Math.max(2, total);

      for (int i = 0; i < count; i++) {
         double a0 = giro + (Math.PI * 2) * i / count;
         sector(pose, buffer, inner, outer, a0, a0 + (Math.PI * 2) * frac / count, r, g, b, a);
      }
   }

   private static void empalizada(
      Matrix4f pose, VertexConsumer buffer, double radio, double alto, int total, double frac, double giro, float r, float g, float b, float a
   ) {
      if (!(alto <= 0.03) && !(radio <= 0.05)) {
         int count = Math.max(6, Math.min(total, 40));
         double grosor = Math.max(0.1, radio * 0.035);

         for (int i = 0; i < count; i++) {
            double a0 = giro + (Math.PI * 2) * i / count;
            double a1 = a0 + (Math.PI * 2) * frac / count;
            double x0 = radio * Math.cos(a0);
            double z0 = radio * Math.sin(a0);
            double x1 = radio * Math.cos(a1);
            double z1 = radio * Math.sin(a1);
            double vira = i % 2 == 0 ? 1.0 : 0.7;
            muro(pose, buffer, x0, z0, x1, z1, 0.0, alto, (float)(r * vira), (float)(g * vira), (float)(b * vira), a);
            sector(
               pose,
               buffer,
               Math.max(0.0, radio - grosor),
               radio,
               a0,
               a1,
               Math.min(1.0F, r * 1.3F + 0.08F),
               Math.min(1.0F, g * 1.25F + 0.08F),
               Math.min(1.0F, b * 1.2F),
               Math.min(1.0F, a * 1.5F)
            );
         }
      }
   }

   private static void mirilla(Matrix4f pose, VertexConsumer buffer, double r, double giro, float red, float green, float blue, float a) {
      double largo = Math.max(0.45, Math.min(1.6, r * 0.16));

      for (int i = 0; i < 4; i++) {
         double ang = giro * 0.5 + i * Math.PI / 2.0;
         double x = Math.cos(ang);
         double z = Math.sin(ang);
         quad(pose, buffer, x * largo * 0.34, z * largo * 0.34, x * largo, z * largo, 0.1, red, green, blue, a);
      }

      band(pose, buffer, 0.0, Math.max(0.16, largo * 0.2), red, green, blue, Math.min(1.0F, a * 1.15F));
   }

   private static void sector(Matrix4f pose, VertexConsumer buffer, double inner, double outer, double a0, double a1, float r, float g, float b, float a) {
      int pasos = Math.max(1, (int)Math.ceil((a1 - a0) / (Math.PI * 2) * 40.0));

      for (int i = 0; i < pasos; i++) {
         double s0 = a0 + (a1 - a0) * i / pasos;
         double s1 = a0 + (a1 - a0) * (i + 1) / pasos;
         corner(pose, buffer, inner * Math.cos(s0), inner * Math.sin(s0), r, g, b, a);
         corner(pose, buffer, outer * Math.cos(s0), outer * Math.sin(s0), r, g, b, a);
         corner(pose, buffer, outer * Math.cos(s1), outer * Math.sin(s1), r, g, b, a);
         corner(pose, buffer, inner * Math.cos(s1), inner * Math.sin(s1), r, g, b, a);
      }
   }

   private static void beam(Matrix4f pose, VertexConsumer buffer, ZonaPeligroEntity zone, float red, float green, float blue, float breathe, double progress) {
      double angle = zone.getBeamAngle();
      float alongX = Mth.cos((float)angle);
      float alongZ = Mth.sin((float)angle);
      double length = zone.getBeamLength();
      double half = Math.max(0.6, zone.getDepth() * 0.5);
      double head = length * progress;
      double trail = Math.max(1.2, length * 0.12);
      strip(pose, buffer, alongX, alongZ, 0.0, length, -half, half, red, green, blue, 0.3F * breathe);
      strip(pose, buffer, alongX, alongZ, 0.0, length, half - 0.35, half, red, green, blue, 0.75F * breathe);
      strip(pose, buffer, alongX, alongZ, 0.0, length, -half, -half + 0.35, red, green, blue, 0.75F * breathe);
      strip(pose, buffer, alongX, alongZ, Math.max(0.0, head - trail), head, -half, half, red, green, blue, 0.85F * breathe);
      strip(pose, buffer, alongX, alongZ, length * 0.72, length, -half, half, red, green, blue, (float)(progress - 0.7) / 0.3F * 0.55F * breathe);
      strip(
         pose,
         buffer,
         alongX,
         alongZ,
         length - 0.22,
         length,
         -half,
         half,
         Math.min(1.0F, red * 1.2F),
         Math.min(1.0F, green * 1.1F + 0.1F),
         blue,
         0.9F * breathe
      );
      if (half < 2.0) {
         double cada = Math.max(2.0, length / 7.0);

         for (int i = 1; i * cada < length; i++) {
            double a = i * cada;
            strip(pose, buffer, alongX, alongZ, a, a + 0.5, half - 0.1, half + 0.55, red, green, blue, 0.55F * breathe);
            strip(pose, buffer, alongX, alongZ, a, a + 0.5, -half - 0.55, -half + 0.1, red, green, blue, 0.55F * breathe);
         }

         double muro = 0.3 + 1.05 * progress;
         cuneta(pose, buffer, alongX, alongZ, 0.0, length, half, muro, red, green, blue, 0.42F * breathe);
         cuneta(pose, buffer, alongX, alongZ, 0.0, length, -half, muro, red * 0.85F, green * 0.85F, blue * 0.92F, 0.34F * breathe);
         if (length > 6.0) {
            double ax = alongX * (length + 0.5);
            double az = alongZ * (length + 0.5);
            double lx = alongX * (length - 1.7) - alongZ * -half;
            double lz = alongZ * (length - 1.7) + alongX * -half;
            double rx = alongX * (length - 1.7) - alongZ * half;
            double rz = alongZ * (length - 1.7) + alongX * half;
            float hot = Math.min(1.0F, red * 1.25F);
            float warm = Math.min(1.0F, green * 1.15F + 0.12F);

            for (int i = 0; i < 2; i++) {
               double ancho = i == 0 ? 0.42 : 0.18;
               float alpha = (i == 0 ? 0.35F : 0.95F) * breathe;
               quad(pose, buffer, lx, lz, ax, az, ancho, hot, warm, blue, alpha);
               quad(pose, buffer, ax, az, rx, rz, ancho, hot, warm, blue, alpha);
            }
         }
      }

      float hot = Math.min(1.0F, red * 1.3F + 0.06F);
      float warm = Math.min(1.0F, green * 1.2F + 0.08F);
      double tope = 0.4 + 1.6 * progress;
      pilote(pose, buffer, alongX * length - alongZ * half, alongZ * length + alongX * half, 0.26, tope, hot, warm, blue, 0.5F * breathe);
      pilote(pose, buffer, alongX * length + alongZ * half, alongZ * length - alongX * half, 0.26, tope, hot, warm, blue, 0.5F * breathe);
   }

   private static void strip(
      Matrix4f pose, VertexConsumer buffer, float alongX, float alongZ, double from, double to, double left, double right, float r, float g, float b, float a
   ) {
      corner(pose, buffer, alongX * from - alongZ * left, alongZ * from + alongX * left, r, g, b, a);
      corner(pose, buffer, alongX * to - alongZ * left, alongZ * to + alongX * left, r, g, b, a);
      corner(pose, buffer, alongX * to - alongZ * right, alongZ * to + alongX * right, r, g, b, a);
      corner(pose, buffer, alongX * from - alongZ * right, alongZ * from + alongX * right, r, g, b, a);
   }

   private static void cuneta(
      Matrix4f pose, VertexConsumer buffer, float alongX, float alongZ, double from, double to, double lado, double alto, float r, float g, float b, float a
   ) {
      if (!(alto <= 0.03) && !(to <= from)) {
         int pasos = Mth.clamp((int)Math.ceil((to - from) / 1.6), 1, 12);
         double paso = (to - from) / pasos;

         for (int i = 0; i < pasos; i++) {
            double d0 = from + paso * i;
            double d1 = from + paso * (i + 1);
            double x0 = alongX * d0 - alongZ * lado;
            double z0 = alongZ * d0 + alongX * lado;
            double x1 = alongX * d1 - alongZ * lado;
            double z1 = alongZ * d1 + alongX * lado;
            double vira = i % 2 == 0 ? 1.0 : 0.7;
            muro(pose, buffer, x0, z0, x1, z1, 0.0, alto, (float)(r * vira), (float)(g * vira), (float)(b * vira), a);
         }
      }
   }

   private static void crack(
      Matrix4f pose, VertexConsumer buffer, ZonaPeligroEntity zone, float red, float green, float blue, float breathe, double progress, float age
   ) {
      double length = zone.getRadius();
      double angle = zone.getBeamAngle();
      int seed = zone.getSeed();
      float body = (float)(1.0 - progress * progress * 0.7) * breathe;
      double alcance = Mth.clamp((age - 1.0F) / 5.0F, 0.0, 1.0) * length;
      linea(pose, buffer, angle, length, 0.52, red, green, blue, body, alcance, seed);
      linea(pose, buffer, angle + Math.PI, length * 0.52, 0.34, red, green, blue, body * 0.9F, alcance, seed + 7);
      linea(pose, buffer, angle + 1.05, length * 0.44, 0.3, red, green, blue, body * 0.8F, alcance, seed + 13);
      linea(pose, buffer, angle - 0.85, length * 0.36, 0.26, red, green, blue, body * 0.75F, alcance, seed + 21);
      if (progress < 0.55) {
         float fresco = (float)(1.0 - progress / 0.55) * breathe;
         band(pose, buffer, 0.0, 0.62, red * 0.4F, green * 0.4F, blue * 0.4F, 0.65F * fresco);
         band(pose, buffer, 0.62, 1.05, red * 0.3F + 0.34F, green * 0.3F + 0.31F, blue * 0.3F + 0.28F, 0.4F * fresco);

         for (int i = 0; i < 8; i++) {
            linea(
               pose,
               buffer,
               angle + i * Math.PI / 4.0 + Math.sin(i * 3.1 + seed) * 0.22,
               length * (0.1 + 0.07 * Math.sin(i * 2.4 + seed)),
               0.11,
               red,
               green,
               blue,
               fresco * (0.8F - 0.06F * i),
               alcance,
               seed + 40 + i * 5
            );
         }
      }
   }

   private static void linea(
      Matrix4f pose, VertexConsumer buffer, double angle, double largo, double width, float red, float green, float blue, float alpha, double alcance, int seed
   ) {
      double avanza = Math.min(1.0, alcance / largo);
      if (!(avanza <= 0.0)) {
         fissure(pose, buffer, angle, largo * avanza, width * (0.45 + 0.55 * avanza), red, green, blue, alpha, seed);
      }
   }

   private static void fissure(
      Matrix4f pose, VertexConsumer buffer, double angle, double length, double width, float red, float green, float blue, float alpha, int seed
   ) {
      double cos = Math.cos(angle);
      double sin = Math.sin(angle);
      double px = 0.0;
      double pz = 0.0;

      for (int i = 1; i <= 14; i++) {
         double along = length * i / 14.0;
         double side = (Math.sin(i * 1.9 + seed) * 0.16 + Math.sin(i * 0.61 + seed * 0.7) * 0.1) * along * 0.6;
         double x = cos * along - sin * side;
         double z = sin * along + cos * side;
         double taper = width * (1.0 - 0.5 * i / 14.0);
         quad(pose, buffer, px, pz, x, z, taper * 2.1, red * 0.4F + 0.36F, green * 0.4F + 0.33F, blue * 0.4F + 0.3F, alpha * 0.45F);
         quad(pose, buffer, px, pz, x, z, taper, red, green, blue, alpha);
         px = x;
         pz = z;
      }
   }

   private static void rayCircle(
      Matrix4f pose,
      VertexConsumer buffer,
      ZonaPeligroEntity zone,
      float red,
      float green,
      float blue,
      float breathe,
      double progress,
      float age,
      float partialTick
   ) {
      double r = zone.getRadius();
      aviso(pose, buffer, zone, red, green, blue, breathe, progress, age);
      float pulso = 0.8F + 0.2F * Mth.sin(age * 0.5F);
      band(pose, buffer, 0.0, r * 0.2, red, green, blue, 0.7F * pulso * breathe);
      band(pose, buffer, r * 0.55, r * 0.62, red, green, blue, 0.5F * pulso * breathe);
      int seed = zone.getSeed();
      double alto = zone.getHeight() > 0.0 ? zone.getHeight() : r * 1.6;
      if (!(progress < 0.55)) {
         float live = (float)Math.min(1.0, (progress - 0.55) / 0.1) * breathe;
         int ciclo = (int)Math.floor(age / 3.0);

         for (int i = 0; i < 4; i++) {
            if ((ciclo + i + seed) % 3 != 1) {
               double ang = (Math.PI * 2) * i / 4.0 + seed * 0.31 + ciclo * 0.2;
               double x = Math.cos(ang) * r * 0.82;
               double z = Math.sin(ang) * r * 0.82;
               double salida = alto * (0.7 + 0.3 * Math.sin(i * 2.3 + seed));
               rayo(pose, buffer, x, 0.0, z, x * 1.25, salida, z * 1.25, alto * 0.14, 0.34, seed + i * 11, red, green, blue, live);
            }
         }

         Vec3 otro = zone.mirrorPaintCentre(partialTick);
         if (otro != null && zone.getId() < zone.getMirrorId()) {
            Vec3 aqui = zone.paintCentre(partialTick);
            double dx = otro.x - aqui.x;
            double dz = otro.z - aqui.z;
            double largo = Math.max(0.001, Math.hypot(dx, dz));
            double nx = dx / largo * r * 0.82;
            double nz = dz / largo * r * 0.82;
            rayo(pose, buffer, nx, 0.0, nz, dx - nx, 0.0, dz - nz, largo * 0.42, 0.3, seed + 5, red, green, blue, live);
         }
      }
   }

   private static void pozo(
      Matrix4f pose, VertexConsumer buffer, ZonaPeligroEntity zone, float red, float green, float blue, float breathe, double progress, float age
   ) {
      double r = zone.getRadius();
      double throat = zone.getHeight() > 0.0 ? zone.getHeight() : r * 0.45;
      double giro = age * 0.2 + zone.getSeed();

      for (int i = 5; i >= 1; i--) {
         double outer = r * i / 5.0;
         double inner = r * (i - 1) / 5.0;
         float oscuro = 0.35F + 0.11F * (5 - i);
         band(pose, buffer, inner, outer, red * 0.16F, green * 0.1F, blue * 0.22F, oscuro * breathe);
      }

      band(pose, buffer, r * 0.9, r, red, green, blue, (0.75F + 0.25F * Mth.sin(age * 0.6F)) * breathe);
      band(pose, buffer, r * 1.02, r * 1.1, red * 0.7F + 0.3F, green * 0.7F + 0.3F, blue * 0.7F + 0.3F, 0.45F * breathe);
      embudo(pose, buffer, r, r * 0.32, throat * (0.55 + 0.45 * (float)progress), red * 0.5F, green * 0.35F, blue * 0.7F, 0.5F * breathe);

      for (int a = 0; a < 3; a++) {
         espiral(pose, buffer, r, 1.45, giro + a * 2.09, 0.5, red, green, blue, 0.8F * breathe);
      }

      int rieles = 10;

      for (int s = 0; s < rieles; s++) {
         double ang = (Math.PI * 2) * s / rieles + giro * 0.5;
         double t = (age * 0.055 + s * 0.137) % 1.0;
         double fuera = r * (1.06 - 0.72 * t);
         double dentro = fuera - Math.max(0.7, r * 0.16);
         double cos = Math.cos(ang);
         double sin = Math.sin(ang);
         quad(
            pose,
            buffer,
            cos * dentro,
            sin * dentro,
            cos * fuera,
            sin * fuera,
            0.16,
            red * 0.5F + 0.5F,
            green * 0.4F + 0.45F,
            blue * 0.6F + 0.4F,
            (float)(1.0 - t) * 0.85F * breathe
         );
      }
   }

   private static void escombros(Matrix4f pose, VertexConsumer buffer, double head, float r, float g, float b, float a) {
      int rieles = 10;

      for (int s = 0; s < rieles; s++) {
         double ang = (Math.PI * 2) * s / rieles + head * 0.16 + s;
         double t = (head * 0.21 + s * 0.19) % 1.0;
         double fuera = head * (1.0 - 0.26 * t);
         double dentro = Math.max(0.05, fuera - Math.max(0.55, head * 0.15));
         double cos = Math.cos(ang);
         double sin = Math.sin(ang);
         quad(
            pose,
            buffer,
            cos * dentro,
            sin * dentro,
            cos * fuera,
            sin * fuera,
            0.14,
            Math.min(1.0F, r * 1.1F + 0.12F),
            Math.min(1.0F, g * 1.05F + 0.1F),
            b,
            (float)(1.0 - t) * a * 0.85F
         );
      }
   }

   private static void embudo(Matrix4f pose, VertexConsumer buffer, double rBase, double rTop, double height, float r, float g, float b, float a) {
      for (int i = 0; i < 40; i++) {
         double a0 = (Math.PI * 2) * i / 40.0;
         double a1 = (Math.PI * 2) * (i + 1) / 40.0;
         p(pose, buffer, Math.cos(a0) * rBase, 0.0, Math.sin(a0) * rBase, r, g, b, a);
         p(pose, buffer, Math.cos(a1) * rBase, 0.0, Math.sin(a1) * rBase, r, g, b, a);
         p(pose, buffer, Math.cos(a1) * rTop, height, Math.sin(a1) * rTop, r, g, b, a);
         p(pose, buffer, Math.cos(a0) * rTop, height, Math.sin(a0) * rTop, r, g, b, a);
      }
   }

   private static void espiral(Matrix4f pose, VertexConsumer buffer, double r, double vueltas, double giro, double width, float rC, float gC, float bC, float a) {
      double px = 0.0;
      double pz = 0.0;

      for (int i = 1; i <= 14; i++) {
         double t = i / 14.0;
         double ang = giro + t * 2.0 * Math.PI * vueltas;
         double radio = r * (1.05 - t);
         double x = Math.cos(ang) * radio;
         double z = Math.sin(ang) * radio;
         quad(pose, buffer, px, pz, x, z, width * (1.0 - 0.55 * t), rC, gC, bC, a * (1.0F - (float)t * 0.55F));
         px = x;
         pz = z;
      }
   }

   private static void columnas(Matrix4f pose, VertexConsumer buffer, ZonaPeligroEntity zone, float red, float green, float blue, float breathe, float age) {
      double area = zone.getRadius();
      int cantidad = ZonaPeligroEntity.columnCount(Math.round(zone.getCount()));
      double alto = zone.getHeight() > 0.0 ? zone.getHeight() : 3.0;
      double subida = zone.getWave() > 0.0 ? zone.getWave() : 8.0;
      int seed = zone.getSeed();

      for (int c = 0; c < cantidad; c++) {
         Vec3 spot = ZonaPeligroEntity.columnSpot(c, cantidad, area, seed);
         double x = spot.x;
         double z = spot.z;
         double lado = 0.75 + 0.5 * ((seed * 31 + c * 17) % 10) / 9.0;
         double t = (age - c * 1.6) / subida;
         if (!(t <= 0.0)) {
            double altura = alto * (0.45 + 0.55 * Math.sin((seed + c) * 1.7) * Math.sin((seed + c) * 1.7)) * Mth.clamp(t, 0.0, 1.0);
            if (t < 1.0) {
               aro(pose, buffer, x, z, Math.max(0.0, lado * 0.9), lado * 1.7, red, green, blue, 0.45F * (1.0F - (float)t) * breathe);
            }

            pilote(pose, buffer, x, z, lado, altura, red, green, blue, breathe);
         }
      }
   }

   private static void pilote(Matrix4f pose, VertexConsumer buffer, double x, double z, double side, double height, float r, float g, float b, float a) {
      if (!(height <= 0.05)) {
         int cursos = (int)Math.ceil(height / side);

         for (int c = 0; c < cursos; c++) {
            caja(pose, buffer, x, z, side * 0.5, c * side, Math.min(height, (c + 1) * side), r, g, b, a, c);
         }
      }
   }

   private static void caja(
      Matrix4f pose, VertexConsumer buffer, double x, double z, double half, double y0, double y1, float r, float g, float b, float a, int curso
   ) {
      float vira = curso % 2 == 0 ? 1.0F : 0.9F;
      muro(pose, buffer, x - half, z - half, x + half, z - half, y0, y1, r * 0.95F * vira, g * 0.95F * vira, b * 0.95F * vira, a);
      muro(pose, buffer, x + half, z + half, x - half, z + half, y0, y1, r * 0.78F * vira, g * 0.78F * vira, b * 0.78F * vira, a);
      muro(pose, buffer, x - half, z + half, x - half, z - half, y0, y1, r * 0.62F * vira, g * 0.62F * vira, b * 0.62F * vira, a);
      muro(pose, buffer, x + half, z - half, x + half, z + half, y0, y1, r * 0.68F * vira, g * 0.68F * vira, b * 0.68F * vira, a);
      p(pose, buffer, x - half, y1, z - half, r * 1.1F, g * 1.1F, b * 1.1F, a);
      p(pose, buffer, x + half, y1, z - half, r * 1.1F, g * 1.1F, b * 1.1F, a);
      p(pose, buffer, x + half, y1, z + half, r * 1.05F, g * 1.05F, b * 1.05F, a);
      p(pose, buffer, x - half, y1, z + half, r * 1.05F, g * 1.05F, b * 1.05F, a);
   }

   private static void muro(
      Matrix4f pose, VertexConsumer buffer, double x0, double z0, double x1, double z1, double y0, double y1, float r, float g, float b, float a
   ) {
      p(pose, buffer, x0, y0, z0, r, g, b, a);
      p(pose, buffer, x1, y0, z1, r, g, b, a);
      p(pose, buffer, x1, y1, z1, r, g, b, a);
      p(pose, buffer, x0, y1, z0, r, g, b, a);
   }

   private static void campo(Matrix4f pose, VertexConsumer buffer, ZonaPeligroEntity zone, float red, float green, float blue, float breathe, double progress) {
      double r = zone.getRadius();
      double quiebra = zone.wander() > 0.0 ? zone.wander() : 0.22;
      int seed = zone.getSeed();
      float body = (float)(1.0 - progress * progress * 0.55) * breathe;
      poligono(pose, buffer, r, quiebra, seed, red * 0.45F + 0.08F, green * 0.4F + 0.05F, blue * 0.4F + 0.08F, 0.32F * body);
      poligono(pose, buffer, r * 0.66, quiebra * 1.3, seed + 9, red, green, blue, 0.26F * body);
      canto(pose, buffer, r * 0.9, r, quiebra, seed, red, green, blue, 0.85F * body);
      canto(pose, buffer, r * 0.62, r * 0.66, quiebra * 1.3, seed + 9, red, green, blue, 0.5F * body);
      double crece = Mth.clamp(progress * 4.0, 0.0, 1.0);
      labio(pose, buffer, r, quiebra, seed, 10, 1.05 * crece * body, red, green, blue, 0.38F * body);
      labio(pose, buffer, r * 0.66, quiebra * 1.3, seed + 9, 6, 0.7 * crece * body, red, green, blue, 0.3F * body);
      double angle = zone.getBeamAngle();

      for (int i = 0; i < 6; i++) {
         double largo = r * (1.15 + 0.35 * Math.sin(i * 2.1 + seed));
         fissure(pose, buffer, angle + i * Math.PI / 3.0 + Math.sin(i + seed) * 0.16, largo, 0.4, red, green, blue, body * (0.9F - 0.1F * i), seed + i * 7);
      }
   }

   private static void poligono(Matrix4f pose, VertexConsumer buffer, double base, double quiebra, int seed, float r, float g, float b, float a) {
      int pasos = 80;

      for (int i = 0; i < pasos; i++) {
         double a0 = (Math.PI * 2) * i / pasos;
         double a1 = (Math.PI * 2) * (i + 1) / pasos;
         double r0 = borde(base, quiebra, seed, a0);
         double r1 = borde(base, quiebra, seed, a1);
         tri(pose, buffer, 0.0, 0.0, r0 * Math.cos(a0), r0 * Math.sin(a0), r1 * Math.cos(a1), r1 * Math.sin(a1), r, g, b, a);
      }
   }

   private static void canto(Matrix4f pose, VertexConsumer buffer, double inner, double outer, double quiebra, int seed, float r, float g, float b, float a) {
      int pasos = 80;

      for (int i = 0; i < pasos; i++) {
         double a0 = (Math.PI * 2) * i / pasos;
         double a1 = (Math.PI * 2) * (i + 1) / pasos;
         double i0 = borde(inner, quiebra, seed, a0);
         double i1 = borde(inner, quiebra, seed, a1);
         double o0 = borde(outer, quiebra, seed, a0);
         double o1 = borde(outer, quiebra, seed, a1);
         p(pose, buffer, i1 * Math.cos(a1), 0.0, i1 * Math.sin(a1), r, g, b, a);
         p(pose, buffer, o1 * Math.cos(a1), 0.0, o1 * Math.sin(a1), r, g, b, a);
         p(pose, buffer, o0 * Math.cos(a0), 0.0, o0 * Math.sin(a0), r, g, b, a);
         p(pose, buffer, i0 * Math.cos(a0), 0.0, i0 * Math.sin(a0), r, g, b, a);
      }
   }

   private static double borde(double base, double quiebra, int seed, double angulo) {
      return base * (1.0 + quiebra * (Math.sin(angulo * 3.0 + seed) * 0.55 + Math.sin(angulo * 8.0 + seed * 2.3) * 0.3));
   }

   private static void labio(
      Matrix4f pose, VertexConsumer buffer, double base, double quiebra, int seed, int total, double alto, float r, float g, float b, float a
   ) {
      if (!(alto <= 0.05)) {
         for (int i = 0; i < total; i++) {
            double ang = (Math.PI * 2) * i / total + seed * 0.11;
            double radial = borde(base, quiebra, seed, ang);
            double lado = Math.max(0.3, base * 0.08);
            pilote(
               pose,
               buffer,
               radial * Math.cos(ang),
               radial * Math.sin(ang),
               lado,
               alto * (0.55 + 0.45 * Math.sin(i * 2.7 + seed) * Math.sin(i * 2.7 + seed)),
               r,
               g,
               b,
               a
            );
         }
      }
   }

   private static void surco(Matrix4f pose, VertexConsumer buffer, ZonaPeligroEntity zone, float red, float green, float blue, float breathe, float age) {
      double length = zone.getBeamLength();
      double half = Math.max(0.8, zone.getDepth() * 0.5);
      double angle = zone.getBeamAngle();
      double travel = zone.getWave() > 0.0 ? zone.getWave() : 20.0;
      double head = length * Mth.clamp(age / travel, 0.0, 1.0);
      int seed = zone.getSeed();
      float alongX = Mth.cos((float)angle);
      float alongZ = Mth.sin((float)angle);
      float body = (float)Math.min(1.0, (age + 2.0) / 5.0) * (float)Math.min(1.0, head / Math.max(0.5, half));
      strip(pose, buffer, alongX, alongZ, 0.0, head, -half, half, red * 0.35F, green * 0.3F, blue * 0.32F, 0.55F * body * breathe);
      strip(pose, buffer, alongX, alongZ, 0.0, head, half, half + 0.45, red, green, blue, 0.8F * body * breathe);
      strip(pose, buffer, alongX, alongZ, 0.0, head, -half - 0.45, -half, red, green, blue, 0.8F * body * breathe);
      strip(pose, buffer, alongX, alongZ, Math.max(0.0, head - 1.6), head, -half, half, red, green, blue, 0.9F * breathe);

      for (int f = 0; f < 4; f++) {
         double off = -half * 0.74 + f * (half * 0.49);
         double largo = head * (0.72 + 0.28 * Math.sin(f * 2.4 + seed));
         surquito(pose, buffer, alongX, alongZ, off, largo, 0.34, seed + f * 13, red, green, blue, 0.75F * body * breathe);
      }

      double banco = 0.35 + 0.5 * body;
      cuneta(pose, buffer, alongX, alongZ, 0.0, head, half + 0.2, banco, red * 0.82F, green * 0.74F, blue * 0.8F, 0.45F * body * breathe);
      cuneta(pose, buffer, alongX, alongZ, 0.0, head, -half - 0.2, banco, red * 0.82F, green * 0.74F, blue * 0.8F, 0.45F * body * breathe);
      muro(
         pose,
         buffer,
         alongX * head - alongZ * -half,
         alongZ * head + alongX * -half,
         alongX * head - alongZ * half,
         alongZ * head + alongX * half,
         0.0,
         0.75 + 0.55 * body,
         Math.min(1.0F, red * 1.2F),
         Math.min(1.0F, green * 1.1F + 0.08F),
         blue,
         0.5F * breathe
      );
   }

   private static void surquito(
      Matrix4f pose, VertexConsumer buffer, float alongX, float alongZ, double off, double length, double width, int seed, float r, float g, float b, float a
   ) {
      double px = alongX * 0.0 - alongZ * off;
      double pz = alongZ * 0.0 + alongX * off;

      for (int i = 1; i <= 14; i++) {
         double along = length * i / 14.0;
         double side = off + Math.sin(i * 1.7 + seed) * 0.22 + Math.sin(i * 0.53 + seed * 1.3) * 0.14;
         double x = alongX * along - alongZ * side;
         double z = alongZ * along + alongX * side;
         double taper = width * (1.0 - 0.45 * i / 14.0);
         quad(pose, buffer, px, pz, x, z, taper * 1.9, r * 0.4F + 0.35F, g * 0.4F + 0.32F, b * 0.4F + 0.3F, a * 0.4F);
         quad(pose, buffer, px, pz, x, z, taper, r, g, b, a);
         px = x;
         pz = z;
      }
   }

   private static void avalancha(Matrix4f pose, VertexConsumer buffer, ZonaPeligroEntity zone, float red, float green, float blue, float breathe, double progress) {
      double length = zone.getBeamLength();
      double thick = Math.max(1.2, (double)zone.getDepth());
      double alto = zone.getHeight() > 0.0 ? zone.getHeight() : 3.5;
      double angle = zone.getBeamAngle();
      int seed = zone.getSeed();
      float alongX = Mth.cos((float)angle);
      float alongZ = Mth.sin((float)angle);
      double frente = length * Mth.clamp(progress, 0.0, 1.0);
      strip(pose, buffer, alongX, alongZ, 0.0, frente, -thick * 0.5, thick * 0.5, red * 0.4F, green * 0.35F, blue * 0.35F, 0.4F * breathe);
      strip(pose, buffer, alongX, alongZ, 0.0, frente, thick * 0.5 - 0.4, thick * 0.5, red, green, blue, 0.7F * breathe);
      strip(pose, buffer, alongX, alongZ, 0.0, frente, -thick * 0.5, -thick * 0.5 + 0.4, red, green, blue, 0.7F * breathe);
      int carriles = Mth.clamp((int)Math.round(thick / 1.3), 3, 9);

      for (int l = 0; l < carriles; l++) {
         double off = -thick * 0.5 + (l + 0.5) * thick / carriles;

         for (int k = 0; k < 4; k++) {
            double along = frente - k * 1.25 + Math.sin(l * 2.6 + seed) * 0.4;
            if (!(along < 0.0)) {
               double lado = 0.8 + (seed * 13 + l * 7 + k * 3) % 10 / 22.0;
               double altura = alto * (1.0 - 0.17 * k) * (0.55 + 0.45 * Math.sin((l + k) * 1.9 + seed * 0.4) * Math.sin((l + k) * 1.9 + seed * 0.4));
               pilote(pose, buffer, alongX * along - alongZ * off, alongZ * along + alongX * off, lado, altura, red, green, blue, breathe);
            }
         }
      }
   }

   private static void rayo(
      Matrix4f pose,
      VertexConsumer buffer,
      double x0,
      double y0,
      double z0,
      double x1,
      double y1,
      double z1,
      double arco,
      double width,
      int seed,
      float r,
      float g,
      float b,
      float a
   ) {
      double dx = x1 - x0;
      double dy = y1 - y0;
      double dz = z1 - z0;
      double largo = Math.max(0.001, Math.sqrt(dx * dx + dy * dy + dz * dz));
      double ix = dx / largo;
      double iy = dy / largo;
      double iz = dz / largo;
      double hx = -iz;
      double hz = ix;
      double hl = Math.hypot(hx, ix);
      if (hl < 1.0E-4) {
         hx = 1.0;
         hz = 0.0;
         hl = 1.0;
      }

      hx /= hl;
      hz /= hl;
      double vx = iy * hz - iz * 0.0;
      double vy = iz * hx - ix * hz;
      double vz = ix * 0.0 - iy * hx;
      double vl = Math.max(1.0E-4, Math.sqrt(vx * vx + vy * vy + vz * vz));
      vx /= vl;
      vy /= vl;
      vz /= vl;
      double px = x0;
      double py = y0;
      double pz = z0;

      for (int i = 1; i <= 10; i++) {
         double t = i / 10.0;
         double desvio = (Math.sin(i * 2.9 + seed) * 0.62 + Math.sin(i * 6.1 + seed * 1.7) * 0.38) * (width * 2.6) * Math.sin(t * Math.PI);
         double x = x0 + dx * t + hx * desvio;
         double y = y0 + dy * t + arco * Math.sin(t * Math.PI) + vy * desvio * 0.3;
         double z = z0 + dz * t + hz * desvio;
         double grueso = width * (0.35 + 0.65 * Math.sin(t * Math.PI));
         cinta(pose, buffer, px, py, pz, x, y, z, hx, 0.0, hz, grueso * 1.8, r, g, b, a * 0.4F);
         cinta(pose, buffer, px, py, pz, x, y, z, vx, vy, vz, grueso * 1.8, r, g, b, a * 0.4F);
         cinta(pose, buffer, px, py, pz, x, y, z, hx, 0.0, hz, grueso, r * 0.5F + 0.5F, g * 0.5F + 0.52F, b * 0.5F + 0.6F, a);
         cinta(pose, buffer, px, py, pz, x, y, z, vx, vy, vz, grueso, r * 0.5F + 0.5F, g * 0.5F + 0.52F, b * 0.5F + 0.6F, a);
         px = x;
         py = y;
         pz = z;
      }
   }

   private static void cinta(
      Matrix4f pose,
      VertexConsumer buffer,
      double x0,
      double y0,
      double z0,
      double x1,
      double y1,
      double z1,
      double ax,
      double ay,
      double az,
      double half,
      float r,
      float g,
      float b,
      float a
   ) {
      p(pose, buffer, x0 + ax * half, y0 + ay * half, z0 + az * half, r, g, b, a);
      p(pose, buffer, x1 + ax * half, y1 + ay * half, z1 + az * half, r, g, b, a);
      p(pose, buffer, x1 - ax * half, y1 - ay * half, z1 - az * half, r, g, b, a);
      p(pose, buffer, x0 - ax * half, y0 - ay * half, z0 - az * half, r, g, b, a);
   }

   private static void quad(Matrix4f pose, VertexConsumer buffer, double x0, double z0, double x1, double z1, double half, float r, float g, float b, float a) {
      double dx = x1 - x0;
      double dz = z1 - z0;
      double length = Math.max(1.0E-4, Math.hypot(dx, dz));
      double nx = -dz / length * half;
      double nz = dx / length * half;
      corner(pose, buffer, x0 + nx, z0 + nz, r, g, b, a);
      corner(pose, buffer, x1 + nx, z1 + nz, r, g, b, a);
      corner(pose, buffer, x1 - nx, z1 - nz, r, g, b, a);
      corner(pose, buffer, x0 - nx, z0 - nz, r, g, b, a);
   }

   private static void tri(
      Matrix4f pose, VertexConsumer buffer, double x0, double z0, double x1, double z1, double x2, double z2, float r, float g, float b, float a
   ) {
      corner(pose, buffer, x0, z0, r, g, b, a);
      corner(pose, buffer, x1, z1, r, g, b, a);
      corner(pose, buffer, x2, z2, r, g, b, a);
      corner(pose, buffer, x2, z2, r, g, b, a);
   }

   private static void aro(Matrix4f pose, VertexConsumer buffer, double cx, double cz, double inner, double outer, float r, float g, float b, float a) {
      for (int i = 0; i < 40; i++) {
         double a0 = (Math.PI * 2) * i / 40.0;
         double a1 = (Math.PI * 2) * (i + 1) / 40.0;
         double cos0 = Math.cos(a0);
         double sin0 = Math.sin(a0);
         double cos1 = Math.cos(a1);
         double sin1 = Math.sin(a1);
         corner(pose, buffer, cx + inner * cos1, cz + inner * sin1, r, g, b, a);
         corner(pose, buffer, cx + outer * cos1, cz + outer * sin1, r, g, b, a);
         corner(pose, buffer, cx + outer * cos0, cz + outer * sin0, r, g, b, a);
         corner(pose, buffer, cx + inner * cos0, cz + inner * sin0, r, g, b, a);
      }
   }

   private static void band(Matrix4f pose, VertexConsumer buffer, double inner, double outer, float r, float g, float b, float a) {
      for (int i = 0; i < 40; i++) {
         double a0 = (Math.PI * 2) * i / 40.0;
         double a1 = (Math.PI * 2) * (i + 1) / 40.0;
         float cos0 = Mth.cos((float)a0);
         float sin0 = Mth.sin((float)a0);
         float cos1 = Mth.cos((float)a1);
         float sin1 = Mth.sin((float)a1);
         corner(pose, buffer, inner * cos1, inner * sin1, r, g, b, a);
         corner(pose, buffer, outer * cos1, outer * sin1, r, g, b, a);
         corner(pose, buffer, outer * cos0, outer * sin0, r, g, b, a);
         corner(pose, buffer, inner * cos0, inner * sin0, r, g, b, a);
      }
   }

   private static void corner(Matrix4f pose, VertexConsumer buffer, double x, double z, float r, float g, float b, float a) {
      p(pose, buffer, x, 0.0, z, r, g, b, a);
   }

   private static void p(Matrix4f pose, VertexConsumer buffer, double x, double y, double z, float r, float g, float b, float a) {
      buffer.addVertex(pose, (float)x, (float)y, (float)z)
         .setColor(
            Mth.clamp(r, 0.0F, 1.0F),
            Mth.clamp(g, 0.0F, 1.0F),
            Mth.clamp(b, 0.0F, 1.0F),
            Mth.clamp(a, 0.0F, 1.0F)
         );
   }
}
