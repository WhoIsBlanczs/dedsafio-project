package net.ruleta.cajero;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.ruleta.Reg;

public final class Cajero {
   public static final Reg<Block> BLOQUE = Reg.block(
      "ruleta",
      "cajero",
      () -> new CajeroBloque(
         net.ruleta.Reg.bprops().mapColor(MapColor.COLOR_LIGHT_GRAY).strength(-1.0F, 3600000.0F).sound(SoundType.METAL).noOcclusion().pushReaction(PushReaction.IMMOVEABLE)
      )
   );
   public static final Reg<Item> ITEM = Reg.item("ruleta", "cajero", () -> new BlockItem(BLOQUE.get(), net.ruleta.Reg.props()));

   private Cajero() {
   }

   public static void init() {
      CajeroRed.registrar();
      CajeroEventos.registrar();
   }
}
