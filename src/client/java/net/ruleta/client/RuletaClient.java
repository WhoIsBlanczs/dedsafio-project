package net.ruleta.client;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;

import net.ruleta.Red;
import net.ruleta.RuletaMod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;

public final class RuletaClient implements ClientModInitializer {
	public static final KeyMapping.Category CATEGORIA = KeyMapping.Category.register(RuletaMod.id("ruleta"));
	public static KeyMapping TECLA;

	@Override
	public void onInitializeClient() {
		TECLA = KeyMappingHelper.registerKeyMapping(new KeyMapping(
				"key.ruleta.lanzador",
				InputConstants.Type.KEYBOARD,
				InputConstants.KEY_G,
				CATEGORIA));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (TECLA.consumeClick()) {
				// Solo los operadores ven la ruleta; a los demás la G no les hace nada.
				if (client.player != null && ClienteEstado.admin) {
					client.gui.setScreen(new PantallaRuleta());
				}
			}
		});

		ClientPlayNetworking.registerGlobalReceiver(Red.Datos.TYPE, (payload, context) ->
				context.client().execute(() -> ClienteEstado.recibir(payload.tipo(), payload.json())));

		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			if (ClientPlayNetworking.canSend(Red.Accion.TYPE)) {
				ClientPlayNetworking.send(new Red.Accion("pedir", new JsonObject().toString()));
			}
		});
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> client.execute(ClienteEstado::reiniciar));

		HudElementRegistry.addLast(RuletaMod.id("anuncio"), HudAnuncio::dibujar);
	}
}
