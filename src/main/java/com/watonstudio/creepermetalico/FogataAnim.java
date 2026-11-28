package com.watonstudio.creepermetalico;

import com.watonstudio.creepermetalico.net.StartFxS2C;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.GameType;

public class FogataAnim {
   public static final int T_DROP = 158;
   public static final int T_STAR = 494;
   public static final int TOTAL = 660;
   static final int ESPERA = 40;
   static final boolean CAMARA_LIBRE = true;
   static final double FX = 0.0;
   static final double FZ = 0.0;
   public static final Map<String, String> REVIVER = new HashMap<>();
   static final Map<UUID, FogataAnim.Anim> ANIMS = new HashMap<>();

   static double sm(double a, double b, double x) {
      double t = Math.min(1.0, Math.max(0.0, (x - a) / (b - a)));
      return t * t * (3.0 - 2.0 * t);
   }

   static double bodyH(double t) {
      if (t < 158.0) {
         return 0.0;
      }

      if (t <= 494.0) {
         return 9.0 * sm(158.0, 494.0, t);
      }

      if (t < 595.0) {
         return 9.0;
      }

      double p = Math.min(1.0, (t - 595.0) / 58.0);
      return 9.0 * (1.0 - p * p);
   }

   public static void start(ServerPlayer p, ServerLevel w, BlockPos fogata, boolean rojo) {
      ANIMS.put(p.getUUID(), new FogataAnim.Anim(w, fogata, rojo));
      p.setGameMode(GameType.SPECTATOR);
   }

   static void camara(ServerPlayer p, FogataAnim.Anim a, double t) {
      double bx = a.fogata.getX() + 0.5 + 0.0;
      double by = a.fogata.getY();
      double bz = a.fogata.getZ() + 0.5 + 0.0;
      double dist = 7.0 + 9.0 * sm(0.0, 660.0, t);
      double eye = by + 2.2 + 1.3 * sm(158.0, 494.0, t);
      double ty = by + 3.5 + bodyH(t) * 0.4;
      float pitch = (float)(-Math.toDegrees(Math.atan2(ty - eye, dist)));
      com.watonstudio.creepermetalico.compat.Compat.teleport(p, a.w, bx, eye - 1.62, bz - dist, 0.0F, pitch);
   }

   public static void tick(MinecraftServer server) {
      Iterator<Entry<UUID, FogataAnim.Anim>> it = ANIMS.entrySet().iterator();

      while (it.hasNext()) {
         Entry<UUID, FogataAnim.Anim> e = it.next();
         ServerPlayer p = server.getPlayerList().getPlayer(e.getKey());
         if (p != null) {
            FogataAnim.Anim a = e.getValue();
            a.t++;
            double cx = a.fogata.getX() + 0.5;
            double cy = a.fogata.getY();
            double cz = a.fogata.getZ() + 0.5;
            if (a.t >= 0) {
               if (a.t == 0) {
                  a.w.playSound(null, a.fogata, FogataMod.REVIVIR, SoundSource.MASTER, 6.0F, 1.0F);

                  for (ServerPlayer v : a.w.players()) {
                     if (v.distanceToSqr(cx, cy, cz) < 25600.0) {
                        ServerPlayNetworking.send(v, new StartFxS2C(a.fogata, a.rojo, p.getGameProfile().name()));
                     }
                  }
               }

               if (a.t >= 660) {
                  double bx = cx + 0.0;
                  double bz = cz + 0.0;
                  com.watonstudio.creepermetalico.compat.Compat.teleport(p, a.w, bx, cy, bz - 2.0, 0.0F, 0.0F);
                  p.setGameMode(GameType.SURVIVAL);
                  String muerto = p.getGameProfile().name();
                  String quien = REVIVER.remove(muerto.toLowerCase());
                  if (quien == null) {
                     quien = muerto;
                  }

                  server.getPlayerList()
                     .broadcastSystemMessage(
                        Component.literal("\ud83c\udfe0 ")
                           .withStyle(ChatFormatting.WHITE)
                           .append(Component.literal("El jugador ").withStyle(style -> style.withColor(3586549)))
                           .append(Component.literal(quien).withStyle(style -> style.withColor(555449)))
                           .append(Component.literal(" ha revivido a ").withStyle(style -> style.withColor(3586549)))
                           .append(Component.literal(muerto).withStyle(style -> style.withColor(555449)))
                           .append(Component.literal(".").withStyle(style -> style.withColor(3586549))),
                        false
                     );
                  it.remove();
               }
            }
         }
      }
   }

   static class Anim {
      ServerLevel w;
      BlockPos fogata;
      boolean rojo;
      int t = -40;

      Anim(ServerLevel w, BlockPos f, boolean r) {
         this.w = w;
         this.fogata = f;
         this.rojo = r;
      }
   }
}
