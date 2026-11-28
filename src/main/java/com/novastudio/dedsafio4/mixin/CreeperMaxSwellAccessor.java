package com.novastudio.dedsafio4.mixin;

import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({Creeper.class})
public interface CreeperMaxSwellAccessor {
   @Accessor("maxSwell")
   void nova$setMaxSwell(int var1);
}
