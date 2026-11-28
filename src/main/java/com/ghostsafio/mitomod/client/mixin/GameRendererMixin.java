package com.ghostsafio.mitomod.client.mixin;

import com.ghostsafio.mitomod.ModItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Environment(EnvType.CLIENT)
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
   @ModifyVariable(method = "displayItemActivation", at = @At("HEAD"), argsOnly = true)
   private ItemStack ghostsafio$swapTotem(ItemStack original) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.player != null && original.is(Items.TOTEM_OF_UNDYING)) {
         for (InteractionHand hand : InteractionHand.values()) {
            ItemStack held = mc.player.getItemInHand(hand);
            if (held.is(ModItems.TOTEM_MONTANA)) {
               return held.copy();
            }
         }
      }

      return original;
   }
}
