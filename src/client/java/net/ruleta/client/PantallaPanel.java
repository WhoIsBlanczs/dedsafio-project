package net.ruleta.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import net.ruleta.client.ui.PantallaBase;
import net.ruleta.client.ui.Tema;

/** "Panel — Configura aquí lo que vaya añadiendo el mod". */
public class PantallaPanel extends PantallaBase {
	private static final int W = 597;
	private static final int H = 298;

	public PantallaPanel(Screen anterior) {
		super(Component.literal("Panel"), anterior);
	}

	@Override
	protected void dibujar(GuiGraphicsExtractor g, float a) {
		int px = (LIENZO_W - W) / 2;
		int py = (LIENZO_H - H) / 2;
		Tema.panel(g, px, py, W, H, 39, a);
		g.text(this.font, Tema.negrita("Panel"), px + 16, py + 10, Tema.alfa(Tema.BLANCO, a), false);
		g.text(this.font, "Configura aquí lo que vaya añadiendo el mod", px + 16, py + 23, Tema.alfa(Tema.TEXTO, a), false);
		botonCerrar(g, px + W - 22, py + 14, a);

		int bw = 169;
		int bh = 54;
		int by = py + 61;
		boton(g, "casino", px + 19, by, bw, bh, Tema.negrita("Casino"), Estilo.AZUL, "Tragamonedas: símbolos, probabilidades y premios",
				() -> abrir(new PantallaCasino(this)), a);
		boton(g, "dulce", px + 203, by, bw, bh, Tema.negrita("Dulce azar"), Estilo.AZUL, "Dimensión DEDsafío: ir, volver, olas y escena",
				() -> abrir(new PantallaDulceAzar(this)), a);
		boton(g, "pedestal", px + 387, by, bw, bh, Tema.negrita("Pedestal de intercambio"), Estilo.AZUL, "Los comercios del Pedestal de canje de Hombrosafío",
				() -> abrir(new PantallaPedestal(this)), a);

		g.text(this.font, "Más opciones próximamente...", px + 18, py + 268, Tema.alfa(Tema.GRIS, a), false);
		boton(g, "volver", px + 508, py + 260, 56, 22, Tema.negrita("Volver"), Estilo.TEXTO, null, this::onClose, a);
	}
}
