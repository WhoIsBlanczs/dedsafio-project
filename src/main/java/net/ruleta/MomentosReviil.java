package net.ruleta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public final class MomentosReviil {
   private static final Set<String> ACTIVOS = new LinkedHashSet<>();
   private static final Map<String, Integer> SEGUNDOS = new HashMap<>();
   private static final Map<String, Integer> SEGUNDOS2 = new HashMap<>();
   private static final Map<String, String> EXTRA = new HashMap<>();
   private static final Map<String, Long> DESDE = new HashMap<>();
   private static Boolean regeneracionAntes;
   private static long tick;
   private static final List<String> SINREGEN_IDS = List.of("sin_regeneracion");
   private static final long TICKS_RULETA_LUNA = 280L;
   private static long lunaEn = -1L;
   private static String lunaQuien;

   static String extra(String id) {
      return EXTRA.getOrDefault(id, "");
   }

   private MomentosReviil() {
   }

   public static boolean activo(String id) {
      return ACTIVOS.contains(id);
   }

   private static boolean enEfecto(String id) {
      return DESDE.containsKey(id);
   }

   static void recibir(ServerPlayer jugador, MomentoPayload m) {
      if (jugador != null && m != null && jugador.level().getServer() != null) {
         if (!jugador.permissions().hasPermission(net.minecraft.server.permissions.Permissions.COMMANDS_GAMEMASTER)) {
            jugador.sendSystemMessage(Component.literal("Solo los operadores pueden cambiar los Momentos Reviil."));
         } else {
            Momentos.Momento momento = Momentos.porId(m.id());
            if (momento != null) {
               MinecraftServer server = jugador.level().getServer();
               if (m.activar()) {
                  if ("creepers_negativas".equals(m.id()) && !enEfecto("gas_abajo")) {
                     jugador.sendSystemMessage(
                        Component.literal("Bloqueado: primero tiene que estar en curso el Momento Reviil del Gas tóxico (bajar a capas negativas).")
                           .withStyle(ChatFormatting.RED)
                     );
                     sincronizar(server);
                     return;
                  }

                  if ("eclipse".equals(m.id()) && !Ruleta.cambioActivo("herobrine")) {
                     jugador.sendSystemMessage(Component.literal("Bloqueado: primero activa el cambio de dificultad Herobrine.").withStyle(ChatFormatting.RED));
                     sincronizar(server);
                     return;
                  }

                  if ("eclipse".equals(m.id()) ? !ACTIVOS.isEmpty() : ACTIVOS.contains("eclipse") || ComandoEclipse.enMarcha()) {
                     jugador.sendSystemMessage(
                        Component.literal("El Eclipse va solo: no se puede juntar con otros Momentos Reviil, ni elegido ni en marcha.")
                           .withStyle(ChatFormatting.RED)
                     );
                     sincronizar(server);
                     return;
                  }

                  for (String otro : ACTIVOS) {
                     if (Momentos.chocan(otro, m.id())) {
                        jugador.sendSystemMessage(
                           Component.literal("No se puede: se contradice con «" + Momentos.porId(otro).titulo() + "».").withStyle(ChatFormatting.RED)
                        );
                        sincronizar(server);
                        return;
                     }
                  }

                  ACTIVOS.add(m.id());
                  SEGUNDOS.put(m.id(), Math.max(0, Math.min(86400, m.segundos())));
                  SEGUNDOS2.put(m.id(), Math.max(0, Math.min(86400, m.segundos2())));
                  EXTRA.put(m.id(), m.extra() == null ? "" : m.extra());
               } else {
                  if (Mensajes.ocupado() && ACTIVOS.contains(m.id())) {
                     jugador.sendSystemMessage(
                        Component.literal("La ruleta ya está lanzada: ese Momento Reviil solo se quita con DETENER RULETA o cuando termine.")
                           .withStyle(ChatFormatting.RED)
                     );
                     sincronizar(server);
                     return;
                  }

                  if ("eclipse".equals(m.id()) && enEfecto("eclipse")) {
                     jugador.sendSystemMessage(
                        Component.literal("El Eclipse Solar ya está lanzado: se libera cuando acaben sus 4 Momentos Reviil (o con DETENER RULETA).")
                           .withStyle(ChatFormatting.RED)
                     );
                     sincronizar(server);
                     return;
                  }

                  if (!"luna".equals(m.id()) && enEfecto(m.id())) {
                     jugador.sendSystemMessage(
                        Component.literal("Ese Momento Reviil ya está lanzado: queda seleccionado hasta que termine.").withStyle(ChatFormatting.RED)
                     );
                     sincronizar(server);
                     return;
                  }

                  if ("luna".equals(m.id()) && enEfecto("luna")) {
                     jugador.sendSystemMessage(
                        Component.literal("La Maldición de la Luna ya está lanzada: se quita sola cuando alguien mire la Luna.").withStyle(ChatFormatting.RED)
                     );
                     sincronizar(server);
                     return;
                  }

                  desactivar(server, m.id());
               }

               sincronizar(server);
            }
         }
      }
   }

   static void terminarUno(MinecraftServer server, String id) {
      desactivar(server, id);
      sincronizar(server);
   }

   static void antesDeLanzar(ServerPlayer admin) {
      MomentosBunker.preparar(admin, new ArrayList<>(ACTIVOS));
   }

   private static void desactivar(MinecraftServer server, String id) {
      ACTIVOS.remove(id);
      SEGUNDOS.remove(id);
      SEGUNDOS2.remove(id);
      EXTRA.remove(id);
      boolean estaba = DESDE.remove(id) != null;
      if (estaba && Momentos.delBunker(id)) {
         MomentosBunker.terminar(server);
      }

      if (SINREGEN_IDS.stream().noneMatch(MomentosReviil::enEfecto)) {
         devolverRegeneracion(server);
      }
   }

   static void alTerminar(MinecraftServer server) {
      MomentosBunker.alTerminarReviil(server);
      ACTIVOS.clear();
      SEGUNDOS.clear();
      SEGUNDOS2.clear();
      EXTRA.clear();
      DESDE.clear();
      devolverRegeneracion(server);
      sincronizar(server);
   }

   static String[] textoArmados() {
      StringBuilder t = new StringBuilder();
      StringBuilder x = new StringBuilder();

      for (String id : ACTIVOS) {
         Momentos.Momento m = Momentos.porId(id);
         if (m != null) {
            if (x.length() > 0) {
               x.append(" ");
            }

            x.append("eclipse".equals(id) ? MomentosBunker.mensajeEclipse(0) : m.texto());
            t = new StringBuilder("Momento Reviil");
         }
      }

      return x.length() == 0 ? null : new String[]{t.toString(), MomentosBunker.conCoordenadas(x.toString())};
   }

   static void lanzarElegidos(MinecraftServer server) {
      if (MomentoReviil.enMarcha()) {
         boolean cambio = false;

         for (String id : new ArrayList<>(ACTIVOS)) {
            if (!DESDE.containsKey(id)) {
               DESDE.put(id, tick);
               empezar(server, id);
               cambio = true;
            }
         }

         if (cambio) {
            sincronizar(server);
         }
      }
   }

   static boolean nadaEnEfecto() {
      return DESDE.isEmpty();
   }

   static boolean hayArmados() {
      return !ACTIVOS.isEmpty();
   }

   static boolean soloLuna() {
      if (!DESDE.containsKey("luna")) {
         return false;
      }

      for (String id : DESDE.keySet()) {
         if (!"luna".equals(id)) {
            return false;
         }
      }

      return true;
   }

   static boolean sinCielo() {
      if (!ACTIVOS.contains("luna") && !ACTIVOS.contains("eclipse")) {
         for (String id : ACTIVOS) {
            if (Momentos.conCieloPropio(id)) {
               return true;
            }
         }

         return false;
      } else {
         return true;
      }
   }

   static void sincronizar(MinecraftServer server) {
      Red.enviarATodos(server, payload());
   }

   private static ReviilPayload payload() {
      StringJoiner armados = new StringJoiner(",");
      ACTIVOS.forEach(armados::add);
      StringJoiner efecto = new StringJoiner(",");
      DESDE.keySet().forEach(efecto::add);
      return new ReviilPayload("momentos", 0, Component.literal(armados.toString()), Component.literal(efecto.toString()));
   }

   public static void alEntrar(ServerPlayer p) {
      Red.enviar(p, payload());
   }

   public static void alApagar(MinecraftServer servidor) {
      ACTIVOS.clear();
      SEGUNDOS.clear();
      SEGUNDOS2.clear();
      EXTRA.clear();
      DESDE.clear();
      regeneracionAntes = null;
      lunaEn = -1L;
   }

   public static void alTick(MinecraftServer servidor) {
      MinecraftServer server = Servidor.actual();
      if (server != null) {
         tick++;
         ruletaLuna(server);
         MomentosBunker.tick(server);
         if (!MomentoReviil.enMarcha()) {
            if (!DESDE.isEmpty()) {
               DESDE.clear();
               devolverRegeneracion(server);
               sincronizar(server);
            }
         } else if (!DESDE.isEmpty()) {
            for (ServerPlayer p : new ArrayList<>(server.getPlayerList().getPlayers())) {
               if (p.isAlive() && !p.isCreative() && !p.isSpectator()) {
                  if (tick % 20L == 0L) {
                     gas(p);
                     creepers(p);
                  }

                  if (tick % 5L == 0L && enEfecto("luna")) {
                     luna(server, p);
                  }
               }
            }
         }
      }
   }

   private static void empezar(MinecraftServer server, String id) {
      Momentos.Momento m = Momentos.porId(id);
      if (m != null) {
         int segundos = SEGUNDOS.getOrDefault(id, 0);
         if (m.temporizador() && segundos <= 0) {
            segundos = Momentos.delBunker(id) ? Ajustes.entero("bunker.contador") : Ajustes.entero("gas.contador");
            SEGUNDOS.put(id, segundos);
         }

         if (m.temporizador() && segundos > 0) {
            Red.enviarATodos(server, new ReviilPayload("temporizador", segundos, Component.empty(), Component.empty()));
         }

         MomentosBunker.empezar(server, id, segundos, SEGUNDOS2.getOrDefault(id, 0));
         if ("ceguera_creepers".equals(id)) {
            for (ServerPlayer p : server.getPlayerList().getPlayers()) {
               p.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, Ajustes.entero("ceguera.duracion") * 20, 0, false, false, true));
            }
         }

         if ("sin_regeneracion".equals(id)) {
            Boolean regla = server.getGameRules().get(GameRules.NATURAL_HEALTH_REGENERATION);
            if (regeneracionAntes == null) {
               regeneracionAntes = regla;
            }

            server.getGameRules().set(GameRules.NATURAL_HEALTH_REGENERATION, false, server);
         }
      }
   }

   private static void devolverRegeneracion(MinecraftServer server) {
      if (regeneracionAntes != null && server != null) {
         server.getGameRules().set(GameRules.NATURAL_HEALTH_REGENERATION, regeneracionAntes, server);
         regeneracionAntes = null;
      }
   }

   private static boolean gasListo(String id) {
      Long desde = DESDE.get(id);
      return desde != null && tick >= desde + SEGUNDOS.getOrDefault(id, 0).intValue() * 20L;
   }

   private static void gas(ServerPlayer p) {
      boolean arriba = gasListo("gas_arriba");
      boolean abajo = gasListo("gas_abajo") || gasListo("creepers_negativas");
      double y = p.getY();
      if (arriba && y < Ajustes.entero("gas.capa_arriba") || abajo && y >= Ajustes.entero("gas.capa_abajo")) {
         p.setInvulnerableTime(0);
         p.hurt(Ruleta.fuenteLetal(p, "ruleta.gas", " murió por el Gas Tóxico de Reviil"), Ajustes.decimal("dano.gas") * 2.0F);
      }
   }

   private static void creepers(ServerPlayer p) {
      boolean hay = false;

      for (String id : DESDE.keySet()) {
         if (Momentos.conCreepers(id)) {
            hay = true;
            break;
         }
      }

      ServerLevel nivel = p.level();
      if (hay && nivel.getDifficulty() != Difficulty.PEACEFUL && p.getRandom().nextInt(Math.max(1, Ajustes.entero("creepers.cada"))) == 0) {
         Creeper c = (Creeper)net.minecraft.world.entity.EntityTypes.CREEPER.create(nivel, net.minecraft.world.entity.EntitySpawnReason.EVENT);
         if (c != null) {
            c.snapTo(p.getX(), p.getY(), p.getZ(), p.getYRot(), 0.0F);
            c.finalizeSpawn(nivel, nivel.getCurrentDifficultyAt(p.blockPosition()), EntitySpawnReason.EVENT, null);
            net.ruleta.compat.Nbt.editar(c, t_ -> t_.putBoolean("powered", true));
            c.setHealth(1.0F);
            nivel.addFreshEntity(c);
         }
      }
   }

   private static void luna(MinecraftServer server, ServerPlayer p) {
      ServerLevel nivel = p.level();
      if (nivel.dimension() == Level.OVERWORLD && !p.hasEffect(MobEffects.BLINDNESS)) {
         float angulo = (((float)(nivel.getOverworldClockTime() % 24000L)) / 24000.0F) * (float) (Math.PI * 2);
         Vec3 luna = new Vec3(Mth.sin(angulo), -Mth.cos(angulo), 0.0);
         if (!(luna.y < 0.05) && !(p.getViewVector(1.0F).dot(luna) < 0.985)) {
            Vec3 ojos = p.getEyePosition();
            BlockHitResult golpe = nivel.clip(new ClipContext(ojos, ojos.add(luna.scale(128.0)), Block.VISUAL, Fluid.NONE, p));
            if (golpe.getType() == Type.MISS) {
               p.hurt(Ruleta.fuenteLetal(p, "ruleta.luna", " miró la Luna maldita de Reviil"), Float.MAX_VALUE);
               desactivar(server, "luna");
               sincronizar(server);
               lunaQuien = p.getGameProfile().name();
               lunaEn = tick + 280L;
            }
         }
      }
   }

   private static void ruletaLuna(MinecraftServer server) {
      if (lunaEn >= 0L && tick >= lunaEn && !Mensajes.ocupado()) {
         lunaEn = -1L;
         String nombre = lunaQuien;
         Ruleta.girar(server, RuletaColor.VERDE, nombre, false);
         Mensajes.programar(
            RuletaColor.VERDE, nombre, null, "El jugador {jugador} ha mirado la luna, ahora ya es seguro volver a mirar la luna.", -1, null, null, () -> {
               if (MomentoReviil.enMarcha() && !MomentoReviil.enMarchaConCielo() && DESDE.isEmpty() && ACTIVOS.isEmpty()) {
                  MomentoReviil.terminar(server);
               }
            }, false
         );
      }
   }
}
