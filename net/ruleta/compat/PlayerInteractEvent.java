package net.ruleta.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public abstract class PlayerInteractEvent extends Evento {
   private final Player jugador;
   private final InteractionHand mano;
   private final BlockPos pos;
   @Nullable
   private final Direction cara;
   private final Level nivel;
   private InteractionResult resultado = InteractionResult.PASS;

   protected PlayerInteractEvent(Player jugador, InteractionHand mano, BlockPos pos, @Nullable Direction cara, Level nivel) {
      this.jugador = jugador;
      this.mano = mano;
      this.pos = pos;
      this.cara = cara;
      this.nivel = nivel;
   }

   public Player getEntity() {
      return this.jugador;
   }

   public InteractionHand getHand() {
      return this.mano;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   @Nullable
   public Direction getFace() {
      return this.cara;
   }

   public Level getLevel() {
      return this.nivel;
   }

   public ItemStack getItemStack() {
      return this.jugador.getItemInHand(this.mano);
   }

   public InteractionResult getCancellationResult() {
      return this.resultado;
   }

   public void setCancellationResult(InteractionResult resultado) {
      this.resultado = resultado;
   }

   public static class EntityInteract extends PlayerInteractEvent {
      private final Entity objetivo;

      public EntityInteract(Player jugador, InteractionHand mano, Entity objetivo) {
         super(jugador, mano, objetivo.blockPosition(), null, jugador.level());
         this.objetivo = objetivo;
      }

      public Entity getTarget() {
         return this.objetivo;
      }
   }

   public static final class EntityInteractSpecific extends PlayerInteractEvent.EntityInteract {
      private final Vec3 local;

      public EntityInteractSpecific(Player jugador, InteractionHand mano, Entity objetivo, Vec3 local) {
         super(jugador, mano, objetivo);
         this.local = local;
      }

      public Vec3 getLocalPos() {
         return this.local;
      }
   }

   public static final class LeftClickBlock extends PlayerInteractEvent {
      public LeftClickBlock(Player jugador, InteractionHand mano, BlockPos pos, Direction cara, Level nivel) {
         super(jugador, mano, pos, cara, nivel);
      }
   }

   public static final class RightClickBlock extends PlayerInteractEvent {
      private final BlockHitResult golpe;
      private Evento.Result usarObjeto = Evento.Result.DEFAULT;
      private Evento.Result usarBloque = Evento.Result.DEFAULT;

      public RightClickBlock(Player jugador, InteractionHand mano, BlockHitResult golpe, Level nivel) {
         super(jugador, mano, golpe.getBlockPos(), golpe.getDirection(), nivel);
         this.golpe = golpe;
      }

      public BlockHitResult getHitVec() {
         return this.golpe;
      }

      public Evento.Result getUseItem() {
         return this.usarObjeto;
      }

      public void setUseItem(Evento.Result r) {
         this.usarObjeto = r;
      }

      public Evento.Result getUseBlock() {
         return this.usarBloque;
      }

      public void setUseBlock(Evento.Result r) {
         this.usarBloque = r;
      }
   }
}
