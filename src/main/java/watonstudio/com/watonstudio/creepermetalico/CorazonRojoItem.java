package com.watonstudio.creepermetalico;

import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.Level;

public class CorazonRojoItem extends Item {
   public CorazonRojoItem(Properties settings) {
      super(settings);
   }

   private static MutableComponent texto(String contenido, int color) {
      return Component.literal(contenido)
         .setStyle(Style.EMPTY.withColor(color).withItalic(false).withBold(false).withUnderlined(false).withStrikethrough(false).withObfuscated(false));
   }

   public static ItemStack crearCorazon(Item item) {
      ItemStack stack = new ItemStack(item);
      stack.set(DataComponents.ITEM_NAME, texto("Corazón Rojo", 5636095));
      MutableComponent cantidad = texto("1 ", 16755200);
      cantidad.append(texto("Corazón de Salud Extra.", 16733695));
      List<Component> lore = List.of(texto("", 16777215), texto("Consúmelo para obtener", 11184810), cantidad);
      stack.set(DataComponents.LORE, new ItemLore(lore, lore));
      return stack;
   }

   public UseAnim getUseAnimation(ItemStack stack) {
      return UseAnim.EAT;
   }

   public int getUseDuration(ItemStack stack, LivingEntity user) {
      return 32;
   }

   public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
      ItemStack stack = user.getItemInHand(hand);
      user.startUsingItem(hand);
      return InteractionResultHolder.consume(stack);
   }

   public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
      if (!world.isClientSide && user instanceof Player player) {
         AttributeInstance atributo = player.getAttribute(Attributes.MAX_HEALTH);
         if (atributo != null) {
            atributo.setBaseValue(atributo.getBaseValue() + 2.0);
         }

         player.heal(2.0F);
         if (!player.getAbilities().instabuild) {
            stack.shrink(1);
         }
      }

      return stack;
   }
}
