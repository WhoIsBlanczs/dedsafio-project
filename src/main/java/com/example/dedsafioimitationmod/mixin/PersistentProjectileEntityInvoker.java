package com.example.dedsafioimitationmod.mixin;

import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({AbstractArrow.class})
public interface PersistentProjectileEntityInvoker {
   @Invoker("setPierceLevel")
   void dedsafio$setPierceLevel(byte var1);
}
