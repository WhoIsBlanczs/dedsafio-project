package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.init.ModmindsModMobEffects;

@Mod.EventBusSubscriber
public class ZombieInventarioJugadorProcedure {
   @SubscribeEvent
   public static void onPlayerDeath(LivingDeathEvent event) {
      LivingEntity entity = event.getEntity();
      Level level = entity.level();
      if (entity instanceof Player player
         && !player.isAlive()
         && !(player.getHealth() > 0.0F)
         && (
            player.hasEffect(CompatAccess.effect(ModmindsModMobEffects.PILDORA_ZOMBIFICACION.get()))
               || player.hasEffect(CompatAccess.effect(ModmindsModMobEffects.ZOMBIFICACION.get()))
         )) {
         Zombie zombie = new Zombie(level);
         zombie.snapTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
         zombie.setCustomName(player.getName());
         zombie.setCustomNameVisible(true);

         for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR || slot == EquipmentSlot.OFFHAND) {
               ItemStack item = player.getItemBySlot(slot);
               if (!item.isEmpty()) {
                  zombie.setItemSlot(slot, item);
                  zombie.setDropChance(slot, 0.0F);
               }
            }
         }

         CompoundTag inventoryTag = new CompoundTag();

         for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty()) {
               inventoryTag.put("Slot_" + i, ItemStack.CODEC.encodeStart(level.registryAccess().createSerializationContext(net.minecraft.nbt.NbtOps.INSTANCE), stack).getOrThrow());
            }
         }

         CompatAccess.persistentData(zombie).put("PlayerInventory", inventoryTag);
         player.getInventory().clearContent();
         level.addFreshEntity(zombie);
      }
   }

   @SubscribeEvent
   public static void onZombieDeath(LivingDeathEvent event) {
      if (event.getEntity() instanceof Zombie zombie && CompatAccess.persistentData(zombie).contains("PlayerInventory")) {
         CompoundTag inventoryTag = CompatAccess.persistentData(zombie).getCompoundOrEmpty("PlayerInventory");

         for (String key : inventoryTag.keySet()) {
            ItemStack stack = ItemStack.OPTIONAL_CODEC.parse(zombie.level().registryAccess().createSerializationContext(net.minecraft.nbt.NbtOps.INSTANCE), inventoryTag.getCompoundOrEmpty(key)).result().orElse(ItemStack.EMPTY);
            if (!stack.isEmpty()) {
               zombie.spawnAtLocation((net.minecraft.server.level.ServerLevel)zombie.level(), stack);
            }
         }
      }
   }
}
