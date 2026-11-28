package com.hexagonsafio2;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;

public class DescItem extends Item {
   public DescItem(Properties properties) {
      super(properties);
   }

   public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, net.minecraft.world.item.component.TooltipDisplay tooltipDisplay$, java.util.function.Consumer<Component> tooltipAdder$, TooltipFlag flag) {
      java.util.List<Component> tooltip = new java.util.ArrayList<>();
      this.appendHoverText$viejo(stack, context, tooltipDisplay$, tooltip, flag);
      tooltip.forEach(tooltipAdder$);
   }

   private void appendHoverText$viejo(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, net.minecraft.world.item.component.TooltipDisplay tooltipDisplay$, List<Component> tooltip, TooltipFlag flag) {
      tooltip.add(Component.translatable(this.getDescriptionId() + ".desc").withStyle(ChatFormatting.GRAY));
   }
}
