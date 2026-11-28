package net.ruleta.compat;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEnderpearl;

public final class EntityTeleportEvent {
   private EntityTeleportEvent() {
   }

   public static final class EnderPearl extends Evento {
      private final ServerPlayer jugador;
      private final ThrownEnderpearl perla;
      private final double x;
      private final double y;
      private final double z;
      private float dano;

      public EnderPearl(ServerPlayer jugador, double x, double y, double z, ThrownEnderpearl perla, float dano) {
         this.jugador = jugador;
         this.perla = perla;
         this.x = x;
         this.y = y;
         this.z = z;
         this.dano = dano;
      }

      public ServerPlayer getPlayer() {
         return this.jugador;
      }

      public ThrownEnderpearl getPearlEntity() {
         return this.perla;
      }

      public double getTargetX() {
         return this.x;
      }

      public double getTargetY() {
         return this.y;
      }

      public double getTargetZ() {
         return this.z;
      }

      public float getAttackDamage() {
         return this.dano;
      }

      public void setAttackDamage(float dano) {
         this.dano = dano;
      }
   }
}
