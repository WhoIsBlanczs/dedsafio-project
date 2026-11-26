package net.ruleta.mixin;

import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.ruleta.candado.Propiedades;
import net.ruleta.client.codigo.MaterialesCofre;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Sheets.class)
public abstract class ChestRendererMixin {
   @Inject(
      method = "chooseMaterial(Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/level/block/state/properties/ChestType;Z)Lnet/minecraft/client/resources/model/Material;",
      at = @At("HEAD"),
      cancellable = true,
      require = 0
   )
   private static void ruleta$conCandado(BlockEntity cofre, ChestType tipo, boolean navidad, CallbackInfoReturnable<Material> cir) {
      if (tipo != ChestType.SINGLE) {
         BlockState e = cofre.getBlockState();
         if (e.hasProperty(Propiedades.LOCKED) && (Boolean)e.getValue(Propiedades.LOCKED)) {
            cir.setReturnValue(tipo == ChestType.LEFT ? MaterialesCofre.IZQUIERDA : MaterialesCofre.DERECHA);
         }
      }
   }
}
