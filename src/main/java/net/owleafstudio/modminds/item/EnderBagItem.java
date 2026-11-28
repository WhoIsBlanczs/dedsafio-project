package net.owleafstudio.modminds.item;

import net.minecraft.world.InteractionResult;

import java.util.List;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item;

public class EnderBagItem extends Item {
   public EnderBagItem() {
      super(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));
   }

   public Component getName(ItemStack stack) {
      return Component.literal("Ender Bag").withStyle(style -> style.withBold(true).withColor(TextColor.fromRgb(5570730)));
   }

   public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext level, net.minecraft.world.item.component.TooltipDisplay tooltipDisplay$, java.util.function.Consumer<Component> tooltipAdder$, TooltipFlag flag) {
      java.util.List<Component> tooltip = new java.util.ArrayList<>();
      this.appendHoverText$viejo(stack, level, tooltipDisplay$, tooltip, flag);
      tooltip.forEach(tooltipAdder$);
   }

   private void appendHoverText$viejo(ItemStack stack, net.minecraft.world.item.Item.TooltipContext level, net.minecraft.world.item.component.TooltipDisplay tooltipDisplay$, List<Component> tooltip, TooltipFlag flag) {
      tooltip.add(Component.literal("Tu propio Ender Chest,").withStyle(style -> style.withColor(TextColor.fromRgb(16733695))));
      tooltip.add(
         Component.literal("¡Ahora en tu bolsillo!").withStyle(style -> style.withBold(true).withColor(TextColor.fromRgb(16733695)))
      );
   }

   public InteractionResult use(Level level, Player player, InteractionHand hand) {
      if (!level.isClientSide()) {
         player.openMenu(
            new SimpleMenuProvider((id, inventory, p) -> ChestMenu.threeRows(id, inventory, player.getEnderChestInventory()), Component.literal("Ender Bag"))
         );
      }

      return InteractionResult.SUCCESS;
   }
}
