package com.ghostsafio.mitomod;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents.CopyFrom;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;

public class HeartHelper {
   private static final ResourceLocation MODIFIER_ID = Ghostsafio.id("corazones_extra");
   public static final double HEALTH_PER_HEART = 2.0;
   public static final double MAX_EXTRA_HEALTH = 40.0;

   public static double getExtraHealth(Player player) {
      AttributeInstance attribute = player.getAttribute(Attributes.MAX_HEALTH);
      if (attribute == null) {
         return 0.0;
      }

      AttributeModifier modifier = attribute.getModifier(MODIFIER_ID);
      return modifier == null ? 0.0 : modifier.amount();
   }

   public static void setExtraHealth(Player player, double amount) {
      AttributeInstance attribute = player.getAttribute(Attributes.MAX_HEALTH);
      if (attribute != null) {
         attribute.removeModifier(MODIFIER_ID);
         if (amount > 0.0) {
            attribute.addPermanentModifier(new AttributeModifier(MODIFIER_ID, amount, Operation.ADD_VALUE));
         }
      }
   }

   public static boolean addHeart(Player player) {
      double current = getExtraHealth(player);
      if (current >= 40.0) {
         return false;
      }

      setExtraHealth(player, Math.min(40.0, current + 2.0));
      player.heal(2.0F);
      return true;
   }

   public static void registerEvents() {
      ServerPlayerEvents.COPY_FROM.register((CopyFrom)(oldPlayer, newPlayer, alive) -> {
         setExtraHealth(newPlayer, getExtraHealth(oldPlayer));
         newPlayer.setHealth(newPlayer.getMaxHealth());
      });
   }
}
