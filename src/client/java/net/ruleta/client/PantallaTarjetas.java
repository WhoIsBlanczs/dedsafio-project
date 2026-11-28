package net.ruleta.client;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import net.ruleta.Datos;
import net.ruleta.client.ui.PantallaBase;
import net.ruleta.client.ui.Rotulo;
import net.ruleta.client.ui.Tema;

/** "Cambios de dificultad" y "Mobs nuevos": tarjetas por páginas, se elige una. */
public class PantallaTarjetas extends PantallaBase {
	private static final int W = 900;
	private static final int H = 520;
	private static final int COL_W = 279;
	private static final int GAP = 10;

	public static final class Estado {
		public String elegido = "";
		public boolean girar = true;
	}

	private final String rotulo;
	private final String subtitulo;
	private final List<List<Datos.Entrada>> paginas;
	private final Estado estado;
	private final Consumer<Datos.Entrada> alCambiar;
	private int pagina;

	public PantallaTarjetas(Screen anterior, String titulo, String rotulo, List<List<Datos.Entrada>> paginas, Estado estado, Consumer<Datos.Entrada> alCambiar) {
		super(Component.literal(titulo), anterior);
		this.rotulo = rotulo;
		this.subtitulo = "Elige uno · los marcados como ACTIVO ya están en marcha";
		this.paginas = paginas;
		this.estado = estado;
		this.alCambiar = alCambiar;

		for (int i = 0; i < paginas.size(); i++) {
			for (Datos.Entrada e : paginas.get(i)) {
				if (e.id().equals(estado.elegido)) {
					pagina = i;
				}
			}
		}
	}

	private static boolean activo(Datos.Entrada e) {
		return ClienteEstado.CAMBIOS.contains(e.id());
	}

	@Override
	protected void dibujar(GuiGraphicsExtractor g, float a) {
		int px = (LIENZO_W - W) / 2;
		int py = (LIENZO_H - H) / 2;
		Tema.panel(g, px, py, W, H, 30, a);
		g.text(this.font, Tema.negrita(this.title.getString()), px + 13, py + 5, Tema.alfa(Tema.BLANCO, a), false);
		g.text(this.font, subtitulo, px + 13, py + 17, Tema.alfa(Tema.TEXTO, a), false);
		botonCerrar(g, px + W - 20, py + 9, a);

		Rotulo.centrado(g, this.font, rotulo, px + W / 2f, py + 41, 3.2f, 0xFFD45A, 0xFF8A1E, 0x5A2A08, a);

		List<Datos.Entrada> lista = paginas.isEmpty() ? List.of() : paginas.get(pagina);
		int y = py + 87;

		for (int fila = 0; fila * 3 < lista.size(); fila++) {
			int n = Math.min(3, lista.size() - fila * 3);
			List<List<String>> titulos = new ArrayList<>();
			List<List<String>> textos = new ArrayList<>();
			int alto = 0;

			for (int c = 0; c < n; c++) {
				Datos.Entrada e = lista.get(fila * 3 + c);
				List<String> t = Tema.lineas(this.font, e.titulo(), COL_W - 26, 2);
				List<String> d = Tema.lineas(this.font, e.texto(), COL_W - 26, 9);
				titulos.add(t);
				textos.add(d);
				alto = Math.max(alto, 30 + t.size() * 11 + d.size() * 10 + 10);
			}

			alto = Math.max(alto, 95);

			for (int c = 0; c < n; c++) {
				tarjeta(g, lista.get(fila * 3 + c), titulos.get(c), textos.get(c), px + 13 + c * (COL_W + GAP), y, alto, a);
			}

			y += alto + 11;
		}

		// Pie.
		g.text(this.font, "Gira la ruleta y luego se activa", px + 13, py + 474, Tema.alfa(Tema.GRIS, a), false);
		boton(g, "ruleta", px + 13, py + 485, 149, 29, Tema.titulo("RULETA: " + (estado.girar ? "SÍ" : "NO"), 0xFFFFFF), Estilo.OSCURO,
				"SÍ: primero gira la ruleta y al parar sale el aviso. NO: el aviso sale enseguida.", () -> estado.girar = !estado.girar, a);

		boolean hayAntes = pagina > 0;
		boolean hayDespues = pagina < paginas.size() - 1;
		boton(g, "antes", px + 357, py + 485, 47, 29, Component.literal("‹"), Estilo.OSCURO, null, hayAntes ? () -> pagina-- : null, hayAntes ? a : a * 0.45f);
		Tema.textoCentrado(g, this.font, Component.literal("Página " + (pagina + 1) + " de " + Math.max(1, paginas.size())), px + W / 2, py + 496, Tema.alfa(Tema.BLANCO, a), false);
		boton(g, "despues", px + 496, py + 485, 47, 29, Component.literal("›"), Estilo.OSCURO, null, hayDespues ? () -> pagina++ : null, hayDespues ? a : a * 0.45f);
		boton(g, "listo", px + 779, py + 485, 108, 29, Tema.negrita("Listo"), Estilo.AZUL, null, this::onClose, a);
	}

	private void tarjeta(GuiGraphicsExtractor g, Datos.Entrada e, List<String> titulo, List<String> texto, int x, int y, int alto, float a) {
		boolean sel = e.id().equals(estado.elegido);
		boolean act = activo(e);
		boolean hv = encima(x, y, COL_W, alto);
		float h = anim("t." + e.id(), hv ? 1 : 0, 14);
		float s = anim("s." + e.id(), sel ? 1 : 0, 12);
		int fondo = Tema.mezclar(Tema.mezclar(0xFF141A26, 0xFF1A2232, h), 0xFF1E2A48, s);
		int borde = Tema.mezclar(Tema.mezclar(0xFF2A3246, 0xFF4A63A0, h), 0xFF7F9BE6, s);
		Tema.caja(g, x, y - Math.round(h), COL_W, alto, Tema.alfa(fondo, a), Tema.alfa(borde, a));
		y -= Math.round(h);
		int ty = y + 12;

		if (sel || act) {
			int c = sel ? 0xFF7F9BE6 : 0xFF3DDC84;
			Tema.rect(g, x + 14, ty + 2, 5, 5, Tema.alfa(c, a));
			g.text(this.font, Tema.negrita(sel ? "ELEGIDO" : "ACTIVO"), x + 24, ty, Tema.alfa(c, a), false);
		}

		ty += 11;

		for (String l : titulo) {
			g.text(this.font, Tema.negrita(l), x + 13, ty, Tema.alfa(0xFFFFB43C, a), false);
			ty += 11;
		}

		ty += 4;

		for (String l : texto) {
			g.text(this.font, l, x + 13, ty, Tema.alfa(0xFFE2E6EE, a), false);
			ty += 10;
		}

		zona(x, y, COL_W, alto, () -> {
			estado.elegido = sel ? "" : e.id();
			alCambiar.accept(sel ? null : e);
		});
	}
}
