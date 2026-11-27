package net.ruleta.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import net.ruleta.client.ui.PantallaBase;
import net.ruleta.client.ui.Tema;

/** "Cambios activos — Elige uno y pulsa Desactivar para quitarlo". */
public class PantallaCambiosActivos extends PantallaBase {
	private static final int W = 899;
	private static final int H = 539;
	private String elegido;
	private float scroll;

	public PantallaCambiosActivos(Screen anterior) {
		super(Component.literal("Cambios activos"), anterior);
	}

	@Override
	protected void dibujar(GuiGraphicsExtractor g, float a) {
		int w = W;
		int h = H;
		int px = (LIENZO_W - w) / 2;
		int py = (LIENZO_H - h) / 2;
		Tema.panel(g, px, py, w, h, 40, a);
		g.text(this.font, Tema.negrita("Cambios activos"), px + 17, py + 11, Tema.alfa(Tema.BLANCO, a), false);
		g.text(this.font, "Elige uno y pulsa Desactivar para quitarlo", px + 17, py + 24, Tema.alfa(Tema.TEXTO, a), false);
		botonCerrar(g, px + w - 22, py + 15, a);

		List<String> cambios = new ArrayList<>(ClienteEstado.CAMBIOS);

		if (elegido != null && !cambios.contains(elegido)) {
			elegido = null;
		}

		if (cambios.isEmpty()) {
			Tema.textoCentrado(g, this.font, Component.literal("No hay cambios de dificultad activos."), px + w / 2, py + h / 2 - 4, Tema.alfa(Tema.TEXTO, a), false);
			return;
		}

		int listaY = py + 50;
		int listaH = h - 50 - 46;
		int alto = cambios.size() * 30;
		scroll = Math.max(0, Math.min(scroll, Math.max(0, alto - listaH)));
		g.enableScissor(px + 1, listaY, px + w - 1, listaY + listaH);

		for (int i = 0; i < cambios.size(); i++) {
			String n = cambios.get(i);
			int y = listaY + i * 30 - (int) scroll;
			boolean sel = n.equals(elegido);
			boolean hv = encima(px + 17, y, w - 34, 26) && ratonY >= listaY && ratonY < listaY + listaH;
			float hh = anim("c." + n, hv ? 1 : 0, 14);
			int fondo = sel ? Tema.SELECCION : Tema.mezclar(Tema.TARJETA, 0xFF222938, hh);
			int borde = sel ? 0xFF5D78BF : Tema.mezclar(Tema.TARJETA_BORDE, Tema.TARJETA_BORDE_HOVER, hh);
			Tema.caja(g, px + 17, y, w - 34, 26, Tema.alfa(fondo, a), Tema.alfa(borde, a));
			Tema.rect(g, px + 27, y + 11, 4, 4, Tema.alfa(0xFFEEA632, a));
			g.text(this.font, sel ? Tema.negrita(n) : Component.literal(n), px + 38, y + 9, Tema.alfa(sel ? Tema.BLANCO : Tema.TEXTO, a), false);

			if (y + 26 > listaY && y < listaY + listaH) {
				zona(px + 17, Math.max(y, listaY), w - 34, Math.min(y + 26, listaY + listaH) - Math.max(y, listaY), () -> elegido = n);
			}
		}

		g.disableScissor();

		g.text(this.font, cambios.size() + (cambios.size() == 1 ? " cambio activo" : " cambios activos"), px + 17, py + h - 28, Tema.alfa(Tema.GRIS, a), false);
		boton(g, "desactivar", px + w - 134, py + h - 36, 117, 24, Tema.negrita("Desactivar"), elegido != null ? Estilo.ROJO : Estilo.OSCURO,
				elegido == null ? "Elige un cambio de la lista" : null, elegido == null ? null : () -> {
					JsonObject o = new JsonObject();
					o.addProperty("nombre", elegido);
					ClienteEstado.accion("desactivar_cambio", o);
					elegido = null;
				}, a);
	}

	@Override
	protected boolean desplazar(double mx, double my, double cantidad) {
		scroll -= (float) cantidad * 30;
		return true;
	}
}
