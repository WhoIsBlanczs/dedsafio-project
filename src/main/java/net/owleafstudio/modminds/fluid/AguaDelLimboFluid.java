package net.owleafstudio.modminds.fluid;

import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.owleafstudio.modminds.init.ModmindsModBlocks;
import net.owleafstudio.modminds.init.ModmindsModFluidTypes;
import net.owleafstudio.modminds.init.ModmindsModFluids;
import net.owleafstudio.modminds.init.ModmindsModItems;

public abstract class AguaDelLimboFluid extends ForgeFlowingFluid {
   public static final ForgeFlowingFluid.Properties PROPERTIES = new ForgeFlowingFluid.Properties(
         () -> ModmindsModFluidTypes.AGUA_DEL_LIMBO_TYPE.get(),
         () -> (Fluid)ModmindsModFluids.AGUA_DEL_LIMBO.get(),
         () -> (Fluid)ModmindsModFluids.FLOWING_AGUA_DEL_LIMBO.get()
      )
      .explosionResistance(100.0F)
      .bucket(() -> ModmindsModItems.AGUA_DEL_LIMBO_BUCKET.get())
      .block(() -> (LiquidBlock)ModmindsModBlocks.AGUA_DEL_LIMBO.get());

   private AguaDelLimboFluid() {
      super(PROPERTIES);
   }

   public static class Flowing extends AguaDelLimboFluid {
      protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
         super.createFluidStateDefinition(builder);
         builder.add(new Property[]{LEVEL});
      }

      public int getAmount(FluidState state) {
         return (Integer)state.getValue(LEVEL);
      }

      public boolean isSource(FluidState state) {
         return false;
      }
   }

   public static class Source extends AguaDelLimboFluid {
      public int getAmount(FluidState state) {
         return 8;
      }

      public boolean isSource(FluidState state) {
         return true;
      }
   }
}
