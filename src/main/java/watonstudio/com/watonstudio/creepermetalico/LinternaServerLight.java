package com.watonstudio.creepermetalico;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStopping;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.EndTick;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public final class LinternaServerLight implements ModInitializer {
   private static final double LIGHT_DISTANCE = 11.0;
   private static final BlockState LIGHT_STATE = (BlockState)Blocks.LIGHT.defaultBlockState().setValue(LightBlock.LEVEL, 15);
   private static final Map<UUID, LinternaServerLight.LightSource> SOURCES = new HashMap<>();

   public void onInitialize() {
      ServerTickEvents.END_SERVER_TICK.register((EndTick)server -> {
         Set<UUID> online = new HashSet<>();

         for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            UUID id = player.getUUID();
            online.add(id);
            updatePlayer(player);
         }

         List<UUID> disconnected = new ArrayList<>();

         for (UUID id : SOURCES.keySet()) {
            if (!online.contains(id)) {
               disconnected.add(id);
            }
         }

         for (UUID id : disconnected) {
            removeSource(id);
         }
      });
      ServerLifecycleEvents.SERVER_STOPPING.register((ServerStopping)server -> clearAll());
   }

   private static void updatePlayer(ServerPlayer player) {
      boolean hasLantern = isLit(player.getMainHandItem()) || isLit(player.getOffhandItem());
      UUID id = player.getUUID();
      if (!hasLantern) {
         removeSource(id);
      } else {
         ServerLevel world = player.serverLevel();
         BlockPos target = getLightTarget(player);
         LinternaServerLight.LightSource current = SOURCES.get(id);
         if (current == null || current.world != world || !current.anchor.equals(target) || !world.getBlockState(current.position).equals(LIGHT_STATE)) {
            removeSource(id);
            BlockPos position = findFreePosition(world, target);
            if (position != null) {
               BlockState previous = world.getBlockState(position);
               world.setBlock(position, LIGHT_STATE, 3);
               SOURCES.put(id, new LinternaServerLight.LightSource(world, target.immutable(), position.immutable(), previous));
            }
         }
      }
   }

   private static BlockPos getLightTarget(ServerPlayer player) {
      HitResult hit = player.pick(11.0, 1.0F, false);
      if (hit instanceof BlockHitResult blockHit && hit.getType() == Type.BLOCK) {
         return blockHit.getBlockPos().relative(blockHit.getDirection()).immutable();
      } else {
         Vec3 end = player.getEyePosition(1.0F).add(player.getViewVector(1.0F).scale(11.0));
         return BlockPos.containing(end);
      }
   }

   private static BlockPos findFreePosition(ServerLevel world, BlockPos target) {
      BlockPos[] candidates = new BlockPos[]{target, target.above(), target.below(), target.north(), target.south(), target.east(), target.west()};

      for (BlockPos candidate : candidates) {
         if (candidate.getY() >= world.getMinBuildHeight()
            && candidate.getY() < world.getMaxBuildHeight()
            && world.getBlockState(candidate).isAir()
            && !isReserved(world, candidate)) {
            return candidate.immutable();
         }
      }

      return null;
   }

   private static boolean isLit(ItemStack stack) {
      return stack.getItem() instanceof LinternaItem item && item.isLit();
   }

   private static boolean isReserved(ServerLevel world, BlockPos position) {
      for (LinternaServerLight.LightSource source : SOURCES.values()) {
         if (source.world == world && source.position.equals(position)) {
            return true;
         }
      }

      return false;
   }

   private static void removeSource(UUID id) {
      LinternaServerLight.LightSource source = SOURCES.remove(id);
      if (source != null) {
         if (source.world.getBlockState(source.position).equals(LIGHT_STATE)) {
            source.world.setBlock(source.position, source.previous, 3);
         }
      }
   }

   private static void clearAll() {
      for (UUID id : new ArrayList<>(SOURCES.keySet())) {
         removeSource(id);
      }
   }

   private static final class LightSource {
      private final ServerLevel world;
      private final BlockPos anchor;
      private final BlockPos position;
      private final BlockState previous;

      private LightSource(ServerLevel world, BlockPos anchor, BlockPos position, BlockState previous) {
         this.world = world;
         this.anchor = anchor;
         this.position = position;
         this.previous = previous;
      }
   }
}
