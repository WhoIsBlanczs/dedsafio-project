package com.ghostsafio.mitomod.compat.mixin;

import com.ghostsafio.mitomod.compat.Ids;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Item.Properties.class)
public abstract class ItemPropertiesMixin {
   @Inject(method = "<init>", at = @At("TAIL"))
   private void ghost$ponerId(CallbackInfo ci) {
      Identifier id = Ids.pendienteItem();
      if (id != null) {
         Item.Properties self = (Item.Properties)(Object)this;
         self.setId(ResourceKey.create(Registries.ITEM, id));
         if (BuiltInRegistries.BLOCK.containsKey(id)) {
            self.useBlockDescriptionPrefix();
         }
      }
   }
}
