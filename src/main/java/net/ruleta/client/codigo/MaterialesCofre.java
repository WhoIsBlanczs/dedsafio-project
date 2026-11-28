package net.ruleta.client.codigo;

import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;

public final class MaterialesCofre {
   public static final Material IZQUIERDA = new Material(
      Sheets.CHEST_SHEET, ResourceLocation.fromNamespaceAndPath("ruletafanproject", "entity/chest/locked_left")
   );
   public static final Material DERECHA = new Material(
      Sheets.CHEST_SHEET, ResourceLocation.fromNamespaceAndPath("ruletafanproject", "entity/chest/locked_right")
   );

   private MaterialesCofre() {
   }
}
