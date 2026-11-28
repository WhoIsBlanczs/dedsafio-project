package net.ruleta.estatua;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.Builder;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.ruleta.Reg;

public final class EstatuaNutria {
   public static final Reg<Block> BLOQUE = Reg.block(
      "ruletafanproject",
      "estatua_nutria",
      () -> new EstatuaNutriaBloque(
         Properties.of().mapColor(MapColor.STONE).strength(-1.0F, 3600000.0F).sound(SoundType.STONE).noOcclusion().pushReaction(PushReaction.BLOCK)
      )
   );
   public static final Reg<Item> ITEM = Reg.item(
      "ruletafanproject", "estatua_nutria", () -> new BlockItem(BLOQUE.get(), new net.minecraft.world.item.Item.Properties())
   );
   public static final Reg<BlockEntityType<EstatuaNutriaBE>> BE = Reg.blockEntity(
      "ruletafanproject", "estatua_nutria", () -> Builder.of(EstatuaNutriaBE::new, new Block[]{BLOQUE.get()}).build(null)
   );

   private EstatuaNutria() {
   }

   public static void init() {
      EstatuaNutriaEventos.registrar();
   }
}
