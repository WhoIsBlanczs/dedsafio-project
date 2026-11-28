package com.watonstudio.creepermetalico.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.watonstudio.creepermetalico.LinternaItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ItemInHandRenderer.class)
public abstract class LinternaHeldAnimationMixin {
   @Unique
   private float watonstudio$mainPose = 0.0F;
   @Unique
   private float watonstudio$offPose = 0.0F;
   private static final String RENDER_METHOD = "renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V";

   @Inject(
      method = "renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
      at = @At("HEAD")
   )
   private void watonstudio$animateLantern(
      AbstractClientPlayer player,
      float tickDelta,
      float pitch,
      InteractionHand hand,
      float swingProgress,
      ItemStack item,
      float equipProgress,
      PoseStack matrices,
      MultiBufferSource vertexConsumers,
      int light,
      CallbackInfo ci
   ) {
      if (item.getItem() instanceof LinternaItem lantern) {
         boolean active = player.isUsingItem() && player.getUsedItemHand() == hand;
         float progress = Mth.clamp((40.0F - player.getUseItemRemainingTicks() + tickDelta) / 40.0F, 0.0F, 1.0F);
         float eased = progress * progress * (3.0F - 2.0F * progress);
         float desired;
         if (active) {
            desired = lantern.isLit() ? 1.0F - eased : eased;
         } else {
            desired = lantern.isLit() ? 1.0F : 0.0F;
         }

         float pose;
         if (hand == InteractionHand.MAIN_HAND) {
            this.watonstudio$mainPose = this.watonstudio$mainPose + (desired - this.watonstudio$mainPose) * 0.22F;
            pose = this.watonstudio$mainPose;
         } else {
            this.watonstudio$offPose = this.watonstudio$offPose + (desired - this.watonstudio$offPose) * 0.22F;
            pose = this.watonstudio$offPose;
         }

         matrices.pushPose();
         float bob = Mth.sin(pose * (float) Math.PI) * 0.018F;
         matrices.translate(0.015F * pose, -0.11F * pose + bob, 0.045F * pose);
         matrices.mulPose(Axis.XP.rotationDegrees(-13.0F * pose));
         matrices.mulPose(Axis.ZP.rotationDegrees(2.5F * pose));
      }
   }

   @Inject(
      method = "renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
      at = @At("RETURN")
   )
   private void watonstudio$restoreLantern(
      AbstractClientPlayer player,
      float tickDelta,
      float pitch,
      InteractionHand hand,
      float swingProgress,
      ItemStack item,
      float equipProgress,
      PoseStack matrices,
      MultiBufferSource vertexConsumers,
      int light,
      CallbackInfo ci
   ) {
      if (item.getItem() instanceof LinternaItem) {
         matrices.popPose();
      }
   }
}
