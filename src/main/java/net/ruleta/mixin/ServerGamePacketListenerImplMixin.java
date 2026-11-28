package net.ruleta.mixin;

import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.ruleta.compat.Bus;
import net.ruleta.compat.ServerChatEvent;
import net.ruleta.tornado.ComandoTornado;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {
   @Shadow
   public ServerPlayer field_14140;
   @Shadow
   private int field_14138;
   @Shadow
   private int field_14137;

   @Inject(method = "tick", at = @At("HEAD"))
   private void ruleta$sinKickPorVolar(CallbackInfo ci) {
      if (this.field_14140 != null && ComandoTornado.atrapado(this.field_14140)) {
         this.field_14138 = 0;
         this.field_14137 = 0;
      }
   }

   @Inject(method = "broadcastChatMessage(Lnet/minecraft/network/chat/PlayerChatMessage;)V", at = @At("HEAD"), cancellable = true, require = 0)
   private void ruleta$chat(PlayerChatMessage mensaje, CallbackInfo ci) {
      if (this.field_14140 != null && Bus.hayOyentes(ServerChatEvent.class) && Bus.post(new ServerChatEvent(this.field_14140, mensaje.decoratedContent()))) {
         ci.cancel();
      }
   }
}
