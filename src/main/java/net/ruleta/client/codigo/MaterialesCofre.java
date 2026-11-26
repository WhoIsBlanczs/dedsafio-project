package net.ruleta.client.codigo;

import net.minecraft.client.renderer.Sheets;
import Nonenet.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;

public final class MaterialesCofre {
   public static final Material IZQUIERDA = new Material(Sheets.CHEST_SHEET, Identifier.fromNamespaceAndPath("ruleta", "entity/chest/locked_left"));
   public static final Material DERECHA = new Material(Sheets.CHEST_SHEET, Identifier.fromNamespaceAndPath("ruleta", "entity/chest/locked_right"));

   private MaterialesCofre() {
   }
}
