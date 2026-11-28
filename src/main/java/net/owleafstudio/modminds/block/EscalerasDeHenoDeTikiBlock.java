package net.owleafstudio.modminds.block;

import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.common.util.ForgeSoundType;
import net.minecraftforge.registries.ForgeRegistries;

public class EscalerasDeHenoDeTikiBlock extends StairBlock {
   public EscalerasDeHenoDeTikiBlock() {
      super(
         Blocks.AIR.defaultBlockState(),
         BlockBehaviour.Properties.of()
            .sound(
               new ForgeSoundType(
                  1.0F,
                  1.0F,
                  () -> ForgeRegistries.SOUND_EVENTS.getValue(Identifier.parse("block.grass.break")),
                  () -> ForgeRegistries.SOUND_EVENTS.getValue(Identifier.parse("block.grass.step")),
                  () -> ForgeRegistries.SOUND_EVENTS.getValue(Identifier.parse("block.grass.place")),
                  () -> ForgeRegistries.SOUND_EVENTS.getValue(Identifier.parse("block.grass.hit")),
                  () -> ForgeRegistries.SOUND_EVENTS.getValue(Identifier.parse("block.grass.fall"))
               )
            )
            .strength(1.0F, 10.0F)
            .noOcclusion()
            .isRedstoneConductor((bs, br, bp) -> false)
      );
   }

   public float getExplosionResistance() {
      return 10.0F;
   }

   public boolean isRandomlyTicking(BlockState state) {
      return false;
   }

   public int getLightDampening(BlockState state) {
      return 0;
   }
}
