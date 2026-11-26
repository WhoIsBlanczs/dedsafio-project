package net.ruleta.tragamonedas;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.Identifier;
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
      "ruleta",
      "tragamonedas",
      () -> new TragamonedasBloque(
         Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).strength(3.0F, 6.0F).sound(SoundType.METAL).noOcclusion().requiresCorrectToolForDrops()
      )
   );
   public static final Reg<Item> ITEM = Reg.item("ruleta", "tragamonedas", () -> new BlockItem(BLOQUE.get(), new net.minecraft.world.item.Item.Properties()));
   public static final Reg<Item> DEDITA = Reg.item("ruleta", "dedita_casino", () -> new DeditaItem(new net.minecraft.world.item.Item.Properties()));
   public static final Reg<Item> DEDITA_AZUL = Reg.item(
      "ruleta", "dedita_azul", () -> new DeditaItem(new net.minecraft.world.item.Item.Properties(), ChatFormatting.BLUE)
   );
   public static final Reg<Item> DEDITA_ROJO = Reg.item(
      "ruleta", "dedita_rojo", () -> new DeditaItem(new net.minecraft.world.item.Item.Properties(), ChatFormatting.RED)
   );
   public static final Reg<Item> DEDITA_CIAN = Reg.item(
      "ruleta", "dedita_cian", () -> new DeditaItem(new net.minecraft.world.item.Item.Properties(), ChatFormatting.AQUA)
   );
   public static final Reg<Item> DEDITA_VERDE = Reg.item(
      "ruleta", "dedita_verde", () -> new DeditaItem(new net.minecraft.world.item.Item.Properties(), ChatFormatting.GREEN)
   );
   public static final Reg<BlockEntityType<TragamonedasBE>> BE = Reg.blockEntity(
      "ruleta", "tragamonedas", () -> Builder.of(TragamonedasBE::new, new Block[]{BLOQUE.get()}).build(null)
   );
   public static final Reg<SoundEvent> SONIDO_PALANCA = Reg.sonido(
      "ruleta", "tragamonedas.palanca", () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("ruleta", "tragamonedas.palanca"))
   );
   public static final Reg<SoundEvent> SONIDO_GIRO = Reg.sonido(
      "ruleta", "tragamonedas.giro", () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("ruleta", "tragamonedas.giro"))
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
