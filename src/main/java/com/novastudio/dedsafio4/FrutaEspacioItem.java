package com.novastudio.dedsafio4;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item;

public final class FrutaEspacioItem extends Item {
   public static final int COOLDOWN_TICKS = 60;
   private static final String TOOLTIP = "tooltip.nexo.fruta_del_espacio_entre_dimensiones.";

   public FrutaEspacioItem() {
      super(new Item.Properties().food(Foods.CHORUS_FRUIT, net.minecraft.world.item.component.Consumables.CHORUS_FRUIT).useCooldown(3.0F).rarity(Rarity.EPIC));
   }

   public ItemStack finishUsingItem(ItemStack var1, Level var2, LivingEntity var3) {
      return super.finishUsingItem(var1, var2, var3);
   }

   public void appendHoverText(ItemStack var1, net.minecraft.world.item.Item.TooltipContext var2, net.minecraft.world.item.component.TooltipDisplay tooltipDisplay$, java.util.function.Consumer<Component> tooltipAdder$, TooltipFlag var4) {
      java.util.List<Component> var3 = new java.util.ArrayList<>();
      this.appendHoverText$viejo(var1, var2, tooltipDisplay$, var3, var4);
      var3.forEach(tooltipAdder$);
   }

   private void appendHoverText$viejo(ItemStack var1, net.minecraft.world.item.Item.TooltipContext var2, net.minecraft.world.item.component.TooltipDisplay tooltipDisplay$, List<Component> var3, TooltipFlag var4) {
      var3.add(line(1));
      var3.add(line(2));
      var3.add(Component.empty());
      var3.add(line(3));
      var3.add(line(4));
      var3.add(line(5));
   }

   private static Component line(int var0) {
      return Component.translatable("tooltip.nexo.fruta_del_espacio_entre_dimensiones." + var0).withStyle(ChatFormatting.GRAY);
   }
}
