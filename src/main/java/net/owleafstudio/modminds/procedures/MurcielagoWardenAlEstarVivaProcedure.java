package net.owleafstudio.modminds.procedures;

import java.awt.Color;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.entity.MurcielagoWardenEntity;
import org.joml.Vector3f;

@Mod.EventBusSubscriber
public class MurcielagoWardenAlEstarVivaProcedure {
   private static final String NBT_LAST_ATTACK = "lastAttackTime";
   private static final String NBT_IS_ATTACKING = "isAttacking";
   private static final int ATTACK_DURATION = 7000;
   private static final int ATTACK_COOLDOWN = 10000;
   private static final int DAMAGE_INTERVAL = 20;
   private static final float DAMAGE_AMOUNT = 1.0F;

   @SubscribeEvent
   public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
      if (event.phase == TickEvent.Phase.END) {
         execute(event.player.level(), event.player.getX(), event.player.getY(), event.player.getZ(), event.player);
      }
   }

   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null && world instanceof Level level && !(entity instanceof Player player && player.isCreative())) {
         for (MurcielagoWardenEntity murcielagowarden : world.getEntitiesOfClass(
            MurcielagoWardenEntity.class, AABB.ofSize(new Vec3(x, y, z), 40.0, 40.0, 40.0), e -> true
         )) {
            CompoundTag data = CompatAccess.persistentData(murcielagowarden);
            long currentTime = System.currentTimeMillis();
            long lastAttackTime = data.getLongOr("lastAttackTime", 0L);
            boolean isAttacking = data.getBooleanOr("isAttacking", false);
            if (!isBlocked(level, entity, murcielagowarden)) {
               if (isAttacking) {
                  if (currentTime - lastAttackTime < 7000L) {
                     spawnLaserParticlesAndApplyDamage(level, entity, murcielagowarden);
                  } else {
                     data.putBoolean("isAttacking", false);
                     data.putLong("lastAttackTime", currentTime);
                  }
               } else if (currentTime - lastAttackTime >= 10000L) {
                  data.putBoolean("isAttacking", true);
                  data.putLong("lastAttackTime", currentTime);
               }
            }
         }
      }
   }

   private static boolean isBlocked(Level level, Entity player, MurcielagoWardenEntity murcielagowarden) {
      Vec3 playerHead = player.position().add(0.0, player.getBbHeight() * 0.75, 0.0);
      Vec3 murcielagowardenHead = murcielagowarden.position().add(0.0, murcielagowarden.getBbHeight() * 0.75, 0.0);
      BlockHitResult result = level.clip(new ClipContext(playerHead, murcielagowardenHead, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
      return result.getType() == HitResult.Type.BLOCK;
   }

   private static void spawnLaserParticlesAndApplyDamage(Level level, Entity target, MurcielagoWardenEntity murcielagowarden) {
      if (target instanceof Player) {
         Vec3 playerHead = target.position().add(0.0, target.getBbHeight() * 0.75, 0.0);
         Vec3 murcielagowardenHead = murcielagowarden.position().add(0.0, murcielagowarden.getBbHeight() * 0.75, 0.0);
         double steps = 20.0;
         double dx = (murcielagowardenHead.x - playerHead.x) / steps;
         double dy = (murcielagowardenHead.y - playerHead.y) / steps;
         double dz = (murcielagowardenHead.z - playerHead.z) / steps;
         Color startColor = new Color(20, 113, 122);
         Color endColor = new Color(29, 163, 173);
         boolean hitPlayer = false;

         for (int i = 0; i < steps; i++) {
            double px = playerHead.x + dx * i;
            double py = playerHead.y + dy * i;
            double pz = playerHead.z + dz * i;
            float ratio = i / (float)steps;
            float r = (startColor.getRed() + ratio * (endColor.getRed() - startColor.getRed())) / 255.0F;
            float g = (startColor.getGreen() + ratio * (endColor.getGreen() - startColor.getGreen())) / 255.0F;
            float b = (startColor.getBlue() + ratio * (endColor.getBlue() - startColor.getBlue())) / 255.0F;
            level.addParticle(new DustColorTransitionOptions(new Vector3f(r, g, b), new Vector3f(0.0F, 0.0F, 0.0F), 1.0F), px, py, pz, 0.0, 0.0, 0.0);
            if (!hitPlayer && target.getBoundingBox().contains(px, py, pz)) {
               hitPlayer = true;
            }
         }

         if (hitPlayer && target instanceof LivingEntity livingPlayer) {
            livingPlayer.hurt(level.damageSources().magic(), 1.0F);
         }
      }
   }
}
