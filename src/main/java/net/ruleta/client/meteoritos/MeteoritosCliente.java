package net.ruleta.client.meteoritos;

import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexSorting;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.DoubleUnaryOperator;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance.Attenuation;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.ruleta.client.EfectosCliente;
import net.ruleta.client.agujero.AgujeroCliente;
import net.ruleta.client.compat.ClientPlayerNetworkEvent;
import net.ruleta.client.compat.RenderLevelStageEvent;
import net.ruleta.client.compat.SubscribeEvent;
import net.ruleta.client.compat.TickEvent;
import net.ruleta.client.compat.ViewportEvent;
import net.ruleta.meteoritos.MeteoritoEntity;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;

public final class MeteoritosCliente {
   private static final SoundEvent VUELO = sonido("meteoritos.vuelo");
   private static final SoundEvent IMPACTO = sonido("meteoritos.impacto");
   private static final SoundEvent AMBIENTE = sonido("meteoritos.ambiente");
   private static final float ENTRA = 5.0F;
   private static final float SALE = 4.0F;
   private static final float RADIO = 4.2F;
   private static final float ESTELA = 30.0F;
   private static final float VIDA = 16.0F;
   private static final float IMPACTO_VIDA = 24.0F;
   private static final int MAX_BOCANADAS = 3600;
   private static ShaderInstance shader;
   private static long desde = -1L;
   private static long acaba = -1L;
   private static final Map<Integer, SoundInstance> SILBIDOS = new HashMap<>();
   private static final Map<Integer, Vec3> ORIGEN = new HashMap<>();
   private static final Map<Integer, MeteoritosCliente.Estela> ESTELAS = new HashMap<>();
   private static final List<MeteoritosCliente.Estela> FIJAS = new ArrayList<>();
   private static final Set<Integer> SUELTOS = new HashSet<>();
   private static final double ALTURA_SUELTA = 50.0;
   private static final float VIDA_FUNDA = 2.5F;
   private static final float VIDA_HUMO = 16.0F;
   private static final List<MeteoritosCliente.Bocanada> HUMOS = new ArrayList<>();
   private static final List<MeteoritosCliente.Impacto> IMPACTOS = new ArrayList<>();
   private static SoundInstance ambiente;
   private static float destello;
   private static float polvo;
   private static long destelloEn;
   private static float camY;

   private MeteoritosCliente() {
   }

   public static void shader(ShaderInstance s) {
      shader = s;
   }

   private static SoundEvent sonido(String id) {
      return SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("ruleta", id));
   }

   public static void recibir(int valor) {
      long ahora = System.nanoTime();
      if (valor >= 0) {
         if (desde < 0L || acaba >= 0L) {
            desde = ahora - valor * 1000000000L;
            acaba = -1L;
         }
      } else if (desde >= 0L && acaba < 0L) {
         acaba = ahora;
      }
   }

   public static void impacto(int tamano, String donde) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.player != null) {
         String[] p = donde.trim().split(" ");
         if (p.length >= 3) {
            Vec3 c;
            try {
               c = new Vec3(Double.parseDouble(p[0]), Double.parseDouble(p[1]), Double.parseDouble(p[2]));
            } catch (NumberFormatException ex) {
               return;
            }

            double d = mc.player.getEyePosition().distanceTo(c);
            IMPACTOS.add(new MeteoritosCliente.Impacto(c, tamano / 100.0F));
            polvo = Math.min(1.0F, polvo + 0.3F);
            float f = (float)Math.max(0.0, 1.0 - d / 220.0);
            if (f > 0.0F) {
               destello = Math.max(destello * fadeDestello(), (float)Math.pow(f, 1.4));
               destelloEn = System.nanoTime();
            }

            if (d < 180.0) {
               EfectosCliente.golpe((float)(5.0 * (1.0 - d / 180.0)));
            }

            mc.getSoundManager()
               .play(
                  new SimpleSoundInstance(
                     IMPACTO.getLocation(),
                     SoundSource.MASTER,
                     d < 40.0 ? 2.0F : 8.0F,
                     0.95F + mc.level.random.nextFloat() * 0.2F,
                     RandomSource.create(),
                     false,
                     0,
                     d < 40.0 ? Attenuation.NONE : Attenuation.LINEAR,
                     c.x,
                     c.y,
                     c.z,
                     d < 40.0
                  )
               );
         }
      }
   }

   public static void limpiar() {
      desde = -1L;
      acaba = -1L;
      HUMOS.clear();
      IMPACTOS.clear();
      SILBIDOS.clear();
      ORIGEN.clear();
      ESTELAS.clear();
      FIJAS.clear();
      SUELTOS.clear();
      if (ambiente != null) {
         Minecraft.getInstance().getSoundManager().stop(ambiente);
         ambiente = null;
      }

      destello = 0.0F;
      polvo = 0.0F;
   }

   private static float suave(float x) {
      x = Mth.clamp(x, 0.0F, 1.0F);
      return x * x * (3.0F - 2.0F * x);
   }

   private static float segundos() {
      return (float)(System.nanoTime() - desde) / 1.0E9F;
   }

   public static boolean enCurso() {
      return desde >= 0L && acaba < 0L;
   }

   public static float fuerza() {
      if (desde < 0L) {
         return 0.0F;
      }

      float f = suave(segundos() / 5.0F);
      if (acaba >= 0L) {
         float s = (float)(System.nanoTime() - acaba) / 1.0E9F;
         f *= 1.0F - suave(s / 4.0F);
         if (s > 4.0F) {
            desde = -1L;
            acaba = -1L;
            return 0.0F;
         }
      }

      return f;
   }

   public static boolean tapaCielo() {
      Minecraft mc = Minecraft.getInstance();
      return shader != null && mc.level != null && mc.level.dimensionType().hasSkyLight() && fuerza() > 0.02F;
   }

   private static float fadeDestello() {
      return (float)Math.exp(-((float)(System.nanoTime() - destelloEn) / 1.0E9F) / 0.35F);
   }

   @SubscribeEvent
   public static void alTick(TickEvent.ClientTickEvent e) {
      if (e.phase == TickEvent.Phase.END) {
         Minecraft mc = Minecraft.getInstance();
         if (mc.level != null) {
            polvo = Math.max(0.0F, polvo - 7.142857E-4F);
            if (fuerza() > 0.0F && ambiente == null) {
               ambiente = new SonidoMeteoritos(AMBIENTE, true, () -> desde < 0L ? -1.0F : 0.9F * fuerza(), null);
               mc.getSoundManager().play(ambiente);
            } else if (desde < 0L && ambiente != null) {
               mc.getSoundManager().stop(ambiente);
               ambiente = null;
            }

            for (Entity ent : mc.level.entitiesForRendering()) {
               if (ent instanceof MeteoritoEntity m) {
                  if (!SILBIDOS.containsKey(m.getId()) && mc.player != null && mc.player.distanceToSqr(m) < 176400.0) {
                     SoundInstance s = new SonidoMeteoritos(VUELO, true, () -> m.isRemoved() ? -1.0F : 26.0F, () -> m.isRemoved() ? null : m.position());
                     SILBIDOS.put(m.getId(), s);
                     mc.getSoundManager().play(s);
                  }

                  float rr = 4.2F * Math.max(0.4F, m.tamano());
                  float dura = Mth.clamp(26.0F * rr / Math.max(1.0F, (float)m.getDeltaMovement().length() * 20.0F), 1.5F, 7.0F);
                  RandomSource re = mc.level.random;

                  for (int k = 0; k < 2; k++) {
                     double f = re.nextDouble();
                     MeteoritosCliente.Bocanada bo = new MeteoritosCliente.Bocanada(
                        Mth.lerp(f, m.xo, m.getX()) + (re.nextDouble() - 0.5) * rr * 0.5,
                        Mth.lerp(f, m.yo, m.getY()) + 0.5 * rr + (re.nextDouble() - 0.5) * rr * 0.5,
                        Mth.lerp(f, m.zo, m.getZ()) + (re.nextDouble() - 0.5) * rr * 0.4,
                        (re.nextFloat() - 0.5F) * 0.25F,
                        (re.nextFloat() - 0.5F) * 0.25F,
                        (re.nextFloat() - 0.5F) * 0.25F,
                        rr * 1.8F,
                        rr * 6.2F,
                        0.0F,
                        0.0F,
                        1.0F,
                        re.nextFloat(),
                        1.0F,
                        1000.0F
                     );
                     bo.estela = true;
                     bo.met = m.getId();
                     HUMOS.add(bo);
                  }

                  rr = Math.max(0.4F, m.tamano());
                  RandomSource r = mc.level.getRandom();
                  if (r.nextInt(2) == 0) {
                     double f = r.nextDouble();
                     HUMOS.add(
                        new MeteoritosCliente.Bocanada(
                           Mth.lerp(f, m.xo, m.getX()) + (r.nextDouble() - 0.5) * 2.4 * rr,
                           Mth.lerp(f, m.yo, m.getY()) + 2.1F * rr + (r.nextDouble() - 0.5) * 2.4 * rr,
                           Mth.lerp(f, m.zo, m.getZ()) + (r.nextDouble() - 0.5) * 2.4 * rr,
                           (r.nextFloat() - 0.5F) * 1.5F,
                           (r.nextFloat() - 0.5F) * 1.5F,
                           (r.nextFloat() - 0.5F) * 1.5F,
                           0.2F * rr,
                           0.26F * rr,
                           1.0F,
                           0.0F,
                           4.0F,
                           r.nextFloat(),
                           1.0F,
                           2.6F
                        )
                     );
                  }
               }
            }

            while (HUMOS.size() > 3600) {
               HUMOS.remove(0);
            }

            SILBIDOS.entrySet().removeIf(x -> mc.level.getEntity(x.getKey()) == null && !mc.getSoundManager().isActive(x.getValue()));
            HUMOS.removeIf(h -> h.edad() > h.dura || h.fin != 0L && (float)(System.nanoTime() - h.fin) / 1.0E9F > 3.1F);
            IMPACTOS.removeIf(p -> p.edad() > 24.0F);
         }
      }
   }

   @SubscribeEvent
   public static void alSalir(ClientPlayerNetworkEvent.LoggingOut e) {
      limpiar();
   }

   @SubscribeEvent
   public static void alColorearNiebla(ViewportEvent.ComputeFogColor e) {
      float k = fuerza() * 0.97F;
      if (!(k <= 0.0F)) {
         float fase = suave(segundos() / 14.0F);
         float r = Mth.lerp(polvo, Mth.lerp(fase, 0.62F, 0.66F), 0.68F);
         float g = Mth.lerp(polvo, Mth.lerp(fase, 0.1F, 0.04F), 0.12F);
         float b = Mth.lerp(polvo, Mth.lerp(fase, 0.12F, 0.04F), 0.08F);
         e.setRed(Mth.lerp(k, e.getRed(), r));
         e.setGreen(Mth.lerp(k, e.getGreen(), g));
         e.setBlue(Mth.lerp(k, e.getBlue(), b));
      }
   }

   @SubscribeEvent
   public static void alDibujar(RenderLevelStageEvent e) {
      if (e.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL && shader != null) {
         Minecraft mc = Minecraft.getInstance();
         if (mc.level != null) {
            float pt = e.getPartialTick();
            float fuerza = fuerza();
            boolean hay = false;

            for (Entity ent : mc.level.entitiesForRendering()) {
               if (ent instanceof MeteoritoEntity) {
                  hay = true;
                  break;
               }
            }

            float flash = destello * fadeDestello();
            if (!(fuerza <= 0.0F) || hay || !HUMOS.isEmpty() || !IMPACTOS.isEmpty() || !(flash < 0.003F)) {
               Camera cam = e.getCamera();
               Vec3 c = cam.getPosition();
               Matrix4f vista = new Matrix4f().rotateX(cam.getXRot() * (float) (Math.PI / 180.0)).rotateY((cam.getYRot() + 180.0F) * (float) (Math.PI / 180.0));
               float lejos = mc.options.getEffectiveRenderDistance() * 16.0F * 4.0F;
               float t = (float)(System.nanoTime() % 3600000000000L) / 1.0E9F;
               Matrix4f proyAntes = new Matrix4f(RenderSystem.getProjectionMatrix());
               VertexSorting ordenAntes = RenderSystem.getVertexSorting();
               Matrix4fStack mv = RenderSystem.getModelViewStack();
               mc.getMainRenderTarget().bindWrite(true);
               RenderSystem.setProjectionMatrix(new Matrix4f(e.getProjectionMatrix()), VertexSorting.DISTANCE_TO_ORIGIN);
               mv.pushMatrix();
               mv.identity();
               mv.mul(vista);
               RenderSystem.applyModelViewMatrix();
               RenderSystem.enableDepthTest();
               RenderSystem.depthFunc(515);

               try {
                  if (fuerza > 0.0F && mc.level.dimensionType().hasSkyLight() && !AgujeroCliente.tapaCielo()) {
                     camY = (float)c.y;
                     cielo(lejos, t, fuerza);
                     plano(Mth.lerp(polvo, 0.55F, 0.58F), Mth.lerp(polvo, 0.08F, 0.2F), Mth.lerp(polvo, 0.06F, 0.1F), 0.26F * fuerza, false);
                  }

                  for (MeteoritosCliente.Impacto n : IMPACTOS) {
                     n.fondo(c, cam, t);
                  }

                  ArrayList<float[]> todas = new ArrayList<>(HUMOS.size() + IMPACTOS.size() * 260);

                  for (MeteoritosCliente.Bocanada h : HUMOS) {
                     h.aniadir(todas, c);
                  }

                  for (MeteoritosCliente.Impacto n : IMPACTOS) {
                     n.bocanadas(todas, c);
                  }

                  if (!todas.isEmpty()) {
                     todas.sort((p1, p2) -> Float.compare(p2[0] * p2[0] + p2[1] * p2[1] + p2[2] * p2[2], p1[0] * p1[0] + p1[1] * p1[1] + p1[2] * p1[2]));
                     usar(9.0F, t, 0.0F, false);
                     RenderSystem.depthMask(false);
                     RenderSystem.disableCull();
                     BufferBuilder b = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

                     for (float[] p : todas) {
                        float uo = p.length > 8 ? 10.0F : 0.0F;
                        Vector3f iz = new Vector3f(cam.getLeftVector()).mul(p[3]);
                        Vector3f ar = new Vector3f(cam.getUpVector()).mul(p[3]);
                        b.addVertex(p[0] + iz.x - ar.x, p[1] + iz.y - ar.y, p[2] + iz.z - ar.z).setColor(p[4], p[5], p[6], p[7]).setUv(uo, 0.0F);
                        b.addVertex(p[0] - iz.x - ar.x, p[1] - iz.y - ar.y, p[2] - iz.z - ar.z).setColor(p[4], p[5], p[6], p[7]).setUv(uo + 1.0F, 0.0F);
                        b.addVertex(p[0] - iz.x + ar.x, p[1] - iz.y + ar.y, p[2] - iz.z + ar.z).setColor(p[4], p[5], p[6], p[7]).setUv(uo + 1.0F, 1.0F);
                        b.addVertex(p[0] + iz.x + ar.x, p[1] + iz.y + ar.y, p[2] + iz.z + ar.z).setColor(p[4], p[5], p[6], p[7]).setUv(uo, 1.0F);
                     }

                     BufferUploader.drawWithShader(b.buildOrThrow());
                  }

                  Set<Integer> vistos = new HashSet<>();

                  for (Entity ent : mc.level.entitiesForRendering()) {
                     if (ent instanceof MeteoritoEntity m) {
                        vistos.add(m.getId());
                        meteorito(m, c, pt, t, cam);
                     }
                  }

                  ORIGEN.keySet().retainAll(vistos);
                  SUELTOS.retainAll(vistos);
                  long ahoraNs = System.nanoTime();

                  for (Entry<Integer, MeteoritosCliente.Estela> en : ESTELAS.entrySet()) {
                     if (!vistos.contains(en.getKey()) && en.getValue().fija < 0L) {
                        en.getValue().fija = ahoraNs;
                        int idm = en.getKey();

                        for (MeteoritosCliente.Bocanada hb : HUMOS) {
                           if (hb.met == idm) {
                              hb.fin = ahoraNs;
                           }
                        }
                     }
                  }

                  ESTELAS.values().removeIf(x -> x.fija >= 0L && (float)(ahoraNs - x.fija) / 1.0E9F > 16.0F);

                  for (MeteoritosCliente.Estela var42 : ESTELAS.values()) {
                     ;
                  }

                  if (flash > 0.003F) {
                     plano(1.0F, 0.8F, 0.55F, Math.min(1.0F, flash), true);
                  }
               } finally {
                  RenderSystem.depthMask(true);
                  RenderSystem.enableDepthTest();
                  RenderSystem.enableCull();
                  RenderSystem.disableBlend();
                  RenderSystem.defaultBlendFunc();
                  mv.popMatrix();
                  RenderSystem.applyModelViewMatrix();
                  RenderSystem.setProjectionMatrix(proyAntes, ordenAntes);
               }
            }
         }
      }
   }

   private static ShaderInstance usar(float modo, float t, float semilla, boolean sumar) {
      ShaderInstance sh = shader;
      sh.safeGetUniform("Modo").set(modo);
      sh.safeGetUniform("Tiempo").set(t % 3600.0F);
      sh.safeGetUniform("Semilla").set(semilla);
      sh.safeGetUniform("Fuerza").set(1.0F);
      sh.safeGetUniform("Avance").set(Math.max(0.0F, desde < 0L ? 0.0F : segundos()));
      sh.safeGetUniform("Polvo").set(polvo);
      RenderSystem.setShader(() -> sh);
      RenderSystem.enableBlend();
      if (sumar) {
         RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
      } else {
         RenderSystem.defaultBlendFunc();
      }

      return sh;
   }

   private static void cielo(float lejos, float t, float fuerza) {
      ShaderInstance sh = usar(0.0F, t, 0.0F, false);
      sh.safeGetUniform("CamPos").set(0.0F, camY, 0.0F);
      sh.safeGetUniform("Fuerza").set(fuerza);
      RenderSystem.depthMask(false);
      RenderSystem.disableCull();
      float s = lejos * 0.3F;
      float[][] caras = new float[][]{
         {-s, s, -s, s, s, -s, s, s, s, -s, s, s},
         {-s, -s, -s, -s, -s, s, s, -s, s, s, -s, -s},
         {-s, -s, -s, s, -s, -s, s, s, -s, -s, s, -s},
         {s, -s, s, -s, -s, s, -s, s, s, s, s, s},
         {-s, -s, s, -s, -s, -s, -s, s, -s, -s, s, s},
         {s, -s, -s, s, -s, s, s, s, s, s, s, -s}
      };
      BufferBuilder b = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

      for (float[] q : caras) {
         for (int k = 0; k < 12; k += 3) {
            b.addVertex(q[k], q[k + 1], q[k + 2]).setColor(1.0F, 1.0F, 1.0F, 1.0F).setUv(0.0F, 0.0F);
         }
      }

      BufferUploader.drawWithShader(b.buildOrThrow());
   }

   private static float r0(float tam) {
      return 4.2F * tam;
   }

   private static void meteorito(MeteoritoEntity m, Vec3 cam, float pt, float t, Camera camara) {
      float tam = Math.max(0.4F, m.tamano());
      Vec3 p = new Vec3(Mth.lerp(pt, m.xo, m.getX()), Mth.lerp(pt, m.yo, m.getY()), Mth.lerp(pt, m.zo, m.getZ())).add(0.0, 0.5 * r0(tam), 0.0).subtract(cam);
      Vec3 v = m.getDeltaMovement();
      if (v.lengthSqr() < 1.0E-6) {
         v = new Vec3(0.0, -1.0, 0.0);
      }

      Vector3f dir = new Vector3f((float)v.x, (float)v.y, (float)v.z).normalize();
      Vector3f h = new Vector3f((float)p.x, (float)p.y, (float)p.z);
      float semilla = m.getId() % 97 * 1.37F;
      float r = 4.2F * tam;
      RenderSystem.disableCull();
      RenderSystem.enableDepthTest();
      RenderSystem.depthMask(true);
      ShaderInstance shb = usar(12.0F, t, semilla, false);
      shb.safeGetUniform("HumoA").set(h.x, h.y, h.z, r);
      Vector3f haciaCam = new Vector3f(h).normalize().mul(-r);
      cartel(new Vector3f(h).add(haciaCam), r * 1.35F, camara, 1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.depthMask(false);
      Vec3 o0 = ORIGEN.computeIfAbsent(m.getId(), k -> new Vec3(m.getX(), m.getY(), m.getZ()));
      Vector3f haciaOrigen = new Vector3f((float)(o0.x + 0.0 - cam.x - h.x + 0.0), (float)(o0.y + 0.5 * r - cam.y - h.y), (float)(o0.z - cam.z - h.z));
      float largoRastro = Math.min(600.0F, Math.max(r * 8.0F, haciaOrigen.length()));
      Vector3f dirRastro = haciaOrigen.length() > r * 4.0F ? haciaOrigen.normalize(new Vector3f()).negate() : new Vector3f(dir);
      Vec3 mundo = p.add(cam);
      MeteoritosCliente.Estela es = ESTELAS.computeIfAbsent(m.getId(), k -> new MeteoritosCliente.Estela(new Vec3(o0.x, o0.y + 0.5 * r, o0.z), r, semilla));
      es.fin = mundo;
      usar(2.0F, t, semilla, true);
      cartel(h, r * 7.0F, camara, 1.0F, 1.0F, 1.0F, 1.0F);
      usar(15.0F, t, semilla, true);
      Vector3f ex = Math.abs(dirRastro.y) > 0.9F ? new Vector3f(1.0F, 0.0F, 0.0F) : new Vector3f(0.0F, 1.0F, 0.0F);
      Vector3f e1 = dirRastro.cross(ex, new Vector3f()).normalize();
      Vector3f e2 = dirRastro.cross(e1, new Vector3f()).normalize();

      for (int i = 0; i < 18; i++) {
         float h1 = frac(i * 0.6180339F + semilla * 0.137F);
         float h2 = frac(i * 0.7548776F + 0.31F + semilla * 0.071F);
         float h3 = frac(i * 0.5698403F + 0.77F);
         float vel = 0.35F + 0.65F * h2;
         float ciclo = frac(t * 0.28F * vel + h1);
         float d = r * (1.2F + 9.0F * ciclo * (0.4F + 0.6F * h3));
         float ang = h1 * 6.2832F + h3 * 3.0F;
         float lado = r * (0.6F + 1.6F * ciclo) * (0.3F + 0.9F * h2);
         Vector3f pos = new Vector3f(h)
            .add(new Vector3f(dirRastro).mul(d))
            .add(new Vector3f(e1).mul((float)Math.cos(ang) * lado))
            .add(new Vector3f(e2).mul((float)Math.sin(ang) * lado));
         float parpadeo = 0.55F + 0.45F * (float)Math.sin(t * (9.0F + 14.0F * h3) + i * 2.1F);
         float a = (1.0F - ciclo) * (1.0F - ciclo) * parpadeo;
         float tamChispa = r * (0.05F + 0.1F * h2) * (1.0F - 0.6F * ciclo);
         cartel(pos, tamChispa * 3.0F, camara, 1.0F, 0.55F + 0.35F * (1.0F - ciclo), 0.18F + 0.4F * (1.0F - ciclo) * (1.0F - ciclo), Math.min(1.0F, a * 1.4F));
      }
   }

   private static void humo(MeteoritosCliente.Estela e, Vec3 cam, float t, boolean bruma) {
      Vector3f fin = new Vector3f((float)(e.fin.x - cam.x), (float)(e.fin.y - cam.y), (float)(e.fin.z - cam.z));
      Vector3f atras = new Vector3f((float)(e.origen.x - cam.x), (float)(e.origen.y - cam.y), (float)(e.origen.z - cam.z)).sub(fin);
      float largo = Math.min(600.0F, Math.max(e.r * 8.0F, atras.length()));
      if (!(atras.lengthSquared() < 1.0E-4F)) {
         Vector3f cola = atras.normalize(new Vector3f());
         float edad = e.fija < 0L ? 0.0F : (float)(System.nanoTime() - e.fija) / 1.0E9F;
         float fade = e.fija < 0L ? 1.0F : (float)(1.0 - smooth01((edad - 3.0) / 13.0));
         if (!(fade < 0.01F)) {
            ShaderInstance sh = usar(bruma ? 17.0F : 16.0F, t, e.semilla, false);
            float delante = e.r * 2.6F;
            Vector3f cabeza = new Vector3f(fin).sub(new Vector3f(cola).mul(delante));
            largo += delante;
            sh.safeGetUniform("HumoA").set(cabeza.x, cabeza.y, cabeza.z, largo);
            sh.safeGetUniform("HumoB").set(cola.x, cola.y, cola.z, e.r);
            sh.safeGetUniform("HumoC").set(fade, e.semilla, edad, delante);
            float rm = e.r * 11.5F * (1.0F + 0.04F * edad) * 1.02F * (bruma ? 2.6F : 1.0F);
            Vector3f dirMov = new Vector3f(cola).negate();
            tubo(cabeza, dirMov, largo, 2, 24, 1.0F, largo, d -> rm, d -> 1.0);
            tapas(cabeza, cola, largo, rm);
         }
      }
   }

   private static void funda(MeteoritosCliente.Estela e, Vec3 cam, float t) {
      Vector3f fin = new Vector3f((float)(e.fin.x - cam.x), (float)(e.fin.y - cam.y), (float)(e.fin.z - cam.z));
      Vector3f atras = new Vector3f((float)(e.origen.x - cam.x), (float)(e.origen.y - cam.y), (float)(e.origen.z - cam.z)).sub(fin);
      float largo = Math.min(Math.min(600.0F, e.r * 150.0F), Math.max(e.r * 10.0F, atras.length()));
      Vector3f dir = atras.lengthSquared() > 1.0E-4F ? atras.normalize(new Vector3f()).negate() : new Vector3f(0.0F, -1.0F, 0.0F);
      float edad = e.fija < 0L ? 0.0F : (float)(System.nanoTime() - e.fija) / 1.0E9F;
      double vivo = e.fija < 0L ? 1.0 : Math.max(0.0, 1.0 - smooth01(edad / 2.5F));
      double crece = 1.0 + 0.12 * Math.min(edad / 2.5F, 1.0F);
      float r = e.r;
      float lt = largo + r * 2.0F;
      if (!(vivo < 0.01)) {
         float punta = r * 3.0F;
         float total = punta + Math.min(900.0F, Math.max(r * 6.0F, atras.length()));
         ShaderInstance sh = usar(17.0F, t, e.semilla, false);
         RenderSystem.blendFunc(SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_ALPHA);
         float largoDetras = total - punta;
         Vector3f atrasDir = new Vector3f(dir).negate();
         sh.safeGetUniform("HumoA").set(fin.x, fin.y, fin.z, largoDetras);
         sh.safeGetUniform("HumoB").set(atrasDir.x, atrasDir.y, atrasDir.z, r);
         sh.safeGetUniform("HumoC").set((float)vivo, e.semilla, (float)crece, 3.0F + largoDetras / r);
         float fw = 5.4F * r;
         float rc = r * (6.2F * (float)crece + 3.6F) * 1.03F;
         float lc = fw + largoDetras + r;
         Vector3f ini = new Vector3f(fin).add(new Vector3f(dir).mul(fw));
         tubo(ini, dir, lc, 2, 64, 1.0F, lc, d -> rc * 1.06F, d -> 1.0);
         tapas(ini, atrasDir, lc, rc * 1.06F);
      }
   }

   private static float frac(float x) {
      return x - (float)Math.floor(x);
   }

   private static double smooth01(double x) {
      double c = Math.max(0.0, Math.min(1.0, x));
      return c * c * (3.0 - 2.0 * c);
   }

   private static void esfera(Vector3f c, float r, int nu, int nv, float sem) {
      BufferBuilder b = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

      for (int i = 0; i < nu; i++) {
         for (int j = 0; j < nv; j++) {
            vertEsfera(b, c, r, i, j, nu, nv, sem);
            vertEsfera(b, c, r, i + 1, j, nu, nv, sem);
            vertEsfera(b, c, r, i + 1, j + 1, nu, nv, sem);
            vertEsfera(b, c, r, i, j + 1, nu, nv, sem);
         }
      }

      BufferUploader.drawWithShader(b.buildOrThrow());
   }

   private static double irregular(double th, double ph, float sem) {
      double x = Math.sin(ph) * Math.cos(th);
      double y = Math.cos(ph);
      double z = Math.sin(ph) * Math.sin(th);
      return 1.0;
   }

   private static Vector3f puntoRoca(double th, double ph, float r, float sem) {
      double k = r * irregular(th, ph, sem);
      return new Vector3f((float)(Math.sin(ph) * Math.cos(th) * k), (float)(Math.cos(ph) * k), (float)(Math.sin(ph) * Math.sin(th) * k));
   }

   private static void vertEsfera(BufferBuilder b, Vector3f c, float r, int i, int j, int nu, int nv, float sem) {
      double th = (double)i / nu * Math.PI * 2.0;
      double ph = Math.min(Math.max((double)j / nv * Math.PI, 0.001), 3.1405926535897932);
      Vector3f p = puntoRoca(th, ph, r, sem);
      double e = 0.001;
      Vector3f dth = puntoRoca(th + e, ph, r, sem).sub(puntoRoca(th - e, ph, r, sem));
      Vector3f dph = puntoRoca(th, ph + e, r, sem).sub(puntoRoca(th, ph - e, r, sem));
      Vector3f n = dth.cross(dph, new Vector3f());
      if (n.lengthSquared() < 1.0E-12F) {
         n.set(p);
      }

      n.normalize();
      if (n.dot(p) < 0.0F) {
         n.negate();
      }

      b.addVertex(c.x + p.x, c.y + p.y, c.z + p.z).setColor(n.x * 0.5F + 0.5F, n.y * 0.5F + 0.5F, n.z * 0.5F + 0.5F, 1.0F).setUv((float)i / nu, (float)j / nv);
   }

   private static void tubo(
      Vector3f cabeza, Vector3f dir, float largo, int nu, int nv, float potencia, float escalaUV, DoubleUnaryOperator radio, DoubleUnaryOperator alfa
   ) {
      Vector3f a = new Vector3f(dir).mul(-1.0F);
      Vector3f e1 = new Vector3f(a).cross(Math.abs(a.y) > 0.9F ? new Vector3f(1.0F, 0.0F, 0.0F) : new Vector3f(0.0F, 1.0F, 0.0F)).normalize();
      Vector3f e2 = new Vector3f(a).cross(e1);
      BufferBuilder b = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

      for (int i = 0; i < nu; i++) {
         for (int j = 0; j < nv; j++) {
            vertTubo(b, cabeza, a, e1, e2, largo, potencia, escalaUV, radio, alfa, i, j, nu, nv);
            vertTubo(b, cabeza, a, e1, e2, largo, potencia, escalaUV, radio, alfa, i + 1, j, nu, nv);
            vertTubo(b, cabeza, a, e1, e2, largo, potencia, escalaUV, radio, alfa, i + 1, j + 1, nu, nv);
            vertTubo(b, cabeza, a, e1, e2, largo, potencia, escalaUV, radio, alfa, i, j + 1, nu, nv);
         }
      }

      BufferUploader.drawWithShader(b.buildOrThrow());
   }

   private static void vertTubo(
      BufferBuilder b,
      Vector3f cabeza,
      Vector3f a,
      Vector3f e1,
      Vector3f e2,
      float largo,
      float potencia,
      float escalaUV,
      DoubleUnaryOperator radio,
      DoubleUnaryOperator alfa,
      int i,
      int j,
      int nu,
      int nv
   ) {
      double tt = (double)i / nu;
      double d = largo * Math.pow(tt, potencia);
      double ph = (double)j / nv * Math.PI * 2.0;
      double r = radio.applyAsDouble(d);
      double eps = Math.max(0.001, largo * 0.001);
      double dr = (radio.applyAsDouble(Math.min(largo, d + eps)) - radio.applyAsDouble(Math.max(0.0, d - eps)))
         / (Math.min(largo, d + eps) - Math.max(0.0, d - eps));
      float cx = (float)Math.cos(ph);
      float sx = (float)Math.sin(ph);
      Vector3f radial = new Vector3f(e1).mul(cx).add(new Vector3f(e2).mul(sx));
      Vector3f pos = new Vector3f(cabeza).add(new Vector3f(a).mul((float)d)).add(new Vector3f(radial).mul((float)r));
      Vector3f n = new Vector3f(radial).sub(new Vector3f(a).mul((float)dr)).normalize();
      b.addVertex(pos.x, pos.y, pos.z)
         .setColor(n.x * 0.5F + 0.5F, n.y * 0.5F + 0.5F, n.z * 0.5F + 0.5F, (float)alfa.applyAsDouble(d))
         .setUv((float)(d / escalaUV), (float)j / nv);
   }

   private static void tapas(Vector3f cabeza, Vector3f cola, float largo, float rm) {
      Vector3f e1 = new Vector3f(cola).cross(Math.abs(cola.y) > 0.9F ? new Vector3f(1.0F, 0.0F, 0.0F) : new Vector3f(0.0F, 1.0F, 0.0F)).normalize();
      Vector3f e2 = new Vector3f(cola).cross(e1);
      BufferBuilder b = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
      int n = 64;

      for (int k = 0; k < 2; k++) {
         Vector3f c = new Vector3f(cabeza).add(new Vector3f(cola).mul(k == 0 ? 0.0F : largo));

         for (int j = 0; j < n; j++) {
            double a0 = (Math.PI * 2) * j / n;
            double a1 = (Math.PI * 2) * (j + 1) / n;
            Vector3f p0 = new Vector3f(c).add(new Vector3f(e1).mul((float)Math.cos(a0) * rm)).add(new Vector3f(e2).mul((float)Math.sin(a0) * rm));
            Vector3f p1 = new Vector3f(c).add(new Vector3f(e1).mul((float)Math.cos(a1) * rm)).add(new Vector3f(e2).mul((float)Math.sin(a1) * rm));
            b.addVertex(c.x, c.y, c.z).setColor(0.5F, 0.5F, 1.0F, 1.0F).setUv(0.0F, 0.0F);
            b.addVertex(p0.x, p0.y, p0.z).setColor(0.5F, 0.5F, 1.0F, 1.0F).setUv(0.0F, 0.0F);
            b.addVertex(p1.x, p1.y, p1.z).setColor(0.5F, 0.5F, 1.0F, 1.0F).setUv(0.0F, 0.0F);
            b.addVertex(c.x, c.y, c.z).setColor(0.5F, 0.5F, 1.0F, 1.0F).setUv(0.0F, 0.0F);
         }
      }

      BufferUploader.drawWithShader(b.buildOrThrow());
   }

   private static void gota(Vector3f cabeza, Vector3f dir, float largo, float ancho) {
      Vector3f cola = new Vector3f(dir).mul(-largo).add(cabeza);
      Vector3f medio = new Vector3f(cabeza).add(cola).mul(0.5F);
      Vector3f lado = new Vector3f(dir).cross(medio);
      if (lado.lengthSquared() < 1.0E-6F) {
         lado.set(1.0F, 0.0F, 0.0F);
      }

      lado.normalize().mul(ancho);
      BufferBuilder b = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
      b.addVertex(cabeza.x - lado.x, cabeza.y - lado.y, cabeza.z - lado.z).setColor(1.0F, 1.0F, 1.0F, 1.0F).setUv(0.0F, 0.0F);
      b.addVertex(cola.x - lado.x, cola.y - lado.y, cola.z - lado.z).setColor(1.0F, 1.0F, 1.0F, 1.0F).setUv(1.0F, 0.0F);
      b.addVertex(cola.x + lado.x, cola.y + lado.y, cola.z + lado.z).setColor(1.0F, 1.0F, 1.0F, 1.0F).setUv(1.0F, 1.0F);
      b.addVertex(cabeza.x + lado.x, cabeza.y + lado.y, cabeza.z + lado.z).setColor(1.0F, 1.0F, 1.0F, 1.0F).setUv(0.0F, 1.0F);
      BufferUploader.drawWithShader(b.buildOrThrow());
   }

   private static void cinta(Vector3f cabeza, Vector3f dir, float largo, float ancho, float alfa) {
      Vector3f cola = new Vector3f(dir).mul(-largo).add(cabeza);
      Vector3f medio = new Vector3f(cabeza).add(cola).mul(0.5F);
      Vector3f lado = new Vector3f(dir).cross(medio);
      if (lado.lengthSquared() < 1.0E-6F) {
         lado.set(1.0F, 0.0F, 0.0F);
      }

      lado.normalize();
      BufferBuilder b = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
      int tramos = 12;

      for (int i = 0; i < tramos; i++) {
         float u0 = (float)i / tramos;
         float u1 = (float)(i + 1) / tramos;
         Vector3f a = new Vector3f(dir).mul(-largo * u0).add(cabeza);
         Vector3f c = new Vector3f(dir).mul(-largo * u1).add(cabeza);
         float w0 = ancho * (0.55F + 0.45F * (1.0F - u0));
         float w1 = ancho * (0.55F + 0.45F * (1.0F - u1));
         b.addVertex(a.x - lado.x * w0, a.y - lado.y * w0, a.z - lado.z * w0).setColor(1.0F, 1.0F, 1.0F, alfa).setUv(u0, 0.0F);
         b.addVertex(c.x - lado.x * w1, c.y - lado.y * w1, c.z - lado.z * w1).setColor(1.0F, 1.0F, 1.0F, alfa).setUv(u1, 0.0F);
         b.addVertex(c.x + lado.x * w1, c.y + lado.y * w1, c.z + lado.z * w1).setColor(1.0F, 1.0F, 1.0F, alfa).setUv(u1, 1.0F);
         b.addVertex(a.x + lado.x * w0, a.y + lado.y * w0, a.z + lado.z * w0).setColor(1.0F, 1.0F, 1.0F, alfa).setUv(u0, 1.0F);
      }

      BufferUploader.drawWithShader(b.buildOrThrow());
   }

   private static void cartel(Vector3f c, float r, Camera cam, float cr, float cg, float cb, float a) {
      Vector3f iz = new Vector3f(cam.getLeftVector()).mul(r);
      Vector3f ar = new Vector3f(cam.getUpVector()).mul(r);
      BufferBuilder b = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
      b.addVertex(c.x + iz.x - ar.x, c.y + iz.y - ar.y, c.z + iz.z - ar.z).setColor(cr, cg, cb, a).setUv(0.0F, 0.0F);
      b.addVertex(c.x - iz.x - ar.x, c.y - iz.y - ar.y, c.z - iz.z - ar.z).setColor(cr, cg, cb, a).setUv(1.0F, 0.0F);
      b.addVertex(c.x - iz.x + ar.x, c.y - iz.y + ar.y, c.z - iz.z + ar.z).setColor(cr, cg, cb, a).setUv(1.0F, 1.0F);
      b.addVertex(c.x + iz.x + ar.x, c.y + iz.y + ar.y, c.z + iz.z + ar.z).setColor(cr, cg, cb, a).setUv(0.0F, 1.0F);
      BufferUploader.drawWithShader(b.buildOrThrow());
   }

   private static void plano(float r, float g, float bl, float a, boolean sumar) {
      Matrix4f proy = new Matrix4f(RenderSystem.getProjectionMatrix());
      VertexSorting orden = RenderSystem.getVertexSorting();
      Matrix4fStack mv = RenderSystem.getModelViewStack();
      mv.pushMatrix();
      mv.identity();
      RenderSystem.applyModelViewMatrix();
      RenderSystem.setProjectionMatrix(new Matrix4f(), VertexSorting.ORTHOGRAPHIC_Z);
      RenderSystem.disableDepthTest();
      usar(6.0F, 0.0F, 0.0F, sumar);
      BufferBuilder b = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
      b.addVertex(-1.0F, -1.0F, 0.0F).setColor(r, g, bl, a).setUv(0.0F, 0.0F);
      b.addVertex(1.0F, -1.0F, 0.0F).setColor(r, g, bl, a).setUv(1.0F, 0.0F);
      b.addVertex(1.0F, 1.0F, 0.0F).setColor(r, g, bl, a).setUv(1.0F, 1.0F);
      b.addVertex(-1.0F, 1.0F, 0.0F).setColor(r, g, bl, a).setUv(0.0F, 1.0F);
      BufferUploader.drawWithShader(b.buildOrThrow());
      RenderSystem.enableDepthTest();
      mv.popMatrix();
      RenderSystem.applyModelViewMatrix();
      RenderSystem.setProjectionMatrix(proy, orden);
   }

   private static final class Bocanada {
      final double x0;
      final double y0;
      final double z0;
      final float vx;
      final float vy;
      final float vz;
      final float tam0;
      final float tam1;
      final float calor0;
      final float osc;
      final float tauCalor;
      final float semilla;
      final float opacidad;
      final float dura;
      int met = -1;
      long fin = 0L;
      boolean estela;
      final long nace = System.nanoTime();

      Bocanada(
         double x,
         double y,
         double z,
         float vx,
         float vy,
         float vz,
         float tam0,
         float tam1,
         float calor0,
         float osc,
         float tauCalor,
         float semilla,
         float opacidad,
         float dura
      ) {
         this.dura = dura;
         this.x0 = x;
         this.y0 = y;
         this.z0 = z;
         this.vx = vx;
         this.vy = vy;
         this.vz = vz;
         this.tam0 = tam0;
         this.tam1 = tam1;
         this.calor0 = calor0;
         this.osc = osc;
         this.tauCalor = tauCalor;
         this.semilla = semilla;
         this.opacidad = opacidad;
      }

      float edad() {
         return (float)(System.nanoTime() - this.nace) / 1.0E9F;
      }

      void aniadir(List<float[]> lista, Vec3 cam) {
         float e = this.edad();
         float alfa = Mth.clamp(e / 0.15F, 0.0F, 1.0F) * (1.0F - MeteoritosCliente.suave((e - this.dura * 0.55F) / (this.dura * 0.45F))) * this.opacidad;
         if (!(alfa <= 0.004F)) {
            if (this.estela) {
               float g = Mth.clamp(e / 6.0F, 0.0F, 1.0F);
               float f = 0.55F * Mth.clamp(e / 10.0F, 0.0F, 1.0F);
               float ap = Mth.clamp(e / 0.06F, 0.0F, 1.0F);
               if (this.fin != 0L) {
                  ap *= 1.0F - MeteoritosCliente.suave((float)(System.nanoTime() - this.fin) / 3.0E9F);
               }

               if (!(ap <= 0.004F)) {
                  lista.add(
                     new float[]{
                        (float)(this.x0 + this.vx * e - cam.x),
                        (float)(this.y0 + this.vy * e - cam.y),
                        (float)(this.z0 + this.vz * e - cam.z),
                        this.tam0 + (this.tam1 - this.tam0) * (float)Math.sqrt(g),
                        f,
                        this.semilla,
                        0.0F,
                        ap * this.opacidad,
                        1.0F
                     }
                  );
               }
            } else {
               float tam = this.tam0 + (this.tam1 - this.tam0) * (1.0F - (float)Math.exp(-e / 6.5));
               float calor = this.calor0 * (float)Math.exp(-e / this.tauCalor);
               float oscuro = Math.min(1.0F, this.osc + 0.45F * MeteoritosCliente.suave((e - 4.0F) / 18.0F));
               lista.add(
                  new float[]{
                     (float)(this.x0 + this.vx * e - cam.x),
                     (float)(this.y0 + this.vy * e - cam.y),
                     (float)(this.z0 + this.vz * e - cam.z),
                     tam,
                     calor,
                     this.semilla,
                     oscuro,
                     alfa
                  }
               );
            }
         }
      }
   }

   private static final class Estela {
      final Vec3 origen;
      Vec3 fin;
      final float r;
      final float semilla;
      long fija = -1L;

      Estela(Vec3 origen, float r, float semilla) {
         this.origen = origen;
         this.fin = origen;
         this.r = r;
         this.semilla = semilla;
      }
   }

   private static final class Impacto {
      final Vec3 centro;
      final long nace = System.nanoTime();
      final float escala;
      final float semilla;
      final float lx;
      final float lz;
      final float[][] domo;
      final float[][] suelo;
      final float[][] columna;
      final float[][] brasas;

      Impacto(Vec3 centro, float escala) {
         this.centro = centro;
         this.escala = Math.max(0.5F, escala);
         Random r = new Random(Double.doubleToLongBits(centro.x * 31.0 + centro.z * 17.0) ^ System.nanoTime());
         this.semilla = r.nextFloat() * 50.0F;
         double inc = r.nextDouble() * Math.PI * 2.0;
         this.lx = (float)Math.cos(inc) * 0.28F;
         this.lz = (float)Math.sin(inc) * 0.28F;
         this.domo = new float[20][];

         for (int i = 0; i < this.domo.length; i++) {
            double u = 0.1 + 0.9 * r.nextDouble();
            double th = r.nextDouble() * Math.PI * 2.0;
            double sq = Math.sqrt(1.0 - u * u);
            float fr = (float)Math.cbrt(r.nextDouble());
            this.domo[i] = new float[]{(float)(sq * Math.cos(th)), (float)u, (float)(sq * Math.sin(th)), fr, 14.0F + r.nextFloat() * 10.0F, r.nextFloat()};
         }

         this.suelo = new float[26][];

         for (int i = 0; i < this.suelo.length; i++) {
            this.suelo[i] = new float[]{
               r.nextFloat() * 6.283F, 0.3F + 0.7F * (float)Math.sqrt(r.nextFloat()), r.nextFloat() * 3.0F, 16.0F + r.nextFloat() * 12.0F, r.nextFloat()
            };
         }

         this.columna = new float[24][];

         for (int i = 0; i < this.columna.length; i++) {
            this.columna[i] = new float[]{
               (i + r.nextFloat()) / 24.0F, r.nextFloat() * 6.283F, (float)Math.sqrt(r.nextFloat()), 8.0F + r.nextFloat() * 6.0F, r.nextFloat()
            };
         }

         this.brasas = new float[70][];

         for (int i = 0; i < this.brasas.length; i++) {
            double th = r.nextDouble() * Math.PI * 2.0;
            double d = 4.0 + 66.0 * Math.sqrt(r.nextDouble());
            this.brasas[i] = new float[]{
               (float)(Math.cos(th) * d),
               1.0F + r.nextFloat() * 30.0F,
               (float)(Math.sin(th) * d),
               1.0F + r.nextFloat() * 3.0F,
               r.nextFloat() * 6.28F,
               0.5F + r.nextFloat()
            };
         }
      }

      float edad() {
         return (float)(System.nanoTime() - this.nace) / 1.0E9F;
      }

      private float alfa(float e) {
         return Mth.clamp(e / 0.2F, 0.0F, 1.0F) * (1.0F - MeteoritosCliente.suave((e - 13.0F) / 11.0F));
      }

      void fondo(Vec3 cam, Camera camara, float t) {
         float e = this.edad();
         float s = this.escala;
         float cx = (float)(this.centro.x - cam.x);
         float cy = (float)(this.centro.y - cam.y);
         float cz = (float)(this.centro.z - cam.z);
         RenderSystem.depthMask(false);
         RenderSystem.disableCull();
         float onda = 120.0F * s * (1.0F - (float)Math.exp(-e / 1.2));
         float ondaAlfa = (1.0F - MeteoritosCliente.suave(e / 3.4F)) * 0.8F;
         if (ondaAlfa > 0.01F) {
            MeteoritosCliente.usar(7.0F, t, this.semilla, false);
            anilloSuelo(cx, cy + 0.6F, cz, Math.max(0.0F, onda - 10.0F * s), onda, ondaAlfa);
         }

         float bolaLuz = (1.0F - MeteoritosCliente.suave((e - 0.3F) / 3.2F)) * Mth.clamp(e / 0.08F, 0.0F, 1.0F);
         if (bolaLuz > 0.01F) {
            MeteoritosCliente.usar(8.0F, t, this.semilla, true);
            float rb = (14.0F + 46.0F * (1.0F - (float)Math.exp(-e / 0.6))) * s;
            MeteoritosCliente.cartel(new Vector3f(cx, cy + rb * 0.3F, cz), rb, camara, 1.0F, 1.0F, 1.0F, bolaLuz);
         }
      }

      void bocanadas(List<float[]> lista, Vec3 cam) {
         float e = this.edad();
         float s = this.escala;
         float al = this.alfa(e);
         if (!(al <= 0.004F)) {
            float cx = (float)(this.centro.x - cam.x);
            float cy = (float)(this.centro.y - cam.y);
            float cz = (float)(this.centro.z - cam.z);
            float calor = (float)Math.exp(-e / 9.0);
            float osc = 0.04F + 0.32F * MeteoritosCliente.suave(e / 20.0F);
            float radio = 6.0F + 20.0F * s * (1.0F - (float)Math.exp(-e / 3.2F));

            for (float[] q : this.domo) {
               float rr = radio * q[3];
               lista.add(
                  new float[]{
                     cx + q[0] * rr,
                     cy + 2.0F + q[1] * rr * 0.55F + e * 0.2F,
                     cz + q[2] * rr,
                     q[4] * s * (0.55F + 0.02F * e),
                     calor * (1.0F - 0.5F * q[3]),
                     q[5],
                     osc,
                     al * 0.16F
                  }
               );
            }

            float rg = 6.0F + 28.0F * s * (1.0F - (float)Math.exp(-e / 2.4F));

            for (float[] q : this.suelo) {
               float rr = rg * q[1];
               lista.add(
                  new float[]{
                     cx + Mth.cos(q[0] + e * 0.03F) * rr,
                     cy + 2.0F + q[2] * s * 0.6F,
                     cz + Mth.sin(q[0] + e * 0.03F) * rr,
                     q[3] * s * (0.5F + 0.02F * e),
                     calor * 0.5F,
                     q[4],
                     osc + 0.05F,
                     al * 0.17F
                  }
               );
            }

            float alto = 0.0F;

            for (float[] q : new float[0][]) {
               float hh = alto * q[0];
               float ancho = (3.0F + 12.0F * q[0]) * s * q[2];
               float giro = q[1] + e * (0.15F + 0.15F * q[0]);
               lista.add(
                  new float[]{
                     cx + this.lx * hh + Mth.cos(giro) * ancho,
                     cy + hh,
                     cz + this.lz * hh + Mth.sin(giro) * ancho,
                     q[3] * s * (0.9F + 0.9F * q[0]),
                     calor * (1.0F - 0.55F * q[0]),
                     q[4],
                     0.1F + 0.5F * q[0] * q[0],
                     al * 0.58F
                  }
               );
            }

            float fb = al * (1.0F - MeteoritosCliente.suave((e - 12.0F) / 10.0F)) * Mth.clamp(e / 0.8F, 0.0F, 1.0F);
            if (fb > 0.01F) {
               for (float[] q : this.brasas) {
                  float par = 0.55F + 0.45F * Mth.sin(e * 5.0F * q[5] + q[4]);
                  float alt = q[1] + q[3] * e;
                  lista.add(
                     new float[]{
                        cx + q[0] + Mth.sin(e * 0.7F + q[4]) * 2.0F,
                        cy + alt,
                        cz + q[2] + Mth.cos(e * 0.6F + q[4]) * 2.0F,
                        0.3F * (0.6F + 0.6F * q[5]),
                        1.0F,
                        q[4] * 0.1F,
                        0.0F,
                        fb * par
                     }
                  );
               }
            }
         }
      }

      private static void anilloSuelo(float cx, float cy, float cz, float r0, float r1, float a) {
         BufferBuilder b = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
         int n = 64;

         for (int i = 0; i < n; i++) {
            float a0 = (float) (Math.PI * 2) * i / n;
            float a1 = (float) (Math.PI * 2) * (i + 1) / n;
            float u0 = (float)i / n;
            float u1 = (float)(i + 1) / n;
            b.addVertex(cx + Mth.cos(a0) * r0, cy, cz + Mth.sin(a0) * r0).setColor(1.0F, 1.0F, 1.0F, a).setUv(u0, 0.0F);
            b.addVertex(cx + Mth.cos(a1) * r0, cy, cz + Mth.sin(a1) * r0).setColor(1.0F, 1.0F, 1.0F, a).setUv(u1, 0.0F);
            b.addVertex(cx + Mth.cos(a1) * r1, cy + 1.5F, cz + Mth.sin(a1) * r1).setColor(1.0F, 1.0F, 1.0F, a).setUv(u1, 1.0F);
            b.addVertex(cx + Mth.cos(a0) * r1, cy + 1.5F, cz + Mth.sin(a0) * r1).setColor(1.0F, 1.0F, 1.0F, a).setUv(u0, 1.0F);
         }

         BufferUploader.drawWithShader(b.buildOrThrow());
      }
   }
}
