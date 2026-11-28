package net.owleafstudio.modminds.potion;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.owleafstudio.modminds.procedures.TexturaGorgonaCongeladoAlEstarActivoElEfectoProcedure;
import net.owleafstudio.modminds.procedures.TexturaGorgonaCongeladoAlFinalizarElEfectoProcedure;

public class TexturaGorgonaCongeladoMobEffect extends MobEffect {
   public TexturaGorgonaCongeladoMobEffect() {
      super(MobEffectCategory.BENEFICIAL, -1);
   }

   public boolean isInstantenous() {
      return true;
   }

   public List<ItemStack> getCurativeItems() {
      return new ArrayList<>();
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      TexturaGorgonaCongeladoAlEstarActivoElEfectoProcedure.execute(entity);
      return true;
   }

   public void onMobRemoved(net.minecraft.server.level.ServerLevel nivel$, LivingEntity entity, int amplifier, Entity.RemovalReason reason) {
      super.onMobRemoved(nivel$, entity, amplifier, reason);
      TexturaGorgonaCongeladoAlFinalizarElEfectoProcedure.execute(entity.level(), entity);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }
}
