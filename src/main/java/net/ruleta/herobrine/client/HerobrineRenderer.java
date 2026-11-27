package net.ruleta.herobrine.client;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.resources.Identifier;
import net.ruleta.herobrine.HerobrineEntity;

/** Herobrine: humanoide con la piel de Steve (sin la capa de ojos brillantes por ahora). */
public class HerobrineRenderer extends HumanoidMobRenderer<HerobrineEntity, AvatarRenderState, PlayerModel> {
   private static final Identifier PIEL = Identifier.fromNamespaceAndPath("minecraft", "textures/entity/player/wide/steve.png");
   public static final Identifier OJOS = Identifier.fromNamespaceAndPath("ruleta", "textures/entity/herobrine_ojos.png");

   public HerobrineRenderer(Context ctx) {
      super(ctx, new PlayerModel(ctx.bakeLayer(ModelLayers.PLAYER), false), 0.5F);
   }

   @Override
   public AvatarRenderState createRenderState() {
      return new AvatarRenderState();
   }

   @Override
   public Identifier getTextureLocation(AvatarRenderState estado) {
      return PIEL;
   }
}
