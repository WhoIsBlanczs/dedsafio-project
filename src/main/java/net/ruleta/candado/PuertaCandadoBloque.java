package net.ruleta.candado;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.BlockSetType;

public class PuertaCandadoBloque extends DoorBlock {
   public PuertaCandadoBloque(Properties propiedades) {
      super(BlockSetType.IRON, propiedades);
   }

   public ItemStack getCloneItemStack(LevelReader nivel, BlockPos pos, BlockState estado) {
      return new ItemStack(Items.IRON_DOOR);
   }
}
