package net.owleafstudio.modminds.fluid.types;

import java.util.function.Consumer;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;

public class AguaDelLimboFluidType extends FluidType {
   public AguaDelLimboFluidType() {
      super(
         FluidType.Properties.create()
            .fallDistanceModifier(0.0F)
            .canExtinguish(true)
            .supportsBoating(true)
            .canHydrate(true)
            .motionScale(0.007)
            .canConvertToSource(true)
            .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
            .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
            .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)
      );
   }

   @Override
   public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
      consumer.accept(new IClientFluidTypeExtensions() {
         private static final Identifier STILL_TEXTURE = Identifier.parse("minecraft:block/water_still");
         private static final Identifier FLOWING_TEXTURE = Identifier.parse("minecraft:block/water_flow");

         @Override
         public Identifier getStillTexture() {
            return STILL_TEXTURE;
         }

         @Override
         public Identifier getFlowingTexture() {
            return FLOWING_TEXTURE;
         }

         @Override
         public int getTintColor() {
            return -16448205;
         }

         @Override
         public int getTintColor(FluidState state, BlockAndTintGetter world, BlockPos pos) {
            return ((Biome)Minecraft.getInstance().level.getBiome(pos).value()).getWaterFogColor() | 0xFF000000;
         }
      });
   }
}
