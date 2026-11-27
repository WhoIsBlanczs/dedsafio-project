package net.ruleta.client.ui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import net.ruleta.RuletaMod;

/**
 * Texto que se puede dibujar: o un Component normal, o texto con las fuentes de
 * píxeles del mod original ("rotulo" y "titulo"), dibujadas letra a letra desde
 * su textura para no depender de la API de fuentes de Minecraft.
 */
public interface Texto {
	int ancho(Font font);

	void dibujar(GuiGraphicsExtractor g, Font font, int x, int y, int color, boolean sombra);

	static Texto de(Component c) {
		return new Normal(c);
	}

	static Texto rotulo(String s, int rgb) {
		return new Pixel(s, Fuente.ROTULO, rgb);
	}

	static Texto titulo(String s, int rgb) {
		return new Pixel(s, Fuente.TITULO, rgb);
	}

	record Normal(Component c) implements Texto {
		@Override
		public int ancho(Font font) {
			return font.width(c);
		}

		@Override
		public void dibujar(GuiGraphicsExtractor g, Font font, int x, int y, int color, boolean sombra) {
			g.text(font, c, x, y, color, sombra);
		}
	}

	enum Fuente {
		ROTULO("rotulo", new int[] {5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 4, 5, 5, 5, 5, 5, 5, 5, 5, 3, 3, 3, 3, 5, 5, 4, 3, 4, 4, 3, 5, 3, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5}),
		TITULO("titulo", new int[] {5, 5, 5, 5, 5, 5, 5, 5, 3, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 3, 5, 5, 5, 5, 3, 5, 5, 5, 5, 5, 5, 5, 5, 1, 1, 1, 1, 4, 4, 4, 1, 3, 2, 2, 5, 1, 5, 5, 5, 5, 5, 5, 5, 5, 3, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 3, 5, 5, 5});

		static final String CARACTERES = "ABCDEFGHIJKLMNOP" + "QRSTUVWXYZÑÁÉÍÓÚ" + "Ü0123456789:.!¡?" + "¿-'\"()/,abcdefgh" + "ijklmnopqrstuvwx" + "yzñáéíóúü";

		final Identifier textura;
		final int[] anchos;

		Fuente(String nombre, int[] anchos) {
			this.textura = RuletaMod.id("textures/font/" + nombre + ".png");
			this.anchos = anchos;
		}
	}

	record Pixel(String s, Fuente fuente, int rgb) implements Texto {
		@Override
		public int ancho(Font font) {
			int w = 0;

			for (int i = 0; i < s.length(); i++) {
				w += avance(font, s.charAt(i));
			}

			return Math.max(0, w - 1);
		}

		private int avance(Font font, char ch) {
			if (ch == ' ') {
				return 4;
			}

			int idx = Fuente.CARACTERES.indexOf(ch);
			return idx >= 0 ? fuente.anchos[idx] + 1 : font.width(String.valueOf(ch));
		}

		@Override
		public void dibujar(GuiGraphicsExtractor g, Font font, int x, int y, int color, boolean sombra) {
			int alfa = color & 0xFF000000;
			int c = alfa | (rgb & 0xFFFFFF);

			if (sombra) {
				int r = ((rgb >> 16) & 0xFF) / 4;
				int gg = ((rgb >> 8) & 0xFF) / 4;
				int b = (rgb & 0xFF) / 4;
				pintar(g, font, x + 1, y + 1, alfa | (r << 16) | (gg << 8) | b);
			}

			pintar(g, font, x, y, c);
		}

		private void pintar(GuiGraphicsExtractor g, Font font, int x, int y, int color) {
			int cx = x;

			for (int i = 0; i < s.length(); i++) {
				char ch = s.charAt(i);
				int idx = Fuente.CARACTERES.indexOf(ch);

				if (ch != ' ' && idx >= 0) {
					int u = (idx % 16) * 8;
					int v = (idx / 16) * 8;
					g.blit(RenderPipelines.GUI_TEXTURED, fuente.textura, cx, y, u, v, 8, 8, 128, 48, color);
				} else if (ch != ' ') {
					g.text(font, String.valueOf(ch), cx, y, color, false);
				}

				cx += avance(font, ch);
			}
		}
	}
}
