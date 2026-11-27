package net.ruleta.herobrine.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Object;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
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
         Object buffers,
         int luz,
         HerobrineEntity e,
         float lim,
         float limAmount,
         float parcial,
         float edad,
         float cabezaYaw,
         float cabezaPitch
      ) { }
   }
}
