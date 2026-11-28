package com.ghostsafio.mitomod.client.mixin;

import com.ghostsafio.mitomod.client.NaveClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.client.gui.Gui.class)
public abstract class LoadingScreenMixin {
   @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true, require = 0)
   private void ghostsafio$noLoadingScreen(Screen screen, CallbackInfo ci) {
      if (screen instanceof LevelLoadingScreen && NaveClient.suppressLoadingScreen()) {
         ci.cancel();
      }
   }
}
