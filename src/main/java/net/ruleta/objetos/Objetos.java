package net.ruleta.objetos;

import net.minecraft.ChatFormatting;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;
import net.ruleta.Reg;

public final class Objetos {
   public static final Reg<Item> PALO_REGRESO = Reg.item("ruleta", "palo_regreso", () -> new PaloRegreso(net.ruleta.Reg.props().stacksTo(1).rarity(Rarity.EPIC)));
   public static final Reg<Item> LINTERNA = Reg.item("ruleta", "linterna", () -> new Linterna(net.ruleta.Reg.props().stacksTo(1)));
   public static final Reg<Item> BATERIA = Reg.item("ruleta", "bateria", () -> new ItemColoreado(net.ruleta.Reg.props().stacksTo(16), ChatFormatting.AQUA));
   public static final Reg<Item> DILITIO = Reg.item("ruleta", "dilitio", () -> new Item(net.ruleta.Reg.props()));
   public static final Reg<Item> PIEDRA_DILITIO = Reg.item("ruleta", "piedra_dilitio", () -> new Item(net.ruleta.Reg.props()));
   public static final Reg<SoundEvent> SONIDO_REGRESO = Reg.sonido(
      "ruleta", "palo.regreso", () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("ruleta", "palo.regreso"))
   );

   private Objetos() {
   }

   public static void init() {
   }
}
