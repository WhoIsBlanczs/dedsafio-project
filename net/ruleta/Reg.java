package net.ruleta;

import java.util.function.Supplier;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class Reg<T> implements Supplier<T> {
   private final T valor;

   private Reg(T valor) {
      this.valor = valor;
   }

   @Override
   public T get() {
      return this.valor;
   }

   public static ResourceLocation id(String espacio, String nombre) {
      return ResourceLocation.fromNamespaceAndPath(espacio, nombre);
   }

   public static <T extends Item> Reg<T> item(String espacio, String nombre, Supplier<T> fabrica) {
      return new Reg<>((T)Registry.register(BuiltInRegistries.ITEM, id(espacio, nombre), fabrica.get()));
   }

   public static <T extends Block> Reg<T> block(String espacio, String nombre, Supplier<T> fabrica) {
      return new Reg<>((T)Registry.register(BuiltInRegistries.BLOCK, id(espacio, nombre), fabrica.get()));
   }

   public static <T extends Entity> Reg<EntityType<T>> entidad(String espacio, String nombre, Supplier<EntityType<T>> fabrica) {
      return new Reg<>((EntityType<T>)Registry.register(BuiltInRegistries.ENTITY_TYPE, id(espacio, nombre), fabrica.get()));
   }

   public static <T extends Entity> Reg<EntityType<T>> entidadFabric(String espacio, String nombre, FabricEntityTypeBuilder<T> constructor) {
      return new Reg<>((EntityType<T>)Registry.register(BuiltInRegistries.ENTITY_TYPE, id(espacio, nombre), constructor.build()));
   }

   public static Reg<SoundEvent> sonido(String espacio, String nombre, Supplier<SoundEvent> fabrica) {
      return new Reg<>((SoundEvent)Registry.register(BuiltInRegistries.SOUND_EVENT, id(espacio, nombre), fabrica.get()));
   }

   public static Reg<Holder<MobEffect>> efecto(String espacio, String nombre, Supplier<? extends MobEffect> fabrica) {
      Holder<MobEffect> h = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, id(espacio, nombre), fabrica.get());
      return new Reg<>(h);
   }

   public static <T extends BlockEntity> Reg<BlockEntityType<T>> blockEntity(String espacio, String nombre, Supplier<BlockEntityType<T>> fabrica) {
      return new Reg<>((BlockEntityType<T>)Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id(espacio, nombre), fabrica.get()));
   }

   public static Reg<CreativeModeTab> pestana(String espacio, String nombre, Supplier<CreativeModeTab> fabrica) {
      return new Reg<>((CreativeModeTab)Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, id(espacio, nombre), fabrica.get()));
   }
}
