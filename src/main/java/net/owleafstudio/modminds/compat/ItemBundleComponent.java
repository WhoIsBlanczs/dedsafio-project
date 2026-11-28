package net.owleafstudio.modminds.compat;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.core.component.DataComponentType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.owleafstudio.modminds.init.ModmindsModItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ItemBundleComponent {
   private static final Logger LOGGER = LoggerFactory.getLogger("modminds-compat");
   private static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, "modminds");
   private static final RegistryObject<DataComponentType<ItemContainerContents>> BUNDLE = register("shulker_bundle");
   private static final RegistryObject<DataComponentType<ItemContainerContents>> AMARILLO = register("shulker_bundle_amarillo");
   private static final RegistryObject<DataComponentType<ItemContainerContents>> AZUL = register("shulker_bundle_azul");
   private static final RegistryObject<DataComponentType<ItemContainerContents>> BLANCO = register("shulker_bundle_blanco");
   private static final RegistryObject<DataComponentType<ItemContainerContents>> CELESTE = register("shulker_bundle_celeste");
   private static final RegistryObject<DataComponentType<ItemContainerContents>> CIAN = register("shulker_bundle_cian");
   private static final RegistryObject<DataComponentType<ItemContainerContents>> GRIS = register("shulker_bundle_gris");
   private static final RegistryObject<DataComponentType<ItemContainerContents>> GRIS_OSCURO = register("shulker_bundle_gris_oscuro");
   private static final RegistryObject<DataComponentType<ItemContainerContents>> LIMA = register("shulker_bundle_lima");
   private static final RegistryObject<DataComponentType<ItemContainerContents>> MAGENTA = register("shulker_bundle_magenta");
   private static final RegistryObject<DataComponentType<ItemContainerContents>> MARRON = register("shulker_bundle_marron");
   private static final RegistryObject<DataComponentType<ItemContainerContents>> MORADO = register("shulker_bundle_morado");
   private static final RegistryObject<DataComponentType<ItemContainerContents>> NARANJA = register("shulker_bundle_naranja");
   private static final RegistryObject<DataComponentType<ItemContainerContents>> NEGRA = register("shulker_bundle_negra");
   private static final RegistryObject<DataComponentType<ItemContainerContents>> ROJA = register("shulker_bundle_roja");
   private static final RegistryObject<DataComponentType<ItemContainerContents>> ROSA = register("shulker_bundle_rosa");
   private static final RegistryObject<DataComponentType<ItemContainerContents>> VERDE_OSCURO = register("shulker_bundle_verde_oscuro");
   private static final Map<Item, DataComponentType<ItemContainerContents>> BY_ITEM = new IdentityHashMap<>();
   private static boolean mapped;

   private ItemBundleComponent() {
   }

   private static RegistryObject<DataComponentType<ItemContainerContents>> register(String name) {
      Supplier<DataComponentType<ItemContainerContents>> supplier = () -> DataComponentType.builder()
         .persistent(ItemContainerContents.CODEC)
         .networkSynchronized(ItemContainerContents.STREAM_CODEC)
         .cacheEncoding()
         .build();
      return COMPONENTS.register(name, supplier);
   }

   public static void init() {
      map();
      LOGGER.debug("Bundle components ready: {}", BY_ITEM.size());
   }

   public static DataComponentType<ItemContainerContents> componentFor(ItemStack stack) {
      if (stack.isEmpty()) {
         return null;
      } else {
         map();
         return BY_ITEM.get(stack.getItem());
      }
   }

   private static void map() {
      if (!mapped) {
         mapped = true;
         put(ModmindsModItems.SHULKER_BUNDLE.get(), BUNDLE);
         put(ModmindsModItems.SHULKER_BUNDLE_AMARILLO.get(), AMARILLO);
         put(ModmindsModItems.SHULKER_BUNDLE_AZUL.get(), AZUL);
         put(ModmindsModItems.SHULKER_BUNDLE_BLANCO.get(), BLANCO);
         put(ModmindsModItems.SHULKER_BUNDLE_CELESTE.get(), CELESTE);
         put(ModmindsModItems.SHULKER_BUNDLE_CIAN.get(), CIAN);
         put(ModmindsModItems.SHULKER_BUNDLE_GRIS.get(), GRIS);
         put(ModmindsModItems.SHULKER_BUNDLE_GRIS_OSCURO.get(), GRIS_OSCURO);
         put(ModmindsModItems.SHULKER_BUNDLE_LIMA.get(), LIMA);
         put(ModmindsModItems.SHULKER_BUNDLE_MAGENTA.get(), MAGENTA);
         put(ModmindsModItems.SHULKER_BUNDLE_MARRON.get(), MARRON);
         put(ModmindsModItems.SHULKER_BUNDLE_MORADO.get(), MORADO);
         put(ModmindsModItems.SHULKER_BUNDLE_NARANJA.get(), NARANJA);
         put(ModmindsModItems.SHULKER_BUNDLE_NEGRA.get(), NEGRA);
         put(ModmindsModItems.SHULKER_BUNDLE_ROJA.get(), ROJA);
         put(ModmindsModItems.SHULKER_BUNDLE_ROSA.get(), ROSA);
         put(ModmindsModItems.SHULKER_BUNDLE_VERDE_OSCURO.get(), VERDE_OSCURO);
      }
   }

   private static void put(Item item, RegistryObject<DataComponentType<ItemContainerContents>> type) {
      if (item != null && type != null && type.isPresent()) {
         BY_ITEM.put(item, type.get());
      }
   }
}
