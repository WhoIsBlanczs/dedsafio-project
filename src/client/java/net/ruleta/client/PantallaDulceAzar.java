package net.ruleta.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import net.ruleta.client.ui.PantallaBase;
import net.ruleta.client.ui.Tema;

/**
 * "Dulce azar" controla la dimensión DEDsafío del mod original (ir, volver, olas y escena).
 * Esa dimensión no forma parte de este mod, así que la pantalla lo explica en vez de hacer nada.
 */
public class PantallaDulceAzar extends PantallaBase {
	private static final int W = 597;
	private static final int H = 298;

	public PantallaDulceAzar(Screen anterior) {
		super(Component.literal("Dulce azar"), anterior);
	}

	@Override
	protected void dibujar(GuiGraphicsExtractor g, float a) {
		int px = (LIENZO_W - W) / 2;
		int py = (LIENZO_H - H) / 2;
		Tema.panel(g, px, py, W, H, 39, a);
		g.text(this.font, Tema.negrita("Dulce azar"), px + 16, py + 10, Tema.alfa(Tema.BLANCO, a), false);
		g.text(this.font, "Dimensión DEDsafío: ir, volver, olas y escena", px + 16, py + 23, Tema.alfa(Tema.TEXTO, a), false);
		botonCerrar(g, px + W - 22, py + 14, a);

		String[] nombres = {"Ir", "Volver", "Olas", "Escena"};
		int bw = 131;

		for (int i = 0; i < nombres.length; i++) {
			boton(g, "d" + i, px + 19 + i * (bw + 9), py + 61, bw, 40, Tema.negrita(nombres[i]), Estilo.OSCURO,
					"Necesita la dimensión DEDsafío del mod original", null, a);
		}

		int y = py + 125;

		for (String l : Tema.lineas(this.font, "Esta sección maneja la dimensión DEDsafío del mod original. Esa dimensión no viene incluida en esta versión del menú, así que estos botones no hacen nada.", W - 38, 4)) {
			g.text(this.font, l, px + 19, y, Tema.alfa(Tema.GRIS, a), false);
			y += 11;
		}

		boton(g, "volver", px + 508, py + 260, 56, 22, Tema.negrita("Volver"), Estilo.TEXTO, null, this::onClose, a);
	}
}
