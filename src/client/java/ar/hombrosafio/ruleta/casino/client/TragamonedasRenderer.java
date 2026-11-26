package ar.hombrosafio.ruleta.casino.client;

import ar.hombrosafio.ruleta.casino.DeditaItem;
import ar.hombrosafio.ruleta.casino.TragamonedasBE;
import ar.hombrosafio.ruleta.casino.TragamonedasBloque;
import ar.hombrosafio.ruleta.RuletaMod;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

/** Rodillos y palanca del tragamonedas (adaptado al sistema de render states de 26.3). */
public class TragamonedasRenderer implements BlockEntityRenderer<TragamonedasBE, TragamonedasRenderer.State> {
   private static final Identifier TEX_TAMBOR = RuletaMod.id("textures/block/tragamonedas/drum.png");
   private static final Identifier TEX_BRAZO = RuletaMod.id("textures/block/tragamonedas/lever_arm.png");
   private static final Identifier TEX_BOLA = RuletaMod.id("textures/block/tragamonedas/red.png");
   private static final float[] RODILLO_X = new float[]{-6.7F, 0.0F, 6.7F};
   private static final float MEDIA_CARA = 5.4F * (float)Math.tan(Math.PI / 8);
   private static final int[] FRENA_DESDE = new int[]{30, 35, 40};
   private final ItemModelResolver itemResolver;

   public TragamonedasRenderer(BlockEntityRendererProvider.Context ctx) {
      this.itemResolver = ctx.itemModelResolver();
   }

   public static final class State extends BlockEntityRenderState {
      Direction frente = Direction.NORTH;
      float t;
      boolean conItems;
      final int[] resultado = new int[3];
      final int[] previo = new int[3];
      final int[] duracion = new int[3];
      final ItemStackRenderState[] items = new ItemStackRenderState[8];
      final boolean[] dedita = new boolean[8];

      public State() {
         for (int i = 0; i < 8; i++) {
            this.items[i] = new ItemStackRenderState();
         }
      }
   }

   @Override
   public State createRenderState() {
      return new State();
   }

   @Override
   public boolean shouldRenderOffScreen() {
      return true;
   }

   @Override
   public void extractRenderState(TragamonedasBE be, State state, float partial, Vec3 camera, CrumblingOverlay overlay) {
      BlockEntityRenderer.super.extractRenderState(be, state, partial, camera, overlay);
      state.frente = be.getBlockState().getValue(TragamonedasBloque.FACING);
      state.t = be.getLevel() == null ? 0.0F : (float)(be.getLevel().getGameTime() - be.inicio) + partial;
      double dx = be.getBlockPos().getX() + 0.5 - camera.x;
      double dy = be.getBlockPos().getY() + 0.5 - camera.y;
      double dz = be.getBlockPos().getZ() + 0.5 - camera.z;
      state.conItems = dx * dx + dy * dy + dz * dz < 784.0;
      for (int r = 0; r < 3; r++) {
         state.resultado[r] = be.resultado[r];
         state.previo[r] = be.previo[r];
         state.duracion[r] = be.duracion[r];
      }
      if (state.conItems) {
         ItemStack[] pilas = be.pilas();
         for (int k = 0; k < 8; k++) {
            state.dedita[k] = pilas[k].getItem() instanceof DeditaItem;
            this.itemResolver.updateForTopItem(state.items[k], pilas[k], state.dedita[k] ? ItemDisplayContext.NONE : ItemDisplayContext.GUI,
               be.getLevel(), null, k);
         }
      }
   }

   @Override
   public void submit(State state, PoseStack ps, SubmitNodeCollector nodes, CameraRenderState camera) {
      ps.pushPose();
      ps.translate(0.5, 0.0, 0.5);
      ps.rotateDegrees(Axis.YP, 180.0F - state.frente.toYRot());
      ps.scale(0.0625F, 0.0625F, 0.0625F);
      ps.translate(0.0, -16.0, 0.0);
      for (int r = 0; r < 3; r++) {
         dibujarRodillo(state, r, angulo(state, r, state.t - 18.0F), ps, nodes);
      }
      dibujarPalanca(palanca(state.t), state.lightCoords, ps, nodes);
      ps.popPose();
   }

   private static float angulo(State be, int r, float t) {
      float fin = -45.0F * be.resultado[r];
      float antes = -45.0F * be.previo[r];
      float dur = be.duracion[r];
      if (t < 0.0F) {
         return antes;
      } else if (t >= dur) {
         return fin;
      } else {
         float inicioFreno = FRENA_DESDE[r];
         float largoFreno = dur - 8.0F - inicioFreno;
         float faltaAlFrenar = 30.0F * largoFreno / 2.0F - 14.0F;
         float objetivo = antes - fin - faltaAlFrenar;
         float d1 = objetivo + 360.0F * Math.round((30.0F * inicioFreno - objetivo) / 360.0F);
         float falta;
         if (t < inicioFreno) {
            falta = faltaAlFrenar + d1 * (inicioFreno - t) / inicioFreno;
         } else if (t < inicioFreno + largoFreno) {
            float s = (t - inicioFreno) / largoFreno;
            falta = faltaAlFrenar - 30.0F * largoFreno * (s - 0.5F * s * s);
         } else {
            float u = (t - inicioFreno - largoFreno) / 8.0F;
            falta = -14.0F * (1.0F - suave(u));
         }

         return fin + falta;
      }
   }

   private static float palanca(float t) {
      float bajada = 5.0F;
      if (t < 0.0F || t >= 18.0F) {
         return 0.0F;
      } else {
         return t < bajada ? -95.0F * suave(t / bajada) : -95.0F * (1.0F - suave((t - bajada) / (18.0F - bajada)));
      }
   }

   private static float suave(float x) {
      x = Mth.clamp(x, 0.0F, 1.0F);
      return x * x * (3.0F - 2.0F * x);
   }

   private static void dibujarRodillo(State st, int r, float grados, PoseStack ps, SubmitNodeCollector nodes) {
      float cx = RODILLO_X[r];
      int luz = st.lightCoords;
      ps.pushPose();
      ps.translate(cx, 25.2F, -1.0F);
      nodes.submitCustomGeometry(ps, RenderTypes.entityCutout(TEX_TAMBOR), (pose, vc) -> {
         for (int k = 0; k < 8; k++) {
            float a = (float)Math.toRadians(k * 45.0F + grados);
            float ny = Mth.sin(a);
            float nz = -Mth.cos(a);
            float ty = Mth.cos(a);
            float tz = Mth.sin(a);
            float cy = 5.4F * ny;
            float cz = 5.4F * nz;
            float y1 = cy - ty * MEDIA_CARA;
            float z1 = cz - tz * MEDIA_CARA;
            float y2 = cy + ty * MEDIA_CARA;
            float z2 = cz + tz * MEDIA_CARA;
            float sombra = 0.78F + 0.22F * Mth.clamp(-nz, 0.0F, 1.0F) + 0.1F * Mth.clamp(ny, 0.0F, 1.0F);
            quad(vc, pose, -2.9F, y1, z1, 2.9F, y1, z1, 2.9F, y2, z2, -2.9F, y2, z2, 0.0F, ny, nz, Math.min(1.0F, sombra), luz, OverlayTexture.NO_OVERLAY);
         }
         float radio = 5.4F / (float)Math.cos(Math.PI / 8);
         for (int lado = 0; lado < 2; lado++) {
            float x = lado == 0 ? -2.9F : 2.9F;
            float nx = lado == 0 ? -1.0F : 1.0F;
            for (int k = 0; k < 8; k++) {
               float b1 = (float)Math.toRadians(k * 45.0F + grados - 22.5F);
               float b2 = (float)Math.toRadians((k + 1) * 45.0F + grados - 22.5F);
               quad(vc, pose, x, 0.0F, 0.0F, x, radio * Mth.sin(b1), -radio * Mth.cos(b1), x, radio * Mth.sin(b2), -radio * Mth.cos(b2),
                  x, 0.0F, 0.0F, nx, 0.0F, 0.0F, 0.7F, luz, OverlayTexture.NO_OVERLAY);
            }
         }
      });
      for (int k = 0; k < 8 && st.conItems; k++) {
         float grad = k * 45.0F + grados;
         float c = Mth.cos((float)Math.toRadians(grad));
         if (!(c < 0.05F)) {
            ps.pushPose();
            ps.rotateDegrees(Axis.XP, grad);
            ps.translate(0.0, 0.0, -5.65F);
            ps.rotateDegrees(Axis.YP, 180.0F);
            ps.scale(4.3F, 4.3F, 0.6F);
            if (st.dedita[k]) {
               ps.translate(0.0, 0.0, 0.171875);
               ps.rotateDegrees(Axis.XP, 90.0F);
            }
            st.items[k].submit(ps, nodes, luz, OverlayTexture.NO_OVERLAY, 0);
            ps.popPose();
         }
      }
      ps.popPose();
   }

   private static void dibujarPalanca(float extra, int luz, PoseStack ps, SubmitNodeCollector nodes) {
      ps.pushPose();
      ps.translate(13.7, 18.785, -5.185);
      ps.rotateDegrees(Axis.XP, extra);
      ps.translate(-13.7, -18.785, 5.185);
      ps.pushPose();
      ps.translate(13.7, 20.8, -7.2);
      ps.rotateDegrees(Axis.XP, -45.0F);
      ps.translate(-13.7, -20.8, 7.2);
      nodes.submitCustomGeometry(ps, RenderTypes.entityCutout(TEX_BRAZO),
         (pose, vc) -> caja(vc, pose, 12.85F, 17.95F, -8.05F, 14.55F, 23.65F, -6.35F, luz, OverlayTexture.NO_OVERLAY));
      ps.popPose();
      ps.pushPose();
      ps.translate(13.7, 23.8, -9.8);
      ps.rotateDegrees(Axis.XP, -45.0F);
      ps.translate(-13.7, -23.8, 9.8);
      nodes.submitCustomGeometry(ps, RenderTypes.entityCutout(TEX_BOLA),
         (pose, vc) -> caja(vc, pose, 12.2F, 22.3F, -11.3F, 15.2F, 25.3F, -8.3F, luz, OverlayTexture.NO_OVERLAY));
      ps.popPose();
      ps.popPose();
   }

   private static void caja(VertexConsumer vc, PoseStack.Pose p, float x1, float y1, float z1, float x2, float y2, float z2, int luz, int ovl) {
      quad(vc, p, x1, y1, z1, x2, y1, z1, x2, y2, z1, x1, y2, z1, 0.0F, 0.0F, -1.0F, 0.8F, luz, ovl);
      quad(vc, p, x2, y1, z2, x1, y1, z2, x1, y2, z2, x2, y2, z2, 0.0F, 0.0F, 1.0F, 0.8F, luz, ovl);
      quad(vc, p, x2, y1, z1, x2, y1, z2, x2, y2, z2, x2, y2, z1, 1.0F, 0.0F, 0.0F, 0.65F, luz, ovl);
      quad(vc, p, x1, y1, z2, x1, y1, z1, x1, y2, z1, x1, y2, z2, -1.0F, 0.0F, 0.0F, 0.65F, luz, ovl);
      quad(vc, p, x1, y2, z1, x2, y2, z1, x2, y2, z2, x1, y2, z2, 0.0F, 1.0F, 0.0F, 1.0F, luz, ovl);
      quad(vc, p, x1, y1, z2, x2, y1, z2, x2, y1, z1, x1, y1, z1, 0.0F, -1.0F, 0.0F, 0.5F, luz, ovl);
   }

   private static void quad(
      VertexConsumer vc,
      PoseStack.Pose p,
      float ax,
      float ay,
      float az,
      float bx,
      float by,
      float bz,
      float cx,
      float cy,
      float cz,
      float dx,
      float dy,
      float dz,
      float nx,
      float ny,
      float nz,
      float brillo,
      int luz,
      int ovl
   ) {
      int c = Math.round(255.0F * brillo);
      v(vc, p, ax, ay, az, c, nx, ny, nz, luz, ovl);
      v(vc, p, bx, by, bz, c, nx, ny, nz, luz, ovl);
      v(vc, p, cx, cy, cz, c, nx, ny, nz, luz, ovl);
      v(vc, p, dx, dy, dz, c, nx, ny, nz, luz, ovl);
   }

   private static void v(VertexConsumer vc, PoseStack.Pose p, float x, float y, float z, int c, float nx, float ny, float nz, int luz, int ovl) {
      vc.addVertex(p, x, y, z).setColor(c, c, c, 255).setUv(0.03F, 0.03F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(luz).setNormal(p, nx, ny, nz);
   }
}
