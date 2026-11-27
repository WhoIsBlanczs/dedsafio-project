package net.ruleta;

import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
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
   private static final ThreadLocal<ResourceKey<Item>> ITEM_KEY = new ThreadLocal<>();
   private static final ThreadLocal<ResourceKey<Block>> BLOCK_KEY = new ThreadLocal<>();
   private static final ThreadLocal<ResourceKey<EntityType<?>>> ENTITY_KEY = new ThreadLocal<>();

   /** Item.Properties con el ID del item que se esta registrando (obligatorio desde 1.21.2). */
   public static Item.Properties props() {
      Item.Properties p = new Item.Properties();
      ResourceKey<Item> k = ITEM_KEY.get();
      return k != null ? p.setId(k) : p;
   }

   public static BlockBehaviour.Properties bprops() {
      BlockBehaviour.Properties p = BlockBehaviour.Properties.of();
      ResourceKey<Block> k = BLOCK_KEY.get();
      return k != null ? p.setId(k) : p;
   }

   public static BlockBehaviour.Properties bcopy(BlockBehaviour origen) {
      BlockBehaviour.Properties p = BlockBehaviour.Properties.ofFullCopy(origen);
      ResourceKey<Block> k = BLOCK_KEY.get();
      return k != null ? p.setId(k) : p;
   }

   public static ResourceKey<EntityType<?>> ekey() {
      return ENTITY_KEY.get();
   }

   private Reg(T valor) {
      this.valor = valor;
   }

   @Override
   public T get() {
      return this.valor;
   }

   public static Identifier id(String espacio, String nombre) {
      return Identifier.fromNamespaceAndPath(espacio, nombre);
   }

   public static <T extends Item> Reg<T> item(String espacio, String nombre, Supplier<T> fabrica) {
      ITEM_KEY.set(ResourceKey.create(Registries.ITEM, id(espacio, nombre)));
      try {
         return new Reg<>((T)Registry.register(BuiltInRegistries.ITEM, id(espacio, nombre), fabrica.get()));
      } finally {
         ITEM_KEY.remove();
      }
   }

   public static <T extends Block> Reg<T> block(String espacio, String nombre, Supplier<T> fabrica) {
      BLOCK_KEY.set(ResourceKey.create(Registries.BLOCK, id(espacio, nombre)));
      try {
         return new Reg<>((T)Registry.register(BuiltInRegistries.BLOCK, id(espacio, nombre), fabrica.get()));
      } finally {
         BLOCK_KEY.remove();
      }
   }

   public static <T extends Entity> Reg<EntityType<T>> entidad(String espacio, String nombre, Supplier<EntityType<T>> fabrica) {
      ENTITY_KEY.set(ResourceKey.create(Registries.ENTITY_TYPE, id(espacio, nombre)));
      try {
         return new Reg<>((EntityType<T>)Registry.register(BuiltInRegistries.ENTITY_TYPE, id(espacio, nombre), fabrica.get()));
      } finally {
         ENTITY_KEY.remove();
      }
   }

   public static <T extends Entity> Reg<EntityType<T>> entidadFabric(String espacio, String nombre, java.util.function.Function<ResourceKey<EntityType<?>>, EntityType<T>> constructor) {
      ResourceKey<EntityType<?>> k = ResourceKey.create(Registries.ENTITY_TYPE, id(espacio, nombre));
      return new Reg<>((EntityType<T>)Registry.register(BuiltInRegistries.ENTITY_TYPE, id(espacio, nombre), constructor.apply(k)));
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
