package net.owleafstudio.modminds.compat;

import java.util.ArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.StartTick;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.owleafstudio.modminds.entity.ShootReviilEntity;

@Environment(EnvType.CLIENT)
public final class ClientTickPump {
   private static boolean installed;
   private static boolean wasSwinging;
   private static boolean wasUseDown;

   private ClientTickPump() {
   }

   public static void install() {
      if (!installed) {
         installed = true;
         ClientTickEvents.START_CLIENT_TICK.register((StartTick)client -> {
            GameEventPumps.post(new TickEvent.ClientTickEvent(TickEvent.Phase.START));
            KeyBindingBridge.tick(client);
            if (client.player != null) {
               GameEventPumps.post(new TickEvent.PlayerTickEvent(TickEvent.Phase.START, client.player));
            }

            tickLivingEntities(client);
            trailReviilShots(client);
         });
         ClientTickEvents.END_CLIENT_TICK.register((EndTick)client -> {
            if (client.player != null) {
               GameEventPumps.post(new TickEvent.PlayerTickEvent(TickEvent.Phase.END, client.player));
               boolean miss = client.hitResult == null || client.hitResult.getType() == HitResult.Type.MISS;
               boolean emptyClick = miss || hitsOwnMount(client);
               if (client.player.isSwinging() && !wasSwinging && emptyClick) {
                  GameEventPumps.post(new PlayerInteractEvent.LeftClickEmpty(client.player));
               }

               boolean useDown = client.options.keyUse.isDown();
               if (useDown && !wasUseDown && emptyClick && client.player.getMainHandItem().isEmpty()) {
                  GameEventPumps.post(new PlayerInteractEvent.RightClickEmpty(client.player));
               }

               wasUseDown = useDown;
               wasSwinging = client.player.isSwinging();
            }

            GameEventPumps.post(new TickEvent.ClientTickEvent(TickEvent.Phase.END));
         });
      }
   }

   private static boolean hitsOwnMount(Minecraft client) {
      Entity vehicle = client.player.getVehicle();
      return vehicle != null
         && client.hitResult instanceof EntityHitResult hit
         && (hit.getEntity() == vehicle || hit.getEntity() == client.player.getRootVehicle());
   }

   private static void trailReviilShots(Minecraft client) {
      ClientLevel var2 = client.level;
      if (var2 instanceof ClientLevel) {
         ClientLevel level = var2;

         for (Entity entity : var2.entitiesForRendering()) {
            if (entity instanceof ShootReviilEntity) {
               level.addParticle(ParticleTypes.END_ROD, entity.getX(), entity.getY(), entity.getZ(), 0.0, 0.02, 0.0);
               level.addParticle(ParticleTypes.ELECTRIC_SPARK, entity.getX(), entity.getY(), entity.getZ(), 0.0, 0.0, 0.0);
            }
         }
      }
   }

   private static void tickLivingEntities(Minecraft client) {
      ClientLevel living = client.level;
      if (living instanceof ClientLevel) {
         ArrayList var6 = new ArrayList();

         for (Entity entity : living.entitiesForRendering()) {
            if (entity instanceof LivingEntity livingEntity && !livingEntity.isRemoved()) {
               var6.add(livingEntity);
            }
         }

         for (LivingEntity livingEntity : var6) {
            GameEventPumps.post(new LivingEvent.LivingTickEvent(livingEntity));
         }
      }
   }
}
