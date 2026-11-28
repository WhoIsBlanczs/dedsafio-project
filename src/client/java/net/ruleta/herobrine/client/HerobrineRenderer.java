package net.ruleta.herobrine.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.ruleta.herobrine.HerobrineEntity;

public class HerobrineRenderer extends HumanoidMobRenderer<HerobrineEntity, AvatarRenderState, PlayerModel> {
   private static final Identifier PIEL = Identifier.fromNamespaceAndPath("minecraft", "textures/entity/player/wide/steve.png");
   public static final Identifier OJOS = Identifier.fromNamespaceAndPath("ruletafanproject", "textures/entity/herobrine_ojos.png");
   private static final float ESCALA = 0.9F;

   public HerobrineRenderer(Context ctx) {
      super(ctx, new PlayerModel(ctx.bakeLayer(ModelLayers.PLAYER), false), 0.5F);
      this.addLayer(new HerobrineRenderer.Ojos(this));
   }

   @Override
   public AvatarRenderState createRenderState() {
      return new AvatarRenderState();
   }

   @Override
   public void extractRenderState(HerobrineEntity e, AvatarRenderState estado, float parcial) {
      super.extractRenderState(e, estado, parcial);
      estado.showHat = true;
      estado.showJacket = true;
      estado.showLeftPants = true;
      estado.showRightPants = true;
      estado.showLeftSleeve = true;
      estado.showRightSleeve = true;
   }

   @Override
   protected void scale(AvatarRenderState estado, PoseStack pose) {
      pose.scale(ESCALA, ESCALA, ESCALA);
   }

   @Override
   public Identifier getTextureLocation(AvatarRenderState estado) {
      return PIEL;
   }

   /** Ojos blancos que brillan en la oscuridad y un resplandor alrededor, como en el original. */
   static final class Ojos extends RenderLayer<AvatarRenderState, PlayerModel> {
      Ojos(RenderLayerParent<AvatarRenderState, PlayerModel> padre) {
         super(padre);
      }

      @Override
      public void submit(PoseStack pose, SubmitNodeCollector colector, int luz, AvatarRenderState estado, float yRot, float xRot) {
         colector.submitModel(this.getParentModel(), estado, pose, RenderTypes.eyes(HerobrineRenderer.OJOS), 15728880, OverlayTexture.NO_OVERLAY, -1, null, estado.outlineColor);
         pose.pushPose();
         this.getParentModel().head.translateAndRotate(pose);
         colector.submitCustomGeometry(pose, RenderTypes.lightning(), (p, vc) -> {
            for (OjosBrillo.RectOjo r : OjosBrillo.rectasDeLosOjos()) {
               OjosBrillo.resplandor(vc, p.pose(), r);
            }
         });
         pose.popPose();
      }
   }
}
