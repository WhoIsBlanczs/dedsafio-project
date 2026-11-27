package net.ruleta.client.ui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

/** Títulos grandes de píxeles («MOMENTOS REVIIL», «CAMBIOS DE DIFICULTAD»...) con sombra y dos tonos. */
public final class Rotulo {
	private Rotulo() {
	}

	public static int ancho(Font font, String texto, float escala) {
		return Math.round(Texto.titulo(texto, 0).ancho(font) * escala);
	}

	/** Dibuja el rótulo con la esquina superior izquierda en (x, y). */
	public static void dibujar(GuiGraphicsExtractor g, Font font, String texto, float x, float y, float escala, int arriba, int abajo, int sombra, float a) {
		Texto t = Texto.titulo(texto, arriba);
		int w = t.ancho(font) + 2;

		g.pose().pushMatrix();
		g.pose().translate(x, y);
		g.pose().scale(escala, escala);
		Texto.titulo(texto, sombra).dibujar(g, font, 1, 1, Tema.alfa(0xFF000000 | sombra, a), false);
		Texto.titulo(texto, abajo).dibujar(g, font, 0, 0, Tema.alfa(0xFF000000 | abajo, a), false);
		g.pose().popMatrix();

		// Mitad de arriba en el tono claro.
		int sx = (int) Math.floor(x);
		int sy = (int) Math.floor(y);
		g.enableScissor(sx, sy, sx + Math.round(w * escala) + 2, sy + Math.round(4 * escala));
		g.pose().pushMatrix();
		g.pose().translate(x, y);
		g.pose().scale(escala, escala);
		t.dibujar(g, font, 0, 0, Tema.alfa(0xFF000000 | arriba, a), false);
		g.pose().popMatrix();
		g.disableScissor();
	}

	public static void centrado(GuiGraphicsExtractor g, Font font, String texto, float cx, float y, float escala, int arriba, int abajo, int sombra, float a) {
		dibujar(g, font, texto, cx - ancho(font, texto, escala) / 2f, y, escala, arriba, abajo, sombra, a);
	}
}
