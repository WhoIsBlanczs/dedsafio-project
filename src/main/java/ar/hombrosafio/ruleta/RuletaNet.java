package ar.hombrosafio.ruleta;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

/** Registro de paquetes y receptores del servidor. */
public final class RuletaNet {
	public static void register() {
		PayloadTypeRegistry.clientboundPlay().register(RoulettePayload.TYPE, RoulettePayload.CODEC);
		PayloadTypeRegistry.clientboundPlay().register(OpenScreenPayload.TYPE, OpenScreenPayload.CODEC);
		PayloadTypeRegistry.clientboundPlay().register(ActiveChangesPayload.TYPE, ActiveChangesPayload.CODEC);
		PayloadTypeRegistry.clientboundPlay().register(SettingsSyncPayload.TYPE, SettingsSyncPayload.CODEC);
		PayloadTypeRegistry.serverboundPlay().register(LaunchPayload.TYPE, LaunchPayload.CODEC);
		PayloadTypeRegistry.serverboundPlay().register(ActionPayload.TYPE, ActionPayload.CODEC);
		PayloadTypeRegistry.serverboundPlay().register(SettingChangePayload.TYPE, SettingChangePayload.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(LaunchPayload.TYPE, (payload, context) -> context.server().execute(() -> {
			String error = RouletteManager.launch(context.player(), payload);
			if (error != null) {
				context.player().sendOverlayMessage(Component.literal(error));
			}
		}));

		ServerPlayNetworking.registerGlobalReceiver(ActionPayload.TYPE, (payload, context) -> context.server().execute(() -> {
			ServerPlayer player = context.player();
			MinecraftServer server = context.server();
			if (!RuletaConfig.canEdit(player)) {
				return;
			}
			switch (payload.action()) {
				case "stop" -> {
					if (RouletteManager.active()) {
						RouletteManager.cancel(server);
						player.sendOverlayMessage(Component.literal("Ruleta cancelada."));
					} else {
						player.sendOverlayMessage(Component.literal("No hay ninguna ruleta activa."));
					}
				}
				case "change_off" -> {
					DifficultyChange change = DifficultyChange.byId(payload.arg());
					if (change != null && DifficultyChanges.deactivate(server, change)) {
						server.getPlayerList().broadcastSystemMessage(
							Component.literal("✦ Se quitó un cambio de la ruleta amarilla: " + change.announcement + ".")
								.withStyle(net.minecraft.ChatFormatting.YELLOW), false);
					}
				}
				default -> {
				}
			}
		}));

		ServerPlayNetworking.registerGlobalReceiver(SettingChangePayload.TYPE, (payload, context) -> context.server().execute(() -> {
			if (!RuletaConfig.canEdit(context.player())) {
				return;
			}
			if (payload.id().equals("*") || payload.id().startsWith("cat:")) {
				RuletaConfig.reset(context.server(), payload.id());
			} else {
				RuletaConfig.set(context.server(), payload.id(), payload.value());
			}
		}));
	}

	private RuletaNet() {
	}
}
