package net.ruleta.nuclear;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.ruleta.Persistente;
import net.ruleta.Red;
import net.ruleta.Reg;
import net.ruleta.Ruleta;
import net.ruleta.compat.EventPriority;
import net.ruleta.compat.LivingDamageEvent;
import net.ruleta.compat.LivingDeathEvent;
import net.ruleta.compat.SubscribeEvent;
import net.ruleta.nuclear.item.TotemItem;
import net.ruleta.nuclear.network.TotemActivatePacket;

public final class TotemEvents {
   private static final ResourceLocation GOLEM_MODIFIER_ID = Reg.id("ruleta", "totem_golem");
   public static final double GOLEM_BONUS_HEALTH = 4.0;
   private static final String EON_HASTA = "ruleta_eon_inmune_hasta";
   private static final long EON_TICKS = 60L;

   private TotemEvents() {
   }

   @SubscribeEvent(priority = EventPriority.HIGH)
   public static void onDeath(LivingDeathEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         if (!event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            if (eonInmune(player)) {
               event.setCanceled(true);
               player.setHealth(1.0F);
            } else {
               ItemStack used = findTotemInHands(player);
               if (used.isEmpty()) {
                  used = findEon(player);
               }

               if (!used.isEmpty()) {
                  TotemItem.Kind kind = ((TotemItem)used.getItem()).getKind();
                  ItemStack shown = used.copy();
                  used.shrink(1);
                  event.setCanceled(true);
                  player.setHealth(1.0F);
                  player.removeAllEffects();
                  player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
                  player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
                  player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
                  if (kind == TotemItem.Kind.EON) {
                     Persistente.de(player).putLong("ruleta_eon_inmune_hasta", player.level().getGameTime() + 60L);
                  }

                  if (kind == TotemItem.Kind.YUNQUE) {
                     repairArmor(player);
                  }

                  player.awardStat(Stats.ITEM_USED.get(shown.getItem()));
                  player.serverLevel().sendParticles(ParticleTypes.TOTEM_OF_UNDYING, player.getX(), player.getY() + 1.0, player.getZ(), 30, 0.5, 0.6, 0.5, 0.4);
                  player.level().playSound(null, player.blockPosition(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
                  Red.enviar(player, new TotemActivatePacket(shown));
                  Ruleta.avisarTotem(player, event.getSource(), Component.literal(kind.displayName));
               }
            }
         }
      }
   }

   private static boolean eonInmune(ServerPlayer p) {
      return Persistente.de(p).getLong("ruleta_eon_inmune_hasta") >= p.level().getGameTime();
   }

   @SubscribeEvent(priority = EventPriority.LOWEST)
   public static void onDamage(LivingDamageEvent event) {
      if (event.getEntity() instanceof ServerPlayer p
         && eonInmune(p)
         && !event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)
         && event.getAmount() >= p.getHealth() + p.getAbsorptionAmount()) {
         event.setCanceled(true);
      }
   }

   private static ItemStack findTotemInHands(ServerPlayer player) {
      ItemStack golem = ItemStack.EMPTY;

      for (InteractionHand hand : InteractionHand.values()) {
         ItemStack stack = player.getItemInHand(hand);
         if (stack.getItem() instanceof TotemItem totem && totem.getKind().revives) {
            if (totem.getKind() != TotemItem.Kind.GOLEM) {
               return stack;
            }

            if (golem.isEmpty()) {
               golem = stack;
            }
         }
      }

      return golem;
   }

   private static boolean hasAnyReviveTotem(ServerPlayer player) {
      int size = player.getInventory().getContainerSize();

      for (int i = 0; i < size; i++) {
         ItemStack stack = player.getInventory().getItem(i);
         if (stack.is(Items.TOTEM_OF_UNDYING)) {
            return true;
         }

         if (stack.getItem() instanceof TotemItem totem && totem.getKind().revives && totem.getKind() != TotemItem.Kind.EON) {
            return true;
         }
      }

      return false;
   }

   private static ItemStack findEon(ServerPlayer player) {
      int size = player.getInventory().getContainerSize();

      for (int i = 0; i < size; i++) {
         ItemStack stack = player.getInventory().getItem(i);
         if (stack.getItem() instanceof TotemItem totem && totem.getKind() == TotemItem.Kind.EON) {
            return stack;
         }
      }

      return ItemStack.EMPTY;
   }

   private static void repairArmor(ServerPlayer player) {
      for (ItemStack armor : player.getArmorSlots()) {
         if (!armor.isEmpty() && armor.isDamageableItem()) {
            armor.setDamageValue(0);
         }
      }
   }

   public static void onPlayerTick(ServerPlayer jugador) {
      Player player = jugador;
      AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
      if (maxHealth != null) {
         boolean holdingGolem = isGolem(player.getMainHandItem()) || isGolem(player.getOffhandItem());
         boolean applied = maxHealth.getModifier(GOLEM_MODIFIER_ID) != null;
         if (holdingGolem && !applied) {
            maxHealth.addTransientModifier(new AttributeModifier(GOLEM_MODIFIER_ID, 4.0, Operation.ADD_VALUE));
         } else if (!holdingGolem && applied) {
            maxHealth.removeModifier(GOLEM_MODIFIER_ID);
            if (player.getHealth() > player.getMaxHealth()) {
               player.setHealth(player.getMaxHealth());
            }
         }
      }
   }

   private static boolean isGolem(ItemStack stack) {
      return stack.getItem() instanceof TotemItem totem && totem.getKind() == TotemItem.Kind.GOLEM;
   }
}
