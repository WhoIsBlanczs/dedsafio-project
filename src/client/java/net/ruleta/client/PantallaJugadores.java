package net.ruleta.client;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

import net.ruleta.client.ui.PantallaBase;
import net.ruleta.client.ui.Tema;

/** Elegir a quién le sale el anuncio: todos o un jugador conectado. */
public class PantallaJugadores extends PantallaBase {
	private static final int W = 347;
	private static final int H = 293;
	private final PantallaLanzar form;
	private float scroll;

	public PantallaJugadores(PantallaLanzar form) {
		super(Component.literal("¿Para quién?"), form);
		this.form = form;
	}

	@Override
	protected void dibujar(GuiGraphicsExtractor g, float a) {
		int px = (LIENZO_W - W) / 2;
		int py = (LIENZO_H - H) / 2;
		Tema.panel(g, px, py, W, H, 35, a);
		g.text(this.font, Tema.negrita("¿Para quién?"), px + 14, py + 10, Tema.alfa(Tema.BLANCO, a), false);
		g.text(this.font, "Elige a quién le sale el anuncio", px + 14, py + 23, Tema.alfa(Tema.TEXTO, a), false);
		botonCerrar(g, px + W - 18, py + 14, a);

		List<String> filas = new ArrayList<>();
		filas.add("");
		filas.addAll(form.jugadoresConectados());

		int listaY = py + 44;
		int listaH = H - 54;
		int alto = filas.size() * 27;
		scroll = Math.max(0, Math.min(scroll, Math.max(0, alto - listaH)));
		g.enableScissor(px + 1, listaY, px + W - 1, listaY + listaH);

		for (int i = 0; i < filas.size(); i++) {
			String n = filas.get(i);
			int y = listaY + i * 27 - (int) scroll;
			boolean sel = n.equals(form.para);
			boolean hv = encima(px + 10, y, W - 20, 24) && ratonY >= listaY && ratonY < listaY + listaH;
			float h = anim("fila." + i, hv ? 1 : 0, 14);
			int fondo = sel ? Tema.SELECCION : Tema.mezclar(Tema.TARJETA, 0xFF222938, h);
			int borde = sel ? 0xFF5D78BF : Tema.mezclar(Tema.TARJETA_BORDE, Tema.TARJETA_BORDE_HOVER, h);
			Tema.caja(g, px + 10, y, W - 20, 24, Tema.alfa(fondo, a), Tema.alfa(borde, a));
			String texto = n.isEmpty() ? "Todos los jugadores" : n;
			g.text(this.font, sel ? Tema.negrita(texto) : Component.literal(texto), px + 20, y + 8, Tema.alfa(sel ? Tema.BLANCO : Tema.TEXTO, a), false);

			if (y + 24 > listaY && y < listaY + listaH) {
				zona(px + 8, Math.max(y, listaY), W - 16, Math.min(y + 24, listaY + listaH) - Math.max(y, listaY), () -> {
					form.para = n;
					onClose();
				});
			}
		}

		g.disableScissor();

		if (filas.size() == 1) {
			Tema.textoCentrado(g, this.font, Component.literal("No hay más jugadores conectados."), px + W / 2, listaY + 60, Tema.alfa(Tema.GRIS, a), false);
		}
	}

	@Override
	protected boolean desplazar(double mx, double my, double cantidad) {
		scroll -= (float) cantidad * 27;
		return true;
	}
}
