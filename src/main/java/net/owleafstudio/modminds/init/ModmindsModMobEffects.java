package net.owleafstudio.modminds.init;

import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.owleafstudio.modminds.potion.BombaNegraEfectoMobEffect;
import net.owleafstudio.modminds.potion.CreeperCakeEfectoMobEffect;
import net.owleafstudio.modminds.potion.EfectoQuemaduraMobEffect;
import net.owleafstudio.modminds.potion.EngullidoSapoMobEffect;
import net.owleafstudio.modminds.potion.FlashBang0MobEffect;
import net.owleafstudio.modminds.potion.HitEfectoMobEffect;
import net.owleafstudio.modminds.potion.HydraEspaciosMobEffect;
import net.owleafstudio.modminds.potion.LecheMobEffect;
import net.owleafstudio.modminds.potion.PantallaGrisEfectoMobEffect;
import net.owleafstudio.modminds.potion.PildoraZombificacionMobEffect;
import net.owleafstudio.modminds.potion.ProteccionContraLasBombasMobEffect;
import net.owleafstudio.modminds.potion.ProteccionSolarMobEffect;
import net.owleafstudio.modminds.potion.RedScreenMobEffect;
import net.owleafstudio.modminds.potion.SirenomorphosisMobEffect;
import net.owleafstudio.modminds.potion.TexturaGorgonaCongeladoMobEffect;
import net.owleafstudio.modminds.potion.ZombificacionMobEffect;

public class ModmindsModMobEffects {
   public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, "modminds");
   public static final RegistryObject<MobEffect> EFECTO_QUEMADURA = REGISTRY.register("efecto_quemadura", () -> new EfectoQuemaduraMobEffect());
   public static final RegistryObject<MobEffect> PROTECCION_SOLAR = REGISTRY.register("proteccion_solar", () -> new ProteccionSolarMobEffect());
   public static final RegistryObject<MobEffect> ZOMBIFICACION = REGISTRY.register("zombificacion", () -> new ZombificacionMobEffect());
   public static final RegistryObject<MobEffect> PILDORA_ZOMBIFICACION = REGISTRY.register("pildora_zombificacion", () -> new PildoraZombificacionMobEffect());
   public static final RegistryObject<MobEffect> TEXTURA_GORGONA_CONGELADO = REGISTRY.register(
      "textura_gorgona_congelado", () -> new TexturaGorgonaCongeladoMobEffect()
   );
   public static final RegistryObject<MobEffect> BOMBA_NEGRA_EFECTO = REGISTRY.register("bomba_negra_efecto", () -> new BombaNegraEfectoMobEffect());
   public static final RegistryObject<MobEffect> SIRENOMORPHOSIS = REGISTRY.register("sirenomorphosis", () -> new SirenomorphosisMobEffect());
   public static final RegistryObject<MobEffect> HIT_EFECTO = REGISTRY.register("hit_efecto", () -> new HitEfectoMobEffect());
   public static final RegistryObject<MobEffect> ENGULLIDO_SAPO = REGISTRY.register("engullido_sapo", () -> new EngullidoSapoMobEffect());
   public static final RegistryObject<MobEffect> CREEPER_CAKE_EFECTO = REGISTRY.register("creeper_cake_efecto", () -> new CreeperCakeEfectoMobEffect());
   public static final RegistryObject<MobEffect> FLASH_BANG_0 = REGISTRY.register("flash_bang_0", () -> new FlashBang0MobEffect());
   public static final RegistryObject<MobEffect> HYDRA_ESPACIOS = REGISTRY.register("hydra_espacios", () -> new HydraEspaciosMobEffect());
   public static final RegistryObject<MobEffect> PANTALLA_GRIS_EFECTO = REGISTRY.register("pantalla_gris_efecto", () -> new PantallaGrisEfectoMobEffect());
   public static final RegistryObject<MobEffect> RED_SCREEN = REGISTRY.register("red_screen", () -> new RedScreenMobEffect());
   public static final RegistryObject<MobEffect> LECHE = REGISTRY.register("leche", () -> new LecheMobEffect());
   public static final RegistryObject<MobEffect> PROTECCION_CONTRA_LAS_BOMBAS = REGISTRY.register(
      "proteccion_contra_las_bombas", () -> new ProteccionContraLasBombasMobEffect()
   );
}
