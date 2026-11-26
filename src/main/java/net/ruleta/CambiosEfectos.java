package net.ruleta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.animal.golem.SnowGolem;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.skeleton.Bogged;
import net.minecraft.world.entity.monster.spider.CaveSpider;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.entity.monster.skeleton.Stray;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.monster.skeleton.WitherSkeleton;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.GameRules.BooleanValue;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.ruleta.bunker.Bunkers;
import net.ruleta.compat.BlockEvent;
import net.ruleta.compat.EntityJoinLevelEvent;
import net.ruleta.compat.EventPriority;
import net.ruleta.compat.LivingDeathEvent;
import net.ruleta.compat.LivingDropsEvent;
import net.ruleta.compat.LivingEntityUseItemEvent;
import net.ruleta.compat.LivingEvent;
import net.ruleta.compat.LivingHurtEvent;
import net.ruleta.compat.PlayerInteractEvent;
import net.ruleta.compat.SubscribeEvent;
import net.ruleta.mobs.BombaWarden;
import net.ruleta.mobs.MobsNuevos;
import net.ruleta.nuclear.NuclearCreeperMod;

public final class CambiosEfectos {
   static final String TAG_BOMBA = "ruleta_bomba_warden";
   private static final Map<UUID, Long> PERLAS = new HashMap<>();
   private static EntityType<?> tipoNucleeper;
   private static final Set<UUID> ARDIENDO = new HashSet<>();
   private static final Map<UUID, BlockPos> PLACAS = new HashMap<>();
   private static final float DANO_BOTON = 4.0E27F;
   private static final float DANO_PLACA = 1.0E12F;
   private static final float DANO_PUERTA = 1.0E12F;
   static final String TAG_INTELIGENTE = "ruleta_zombie_inteligente";
   private static final Identifier UUID_VELOCIDAD_INTELIGENTE = Reg.id("ruleta", "zombie_inteligente");
   private static final Map<UUID, Integer> AIRE = new HashMap<>();
   private static boolean nucleeperResuelto;
   static final String TAG_TREPADOR = "ruleta_zombie_trepador";
   static final String TAG_NO_TREPADOR = "ruleta_zombie_normal";
   public static volatile Set<String> activosCliente = Set.of();

   private static int INTERVALO_PROFUNDO() {
      return Math.max(20, Ajustes.entero("cambios.profundo") * 20);
   }

   private static int MAX_BOMBAS() {
      return Ajustes.entero("cambios.max_bombas");
   }

   private static int MAX_BLAZES() {
      return Ajustes.entero("cambios.max_blazes");
   }

   private static float PROB_ARANA_CUEVA() {
      return Ajustes.entero("cambios.prob_arana") / 100.0F;
   }

   private static float DANO_ENDERMITE() {
      return Ajustes.decimal("cambios.dano_endermite") * 2.0F;
   }

   private static float DANO_ENDERMAN() {
      return Ajustes.decimal("cambios.dano_enderman") * 2.0F;
   }

   private static int TICKS_OJOS() {
      return Ajustes.entero("cambios.ojos") * 20;
   }

   private static float PROB_PERLA_NUCLEEPER() {
      return Ajustes.decimal("cambios.prob_nucleeper") / 100.0F;
   }

   private static int CAPA_INTELIGENTES() {
      return Ajustes.entero("cambios.capa_inteligentes");
   }

   private static float DUREZA_ZOMBIE() {
      return Ajustes.decimal("cambios.dureza_zombie");
   }

   private static int INTERVALO_PICAR() {
      return Math.max(1, Ajustes.entero("cambios.picar"));
   }

   private CambiosEfectos() {
   }

   static void alActivar(MinecraftServer server, String id) {
      if ("sin-regeneracion".equals(id)) {
         ((BooleanValue)server.getGameRules().getRule(GameRules.RULE_NATURAL_REGENERATION)).set(false, server);
      }

      if ("respirar-bajo-agua".equals(id)) {
         llenarTrampillasConAgua(server);
      }

      if ("zombies-mejorados".equals(id)) {
         for (ServerLevel nivel : server.getAllLevels()) {
            for (Entity entidad : nivel.getAllEntities()) {
               if (entidad instanceof Zombie zombie && esZombieComun(zombie)) {
                  mejorarZombie(zombie, false);
               }
            }
         }
      }

      if ("aranas-cueva-overworld".equals(id) || "aranas-fuerza".equals(id) || "aranas-fuerza-iv".equals(id)) {
         for (ServerLevel nivel : server.getAllLevels()) {
            for (Entity entidad : nivel.getAllEntities()) {
               if (entidad instanceof Spider arana) {
                  potenciarArana(arana);
               }
            }
         }
      }

      if ("extincion-animales".equals(id)) {
         for (ServerLevel nivel : server.getAllLevels()) {
            List<Animal> victimas = new ArrayList<>();

            for (Entity entidad : nivel.getAllEntities()) {
               if (entidad instanceof Animal animal && esAnimalExtinguible(animal)) {
                  victimas.add(animal);
               }
            }

            for (Animal animal : victimas) {
               animal.discard();
            }
         }
      }

      if ("creepers-electricos".equals(id)) {
         for (ServerLevel nivel : server.getAllLevels()) {
            for (Entity entidad : nivel.getAllEntities()) {
               if (entidad instanceof Creeper creeper) {
                  electrificar(creeper);
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void alUnirseAlMundo(EntityJoinLevelEvent e) {
      if (!e.getLevel().isClientSide()) {
         Entity entidad = e.getEntity();
         if (entidad instanceof Creeper creeper) {
            if (Ruleta.cambioActivo("creepers-electricos")) {
               electrificar(creeper);
            }
         } else if (entidad instanceof SnowGolem || entidad instanceof IronGolem golem && golem.isPlayerCreated()) {
            if (!e.loadedFromDisk() && Ruleta.cambioActivo("sin-golems")) {
               e.setCanceled(true);
            }
         } else if (entidad instanceof Animal animal) {
            if (esAnimalExtinguible(animal) && Ruleta.cambioActivo("extincion-animales")) {
               e.setCanceled(true);
            }
         } else if (entidad instanceof Spider arana) {
            potenciarArana(arana);
         } else if (entidad instanceof Zombie zombie) {
            if (esZombieComun(zombie)) {
               if (Ruleta.cambioActivo("zombies-mejorados")) {
                  mejorarZombie(zombie, !e.loadedFromDisk());
               } else if (!e.loadedFromDisk()
                  && !zombie.getTags().contains("ruleta_zombie_trepador")
                  && !zombie.getTags().contains("ruleta_zombie_normal")
                  && Ruleta.cambioActivo("zombies-trepadores")) {
                  zombie.addTag(zombie.getRandom().nextBoolean() ? "ruleta_zombie_trepador" : "ruleta_zombie_normal");
               }

               if (!e.loadedFromDisk() && Ruleta.cambioActivo("zombies-pico-diamante")) {
                  ponerPico(zombie);
               }
            }
         } else if (entidad instanceof ItemEntity item
            && !e.loadedFromDisk()
            && item.getItem().is(ItemTags.SAPLINGS)
            && item.getOwner() == null
            && Ruleta.cambioActivo("sin-brotes")
            && hayHojasCerca(e.getLevel(), item.blockPosition())) {
            e.setCanceled(true);
         }
      }
   }

   private static EntityType<?> tipoNucleeper() {
      if (!nucleeperResuelto) {
         nucleeperResuelto = true;
         tipoNucleeper = NuclearCreeperMod.NUCLEAR_CREEPER.get();
      }

      return tipoNucleeper;
   }

   private static boolean esAnimalExtinguible(Animal animal) {
      if (!(animal instanceof Enemy) && !animal.hasCustomName() && !animal.isLeashed()) {
         return animal instanceof TamableAnimal mascota && mascota.isTame() ? false : !(animal instanceof AbstractHorse caballo && caballo.isTamed());
      } else {
         return false;
      }
   }

   @SubscribeEvent
   public static void alHerir(LivingHurtEvent e) {
      LivingEntity victima = e.getEntity();
      if (!victima.level().isClientSide()) {
         if (!e.getSource().is(DamageTypes.IN_FIRE) && !e.getSource().is(DamageTypes.ON_FIRE) && !e.getSource().is(DamageTypes.LAVA)) {
            if (e.getSource().is(DamageTypes.FALL)) {
               if (!eraPerla(victima) && Ruleta.cambioActivo("dano-caida")) {
                  e.setAmount(e.getAmount() * 2.0F);
               }
            } else if (e.getSource().is(DamageTypes.DROWN) && Ruleta.cambioActivo("ahogamiento-aumentado")) {
               e.setAmount(e.getAmount() * 10.0F);
            }
         } else {
            if (Ruleta.cambioActivo("dano-lava-fuego")) {
               e.setAmount(e.getAmount() * 3.0F);
            }

            if (Ruleta.cambioActivo("dano-fuego-lava-x10")) {
               e.setAmount(e.getAmount() * 10.0F);
            }
         }

         if (e.getSource().getDirectEntity() instanceof Endermite
            && victima instanceof Player
            && e.getAmount() > 0.0F
            && Ruleta.cambioActivo("endermite-100-dano")) {
            e.setAmount(DANO_ENDERMITE());
         }

         if (e.getSource().getDirectEntity() instanceof EnderMan
            && victima instanceof Player
            && e.getAmount() > 0.0F
            && Ruleta.cambioActivo("enderman-100-dano")) {
            e.setAmount(DANO_ENDERMAN());
         }

         if (e.getSource().getDirectEntity() instanceof Spider && victima instanceof Player && e.getAmount() > 0.0F && Ruleta.cambioActivo("aranas-telarana")) {
            ponerTelarana(victima.level(), victima.blockPosition());
         }
      }
   }

   @SubscribeEvent
   public static void alMorir(LivingDeathEvent e) {
      if (e.getEntity() instanceof ServerPlayer jugador) {
         ARDIENDO.remove(jugador.getUUID());
         PLACAS.remove(jugador.getUUID());
      }

      if (e.getEntity() instanceof Spider arana && !arana.level().isClientSide() && Ruleta.cambioActivo("aranas-telarana")) {
         ponerTelarana(arana.level(), arana.blockPosition());
      }
   }

   private static void ponerTelarana(Level nivel, BlockPos pos) {
      if (nivel.getGameRules().get(GameRules.MOB_GRIEFING)) {
         BlockState actual = nivel.getBlockState(pos);
         if ((actual.isAir() || actual.canBeReplaced()) && actual.getFluidState().isEmpty()) {
            nivel.setBlockAndUpdate(pos, Blocks.COBWEB.defaultBlockState());
         }
      }
   }

   static void marcarPerla(ServerPlayer jugador) {
      PERLAS.put(jugador.getUUID(), jugador.level().getGameTime());
   }

   private static boolean eraPerla(LivingEntity victima) {
      if (!(victima instanceof ServerPlayer jugador)) {
         return false;
      } else {
         Long tick = PERLAS.remove(jugador.getUUID());
         return tick != null && tick == jugador.level().getGameTime();
      }
   }

   private static void potenciarArana(Spider arana) {
      if (Ruleta.cambioActivo("aranas-cueva-overworld")) {
         darEfectoInfinito(arana, MobEffects.MOVEMENT_SPEED, 0);
      }

      if (Ruleta.cambioActivo("aranas-fuerza")) {
         darEfectoInfinito(arana, MobEffects.DAMAGE_BOOST, 2);
      }

      if (Ruleta.cambioActivo("aranas-fuerza-iv")) {
         darEfectoInfinito(arana, MobEffects.DAMAGE_BOOST, 3);
      }
   }

   private static void darEfectoInfinito(LivingEntity criatura, Holder<MobEffect> efecto, int amplificador) {
      MobEffectInstance actual = criatura.getEffect(efecto);
      if (actual == null || actual.getAmplifier() < amplificador || actual.getDuration() != -1) {
         criatura.addEffect(new MobEffectInstance(efecto, -1, amplificador, false, false));
      }
   }

   @SubscribeEvent
   public static void alMedirVisibilidad(LivingEvent.LivingVisibilityEvent e) {
      LivingEntity visto = e.getEntity();
      if (visto.isInvisible() && !visto.level().isClientSide() && Ruleta.cambioActivo("criaturas-detectan-invisibles")) {
         int piezas = 0;
         int total = 0;

         for (ItemStack pieza : visto.getArmorSlots()) {
            total++;
            if (!pieza.isEmpty()) {
               piezas++;
            }
         }

         double cobertura = total > 0 ? (double)piezas / total : 0.0;
         e.modifyVisibility(1.0 / (0.7 * Math.max(0.1, cobertura)));
      }
   }

   private static void electrificar(Creeper creeper) {
      if (!creeper.isPowered() && creeper.getType() != tipoNucleeper()) {
         CompoundTag tag = new CompoundTag();
         creeper.addAdditionalSaveData(tag);
         tag.putBoolean("powered", true);
         creeper.readAdditionalSaveData(tag);
      }
   }

   private static boolean hayHojasCerca(Level nivel, BlockPos centro) {
      for (BlockPos pos : BlockPos.betweenClosed(centro.offset(-1, -1, -1), centro.offset(1, 1, 1))) {
         if (nivel.getBlockState(pos).is(BlockTags.LEAVES)) {
            return true;
         }
      }

      return false;
   }

   @SubscribeEvent
   public static void alSoltarBotin(LivingDropsEvent e) {
      LivingEntity victima = e.getEntity();
      if (victima instanceof Warden && !victima.level().isClientSide() && Ruleta.cambioActivo("warden-sculk-shrieker")) {
         ItemEntity shrieker = new ItemEntity(victima.level(), victima.getX(), victima.getY() + 0.5, victima.getZ(), new ItemStack(Items.SCULK_SHRIEKER));
         shrieker.setDefaultPickUpDelay();
         e.getDrops().add(shrieker);
      }
   }

   private static boolean sinLeche(Level nivel) {
      return nivel.isClientSide() ? activosCliente.contains("sin-leche") : Ruleta.cambioActivo("sin-leche");
   }

   @SubscribeEvent
   public static void alInteractuarConEntidadEspecifica(PlayerInteractEvent.EntityInteractSpecific e) {
      if (e.getTarget() instanceof Cow && e.getItemStack().is(Items.BUCKET) && sinLeche(e.getLevel())) {
         e.setCanceled(true);
         e.setCancellationResult(InteractionResult.FAIL);
      }
   }

   @SubscribeEvent
   public static void alInteractuarConEntidad(PlayerInteractEvent.EntityInteract e) {
      if (e.getTarget() instanceof Cow && e.getItemStack().is(Items.BUCKET) && sinLeche(e.getLevel())) {
         e.setCanceled(true);
         e.setCancellationResult(InteractionResult.FAIL);
         if (e.getEntity() instanceof ServerPlayer jugador) {
            jugador.sendOverlayMessage(Component.literal("Reviil ordeñó a las vacas: ya no se puede sacar leche."));
            jugador.inventoryMenu.sendAllDataToRemote();
         }
      }
   }

   public static void alTickJugador(ServerPlayer jugador) {
      respirarBajoElAgua(jugador);
      ahogamientoAumentado(jugador);
      aparicionesEnElProfundo(jugador);
      aparicionesEnElNether(jugador);
      aparicionesTerrestres(jugador);
      fuegoEnElNether(jugador);
      sinRegeneracion(jugador);
      placaDePresion(jugador);
      misOjos(jugador);
   }

   public static void alDesconectarse(ServerPlayer jugador) {
      ARDIENDO.remove(jugador.getUUID());
      PLACAS.remove(jugador.getUUID());
      AIRE.remove(jugador.getUUID());
   }

   private static void fuegoEnElNether(ServerPlayer jugador) {
      UUID id = jugador.getUUID();
      if (jugador.level().dimension() == Level.NETHER && jugador.isAlive() && !jugador.isCreative() && !jugador.isSpectator()) {
         boolean arde = false;
         if (Ruleta.cambioActivo("fuego-sin-armadura-nether")) {
            for (ItemStack pieza : jugador.getArmorSlots()) {
               if (pieza.isEmpty()) {
                  arde = true;
                  break;
               }
            }
         }

         if (Ruleta.cambioActivo("fuego-infinito-nether")) {
            if (jugador.isOnFire()) {
               ARDIENDO.add(id);
            }

            arde |= ARDIENDO.contains(id);
         }

         if (arde && jugador.getRemainingFireTicks() < 40) {
            jugador.setRemainingFireTicks(200);
         }
      } else {
         ARDIENDO.remove(id);
      }
   }

   private static void sinRegeneracion(ServerPlayer jugador) {
      MinecraftServer server = jugador.getServer();
      if (server != null && jugador.tickCount % 100 == 0 && Ruleta.cambioActivo("sin-regeneracion")) {
         BooleanValue regla = (BooleanValue)server.getGameRules().getRule(GameRules.RULE_NATURAL_REGENERATION);
         if (regla.get()) {
            regla.set(false, server);
         }
      }
   }

   private static void placaDePresion(ServerPlayer jugador) {
      UUID id = jugador.getUUID();
      if (jugador.isAlive() && !jugador.isCreative() && !jugador.isSpectator() && Ruleta.cambioActivo("dano-placa")) {
         BlockPos pos = jugador.blockPosition();
         if (!jugador.level().getBlockState(pos).is(BlockTags.PRESSURE_PLATES)) {
            PLACAS.remove(id);
         } else {
            if (!pos.equals(PLACAS.get(id))) {
               PLACAS.put(id, pos.immutable());
               jugador.hurt(Ruleta.fuenteLetal(jugador, "ruleta.placa", " murió al pisar una placa de presión"), 1.0E12F);
            }
         }
      } else {
         PLACAS.remove(id);
      }
   }

   @SubscribeEvent
   public static void alUsarBloque(PlayerInteractEvent.RightClickBlock e) {
      if (!e.getLevel().isClientSide() && e.getHand() == InteractionHand.MAIN_HAND && e.getEntity() instanceof ServerPlayer jugador) {
         BlockState estado = e.getLevel().getBlockState(e.getPos());
         boolean boton = estado.is(BlockTags.BUTTONS);
         boolean puerta = !boton && esPuertaDeMano(estado);
         if (!jugador.isCreative() && !jugador.isSpectator() && (boton || puerta)) {
            if (!jugador.isSecondaryUseActive() || jugador.getMainHandItem().isEmpty() && jugador.getOffhandItem().isEmpty()) {
               if (boton && Ruleta.cambioActivo("dano-boton")) {
                  jugador.hurt(Ruleta.fuenteLetal(jugador, "ruleta.boton", " murió al pulsar un botón"), 4.0E27F);
               } else if (puerta && Ruleta.cambioActivo("dano-puertas")) {
                  jugador.hurt(Ruleta.fuenteLetal(jugador, "ruleta.puerta", " murió al abrir una puerta"), 1.0E12F);
               }
            }
         }
      }
   }

   private static boolean esPuertaDeMano(BlockState estado) {
      Block bloque = estado.getBlock();
      if (bloque instanceof DoorBlock && bloque != Blocks.IRON_DOOR) {
         Identifier clave = BuiltInRegistries.BLOCK.getKey(bloque);
         return clave == null || !"securitycraft".equals(clave.getNamespace());
      } else {
         return false;
      }
   }

   @SubscribeEvent
   public static void alTerminarDeComer(LivingEntityUseItemEvent.Finish e) {
      if (!e.getEntity().level().isClientSide() && e.getEntity() instanceof ServerPlayer jugador) {
         ItemStack comida = e.getItem();
         if ((comida.is(Items.GOLDEN_APPLE) || comida.is(Items.ENCHANTED_GOLDEN_APPLE)) && Ruleta.cambioActivo("sin-absorcion")) {
            jugador.removeEffect(MobEffects.ABSORPTION);
         }
      }
   }

   private static void ahogamientoAumentado(ServerPlayer jugador) {
      UUID id = jugador.getUUID();
      if (jugador.isAlive() && !jugador.isCreative() && !jugador.isSpectator() && Ruleta.cambioActivo("ahogamiento-aumentado")) {
         int aire = jugador.getAirSupply();
         Integer antes = AIRE.get(id);
         if (antes != null && aire < antes) {
            aire = Math.max(-19, aire - 4);
            jugador.setAirSupply(aire);
         }

         AIRE.put(id, aire);
      } else {
         AIRE.remove(id);
      }
   }

   private static void ponerPico(Zombie zombie) {
      EquipmentSlot mano = Ruleta.cambioActivo("zombies-mejorados") ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
      if (!zombie.getItemBySlot(mano).is(Items.DIAMOND_PICKAXE)) {
         zombie.setItemSlot(mano, new ItemStack(Items.DIAMOND_PICKAXE));
      }
   }

   private static void respirarBajoElAgua(ServerPlayer jugador) {
      if (Ruleta.cambioActivo("respirar-bajo-agua") && jugador.isAlive() && !jugador.isCreative() && !jugador.isSpectator()) {
         Level nivel = jugador.level();
         BlockPos ojos = BlockPos.containing(jugador.getEyePosition());
         BlockState estado = nivel.getBlockState(ojos);
         if ((estado.getBlock() instanceof DoorBlock || estado.getBlock() instanceof TrapDoorBlock) && Vanilla.es(estado.getBlock())) {
            boolean aguaEncima = nivel.getFluidState(ojos.above()).is(FluidTags.WATER);
            if (!aguaEncima && estado.getBlock() instanceof DoorBlock && estado.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER) {
               aguaEncima = nivel.getBlockState(ojos.above()).getBlock() instanceof DoorBlock && nivel.getFluidState(ojos.above(2)).is(FluidTags.WATER);
            }

            if (estado.getFluidState().isEmpty() && aguaEncima && !MobEffectUtil.hasWaterBreathing(jugador)) {
               int respiracion = (int)Math.round(jugador.getAttributeValue(Attributes.OXYGEN_BONUS));
               boolean pierde = respiracion <= 0 || jugador.getRandom().nextInt(respiracion + 1) == 0;
               int aire = jugador.getAirSupply() - (pierde ? 5 : 4);
               if (aire <= -20) {
                  aire = 0;
                  jugador.hurt(jugador.damageSources().drown(), 2.0F);
               }

               jugador.setAirSupply(aire);
            }
         }
      }
   }

   private static void llenarTrampillasConAgua(MinecraftServer server) {
      int radio = 32;

      for (ServerPlayer jugador : server.getPlayerList().getPlayers()) {
         ServerLevel nivel = jugador.level();
         BlockPos c = jugador.blockPosition();
         int y0 = Math.max(nivel.getMinY(), c.getY() - 32);
         int y1 = Math.min(nivel.getMaxY() - 1, c.getY() + 32);
         MutableBlockPos p = new MutableBlockPos();
         MutableBlockPos q = new MutableBlockPos();

         for (int x = c.getX() - 32; x <= c.getX() + 32; x++) {
            for (int z = c.getZ() - 32; z <= c.getZ() + 32; z++) {
               if (nivel.hasChunk(x >> 4, z >> 4)) {
                  for (int y = y0; y <= y1; y++) {
                     p.set(x, y, z);
                     BlockState estado = nivel.getBlockState(p);
                     boolean puerta = estado.getBlock() instanceof DoorBlock;
                     if ((puerta || estado.getBlock() instanceof TrapDoorBlock)
                        && Vanilla.es(estado.getBlock())
                        && estado.hasProperty(BlockStateProperties.WATERLOGGED)
                        && !(Boolean)estado.getValue(BlockStateProperties.WATERLOGGED)) {
                        for (Direction d : Direction.values()) {
                           q.set(p).move(d);
                           if ((!puerta || !(nivel.getBlockState(q).getBlock() instanceof DoorBlock)) && nivel.getFluidState(q).is(FluidTags.WATER)) {
                              nivel.setBlock(p, (BlockState)estado.setValue(BlockStateProperties.WATERLOGGED, true), 2);
                              nivel.scheduleTick(p, Fluids.WATER, Fluids.WATER.getTickDelay(nivel));
                              break;
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static void aparicionesEnElProfundo(ServerPlayer jugador) {
      if (jugador.tickCount % INTERVALO_PROFUNDO() == 0 && !jugador.isSpectator()) {
         ServerLevel nivel = jugador.level();
         if (nivel.getDifficulty() != Difficulty.PEACEFUL
            && nivel.getGameRules().get(GameRules.SPAWN_MOBS)
            && nivel.getBiome(jugador.blockPosition()).is(Biomes.DEEP_DARK)) {
            RandomSource azar = nivel.getRandom();
            if (azar.nextFloat() < 0.25F && Ruleta.cambioActivo("bombas-warden")) {
               aparecerBomba(nivel, jugador, azar);
            }

            if (azar.nextFloat() < 0.04F && Ruleta.cambioActivo("warden-deep-dark")) {
               aparecerWarden(nivel, jugador, azar);
            }
         }
      }
   }

   private static void aparecerWarden(ServerLevel nivel, ServerPlayer jugador, RandomSource azar) {
      if (nivel.getEntitiesOfClass(Warden.class, jugador.getBoundingBox().inflate(64.0)).isEmpty()) {
         for (int intento = 0; intento < 12; intento++) {
            double angulo = azar.nextDouble() * Math.PI * 2.0;
            double distancia = 18.0 + azar.nextDouble() * 14.0;
            int x = Mth.floor(jugador.getX() + Math.cos(angulo) * distancia);
            int z = Mth.floor(jugador.getZ() + Math.sin(angulo) * distancia);
            BlockPos suelo = buscarSuelo(nivel, x, Mth.floor(jugador.getY()) + 6, z);
            if (suelo != null) {
               Warden warden = (Warden)EntityType.WARDEN.create(nivel);
               if (warden == null) {
                  return;
               }

               warden.snapTo(suelo.getX() + 0.5, suelo.getY(), suelo.getZ() + 0.5, azar.nextFloat() * 360.0F, 0.0F);
               warden.finalizeSpawn(nivel, nivel.getCurrentDifficultyAt(suelo), EntitySpawnReason.TRIGGERED, null);
               nivel.addFreshEntityWithPassengers(warden);
               return;
            }
         }
      }
   }

   private static BlockPos buscarSuelo(ServerLevel nivel, int x, int yInicio, int z) {
      for (int y = yInicio; y > yInicio - 16; y--) {
         BlockPos pos = new BlockPos(x, y, z);
         BlockPos debajo = pos.below();
         if (nivel.getBlockState(debajo).isFaceSturdy(nivel, debajo, Direction.UP)
            && nivel.getFluidState(pos).isEmpty()
            && nivel.noCollision(EntityType.WARDEN.getSpawnAABB(x + 0.5, y, z + 0.5))) {
            return nivel.getBiome(pos).is(Biomes.DEEP_DARK) && !nivel.canSeeSky(pos) ? pos : null;
         }
      }

      return null;
   }

   private static void aparecerBomba(ServerLevel nivel, ServerPlayer jugador, RandomSource azar) {
      if (BombaWarden.esPresa(jugador)) {
         if (nivel.getEntitiesOfClass(BombaWarden.class, jugador.getBoundingBox().inflate(48.0)).size() < MAX_BOMBAS()) {
            for (int intento = 0; intento < 12; intento++) {
               double angulo = azar.nextDouble() * Math.PI * 2.0;
               double distancia = 12.0 + azar.nextDouble() * 14.0;
               double x = jugador.getX() + Math.cos(angulo) * distancia;
               double y = jugador.getY() + azar.nextInt(7);
               double z = jugador.getZ() + Math.sin(angulo) * distancia;
               BlockPos pos = BlockPos.containing(x, y, z);
               if (nivel.getBiome(pos).is(Biomes.DEEP_DARK) && nivel.noCollision(MobsNuevos.BOMBA_WARDEN.get().getSpawnAABB(x, y, z))) {
                  Vex bomba = (Vex)MobsNuevos.BOMBA_WARDEN.get().create(nivel);
                  if (bomba == null) {
                     return;
                  }

                  bomba.snapTo(x, y, z, azar.nextFloat() * 360.0F, 0.0F);
                  bomba.addTag("ruleta_bomba_warden");
                  bomba.setBoundOrigin(pos);
                  bomba.setTarget(jugador);
                  nivel.addFreshEntity(bomba);
                  return;
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void alTickCriatura(LivingEvent.LivingTickEvent e) {
      LivingEntity criatura = e.getEntity();
      if (!criatura.level().isClientSide()) {
         if (criatura instanceof Zombie zombie) {
            tickZombieTrepador(zombie);
            tickZombieInteligente(zombie);
         } else if (criatura instanceof Mob mob && mob.getType() == tipoNucleeper()) {
            tickNucleeper(mob);
         } else if (criatura instanceof BombaWarden bomba && !bomba.isRemoved()) {
            ServerLevel nivel = (ServerLevel)bomba.level();
            LivingEntity objetivo = bomba.getTarget();
            if (!BombaWarden.esPresa(objetivo)) {
               objetivo = null;
               bomba.setTarget(null);
               if (bomba.tickCount % 20 == 0) {
                  objetivo = nivel.getNearestPlayer(bomba.getX(), bomba.getY(), bomba.getZ(), 32.0, true);
                  if (!BombaWarden.esPresa(objetivo)) {
                     objetivo = null;
                  }

                  bomba.setTarget(objetivo);
               }
            }

            if (BombaWarden.esPresa(objetivo) && bomba.getBoundingBox().inflate(0.08).intersects(objetivo.getBoundingBox())) {
               detonar(nivel, bomba);
            }
         }
      }
   }

   private static boolean esZombieComun(Zombie zombie) {
      return zombie.getType() == EntityType.ZOMBIE || zombie.getType() == EntityType.ZOMBIE_VILLAGER;
   }

   private static void mejorarZombie(Zombie zombie, boolean conEspada) {
      zombie.removeTag("ruleta_zombie_normal");
      zombie.addTag("ruleta_zombie_trepador");
      Holder<Enchantment> filo = zombie.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SHARPNESS);
      if (conEspada && EnchantmentHelper.getItemEnchantmentLevel(filo, zombie.getMainHandItem()) < 5) {
         ItemStack espada = new ItemStack(Items.IRON_SWORD);
         espada.enchant(filo, 5);
         zombie.setItemSlot(EquipmentSlot.MAINHAND, espada);
      }
   }

   private static void tickZombieTrepador(Zombie zombie) {
      if (zombie.isAlive() && zombie.getTags().contains("ruleta_zombie_trepador")) {
         Level nivel = zombie.level();
         if (zombie.getRemainingFireTicks() > 0 && nivel.isDay() && !zombie.isInLava() && nivel.canSeeSky(zombie.blockPosition())) {
            zombie.clearFire();
         }

         LivingEntity objetivo = zombie.getTarget();
         if (objetivo != null && objetivo.isAlive() && !(zombie.distanceToSqr(objetivo) > 576.0)) {
            if (zombie.getNavigation().isDone()) {
               zombie.getMoveControl().setWantedPosition(objetivo.getX(), objetivo.getY(), objetivo.getZ(), 1.0);
            }

            if (zombie.horizontalCollision) {
               Vec3 movimiento = zombie.getDeltaMovement();
               zombie.setDeltaMovement(movimiento.x, 0.2, movimiento.z);
               zombie.resetFallDistance();
            }
         }
      }
   }

   private static void tickNucleeper(Mob nucleeper) {
      if (nucleeper.isAlive() && Ruleta.cambioActivo("nucleepers-techo-nether")) {
         ServerLevel nivel = (ServerLevel)nucleeper.level();
         LivingEntity objetivo = nucleeper.getTarget();
         if ((objetivo == null || !objetivo.isAlive()) && nucleeper.tickCount % 20 == 0) {
            objetivo = nivel.getNearestPlayer(nucleeper, 48.0);
            nucleeper.setTarget(objetivo);
         }

         if (objetivo != null && objetivo.isAlive() && !(nucleeper.distanceToSqr(objetivo) > 2304.0)) {
            if (nucleeper.getNavigation().isDone()) {
               nucleeper.getMoveControl().setWantedPosition(objetivo.getX(), objetivo.getY(), objetivo.getZ(), 1.0);
            }

            if (nucleeper.tickCount % 5 == 0 && nivel.getGameRules().get(GameRules.MOB_GRIEFING)) {
               abrirCamino(nivel, nucleeper, objetivo);
            }
         }
      }
   }

   private static void abrirCamino(ServerLevel nivel, Mob criatura, LivingEntity objetivo) {
      abrirCamino(nivel, criatura, objetivo, Float.MAX_VALUE, true);
   }

   private static void abrirCamino(ServerLevel nivel, Mob criatura, LivingEntity objetivo, float durezaMax, boolean soltar) {
      double dx = objetivo.getX() - criatura.getX();
      double dz = objetivo.getZ() - criatura.getZ();
      double dy = objetivo.getY() - criatura.getY();
      double largo = Math.sqrt(dx * dx + dz * dz);
      List<BlockPos> candidatos = new ArrayList<>();
      if (largo > 0.5) {
         double ux = dx / largo;
         double uz = dz / largo;
         candidatos.add(BlockPos.containing(criatura.getX() + ux, criatura.getY(), criatura.getZ() + uz));
         candidatos.add(BlockPos.containing(criatura.getX() + ux, criatura.getY() + 1.0, criatura.getZ() + uz));
         if (dy > 1.5) {
            candidatos.add(BlockPos.containing(criatura.getX() + ux, criatura.getY() + 2.0, criatura.getZ() + uz));
         }
      }

      if (dy > 1.5) {
         candidatos.add(BlockPos.containing(criatura.getX(), criatura.getY() + 2.0, criatura.getZ()));
      } else if (dy < -1.5) {
         candidatos.add(BlockPos.containing(criatura.getX(), criatura.getY() - 1.0, criatura.getZ()));
      }

      for (BlockPos pos : candidatos) {
         BlockState estado = nivel.getBlockState(pos);
         float dureza = estado.getDestroySpeed(nivel, pos);
         if (!estado.isAir() && estado.getFluidState().isEmpty() && dureza >= 0.0F && dureza <= durezaMax && !Bunkers.protegido(nivel, pos, estado)) {
            nivel.destroyBlock(pos, soltar, criatura);
         }
      }
   }

   @SubscribeEvent(priority = EventPriority.LOWEST)
   public static void alRomperBloque(BlockEvent.BreakEvent e) {
      if (e.getState().is(Blocks.COBWEB) && e.getLevel() instanceof ServerLevel nivel && nivel.getDifficulty() != Difficulty.PEACEFUL) {
         if (Ruleta.cambioActivo("aranas-telaranas") && !(nivel.getRandom().nextFloat() >= PROB_ARANA_CUEVA())) {
            CaveSpider arana = (CaveSpider)EntityType.CAVE_SPIDER.create(nivel);
            if (arana != null) {
               BlockPos pos = e.getPos();
               arana.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, nivel.getRandom().nextFloat() * 360.0F, 0.0F);
               arana.setTarget(e.getPlayer());
               nivel.addFreshEntity(arana);
            }
         }
      }
   }

   private static void aparicionesTerrestres(ServerPlayer jugador) {
      if (jugador.tickCount % INTERVALO_PROFUNDO() == 0 && !jugador.isSpectator()) {
         ServerLevel nivel = jugador.level();
         if (nivel.getDifficulty() != Difficulty.PEACEFUL && nivel.getGameRules().get(GameRules.SPAWN_MOBS)) {
            RandomSource azar = nivel.getRandom();
            AABB zona = jugador.getBoundingBox().inflate(48.0);
            if (nivel.dimension() == Level.NETHER) {
               if (azar.nextFloat() < 0.35F
                  && Ruleta.cambioActivo("zombies-nether")
                  && nivel.getEntitiesOfClass(Zombie.class, zona, z -> z.getType() == EntityType.ZOMBIE).size() < 8) {
                  aparecerCerca(nivel, jugador, azar, EntityType.ZOMBIE, false);
               }

               if (azar.nextFloat() < 0.25F && Ruleta.cambioActivo("wither-skeleton-nether") && nivel.getEntitiesOfClass(WitherSkeleton.class, zona).size() < 4
                  )
                {
                  aparecerCerca(nivel, jugador, azar, EntityType.WITHER_SKELETON, false);
               }

               EntityType<?> nucleeper = tipoNucleeper();
               if (nucleeper != null
                  && jugador.getY() >= 128.0
                  && azar.nextFloat() < 0.25F
                  && Ruleta.cambioActivo("nucleepers-techo-nether")
                  && nivel.getEntitiesOfClass(Mob.class, zona, m -> m.getType() == nucleeper).size() < 3) {
                  EntityType<Mob> tipo = (EntityType<Mob>)nucleeper;
                  aparecerCerca(nivel, jugador, azar, tipo, false);
               }
            } else if (nivel.dimension() == Level.OVERWORLD && nivel.isNight()) {
               if (azar.nextFloat() < 0.25F && Ruleta.cambioActivo("stray-overworld") && nivel.getEntitiesOfClass(Stray.class, zona).size() < 4) {
                  aparecerCerca(nivel, jugador, azar, EntityType.STRAY, true);
               }

               if (azar.nextFloat() < 0.25F && Ruleta.cambioActivo("aranas-cueva-overworld") && nivel.getEntitiesOfClass(CaveSpider.class, zona).size() < 4) {
                  aparecerCerca(nivel, jugador, azar, EntityType.CAVE_SPIDER, true);
               }

               if (azar.nextFloat() < 0.25F && Ruleta.cambioActivo("wither-skeleton-noche") && nivel.getEntitiesOfClass(WitherSkeleton.class, zona).size() < 4) {
                  aparecerCerca(nivel, jugador, azar, EntityType.WITHER_SKELETON, true);
               }

               if (azar.nextFloat() < 0.25F && Ruleta.cambioActivo("bogged-noche") && nivel.getEntitiesOfClass(Bogged.class, zona).size() < 4) {
                  aparecerCerca(nivel, jugador, azar, EntityType.BOGGED, true);
               }
            }

            if (nivel.dimension() == Level.OVERWORLD) {
               EntityType<?> nucleeperNieve = tipoNucleeper();
               if (nucleeperNieve != null
                  && azar.nextFloat() < 0.2F
                  && Ruleta.cambioActivo("mob-creeper-nuclear-nieve")
                  && ((Biome)nivel.getBiome(jugador.blockPosition()).value()).coldEnoughToSnow(jugador.blockPosition())
                  && nivel.getEntitiesOfClass(Mob.class, zona, m -> m.getType() == nucleeperNieve).size() < 2) {
                  EntityType<Mob> tipo = (EntityType<Mob>)nucleeperNieve;
                  Mob nuevo = aparecerCerca(nivel, jugador, azar, tipo, false);
                  if (nuevo != null && !((Biome)nivel.getBiome(nuevo.blockPosition()).value()).coldEnoughToSnow(nuevo.blockPosition())) {
                     nuevo.discard();
                  }
               }

               if (azar.nextFloat() < 0.25F && Ruleta.cambioActivo("aranas-fuerza-iv") && nivel.getEntitiesOfClass(CaveSpider.class, zona).size() < 4) {
                  aparecerCerca(nivel, jugador, azar, EntityType.CAVE_SPIDER, false, true, Integer.MAX_VALUE);
               }

               if (jugador.getY() <= CAPA_INTELIGENTES()
                  && azar.nextFloat() < 0.25F
                  && Ruleta.cambioActivo("zombies-inteligentes")
                  && nivel.getEntitiesOfClass(Zombie.class, zona, z -> z.getTags().contains("ruleta_zombie_inteligente")).size() < 5) {
                  Zombie inteligente = aparecerCerca(nivel, jugador, azar, EntityType.ZOMBIE, true, false, CAPA_INTELIGENTES());
                  if (inteligente != null) {
                     inteligente.addTag("ruleta_zombie_inteligente");
                  }
               }
            }
         }
      }
   }

   private static <T extends Mob> T aparecerCerca(ServerLevel nivel, ServerPlayer jugador, RandomSource azar, EntityType<T> tipo, boolean exigirOscuridad) {
      return aparecerCerca(nivel, jugador, azar, tipo, exigirOscuridad, false, Integer.MAX_VALUE);
   }

   private static <T extends Mob> T aparecerCerca(
      ServerLevel nivel, ServerPlayer jugador, RandomSource azar, EntityType<T> tipo, boolean exigirOscuridad, boolean soloHielo, int maxY
   ) {
      for (int intento = 0; intento < 12; intento++) {
         double angulo = azar.nextDouble() * Math.PI * 2.0;
         double distancia = 16.0 + azar.nextDouble() * 16.0;
         int x = Mth.floor(jugador.getX() + Math.cos(angulo) * distancia);
         int z = Mth.floor(jugador.getZ() + Math.sin(angulo) * distancia);
         int yInicio = Mth.floor(jugador.getY()) + 6;
         if (nivel.isLoaded(new BlockPos(x, yInicio, z))) {
            for (int y = yInicio; y > yInicio - 16; y--) {
               BlockPos pos = new BlockPos(x, y, z);
               BlockPos debajo = pos.below();
               boolean sueloValido = soloHielo
                  ? nivel.getBlockState(debajo).is(BlockTags.ICE)
                  : nivel.getBlockState(debajo).isFaceSturdy(nivel, debajo, Direction.UP);
               if (sueloValido && y <= maxY && nivel.getFluidState(pos).isEmpty() && nivel.noCollision(tipo.getSpawnAABB(x + 0.5, y, z + 0.5))) {
                  if (!exigirOscuridad || Monster.isDarkEnoughToSpawn(nivel, pos, azar)) {
                     T criatura = (T)tipo.create(nivel);
                     if (criatura == null) {
                        return null;
                     }

                     criatura.snapTo(x + 0.5, y, z + 0.5, azar.nextFloat() * 360.0F, 0.0F);
                     criatura.finalizeSpawn(nivel, nivel.getCurrentDifficultyAt(pos), EntitySpawnReason.NATURAL, null);
                     nivel.addFreshEntityWithPassengers(criatura);
                     return criatura;
                  }
                  break;
               }
            }
         }
      }

      return null;
   }

   private static void aparicionesEnElNether(ServerPlayer jugador) {
      if (jugador.tickCount % INTERVALO_PROFUNDO() == 0 && !jugador.isSpectator()) {
         ServerLevel nivel = jugador.level();
         if (nivel.dimension() == Level.NETHER
            && nivel.getDifficulty() != Difficulty.PEACEFUL
            && nivel.getGameRules().get(GameRules.SPAWN_MOBS)
            && Ruleta.cambioActivo("blazes-nether")) {
            RandomSource azar = nivel.getRandom();
            if (!(azar.nextFloat() >= 0.3F) && nivel.getEntitiesOfClass(Blaze.class, jugador.getBoundingBox().inflate(48.0)).size() < MAX_BLAZES()) {
               for (int intento = 0; intento < 12; intento++) {
                  double angulo = azar.nextDouble() * Math.PI * 2.0;
                  double distancia = 14.0 + azar.nextDouble() * 14.0;
                  double x = jugador.getX() + Math.cos(angulo) * distancia;
                  double y = jugador.getY() + azar.nextInt(9) - 3.0;
                  double z = jugador.getZ() + Math.sin(angulo) * distancia;
                  BlockPos pos = BlockPos.containing(x, y, z);
                  if (nivel.isLoaded(pos) && nivel.getFluidState(pos).isEmpty() && nivel.noCollision(EntityType.BLAZE.getSpawnAABB(x, y, z))) {
                     Blaze blaze = (Blaze)EntityType.BLAZE.create(nivel);
                     if (blaze == null) {
                        return;
                     }

                     blaze.snapTo(x, y, z, azar.nextFloat() * 360.0F, 0.0F);
                     blaze.finalizeSpawn(nivel, nivel.getCurrentDifficultyAt(pos), EntitySpawnReason.NATURAL, null);
                     nivel.addFreshEntityWithPassengers(blaze);
                     return;
                  }
               }
            }
         }
      }
   }

   private static void misOjos(ServerPlayer jugador) {
      if (jugador.tickCount % 5 == 0 && jugador.isAlive() && !jugador.isCreative() && !jugador.isSpectator() && Ruleta.cambioActivo("mis-ojos")) {
         ServerLevel nivel = jugador.level();
         if (nivel.dimension() == Level.OVERWORLD && !nivel.isRaining() && !jugador.hasEffect(MobEffects.BLINDNESS)) {
            float angulo = nivel.getTimeOfDay(1.0F) * (float) (Math.PI * 2);
            Vec3 sol = new Vec3(-Mth.sin(angulo), Mth.cos(angulo), 0.0);
            if (!(sol.y < 0.05) && !(jugador.getViewVector(1.0F).dot(sol) < 0.985)) {
               Vec3 ojos = jugador.getEyePosition();
               BlockHitResult golpe = nivel.clip(
                  new ClipContext(ojos, ojos.add(sol.scale(128.0)), net.minecraft.world.level.ClipContext.Block.VISUAL, Fluid.NONE, jugador)
               );
               if (golpe.getType() == Type.MISS) {
                  jugador.addEffect(new MobEffectInstance(Efectos.MIOPIA.get(), TICKS_OJOS(), 0, false, false, false));
                  jugador.addEffect(new MobEffectInstance(Efectos.DALTONISMO.get(), TICKS_OJOS(), 0, false, false, false));
               }
            }
         }
      }
   }

   static void perlaAterrizo(ServerPlayer jugador, double x, double y, double z) {
      if (Ruleta.cambioActivo("perla-creeper-nuclear") && !(jugador.getRandom().nextFloat() >= PROB_PERLA_NUCLEEPER())) {
         EntityType<?> tipo = tipoNucleeper();
         ServerLevel nivel = jugador.level();
         if (tipo != null && nivel.getDifficulty() != Difficulty.PEACEFUL && tipo.create(nivel) instanceof Mob nucleeper) {
            double[][] desvios = new double[][]{{2.0, 0.0}, {-2.0, 0.0}, {0.0, 2.0}, {0.0, -2.0}, {0.0, 0.0}};

            for (double[] d : desvios) {
               if (nivel.noCollision(tipo.getSpawnAABB(x + d[0], y, z + d[1]))) {
                  nucleeper.snapTo(x + d[0], y, z + d[1], nivel.getRandom().nextFloat() * 360.0F, 0.0F);
                  nucleeper.finalizeSpawn(nivel, nivel.getCurrentDifficultyAt(BlockPos.containing(x, y, z)), EntitySpawnReason.EVENT, null);
                  nucleeper.setTarget(jugador);
                  nivel.addFreshEntityWithPassengers(nucleeper);
                  return;
               }
            }
         }
      }
   }

   private static void tickZombieInteligente(Zombie zombie) {
      if (zombie.isAlive() && zombie.getTags().contains("ruleta_zombie_inteligente")) {
         AttributeInstance velocidad = zombie.getAttribute(Attributes.MOVEMENT_SPEED);
         if (velocidad != null && velocidad.getModifier(UUID_VELOCIDAD_INTELIGENTE) == null) {
            velocidad.addPermanentModifier(new AttributeModifier(UUID_VELOCIDAD_INTELIGENTE, 0.5, Operation.ADD_MULTIPLIED_BASE));
         }

         ServerLevel nivel = (ServerLevel)zombie.level();
         LivingEntity objetivo = zombie.getTarget();
         if ((objetivo == null || !objetivo.isAlive()) && zombie.tickCount % 20 == 0) {
            objetivo = nivel.getNearestPlayer(zombie.getX(), zombie.getY(), zombie.getZ(), 24.0, true);
            zombie.setTarget(objetivo);
         }

         if (objetivo != null && objetivo.isAlive() && !(zombie.distanceToSqr(objetivo) > 576.0)) {
            if (zombie.getNavigation().isDone()) {
               zombie.getMoveControl().setWantedPosition(objetivo.getX(), objetivo.getY(), objetivo.getZ(), 1.0);
            }

            if (nivel.getGameRules().get(GameRules.MOB_GRIEFING)) {
               double dx = objetivo.getX() - zombie.getX();
               double dz = objetivo.getZ() - zombie.getZ();
               double dy = objetivo.getY() - zombie.getY();
               double horizontal = Math.sqrt(dx * dx + dz * dz);
               BlockState piedra = Blocks.COBBLESTONE.defaultBlockState();
               boolean subiendo = dy > 1.5;
               if (subiendo && horizontal >= 1.5) {
                  if (zombie.tickCount % INTERVALO_PICAR() == 0) {
                     Direction rumbo = Direction.getNearest(dx, 0.0, dz);
                     BlockPos pies = zombie.blockPosition();
                     BlockPos escalon = pies.relative(rumbo);
                     romperBloque(nivel, zombie, pies.above(2));
                     romperBloque(nivel, zombie, escalon.above(1));
                     romperBloque(nivel, zombie, escalon.above(2));
                     colocarBloque(nivel, escalon, piedra);
                     if (zombie.onGround()) {
                        zombie.getMoveControl().setWantedPosition(escalon.getX() + 0.5, escalon.getY() + 1.0, escalon.getZ() + 0.5, 1.0);
                        zombie.getJumpControl().jump();
                     }
                  }
               } else {
                  if (zombie.tickCount % INTERVALO_PICAR() == 0) {
                     abrirCamino(nivel, zombie, objetivo, DUREZA_ZOMBIE(), false);
                  }

                  if (subiendo) {
                     if (zombie.onGround()) {
                        if (nivel.getBlockState(zombie.blockPosition().above(2)).getCollisionShape(nivel, zombie.blockPosition().above(2)).isEmpty()) {
                           zombie.getJumpControl().jump();
                        }
                     } else {
                        colocarBloque(nivel, BlockPos.containing(zombie.getX(), zombie.getY() - 1.0, zombie.getZ()), piedra);
                     }
                  } else if (zombie.onGround() && zombie.tickCount % 2 == 0 && dy > -2.0 && horizontal > 1.5) {
                     BlockPos delante = BlockPos.containing(zombie.getX() + dx / horizontal, zombie.getY(), zombie.getZ() + dz / horizontal);
                     if (nivel.getBlockState(delante).isAir() && nivel.getBlockState(delante.below()).isAir()) {
                        colocarBloque(nivel, delante.below(), piedra);
                     }
                  }
               }
            }
         }
      }
   }

   private static void romperBloque(ServerLevel nivel, Mob criatura, BlockPos pos) {
      BlockState estado = nivel.getBlockState(pos);
      float dureza = estado.getDestroySpeed(nivel, pos);
      if (!estado.isAir() && estado.getFluidState().isEmpty() && dureza >= 0.0F && dureza <= DUREZA_ZOMBIE() && !Bunkers.protegido(nivel, pos, estado)) {
         nivel.destroyBlock(pos, false, criatura);
      }
   }

   private static void colocarBloque(ServerLevel nivel, BlockPos pos, BlockState estado) {
      BlockState actual = nivel.getBlockState(pos);
      if ((actual.isAir() || actual.canBeReplaced()) && actual.getFluidState().isEmpty()) {
         nivel.setBlockAndUpdate(pos, estado);
         nivel.playSound(null, pos, estado.getSoundType().getPlaceSound(), SoundSource.HOSTILE, 1.0F, 0.8F);
      }
   }

   private static void detonar(ServerLevel nivel, Vex bomba) {
      if (bomba instanceof BombaWarden b) {
         b.explotar(nivel);
      } else {
         bomba.discard();
      }
   }
}
