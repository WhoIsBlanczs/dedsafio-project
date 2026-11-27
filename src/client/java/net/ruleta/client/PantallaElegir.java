package net.ruleta.client;

import java.util.List;
import java.util.Set;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

import net.ruleta.client.ui.PantallaBase;
import net.ruleta.client.ui.Tema;

/** Lista con casillas para marcar varias opciones (mobs nuevos o cambios de dificultad). */
public class PantallaElegir extends PantallaBase {
	private static final int W = 400;
	private static final int H = 320;
	private final String subtitulo;
	private final List<String> opciones;
	private final Set<String> elegidas;
	private float scroll;

	public PantallaElegir(PantallaLanzar form, String titulo, String subtitulo, List<String> opciones, Set<String> elegidas) {
		super(Component.literal(titulo), form);
		this.subtitulo = subtitulo;
		this.opciones = opciones;
		this.elegidas = elegidas;
	}

	@Override
	protected void dibujar(GuiGraphicsExtractor g, float a) {
		int px = (LIENZO_W - W) / 2;
		int py = (LIENZO_H - H) / 2;
		Tema.panel(g, px, py, W, H, 35, a);
		g.text(this.font, Tema.negrita(this.title.getString()), px + 14, py + 10, Tema.alfa(Tema.BLANCO, a), false);
		g.text(this.font, subtitulo, px + 14, py + 23, Tema.alfa(Tema.TEXTO, a), false);
		botonCerrar(g, px + W - 18, py + 14, a);

		int listaY = py + 44;
		int listaH = H - 84;
		int alto = opciones.size() * 27;
		scroll = Math.max(0, Math.min(scroll, Math.max(0, alto - listaH)));
		g.enableScissor(px + 1, listaY, px + W - 1, listaY + listaH);

		for (int i = 0; i < opciones.size(); i++) {
			String n = opciones.get(i);
			int y = listaY + i * 27 - (int) scroll;
			boolean sel = elegidas.contains(n);
			boolean hv = encima(px + 10, y, W - 20, 24) && ratonY >= listaY && ratonY < listaY + listaH;
			float h = anim("fila." + i, hv ? 1 : 0, 14);
			Tema.caja(g, px + 10, y, W - 20, 24, Tema.alfa(Tema.mezclar(Tema.TARJETA, 0xFF222938, h), a),
					Tema.alfa(Tema.mezclar(Tema.TARJETA_BORDE, Tema.TARJETA_BORDE_HOVER, h), a));
			Tema.caja(g, px + 18, y + 6, 12, 12, Tema.alfa(sel ? Tema.AZUL : Tema.CONTROL, a), Tema.alfa(sel ? Tema.AZUL_BORDE : Tema.CONTROL_BORDE, a));

			if (sel) {
				g.text(this.font, "✔", px + 20, y + 8, Tema.alfa(0xFFFFFFFF, a), false);
			}

			g.text(this.font, n, px + 38, y + 8, Tema.alfa(sel ? Tema.BLANCO : Tema.TEXTO, a), false);

			if (y + 24 > listaY && y < listaY + listaH) {
				zona(px + 8, Math.max(y, listaY), W - 16, Math.min(y + 24, listaY + listaH) - Math.max(y, listaY), () -> {
					if (!elegidas.remove(n)) {
						elegidas.add(n);
					}
				});
			}
		}

		g.disableScissor();

		g.text(this.font, elegidas.size() + " elegidos", px + 14, py + H - 26, Tema.alfa(Tema.GRIS, a), false);
		boton(g, "listo", px + W - 120, py + H - 34, 106, 23, Tema.negrita("Listo"), Estilo.AZUL, null, this::onClose, a);
	}

	@Override
	protected boolean desplazar(double mx, double my, double cantidad) {
		scroll -= (float) cantidad * 27;
		return true;
	}
}
