package net.minecraftforge.fluids;

import java.util.function.Consumer;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundActions;

public class FluidType {
   private final FluidType.Properties properties;

   public FluidType(FluidType.Properties properties) {
      this.properties = properties;
   }

   public FluidType.Properties getProperties() {
      return this.properties;
   }

   public boolean canExtinguish(FluidState state, BlockAndTintGetter level, BlockPos pos) {
      return this.properties.canExtinguish;
   }

   public boolean canHydrate(FluidState state, BlockAndTintGetter level, BlockPos pos) {
      return this.properties.canHydrate;
   }

   public boolean supportsBoating(FluidState state, BlockAndTintGetter level, BlockPos pos) {
      return this.properties.supportsBoating;
   }

   public boolean canConvertToSource() {
      return this.properties.canConvertToSource;
   }

   public double getMotionScale() {
      return this.properties.motionScale;
   }

   public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
   }

   public static class Properties {
      private float fallDistanceModifier = 0.2F;
      private boolean canExtinguish;
      private boolean supportsBoating;
      private boolean canHydrate;
      private boolean canConvertToSource;
      private double motionScale = 0.014;
      private float density = 1000.0F;
      private float temperature = 0.5F;
      private float viscosity = 1.0F;
      private SoundEvent bucketFill;
      private SoundEvent bucketEmpty;
      private SoundEvent fluidVaporize;

      public static FluidType.Properties create() {
         return new FluidType.Properties();
      }

      public FluidType.Properties fallDistanceModifier(float value) {
         this.fallDistanceModifier = value;
         return this;
      }

      public FluidType.Properties canExtinguish(boolean value) {
         this.canExtinguish = value;
         return this;
      }

      public FluidType.Properties supportsBoating(boolean value) {
         this.supportsBoating = value;
         return this;
      }

      public FluidType.Properties canHydrate(boolean value) {
         this.canHydrate = value;
         return this;
      }

      public FluidType.Properties canConvertToSource(boolean value) {
         this.canConvertToSource = value;
         return this;
      }

      public FluidType.Properties motionScale(double value) {
         this.motionScale = value;
         return this;
      }

      public FluidType.Properties density(int value) {
         this.density = value;
         return this;
      }

      public FluidType.Properties temperature(int value) {
         this.temperature = value;
         return this;
      }

      public FluidType.Properties viscosity(int value) {
         this.viscosity = value;
         return this;
      }

      public FluidType.Properties sound(SoundActions action, SoundEvent event) {
         if (action == SoundActions.BUCKET_FILL) {
            this.bucketFill = event;
         } else if (action == SoundActions.BUCKET_EMPTY) {
            this.bucketEmpty = event;
         } else if (action == SoundActions.FLUID_VAPORIZE) {
            this.fluidVaporize = event;
         }

         return this;
      }

      public SoundEvent getSound(SoundActions action) {
         if (action == SoundActions.BUCKET_FILL) {
            return this.bucketFill;
         } else {
            return action == SoundActions.BUCKET_EMPTY ? this.bucketEmpty : this.fluidVaporize;
         }
      }
   }
}
