package com.watonstudio.creepermetalico.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Environment(EnvType.CLIENT)
@Mixin(MerchantScreen.class)
public abstract class PhoraMerchantHudMixin {
   private static final ResourceLocation WATON_PHORA_HUD = ResourceLocation.fromNamespaceAndPath("watonstudio", "textures/gui/phora_hud.png");

   private boolean watonstudio$isPhoraScreen() {
      MerchantScreen screen = (MerchantScreen)this;
      return screen.getTitle().getString().contains("Aldeano Phora de Recursos");
   }

   @Redirect(
      method = "drawBackground",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIFFIIII)V")
   )
   private void watonstudio$replaceMerchantBackground(
      GuiGraphics context, ResourceLocation vanillaTexture, int x, int y, int z, float u, float v, int width, int height, int textureWidth, int textureHeight
   ) {
      if (this.watonstudio$isPhoraScreen()) {
         context.blit(WATON_PHORA_HUD, x, y, width, height, 0.0F, 0.0F, 1599, 984, 1599, 984);
      } else {
         context.blit(vanillaTexture, x, y, z, u, v, width, height, textureWidth, textureHeight);
      }
   }
}
