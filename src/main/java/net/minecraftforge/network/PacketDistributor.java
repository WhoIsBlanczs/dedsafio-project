package net.minecraftforge.network;

import java.util.function.Supplier;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceKey;

public class PacketDistributor<T> {
   public static final PacketDistributor<ServerPlayer> PLAYER = new PacketDistributor<>(PacketDistributor.Kind.PLAYER);
   public static final PacketDistributor<Void> ALL = new PacketDistributor<>(PacketDistributor.Kind.ALL);
   public static final PacketDistributor<ResourceKey<Level>> DIMENSION = new PacketDistributor<>(PacketDistributor.Kind.DIMENSION);
   public final PacketDistributor.Kind kind;

   private PacketDistributor(PacketDistributor.Kind kind) {
      this.kind = kind;
   }

   public PacketDistributor.Producer<T> with(Supplier<T> target) {
      return new PacketDistributor.Producer<>(this, target);
   }

   public PacketDistributor.Producer<T> noArg() {
      return this.with(() -> null);
   }

   public static enum Kind {
      PLAYER,
      ALL,
      DIMENSION;
   }

   public static final class Producer<T> {
      public final PacketDistributor<T> distributor;
      public final Supplier<T> target;

      Producer(PacketDistributor<T> distributor, Supplier<T> target) {
         this.distributor = distributor;
         this.target = target;
      }
   }
}
