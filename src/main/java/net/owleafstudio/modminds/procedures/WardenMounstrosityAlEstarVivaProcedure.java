package net.owleafstudio.modminds.procedures;

import java.util.Comparator;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerPlayer;

public class WardenMounstrosityAlEstarVivaProcedure {
   public static void execute(LevelAccessor world, Entity entity) {
      if (entity != null) {
         Vec3 _center = new Vec3(entity.getX(), entity.getY(), entity.getZ());

         for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(25.0), e -> true)
            .stream()
            .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
            .toList()) {
            if ((entityiterator instanceof Player || entityiterator instanceof ServerPlayer)
               && !(entityiterator instanceof Player _plr && _plr.getAbilities().instabuild)
               && entityiterator instanceof LivingEntity _entity
               && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 60, 1, true, false));
            }
         }
      }
   }
}
