package net.ruleta.client;

import com.google.gson.JsonObject;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import net.ruleta.client.ui.PantallaBase;
import net.ruleta.client.ui.Tema;

/** "Bunkers": lista de bunkers construidos. Este mod no construye bunkers, así que muestra el estado vacío del original. */
public class PantallaBunkers extends PantallaBase {
	private static final int W = 619;
	private static final int H = 428;

	public PantallaBunkers(Screen anterior) {
		super(Component.literal("Bunkers"), anterior);
	}

	@Override
	protected void dibujar(GuiGraphicsExtractor g, float a) {
		int px = (LIENZO_W - W) / 2;
		int py = (LIENZO_H - H) / 2;
		int n = ClienteEstado.BUNKERS.size();
		Tema.panel(g, px, py, W, H, 40, a);
		Tema.caja(g, px + 13, py + 9, 23, 23, Tema.alfa(0xFF405078, a), Tema.alfa(0xFF5A6C9C, a));
		g.text(this.font, "⌂", px + 22, py + 17, Tema.alfa(0xFFFFFFFF, a), false);
		g.text(this.font, Tema.negrita("Bunkers"), px + 45, py + 11, Tema.alfa(Tema.BLANCO, a), false);
		String sub = n == 0 ? "No hay ningún bunker construido" : n == 1 ? "1 bunker construido" : n + " bunkers construidos";
		g.text(this.font, sub, px + 45, py + 24, Tema.alfa(Tema.TEXTO, a), false);
		botonCerrar(g, px + W - 22, py + 15, a);

		if (n == 0) {
			Tema.textoCentrado(g, this.font, Component.literal("⌂"), px + 301, py + 164, Tema.alfa(Tema.GRIS, a), false);
			Tema.textoCentrado(g, this.font, Tema.negrita("Todavía no hay bunkers"), px + W / 2, py + 194, Tema.alfa(Tema.BLANCO, a), false);
			Tema.textoCentrado(g, this.font, Component.literal("Se crean con /bunker set y /bunker build"), px + W / 2, py + 208, Tema.alfa(Tema.TEXTO, a), false);
		} else {
			int y = py + 50;

			for (JsonObject b : ClienteEstado.BUNKERS) {
				Tema.caja(g, px + 17, y, W - 34, 30, Tema.alfa(Tema.TARJETA, a), Tema.alfa(Tema.TARJETA_BORDE, a));
				String nombre = b.has("nombre") ? b.get("nombre").getAsString() : "Bunker";
				g.text(this.font, Tema.negrita(nombre), px + 27, y + 11, Tema.alfa(Tema.BLANCO, a), false);
				y += 35;
			}
		}

		g.text(this.font, "Demoler quita todos los bloques de la estructura, poco a poco.", px + 17, py + 395, Tema.alfa(Tema.TEXTO, a), false);
		g.text(this.font, "El terreno de alrededor no se toca.", px + 17, py + 406, Tema.alfa(Tema.TEXTO, a), false);
		boton(g, "cerrar", px + 496, py + 388, 109, 27, Tema.negrita("Cerrar"), Estilo.AZUL, null, this::onClose, a);
	}
}
