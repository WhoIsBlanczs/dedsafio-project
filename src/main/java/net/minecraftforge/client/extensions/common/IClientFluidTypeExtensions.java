package net.minecraftforge.client.extensions.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.FluidType;

public interface IClientFluidTypeExtensions {
   Map<FluidType, IClientFluidTypeExtensions> EXTENSIONS = new ConcurrentHashMap<>();
   Identifier STILL_WATER = Identifier.parse("minecraft:block/water_still");
   Identifier FLOWING_WATER = Identifier.parse("minecraft:block/water_flow");
   int WATER_TINT = -12618012;

   static IClientFluidTypeExtensions of(FluidType type) {
      return type == null ? null : EXTENSIONS.get(type);
   }

   default Identifier getStillTexture() {
      return STILL_WATER;
   }

   default Identifier getFlowingTexture() {
      return FLOWING_WATER;
   }

   default Identifier getOverlayTexture() {
      return Identifier.parse("minecraft:block/water_overlay");
   }

   default int getTintColor() {
      return -12618012;
   }

   default int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
      return this.getTintColor();
   }
}
