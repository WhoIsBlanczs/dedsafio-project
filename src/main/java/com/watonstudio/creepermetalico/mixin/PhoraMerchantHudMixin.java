package com.watonstudio.creepermetalico.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Environment(EnvType.CLIENT)
@Mixin(MerchantScreen.class)
public abstract class PhoraMerchantHudMixin {
   private static final Identifier WATON_PHORA_HUD = Identifier.fromNamespaceAndPath("watonstudio", "textures/gui/phora_hud.png");

   private boolean watonstudio$isPhoraScreen() {
      MerchantScreen screen = (MerchantScreen)(Object)this;
      return screen.getTitle().getString().contains("Aldeano Phora de Recursos");
   }

   @org.spongepowered.asm.mixin.injection.Inject(method = "extractBackground", at = @At("TAIL"))
   private void watonstudio$replaceMerchantBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta, org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
      if (this.watonstudio$isPhoraScreen()) {
         MerchantScreen screen = (MerchantScreen)(Object)this;
         int x = (screen.width - 276) / 2;
         int y = (screen.height - 166) / 2;
         context.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, WATON_PHORA_HUD, x, y, 0.0F, 0.0F, 276, 166, 1599, 984, 1599, 984);
      }
   }
}
