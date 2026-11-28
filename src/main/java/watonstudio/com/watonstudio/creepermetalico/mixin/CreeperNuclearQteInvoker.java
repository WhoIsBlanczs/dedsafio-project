package com.watonstudio.creepermetalico.mixin;

import com.watonstudio.creepermetalico.CreeperNuclearEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CreeperNuclearEntity.class)
public interface CreeperNuclearQteInvoker {
   @Invoker(value = "detonate", remap = false)
   void watonstudio$invokeDetonate();
}
