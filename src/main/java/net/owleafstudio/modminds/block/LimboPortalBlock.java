package net.owleafstudio.modminds.block;

import java.util.Optional;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.world.teleporter.LimboPortalShape;
import net.owleafstudio.modminds.world.teleporter.LimboTeleporter;

public class LimboPortalBlock extends NetherPortalBlock {
   public LimboPortalBlock() {
      super(
         BlockBehaviour.Properties.of()
            .noCollision()
            .randomTicks()
            .pushReaction(PushReaction.IMMOVEABLE)
            .strength(-1.0F)
            .sound(SoundType.GLASS)
            .lightLevel(s -> 0)
            .noLootTable()
      );
   }

   public void randomTick(BlockState blockstate, ServerLevel world, BlockPos pos, RandomSource random) {
   }

   public static void portalSpawn(Level world, BlockPos pos) {
      Optional<LimboPortalShape> optional = LimboPortalShape.findEmptyPortalShape(world, pos, Direction.Axis.X);
      if (optional.isPresent()) {
         optional.get().createPortalBlocks();
      }
   }

   public BlockState updateShape(BlockState p_54928_, net.minecraft.world.level.LevelReader p_54931_, net.minecraft.world.level.ScheduledTickAccess tick$, BlockPos p_54932_, Direction p_54929_, BlockPos p_54933_, BlockState p_54930_, net.minecraft.util.RandomSource rnd$) {
      Direction.Axis direction$axis = p_54929_.getAxis();
      Direction.Axis direction$axis1 = (Direction.Axis)p_54928_.getValue(AXIS);
      boolean flag = direction$axis1 != direction$axis && direction$axis.isHorizontal();
      return !flag && !p_54930_.is(this) && !new LimboPortalShape(p_54931_, p_54932_, direction$axis1).isComplete()
         ? Blocks.AIR.defaultBlockState()
         : super.updateShape(p_54928_, p_54931_, tick$, p_54932_, p_54929_, p_54933_, p_54930_, rnd$);
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
      for (int i = 0; i < 4; i++) {
         double px = pos.getX() + random.nextFloat();
         double py = pos.getY() + random.nextFloat();
         double pz = pos.getZ() + random.nextFloat();
         double vx = (random.nextFloat() - 0.5) / 2.0;
         double vy = (random.nextFloat() - 0.5) / 2.0;
         double vz = (random.nextFloat() - 0.5) / 2.0;
         int j = random.nextInt(4) - 1;
         if (world.getBlockState(pos.west()).getBlock() != this && world.getBlockState(pos.east()).getBlock() != this) {
            px = pos.getX() + 0.5 + 0.25 * j;
            vx = random.nextFloat() * 2.0F * j;
         } else {
            pz = pos.getZ() + 0.5 + 0.25 * j;
            vz = random.nextFloat() * 2.0F * j;
         }

         world.addParticle(ParticleTypes.PORTAL, px, py, pz, vx, vy, vz);
      }

      if (random.nextInt(110) == 0) {
         world.playSound(
            null,
            pos.getX() + 0.5,
            pos.getY() + 0.5,
            pos.getZ() + 0.5,
            ForgeRegistries.SOUND_EVENTS.getValue(Identifier.parse("block.portal.ambient")),
            SoundSource.BLOCKS,
            0.5F,
            random.nextFloat() * 0.4F + 0.8F
         );
      }
   }

   public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity, net.minecraft.world.entity.InsideBlockEffectApplier efectos$, boolean pasando$) {
      if (CompatAccess.canChangeDimensions(entity) && !entity.level().isClientSide()) {
         if (entity.isOnPortalCooldown()) {
            entity.setPortalCooldown();
         } else if (entity.level().dimension() != ResourceKey.create(Registries.DIMENSION, Identifier.parse("modminds:limbo"))) {
            entity.setPortalCooldown();
            this.teleportToDimension(entity, pos, ResourceKey.create(Registries.DIMENSION, Identifier.parse("modminds:limbo")));
         } else {
            entity.setPortalCooldown();
            this.teleportToDimension(entity, pos, Level.OVERWORLD);
         }
      }
   }

   private void teleportToDimension(Entity entity, BlockPos pos, ResourceKey<Level> destinationType) {
      CompatAccess.changeDimension(
         entity, CompatAccess.server(entity).getLevel(destinationType), new LimboTeleporter(CompatAccess.server(entity).getLevel(destinationType), pos)
      );
   }
}
