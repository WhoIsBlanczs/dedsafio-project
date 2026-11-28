package net.ruleta.cajero;

import net.minecraft.util.StringRepresentable;

public enum ParteCajero implements StringRepresentable {
   LOWER("lower"),
   UPPER("upper");

   private final String nombre;

   ParteCajero(String nombre) {
      this.nombre = nombre;
   }

   public int altura() {
      return this.ordinal();
   }

   public String getSerializedName() {
      return this.nombre;
   }
}
