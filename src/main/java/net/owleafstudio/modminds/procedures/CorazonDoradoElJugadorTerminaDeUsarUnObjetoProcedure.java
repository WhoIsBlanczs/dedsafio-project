package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.owleafstudio.modminds.init.ModmindsModItems;

public class CorazonDoradoElJugadorTerminaDeUsarUnObjetoProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         if ((entity instanceof LivingEntity _entGetArmorxxxxx ? _entGetArmorxxxxx.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY).getItem()
               != ModmindsModItems.ARMADURA_INFERNAL_HELMET.get()
            && (entity instanceof LivingEntity _entGetArmorxxxx ? _entGetArmorxxxx.getItemBySlot(EquipmentSlot.FEET) : ItemStack.EMPTY).getItem()
               != ModmindsModItems.ARMADURA_INFERNAL_BOOTS.get()
            && (entity instanceof LivingEntity _entGetArmorxxx ? _entGetArmorxxx.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY).getItem()
               != ModmindsModItems.ARMADURAS_NAONEIM_HELMET.get()
            && (entity instanceof LivingEntity _entGetArmorxx ? _entGetArmorxx.getItemBySlot(EquipmentSlot.FEET) : ItemStack.EMPTY).getItem()
               != ModmindsModItems.ARMADURAS_NAONEIM_BOOTS.get()
            && (entity instanceof LivingEntity _entGetArmorx ? _entGetArmorx.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY).getItem()
               != ModmindsModItems.ARMADURA_DIMENSIONAL_HELMET.get()
            && (entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.FEET) : ItemStack.EMPTY).getItem()
               != ModmindsModItems.ARMADURA_DIMENSIONAL_BOOTS.get()
            && (
                  entity instanceof LivingEntity _livingEntity12 && _livingEntity12.getAttributes().hasAttribute(Attributes.MAX_HEALTH)
                     ? _livingEntity12.getAttribute(Attributes.MAX_HEALTH).getBaseValue()
                     : 0.0
               )
               <= 20.0) {
            if (entity instanceof LivingEntity _livingEntity13 && _livingEntity13.getAttributes().hasAttribute(Attributes.MAX_HEALTH)) {
               _livingEntity13.getAttribute(Attributes.MAX_HEALTH).setBaseValue(20.0);
            }
         } else if ((entity instanceof LivingEntity _entGetArmorxxxxxxx ? _entGetArmorxxxxxxx.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY)
                  .getItem()
               == ModmindsModItems.ARMADURA_INFERNAL_HELMET.get()
            && (entity instanceof LivingEntity _entGetArmorxxxxxx ? _entGetArmorxxxxxx.getItemBySlot(EquipmentSlot.FEET) : ItemStack.EMPTY).getItem()
               == ModmindsModItems.ARMADURA_INFERNAL_BOOTS.get()
            && (
                  entity instanceof LivingEntity _livingEntity18 && _livingEntity18.getAttributes().hasAttribute(Attributes.MAX_HEALTH)
                     ? _livingEntity18.getAttribute(Attributes.MAX_HEALTH).getBaseValue()
                     : 0.0
               )
               <= 24.0) {
            if (entity instanceof LivingEntity _livingEntity19 && _livingEntity19.getAttributes().hasAttribute(Attributes.MAX_HEALTH)) {
               _livingEntity19.getAttribute(Attributes.MAX_HEALTH).setBaseValue(24.0);
            }
         } else if ((entity instanceof LivingEntity _entGetArmorxxxxxxxxx ? _entGetArmorxxxxxxxxx.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY)
                  .getItem()
               == ModmindsModItems.ARMADURAS_NAONEIM_HELMET.get()
            && (entity instanceof LivingEntity _entGetArmorxxxxxxxx ? _entGetArmorxxxxxxxx.getItemBySlot(EquipmentSlot.FEET) : ItemStack.EMPTY)
                  .getItem()
               == ModmindsModItems.ARMADURAS_NAONEIM_BOOTS.get()
            && (
                  entity instanceof LivingEntity _livingEntity24 && _livingEntity24.getAttributes().hasAttribute(Attributes.MAX_HEALTH)
                     ? _livingEntity24.getAttribute(Attributes.MAX_HEALTH).getBaseValue()
                     : 0.0
               )
               <= 28.0) {
            if (entity instanceof LivingEntity _livingEntity25 && _livingEntity25.getAttributes().hasAttribute(Attributes.MAX_HEALTH)) {
               _livingEntity25.getAttribute(Attributes.MAX_HEALTH).setBaseValue(28.0);
            }
         } else if ((entity instanceof LivingEntity _entGetArmorxxxxxxxxxxx ? _entGetArmorxxxxxxxxxxx.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY)
                  .getItem()
               == ModmindsModItems.ARMADURA_DIMENSIONAL_HELMET.get()
            && (entity instanceof LivingEntity _entGetArmorxxxxxxxxxx ? _entGetArmorxxxxxxxxxx.getItemBySlot(EquipmentSlot.FEET) : ItemStack.EMPTY)
                  .getItem()
               == ModmindsModItems.ARMADURA_DIMENSIONAL_BOOTS.get()
            && (
                  entity instanceof LivingEntity _livingEntity30 && _livingEntity30.getAttributes().hasAttribute(Attributes.MAX_HEALTH)
                     ? _livingEntity30.getAttribute(Attributes.MAX_HEALTH).getBaseValue()
                     : 0.0
               )
               <= 36.0
            && entity instanceof LivingEntity _livingEntity31
            && _livingEntity31.getAttributes().hasAttribute(Attributes.MAX_HEALTH)) {
            _livingEntity31.getAttribute(Attributes.MAX_HEALTH).setBaseValue(36.0);
         }
      }
   }
}
