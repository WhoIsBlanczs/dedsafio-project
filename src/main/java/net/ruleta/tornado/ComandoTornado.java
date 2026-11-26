package net.ruleta.tornado;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.ruleta.Ajustes;
import net.ruleta.MomentosBunker;
import net.ruleta.Red;
import net.ruleta.Ruleta;
import net.ruleta.Servidor;

public final class ComandoTornado {
   public static final float RADIO_MIN = 4.0F;
   public static final float RADIO_MAX = 120.0F;
   private static final Map<UUID, ComandoTornado.Captura> CAPTURAS = new HashMap<>();
   private static final Map<UUID, Long> ENFRIA = new HashMap<>();
   private static boolean cielo;
   private static final List<ComandoTornado.Tornado> LISTA = new ArrayList<>();
   private static int siguienteId = 1;
   private static long tick;
   private static final double RADIO_ATRACCION = 20.0;

   public static boolean atrapado(Entity e) {
      return e != null && CAPTURAS.containsKey(e.getUUID());
   }

   private ComandoTornado() {
   }

   public static void alRegistrar(CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("tornado")
                     .requires(s -> s.hasPermission(2)))
                  .then(Commands.literal("cielo").executes(ctx -> ponerCielo((CommandSourceStack)ctx.getSource()))))
               .then(Commands.literal("stop").executes(ctx -> parar((CommandSourceStack)ctx.getSource()))))
            .then(
               ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("crear")
                           .executes(ctx -> crear((CommandSourceStack)ctx.getSource(), null, Ajustes.decimal("tornado.radio"))))
                        .then(tamano("tamaño", null)))
                     .then(tamano("tamano", null)))
                  .then(
                     ((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("pos", Vec3Argument.vec3())
                              .executes(ctx -> crear((CommandSourceStack)ctx.getSource(), Vec3Argument.getVec3(ctx, "pos"), Ajustes.decimal("tornado.radio"))))
                           .then(tamano("tamaño", "pos")))
                        .then(tamano("tamano", "pos"))
                  )
            )
      );
   }

   private static LiteralArgumentBuilder<CommandSourceStack> tamano(String palabra, String pos) {
      return (LiteralArgumentBuilder<CommandSourceStack>)Commands.literal(palabra)
         .then(
            Commands.argument("tam", FloatArgumentType.floatArg(4.0F, 120.0F))
               .executes(
                  ctx -> crear((CommandSourceStack)ctx.getSource(), pos == null ? null : Vec3Argument.getVec3(ctx, pos), FloatArgumentType.getFloat(ctx, "tam"))
               )
         );
   }

   private static int ponerCielo(CommandSourceStack fuente) {
      if (cielo) {
         fuente.sendFailure(Component.literal("La tormenta ya está en marcha."));
         return 0;
      } else {
         cielo = true;
         lluvia(fuente.getServer());
         enviarATodos(fuente.getServer());
         fuente.sendSuccess(
            () -> Component.literal("Tormenta: el cielo se ha cerrado y empieza a llover. Usa /tornado crear para que aparezca un tornado."), true
         );
         return 1;
      }
   }

   private static int parar(CommandSourceStack fuente) {
      if (!cielo && LISTA.isEmpty()) {
         fuente.sendFailure(Component.literal("No hay ninguna tormenta en marcha."));
         return 0;
      } else {
         pararTodo(fuente.getServer());
         fuente.sendSuccess(() -> Component.literal("Tormenta detenida."), true);
         return 1;
      }
   }

   public static void pararTodo(MinecraftServer server) {
      boolean habia = cielo;
      cielo = false;
      LISTA.clear();
      CAPTURAS.clear();
      if (habia) {
         server.overworld().setWeatherParameters(6000, 0, false, false);
      }

      enviarATodos(server);
   }

   private static void lluvia(MinecraftServer server) {
      ServerLevel mundo = server.overworld();
      if (!mundo.isRaining() || !mundo.isThundering()) {
         mundo.setWeatherParameters(0, 24000, true, true);
      }
   }

   public static void momentoTornados(MinecraftServer server) {
      ServerLevel mundo = server.overworld();
      List<ServerPlayer> jugadores = new ArrayList<>();

      for (ServerPlayer p : mundo.players()) {
         if (!p.isSpectator()) {
            jugadores.add(p);
         }
      }

      cielo = true;
      lluvia(server);
      if (jugadores.isEmpty()) {
         enviarATodos(server);
      } else {
         int cuantos = Ajustes.entero("tornado.momento_cuantos");
         double separacion = Ajustes.entero("tornado.momento_separacion");
         double alcance = Math.max(48.0, server.getPlayerList().getSimulationDistance() * 16.0 - 24.0);
         LISTA.removeIf(t -> {
            for (ServerPlayer p : jugadores) {
               if (Math.hypot(p.getX() - t.x, p.getZ() - t.z) <= alcance * 2.0) {
                  return false;
               }
            }

            return true;
         });
         int faltan = cuantos - LISTA.size();
         double[] bun = MomentosBunker.centro();
         List<double[]> puestos = new ArrayList<>();

         for (ComandoTornado.Tornado t : LISTA) {
            puestos.add(new double[]{t.x, t.z});
         }

         float radio = Ajustes.decimal("tornado.radio");
         float alto = Ajustes.decimal("tornado.alto");
         int puestosNuevos = 0;

         for (int intento = 0; intento < Math.max(1, faltan) * 60 && puestosNuevos < faltan; intento++) {
            ServerPlayer base = jugadores.get(mundo.random.nextInt(jugadores.size()));
            double ang = mundo.getRandom().nextDouble() * Math.PI * 2.0;
            double d = 20.0 + mundo.random.nextDouble() * alcance;
            double x = base.getX() + Math.cos(ang) * d;
            double z = base.getZ() + Math.sin(ang) * d;
            if (mundo.hasChunkAt(BlockPos.containing(x, base.getY(), z)) && (bun == null || !(Math.hypot(x - bun[0], z - bun[1]) < 200.0))) {
               boolean junto = false;

               for (double[] o : puestos) {
                  junto |= Math.hypot(x - o[0], z - o[1]) < separacion;
               }

               if (!junto) {
                  double y = sueloBajo(mundo, x, z, Math.max(2.0, radio * 0.35)) - 2.0;
                  LISTA.add(
                     new ComandoTornado.Tornado(
                        siguienteId++,
                        mundo.dimension(),
                        x,
                        y,
                        z,
                        radio,
                        alto,
                        mundo.getRandom().nextDouble() * Math.PI * 2.0,
                        0,
                        mundo.random.nextDouble() * 1000.0
                     )
                  );
                  puestos.add(new double[]{x, z});
                  puestosNuevos++;
               }
            }
         }

         enviarATodos(server);
      }
   }

   private static int crear(CommandSourceStack fuente, Vec3 sitio, float radio) {
      ServerLevel mundo = fuente.getLevel();
      if (sitio == null) {
         Vec3 ojos = fuente.getPosition();
         Vec3 mira = new Vec3(0.0, 0.0, 1.0);
         if (fuente.getEntity() != null) {
            ojos = fuente.getEntity().getEyePosition();
            mira = fuente.getEntity().getLookAngle();
         }

         Vec3 plano = new Vec3(mira.x, 0.0, mira.z);
         plano = plano.lengthSqr() < 1.0E-4 ? new Vec3(0.0, 0.0, 1.0) : plano.normalize();
         sitio = new Vec3(ojos.x + plano.x * radio * 5.0, ojos.y, ojos.z + plano.z * radio * 5.0);
      }

      double y = sitio.y;
      BlockPos bp = BlockPos.containing(sitio);
      if (mundo.hasChunkAt(bp)) {
         y = sueloBajo(mundo, sitio.x, sitio.z, Math.max(2.0, radio * 0.35)) - 2.0;
      }

      float alto = Ajustes.decimal("tornado.alto");
      int vida = Ajustes.entero("tornado.vida") * 20;
      ComandoTornado.Tornado t = new ComandoTornado.Tornado(
         siguienteId++,
         mundo.dimension(),
         sitio.x,
         y,
         sitio.z,
         radio,
         alto,
         mundo.getRandom().nextDouble() * Math.PI * 2.0,
         vida,
         mundo.random.nextDouble() * 1000.0
      );
      LISTA.add(t);
      if (Ajustes.bool("tornado.lluvia") && !cielo) {
         cielo = true;
         lluvia(fuente.getServer());
      }

      enviarATodos(fuente.getServer());
      Vec3 donde = sitio;
      fuente.sendSuccess(
         () -> Component.literal(String.format("Ha aparecido un tornado (radio %.0f) en %.0f, %.0f, %.0f.", radio, donde.x, t.y, donde.z)), true
      );
      return 1;
   }

   public static void alTick(MinecraftServer servidor) {
      MinecraftServer server = Servidor.actual();
      if (server != null && (cielo || !LISTA.isEmpty() || !CAPTURAS.isEmpty())) {
         tick++;
         if (cielo && tick % 200L == 0L && Ajustes.bool("tornado.lluvia")) {
            lluvia(server);
         }

         boolean cambio = false;
         Iterator<ComandoTornado.Tornado> it = LISTA.iterator();

         while (it.hasNext()) {
            ComandoTornado.Tornado t = it.next();
            ServerLevel mundo = server.getLevel(t.dim);
            if (mundo != null && (t.vida <= 0 || t.edad <= t.vida)) {
               t.edad++;
               mover(mundo, t);
               efectos(mundo, t);
            } else {
               it.remove();
               cambio = true;
            }
         }

         cambio |= actualizarCapturas(server);
         if (tick % 20L == 0L) {
            ENFRIA.values().removeIf(v -> v < tick);
         }

         if (!LISTA.isEmpty() && tick % 2L == 0L) {
            enviarATodos(server);
         } else if (cambio) {
            enviarATodos(server);
         }
      }
   }

   private static void mover(ServerLevel mundo, ComandoTornado.Tornado t) {
      double v = Ajustes.decimal("tornado.velocidad") / 20.0;
      t.rumbo = t.rumbo + (Math.sin(t.edad * 0.004 + t.semilla) * 0.003 + (mundo.getRandom().nextDouble() - 0.5) * 0.003);
      double nx = t.x + Math.cos(t.rumbo) * v;
      double nz = t.z + Math.sin(t.rumbo) * v;
      BlockPos p = BlockPos.containing(nx, t.y, nz);
      if (!mundo.hasChunkAt(p)) {
         t.rumbo += Math.PI / 2;
         t.vx = 0.0;
         t.vz = 0.0;
      } else {
         t.vx = nx - t.x;
         t.vz = nz - t.z;
         t.x = nx;
         t.z = nz;
         double suelo = sueloBajo(mundo, t.x, t.z, Math.max(2.0, t.radio * 0.35)) - 2.0;
         t.y = Mth.lerp(suelo < t.y ? 0.5 : 0.15, t.y, suelo);
      }
   }

   public static double sueloBajo(ServerLevel mundo, double x, double z, double r) {
      double[][] pts = new double[][]{{0.0, 0.0}, {r, 0.0}, {-r, 0.0}, {0.0, r}, {0.0, -r}};
      double min = Double.MAX_VALUE;

      for (double[] q : pts) {
         int bx = Mth.floor(x + q[0]);
         int bz = Mth.floor(z + q[1]);
         if (mundo.hasChunkAt(new BlockPos(bx, 64, bz))) {
            min = Math.min(min, mundo.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, bx, bz));
         }
      }

      return min == Double.MAX_VALUE ? mundo.getMinY() : min;
   }

   private static void efectos(ServerLevel mundo, ComandoTornado.Tornado t) {
      double tirar = 20.0;
      double nucleo = Math.min(Math.max(5.0, t.radio * 0.55), tirar / 1.4);
      float fuerza = Ajustes.decimal("tornado.fuerza");
      boolean golpe = t.edad % (Math.max(1, Ajustes.entero("tornado.dano_cada")) * 10) == 0;
      float dano = Ajustes.decimal("tornado.dano") * 2.0F;
      AABB caja = new AABB(t.x - tirar, t.y - 4.0, t.z - tirar, t.x + tirar, t.y + t.alto, t.z + tirar);

      for (Entity en : mundo.getEntities((Entity)null, caja, x -> !x.isSpectator() && !x.isRemoved() && (!(x instanceof LivingEntity) || x instanceof Player))) {
         if (!(en instanceof Player p && (p.getAbilities().flying || p.isCreative())) && !CAPTURAS.containsKey(en.getUUID())) {
            double dx = en.getX() - t.x;
            double dz = en.getZ() - t.z;
            double d = Math.sqrt(dx * dx + dz * dz);
            if (!(d > tirar)) {
               if (d < nucleo * 1.4 && en.getY() < t.y + t.alto * 0.45 && puedeAtraparse(en) && ENFRIA.getOrDefault(en.getUUID(), 0L) < tick) {
                  CAPTURAS.put(en.getUUID(), new ComandoTornado.Captura(en, t, Math.atan2(dz, dx), Math.max(2.5, d), Math.max(0.0, en.getY() - t.y)));
               } else {
                  double ux = d < 0.001 ? 1.0 : dx / d;
                  double uz = d < 0.001 ? 0.0 : dz / d;
                  double k = Math.pow(1.0 - d / tirar, 1.4);
                  Vec3 m = en.getDeltaMovement();
                  double giro = 0.3 * fuerza * k;
                  double hacia = 0.08 * fuerza * k;
                  double nx = m.x * 0.9 + -uz * giro - ux * hacia;
                  double nz = m.z * 0.9 + ux * giro - uz * hacia;
                  double vel = Math.sqrt(nx * nx + nz * nz);
                  if (vel > 0.9) {
                     nx *= 0.9 / vel;
                     nz *= 0.9 / vel;
                  }

                  en.setDeltaMovement(nx, m.y, nz);
                  en.hurtMarked = true;
                  if (golpe && d < nucleo && en instanceof LivingEntity viva && dano > 0.0F) {
                     if (viva instanceof ServerPlayer jugador) {
                        jugador.hurt(Ruleta.fuenteLetalPublica(jugador, "ruleta.tornado", " murió por el Tornado"), dano);
                     } else {
                        viva.hurt(mundo.damageSources().generic(), dano);
                     }
                  }
               }
            }
         }
      }
   }

   private static boolean puedeAtraparse(Entity en) {
      if (!Ajustes.bool("tornado.atrapar") || en.isPassenger() || en.isSpectator() || !en.isAlive()) {
         return false;
      } else {
         return !(en instanceof ServerPlayer p) ? false : !p.isCreative() && !p.getAbilities().flying;
      }
   }

   private static double radioEn(double R, double v) {
      return R * (0.06 + 1.55 * v + 1.1 * Math.pow(v, 5.0) + 0.3 * Math.exp(-v * 18.0));
   }

   private static double[] desvio(ComandoTornado.Tornado t, double v) {
      double atras = t.alto * 0.14 * Math.pow(Math.max(0.0, v), 1.3);
      return new double[]{-Math.cos(t.rumbo) * atras, -Math.sin(t.rumbo) * atras};
   }

   private static boolean actualizarCapturas(MinecraftServer server) {
      boolean cambio = false;
      float danoGiro = Ajustes.decimal("tornado.dano_giro") * 2.0F;
      double sube = 0.28 * Ajustes.decimal("tornado.subida");
      double fraccion = Ajustes.decimal("tornado.tragar_altura");
      boolean traga = Ajustes.bool("tornado.tragar");
      Iterator<ComandoTornado.Captura> it = CAPTURAS.values().iterator();

      while (it.hasNext()) {
         ComandoTornado.Captura c = it.next();
         Entity e = c.ent;
         ComandoTornado.Tornado t = c.t;
         boolean suelto = e.isRemoved()
            || !e.isAlive()
            || !LISTA.contains(t)
            || !(e.level() instanceof ServerLevel mundo && mundo.dimension() == t.dim && c.ticks <= 1200 && Ajustes.bool("tornado.atrapar"))
            || e instanceof ServerPlayer p && (p.isCreative() || p.isSpectator() || p.getAbilities().flying);
         if (suelto) {
            it.remove();
            soltar(e, null);
            cambio = true;
         } else {
            ServerLevel mundo = (ServerLevel)e.level();
            c.ticks++;
            double v = Math.min(1.0, c.h / t.alto);
            if (!c.traga) {
               c.h += sube;
               double objetivo = Math.max(3.0, radioEn(t.radio, v) * 0.85 + 2.0);
               c.r = c.r + (objetivo - c.r) * 0.08;
               c.ang = c.ang + Mth.clamp(0.85 / Math.max(c.r, 4.0), 0.06, 0.3);
               if (c.h >= t.alto * fraccion) {
                  if (!traga) {
                     it.remove();
                     soltar(e, c);
                     cambio = true;
                     continue;
                  }

                  c.traga = true;
               }
            } else {
               c.r *= 0.82;
               c.ang += 0.34;
               c.tragaTicks++;
               if (c.tragaTicks >= 14) {
                  it.remove();
                  if (e instanceof ServerPlayer p) {
                     p.setInvulnerableTime(0);
                     p.hurt(Ruleta.fuenteLetalPublica(p, "ruleta.tornado", " fue tragado por el Tornado"), 1000.0F);
                  } else if (e instanceof LivingEntity viva) {
                     viva.setInvulnerableTime(0);
                     viva.hurt(mundo.damageSources().generic(), 1000.0F);
                  }

                  soltar(e, c);
                  cambio = true;
                  continue;
               }
            }

            double[] off = desvio(t, v);
            double tx = t.x + off[0] + Math.cos(c.ang) * c.r;
            double tz = t.z + off[1] + Math.sin(c.ang) * c.r;
            double ty = t.y + c.h;
            Vec3 falta = new Vec3(tx - e.getX(), ty - e.getY(), tz - e.getZ());
            Vec3 vel = falta.scale(0.5);
            double l = vel.length();
            if (l > 1.6) {
               vel = vel.scale(1.6 / l);
            }

            e.setDeltaMovement(vel.x, vel.y + 0.08, vel.z);
            e.hurtMarked = true;
            e.fallDistance = 0.0F;
            if (e instanceof LivingEntity viva && !c.traga && danoGiro > 0.0F && c.ticks % 10 == 0) {
               viva.setInvulnerableTime(0);
               if (viva instanceof ServerPlayer jugador) {
                  jugador.hurt(Ruleta.fuenteLetalPublica(jugador, "ruleta.tornado", " murió por el Tornado"), danoGiro);
               } else {
                  viva.hurt(mundo.damageSources().generic(), danoGiro);
               }
            }
         }
      }

      return cambio;
   }

   private static void soltar(Entity e, ComandoTornado.Captura c) {
      ENFRIA.put(e.getUUID(), tick + 200L);
      e.fallDistance = 0.0F;
      if (c != null && e.isAlive()) {
         double a = c.ang;
         e.setDeltaMovement(Math.cos(a) * 0.9 - Math.sin(a) * 1.1, 0.5, Math.sin(a) * 0.9 + Math.cos(a) * 1.1);
      } else if (e.isAlive()) {
         e.setDeltaMovement(e.getDeltaMovement().scale(0.3));
      }

      e.hurtMarked = true;
   }

   private static TornadoPayload estado() {
      List<TornadoPayload.Torn> l = new ArrayList<>();

      for (ComandoTornado.Tornado t : LISTA) {
         l.add(new TornadoPayload.Torn(t.id, new Vec3(t.x, t.y, t.z), t.radio, t.alto, (float)t.vx, (float)t.vz, t.edad, t.dim.location().toString()));
      }

      int[] ids = new int[CAPTURAS.size()];
      int k = 0;

      for (ComandoTornado.Captura c : CAPTURAS.values()) {
         ids[k++] = c.ent.getId();
      }

      return new TornadoPayload(cielo, l, ids);
   }

   private static void enviarATodos(MinecraftServer server) {
      Red.enviarATodos(server, estado());
   }

   public static void alEntrar(ServerPlayer p) {
      if (cielo || !LISTA.isEmpty()) {
         Red.enviar(p, estado());
      }
   }

   public static void alApagar(MinecraftServer servidor) {
      cielo = false;
      LISTA.clear();
      CAPTURAS.clear();
      ENFRIA.clear();
   }

   private static final class Captura {
      final Entity ent;
      final ComandoTornado.Tornado t;
      double ang;
      double r;
      double h;
      int ticks;
      boolean traga;
      int tragaTicks;

      Captura(Entity ent, ComandoTornado.Tornado t, double ang, double r, double h) {
         this.ent = ent;
         this.t = t;
         this.ang = ang;
         this.r = r;
         this.h = h;
      }
   }

   private static final class Tornado {
      final int id;
      final ResourceKey<Level> dim;
      double x;
      double y;
      double z;
      final float radio;
      final float alto;
      double rumbo;
      double vx;
      double vz;
      int edad;
      final int vida;
      final double semilla;

      Tornado(int id, ResourceKey<Level> dim, double x, double y, double z, float radio, float alto, double rumbo, int vida, double semilla) {
         this.id = id;
         this.dim = dim;
         this.x = x;
         this.y = y;
         this.z = z;
         this.radio = radio;
         this.alto = alto;
         this.rumbo = rumbo;
         this.vida = vida;
         this.semilla = semilla;
      }
   }
}
