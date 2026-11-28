package com.watonstudio.creepermetalico.mixin;

import com.watonstudio.creepermetalico.CreeperMetalicoMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientPacketListener.class)
public abstract class TotemEonClientMixin {
   @Shadow
   private ClientLevel field_3699;

   @Inject(method = "onEntityStatus(Lnet/minecraft/network/packet/s2c/play/EntityStatusS2CPacket;)V", at = @At("HEAD"), cancellable = true)
   private void watonstudio$animacionEon(ClientboundEntityEventPacket packet, CallbackInfo ci) {
      if (packet.getEventId() == 36) {
         ci.cancel();
         Minecraft client = Minecraft.getInstance();
         client.execute(() -> {
            ClientLevel mundo = this.field_3699;
            if (mundo != null) {
               Entity entidad = packet.getEntity(mundo);
               if (entidad != null) {
                  mundo.playLocalSound(entidad.getX(), entidad.getY(), entidad.getZ(), SoundEvents.TOTEM_USE, entidad.getSoundSource(), 1.0F, 1.0F, false);
                  client.particleEngine.createTrackingEmitter(entidad, ParticleTypes.TOTEM_OF_UNDYING, 30);
                  if (entidad == client.player) {
                     client.gameRenderer.displayItemActivation(CreeperMetalicoMod.TOTEM_EON.getDefaultInstance());
                  }
               }
            }
         });
      }
   }
}
