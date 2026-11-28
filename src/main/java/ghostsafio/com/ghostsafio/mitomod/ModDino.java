package com.ghostsafio.mitomod;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents.ModifyEntries;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TallGrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.levelgen.Heightmap.Types;

public class ModDino {
   public static final ResourceKey<Level> DINO = ResourceKey.create(Registries.DIMENSION, Ghostsafio.id("jurasico"));
   private static final List<Item> ITEMS = new ArrayList<>();
   public static final Block TIERRA = register("tierra_jurasica", new Block(Properties.ofFullCopy(Blocks.DIRT)));
   public static final Block CESPED = register("cesped_jurasico", new Block(Properties.ofFullCopy(Blocks.DIRT).sound(SoundType.GRASS)));
   public static final Block PIEDRA_RUINAS = register("piedra_ruinas", new Block(Properties.ofFullCopy(Blocks.STONE_BRICKS)));
   public static final Block PILAR_RUINAS = register("pilar_ruinas", new RotatedPillarBlock(Properties.ofFullCopy(Blocks.STONE_BRICKS)));
   public static final Block MENA_AMBAR = register("mena_ambar", new Block(Properties.of().strength(1.5F, 3.0F).sound(SoundType.STONE).lightLevel(s -> 9)));
   public static final Block PORTAL = register("portal_dino", new DinoPortalBlock(Properties.of().noCollission().strength(2.0F), true));
   public static final Block SALIDA = register("salida_dino", new DinoPortalBlock(Properties.of().noCollission().strength(2.0F), false));
   public static final Block CUERDA = register("cuerda", new RopeBlock(Properties.of().noCollission().noOcclusion().strength(0.5F).sound(SoundType.GRASS)));
   public static final Block CICA = register("cica", new ModDino.JurassicPlant(Properties.ofFullCopy(Blocks.FERN)));
   public static final Block HELECHO_ROCA = register("helecho_roca", new ModDino.JurassicPlant(Properties.ofFullCopy(Blocks.FERN)));
   public static final Item AMBAR = (Item)Registry.register(
      BuiltInRegistries.ITEM, Ghostsafio.id("ambar"), new Item(new net.minecraft.world.item.Item.Properties())
   );
   private static final int FLAGS = 2;

   private static Block register(String id, Block block) {
      Registry.register(BuiltInRegistries.BLOCK, Ghostsafio.id(id), block);
      ITEMS.add((Item)Registry.register(BuiltInRegistries.ITEM, Ghostsafio.id(id), new BlockItem(block, new net.minecraft.world.item.Item.Properties())));
      return block;
   }

   public static void register() {
      PayloadTypeRegistry.playS2C().register(DinoTravelPayload.TYPE, DinoTravelPayload.CODEC);
      ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.BUILDING_BLOCKS).register((ModifyEntries)e -> ITEMS.forEach(e::accept));
      ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS).register((ModifyEntries)e -> e.accept(AMBAR));
      CommandRegistrationCallback.EVENT
         .register(
            (CommandRegistrationCallback)(dispatcher, registryAccess, environment) -> dispatcher.register(
               (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("biomadino")
                           .requires(src -> src.hasPermission(2)))
                        .then(
                           Commands.literal("hueco")
                              .then(
                                 Commands.argument("pos", BlockPosArgument.blockPos())
                                    .executes(
                                       ctx -> {
                                          BlockPos pos = BlockPosArgument.getBlockPos(ctx, "pos");
                                          ServerLevel overworld = ((CommandSourceStack)ctx.getSource()).getServer().getLevel(Level.OVERWORLD);
                                          ServerLevel dino = ((CommandSourceStack)ctx.getSource()).getServer().getLevel(DINO);
                                          if (dino == null) {
                                             ((CommandSourceStack)ctx.getSource())
                                                .sendFailure(
                                                   Component.literal(
                                                      "La dimensión ghostsafio:jurasico NO está cargada. Revisa data/ghostsafio/dimension/jurasico.json y el log."
                                                   )
                                                );
                                             return 0;
                                          } else {
                                             buildHole(overworld, pos);
                                             buildDino(dino, pos.getX(), pos.getZ());
                                             ((CommandSourceStack)ctx.getSource())
                                                .sendSuccess(
                                                   () -> Component.literal(
                                                      "Hueco creado en "
                                                         + pos.getX()
                                                         + " "
                                                         + pos.getY()
                                                         + " "
                                                         + pos.getZ()
                                                         + ". Salta dentro y caerás hasta el bioma jurásico."
                                                   ),
                                                   true
                                                );
                                             return 1;
                                          }
                                       }
                                    )
                              )
                        ))
                     .then(Commands.literal("ir").executes(ctx -> {
                        ServerPlayer player = ((CommandSourceStack)ctx.getSource()).getPlayerOrException();
                        ServerLevel dino = ((CommandSourceStack)ctx.getSource()).getServer().getLevel(DINO);
                        if (dino == null) {
                           ((CommandSourceStack)ctx.getSource()).sendFailure(Component.literal("La dimensión ghostsafio:jurasico NO está cargada."));
                           return 0;
                        } else {
                           buildDino(dino, Mth.floor(player.getX()), Mth.floor(player.getZ()));
                           travel(player, true);
                           return 1;
                        }
                     })))
                  .then(Commands.literal("salir").executes(ctx -> {
                     travel(((CommandSourceStack)ctx.getSource()).getPlayerOrException(), false);
                     return 1;
                  }))
            )
         );
   }

   public static void travel(ServerPlayer player, boolean toDino) {
      MinecraftServer server = player.getServer();
      if (server != null) {
         ServerLevel target = server.getLevel(toDino ? DINO : Level.OVERWORLD);
         if (target != null) {
            int x = Mth.floor(player.getX());
            int z = Mth.floor(player.getZ());
            double y;
            if (toDino) {
               target.getChunk(x >> 4, z >> 4);
               y = ceilingY(target, x, z) - 3.0;
            } else {
               x += 38;
               target.getChunk(x >> 4, z >> 4);
               y = target.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, x, z) + 0.2;
            }

            ServerPlayNetworking.send(player, new DinoTravelPayload());
            player.teleportTo(target, x + 0.5, y, z + 0.5, player.getYRot(), player.getXRot());
            player.fallDistance = 0.0F;
            player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 200, 0, false, false));
         }
      }
   }

   private static double wobble(int dx, int dz) {
      double a = Math.atan2(dz, dx);
      return 1.0 + 0.16 * Math.sin(a * 3.0 + 1.3) + 0.1 * Math.sin(a * 7.0 + 0.4) + 0.05 * Math.sin(a * 13.0);
   }

   private static double rnd(int x, int z) {
      int h = x * 374761393 + z * 668265263;
      h = (h ^ h >>> 13) * 1274126177;
      h ^= h >>> 16;
      return (h & 65535) / 65535.0;
   }

   private static void buildHole(ServerLevel level, BlockPos top) {
      int R = 22;
      int D = 70;
      int bottom = level.getMinBuildHeight() + 7;
      int y0 = Math.min(top.getY(), level.getMaxBuildHeight() - 12);
      MutableBlockPos p = new MutableBlockPos();
      BlockState air = Blocks.AIR.defaultBlockState();
      BlockState[] dark = new BlockState[]{
         Blocks.COBBLED_DEEPSLATE.defaultBlockState(),
         Blocks.COBBLED_DEEPSLATE.defaultBlockState(),
         Blocks.DEEPSLATE.defaultBlockState(),
         Blocks.BLACKSTONE.defaultBlockState(),
         Blocks.COBBLED_DEEPSLATE.defaultBlockState(),
         Blocks.DEEPSLATE.defaultBlockState(),
         Blocks.COBBLED_DEEPSLATE.defaultBlockState(),
         Blocks.BLACKSTONE.defaultBlockState()
      };
      int[][] lobes = new int[][]{{-19, 0, 19}, {-38, 5, 16}, {-13, -16, 13}, {-35, -14, 13}, {-14, 18, 13}, {-54, -3, 11}, {-29, 19, 10}};

      for (int dx = -72; dx <= 30; dx++) {
         for (int dz = -38; dz <= 38; dz++) {
            double f = 0.0;

            for (int[] l : lobes) {
               double ddx = dx - l[0];
               double ddz = dz - l[1];
               f += l[2] * l[2] / (ddx * ddx + ddz * ddz + 1.0);
            }

            if (!(f < 1.0 + (rnd(dx, dz) - 0.5) * 0.45)) {
               int x = top.getX() + dx;
               int z = top.getZ() + dz;

               for (int y = y0 + 6; y > y0; y--) {
                  p.set(x, y, z);
                  if (!level.getBlockState(p).isAir()) {
                     level.setBlock(p, air, 2);
                  }
               }

               for (int y = y0; y >= y0 - 2; y--) {
                  p.set(x, y, z);
                  level.setBlock(p, dark[(int)(rnd(x + y, z - y) * dark.length) % dark.length], 2);
               }

               for (int y = y0 - 3; y >= y0 - 10; y--) {
                  p.set(x, y, z);
                  BlockState st = level.getBlockState(p);
                  if (!st.isAir() && st.getFluidState().isEmpty()) {
                     break;
                  }

                  level.setBlock(p, dark[(int)(rnd(x, z) * dark.length) % dark.length], 2);
               }
            }
         }
      }

      BlockState[] stripes = new BlockState[]{
         Blocks.STONE.defaultBlockState(),
         Blocks.COBBLESTONE.defaultBlockState(),
         Blocks.STONE.defaultBlockState(),
         Blocks.ANDESITE.defaultBlockState(),
         Blocks.STONE.defaultBlockState(),
         Blocks.COBBLESTONE.defaultBlockState(),
         Blocks.GRAVEL.defaultBlockState(),
         Blocks.STONE.defaultBlockState()
      };
      BlockState beam = Blocks.OAK_LOG.defaultBlockState();
      BlockState planks = Blocks.OAK_PLANKS.defaultBlockState();
      int reach = 34;

      for (int dx = -reach; dx <= reach; dx++) {
         for (int dz = -reach; dz <= reach; dz++) {
            double r = Math.sqrt(dx * dx + dz * dz);
            double a = Math.atan2(dz, dx);
            double w = wobble(dx, dz);
            double jag = (rnd(dx, dz) - 0.5) * 2.6;
            int stripe = Math.floorMod((int)Math.floor((a + Math.PI) * 5.0) + (int)(rnd(dx, dz) * 2.0), stripes.length);

            for (int y = y0 + 8; y >= bottom; y--) {
               int d = Math.max(0, y0 - y);
               double t = Math.max(0.0, 1.0 - d / 70.0);
               double ri = 5.0 + 17.0 * t * t * w + jag * Math.min(1.0, t * 1.5);
               if (!(r > ri + 2.4)) {
                  p.set(top.getX() + dx, y, top.getZ() + dz);
                  if (r <= ri) {
                     level.setBlock(p, y == bottom ? PORTAL.defaultBlockState() : air, 2);
                  } else if (y <= y0 && !level.getBlockState(p).isAir()) {
                     BlockState wall = stripes[stripe];
                     if (d % 10 == 0 && d > 0) {
                        wall = Math.floorMod(dx + dz, 3) == 0 ? beam : planks;
                     }

                     level.setBlock(p, wall, 2);
                  }
               }
            }

            if (r <= 8.0) {
               p.set(top.getX() + dx, bottom - 1, top.getZ() + dz);
               level.setBlock(p, stripes[0], 2);
            }
         }
      }
   }

   private static int ceilingY(ServerLevel level, int x, int z) {
      MutableBlockPos p = new MutableBlockPos();

      for (int y = 100; y <= 210; y++) {
         p.set(x, y, z);
         BlockState st = level.getBlockState(p);
         if (!st.isAir() && st.getFluidState().isEmpty() && !st.is(BlockTags.LEAVES) && !st.is(BlockTags.LOGS)) {
            return y;
         }
      }

      return 150;
   }

   private static int surfaceY(ServerLevel level, int x, int z) {
      MutableBlockPos p = new MutableBlockPos();

      for (int y = 125; y > 45; y--) {
         p.set(x, y, z);
         BlockState st = level.getBlockState(p);
         if (!st.isAir() && st.getFluidState().isEmpty() && !st.is(BlockTags.LEAVES) && !st.is(BlockTags.LOGS)) {
            return y + 1;
         }
      }

      return 63;
   }

   private static void loadArea(ServerLevel level, int cx, int cz, int r) {
      for (int x = cx - r >> 4; x <= cx + r >> 4; x++) {
         for (int z = cz - r >> 4; z <= cz + r >> 4; z++) {
            level.getChunk(x, z);
         }
      }
   }

   private static void buildDino(ServerLevel level, int cx, int cz) {
      MutableBlockPos p = new MutableBlockPos();
      BlockState air = Blocks.AIR.defaultBlockState();
      BlockState water = Blocks.WATER.defaultBlockState();
      BlockState dirt = TIERRA.defaultBlockState();
      BlockState grass = CESPED.defaultBlockState();
      int RP = 19;
      int BEACH = 4;
      int RS = 72;
      loadArea(level, cx, cz, 78);

      for (int dx = -74; dx <= 74; dx++) {
         for (int dz = -74; dz <= 74; dz++) {
            double r = Math.sqrt(dx * dx + dz * dz);
            if (!(r > 73.0)) {
               double w = wobble(dx, dz);
               int x = cx + dx;
               int z = cz + dz;
               if (r <= 19.0 * w) {
                  int floor = Math.max(61 - (int)((19.0 * w - r) / 1.8), 54);

                  for (int y = 48; y <= 125; y++) {
                     p.set(x, y, z);
                     BlockState st = y <= floor ? dirt : (y <= 62 ? water : air);
                     level.setBlock(p, st, 2);
                  }
               } else {
                  double rr = r - 19.0 * w;
                  int top = rr <= 4.0 ? 63 : 63 + (int)Math.round((rr - 4.0) * 0.55);
                  int natural = surfaceY(level, x, z) - 1;
                  boolean flat = rr <= 6.0;
                  if (flat) {
                     top = 63;
                  } else if (natural <= top) {
                     continue;
                  }

                  int clearTo = Math.min(125, Math.max(natural, top) + 45);

                  for (int y = top + 1; y <= clearTo; y++) {
                     p.set(x, y, z);
                     if (!level.getBlockState(p).isAir()) {
                        level.setBlock(p, air, 2);
                     }
                  }

                  for (int y = Math.max(48, top - 8); y < top; y++) {
                     p.set(x, y, z);
                     BlockState cur = level.getBlockState(p);
                     if (cur.isAir() || !cur.getFluidState().isEmpty()) {
                        level.setBlock(p, dirt, 2);
                     }
                  }

                  p.set(x, top, z);
                  level.setBlock(p, rr > 3.0 ? grass : dirt, 2);
               }
            }
         }
      }

      int ceil = ceilingY(level, cx, cz);
      BlockState wall = PIEDRA_RUINAS.defaultBlockState();
      BlockState exit = SALIDA.defaultBlockState();

      for (int dx = -22; dx <= 22; dx++) {
         for (int dz = -22; dz <= 22; dz++) {
            double r = Math.sqrt(dx * dx + dz * dz);
            double lim = 19.0 * wobble(dx, dz);
            if (!(r > lim + 1.8)) {
               for (int y = ceil - 14; y <= ceil + 7; y++) {
                  p.set(cx + dx, y, cz + dz);
                  if (r <= lim) {
                     level.setBlock(p, y == ceil + 7 ? exit : air, 2);
                  } else if (y >= ceil - 2 && !level.getBlockState(p).isAir()) {
                     level.setBlock(p, wall, 2);
                  }
               }
            }
         }
      }

      BlockState ropeY = CUERDA.defaultBlockState();

      for (int y = ceil + 6; y >= 62; y--) {
         p.set(cx, y, cz);
         level.setBlock(p, ropeY, 2);
      }

      int[][] dirs4 = new int[][]{{6, 0}, {-6, 0}, {0, 6}, {0, -6}};

      for (int[] d : dirs4) {
         for (int y = ceil + 6; y >= ceil - 8; y--) {
            p.set(cx + d[0], y, cz + d[1]);
            level.setBlock(p, ropeY, 2);
         }
      }
   }

   private static void buildRamp(ServerLevel level, int cx, int cz, int rp) {
      int[][] dirs = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
      int bestDir = 0;
      int bestLen = Integer.MAX_VALUE;

      for (int i = 0; i < 4; i++) {
         int dx = dirs[i][0];
         int dz = dirs[i][1];
         int start = (int)Math.ceil(rp * wobble(dx, dz)) + 1;

         int len;
         for (len = 0; len < 90; len++) {
            int k = start + len;
            if (surfaceY(level, cx + dx * k, cz + dz * k) <= 63 + len + 1) {
               break;
            }
         }

         if (len < bestLen) {
            bestLen = len;
            bestDir = i;
         }
      }

      int dx = dirs[bestDir][0];
      int dz = dirs[bestDir][1];
      int start = (int)Math.ceil(rp * wobble(dx, dz)) + 1;
      Direction facing = dx == 1 ? Direction.WEST : (dx == -1 ? Direction.EAST : (dz == 1 ? Direction.NORTH : Direction.SOUTH));
      BlockState stair = (BlockState)Blocks.COBBLESTONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, facing);
      BlockState fill = TIERRA.defaultBlockState();
      BlockState air = Blocks.AIR.defaultBlockState();
      MutableBlockPos p = new MutableBlockPos();

      for (int n = 0; n <= bestLen; n++) {
         int k = start + n;
         int hk = 63 + n + 1;

         for (int j = -3; j <= 3; j++) {
            int x = cx + dx * k + (dz != 0 ? j : 0);
            int z = cz + dz * k + (dx != 0 ? j : 0);

            for (int y = Math.max(50, hk - 40); y < hk; y++) {
               p.set(x, y, z);
               BlockState st = level.getBlockState(p);
               if (st.isAir() || !st.getFluidState().isEmpty()) {
                  level.setBlock(p, fill, 2);
               }
            }

            p.set(x, hk, z);
            level.setBlock(p, stair, 2);

            for (int y = hk + 1; y <= hk + 5; y++) {
               p.set(x, y, z);
               level.setBlock(p, air, 2);
            }
         }
      }
   }

   public static class JurassicPlant extends TallGrassBlock {
      public JurassicPlant(Properties props) {
         super(props);
      }

      protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
         return state.is(ModDino.TIERRA) || state.is(ModDino.CESPED) || super.mayPlaceOn(state, level, pos);
      }
   }
}
