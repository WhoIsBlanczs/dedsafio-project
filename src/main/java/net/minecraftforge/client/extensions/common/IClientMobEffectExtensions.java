package net.minecraftforge.client.extensions.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public interface IClientMobEffectExtensions {
   Map<MobEffect, IClientMobEffectExtensions> EXTENSIONS = new ConcurrentHashMap<>();

   static IClientMobEffectExtensions of(MobEffect effect) {
      return effect == null ? null : EXTENSIONS.get(effect);
   }

   default boolean isVisibleInInventory(MobEffectInstance effect) {
      return true;
   }

   default boolean isVisibleInGui(MobEffectInstance effect) {
      return true;
   }

   default boolean isVisibleInHud(MobEffectInstance effect) {
      return this.isVisibleInGui(effect);
   }

   default boolean renderInventoryText(MobEffectInstance instance, net.minecraft.client.gui.screens.Screen screen, GuiGraphicsExtractor guiGraphics, int x, int y, int blitOffset) {
      return false;
   }
}
