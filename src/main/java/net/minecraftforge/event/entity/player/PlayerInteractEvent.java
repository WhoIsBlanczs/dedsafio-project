package net.minecraftforge.event.entity.player;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class PlayerInteractEvent extends PlayerEvent {
   private final InteractionHand hand;
   private final BlockPos pos;
   private final Direction face;

   public PlayerInteractEvent(Player player, InteractionHand hand, BlockPos pos, Direction face) {
      super(player);
      this.hand = hand;
      this.pos = pos;
      this.face = face;
   }

   public InteractionHand getHand() {
      return this.hand;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public Direction getFace() {
      return this.face;
   }

   public static class EntityInteract extends PlayerInteractEvent {
      private final Entity target;

      public EntityInteract(Player player, InteractionHand hand, Entity target) {
         super(player, hand, BlockPos.containing(target.position()), Direction.UP);
         this.target = target;
      }

      public Entity getTarget() {
         return this.target;
      }
   }

   public static class LeftClickEmpty extends PlayerInteractEvent {
      public LeftClickEmpty(Player player) {
         super(player, InteractionHand.MAIN_HAND, BlockPos.containing(player.position()), Direction.UP);
      }
   }

   public static class RightClickBlock extends PlayerInteractEvent {
      public RightClickBlock(Player player, InteractionHand hand, BlockPos pos, Direction face) {
         super(player, hand, pos, face);
      }
   }

   public static class RightClickEmpty extends PlayerInteractEvent {
      public RightClickEmpty(Player player) {
         super(player, InteractionHand.MAIN_HAND, BlockPos.containing(player.position()), Direction.UP);
      }
   }
}
