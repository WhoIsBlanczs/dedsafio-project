package net.owleafstudio.modminds.procedures;

import javax.annotation.Nullable;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.init.ModmindsModMobEffects;
import net.owleafstudio.modminds.network.ModmindsModVariables;

@Mod.EventBusSubscriber
public class SiempreActivoZombificacionProcedure {
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
      if (entity != null
         && ModmindsModVariables.MapVariables.get(world).PersistenciaZombificacion
         && !(entity instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(CompatAccess.effect(ModmindsModMobEffects.ZOMBIFICACION.get())))) {
         if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(CompatAccess.effect(ModmindsModMobEffects.ZOMBIFICACION.get()), 2400, 0, false, false));
         }

         if (entity instanceof LivingEntity _livEnt2
            && _livEnt2.hasEffect(CompatAccess.effect(ModmindsModMobEffects.PILDORA_ZOMBIFICACION.get()))
            && entity instanceof LivingEntity _entity) {
            _entity.removeEffect(CompatAccess.effect(ModmindsModMobEffects.ZOMBIFICACION.get()));
         }
      }
   }
}
