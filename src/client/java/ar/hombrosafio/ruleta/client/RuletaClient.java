package ar.hombrosafio.ruleta.client;

import ar.hombrosafio.ruleta.ActiveChangesPayload;
import ar.hombrosafio.ruleta.OpenScreenPayload;
import ar.hombrosafio.ruleta.RuletaConfig;
import ar.hombrosafio.ruleta.SettingsSyncPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public final class RuletaClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		RouletteHud.register();

		ClientPlayNetworking.registerGlobalReceiver(SettingsSyncPayload.TYPE, (payload, context) -> context.client().execute(() -> {
			ClientState.canEdit = payload.canEdit();
			RuletaConfig.replace(payload.values());
		}));
		ClientPlayNetworking.registerGlobalReceiver(ActiveChangesPayload.TYPE, (payload, context) -> context.client().execute(() -> {
			ClientState.activeChanges = new java.util.ArrayList<>(payload.ids());
			if (context.client().screen instanceof ActiveChangesScreen screen) {
				screen.refresh();
			}
		}));
		ClientPlayNetworking.registerGlobalReceiver(OpenScreenPayload.TYPE, (payload, context) -> context.client().execute(() -> {
			Minecraft mc = context.client();
			Screen screen = switch (payload.screen()) {
				case "ajustes" -> new SettingsScreen(null);
				case "cambios" -> new ActiveChangesScreen(null);
				case "panel" -> new PanelScreen(null);
				default -> new PickerScreen();
			};
			mc.gui.setScreen(screen);
		}));
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ClientState.reset());
	}
}
