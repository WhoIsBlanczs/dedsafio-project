package net.ruleta.tragamonedas;

import java.util.List;
import net.minecraft.ChatFormatting;
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
import net.ruleta.Reg;

public final class Tragamonedas {
   public static final Reg<Block> BLOQUE = Reg.block(
      "ruletafanproject",
      "tragamonedas",
      () -> new TragamonedasBloque(
         Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).strength(3.0F, 6.0F).sound(SoundType.METAL).noOcclusion().requiresCorrectToolForDrops()
      )
   );
   public static final Reg<Item> ITEM = Reg.item(
      "ruletafanproject", "tragamonedas", () -> new BlockItem(BLOQUE.get(), new net.minecraft.world.item.Item.Properties())
   );
   public static final Reg<Item> DEDITA = Reg.item("ruletafanproject", "dedita_casino", () -> new DeditaItem(new net.minecraft.world.item.Item.Properties()));
   public static final Reg<Item> DEDITA_AZUL = Reg.item(
      "ruletafanproject", "dedita_azul", () -> new DeditaItem(new net.minecraft.world.item.Item.Properties(), ChatFormatting.BLUE)
   );
   public static final Reg<Item> DEDITA_ROJO = Reg.item(
      "ruletafanproject", "dedita_rojo", () -> new DeditaItem(new net.minecraft.world.item.Item.Properties(), ChatFormatting.RED)
   );
   public static final Reg<Item> DEDITA_CIAN = Reg.item(
      "ruletafanproject", "dedita_cian", () -> new DeditaItem(new net.minecraft.world.item.Item.Properties(), ChatFormatting.AQUA)
   );
   public static final Reg<Item> DEDITA_VERDE = Reg.item(
      "ruletafanproject", "dedita_verde", () -> new DeditaItem(new net.minecraft.world.item.Item.Properties(), ChatFormatting.GREEN)
   );
   public static final Reg<BlockEntityType<TragamonedasBE>> BE = Reg.blockEntity(
      "ruletafanproject", "tragamonedas", () -> Builder.of(TragamonedasBE::new, new Block[]{BLOQUE.get()}).build(null)
   );
   public static final Reg<SoundEvent> SONIDO_PALANCA = Reg.sonido(
      "ruletafanproject",
      "tragamonedas.palanca",
      () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("ruletafanproject", "tragamonedas.palanca"))
   );
   public static final Reg<SoundEvent> SONIDO_GIRO = Reg.sonido(
      "ruletafanproject",
      "tragamonedas.giro",
      () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("ruletafanproject", "tragamonedas.giro"))
   );

   private Tragamonedas() {
   }

   public static List<Reg<Item>> todasLasDeditas() {
      return List.of(DEDITA, DEDITA_AZUL, DEDITA_ROJO, DEDITA_CIAN, DEDITA_VERDE);
   }

   public static void init() {
      ComandoDeditas.registrar();
   }
}
