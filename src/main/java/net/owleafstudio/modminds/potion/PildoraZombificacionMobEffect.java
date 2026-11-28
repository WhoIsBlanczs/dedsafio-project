package net.owleafstudio.modminds.potion;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.owleafstudio.modminds.procedures.PildoraZombificacionAlEstarActivoElEfectoProcedure;
import net.owleafstudio.modminds.procedures.ZombificacionAlFinalizarElEfectoProcedure;

public class PildoraZombificacionMobEffect extends MobEffect {
   public PildoraZombificacionMobEffect() {
      super(MobEffectCategory.BENEFICIAL, -26215);
   }

   public List<ItemStack> getCurativeItems() {
      return new ArrayList<>();
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      PildoraZombificacionAlEstarActivoElEfectoProcedure.execute(entity);
      return true;
   }

   public void onMobRemoved(net.minecraft.server.level.ServerLevel nivel$, LivingEntity entity, int amplifier, Entity.RemovalReason reason) {
      super.onMobRemoved(nivel$, entity, amplifier, reason);
      ZombificacionAlFinalizarElEfectoProcedure.execute(entity.level(), entity);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }
}
