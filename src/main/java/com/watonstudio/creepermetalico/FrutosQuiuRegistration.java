package com.watonstudio.creepermetalico;

import net.minecraft.world.InteractionResult;

import java.util.List;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.ItemLore;

public class FrutosQuiuRegistration {
   public static final Item HUEVO_EBURIA = (Item)Registry.register(BuiltInRegistries.ITEM, com.watonstudio.creepermetalico.compat.Ids.item(Identifier.fromNamespaceAndPath("watonstudio", "huevo_eburia")), new HuevoEburiaItem(
         ajustes("Huevo Eburia")
            .component(
               DataComponents.LORE,
               new ItemLore(
                  List.of(
                     texto("Salud mejorada (01:00)", 5592575),
                     texto("", 16777215),
                     texto("Al aplicarse:", 11141290),
                     texto("Salud máxima: +4", 5592575),
                     texto("", 16777215),
                     texto("Encuéntralo y coséchalo en", 11184810),
                     texto("los árboles del Centro de Quiu.", 11184810),
                     texto("", 16777215),
                     texto("⚠ Atención: Al alcanzar su punto", 16777045),
                     texto("máximo de crecimiento debes", 11184810),
                     texto("recogerlo lo antes posible.", 11184810),
                     texto("Después de un tiempo se pudrirá", 11184810),
                     texto("si no lo cosechas.", 11184810),
                     texto("", 16777215),
                     texto("Utiliza unas Tijeras mientras tienes", 11184810),
                     texto("el Huevo en la Mano Secundaria", 11184810),
                     texto("para extraer Pelo de Eburia.", 11184810),
                     texto("", 16777215),
                     texto("Utiliza un Hacha mientras tienes", 11184810),
                     texto("un Huevo en la Mano Secundaria", 11184810),
                     texto("para extraer la Semilla.", 11184810)
                  )
               )
            )
      ));
   public static final Item ARANDANO_NOCTURNO = (Item)Registry.register(BuiltInRegistries.ITEM, com.watonstudio.creepermetalico.compat.Ids.item(Identifier.fromNamespaceAndPath("watonstudio", "arandano_nocturno")), new ArandanoNocturnoItem(
         ajustes("Arándano Nocturno")
            .component(
               DataComponents.LORE,
               new ItemLore(
                  List.of(
                     texto("Visión nocturna (03:00)", 5592575),
                     texto("", 16777215),
                     texto("Encuéntralo y coséchalo en", 11184810),
                     texto("los árboles del Centro de Quiu.", 11184810),
                     texto("", 16777215),
                     texto("⚠ Atención: Al alcanzar su punto", 16777045),
                     texto("máximo de crecimiento debes", 11184810),
                     texto("recogerlo lo antes posible.", 11184810),
                     texto("Después de un tiempo se pudrirá", 11184810),
                     texto("si no lo cosechas.", 11184810),
                     texto("", 16777215),
                     texto("Utiliza un Hacha mientras tienes", 11184810),
                     texto("el Arándano en la Mano Secundaria", 11184810),
                     texto("para extraer la Semilla.", 11184810)
                  )
               )
            )
      ));
   public static final Item PELO_EBURIA = (Item)Registry.register(BuiltInRegistries.ITEM, com.watonstudio.creepermetalico.compat.Ids.item(Identifier.fromNamespaceAndPath("watonstudio", "pelo_eburia")), new Item(ajustes("Pelo de Eburia")));
   public static final Item SEMILLA_EBURIA = (Item)Registry.register(BuiltInRegistries.ITEM, com.watonstudio.creepermetalico.compat.Ids.item(Identifier.fromNamespaceAndPath("watonstudio", "semilla_eburia")), new Item(ajustes("Semilla de Eburia")));
   public static final Item SEMILLA_ARANDANO_NOCTURNO = (Item)Registry.register(BuiltInRegistries.ITEM, com.watonstudio.creepermetalico.compat.Ids.item(Identifier.fromNamespaceAndPath("watonstudio", "semilla_arandano_nocturno")), new Item(ajustes("Semilla de Arándano Nocturno")));

   private static Component texto(String contenido, int color) {
      return Component.literal(contenido)
         .setStyle(Style.EMPTY.withColor(color).withItalic(false).withBold(false).withUnderlined(false).withStrikethrough(false).withObfuscated(false));
   }

   private static Properties ajustes(String nombre) {
      return new Properties().stacksTo(64).component(DataComponents.ITEM_NAME, texto(nombre, 5636095));
   }

   private static void registrarExtraccion() {
      UseItemCallback.EVENT.register((UseItemCallback)(player, world, hand) -> {
         if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
         }

         ItemStack herramienta = player.getMainHandItem();
         ItemStack fruto = player.getOffhandItem();
         Item resultado = null;
         if (fruto.is(HUEVO_EBURIA) && herramienta.is(Items.SHEARS)) {
            resultado = PELO_EBURIA;
         } else if (fruto.is(HUEVO_EBURIA) && herramienta.is(ItemTags.AXES)) {
            resultado = SEMILLA_EBURIA;
         } else if (fruto.is(ARANDANO_NOCTURNO) && herramienta.is(ItemTags.AXES)) {
            resultado = SEMILLA_ARANDANO_NOCTURNO;
         }

         if (resultado == null) {
            return InteractionResult.PASS;
         }

         if (!world.isClientSide()) {
            if (!player.isCreative()) {
               fruto.shrink(1);
            }

            player.addItem(new ItemStack(resultado));
         }

         return InteractionResult.SUCCESS;
      });
   }

   public static void initialize() {
      registrarExtraccion();
      System.out.println("Frutos del Centro de Quiu registrados.");
   }
}
