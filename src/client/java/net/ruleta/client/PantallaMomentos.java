package net.ruleta.client;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import net.ruleta.MomentosHS;
import net.ruleta.RuletaMod;
import net.ruleta.client.ui.PantallaBase;
import net.ruleta.client.ui.Rotulo;
import net.ruleta.client.ui.Tema;

/** "Momentos Reviil": los Momentos Revil de Hombrosafío; se eligen varios y se activan al salir el mensaje. */
public class PantallaMomentos extends PantallaBase {
	private static final Identifier CARA = RuletaMod.id("textures/gui/reviil_momentos.png");
	private static final int W = 900;
	private static final int H = 520;
	private static final int COL_W = 279;
	private static final int GAP = 10;
	private static final int TOP = 137;

	/** Lo elegido para cada momento: plazo en segundos, muertes (votación) y bioma. */
	public record Eleccion(int segundos, int num, String texto) { }

	private final Map<String, Eleccion> elegidos;
	private final Consumer<Map<String, Eleccion>> alCambiar;
	private float scroll;
	private float scrollVisual;

	public PantallaMomentos(Screen anterior, Map<String, Eleccion> elegidos, Consumer<Map<String, Eleccion>> alCambiar) {
		super(Component.literal("Momentos Reviil"), anterior);
		this.elegidos = elegidos;
		this.alCambiar = alCambiar;
	}

	private String choque(MomentosHS.Momento m) {
		for (String otro : elegidos.keySet()) {
			if (MomentosHS.chocan(otro, m.id())) {
				return MomentosHS.porId(otro).titulo();
			}
		}

		return null;
	}

	void elegir(MomentosHS.Momento m, Eleccion e) {
		elegidos.put(m.id(), e);
		alCambiar.accept(elegidos);
	}

	@Override
	protected void dibujar(GuiGraphicsExtractor g, float a) {
		int px = (LIENZO_W - W) / 2;
		int py = (LIENZO_H - H) / 2;
		Tema.panel(g, px, py, W, H, 38, a);
		Tema.caja(g, px + 13, py + 8, 24, 24, Tema.alfa(0xFF405078, a), Tema.alfa(0xFF5A6C9C, a));
		g.text(this.font, "★", px + 21, py + 16, Tema.alfa(0xFFFFFFFF, a), false);
		g.text(this.font, Tema.negrita("Momentos Reviil"), px + 45, py + 9, Tema.alfa(Tema.BLANCO, a), false);
		g.text(this.font, "Elige los que quieras: se activan cuando sale el mensaje de la ruleta roja", px + 45, py + 22, Tema.alfa(Tema.TEXTO, a), false);
		botonCerrar(g, px + W - 22, py + 15, a);

		// Encabezado: la cara de Reviil y el rótulo rojo.
		g.blit(RenderPipelines.GUI_TEXTURED, CARA, px + 35, py + 54, 0, 0, 56, 61, 96, 104, 96, 104, Tema.alfa(0xFFFFFFFF, a));
		Rotulo.dibujar(g, this.font, "MOMENTOS REVIIL", px + 122, py + 69, 3.6f, 0xFF4A5A, 0xD8102A, 0x4A0010, a);

		if (!ClienteEstado.hombrosafio) {
			g.text(this.font, "Hombrosafío no está instalado en el servidor: estos momentos no se podrán activar.", px + 122, py + 108, Tema.alfa(0xFFE0A04A, a), false);
		}

		// Tarjetas.
		List<MomentosHS.Momento> todos = MomentosHS.TODOS;
		int top = py + TOP;
		int bottom = py + H - 12;
		List<int[]> pos = new ArrayList<>();
		List<List<String>> textos = new ArrayList<>();
		int y = 0;

		for (int fila = 0; fila * 3 < todos.size(); fila++) {
			int n = Math.min(3, todos.size() - fila * 3);
			int alto = 0;

			for (int c = 0; c < n; c++) {
				List<String> t = Tema.lineas(this.font, todos.get(fila * 3 + c).descripcion(), COL_W - 28, 8);
				textos.add(t);
				alto = Math.max(alto, 44 + t.size() * 10 + 10);
			}

			for (int c = 0; c < n; c++) {
				pos.add(new int[] {px + 13 + c * (COL_W + GAP), y, alto});
			}

			y += alto + 11;
		}

		int total = Math.max(0, y - 11);
		float max = Math.max(0, total - (bottom - top));
		scroll = Math.max(0, Math.min(scroll, max));
		scrollVisual += (scroll - scrollVisual) * (1 - (float) Math.exp(-18 * dt));
		g.enableScissor(px + 4, top, px + W - 4, bottom);

		for (int i = 0; i < todos.size(); i++) {
			int[] p = pos.get(i);
			int cy = top + p[1] - Math.round(scrollVisual);

			if (cy + p[2] < top || cy > bottom) {
				continue;
			}

			tarjeta(g, todos.get(i), textos.get(i), p[0], cy, p[2], top, bottom, a);
		}

		g.disableScissor();

		if (max > 0) {
			int vis = bottom - top;
			int barra = Math.max(18, vis * vis / Math.max(1, total));
			int by = top + Math.round((vis - barra) * (scrollVisual / max));
			Tema.rect(g, px + W - 9, top, 2, vis, Tema.alfa(0xFF1C202D, a));
			Tema.rect(g, px + W - 9, by, 2, barra, Tema.alfa(0xFF8993A3, a));
		}
	}

	private void tarjeta(GuiGraphicsExtractor g, MomentosHS.Momento m, List<String> texto, int x, int y, int alto, int top, int bottom, float a) {
		boolean activo = elegidos.containsKey(m.id());
		String choca = activo ? null : choque(m);
		boolean bloq = choca != null;
		boolean dentro = ratonY >= top && ratonY < bottom;
		boolean hv = !bloq && dentro && encima(x, y, COL_W, alto);
		float h = anim("m." + m.id(), hv ? 1 : 0, 14);
		float ac = anim("a." + m.id(), activo ? 1 : 0, 10);
		float op = bloq ? 0.45f : 1f;
		int fondo = Tema.mezclar(Tema.mezclar(0xFF161C28, 0xFF1C2436, h), 0xFF123228, ac);
		int borde = Tema.mezclar(Tema.mezclar(0xFF2A3246, 0xFF6C8FE8, h), 0xFF2FD07A, ac);
		Tema.caja(g, x, y, COL_W, alto, Tema.alfa(fondo, a * (bloq ? 0.7f : 1f)), Tema.alfa(borde, a * op));

		String estado;
		int colorEstado;

		if (activo) {
			Eleccion e = elegidos.get(m.id());
			estado = "ACTIVADO · empieza con el mensaje" + resumen(m, e);
			colorEstado = 0xFF3DDC84;
		} else if (bloq) {
			estado = "NO COMPATIBLE · choca con «" + choca + "»";
			colorEstado = 0xFF8E95A6;
		} else {
			estado = "DESACTIVADO";
			colorEstado = 0xFFFF4D6A;
		}

		int tx = x + 14;
		int ty = y + 12;
		Tema.rect(g, tx, ty + 2, 5, 5, Tema.alfa(colorEstado, a * op));
		String est = estado;

		while (this.font.width(Tema.negrita(est)) > COL_W - 40 && est.length() > 4) {
			est = est.substring(0, est.length() - 2);
		}

		if (!est.equals(estado)) {
			est = est + "…";
		}

		g.text(this.font, Tema.negrita(est), tx + 10, ty, Tema.alfa(colorEstado, a * op), false);
		ty += 15;
		g.text(this.font, Tema.negrita(m.titulo()), tx, ty, Tema.alfa(bloq ? 0xFF8E95A6 : 0xFFF5C242, a * op), false);
		ty += 14;

		for (String l : texto) {
			g.text(this.font, l, tx, ty, Tema.alfa(bloq ? 0xFF8E95A6 : 0xFFE2E6EE, a * op), false);
			ty += 10;
		}

		int zy = Math.max(y, top);
		int zh = Math.min(y + alto, bottom) - zy;

		if (bloq || zh <= 0) {
			return;
		}

		zona(x, zy, COL_W, zh, () -> {
			if (activo) {
				elegidos.remove(m.id());
				alCambiar.accept(elegidos);
			} else if (m.parametro() == MomentosHS.Parametro.NINGUNO) {
				elegir(m, new Eleccion(0, 0, ""));
			} else {
				abrir(new PantallaParametros(this, m));
			}
		});
	}

	static String resumen(MomentosHS.Momento m, Eleccion e) {
		return switch (m.parametro()) {
			case MINUTOS, REFUGIO -> " · " + PantallaLanzar.formatoTiempo(e.segundos());
			case BIOMA -> " · " + e.texto();
			case VOTACION -> " · " + e.num() + (e.num() == 1 ? " muerte" : " muertes");
			default -> "";
		};
	}

	@Override
	protected boolean desplazar(double mx, double my, double cantidad) {
		scroll -= (float) cantidad * 40;
		return true;
	}

	/** Copia ordenada de lo elegido, para guardar en el formulario. */
	static Map<String, Eleccion> copia(Map<String, Eleccion> m) {
		return new LinkedHashMap<>(m);
	}
}
