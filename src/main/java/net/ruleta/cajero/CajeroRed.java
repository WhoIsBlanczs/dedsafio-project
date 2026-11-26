package net.ruleta.cajero;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.Identifier;

public final class CajeroRed {
   private CajeroRed() {
   }

   public static void registrar() {
      PayloadTypeRegistry.clientboundPlay().register(CajeroRed.AbrirPayload.TYPE, CajeroRed.AbrirPayload.CODEC);
      PayloadTypeRegistry.clientboundPlay().register(CajeroRed.SaldoPayload.TYPE, CajeroRed.SaldoPayload.CODEC);
      PayloadTypeRegistry.clientboundPlay().register(CajeroRed.RespuestaPayload.TYPE, CajeroRed.RespuestaPayload.CODEC);
      PayloadTypeRegistry.serverboundPlay().register(CajeroRed.AccionPayload.TYPE, CajeroRed.AccionPayload.CODEC);
      ServerPlayNetworking.registerGlobalReceiver(CajeroRed.AccionPayload.TYPE, (m, ctx) -> CajeroServidor.accion(ctx.player(), m));
   }

   public record AbrirPayload(BlockPos pos, long saldo) implements CustomPacketPayload {
      public static final Type<CajeroRed.AbrirPayload> TYPE = new Type(Identifier.fromNamespaceAndPath("ruleta", "cajero_abrir"));
      public static final StreamCodec<FriendlyByteBuf, CajeroRed.AbrirPayload> CODEC = StreamCodec.ofMember(
         CajeroRed.AbrirPayload::encode, CajeroRed.AbrirPayload::decode
      );

      public Type<? extends CustomPacketPayload> type() {
         return TYPE;
      }

      public static void encode(CajeroRed.AbrirPayload m, FriendlyByteBuf b) {
         b.writeBlockPos(m.pos);
         b.writeLong(m.saldo);
      }

      public static CajeroRed.AbrirPayload decode(FriendlyByteBuf b) {
         return new CajeroRed.AbrirPayload(b.readBlockPos(), b.readLong());
      }
   }

   public record AccionPayload(BlockPos pos, boolean ingresar, int rojas, int verdes, int azules) implements CustomPacketPayload {
      public static final Type<CajeroRed.AccionPayload> TYPE = new Type(Identifier.fromNamespaceAndPath("ruleta", "cajero_accion"));
      public static final StreamCodec<FriendlyByteBuf, CajeroRed.AccionPayload> CODEC = StreamCodec.ofMember(
         CajeroRed.AccionPayload::encode, CajeroRed.AccionPayload::decode
      );

      public Type<? extends CustomPacketPayload> type() {
         return TYPE;
      }

      public static void encode(CajeroRed.AccionPayload m, FriendlyByteBuf b) {
         b.writeBlockPos(m.pos);
         b.writeBoolean(m.ingresar);
         b.writeVarInt(m.rojas);
         b.writeVarInt(m.verdes);
         b.writeVarInt(m.azules);
      }

      public static CajeroRed.AccionPayload decode(FriendlyByteBuf b) {
         return new CajeroRed.AccionPayload(b.readBlockPos(), b.readBoolean(), b.readVarInt(), b.readVarInt(), b.readVarInt());
      }
   }

   public record RespuestaPayload(boolean ok, String clave) implements CustomPacketPayload {
      public static final Type<CajeroRed.RespuestaPayload> TYPE = new Type(Identifier.fromNamespaceAndPath("ruleta", "cajero_respuesta"));
      public static final StreamCodec<FriendlyByteBuf, CajeroRed.RespuestaPayload> CODEC = StreamCodec.ofMember(
         CajeroRed.RespuestaPayload::encode, CajeroRed.RespuestaPayload::decode
      );

      public Type<? extends CustomPacketPayload> type() {
         return TYPE;
      }

      public static void encode(CajeroRed.RespuestaPayload m, FriendlyByteBuf b) {
         b.writeBoolean(m.ok);
         b.writeUtf(m.clave, 32);
      }

      public static CajeroRed.RespuestaPayload decode(FriendlyByteBuf b) {
         return new CajeroRed.RespuestaPayload(b.readBoolean(), b.readUtf(32));
      }
   }

   public record SaldoPayload(long saldo) implements CustomPacketPayload {
      public static final Type<CajeroRed.SaldoPayload> TYPE = new Type(Identifier.fromNamespaceAndPath("ruleta", "cajero_saldo"));
      public static final StreamCodec<FriendlyByteBuf, CajeroRed.SaldoPayload> CODEC = StreamCodec.ofMember(
         CajeroRed.SaldoPayload::encode, CajeroRed.SaldoPayload::decode
      );

      public Type<? extends CustomPacketPayload> type() {
         return TYPE;
      }

      public static void encode(CajeroRed.SaldoPayload m, FriendlyByteBuf b) {
         b.writeLong(m.saldo);
      }

      public static CajeroRed.SaldoPayload decode(FriendlyByteBuf b) {
         return new CajeroRed.SaldoPayload(b.readLong());
      }
   }
}
