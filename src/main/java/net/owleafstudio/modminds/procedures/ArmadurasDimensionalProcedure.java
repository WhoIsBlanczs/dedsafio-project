package net.owleafstudio.modminds.procedures;

import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.init.ModmindsModItems;
import net.owleafstudio.modminds.network.ModmindsModVariables;

@Mod.EventBusSubscriber
public class ArmadurasDimensionalProcedure {
   @SubscribeEvent
   public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
      if (event.phase == TickEvent.Phase.END) {
         execute(event, event.player);
      }
   }

   public static void execute(Entity entity) {
      execute(null, entity);
   }

   private static void execute(@Nullable Event event, Entity entity) {
      if (entity != null) {
         if (!ModmindsModVariables.getVariables(entity).orElse(new ModmindsModVariables.PlayerVariables()).CascoDimensional) {
            if ((entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY).getItem()
               == ModmindsModItems.ARMADURA_DIMENSIONAL_HELMET.get()) {
               boolean _setval = true;
               ModmindsModVariables.getVariables(entity).ifPresent(capability -> {
                  capability.CascoDimensional = _setval;
                  capability.syncPlayerVariables(entity);
               });
               if (!entity.level().isClientSide() && CompatAccess.server(entity) != null) {
                  CompatAccess.server(entity)
                     .getCommands()
                     .performPrefixedCommand(
                        net.owleafstudio.modminds.compat.Compat.fuente(CommandSource.NULL, entity.position(), entity.getRotationVector(), entity.level() instanceof ServerLevel ? (ServerLevel)entity.level() : null, 4, entity.getName().getString(), entity.getDisplayName(), entity.level().getServer(), entity),
                        "attribute @s minecraft:generic.max_health base set " + ((entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1.0F) + 8.0F)
                     );
               }
            }
         } else if ((entity instanceof LivingEntity _entGetArmorx ? _entGetArmorx.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY).getItem()
            != ModmindsModItems.ARMADURA_DIMENSIONAL_HELMET.get()) {
            boolean _setval = false;
            ModmindsModVariables.getVariables(entity).ifPresent(capability -> {
               capability.CascoDimensional = _setval;
               capability.syncPlayerVariables(entity);
            });
            if (!entity.level().isClientSide() && CompatAccess.server(entity) != null) {
               CompatAccess.server(entity)
                  .getCommands()
                  .performPrefixedCommand(
                     net.owleafstudio.modminds.compat.Compat.fuente(CommandSource.NULL, entity.position(), entity.getRotationVector(), entity.level() instanceof ServerLevel ? (ServerLevel)entity.level() : null, 4, entity.getName().getString(), entity.getDisplayName(), entity.level().getServer(), entity),
                     "attribute @s minecraft:generic.max_health base set " + ((entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1.0F) - 8.0F)
                  );
            }

            if ((entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1.0F) > 1.0F && entity instanceof LivingEntity _entity) {
               _entity.setHealth((entity instanceof LivingEntity _livEntx ? _livEntx.getHealth() : -1.0F) - 1.0F);
            }
         }

         if (!ModmindsModVariables.getVariables(entity).orElse(new ModmindsModVariables.PlayerVariables()).PetoDimensional) {
            if ((entity instanceof LivingEntity _entGetArmorx ? _entGetArmorx.getItemBySlot(EquipmentSlot.CHEST) : ItemStack.EMPTY).getItem()
               == ModmindsModItems.ARMADURA_DIMENSIONAL_CHESTPLATE.get()) {
               boolean _setvalx = true;
               ModmindsModVariables.getVariables(entity).ifPresent(capability -> {
                  capability.PetoDimensional = _setvalx;
                  capability.syncPlayerVariables(entity);
               });
            }
         } else if ((entity instanceof LivingEntity _entGetArmorx ? _entGetArmorx.getItemBySlot(EquipmentSlot.CHEST) : ItemStack.EMPTY).getItem()
            != ModmindsModItems.ARMADURA_DIMENSIONAL_CHESTPLATE.get()) {
            boolean _setvalx = false;
            ModmindsModVariables.getVariables(entity).ifPresent(capability -> {
               capability.PetoDimensional = _setvalx;
               capability.syncPlayerVariables(entity);
            });
         }

         if (!ModmindsModVariables.getVariables(entity).orElse(new ModmindsModVariables.PlayerVariables()).PantalonesDimensional) {
            if ((entity instanceof LivingEntity _entGetArmorx ? _entGetArmorx.getItemBySlot(EquipmentSlot.LEGS) : ItemStack.EMPTY).getItem()
               == ModmindsModItems.ARMADURA_DIMENSIONAL_LEGGINGS.get()) {
               boolean _setvalx = true;
               ModmindsModVariables.getVariables(entity).ifPresent(capability -> {
                  capability.PantalonesDimensional = _setvalx;
                  capability.syncPlayerVariables(entity);
               });
            }
         } else if ((entity instanceof LivingEntity _entGetArmorx ? _entGetArmorx.getItemBySlot(EquipmentSlot.LEGS) : ItemStack.EMPTY).getItem()
            != ModmindsModItems.ARMADURA_DIMENSIONAL_LEGGINGS.get()) {
            boolean _setvalx = false;
            ModmindsModVariables.getVariables(entity).ifPresent(capability -> {
               capability.PantalonesDimensional = _setvalx;
               capability.syncPlayerVariables(entity);
            });
         }

         if (!ModmindsModVariables.getVariables(entity).orElse(new ModmindsModVariables.PlayerVariables()).BotasDimensional) {
            if ((entity instanceof LivingEntity _entGetArmorx ? _entGetArmorx.getItemBySlot(EquipmentSlot.FEET) : ItemStack.EMPTY).getItem()
               == ModmindsModItems.ARMADURA_DIMENSIONAL_BOOTS.get()) {
               boolean _setvalx = true;
               ModmindsModVariables.getVariables(entity).ifPresent(capability -> {
                  capability.BotasDimensional = _setvalx;
                  capability.syncPlayerVariables(entity);
               });
               if (!entity.level().isClientSide() && CompatAccess.server(entity) != null) {
                  CompatAccess.server(entity)
                     .getCommands()
                     .performPrefixedCommand(
                        net.owleafstudio.modminds.compat.Compat.fuente(CommandSource.NULL, entity.position(), entity.getRotationVector(), entity.level() instanceof ServerLevel ? (ServerLevel)entity.level() : null, 4, entity.getName().getString(), entity.getDisplayName(), entity.level().getServer(), entity),
                        "attribute @s minecraft:generic.max_health base set " + ((entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1.0F) + 8.0F)
                     );
               }
            }
         } else if ((entity instanceof LivingEntity _entGetArmorxx ? _entGetArmorxx.getItemBySlot(EquipmentSlot.FEET) : ItemStack.EMPTY).getItem()
            != ModmindsModItems.ARMADURA_DIMENSIONAL_BOOTS.get()) {
            boolean _setvalx = false;
            ModmindsModVariables.getVariables(entity).ifPresent(capability -> {
               capability.BotasDimensional = _setvalx;
               capability.syncPlayerVariables(entity);
            });
            if (!entity.level().isClientSide() && CompatAccess.server(entity) != null) {
               CompatAccess.server(entity)
                  .getCommands()
                  .performPrefixedCommand(
                     net.owleafstudio.modminds.compat.Compat.fuente(CommandSource.NULL, entity.position(), entity.getRotationVector(), entity.level() instanceof ServerLevel ? (ServerLevel)entity.level() : null, 4, entity.getName().getString(), entity.getDisplayName(), entity.level().getServer(), entity),
                     "attribute @s minecraft:generic.max_health base set " + ((entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1.0F) - 8.0F)
                  );
            }

            if ((entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1.0F) > 1.0F && entity instanceof LivingEntity _entity) {
               _entity.setHealth((entity instanceof LivingEntity _livEntx ? _livEntx.getHealth() : -1.0F) - 1.0F);
            }
         }
      }
   }
}
