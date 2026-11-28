package com.watonstudio.creepermetalico.client;

import com.watonstudio.creepermetalico.LinternaItem;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

@Environment(EnvType.CLIENT)
public final class EclipseLighting {
   private static final double LIGHT_DISTANCE = 11.0;
   private static volatile float darkness = 0.0F;
   private static volatile List<BlockPos> lightPositions = List.of();
   private static ClientLevel lastWorld;
   private static int lastRebuildStep = -1;

   private EclipseLighting() {
   }

   public static void tick(Minecraft client) {
      if (client.level != null && client.player != null) {
         if (lastWorld != client.level) {
            lastWorld = client.level;
            lastRebuildStep = -1;
            lightPositions = List.of();
         }

         darkness = clamp(ClientEclipse.ambientDarkness(1.0F));
         Set<BlockPos> found = new HashSet<>();

         for (Player player : client.level.players()) {
            if (isLit(player.getMainHandItem()) || isLit(player.getOffhandItem())) {
               BlockPos expected = getLightTarget(player);
               findSyncedLights(client.level, expected, found);
            }
         }

         List<BlockPos> next = new ArrayList<>(found);
         if (!next.equals(lightPositions)) {
            Set<BlockPos> changed = new HashSet<>(lightPositions);
            changed.addAll(next);
            lightPositions = List.copyOf(next);

            for (BlockPos pos : changed) {
               scheduleAround(client.levelRenderer, pos, 9);
            }
         }

         int step = Math.round(darkness * 24.0F);
         if (step != lastRebuildStep) {
            lastRebuildStep = step;
            int radius = Math.min(96, (client.options.getEffectiveRenderDistance() + 1) * 16);
            int x = client.player.getBlockX();
            int z = client.player.getBlockZ();
            client.levelRenderer
               .setBlocksDirty(x - radius, client.level.getMinBuildHeight(), z - radius, x + radius, client.level.getMaxBuildHeight(), z + radius);
         }
      } else {
         darkness = 0.0F;
         lightPositions = List.of();
         lastWorld = null;
         lastRebuildStep = -1;
      }
   }

   private static void scheduleAround(LevelRenderer renderer, BlockPos pos, int radius) {
      renderer.setBlocksDirty(pos.getX() - radius, pos.getY() - radius, pos.getZ() - radius, pos.getX() + radius, pos.getY() + radius, pos.getZ() + radius);
   }

   private static boolean isLit(ItemStack stack) {
      return stack.getItem() instanceof LinternaItem item && item.isLit();
   }

   private static BlockPos getLightTarget(Player player) {
      HitResult hit = player.pick(11.0, 1.0F, false);
      if (hit instanceof BlockHitResult blockHit && hit.getType() == Type.BLOCK) {
         return blockHit.getBlockPos().relative(blockHit.getDirection()).immutable();
      } else {
         Vec3 end = player.getEyePosition(1.0F).add(player.getViewVector(1.0F).scale(11.0));
         return BlockPos.containing(end);
      }
   }

   private static void findSyncedLights(ClientLevel world, BlockPos expected, Set<BlockPos> result) {
      for (int dx = -1; dx <= 1; dx++) {
         for (int dy = -1; dy <= 2; dy++) {
            for (int dz = -1; dz <= 1; dz++) {
               BlockPos candidate = expected.offset(dx, dy, dz);
               BlockState state = world.getBlockState(candidate);
               if (state.is(Blocks.LIGHT) && (Integer)state.getValue(LightBlock.LEVEL) == 15) {
                  result.add(candidate.immutable());
               }
            }
         }
      }
   }

   public static int filterLightLevel(LightLayer type, BlockPos pos, int original) {
      float d = darkness;
      if (d <= 0.001F) {
         return original;
      }

      float skyAmbient = 1.0F - 0.999F * d;
      if (type == LightLayer.SKY) {
         return clampLight(original * skyAmbient);
      }

      float artificialAmbient = 1.0F - 0.995F * d;
      float flashlight = flashlightFactor(pos);
      float multiplier = Math.max(artificialAmbient, flashlight);
      int filtered = Math.round(original * multiplier);
      if (original > 0 && flashlight < 0.999F && filtered == 0) {
         filtered = 1;
      }

      return Math.max(0, Math.min(15, filtered));
   }

   private static float flashlightFactor(BlockPos pos) {
      float best = 0.0F;

      for (BlockPos light : lightPositions) {
         double dx = pos.getX() + 0.5 - (light.getX() + 0.5);
         double dy = (pos.getY() + 0.5 - (light.getY() + 0.5)) * 0.45;
         double dz = pos.getZ() + 0.5 - (light.getZ() + 0.5);
         double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
         float factor = 1.0F - smoothstep(2.0F, 8.0F, (float)distance);
         best = Math.max(best, factor);
         if (best >= 1.0F) {
            return 1.0F;
         }
      }

      return best;
   }

   private static int clampLight(float value) {
      return Math.max(0, Math.min(15, Math.round(value)));
   }

   private static float smoothstep(float a, float b, float x) {
      float t = clamp((x - a) / (b - a));
      return t * t * (3.0F - 2.0F * t);
   }

   private static float clamp(float value) {
      return Math.max(0.0F, Math.min(1.0F, value));
   }
}
