package com.hexagonsafio2;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;

public class NaveCraftClient implements ClientModInitializer {
   public void onInitializeClient() {
      ClientTickEvents.END_CLIENT_TICK
         .register(
            (EndTick)mc -> {
               ClientLevel level = mc.level;
               if (level != null && !mc.isPaused()) {
                  long t = level.getGameTime();
                  RandomSource r = level.random;

                  for (Entity e : level.entitiesForRendering()) {
                     if (BuiltInRegistries.ENTITY_TYPE.getKey(e.getType()).equals(NaveCraft.NAVE_ID)) {
                        double dx = e.getX() - e.xo;
                        double dy = e.getY() - e.yo;
                        double dz = e.getZ() - e.zo;
                        if (!(dx * dx + dy * dy + dz * dz < 0.0025)) {
                           double x = e.getX();
                           double y = e.getY();
                           double z = e.getZ();
                           double h = e.getBbHeight();

                           for (int i = 0; i < 4; i++) {
                              level.addParticle(
                                 ParticleTypes.FLAME, x + (r.nextDouble() - 0.5) * 0.6, y - 0.1, z + (r.nextDouble() - 0.5) * 0.6, 0.0, -0.25, 0.0
                              );
                           }

                           level.addParticle(ParticleTypes.CLOUD, x + (r.nextDouble() - 0.5) * 0.5, y - 0.3, z + (r.nextDouble() - 0.5) * 0.5, 0.0, -0.15, 0.0);
                           if (t % 3L == 0L) {
                              int n = 14;

                              for (int i = 0; i < n; i++) {
                                 double a = (Math.PI * 2) * i / n + t * 0.15;
                                 level.addParticle(ParticleTypes.END_ROD, x + Math.cos(a) * 1.6, y - 0.5, z + Math.sin(a) * 1.6, 0.0, -0.02, 0.0);
                              }
                           }

                           int m = 12;

                           for (int i = 0; i < m; i++) {
                              double a = (Math.PI * 2) * i / m - t * 0.2;
                              level.addParticle(
                                 ParticleTypes.CLOUD, x + Math.cos(a) * 1.8, y + h + 0.6, z + Math.sin(a) * 1.8, -Math.sin(a) * 0.06, 0.02, Math.cos(a) * 0.06
                              );
                           }
                        }
                     }
                  }
               }
            }
         );
   }
}
