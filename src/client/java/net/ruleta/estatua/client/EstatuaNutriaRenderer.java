package net.ruleta.estatua.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Axis;
import net.ruleta.client.compat.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.ruleta.estatua.EstatuaNutriaBE;
import net.ruleta.estatua.EstatuaNutriaBloque;

public class EstatuaNutriaRenderer implements BlockEntityRenderer<EstatuaNutriaBE, net.ruleta.client.compat.EstadoBE<EstatuaNutriaBE>> {
   @Override
   public net.ruleta.client.compat.EstadoBE<EstatuaNutriaBE> createRenderState() {
      return new net.ruleta.client.compat.EstadoBE<>();
   }

   @Override
   public void extractRenderState(EstatuaNutriaBE be$, net.ruleta.client.compat.EstadoBE<EstatuaNutriaBE> estado$, float pt$, net.minecraft.world.phys.Vec3 camara$, net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay rotura$) {
      BlockEntityRenderer.super.extractRenderState(be$, estado$, pt$, camara$, rotura$);
      estado$.be = be$;
      estado$.pt = pt$;
   }

   @Override
   public void submit(net.ruleta.client.compat.EstadoBE<EstatuaNutriaBE> estado$, PoseStack ps$, net.minecraft.client.renderer.SubmitNodeCollector colector$, net.minecraft.client.renderer.state.level.CameraRenderState camara$) {
      net.ruleta.client.compat.Grabadora g$ = new net.ruleta.client.compat.Grabadora(colector$);
      this.render(estado$.be, estado$.pt, ps$, g$, estado$.lightCoords, net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY);
      g$.enviar();
   }

   public EstatuaNutriaRenderer(Context ctx) {
   }

   public boolean shouldRenderOffScreen() {
      return true;
   }

   public int getViewDistance() {
      return 96;
   }

   public void render(EstatuaNutriaBE be, float parcial, PoseStack ps, MultiBufferSource buf, int luz, int overlay) {
      BlockState estado = be.getBlockState();
      if (estado.getBlock() instanceof EstatuaNutriaBloque) {
         ps.pushPose();
         ps.translate(0.5, 0.0, 0.5);
         ps.rotate(Axis.YP.rotationDegrees(180.0F - ((Direction)estado.getValue(EstatuaNutriaBloque.FACING)).toYRot()));
         dibujar(ps, buf, luz, overlay);
         ps.popPose();
      }
   }

   public static void dibujar(PoseStack ps, MultiBufferSource buf, int luz, int overlay) {
      ObjModelo modelo = ObjModelo.obtener();
      if (!modelo.grupos().isEmpty()) {
         Pose p = ps.last();

         for (ObjModelo.Grupo g : modelo.grupos()) {
            VertexConsumer vc = buf.getBuffer(net.minecraft.client.renderer.rendertype.RenderTypes.entityCutout(g.textura()));
            float[] d = g.datos();

            for (int i = 0; i < d.length; i += 8) {
               vc.addVertex(p, d[i], d[i + 1], d[i + 2])
                  .setColor(255, 255, 255, 255)
                  .setUv(d[i + 3], d[i + 4])
                  .setOverlay(OverlayTexture.NO_OVERLAY)
                  .setLight(luz)
                  .setNormal(p, d[i + 5], d[i + 6], d[i + 7]);
            }
         }
      }
   }
}
