package com.novastudio.dedsafio4;

import java.util.Random;
import java.util.Set;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.EndTick;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;

public final class TotemsPlus implements ModInitializer {
   public static Item TOTEM_NUTRI;
   public static Item TOTEM_GOLEM;
   public static Item TOTEM_EON;
   public static Item TOTEM_YUNQUE;
   public static Item TOTEM_GLEBA;
   private static final Random RNG = new Random();
   private static final Identifier GOLEM_MODIFIER = Identifier.fromNamespaceAndPath("nexo", "totem_golem_health");
   private static final Set<String> HAZARDS = Set.of(
      "magma_block",
      "cactus",
      "campfire",
      "soul_campfire",
      "fire",
      "soul_fire",
      "lava",
      "sweet_berry_bush",
      "wither_rose",
      "powder_snow",
      "cobweb",
      "bubble_column",
      "pointed_dripstone"
   );

   private static Item totem(String id) {
      return (Item)Registry.register(BuiltInRegistries.ITEM, com.novastudio.dedsafio4.compat.Ids.item(Identifier.fromNamespaceAndPath("nexo", id)), new Item(new Item.Properties().stacksTo(1)));
   }

   public void onInitialize() {
      TOTEM_NUTRI = totem("totem_nutri");
      TOTEM_GOLEM = totem("totem_hierro_golem");
      TOTEM_EON = totem("totem_eon");
      TOTEM_YUNQUE = totem("totem_yunque");
      TOTEM_GLEBA = totem("totem_gleba");
      ServerLivingEntityEvents.ALLOW_DEATH.register(TotemsPlus::allowDeath);
      ServerTickEvents.END_SERVER_TICK.register((EndTick)server -> {
         for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            applyGolem(player);
         }
      });
   }

   private static boolean allowDeath(LivingEntity entity, DamageSource source, float amount) {
      if (!(entity instanceof ServerPlayer player)) {
         return true;
      } else {
         Item[] totems = new Item[]{TOTEM_NUTRI, TOTEM_GOLEM, TOTEM_EON, TOTEM_YUNQUE, TOTEM_GLEBA};
         String[] kinds = new String[]{"nutri", "golem", "eon", "yunque", "gleba"};
         ItemStack found = null;
         String kind = null;

         label53:
         for (ItemStack held : new ItemStack[]{player.getOffhandItem(), player.getMainHandItem()}) {
            for (int i = 0; i < totems.length; i++) {
               if (held.is(totems[i])) {
                  found = held;
                  kind = kinds[i];
                  break label53;
               }
            }
         }

         if (found == null) {
            Inventory inventory = player.getInventory();

            for (int ix = 0; ix < inventory.getContainerSize(); ix++) {
               ItemStack stack = inventory.getItem(ix);
               if (stack.is(TOTEM_EON)) {
                  found = stack;
                  kind = "eon";
                  break;
               }
            }
         }

         if (found == null) {
            return true;
         } else {
            found.shrink(1);
            vanillaRecovery(player);
            if (kind.equals("yunque")) {
               repairArmor(player);
            } else if (kind.equals("gleba")) {
               teleportSafe(player);
            }

            return false;
         }
      }
   }

   private static void vanillaRecovery(ServerPlayer player) {
      player.setHealth(1.0F);
      player.removeAllEffects();
      player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
      player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
      player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
      player.level().broadcastEntityEvent(player, (byte)35);
   }

   private static void repairArmor(ServerPlayer player) {
      for (net.minecraft.world.entity.EquipmentSlot slot : net.minecraft.world.entity.EquipmentSlot.values()) {
         if (slot.getType() != net.minecraft.world.entity.EquipmentSlot.Type.HUMANOID_ARMOR) continue;
         ItemStack stack = player.getItemBySlot(slot);
         if (!stack.isEmpty() && stack.isDamaged()) {
            stack.setDamageValue(0);
         }
      }
   }

   private static void teleportSafe(ServerPlayer player) {
      Level level = player.level();
      double x = player.getX();
      double z = player.getZ();
      int minY = level.getMinY();
      int maxY = level.getMaxY();
      int baseY = (int)Math.floor(player.getY());

      for (int attempt = 0; attempt < 80; attempt++) {
         double angle = RNG.nextDouble() * Math.PI * 2.0;
         double distance = 2.0 + RNG.nextDouble() * 13.0;
         int bx = (int)Math.floor(x + Math.cos(angle) * distance);
         int bz = (int)Math.floor(z + Math.sin(angle) * distance);

         for (int by = Math.min(maxY - 2, baseY + 10); by >= Math.max(minY + 1, baseY - 16); by--) {
            BlockPos pos = new BlockPos(bx, by, bz);
            if (level.getBlockState(pos).isAir() && level.getBlockState(pos.above()).isAir()) {
               BlockPos below = pos.below();
               BlockState support = level.getBlockState(below);
               if (!support.isAir()) {
                  if (support.getFluidState().isEmpty() && support.isFaceSturdy(level, below, Direction.UP)) {
                     String name = BuiltInRegistries.BLOCK.getKey(support.getBlock()).getPath();
                     if (!HAZARDS.contains(name)) {
                        player.connection.teleport(bx + 0.5, by, bz + 0.5, player.getYRot(), player.getXRot());
                        player.fallDistance = 0.0F;
                        return;
                     }
                  }
                  break;
               }
            }
         }
      }
   }

   private static void applyGolem(ServerPlayer player) {
      boolean holding = player.getMainHandItem().is(TOTEM_GOLEM) || player.getOffhandItem().is(TOTEM_GOLEM);
      AttributeInstance health = player.getAttribute(Attributes.MAX_HEALTH);
      if (health != null) {
         boolean applied = health.hasModifier(GOLEM_MODIFIER);
         if (holding && !applied) {
            health.addTransientModifier(new AttributeModifier(GOLEM_MODIFIER, 4.0, AttributeModifier.Operation.ADD_VALUE));
         } else if (!holding && applied) {
            health.removeModifier(GOLEM_MODIFIER);
            if (player.getHealth() > player.getMaxHealth()) {
               player.setHealth(player.getMaxHealth());
            }
         }
      }
   }
}
