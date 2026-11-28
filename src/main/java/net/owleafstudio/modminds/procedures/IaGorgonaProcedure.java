package net.owleafstudio.modminds.procedures;

import java.awt.Color;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.entity.GorgonaEntity;
import net.owleafstudio.modminds.init.ModmindsModMobEffects;
import net.owleafstudio.modminds.network.ModmindsModVariables;
import org.joml.Vector3f;

@Mod.EventBusSubscriber
public class IaGorgonaProcedure {
   private static boolean effectApplied = false;
   private static long lastParticleTime = 0L;

   @SubscribeEvent
   public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
      if (event.phase == TickEvent.Phase.END) {
         execute(event.player.level(), event.player.getX(), event.player.getY(), event.player.getZ(), event.player);
      }
   }

   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null && world instanceof Level level && !(entity instanceof Player player && player.isCreative())) {
         for (GorgonaEntity gorgona : world.getEntitiesOfClass(GorgonaEntity.class, AABB.ofSize(new Vec3(x, y, z), 20.0, 20.0, 20.0), e -> true)) {
            if (!isBlocked(level, entity, gorgona)) {
               ModmindsModVariables.getVariables(entity).ifPresent(capability -> {
                  capability.GorgonaContadorCongelar++;
                  capability.syncPlayerVariables(entity);
               });
               double contador = ModmindsModVariables.getVariables(entity).orElse(new ModmindsModVariables.PlayerVariables()).GorgonaContadorCongelar;
               boolean effectActive = entity instanceof LivingEntity _entity
                  && _entity.hasEffect(CompatAccess.effect(ModmindsModMobEffects.TEXTURA_GORGONA_CONGELADO.get()));
               if (!effectActive && !effectApplied) {
                  spawnLaserParticles(level, entity, gorgona);
               }

               if (contador >= 80.0 && !effectApplied) {
                  if (entity instanceof LivingEntity _entityx && !level.isClientSide()) {
                     _entityx.addEffect(new MobEffectInstance(CompatAccess.effect(ModmindsModMobEffects.TEXTURA_GORGONA_CONGELADO.get()), 300, 0, false, false));
                     effectApplied = true;
                  }

                  ModmindsMod.queueServerWork(350, () -> {
                     ModmindsModVariables.getVariables(entity).ifPresent(capability -> {
                        capability.GorgonaContadorCongelar = 0.0;
                        capability.syncPlayerVariables(entity);
                     });
                     effectApplied = false;
                  });
               }
            }
         }
      }
   }

   private static boolean isBlocked(Level level, Entity player, GorgonaEntity gorgona) {
      Vec3 playerHead = player.position().add(0.0, player.getBbHeight() * 0.75, 0.0);
      Vec3 gorgonaHead = gorgona.position().add(0.0, gorgona.getBbHeight() * 0.75, 0.0);
      BlockHitResult result = level.clip(new ClipContext(playerHead, gorgonaHead, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
      return result.getType() == HitResult.Type.BLOCK;
   }

   private static void spawnLaserParticles(Level level, Entity player, GorgonaEntity gorgona) {
      long currentTime = System.currentTimeMillis();
      if (currentTime - lastParticleTime >= 50L) {
         lastParticleTime = currentTime;
         Vec3 playerHead = player.position().add(0.0, player.getBbHeight() * 0.75, 0.0);
         Vec3 gorgonaHead = gorgona.position().add(0.0, gorgona.getBbHeight() * 0.75, 0.0);
         double steps = 20.0;
         double dx = (gorgonaHead.x - playerHead.x) / steps;
         double dy = (gorgonaHead.y - playerHead.y) / steps;
         double dz = (gorgonaHead.z - playerHead.z) / steps;
         Color startColor = new Color(43, 70, 58);
         Color endColor = new Color(143, 135, 106);

         for (int i = 0; i < steps; i++) {
            double px = playerHead.x + dx * i;
            double py = playerHead.y + dy * i;
            double pz = playerHead.z + dz * i;
            float ratio = i / (float)steps;
            float r = (startColor.getRed() + ratio * (endColor.getRed() - startColor.getRed())) / 255.0F;
            float g = (startColor.getGreen() + ratio * (endColor.getGreen() - startColor.getGreen())) / 255.0F;
            float b = (startColor.getBlue() + ratio * (endColor.getBlue() - startColor.getBlue())) / 255.0F;
            level.addParticle(new DustColorTransitionOptions(new Vector3f(r, g, b), new Vector3f(0.0F, 0.0F, 0.0F), 1.0F), px, py, pz, 0.0, 0.0, 0.0);
         }
      }
   }
}
