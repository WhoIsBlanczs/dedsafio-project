package net.ruleta.nuclear.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.CreeperRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.ruleta.nuclear.entity.NuclearCreeperEntity;

public class NuclearCreeperRenderer extends MobRenderer<NuclearCreeperEntity, CreeperRenderState, NuclearCreeperModel<CreeperRenderState>> {
   private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("nuclearcreeper", "textures/entity/nuclear_creeper.png");
   private static final float BASE_SCALE = 0.85F;

   public NuclearCreeperRenderer(Context context) {
      super(context, new NuclearCreeperModel<>(context.bakeLayer(NuclearCreeperModel.LAYER_LOCATION)), 0.5F);
   }

   @Override
   public CreeperRenderState createRenderState() {
      return new CreeperRenderState();
   }

   @Override
   public void extractRenderState(NuclearCreeperEntity entity, CreeperRenderState state, float partialTick) {
      super.extractRenderState(entity, state, partialTick);
      state.swelling = entity.getSwelling(partialTick);
      state.isPowered = entity.isPowered();
   }

   @Override
   protected void scale(CreeperRenderState state, PoseStack pose) {
      float swell = state.swelling;
      float wobble = 1.0F + Mth.sin(swell * 100.0F) * swell * 0.01F;
      swell = Mth.clamp(swell, 0.0F, 1.0F);
      swell *= swell;
      swell *= swell;
      float sxz = (1.0F + swell * 0.4F) * wobble;
      float sy = (1.0F + swell * 0.1F) / wobble;
      pose.scale(sxz * BASE_SCALE, sy * BASE_SCALE, sxz * BASE_SCALE);
   }

   @Override
   protected float getWhiteOverlayProgress(CreeperRenderState state) {
      float swell = state.swelling;
      return (int)(swell * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(swell, 0.5F, 1.0F);
   }

   @Override
   public Identifier getTextureLocation(CreeperRenderState state) {
      return TEXTURE;
   }
}
