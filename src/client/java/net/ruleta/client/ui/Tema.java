package net.ruleta.client.ui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;


/** Colores y piezas de dibujo comunes, sacados del video del menú original. */
public final class Tema {
	// Paneles
	public static final int FONDO = 0xF20B101A;
	public static final int FONDO_LATERAL = 0xF20A0F18;
	public static final int CABECERA_ARRIBA = 0xFF252E47;
	public static final int CABECERA_ABAJO = 0xFF141A26;
	public static final int BORDE_PANEL = 0xFF1E2433;
	public static final int BRILLO_ARRIBA = 0xFF5E72A9;
	public static final int SEPARADOR = 0xFF262A37;

	// Tarjetas y controles
	public static final int TARJETA = 0xFF1A1F27;
	public static final int TARJETA_BORDE = 0xFF262C3A;
	public static final int TARJETA_BORDE_HOVER = 0xFF4A63A0;
	public static final int CONTROL = 0xFF262C3D;
	public static final int CONTROL_BORDE = 0xFF353C50;
	public static final int PISTA = 0xFF272D3E;
	public static final int CAMPO = 0xFF181F2D;
	public static final int CAMPO_BORDE = 0xFF292C3E;

	// Acentos
	public static final int AZUL = 0xFF5D7DCD;
	public static final int AZUL_CLARO = 0xFF6B93F5;
	public static final int AZUL_BORDE = 0xFF8FA9EE;
	public static final int SELECCION = 0xFF293451;
	public static final int ROJO_FONDO = 0xFF532B3E;
	public static final int ROJO_BORDE = 0xFF8A3A52;
	public static final int ROJO_TEXTO = 0xFFFF6B8A;

	// Texto
	public static final int BLANCO = 0xFFECEFF5;
	public static final int TEXTO = 0xFFC9CFDB;
	public static final int GRIS = 0xFF9AA3B5;
	public static final int GRIS_OSCURO = 0xFF6C7488;

	// Botones de la barra de la ruleta
	public static final int BARRA = 0xE6232A3A;
	public static final int BARRA_BORDE = 0xFF3C4459;

	private Tema() {
	}

	public static int alfa(int color, float a) {
		int al = (int) (((color >>> 24) & 0xFF) * Math.max(0, Math.min(1, a)));
		return (al << 24) | (color & 0xFFFFFF);
	}

	public static int mezclar(int a, int b, float t) {
		t = Math.max(0, Math.min(1, t));
		int aa = (a >>> 24) & 0xFF, ar = (a >> 16) & 0xFF, ag = (a >> 8) & 0xFF, ab = a & 0xFF;
		int ba = (b >>> 24) & 0xFF, br = (b >> 16) & 0xFF, bg = (b >> 8) & 0xFF, bb = b & 0xFF;
		return ((int) (aa + (ba - aa) * t) << 24) | ((int) (ar + (br - ar) * t) << 16) | ((int) (ag + (bg - ag) * t) << 8) | (int) (ab + (bb - ab) * t);
	}

	public static Texto rotulo(String texto, int rgb) {
		return Texto.rotulo(texto, rgb);
	}

	public static Texto titulo(String texto, int rgb) {
		return Texto.titulo(texto, rgb);
	}

	public static void centrado(GuiGraphicsExtractor g, Font f, Texto t, int cx, int y, int color, boolean sombra) {
		t.dibujar(g, f, cx - t.ancho(f) / 2, y, color, sombra);
	}

	public static MutableComponent negrita(String texto) {
		return Component.literal(texto).withStyle(ChatFormatting.BOLD);
	}

	public static void rect(GuiGraphicsExtractor g, int x, int y, int w, int h, int color) {
		if (w > 0 && h > 0) {
			g.fill(x, y, x + w, y + h, color);
		}
	}

	/** Rectángulo con esquinas de 1 px recortadas y borde. */
	public static void caja(GuiGraphicsExtractor g, int x, int y, int w, int h, int fondo, int borde) {
		rect(g, x + 1, y, w - 2, 1, borde);
		rect(g, x + 1, y + h - 1, w - 2, 1, borde);
		rect(g, x, y + 1, 1, h - 2, borde);
		rect(g, x + w - 1, y + 1, 1, h - 2, borde);
		rect(g, x + 1, y + 1, w - 2, h - 2, fondo);
	}

	/** Panel principal: fondo oscuro, cabecera con degradado, brillo azul arriba. */
	public static void panel(GuiGraphicsExtractor g, int x, int y, int w, int h, int alturaCabecera, float a) {
		caja(g, x, y, w, h, alfa(FONDO, a), alfa(BORDE_PANEL, a));
		g.fillGradient(x + 1, y + 1, x + w - 1, y + alturaCabecera, alfa(CABECERA_ARRIBA, a), alfa(CABECERA_ABAJO, a));
		g.fillGradient(x + 1, y, x + w / 2, y + 1, alfa(0x005E72A9, a), alfa(BRILLO_ARRIBA, a));
		g.fillGradient(x + w / 2, y, x + w - 1, y + 1, alfa(BRILLO_ARRIBA, a), alfa(0x005E72A9, a));
		rect(g, x + 8, y + alturaCabecera, w - 16, 1, alfa(SEPARADOR, a));
	}

	public static void texto(GuiGraphicsExtractor g, Font f, Component c, int x, int y, int color, boolean sombra) {
		g.text(f, c, x, y, color, sombra);
	}

	public static void texto(GuiGraphicsExtractor g, Font f, String s, int x, int y, int color) {
		g.text(f, s, x, y, color, false);
	}

	public static void textoCentrado(GuiGraphicsExtractor g, Font f, Component c, int cx, int y, int color, boolean sombra) {
		g.text(f, c, cx - f.width(c) / 2, y, color, sombra);
	}

	public static void textoDerecha(GuiGraphicsExtractor g, Font f, Component c, int xDerecha, int y, int color) {
		g.text(f, c, xDerecha - f.width(c), y, color, false);
	}

	/** Parte el texto en líneas y, si no caben, corta la última con "...". */
	public static List<String> lineas(Font f, String s, int ancho, int maxLineas) {
		List<String> todas = new ArrayList<>();
		StringBuilder linea = new StringBuilder();

		for (String palabra : s.split(" ")) {
			String prueba = linea.isEmpty() ? palabra : linea + " " + palabra;

			if (f.width(prueba) <= ancho || linea.isEmpty()) {
				linea.setLength(0);
				linea.append(prueba);
			} else {
				todas.add(linea.toString());
				linea.setLength(0);
				linea.append(palabra);
			}
		}

		if (!linea.isEmpty()) {
			todas.add(linea.toString());
		}

		if (todas.size() <= maxLineas) {
			return todas;
		}

		List<String> r = new ArrayList<>(todas.subList(0, maxLineas));
		String ultima = r.get(maxLineas - 1);

		while (!ultima.isEmpty() && f.width(ultima + "...") > ancho) {
			int corte = ultima.lastIndexOf(' ');
			ultima = corte > 0 ? ultima.substring(0, corte) : ultima.substring(0, ultima.length() - 1);
		}

		r.set(maxLineas - 1, ultima + "...");
		return r;
	}

	public static List<FormattedCharSequence> secuencias(List<String> lineas) {
		List<FormattedCharSequence> r = new ArrayList<>();

		for (String l : lineas) {
			r.add(Component.literal(l).getVisualOrderText());
		}

		return r;
	}

	/** Tooltip con el aspecto de los de Minecraft (fondo morado oscuro con borde). */
	public static void tooltip(GuiGraphicsExtractor g, Font f, List<FormattedCharSequence> lineas, int mx, int my, int anchoPantalla, int altoPantalla) {
		if (lineas.isEmpty()) {
			return;
		}

		int w = 0;

		for (FormattedCharSequence l : lineas) {
			w = Math.max(w, f.width(l));
		}

		int h = lineas.size() * 10 - 2;
		int x = mx + 12;
		int y = my - 12;

		if (x + w + 4 > anchoPantalla) {
			x = mx - 16 - w;
		}

		if (y + h + 4 > altoPantalla) {
			y = altoPantalla - h - 4;
		}

		y = Math.max(4, y);
		int fondo = 0xF0100010;
		rect(g, x - 3, y - 4, w + 6, 1, fondo);
		rect(g, x - 3, y + h + 3, w + 6, 1, fondo);
		rect(g, x - 3, y - 3, w + 6, h + 6, fondo);
		rect(g, x - 4, y - 3, 1, h + 6, fondo);
		rect(g, x + w + 3, y - 3, 1, h + 6, fondo);
		g.fillGradient(x - 3, y - 2, x - 2, y + h + 2, 0x505000FF, 0x5028007F);
		g.fillGradient(x + w + 2, y - 2, x + w + 3, y + h + 2, 0x505000FF, 0x5028007F);
		rect(g, x - 3, y - 3, w + 6, 1, 0x505000FF);
		rect(g, x - 3, y + h + 2, w + 6, 1, 0x5028007F);

		int ly = y;

		for (FormattedCharSequence l : lineas) {
			g.text(f, l, x, ly, 0xFFFFFFFF, true);
			ly += 10;
		}
	}
}
