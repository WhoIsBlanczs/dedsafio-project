package net.ruleta.bunker;

import java.util.BitSet;
import java.util.UUID;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

final class Demolicion {
   private static final long PRESUPUESTO_NS = 4000000L;
   final Bunkers.Bunker bunker;
   final ServerLevel nivel;
   final UUID quien;
   private final BitSet solido;
   private final BitSet interior;
   private int y = 33;
   private int z = 0;
   private int x = 0;
   private long visitadas = 0L;
   private final long total = 323136L;

   Demolicion(ServerLevel nivel, Bunkers.Bunker bunker, BitSet solido, BitSet interior, UUID quien) {
      this.nivel = nivel;
      this.bunker = bunker;
      this.solido = solido;
      this.interior = interior;
      this.quien = quien;
   }

   int porcentaje() {
      return (int)Math.min(99L, this.visitadas * 100L / 323136L);
   }

   boolean paso() {
      long limite = System.nanoTime() + 4000000L;
      BlockState aire = Blocks.AIR.defaultBlockState();
      MutableBlockPos pos = new MutableBlockPos();
      int quitados = 0;
      double sx = 0.0;
      double sy = 0.0;
      double sz = 0.0;

      while (this.y >= 0) {
         int idx = (this.y * 99 + this.z) * 96 + this.x;
         if (this.solido.get(idx) || this.interior.get(idx)) {
            pos.set(this.bunker.ex() + this.x, this.bunker.ey() + this.y, this.bunker.ez() + this.z);
            if (!this.nivel.getBlockState(pos).isAir()) {
               this.nivel.setBlock(pos, aire, 18);
               quitados++;
               sx = pos.getX();
               sy = pos.getY();
               sz = pos.getZ();
            }
         }

         this.visitadas++;
         if (++this.x >= 96) {
            this.x = 0;
            if (++this.z >= 99) {
               this.z = 0;
               this.y--;
            }
         }

         if ((this.visitadas & 255L) == 0L && System.nanoTime() > limite) {
            break;
         }
      }

      if (quitados > 150) {
         this.nivel.playSound(null, sx, sy, sz, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 1.5F, 0.6F);
      }

      if (this.y >= 0) {
         return false;
      }

      AABB caja = new AABB(this.bunker.ex(), this.bunker.ey(), this.bunker.ez(), this.bunker.ex() + 96, this.bunker.ey() + 34, this.bunker.ez() + 99);

      for (ItemEntity it : this.nivel.getEntitiesOfClass(ItemEntity.class, caja)) {
         it.discard();
      }

      return true;
   }
}
