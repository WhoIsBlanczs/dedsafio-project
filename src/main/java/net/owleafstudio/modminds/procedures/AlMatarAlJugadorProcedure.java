package net.owleafstudio.modminds.procedures;

import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.owleafstudio.modminds.entity.GorgonaEntity;
import net.owleafstudio.modminds.init.ModmindsModBlocks;

@Mod.EventBusSubscriber
public class AlMatarAlJugadorProcedure {
   @SubscribeEvent
   public static void onEntityDeath(LivingDeathEvent event) {
      if (event != null && event.getEntity() != null) {
         execute(
            event,
            event.getEntity().level(),
            event.getEntity().getX(),
            event.getEntity().getY(),
            event.getEntity().getZ(),
            event.getEntity()
         );
      }
   }

   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      execute(null, world, x, y, z, entity);
   }

   private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         double RandomStatua = 0.0;
         RandomStatua = Mth.nextInt(RandomSource.create(), 1, 3);
         if (!world.getEntitiesOfClass(GorgonaEntity.class, AABB.ofSize(new Vec3(x, y, z), 20.0, 20.0, 20.0), e -> true).isEmpty()
            && (entity instanceof Player || entity instanceof ServerPlayer)
            && !entity.isAlive()) {
            if (RandomStatua == 1.0) {
               world.setBlock(BlockPos.containing(x, y, z), ModmindsModBlocks.ESTATUA_ECHADA.get().defaultBlockState(), 3);
            } else if (RandomStatua == 2.0) {
               world.setBlock(BlockPos.containing(x, y, z), ModmindsModBlocks.ESTATUA_SENTADO.get().defaultBlockState(), 3);
            } else if (RandomStatua == 3.0) {
               world.setBlock(BlockPos.containing(x, y, z), ModmindsModBlocks.ESTATUA_CRUCIFICADO.get().defaultBlockState(), 3);
            }
         }
      }
   }
}
