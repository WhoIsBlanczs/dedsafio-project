package net.owleafstudio.modminds.procedures;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.item.PerforadorDelVacioItem;

public class PerforadorDelVacioClickDerechoProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 10, 5, true, false));
         }

         if ((
                  entity instanceof LivingEntity _livEntx && _livEntx.hasEffect(MobEffects.SLOWNESS)
                     ? _livEntx.getEffect(MobEffects.SLOWNESS).getAmplifier()
                     : 0
               )
               == 5
            && (entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() instanceof PerforadorDelVacioItem) {
            CompatAccess.itemTag(entity instanceof LivingEntity _livEntxx ? _livEntxx.getMainHandItem() : ItemStack.EMPTY).putString("geckoAnim", "point");
         }
      }
   }
}
