package com.watonstudio.creepermetalico.mixin;

import com.watonstudio.creepermetalico.client.EclipseLighting;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.LightLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Environment(EnvType.CLIENT)
@Mixin(BlockAndLightGetter.class)
public interface BlockRenderViewLightMixin {
   /**
    * @author WatonStudio
    * @reason Luz filtrada durante el eclipse.
    */
   @Overwrite
   default int getBrightness(LightLayer type, BlockPos pos) {
      BlockAndLightGetter view = (BlockAndLightGetter)(Object)this;
      int vanillaLight = view.getLightEngine().getLayerListener(type).getLightValue(pos);
      return EclipseLighting.filterLightLevel(type, pos, vanillaLight);
   }
}
