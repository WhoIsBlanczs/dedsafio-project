package ar.hombrosafio.ruleta.casino;

import ar.hombrosafio.ruleta.RuletaConfig;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;

public final class CasinoRed {
	private CasinoRed() {
	}

	public static void registrar() {
		PayloadTypeRegistry.serverboundPlay().register(CasinoPedirPayload.TYPE, CasinoPedirPayload.CODEC);
		PayloadTypeRegistry.serverboundPlay().register(CasinoGuardarPayload.TYPE, CasinoGuardarPayload.CODEC);
		PayloadTypeRegistry.clientboundPlay().register(CasinoDatosPayload.TYPE, CasinoDatosPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(CasinoPedirPayload.TYPE, (m, ctx) -> ctx.server().execute(() -> enviar(ctx.player())));
		ServerPlayNetworking.registerGlobalReceiver(CasinoGuardarPayload.TYPE, (m, ctx) -> ctx.server().execute(() -> guardar(ctx.player(), m.json())));
	}

	private static void enviar(ServerPlayer jugador) {
		if (RuletaConfig.canEdit(jugador)) {
			ServerPlayNetworking.send(jugador, new CasinoDatosPayload(ConfigTragamonedas.get().aJsonCompacto()));
		}
	}

	private static void guardar(ServerPlayer jugador, String json) {
		if (RuletaConfig.canEdit(jugador)) {
			ConfigTragamonedas c = ConfigTragamonedas.desdeJson(json);
			if (c != null) {
				ConfigTragamonedas.guardar(c);
				ServerPlayNetworking.send(jugador, new CasinoDatosPayload(c.aJsonCompacto()));
			}
		}
	}
}
