package net.ruleta.nuclear;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.ruleta.Persistente;
import net.ruleta.Reg;

public final class HeartEvents {
   private static final String KEY = "nuclearcreeper_extra_hearts";
   private static final Identifier MODIFIER_ID = Reg.id("ruleta", "corazon_rojo");

   private HeartEvents() {
   }

   public static int getHearts(Player player) {
      return Persistente.de(player).getInt("nuclearcreeper_extra_hearts");
   }

   public static void addHeart(ServerPlayer player) {
      Persistente.de(player).putInt("nuclearcreeper_extra_hearts", getHearts(player) + 1);
      apply(player);
      player.heal(2.0F);
   }

   public static void apply(Player player) {
      AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
      if (maxHealth != null) {
         maxHealth.removeModifier(MODIFIER_ID);
         int hearts = getHearts(player);
         if (hearts > 0) {
            maxHealth.addTransientModifier(new AttributeModifier(MODIFIER_ID, hearts * 2.0, Operation.ADD_VALUE));
         }

         if (player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
         }
      }
   }

   public static void onClone(ServerPlayer original, ServerPlayer renacido, boolean murio) {
      int hearts = Persistente.de(original).getInt("nuclearcreeper_extra_hearts");
      Persistente.de(renacido).putInt("nuclearcreeper_extra_hearts", hearts);
      apply(renacido);
   }

   public static void onLogin(ServerPlayer jugador) {
      apply(jugador);
   }

   public static void onRespawn(ServerPlayer jugador, boolean vivo) {
      apply(jugador);
      if (!vivo) {
         jugador.setHealth(jugador.getMaxHealth());
      }
   }
}
