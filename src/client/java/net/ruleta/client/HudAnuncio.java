package net.ruleta.client;

import java.util.List;

import com.google.gson.JsonObject;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;

import net.ruleta.client.ui.Tema;

/** Aviso en pantalla cuando se lanza una casilla (tamaño normal o mini, con temporizador opcional). */
public final class HudAnuncio {
	private static JsonObject actual;
	private static long inicio;
	private static long duracionMs;
	private static long finTemporizador;

	private HudAnuncio() {
	}

	public static void mostrar(JsonObject json) {
		actual = json;
		inicio = System.currentTimeMillis();
		int seg = json.has("segundos") ? json.get("segundos").getAsInt() : -1;
		String msg = json.has("mensaje") ? json.get("mensaje").getAsString() : "";

		if (seg > 0) {
			finTemporizador = inicio + seg * 1000L;
			duracionMs = seg * 1000L + 3000;
		} else {
			finTemporizador = 0;
			duracionMs = Math.min(20000, 6000 + msg.length() * 50L);
		}
	}

	public static void detener() {
		actual = null;
	}

	private static String texto(String k) {
		return actual.has(k) ? actual.get(k).getAsString() : "";
	}

	public static void dibujar(GuiGraphicsExtractor g, DeltaTracker delta) {
		if (actual == null) {
			return;
		}

		long t = System.currentTimeMillis() - inicio;

		if (t > duracionMs) {
			actual = null;
			return;
		}

		float a = Math.min(1, Math.min(t / 300f, (duracionMs - t) / 400f));
		Minecraft mc = Minecraft.getInstance();
		Font f = mc.font;
		int gw = mc.getWindow().getGuiScaledWidth();
		int gh = mc.getWindow().getGuiScaledHeight();
		float esc = Math.min(gw / 1280f, gh / 720f);
		int sw = Math.round(gw / esc);
		g.pose().pushMatrix();
		g.pose().scale(esc, esc);

		try {
			dibujarEnLienzo(g, f, sw, a);
		} finally {
			g.pose().popMatrix();
		}
	}

	private static void dibujarEnLienzo(GuiGraphicsExtractor g, Font f, int sw, float a) {
		Casilla c = Casilla.porId(texto("tipo"));
		boolean mini = "mini".equals(texto("tamano"));
		String titulo = texto("titulo");
		String mensaje = texto("mensaje");
		String extra = switch (c) {
			case REVIIL -> texto("momento");
			case NUTRIA -> actual.has("nutria") && actual.get("nutria").getAsBoolean() ? "Momento Nutria" : "";
			default -> "";
		};

		if (mini) {
			int w = 253;
			int x = sw - w - 11;
			int y = 11;
			List<String> lineas = Tema.lineas(f, mensaje, w - 34, 3);
			int h = 30 + lineas.size() * 9 + (finTemporizador > 0 ? 10 : 0);
			Tema.panel(g, x, y, w, h, 0, a);
			g.blit(RenderPipelines.GUI_TEXTURED, c.icono, x + 6, y + 6, 0, 0, 16, 16, 256, 256, 256, 256, Tema.alfa(0xFFFFFFFF, a));
			c.rotulo().dibujar(g, f, x + 28, y + 5, Tema.alfa(0xFFFFFFFF, a), false);
			g.text(f, Tema.negrita(titulo.isEmpty() ? extra : titulo), x + 28, y + 16, Tema.alfa(Tema.BLANCO, a), false);
			int ly = y + 28;

			for (String l : lineas) {
				g.text(f, l, x + 28, ly, Tema.alfa(Tema.TEXTO, a), false);
				ly += 9;
			}

			if (finTemporizador > 0) {
				g.text(f, Tema.negrita(tiempoRestante()), x + 28, ly + 1, Tema.alfa(0xFF000000 | c.rgb, a), false);
			}

			return;
		}

		int w = Math.min(400, sw - 54);
		int x = (sw - w) / 2;
		int y = 19;
		List<String> lineas = Tema.lineas(f, mensaje, w - 24, 6);
		int h = 44 + lineas.size() * 10 + (finTemporizador > 0 ? 26 : 0);
		Tema.panel(g, x, y, w, h, 30, a);
		g.blit(RenderPipelines.GUI_TEXTURED, c.icono, x + 9, y + 6, 0, 0, 18, 18, 256, 256, 256, 256, Tema.alfa(0xFFFFFFFF, a));
		c.rotulo().dibujar(g, f, x + 32, y + 6, Tema.alfa(0xFFFFFFFF, a), true);

		if (!extra.isEmpty()) {
			Tema.textoDerecha(g, f, Component.literal(extra), x + w - 10, y + 6, Tema.alfa(Tema.GRIS, a));
		}

		g.text(f, Tema.negrita(titulo), x + 32, y + 17, Tema.alfa(Tema.BLANCO, a), false);
		Tema.rect(g, x + 27, y + 30, 28, 1, Tema.alfa(0xFF000000 | c.rgb, a));
		int ly = y + 37;

		for (String l : lineas) {
			Tema.textoCentrado(g, f, Component.literal(l), x + w / 2, ly, Tema.alfa(Tema.TEXTO, a), false);
			ly += 10;
		}

		if (finTemporizador > 0) {
			String txt = tiempoRestante();
			g.pose().pushMatrix();
			g.pose().translate(x + w / 2f, ly + 4);
			g.pose().scale(2, 2);
			Component comp = Tema.negrita(txt);
			g.text(f, comp, -f.width(comp) / 2, 0, Tema.alfa(0xFF000000 | c.rgb, a), true);
			g.pose().popMatrix();
		}
	}

	private static String tiempoRestante() {
		long resto = finTemporizador - System.currentTimeMillis();

		if (resto <= 0) {
			return "¡TIEMPO!";
		}

		return PantallaLanzar.formatoTiempo((int) Math.ceil(resto / 1000.0));
	}
}
