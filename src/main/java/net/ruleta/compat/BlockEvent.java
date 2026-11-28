package net.ruleta.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BlockEvent extends Evento {
   private final LevelAccessor nivel;
   private final BlockPos pos;
   private final BlockState estado;

   protected BlockEvent(LevelAccessor nivel, BlockPos pos, BlockState estado) {
      this.nivel = nivel;
      this.pos = pos;
      this.estado = estado;
   }

   public LevelAccessor getLevel() {
      return this.nivel;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public BlockState getState() {
      return this.estado;
   }

   public static final class BreakEvent extends BlockEvent {
      private final Player jugador;

      public BreakEvent(LevelAccessor nivel, BlockPos pos, BlockState estado, Player jugador) {
         super(nivel, pos, estado);
         this.jugador = jugador;
      }

      public Player getPlayer() {
         return this.jugador;
      }
   }

   public static final class EntityPlaceEvent extends BlockEvent {
      private final Entity entidad;

      public EntityPlaceEvent(LevelAccessor nivel, BlockPos pos, BlockState estado, Entity entidad) {
         super(nivel, pos, estado);
         this.entidad = entidad;
      }

      public Entity getEntity() {
         return this.entidad;
      }
   }

   public static final class FluidPlaceBlockEvent extends BlockEvent {
      private final BlockPos pozo;

      public FluidPlaceBlockEvent(LevelAccessor nivel, BlockPos pos, BlockPos pozo, BlockState estado) {
         super(nivel, pos, estado);
         this.pozo = pozo;
      }

      public BlockPos getLiquidPos() {
         return this.pozo;
      }
   }
}
