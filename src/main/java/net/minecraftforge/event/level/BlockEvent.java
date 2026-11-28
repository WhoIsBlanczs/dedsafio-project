package net.minecraftforge.event.level;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.Event;

public class BlockEvent extends Event {
   private final Level level;
   private final BlockPos pos;

   public BlockEvent(Level level, BlockPos pos) {
      this.level = level;
      this.pos = pos;
   }

   public Level getLevel() {
      return this.level;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public static class BreakEvent extends BlockEvent {
      private final Player player;
      private final BlockState state;
      private int expToDrop;

      public BreakEvent(Level level, BlockPos pos, Player player, BlockState state) {
         super(level, pos);
         this.player = player;
         this.state = state;
      }

      public Player getPlayer() {
         return this.player;
      }

      public BlockState getState() {
         return this.state;
      }

      public int getExpToDrop() {
         return this.expToDrop;
      }

      public void setExpToDrop(int exp) {
         this.expToDrop = exp;
      }
   }
}
