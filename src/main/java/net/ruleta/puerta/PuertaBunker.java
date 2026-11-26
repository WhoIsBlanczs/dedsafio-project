package net.ruleta.puerta;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
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

public final class PuertaBunker {
   public static final String SONIDOS_ESPACIO = "ruleta_puerta";
   public static final Reg<SoundEvent> SONIDO_ABRIR = Reg.sonido(
      "ruleta_puerta",
      "puerta_bunker.abrir",
      () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("ruleta_puerta", "puerta_bunker.abrir"))
   );
   public static final Reg<SoundEvent> SONIDO_CERRAR = Reg.sonido(
      "ruleta_puerta",
      "puerta_bunker.cerrar",
      () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("ruleta_puerta", "puerta_bunker.cerrar"))
   );
   public static final Reg<Block> BLOQUE = Reg.block(
      "ruleta",
      "puerta_bunker",
      () -> new PuertaBunkerBloque(
         Properties.of().mapColor(MapColor.COLOR_GRAY).strength(20.0F, 1200.0F).sound(SoundType.NETHERITE_BLOCK).noOcclusion().pushReaction(PushReaction.BLOCK),
         false
      )
   );
   public static final Reg<Block> BLOQUE_HOJA = Reg.block(
      "ruleta",
      "puerta_bunker_hoja",
      () -> new PuertaBunkerHoja(
         Properties.of()
            .mapColor(MapColor.COLOR_GRAY)
            .strength(-1.0F, 3600000.0F)
            .noLootTable()
            .noOcclusion()
            .isViewBlocking((st, lv, p) -> false)
            .isSuffocating((st, lv, p) -> false)
            .pushReaction(PushReaction.BLOCK)
      )
   );
   public static final Reg<Block> BLOQUE_GRANDE = Reg.block(
      "ruleta",
      "puerta_grande",
      () -> new PuertaBunkerBloque(
         Properties.of().mapColor(MapColor.COLOR_GRAY).strength(20.0F, 1200.0F).sound(SoundType.NETHERITE_BLOCK).noOcclusion().pushReaction(PushReaction.BLOCK),
         true
      )
   );
   public static final Reg<Item> ITEM = Reg.item("ruleta", "puerta_bunker", () -> new BlockItem(BLOQUE.get(), new net.minecraft.world.item.Item.Properties()));
   public static final Reg<Item> ITEM_GRANDE = Reg.item(
      "ruleta", "puerta_grande", () -> new BlockItem(BLOQUE_GRANDE.get(), new net.minecraft.world.item.Item.Properties())
   );
   public static final Reg<BlockEntityType<PuertaBunkerBE>> BE = Reg.blockEntity(
      "ruleta", "puerta_bunker", () -> Builder.of(PuertaBunkerBE::new, new Block[]{BLOQUE.get(), BLOQUE_GRANDE.get()}).build(null)
   );

   private PuertaBunker() {
   }

   public static void init() {
      BloquesBunker.init();
   }
}
