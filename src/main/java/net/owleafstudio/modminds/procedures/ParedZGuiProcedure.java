package net.owleafstudio.modminds.procedures;

import java.util.Comparator;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.entity.OrbEntity;

public class ParedZGuiProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z) {
      Vec3 _center = new Vec3(x, y, z);

      for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(4.0), e -> true)
         .stream()
         .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
         .toList()) {
         if (entityiterator instanceof OrbEntity) {
            if (!entityiterator.level().isClientSide() && CompatAccess.server(entityiterator) != null) {
               CompatAccess.server(entityiterator)
                  .getCommands()
                  .performPrefixedCommand(
                     net.owleafstudio.modminds.compat.Compat.fuente(CommandSource.NULL, entityiterator.position(), entityiterator.getRotationVector(), entityiterator.level() instanceof ServerLevel ? (ServerLevel)entityiterator.level() : null, 4, entityiterator.getName().getString(), entityiterator.getDisplayName(), entityiterator.level().getServer(), entityiterator),
                     "/fill ~ ~25 ~25 ~ ~-25 ~-25 minecraft:barrier replace minecraft:air"
                  );
            }

            entityiterator.setYRot(90.0F);
            entityiterator.setXRot(0.0F);
            entityiterator.setYBodyRot(entityiterator.getYRot());
            entityiterator.setYHeadRot(entityiterator.getYRot());
            entityiterator.yRotO = entityiterator.getYRot();
            entityiterator.xRotO = entityiterator.getXRot();
            if (entityiterator instanceof LivingEntity _entity) {
               _entity.yBodyRotO = _entity.getYRot();
               _entity.yHeadRotO = _entity.getYRot();
            }

            if (entityiterator instanceof OrbEntity) {
               ((OrbEntity)entityiterator).setAnimation("entityeufonia.orb.barrier_open");
            }
         }
      }
   }
}
