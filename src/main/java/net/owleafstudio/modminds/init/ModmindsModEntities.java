package net.owleafstudio.modminds.init;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.owleafstudio.modminds.entity.AlmaPerdidaEntity;
import net.owleafstudio.modminds.entity.BalaDeDilitioProyectilEntity;
import net.owleafstudio.modminds.entity.BombaAcuaticaGrandeEntity;
import net.owleafstudio.modminds.entity.BombaAcuaticaPequenaEntity;
import net.owleafstudio.modminds.entity.BombaAmarillaEntity;
import net.owleafstudio.modminds.entity.BombaAmarillaSinAlasEntidadEntity;
import net.owleafstudio.modminds.entity.BombaAmarillaSinAlasEntity;
import net.owleafstudio.modminds.entity.BombaAmarillaSinAlasProyectilEntity;
import net.owleafstudio.modminds.entity.BombaNegraEntity;
import net.owleafstudio.modminds.entity.BombaNegraSinAlasEntidadEntity;
import net.owleafstudio.modminds.entity.BombaNegraSinAlasEntity;
import net.owleafstudio.modminds.entity.BombaNegraSinAlasProyectilEntity;
import net.owleafstudio.modminds.entity.BombaRojaEntity;
import net.owleafstudio.modminds.entity.BombaRojaSinAlasEntidadEntity;
import net.owleafstudio.modminds.entity.BombaRojaSinAlasEntity;
import net.owleafstudio.modminds.entity.BombaRojaSinAlasProyectilEntity;
import net.owleafstudio.modminds.entity.BoneSpiderEntity;
import net.owleafstudio.modminds.entity.CandadoTikiEntity;
import net.owleafstudio.modminds.entity.CasinoEntity;
import net.owleafstudio.modminds.entity.ColosoEntity;
import net.owleafstudio.modminds.entity.CreeperCakeEntity;
import net.owleafstudio.modminds.entity.DardoTikiProyectilEntity;
import net.owleafstudio.modminds.entity.FantasmaAmarilloEntity;
import net.owleafstudio.modminds.entity.FantasmaNegroEntity;
import net.owleafstudio.modminds.entity.FantasmaRojoEntity;
import net.owleafstudio.modminds.entity.FlashBangEntity;
import net.owleafstudio.modminds.entity.GloboEntity;
import net.owleafstudio.modminds.entity.GorgonaEntity;
import net.owleafstudio.modminds.entity.GreamReaperPequenaEntity;
import net.owleafstudio.modminds.entity.GrimReaperEntity;
import net.owleafstudio.modminds.entity.HydraEntity;
import net.owleafstudio.modminds.entity.InfernalBullEntity;
import net.owleafstudio.modminds.entity.IntrusoEntity;
import net.owleafstudio.modminds.entity.LimboTitanEntity;
import net.owleafstudio.modminds.entity.MurcielagoWardenEntity;
import net.owleafstudio.modminds.entity.NaveEnemigaAcorazadaBorgEntity;
import net.owleafstudio.modminds.entity.NaveEnemigaAcorazadaRojaBorgEntity;
import net.owleafstudio.modminds.entity.NaveEnemigaAcorazadaRojaProyectilEntity;
import net.owleafstudio.modminds.entity.NaveEnemigaAmarillaAcorazadaBorgEntity;
import net.owleafstudio.modminds.entity.NaveEnemigaAmarillaAcorazadaProyectilEntity;
import net.owleafstudio.modminds.entity.NaveEnemigaAtomizadoraDeAstrosBorgEntity;
import net.owleafstudio.modminds.entity.NaveEnemigaAtomizadoraProyectilEntity;
import net.owleafstudio.modminds.entity.NaveEnemigaAzulClaroBorgEntity;
import net.owleafstudio.modminds.entity.NaveEnemigaAzulClaroProyectilEntity;
import net.owleafstudio.modminds.entity.NaveEnemigaCasitaBorgEntity;
import net.owleafstudio.modminds.entity.NaveEnemigaCasitaProyectilEntity;
import net.owleafstudio.modminds.entity.NaveEnemigaCortaBorgEntity;
import net.owleafstudio.modminds.entity.NaveEnemigaCortaProyectilEntity;
import net.owleafstudio.modminds.entity.NaveEnemigaDeAcorazadaProyectilEntity;
import net.owleafstudio.modminds.entity.NaveEnemigaDePulsoBorgEntity;
import net.owleafstudio.modminds.entity.NaveEnemigaDePulsoProyectilEntity;
import net.owleafstudio.modminds.entity.NaveEnemigaPerforadoraDelVacioBorgEntity;
import net.owleafstudio.modminds.entity.NaveEnemigaPerforadoraDelVacioProyectilEntity;
import net.owleafstudio.modminds.entity.NaveEnemigaTanqueBorgEntity;
import net.owleafstudio.modminds.entity.NaveEnemigaTripulsadoraDeIonesBorgEntity;
import net.owleafstudio.modminds.entity.NereidaAguaEntity;
import net.owleafstudio.modminds.entity.NereidaEntity;
import net.owleafstudio.modminds.entity.OjoDeLimboEntity;
import net.owleafstudio.modminds.entity.OrbEntity;
import net.owleafstudio.modminds.entity.PiglinDelLimboEntity;
import net.owleafstudio.modminds.entity.PuertaBorgAmarilloEntity;
import net.owleafstudio.modminds.entity.PuertaBorgAzulEntity;
import net.owleafstudio.modminds.entity.PuertaBorgMoradaEntity;
import net.owleafstudio.modminds.entity.PuertaBorgRojaEntity;
import net.owleafstudio.modminds.entity.PuertaBorgRosaEntity;
import net.owleafstudio.modminds.entity.PuertaBorgVerdeEntity;
import net.owleafstudio.modminds.entity.RadicoraEntity;
import net.owleafstudio.modminds.entity.ReactorBorgEntity;
import net.owleafstudio.modminds.entity.ReactorEntity;
import net.owleafstudio.modminds.entity.ReactorHydraEntity;
import net.owleafstudio.modminds.entity.ReviilEntity;
import net.owleafstudio.modminds.entity.SapitoEntity;
import net.owleafstudio.modminds.entity.SapoEntity;
import net.owleafstudio.modminds.entity.SerpienteEntity;
import net.owleafstudio.modminds.entity.ShootReviilEntity;
import net.owleafstudio.modminds.entity.SoldadoReviilEntity;
import net.owleafstudio.modminds.entity.SpaceshipEntity;
import net.owleafstudio.modminds.entity.Taxi23DeCuatroAsientosEntity;
import net.owleafstudio.modminds.entity.Taxi23Entity;
import net.owleafstudio.modminds.entity.TikiGuardianEntity;
import net.owleafstudio.modminds.entity.TorretaBorgEntity;
import net.owleafstudio.modminds.entity.TorretaTikiEntity;
import net.owleafstudio.modminds.entity.TortugaNetheritaEntity;
import net.owleafstudio.modminds.entity.TridenteProyectilEntity;
import net.owleafstudio.modminds.entity.WardenMounstrosityEntity;
import net.owleafstudio.modminds.entity.WaterChargeEntidadEntity;
import net.owleafstudio.modminds.entity.WaterChargeEntity;
import net.owleafstudio.modminds.entity.WildFireEntity;
import net.owleafstudio.modminds.entity.ZonaPeligroEntity;

@Mod.EventBusSubscriber(
   bus = Mod.EventBusSubscriber.Bus.MOD
)
public class ModmindsModEntities {
   public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, "modminds");
   public static final RegistryObject<EntityType<CasinoEntity>> CASINO = register(
      "casino",
      EntityType.Builder.<CasinoEntity>of((_type, _level) -> new CasinoEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .fireImmune()
         .sized(0.6F, 1.8F)
   );
   public static final RegistryObject<EntityType<TortugaNetheritaEntity>> TORTUGA_NETHERITA = register(
      "tortuga_netherita",
      EntityType.Builder.<TortugaNetheritaEntity>of((_type, _level) -> new TortugaNetheritaEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .sized(1.5F, 1.8F)
   );
   public static final RegistryObject<EntityType<SapitoEntity>> SAPITO = register(
      "sapito",
      EntityType.Builder.<SapitoEntity>of((_type, _level) -> new SapitoEntity(_type, _level), MobCategory.CREATURE)
         .clientTrackingRange(64)
         .updateInterval(3)
         .sized(0.6F, 0.4F)
   );
   public static final RegistryObject<EntityType<GorgonaEntity>> GORGONA = register(
      "gorgona",
      EntityType.Builder.<GorgonaEntity>of((_type, _level) -> new GorgonaEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .sized(0.6F, 2.8F)
   );
   public static final RegistryObject<EntityType<BombaNegraEntity>> BOMBA_NEGRA = register(
      "bomba_negra",
      EntityType.Builder.<BombaNegraEntity>of((_type, _level) -> new BombaNegraEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .sized(0.6F, 0.5F)
   );
   public static final RegistryObject<EntityType<BombaNegraSinAlasEntity>> BOMBA_NEGRA_SIN_ALAS = register(
      "bomba_negra_sin_alas",
      EntityType.Builder.<BombaNegraSinAlasEntity>of((_type, _level) -> new BombaNegraSinAlasEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .fireImmune()
         .sized(0.6F, 0.5F)
   );
   public static final RegistryObject<EntityType<InfernalBullEntity>> INFERNAL_BULL = register(
      "infernal_bull",
      EntityType.Builder.<InfernalBullEntity>of((_type, _level) -> new InfernalBullEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .fireImmune()
         .sized(2.0F, 2.0F)
   );
   public static final RegistryObject<EntityType<MurcielagoWardenEntity>> MURCIELAGO_WARDEN = register(
      "murcielago_warden",
      EntityType.Builder.<MurcielagoWardenEntity>of((_type, _level) -> new MurcielagoWardenEntity(_type, _level), MobCategory.CREATURE)
         .clientTrackingRange(100)
         .updateInterval(3)
         .sized(0.6F, 1.4F)
   );
   public static final RegistryObject<EntityType<TridenteProyectilEntity>> TRIDENTE_PROYECTIL = register(
      "tridente_proyectil",
      EntityType.Builder.<TridenteProyectilEntity>of((_type, _level) -> new TridenteProyectilEntity(_type, _level), MobCategory.MISC)
         .clientTrackingRange(64)
         .updateInterval(1)
         .sized(0.5F, 0.5F)
   );
   public static final RegistryObject<EntityType<Taxi23Entity>> TAXI_23 = register(
      "taxi_23",
      EntityType.Builder.<Taxi23Entity>of((_type, _level) -> new Taxi23Entity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .sized(0.6F, 1.8F)
   );
   public static final RegistryObject<EntityType<Taxi23DeCuatroAsientosEntity>> TAXI_23_DE_CUATRO_ASIENTOS = register(
      "taxi_23_de_cuatro_asientos",
      EntityType.Builder.<Taxi23DeCuatroAsientosEntity>of((_type, _level) -> new Taxi23DeCuatroAsientosEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .sized(0.6F, 1.8F)
   );
   public static final RegistryObject<EntityType<NaveEnemigaDePulsoBorgEntity>> NAVE_ENEMIGA_DE_PULSO_BORG = register(
      "nave_enemiga_de_pulso_borg",
      EntityType.Builder.<NaveEnemigaDePulsoBorgEntity>of((_type, _level) -> new NaveEnemigaDePulsoBorgEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .sized(0.6F, 1.8F)
   );
   public static final RegistryObject<EntityType<NaveEnemigaDePulsoProyectilEntity>> NAVE_ENEMIGA_DE_PULSO_PROYECTIL = register(
      "nave_enemiga_de_pulso_proyectil",
      EntityType.Builder.<NaveEnemigaDePulsoProyectilEntity>of((_type, _level) -> new NaveEnemigaDePulsoProyectilEntity(_type, _level), MobCategory.MISC)
         .clientTrackingRange(64)
         .updateInterval(1)
         .sized(0.3F, 0.3F)
   );
   public static final RegistryObject<EntityType<NaveEnemigaTanqueBorgEntity>> NAVE_ENEMIGA_TANQUE_BORG = register(
      "nave_enemiga_tanque_borg",
      EntityType.Builder.<NaveEnemigaTanqueBorgEntity>of((_type, _level) -> new NaveEnemigaTanqueBorgEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .sized(0.6F, 1.8F)
   );
   public static final RegistryObject<EntityType<NaveEnemigaAcorazadaBorgEntity>> NAVE_ENEMIGA_ACORAZADA_BORG = register(
      "nave_enemiga_acorazada_borg",
      EntityType.Builder.<NaveEnemigaAcorazadaBorgEntity>of((_type, _level) -> new NaveEnemigaAcorazadaBorgEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .sized(0.6F, 1.8F)
   );
   public static final RegistryObject<EntityType<NaveEnemigaDeAcorazadaProyectilEntity>> NAVE_ENEMIGA_DE_ACORAZADA_PROYECTIL = register(
      "nave_enemiga_de_acorazada_proyectil",
      EntityType.Builder.<NaveEnemigaDeAcorazadaProyectilEntity>of((_type, _level) -> new NaveEnemigaDeAcorazadaProyectilEntity(_type, _level), MobCategory.MISC)
         .clientTrackingRange(64)
         .updateInterval(1)
         .sized(0.3F, 0.3F)
   );
   public static final RegistryObject<EntityType<NaveEnemigaAtomizadoraDeAstrosBorgEntity>> NAVE_ENEMIGA_ATOMIZADORA_DE_ASTROS_BORG = register(
      "nave_enemiga_atomizadora_de_astros_borg",
      EntityType.Builder.<NaveEnemigaAtomizadoraDeAstrosBorgEntity>of((_type, _level) -> new NaveEnemigaAtomizadoraDeAstrosBorgEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .sized(0.6F, 1.8F)
   );
   public static final RegistryObject<EntityType<NaveEnemigaAtomizadoraProyectilEntity>> NAVE_ENEMIGA_ATOMIZADORA_PROYECTIL = register(
      "nave_enemiga_atomizadora_proyectil",
      EntityType.Builder.<NaveEnemigaAtomizadoraProyectilEntity>of((_type, _level) -> new NaveEnemigaAtomizadoraProyectilEntity(_type, _level), MobCategory.MISC)
         .clientTrackingRange(64)
         .updateInterval(1)
         .sized(0.3F, 0.3F)
   );
   public static final RegistryObject<EntityType<NaveEnemigaCortaBorgEntity>> NAVE_ENEMIGA_CORTA_BORG = register(
      "nave_enemiga_corta_borg",
      EntityType.Builder.<NaveEnemigaCortaBorgEntity>of((_type, _level) -> new NaveEnemigaCortaBorgEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .sized(0.6F, 1.8F)
   );
   public static final RegistryObject<EntityType<NaveEnemigaCortaProyectilEntity>> NAVE_ENEMIGA_CORTA_PROYECTIL = register(
      "nave_enemiga_corta_proyectil",
      EntityType.Builder.<NaveEnemigaCortaProyectilEntity>of((_type, _level) -> new NaveEnemigaCortaProyectilEntity(_type, _level), MobCategory.MISC)
         .clientTrackingRange(64)
         .updateInterval(1)
         .sized(0.3F, 0.3F)
   );
   public static final RegistryObject<EntityType<NaveEnemigaAmarillaAcorazadaBorgEntity>> NAVE_ENEMIGA_AMARILLA_ACORAZADA_BORG = register(
      "nave_enemiga_amarilla_acorazada_borg",
      EntityType.Builder.<NaveEnemigaAmarillaAcorazadaBorgEntity>of((_type, _level) -> new NaveEnemigaAmarillaAcorazadaBorgEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .sized(0.6F, 1.8F)
   );
   public static final RegistryObject<EntityType<NaveEnemigaAmarillaAcorazadaProyectilEntity>> NAVE_ENEMIGA_AMARILLA_ACORAZADA_PROYECTIL = register(
      "nave_enemiga_amarilla_acorazada_proyectil",
      EntityType.Builder.<NaveEnemigaAmarillaAcorazadaProyectilEntity>of((_type, _level) -> new NaveEnemigaAmarillaAcorazadaProyectilEntity(_type, _level), MobCategory.MISC)
         .clientTrackingRange(64)
         .updateInterval(1)
         .sized(0.3F, 0.3F)
   );
   public static final RegistryObject<EntityType<NaveEnemigaPerforadoraDelVacioBorgEntity>> NAVE_ENEMIGA_PERFORADORA_DEL_VACIO_BORG = register(
      "nave_enemiga_perforadora_del_vacio_borg",
      EntityType.Builder.<NaveEnemigaPerforadoraDelVacioBorgEntity>of((_type, _level) -> new NaveEnemigaPerforadoraDelVacioBorgEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .sized(0.6F, 1.8F)
   );
   public static final RegistryObject<EntityType<NaveEnemigaPerforadoraDelVacioProyectilEntity>> NAVE_ENEMIGA_PERFORADORA_DEL_VACIO_PROYECTIL = register(
      "nave_enemiga_perforadora_del_vacio_proyectil",
      EntityType.Builder.<NaveEnemigaPerforadoraDelVacioProyectilEntity>of((_type, _level) -> new NaveEnemigaPerforadoraDelVacioProyectilEntity(_type, _level), MobCategory.MISC)
         .clientTrackingRange(64)
         .updateInterval(1)
         .sized(0.3F, 0.3F)
   );
   public static final RegistryObject<EntityType<NaveEnemigaAcorazadaRojaBorgEntity>> NAVE_ENEMIGA_ACORAZADA_ROJA_BORG = register(
      "nave_enemiga_acorazada_roja_borg",
      EntityType.Builder.<NaveEnemigaAcorazadaRojaBorgEntity>of((_type, _level) -> new NaveEnemigaAcorazadaRojaBorgEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .sized(0.6F, 1.8F)
   );
   public static final RegistryObject<EntityType<NaveEnemigaAcorazadaRojaProyectilEntity>> NAVE_ENEMIGA_ACORAZADA_ROJA_PROYECTIL = register(
      "nave_enemiga_acorazada_roja_proyectil",
      EntityType.Builder.<NaveEnemigaAcorazadaRojaProyectilEntity>of((_type, _level) -> new NaveEnemigaAcorazadaRojaProyectilEntity(_type, _level), MobCategory.MISC)
         .clientTrackingRange(64)
         .updateInterval(1)
         .sized(0.3F, 0.3F)
   );
   public static final RegistryObject<EntityType<NaveEnemigaTripulsadoraDeIonesBorgEntity>> NAVE_ENEMIGA_TRIPULSADORA_DE_IONES_BORG = register(
      "nave_enemiga_tripulsadora_de_iones_borg",
      EntityType.Builder.<NaveEnemigaTripulsadoraDeIonesBorgEntity>of((_type, _level) -> new NaveEnemigaTripulsadoraDeIonesBorgEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .sized(0.6F, 1.8F)
   );
   public static final RegistryObject<EntityType<NaveEnemigaCasitaBorgEntity>> NAVE_ENEMIGA_CASITA_BORG = register(
      "nave_enemiga_casita_borg",
      EntityType.Builder.<NaveEnemigaCasitaBorgEntity>of((_type, _level) -> new NaveEnemigaCasitaBorgEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .sized(0.6F, 1.8F)
   );
   public static final RegistryObject<EntityType<NaveEnemigaCasitaProyectilEntity>> NAVE_ENEMIGA_CASITA_PROYECTIL = register(
      "nave_enemiga_casita_proyectil",
      EntityType.Builder.<NaveEnemigaCasitaProyectilEntity>of((_type, _level) -> new NaveEnemigaCasitaProyectilEntity(_type, _level), MobCategory.MISC)
         .clientTrackingRange(64)
         .updateInterval(1)
         .sized(0.3F, 0.3F)
   );
   public static final RegistryObject<EntityType<NaveEnemigaAzulClaroBorgEntity>> NAVE_ENEMIGA_AZUL_CLARO_BORG = register(
      "nave_enemiga_azul_claro_borg",
      EntityType.Builder.<NaveEnemigaAzulClaroBorgEntity>of((_type, _level) -> new NaveEnemigaAzulClaroBorgEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .sized(0.6F, 1.8F)
   );
   public static final RegistryObject<EntityType<NaveEnemigaAzulClaroProyectilEntity>> NAVE_ENEMIGA_AZUL_CLARO_PROYECTIL = register(
      "nave_enemiga_azul_claro_proyectil",
      EntityType.Builder.<NaveEnemigaAzulClaroProyectilEntity>of((_type, _level) -> new NaveEnemigaAzulClaroProyectilEntity(_type, _level), MobCategory.MISC)
         .clientTrackingRange(64)
         .updateInterval(1)
         .sized(0.3F, 0.3F)
   );
   public static final RegistryObject<EntityType<BalaDeDilitioProyectilEntity>> BALA_DE_DILITIO_PROYECTIL = register(
      "bala_de_dilitio_proyectil",
      EntityType.Builder.<BalaDeDilitioProyectilEntity>of((_type, _level) -> new BalaDeDilitioProyectilEntity(_type, _level), MobCategory.MISC)
         .clientTrackingRange(64)
         .updateInterval(1)
         .sized(0.3F, 0.3F)
   );
   public static final RegistryObject<EntityType<DardoTikiProyectilEntity>> DARDO_TIKI_PROYECTIL = register(
      "dardo_tiki_proyectil",
      EntityType.Builder.<DardoTikiProyectilEntity>of((_type, _level) -> new DardoTikiProyectilEntity(_type, _level), MobCategory.MISC)
         .clientTrackingRange(64)
         .updateInterval(1)
         .sized(0.5F, 0.5F)
   );
   public static final RegistryObject<EntityType<ZonaPeligroEntity>> ZONA_PELIGRO = register(
      "zona_peligro",
      EntityType.Builder.<ZonaPeligroEntity>of((_type, _level) -> new ZonaPeligroEntity(_type, _level), MobCategory.MISC)
         .sized(0.1F, 0.1F)
         .clientTrackingRange(16)
         .updateInterval(20)
         .noSave()
         .noSummon()
   );
   public static final RegistryObject<EntityType<SerpienteEntity>> SERPIENTE = register(
      "serpiente",
      EntityType.Builder.<SerpienteEntity>of((_type, _level) -> new SerpienteEntity(_type, _level), MobCategory.CREATURE)
         .clientTrackingRange(64)
         .updateInterval(3)
         .sized(0.6F, 0.4F)
   );
   public static final RegistryObject<EntityType<SapoEntity>> SAPO = register(
      "sapo",
      EntityType.Builder.<SapoEntity>of((_type, _level) -> new SapoEntity(_type, _level), MobCategory.MONSTER).clientTrackingRange(64).updateInterval(3).sized(2.0F, 3.0F)
   );
   public static final RegistryObject<EntityType<FantasmaRojoEntity>> FANTASMA_ROJO = register(
      "fantasma_rojo",
      EntityType.Builder.<FantasmaRojoEntity>of((_type, _level) -> new FantasmaRojoEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .sized(1.5F, 1.6F)
   );
   public static final RegistryObject<EntityType<PuertaBorgVerdeEntity>> PUERTA_BORG_VERDE = register(
      "puerta_borg_verde",
      EntityType.Builder.<PuertaBorgVerdeEntity>of((_type, _level) -> new PuertaBorgVerdeEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .sized(5.12F, 5.12F)
   );
   public static final RegistryObject<EntityType<FantasmaAmarilloEntity>> FANTASMA_AMARILLO = register(
      "fantasma_amarillo",
      EntityType.Builder.<FantasmaAmarilloEntity>of((_type, _level) -> new FantasmaAmarilloEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .sized(1.5F, 1.6F)
   );
   public static final RegistryObject<EntityType<FantasmaNegroEntity>> FANTASMA_NEGRO = register(
      "fantasma_negro",
      EntityType.Builder.<FantasmaNegroEntity>of((_type, _level) -> new FantasmaNegroEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .sized(1.5F, 1.6F)
   );
   public static final RegistryObject<EntityType<PuertaBorgAmarilloEntity>> PUERTA_BORG_AMARILLO = register(
      "puerta_borg_amarillo",
      EntityType.Builder.<PuertaBorgAmarilloEntity>of((_type, _level) -> new PuertaBorgAmarilloEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .sized(5.12F, 5.12F)
   );
   public static final RegistryObject<EntityType<PuertaBorgRosaEntity>> PUERTA_BORG_ROSA = register(
      "puerta_borg_rosa",
      EntityType.Builder.<PuertaBorgRosaEntity>of((_type, _level) -> new PuertaBorgRosaEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .sized(5.12F, 5.12F)
   );
   public static final RegistryObject<EntityType<PuertaBorgAzulEntity>> PUERTA_BORG_AZUL = register(
      "puerta_borg_azul",
      EntityType.Builder.<PuertaBorgAzulEntity>of((_type, _level) -> new PuertaBorgAzulEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .sized(5.12F, 5.12F)
   );
   public static final RegistryObject<EntityType<PuertaBorgRojaEntity>> PUERTA_BORG_ROJA = register(
      "puerta_borg_roja",
      EntityType.Builder.<PuertaBorgRojaEntity>of((_type, _level) -> new PuertaBorgRojaEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .sized(5.12F, 5.12F)
   );
   public static final RegistryObject<EntityType<PuertaBorgMoradaEntity>> PUERTA_BORG_MORADA = register(
      "puerta_borg_morada",
      EntityType.Builder.<PuertaBorgMoradaEntity>of((_type, _level) -> new PuertaBorgMoradaEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .sized(5.12F, 5.12F)
   );
   public static final RegistryObject<EntityType<BombaNegraSinAlasProyectilEntity>> BOMBA_NEGRA_SIN_ALAS_PROYECTIL = register(
      "bomba_negra_sin_alas_proyectil",
      EntityType.Builder.<BombaNegraSinAlasProyectilEntity>of((_type, _level) -> new BombaNegraSinAlasProyectilEntity(_type, _level), MobCategory.MISC)
         .clientTrackingRange(64)
         .updateInterval(1)
         .sized(0.5F, 0.5F)
   );
   public static final RegistryObject<EntityType<BombaNegraSinAlasEntidadEntity>> BOMBA_NEGRA_SIN_ALAS_ENTIDAD = register(
      "bomba_negra_sin_alas_entidad",
      EntityType.Builder.<BombaNegraSinAlasEntidadEntity>of((_type, _level) -> new BombaNegraSinAlasEntidadEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .fireImmune()
         .sized(0.6F, 0.6F)
   );
   public static final RegistryObject<EntityType<BombaRojaEntity>> BOMBA_ROJA = register(
      "bomba_roja",
      EntityType.Builder.<BombaRojaEntity>of((_type, _level) -> new BombaRojaEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(120)
         .updateInterval(3)
         .fireImmune()
         .sized(0.6F, 0.6F)
   );
   public static final RegistryObject<EntityType<BombaRojaSinAlasEntity>> BOMBA_ROJA_SIN_ALAS = register(
      "bomba_roja_sin_alas",
      EntityType.Builder.<BombaRojaSinAlasEntity>of((_type, _level) -> new BombaRojaSinAlasEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .fireImmune()
         .sized(0.6F, 0.6F)
   );
   public static final RegistryObject<EntityType<BombaRojaSinAlasEntidadEntity>> BOMBA_ROJA_SIN_ALAS_ENTIDAD = register(
      "bomba_roja_sin_alas_entidad",
      EntityType.Builder.<BombaRojaSinAlasEntidadEntity>of((_type, _level) -> new BombaRojaSinAlasEntidadEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .fireImmune()
         .sized(0.6F, 0.6F)
   );
   public static final RegistryObject<EntityType<BombaRojaSinAlasProyectilEntity>> BOMBA_ROJA_SIN_ALAS_PROYECTIL = register(
      "bomba_roja_sin_alas_proyectil",
      EntityType.Builder.<BombaRojaSinAlasProyectilEntity>of((_type, _level) -> new BombaRojaSinAlasProyectilEntity(_type, _level), MobCategory.MISC)
         .clientTrackingRange(64)
         .updateInterval(1)
         .sized(0.5F, 0.5F)
   );
   public static final RegistryObject<EntityType<BombaAmarillaEntity>> BOMBA_AMARILLA = register(
      "bomba_amarilla",
      EntityType.Builder.<BombaAmarillaEntity>of((_type, _level) -> new BombaAmarillaEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(120)
         .updateInterval(3)
         .fireImmune()
         .sized(0.6F, 0.6F)
   );
   public static final RegistryObject<EntityType<BombaAmarillaSinAlasEntity>> BOMBA_AMARILLA_SIN_ALAS = register(
      "bomba_amarilla_sin_alas",
      EntityType.Builder.<BombaAmarillaSinAlasEntity>of((_type, _level) -> new BombaAmarillaSinAlasEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .fireImmune()
         .sized(0.6F, 0.6F)
   );
   public static final RegistryObject<EntityType<BombaAmarillaSinAlasEntidadEntity>> BOMBA_AMARILLA_SIN_ALAS_ENTIDAD = register(
      "bomba_amarilla_sin_alas_entidad",
      EntityType.Builder.<BombaAmarillaSinAlasEntidadEntity>of((_type, _level) -> new BombaAmarillaSinAlasEntidadEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .fireImmune()
         .sized(0.6F, 0.6F)
   );
   public static final RegistryObject<EntityType<BombaAmarillaSinAlasProyectilEntity>> BOMBA_AMARILLA_SIN_ALAS_PROYECTIL = register(
      "bomba_amarilla_sin_alas_proyectil",
      EntityType.Builder.<BombaAmarillaSinAlasProyectilEntity>of((_type, _level) -> new BombaAmarillaSinAlasProyectilEntity(_type, _level), MobCategory.MISC)
         .clientTrackingRange(64)
         .updateInterval(1)
         .sized(0.5F, 0.5F)
   );
   public static final RegistryObject<EntityType<CreeperCakeEntity>> CREEPER_CAKE = register(
      "creeper_cake",
      EntityType.Builder.<CreeperCakeEntity>of((_type, _level) -> new CreeperCakeEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .sized(0.6F, 1.5F)
   );
   public static final RegistryObject<EntityType<FlashBangEntity>> FLASH_BANG = register(
      "flash_bang",
      EntityType.Builder.<FlashBangEntity>of((_type, _level) -> new FlashBangEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .sized(0.6F, 1.8F)
   );
   public static final RegistryObject<EntityType<BombaAcuaticaGrandeEntity>> BOMBA_ACUATICA_GRANDE = register(
      "bomba_acuatica_grande",
      EntityType.Builder.<BombaAcuaticaGrandeEntity>of((_type, _level) -> new BombaAcuaticaGrandeEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .fireImmune()
         .sized(2.7F, 2.7F)
   );
   public static final RegistryObject<EntityType<BombaAcuaticaPequenaEntity>> BOMBA_ACUATICA_PEQUENA = register(
      "bomba_acuatica_pequena",
      EntityType.Builder.<BombaAcuaticaPequenaEntity>of((_type, _level) -> new BombaAcuaticaPequenaEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .fireImmune()
         .sized(1.3F, 1.3F)
   );
   public static final RegistryObject<EntityType<WildFireEntity>> WILD_FIRE = register(
      "wild_fire",
      EntityType.Builder.<WildFireEntity>of((_type, _level) -> new WildFireEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .fireImmune()
         .sized(0.8F, 1.6F)
   );
   public static final RegistryObject<EntityType<WardenMounstrosityEntity>> WARDEN_MOUNSTROSITY = register(
      "warden_mounstrosity",
      EntityType.Builder.<WardenMounstrosityEntity>of((_type, _level) -> new WardenMounstrosityEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(32)
         .updateInterval(3)
         .sized(3.0F, 3.0F)
   );
   public static final RegistryObject<EntityType<PiglinDelLimboEntity>> PIGLIN_DEL_LIMBO = register(
      "piglin_del_limbo",
      EntityType.Builder.<PiglinDelLimboEntity>of((_type, _level) -> new PiglinDelLimboEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .sized(0.7F, 1.95F)
   );
   public static final RegistryObject<EntityType<BoneSpiderEntity>> BONE_SPIDER = register(
      "bone_spider",
      EntityType.Builder.<BoneSpiderEntity>of((_type, _level) -> new BoneSpiderEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .sized(1.4F, 0.9F)
   );
   public static final RegistryObject<EntityType<ReactorEntity>> REACTOR = register(
      "reactor",
      EntityType.Builder.<ReactorEntity>of((_type, _level) -> new ReactorEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .fireImmune()
         .sized(4.0F, 4.5F)
   );
   public static final RegistryObject<EntityType<TikiGuardianEntity>> TIKI_GUARDIAN = register(
      "tiki_guardian",
      EntityType.Builder.<TikiGuardianEntity>of((_type, _level) -> new TikiGuardianEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(128)
         .updateInterval(3)
         .sized(3.0F, 6.0F)
   );
   public static final RegistryObject<EntityType<SoldadoReviilEntity>> SOLDADO_REVIIL = register(
      "soldado_reviil",
      EntityType.Builder.<SoldadoReviilEntity>of((_type, _level) -> new SoldadoReviilEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .sized(1.3F, 3.4F)
   );
   public static final RegistryObject<EntityType<GloboEntity>> GLOBO = register(
      "globo",
      EntityType.Builder.<GloboEntity>of((_type, _level) -> new GloboEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .fireImmune()
         .sized(3.0F, 9.0F)
   );
   public static final RegistryObject<EntityType<HydraEntity>> HYDRA = register(
      "hydra",
      EntityType.Builder.<HydraEntity>of((_type, _level) -> new HydraEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(128)
         .updateInterval(3)
         .fireImmune()
         .sized(3.0F, 24.0F)
   );
   public static final RegistryObject<EntityType<WaterChargeEntity>> WATER_CHARGE = register(
      "water_charge",
      EntityType.Builder.<WaterChargeEntity>of((_type, _level) -> new WaterChargeEntity(_type, _level), MobCategory.MISC)
         .clientTrackingRange(64)
         .updateInterval(1)
         .sized(0.5F, 0.5F)
   );
   public static final RegistryObject<EntityType<WaterChargeEntidadEntity>> WATER_CHARGE_ENTIDAD = register(
      "water_charge_entidad",
      EntityType.Builder.<WaterChargeEntidadEntity>of((_type, _level) -> new WaterChargeEntidadEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .fireImmune()
         .sized(0.2F, 0.2F)
   );
   public static final RegistryObject<EntityType<LimboTitanEntity>> LIMBO_TITAN = register(
      "limbo_titan",
      EntityType.Builder.<LimboTitanEntity>of((_type, _level) -> new LimboTitanEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(180)
         .updateInterval(3)
         .fireImmune()
         .sized(3.3F, 9.0F)
   );
   public static final RegistryObject<EntityType<ReviilEntity>> REVIIL = register(
      "reviil",
      EntityType.Builder.<ReviilEntity>of((_type, _level) -> new ReviilEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(200)
         .updateInterval(3)
         .fireImmune()
         .sized(3.0F, 4.5F)
   );
   public static final RegistryObject<EntityType<NereidaEntity>> NEREIDA = register(
      "nereida",
      EntityType.Builder.<NereidaEntity>of((_type, _level) -> new NereidaEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .sized(0.6F, 3.0F)
   );
   public static final RegistryObject<EntityType<NereidaAguaEntity>> NEREIDA_AGUA = register(
      "nereida_agua",
      EntityType.Builder.<NereidaAguaEntity>of((_type, _level) -> new NereidaAguaEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .sized(0.7F, 1.3F)
   );
   public static final RegistryObject<EntityType<ShootReviilEntity>> SHOOT_REVIIL = register(
      "shoot_reviil",
      EntityType.Builder.<ShootReviilEntity>of((_type, _level) -> new ShootReviilEntity(_type, _level), MobCategory.MISC)
         .clientTrackingRange(64)
         .updateInterval(1)
         .sized(0.5F, 0.5F)
   );
   public static final RegistryObject<EntityType<TorretaTikiEntity>> TORRETA_TIKI = register(
      "torreta_tiki",
      EntityType.Builder.<TorretaTikiEntity>of((_type, _level) -> new TorretaTikiEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(100)
         .updateInterval(3)
         .fireImmune()
         .sized(0.9F, 1.0F)
   );
   public static final RegistryObject<EntityType<OrbEntity>> ORB = register(
      "orb",
      EntityType.Builder.<OrbEntity>of((_type, _level) -> new OrbEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .fireImmune()
         .sized(0.8F, 0.8F)
   );
   public static final RegistryObject<EntityType<AlmaPerdidaEntity>> ALMA_PERDIDA = register(
      "alma_perdida",
      EntityType.Builder.<AlmaPerdidaEntity>of((_type, _level) -> new AlmaPerdidaEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .fireImmune()
         .sized(0.6F, 1.8F)
   );
   public static final RegistryObject<EntityType<GrimReaperEntity>> GRIM_REAPER = register(
      "grim_reaper",
      EntityType.Builder.<GrimReaperEntity>of((_type, _level) -> new GrimReaperEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .sized(1.2F, 4.1F)
   );
   public static final RegistryObject<EntityType<TorretaBorgEntity>> TORRETA_BORG = register(
      "torreta_borg",
      EntityType.Builder.<TorretaBorgEntity>of((_type, _level) -> new TorretaBorgEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .fireImmune()
         .sized(0.6F, 1.0F)
   );
   public static final RegistryObject<EntityType<SpaceshipEntity>> SPACESHIP = register(
      "spaceship",
      EntityType.Builder.<SpaceshipEntity>of((_type, _level) -> new SpaceshipEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .fireImmune()
         .sized(1.2F, 1.2F)
   );
   public static final RegistryObject<EntityType<RadicoraEntity>> RADICORA = register(
      "radicora",
      EntityType.Builder.<RadicoraEntity>of((_type, _level) -> new RadicoraEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .sized(2.7F, 3.0F)
   );
   public static final RegistryObject<EntityType<GreamReaperPequenaEntity>> GREAM_REAPER_PEQUENA = register(
      "gream_reaper_pequena",
      EntityType.Builder.<GreamReaperPequenaEntity>of((_type, _level) -> new GreamReaperPequenaEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .sized(0.6F, 1.2F)
   );
   public static final RegistryObject<EntityType<OjoDeLimboEntity>> OJO_DE_LIMBO = register(
      "ojo_de_limbo",
      EntityType.Builder.<OjoDeLimboEntity>of((_type, _level) -> new OjoDeLimboEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(32)
         .updateInterval(3)
         .fireImmune()
         .sized(1.0F, 1.0F)
   );
   public static final RegistryObject<EntityType<CandadoTikiEntity>> CANDADO_TIKI = register(
      "candado_tiki",
      EntityType.Builder.<CandadoTikiEntity>of((_type, _level) -> new CandadoTikiEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .fireImmune()
         .sized(1.3F, 1.3F)
   );
   public static final RegistryObject<EntityType<IntrusoEntity>> INTRUSO = register(
      "intruso",
      EntityType.Builder.<IntrusoEntity>of((_type, _level) -> new IntrusoEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .sized(5.0F, 6.25F)
   );
   public static final RegistryObject<EntityType<ColosoEntity>> COLOSO = register(
      "coloso",
      EntityType.Builder.<ColosoEntity>of((_type, _level) -> new ColosoEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .sized(5.0F, 2.7F)
   );
   public static final RegistryObject<EntityType<ReactorBorgEntity>> REACTOR_BORG = register(
      "reactor_borg",
      EntityType.Builder.<ReactorBorgEntity>of((_type, _level) -> new ReactorBorgEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .sized(3.0F, 3.0F)
   );
   public static final RegistryObject<EntityType<ReactorHydraEntity>> REACTOR_HYDRA = register(
      "reactor_hydra",
      EntityType.Builder.<ReactorHydraEntity>of((_type, _level) -> new ReactorHydraEntity(_type, _level), MobCategory.MONSTER)
         .clientTrackingRange(64)
         .updateInterval(3)
         .fireImmune()
         .sized(5.0F, 4.0F)
   );

   private static <T extends Entity> RegistryObject<EntityType<T>> register(String registryname, EntityType.Builder<T> entityTypeBuilder) {
      return REGISTRY.register(registryname, () -> entityTypeBuilder.build(net.owleafstudio.modminds.compat.Ids.entidad(registryname)));
   }

   @SubscribeEvent
   public static void init(FMLCommonSetupEvent event) {
      event.enqueueWork(() -> {
         CasinoEntity.init();
         TortugaNetheritaEntity.init();
         SapitoEntity.init();
         GorgonaEntity.init();
         BombaNegraEntity.init();
         BombaNegraSinAlasEntity.init();
         InfernalBullEntity.init();
         MurcielagoWardenEntity.init();
         Taxi23Entity.init();
         Taxi23DeCuatroAsientosEntity.init();
         NaveEnemigaDePulsoBorgEntity.init();
         NaveEnemigaTanqueBorgEntity.init();
         NaveEnemigaAcorazadaBorgEntity.init();
         NaveEnemigaAtomizadoraDeAstrosBorgEntity.init();
         NaveEnemigaCortaBorgEntity.init();
         NaveEnemigaAmarillaAcorazadaBorgEntity.init();
         NaveEnemigaPerforadoraDelVacioBorgEntity.init();
         NaveEnemigaAcorazadaRojaBorgEntity.init();
         NaveEnemigaTripulsadoraDeIonesBorgEntity.init();
         NaveEnemigaCasitaBorgEntity.init();
         NaveEnemigaAzulClaroBorgEntity.init();
         SerpienteEntity.init();
         SapoEntity.init();
         FantasmaRojoEntity.init();
         PuertaBorgVerdeEntity.init();
         FantasmaAmarilloEntity.init();
         FantasmaNegroEntity.init();
         PuertaBorgAmarilloEntity.init();
         PuertaBorgRosaEntity.init();
         PuertaBorgAzulEntity.init();
         PuertaBorgRojaEntity.init();
         PuertaBorgMoradaEntity.init();
         BombaNegraSinAlasEntidadEntity.init();
         BombaRojaEntity.init();
         BombaRojaSinAlasEntity.init();
         BombaRojaSinAlasEntidadEntity.init();
         BombaAmarillaEntity.init();
         BombaAmarillaSinAlasEntity.init();
         BombaAmarillaSinAlasEntidadEntity.init();
         CreeperCakeEntity.init();
         FlashBangEntity.init();
         BombaAcuaticaGrandeEntity.init();
         BombaAcuaticaPequenaEntity.init();
         WildFireEntity.init();
         WardenMounstrosityEntity.init();
         PiglinDelLimboEntity.init();
         BoneSpiderEntity.init();
         ReactorEntity.init();
         TikiGuardianEntity.init();
         SoldadoReviilEntity.init();
         GloboEntity.init();
         HydraEntity.init();
         WaterChargeEntidadEntity.init();
         LimboTitanEntity.init();
         ReviilEntity.init();
         NereidaEntity.init();
         NereidaAguaEntity.init();
         TorretaTikiEntity.init();
         OrbEntity.init();
         AlmaPerdidaEntity.init();
         GrimReaperEntity.init();
         TorretaBorgEntity.init();
         SpaceshipEntity.init();
         RadicoraEntity.init();
         GreamReaperPequenaEntity.init();
         OjoDeLimboEntity.init();
         CandadoTikiEntity.init();
         IntrusoEntity.init();
         ColosoEntity.init();
         ReactorBorgEntity.init();
         ReactorHydraEntity.init();
      });
   }

   @SubscribeEvent
   public static void registerAttributes(EntityAttributeCreationEvent event) {
      event.put(CASINO.get(), CasinoEntity.createAttributes().build());
      event.put(TORTUGA_NETHERITA.get(), TortugaNetheritaEntity.createAttributes().build());
      event.put(SAPITO.get(), SapitoEntity.createAttributes().build());
      event.put(GORGONA.get(), GorgonaEntity.createAttributes().build());
      event.put(BOMBA_NEGRA.get(), BombaNegraEntity.createAttributes().build());
      event.put(BOMBA_NEGRA_SIN_ALAS.get(), BombaNegraSinAlasEntity.createAttributes().build());
      event.put(INFERNAL_BULL.get(), InfernalBullEntity.createAttributes().build());
      event.put(MURCIELAGO_WARDEN.get(), MurcielagoWardenEntity.createAttributes().build());
      event.put(TAXI_23.get(), Taxi23Entity.createAttributes().build());
      event.put(TAXI_23_DE_CUATRO_ASIENTOS.get(), Taxi23DeCuatroAsientosEntity.createAttributes().build());
      event.put(NAVE_ENEMIGA_DE_PULSO_BORG.get(), NaveEnemigaDePulsoBorgEntity.createAttributes().build());
      event.put(NAVE_ENEMIGA_TANQUE_BORG.get(), NaveEnemigaTanqueBorgEntity.createAttributes().build());
      event.put(NAVE_ENEMIGA_ACORAZADA_BORG.get(), NaveEnemigaAcorazadaBorgEntity.createAttributes().build());
      event.put(NAVE_ENEMIGA_ATOMIZADORA_DE_ASTROS_BORG.get(), NaveEnemigaAtomizadoraDeAstrosBorgEntity.createAttributes().build());
      event.put(NAVE_ENEMIGA_CORTA_BORG.get(), NaveEnemigaCortaBorgEntity.createAttributes().build());
      event.put(NAVE_ENEMIGA_AMARILLA_ACORAZADA_BORG.get(), NaveEnemigaAmarillaAcorazadaBorgEntity.createAttributes().build());
      event.put(NAVE_ENEMIGA_PERFORADORA_DEL_VACIO_BORG.get(), NaveEnemigaPerforadoraDelVacioBorgEntity.createAttributes().build());
      event.put(NAVE_ENEMIGA_ACORAZADA_ROJA_BORG.get(), NaveEnemigaAcorazadaRojaBorgEntity.createAttributes().build());
      event.put(NAVE_ENEMIGA_TRIPULSADORA_DE_IONES_BORG.get(), NaveEnemigaTripulsadoraDeIonesBorgEntity.createAttributes().build());
      event.put(NAVE_ENEMIGA_CASITA_BORG.get(), NaveEnemigaCasitaBorgEntity.createAttributes().build());
      event.put(NAVE_ENEMIGA_AZUL_CLARO_BORG.get(), NaveEnemigaAzulClaroBorgEntity.createAttributes().build());
      event.put(SERPIENTE.get(), SerpienteEntity.createAttributes().build());
      event.put(SAPO.get(), SapoEntity.createAttributes().build());
      event.put(FANTASMA_ROJO.get(), FantasmaRojoEntity.createAttributes().build());
      event.put(PUERTA_BORG_VERDE.get(), PuertaBorgVerdeEntity.createAttributes().build());
      event.put(FANTASMA_AMARILLO.get(), FantasmaAmarilloEntity.createAttributes().build());
      event.put(FANTASMA_NEGRO.get(), FantasmaNegroEntity.createAttributes().build());
      event.put(PUERTA_BORG_AMARILLO.get(), PuertaBorgAmarilloEntity.createAttributes().build());
      event.put(PUERTA_BORG_ROSA.get(), PuertaBorgRosaEntity.createAttributes().build());
      event.put(PUERTA_BORG_AZUL.get(), PuertaBorgAzulEntity.createAttributes().build());
      event.put(PUERTA_BORG_ROJA.get(), PuertaBorgRojaEntity.createAttributes().build());
      event.put(PUERTA_BORG_MORADA.get(), PuertaBorgMoradaEntity.createAttributes().build());
      event.put(BOMBA_NEGRA_SIN_ALAS_ENTIDAD.get(), BombaNegraSinAlasEntidadEntity.createAttributes().build());
      event.put(BOMBA_ROJA.get(), BombaRojaEntity.createAttributes().build());
      event.put(BOMBA_ROJA_SIN_ALAS.get(), BombaRojaSinAlasEntity.createAttributes().build());
      event.put(BOMBA_ROJA_SIN_ALAS_ENTIDAD.get(), BombaRojaSinAlasEntidadEntity.createAttributes().build());
      event.put(BOMBA_AMARILLA.get(), BombaAmarillaEntity.createAttributes().build());
      event.put(BOMBA_AMARILLA_SIN_ALAS.get(), BombaAmarillaSinAlasEntity.createAttributes().build());
      event.put(BOMBA_AMARILLA_SIN_ALAS_ENTIDAD.get(), BombaAmarillaSinAlasEntidadEntity.createAttributes().build());
      event.put(CREEPER_CAKE.get(), CreeperCakeEntity.createAttributes().build());
      event.put(FLASH_BANG.get(), FlashBangEntity.createAttributes().build());
      event.put(BOMBA_ACUATICA_GRANDE.get(), BombaAcuaticaGrandeEntity.createAttributes().build());
      event.put(BOMBA_ACUATICA_PEQUENA.get(), BombaAcuaticaPequenaEntity.createAttributes().build());
      event.put(WILD_FIRE.get(), WildFireEntity.createAttributes().build());
      event.put(WARDEN_MOUNSTROSITY.get(), WardenMounstrosityEntity.createAttributes().build());
      event.put(PIGLIN_DEL_LIMBO.get(), PiglinDelLimboEntity.createAttributes().build());
      event.put(BONE_SPIDER.get(), BoneSpiderEntity.createAttributes().build());
      event.put(REACTOR.get(), ReactorEntity.createAttributes().build());
      event.put(TIKI_GUARDIAN.get(), TikiGuardianEntity.createAttributes().build());
      event.put(SOLDADO_REVIIL.get(), SoldadoReviilEntity.createAttributes().build());
      event.put(GLOBO.get(), GloboEntity.createAttributes().build());
      event.put(HYDRA.get(), HydraEntity.createAttributes().build());
      event.put(WATER_CHARGE_ENTIDAD.get(), WaterChargeEntidadEntity.createAttributes().build());
      event.put(LIMBO_TITAN.get(), LimboTitanEntity.createAttributes().build());
      event.put(REVIIL.get(), ReviilEntity.createAttributes().build());
      event.put(NEREIDA.get(), NereidaEntity.createAttributes().build());
      event.put(NEREIDA_AGUA.get(), NereidaAguaEntity.createAttributes().build());
      event.put(TORRETA_TIKI.get(), TorretaTikiEntity.createAttributes().build());
      event.put(ORB.get(), OrbEntity.createAttributes().build());
      event.put(ALMA_PERDIDA.get(), AlmaPerdidaEntity.createAttributes().build());
      event.put(GRIM_REAPER.get(), GrimReaperEntity.createAttributes().build());
      event.put(TORRETA_BORG.get(), TorretaBorgEntity.createAttributes().build());
      event.put(SPACESHIP.get(), SpaceshipEntity.createAttributes().build());
      event.put(RADICORA.get(), RadicoraEntity.createAttributes().build());
      event.put(GREAM_REAPER_PEQUENA.get(), GreamReaperPequenaEntity.createAttributes().build());
      event.put(OJO_DE_LIMBO.get(), OjoDeLimboEntity.createAttributes().build());
      event.put(CANDADO_TIKI.get(), CandadoTikiEntity.createAttributes().build());
      event.put(INTRUSO.get(), IntrusoEntity.createAttributes().build());
      event.put(COLOSO.get(), ColosoEntity.createAttributes().build());
      event.put(REACTOR_BORG.get(), ReactorBorgEntity.createAttributes().build());
      event.put(REACTOR_HYDRA.get(), ReactorHydraEntity.createAttributes().build());
   }
}
