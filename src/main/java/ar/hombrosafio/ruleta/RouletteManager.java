package ar.hombrosafio.ruleta;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LightningBolt;

/** Estado de la ruleta: comandos, giro sincronizado, anuncio final, castigos y temporizadores. */
public final class RouletteManager {
	/** Opciones del formulario del cliente. */
	public record Launch(String title, String message, String target, boolean mini, String punishment, int timerSeconds) {
		public static Launch plain() {
			return new Launch("", "", "*", false, "none", 0);
		}
	}

	private record Timer(long endTick, String label, List<java.util.UUID> targets) {
	}

	public static final List<String> PUNISHMENTS = List.of("none", "lightning", "hurt", "blind", "kill");

	private static RoulettePayload state = RoulettePayload.NONE;
	private static long sequence;
	private static int elapsedTicks;
	private static int lastSent;
	private static DifficultyChange pendingChange;
	private static Launch pendingLaunch = Launch.plain();
	private static final List<Timer> TIMERS = new ArrayList<>();

	public static void register() {
		CommandRegistrationCallback.EVENT.register((dispatcher, access, environment) -> {
			LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("ruleta").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS));
			root.executes(c -> open(c.getSource(), "picker"));
			for (String screen : List.of("ajustes", "cambios", "panel")) {
				root.then(Commands.literal(screen).executes(c -> open(c.getSource(), screen)));
			}
			for (RouletteColor color : RouletteColor.values()) {
				LiteralArgumentBuilder<CommandSourceStack> branch = Commands.literal(color.command);
				if (color == RouletteColor.YELLOW) {
					for (DifficultyChange change : DifficultyChange.values()) {
						LiteralArgumentBuilder<CommandSourceStack> option = Commands.literal(change.id);
						if (change == DifficultyChange.TEXT) {
							option.then(Commands.argument("texto", StringArgumentType.greedyString())
								.executes(c -> run(c.getSource(), color, StringArgumentType.getString(c, "texto"), null, Launch.plain())));
						} else {
							option.executes(c -> run(c.getSource(), color, change.announcement, change, Launch.plain()));
						}
						branch.then(option);
					}
					branch.then(Commands.literal("quitar").then(Commands.argument("cambio", StringArgumentType.word()).suggests((c, b) -> {
						for (DifficultyChange change : DifficultyChanges.active()) {
							b.suggest(change.id);
						}
						return b.buildFuture();
					}).executes(c -> removeChange(c.getSource().getServer(), c.getSource(), StringArgumentType.getString(c, "cambio")))));
				} else {
					branch.then(Commands.argument("texto", StringArgumentType.greedyString())
						.executes(c -> run(c.getSource(), color, StringArgumentType.getString(c, "texto"), null, Launch.plain())));
				}
				root.then(branch);
			}
			root.then(Commands.literal("cancelar").executes(c -> {
				if (!active()) {
					return fail(c.getSource(), "No hay ninguna ruleta activa.");
				}
				cancel(c.getSource().getServer());
				return reply(c.getSource(), "Ruleta cancelada.");
			}));
			dispatcher.register(root);
		});
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> sync(handler.player));
		ServerTickEvents.END_SERVER_TICK.register(RouletteManager::tick);
		ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
			clear();
			TIMERS.clear();
		});
	}

	public static boolean active() {
		return state.active();
	}

	// ------------------------------------------------------------------ entrada

	private static int open(CommandSourceStack source, String screen) {
		if (source.getEntity() instanceof ServerPlayer player) {
			if (ServerPlayNetworking.canSend(player, OpenScreenPayload.TYPE)) {
				ServerPlayNetworking.send(player, new OpenScreenPayload(screen));
				return 1;
			}
			return fail(source, "Tu cliente no tiene el mod Ruleta instalado.");
		}
		return fail(source, "Este comando solo lo puede usar un jugador.");
	}

	private static int run(CommandSourceStack source, RouletteColor color, String text, DifficultyChange change, Launch launch) {
		String error = start(source.getServer(), color, text, change, launch);
		return error == null ? reply(source, "Ruleta " + color.command + " iniciada.") : fail(source, error);
	}

	private static int removeChange(MinecraftServer server, CommandSourceStack source, String id) {
		DifficultyChange change = DifficultyChange.byId(id);
		if (change == null || change == DifficultyChange.TEXT) {
			return fail(source, "No existe el cambio " + id + ".");
		}
		if (!DifficultyChanges.deactivate(server, change)) {
			return fail(source, "El cambio " + id + " no está activo.");
		}
		server.getPlayerList().broadcastSystemMessage(
			Component.literal("✦ Se quitó un cambio de la ruleta amarilla: " + change.announcement + ".").withStyle(ChatFormatting.YELLOW), false);
		return reply(source, "Cambio " + id + " desactivado.");
	}

	/** Lanzamiento desde el formulario del cliente. Devuelve un mensaje de error o {@code null}. */
	public static String launch(ServerPlayer player, LaunchPayload p) {
		if (!RuletaConfig.canEdit(player)) {
			return "No tienes permiso para usar la ruleta.";
		}
		if (p.color() < 0 || p.color() >= RouletteColor.values().length) {
			return "Color de ruleta inválido.";
		}
		RouletteColor color = RouletteColor.values()[p.color()];
		MinecraftServer server = player.level().getServer();
		if (color == RouletteColor.YELLOW) {
			DifficultyChange change = DifficultyChange.byId(p.change());
			if (change == null) {
				return "Elige un cambio de dificultad.";
			}
			if (change == DifficultyChange.TEXT) {
				return start(server, color, p.message(), null, Launch.plain());
			}
			if (DifficultyChanges.isActive(change)) {
				return "Ese cambio ya está activo.";
			}
			return start(server, color, change.announcement, change, Launch.plain());
		}
		String punishment = PUNISHMENTS.contains(p.punishment()) ? p.punishment() : "none";
		String text = p.message().isBlank() ? p.title() : p.message();
		return start(server, color, text, null, new Launch(p.title().strip(), p.message().strip(), p.target(), p.mini(), punishment, parseTimer(p.timer())));
	}

	public static int parseTimer(String text) {
		String t = text.strip();
		if (t.isEmpty()) {
			return 0;
		}
		try {
			String[] parts = t.split(":");
			int total = 0;
			for (String part : parts) {
				total = total * 60 + Integer.parseInt(part.strip());
			}
			return Math.clamp(total, 0, 86_400);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	/** Inicia el giro. Devuelve un mensaje de error o {@code null} si salió bien. */
	public static String start(MinecraftServer server, RouletteColor color, String text, DifficultyChange change, Launch launch) {
		if (active()) {
			return "Ya hay una ruleta activa. Usa /ruleta cancelar antes de iniciar otra.";
		}
		String shown = text == null ? "" : text.strip();
		if (shown.isEmpty() || shown.length() > 1000 || shown.chars().anyMatch(ch -> Character.isISOControl(ch) && ch != '\n')) {
			return "El anuncio debe tener entre 1 y 1000 caracteres.";
		}
		elapsedTicks = 0;
		lastSent = 0;
		String payloadText = (launch.title().isBlank() ? shown : launch.title());
		state = new RoulettePayload(++sequence, 0, color.ordinal(), payloadText.substring(0, Math.min(payloadText.length(), 200)));
		pendingChange = change;
		pendingLaunch = launch.message().isBlank() ? new Launch(launch.title(), shown, launch.target(), launch.mini(), launch.punishment(), launch.timerSeconds()) : launch;
		broadcast(server);
		return null;
	}

	public static void cancel(MinecraftServer server) {
		clear();
		broadcast(server);
	}

	private static void clear() {
		state = RoulettePayload.NONE;
		pendingChange = null;
		pendingLaunch = Launch.plain();
		elapsedTicks = 0;
		lastSent = 0;
	}

	// ------------------------------------------------------------------ tick

	private static void tick(MinecraftServer server) {
		tickTimers(server);
		if (!active()) {
			return;
		}
		elapsedTicks++;
		int elapsed = Math.min(RouletteColor.TOTAL, elapsedTicks);
		if (elapsed >= RouletteColor.TOTAL) {
			finish(server);
		} else {
			state = new RoulettePayload(state.sequence(), elapsed, state.color(), state.message());
			if (elapsed - lastSent >= 10 || lastSent < RouletteColor.SPIN && elapsed >= RouletteColor.SPIN) {
				lastSent = elapsed;
				broadcast(server);
			}
		}
	}

	private static void finish(MinecraftServer server) {
		RoulettePayload finished = state;
		RouletteColor color = finished.chosen();
		DifficultyChange change = pendingChange;
		Launch launch = pendingLaunch;
		clear();
		broadcast(server);

		String body = change != null && change != DifficultyChange.TEXT ? change.description : launch.message().isBlank() ? finished.message() : launch.message();
		List<ServerPlayer> targets = targets(server, launch.target());
		Component announcement = RouletteAnnouncement.message(color, launch.title().isBlank() || color == RouletteColor.YELLOW ? body : launch.title() + "\n" + body);

		for (ServerPlayer p : color == RouletteColor.YELLOW ? server.getPlayerList().getPlayers() : targets) {
			p.sendSystemMessage(announcement);
			if (!launch.mini() && color != RouletteColor.YELLOW) {
				p.connection.send(new ClientboundSetTitlesAnimationPacket(10, 70, 20));
				Component big = Component.literal(launch.title().isBlank() ? color.label : launch.title())
					.withStyle(style -> style.withColor(color.argb & 0xFFFFFF).withBold(true));
				p.connection.send(new ClientboundSetTitleTextPacket(big));
				String sub = body.length() > 90 ? body.substring(0, 87) + "…" : body;
				p.connection.send(new ClientboundSetSubtitleTextPacket(Component.literal(sub.replace('\n', ' '))));
			} else if (launch.mini() && color != RouletteColor.YELLOW) {
				p.sendOverlayMessage(Component.literal(launch.title().isBlank() ? body : launch.title() + " — " + body)
					.withStyle(style -> style.withColor(color.argb & 0xFFFFFF)));
			}
		}

		if (color == RouletteColor.YELLOW && change != null && change != DifficultyChange.TEXT) {
			DifficultyChanges.activate(server, change);
		}
		if (color != RouletteColor.YELLOW) {
			for (ServerPlayer p : targets) {
				punish(p, launch.punishment());
			}
			if (launch.timerSeconds() > 0) {
				List<java.util.UUID> ids = new ArrayList<>();
				for (ServerPlayer p : targets) {
					ids.add(p.getUUID());
				}
				TIMERS.add(new Timer(server.getTickCount() + launch.timerSeconds() * 20L, launch.title().isBlank() ? color.label : launch.title(), ids));
			}
		}
	}

	private static List<ServerPlayer> targets(MinecraftServer server, String target) {
		if (target == null || target.isBlank() || target.equals("*")) {
			return new ArrayList<>(server.getPlayerList().getPlayers());
		}
		ServerPlayer one = server.getPlayerList().getPlayerByName(target);
		List<ServerPlayer> list = new ArrayList<>();
		if (one != null) {
			list.add(one);
		}
		return list;
	}

	private static void punish(ServerPlayer p, String punishment) {
		if (!p.isAlive() || p.isSpectator()) {
			return;
		}
		ServerLevel level = p.level();
		switch (punishment) {
			case "lightning" -> {
				LightningBolt bolt = EntityTypes.LIGHTNING_BOLT.create(level, EntitySpawnReason.EVENT);
				if (bolt != null) {
					bolt.setVisualOnly(true);
					bolt.setPos(p.getX(), p.getY(), p.getZ());
					level.addFreshEntity(bolt);
				}
				p.hurtServer(level, level.damageSources().lightningBolt(), 6.0F);
			}
			case "hurt" -> p.hurtServer(level, level.damageSources().magic(), 10.0F);
			case "blind" -> {
				p.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 300, 0));
				p.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 300, 0));
			}
			case "kill" -> p.kill(level);
			default -> {
			}
		}
	}

	private static void tickTimers(MinecraftServer server) {
		if (TIMERS.isEmpty()) {
			return;
		}
		long now = server.getTickCount();
		for (Timer t : List.copyOf(TIMERS)) {
			long left = t.endTick() - now;
			boolean done = left <= 0;
			if (!done && left % 20 != 0) {
				continue;
			}
			long seconds = Math.max(0, left / 20);
			String clock = seconds >= 3600
				? String.format(Locale.ROOT, "%d:%02d:%02d", seconds / 3600, seconds % 3600 / 60, seconds % 60)
				: String.format(Locale.ROOT, "%d:%02d", seconds / 60, seconds % 60);
			for (java.util.UUID id : t.targets()) {
				ServerPlayer p = server.getPlayerList().getPlayer(id);
				if (p != null) {
					if (done) {
						p.sendSystemMessage(Component.literal("⏱ ¡Se acabó el tiempo: " + t.label() + "!").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
					} else {
						p.sendOverlayMessage(Component.literal("⏱ " + t.label() + " — " + clock).withStyle(ChatFormatting.GOLD));
					}
				}
			}
			if (done) {
				TIMERS.remove(t);
			}
		}
	}

	// ------------------------------------------------------------------ red

	public static RoulettePayload snapshot() {
		return state;
	}

	private static void broadcast(MinecraftServer server) {
		server.getPlayerList().getPlayers().forEach(RouletteManager::sync);
	}

	private static void sync(ServerPlayer player) {
		if (ServerPlayNetworking.canSend(player, RoulettePayload.TYPE)) {
			ServerPlayNetworking.send(player, snapshot());
		}
	}

	private static int fail(CommandSourceStack source, String text) {
		source.sendFailure(Component.literal(text));
		return 0;
	}

	private static int reply(CommandSourceStack source, String text) {
		if (source.getEntity() instanceof ServerPlayer player) {
			player.sendOverlayMessage(Component.literal(text));
		} else {
			source.sendSuccess(() -> Component.literal(text), false);
		}
		return 1;
	}

	private RouletteManager() {
	}
}
