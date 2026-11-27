package net.ruleta.arboles;

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
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.ruleta.Reg;

public final class Arboles {
   public static final Reg<Block> BROTE_COSTA = Reg.block("ruleta", "brote_costa", () -> new BroteBloque(propiedades(), 0));
   public static final Reg<Block> BROTE_PALMERA = Reg.block("ruleta", "brote_palmera", () -> new BroteBloque(propiedades(), 1));
   public static final Reg<Item> ITEM_BROTE_COSTA = Reg.item(
      "ruleta", "brote_costa", () -> new Arboles.NombreBlockItem(BROTE_COSTA.get(), net.ruleta.Reg.props(), "Retoño de árbol de costa")
   );
   public static final Reg<Item> ITEM_BROTE_PALMERA = Reg.item(
      "ruleta", "brote_palmera", () -> new Arboles.NombreBlockItem(BROTE_PALMERA.get(), net.ruleta.Reg.props(), "Retoño de palmera")
   );
   public static final Reg<Item> PINCEL = Reg.item("ruleta", "pincel_bosque", () -> new PincelBosqueItem(net.ruleta.Reg.props().stacksTo(1)));

   private Arboles() {
   }

   private static net.minecraft.world.level.block.state.BlockBehaviour.Properties propiedades() { return null; }

   public static void init() { }

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
