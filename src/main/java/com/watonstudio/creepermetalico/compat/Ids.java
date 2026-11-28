package com.watonstudio.creepermetalico.compat;

import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;

/**
 * Desde 1.21.2 cada Item.Properties y Block.Properties tiene que saber su id antes de crear el objeto.
 * Este ayudante recuerda el id que se está registrando para que los mixins lo pongan solos.
 */
public final class Ids {
   public static final String NS = "watonstudio";
   static final ThreadLocal<Identifier> ITEM = new ThreadLocal<>();
   static final ThreadLocal<Identifier> BLOQUE = new ThreadLocal<>();

   private Ids() {
   }

   public static Identifier id(String nombre) {
      return nombre.indexOf(':') >= 0 ? Identifier.parse(nombre) : Identifier.fromNamespaceAndPath(NS, nombre);
   }

   public static Identifier item(Identifier id) {
      ITEM.set(id);
      return id;
   }

   public static Identifier item(String id) {
      return item(id(id));
   }

   public static Identifier bloque(Identifier id) {
      BLOQUE.set(id);
      return id;
   }

   public static Identifier bloque(String id) {
      return bloque(id(id));
   }

   public static <T> T conItem(Identifier id, Supplier<T> s) {
      Identifier antes = ITEM.get();
      ITEM.set(id);
      try {
         return s.get();
      } finally {
         ITEM.set(antes);
      }
   }

   public static <T> T conBloque(Identifier id, Supplier<T> s) {
      Identifier antes = BLOQUE.get();
      BLOQUE.set(id);
      try {
         return s.get();
      } finally {
         BLOQUE.set(antes);
      }
   }

   public static ResourceKey<EntityType<?>> entidad(String nombre) {
      return ResourceKey.create(Registries.ENTITY_TYPE, id(nombre));
   }

   public static ResourceKey<EntityType<?>> entidad(Identifier id) {
      return ResourceKey.create(Registries.ENTITY_TYPE, id);
   }

   public static Identifier pendienteItem() {
      return ITEM.get();
   }

   public static Identifier pendienteBloque() {
      return BLOQUE.get();
   }
}
