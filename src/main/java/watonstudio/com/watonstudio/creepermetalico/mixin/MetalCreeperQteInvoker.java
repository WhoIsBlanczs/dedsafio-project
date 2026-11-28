package com.watonstudio.creepermetalico.mixin;

import com.watonstudio.creepermetalico.MetalCreeperEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MetalCreeperEntity.class)
public interface MetalCreeperQteInvoker {
   @Invoker(value = "detonate", remap = false)
   void watonstudio$invokeDetonate();
}
