package net.owleafstudio.modminds.network;

import java.util.function.Supplier;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.core.HolderLookup;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.compat.ClientHooks;

@Mod.EventBusSubscriber(
   bus = Mod.EventBusSubscriber.Bus.MOD
)
public class ModmindsModVariables {
   public static final AttachmentType<ModmindsModVariables.PlayerVariables> PLAYER_VARIABLES = AttachmentRegistry.create(
      Identifier.fromNamespaceAndPath("modminds", "player_variables"),
      builder -> builder.initializer(ModmindsModVariables.PlayerVariables::new).copyOnDeath().persistent(CompoundTag.CODEC.xmap(tag -> {
         ModmindsModVariables.PlayerVariables variables = new ModmindsModVariables.PlayerVariables();
         variables.readNBT(tag);
         return variables;
      }, ModmindsModVariables.PlayerVariables::writeNBTCompound))
   );
   public static final Capability<ModmindsModVariables.PlayerVariables> PLAYER_VARIABLES_CAPABILITY = CapabilityManager.get(
      new CapabilityToken<ModmindsModVariables.PlayerVariables>() {}
   );

   public static LazyOptional<ModmindsModVariables.PlayerVariables> getVariables(Entity entity) {
      return entity == null
         ? LazyOptional.of(ModmindsModVariables.PlayerVariables::new)
         : LazyOptional.of(() -> (ModmindsModVariables.PlayerVariables)entity.getAttachedOrCreate(PLAYER_VARIABLES));
   }

   @SubscribeEvent
   public static void init(FMLCommonSetupEvent event) {
      ModmindsMod.addNetworkMessage(
         ModmindsModVariables.SavedDataSyncMessage.class,
         ModmindsModVariables.SavedDataSyncMessage::buffer,
         ModmindsModVariables.SavedDataSyncMessage::new,
         ModmindsModVariables.SavedDataSyncMessage::handler
      );
      ModmindsMod.addNetworkMessage(
         ModmindsModVariables.PlayerVariablesSyncMessage.class,
         ModmindsModVariables.PlayerVariablesSyncMessage::buffer,
         ModmindsModVariables.PlayerVariablesSyncMessage::new,
         ModmindsModVariables.PlayerVariablesSyncMessage::handler
      );
   }

   @SubscribeEvent
   public static void init(RegisterCapabilitiesEvent event) {
      event.register(ModmindsModVariables.PlayerVariables.class);
   }

   @Mod.EventBusSubscriber
   public static class EventBusVariableHandlers {
      @SubscribeEvent
      public static void onPlayerLoggedInSyncPlayerVariables(PlayerEvent.PlayerLoggedInEvent event) {
         if (!event.getEntity().level().isClientSide()) {
            ModmindsModVariables.getVariables(event.getEntity()).orElse(new ModmindsModVariables.PlayerVariables()).syncPlayerVariables(event.getEntity());
         }
      }

      @SubscribeEvent
      public static void onPlayerRespawnedSyncPlayerVariables(PlayerEvent.PlayerRespawnEvent event) {
         if (!event.getEntity().level().isClientSide()) {
            ModmindsModVariables.getVariables(event.getEntity()).orElse(new ModmindsModVariables.PlayerVariables()).syncPlayerVariables(event.getEntity());
         }
      }

      @SubscribeEvent
      public static void onPlayerChangedDimensionSyncPlayerVariables(PlayerEvent.PlayerChangedDimensionEvent event) {
         if (!event.getEntity().level().isClientSide()) {
            ModmindsModVariables.getVariables(event.getEntity()).orElse(new ModmindsModVariables.PlayerVariables()).syncPlayerVariables(event.getEntity());
         }
      }

      @SubscribeEvent
      public static void clonePlayer(PlayerEvent.Clone event) {
         ModmindsModVariables.PlayerVariables original = ModmindsModVariables.getVariables(event.getOriginal())
            .orElse(new ModmindsModVariables.PlayerVariables());
         ModmindsModVariables.PlayerVariables clone = ModmindsModVariables.getVariables(event.getEntity()).orElse(new ModmindsModVariables.PlayerVariables());
         clone.PersistenciaVariable = original.PersistenciaVariable;
         clone.SubmarinoArmado = original.SubmarinoArmado;
         clone.VariableNutritotem = original.VariableNutritotem;
         clone.VariableTotemEon = original.VariableTotemEon;
         clone.VariableTotemEonMariposa = original.VariableTotemEonMariposa;
         clone.VariableTotemRaro = original.VariableTotemRaro;
         clone.VariableTotemRevil = original.VariableTotemRevil;
         clone.TieneAlma = original.TieneAlma;
         if (!event.isWasDeath()) {
            clone.ArmaCortaEspacialCargador = original.ArmaCortaEspacialCargador;
            clone.BalasCartucho = original.BalasCartucho;
            clone.BombaNegraContadorDeEspacios = original.BombaNegraContadorDeEspacios;
            clone.BombaNegraEspacioLogic = original.BombaNegraEspacioLogic;
            clone.BotasInfernales = original.BotasInfernales;
            clone.BotasNaoneim = original.BotasNaoneim;
            clone.CascoInfernal = original.CascoInfernal;
            clone.CascoNaoneim = original.CascoNaoneim;
            clone.CreeperCakeLogica = original.CreeperCakeLogica;
            clone.EspaciosHydra = original.EspaciosHydra;
            clone.FlashBang = original.FlashBang;
            clone.GorgonaContadorCongelar = original.GorgonaContadorCongelar;
            clone.GorgonaContadorDeEspacios = original.GorgonaContadorDeEspacios;
            clone.GorgonaEspacioLogic = original.GorgonaEspacioLogic;
            clone.MostrarEspacioHydra = original.MostrarEspacioHydra;
            clone.MunicionData = original.MunicionData;
            clone.PanatalonesInfernales = original.PanatalonesInfernales;
            clone.PantalonesNaoneim = original.PantalonesNaoneim;
            clone.PecheraInfernal = original.PecheraInfernal;
            clone.PecheraNaoneim = original.PecheraNaoneim;
            clone.PistolaPunteroRojo = original.PistolaPunteroRojo;
            clone.RevillPunch = original.RevillPunch;
            clone.SapoContadorDeEspacios = original.SapoContadorDeEspacios;
            clone.SapoCooldown = original.SapoCooldown;
            clone.SapoEspacioLogic = original.SapoEspacioLogic;
            clone.Wpressed = original.Wpressed;
            clone.CascoDimensional = original.CascoDimensional;
            clone.PetoDimensional = original.PetoDimensional;
            clone.PantalonesDimensional = original.PantalonesDimensional;
            clone.BotasDimensional = original.BotasDimensional;
            clone.InfernalBullStun = original.InfernalBullStun;
            clone.InfernalBullRoar = original.InfernalBullRoar;
         }
      }

      @SubscribeEvent
      public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
         if (!event.getEntity().level().isClientSide()) {
            SavedData mapdata = ModmindsModVariables.MapVariables.get(event.getEntity().level());
            SavedData worlddata = ModmindsModVariables.WorldVariables.get(event.getEntity().level());
            if (mapdata != null) {
               ModmindsMod.PACKET_HANDLER
                  .send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)event.getEntity()), new ModmindsModVariables.SavedDataSyncMessage(0, mapdata));
            }

            if (worlddata != null) {
               ModmindsMod.PACKET_HANDLER
                  .send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)event.getEntity()), new ModmindsModVariables.SavedDataSyncMessage(1, worlddata));
            }
         }
      }

      @SubscribeEvent
      public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
         if (!event.getEntity().level().isClientSide()) {
            SavedData worlddata = ModmindsModVariables.WorldVariables.get(event.getEntity().level());
            if (worlddata != null) {
               ModmindsMod.PACKET_HANDLER
                  .send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)event.getEntity()), new ModmindsModVariables.SavedDataSyncMessage(1, worlddata));
            }
         }
      }
   }

   public static class MapVariables extends SavedData {
      public static final net.minecraft.world.level.saveddata.SavedDataType<ModmindsModVariables.MapVariables> TYPE = new net.minecraft.world.level.saveddata.SavedDataType<>(
         net.minecraft.resources.Identifier.fromNamespaceAndPath("modminds", "modminds_mapvars"),
         ModmindsModVariables.MapVariables::new,
         CompoundTag.CODEC.xmap(ModmindsModVariables.MapVariables::load, d -> d.save(new CompoundTag(), null)),
         DataFixTypes.LEVEL
      );
      public static final String DATA_NAME = "modminds_mapvars";
      public double AngleReviil = 0.0;
      public boolean ComandoBloqueador = false;
      public boolean ComandoSapitoSpawn = true;
      public boolean ComandoTortugaNetheritaSpawn = true;
      public double HydraAtaques = 0.0;
      public boolean InfernalBull = false;
      public double iReviil = 0.0;
      public boolean PersistenciaZombificacion = false;
      public double RadioReviil = 0.0;
      public double ReviilAtaques = 0.0;
      public boolean ReviilMontar = false;
      public boolean RevilPunchCooldown = true;
      public boolean SpawnEndermans = false;
      public double TitanLimboAtaques = 0.0;
      public double Xreviil = 0.0;
      public double Yreviil = 0.0;
      public boolean ZombificacionPorZombi = false;
      public double Zreviil = 0.0;
      public boolean ReactorAlSerDanada = false;
      static ModmindsModVariables.MapVariables clientSide = new ModmindsModVariables.MapVariables();

      public static ModmindsModVariables.MapVariables load(CompoundTag tag) {
         ModmindsModVariables.MapVariables data = new ModmindsModVariables.MapVariables();
         data.read(tag);
         return data;
      }

      public void read(CompoundTag nbt) {
         this.AngleReviil = nbt.getDoubleOr("AngleReviil", 0.0);
         this.ComandoBloqueador = nbt.getBooleanOr("ComandoBloqueador", false);
         this.ComandoSapitoSpawn = readFlag(nbt, "ComandoSapitoSpawn", this.ComandoSapitoSpawn);
         this.ComandoTortugaNetheritaSpawn = readFlag(nbt, "ComandoTortugaNetheritaSpawn", this.ComandoTortugaNetheritaSpawn);
         this.HydraAtaques = nbt.getDoubleOr("HydraAtaques", 0.0);
         this.InfernalBull = nbt.getBooleanOr("InfernalBull", false);
         this.iReviil = nbt.getDoubleOr("iReviil", 0.0);
         this.PersistenciaZombificacion = nbt.getBooleanOr("PersistenciaZombificacion", false);
         this.RadioReviil = nbt.getDoubleOr("RadioReviil", 0.0);
         this.ReviilAtaques = nbt.getDoubleOr("ReviilAtaques", 0.0);
         this.ReviilMontar = nbt.getBooleanOr("ReviilMontar", false);
         this.RevilPunchCooldown = readFlag(nbt, "RevilPunchCooldown", this.RevilPunchCooldown);
         this.SpawnEndermans = nbt.getBooleanOr("SpawnEndermans", false);
         this.TitanLimboAtaques = nbt.getDoubleOr("TitanLimboAtaques", 0.0);
         this.Xreviil = nbt.getDoubleOr("Xreviil", 0.0);
         this.Yreviil = nbt.getDoubleOr("Yreviil", 0.0);
         this.ZombificacionPorZombi = nbt.getBooleanOr("ZombificacionPorZombi", false);
         this.Zreviil = nbt.getDoubleOr("Zreviil", 0.0);
         this.ReactorAlSerDanada = nbt.getBooleanOr("ReactorAlSerDanada", false);
      }

      private static boolean readFlag(CompoundTag nbt, String key, boolean declaredDefault) {
         return nbt.getBooleanOr(key, declaredDefault);
      }

      public CompoundTag save(CompoundTag nbt, HolderLookup.Provider _registries) {
         nbt.putDouble("AngleReviil", this.AngleReviil);
         nbt.putBoolean("ComandoBloqueador", this.ComandoBloqueador);
         nbt.putBoolean("ComandoSapitoSpawn", this.ComandoSapitoSpawn);
         nbt.putBoolean("ComandoTortugaNetheritaSpawn", this.ComandoTortugaNetheritaSpawn);
         nbt.putDouble("HydraAtaques", this.HydraAtaques);
         nbt.putBoolean("InfernalBull", this.InfernalBull);
         nbt.putDouble("iReviil", this.iReviil);
         nbt.putBoolean("PersistenciaZombificacion", this.PersistenciaZombificacion);
         nbt.putDouble("RadioReviil", this.RadioReviil);
         nbt.putDouble("ReviilAtaques", this.ReviilAtaques);
         nbt.putBoolean("ReviilMontar", this.ReviilMontar);
         nbt.putBoolean("RevilPunchCooldown", this.RevilPunchCooldown);
         nbt.putBoolean("SpawnEndermans", this.SpawnEndermans);
         nbt.putDouble("TitanLimboAtaques", this.TitanLimboAtaques);
         nbt.putDouble("Xreviil", this.Xreviil);
         nbt.putDouble("Yreviil", this.Yreviil);
         nbt.putBoolean("ZombificacionPorZombi", this.ZombificacionPorZombi);
         nbt.putDouble("Zreviil", this.Zreviil);
         nbt.putBoolean("ReactorAlSerDanada", this.ReactorAlSerDanada);
         return nbt;
      }

      public void syncData(LevelAccessor world) {
         this.setDirty();
         if (world instanceof Level && !world.isClientSide()) {
            ModmindsMod.PACKET_HANDLER.send(PacketDistributor.ALL.noArg(), new ModmindsModVariables.SavedDataSyncMessage(0, this));
         }
      }

      public static ModmindsModVariables.MapVariables get(LevelAccessor world) {
         return world instanceof ServerLevelAccessor serverLevelAcc
            ? (ModmindsModVariables.MapVariables)serverLevelAcc.getLevel()
               .getServer()
               .getLevel(Level.OVERWORLD)
               .getDataStorage()
               .computeIfAbsent(
                  ModmindsModVariables.MapVariables.TYPE
               )
            : clientSide;
      }
   }

   public static class PlayerVariables {
      public double ArmaCortaEspacialCargador = 0.0;
      public double BalasCartucho = 0.0;
      public double BombaNegraContadorDeEspacios = 0.0;
      public boolean BombaNegraEspacioLogic = true;
      public boolean BotasInfernales = false;
      public boolean BotasNaoneim = false;
      public boolean CascoInfernal = false;
      public boolean CascoNaoneim = false;
      public boolean CreeperCakeLogica = false;
      public double EspaciosHydra = 0.0;
      public boolean FlashBang = false;
      public double GorgonaContadorCongelar = 0.0;
      public double GorgonaContadorDeEspacios = 0.0;
      public boolean GorgonaEspacioLogic = true;
      public boolean MostrarEspacioHydra = false;
      public String MunicionData = "\"\"";
      public boolean PanatalonesInfernales = false;
      public boolean PantalonesNaoneim = false;
      public boolean PecheraInfernal = false;
      public boolean PecheraNaoneim = false;
      public boolean PersistenciaVariable = false;
      public boolean PistolaPunteroRojo = false;
      public boolean RevillPunch = false;
      public double SapoContadorDeEspacios = 0.0;
      public double SapoCooldown = 0.0;
      public boolean SapoEspacioLogic = false;
      public double SubmarinoArmado = 0.0;
      public boolean VariableNutritotem = false;
      public boolean VariableTotemEon = false;
      public boolean VariableTotemEonMariposa = false;
      public boolean VariableTotemRaro = false;
      public boolean VariableTotemRevil = false;
      public double Wpressed = 0.0;
      public boolean TieneAlma = true;
      public boolean CascoDimensional = false;
      public boolean PetoDimensional = false;
      public boolean PantalonesDimensional = false;
      public boolean BotasDimensional = false;
      public boolean InfernalBullStun = false;
      public boolean InfernalBullRoar = false;

      public void syncPlayerVariables(Entity entity) {
         if (entity instanceof ServerPlayer serverPlayer) {
            ModmindsMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new ModmindsModVariables.PlayerVariablesSyncMessage(this));
         }
      }

      public Tag writeNBT() {
         CompoundTag nbt = new CompoundTag();
         nbt.putDouble("ArmaCortaEspacialCargador", this.ArmaCortaEspacialCargador);
         nbt.putDouble("BalasCartucho", this.BalasCartucho);
         nbt.putDouble("BombaNegraContadorDeEspacios", this.BombaNegraContadorDeEspacios);
         nbt.putBoolean("BombaNegraEspacioLogic", this.BombaNegraEspacioLogic);
         nbt.putBoolean("BotasInfernales", this.BotasInfernales);
         nbt.putBoolean("BotasNaoneim", this.BotasNaoneim);
         nbt.putBoolean("CascoInfernal", this.CascoInfernal);
         nbt.putBoolean("CascoNaoneim", this.CascoNaoneim);
         nbt.putBoolean("CreeperCakeLogica", this.CreeperCakeLogica);
         nbt.putDouble("EspaciosHydra", this.EspaciosHydra);
         nbt.putBoolean("FlashBang", this.FlashBang);
         nbt.putDouble("GorgonaContadorCongelar", this.GorgonaContadorCongelar);
         nbt.putDouble("GorgonaContadorDeEspacios", this.GorgonaContadorDeEspacios);
         nbt.putBoolean("GorgonaEspacioLogic", this.GorgonaEspacioLogic);
         nbt.putBoolean("MostrarEspacioHydra", this.MostrarEspacioHydra);
         nbt.putString("MunicionData", this.MunicionData);
         nbt.putBoolean("PanatalonesInfernales", this.PanatalonesInfernales);
         nbt.putBoolean("PantalonesNaoneim", this.PantalonesNaoneim);
         nbt.putBoolean("PecheraInfernal", this.PecheraInfernal);
         nbt.putBoolean("PecheraNaoneim", this.PecheraNaoneim);
         nbt.putBoolean("PersistenciaVariable", this.PersistenciaVariable);
         nbt.putBoolean("PistolaPunteroRojo", this.PistolaPunteroRojo);
         nbt.putBoolean("RevillPunch", this.RevillPunch);
         nbt.putDouble("SapoContadorDeEspacios", this.SapoContadorDeEspacios);
         nbt.putDouble("SapoCooldown", this.SapoCooldown);
         nbt.putBoolean("SapoEspacioLogic", this.SapoEspacioLogic);
         nbt.putDouble("SubmarinoArmado", this.SubmarinoArmado);
         nbt.putBoolean("VariableNutritotem", this.VariableNutritotem);
         nbt.putBoolean("VariableTotemEon", this.VariableTotemEon);
         nbt.putBoolean("VariableTotemEonMariposa", this.VariableTotemEonMariposa);
         nbt.putBoolean("VariableTotemRaro", this.VariableTotemRaro);
         nbt.putBoolean("VariableTotemRevil", this.VariableTotemRevil);
         nbt.putDouble("Wpressed", this.Wpressed);
         nbt.putBoolean("TieneAlma", this.TieneAlma);
         nbt.putBoolean("CascoDimensional", this.CascoDimensional);
         nbt.putBoolean("PetoDimensional", this.PetoDimensional);
         nbt.putBoolean("PantalonesDimensional", this.PantalonesDimensional);
         nbt.putBoolean("BotasDimensional", this.BotasDimensional);
         nbt.putBoolean("InfernalBullStun", this.InfernalBullStun);
         nbt.putBoolean("InfernalBullRoar", this.InfernalBullRoar);
         return nbt;
      }

      public CompoundTag writeNBTCompound() {
         return (CompoundTag)this.writeNBT();
      }

      public void readNBT(Tag tag) {
         CompoundTag nbt = (CompoundTag)tag;
         this.ArmaCortaEspacialCargador = nbt.getDoubleOr("ArmaCortaEspacialCargador", 0.0);
         this.BalasCartucho = nbt.getDoubleOr("BalasCartucho", 0.0);
         this.BombaNegraContadorDeEspacios = nbt.getDoubleOr("BombaNegraContadorDeEspacios", 0.0);
         this.BombaNegraEspacioLogic = nbt.getBooleanOr("BombaNegraEspacioLogic", false);
         this.BotasInfernales = nbt.getBooleanOr("BotasInfernales", false);
         this.BotasNaoneim = nbt.getBooleanOr("BotasNaoneim", false);
         this.CascoInfernal = nbt.getBooleanOr("CascoInfernal", false);
         this.CascoNaoneim = nbt.getBooleanOr("CascoNaoneim", false);
         this.CreeperCakeLogica = nbt.getBooleanOr("CreeperCakeLogica", false);
         this.EspaciosHydra = nbt.getDoubleOr("EspaciosHydra", 0.0);
         this.FlashBang = nbt.getBooleanOr("FlashBang", false);
         this.GorgonaContadorCongelar = nbt.getDoubleOr("GorgonaContadorCongelar", 0.0);
         this.GorgonaContadorDeEspacios = nbt.getDoubleOr("GorgonaContadorDeEspacios", 0.0);
         this.GorgonaEspacioLogic = nbt.getBooleanOr("GorgonaEspacioLogic", false);
         this.MostrarEspacioHydra = nbt.getBooleanOr("MostrarEspacioHydra", false);
         this.MunicionData = nbt.getStringOr("MunicionData", "");
         this.PanatalonesInfernales = nbt.getBooleanOr("PanatalonesInfernales", false);
         this.PantalonesNaoneim = nbt.getBooleanOr("PantalonesNaoneim", false);
         this.PecheraInfernal = nbt.getBooleanOr("PecheraInfernal", false);
         this.PecheraNaoneim = nbt.getBooleanOr("PecheraNaoneim", false);
         this.PersistenciaVariable = nbt.getBooleanOr("PersistenciaVariable", false);
         this.PistolaPunteroRojo = nbt.getBooleanOr("PistolaPunteroRojo", false);
         this.RevillPunch = nbt.getBooleanOr("RevillPunch", false);
         this.SapoContadorDeEspacios = nbt.getDoubleOr("SapoContadorDeEspacios", 0.0);
         this.SapoCooldown = nbt.getDoubleOr("SapoCooldown", 0.0);
         this.SapoEspacioLogic = nbt.getBooleanOr("SapoEspacioLogic", false);
         this.SubmarinoArmado = nbt.getDoubleOr("SubmarinoArmado", 0.0);
         this.VariableNutritotem = nbt.getBooleanOr("VariableNutritotem", false);
         this.VariableTotemEon = nbt.getBooleanOr("VariableTotemEon", false);
         this.VariableTotemEonMariposa = nbt.getBooleanOr("VariableTotemEonMariposa", false);
         this.VariableTotemRaro = nbt.getBooleanOr("VariableTotemRaro", false);
         this.VariableTotemRevil = nbt.getBooleanOr("VariableTotemRevil", false);
         this.Wpressed = nbt.getDoubleOr("Wpressed", 0.0);
         this.TieneAlma = nbt.getBooleanOr("TieneAlma", false);
         this.CascoDimensional = nbt.getBooleanOr("CascoDimensional", false);
         this.PetoDimensional = nbt.getBooleanOr("PetoDimensional", false);
         this.PantalonesDimensional = nbt.getBooleanOr("PantalonesDimensional", false);
         this.BotasDimensional = nbt.getBooleanOr("BotasDimensional", false);
         this.InfernalBullStun = nbt.getBooleanOr("InfernalBullStun", false);
         this.InfernalBullRoar = nbt.getBooleanOr("InfernalBullRoar", false);
      }
   }

   @Mod.EventBusSubscriber
   private static class PlayerVariablesProvider implements ICapabilitySerializable<Tag> {
      private final ModmindsModVariables.PlayerVariables playerVariables = new ModmindsModVariables.PlayerVariables();
      private final LazyOptional<ModmindsModVariables.PlayerVariables> instance = LazyOptional.of(() -> this.playerVariables);

      @SubscribeEvent
      public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
         if (event.getObject() instanceof Player && !(event.getObject() instanceof FakePlayer)) {
            event.addCapability(Identifier.fromNamespaceAndPath("modminds", "player_variables"), new ModmindsModVariables.PlayerVariablesProvider());
         }
      }

      @Override
      public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
         return cap == ModmindsModVariables.PLAYER_VARIABLES_CAPABILITY ? this.instance.cast() : LazyOptional.empty();
      }

      @Override
      public Tag serializeNBT() {
         return this.playerVariables.writeNBT();
      }

      @Override
      public void deserializeNBT(Tag nbt) {
         this.playerVariables.readNBT(nbt);
      }
   }

   public static class PlayerVariablesSyncMessage {
      private final ModmindsModVariables.PlayerVariables data;

      public PlayerVariablesSyncMessage(FriendlyByteBuf buffer) {
         this.data = new ModmindsModVariables.PlayerVariables();
         this.data.readNBT(buffer.readNbt());
      }

      public PlayerVariablesSyncMessage(ModmindsModVariables.PlayerVariables data) {
         this.data = data;
      }

      public static void buffer(ModmindsModVariables.PlayerVariablesSyncMessage message, FriendlyByteBuf buffer) {
         buffer.writeNbt((CompoundTag)message.data.writeNBT());
      }

      public static void handler(ModmindsModVariables.PlayerVariablesSyncMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
         NetworkEvent.Context context = contextSupplier.get();
         context.enqueueWork(
            () -> {
               if (!context.getDirection().getReceptionSide().isServer()) {
                  ModmindsModVariables.PlayerVariables variables = ClientHooks.clientPlayer()
                     .map(ModmindsModVariables::getVariables)
                     .orElse(LazyOptional.of(ModmindsModVariables.PlayerVariables::new))
                     .orElse(new ModmindsModVariables.PlayerVariables());
                  variables.ArmaCortaEspacialCargador = message.data.ArmaCortaEspacialCargador;
                  variables.BalasCartucho = message.data.BalasCartucho;
                  variables.BombaNegraContadorDeEspacios = message.data.BombaNegraContadorDeEspacios;
                  variables.BombaNegraEspacioLogic = message.data.BombaNegraEspacioLogic;
                  variables.BotasInfernales = message.data.BotasInfernales;
                  variables.BotasNaoneim = message.data.BotasNaoneim;
                  variables.CascoInfernal = message.data.CascoInfernal;
                  variables.CascoNaoneim = message.data.CascoNaoneim;
                  variables.CreeperCakeLogica = message.data.CreeperCakeLogica;
                  variables.EspaciosHydra = message.data.EspaciosHydra;
                  variables.FlashBang = message.data.FlashBang;
                  variables.GorgonaContadorCongelar = message.data.GorgonaContadorCongelar;
                  variables.GorgonaContadorDeEspacios = message.data.GorgonaContadorDeEspacios;
                  variables.GorgonaEspacioLogic = message.data.GorgonaEspacioLogic;
                  variables.MostrarEspacioHydra = message.data.MostrarEspacioHydra;
                  variables.MunicionData = message.data.MunicionData;
                  variables.PanatalonesInfernales = message.data.PanatalonesInfernales;
                  variables.PantalonesNaoneim = message.data.PantalonesNaoneim;
                  variables.PecheraInfernal = message.data.PecheraInfernal;
                  variables.PecheraNaoneim = message.data.PecheraNaoneim;
                  variables.PersistenciaVariable = message.data.PersistenciaVariable;
                  variables.PistolaPunteroRojo = message.data.PistolaPunteroRojo;
                  variables.RevillPunch = message.data.RevillPunch;
                  variables.SapoContadorDeEspacios = message.data.SapoContadorDeEspacios;
                  variables.SapoCooldown = message.data.SapoCooldown;
                  variables.SapoEspacioLogic = message.data.SapoEspacioLogic;
                  variables.SubmarinoArmado = message.data.SubmarinoArmado;
                  variables.VariableNutritotem = message.data.VariableNutritotem;
                  variables.VariableTotemEon = message.data.VariableTotemEon;
                  variables.VariableTotemEonMariposa = message.data.VariableTotemEonMariposa;
                  variables.VariableTotemRaro = message.data.VariableTotemRaro;
                  variables.VariableTotemRevil = message.data.VariableTotemRevil;
                  variables.Wpressed = message.data.Wpressed;
                  variables.TieneAlma = message.data.TieneAlma;
                  variables.CascoDimensional = message.data.CascoDimensional;
                  variables.PetoDimensional = message.data.PetoDimensional;
                  variables.PantalonesDimensional = message.data.PantalonesDimensional;
                  variables.BotasDimensional = message.data.BotasDimensional;
                  variables.InfernalBullStun = message.data.InfernalBullStun;
                  variables.InfernalBullRoar = message.data.InfernalBullRoar;
               }
            }
         );
         context.setPacketHandled(true);
      }
   }

   public static class SavedDataSyncMessage {
      private final int type;
      private SavedData data;

      public SavedDataSyncMessage(FriendlyByteBuf buffer) {
         this.type = buffer.readInt();
         CompoundTag nbt = buffer.readNbt();
         if (nbt != null) {
            this.data = (SavedData)(this.type == 0 ? new ModmindsModVariables.MapVariables() : new ModmindsModVariables.WorldVariables());
            if (this.data instanceof ModmindsModVariables.MapVariables mapVariables) {
               mapVariables.read(nbt);
            } else if (this.data instanceof ModmindsModVariables.WorldVariables worldVariables) {
               worldVariables.read(nbt);
            }
         }
      }

      public SavedDataSyncMessage(int type, SavedData data) {
         this.type = type;
         this.data = data;
      }

      public static void buffer(ModmindsModVariables.SavedDataSyncMessage message, FriendlyByteBuf buffer) {
         buffer.writeInt(message.type);
         if (message.data != null) {
            buffer.writeNbt(message.data instanceof ModmindsModVariables.MapVariables mv ? mv.save(new CompoundTag(), null) : ((ModmindsModVariables.WorldVariables)message.data).save(new CompoundTag(), null));
         }
      }

      public static void handler(ModmindsModVariables.SavedDataSyncMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
         NetworkEvent.Context context = contextSupplier.get();
         context.enqueueWork(() -> {
            if (!context.getDirection().getReceptionSide().isServer() && message.data != null) {
               if (message.type == 0) {
                  ModmindsModVariables.MapVariables.clientSide = (ModmindsModVariables.MapVariables)message.data;
               } else {
                  ModmindsModVariables.WorldVariables.clientSide = (ModmindsModVariables.WorldVariables)message.data;
               }
            }
         });
         context.setPacketHandled(true);
      }
   }

   public static class WorldVariables extends SavedData {
      public static final net.minecraft.world.level.saveddata.SavedDataType<ModmindsModVariables.WorldVariables> TYPE = new net.minecraft.world.level.saveddata.SavedDataType<>(
         net.minecraft.resources.Identifier.fromNamespaceAndPath("modminds", "modminds_worldvars"),
         ModmindsModVariables.WorldVariables::new,
         CompoundTag.CODEC.xmap(ModmindsModVariables.WorldVariables::load, d -> d.save(new CompoundTag(), null)),
         DataFixTypes.LEVEL
      );
      public static final String DATA_NAME = "modminds_worldvars";
      static ModmindsModVariables.WorldVariables clientSide = new ModmindsModVariables.WorldVariables();

      public static ModmindsModVariables.WorldVariables load(CompoundTag tag) {
         ModmindsModVariables.WorldVariables data = new ModmindsModVariables.WorldVariables();
         data.read(tag);
         return data;
      }

      public void read(CompoundTag nbt) {
      }

      public CompoundTag save(CompoundTag nbt, HolderLookup.Provider _registries) {
         return nbt;
      }

      public void syncData(LevelAccessor world) {
         this.setDirty();
         if (world instanceof Level level && !level.isClientSide()) {
            ModmindsMod.PACKET_HANDLER.send(PacketDistributor.DIMENSION.with(level::dimension), new ModmindsModVariables.SavedDataSyncMessage(1, this));
         }
      }

      public static ModmindsModVariables.WorldVariables get(LevelAccessor world) {
         return world instanceof ServerLevel level
            ? (ModmindsModVariables.WorldVariables)level.getDataStorage()
               .computeIfAbsent(
                  ModmindsModVariables.WorldVariables.TYPE
               )
            : clientSide;
      }
   }
}
