package net.ruleta.creeperes;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.ruleta.Ajustes;
import net.ruleta.Ruleta;
import net.ruleta.compat.BlockEvent;
import net.ruleta.compat.MobSpawnEvent;
import net.ruleta.compat.SubscribeEvent;
import net.ruleta.creeperrojo.CreeperRojo;
import net.ruleta.creeperrojo.CreeperRojoEntity;

public final class MobsNuevos {
   private static final float PROB_PASTO = 0.08F;
   private static final float PROB_ROJO = 0.25F;

   private MobsNuevos() {
   }

   private static boolean esHierba(BlockState s) {
      Block b = s.getBlock();
      return b == Blocks.GRASS_BLOCK
         || b == Blocks.SHORT_GRASS
         || b == Blocks.TALL_GRASS
         || b == Blocks.FERN
         || b == Blocks.LARGE_FERN
         || b == Blocks.DEAD_BUSH
         || b == Blocks.SWEET_BERRY_BUSH
         || b == Blocks.AZALEA
         || b == Blocks.FLOWERING_AZALEA
         || b == Blocks.SEAGRASS
         || b == Blocks.TALL_SEAGRASS
         || b == Blocks.NETHER_SPROUTS
         || b == Blocks.CRIMSON_ROOTS
         || b == Blocks.WARPED_ROOTS
         || b == Blocks.HANGING_ROOTS
         || b == Blocks.MOSS_CARPET;
   }

   @SubscribeEvent
   public static void alRomper(BlockEvent.BreakEvent e) {
      if (!e.isCanceled() && e.getPlayer() != null && !e.getPlayer().isCreative() && e.getLevel() instanceof ServerLevel nivel) {
         if (esHierba(e.getState())
            && Ruleta.cambioActivoPublico("mob-creeper-pasto")
            && !(nivel.getRandom().nextFloat() >= Ajustes.entero("mobs.prob_pasto") / 100.0F)) {
            CreeperCespedEntity c = (CreeperCespedEntity)Creeperes.CESPED.get().create(nivel, net.minecraft.world.entity.EntitySpawnReason.EVENT);
            if (c != null) {
               c.snapTo(e.getPos().getX() + 0.5, e.getPos().getY(), e.getPos().getZ() + 0.5, nivel.getRandom().nextFloat() * 360.0F, 0.0F);
               c.finalizeSpawn(nivel, nivel.getCurrentDifficultyAt(e.getPos()), EntitySpawnReason.EVENT, null);
               nivel.addFreshEntity(c);
            }
         }
      }
   }

   @SubscribeEvent
   public static void alAparecer(MobSpawnEvent.FinalizeSpawn e) {
      if (e.getEntity().getType() == net.minecraft.world.entity.EntityTypes.CREEPER && !e.isSpawnCancelled()) {
         EntitySpawnReason razon = e.getSpawnType();
         if (razon == EntitySpawnReason.NATURAL || razon == EntitySpawnReason.CHUNK_GENERATION) {
            if (Ruleta.cambioActivoPublico("mob-creeper-rojo") && !(e.getEntity().getRandom().nextFloat() >= Ajustes.entero("mobs.prob_rojo") / 100.0F)) {
               ServerLevel nivel = e.getLevel().getLevel();
               CreeperRojoEntity rojo = (CreeperRojoEntity)CreeperRojo.TIPO.get().create(nivel, net.minecraft.world.entity.EntitySpawnReason.EVENT);
               if (rojo != null) {
                  e.setSpawnCancelled(true);
                  rojo.snapTo(e.getX(), e.getY(), e.getZ(), e.getEntity().getYRot(), 0.0F);
                  rojo.finalizeSpawn(e.getLevel(), e.getDifficulty(), razon, null);
                  e.getLevel().addFreshEntity(rojo);
               }
            }
         }
      }
   }
}
