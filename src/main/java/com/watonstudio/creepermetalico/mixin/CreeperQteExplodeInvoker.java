package com.watonstudio.creepermetalico.mixin;

import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Creeper.class)
public interface CreeperQteExplodeInvoker {
   @Invoker("explodeCreeper")
   void watonstudio$invokeExplode();
}
