package net.owleafstudio.modminds.procedures;

import javax.annotation.Nullable;
import net.minecraft.world.effect.MobEffects;
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
public class ZombificacionCuraProcedure {
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
         && !ModmindsModVariables.MapVariables.get(world).PersistenciaZombificacion
         && ModmindsModVariables.MapVariables.get(world).ZombificacionPorZombi
         && entity instanceof LivingEntity _livEnt0
         && _livEnt0.hasEffect(CompatAccess.effect(ModmindsModMobEffects.ZOMBIFICACION.get()))
         && entity instanceof LivingEntity _livEnt1
         && _livEnt1.hasEffect(MobEffects.WEAKNESS)
         && entity instanceof LivingEntity _livEnt2
         && _livEnt2.hasEffect(MobEffects.REGENERATION)
         && entity instanceof LivingEntity _entity) {
         _entity.removeEffect(CompatAccess.effect(ModmindsModMobEffects.ZOMBIFICACION.get()));
      }
   }
}
