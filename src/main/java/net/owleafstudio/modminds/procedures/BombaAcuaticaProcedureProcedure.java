package net.owleafstudio.modminds.procedures;

import java.util.Comparator;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.Level;
import net.owleafstudio.modminds.compat.CompatAccess;

public class BombaAcuaticaProcedureProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
      if (entity != null && sourceentity != null) {
         Vec3 _center = new Vec3(x, y, z);

         for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(25.0), e -> true)
            .stream()
            .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
            .toList()) {
            if ((entityiterator instanceof ServerPlayer || entityiterator instanceof Player)
               && entityiterator.isInWater()
               && !(entityiterator instanceof Player _plr && _plr.getAbilities().instabuild)) {
               entityiterator.hurt(
                  new DamageSource(
                     world.registryAccess()
                        .lookupOrThrow(Registries.DAMAGE_TYPE)
                        .getOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, Identifier.parse("modminds:bomba_acuatica_dano")))
                  ),
                  entityiterator instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1.0F
               );
               if (!sourceentity.level().isClientSide() && CompatAccess.server(sourceentity) != null) {
                  CompatAccess.server(sourceentity)
                     .getCommands()
                     .performPrefixedCommand(
                        net.owleafstudio.modminds.compat.Compat.fuente(CommandSource.NULL, sourceentity.position(), sourceentity.getRotationVector(), sourceentity.level() instanceof ServerLevel ? (ServerLevel)sourceentity.level() : null, 4, sourceentity.getName().getString(), sourceentity.getDisplayName(), sourceentity.level().getServer(), sourceentity),
                        "/tellraw @a [\"\",{\"text\":\"☎ \",\"bold\":true,\"color\":\"gold\"},{\"selector\":\"@s\",\"bold\":true,\"color\":\"gold\"},{\"text\":\" ha explotado la \",\"color\":\"gold\"},{\"text\":\"Bomba Acuática.\",\"bold\":true,\"color\":\"gold\"}]"
                     );
               }

               if (!entity.level().isClientSide()) {
                  entity.discard();
               }

               if (world instanceof Level _level && !_level.isClientSide()) {
                  _level.explode(null, x, y, z, 1.0F, Level.ExplosionInteraction.NONE);
               }
            }
         }
      }
   }
}
