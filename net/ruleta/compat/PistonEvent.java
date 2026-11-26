package net.ruleta.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonStructureResolver;

public abstract class PistonEvent extends Evento {
   private final Level nivel;
   private final BlockPos pos;
   private final Direction direccion;
   private final boolean extiende;
   private PistonStructureResolver estructura;

   protected PistonEvent(Level nivel, BlockPos pos, Direction direccion, boolean extiende) {
      this.nivel = nivel;
      this.pos = pos;
      this.direccion = direccion;
      this.extiende = extiende;
   }

   public Level getLevel() {
      return this.nivel;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public Direction getDirection() {
      return this.direccion;
   }

   public boolean isExtending() {
      return this.extiende;
   }

   public PistonStructureResolver getStructureHelper() {
      if (this.estructura == null) {
         this.estructura = new PistonStructureResolver(this.nivel, this.pos, this.direccion, this.extiende);
      }

      return this.estructura;
   }

   public static final class Pre extends PistonEvent {
      public Pre(Level nivel, BlockPos pos, Direction direccion, boolean extiende) {
         super(nivel, pos, direccion, extiende);
      }
   }
}
