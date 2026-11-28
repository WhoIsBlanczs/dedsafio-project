package com.watonstudio.creepermetalico.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.watonstudio.creepermetalico.CreeperQteClient;
import com.watonstudio.creepermetalico.FloralRootsFeatureRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(PlayerRenderer.class)
public abstract class FloralFirstPersonRootsMixin {
   @Inject(method = "renderArm", at = @At("TAIL"))
   private void watonstudio$renderRootsOnFirstPersonArm(
      PoseStack matrices, MultiBufferSource vertexConsumers, int light, AbstractClientPlayer player, ModelPart arm, ModelPart sleeve, CallbackInfo ci
   ) {
      Minecraft client = Minecraft.getInstance();
      if (client.options.getCameraType().isFirstPerson() && CreeperQteClient.shouldRenderRoots(player)) {
         FloralRootsFeatureRenderer.renderFirstPersonArmRoots(matrices, vertexConsumers, light, player, arm);
      }
   }
}
