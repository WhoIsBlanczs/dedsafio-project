package net.owleafstudio.modminds.block;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.owleafstudio.modminds.init.ModmindsModFluids;
import net.owleafstudio.modminds.procedures.AguaDelLimboAlEntidadColisionarConElBloqueProcedure;

public class AguaDelLimboBlock extends LiquidBlock {
   public AguaDelLimboBlock() {
      super(
         ModmindsModFluids.AGUA_DEL_LIMBO.get(),
         BlockBehaviour.Properties.of()
            .mapColor(MapColor.WATER)
            .strength(100.0F)
            .noCollision()
            .noLootTable()
            .liquid()
            .pushReaction(PushReaction.POPPED)
            .sound(SoundType.EMPTY)
            .replaceable()
      );
   }

   public void entityInside(BlockState blockstate, Level world, BlockPos pos, Entity entity, net.minecraft.world.entity.InsideBlockEffectApplier efectos$, boolean pasando$) {
      super.entityInside(blockstate, world, pos, entity, efectos$, pasando$);
      AguaDelLimboAlEntidadColisionarConElBloqueProcedure.execute(world, entity);
   }
}
