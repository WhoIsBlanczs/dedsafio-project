package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.boss.BossFx;
import net.owleafstudio.modminds.boss.ReviilFx;
import net.owleafstudio.modminds.entity.ReviilEntity;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class ReviilFuriaProcedure {
   private static final int STOMP_TICKS = 26;
   private static final int CLIP_TICKS = 40;

   public static void execute(LevelAccessor world, Entity entity) {
      if (entity instanceof ReviilEntity reviil) {
         ServerLevel var4 = BossFx.server(world);
         if (var4 instanceof ServerLevel && reviil.isAlive()) {
            ModmindsModVariables.MapVariables.get(world).ReviilAtaques = 0.0;
            ModmindsModVariables.MapVariables.get(world).syncData(world);
            if (reviil.escenaTicks > 0) {
               BossFx.subtitle(var4, BossFx.centre(reviil), 140.0, "REVIIL NO CAMBIA DE FASE DURANTE LA ESCENA", 50);
               return;
            }

            if (!reviil.enterFury()) {
               BossFx.subtitle(var4, BossFx.centre(reviil), 140.0, "REVIIL YA ESTA EN FURIA", 46);
               return;
            }

            reviil.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0);
            reviil.setAnimation("emote.fury.enter");
            ReviilFx.furyEnter(var4, reviil);
            ModmindsMod.queueServerWork(26, () -> {
               if (reviil.isAlive()) {
                  ReviilFx.furyStomp(var4, reviil);
               }
            });
            ModmindsMod.queueServerWork(40, () -> {
               if (reviil.isAlive()) {
                  reviil.releasePose();
               }
            });
            return;
         }
      }
   }
}
