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
      "ruletafanproject",
      "ladrillo_bunker",
      () -> new LadrilloBunkerBloque(
         Properties.of().mapColor(MapColor.COLOR_GRAY).strength(3.0F, 12.0F).sound(SoundType.DEEPSLATE_BRICKS).requiresCorrectToolForDrops()
      )
   );
   public static final Reg<Block> ESCALERA = Reg.block(
      "ruletafanproject",
      "escalera_bunker",
      () -> new StairBlock(
         LADRILLO.get().defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_GRAY).strength(3.0F, 12.0F).sound(SoundType.DEEPSLATE_BRICKS).requiresCorrectToolForDrops()
      )
   );
   public static final Reg<Block> PISO = Reg.block(
      "ruletafanproject",
      "piso_bunker",
      () -> new Block(Properties.of().mapColor(MapColor.COLOR_BROWN).strength(2.0F, 8.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final Reg<Block> PELIGRO = Reg.block(
      "ruletafanproject",
      "peligro_bunker",
      () -> new Block(Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(3.0F, 12.0F).sound(SoundType.DEEPSLATE_BRICKS).requiresCorrectToolForDrops())
   );
   public static final Reg<Item> ITEM_LADRILLO = Reg.item(
      "ruletafanproject", "ladrillo_bunker", () -> new BlockItem(LADRILLO.get(), new net.minecraft.world.item.Item.Properties())
   );
   public static final Reg<Item> ITEM_ESCALERA = Reg.item(
      "ruletafanproject", "escalera_bunker", () -> new BlockItem(ESCALERA.get(), new net.minecraft.world.item.Item.Properties())
   );
   public static final Reg<Item> ITEM_PISO = Reg.item(
      "ruletafanproject", "piso_bunker", () -> new BlockItem(PISO.get(), new net.minecraft.world.item.Item.Properties())
   );
   public static final Reg<Item> ITEM_PELIGRO = Reg.item(
      "ruletafanproject", "peligro_bunker", () -> new BlockItem(PELIGRO.get(), new net.minecraft.world.item.Item.Properties())
   );

   private BloquesBunker() {
   }

   public static void init() {
   }
}
