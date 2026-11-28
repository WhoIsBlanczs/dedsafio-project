package net.minecraftforge.fluids;

import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.LevelReader;
import net.minecraftforge.common.SoundActions;

public abstract class ForgeFlowingFluid extends FlowingFluid {
   private final ForgeFlowingFluid.Properties properties;

   protected ForgeFlowingFluid(ForgeFlowingFluid.Properties properties) {
      this.properties = properties;
   }

   protected ForgeFlowingFluid.Properties forgeProperties() {
      return this.properties;
   }

   public Fluid getFlowing() {
      return this.properties.flowing.get();
   }

   public Fluid getSource() {
      return this.properties.still.get();
   }

   public Item getBucket() {
      return this.properties.bucket != null ? this.properties.bucket.get() : Items.BUCKET;
   }

   protected boolean canConvertToSource(Level level) {
      return this.properties.type != null && this.properties.type.get().canConvertToSource();
   }

   protected void beforeDestroyingBlock(LevelAccessor level, BlockPos pos, BlockState state) {
   }

   protected int getSlopeFindDistance(LevelReader level) {
      return this.properties.slopeFindDistance;
   }

   protected int getDropOff(LevelReader level) {
      return this.properties.dropOff;
   }

   public int getTickDelay(LevelReader level) {
      return this.properties.tickDelay;
   }

   protected float getExplosionResistance() {
      return this.properties.explosionResistance;
   }

   protected BlockState createLegacyBlock(FluidState state) {
      return this.properties.block != null
         ? (BlockState)this.properties.block.get().defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(state))
         : (BlockState)Blocks.WATER.defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(state));
   }

   public boolean isSame(Fluid fluid) {
      return fluid == this.properties.still.get() || fluid == this.properties.flowing.get();
   }

   protected boolean canBeReplacedWith(FluidState state, BlockGetter level, BlockPos pos, Fluid fluid, Direction direction) {
      return false;
   }

   public Optional<SoundEvent> getPickupSound() {
      return this.properties.type != null
         ? Optional.ofNullable(this.properties.type.get().getProperties().getSound(SoundActions.BUCKET_FILL))
         : Optional.empty();
   }

   public static class Properties {
      private final Supplier<FluidType> type;
      private final Supplier<Fluid> still;
      private final Supplier<Fluid> flowing;
      private Supplier<? extends LiquidBlock> block;
      private Supplier<? extends Item> bucket;
      private float explosionResistance = 100.0F;
      private int slopeFindDistance = 4;
      private int dropOff = 1;
      private int tickDelay = 5;

      public Properties(Supplier<FluidType> type, Supplier<Fluid> still, Supplier<Fluid> flowing) {
         this.type = type;
         this.still = still;
         this.flowing = flowing;
      }

      public ForgeFlowingFluid.Properties bucket(Supplier<? extends Item> bucket) {
         this.bucket = bucket;
         return this;
      }

      public ForgeFlowingFluid.Properties block(Supplier<? extends LiquidBlock> block) {
         this.block = block;
         return this;
      }

      public ForgeFlowingFluid.Properties explosionResistance(float value) {
         this.explosionResistance = value;
         return this;
      }

      public ForgeFlowingFluid.Properties slopeFindDistance(int value) {
         this.slopeFindDistance = value;
         return this;
      }

      public ForgeFlowingFluid.Properties dropOff(int value) {
         this.dropOff = value;
         return this;
      }

      public ForgeFlowingFluid.Properties tickDelay(int value) {
         this.tickDelay = value;
         return this;
      }

      public FluidType getFluidType() {
         return this.type.get();
      }

      public Supplier<? extends LiquidBlock> getBlock() {
         return this.block;
      }

      public Supplier<? extends Item> getBucket() {
         return this.bucket;
      }
   }
}
