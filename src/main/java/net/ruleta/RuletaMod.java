package net.ruleta;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.resources.Identifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public final class RuletaMod implements ModInitializer {
	public static final String MOD_ID = "ruleta";
	public static final Logger LOGGER = LoggerFactory.getLogger("Ruleta");

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	@Override
	public void onInitialize() {
		Red.registrar();

		ServerLifecycleEvents.SERVER_STARTED.register(Servidor::iniciar);
		ServerLifecycleEvents.SERVER_STARTED.register(Autoprueba::correr);
		ServerLifecycleEvents.SERVER_STOPPING.register(server -> Servidor.guardarTodo());
		ServerTickEvents.END_SERVER_TICK.register(Servidor::tick);
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> server.execute(() -> Servidor.alEntrar(handler.player)));
		ServerPlayNetworking.registerGlobalReceiver(Red.Accion.TYPE, (payload, context) ->
				context.server().execute(() -> Servidor.recibir(context.player(), payload.tipo(), payload.json())));
	}
}
