package net.ruleta.playa;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.OffsetType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.ruleta.Reg;

public final class Playa {
   public static final Reg<Block> CONCHA = Reg.block("ruleta", "concha", () -> new DecoPlayaBloque(propiedades(MapColor.SAND, SoundType.SAND), "Concha de mar"));
   public static final Reg<Block> CARACOLA = Reg.block("ruleta", "caracola", () -> new DecoPlayaBloque(propiedades(MapColor.SAND, SoundType.SAND), "Caracola"));
   public static final Reg<Block> PIEDRITAS = Reg.block(
      "ruleta", "piedritas", () -> new DecoPlayaBloque(propiedades(MapColor.STONE, SoundType.STONE), "Piedritas")
   );
   public static final Reg<Block> ESTRELLA_MAR = Reg.block(
      "ruleta", "estrella_mar", () -> new DecoPlayaBloque(propiedades(MapColor.COLOR_ORANGE, SoundType.SAND), "Estrella de mar")
   );
   public static final Reg<Item> ITEM_CONCHA = Reg.item("ruleta", "concha", () -> new Playa.NombreBlockItem(CONCHA.get(), net.ruleta.Reg.props(), "Concha de mar"));
   public static final Reg<Item> ITEM_CARACOLA = Reg.item("ruleta", "caracola", () -> new Playa.NombreBlockItem(CARACOLA.get(), net.ruleta.Reg.props(), "Caracola"));
   public static final Reg<Item> ITEM_PIEDRITAS = Reg.item(
      "ruleta", "piedritas", () -> new Playa.NombreBlockItem(PIEDRITAS.get(), net.ruleta.Reg.props(), "Piedritas")
   );
   public static final Reg<Item> ITEM_ESTRELLA_MAR = Reg.item(
      "ruleta", "estrella_mar", () -> new Playa.NombreBlockItem(ESTRELLA_MAR.get(), net.ruleta.Reg.props(), "Estrella de mar")
   );

   private Playa() {
   }

   private static net.minecraft.world.level.block.state.BlockBehaviour.Properties propiedades(MapColor color, SoundType sonido) { return net.ruleta.Reg.bprops().mapColor(color).noCollision().instabreak().sound(sonido); }

   public static void init() {
      ResourceKey<CreativeModeTab> pestana = ResourceKey.create(Registries.CREATIVE_MODE_TAB, Reg.id("ruleta", "ruleta"));
      ItemGroupEvents.modifyEntriesEvent(pestana).register((ModifyEntries)salida -> {
         salida.accept((ItemLike)ITEM_CONCHA.get());
         salida.accept((ItemLike)ITEM_CARACOLA.get());
         salida.accept((ItemLike)ITEM_PIEDRITAS.get());
         salida.accept((ItemLike)ITEM_ESTRELLA_MAR.get());
      });
   }

   private static final class NombreBlockItem extends BlockItem {
      private final String nombre;

      NombreBlockItem(Block b, Properties p, String nombre) {
         super(b, p);
         this.nombre = nombre;
      }

      public Component getName(ItemStack pila) {
         return Component.literal(this.nombre);
      }
   }
}
