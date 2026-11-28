package net.ruleta;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.ItemLike;
import net.ruleta.burbuja.Burbujas;
import net.ruleta.cajero.Cajero;
import net.ruleta.candado.Candados;
import net.ruleta.creeperes.Creeperes;
import net.ruleta.creeperrojo.CreeperRojo;
import net.ruleta.estatua.EstatuaNutria;
import net.ruleta.herobrine.Herobrine;
import net.ruleta.meteoritos.MeteoritosMod;
import net.ruleta.mobs.MobsNuevos;
import net.ruleta.nuclear.NuclearCreeperMod;
import net.ruleta.objetos.Objetos;
import net.ruleta.phora.PhoraMod;
import net.ruleta.puerta.BloquesBunker;
import net.ruleta.puerta.PuertaBunker;
import net.ruleta.tragamonedas.Tragamonedas;

public final class PestanaRuleta {
   public static final Reg<Item> ICONO = Reg.item("ruletafanproject", "ruletafanproject", () -> new Item(new Properties()));
   public static final Reg<CreativeModeTab> PESTANA = Reg.pestana(
      "ruletafanproject",
      "ruletafanproject",
      () -> FabricItemGroup.builder()
         .title(Component.translatable("itemGroup.ruleta"))
         .icon(() -> new ItemStack((ItemLike)ICONO.get()))
         .displayItems((parametros, salida) -> {
            salida.accept((ItemLike)PuertaBunker.ITEM.get());
            salida.accept((ItemLike)PuertaBunker.ITEM_GRANDE.get());
            salida.accept((ItemLike)BloquesBunker.ITEM_LADRILLO.get());
            salida.accept((ItemLike)BloquesBunker.ITEM_ESCALERA.get());
            salida.accept((ItemLike)BloquesBunker.ITEM_PISO.get());
            salida.accept((ItemLike)BloquesBunker.ITEM_PELIGRO.get());
            salida.accept((ItemLike)PhoraMod.PALO.get());
            salida.accept((ItemLike)MeteoritosMod.PALO.get());
            salida.accept((ItemLike)NuclearCreeperMod.GLEBANOIDE_TOTEM.get());
            salida.accept((ItemLike)NuclearCreeperMod.NUTRITOTEM.get());
            salida.accept((ItemLike)NuclearCreeperMod.EON_TOTEM.get());
            salida.accept((ItemLike)NuclearCreeperMod.YUNQUE_TOTEM.get());
            salida.accept((ItemLike)NuclearCreeperMod.GOLEM_TOTEM.get());
            salida.accept((ItemLike)NuclearCreeperMod.RED_HEART.get());
            salida.accept((ItemLike)NuclearCreeperMod.ABSORPTION_HEART.get());
            salida.accept((ItemLike)NuclearCreeperMod.NUCLEAR_CREEPER_SPAWN_EGG.get());
            salida.accept((ItemLike)CreeperRojo.HUEVO.get());
            salida.accept((ItemLike)Creeperes.HUEVO_CESPED.get());
            salida.accept((ItemLike)Herobrine.HUEVO.get());
            salida.accept((ItemLike)MobsNuevos.HUEVO_BOMBA_WARDEN.get());
            salida.accept((ItemLike)Objetos.LINTERNA.get());
            salida.accept((ItemLike)Objetos.BATERIA.get());
            salida.accept((ItemLike)Objetos.DILITIO.get());
            salida.accept((ItemLike)Objetos.PIEDRA_DILITIO.get());
            salida.accept((ItemLike)Objetos.PALO_REGRESO.get());
            salida.accept((ItemLike)Candados.CHIP_PHORA.get());
            salida.accept((ItemLike)Candados.CANDADO.get());
            salida.accept((ItemLike)Burbujas.ITEM.get());
            salida.accept((ItemLike)Tragamonedas.ITEM.get());

            for (Reg<Item> d : Tragamonedas.todasLasDeditas()) {
               salida.accept((ItemLike)d.get());
            }

            salida.accept((ItemLike)Cajero.ITEM.get());
            salida.accept((ItemLike)EstatuaNutria.ITEM.get());
         })
         .build()
   );

   private PestanaRuleta() {
   }

   public static void init() {
   }
}
