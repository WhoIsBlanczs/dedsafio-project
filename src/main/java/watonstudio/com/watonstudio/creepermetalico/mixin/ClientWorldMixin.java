package com.watonstudio.creepermetalico.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.watonstudio.creepermetalico.client.ClientEclipse;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(ClientLevel.class)
public abstract class ClientWorldMixin {
   @ModifyReturnValue(method = "getCloudsColor", at = @At("RETURN"))
   private Vec3 eclipse$darkClouds(Vec3 original, float tickDelta) {
      float e = ClientEclipse.darkness(tickDelta);
      return e <= 0.0F ? original : original.lerp(new Vec3(0.03, 0.03, 0.045), e);
   }
}
