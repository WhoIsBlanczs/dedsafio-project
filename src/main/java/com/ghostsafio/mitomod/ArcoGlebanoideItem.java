package com.ghostsafio.mitomod;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;

public class ArcoGlebanoideItem extends BowItem {
   public ArcoGlebanoideItem(Properties properties) {
      super(properties);
   }

   public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, net.minecraft.world.item.component.TooltipDisplay tooltipDisplay$, java.util.function.Consumer<Component> tooltipAdder$, TooltipFlag flag) {
      java.util.List<Component> tooltip = new java.util.ArrayList<>();
      this.appendHoverText$viejo(stack, context, tooltipDisplay$, tooltip, flag);
      tooltip.forEach(tooltipAdder$);
   }

   private void appendHoverText$viejo(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, net.minecraft.world.item.component.TooltipDisplay tooltipDisplay$, List<Component> tooltip, TooltipFlag flag) {
      tooltip.add(
         Component.literal("Dispara flechas y las hace ")
            .withStyle(ChatFormatting.GRAY)
            .append(Component.literal("rebotar en enemigos cercanos.").withStyle(ChatFormatting.LIGHT_PURPLE))
      );
   }
}
