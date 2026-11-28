package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.owleafstudio.modminds.compat.CompatAccess;

public class BoneSpiderAlEstarVivaProcedure {
   public static void execute(LevelAccessor world, Entity entity) {
      if (entity != null) {
         if (world.getBlockState(BlockPos.containing(entity.getX(), entity.getY(), entity.getZ())).getBlock()
            == Blocks.COBWEB) {
            if (!entity.level().isClientSide() && CompatAccess.server(entity) != null) {
               CompatAccess.server(entity)
                  .getCommands()
                  .performPrefixedCommand(
                     net.owleafstudio.modminds.compat.Compat.fuente(CommandSource.NULL, entity.position(), entity.getRotationVector(), entity.level() instanceof ServerLevel ? (ServerLevel)entity.level() : null, 4, entity.getName().getString(), entity.getDisplayName(), entity.level().getServer(), entity),
                     "/attribute @s minecraft:generic.movement_speed base set 1.2"
                  );
            }
         } else if (!entity.level().isClientSide() && CompatAccess.server(entity) != null) {
            CompatAccess.server(entity)
               .getCommands()
               .performPrefixedCommand(
                  net.owleafstudio.modminds.compat.Compat.fuente(CommandSource.NULL, entity.position(), entity.getRotationVector(), entity.level() instanceof ServerLevel ? (ServerLevel)entity.level() : null, 4, entity.getName().getString(), entity.getDisplayName(), entity.level().getServer(), entity),
                  "/attribute @s minecraft:generic.movement_speed base set 0.3"
               );
         }
      }
   }
}
