package com.novastudio.dedsafio4;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStarted;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.EndTick;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.Level;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class PrehistoricContent {
   public static final ResourceKey<Level> WORLD = ResourceKey.create(Registries.DIMENSION, id("prehistorico"));
   public static Block FERN;
   private static final int SEARCH_RINGS = 6;
   private static final int SEARCH_STEP = 8;
   static final double BORDER_SIZE = 2000.0;

   private PrehistoricContent() {
   }

   private static Identifier id(String var0) {
      return Identifier.fromNamespaceAndPath("nexo", var0);
   }

   private static Block block(String var0, java.util.function.Supplier<? extends Block> f) {
      Block var1 = Registry.register(BuiltInRegistries.BLOCK, com.novastudio.dedsafio4.compat.Ids.bloque(id(var0)), f.get());
      Registry.register(BuiltInRegistries.ITEM, com.novastudio.dedsafio4.compat.Ids.item(id(var0)), new BlockItem(var1, new Item.Properties()));
      return var1;
   }

   private static BlockBehaviour.Properties soil(MapColor var0, SoundType var1, float var2) {
      return BlockBehaviour.Properties.of().mapColor(var0).strength(var2).sound(var1);
   }

   static void register() {
      block("cesped_prehistorico", () -> new Block(soil(MapColor.PLANT, SoundType.GRASS, 0.6F)));
      block("tierra_prehistorica", () -> new Block(soil(MapColor.DIRT, SoundType.GRAVEL, 0.5F)));
      block("tierra_gruesa_prehistorica", () -> new Block(soil(MapColor.DIRT, SoundType.GRAVEL, 0.5F)));
      block("tierra_oscura_prehistorica", () -> new Block(soil(MapColor.PODZOL, SoundType.ROOTED_DIRT, 0.5F)));
      block("piedra_prehistorica", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F).requiresCorrectToolForDrops()));
      FERN = block("helecho_prehistorico", () -> new HelechoPrehistoricoBlock(
            BlockBehaviour.Properties.of()
               .mapColor(MapColor.PLANT)
               .replaceable()
               .noCollision()
               .instabreak()
               .sound(SoundType.GRASS)
               .offsetType(BlockBehaviour.OffsetType.XZ)
         ));
      block("farol_de_ambar", () -> new Block(
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).strength(1.0F).sound(SoundType.SHROOMLIGHT).lightLevel(var0 -> 15)
         ));
      block("cristal_turquesa", () -> new Block(
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).strength(1.5F, 6.0F).sound(SoundType.AMETHYST).lightLevel(var0 -> 7)
         ));
      Registry.register(BuiltInRegistries.FEATURE_TYPE, id("araucaria"), PrimordialFeatures.Araucaria.CODEC);
      Registry.register(BuiltInRegistries.FEATURE_TYPE, id("cicada"), PrimordialFeatures.Cicada.CODEC);
      Registry.register(BuiltInRegistries.FEATURE_TYPE, id("racimo_de_ambar"), PrimordialFeatures.AmberCluster.CODEC);
      Registry.register(BuiltInRegistries.FEATURE_TYPE, id("columna_de_cristal"), PrimordialFeatures.CrystalColumn.CODEC);
      Registry.register(BuiltInRegistries.FEATURE_TYPE, id("quiu_oak_tree"), PrehistoricTreeFeature.ROBLE);
      ServerLevelEvents.LOAD.register((var0, var1) -> border(var1));
      ServerLifecycleEvents.SERVER_STARTED.register((ServerStarted)var0 -> border(var0.getLevel(WORLD)));
      ServerTickEvents.END_SERVER_TICK.register((EndTick)var0 -> {
         if (var0.getTickCount() % 100 == 0) {
            border(var0.getLevel(WORLD));
         }
      });
      Registry.register(BuiltInRegistries.FEATURE_TYPE, id("quiu_birch_tree"), PrehistoricTreeFeature.ABEDUL);
   }

   private static void border(ServerLevel var0) {
      if (var0 != null && var0.dimension() == WORLD) {
         WorldBorder var1 = var0.getWorldBorder();
         if (var1.getSize() != 2000.0 || var1.getCenterX() != 0.0 || var1.getCenterZ() != 0.0) {
            var1.setCenter(0.0, 0.0);
            var1.setSize(2000.0);
         }
      }
   }

   static int teleport(CommandSourceStack var0) throws CommandSyntaxException {
      ServerPlayer var1 = var0.getPlayerOrException();
      ServerLevel var2 = var0.getServer().getLevel(WORLD);
      if (var2 == null) {
         var0.sendFailure(Component.literal("La dimensión prehistórica no está disponible en este servidor."));
         return 0;
      } else if (var1.level() == var2) {
         var0.sendFailure(Component.literal("Ya estás en la dimensión prehistórica. Usa /dino dimension salir para volver."));
         return 0;
      } else {
         short var3 = 952;
         BlockPos var4 = findLanding(
            var2, Math.max(-var3, Math.min(var3, (int)Math.floor(var1.getX()))), Math.max(-var3, Math.min(var3, (int)Math.floor(var1.getZ())))
         );
         com.novastudio.dedsafio4.compat.Compat.teleport(var1, var2, var4.getX() + 0.5, var4.getY(), var4.getZ() + 0.5, var1.getYRot(), 0.0F);
         var0.sendSuccess(
            () -> Component.literal(
               "§a[Dino] Has llegado a la dimensión prehistórica (" + var4.getX() + ", " + var4.getY() + ", " + var4.getZ() + ")."
            ),
            false
         );
         return 1;
      }
   }

   static int leave(CommandSourceStack var0) throws CommandSyntaxException {
      ServerPlayer var1 = var0.getPlayerOrException();
      if (var1.level().dimension() != WORLD) {
         var0.sendFailure(Component.literal("No estás en la dimensión prehistórica."));
         return 0;
      } else {
         boolean var2 = PrehistoricReturn.toBedOrSpawn(var1);
         var0.sendSuccess(() -> Component.literal(var2 ? "§a[Dino] Has vuelto a tu cama." : "§a[Dino] Has vuelto al spawn del mundo."), false);
         return 1;
      }
   }

   static BlockPos findLanding(ServerLevel var0, int var1, int var2) {
      for (int var3 = 0; var3 <= 6; var3++) {
         for (int var4 = -var3; var4 <= var3; var4++) {
            for (int var5 = -var3; var5 <= var3; var5++) {
               if (Math.max(Math.abs(var4), Math.abs(var5)) == var3) {
                  int var6 = var1 + var4 * 8;
                  int var7 = var2 + var5 * 8;
                  var0.getChunk(var6 >> 4, var7 >> 4);
                  if (var0.dimensionType().hasCeiling()) {
                     for (int var13 = var0.getSeaLevel() + 1; var13 < var0.getMaxY() - 8; var13++) {
                        if (safe(var0, new BlockPos(var6, var13, var7))) {
                           return new BlockPos(var6, var13, var7);
                        }
                     }
                  } else {
                     BlockPos var8 = var0.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(var6, 0, var7));
                     if (safe(var0, var8)) {
                        return var8;
                     }
                  }
               }
            }
         }
      }

      var0.getChunk(var1 >> 4, var2 >> 4);
      BlockPos var9 = var0.dimensionType().hasCeiling()
         ? new BlockPos(var1, var0.getSeaLevel() + 8, var2)
         : var0.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(var1, 0, var2));

      for (int var10 = -1; var10 <= 1; var10++) {
         for (int var11 = -1; var11 <= 1; var11++) {
            var0.setBlockAndUpdate(var9.offset(var10, -1, var11), ((Block)BuiltInRegistries.BLOCK.getValue(id("piedra_prehistorica"))).defaultBlockState());

            for (int var12 = 0; var12 < 3; var12++) {
               var0.setBlockAndUpdate(var9.offset(var10, var12, var11), Blocks.AIR.defaultBlockState());
            }
         }
      }

      return var9;
   }

   private static boolean safe(ServerLevel var0, BlockPos var1) {
      BlockState var2 = var0.getBlockState(var1.below());
      if (var2.isFaceSturdy(var0, var1.below(), Direction.UP)
         && var2.getFluidState().isEmpty()
         && !var2.is(BlockTags.LEAVES)) {
         for (int var3 = 0; var3 < 4; var3++) {
            BlockState var4 = var0.getBlockState(var1.above(var3));
            if (!var4.isAir()) {
               return false;
            }
         }

         for (int var5 = -1; var5 <= 1; var5++) {
            for (int var6 = -1; var6 <= 1; var6++) {
               if (!var0.getBlockState(var1.offset(var5, -1, var6)).getFluidState().isEmpty()) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }
}
