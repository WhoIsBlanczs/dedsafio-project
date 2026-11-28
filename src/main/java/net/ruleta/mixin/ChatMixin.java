package net.ruleta.mixin;

import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import net.ruleta.client.compat.Bus;
import net.ruleta.client.compat.ClientChatReceivedEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatComponent.class)
public abstract class ChatMixin {
   @Unique
   private boolean ruleta$dentro;

   @Inject(
      method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)V",
      at = @At("HEAD"),
      cancellable = true,
      require = 0
   )
   private void ruleta$alLlegar(Component mensaje, MessageSignature firma, GuiMessageTag etiqueta, CallbackInfo ci) {
      if (!this.ruleta$dentro && Bus.hayOyentes(ClientChatReceivedEvent.class)) {
         ClientChatReceivedEvent e = new ClientChatReceivedEvent(mensaje, firma == null);
         Bus.post(e);
         if (e.getMessage() != mensaje) {
            this.ruleta$dentro = true;

            try {
               ((ChatComponent)this).addMessage(e.getMessage(), firma, etiqueta);
            } finally {
               this.ruleta$dentro = false;
            }

            ci.cancel();
         }
      }
   }
}
