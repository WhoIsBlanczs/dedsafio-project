package net.ruleta.nuclear;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.ruleta.Ajustes;
import net.ruleta.bunker.Bunkers;
import net.ruleta.nuclear.entity.NuclearCreeperEntity;

public final class NuclearExplosion {
   public static final double DAMAGE_RADIUS = 40.0;
   public static final float DAMAGE = 30.0F;
   public static final int CRATER_RADIUS = 7;
   private static final int BLOCKS_PER_TICK = 2500;
   private static final ResourceKey<DamageType> DAMAGE_TYPE = ResourceKey.create(
      Registries.DAMAGE_TYPE, Identifier.fromNamespaceAndPath("nuclearcreeper", "nuclear_explosion")
   );
   private static final List<NuclearExplosion.Crater> CRATERS = new ArrayList<>();

   private NuclearExplosion() {
   }

   public static void detonate(ServerLevel level, NuclearCreeperEntity creeper) {
      double x = creeper.getX();
      double y = creeper.getY() + creeper.getBbHeight() / 2.0;
      double z = creeper.getZ();
      level.playSound(null, x, y, z, SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 12.0F, 0.5F);
      level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, x, y, z, 8, 6.0, 2.0, 6.0, 0.0);
      DamageSource source = new DamageSource(level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DAMAGE_TYPE), creeper, creeper);
      double radius = Ajustes.entero("nuclear.radio");
      AABB box = new AABB(x - radius, y - radius, z - radius, x + radius, y + radius, z + radius);
      double maxSq = radius * radius;

      for (LivingEntity victim : level.getEntitiesOfClass(
         LivingEntity.class, box, e -> e != creeper && e.isAlive() && !e.isSpectator() && e.distanceToSqr(x, y, z) <= maxSq
      )) {
         if (victim instanceof Player player && player.isBlocking()) {
            breakShield(player);
         } else {
            victim.hurt(source, Ajustes.decimal("nuclear.dano") * 2.0F);
         }
      }

      if (level.getGameRules().get(GameRules.MOB_GRIEFING)) {
         queueCrater(level, BlockPos.containing(x, y, z));
      }
   }

   private static void breakShield(Player player) {
      InteractionHand hand = player.getUsedItemHand();
      ItemStack shield = player.getItemInHand(hand);
      player.stopUsingItem();
      shield.shrink(1);
      player.level().broadcastEntityEvent(player, (byte)30);
   }

   private static void queueCrater(ServerLevel level, BlockPos center) {
      RandomSource random = level.getRandom();
      int r = 7;
      int inner = (r - 2) * (r - 2);
      int outer = r * r;
      List<BlockPos> list = new ArrayList<>();

      for (int dx = -r; dx <= r; dx++) {
         for (int dy = -r; dy <= r; dy++) {
            for (int dz = -r; dz <= r; dz++) {
               int d2 = dx * dx + dy * dy + dz * dz;
               if (d2 <= outer && (d2 <= inner || !(random.nextFloat() < 0.4F))) {
                  list.add(center.offset(dx, dy, dz));
               }
            }
         }
      }

      list.sort(Comparator.comparingDouble(p -> p.distSqr(center)));
      CRATERS.add(new NuclearExplosion.Crater(level, list));
   }

   public static void tick() {
      if (!CRATERS.isEmpty()) {
         Iterator<NuclearExplosion.Crater> it = CRATERS.iterator();

         while (it.hasNext()) {
            NuclearExplosion.Crater c = it.next();

            for (int end = Math.min(c.index + 2500, c.positions.size()); c.index < end; c.index++) {
               BlockPos pos = c.positions.get(c.index);
               BlockState state = c.level.getBlockState(pos);
               if (!state.isAir() && !Bunkers.protegido(c.level, pos, state)) {
                  c.level.setBlock(pos, Blocks.AIR.defaultBlockState(), 18);
               }
            }

            if (c.index >= c.positions.size()) {
               it.remove();
            }
         }
      }
   }

   public static void clear() {
      CRATERS.clear();
   }

   private static final class Crater {
      final ServerLevel level;
      final List<BlockPos> positions;
      int index = 0;

      Crater(ServerLevel level, List<BlockPos> positions) {
         this.level = level;
         this.positions = positions;
      }
   }
}
