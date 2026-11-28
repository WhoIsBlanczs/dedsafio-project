package com.ghostsafio.mitomod;

import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class GlebaPad {
   public static final int RADIUS = 26;
   private static final int FLAGS = 18;

   public static int find(ServerLevel level) {
      MutableBlockPos pos = new MutableBlockPos();

      for (int y = 260; y > level.getMinY(); y--) {
         pos.set(0, y, 0);
         if (level.getBlockState(pos).is(Blocks.CHISELED_DEEPSLATE)) {
            return y;
         }
      }

      return Integer.MIN_VALUE;
   }

   private static int groundY(ServerLevel level, MutableBlockPos pos, int x, int z) {
      for (int y = 150; y > level.getMinY(); y--) {
         pos.set(x, y, z);
         BlockState st = level.getBlockState(pos);
         if (!st.isAir() && st.getFluidState().isEmpty()) {
            return y;
         }
      }

      return level.getMinY();
   }

   public static int ensure(ServerLevel level) {
      int existing = find(level);
      if (existing != Integer.MIN_VALUE) {
         return existing;
      }

      MutableBlockPos pos = new MutableBlockPos();
      long sum = 0L;
      int count = 0;

      for (int x = -26; x <= 26; x += 4) {
         for (int z = -26; z <= 26; z += 4) {
            if (x * x + z * z <= 676) {
               sum += groundY(level, pos, x, z);
               count++;
            }
         }
      }

      int padY = Math.max(68, (int)(sum / Math.max(1, count)));
      BlockState air = Blocks.AIR.defaultBlockState();
      BlockState fill = ModBlocks.BLOQUE_GLEBA.defaultBlockState();
      BlockState rim = Blocks.POLISHED_DEEPSLATE.defaultBlockState();
      BlockState base = Blocks.CONCRETE.pick(net.minecraft.world.item.DyeColor.GRAY).defaultBlockState();
      BlockState line = Blocks.CONCRETE.pick(net.minecraft.world.item.DyeColor.LIGHT_GRAY).defaultBlockState();
      BlockState under = Blocks.DEEPSLATE_TILES.defaultBlockState();
      BlockState center = Blocks.CHISELED_DEEPSLATE.defaultBlockState();
      int r = 26;

      for (int x = -r - 1; x <= r + 1; x++) {
         for (int z = -r - 1; z <= r + 1; z++) {
            int d2 = x * x + z * z;
            if (d2 <= (r + 1) * (r + 1)) {
               for (int y = padY + 1; y <= padY + 45; y++) {
                  pos.set(x, y, z);
                  if (!level.getBlockState(pos).isAir()) {
                     level.setBlock(pos, air, 18);
                  }
               }

               for (int y = padY - 2; y > padY - 90 && y > level.getMinY(); y--) {
                  pos.set(x, y, z);
                  BlockState below = level.getBlockState(pos);
                  if (!below.isAir() && below.getFluidState().isEmpty()) {
                     break;
                  }

                  level.setBlock(pos, fill, 18);
               }

               pos.set(x, padY - 1, z);
               level.setBlock(pos, under, 18);
               BlockState top;
               if (x == 0 && z == 0) {
                  top = center;
               } else if (d2 > (r - 2) * (r - 2)) {
                  top = rim;
               } else if (Math.floorMod(x, 6) != 0 && Math.floorMod(z, 6) != 0) {
                  top = base;
               } else {
                  top = line;
               }

               pos.set(x, padY, z);
               level.setBlock(pos, top, 18);
            }
         }
      }

      return padY;
   }
}
