package com.watonstudio.creepermetalico.mixin;

import com.watonstudio.creepermetalico.client.EclipseLighting;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.LightLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Environment(EnvType.CLIENT)
@Mixin(BlockAndTintGetter.class)
public interface BlockRenderViewLightMixin {
   @Overwrite
   default int method_8314(LightLayer type, BlockPos pos) {
      BlockAndTintGetter view = (BlockAndTintGetter)this;
      int vanillaLight = view.getLightEngine().getLayerListener(type).getLightValue(pos);
      return EclipseLighting.filterLightLevel(type, pos, vanillaLight);
   }
}
