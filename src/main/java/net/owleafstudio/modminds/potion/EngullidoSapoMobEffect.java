package net.owleafstudio.modminds.potion;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.owleafstudio.modminds.procedures.EngullidoSapoAlEstarActivoProcedure;
import net.owleafstudio.modminds.procedures.EngullidoSapoCuandoElEfectoEmpiezaAAplicarseProcedure;

public class EngullidoSapoMobEffect extends MobEffect {
   public EngullidoSapoMobEffect() {
      super(MobEffectCategory.NEUTRAL, -16724992);
   }

   public List<ItemStack> getCurativeItems() {
      ArrayList<ItemStack> cures = new ArrayList<>();
      cures.add(new ItemStack(Items.TOTEM_OF_UNDYING));
      return cures;
   }

   public void addAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
      EngullidoSapoCuandoElEfectoEmpiezaAAplicarseProcedure.execute(entity);
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      EngullidoSapoAlEstarActivoProcedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity);
      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }
}
