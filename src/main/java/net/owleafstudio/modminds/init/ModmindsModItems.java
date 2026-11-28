package net.owleafstudio.modminds.init;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.owleafstudio.modminds.compat.ClientCompat;
import net.owleafstudio.modminds.item.AguaDelLimboItem;
import net.owleafstudio.modminds.item.AguapanelaItem;
import net.owleafstudio.modminds.item.AlaDeMurcielagoWardenItem;
import net.owleafstudio.modminds.item.AlaDerechaDeNaveItem;
import net.owleafstudio.modminds.item.AlaDerechaFrontalDeNaveItem;
import net.owleafstudio.modminds.item.AlaIzquierdaDeNaveItem;
import net.owleafstudio.modminds.item.AlaIzquierdaFrontalDeNaveItem;
import net.owleafstudio.modminds.item.AlmaDeGolemItem;
import net.owleafstudio.modminds.item.AnvilDoradoItem;
import net.owleafstudio.modminds.item.AnvilPlateadoItem;
import net.owleafstudio.modminds.item.ArepaDeCarneItem;
import net.owleafstudio.modminds.item.ArepaItem;
import net.owleafstudio.modminds.item.ArmaCortaEspacialItem;
import net.owleafstudio.modminds.item.ArmaduraDimensionalItem;
import net.owleafstudio.modminds.item.ArmaduraInfernalItem;
import net.owleafstudio.modminds.item.ArmadurasNaoneimItem;
import net.owleafstudio.modminds.item.AtomizadorDeAstrosItem;
import net.owleafstudio.modminds.item.BalaDeDilitioItem;
import net.owleafstudio.modminds.item.BarraDeChocolate2Item;
import net.owleafstudio.modminds.item.BarraDeChocolateItem;
import net.owleafstudio.modminds.item.BloqueadorDeBombasItem;
import net.owleafstudio.modminds.item.BloqueadorItem;
import net.owleafstudio.modminds.item.BolaDeHydraItem;
import net.owleafstudio.modminds.item.BuhoTotemItem;
import net.owleafstudio.modminds.item.CabinaDeNaveItem;
import net.owleafstudio.modminds.item.CabinaFrontalDeLaNaveItem;
import net.owleafstudio.modminds.item.CambiarColorGloboItem;
import net.owleafstudio.modminds.item.CaramelitoDeFrambuesaItem;
import net.owleafstudio.modminds.item.CaramelitoDeMoraAzulItem;
import net.owleafstudio.modminds.item.CaramelitoDeMoraItem;
import net.owleafstudio.modminds.item.CaramelitoDeNaranjaItem;
import net.owleafstudio.modminds.item.CaramelitoDePinaItem;
import net.owleafstudio.modminds.item.CerbatanaDeGoblinItem;
import net.owleafstudio.modminds.item.CervezaItem;
import net.owleafstudio.modminds.item.ChurroItem;
import net.owleafstudio.modminds.item.CorazonDoradoItem;
import net.owleafstudio.modminds.item.CorazonItem;
import net.owleafstudio.modminds.item.CucharaDeResurreccionItem;
import net.owleafstudio.modminds.item.CucharaNormalItem;
import net.owleafstudio.modminds.item.CuernoDeInfernalBullItem;
import net.owleafstudio.modminds.item.DardosTikisItem;
import net.owleafstudio.modminds.item.DeditaAzulItem;
import net.owleafstudio.modminds.item.DeditaCelesteItem;
import net.owleafstudio.modminds.item.DeditaNaranjaItem;
import net.owleafstudio.modminds.item.EmpanadaItem;
import net.owleafstudio.modminds.item.EnderBagItem;
import net.owleafstudio.modminds.item.EscudoTikiItem;
import net.owleafstudio.modminds.item.EspadaDeCongelacionItem;
import net.owleafstudio.modminds.item.EspadaFantasMalItem;
import net.owleafstudio.modminds.item.EspadaInanimadaItem;
import net.owleafstudio.modminds.item.EspadaInfernalCurativaItem;
import net.owleafstudio.modminds.item.EspadaTikiItem;
import net.owleafstudio.modminds.item.ForzitaItem;
import net.owleafstudio.modminds.item.FragmentoDeCoronaDeWildfireItem;
import net.owleafstudio.modminds.item.FragmentoDeHuesoItem;
import net.owleafstudio.modminds.item.FragmentosDeGemaDeAlmasItem;
import net.owleafstudio.modminds.item.GemaDeAlmasItem;
import net.owleafstudio.modminds.item.GemaDeDilitioItem;
import net.owleafstudio.modminds.item.GemaDeNereidaItem;
import net.owleafstudio.modminds.item.HamburguesaItem;
import net.owleafstudio.modminds.item.HeladoDeVainillaItem;
import net.owleafstudio.modminds.item.HuesoDeLaGrimReaperItem;
import net.owleafstudio.modminds.item.ICaramelitoDeFresaItem;
import net.owleafstudio.modminds.item.JeringaDelLimboItem;
import net.owleafstudio.modminds.item.JeringaDelLimboLlenaItem;
import net.owleafstudio.modminds.item.JeringaEspacialItem;
import net.owleafstudio.modminds.item.JeringaEspacialLlenaItem;
import net.owleafstudio.modminds.item.JeringaNereidaItem;
import net.owleafstudio.modminds.item.JeringaNereidaLlenaItem;
import net.owleafstudio.modminds.item.JeringaNetherItem;
import net.owleafstudio.modminds.item.JeringaNetherLlenaItem;
import net.owleafstudio.modminds.item.JeringaOverworldItem;
import net.owleafstudio.modminds.item.JeringaOverworldLLenaItem;
import net.owleafstudio.modminds.item.LaminaReforzadaDeAceroNegroAureoItem;
import net.owleafstudio.modminds.item.LimboItem;
import net.owleafstudio.modminds.item.LingoteDeAceroNegroAureoItem;
import net.owleafstudio.modminds.item.LingoteDeHierroReforzadoItem;
import net.owleafstudio.modminds.item.LlaveDeAdministradorItem;
import net.owleafstudio.modminds.item.LlaveTikiAzulItem;
import net.owleafstudio.modminds.item.LlaveTikiRojaItem;
import net.owleafstudio.modminds.item.LlaveTikiRosaItem;
import net.owleafstudio.modminds.item.LlaveTikiVerdeItem;
import net.owleafstudio.modminds.item.MAndMAzukItem;
import net.owleafstudio.modminds.item.MAndMItem;
import net.owleafstudio.modminds.item.MAndMMarronItem;
import net.owleafstudio.modminds.item.MAndMNaranjaItem;
import net.owleafstudio.modminds.item.MAndMVerdeItem;
import net.owleafstudio.modminds.item.MontonDeAlasDeMurcielagoWardenItem;
import net.owleafstudio.modminds.item.MotorDeDilitioItem;
import net.owleafstudio.modminds.item.MounstrosityDimensionItem;
import net.owleafstudio.modminds.item.NutritotemItem;
import net.owleafstudio.modminds.item.OjoDeIntrusoItem;
import net.owleafstudio.modminds.item.OjoDeRanaItem;
import net.owleafstudio.modminds.item.OrbeAmarilloItem;
import net.owleafstudio.modminds.item.OrbeAzulItem;
import net.owleafstudio.modminds.item.OrbeCyanItem;
import net.owleafstudio.modminds.item.OrbeMoradoItem;
import net.owleafstudio.modminds.item.OrbeNaranjaItem;
import net.owleafstudio.modminds.item.OrbeRojoItem;
import net.owleafstudio.modminds.item.OrbeRosaItem;
import net.owleafstudio.modminds.item.OrbeVerdeItem;
import net.owleafstudio.modminds.item.OsitoAmarilloItem;
import net.owleafstudio.modminds.item.OsitoAzulItem;
import net.owleafstudio.modminds.item.OsitoMoradoItem;
import net.owleafstudio.modminds.item.OsitoNaranjaItem;
import net.owleafstudio.modminds.item.OsitoRojoItem;
import net.owleafstudio.modminds.item.OsitoRosaItem;
import net.owleafstudio.modminds.item.OsitoVerdeItem;
import net.owleafstudio.modminds.item.PaletaDeChocolateItem;
import net.owleafstudio.modminds.item.PaltaItem;
import net.owleafstudio.modminds.item.PanDeMuertoItem;
import net.owleafstudio.modminds.item.PanItem;
import net.owleafstudio.modminds.item.PapasFritasItem;
import net.owleafstudio.modminds.item.PataDeSapoItem;
import net.owleafstudio.modminds.item.PepitaDeAceroNegroAureroItem;
import net.owleafstudio.modminds.item.PepitaDeDilitioItem;
import net.owleafstudio.modminds.item.PepitaDeGemaDeNereidaItem;
import net.owleafstudio.modminds.item.PerforadorDelVacioItem;
import net.owleafstudio.modminds.item.PildoraCapsulaItem;
import net.owleafstudio.modminds.item.PildoraDelExperimento110Item;
import net.owleafstudio.modminds.item.PlacaDeWildfireItem;
import net.owleafstudio.modminds.item.PlantillaDeMejoraDeDilitioItem;
import net.owleafstudio.modminds.item.PropulsorDeDilitioDerechoItem;
import net.owleafstudio.modminds.item.PropulsorDeDilitioIzquierdaItem;
import net.owleafstudio.modminds.item.PuertaBorgChipAmarillaItem;
import net.owleafstudio.modminds.item.PuertaBorgChipAzulItem;
import net.owleafstudio.modminds.item.PuertaBorgChipItem;
import net.owleafstudio.modminds.item.PuertaBorgChipMoradaItem;
import net.owleafstudio.modminds.item.PuertaBorgChipRojaItem;
import net.owleafstudio.modminds.item.PuertaBorgChipRosaItem;
import net.owleafstudio.modminds.item.RabanaguaItem;
import net.owleafstudio.modminds.item.RebanadaDePanItem;
import net.owleafstudio.modminds.item.RifleDePulsoBorg2Item;
import net.owleafstudio.modminds.item.SangreDeGorgonaItem;
import net.owleafstudio.modminds.item.SemillaDePaltaItem;
import net.owleafstudio.modminds.item.ShulkerBundleAmarilloItem;
import net.owleafstudio.modminds.item.ShulkerBundleAzulItem;
import net.owleafstudio.modminds.item.ShulkerBundleBlancoItem;
import net.owleafstudio.modminds.item.ShulkerBundleCelesteItem;
import net.owleafstudio.modminds.item.ShulkerBundleCianItem;
import net.owleafstudio.modminds.item.ShulkerBundleGrisItem;
import net.owleafstudio.modminds.item.ShulkerBundleGrisOscuroItem;
import net.owleafstudio.modminds.item.ShulkerBundleItem;
import net.owleafstudio.modminds.item.ShulkerBundleLimaItem;
import net.owleafstudio.modminds.item.ShulkerBundleMagentaItem;
import net.owleafstudio.modminds.item.ShulkerBundleMarronItem;
import net.owleafstudio.modminds.item.ShulkerBundleMoradoItem;
import net.owleafstudio.modminds.item.ShulkerBundleNaranjaItem;
import net.owleafstudio.modminds.item.ShulkerBundleNegraItem;
import net.owleafstudio.modminds.item.ShulkerBundleRojaItem;
import net.owleafstudio.modminds.item.ShulkerBundleRosaItem;
import net.owleafstudio.modminds.item.ShulkerBundleVerdeOscuroItem;
import net.owleafstudio.modminds.item.SilbatoDeTaxi23Item;
import net.owleafstudio.modminds.item.SilbatoDeTaxiDe4AsientosItem;
import net.owleafstudio.modminds.item.TenedorDeResurreccionItem;
import net.owleafstudio.modminds.item.TenedorNormalItem;
import net.owleafstudio.modminds.item.TotemEonMariposaItem;
import net.owleafstudio.modminds.item.TotemRaroItem;
import net.owleafstudio.modminds.item.TotemRevilItem;
import net.owleafstudio.modminds.item.TotemeonItem;
import net.owleafstudio.modminds.item.VaraDeDilitioItem;

@Mod.EventBusSubscriber(
   bus = Mod.EventBusSubscriber.Bus.MOD,
   value = {Dist.CLIENT}
)
public class ModmindsModItems {
   public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, "modminds");
   public static final RegistryObject<Item> BLOQUEADOR = REGISTRY.register("bloqueador", () -> new BloqueadorItem());
   public static final RegistryObject<Item> BUHO_TOTEM = REGISTRY.register("buho_totem", () -> new BuhoTotemItem());
   public static final RegistryObject<Item> NUTRITOTEM = REGISTRY.register("nutritotem", () -> new NutritotemItem());
   public static final RegistryObject<Item> TOTEM_ESPACIAL = REGISTRY.register("totem_espacial", () -> new TotemRaroItem());
   public static final RegistryObject<Item> TOTEM_REVIL = REGISTRY.register("totem_revil", () -> new TotemRevilItem());
   public static final RegistryObject<Item> TOTEM_EON_MARIPOSA = REGISTRY.register("totem_eon_mariposa", () -> new TotemEonMariposaItem());
   public static final RegistryObject<Item> TOTEMEON = REGISTRY.register("totemeon", () -> new TotemeonItem());
   public static final RegistryObject<Item> CHURRO = REGISTRY.register("churro", () -> new ChurroItem());
   public static final RegistryObject<Item> CORAZON = REGISTRY.register("corazon", () -> new CorazonItem());
   public static final RegistryObject<Item> PILDORA_DEL_EXPERIMENTO_110 = REGISTRY.register(
      "pildora_del_experimento_110", () -> new PildoraDelExperimento110Item()
   );
   public static final RegistryObject<Item> SHULKER_BUNDLE = REGISTRY.register("shulker_bundle", () -> new ShulkerBundleItem());
   public static final RegistryObject<Item> SHULKER_BUNDLE_AMARILLO = REGISTRY.register("shulker_bundle_amarillo", () -> new ShulkerBundleAmarilloItem());
   public static final RegistryObject<Item> SHULKER_BUNDLE_AZUL = REGISTRY.register("shulker_bundle_azul", () -> new ShulkerBundleAzulItem());
   public static final RegistryObject<Item> SHULKER_BUNDLE_BLANCO = REGISTRY.register("shulker_bundle_blanco", () -> new ShulkerBundleBlancoItem());
   public static final RegistryObject<Item> SHULKER_BUNDLE_CELESTE = REGISTRY.register("shulker_bundle_celeste", () -> new ShulkerBundleCelesteItem());
   public static final RegistryObject<Item> SHULKER_BUNDLE_CIAN = REGISTRY.register("shulker_bundle_cian", () -> new ShulkerBundleCianItem());
   public static final RegistryObject<Item> SHULKER_BUNDLE_GRIS = REGISTRY.register("shulker_bundle_gris", () -> new ShulkerBundleGrisItem());
   public static final RegistryObject<Item> SHULKER_BUNDLE_GRIS_OSCURO = REGISTRY.register(
      "shulker_bundle_gris_oscuro", () -> new ShulkerBundleGrisOscuroItem()
   );
   public static final RegistryObject<Item> SHULKER_BUNDLE_LIMA = REGISTRY.register("shulker_bundle_lima", () -> new ShulkerBundleLimaItem());
   public static final RegistryObject<Item> SHULKER_BUNDLE_MAGENTA = REGISTRY.register("shulker_bundle_magenta", () -> new ShulkerBundleMagentaItem());
   public static final RegistryObject<Item> SHULKER_BUNDLE_MARRON = REGISTRY.register("shulker_bundle_marron", () -> new ShulkerBundleMarronItem());
   public static final RegistryObject<Item> SHULKER_BUNDLE_MORADO = REGISTRY.register("shulker_bundle_morado", () -> new ShulkerBundleMoradoItem());
   public static final RegistryObject<Item> SHULKER_BUNDLE_NARANJA = REGISTRY.register("shulker_bundle_naranja", () -> new ShulkerBundleNaranjaItem());
   public static final RegistryObject<Item> SHULKER_BUNDLE_NEGRA = REGISTRY.register("shulker_bundle_negra", () -> new ShulkerBundleNegraItem());
   public static final RegistryObject<Item> SHULKER_BUNDLE_ROJA = REGISTRY.register("shulker_bundle_roja", () -> new ShulkerBundleRojaItem());
   public static final RegistryObject<Item> SHULKER_BUNDLE_ROSA = REGISTRY.register("shulker_bundle_rosa", () -> new ShulkerBundleRosaItem());
   public static final RegistryObject<Item> SHULKER_BUNDLE_VERDE_OSCURO = REGISTRY.register(
      "shulker_bundle_verde_oscuro", () -> new ShulkerBundleVerdeOscuroItem()
   );
   public static final RegistryObject<Item> PILDORA_AZUL_VACIA = REGISTRY.register("pildora_azul_vacia", () -> new PildoraCapsulaItem());
   public static final RegistryObject<Item> TABLONES_ANTIGUOS = block(ModmindsModBlocks.TABLONES_ANTIGUOS);
   public static final RegistryObject<Item> LOSA_DE_TABLONES_ANTIGUOS = block(ModmindsModBlocks.LOSA_DE_TABLONES_ANTIGUOS);
   public static final RegistryObject<Item> ESCALERAS_DE_TABLONES_ANTIGUOS = block(ModmindsModBlocks.ESCALERAS_DE_TABLONES_ANTIGUOS);
   public static final RegistryObject<Item> VALLA_DE_TABLONES_ANTIGUOS = block(ModmindsModBlocks.VALLA_DE_TABLONES_ANTIGUOS);
   public static final RegistryObject<Item> PUERTA_DE_VALLA_DE_TABLONES_ANTIGUOS = block(ModmindsModBlocks.PUERTA_DE_VALLA_DE_TABLONES_ANTIGUOS);
   public static final RegistryObject<Item> TABLONES_AMARILLOS_ANTIGUOS = block(ModmindsModBlocks.TABLONES_AMARILLOS_ANTIGUOS);
   public static final RegistryObject<Item> LOSA_DE_TABLONES_AMARILLOS_ANTIGUOS = block(ModmindsModBlocks.LOSA_DE_TABLONES_AMARILLOS_ANTIGUOS);
   public static final RegistryObject<Item> ESCALERAS_DE_TABLONES_AMARILLOS_ANTIGUOS = block(ModmindsModBlocks.ESCALERAS_DE_TABLONES_AMARILLOS_ANTIGUOS);
   public static final RegistryObject<Item> VALLA_DE_TABLONES_AMARILLOS_ANTIGUOS = block(ModmindsModBlocks.VALLA_DE_TABLONES_AMARILLOS_ANTIGUOS);
   public static final RegistryObject<Item> PUERTA_DE_VALLA_TABLONES_AMARILLOS_ANTIGUOS = block(
      ModmindsModBlocks.PUERTA_DE_VALLA_TABLONES_AMARILLOS_ANTIGUOS
   );
   public static final RegistryObject<Item> TABLONES_VERDES_ANTIGUOS = block(ModmindsModBlocks.TABLONES_VERDES_ANTIGUOS);
   public static final RegistryObject<Item> LOSA_DE_TABLONES_VERDES_ANTIGUOS = block(ModmindsModBlocks.LOSA_DE_TABLONES_VERDES_ANTIGUOS);
   public static final RegistryObject<Item> ESCALERAS_DE_TABLONES_VERDES_ANTIGUOS = block(ModmindsModBlocks.ESCALERAS_DE_TABLONES_VERDES_ANTIGUOS);
   public static final RegistryObject<Item> VALLA_DE_TABLONES_VERDES_ANTIGUOS = block(ModmindsModBlocks.VALLA_DE_TABLONES_VERDES_ANTIGUOS);
   public static final RegistryObject<Item> PUERTA_DE_VALLA_TABLONES_VERDES_ANTIGUOS = block(ModmindsModBlocks.PUERTA_DE_VALLA_TABLONES_VERDES_ANTIGUOS);
   public static final RegistryObject<Item> TABLONES_ROJOS_ANTIGUOS = block(ModmindsModBlocks.TABLONES_ROJOS_ANTIGUOS);
   public static final RegistryObject<Item> LOSA_DE_TABLONES_ROJOS_ANTIGUOS = block(ModmindsModBlocks.LOSA_DE_TABLONES_ROJOS_ANTIGUOS);
   public static final RegistryObject<Item> ESCALERAS_DE_TABLONES_ROJOS_ANTIGUOS = block(ModmindsModBlocks.ESCALERAS_DE_TABLONES_ROJOS_ANTIGUOS);
   public static final RegistryObject<Item> VALLA_DE_TABLONES_ROJOS_ANTIGUOS = block(ModmindsModBlocks.VALLA_DE_TABLONES_ROJOS_ANTIGUOS);
   public static final RegistryObject<Item> PUERTA_DE_VALLA_TABLONES_ROJOS_ANTIGUOS = block(ModmindsModBlocks.PUERTA_DE_VALLA_TABLONES_ROJOS_ANTIGUOS);
   public static final RegistryObject<Item> LADRILLOS_NEGROS_AGRIETADOS = block(ModmindsModBlocks.LADRILLOS_NEGROS_AGRIETADOS);
   public static final RegistryObject<Item> LOSA_DE_LADRILLOS_NEGROS_AGRIETADOS = block(ModmindsModBlocks.LOSA_DE_LADRILLOS_NEGROS_AGRIETADOS);
   public static final RegistryObject<Item> ESCALERAS_DE_LADRILLOS_NEGROS_AGRIETADOS = block(ModmindsModBlocks.ESCALERAS_DE_LADRILLOS_NEGROS_AGRIETADOS);
   public static final RegistryObject<Item> LADRILLOS_DE_MAGMA = block(ModmindsModBlocks.LADRILLOS_DE_MAGMA);
   public static final RegistryObject<Item> LOSA_DE_LADRILLOS_DE_MAGMA = block(ModmindsModBlocks.LOSA_DE_LADRILLOS_DE_MAGMA);
   public static final RegistryObject<Item> ESCALERAS_DE_LADRILLOS_DE_MAGMA = block(ModmindsModBlocks.ESCALERAS_DE_LADRILLOS_DE_MAGMA);
   public static final RegistryObject<Item> TABLONES_MAGENTAS_ANTIGUOS = block(ModmindsModBlocks.TABLONES_MAGENTAS_ANTIGUOS);
   public static final RegistryObject<Item> LOSA_DE_TABLONES_MAGENTAS_ANTIGUOS = block(ModmindsModBlocks.LOSA_DE_TABLONES_MAGENTAS_ANTIGUOS);
   public static final RegistryObject<Item> ESCALERAS_DE_TABLONES_MAGENTAS_ANTIGUOS = block(ModmindsModBlocks.ESCALERAS_DE_TABLONES_MAGENTAS_ANTIGUOS);
   public static final RegistryObject<Item> VALLA_DE_TABLONES_MAGENTAS_ANTIGUOS = block(ModmindsModBlocks.VALLA_DE_TABLONES_MAGENTAS_ANTIGUOS);
   public static final RegistryObject<Item> PUERTA_DE_VALLA_TABLONES_MAGENTAS_ANTIGUOS = block(
      ModmindsModBlocks.PUERTA_DE_VALLA_TABLONES_MAGENTAS_ANTIGUOS
   );
   public static final RegistryObject<Item> TABLONES_MARRONES_ANTIGUOS = block(ModmindsModBlocks.TABLONES_MARRONES_ANTIGUOS);
   public static final RegistryObject<Item> LOSA_DE_TABLONES_MARRONES_ANTIGUOS = block(ModmindsModBlocks.LOSA_DE_TABLONES_MARRONES_ANTIGUOS);
   public static final RegistryObject<Item> ESCALERAS_DE_TABLONES_MARRONES_ANTIGUOS = block(ModmindsModBlocks.ESCALERAS_DE_TABLONES_MARRONES_ANTIGUOS);
   public static final RegistryObject<Item> VALLA_DE_TABLONES_MARRONES_ANTIGUOS = block(ModmindsModBlocks.VALLA_DE_TABLONES_MARRONES_ANTIGUOS);
   public static final RegistryObject<Item> PUERTA_DE_VALLA_TABLONES_MARRONES_ANTIGUOS = block(
      ModmindsModBlocks.PUERTA_DE_VALLA_TABLONES_MARRONES_ANTIGUOS
   );
   public static final RegistryObject<Item> YUNQUE_PORTATIL = REGISTRY.register("yunque_portatil", () -> new AnvilDoradoItem());
   public static final RegistryObject<Item> ENDER_BAG = REGISTRY.register("ender_bag", () -> new EnderBagItem());
   public static final RegistryObject<Item> CASINO_SPAWN_EGG = REGISTRY.register(
      "casino_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.CASINO, -13421773, -16777216, new Item.Properties())
   );
   public static final RegistryObject<Item> DEDITA_AZUL = REGISTRY.register("dedita_azul", () -> new DeditaAzulItem());
   public static final RegistryObject<Item> DEDITA_NARANJA = REGISTRY.register("dedita_naranja", () -> new DeditaNaranjaItem());
   public static final RegistryObject<Item> DEDITA_CELESTE = REGISTRY.register("dedita_celeste", () -> new DeditaCelesteItem());
   public static final RegistryObject<Item> BARS_BLOCK = block(ModmindsModBlocks.BARS_BLOCK);
   public static final RegistryObject<Item> LARGE_BARS_BLOCK = block(ModmindsModBlocks.LARGE_BARS_BLOCK);
   public static final RegistryObject<Item> ESPADA_INFERNAL_CURATIVA = REGISTRY.register(
      "espada_infernal_curativa", () -> new EspadaInfernalCurativaItem()
   );
   public static final RegistryObject<Item> CUCHARA_DE_RESURRECCION = REGISTRY.register("cuchara_de_resurreccion", () -> new CucharaDeResurreccionItem());
   public static final RegistryObject<Item> TENEDOR_DE_RESURRECCION = REGISTRY.register("tenedor_de_resurreccion", () -> new TenedorDeResurreccionItem());
   public static final RegistryObject<Item> TORTUGA_NETHERITA_SPAWN_EGG = REGISTRY.register(
      "tortuga_netherita_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.TORTUGA_NETHERITA, -14211289, -1279440, new Item.Properties())
   );
   public static final RegistryObject<Item> LADRILLOS_DE_FORTALEZA = block(ModmindsModBlocks.LADRILLOS_DE_FORTALEZA);
   public static final RegistryObject<Item> LOSA_DE_LADRILLOS_DE_FORTALEZA = block(ModmindsModBlocks.LOSA_DE_LADRILLOS_DE_FORTALEZA);
   public static final RegistryObject<Item> ESCALERAS_DE_LADRILLOS_DE_FORTALEZA = block(ModmindsModBlocks.ESCALERAS_DE_LADRILLOS_DE_FORTALEZA);
   public static final RegistryObject<Item> MURO_DE_LADRILLOS_DE_FORTALEZA = block(ModmindsModBlocks.MURO_DE_LADRILLOS_DE_FORTALEZA);
   public static final RegistryObject<Item> MURO_DE_LADRILLOS_DE_FORTALEZA_CINCELADO = block(ModmindsModBlocks.MURO_DE_LADRILLOS_DE_FORTALEZA_CINCELADO);
   public static final RegistryObject<Item> LADRILLO_DE_FORTALEZA_CINCELADO = block(ModmindsModBlocks.LADRILLO_DE_FORTALEZA_CINCELADO);
   public static final RegistryObject<Item> LOSA_DE_LADRILLO_DE_FORTALEZA_CINCELADO = block(ModmindsModBlocks.LOSA_DE_LADRILLO_DE_FORTALEZA_CINCELADO);
   public static final RegistryObject<Item> ESCALERAS_DE_LADRILLO_DE_FORTALEZA_CINCELADO = block(
      ModmindsModBlocks.ESCALERAS_DE_LADRILLO_DE_FORTALEZA_CINCELADO
   );
   public static final RegistryObject<Item> ANVIL_PLATEADO = REGISTRY.register("anvil_plateado", () -> new AnvilPlateadoItem());
   public static final RegistryObject<Item> CUCHARA_DE_MADERA = REGISTRY.register("cuchara_de_madera", () -> new CucharaNormalItem());
   public static final RegistryObject<Item> TENEDOR_NORMAL = REGISTRY.register("tenedor_normal", () -> new TenedorNormalItem());
   public static final RegistryObject<Item> PEPITA_DE_DILITIO = REGISTRY.register("pepita_de_dilitio", () -> new PepitaDeDilitioItem());
   public static final RegistryObject<Item> GEMA_DE_DILITIO = REGISTRY.register("gema_de_dilitio", () -> new GemaDeDilitioItem());
   public static final RegistryObject<Item> SEMILLA_DE_PALTA = REGISTRY.register("semilla_de_palta", () -> new SemillaDePaltaItem());
   public static final RegistryObject<Item> PALTA = REGISTRY.register("palta", () -> new PaltaItem());
   public static final RegistryObject<Item> CRISTAL_DE_TEMPLO_NEGRO = block(ModmindsModBlocks.CRISTAL_DE_TEMPLO_NEGRO);
   public static final RegistryObject<Item> LOSA_DE_CRISTAL_DE_TEMPLO_NEGRO = block(ModmindsModBlocks.LOSA_DE_CRISTAL_DE_TEMPLO_NEGRO);
   public static final RegistryObject<Item> ESCALERA_DE_CRISTAL_DE_TEMPLO_NEGRO = block(ModmindsModBlocks.ESCALERA_DE_CRISTAL_DE_TEMPLO_NEGRO);
   public static final RegistryObject<Item> CRISTAL_DE_DILITIO = block(ModmindsModBlocks.CRISTAL_DE_DILITIO);
   public static final RegistryObject<Item> CRISTAL_DE_DILITIO_ROJO = block(ModmindsModBlocks.CRISTAL_DE_DILITIO_ROJO);
   public static final RegistryObject<Item> LOSA_DE_CRISTAL_DE_DILITIO_ROJO = block(ModmindsModBlocks.LOSA_DE_CRISTAL_DE_DILITIO_ROJO);
   public static final RegistryObject<Item> ESCALERAS_DE_CRISTAL_DE_DILITIO_ROJO = block(ModmindsModBlocks.ESCALERAS_DE_CRISTAL_DE_DILITIO_ROJO);
   public static final RegistryObject<Item> CUERNO_DE_INFERNAL_BULL = REGISTRY.register("cuerno_de_infernal_bull", () -> new CuernoDeInfernalBullItem());
   public static final RegistryObject<Item> SAPITO_SPAWN_EGG = REGISTRY.register(
      "sapito_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.SAPITO, -16751104, -16724992, new Item.Properties())
   );
   public static final RegistryObject<Item> PEPITA_DE_ACERO_NEGRO_AURERO = REGISTRY.register(
      "pepita_de_acero_negro_aurero", () -> new PepitaDeAceroNegroAureroItem()
   );
   public static final RegistryObject<Item> LAMINA_REFORZADA_DE_ACERO_NEGRO_AUREO = REGISTRY.register(
      "lamina_reforzada_de_acero_negro_aureo", () -> new LaminaReforzadaDeAceroNegroAureoItem()
   );
   public static final RegistryObject<Item> LINGOTE_DE_ACERO_NEGRO_AUREO = REGISTRY.register(
      "lingote_de_acero_negro_aureo", () -> new LingoteDeAceroNegroAureoItem()
   );
   public static final RegistryObject<Item> OJO_DE_SAPO = REGISTRY.register("ojo_de_sapo", () -> new OjoDeRanaItem());
   public static final RegistryObject<Item> CRISTAL_REFORZADO = block(ModmindsModBlocks.CRISTAL_REFORZADO);
   public static final RegistryObject<Item> FRAGMENTO_DE_CORONA_DE_WILDFIRE = REGISTRY.register(
      "fragmento_de_corona_de_wildfire", () -> new FragmentoDeCoronaDeWildfireItem()
   );
   public static final RegistryObject<Item> PISO_ESCENARIO = block(ModmindsModBlocks.PISO_ESCENARIO);
   public static final RegistryObject<Item> PISO_ESCENARIO_DEGRADADO = block(ModmindsModBlocks.PISO_ESCENARIO_DEGRADADO);
   public static final RegistryObject<Item> ARMADURA_INFERNAL_HELMET = REGISTRY.register(
      "armadura_infernal_helmet", () -> new ArmaduraInfernalItem.Helmet()
   );
   public static final RegistryObject<Item> ARMADURA_INFERNAL_CHESTPLATE = REGISTRY.register(
      "armadura_infernal_chestplate", () -> new ArmaduraInfernalItem.Chestplate()
   );
   public static final RegistryObject<Item> ARMADURA_INFERNAL_LEGGINGS = REGISTRY.register(
      "armadura_infernal_leggings", () -> new ArmaduraInfernalItem.Leggings()
   );
   public static final RegistryObject<Item> ARMADURA_INFERNAL_BOOTS = REGISTRY.register("armadura_infernal_boots", () -> new ArmaduraInfernalItem.Boots());
   public static final RegistryObject<Item> MOTOR_DE_DILITIO = REGISTRY.register("motor_de_dilitio", () -> new MotorDeDilitioItem());
   public static final RegistryObject<Item> PLACA_DE_WILDFIRE = REGISTRY.register("placa_de_wildfire", () -> new PlacaDeWildfireItem());
   public static final RegistryObject<Item> GEMA_DE_NEREIDA = REGISTRY.register("gema_de_nereida", () -> new GemaDeNereidaItem());
   public static final RegistryObject<Item> PEPITA_DE_GEMA_DE_NEREIDA = REGISTRY.register(
      "pepita_de_gema_de_nereida", () -> new PepitaDeGemaDeNereidaItem()
   );
   public static final RegistryObject<Item> CRISTAL_DE_DILITIO_MORADO = block(ModmindsModBlocks.CRISTAL_DE_DILITIO_MORADO);
   public static final RegistryObject<Item> LOSA_DE_CRISTAL_DE_DILITIO_MORADO = block(ModmindsModBlocks.LOSA_DE_CRISTAL_DE_DILITIO_MORADO);
   public static final RegistryObject<Item> ESCALERAS_DE_CRISTAL_DE_DILITIO_MORADO = block(ModmindsModBlocks.ESCALERAS_DE_CRISTAL_DE_DILITIO_MORADO);
   public static final RegistryObject<Item> CRISTAL_DE_DILITIO_AZUL = block(ModmindsModBlocks.CRISTAL_DE_DILITIO_AZUL);
   public static final RegistryObject<Item> LOSA_DE_CRISTAL_DE_DILITIO_AZUL = block(ModmindsModBlocks.LOSA_DE_CRISTAL_DE_DILITIO_AZUL);
   public static final RegistryObject<Item> ESCALERAS_DE_CRISTAL_DE_DILITIO_AZUL = block(ModmindsModBlocks.ESCALERAS_DE_CRISTAL_DE_DILITIO_AZUL);
   public static final RegistryObject<Item> GORGONA_SPAWN_EGG = REGISTRY.register(
      "gorgona_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.GORGONA, -7436695, -14072776, new Item.Properties())
   );
   public static final RegistryObject<Item> ESTATUA_ECHADA = block(ModmindsModBlocks.ESTATUA_ECHADA);
   public static final RegistryObject<Item> ESTATUA_SENTADO = block(ModmindsModBlocks.ESTATUA_SENTADO);
   public static final RegistryObject<Item> ESTATUA_CRUCIFICADO = block(ModmindsModBlocks.ESTATUA_CRUCIFICADO);
   public static final RegistryObject<Item> CRISTAL_DE_DILITIO_CIAN = block(ModmindsModBlocks.CRISTAL_DE_DILITIO_CIAN);
   public static final RegistryObject<Item> LOSA_DE_CRISTAL_DE_DILITIO_CIAN = block(ModmindsModBlocks.LOSA_DE_CRISTAL_DE_DILITIO_CIAN);
   public static final RegistryObject<Item> ESCALERAS_DE_CRISTAL_DE_DILITIO_CIAN = block(ModmindsModBlocks.ESCALERAS_DE_CRISTAL_DE_DILITIO_CIAN);
   public static final RegistryObject<Item> CRISTAL_DE_DILITIO_ROSADO = block(ModmindsModBlocks.CRISTAL_DE_DILITIO_ROSADO);
   public static final RegistryObject<Item> LOSA_DE_CRISTAL_DE_DILITIO_ROSADO = block(ModmindsModBlocks.LOSA_DE_CRISTAL_DE_DILITIO_ROSADO);
   public static final RegistryObject<Item> ESCALERA_DE_CRISTAL_DE_DILITIO_ROSADO = block(ModmindsModBlocks.ESCALERA_DE_CRISTAL_DE_DILITIO_ROSADO);
   public static final RegistryObject<Item> CRISTAL_DE_DILITIO_ANARANJADO = block(ModmindsModBlocks.CRISTAL_DE_DILITIO_ANARANJADO);
   public static final RegistryObject<Item> LOSA_DE_CRISTAL_DE_DILITIO_ANARANJADO = block(ModmindsModBlocks.LOSA_DE_CRISTAL_DE_DILITIO_ANARANJADO);
   public static final RegistryObject<Item> ESCALERAS_DE_CRISTAL_DE_DILITIO_ANARANJADO = block(
      ModmindsModBlocks.ESCALERAS_DE_CRISTAL_DE_DILITIO_ANARANJADO
   );
   public static final RegistryObject<Item> CRISTAL_DE_DILITIO_AMARILLO = block(ModmindsModBlocks.CRISTAL_DE_DILITIO_AMARILLO);
   public static final RegistryObject<Item> LOSA_DE_CRISTAL_DE_DILITIO_AMARILLO = block(ModmindsModBlocks.LOSA_DE_CRISTAL_DE_DILITIO_AMARILLO);
   public static final RegistryObject<Item> ESCALERAS_DE_CRISTAL_DE_DILITIO_AMARILLO = block(ModmindsModBlocks.ESCALERAS_DE_CRISTAL_DE_DILITIO_AMARILLO);
   public static final RegistryObject<Item> ARMADURAS_NAONEIM_HELMET = REGISTRY.register(
      "armaduras_naoneim_helmet", () -> new ArmadurasNaoneimItem.Helmet()
   );
   public static final RegistryObject<Item> ARMADURAS_NAONEIM_CHESTPLATE = REGISTRY.register(
      "armaduras_naoneim_chestplate", () -> new ArmadurasNaoneimItem.Chestplate()
   );
   public static final RegistryObject<Item> ARMADURAS_NAONEIM_LEGGINGS = REGISTRY.register(
      "armaduras_naoneim_leggings", () -> new ArmadurasNaoneimItem.Leggings()
   );
   public static final RegistryObject<Item> ARMADURAS_NAONEIM_BOOTS = REGISTRY.register("armaduras_naoneim_boots", () -> new ArmadurasNaoneimItem.Boots());
   public static final RegistryObject<Item> PLANTILLA_DE_MEJORA_DE_DILITIO = REGISTRY.register(
      "plantilla_de_mejora_de_dilitio", () -> new PlantillaDeMejoraDeDilitioItem()
   );
   public static final RegistryObject<Item> LINGOTE_DE_HIERRO_REFORZADO = REGISTRY.register(
      "lingote_de_hierro_reforzado", () -> new LingoteDeHierroReforzadoItem()
   );
   public static final RegistryObject<Item> MINERAL_DE_NEREIDA = block(ModmindsModBlocks.MINERAL_DE_NEREIDA);
   public static final RegistryObject<Item> PISO_BORG = block(ModmindsModBlocks.PISO_BORG);
   public static final RegistryObject<Item> LOSA_DE_PISO_BORG = block(ModmindsModBlocks.LOSA_DE_PISO_BORG);
   public static final RegistryObject<Item> ESCALERAS_DE_PISO_BORG = block(ModmindsModBlocks.ESCALERAS_DE_PISO_BORG);
   public static final RegistryObject<Item> PILAR_BORG = block(ModmindsModBlocks.PILAR_BORG);
   public static final RegistryObject<Item> LOSA_DE_PILAR_BORG = block(ModmindsModBlocks.LOSA_DE_PILAR_BORG);
   public static final RegistryObject<Item> ESCALERAS_DE_PILAR_BORG = block(ModmindsModBlocks.ESCALERAS_DE_PILAR_BORG);
   public static final RegistryObject<Item> PARED_BORG = block(ModmindsModBlocks.PARED_BORG);
   public static final RegistryObject<Item> LOSA_DE_PARED_BORG = block(ModmindsModBlocks.LOSA_DE_PARED_BORG);
   public static final RegistryObject<Item> ESCALERAS_DE_PARED_BORG = block(ModmindsModBlocks.ESCALERAS_DE_PARED_BORG);
   public static final RegistryObject<Item> LADRILLO_DE_PISO_BORG = block(ModmindsModBlocks.LADRILLO_DE_PISO_BORG);
   public static final RegistryObject<Item> LOSA_DE_LADRILLO_DE_PISO_BORG = block(ModmindsModBlocks.LOSA_DE_LADRILLO_DE_PISO_BORG);
   public static final RegistryObject<Item> ESCALERAS_DE_LADRILLO_DE_PISO_BORG = block(ModmindsModBlocks.ESCALERAS_DE_LADRILLO_DE_PISO_BORG);
   public static final RegistryObject<Item> LADRILLO_BORG = block(ModmindsModBlocks.LADRILLO_BORG);
   public static final RegistryObject<Item> LOSA_DE_LADRILLO_BORG = block(ModmindsModBlocks.LOSA_DE_LADRILLO_BORG);
   public static final RegistryObject<Item> ESCALERAS_DE_LADRILLO_BORG = block(ModmindsModBlocks.ESCALERAS_DE_LADRILLO_BORG);
   public static final RegistryObject<Item> BLOQUE_BORG_CINCELADO = block(ModmindsModBlocks.BLOQUE_BORG_CINCELADO);
   public static final RegistryObject<Item> LOSA_DE_BLOQUE_BORG_CINCELADO = block(ModmindsModBlocks.LOSA_DE_BLOQUE_BORG_CINCELADO);
   public static final RegistryObject<Item> ESCALERAS_DE_BLOQUE_BORG_CINCELADO = block(ModmindsModBlocks.ESCALERAS_DE_BLOQUE_BORG_CINCELADO);
   public static final RegistryObject<Item> BLOQUE_BORG = block(ModmindsModBlocks.BLOQUE_BORG);
   public static final RegistryObject<Item> LOSA_DE_BLOQUE_BORG = block(ModmindsModBlocks.LOSA_DE_BLOQUE_BORG);
   public static final RegistryObject<Item> ESCALERAS_DE_BLOQUE_BORG = block(ModmindsModBlocks.ESCALERAS_DE_BLOQUE_BORG);
   public static final RegistryObject<Item> BARRICADA_DE_SEGURIDAD = block(ModmindsModBlocks.BARRICADA_DE_SEGURIDAD);
   public static final RegistryObject<Item> CONO_DE_SEGURIDAD = block(ModmindsModBlocks.CONO_DE_SEGURIDAD);
   public static final RegistryObject<Item> CERVEZA = REGISTRY.register("cerveza", () -> new CervezaItem());
   public static final RegistryObject<Item> PAPAS_FRITAS = REGISTRY.register("papas_fritas", () -> new PapasFritasItem());
   public static final RegistryObject<Item> HAMBURGUESA = REGISTRY.register("hamburguesa", () -> new HamburguesaItem());
   public static final RegistryObject<Item> RABANAGUA = REGISTRY.register("rabanagua", () -> new RabanaguaItem());
   public static final RegistryObject<Item> FRAGMENTO_DE_HUESO = REGISTRY.register("fragmento_de_hueso", () -> new FragmentoDeHuesoItem());
   public static final RegistryObject<Item> BOMBA_NEGRA_SPAWN_EGG = REGISTRY.register(
      "bomba_negra_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.BOMBA_NEGRA, -13421773, -16777216, new Item.Properties())
   );
   public static final RegistryObject<Item> BOMBA_NEGRA_SIN_ALAS_SPAWN_EGG = REGISTRY.register(
      "bomba_negra_sin_alas_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.BOMBA_NEGRA_SIN_ALAS, -13421773, -16777216, new Item.Properties())
   );
   public static final RegistryObject<Item> INFERNAL_BULL_SPAWN_EGG = REGISTRY.register(
      "infernal_bull_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.INFERNAL_BULL, -83, -3766937, new Item.Properties())
   );
   public static final RegistryObject<Item> BLOQUES_BORG_CON_LUCES_VERDES = block(ModmindsModBlocks.BLOQUES_BORG_CON_LUCES_VERDES);
   public static final RegistryObject<Item> BLOQUES_BORG_CON_LUCES_ROJAS = block(ModmindsModBlocks.BLOQUES_BORG_CON_LUCES_ROJAS);
   public static final RegistryObject<Item> BLOQUE_AZUL_DE_RING = block(ModmindsModBlocks.BLOQUE_AZUL_DE_RING);
   public static final RegistryObject<Item> ALA_DE_MURCIELAGO_WARDEN = REGISTRY.register(
      "ala_de_murcielago_warden", () -> new AlaDeMurcielagoWardenItem()
   );
   public static final RegistryObject<Item> TABLONES_ROSAS_ANTIGUOS = block(ModmindsModBlocks.TABLONES_ROSAS_ANTIGUOS);
   public static final RegistryObject<Item> LOSA_DE_TABLONES_ROSAS_ANTIGUOS = block(ModmindsModBlocks.LOSA_DE_TABLONES_ROSAS_ANTIGUOS);
   public static final RegistryObject<Item> ESCALERAS_DE_TABLONES_ROSAS_ANTIGUOS = block(ModmindsModBlocks.ESCALERAS_DE_TABLONES_ROSAS_ANTIGUOS);
   public static final RegistryObject<Item> BLOQUE_NEGRO_DE_RING = block(ModmindsModBlocks.BLOQUE_NEGRO_DE_RING);
   public static final RegistryObject<Item> BLOQUE_ROJO_DE_RING = block(ModmindsModBlocks.BLOQUE_ROJO_DE_RING);
   public static final RegistryObject<Item> DEDITA_AMARILLA_OWLEAF = null;
   public static final RegistryObject<Item> DEDITA_ROJO_OWLEAF = null;
   public static final RegistryObject<Item> DEDITA_MORADA_OWLEAF = null;
   public static final RegistryObject<Item> DEDITA_ROSA_OWLEAF = null;
   public static final RegistryObject<Item> DEDITA_CELESTE_OWLEAF = null;
   public static final RegistryObject<Item> DEDITA_NARANJA_OWLEAF = null;
   public static final RegistryObject<Item> DEDITA_AZUL_OWLEAF = null;
   public static final RegistryObject<Item> DEDITA_LIMA_OWLEAF = null;
   public static final RegistryObject<Item> DEDITA_VERDE_OWLEAF = null;
   public static final RegistryObject<Item> MONTON_DE_ALAS_DE_MURCIELAGO_WARDEN = REGISTRY.register(
      "monton_de_alas_de_murcielago_warden", () -> new MontonDeAlasDeMurcielagoWardenItem()
   );
   public static final RegistryObject<Item> HENO_DE_TIKI = block(ModmindsModBlocks.HENO_DE_TIKI);
   public static final RegistryObject<Item> LOSA_DE_HENO_DE_TIKI = block(ModmindsModBlocks.LOSA_DE_HENO_DE_TIKI);
   public static final RegistryObject<Item> ESCALERAS_DE_HENO_DE_TIKI = block(ModmindsModBlocks.ESCALERAS_DE_HENO_DE_TIKI);
   public static final RegistryObject<Item> BLOQUE_DE_CORAL_ROSADO_LUMINOSO = block(ModmindsModBlocks.BLOQUE_DE_CORAL_ROSADO_LUMINOSO);
   public static final RegistryObject<Item> LOSA_DE_CORAL_ROSADO_LUMINOSO = block(ModmindsModBlocks.LOSA_DE_CORAL_ROSADO_LUMINOSO);
   public static final RegistryObject<Item> ESCALERA_DE_CORAL_ROSADO_LUMINOSO = block(ModmindsModBlocks.ESCALERA_DE_CORAL_ROSADO_LUMINOSO);
   public static final RegistryObject<Item> BLOQUE_DE_CORAL_PURPURA_LUMINOSO = block(ModmindsModBlocks.BLOQUE_DE_CORAL_PURPURA_LUMINOSO);
   public static final RegistryObject<Item> LOSA_DE_CORAL_PURPURA_LUMINOSO = block(ModmindsModBlocks.LOSA_DE_CORAL_PURPURA_LUMINOSO);
   public static final RegistryObject<Item> ESCALERA_DE_CORAL_PURPURA_LUMINOSO = block(ModmindsModBlocks.ESCALERA_DE_CORAL_PURPURA_LUMINOSO);
   public static final RegistryObject<Item> BLOQUE_DE_NEREIDA = block(ModmindsModBlocks.BLOQUE_DE_NEREIDA);
   public static final RegistryObject<Item> CINTA_DE_SEGURIDAD = block(ModmindsModBlocks.CINTA_DE_SEGURIDAD);
   public static final RegistryObject<Item> MURCIELAGO_WARDEN_SPAWN_EGG = REGISTRY.register(
      "murcielago_warden_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.MURCIELAGO_WARDEN, -16767187, -14834771, new Item.Properties())
   );
   public static final RegistryObject<Item> ESPADA_DE_CONGELACION = REGISTRY.register("espada_de_congelacion", () -> new EspadaDeCongelacionItem());
   public static final RegistryObject<Item> BALA_DE_DILITIO = REGISTRY.register("bala_de_dilitio", () -> new BalaDeDilitioItem());
   public static final RegistryObject<Item> TABLONES_VERDE_ANTIGUOS_LOSA_VERTICAL = block(ModmindsModBlocks.TABLONES_VERDE_ANTIGUOS_LOSA_VERTICAL);
   public static final RegistryObject<Item> TABLONES_ROSA_ANTIGUOS_LOSA_VERTICAL = block(ModmindsModBlocks.TABLONES_ROSA_ANTIGUOS_LOSA_VERTICAL);
   public static final RegistryObject<Item> TABLONES_AMARILLOS_ANTIGUOS_LOSA_VERTICAL = block(ModmindsModBlocks.TABLONES_AMARILLOS_ANTIGUOS_LOSA_VERTICAL);
   public static final RegistryObject<Item> TABLONES_ROJOS_ANTIGUOS_LOSA_VERTICAL = block(ModmindsModBlocks.TABLONES_ROJOS_ANTIGUOS_LOSA_VERTICAL);
   public static final RegistryObject<Item> TABLONES_MARRONES_ANTIGUOS_LOSA_VERTICAL = block(ModmindsModBlocks.TABLONES_MARRONES_ANTIGUOS_LOSA_VERTICAL);
   public static final RegistryObject<Item> TABLONES_MAGENTAS_ANTIGUOS_LOSA_VERTICAL = block(ModmindsModBlocks.TABLONES_MAGENTAS_ANTIGUOS_LOSA_VERTICAL);
   public static final RegistryObject<Item> HENO_DE_TIKI_LOSA_VERTICAL = block(ModmindsModBlocks.HENO_DE_TIKI_LOSA_VERTICAL);
   public static final RegistryObject<Item> CRISTAL_VERDE_ESPACIAL = block(ModmindsModBlocks.CRISTAL_VERDE_ESPACIAL);
   public static final RegistryObject<Item> BLOQUE_DE_ISLAS_DE_ESPACIO = block(ModmindsModBlocks.BLOQUE_DE_ISLAS_DE_ESPACIO);
   public static final RegistryObject<Item> ACERO_DE_LIMBO = block(ModmindsModBlocks.ACERO_DE_LIMBO);
   public static final RegistryObject<Item> CRIMSON_TALLADO = block(ModmindsModBlocks.CRIMSON_TALLADO);
   public static final RegistryObject<Item> BLOQUE_DE_REJILLAS_ROTAS = block(ModmindsModBlocks.BLOQUE_DE_REJILLAS_ROTAS);
   public static final RegistryObject<Item> PIEDRA_ROSADA_DEL_LIMBO = block(ModmindsModBlocks.PIEDRA_ROSADA_DEL_LIMBO);
   public static final RegistryObject<Item> PIEDRA_ROCOSA_DEL_LIMBO = block(ModmindsModBlocks.PIEDRA_ROCOSA_DEL_LIMBO);
   public static final RegistryObject<Item> CARMESI_PULIDO = block(ModmindsModBlocks.CARMESI_PULIDO);
   public static final RegistryObject<Item> PILAR_DE_MANSION = block(ModmindsModBlocks.PILAR_DE_MANSION);
   public static final RegistryObject<Item> LADRILLO_DE_MANSION = block(ModmindsModBlocks.LADRILLO_DE_MANSION);
   public static final RegistryObject<Item> TAXI_23_SPAWN_EGG = REGISTRY.register(
      "taxi_23_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.TAXI_23, -1, -1, new Item.Properties())
   );
   public static final RegistryObject<Item> SILBATO_DE_TAXI_23 = REGISTRY.register("silbato_de_taxi_23", () -> new SilbatoDeTaxi23Item());
   public static final RegistryObject<Item> PIEDRA_PULIDA_DE_SUELO_EXTRANO = block(ModmindsModBlocks.PIEDRA_PULIDA_DE_SUELO_EXTRANO);
   public static final RegistryObject<Item> SANGRE_DE_GORGONA = REGISTRY.register("sangre_de_gorgona", () -> new SangreDeGorgonaItem());
   public static final RegistryObject<Item> BLOQUE_BORG_CHISELADO = block(ModmindsModBlocks.BLOQUE_BORG_CHISELADO);
   public static final RegistryObject<Item> ESCALERAS_DE_BLOQUE_BORG_CHISELADO = block(ModmindsModBlocks.ESCALERAS_DE_BLOQUE_BORG_CHISELADO);
   public static final RegistryObject<Item> LOSA_DE_BLOQUE_BORG_CHISELADO = block(ModmindsModBlocks.LOSA_DE_BLOQUE_BORG_CHISELADO);
   public static final RegistryObject<Item> BLOQUE_PULIDO = block(ModmindsModBlocks.BLOQUE_PULIDO);
   public static final RegistryObject<Item> ESCALERAS_DE_BLOQUE_PULIDO = block(ModmindsModBlocks.ESCALERAS_DE_BLOQUE_PULIDO);
   public static final RegistryObject<Item> LOSAS_DE_BLOQUE_PULIDO = block(ModmindsModBlocks.LOSAS_DE_BLOQUE_PULIDO);
   public static final RegistryObject<Item> CRISTAL_DE_DILITIO_INDESTRUCTIBLE = block(ModmindsModBlocks.CRISTAL_DE_DILITIO_INDESTRUCTIBLE);
   public static final RegistryObject<Item> LOSA_DE_CRISTAL_DE_DILITIO_INDESTRUCTIBLE = block(ModmindsModBlocks.LOSA_DE_CRISTAL_DE_DILITIO_INDESTRUCTIBLE);
   public static final RegistryObject<Item> ESCALERAS_DE_CRISTAL_DE_DILITIO_INDESTRUCTIBLE = block(
      ModmindsModBlocks.ESCALERAS_DE_CRISTAL_DE_DILITIO_INDESTRUCTIBLE
   );
   public static final RegistryObject<Item> BLOQUE_DE_CORAL_NARANJA_LUMINOSO = block(ModmindsModBlocks.BLOQUE_DE_CORAL_NARANJA_LUMINOSO);
   public static final RegistryObject<Item> LOSA_DE_BLOQUE_DE_CORAL_PURPURA_LUMINOSO = block(ModmindsModBlocks.LOSA_DE_BLOQUE_DE_CORAL_PURPURA_LUMINOSO);
   public static final RegistryObject<Item> ESCALERAS_DE_CORAL_PURPURA_LUMINOSO = block(ModmindsModBlocks.ESCALERAS_DE_CORAL_PURPURA_LUMINOSO);
   public static final RegistryObject<Item> BLOQUE_DE_LIMBO_LUMINOSO = block(ModmindsModBlocks.BLOQUE_DE_LIMBO_LUMINOSO);
   public static final RegistryObject<Item> LOSA_DE_LIMBO_LUMINOSO = block(ModmindsModBlocks.LOSA_DE_LIMBO_LUMINOSO);
   public static final RegistryObject<Item> ESCALERAS_DE_LIMBO_LUMINOSO = block(ModmindsModBlocks.ESCALERAS_DE_LIMBO_LUMINOSO);
   public static final RegistryObject<Item> LADRILLOS_DE_ADOBE_CINCELADA = block(ModmindsModBlocks.LADRILLOS_DE_ADOBE_CINCELADA);
   public static final RegistryObject<Item> LOSA_DE_LADRILLOS_DE_ADOBE_CINCELADA = block(ModmindsModBlocks.LOSA_DE_LADRILLOS_DE_ADOBE_CINCELADA);
   public static final RegistryObject<Item> ESCALERAS_DE_LADRILLOS_DE_ADOBE_CINCELADA = block(ModmindsModBlocks.ESCALERAS_DE_LADRILLOS_DE_ADOBE_CINCELADA);
   public static final RegistryObject<Item> BLOQUE_DE_LUCES_HORIZONTALES_ROJO = block(ModmindsModBlocks.BLOQUE_DE_LUCES_HORIZONTALES_ROJO);
   public static final RegistryObject<Item> BLOQUE_DE_LUCES_HORIZONTALES_AMARILLO = block(ModmindsModBlocks.BLOQUE_DE_LUCES_HORIZONTALES_AMARILLO);
   public static final RegistryObject<Item> LOSA_DE_LUCES_HORIZONTALES_ROJO = block(ModmindsModBlocks.LOSA_DE_LUCES_HORIZONTALES_ROJO);
   public static final RegistryObject<Item> ESCALERAS_DE_LUCES_HORIZONTALES_ROJO = block(ModmindsModBlocks.ESCALERAS_DE_LUCES_HORIZONTALES_ROJO);
   public static final RegistryObject<Item> LOSA_DE_LUCES_HORIZONTALES_AMARILLO = block(ModmindsModBlocks.LOSA_DE_LUCES_HORIZONTALES_AMARILLO);
   public static final RegistryObject<Item> ESCALERAS_DE_LUCES_HORIZONTALES_AMARILLO = block(ModmindsModBlocks.ESCALERAS_DE_LUCES_HORIZONTALES_AMARILLO);
   public static final RegistryObject<Item> BLOQUE_DE_LUCES_VERTICALES_ROJO = block(ModmindsModBlocks.BLOQUE_DE_LUCES_VERTICALES_ROJO);
   public static final RegistryObject<Item> LOSA_DE_LUCES_VERTICALES_ROJO = block(ModmindsModBlocks.LOSA_DE_LUCES_VERTICALES_ROJO);
   public static final RegistryObject<Item> ESCALERAS_DE_LUCES_VERTICALES_ROJO = block(ModmindsModBlocks.ESCALERAS_DE_LUCES_VERTICALES_ROJO);
   public static final RegistryObject<Item> BLOQUE_DE_LUCES_VERTICALES_AMARILLO = block(ModmindsModBlocks.BLOQUE_DE_LUCES_VERTICALES_AMARILLO);
   public static final RegistryObject<Item> LOSA_DE_LUCES_VERTICALES_AMARILLO = block(ModmindsModBlocks.LOSA_DE_LUCES_VERTICALES_AMARILLO);
   public static final RegistryObject<Item> ESCALERAS_DE_LUCES_VERTICALES_AMARILLO = block(ModmindsModBlocks.ESCALERAS_DE_LUCES_VERTICALES_AMARILLO);
   public static final RegistryObject<Item> BLOQUE_DE_TUBERIA_BORG = block(ModmindsModBlocks.BLOQUE_DE_TUBERIA_BORG);
   public static final RegistryObject<Item> DARDOS_TIKIS = REGISTRY.register("dardos_tikis", () -> new DardosTikisItem());
   public static final RegistryObject<Item> ESPADA_TIKI = REGISTRY.register("espada_tiki", () -> new EspadaTikiItem());
   public static final RegistryObject<Item> LOSA_DE_TUBERIA_BORG = block(ModmindsModBlocks.LOSA_DE_TUBERIA_BORG);
   public static final RegistryObject<Item> ESCALERAS_DE_TUBERIA_BORG = block(ModmindsModBlocks.ESCALERAS_DE_TUBERIA_BORG);
   public static final RegistryObject<Item> TAXI_23_DE_CUATRO_ASIENTOS_SPAWN_EGG = REGISTRY.register(
      "taxi_23_de_cuatro_asientos_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.TAXI_23_DE_CUATRO_ASIENTOS, -1, -1, new Item.Properties())
   );
   public static final RegistryObject<Item> TRES_PALAS_ACOSTADAS = block(ModmindsModBlocks.TRES_PALAS_ACOSTADAS);
   public static final RegistryObject<Item> CUATRO_TABLAS_DE_MADERA = block(ModmindsModBlocks.CUATRO_TABLAS_DE_MADERA);
   public static final RegistryObject<Item> TRES_TABLAS_DE_MADERA_ENTRELAZADAS = block(ModmindsModBlocks.TRES_TABLAS_DE_MADERA_ENTRELAZADAS);
   public static final RegistryObject<Item> DOS_TABLAS_DE_MADERA = block(ModmindsModBlocks.DOS_TABLAS_DE_MADERA);
   public static final RegistryObject<Item> TRES_TABLAS_DE_MADERA = block(ModmindsModBlocks.TRES_TABLAS_DE_MADERA);
   public static final RegistryObject<Item> PAPEL_DE_BANO = block(ModmindsModBlocks.PAPEL_DE_BANO);
   public static final RegistryObject<Item> PALA_ACOSTADA = block(ModmindsModBlocks.PALA_ACOSTADA);
   public static final RegistryObject<Item> MACETA_NEGRA = block(ModmindsModBlocks.MACETA_NEGRA);
   public static final RegistryObject<Item> DOS_PALAS_ACOSTADAS = block(ModmindsModBlocks.DOS_PALAS_ACOSTADAS);
   public static final RegistryObject<Item> CUERDA = block(ModmindsModBlocks.CUERDA);
   public static final RegistryObject<Item> BALDE_VACIO = block(ModmindsModBlocks.BALDE_VACIO);
   public static final RegistryObject<Item> BALDE_LLENO = block(ModmindsModBlocks.BALDE_LLENO);
   public static final RegistryObject<Item> BALDE_CON_PINTURA_AZUL = block(ModmindsModBlocks.BALDE_CON_PINTURA_AZUL);
   public static final RegistryObject<Item> BALDE_CON_PINTURA_BLANCA = block(ModmindsModBlocks.BALDE_CON_PINTURA_BLANCA);
   public static final RegistryObject<Item> MACETA_BLANCA = block(ModmindsModBlocks.MACETA_BLANCA);
   public static final RegistryObject<Item> SILBATO_DE_TAXI_DE_4_ASIENTOS = REGISTRY.register(
      "silbato_de_taxi_de_4_asientos", () -> new SilbatoDeTaxiDe4AsientosItem()
   );
   public static final RegistryObject<Item> CRISTAL_DE_DILITIO_AZUL_CLARO = block(ModmindsModBlocks.CRISTAL_DE_DILITIO_AZUL_CLARO);
   public static final RegistryObject<Item> LOSA_DE_CRISTAL_DE_DILITIO_AZUL_CLARO = block(ModmindsModBlocks.LOSA_DE_CRISTAL_DE_DILITIO_AZUL_CLARO);
   public static final RegistryObject<Item> ESCALERAS_DE_CRISTAL_DE_DILITIO_AZUL_CLARO = block(
      ModmindsModBlocks.ESCALERAS_DE_CRISTAL_DE_DILITIO_AZUL_CLARO
   );
   public static final RegistryObject<Item> CRISTAL_DE_DILITIO_MAGENTA = block(ModmindsModBlocks.CRISTAL_DE_DILITIO_MAGENTA);
   public static final RegistryObject<Item> LOSA_DE_CRISTAL_DE_DILITIO_MAGENTA = block(ModmindsModBlocks.LOSA_DE_CRISTAL_DE_DILITIO_MAGENTA);
   public static final RegistryObject<Item> ESCALERAS_DE_CRISTAL_DE_DILITIO_MAGENTA = block(ModmindsModBlocks.ESCALERAS_DE_CRISTAL_DE_DILITIO_MAGENTA);
   public static final RegistryObject<Item> CRISTAL_DE_DILITIO_VERDE = block(ModmindsModBlocks.CRISTAL_DE_DILITIO_VERDE);
   public static final RegistryObject<Item> LOSA_CRISTAL_DE_DILITIO_VERDE = block(ModmindsModBlocks.LOSA_CRISTAL_DE_DILITIO_VERDE);
   public static final RegistryObject<Item> ESCALERAS_DE_CRISTAL_DE_DILITIO_VERDE = block(ModmindsModBlocks.ESCALERAS_DE_CRISTAL_DE_DILITIO_VERDE);
   public static final RegistryObject<Item> MURO_DE_LADRILLOS_NEGROS_AGRIETADOS = block(ModmindsModBlocks.MURO_DE_LADRILLOS_NEGROS_AGRIETADOS);
   public static final RegistryObject<Item> MURO_DE_LADRILLOS_DE_MAGMA = block(ModmindsModBlocks.MURO_DE_LADRILLOS_DE_MAGMA);
   public static final RegistryObject<Item> LOSA_DE_LADRILLO_DE_MANSION = block(ModmindsModBlocks.LOSA_DE_LADRILLO_DE_MANSION);
   public static final RegistryObject<Item> ESCALERAS_DE_LADRILLO_DE_MANSION = block(ModmindsModBlocks.ESCALERAS_DE_LADRILLO_DE_MANSION);
   public static final RegistryObject<Item> ADOBE_PULIDO = block(ModmindsModBlocks.ADOBE_PULIDO);
   public static final RegistryObject<Item> LOSA_DE_ADOBE_PULIDO = block(ModmindsModBlocks.LOSA_DE_ADOBE_PULIDO);
   public static final RegistryObject<Item> ESCALERAS_DE_ABOBE_PULIDO = block(ModmindsModBlocks.ESCALERAS_DE_ABOBE_PULIDO);
   public static final RegistryObject<Item> BLOQUE_DE_LUCES_HORIZONTALES_AZUL_CLARO = block(ModmindsModBlocks.BLOQUE_DE_LUCES_HORIZONTALES_AZUL_CLARO);
   public static final RegistryObject<Item> LOSA_DE_LUCES_HORIZONTALES_AZUL_CLARO = block(ModmindsModBlocks.LOSA_DE_LUCES_HORIZONTALES_AZUL_CLARO);
   public static final RegistryObject<Item> ESCALERAS_DE_LUCES_HORIZONTALES_AZUL_CLARO = block(
      ModmindsModBlocks.ESCALERAS_DE_LUCES_HORIZONTALES_AZUL_CLARO
   );
   public static final RegistryObject<Item> BLOQUE_DE_LUCES_HORIZONTALES_AZUL = block(ModmindsModBlocks.BLOQUE_DE_LUCES_HORIZONTALES_AZUL);
   public static final RegistryObject<Item> LOSA_DE_LUCES_HORIZONTALES_AZUL = block(ModmindsModBlocks.LOSA_DE_LUCES_HORIZONTALES_AZUL);
   public static final RegistryObject<Item> ESCALERAS_DE_LUCES_HORIZONTALES_AZUL = block(ModmindsModBlocks.ESCALERAS_DE_LUCES_HORIZONTALES_AZUL);
   public static final RegistryObject<Item> BLOQUE_DE_LUCES_HORIZONTALES_BLANCO = block(ModmindsModBlocks.BLOQUE_DE_LUCES_HORIZONTALES_BLANCO);
   public static final RegistryObject<Item> LOSA_DE_LUCES_HORIZONTALES_BLANCO = block(ModmindsModBlocks.LOSA_DE_LUCES_HORIZONTALES_BLANCO);
   public static final RegistryObject<Item> ESCALERAS_DE_LUCES_HORIZONTALES_BLANCO = block(ModmindsModBlocks.ESCALERAS_DE_LUCES_HORIZONTALES_BLANCO);
   public static final RegistryObject<Item> BLOQUE_DE_LUCES_HORIZONTALES_CIAN = block(ModmindsModBlocks.BLOQUE_DE_LUCES_HORIZONTALES_CIAN);
   public static final RegistryObject<Item> LOSA_DE_LUCES_HORIZONTALES_CIAN = block(ModmindsModBlocks.LOSA_DE_LUCES_HORIZONTALES_CIAN);
   public static final RegistryObject<Item> ESCALERAS_DE_LUCES_HORIZONTALES_CIAN = block(ModmindsModBlocks.ESCALERAS_DE_LUCES_HORIZONTALES_CIAN);
   public static final RegistryObject<Item> BLOQUE_DE_LUCES_HORIZONTALES_MAGENTA = block(ModmindsModBlocks.BLOQUE_DE_LUCES_HORIZONTALES_MAGENTA);
   public static final RegistryObject<Item> LOSA_DE_LUCES_HORIZONTALES_MAGENTA = block(ModmindsModBlocks.LOSA_DE_LUCES_HORIZONTALES_MAGENTA);
   public static final RegistryObject<Item> ESCALERAS_DE_LUCES_HORIZONTALES_MAGENTA = block(ModmindsModBlocks.ESCALERAS_DE_LUCES_HORIZONTALES_MAGENTA);
   public static final RegistryObject<Item> BLOQUE_DE_LUCES_HORIZONTALES_NARANJA = block(ModmindsModBlocks.BLOQUE_DE_LUCES_HORIZONTALES_NARANJA);
   public static final RegistryObject<Item> LOSA_DE_LUCES_HORIZONTALES_NARANJA = block(ModmindsModBlocks.LOSA_DE_LUCES_HORIZONTALES_NARANJA);
   public static final RegistryObject<Item> ESCALERAS_DE_LUCES_HORIZONTALES_NARANJA = block(ModmindsModBlocks.ESCALERAS_DE_LUCES_HORIZONTALES_NARANJA);
   public static final RegistryObject<Item> BLOQUE_DE_LUCES_HORIZONTALES_PURPURA = block(ModmindsModBlocks.BLOQUE_DE_LUCES_HORIZONTALES_PURPURA);
   public static final RegistryObject<Item> LOSA_DE_LUCES_HORIZONTALES_PURPURA = block(ModmindsModBlocks.LOSA_DE_LUCES_HORIZONTALES_PURPURA);
   public static final RegistryObject<Item> ESCALERAS_DE_LUCES_HORIZONTALES_PURPURA = block(ModmindsModBlocks.ESCALERAS_DE_LUCES_HORIZONTALES_PURPURA);
   public static final RegistryObject<Item> BLOQUE_DE_LUCES_HORIZONTALES_ROSA = block(ModmindsModBlocks.BLOQUE_DE_LUCES_HORIZONTALES_ROSA);
   public static final RegistryObject<Item> LOSA_DE_LUCES_HORIZONTALES_ROSA = block(ModmindsModBlocks.LOSA_DE_LUCES_HORIZONTALES_ROSA);
   public static final RegistryObject<Item> ESCALERAS_DE_LUCES_HORIZONTALES_ROSA = block(ModmindsModBlocks.ESCALERAS_DE_LUCES_HORIZONTALES_ROSA);
   public static final RegistryObject<Item> BLOQUE_DE_LUCES_HORIZONTALES_VERDE_LIMA = block(ModmindsModBlocks.BLOQUE_DE_LUCES_HORIZONTALES_VERDE_LIMA);
   public static final RegistryObject<Item> LOSA_DE_LUCES_HORIZONTALES_VERDE_LIMA = block(ModmindsModBlocks.LOSA_DE_LUCES_HORIZONTALES_VERDE_LIMA);
   public static final RegistryObject<Item> ESCALERAS_DE_LUCES_HORIZONTALES_VERDE_LIMA = block(
      ModmindsModBlocks.ESCALERAS_DE_LUCES_HORIZONTALES_VERDE_LIMA
   );
   public static final RegistryObject<Item> CRISTAL_DE_DILITIO_BLANCO = block(ModmindsModBlocks.CRISTAL_DE_DILITIO_BLANCO);
   public static final RegistryObject<Item> LOSA_DE_CRITAL_DE_DILITIO_BLANCO = block(ModmindsModBlocks.LOSA_DE_CRITAL_DE_DILITIO_BLANCO);
   public static final RegistryObject<Item> ESCALERAS_DE_CRITAL_DE_DILITIO_BLANCO = block(ModmindsModBlocks.ESCALERAS_DE_CRITAL_DE_DILITIO_BLANCO);
   public static final RegistryObject<Item> MADERA_CARMESI_PULIDA = block(ModmindsModBlocks.MADERA_CARMESI_PULIDA);
   public static final RegistryObject<Item> MADERA_DISTORSIONADA_PULIDA = block(ModmindsModBlocks.MADERA_DISTORSIONADA_PULIDA);
   public static final RegistryObject<Item> LADRILLOS_DE_ADOBE_ANTIGUOS = block(ModmindsModBlocks.LADRILLOS_DE_ADOBE_ANTIGUOS);
   public static final RegistryObject<Item> LOSA_DE_LADRILLOS_DE_ABOBE_ANTIGUOS = block(ModmindsModBlocks.LOSA_DE_LADRILLOS_DE_ABOBE_ANTIGUOS);
   public static final RegistryObject<Item> ESCALERAS_DE_LADRILLOS_DE_ADOBE_ANTIGUOS = block(ModmindsModBlocks.ESCALERAS_DE_LADRILLOS_DE_ADOBE_ANTIGUOS);
   public static final RegistryObject<Item> BLOQUE_DE_LUCES_HORIZONTALES_VERDE = block(ModmindsModBlocks.BLOQUE_DE_LUCES_HORIZONTALES_VERDE);
   public static final RegistryObject<Item> LOSA_DE_LUCES_HORIZONTALES_VERDE = block(ModmindsModBlocks.LOSA_DE_LUCES_HORIZONTALES_VERDE);
   public static final RegistryObject<Item> ESCALERAS_DE_LUCES_HORIZONTALES_VERDE = block(ModmindsModBlocks.ESCALERAS_DE_LUCES_HORIZONTALES_VERDE);
   public static final RegistryObject<Item> MURO_DE_PILAR_BORG = block(ModmindsModBlocks.MURO_DE_PILAR_BORG);
   public static final RegistryObject<Item> MURO_DE_BLOQUE_BORG_CHISELADO = block(ModmindsModBlocks.MURO_DE_BLOQUE_BORG_CHISELADO);
   public static final RegistryObject<Item> MURO_DE_BLOQUE_BORG = block(ModmindsModBlocks.MURO_DE_BLOQUE_BORG);
   public static final RegistryObject<Item> MURO_DE_BLOQUE_BORG_CINCELADO = block(ModmindsModBlocks.MURO_DE_BLOQUE_BORG_CINCELADO);
   public static final RegistryObject<Item> LOSA_BORG_CON_LUCES_VERDES = block(ModmindsModBlocks.LOSA_BORG_CON_LUCES_VERDES);
   public static final RegistryObject<Item> ESCALERAS_BORG_CON_LUCES_VERDES = block(ModmindsModBlocks.ESCALERAS_BORG_CON_LUCES_VERDES);
   public static final RegistryObject<Item> LOSA_BLOQUES_BORG_CON_LUCES_ROJAS = block(ModmindsModBlocks.LOSA_BLOQUES_BORG_CON_LUCES_ROJAS);
   public static final RegistryObject<Item> ESCALERAS_BLOQUES_BORG_CON_LUCES_ROJAS = block(ModmindsModBlocks.ESCALERAS_BLOQUES_BORG_CON_LUCES_ROJAS);
   public static final RegistryObject<Item> MURO_DE_LADRILLO_BORG = block(ModmindsModBlocks.MURO_DE_LADRILLO_BORG);
   public static final RegistryObject<Item> MURO_DE_BLOQUE_PULIDO = block(ModmindsModBlocks.MURO_DE_BLOQUE_PULIDO);
   public static final RegistryObject<Item> ESCALERA_DE_MADERA_CARMESI_PULIDA = block(ModmindsModBlocks.ESCALERA_DE_MADERA_CARMESI_PULIDA);
   public static final RegistryObject<Item> LOSA_DE_MADERA_CARMESI_PULIDA = block(ModmindsModBlocks.LOSA_DE_MADERA_CARMESI_PULIDA);
   public static final RegistryObject<Item> LOSA_MADERA_DISTORSIONADA_PULIDA = block(ModmindsModBlocks.LOSA_MADERA_DISTORSIONADA_PULIDA);
   public static final RegistryObject<Item> ESCALERA_MADERA_DISTORSIONADA_PULIDA = block(ModmindsModBlocks.ESCALERA_MADERA_DISTORSIONADA_PULIDA);
   public static final RegistryObject<Item> MURO_DE_TABLONES_ANTIGUOS = block(ModmindsModBlocks.MURO_DE_TABLONES_ANTIGUOS);
   public static final RegistryObject<Item> MURO_DE_TABLONES_AMARILLOS_ANTIGUOS = block(ModmindsModBlocks.MURO_DE_TABLONES_AMARILLOS_ANTIGUOS);
   public static final RegistryObject<Item> MURO_DE_TABLONES_ROJOS_ANTIGUOS = block(ModmindsModBlocks.MURO_DE_TABLONES_ROJOS_ANTIGUOS);
   public static final RegistryObject<Item> MURO_DE_TABLONES_VERDES_ANTIGUOS = block(ModmindsModBlocks.MURO_DE_TABLONES_VERDES_ANTIGUOS);
   public static final RegistryObject<Item> MURO_DE_TABLONES_MAGENTAS_ANTIGUOS = block(ModmindsModBlocks.MURO_DE_TABLONES_MAGENTAS_ANTIGUOS);
   public static final RegistryObject<Item> MURO_DE_TABLONES_ROSAS_ANTIGUOS = block(ModmindsModBlocks.MURO_DE_TABLONES_ROSAS_ANTIGUOS);
   public static final RegistryObject<Item> BLOQUE_DE_LUZ_VERTICAL_AZUL = block(ModmindsModBlocks.BLOQUE_DE_LUZ_VERTICAL_AZUL);
   public static final RegistryObject<Item> LOSA_DE_LUZ_VERTICAL_AZUL = block(ModmindsModBlocks.LOSA_DE_LUZ_VERTICAL_AZUL);
   public static final RegistryObject<Item> ESCALERA_DE_LUZ_VERTICAL_AZUL = block(ModmindsModBlocks.ESCALERA_DE_LUZ_VERTICAL_AZUL);
   public static final RegistryObject<Item> BLOQUE_DE_LUZ_VERTICAL_AZUL_CLARO = block(ModmindsModBlocks.BLOQUE_DE_LUZ_VERTICAL_AZUL_CLARO);
   public static final RegistryObject<Item> LOSA_DE_LUZ_VERTICAL_AZUL_CLARO = block(ModmindsModBlocks.LOSA_DE_LUZ_VERTICAL_AZUL_CLARO);
   public static final RegistryObject<Item> ESCALERA_DE_LUZ_VERTICAL_AZUL_CLARO = block(ModmindsModBlocks.ESCALERA_DE_LUZ_VERTICAL_AZUL_CLARO);
   public static final RegistryObject<Item> BLOQUE_DE_LUZ_VERTICA_CIAN = block(ModmindsModBlocks.BLOQUE_DE_LUZ_VERTICA_CIAN);
   public static final RegistryObject<Item> LOSA_DE_LUZ_VERTICA_CIAN = block(ModmindsModBlocks.LOSA_DE_LUZ_VERTICA_CIAN);
   public static final RegistryObject<Item> ESCALERA_DE_LUZ_VERTICA_CIAN = block(ModmindsModBlocks.ESCALERA_DE_LUZ_VERTICA_CIAN);
   public static final RegistryObject<Item> BLOQUE_DE_LUZ_VERTICAL_BLANCO = block(ModmindsModBlocks.BLOQUE_DE_LUZ_VERTICAL_BLANCO);
   public static final RegistryObject<Item> LOSA_DE_LUZ_VERTICAL_BLANCO = block(ModmindsModBlocks.LOSA_DE_LUZ_VERTICAL_BLANCO);
   public static final RegistryObject<Item> ESCALERA_DE_LUZ_VERTICAL_BLANCO = block(ModmindsModBlocks.ESCALERA_DE_LUZ_VERTICAL_BLANCO);
   public static final RegistryObject<Item> BLOQUE_DE_LUZ_VERTICAL_MAGENTA = block(ModmindsModBlocks.BLOQUE_DE_LUZ_VERTICAL_MAGENTA);
   public static final RegistryObject<Item> LOSA_DE_LUZ_VERTICAL_MAGENTA = block(ModmindsModBlocks.LOSA_DE_LUZ_VERTICAL_MAGENTA);
   public static final RegistryObject<Item> ESCALERAS_DE_LUZ_VERTICAL_MAGENTA = block(ModmindsModBlocks.ESCALERAS_DE_LUZ_VERTICAL_MAGENTA);
   public static final RegistryObject<Item> BLOQUE_DE_LUZ_VERTICAL_NARANJA = block(ModmindsModBlocks.BLOQUE_DE_LUZ_VERTICAL_NARANJA);
   public static final RegistryObject<Item> LOSA_DE_LUZ_VERTICAL_NARANJA = block(ModmindsModBlocks.LOSA_DE_LUZ_VERTICAL_NARANJA);
   public static final RegistryObject<Item> ESCALERAS_DE_LUZ_VERTICAL_NARANJA = block(ModmindsModBlocks.ESCALERAS_DE_LUZ_VERTICAL_NARANJA);
   public static final RegistryObject<Item> BLOQUE_DE_LUZ_VERTICAL_PURPURA = block(ModmindsModBlocks.BLOQUE_DE_LUZ_VERTICAL_PURPURA);
   public static final RegistryObject<Item> LOSA_DE_LUZ_VERTICAL_PURPURA = block(ModmindsModBlocks.LOSA_DE_LUZ_VERTICAL_PURPURA);
   public static final RegistryObject<Item> ESCALERAS_DE_LUZ_VERTICAL_PURPURA = block(ModmindsModBlocks.ESCALERAS_DE_LUZ_VERTICAL_PURPURA);
   public static final RegistryObject<Item> BLOQUE_DE_LUZ_VERTICAL_ROSA = block(ModmindsModBlocks.BLOQUE_DE_LUZ_VERTICAL_ROSA);
   public static final RegistryObject<Item> LOSA_DE_LUZ_VERTICAL_ROSA = block(ModmindsModBlocks.LOSA_DE_LUZ_VERTICAL_ROSA);
   public static final RegistryObject<Item> ESCALERAS_DE_LUZ_VERTICAL_ROSA = block(ModmindsModBlocks.ESCALERAS_DE_LUZ_VERTICAL_ROSA);
   public static final RegistryObject<Item> BLOQUE_DE_LUZ_VERTICAL_VERDE_LIMA = block(ModmindsModBlocks.BLOQUE_DE_LUZ_VERTICAL_VERDE_LIMA);
   public static final RegistryObject<Item> LOSA_DE_LUZ_VERTICAL_VERDE_LIMA = block(ModmindsModBlocks.LOSA_DE_LUZ_VERTICAL_VERDE_LIMA);
   public static final RegistryObject<Item> ESCALERAS_DE_LUZ_VERTICAL_VERDE_LIMA = block(ModmindsModBlocks.ESCALERAS_DE_LUZ_VERTICAL_VERDE_LIMA);
   public static final RegistryObject<Item> BLOQUE_DE_LUZ_VERTICAL_VERDE = block(ModmindsModBlocks.BLOQUE_DE_LUZ_VERTICAL_VERDE);
   public static final RegistryObject<Item> LOSA_DE_LUZ_VERTICAL_VERDE = block(ModmindsModBlocks.LOSA_DE_LUZ_VERTICAL_VERDE);
   public static final RegistryObject<Item> ESCALERAS_DE_LUZ_VERTICAL_VERDE = block(ModmindsModBlocks.ESCALERAS_DE_LUZ_VERTICAL_VERDE);
   public static final RegistryObject<Item> PILAR_BORG_CON_LUZ_VERCICAL_AMARILLA = block(ModmindsModBlocks.PILAR_BORG_CON_LUZ_VERCICAL_AMARILLA);
   public static final RegistryObject<Item> LOSA_DE_PILAR_BORG_CON_LUZ_VERCICAL_VERDE = block(ModmindsModBlocks.LOSA_DE_PILAR_BORG_CON_LUZ_VERCICAL_VERDE);
   public static final RegistryObject<Item> ESCALERAS_DE_PILAR_BORG_CON_LUZ_VERCICAL_AMARILLO = block(
      ModmindsModBlocks.ESCALERAS_DE_PILAR_BORG_CON_LUZ_VERCICAL_AMARILLO
   );
   public static final RegistryObject<Item> PILAR_BORG_CON_LUZ_VERCICAL_AZUL = block(ModmindsModBlocks.PILAR_BORG_CON_LUZ_VERCICAL_AZUL);
   public static final RegistryObject<Item> LOSA_DE_PILAR_BORG_CON_LUZ_VERCICAL_AZUL = block(ModmindsModBlocks.LOSA_DE_PILAR_BORG_CON_LUZ_VERCICAL_AZUL);
   public static final RegistryObject<Item> ESCALERAS_PILAR_BORG_CON_LUZ_VERCICAL_AZUL = block(
      ModmindsModBlocks.ESCALERAS_PILAR_BORG_CON_LUZ_VERCICAL_AZUL
   );
   public static final RegistryObject<Item> PILAR_BORG_CON_LUZ_VERCICAL_CIAN = block(ModmindsModBlocks.PILAR_BORG_CON_LUZ_VERCICAL_CIAN);
   public static final RegistryObject<Item> LOSA_DE_PILAR_BORG_CON_LUZ_VERCICAL_CIAN = block(ModmindsModBlocks.LOSA_DE_PILAR_BORG_CON_LUZ_VERCICAL_CIAN);
   public static final RegistryObject<Item> ESCALERAS_DE_PILAR_BORG_CON_LUZ_VERCICAL_CIAN = block(
      ModmindsModBlocks.ESCALERAS_DE_PILAR_BORG_CON_LUZ_VERCICAL_CIAN
   );
   public static final RegistryObject<Item> PILAR_BORG_CON_LUZ_VERCICAL_AZUL_CLARO = block(ModmindsModBlocks.PILAR_BORG_CON_LUZ_VERCICAL_AZUL_CLARO);
   public static final RegistryObject<Item> LOSA_DE_PILAR_BORG_CON_LUZ_VERCICAL_AZUL_CLARO = block(
      ModmindsModBlocks.LOSA_DE_PILAR_BORG_CON_LUZ_VERCICAL_AZUL_CLARO
   );
   public static final RegistryObject<Item> ESCALERAS_DE_PILAR_BORG_CON_LUZ_VERCICAL_AZUL_CLARO = block(
      ModmindsModBlocks.ESCALERAS_DE_PILAR_BORG_CON_LUZ_VERCICAL_AZUL_CLARO
   );
   public static final RegistryObject<Item> PILAR_BORG_CON_LUZ_VERCICAL_PURPURA = block(ModmindsModBlocks.PILAR_BORG_CON_LUZ_VERCICAL_PURPURA);
   public static final RegistryObject<Item> LOSA_DE_PILAR_BORG_CON_LUZ_VERCICAL_PURPURA = block(
      ModmindsModBlocks.LOSA_DE_PILAR_BORG_CON_LUZ_VERCICAL_PURPURA
   );
   public static final RegistryObject<Item> ESCALERAS_DE_PILAR_BORG_CON_LUZ_VERCICAL_PURPURA = block(
      ModmindsModBlocks.ESCALERAS_DE_PILAR_BORG_CON_LUZ_VERCICAL_PURPURA
   );
   public static final RegistryObject<Item> PILAR_BORG_CON_LUZ_VERCICAL_ROSA = block(ModmindsModBlocks.PILAR_BORG_CON_LUZ_VERCICAL_ROSA);
   public static final RegistryObject<Item> LOSA_DE_PILAR_BORG_CON_LUZ_VERCICAL_ROSA = block(ModmindsModBlocks.LOSA_DE_PILAR_BORG_CON_LUZ_VERCICAL_ROSA);
   public static final RegistryObject<Item> ESCALERAS_DE_PILAR_BORG_CON_LUZ_VERCICAL_ROSA = block(
      ModmindsModBlocks.ESCALERAS_DE_PILAR_BORG_CON_LUZ_VERCICAL_ROSA
   );
   public static final RegistryObject<Item> PILAR_BORG_CON_LUZ_VERTICAL_MAGENTA = block(ModmindsModBlocks.PILAR_BORG_CON_LUZ_VERTICAL_MAGENTA);
   public static final RegistryObject<Item> LOSA_DE_PILAR_BORG_CON_LUZ_VERTICAL_MAGENTA = block(
      ModmindsModBlocks.LOSA_DE_PILAR_BORG_CON_LUZ_VERTICAL_MAGENTA
   );
   public static final RegistryObject<Item> ESCALERAS_DE_PILAR_BORG_CON_LUZ_VERTICAL_MAGENTA = block(
      ModmindsModBlocks.ESCALERAS_DE_PILAR_BORG_CON_LUZ_VERTICAL_MAGENTA
   );
   public static final RegistryObject<Item> PILAR_BORG_CON_LUZ_VERTICAL_BLANCO = block(ModmindsModBlocks.PILAR_BORG_CON_LUZ_VERTICAL_BLANCO);
   public static final RegistryObject<Item> LOSA_DE_PILAR_BORG_CON_LUZ_VERTICAL_BLANCO = block(
      ModmindsModBlocks.LOSA_DE_PILAR_BORG_CON_LUZ_VERTICAL_BLANCO
   );
   public static final RegistryObject<Item> ESCALERAS_DE_PILAR_BORG_CON_LUZ_VERTICAL_BLANCO = block(
      ModmindsModBlocks.ESCALERAS_DE_PILAR_BORG_CON_LUZ_VERTICAL_BLANCO
   );
   public static final RegistryObject<Item> PILAR_BORG_CON_LUZ_VERTICAL_NARANJA = block(ModmindsModBlocks.PILAR_BORG_CON_LUZ_VERTICAL_NARANJA);
   public static final RegistryObject<Item> LOSA_DE_PILAR_BORG_CON_LUZ_VERTICAL_NARANJA = block(
      ModmindsModBlocks.LOSA_DE_PILAR_BORG_CON_LUZ_VERTICAL_NARANJA
   );
   public static final RegistryObject<Item> ESCALERAS_DE_PILAR_BORG_CON_LUZ_VERTICAL_NARANJA = block(
      ModmindsModBlocks.ESCALERAS_DE_PILAR_BORG_CON_LUZ_VERTICAL_NARANJA
   );
   public static final RegistryObject<Item> PILAR_BORG_CON_LUZ_VERTICAL_ROJO = block(ModmindsModBlocks.PILAR_BORG_CON_LUZ_VERTICAL_ROJO);
   public static final RegistryObject<Item> LOSA_DE_PILAR_BORG_CON_LUZ_VERTICAL_ROJO = block(ModmindsModBlocks.LOSA_DE_PILAR_BORG_CON_LUZ_VERTICAL_ROJO);
   public static final RegistryObject<Item> ESCALERAS_DE_PILAR_BORG_CON_LUZ_VERTICAL_ROJO = block(
      ModmindsModBlocks.ESCALERAS_DE_PILAR_BORG_CON_LUZ_VERTICAL_ROJO
   );
   public static final RegistryObject<Item> PILAR_BORG_CON_LUZ_VERTICAL_VERDE_LIMA = block(ModmindsModBlocks.PILAR_BORG_CON_LUZ_VERTICAL_VERDE_LIMA);
   public static final RegistryObject<Item> LOSA_DE_PILAR_BORG_CON_LUZ_VERTICAL_VERDE_LIMA = block(
      ModmindsModBlocks.LOSA_DE_PILAR_BORG_CON_LUZ_VERTICAL_VERDE_LIMA
   );
   public static final RegistryObject<Item> ESCALERAS_DE_PILAR_BORG_CON_LUZ_VERTICAL_VERDE_LIMA = block(
      ModmindsModBlocks.ESCALERAS_DE_PILAR_BORG_CON_LUZ_VERTICAL_VERDE_LIMA
   );
   public static final RegistryObject<Item> PILAR_BORG_CON_LUZ_VERTICAL_VERDE = block(ModmindsModBlocks.PILAR_BORG_CON_LUZ_VERTICAL_VERDE);
   public static final RegistryObject<Item> LOSA_DE_PILAR_BORG_CON_LUZ_VERTICAL_VERDE = block(ModmindsModBlocks.LOSA_DE_PILAR_BORG_CON_LUZ_VERTICAL_VERDE);
   public static final RegistryObject<Item> ESCALERAS_DE_PILAR_BORG_CON_LUZ_VERTICAL_VERDE = block(
      ModmindsModBlocks.ESCALERAS_DE_PILAR_BORG_CON_LUZ_VERTICAL_VERDE
   );
   public static final RegistryObject<Item> NAVE_ENEMIGA_DE_PULSO_BORG_SPAWN_EGG = REGISTRY.register(
      "nave_enemiga_de_pulso_borg_spawn_egg",
      () -> new ForgeSpawnEggItem(ModmindsModEntities.NAVE_ENEMIGA_DE_PULSO_BORG, -16737997, -16724941, new Item.Properties())
   );
   public static final RegistryObject<Item> NAVE_ENEMIGA_TANQUE_BORG_SPAWN_EGG = REGISTRY.register(
      "nave_enemiga_tanque_borg_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.NAVE_ENEMIGA_TANQUE_BORG, -16751104, -13408768, new Item.Properties())
   );
   public static final RegistryObject<Item> NAVE_ENEMIGA_ACORAZADA_BORG_SPAWN_EGG = REGISTRY.register(
      "nave_enemiga_acorazada_borg_spawn_egg",
      () -> new ForgeSpawnEggItem(ModmindsModEntities.NAVE_ENEMIGA_ACORAZADA_BORG, -10066177, -6684775, new Item.Properties())
   );
   public static final RegistryObject<Item> NAVE_ENEMIGA_ATOMIZADORA_DE_ASTROS_BORG_SPAWN_EGG = REGISTRY.register(
      "nave_enemiga_atomizadora_de_astros_borg_spawn_egg",
      () -> new ForgeSpawnEggItem(ModmindsModEntities.NAVE_ENEMIGA_ATOMIZADORA_DE_ASTROS_BORG, -10079488, -6724096, new Item.Properties())
   );
   public static final RegistryObject<Item> NAVE_ENEMIGA_CORTA_BORG_SPAWN_EGG = REGISTRY.register(
      "nave_enemiga_corta_borg_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.NAVE_ENEMIGA_CORTA_BORG, -6737152, -10079488, new Item.Properties())
   );
   public static final RegistryObject<Item> NAVE_ENEMIGA_AMARILLA_ACORAZADA_BORG_SPAWN_EGG = REGISTRY.register(
      "nave_enemiga_amarilla_acorazada_borg_spawn_egg",
      () -> new ForgeSpawnEggItem(ModmindsModEntities.NAVE_ENEMIGA_AMARILLA_ACORAZADA_BORG, -13312, -13421773, new Item.Properties())
   );
   public static final RegistryObject<Item> NAVE_ENEMIGA_PERFORADORA_DEL_VACIO_BORG_SPAWN_EGG = REGISTRY.register(
      "nave_enemiga_perforadora_del_vacio_borg_spawn_egg",
      () -> new ForgeSpawnEggItem(ModmindsModEntities.NAVE_ENEMIGA_PERFORADORA_DEL_VACIO_BORG, -3381760, -13421773, new Item.Properties())
   );
   public static final RegistryObject<Item> NAVE_ENEMIGA_ACORAZADA_ROJA_BORG_SPAWN_EGG = REGISTRY.register(
      "nave_enemiga_acorazada_roja_borg_spawn_egg",
      () -> new ForgeSpawnEggItem(ModmindsModEntities.NAVE_ENEMIGA_ACORAZADA_ROJA_BORG, -6750208, -13421773, new Item.Properties())
   );
   public static final RegistryObject<Item> NAVE_ENEMIGA_TRIPULSADORA_DE_IONES_BORG_SPAWN_EGG = REGISTRY.register(
      "nave_enemiga_tripulsadora_de_iones_borg_spawn_egg",
      () -> new ForgeSpawnEggItem(ModmindsModEntities.NAVE_ENEMIGA_TRIPULSADORA_DE_IONES_BORG, -6710887, -16750951, new Item.Properties())
   );
   public static final RegistryObject<Item> NAVE_ENEMIGA_CASITA_BORG_SPAWN_EGG = REGISTRY.register(
      "nave_enemiga_casita_borg_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.NAVE_ENEMIGA_CASITA_BORG, -3342388, -13421773, new Item.Properties())
   );
   public static final RegistryObject<Item> NAVE_ENEMIGA_AZUL_CLARO_BORG_SPAWN_EGG = REGISTRY.register(
      "nave_enemiga_azul_claro_borg_spawn_egg",
      () -> new ForgeSpawnEggItem(ModmindsModEntities.NAVE_ENEMIGA_AZUL_CLARO_BORG, -16724788, -6750208, new Item.Properties())
   );
   public static final RegistryObject<Item> ARMA_CORTA_ESPACIAL = REGISTRY.register("arma_corta_espacial", () -> new ArmaCortaEspacialItem());
   public static final RegistryObject<Item> RIFLE_DE_PULSO_BORG_2 = REGISTRY.register("rifle_de_pulso_borg_2", () -> new RifleDePulsoBorg2Item());
   public static final RegistryObject<Item> CERBATANA_DE_GOBLIN = REGISTRY.register("cerbatana_de_goblin", () -> new CerbatanaDeGoblinItem());
   public static final RegistryObject<Item> PERFORADOR_DEL_VACIO = REGISTRY.register("perforador_del_vacio", () -> new PerforadorDelVacioItem());
   public static final RegistryObject<Item> ATOMIZADOR_DE_ASTROS = REGISTRY.register("atomizador_de_astros", () -> new AtomizadorDeAstrosItem());
   public static final RegistryObject<Item> BLOQUE_ILUMINANTE_AMARILLO = block(ModmindsModBlocks.BLOQUE_ILUMINANTE_AMARILLO);
   public static final RegistryObject<Item> BLOQUE_ILUMINANTE_AZUL = block(ModmindsModBlocks.BLOQUE_ILUMINANTE_AZUL);
   public static final RegistryObject<Item> BLOQUE_ILUMINANTE_AZUL_CLARO = block(ModmindsModBlocks.BLOQUE_ILUMINANTE_AZUL_CLARO);
   public static final RegistryObject<Item> BLOQUE_ILUMINANTE_CIAN = block(ModmindsModBlocks.BLOQUE_ILUMINANTE_CIAN);
   public static final RegistryObject<Item> BLOQUE_ILUMINANTE_MAGENTA = block(ModmindsModBlocks.BLOQUE_ILUMINANTE_MAGENTA);
   public static final RegistryObject<Item> BLOQUE_ILUMINANTE_NARANJA = block(ModmindsModBlocks.BLOQUE_ILUMINANTE_NARANJA);
   public static final RegistryObject<Item> BLOQUE_ILUMINANTE_PURPURA = block(ModmindsModBlocks.BLOQUE_ILUMINANTE_PURPURA);
   public static final RegistryObject<Item> BLOQUE_ILUMINANTE_ROJO = block(ModmindsModBlocks.BLOQUE_ILUMINANTE_ROJO);
   public static final RegistryObject<Item> BLOQUE_ILUMINANTE_ROSA = block(ModmindsModBlocks.BLOQUE_ILUMINANTE_ROSA);
   public static final RegistryObject<Item> BLOQUE_ILUMINANTE_VERDE = block(ModmindsModBlocks.BLOQUE_ILUMINANTE_VERDE);
   public static final RegistryObject<Item> BLOQUE_ILUMINANTE_VERDE_LIRMA = block(ModmindsModBlocks.BLOQUE_ILUMINANTE_VERDE_LIRMA);
   public static final RegistryObject<Item> ESCALERAS_DE_BLOQUE_ILUMINANTE_AMARILLO = block(ModmindsModBlocks.ESCALERAS_DE_BLOQUE_ILUMINANTE_AMARILLO);
   public static final RegistryObject<Item> LOSA_DE_BLOQUE_ILUMINANTE_AMARILLO = block(ModmindsModBlocks.LOSA_DE_BLOQUE_ILUMINANTE_AMARILLO);
   public static final RegistryObject<Item> MURO_DE_BLOQUE_ILUMINANTE_AMARILLO = block(ModmindsModBlocks.MURO_DE_BLOQUE_ILUMINANTE_AMARILLO);
   public static final RegistryObject<Item> ESCALERAS_DE_BLOQUE_ILUMINANTE_AZUL = block(ModmindsModBlocks.ESCALERAS_DE_BLOQUE_ILUMINANTE_AZUL);
   public static final RegistryObject<Item> LOSA_DE_BLOQUE_ILUMINANTE_AZUL = block(ModmindsModBlocks.LOSA_DE_BLOQUE_ILUMINANTE_AZUL);
   public static final RegistryObject<Item> MURO_DE_BLOQUE_ILUMINANTE_AZUL = block(ModmindsModBlocks.MURO_DE_BLOQUE_ILUMINANTE_AZUL);
   public static final RegistryObject<Item> ESCALERAS_DE_BLOQUE_ILUMINANTE_AZUL_CLARO = block(ModmindsModBlocks.ESCALERAS_DE_BLOQUE_ILUMINANTE_AZUL_CLARO);
   public static final RegistryObject<Item> LOSA_DE_BLOQUE_ILUMINANTE_AZUL_CLARO = block(ModmindsModBlocks.LOSA_DE_BLOQUE_ILUMINANTE_AZUL_CLARO);
   public static final RegistryObject<Item> MURO_DE_BLOQUE_ILUMINANTE_AZUL_CLARO = block(ModmindsModBlocks.MURO_DE_BLOQUE_ILUMINANTE_AZUL_CLARO);
   public static final RegistryObject<Item> ESCALERAS_DE_BLOQUE_ILUMINANTE_CIAN = block(ModmindsModBlocks.ESCALERAS_DE_BLOQUE_ILUMINANTE_CIAN);
   public static final RegistryObject<Item> LOSA_DE_BLOQUE_ILUMINANTE_CIAN = block(ModmindsModBlocks.LOSA_DE_BLOQUE_ILUMINANTE_CIAN);
   public static final RegistryObject<Item> MURO_DE_BLOQUE_ILUMINANTE_CIAN = block(ModmindsModBlocks.MURO_DE_BLOQUE_ILUMINANTE_CIAN);
   public static final RegistryObject<Item> ESCALERAS_DE_BLOQUE_ILUMINANTE_MAGENTA = block(ModmindsModBlocks.ESCALERAS_DE_BLOQUE_ILUMINANTE_MAGENTA);
   public static final RegistryObject<Item> LOSA_DE_BLOQUE_ILUMINANTE_MAGENTA = block(ModmindsModBlocks.LOSA_DE_BLOQUE_ILUMINANTE_MAGENTA);
   public static final RegistryObject<Item> MURO_DE_BLOQUE_ILUMINANTE_MAGENTA = block(ModmindsModBlocks.MURO_DE_BLOQUE_ILUMINANTE_MAGENTA);
   public static final RegistryObject<Item> ESCALERAS_DE_BLOQUE_ILUMINANTE_NARANJA = block(ModmindsModBlocks.ESCALERAS_DE_BLOQUE_ILUMINANTE_NARANJA);
   public static final RegistryObject<Item> LOSA_DE_BLOQUE_ILUMINANTE_NARANJA = block(ModmindsModBlocks.LOSA_DE_BLOQUE_ILUMINANTE_NARANJA);
   public static final RegistryObject<Item> MURO_DE_BLOQUE_ILUMINANTE_NARANJA = block(ModmindsModBlocks.MURO_DE_BLOQUE_ILUMINANTE_NARANJA);
   public static final RegistryObject<Item> ESCALERAS_DE_BLOQUE_ILUMINANTE_PURPURA = block(ModmindsModBlocks.ESCALERAS_DE_BLOQUE_ILUMINANTE_PURPURA);
   public static final RegistryObject<Item> LOSA_DE_BLOQUE_ILUMINANTE_PURPURA = block(ModmindsModBlocks.LOSA_DE_BLOQUE_ILUMINANTE_PURPURA);
   public static final RegistryObject<Item> MURO_DE_BLOQUE_ILUMINANTE_PURPURA = block(ModmindsModBlocks.MURO_DE_BLOQUE_ILUMINANTE_PURPURA);
   public static final RegistryObject<Item> ESCALERAS_DE_BLOQUE_ILUMINANTE_ROJO = block(ModmindsModBlocks.ESCALERAS_DE_BLOQUE_ILUMINANTE_ROJO);
   public static final RegistryObject<Item> LOSA_DE_BLOQUE_ILUMINANTE_ROJO = block(ModmindsModBlocks.LOSA_DE_BLOQUE_ILUMINANTE_ROJO);
   public static final RegistryObject<Item> MURO_DE_BLOQUE_ILUMINANTE_ROJO = block(ModmindsModBlocks.MURO_DE_BLOQUE_ILUMINANTE_ROJO);
   public static final RegistryObject<Item> ESCALERAS_DE_BLOQUE_ILUMINANTE_ROSA = block(ModmindsModBlocks.ESCALERAS_DE_BLOQUE_ILUMINANTE_ROSA);
   public static final RegistryObject<Item> LOSA_DE_BLOQUE_ILUMINANTE_ROSA = block(ModmindsModBlocks.LOSA_DE_BLOQUE_ILUMINANTE_ROSA);
   public static final RegistryObject<Item> MURO_DE_BLOQUE_ILUMINANTE_ROSA = block(ModmindsModBlocks.MURO_DE_BLOQUE_ILUMINANTE_ROSA);
   public static final RegistryObject<Item> ESCALERAS_DE_BLOQUE_ILUMINANTE_VERDE = block(ModmindsModBlocks.ESCALERAS_DE_BLOQUE_ILUMINANTE_VERDE);
   public static final RegistryObject<Item> LOSA_DE_BLOQUE_ILUMINANTE_VERDE = block(ModmindsModBlocks.LOSA_DE_BLOQUE_ILUMINANTE_VERDE);
   public static final RegistryObject<Item> MURO_DE_BLOQUE_ILUMINANTE_VERDE = block(ModmindsModBlocks.MURO_DE_BLOQUE_ILUMINANTE_VERDE);
   public static final RegistryObject<Item> ESCALERA_DE_BLOQUE_ILUMINANTE_VERDE_LIMA = block(ModmindsModBlocks.ESCALERA_DE_BLOQUE_ILUMINANTE_VERDE_LIMA);
   public static final RegistryObject<Item> LOSA_DE_BLOQUE_ILUMINANTE_VERDE_LIMA = block(ModmindsModBlocks.LOSA_DE_BLOQUE_ILUMINANTE_VERDE_LIMA);
   public static final RegistryObject<Item> MURO_DE_BLOQUE_ILUMINANTE_VERDE_LIMA = block(ModmindsModBlocks.MURO_DE_BLOQUE_ILUMINANTE_VERDE_LIMA);
   public static final RegistryObject<Item> SERPIENTE_SPAWN_EGG = REGISTRY.register(
      "serpiente_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.SERPIENTE, -10400692, -8229830, new Item.Properties())
   );
   public static final RegistryObject<Item> SAPO_SPAWN_EGG = REGISTRY.register(
      "sapo_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.SAPO, -16738048, -16724992, new Item.Properties())
   );
   public static final RegistryObject<Item> FANTASMA_ROJO_SPAWN_EGG = REGISTRY.register(
      "fantasma_rojo_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.FANTASMA_ROJO, -65536, -52429, new Item.Properties())
   );
   public static final RegistryObject<Item> PUERTA_BORG_VERDE_SPAWN_EGG = REGISTRY.register(
      "puerta_borg_verde_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.PUERTA_BORG_VERDE, -13421773, -16738048, new Item.Properties())
   );
   public static final RegistryObject<Item> PUERTA_BORG_CHIP = REGISTRY.register("puerta_borg_chip", () -> new PuertaBorgChipItem());
   public static final RegistryObject<Item> FANTASMA_AMARILLO_SPAWN_EGG = REGISTRY.register(
      "fantasma_amarillo_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.FANTASMA_AMARILLO, -6711040, -256, new Item.Properties())
   );
   public static final RegistryObject<Item> FANTASMA_NEGRO_SPAWN_EGG = REGISTRY.register(
      "fantasma_negro_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.FANTASMA_NEGRO, -13421773, -10066330, new Item.Properties())
   );
   public static final RegistryObject<Item> PUERTA_BORG_AMARILLO_SPAWN_EGG = REGISTRY.register(
      "puerta_borg_amarillo_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.PUERTA_BORG_AMARILLO, -13421773, -3355648, new Item.Properties())
   );
   public static final RegistryObject<Item> PUERTA_BORG_CHIP_AMARILLA = REGISTRY.register(
      "puerta_borg_chip_amarilla", () -> new PuertaBorgChipAmarillaItem()
   );
   public static final RegistryObject<Item> PUERTA_BORG_ROSA_SPAWN_EGG = REGISTRY.register(
      "puerta_borg_rosa_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.PUERTA_BORG_ROSA, -13421773, -3407719, new Item.Properties())
   );
   public static final RegistryObject<Item> PUERTA_BORG_CHIP_ROSA = REGISTRY.register("puerta_borg_chip_rosa", () -> new PuertaBorgChipRosaItem());
   public static final RegistryObject<Item> PUERTA_BORG_CHIP_AZUL = REGISTRY.register("puerta_borg_chip_azul", () -> new PuertaBorgChipAzulItem());
   public static final RegistryObject<Item> PUERTA_BORG_CHIP_ROJA = REGISTRY.register("puerta_borg_chip_roja", () -> new PuertaBorgChipRojaItem());
   public static final RegistryObject<Item> PUERTA_BORG_CHIP_MORADA = REGISTRY.register("puerta_borg_chip_morada", () -> new PuertaBorgChipMoradaItem());
   public static final RegistryObject<Item> PUERTA_BORG_AZUL_SPAWN_EGG = REGISTRY.register(
      "puerta_borg_azul_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.PUERTA_BORG_AZUL, -13421773, -16750900, new Item.Properties())
   );
   public static final RegistryObject<Item> PUERTA_BORG_ROJA_SPAWN_EGG = REGISTRY.register(
      "puerta_borg_roja_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.PUERTA_BORG_ROJA, -13421773, -6750208, new Item.Properties())
   );
   public static final RegistryObject<Item> PUERTA_BORG_MORADA_SPAWN_EGG = REGISTRY.register(
      "puerta_borg_morada_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.PUERTA_BORG_MORADA, -13421773, -10092442, new Item.Properties())
   );
   public static final RegistryObject<Item> BLOQUE_DE_PISO_BORG_DIVIDIDO_VERTICAL = block(ModmindsModBlocks.BLOQUE_DE_PISO_BORG_DIVIDIDO_VERTICAL);
   public static final RegistryObject<Item> ESCALERA_DE_PISO_BORG_DIVIDIDO_VERTICAL = block(ModmindsModBlocks.ESCALERA_DE_PISO_BORG_DIVIDIDO_VERTICAL);
   public static final RegistryObject<Item> LOSA_DE_PISO_BORG_DIVIDIDO_VERTICAL = block(ModmindsModBlocks.LOSA_DE_PISO_BORG_DIVIDIDO_VERTICAL);
   public static final RegistryObject<Item> MURO_DE_PISO_BORG_DIVIDIDO_VERTICAL = block(ModmindsModBlocks.MURO_DE_PISO_BORG_DIVIDIDO_VERTICAL);
   public static final RegistryObject<Item> BLOQUE_DE_HIERRO_REFORZADO = block(ModmindsModBlocks.BLOQUE_DE_HIERRO_REFORZADO);
   public static final RegistryObject<Item> BOMBA_ROJA_SPAWN_EGG = REGISTRY.register(
      "bomba_roja_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.BOMBA_ROJA, -10092544, -16777216, new Item.Properties())
   );
   public static final RegistryObject<Item> BOMBA_AMARILLA_SPAWN_EGG = REGISTRY.register(
      "bomba_amarilla_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.BOMBA_AMARILLA, -256, -16777216, new Item.Properties())
   );
   public static final RegistryObject<Item> CREEPER_CAKE_SPAWN_EGG = REGISTRY.register(
      "creeper_cake_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.CREEPER_CAKE, -6737152, -1, new Item.Properties())
   );
   public static final RegistryObject<Item> FLASH_BANG_SPAWN_EGG = REGISTRY.register(
      "flash_bang_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.FLASH_BANG, -16777216, -13421773, new Item.Properties())
   );
   public static final RegistryObject<Item> BOMBA_ACUATICA_GRANDE_SPAWN_EGG = REGISTRY.register(
      "bomba_acuatica_grande_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.BOMBA_ACUATICA_GRANDE, -3355393, -6710785, new Item.Properties())
   );
   public static final RegistryObject<Item> BOMBA_ACUATICA_PEQUENA_SPAWN_EGG = REGISTRY.register(
      "bomba_acuatica_pequena_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.BOMBA_ACUATICA_PEQUENA, -6710785, -3355393, new Item.Properties())
   );
   public static final RegistryObject<Item> WILD_FIRE_SPAWN_EGG = REGISTRY.register(
      "wild_fire_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.WILD_FIRE, -13261, -10092544, new Item.Properties())
   );
   public static final RegistryObject<Item> BLOQUE_BORG_VARIANTE_10 = block(ModmindsModBlocks.BLOQUE_BORG_VARIANTE_10);
   public static final RegistryObject<Item> ESCALERAS_DE_BORG_VARIANTE_10 = block(ModmindsModBlocks.ESCALERAS_DE_BORG_VARIANTE_10);
   public static final RegistryObject<Item> LOSA_DE_BORG_VARIANTE_10 = block(ModmindsModBlocks.LOSA_DE_BORG_VARIANTE_10);
   public static final RegistryObject<Item> MURO_DE_BORG_VARIANTE_10 = block(ModmindsModBlocks.MURO_DE_BORG_VARIANTE_10);
   public static final RegistryObject<Item> BLOQUE_BORG_ANIMADO_NUCLEO_1 = block(ModmindsModBlocks.BLOQUE_BORG_ANIMADO_NUCLEO_1);
   public static final RegistryObject<Item> ESCALERAS_DE_BORG_ANIMADO_NUCLEO_1 = block(ModmindsModBlocks.ESCALERAS_DE_BORG_ANIMADO_NUCLEO_1);
   public static final RegistryObject<Item> LOSA_DE_BORG_ANIMADO_NUCLEO_1 = block(ModmindsModBlocks.LOSA_DE_BORG_ANIMADO_NUCLEO_1);
   public static final RegistryObject<Item> BLOQUE_BORG_PANELES_MAGENTAS = block(ModmindsModBlocks.BLOQUE_BORG_PANELES_MAGENTAS);
   public static final RegistryObject<Item> ESCALERAS_DE_BLOQUE_BORG_PANELES_MAGENTAS = block(ModmindsModBlocks.ESCALERAS_DE_BLOQUE_BORG_PANELES_MAGENTAS);
   public static final RegistryObject<Item> LOSA_DE_BLOQUE_BORG_PANELES_MAGENTAS = block(ModmindsModBlocks.LOSA_DE_BLOQUE_BORG_PANELES_MAGENTAS);
   public static final RegistryObject<Item> MANSION = block(ModmindsModBlocks.MANSION);
   public static final RegistryObject<Item> PILAR_DE_WARPED = block(ModmindsModBlocks.PILAR_DE_WARPED);
   public static final RegistryObject<Item> LOSA_DE_PILAR_DE_WARPED = block(ModmindsModBlocks.LOSA_DE_PILAR_DE_WARPED);
   public static final RegistryObject<Item> ESCALERAS_DE_PILAR_DE_WARPED = block(ModmindsModBlocks.ESCALERAS_DE_PILAR_DE_WARPED);
   public static final RegistryObject<Item> BLOQUE_BORG_VARIANTE_11 = block(ModmindsModBlocks.BLOQUE_BORG_VARIANTE_11);
   public static final RegistryObject<Item> BLOQUE_BORG_VARIANTE_12 = block(ModmindsModBlocks.BLOQUE_BORG_VARIANTE_12);
   public static final RegistryObject<Item> BLOQUE_BORG_VARIANTE_13 = block(ModmindsModBlocks.BLOQUE_BORG_VARIANTE_13);
   public static final RegistryObject<Item> BLOQUE_BORG_VARIANTE_14 = block(ModmindsModBlocks.BLOQUE_BORG_VARIANTE_14);
   public static final RegistryObject<Item> BLOQUE_BORG_VARIANTE_15 = block(ModmindsModBlocks.BLOQUE_BORG_VARIANTE_15);
   public static final RegistryObject<Item> BLOQUE_BORG_VARIANTE_16 = block(ModmindsModBlocks.BLOQUE_BORG_VARIANTE_16);
   public static final RegistryObject<Item> MURO_DE_NEGRO_DE_RING = block(ModmindsModBlocks.MURO_DE_NEGRO_DE_RING);
   public static final RegistryObject<Item> LADRILLO_PULIDO_DE_MANSION = block(ModmindsModBlocks.LADRILLO_PULIDO_DE_MANSION);
   public static final RegistryObject<Item> LOSA_DE_LADRILLO_PULIDO_DE_MANSION = block(ModmindsModBlocks.LOSA_DE_LADRILLO_PULIDO_DE_MANSION);
   public static final RegistryObject<Item> ESCALERAS_DE_LADRILLO_PULIDO_DE_MANSION = block(ModmindsModBlocks.ESCALERAS_DE_LADRILLO_PULIDO_DE_MANSION);
   public static final RegistryObject<Item> JERINGA_OVERWORLD = REGISTRY.register("jeringa_overworld", () -> new JeringaOverworldItem());
   public static final RegistryObject<Item> JERINGA_OVERWORLD_L_LENA = REGISTRY.register(
      "jeringa_overworld_l_lena", () -> new JeringaOverworldLLenaItem()
   );
   public static final RegistryObject<Item> JERINGA_NETHER = REGISTRY.register("jeringa_nether", () -> new JeringaNetherItem());
   public static final RegistryObject<Item> JERINGA_NETHER_LLENA = REGISTRY.register("jeringa_nether_llena", () -> new JeringaNetherLlenaItem());
   public static final RegistryObject<Item> JERINGA_NEREIDA = REGISTRY.register("jeringa_nereida", () -> new JeringaNereidaItem());
   public static final RegistryObject<Item> JERINGA_NEREIDA_LLENA = REGISTRY.register("jeringa_nereida_llena", () -> new JeringaNereidaLlenaItem());
   public static final RegistryObject<Item> JERINGA_ESPACIAL = REGISTRY.register("jeringa_espacial", () -> new JeringaEspacialItem());
   public static final RegistryObject<Item> JERINGA_ESPACIAL_LLENA = REGISTRY.register("jeringa_espacial_llena", () -> new JeringaEspacialLlenaItem());
   public static final RegistryObject<Item> AGUA_DEL_LIMBO_BUCKET = REGISTRY.register("agua_del_limbo_bucket", () -> new AguaDelLimboItem());
   public static final RegistryObject<Item> LIMBO = REGISTRY.register("limbo", () -> new LimboItem());
   public static final RegistryObject<Item> PISCINA_DE_DILITIO_BORG = block(ModmindsModBlocks.PISCINA_DE_DILITIO_BORG);
   public static final RegistryObject<Item> RAYO_LEVITANTE_BORG = block(ModmindsModBlocks.RAYO_LEVITANTE_BORG);
   public static final RegistryObject<Item> WARDEN_MOUNSTROSITY_SPAWN_EGG = REGISTRY.register(
      "warden_mounstrosity_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.WARDEN_MOUNSTROSITY, -16777216, -16764109, new Item.Properties())
   );
   public static final RegistryObject<Item> AQUA_STONE = block(ModmindsModBlocks.AQUA_STONE);
   public static final RegistryObject<Item> LOSAS_DE_AQUA_STONE = block(ModmindsModBlocks.LOSAS_DE_AQUA_STONE);
   public static final RegistryObject<Item> ESCALERAS_DE_AQUA_STONE = block(ModmindsModBlocks.ESCALERAS_DE_AQUA_STONE);
   public static final RegistryObject<Item> MURO_DE_AQUA_STONE = block(ModmindsModBlocks.MURO_DE_AQUA_STONE);
   public static final RegistryObject<Item> BLUE_STONE = block(ModmindsModBlocks.BLUE_STONE);
   public static final RegistryObject<Item> LOSAS_DE_BLUE_STONE = block(ModmindsModBlocks.LOSAS_DE_BLUE_STONE);
   public static final RegistryObject<Item> ESCALERAS_DE_BLUE_STONE = block(ModmindsModBlocks.ESCALERAS_DE_BLUE_STONE);
   public static final RegistryObject<Item> MURO_DE_BLUE_STONE = block(ModmindsModBlocks.MURO_DE_BLUE_STONE);
   public static final RegistryObject<Item> PIGLIN_DEL_LIMBO_SPAWN_EGG = REGISTRY.register(
      "piglin_del_limbo_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.PIGLIN_DEL_LIMBO, -12309640, -2391231, new Item.Properties())
   );
   public static final RegistryObject<Item> GEMA_DE_ALMAS = REGISTRY.register("gema_de_almas", () -> new GemaDeAlmasItem());
   public static final RegistryObject<Item> FRAGMENTOS_DE_GEMA_DE_ALMAS = REGISTRY.register(
      "fragmentos_de_gema_de_almas", () -> new FragmentosDeGemaDeAlmasItem()
   );
   public static final RegistryObject<Item> JERINGA_DEL_LIMBO = REGISTRY.register("jeringa_del_limbo", () -> new JeringaDelLimboItem());
   public static final RegistryObject<Item> JERINGA_DEL_LIMBO_LLENA = REGISTRY.register("jeringa_del_limbo_llena", () -> new JeringaDelLimboLlenaItem());
   public static final RegistryObject<Item> HUESO_DE_LA_GRIM_REAPER = REGISTRY.register("hueso_de_la_grim_reaper", () -> new HuesoDeLaGrimReaperItem());
   public static final RegistryObject<Item> BLOQUE_DE_RESTAURANTR = block(ModmindsModBlocks.BLOQUE_DE_RESTAURANTR);
   public static final RegistryObject<Item> ESPADA_FANTAS_MAL = REGISTRY.register("espada_fantas_mal", () -> new EspadaFantasMalItem());
   public static final RegistryObject<Item> BONE_SPIDER_SPAWN_EGG = REGISTRY.register(
      "bone_spider_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.BONE_SPIDER, -12974017, -1, new Item.Properties())
   );
   public static final RegistryObject<Item> REACTOR_SPAWN_EGG = REGISTRY.register(
      "reactor_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.REACTOR, -9895165, -13294299, new Item.Properties())
   );
   public static final RegistryObject<Item> MOUNSTROSITY_DIMENSION = REGISTRY.register("mounstrosity_dimension", () -> new MounstrosityDimensionItem());
   public static final RegistryObject<Item> TIKI_GUARDIAN_SPAWN_EGG = REGISTRY.register(
      "tiki_guardian_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.TIKI_GUARDIAN, -13090268, -13951207, new Item.Properties())
   );
   public static final RegistryObject<Item> SOLDADO_REVIIL_SPAWN_EGG = REGISTRY.register(
      "soldado_reviil_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.SOLDADO_REVIIL, -6908267, -16744192, new Item.Properties())
   );
   public static final RegistryObject<Item> FORZITA = REGISTRY.register("forzita", () -> new ForzitaItem());
   public static final RegistryObject<Item> GLOBO_SPAWN_EGG = REGISTRY.register(
      "globo_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.GLOBO, -39169, -10027162, new Item.Properties())
   );
   public static final RegistryObject<Item> CAMBIAR_COLOR_GLOBO = REGISTRY.register("cambiar_color_globo", () -> new CambiarColorGloboItem());
   public static final RegistryObject<Item> LLAVE_DE_ADMINISTRADOR = REGISTRY.register("llave_de_administrador", () -> new LlaveDeAdministradorItem());
   public static final RegistryObject<Item> HYDRA_SPAWN_EGG = REGISTRY.register(
      "hydra_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.HYDRA, -16031832, -15772264, new Item.Properties())
   );
   public static final RegistryObject<Item> BOLA_DE_HYDRA = REGISTRY.register("bola_de_hydra", () -> new BolaDeHydraItem());
   public static final RegistryObject<Item> WATER_CHARGE_ENTIDAD_SPAWN_EGG = REGISTRY.register(
      "water_charge_entidad_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.WATER_CHARGE_ENTIDAD, -1, -1, new Item.Properties())
   );
   public static final RegistryObject<Item> LIMBO_TITAN_SPAWN_EGG = REGISTRY.register(
      "limbo_titan_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.LIMBO_TITAN, -15400960, -16128, new Item.Properties())
   );
   public static final RegistryObject<Item> REVIIL_SPAWN_EGG = REGISTRY.register(
      "reviil_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.REVIIL, -14931454, -12648191, new Item.Properties())
   );
   public static final RegistryObject<Item> NEREIDA_SPAWN_EGG = REGISTRY.register(
      "nereida_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.NEREIDA, -16711732, -307713, new Item.Properties())
   );
   public static final RegistryObject<Item> TORRETA_TIKI_SPAWN_EGG = REGISTRY.register(
      "torreta_tiki_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.TORRETA_TIKI, -10207181, -13951203, new Item.Properties())
   );
   public static final RegistryObject<Item> BLOQUE_RULETA_AZUL = block(ModmindsModBlocks.BLOQUE_RULETA_AZUL);
   public static final RegistryObject<Item> BLOQUE_RULETA_AMARILLO = block(ModmindsModBlocks.BLOQUE_RULETA_AMARILLO);
   public static final RegistryObject<Item> BLOQUE_RULETA_CYAN = block(ModmindsModBlocks.BLOQUE_RULETA_CYAN);
   public static final RegistryObject<Item> BLOQUE_RULETA_MORADO = block(ModmindsModBlocks.BLOQUE_RULETA_MORADO);
   public static final RegistryObject<Item> BLOQUE_RULETA_NARANJA = block(ModmindsModBlocks.BLOQUE_RULETA_NARANJA);
   public static final RegistryObject<Item> BLOQUE_RULETA_ROJO = block(ModmindsModBlocks.BLOQUE_RULETA_ROJO);
   public static final RegistryObject<Item> BLOQUE_RULETA_VERDE = block(ModmindsModBlocks.BLOQUE_RULETA_VERDE);
   public static final RegistryObject<Item> BLOQUE_RULETA_ROSA = block(ModmindsModBlocks.BLOQUE_RULETA_ROSA);
   public static final RegistryObject<Item> BLOQUE_RULETA_AZUL_GLOWING = block(ModmindsModBlocks.BLOQUE_RULETA_AZUL_GLOWING);
   public static final RegistryObject<Item> BLOQUE_RULETA_GLOWING_AMARILLO = block(ModmindsModBlocks.BLOQUE_RULETA_GLOWING_AMARILLO);
   public static final RegistryObject<Item> BLOQUE_RULETA_GLOWING_CYAN = block(ModmindsModBlocks.BLOQUE_RULETA_GLOWING_CYAN);
   public static final RegistryObject<Item> BLOQUE_RULETA_GLOWING_MORADA = block(ModmindsModBlocks.BLOQUE_RULETA_GLOWING_MORADA);
   public static final RegistryObject<Item> BLOQUE_RULETA_GLOWING_NARANJA = block(ModmindsModBlocks.BLOQUE_RULETA_GLOWING_NARANJA);
   public static final RegistryObject<Item> BLOQUE_RULETA_GLOWING_ROJO = block(ModmindsModBlocks.BLOQUE_RULETA_GLOWING_ROJO);
   public static final RegistryObject<Item> BLOQUE_RULETA_GLOWING_VERDE = block(ModmindsModBlocks.BLOQUE_RULETA_GLOWING_VERDE);
   public static final RegistryObject<Item> BLOQUE_RULETA_GLOWING_ROSA = block(ModmindsModBlocks.BLOQUE_RULETA_GLOWING_ROSA);
   public static final RegistryObject<Item> ESPADA_INANIMADA = REGISTRY.register("espada_inanimada", () -> new EspadaInanimadaItem());
   public static final RegistryObject<Item> AREPA = REGISTRY.register("arepa", () -> new ArepaItem());
   public static final RegistryObject<Item> AREPA_DE_CARNE = REGISTRY.register("arepa_de_carne", () -> new ArepaDeCarneItem());
   public static final RegistryObject<Item> BARRA_DE_CHOCOLATE = REGISTRY.register("barra_de_chocolate", () -> new BarraDeChocolateItem());
   public static final RegistryObject<Item> BARRA_DE_CHOCOLATE_2 = REGISTRY.register("barra_de_chocolate_2", () -> new BarraDeChocolate2Item());
   public static final RegistryObject<Item> AGUAPANELA = REGISTRY.register("aguapanela", () -> new AguapanelaItem());
   public static final RegistryObject<Item> I_CARAMELITO_DE_FRESA = REGISTRY.register("i_caramelito_de_fresa", () -> new ICaramelitoDeFresaItem());
   public static final RegistryObject<Item> CARAMELITO_DE_MORA = REGISTRY.register("caramelito_de_mora", () -> new CaramelitoDeMoraItem());
   public static final RegistryObject<Item> CARAMELITO_DE_NARANJA = REGISTRY.register("caramelito_de_naranja", () -> new CaramelitoDeNaranjaItem());
   public static final RegistryObject<Item> CARAMELITO_DE_FRAMBUESA = REGISTRY.register("caramelito_de_frambuesa", () -> new CaramelitoDeFrambuesaItem());
   public static final RegistryObject<Item> CARAMELITO_DE_PINA = REGISTRY.register("caramelito_de_pina", () -> new CaramelitoDePinaItem());
   public static final RegistryObject<Item> CARAMELITO_DE_MORA_AZUL = REGISTRY.register("caramelito_de_mora_azul", () -> new CaramelitoDeMoraAzulItem());
   public static final RegistryObject<Item> M_AND_M = REGISTRY.register("m_and_m", () -> new MAndMItem());
   public static final RegistryObject<Item> M_AND_M_NARANJA = REGISTRY.register("m_and_m_naranja", () -> new MAndMNaranjaItem());
   public static final RegistryObject<Item> M_AND_M_VERDE = REGISTRY.register("m_and_m_verde", () -> new MAndMVerdeItem());
   public static final RegistryObject<Item> M_AND_M_AZUK = REGISTRY.register("m_and_m_azuk", () -> new MAndMAzukItem());
   public static final RegistryObject<Item> M_AND_M_MARRON = REGISTRY.register("m_and_m_marron", () -> new MAndMMarronItem());
   public static final RegistryObject<Item> EMPANADA = REGISTRY.register("empanada", () -> new EmpanadaItem());
   public static final RegistryObject<Item> HELADO_DE_VAINILLA = REGISTRY.register("helado_de_vainilla", () -> new HeladoDeVainillaItem());
   public static final RegistryObject<Item> OSITO_MORADO = REGISTRY.register("osito_morado", () -> new OsitoMoradoItem());
   public static final RegistryObject<Item> OSITO_AZUL = REGISTRY.register("osito_azul", () -> new OsitoAzulItem());
   public static final RegistryObject<Item> OSITO_VERDE = REGISTRY.register("osito_verde", () -> new OsitoVerdeItem());
   public static final RegistryObject<Item> OSITO_ROJO = REGISTRY.register("osito_rojo", () -> new OsitoRojoItem());
   public static final RegistryObject<Item> OSITO_ROSA = REGISTRY.register("osito_rosa", () -> new OsitoRosaItem());
   public static final RegistryObject<Item> OSITO_NARANJA = REGISTRY.register("osito_naranja", () -> new OsitoNaranjaItem());
   public static final RegistryObject<Item> OSITO_AMARILLO = REGISTRY.register("osito_amarillo", () -> new OsitoAmarilloItem());
   public static final RegistryObject<Item> PALETA_DE_CHOCOLATE = REGISTRY.register("paleta_de_chocolate", () -> new PaletaDeChocolateItem());
   public static final RegistryObject<Item> PAN = REGISTRY.register("pan", () -> new PanItem());
   public static final RegistryObject<Item> PAN_DE_MUERTO = REGISTRY.register("pan_de_muerto", () -> new PanDeMuertoItem());
   public static final RegistryObject<Item> REBANADA_DE_PAN = REGISTRY.register("rebanada_de_pan", () -> new RebanadaDePanItem());
   public static final RegistryObject<Item> ORB_SPAWN_EGG = REGISTRY.register(
      "orb_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.ORB, -52429, -3407872, new Item.Properties())
   );
   public static final RegistryObject<Item> CORAZON_DORADO = REGISTRY.register("corazon_dorado", () -> new CorazonDoradoItem());
   public static final RegistryObject<Item> ORBE_AMARILLO = REGISTRY.register("orbe_amarillo", () -> new OrbeAmarilloItem());
   public static final RegistryObject<Item> ORBE_AZUL = REGISTRY.register("orbe_azul", () -> new OrbeAzulItem());
   public static final RegistryObject<Item> ORBE_CYAN = REGISTRY.register("orbe_cyan", () -> new OrbeCyanItem());
   public static final RegistryObject<Item> ORBE_MORADO = REGISTRY.register("orbe_morado", () -> new OrbeMoradoItem());
   public static final RegistryObject<Item> ORBE_NARANJA = REGISTRY.register("orbe_naranja", () -> new OrbeNaranjaItem());
   public static final RegistryObject<Item> ORBE_ROJO = REGISTRY.register("orbe_rojo", () -> new OrbeRojoItem());
   public static final RegistryObject<Item> ORBE_ROSA = REGISTRY.register("orbe_rosa", () -> new OrbeRosaItem());
   public static final RegistryObject<Item> ORBE_VERDE = REGISTRY.register("orbe_verde", () -> new OrbeVerdeItem());
   public static final RegistryObject<Item> ALMA_PERDIDA_SPAWN_EGG = REGISTRY.register(
      "alma_perdida_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.ALMA_PERDIDA, -16711681, -13382401, new Item.Properties())
   );
   public static final RegistryObject<Item> GRIM_REAPER_SPAWN_EGG = REGISTRY.register(
      "grim_reaper_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.GRIM_REAPER, -13421773, -8224134, new Item.Properties())
   );
   public static final RegistryObject<Item> TORRETA_BORG_SPAWN_EGG = REGISTRY.register(
      "torreta_borg_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.TORRETA_BORG, -10066330, -65536, new Item.Properties())
   );
   public static final RegistryObject<Item> SPACESHIP_SPAWN_EGG = REGISTRY.register(
      "spaceship_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.SPACESHIP, -3355444, -1, new Item.Properties())
   );
   public static final RegistryObject<Item> ALA_DERECHA_DE_NAVE = REGISTRY.register("ala_derecha_de_nave", () -> new AlaDerechaDeNaveItem());
   public static final RegistryObject<Item> ALA_DERECHA_FRONTAL_DE_NAVE = REGISTRY.register(
      "ala_derecha_frontal_de_nave", () -> new AlaDerechaFrontalDeNaveItem()
   );
   public static final RegistryObject<Item> ALA_IZQUIERDA_DE_NAVE = REGISTRY.register("ala_izquierda_de_nave", () -> new AlaIzquierdaDeNaveItem());
   public static final RegistryObject<Item> ALA_IZQUIERDA_FRONTAL_DE_NAVE = REGISTRY.register(
      "ala_izquierda_frontal_de_nave", () -> new AlaIzquierdaFrontalDeNaveItem()
   );
   public static final RegistryObject<Item> CABINA_DE_NAVE = REGISTRY.register("cabina_de_nave", () -> new CabinaDeNaveItem());
   public static final RegistryObject<Item> CABINA_FRONTAL_DE_LA_NAVE = REGISTRY.register(
      "cabina_frontal_de_la_nave", () -> new CabinaFrontalDeLaNaveItem()
   );
   public static final RegistryObject<Item> PROPULSOR_DE_DILITIO_DERECHO = REGISTRY.register(
      "propulsor_de_dilitio_derecho", () -> new PropulsorDeDilitioDerechoItem()
   );
   public static final RegistryObject<Item> PROPULSOR_DE_DILITIO_IZQUIERDA = REGISTRY.register(
      "propulsor_de_dilitio_izquierda", () -> new PropulsorDeDilitioIzquierdaItem()
   );
   public static final RegistryObject<Item> RADICORA_SPAWN_EGG = REGISTRY.register(
      "radicora_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.RADICORA, -14469856, -1824030, new Item.Properties())
   );
   public static final RegistryObject<Item> GREAM_REAPER_PEQUENA_SPAWN_EGG = REGISTRY.register(
      "gream_reaper_pequena_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.GREAM_REAPER_PEQUENA, -13027015, -6513806, new Item.Properties())
   );
   public static final RegistryObject<Item> ARMADURA_DIMENSIONAL_HELMET = REGISTRY.register(
      "armadura_dimensional_helmet", () -> new ArmaduraDimensionalItem.Helmet()
   );
   public static final RegistryObject<Item> ARMADURA_DIMENSIONAL_CHESTPLATE = REGISTRY.register(
      "armadura_dimensional_chestplate", () -> new ArmaduraDimensionalItem.Chestplate()
   );
   public static final RegistryObject<Item> ARMADURA_DIMENSIONAL_LEGGINGS = REGISTRY.register(
      "armadura_dimensional_leggings", () -> new ArmaduraDimensionalItem.Leggings()
   );
   public static final RegistryObject<Item> ARMADURA_DIMENSIONAL_BOOTS = REGISTRY.register(
      "armadura_dimensional_boots", () -> new ArmaduraDimensionalItem.Boots()
   );
   public static final RegistryObject<Item> CONO_GORDO = block(ModmindsModBlocks.CONO_GORDO);
   public static final RegistryObject<Item> BLOQUE_GULAG_FINAL = block(ModmindsModBlocks.BLOQUE_GULAG_FINAL);
   public static final RegistryObject<Item> BLOQUE_GULAG_FINAL_2 = block(ModmindsModBlocks.BLOQUE_GULAG_FINAL_2);
   public static final RegistryObject<Item> BLOQUE_GULAG_FINAL_3 = block(ModmindsModBlocks.BLOQUE_GULAG_FINAL_3);
   public static final RegistryObject<Item> SEAT = block(ModmindsModBlocks.SEAT);
   public static final RegistryObject<Item> RING_STAIRS = block(ModmindsModBlocks.RING_STAIRS);
   public static final RegistryObject<Item> ALMA_DE_GOLEM = REGISTRY.register("alma_de_golem", () -> new AlmaDeGolemItem());
   public static final RegistryObject<Item> ESCUDO_TIKI = REGISTRY.register("escudo_tiki", () -> new EscudoTikiItem());
   public static final RegistryObject<Item> VARA_DE_DILITIO = REGISTRY.register("vara_de_dilitio", () -> new VaraDeDilitioItem());
   public static final RegistryObject<Item> BLOQUEADOR_DE_BOMBAS = REGISTRY.register("bloqueador_de_bombas", () -> new BloqueadorDeBombasItem());
   public static final RegistryObject<Item> OJO_DE_LIMBO_SPAWN_EGG = REGISTRY.register(
      "ojo_de_limbo_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.OJO_DE_LIMBO, -3101524, -7066837, new Item.Properties())
   );
   public static final RegistryObject<Item> PATA_DE_SAPO = REGISTRY.register("pata_de_sapo", () -> new PataDeSapoItem());
   public static final RegistryObject<Item> CANDADO_TIKI_SPAWN_EGG = REGISTRY.register(
      "candado_tiki_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.CANDADO_TIKI, -52429, -11720407, new Item.Properties())
   );
   public static final RegistryObject<Item> LLAVE_TIKI_VERDE = REGISTRY.register("llave_tiki_verde", () -> new LlaveTikiVerdeItem());
   public static final RegistryObject<Item> LLAVE_TIKI_ROSA = REGISTRY.register("llave_tiki_rosa", () -> new LlaveTikiRosaItem());
   public static final RegistryObject<Item> LLAVE_TIKI_ROJA = REGISTRY.register("llave_tiki_roja", () -> new LlaveTikiRojaItem());
   public static final RegistryObject<Item> LLAVE_TIKI_AZUL = REGISTRY.register("llave_tiki_azul", () -> new LlaveTikiAzulItem());
   public static final RegistryObject<Item> INTRUSO_SPAWN_EGG = REGISTRY.register(
      "intruso_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.INTRUSO, -9872067, -10794464, new Item.Properties())
   );
   public static final RegistryObject<Item> COLOSO_SPAWN_EGG = REGISTRY.register(
      "coloso_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.COLOSO, -2917252, -10459348, new Item.Properties())
   );
   public static final RegistryObject<Item> REACTOR_BORG_SPAWN_EGG = REGISTRY.register(
      "reactor_borg_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.REACTOR_BORG, -3460839, -677351, new Item.Properties())
   );
   public static final RegistryObject<Item> BLOQUE_ILUMINANTE_BORG_NARANJA = block(ModmindsModBlocks.BLOQUE_ILUMINANTE_BORG_NARANJA);
   public static final RegistryObject<Item> TUBERIA_BORG = block(ModmindsModBlocks.TUBERIA_BORG);
   public static final RegistryObject<Item> BLOQUE_BORG_CON_PATRON = block(ModmindsModBlocks.BLOQUE_BORG_CON_PATRON);
   public static final RegistryObject<Item> BLOQUE_BORG_GRIS_CON_NARANJA = block(ModmindsModBlocks.BLOQUE_BORG_GRIS_CON_NARANJA);
   public static final RegistryObject<Item> REACTOR_HYDRA_SPAWN_EGG = REGISTRY.register(
      "reactor_hydra_spawn_egg", () -> new ForgeSpawnEggItem(ModmindsModEntities.REACTOR_HYDRA, -13434880, -65536, new Item.Properties())
   );
   public static final RegistryObject<Item> OJO_DE_INTRUSO = REGISTRY.register("ojo_de_intruso", () -> new OjoDeIntrusoItem());

   private static RegistryObject<Item> block(RegistryObject<Block> block) {
      return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties()));
   }

   @SubscribeEvent
   public static void clientLoad(FMLClientSetupEvent event) {
      event.enqueueWork(() -> ClientCompat.registerBlocking(ESCUDO_TIKI.get()));
   }
}
