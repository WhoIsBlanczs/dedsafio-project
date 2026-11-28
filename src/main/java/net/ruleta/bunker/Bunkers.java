package net.ruleta.bunker;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedData.Factory;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.ruleta.AbrirPantallaPayload;
import net.ruleta.Ajustes;
import net.ruleta.Efectos;
import net.ruleta.MomentosBunker;
import net.ruleta.Proteccion;
import net.ruleta.Red;
import net.ruleta.ZonaAvisoPayload;
import net.ruleta.compat.BlockEvent;
import net.ruleta.compat.EntityJoinLevelEvent;
import net.ruleta.compat.EventPriority;
import net.ruleta.compat.Evento;
import net.ruleta.compat.ExplosionEvent;
import net.ruleta.compat.FillBucketEvent;
import net.ruleta.compat.PlayerInteractEvent;
import net.ruleta.compat.SubscribeEvent;
import net.ruleta.puerta.BloquesBunker;
import net.ruleta.puerta.PuertaBunker;
import org.jetbrains.annotations.Nullable;

public final class Bunkers {
   public static final int SX = 96;
   public static final int SY = 34;
   public static final int SZ = 99;
   public static final String NOMBRE_DEFECTO = "Bunker de Eón";
   private static final ResourceLocation ESTRUCTURA = ResourceLocation.fromNamespaceAndPath("ruletafanproject", "bunker");
   private static final Map<UUID, Bunkers.Pendiente> PENDIENTES = new HashMap<>();
   private static BitSet interior;
   private static BitSet ocupado;
   public static final BlockPos[] PUERTAS = new BlockPos[]{new BlockPos(5, 3, 54), new BlockPos(45, 3, 7), new BlockPos(52, 3, 94), new BlockPos(92, 3, 47)};
   private static final List<Object[]> OBRAS = new ArrayList<>();
   private static final List<Demolicion> DEMOLICIONES = new ArrayList<>();
   private static final Set<UUID> VIENDO = new HashSet<>();
   private static BitSet solidoEstructura;
   private static final Map<UUID, Long> AVISOS = new HashMap<>();
   @Nullable
   private static Bunkers.Bunker visible;

   private Bunkers() {
   }

   private static int i(int x, int y, int z) {
      return (y * 99 + z) * 96 + x;
   }

   public static void alRegistrar(CommandDispatcher<CommandSourceStack> dispatcher) {
      CommandDispatcher<CommandSourceStack> d = dispatcher;
      d.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal(
                              "bunker"
                           )
                           .requires(s -> s.hasPermission(2)))
                        .then(
                           ((LiteralArgumentBuilder)Commands.literal("set").executes(ctx -> set(ctx, "Bunker de Eón")))
                              .then(
                                 Commands.argument("nombre", StringArgumentType.greedyString())
                                    .executes(ctx -> set(ctx, StringArgumentType.getString(ctx, "nombre")))
                              )
                        ))
                     .then(Commands.literal("build").executes(Bunkers::build)))
                  .then(Commands.literal("list").executes(Bunkers::list)))
               .then(Commands.literal("ajustes").executes(ctx -> {
                  ServerPlayer p = ((CommandSourceStack)ctx.getSource()).getPlayerOrException();
                  Red.enviar(p, new AbrirPantallaPayload("ajustes", ""));
                  return 1;
               })))
            .then(
               ((LiteralArgumentBuilder)Commands.literal("delete").executes(ctx -> {
                     ServerPlayer p = ((CommandSourceStack)ctx.getSource()).getPlayerOrException();
                     abrirBorrar(p);
                     return 1;
                  }))
                  .then(
                     Commands.argument("nombre", StringArgumentType.greedyString())
                        .suggests(
                           (ctx, b) -> SharedSuggestionProvider.suggest(
                              datos(((CommandSourceStack)ctx.getSource()).getServer()).lista.stream().map(Bunkers.Bunker::nombre).toList(), b
                           )
                        )
                        .executes(Bunkers::delete)
                  )
            )
      );
   }

   private static String limpiar(String s) {
      String r = s.trim();

      while (r.length() >= 2 && (r.startsWith("\"") && r.endsWith("\"") || r.startsWith("'") && r.endsWith("'"))) {
         r = r.substring(1, r.length() - 1).trim();
      }

      return r;
   }

   private static int set(CommandContext<CommandSourceStack> ctx, String crudo) throws CommandSyntaxException {
      ServerPlayer p = ((CommandSourceStack)ctx.getSource()).getPlayerOrException();
      String nombre = limpiar(crudo);
      if (!nombre.isEmpty() && nombre.length() <= 32) {
         BlockPos pie = p.blockPosition();
         BlockPos esquina = pie.offset(-48, 0, -49);
         String malo = motivoCerca(p.getServer(), p.level().dimension().location().toString(), esquina);
         if (malo != null) {
            ((CommandSourceStack)ctx.getSource()).sendFailure(Component.literal(malo));
            return 0;
         } else {
            PENDIENTES.put(p.getUUID(), new Bunkers.Pendiente(nombre, p.level().dimension(), esquina));
            BlockPos otra = esquina.offset(95, 33, 98);
            ((CommandSourceStack)ctx.getSource())
               .sendSuccess(
                  () -> Component.literal("«" + nombre + "» apuntado aquí. Ocupará de " + texto(esquina) + " a " + texto(otra) + ". Usa ")
                     .withStyle(ChatFormatting.YELLOW)
                     .append(
                        Component.literal("/bunker build")
                           .withStyle(
                              s -> s.withColor(ChatFormatting.GOLD)
                                 .withUnderlined(true)
                                 .withClickEvent(new ClickEvent(Action.SUGGEST_COMMAND, "/bunker build"))
                           )
                     )
                     .append(Component.literal(" para construirlo.").withStyle(ChatFormatting.YELLOW)),
                  false
               );
            return 1;
         }
      } else {
         ((CommandSourceStack)ctx.getSource()).sendFailure(Component.literal("El nombre tiene que tener entre 1 y 32 letras."));
         return 0;
      }
   }

   private static int build(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      ServerPlayer p = ((CommandSourceStack)ctx.getSource()).getPlayerOrException();
      Bunkers.Pendiente pe = PENDIENTES.get(p.getUUID());
      if (pe == null) {
         ((CommandSourceStack)ctx.getSource()).sendFailure(Component.literal("Primero apunta dónde va con /bunker set [nombre]."));
         return 0;
      }

      MinecraftServer server = p.getServer();
      ServerLevel nivel = server.getLevel(pe.dimension());
      if (nivel == null) {
         return 0;
      }

      Optional<StructureTemplate> t = nivel.getStructureManager().get(ESTRUCTURA);
      if (!t.isEmpty() && mascaras(nivel)) {
         String malo = motivoCerca(server, nivel.dimension().location().toString(), pe.esquina());
         if (malo != null) {
            ((CommandSourceStack)ctx.getSource()).sendFailure(Component.literal(malo));
            return 0;
         } else {
            ((CommandSourceStack)ctx.getSource())
               .sendSuccess(
                  () -> Component.literal("Construyendo «" + pe.nombre() + "» y preparando el terreno... (unos segundos)").withStyle(ChatFormatting.GRAY),
                  false
               );
            CommandSourceStack fuente = (CommandSourceStack)ctx.getSource();
            Bunkers.Bunker b = construir(nivel, pe.esquina(), pe.nombre(), hecho -> {
               if (hecho != null) {
                  fuente.sendSuccess(() -> Component.literal("«" + hecho.nombre() + "» construido.").withStyle(ChatFormatting.GREEN), true);
               } else {
                  fuente.sendFailure(Component.literal("No se pudo construir el bunker ahí."));
               }
            });
            if (b == null) {
               ((CommandSourceStack)ctx.getSource()).sendFailure(Component.literal("No se pudo construir el bunker ahí."));
               return 0;
            } else {
               PENDIENTES.remove(p.getUUID());
               return 1;
            }
         }
      } else {
         ((CommandSourceStack)ctx.getSource())
            .sendFailure(Component.literal("No se encontró la estructura del bunker (data/ruletafanproject/structures/bunker.nbt)."));
         return 0;
      }
   }

   public static BlockPos puerta(Bunkers.Bunker b, int i) {
      return new BlockPos(b.ex(), b.ey(), b.ez()).offset(PUERTAS[i]);
   }

   @Nullable
   public static ServerLevel nivel(MinecraftServer server, Bunkers.Bunker b) {
      return server.getLevel(ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(b.dimension())));
   }

   @Nullable
   public static Bunkers.Bunker elegirOConstruir(ServerPlayer admin) {
      MinecraftServer server = admin.getServer();
      List<Bunkers.Bunker> l = datos(server).lista;
      if (!l.isEmpty()) {
         return l.get(admin.getRandom().nextInt(l.size()));
      }

      ServerLevel nivel = server.overworld();
      if (!mascaras(nivel)) {
         return null;
      }

      double ang = admin.getRandom().nextDouble() * Math.PI * 2.0;
      int cx = (int)Math.floor(admin.getX() + Math.cos(ang) * 1000.0);
      int cz = (int)Math.floor(admin.getZ() + Math.sin(ang) * 1000.0);
      int cy = nivel.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, cx, cz);
      String nombre = "Bunker de Eón";
      BlockPos esquina = new BlockPos(cx - 48, Math.max(cy, nivel.getSeaLevel()), cz - 49);
      return construir(nivel, esquina, nombre, null);
   }

   @Nullable
   static Bunkers.Bunker construir(ServerLevel nivel, BlockPos esquina, String nombre, @Nullable Consumer<Bunkers.Bunker> alTerminar) {
      if (!nivel.getStructureManager().get(ESTRUCTURA).isEmpty() && mascaras(nivel)) {
         if (motivoCerca(nivel.getServer(), nivel.dimension().location().toString(), esquina) != null) {
            return null;
         }

         Bunkers.Bunker b = new Bunkers.Bunker(nombre, nivel.dimension().location().toString(), esquina.getX(), esquina.getY(), esquina.getZ());
         OBRAS.add(new Object[]{new Terreno.Obra(nivel, esquina, ocupado), b, alTerminar});
         return b;
      } else {
         return null;
      }
   }

   public static void alTickServidor(MinecraftServer servidor) {
      if (!DEMOLICIONES.isEmpty()) {
         avanzarDemoliciones(servidor);
      }

      if (!OBRAS.isEmpty()) {
         Object[] o = OBRAS.get(0);
         Terreno.Obra obra = (Terreno.Obra)o[0];
         if (obra.paso()) {
            OBRAS.remove(0);
            Bunkers.Bunker b = (Bunkers.Bunker)o[1];
            Consumer<Bunkers.Bunker> fin = (Consumer<Bunkers.Bunker>)o[2];
            ServerLevel nivel = obra.nivel();
            Optional<StructureTemplate> t = nivel.getStructureManager().get(ESTRUCTURA);
            BlockPos esquina = new BlockPos(b.ex(), b.ey(), b.ez());
            StructurePlaceSettings ajustes = new StructurePlaceSettings().setIgnoreEntities(false);
            boolean ok = t.isPresent() && t.get().placeInWorld(nivel, esquina, esquina, ajustes, nivel.getRandom(), 18);
            if (ok) {
               Bunkers.Datos d = datos(nivel.getServer());
               d.lista.add(b);
               d.setDirty();
               sincronizar(nivel.getServer());
            }

            if (fin != null) {
               fin.accept(ok ? b : null);
            }
         }
      }
   }

   private static int list(CommandContext<CommandSourceStack> ctx) {
      List<Bunkers.Bunker> l = datos(((CommandSourceStack)ctx.getSource()).getServer()).lista;
      if (l.isEmpty()) {
         ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.literal("No hay ningún bunker construido.").withStyle(ChatFormatting.GRAY), false);
         return 0;
      }

      ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.literal("Bunkers (" + l.size() + "):").withStyle(ChatFormatting.GOLD), false);

      for (Bunkers.Bunker b : l) {
         int x = (int)Math.floor(b.mx());
         int y = b.ey() + 1;
         int z = (int)Math.floor(b.mz());
         String tp = "/execute in " + b.dimension() + " run tp @s " + x + " " + (b.ey() + 34 + 1) + " " + z;
         Component linea = Component.literal(" • ")
            .withStyle(ChatFormatting.GRAY)
            .append(Component.literal(b.nombre()).withStyle(new ChatFormatting[]{ChatFormatting.WHITE, ChatFormatting.BOLD}))
            .append(Component.literal("  " + x + ", " + y + ", " + z + "  (" + dim(b.dimension()) + ")").withStyle(ChatFormatting.GRAY))
            .withStyle(
               s -> s.withClickEvent(new ClickEvent(Action.SUGGEST_COMMAND, tp))
                  .withHoverEvent(new HoverEvent(net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT, Component.literal("Clic para ir")))
            );
         ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> linea, false);
      }

      return l.size();
   }

   private static int delete(CommandContext<CommandSourceStack> ctx) {
      String nombre = limpiar(StringArgumentType.getString(ctx, "nombre"));
      MinecraftServer server = ((CommandSourceStack)ctx.getSource()).getServer();
      Bunkers.Bunker b = null;
      double mejor = Double.MAX_VALUE;
      Vec3 desde = ((CommandSourceStack)ctx.getSource()).getPosition();

      for (Bunkers.Bunker k : datos(server).lista) {
         if (k.nombre().equalsIgnoreCase(nombre)) {
            double d = desde.distanceToSqr(k.mx(), k.my(), k.mz());
            if (d < mejor) {
               mejor = d;
               b = k;
            }
         }
      }

      if (b == null) {
         ((CommandSourceStack)ctx.getSource()).sendFailure(Component.literal("No hay ningún bunker llamado «" + nombre + "»."));
         return 0;
      } else {
         String fallo = demoler(server, b, ((CommandSourceStack)ctx.getSource()).getEntity() instanceof ServerPlayer sp ? sp.getUUID() : null);
         if (fallo != null) {
            ((CommandSourceStack)ctx.getSource()).sendFailure(Component.literal(fallo));
            return 0;
         } else {
            Bunkers.Bunker borrado = b;
            ((CommandSourceStack)ctx.getSource())
               .sendSuccess(() -> Component.literal("Demoliendo «" + borrado.nombre() + "»...").withStyle(ChatFormatting.YELLOW), true);
            return 1;
         }
      }
   }

   private static double entre(int ex1, int ez1, int ex2, int ez2) {
      return Math.hypot(ex1 - ex2, ez1 - ez2);
   }

   public static boolean cercaDeBunker(ServerLevel nivel, double x, double z, double margen) {
      if (margen <= 0.0) {
         return false;
      }

      String dim = nivel.dimension().location().toString();
      List<Bunkers.Bunker> todos = new ArrayList<>(datos(nivel.getServer()).lista);

      for (Object[] o : OBRAS) {
         todos.add((Bunkers.Bunker)o[1]);
      }

      for (Bunkers.Bunker b : todos) {
         if (b.dimension().equals(dim) && x >= b.ex() - margen && x <= b.ex() + 96 + margen && z >= b.ez() - margen && z <= b.ez() + 99 + margen) {
            return true;
         }
      }

      return false;
   }

   @Nullable
   public static String motivoCerca(MinecraftServer server, String dimension, BlockPos esquina) {
      int min = Ajustes.entero("bunker.distancia_min");
      if (min <= 0) {
         return null;
      }

      Bunkers.Bunker peor = null;
      double dPeor = Double.MAX_VALUE;
      List<Bunkers.Bunker> todos = new ArrayList<>(datos(server).lista);

      for (Object[] o : OBRAS) {
         todos.add((Bunkers.Bunker)o[1]);
      }

      for (Bunkers.Bunker k : todos) {
         if (k.dimension().equals(dimension)) {
            double d = entre(esquina.getX(), esquina.getZ(), k.ex(), k.ez());
            if (d < min && d < dPeor) {
               dPeor = d;
               peor = k;
            }
         }
      }

      return peor == null
         ? null
         : "Demasiado cerca de «"
            + peor.nombre()
            + "» (a "
            + Math.round(dPeor)
            + " bloques): entre dos bunkers tiene que haber al menos "
            + min
            + " bloques (se cambia en Ajustes).";
   }

   private static BitSet solidos(ServerLevel nivel) {
      if (solidoEstructura != null) {
         return solidoEstructura;
      }

      BitSet r = new BitSet(323136);
      Optional<StructureTemplate> t = nivel.getStructureManager().get(ESTRUCTURA);
      if (t.isPresent()) {
         CompoundTag nbt = t.get().save(new CompoundTag());
         ListTag paleta = nbt.getList("palette", 10);
         if (paleta.isEmpty() && nbt.contains("palettes", 9)) {
            ListTag ps = nbt.getList("palettes", 9);
            if (!ps.isEmpty()) {
               paleta = ps.getList(0);
            }
         }

         BitSet aire = new BitSet();

         for (int k = 0; k < paleta.size(); k++) {
            String n = paleta.getCompound(k).getString("Name");
            if (n.equals("minecraft:air") || n.equals("minecraft:cave_air") || n.equals("minecraft:void_air") || n.equals("minecraft:structure_void")) {
               aire.set(k);
            }
         }

         ListTag bloques = nbt.getList("blocks", 10);

         for (int k = 0; k < bloques.size(); k++) {
            CompoundTag c = bloques.getCompound(k);
            if (!aire.get(c.getInt("state"))) {
               ListTag pos = c.getList("pos", 3);
               if (pos.size() == 3) {
                  marcar(r, new BlockPos(pos.getInt(0), pos.getInt(1), pos.getInt(2)));
               }
            }
         }
      }

      solidoEstructura = r;
      return r;
   }

   @Nullable
   static String demoler(MinecraftServer server, Bunkers.Bunker b, @Nullable UUID quien) {
      if (MomentosBunker.usa(b)) {
         return "«" + b.nombre() + "» está en un Momento Reviil: espera a que termine.";
      }

      ServerLevel nivel = nivel(server, b);
      if (nivel != null && mascaras(nivel)) {
         Bunkers.Datos d = datos(server);
         if (!d.lista.remove(b)) {
            return "Ese bunker ya no existe.";
         }

         d.setDirty();
         if (visible != null && visible.clave().equals(b.clave())) {
            visible = null;
         }

         DEMOLICIONES.add(new Demolicion(nivel, b, solidos(nivel), interior, quien));
         sincronizar(server);
         return null;
      } else {
         return "No se pudo leer la estructura del bunker.";
      }
   }

   private static void avanzarDemoliciones(MinecraftServer server) {
      Demolicion d = DEMOLICIONES.get(0);
      boolean fin = d.paso();
      if (fin) {
         DEMOLICIONES.remove(0);
         if (d.quien != null) {
            ServerPlayer p = server.getPlayerList().getPlayer(d.quien);
            if (p != null) {
               p.sendSystemMessage(Component.literal("«" + d.bunker.nombre() + "» demolido.").withStyle(ChatFormatting.GREEN));
            }
         }
      }

      if (fin || server.getTickCount() % 10 == 0) {
         for (UUID u : new ArrayList<>(VIENDO)) {
            ServerPlayer p = server.getPlayerList().getPlayer(u);
            if (p == null) {
               VIENDO.remove(u);
            } else {
               Red.enviar(p, new AbrirPantallaPayload("bunker_borrar_act", datosBorrar(p)));
            }
         }

         if (DEMOLICIONES.isEmpty()) {
            VIENDO.clear();
         }
      }
   }

   private static String limpio(String s) {
      return s.replace('\t', ' ').replace('\n', ' ');
   }

   private static String datosBorrar(ServerPlayer p) {
      StringBuilder sb = new StringBuilder();
      String dimP = p.level().dimension().location().toString();

      for (Bunkers.Bunker b : datos(p.getServer()).lista) {
         sb.append(linea(b, dimP, p, -1));
      }

      for (Demolicion d : DEMOLICIONES) {
         sb.append(linea(d.bunker, dimP, p, d.porcentaje()));
      }

      return sb.toString();
   }

   private static String linea(Bunkers.Bunker b, String dimP, ServerPlayer p, int pct) {
      long dist = b.dimension().equals(dimP) ? Math.round(Math.hypot(p.getX() - b.mx(), p.getZ() - b.mz())) : -1L;
      return b.clave()
         + "\t"
         + limpio(b.nombre())
         + "\t"
         + b.dimension()
         + "\t"
         + (int)Math.floor(b.mx())
         + "\t"
         + (b.ey() + 1)
         + "\t"
         + (int)Math.floor(b.mz())
         + "\t"
         + dist
         + "\t"
         + pct
         + "\n";
   }

   static void abrirBorrar(ServerPlayer p) {
      VIENDO.add(p.getUUID());
      Red.enviar(p, new AbrirPantallaPayload("bunker_borrar", datosBorrar(p)));
   }

   public static void accion(@Nullable ServerPlayer p, String accion, String clave) {
      if (p != null && p.getServer() != null && p.hasPermissions(2)) {
         MinecraftServer server = p.getServer();
         if ("abrir".equals(accion)) {
            abrirBorrar(p);
         } else if ("borrar".equals(accion)) {
            Bunkers.Bunker b = porClave(server, clave);
            if (b == null) {
               return;
            }

            String fallo = demoler(server, b, p.getUUID());
            if (fallo != null) {
               p.sendSystemMessage(Component.literal(fallo).withStyle(ChatFormatting.RED));
            } else {
               p.sendSystemMessage(Component.literal("Demoliendo «" + b.nombre() + "»...").withStyle(ChatFormatting.YELLOW));
            }

            Red.enviar(p, new AbrirPantallaPayload("bunker_borrar_act", datosBorrar(p)));
         } else if ("ir".equals(accion)) {
            Bunkers.Bunker b = porClave(server, clave);
            ServerLevel nivel = b == null ? null : nivel(server, b);
            if (b != null && nivel != null) {
               p.teleportTo(nivel, b.mx(), b.ey() + 34 + 1, b.mz(), p.getYRot(), p.getXRot());
            }
         }
      }
   }

   @Nullable
   public static Bunkers.Bunker porClave(MinecraftServer server, String clave) {
      if (clave != null && !clave.isEmpty()) {
         for (Bunkers.Bunker b : datos(server).lista) {
            if (b.clave().equals(clave)) {
               return b;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private static Bunkers.Bunker buscar(MinecraftServer server, String nombre) {
      for (Bunkers.Bunker b : datos(server).lista) {
         if (b.nombre().equalsIgnoreCase(nombre)) {
            return b;
         }
      }

      return null;
   }

   private static String texto(BlockPos p) {
      return p.getX() + " " + p.getY() + " " + p.getZ();
   }

   private static String dim(String id) {
      return switch (id) {
         case "minecraft:overworld" -> "Overworld";
         case "minecraft:the_nether" -> "Nether";
         case "minecraft:the_end" -> "End";
         default -> id;
      };
   }

   private static boolean mascaras(ServerLevel nivel) {
      if (interior != null) {
         return true;
      }

      Optional<StructureTemplate> t = nivel.getStructureManager().get(ESTRUCTURA);
      if (t.isEmpty()) {
         return false;
      }

      BitSet in = new BitSet(323136);
      BitSet oc = new BitSet(323136);
      StructurePlaceSettings s = new StructurePlaceSettings();

      for (StructureBlockInfo info : t.get().filterBlocks(BlockPos.ZERO, s, Blocks.AIR)) {
         marcar(in, info.pos());
      }

      Block[] propios = new Block[]{
         BloquesBunker.LADRILLO.get(),
         BloquesBunker.ESCALERA.get(),
         BloquesBunker.PISO.get(),
         BloquesBunker.PELIGRO.get(),
         PuertaBunker.BLOQUE.get(),
         PuertaBunker.BLOQUE_HOJA.get()
      };

      for (Block bl : propios) {
         for (StructureBlockInfo info : t.get().filterBlocks(BlockPos.ZERO, s, bl)) {
            marcar(oc, info.pos());
         }
      }

      interior = in;
      ocupado = oc;
      return true;
   }

   private static void marcar(BitSet b, BlockPos p) {
      if (p.getX() >= 0 && p.getY() >= 0 && p.getZ() >= 0 && p.getX() < 96 && p.getY() < 34 && p.getZ() < 99) {
         b.set(i(p.getX(), p.getY(), p.getZ()));
      }
   }

   @Nullable
   public static Bunkers.Bunker interiorDe(ServerLevel nivel, BlockPos p) {
      if (!mascaras(nivel)) {
         return null;
      }

      String dim = nivel.dimension().location().toString();

      for (Bunkers.Bunker b : datos(nivel.getServer()).lista) {
         if (b.dimension().equals(dim) && b.contiene(p) && interior.get(i(p.getX() - b.ex(), p.getY() - b.ey(), p.getZ() - b.ez()))) {
            return b;
         }
      }

      return null;
   }

   public static void sacar(ServerPlayer p, Bunkers.Bunker b) {
      double x = p.getX();
      double z = p.getZ();
      double aOeste = x - b.ex();
      double aEste = b.ex() + 96 - x;
      double aNorte = z - b.ez();
      double aSur = b.ez() + 99 - z;
      double min = Math.min(Math.min(aOeste, aEste), Math.min(aNorte, aSur));
      if (min == aOeste) {
         x = b.ex() - 4.5;
      } else if (min == aEste) {
         x = b.ex() + 96 + 3.5;
      } else if (min == aNorte) {
         z = b.ez() - 4.5;
      } else {
         z = b.ez() + 99 + 3.5;
      }

      ServerLevel nivel = p.serverLevel();
      MutableBlockPos q = new MutableBlockPos(x, b.ey(), z);

      for (int i = 0; i < 64 && (!nivel.getBlockState(q).isAir() || !nivel.getBlockState(q.above()).isAir()); i++) {
         q.move(0, 1, 0);
      }

      p.teleportTo(nivel, x, q.getY(), z, p.getYRot(), p.getXRot());
      p.displayClientMessage(Component.literal("El bunker se ha cerrado."), true);
   }

   public static boolean enCaja(Level nivel, BlockPos p) {
      if (nivel instanceof ServerLevel sl) {
         String dim = sl.dimension().location().toString();

         for (Bunkers.Bunker b : datos(sl.getServer()).lista) {
            if (b.dimension().equals(dim) && b.contiene(p)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static boolean protegido(Level nivel, BlockPos p, BlockState s) {
      return Proteccion.protegido(s.getBlock()) || esBloqueDeBunker(s) && enCaja(nivel, p);
   }

   private static boolean esBloqueDeBunker(BlockState s) {
      Block b = s.getBlock();
      return b == BloquesBunker.LADRILLO.get()
         || b == BloquesBunker.ESCALERA.get()
         || b == BloquesBunker.PISO.get()
         || b == BloquesBunker.PELIGRO.get()
         || b == PuertaBunker.BLOQUE.get()
         || b == PuertaBunker.BLOQUE_HOJA.get();
   }

   @SubscribeEvent(priority = EventPriority.HIGHEST)
   public static void alRomper(BlockEvent.BreakEvent e) {
      if (esBloqueDeBunker(e.getState()) && enCaja((Level)e.getLevel(), e.getPos())) {
         e.setCanceled(true);
      }
   }

   @SubscribeEvent(priority = EventPriority.HIGHEST)
   public static void alPicar(PlayerInteractEvent.LeftClickBlock e) {
      if (esBloqueDeBunker(e.getLevel().getBlockState(e.getPos())) && (enCaja(e.getLevel(), e.getPos()) || !Proteccion.puede(e.getEntity()))) {
         e.setCanceled(true);
      }
   }

   private static boolean privilegiado(Player p) {
      return p == null ? false : p.isCreative() || p instanceof ServerPlayer sp && sp.hasPermissions(Ajustes.entero("bunker.nivel_construir"));
   }

   private static void avisar(Player p) {
      if (p instanceof ServerPlayer sp) {
         long t = sp.serverLevel().getGameTime();
         Long ult = AVISOS.get(sp.getUUID());
         if (ult == null || t - ult >= 8L) {
            AVISOS.put(sp.getUUID(), t);
            Red.enviar(sp, new ZonaAvisoPayload("No puedes colocar bloques en esta zona.", Math.round(Ajustes.decimal("bunker.aviso_seg") * 1000.0F)));
         }
      }
   }

   @SubscribeEvent(priority = EventPriority.HIGHEST)
   public static void alUsarBloque(PlayerInteractEvent.RightClickBlock e) {
      Level nivel = e.getLevel();
      BlockPos donde = e.getPos().relative(e.getFace() == null ? Direction.UP : e.getFace());
      if (enCaja(nivel, e.getPos()) || enCaja(nivel, donde)) {
         if (privilegiado(e.getEntity())) {
            return;
         }

         ItemStack mano = e.getItemStack();
         if (mano.isEmpty()) {
            return;
         }

         e.setUseItem(Evento.Result.DENY);
         if (mano.getItem() instanceof BlockItem || mano.getItem() instanceof BucketItem) {
            avisar(e.getEntity());
         }
      }
   }

   @SubscribeEvent(priority = EventPriority.HIGHEST)
   public static void alCubo(FillBucketEvent e) {
      if (e.getTarget() instanceof BlockHitResult hit && hit.getType() != Type.MISS) {
         Level nivel = e.getLevel();
         if (enCaja(nivel, hit.getBlockPos()) || enCaja(nivel, hit.getBlockPos().relative(hit.getDirection())) || enCaja(nivel, e.getEntity().blockPosition())) {
            if (privilegiado(e.getEntity())) {
               return;
            }

            e.setCanceled(true);
            avisar(e.getEntity());
         }
      }
   }

   @SubscribeEvent(priority = EventPriority.HIGHEST)
   public static void alColocar(BlockEvent.EntityPlaceEvent e) {
      if (e.getLevel() instanceof Level nivel && enCaja(nivel, e.getPos())) {
         if (e.getEntity() instanceof Player pl) {
            if (privilegiado(pl)) {
               return;
            }

            avisar(pl);
         }

         e.setCanceled(true);
      }
   }

   @SubscribeEvent(priority = EventPriority.HIGHEST)
   public static void alFluir(BlockEvent.FluidPlaceBlockEvent e) {
      if (e.getLevel() instanceof Level nivel && enCaja(nivel, e.getPos())) {
         e.setCanceled(true);
      }
   }

   @SubscribeEvent
   public static void alExplotar(ExplosionEvent.Detonate e) {
      Level nivel = e.getLevel();
      e.getAffectedBlocks().removeIf(p -> esBloqueDeBunker(nivel.getBlockState(p)) && enCaja(nivel, p));
   }

   @SubscribeEvent
   public static void alAparecer(EntityJoinLevelEvent e) {
      if (!e.loadedFromDisk() && e.getEntity() instanceof Mob && e.getLevel() instanceof ServerLevel nivel) {
         if (interiorDe(nivel, e.getEntity().blockPosition()) != null) {
            e.setCanceled(true);
         }
      }
   }

   public static void alTick(ServerPlayer p) {
      if (p.tickCount % 10 == 0) {
         boolean dentro = interiorDe(p.serverLevel(), p.blockPosition()) != null
            || interiorDe(p.serverLevel(), BlockPos.containing(p.getEyePosition())) != null;
         boolean tiene = p.hasEffect(Efectos.ZONA_SEGURA.get());
         if (dentro && !tiene) {
            p.addEffect(new MobEffectInstance(Efectos.ZONA_SEGURA.get(), -1, 0, false, false, true));
         } else if (!dentro && tiene) {
            p.removeEffect(Efectos.ZONA_SEGURA.get());
         }
      }
   }

   public static void mostrarMarcador(MinecraftServer server, @Nullable Bunkers.Bunker b) {
      visible = b;
      sincronizar(server);
   }

   private static BunkerPayload marcadores() {
      return new BunkerPayload(visible == null ? new ArrayList<>() : new ArrayList<>(List.of(visible)), false);
   }

   private static BunkerPayload todos(MinecraftServer server) {
      return new BunkerPayload(new ArrayList<>(datos(server).lista), true);
   }

   static void sincronizar(MinecraftServer server) {
      Red.enviarATodos(server, marcadores());
      Red.enviarATodos(server, todos(server));
   }

   public static void alEntrar(ServerPlayer p) {
      if (p.getServer() != null) {
         Red.enviar(p, marcadores());
         Red.enviar(p, todos(p.getServer()));
      }
   }

   public static void alApagar(MinecraftServer servidor) {
      PENDIENTES.clear();
      OBRAS.clear();
      DEMOLICIONES.clear();
      VIENDO.clear();
      AVISOS.clear();
      solidoEstructura = null;
      visible = null;
      interior = null;
      ocupado = null;
   }

   private static Bunkers.Datos datos(MinecraftServer server) {
      return (Bunkers.Datos)server.overworld()
         .getDataStorage()
         .computeIfAbsent(new Factory(Bunkers.Datos::new, Bunkers.Datos::cargar, DataFixTypes.LEVEL), "ruleta_bunkers");
   }

   public record Bunker(String nombre, String dimension, int ex, int ey, int ez) {
      public double mx() {
         return this.ex + 48.0;
      }

      public double my() {
         return this.ey + 34 + 3;
      }

      public double mz() {
         return this.ez + 49.5;
      }

      public String clave() {
         return this.dimension + "|" + this.ex + "|" + this.ey + "|" + this.ez;
      }

      boolean contiene(BlockPos p) {
         int x = p.getX() - this.ex;
         int y = p.getY() - this.ey;
         int z = p.getZ() - this.ez;
         return x >= 0 && y >= 0 && z >= 0 && x < 96 && y < 34 && z < 99;
      }
   }

   static final class Datos extends SavedData {
      final List<Bunkers.Bunker> lista = new ArrayList<>();

      static Bunkers.Datos cargar(CompoundTag tag, Provider registros) {
         Bunkers.Datos d = new Bunkers.Datos();
         ListTag l = tag.getList("bunkers", 10);

         for (int k = 0; k < l.size(); k++) {
            CompoundTag c = l.getCompound(k);
            if (c.contains("ex")) {
               d.lista.add(new Bunkers.Bunker(c.getString("nombre"), c.getString("dim"), c.getInt("ex"), c.getInt("ey"), c.getInt("ez")));
            } else {
               d.lista.add(new Bunkers.Bunker(c.getString("nombre"), c.getString("dim"), c.getInt("x") - 48, c.getInt("y") - 34 - 1, c.getInt("z") - 49));
            }
         }

         return d;
      }

      public CompoundTag save(CompoundTag tag, Provider registros) {
         ListTag l = new ListTag();

         for (Bunkers.Bunker b : this.lista) {
            CompoundTag c = new CompoundTag();
            c.putString("nombre", b.nombre());
            c.putString("dim", b.dimension());
            c.putInt("ex", b.ex());
            c.putInt("ey", b.ey());
            c.putInt("ez", b.ez());
            l.add(c);
         }

         tag.put("bunkers", l);
         return tag;
      }
   }

   private record Pendiente(String nombre, ResourceKey<Level> dimension, BlockPos esquina) {
   }
}
