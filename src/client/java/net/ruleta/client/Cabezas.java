package net.ruleta.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PlayerFaceExtractor;
import net.minecraft.client.multiplayer.PlayerInfo;

import net.ruleta.client.ui.Tema;

/** Dibuja la cara de la skin de un jugador conectado. */
public final class Cabezas {
	private Cabezas() {
	}

	public static void dibujar(GuiGraphicsExtractor g, PlayerInfo info, int x, int y, int lado, float a) {
		Tema.rect(g, x - 1, y - 1, lado + 2, lado + 2, Tema.alfa(0xFF6C8FE8, a));
		PlayerFaceExtractor.extractRenderState(g, info.getSkin(), x, y, lado, Tema.alfa(0xFFFFFFFF, a));
	}
}
