package net.ruleta.puerta;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;
import net.ruleta.Reg;

public final class BloquesBunker {
   public static final Reg<Block> LADRILLO = Reg.block(
      "ruleta",
      "ladrillo_bunker",
      () -> new LadrilloBunkerBloque(
         net.ruleta.Reg.bprops().mapColor(MapColor.COLOR_GRAY).strength(3.0F, 12.0F).sound(SoundType.DEEPSLATE_BRICKS).requiresCorrectToolForDrops()
      )
   );
   public static final Reg<Block> ESCALERA = Reg.block(
      "ruleta",
      "escalera_bunker",
      () -> new StairBlock(
         LADRILLO.get().defaultBlockState(),
         net.ruleta.Reg.bprops().mapColor(MapColor.COLOR_GRAY).strength(3.0F, 12.0F).sound(SoundType.DEEPSLATE_BRICKS).requiresCorrectToolForDrops()
      )
   );
   public static final Reg<Block> PISO = Reg.block(
      "ruleta",
      "piso_bunker",
      () -> new Block(net.ruleta.Reg.bprops().mapColor(MapColor.COLOR_BROWN).strength(2.0F, 8.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final Reg<Block> PELIGRO = Reg.block(
      "ruleta",
      "peligro_bunker",
      () -> new Block(net.ruleta.Reg.bprops().mapColor(MapColor.COLOR_YELLOW).strength(3.0F, 12.0F).sound(SoundType.DEEPSLATE_BRICKS).requiresCorrectToolForDrops())
   );
   public static final Reg<Item> ITEM_LADRILLO = Reg.item(
      "ruleta", "ladrillo_bunker", () -> new BlockItem(LADRILLO.get(), net.ruleta.Reg.props())
   );
   public static final Reg<Item> ITEM_ESCALERA = Reg.item(
      "ruleta", "escalera_bunker", () -> new BlockItem(ESCALERA.get(), net.ruleta.Reg.props())
   );
   public static final Reg<Item> ITEM_PISO = Reg.item("ruleta", "piso_bunker", () -> new BlockItem(PISO.get(), net.ruleta.Reg.props()));
   public static final Reg<Item> ITEM_PELIGRO = Reg.item(
      "ruleta", "peligro_bunker", () -> new BlockItem(PELIGRO.get(), net.ruleta.Reg.props())
   );

   private BloquesBunker() {
   }

   public static void init() {
   }
}
