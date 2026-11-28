package net.owleafstudio.modminds.procedures;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageTypes;
import net.owleafstudio.modminds.compat.CompatAccess;

public class AguaDelLimboAlEntidadColisionarConElBloqueProcedure {
   public static void execute(LevelAccessor world, Entity entity) {
      if (entity != null && !(entity instanceof Player _plr && _plr.getAbilities().instabuild)) {
         CompatAccess.persistentData(entity).putDouble("aguadellimbo", CompatAccess.persistentData(entity).getDoubleOr("aguadellimbo", 0.0) + 1.0);
         if (CompatAccess.persistentData(entity).getDoubleOr("aguadellimbo", 0.0) >= 40.0
            && (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1.0F) > 2.0F
            && (entity instanceof ServerPlayer || entity instanceof Player)) {
            if (entity instanceof LivingEntity _livingEntity8 && _livingEntity8.getAttributes().hasAttribute(Attributes.MAX_HEALTH)) {
               _livingEntity8.getAttribute(Attributes.MAX_HEALTH).setBaseValue((entity instanceof LivingEntity _livEntx ? _livEntx.getMaxHealth() : -1.0F) - 2.0F);
            }

            CompatAccess.persistentData(entity).putDouble("aguadellimbo", 0.0);
            if ((entity instanceof LivingEntity _livEntx ? _livEntx.getHealth() : -1.0F) > 2.0F) {
               entity.hurt(new DamageSource(world.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(DamageTypes.GENERIC)), 2.0F);
            }
         }
      }
   }
}
