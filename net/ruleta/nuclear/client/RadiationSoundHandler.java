package net.ruleta.nuclear.client;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.world.phys.AABB;
import net.ruleta.client.compat.SubscribeEvent;
import net.ruleta.client.compat.TickEvent;
import net.ruleta.nuclear.entity.NuclearCreeperEntity;

public class RadiationSoundHandler {
   public static final float RANGE = 6.0F;
   private static final Map<Integer, RadiationSoundInstance> ACTIVE = new HashMap<>();

   @SubscribeEvent
   public static void onClientTick(TickEvent.ClientTickEvent event) {
      if (event.phase == TickEvent.Phase.END) {
         Minecraft mc = Minecraft.getInstance();
         if (mc.level != null && mc.player != null) {
            ACTIVE.values().removeIf(AbstractTickableSoundInstance::isStopped);
            AABB box = mc.player.getBoundingBox().inflate(6.0);

            for (NuclearCreeperEntity creeper : mc.level.getEntitiesOfClass(NuclearCreeperEntity.class, box)) {
               if (creeper.isAlive()) {
                  RadiationSoundInstance current = ACTIVE.get(creeper.getId());
                  if (current == null || current.isStopped()) {
                     RadiationSoundInstance instance = new RadiationSoundInstance(creeper);
                     ACTIVE.put(creeper.getId(), instance);
                     mc.getSoundManager().play(instance);
                  }
               }
            }
         } else {
            ACTIVE.clear();
         }
      }
   }
}
