package com.watonstudio.creepermetalico;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties.Builder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.Level;

public class CorazonLimboItem extends Item {
   private static final Identifier LIMBO_EXTRA_HEART = Identifier.fromNamespaceAndPath("watonstudio", "limbo_extra_heart");

   public CorazonLimboItem(Properties settings) {
      super(settings.food(new Builder().nutrition(1).saturationModifier(0.1F).alwaysEdible().build()));
   }

   public static ItemStack crearCorazonLimbo(Item item) {
      ItemStack stack = new ItemStack(item);
      stack.set(
         DataComponents.CUSTOM_NAME,
         Component.literal("Corazón del Limbo")
            .setStyle(Style.EMPTY.withColor(5636095).withItalic(false).withBold(false).withUnderlined(false).withStrikethrough(false).withObfuscated(false))
      );
      stack.set(
         DataComponents.LORE,
         new ItemLore(
            List.of(
               Component.empty()
                  .append(Component.literal("Consúmelo para ").withStyle(s -> s.withColor(ChatFormatting.WHITE).withItalic(false)))
                  .append(Component.literal("reestablecer").withStyle(s -> s.withColor(ChatFormatting.GOLD).withItalic(false))),
               Component.literal("tu Barra de Salud al máximo (20).").withStyle(s -> s.withColor(13395711).withItalic(false)),
               Component.empty(),
               Component.empty()
                  .append(Component.literal("⚠ Atención: ").withStyle(s -> s.withColor(ChatFormatting.YELLOW).withItalic(false)))
                  .append(Component.literal("Si tienes Corazones").withStyle(s -> s.withColor(16742263).withItalic(false))),
               Component.literal("Extra, los PERDERÁS al consumirlo.").withStyle(s -> s.withColor(16742263).withItalic(false))
            )
         )
      );
      return stack;
   }

   public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
      ItemStack result = super.finishUsingItem(stack, world, user);
      if (!world.isClientSide() && user instanceof Player player) {
         AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
         if (maxHealth != null) {
            for (AttributeModifier modifier : new ArrayList<>(maxHealth.getModifiers())) {
               Identifier id = modifier.id();
               if (id.getNamespace().equals("watonstudio") && (id.getPath().startsWith("extra_heart_") || id.getPath().startsWith("limbo_extra_heart"))) {
                  maxHealth.removeModifier(id);
               }
            }

            maxHealth.setBaseValue(20.0);
            maxHealth.addPermanentModifier(new AttributeModifier(LIMBO_EXTRA_HEART, 4.0, Operation.ADD_VALUE));
            player.setHealth(player.getMaxHealth());
         }
      }

      return result;
   }
}
