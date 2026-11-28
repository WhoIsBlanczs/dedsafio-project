package net.owleafstudio.modminds.compat.mixin;

import net.owleafstudio.modminds.compat.Ids;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.Properties.class)
public abstract class BlockPropertiesMixin {
   @Inject(method = {"of", "ofFullCopy", "ofLegacyCopy"}, at = @At("RETURN"))
   private static void modminds$ponerId(CallbackInfoReturnable<BlockBehaviour.Properties> cir) {
      Identifier id = Ids.pendienteBloque();
      if (id != null) {
         cir.getReturnValue().setId(ResourceKey.create(Registries.BLOCK, id));
      }
   }
}
