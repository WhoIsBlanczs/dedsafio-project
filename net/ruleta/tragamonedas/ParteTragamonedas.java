package net.ruleta.tragamonedas;

import net.minecraft.util.StringRepresentable;

public enum ParteTragamonedas implements StringRepresentable {
   LOWER("lower"),
   MIDDLE("middle"),
   UPPER("upper");

   private final String nombre;

   private ParteTragamonedas(String nombre) {
      this.nombre = nombre;
   }

   public int altura() {
      return this.ordinal();
   }

   public String getSerializedName() {
      return this.nombre;
   }
}
