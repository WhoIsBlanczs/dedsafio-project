package com.supernova.client;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexSorting;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.logging.LogUtils;
import com.supernova.SupernovaState;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.ruleta.client.OpcionesCliente;
import net.ruleta.client.compat.RenderLevelStageEvent;
import net.ruleta.client.compat.SubscribeEvent;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;

public class SupernovaRenderHandler {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static long lastErrLog;
   private static long lastNullLog;
   public static boolean dbgFlip;
   public static boolean dbgNoCull;
   public static boolean dbgNoDepth;
   public static boolean dbgMagenta;
   public static boolean dbgNoChain;
   private static final int ALPHA = 0;
   private static final int ADD = 1;
   private static final int MUL = 2;
   private static float rayNear;
   private static final float JET_GIRO = 0.32F;
   private static final float JET_INCLINA = 42.0F;
   private static final float JET_VAIVEN = 12.0F;
   private static TextureTarget profCopia;
   public static final float BLANCO_FIJO = 120.0F;
   public static final float BLANCO_FUNDE = 210.0F;

   @SubscribeEvent
   public static void onRender(RenderLevelStageEvent e) {
      if (e.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
         Minecraft mc = Minecraft.getInstance();
         if (mc.level != null) {
            if (ClientSetup.getSphere() != null && ClientSetup.getAdditive() != null && ClientSetup.getMultiply() != null) {
               float pt = e.getPartialTick();
               float now = (float)mc.level.getGameTime() + pt;
               List<SupernovaState> list = SupernovaGhosts.active(now);
               if (!list.isEmpty()) {
                  ScreenEffectHandler.compute(pt);
                  Camera camera = e.getCamera();
                  Vec3 cam = camera.getPosition();
                  Matrix4f view = new Matrix4f()
                     .rotateX(camera.getXRot() * (float) (Math.PI / 180.0))
                     .rotateY((camera.getYRot() + 180.0F) * (float) (Math.PI / 180.0));
                  Matrix4f proj = new Matrix4f(e.getProjectionMatrix());
                  float far = mc.options.getEffectiveRenderDistance() * 16.0F * 4.0F;
                  Matrix4f prevProj = new Matrix4f(RenderSystem.getProjectionMatrix());
                  VertexSorting prevSort = RenderSystem.getVertexSorting();
                  Matrix4fStack mv = RenderSystem.getModelViewStack();
                  mc.getMainRenderTarget().bindWrite(true);
                  RenderSystem.setProjectionMatrix(proj, VertexSorting.DISTANCE_TO_ORIGIN);
                  mv.pushMatrix();
                  mv.identity();
                  RenderSystem.applyModelViewMatrix();
                  RenderSystem.depthFunc(515);
                  RenderSystem.depthMask(false);

                  try {
                     float skyTime = ((float)(mc.level.getGameTime() % 72000L) + pt) / 20.0F;
                     if (!dbgNoChain) {
                        worldTint(proj);
                        skyDome(view, far, skyTime, cam.y);
                        godRays(proj, view);
                     }

                     rayNear = 0.0F;

                     for (SupernovaState n : list) {
                        drawSupernova(n, cam, view, far, now);
                     }

                     if (!dbgNoChain) {
                        overlays(proj, view);
                     }
                  } catch (RuntimeException ex) {
                     if (System.nanoTime() - lastErrLog > 2000000000L) {
                        lastErrLog = System.nanoTime();
                        LOGGER.error("Supernova: error al dibujar", ex);
                     }
                  } finally {
                     int glErr = GL11.glGetError();
                     if (glErr != 0 && System.nanoTime() - lastErrLog > 2000000000L) {
                        lastErrLog = System.nanoTime();
                        LOGGER.warn("Supernova: error de OpenGL 0x{} tras dibujar", Integer.toHexString(glErr));
                     }

                     RenderSystem.depthMask(true);
                     RenderSystem.enableDepthTest();
                     RenderSystem.enableCull();
                     RenderSystem.disableBlend();
                     RenderSystem.defaultBlendFunc();
                     mv.popMatrix();
                     RenderSystem.applyModelViewMatrix();
                     RenderSystem.setProjectionMatrix(prevProj, prevSort);
                  }
               }
            } else {
               if (System.nanoTime() - lastNullLog > 5000000000L) {
                  lastNullLog = System.nanoTime();
                  LOGGER.warn("Supernova: los shaders no se cargaron, no se dibuja nada (revisa el log al iniciar)");
               }
            }
         }
      }
   }

   private static ShaderInstance use(int blend) {
      RenderSystem.enableBlend();
      Supplier<ShaderInstance> sup;
      switch (blend) {
         case 1:
            sup = ClientSetup::getAdditive;
            RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
            break;
         case 2:
            sup = ClientSetup::getMultiply;
            RenderSystem.blendFunc(SourceFactor.DST_COLOR, DestFactor.ZERO);
            break;
         default:
            sup = ClientSetup::getSphere;
            RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
      }

      RenderSystem.setShader(sup);
      ShaderInstance sh = sup.get();
      sh.safeGetUniform("Debug").set(dbgMagenta ? 1.0F : 0.0F);
      sh.safeGetUniform("Fade").set(1.0F);
      sh.safeGetUniform("Intensity").set(1.0F);
      sh.safeGetUniform("Charge").set(0.0F);
      sh.safeGetUniform("Expl").set(0.0F);
      sh.safeGetUniform("SkyMix").set(0.0F);
      sh.safeGetUniform("SkyBright").set(0.0F);
      sh.safeGetUniform("SkyDark").set(0.0F);
      sh.safeGetUniform("SphereCenter").set(0.0F, 0.0F, 0.0F);
      sh.safeGetUniform("ViewDirW").set(0.0F, 0.0F, 1.0F);
      sh.safeGetUniform("Radius").set(1.0F);
      sh.safeGetUniform("Apoc").set(0.0F);
      sh.safeGetUniform("CamY").set(64.0F);
      sh.safeGetUniform("CloudQ").set(OpcionesCliente.supernovaNubes);
      return sh;
   }

   private static void depth(boolean test) {
      if (test && !dbgNoDepth) {
         RenderSystem.enableDepthTest();
      } else {
         RenderSystem.disableDepthTest();
      }
   }

   private static void worldTint(Matrix4f proj) {
      boolean mul = ScreenEffectHandler.mulR < 0.999F || ScreenEffectHandler.mulG < 0.999F || ScreenEffectHandler.mulB < 0.999F;
      boolean add = ScreenEffectHandler.addR + ScreenEffectHandler.addG + ScreenEffectHandler.addB > 0.003F;
      if (mul || add) {
         if (mul) {
            ShaderInstance sh = use(2);
            sh.safeGetUniform("Mode").set(6.0F);
            fullscreen(proj, ScreenEffectHandler.mulR, ScreenEffectHandler.mulG, ScreenEffectHandler.mulB, 1.0F);
         }

         if (add) {
            ShaderInstance sh = use(1);
            sh.safeGetUniform("Mode").set(6.0F);
            fullscreen(proj, ScreenEffectHandler.addR, ScreenEffectHandler.addG, ScreenEffectHandler.addB, 1.0F);
         }
      }
   }

   private static void skyDome(Matrix4f view, float far, float time, double camY) {
      if (!(ScreenEffectHandler.skyMix <= 0.002F)) {
         ShaderInstance sh = use(0);
         sh.safeGetUniform("Mode").set(3.0F);
         sh.safeGetUniform("Time").set(time);
         sh.safeGetUniform("SkyMix").set(ScreenEffectHandler.skyMix);
         sh.safeGetUniform("SkyBright").set(ScreenEffectHandler.skyBright);
         sh.safeGetUniform("SkyDark").set(ScreenEffectHandler.skyDark);
         sh.safeGetUniform("Intensity").set(ScreenEffectHandler.dia);
         sh.safeGetUniform("Charge").set(ScreenEffectHandler.naranja);
         sh.safeGetUniform("SphereCenter").set(ScreenEffectHandler.fogOutR, ScreenEffectHandler.fogOutG, ScreenEffectHandler.fogOutB);
         sh.safeGetUniform("Aparece").set(ScreenEffectHandler.aparece);
         sh.safeGetUniform("Apoc").set(ScreenEffectHandler.apoc);
         sh.safeGetUniform("CamY").set((float)camY);
         sh.safeGetUniform("StarDirW").set(ScreenEffectHandler.starX, ScreenEffectHandler.starY, ScreenEffectHandler.starZ);
         RenderSystem.disableCull();
         depth(true);
         drawSphere(view, far * 0.7F, true);
      }
   }

   private static void drawSupernova(SupernovaState n, Vec3 cam, Matrix4f view, float far, float now) {
      float fade = n.fade(now);
      if (!(fade <= 0.002F)) {
         float time = n.age(now) / 20.0F;
         Vec3 c = n.center();
         Vector3f cv = view.transformPosition(new Vector3f((float)(c.x - cam.x), (float)(c.y - cam.y), (float)(c.z - cam.z)));
         float dist = cv.length();
         if (!(dist < 0.001F)) {
            float maxD = far * 0.4F;
            float scale = dist > maxD ? maxD / dist : 1.0F;
            Vector3f cs = new Vector3f(cv).mul(scale);
            float dS = dist * scale;
            Vec3 toCam = cam.subtract(c);
            Vec3 vd = toCam.lengthSqr() < 1.0E-6 ? new Vec3(0.0, 0.0, 1.0) : toCam.normalize();
            Matrix4f sphereM = new Matrix4f().translation(cs).mul(view);
            if (!n.isBlasting(now)) {
               float charge = n.charge(now);
               float R = n.starRadius(now) * n.breath(now) * scale;
               if (dS > R * 1.02F) {
                  ShaderInstance sh = use(0);
                  setSphere(sh, 0.0F, time, charge, 0.0F, fade, cs, vd);
                  sh.safeGetUniform("SkyBright").set(n.activation(now));
                  if (dbgNoCull) {
                     RenderSystem.disableCull();
                  } else {
                     RenderSystem.enableCull();
                  }

                  depth(true);
                  drawSphere(sphereM, R, dbgFlip);
               }

               float gasR = R * 1.9F;
               if (dS > gasR * 1.02F) {
                  ShaderInstance gs = use(1);
                  setSphere(gs, 9.0F, time, charge, 0.0F, fade, cs, vd);
                  gs.safeGetUniform("Radius").set(R);
                  gs.safeGetUniform("Intensity").set(1.0F);
                  gs.safeGetUniform("SkyBright").set(n.activation(now));
                  RenderSystem.enableCull();
                  depth(true);
                  drawSphere(sphereM, gasR, false);
               }

               float k = SupernovaState.smooth(charge);
               float size = Mth.lerp(k, R * 4.4F, R * 1.8F);
               size = Math.max(size, dS * 0.22F);
               ShaderInstance sh = use(1);
               setSphere(sh, 4.0F, time, charge, 0.0F, fade, cs, vd);
               sh.safeGetUniform("Intensity").set(0.8F + 0.35F * charge);
               sh.safeGetUniform("SkyBright").set(n.activation(now));
               sh.safeGetUniform("Radius").set(Mth.clamp(R / size, 0.08F, 0.8F));
               RenderSystem.disableCull();
               depth(true);
               billboard(cs, size);
               float dk = SupernovaState.smooth(Mth.clamp((charge - 0.35F) / 0.65F, 0.0F, 1.0F));
               if (dk > 0.02F && dS > R * 1.02F) {
                  ShaderInstance fd = use(1);
                  setSphere(fd, 15.0F, time, dk, 0.0F, fade, cs, vd);
                  fd.safeGetUniform("Intensity").set(0.95F * dk * dk);
                  RenderSystem.disableCull();
                  depth(true);
                  billboard(cs, Mth.lerp(dk, R * 6.0F, R * 1.35F));
               }
            } else {
               float bt = n.blastTicks(now);
               float expl = SupernovaState.sat(bt / 30.0F);
               float rReal = n.radius(now) * OpcionesCliente.supernovaTamano;
               float R = rReal * scale;
               if (dS > R * 1.02F) {
                  ShaderInstance sh = use(0);
                  setSphere(sh, 2.0F, time, 1.0F, expl, fade, cs, vd);
                  if (dbgNoCull) {
                     RenderSystem.disableCull();
                  } else {
                     RenderSystem.enableCull();
                  }

                  depth(true);
                  drawSphere(sphereM, R, dbgFlip);
                  float shellR = R * 1.12F;
                  sh = use(1);
                  setSphere(sh, 7.0F, time, 1.0F, expl, fade, cs, vd);
                  sh.safeGetUniform("Radius").set(R);
                  sh.safeGetUniform("Intensity").set(SupernovaState.smooth(bt / 20.0F));
                  RenderSystem.enableCull();
                  depth(true);
                  drawSphere(sphereM, shellR, dS < shellR);
               }

               float jetI = SupernovaState.smooth(bt / 40.0F) * 1.0F;
               if (jetI > 0.002F) {
                  float secs = bt / 20.0F;
                  float ang = n.id * 2.39996F + secs * 0.32F;
                  float tilt = (42.0F + 12.0F * Mth.sin(secs * 0.45F)) * (float) (Math.PI / 180.0);
                  Vector3f axisW = new Vector3f(Mth.sin(tilt) * Mth.cos(ang), Mth.cos(tilt), Mth.sin(tilt) * Mth.sin(ang));
                  Vector3f axis = view.transformDirection(axisW).normalize();
                  float len = Math.min(Math.max(R * 3.0F, 40.0F * scale), far * 0.55F);
                  float width = Math.max(R * 0.9F, 8.0F * scale);
                  ShaderInstance sh = use(1);
                  setSphere(sh, 5.0F, time, 1.0F, expl, fade, cs, vd);
                  sh.safeGetUniform("Intensity").set(jetI);
                  RenderSystem.disableCull();
                  depth(true);
                  beam(cs, axis, len, width);
               }

               float big = 1.0F - SupernovaState.smooth((bt - 120.0F) / 270.0F);
               float size = Math.max(Mth.lerp(big, R * 0.45F, Math.min(dS * 0.75F, R * 1.5F)), 1.0F * scale);
               ShaderInstance sh = use(1);
               setSphere(sh, 4.0F, time, 1.0F, 1.0F, fade, cs, vd);
               sh.safeGetUniform("Intensity").set(0.25F + 0.6F * big);
               RenderSystem.disableCull();
               depth(true);
               billboard(cs, size);
               float flDisco = 1.0F - SupernovaState.smooth((bt - 120.0F) / 210.0F);
               if (flDisco > 0.01F && dS > R * 1.02F) {
                  ShaderInstance fd = use(1);
                  setSphere(fd, 15.0F, time, 1.0F, 1.0F, fade, cs, vd);
                  fd.safeGetUniform("Intensity").set(flDisco);
                  RenderSystem.disableCull();
                  depth(true);
                  billboard(cs, R * 1.6F);
               }

               ShaderInstance ls = use(1);
               setSphere(ls, 11.0F, time, 1.0F, 1.0F, fade, cs, vd);
               ls.safeGetUniform("Intensity").set(0.55F);
               RenderSystem.disableCull();
               depth(true);
               streak(cs, dS * 2.2F, Math.max(dS * 0.006F, 0.02F));
            }
         }
      }
   }

   private static void drawRays(SupernovaState n, Vec3 cam, Matrix4f view, float far, float now, Vector3f cs, float dS, float fade, float charge, float time) {
      if (!(dS < 20.0F)) {
         int rayos = 2;
         float fuerza = (0.45F + 0.75F * charge) * (0.85F + 0.15F * Mth.sin(time * 0.9F));
         Vec3 c = n.center();

         for (int i = 0; i < rayos; i++) {
            float ang = n.id * 1.7F + time * 0.06F + i * ((float) (Math.PI * 2) / rayos);
            float radio = 10.0F + 22.0F * (0.5F + 0.5F * Mth.sin(time * 0.17F + i * 1.9F));
            float alto = 4.0F + 10.0F * Mth.sin(time * 0.11F + i * 2.3F);
            Vector3f offW = new Vector3f(Mth.cos(ang) * radio, alto, Mth.sin(ang) * radio);
            Vector3f offV = view.transformDirection(new Vector3f(offW));
            Vector3f dir = new Vector3f(offV).sub(cs);
            float L = dir.length();
            if (!(L < 1.0F)) {
               dir.div(L);
               float ancho = 7.0F;
               Vector3f aCam = new Vector3f(cs).negate();
               float perp = new Vector3f(dir).cross(aCam).length();
               float u = Mth.clamp(aCam.dot(dir) / (L * 1.18F), 0.0F, 1.0F);
               float wCam = ancho * (0.03F + 0.97F * (float)Math.pow(u, 1.1));
               rayNear = Math.max(rayNear, (float)Math.exp(-(perp / Math.max(wCam, 0.5F)) * (perp / Math.max(wCam, 0.5F))) * fade * fuerza);
               ShaderInstance sh = use(1);
               setSphere(sh, 10.0F, time + i * 3.1F, charge, 0.0F, fade, cs, new Vec3(0.0, 0.0, 1.0));
               sh.safeGetUniform("Intensity").set(fuerza);
               RenderSystem.disableCull();
               depth(true);
               beamOneSided(cs, dir, L * 1.18F, ancho);
               Vec3 dW = new Vec3(offW.x(), offW.y(), offW.z()).add(cam).subtract(c);
               if (dW.lengthSqr() > 1.0E-4) {
                  dW = dW.normalize();
                  float lenW = (float)c.distanceTo(cam) * 1.15F + 20.0F;
                  groundLight(n, new Vector3f((float)(-dW.x), (float)(-dW.y), (float)(-dW.z)), lenW, 6.0F, cam, view, far, fuerza * fade * 0.8F);
               }
            }
         }
      }
   }

   private static void beamOneSided(Vector3f start, Vector3f axis, float len, float width) {
      Vector3f p0 = new Vector3f(start);
      Vector3f p1 = new Vector3f(start).add(new Vector3f(axis).mul(len));
      Vector3f mid = new Vector3f(p0).add(p1).mul(0.5F);
      Vector3f side = new Vector3f(axis).cross(mid);
      if (side.lengthSquared() < 1.0E-6F) {
         side = new Vector3f(axis).cross(0.0F, 1.0F, 0.0F);
      }

      if (side.lengthSquared() < 1.0E-6F) {
         side = new Vector3f(1.0F, 0.0F, 0.0F);
      }

      side.normalize().mul(width);
      BufferBuilder bb = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
      bb.addVertex(p0.x() - side.x(), p0.y() - side.y(), p0.z() - side.z()).setColor(1.0F, 1.0F, 1.0F, 1.0F).setUv(0.0F, 0.0F);
      bb.addVertex(p1.x() - side.x(), p1.y() - side.y(), p1.z() - side.z()).setColor(1.0F, 1.0F, 1.0F, 1.0F).setUv(1.0F, 0.0F);
      bb.addVertex(p1.x() + side.x(), p1.y() + side.y(), p1.z() + side.z()).setColor(1.0F, 1.0F, 1.0F, 1.0F).setUv(1.0F, 1.0F);
      bb.addVertex(p0.x() + side.x(), p0.y() + side.y(), p0.z() + side.z()).setColor(1.0F, 1.0F, 1.0F, 1.0F).setUv(0.0F, 1.0F);
      BufferUploader.drawWithShader(bb.buildOrThrow());
   }

   private static void groundLight(SupernovaState n, Vector3f axisW, float lenW, float widthW, Vec3 cam, Matrix4f view, float far, float strength) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.level != null && mc.player != null && !(strength <= 0.01F)) {
         Vec3 c = n.center();
         Vec3 to = c.subtract(axisW.x() * lenW, axisW.y() * lenW, axisW.z() * lenW);
         BlockHitResult hit = mc.level.clip(new ClipContext(c, to, Block.VISUAL, Fluid.ANY, mc.player));
         if (hit.getType() == Type.BLOCK) {
            Vec3 p = hit.getLocation();
            if (!(p.distanceTo(cam) > far * 0.9F)) {
               float r = Math.max(6.0F, widthW * 2.2F);
               float caida = Math.max(0.25F, Math.abs(axisW.y()));
               Vector3f horiz = new Vector3f(axisW.x(), 0.0F, axisW.z());
               if (horiz.lengthSquared() < 1.0E-5F) {
                  horiz.set(1.0F, 0.0F, 0.0F);
               }

               horiz.normalize();
               Vector3f u = new Vector3f(horiz).mul(r / caida);
               Vector3f w = new Vector3f(-horiz.z(), 0.0F, horiz.x()).mul(r);
               float y = (float)(p.y - cam.y) + 0.06F;
               float px = (float)(p.x - cam.x);
               float pz = (float)(p.z - cam.z);
               ShaderInstance sh = use(1);
               sh.safeGetUniform("Mode").set(8.0F);
               sh.safeGetUniform("Intensity").set(Math.min(1.0F, strength));
               RenderSystem.disableCull();
               depth(true);
               BufferBuilder bb = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
               float cr = 1.0F;
               float cg = 0.86F;
               float cb = 0.95F;
               float ca = 0.9F;
               groundVert(bb, view, px - u.x() - w.x(), y, pz - u.z() - w.z(), cr, cg, cb, ca, 0.0F, 0.0F);
               groundVert(bb, view, px + u.x() - w.x(), y, pz + u.z() - w.z(), cr, cg, cb, ca, 1.0F, 0.0F);
               groundVert(bb, view, px + u.x() + w.x(), y, pz + u.z() + w.z(), cr, cg, cb, ca, 1.0F, 1.0F);
               groundVert(bb, view, px - u.x() + w.x(), y, pz - u.z() + w.z(), cr, cg, cb, ca, 0.0F, 1.0F);
               BufferUploader.drawWithShader(bb.buildOrThrow());
            }
         }
      }
   }

   private static void groundVert(BufferBuilder bb, Matrix4f view, float x, float y, float z, float r, float g, float b, float a, float u, float v) {
      Vector3f q = view.transformPosition(new Vector3f(x, y, z));
      bb.addVertex(q.x(), q.y(), q.z()).setColor(r, g, b, a).setUv(u, v);
   }

   private static void setSphere(ShaderInstance sh, float mode, float time, float charge, float expl, float fade, Vector3f center, Vec3 viewDirW) {
      sh.safeGetUniform("Mode").set(mode);
      sh.safeGetUniform("Time").set(time);
      sh.safeGetUniform("Charge").set(charge);
      sh.safeGetUniform("Expl").set(expl);
      sh.safeGetUniform("Fade").set(fade);
      sh.safeGetUniform("SphereCenter").set(center.x(), center.y(), center.z());
      sh.safeGetUniform("ViewDirW").set((float)viewDirW.x, (float)viewDirW.y, (float)viewDirW.z);
   }

   private static int copiarProfundidad(Minecraft mc) {
      RenderTarget main = mc.getMainRenderTarget();
      if (profCopia == null) {
         profCopia = new TextureTarget(main.width, main.height, true, Minecraft.ON_OSX);
      } else if (profCopia.width != main.width || profCopia.height != main.height) {
         profCopia.resize(main.width, main.height, Minecraft.ON_OSX);
      }

      profCopia.copyDepthFrom(main);
      main.bindWrite(true);
      return profCopia.getDepthTextureId();
   }

   private static void godRays(Matrix4f proj, Matrix4f view) {
      if (!ScreenEffectHandler.luces.isEmpty()) {
         Minecraft mc = Minecraft.getInstance();
         float asp = mc.getWindow().getWidth() / Math.max(1.0F, mc.getWindow().getHeight());
         int prof = -1;
         int dibujadas = 0;
         List<float[]> luces = new ArrayList<>(ScreenEffectHandler.luces);
         luces.sort((p, q) -> Float.compare(q[3], p[3]));

         for (float[] l : luces) {
            if (dibujadas >= 6) {
               break;
            }

            float inten = l[3];
            if (!(inten <= 0.01F)) {
               Vector3f v = view.transformDirection(new Vector3f(l[0], l[1], l[2]));
               float len = v.length();
               if (!(len < 1.0E-4F)) {
                  float frente = -v.z() / len;
                  if (!(frente <= 0.02F)) {
                     Vector4f clip = proj.transform(new Vector4f(v.x(), v.y(), v.z(), 1.0F));
                     if (!(clip.w() <= 1.0E-4F)) {
                        float uvx = clip.x() / clip.w() * 0.5F + 0.5F;
                        float uvy = clip.y() / clip.w() * 0.5F + 0.5F;
                        if (prof < 0) {
                           prof = copiarProfundidad(mc);
                        }

                        ShaderInstance sh = use(1);
                        sh.safeGetUniform("Mode").set(14.0F);
                        sh.safeGetUniform("SphereCenter").set(uvx, uvy, asp);
                        sh.safeGetUniform("Intensity").set(inten * SupernovaState.smooth(frente / 0.35F));
                        sh.safeGetUniform("Charge").set(l[4]);
                        sh.safeGetUniform("Fade").set(1.0F);
                        sh.setSampler("DepthSampler", prof);
                        fullscreen(proj, 1.0F, 1.0F, 1.0F, 1.0F);
                        dibujadas++;
                     }
                  }
               }
            }
         }
      }
   }

   private static void overlays(Matrix4f proj, Matrix4f view) {
      if (rayNear > 0.01F) {
         ShaderInstance sh = use(1);
         sh.safeGetUniform("Mode").set(6.0F);
         fullscreen(proj, 0.55F, 0.5F, 0.95F, 0.2F * Math.min(1.0F, rayNear));
      }

      if (ScreenEffectHandler.flash > 0.003F) {
         ShaderInstance sh = use(1);
         sh.safeGetUniform("Mode").set(16.0F);
         sh.setSampler("DepthSampler", copiarProfundidad(Minecraft.getInstance()));
         float f = ScreenEffectHandler.flash;
         fullscreen(proj, 1.0F, 0.98F, 1.0F, Math.min(1.0F, f * 1.15F));
      }

      if (ScreenEffectHandler.inside > 0.003F) {
         ShaderInstance sh = use(0);
         sh.safeGetUniform("Mode").set(6.0F);
         fullscreen(proj, 1.0F, 0.7F, 0.85F, 0.62F * ScreenEffectHandler.inside);
         sh = use(1);
         sh.safeGetUniform("Mode").set(6.0F);
         fullscreen(proj, 0.45F, 0.95F, 0.9F, 0.18F * ScreenEffectHandler.inside);
      }
   }

   private static void fullscreen(Matrix4f proj, float r, float g, float b, float a) {
      r = Mth.clamp(r, 0.0F, 1.0F);
      g = Mth.clamp(g, 0.0F, 1.0F);
      b = Mth.clamp(b, 0.0F, 1.0F);
      a = Mth.clamp(a, 0.0F, 1.0F);
      RenderSystem.setProjectionMatrix(new Matrix4f(), VertexSorting.ORTHOGRAPHIC_Z);
      RenderSystem.disableDepthTest();
      RenderSystem.disableCull();
      BufferBuilder bb = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
      bb.addVertex(-1.0F, -1.0F, 0.0F).setColor(r, g, b, a).setUv(0.0F, 0.0F);
      bb.addVertex(1.0F, -1.0F, 0.0F).setColor(r, g, b, a).setUv(1.0F, 0.0F);
      bb.addVertex(1.0F, 1.0F, 0.0F).setColor(r, g, b, a).setUv(1.0F, 1.0F);
      bb.addVertex(-1.0F, 1.0F, 0.0F).setColor(r, g, b, a).setUv(0.0F, 1.0F);
      BufferUploader.drawWithShader(bb.buildOrThrow());
      RenderSystem.setProjectionMatrix(proj, VertexSorting.DISTANCE_TO_ORIGIN);
   }

   private static void streak(Vector3f c, float halfW, float halfH) {
      BufferBuilder bb = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
      bb.addVertex(c.x() - halfW, c.y() - halfH, c.z()).setColor(1.0F, 1.0F, 1.0F, 1.0F).setUv(0.0F, 0.0F);
      bb.addVertex(c.x() + halfW, c.y() - halfH, c.z()).setColor(1.0F, 1.0F, 1.0F, 1.0F).setUv(1.0F, 0.0F);
      bb.addVertex(c.x() + halfW, c.y() + halfH, c.z()).setColor(1.0F, 1.0F, 1.0F, 1.0F).setUv(1.0F, 1.0F);
      bb.addVertex(c.x() - halfW, c.y() + halfH, c.z()).setColor(1.0F, 1.0F, 1.0F, 1.0F).setUv(0.0F, 1.0F);
      BufferUploader.drawWithShader(bb.buildOrThrow());
   }

   private static void billboard(Vector3f c, float h) {
      BufferBuilder bb = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
      bb.addVertex(c.x() - h, c.y() - h, c.z()).setColor(1.0F, 1.0F, 1.0F, 1.0F).setUv(0.0F, 0.0F);
      bb.addVertex(c.x() + h, c.y() - h, c.z()).setColor(1.0F, 1.0F, 1.0F, 1.0F).setUv(1.0F, 0.0F);
      bb.addVertex(c.x() + h, c.y() + h, c.z()).setColor(1.0F, 1.0F, 1.0F, 1.0F).setUv(1.0F, 1.0F);
      bb.addVertex(c.x() - h, c.y() + h, c.z()).setColor(1.0F, 1.0F, 1.0F, 1.0F).setUv(0.0F, 1.0F);
      BufferUploader.drawWithShader(bb.buildOrThrow());
   }

   private static void beam(Vector3f c, Vector3f axis, float len, float width) {
      Vector3f side = new Vector3f(axis).cross(c);
      if (side.lengthSquared() < 1.0E-6F) {
         side = new Vector3f(axis).cross(0.0F, 1.0F, 0.0F);
      }

      if (side.lengthSquared() < 1.0E-6F) {
         side = new Vector3f(1.0F, 0.0F, 0.0F);
      }

      side.normalize().mul(width);
      Vector3f a = new Vector3f(axis).mul(len);
      Vector3f p0 = new Vector3f(c).sub(a);
      Vector3f p1 = new Vector3f(c).add(a);
      BufferBuilder bb = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
      bb.addVertex(p0.x() - side.x(), p0.y() - side.y(), p0.z() - side.z()).setColor(1.0F, 1.0F, 1.0F, 1.0F).setUv(0.0F, 0.0F);
      bb.addVertex(p1.x() - side.x(), p1.y() - side.y(), p1.z() - side.z()).setColor(1.0F, 1.0F, 1.0F, 1.0F).setUv(1.0F, 0.0F);
      bb.addVertex(p1.x() + side.x(), p1.y() + side.y(), p1.z() + side.z()).setColor(1.0F, 1.0F, 1.0F, 1.0F).setUv(1.0F, 1.0F);
      bb.addVertex(p0.x() + side.x(), p0.y() + side.y(), p0.z() + side.z()).setColor(1.0F, 1.0F, 1.0F, 1.0F).setUv(0.0F, 1.0F);
      BufferUploader.drawWithShader(bb.buildOrThrow());
   }

   private static void drawSphere(Matrix4f m, float r, boolean inside) {
      BufferBuilder bb = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
      float PI = (float) Math.PI;
      int lat = 40;
      int lon = 80;

      for (int i = 0; i < 40; i++) {
         float a0 = (float) Math.PI * i / 40.0F;
         float a1 = (float) Math.PI * (i + 1) / 40.0F;

         for (int j = 0; j < 80; j++) {
            float b0 = (float) (Math.PI * 2) * j / 80.0F;
            float b1 = (float) (Math.PI * 2) * (j + 1) / 80.0F;
            if (inside) {
               vert(bb, m, r, a0, b0);
               vert(bb, m, r, a1, b0);
               vert(bb, m, r, a1, b1);
               vert(bb, m, r, a0, b1);
            } else {
               vert(bb, m, r, a0, b0);
               vert(bb, m, r, a0, b1);
               vert(bb, m, r, a1, b1);
               vert(bb, m, r, a1, b0);
            }
         }
      }

      BufferUploader.drawWithShader(bb.buildOrThrow());
   }

   private static void vert(BufferBuilder bb, Matrix4f m, float r, float a, float b) {
      float nx = Mth.sin(a) * Mth.cos(b);
      float ny = Mth.cos(a);
      float nz = Mth.sin(a) * Mth.sin(b);
      bb.addVertex(m, nx * r, ny * r, nz * r)
         .setColor(nx * 0.5F + 0.5F, ny * 0.5F + 0.5F, nz * 0.5F + 0.5F, 1.0F)
         .setUv(b / (float) (Math.PI * 2), a / (float) Math.PI);
   }
}
