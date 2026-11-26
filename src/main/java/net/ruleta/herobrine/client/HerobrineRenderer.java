package net.ruleta.herobrine.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import Nonenet.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.ruleta.herobrine.HerobrineEntity;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class HerobrineRenderer extends HumanoidMobRenderer<HerobrineEntity, PlayerModel<HerobrineEntity>> {
   private static final Identifier PIEL = Identifier.fromNamespaceAndPath("minecraft", "textures/entity/player/wide/steve.png");
   public static final Identifier OJOS = Identifier.fromNamespaceAndPath("ruleta", "textures/entity/herobrine_ojos.png");
   private static final float ESCALA = 0.9F;

   public HerobrineRenderer(Context ctx) {
      super(ctx, new PlayerModel(ctx.bakeLayer(ModelLayers.PLAYER), false), 0.5F);
      this.addLayer(new HerobrineRenderer.Ojos(this));
   }

   protected void scale(HerobrineEntity e, PoseStack pose, float parcial) {
      pose.scale(0.9F, 0.9F, 0.9F);
   }

   public Identifier getTextureLocation(HerobrineEntity e) {
      return PIEL;
   }

   static final class Ojos extends RenderLayer<HerobrineEntity, PlayerModel<HerobrineEntity>> {
      Ojos(RenderLayerParent<HerobrineEntity, PlayerModel<HerobrineEntity>> padre) {
         super(padre);
      }

      public void render(
         PoseStack pose,
         MultiBufferSource buffers,
         int luz,
         HerobrineEntity e,
         float lim,
         float limAmount,
         float parcial,
         float edad,
         float cabezaYaw,
         float cabezaPitch
      ) {
         VertexConsumer vc = buffers.getBuffer(RenderType.eyes(HerobrineRenderer.OJOS));
         ((PlayerModel)this.getParentModel()).renderToBuffer(pose, vc, 15728880, OverlayTexture.NO_OVERLAY, -1);
         pose.pushPose();
         ((PlayerModel)this.getParentModel()).head.translateAndRotate(pose);
         Matrix4f m = pose.last().pose();
         float u = 0.0625F;
         Vector3f px = m.transformDirection(new Vector3f(0.0625F, 0.0F, 0.0F));
         Vector3f py = m.transformDirection(new Vector3f(0.0F, -0.0625F, 0.0F));
         Vector3f normal = m.transformDirection(new Vector3f(0.0F, 0.0F, -1.0F)).normalize();
         pose.popPose();
         VertexConsumer glow = buffers.getBuffer(RenderType.lightning());
         int idx = 0;

         for (OjosBrillo.RectOjo r : OjosBrillo.rectasDeLosOjos()) {
            int clave = e.getId() * 4 + idx++;
            Vector3f c = m.transformPosition(new Vector3f(r.x() * 0.0625F, r.y() * 0.0625F, -0.25F), new Vector3f());
            if (normal.dot(c) < -0.02F * c.length()) {
               OjosBrillo.Ojo o = new OjosBrillo.Ojo(c, px, py, normal, r.hw(), r.hh(), System.nanoTime(), clave);
               OjosBrillo.resplandor(glow, o);
               OjosBrillo.registrar(o);
            }
         }
      }
   }
}
