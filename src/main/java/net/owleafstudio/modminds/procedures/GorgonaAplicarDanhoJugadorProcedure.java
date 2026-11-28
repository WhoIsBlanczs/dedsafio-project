package net.owleafstudio.modminds.procedures;

import javax.annotation.Nullable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.owleafstudio.modminds.entity.GorgonaEntity;

@Mod.EventBusSubscriber
public class GorgonaAplicarDanhoJugadorProcedure {
   @SubscribeEvent
   public static void onEntityAttacked(LivingAttackEvent event) {
      if (event != null && event.getEntity() != null) {
         execute(event, event.getEntity().level(), event.getEntity(), event.getSource().getEntity());
      }
   }

   public static void execute(LevelAccessor world, Entity entity, Entity sourceentity) {
      execute(null, world, entity, sourceentity);
   }

   private static void execute(@Nullable Event event, LevelAccessor world, Entity entity, Entity sourceentity) {
      if (entity != null && sourceentity != null) {
         if (sourceentity instanceof GorgonaEntity && (entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1.0F) <= 4.0F) {
            entity.hurt(
               new DamageSource(
                  world.registryAccess()
                     .lookupOrThrow(Registries.DAMAGE_TYPE)
                     .getOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, Identifier.parse("modminds:gorgona_atack")))
               ),
               (entity instanceof LivingEntity _livEntx ? _livEntx.getHealth() : -1.0F) + 4.0F
            );
         } else if (sourceentity instanceof GorgonaEntity
            && (entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1.0F) > 4.0F
            && entity instanceof LivingEntity _entity) {
            _entity.setHealth((entity instanceof LivingEntity _livEntx ? _livEntx.getHealth() : -1.0F) - 4.0F);
         }
      }
   }
}
