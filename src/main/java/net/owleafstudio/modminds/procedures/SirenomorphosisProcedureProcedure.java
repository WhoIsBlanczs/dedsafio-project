package net.owleafstudio.modminds.procedures;

import javax.annotation.Nullable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.init.ModmindsModMobEffects;

@Mod.EventBusSubscriber
public class SirenomorphosisProcedureProcedure {
   @SubscribeEvent
   public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
      if (event.phase == TickEvent.Phase.END) {
         execute(event, event.player.level(), event.player);
      }
   }

   public static void execute(LevelAccessor world, Entity entity) {
      execute(null, world, entity);
   }

   private static void execute(@Nullable Event event, LevelAccessor world, Entity entity) {
      if (entity != null) {
         if (entity.isInWater()
            && entity instanceof LivingEntity _livEnt1
            && _livEnt1.hasEffect(CompatAccess.effect(ModmindsModMobEffects.SIRENOMORPHOSIS.get()))) {
            entity.setAirSupply(0);
            if (entity instanceof LivingEntity _livingEntity3 && _livingEntity3.getAttributes().hasAttribute(ForgeMod.SWIM_SPEED.get())) {
               _livingEntity3.getAttribute(ForgeMod.SWIM_SPEED.get()).setBaseValue(1.746);
            }

            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 30, 255, true, false));
            }
         } else if (entity instanceof LivingEntity _livEnt5 && _livEnt5.hasEffect(CompatAccess.effect(ModmindsModMobEffects.SIRENOMORPHOSIS.get()))) {
            if (entity instanceof LivingEntity _entity) {
               _entity.removeEffect(MobEffects.NIGHT_VISION);
            }

            if (entity instanceof LivingEntity _livingEntity7 && _livingEntity7.getAttributes().hasAttribute(ForgeMod.SWIM_SPEED.get())) {
               _livingEntity7.getAttribute(ForgeMod.SWIM_SPEED.get()).setBaseValue(1.0);
            }

            entity.setAirSupply((int)(entity.getAirSupply() + 0.6));
            if (entity.getAirSupply() > 10) {
               entity.hurt(new DamageSource(world.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(DamageTypes.DROWN)), 1.0F);
            }
         } else if (entity instanceof LivingEntity _livingEntity13 && _livingEntity13.getAttributes().hasAttribute(ForgeMod.SWIM_SPEED.get())) {
            _livingEntity13.getAttribute(ForgeMod.SWIM_SPEED.get()).setBaseValue(1.0);
         }
      }
   }
}
