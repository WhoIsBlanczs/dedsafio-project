package net.minecraftforge.network.simple;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.level.Level;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.owleafstudio.modminds.compat.NetworkCompat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SimpleChannel {
   private static final Logger LOGGER = LoggerFactory.getLogger("modminds-network-shim");
   private static final boolean IS_CLIENT = FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
   private final Identifier name;
   private final Map<Class<?>, SimpleChannel.MessageRegistration<?>> byClass = new ConcurrentHashMap<>();
   private final List<SimpleChannel.MessageRegistration<?>> ordered = new ArrayList<>();
   private boolean commonInstalled;
   private boolean clientInstalled;

   public SimpleChannel(Identifier name) {
      this.name = name;
   }

   public Identifier name() {
      return this.name;
   }

   public <T> void registerMessage(
      int index,
      Class<T> messageType,
      BiConsumer<T, FriendlyByteBuf> encoder,
      Function<FriendlyByteBuf, T> decoder,
      BiConsumer<T, Supplier<NetworkEvent.Context>> handler
   ) {
      CustomPacketPayload.Type<SimpleChannel.Envelope> id = new CustomPacketPayload.Type(
         Identifier.fromNamespaceAndPath(this.name.getNamespace(), messageType.getSimpleName().toLowerCase(Locale.ROOT))
      );
      SimpleChannel.MessageRegistration<T> reg = new SimpleChannel.MessageRegistration<>(id, messageType, encoder, decoder, handler);
      synchronized (this) {
         SimpleChannel.MessageRegistration<?> previous = this.byClass.put(messageType, reg);
         if (previous != null) {
            this.ordered.set(this.ordered.indexOf(previous), reg);
         } else {
            this.ordered.add(reg);
         }

         if (this.commonInstalled) {
            this.installCommonOne(reg);
         }

         if (this.clientInstalled) {
            this.installClientOne(reg);
         }
      }
   }

   public synchronized void installCommon() {
      if (!this.commonInstalled) {
         for (SimpleChannel.MessageRegistration<?> reg : this.ordered) {
            this.installCommonOne(reg);
         }

         this.commonInstalled = true;
      }
   }

   public synchronized void installClient() {
      if (!this.clientInstalled) {
         for (SimpleChannel.MessageRegistration<?> reg : this.ordered) {
            this.installClientOne(reg);
         }

         this.clientInstalled = true;
      }
   }

   private void installCommonOne(SimpleChannel.MessageRegistration<?> reg) {
      StreamCodec<FriendlyByteBuf, SimpleChannel.Envelope> codec = reg.codec();
      PayloadTypeRegistry.serverboundPlay().register(reg.id(), codec);
      PayloadTypeRegistry.clientboundPlay().register(reg.id(), codec);
      ServerPlayNetworking.registerGlobalReceiver(reg.id(), (envelope, ctx) -> {
         NetworkEvent.Context context = new NetworkEvent.Context(ctx.player(), NetworkEvent.Direction.PLAY_TO_SERVER);
         reg.dispatch(envelope.message(), () -> context);
      });
   }

   private void installClientOne(SimpleChannel.MessageRegistration<?> reg) {
      ClientPlayNetworking.registerGlobalReceiver(reg.id(), (envelope, ctx) -> {
         NetworkEvent.Context context = new NetworkEvent.Context(null, NetworkEvent.Direction.PLAY_TO_CLIENT);
         reg.dispatch(envelope.message(), () -> context);
      });
   }

   public <T> void sendToServer(T message) {
      SimpleChannel.MessageRegistration<?> reg = this.registrationFor(message.getClass());
      if (reg == null) {
         LOGGER.error("Message {} was never registered on channel {}", message.getClass().getName(), this.name);
      } else if (!IS_CLIENT) {
         LOGGER.error("sendToServer called on the dedicated server for {}", message.getClass().getName());
      } else if (Minecraft.getInstance().getConnection() != null) {
         ClientPlayNetworking.send(new SimpleChannel.Envelope(reg.id(), message));
      }
   }

   public <X, M> void send(PacketDistributor<X> distributor, M message) {
      this.send(distributor.noArg(), message);
   }

   public <X, M> void send(PacketDistributor.Producer<X> producer, M message) {
      SimpleChannel.MessageRegistration<?> reg = this.registrationFor(message.getClass());
      if (reg == null) {
         LOGGER.error("Message {} was never registered on channel {}", message.getClass().getName(), this.name);
      } else {
         SimpleChannel.Envelope envelope = new SimpleChannel.Envelope(reg.id(), message);
         switch (producer.distributor.kind) {
            case PLAYER:
               ServerPlayer target = (ServerPlayer)producer.target.get();
               if (target != null) {
                  ServerPlayNetworking.send(target, envelope);
               }
               break;
            case ALL:
               MinecraftServer serverx = NetworkCompat.getServer();
               if (serverx != null) {
                  for (ServerPlayer player : PlayerLookup.all(serverx)) {
                     ServerPlayNetworking.send(player, envelope);
                  }
               }
               break;
            case DIMENSION:
               MinecraftServer server = NetworkCompat.getServer();
               ResourceKey<Level> key = (ResourceKey<Level>)producer.target.get();
               ServerLevel level = server != null ? server.getLevel(key) : null;
               if (level != null) {
                  for (ServerPlayer player : PlayerLookup.level(level)) {
                     ServerPlayNetworking.send(player, envelope);
                  }
               }
         }
      }
   }

   @Nullable
   public CustomPacketPayload.Type<SimpleChannel.Envelope> typeFor(Class<?> messageType) {
      SimpleChannel.MessageRegistration<?> reg = this.registrationFor(messageType);
      return reg != null ? reg.id() : null;
   }

   @Nullable
   private SimpleChannel.MessageRegistration<?> registrationFor(Class<?> messageType) {
      for (Class<?> current = messageType; current != null; current = current.getSuperclass()) {
         SimpleChannel.MessageRegistration<?> reg = this.byClass.get(current);
         if (reg != null) {
            return reg;
         }
      }

      return null;
   }

   public record Envelope(CustomPacketPayload.Type<SimpleChannel.Envelope> type, Object message) implements CustomPacketPayload {
      public CustomPacketPayload.Type<SimpleChannel.Envelope> type() {
         return this.type;
      }
   }

   public record MessageRegistration<T>(
      CustomPacketPayload.Type<SimpleChannel.Envelope> id,
      Class<T> messageType,
      BiConsumer<T, FriendlyByteBuf> encoder,
      Function<FriendlyByteBuf, T> decoder,
      BiConsumer<T, Supplier<NetworkEvent.Context>> handler
   ) {
      StreamCodec<FriendlyByteBuf, SimpleChannel.Envelope> codec() {
         return StreamCodec.of(
            (buf, envelope) -> this.encode(buf, envelope.message()), buf -> new SimpleChannel.Envelope(this.id, this.decoder.apply(buf))
         );
      }

      private void encode(FriendlyByteBuf buf, Object message) {
         this.encoder.accept((T)message, buf);
      }

      void dispatch(Object message, Supplier<NetworkEvent.Context> contextSupplier) {
         this.handler.accept((T)message, contextSupplier);
      }
   }
}
