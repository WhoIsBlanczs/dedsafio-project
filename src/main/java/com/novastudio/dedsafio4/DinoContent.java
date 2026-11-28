package com.novastudio.dedsafio4;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType.Builder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.phys.AABB;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.util.RandomSource;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.levelgen.Heightmap;

public final class DinoContent {
   private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("nexo_dinos.json");
   public static final Map<String, Integer> CAPS = new LinkedHashMap<>();
   public static EntityType<RaptorEntity> RAPTOR;
   public static EntityType<PterodactiloEntity> PTERODACTILO;
   public static Block CACA_BLOCK;
   static final String[] BEASTS = new String[]{"t_rex", "sauropodo_morado"};
   public static final Map<String, EntityType<DinoBeastEntity>> BEAST_TYPES = new LinkedHashMap<>();

   private DinoContent() {
   }

   private static Identifier id(String var0) {
      return Identifier.fromNamespaceAndPath("nexo", var0);
   }

   public static void register() {
      CAPS.put("raptor", 4);
      CAPS.put("pterodactilo", 3);
      CAPS.put("t_rex", 2);
      CAPS.put("sauropodo_morado", 4);
      load();
      RAPTOR = (EntityType<RaptorEntity>)Registry.register(
         BuiltInRegistries.ENTITY_TYPE,
         id("raptor"),
         Builder.createMob(
               RaptorEntity::new,
               MobCategory.MONSTER,
               var0 -> var0.spawnPlacement(
                     SpawnPlacementTypes.ON_GROUND,
                     Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                     (var0x, var1, var2, var3x, var4x) -> canSpawn(var0x, var1, var2, var3x, var4x, "raptor", 4)
                  )
                  .defaultAttributes(RaptorEntity::createAttributes)
            )
            .sized(1.4F, 1.9F)
            .clientTrackingRange(10)
            .build(com.novastudio.dedsafio4.compat.Ids.entidad("raptor"))
      );
      PTERODACTILO = (EntityType<PterodactiloEntity>)Registry.register(
         BuiltInRegistries.ENTITY_TYPE,
         id("pterodactilo"),
         Builder.createMob(
               PterodactiloEntity::new,
               MobCategory.MONSTER,
               var0 -> var0.spawnPlacement(
                     SpawnPlacementTypes.ON_GROUND,
                     Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                     (var0x, var1, var2, var3x, var4x) -> canSpawn(var0x, var1, var2, var3x, var4x, "pterodactilo", 3)
                  )
                  .defaultAttributes(PterodactiloEntity::createAttributes)
            )
            .sized(1.6F, 0.9F)
            .clientTrackingRange(10)
            .build(com.novastudio.dedsafio4.compat.Ids.entidad("pterodactilo"))
      );
      beast(
         new DinoBeastEntity.Species(
            "t_rex",
            true,
            false,
            150.0,
            0.28,
            16.0,
            1.0,
            0.5F,
            SoundEvents.RAVAGER_AMBIENT,
            SoundEvents.RAVAGER_HURT,
            SoundEvents.RAVAGER_DEATH,
            SoundEvents.RAVAGER_ROAR,
            "run",
            new String[]{"attack", "attack_swing"},
            "roar",
            "eat",
            null,
            "hurt"
         ),
         MobCategory.MONSTER,
         2.6F,
         3.9F,
         2,
         5917234,
         12092974
      );
      beast(
         new DinoBeastEntity.Species(
            "sauropodo_morado",
            false,
            false,
            180.0,
            0.2,
            12.0,
            1.0,
            0.4F,
            SoundEvents.HOGLIN_AMBIENT,
            SoundEvents.HOGLIN_HURT,
            SoundEvents.HOGLIN_DEATH,
            SoundEvents.RAVAGER_ROAR,
            null,
            new String[]{"attack", "tail_whip"},
            "roar",
            "idle_eat",
            "look_around",
            null
         ),
         MobCategory.CREATURE,
         3.2F,
         3.6F,
         4,
         6961802,
         12094168
      );
      item("raptor_spawn_egg", () -> new SpawnEggItem((new Item.Properties()).spawnEgg(RAPTOR)));
      item("pterodactilo_spawn_egg", () -> new SpawnEggItem((new Item.Properties()).spawnEgg(PTERODACTILO)));
      CACA_BLOCK = (Block)Registry.register(BuiltInRegistries.BLOCK, com.novastudio.dedsafio4.compat.Ids.bloque(id("caca_dinosaurio")), new CacaDinoBlock());
      item("caca_dinosaurio", () -> new BlockItem(CACA_BLOCK, new Item.Properties()));
      PteroEscape.register();
   }

   private static void beast(DinoBeastEntity.Species var0, MobCategory var1, float var2, float var3, int var4, int var5, int var6) {
      String var7 = var0.id();
      EntityType var8 = (EntityType)Registry.register(
         BuiltInRegistries.ENTITY_TYPE,
         id(var7),
         Builder.createMob(
               DinoBeastEntity::new,
               var1,
               var3x -> var3x.spawnPlacement(
                     SpawnPlacementTypes.ON_GROUND,
                     Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                     (var2xx, var3xx, var4xx, var5x, var6x) -> canSpawn(var2xx, var3xx, var4xx, var5x, var6x, var7, var4)
                  )
                  .defaultAttributes(() -> DinoBeastEntity.createAttributes(var0))
            )
            .sized(var2, var3)
            .clientTrackingRange(10)
            .build(com.novastudio.dedsafio4.compat.Ids.entidad(var7))
      );
      DinoBeastEntity.SPECIES.put(var8, var0);
      BEAST_TYPES.put(var7, var8);
      item(var7 + "_spawn_egg", () -> new SpawnEggItem((new Item.Properties()).spawnEgg(var8)));
   }

   private static Item item(String var0, java.util.function.Supplier<? extends Item> var1) {
      return (Item)Registry.register(BuiltInRegistries.ITEM, com.novastudio.dedsafio4.compat.Ids.item(id(var0)), var1.get());
   }

   private static <T extends Mob> boolean canSpawn(
      EntityType<T> var0, ServerLevelAccessor var1, EntitySpawnReason var2, BlockPos var3, RandomSource var4, String var5, int var6
   ) {
      if ((var2 == EntitySpawnReason.NATURAL || var2 == EntitySpawnReason.CHUNK_GENERATION) && CAPS.getOrDefault(var5, var6) <= 0) {
         return false;
      } else if (var0.getCategory() == MobCategory.MONSTER && var1.getDifficulty() == Difficulty.PEACEFUL) {
         return false;
      } else {
         return !Mob.checkMobSpawnRules(var0, var1, var2, var3, var4)
            ? false
            : var1.getEntitiesOfClass(Mob.class, new AABB(var3).inflate(48.0), var1x -> var1x.getType() == var0).size()
               < CAPS.getOrDefault(var5, var6);
      }
   }

   private static void load() {
      try {
         if (!Files.exists(FILE)) {
            return;
         }

         JsonObject var0 = JsonParser.parseString(Files.readString(FILE)).getAsJsonObject();

         for (String var2 : CAPS.keySet()) {
            if (var0.has("cap_" + var2)) {
               CAPS.put(var2, var0.get("cap_" + var2).getAsInt());
            }
         }
      } catch (Exception var31) {
         System.err.println("[Dedsafio4] nexo_dinos.json unreadable, using defaults: " + var31);
      }
   }

   private static void save() {
      try {
         JsonObject var0 = new JsonObject();
         CAPS.forEach((var1x, var2) -> var0.addProperty("cap_" + var1x, var2));
         Files.writeString(FILE, var0.toString());
      } catch (Exception var11) {
         System.err.println("[Dedsafio4] could not save nexo_dinos.json: " + var11);
      }
   }
}
