package net.ruleta.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;

import net.ruleta.client.ui.PantallaBase;
import net.ruleta.client.ui.Tema;

/** "Elegir jugador — Jugadores conectados": buscador, fila «Todos» y una fila por jugador con su cabeza. */
public class PantallaJugadores extends PantallaBase {
	private static final int W = 480;
	private static final int H = 420;

	private final String elegido;
	private final boolean conTodos;
	private final Consumer<String> alElegir;
	private EditBox buscar;
	private String busqueda = "";
	private float scroll;

	public PantallaJugadores(Screen anterior, String elegido, boolean conTodos, Consumer<String> alElegir) {
		super(Component.literal("Elegir jugador"), anterior);
		this.elegido = elegido == null ? "" : elegido;
		this.conTodos = conTodos;
		this.alElegir = alElegir;
	}

	private int px() {
		return (LIENZO_W - W) / 2;
	}

	private int py() {
		return (LIENZO_H - H) / 2;
	}

	@Override
	protected void iniciar() {
		buscar = new EditBox(this.font, px() + 22, py() + 63, W - 44, 10, Component.literal("Buscar"));
		buscar.setBordered(false);
		buscar.setMaxLength(32);
		buscar.setHint(Component.literal("Buscar jugador...").withColor(0xD4D8E0));
		buscar.setValue(busqueda);
		buscar.setResponder(s -> {
			busqueda = s;
			scroll = 0;
		});
		campoTexto(buscar, px() + 16, py() + 56, W - 32, 22);
	}

	private List<PlayerInfo> jugadores() {
		List<PlayerInfo> r = new ArrayList<>();

		if (this.minecraft.getConnection() != null) {
			String q = busqueda.trim().toLowerCase(Locale.ROOT);

			for (PlayerInfo info : this.minecraft.getConnection().getOnlinePlayers()) {
				if (q.isEmpty() || info.getProfile().name().toLowerCase(Locale.ROOT).contains(q)) {
					r.add(info);
				}
			}
		}

		r.sort((a, b) -> a.getProfile().name().compareToIgnoreCase(b.getProfile().name()));
		return r;
	}

	@Override
	protected void dibujar(GuiGraphicsExtractor g, float a) {
		int px = px();
		int py = py();
		buscar.visible = a > 0.6f;
		Tema.panel(g, px, py, W, H, 42, a);
		Tema.caja(g, px + 14, py + 9, 24, 24, Tema.alfa(0xFF405078, a), Tema.alfa(0xFF5A6C9C, a));
		g.text(this.font, "👤", px + 21, py + 17, Tema.alfa(0xFFFFFFFF, a), false);
		g.text(this.font, Tema.negrita("Elegir jugador"), px + 46, py + 11, Tema.alfa(Tema.BLANCO, a), false);
		g.text(this.font, "Jugadores conectados", px + 46, py + 24, Tema.alfa(Tema.TEXTO, a), false);
		botonCerrar(g, px + W - 22, py + 16, a);

		boolean foco = buscar.isFocused();
		Tema.caja(g, px + 16, py + 56, W - 32, 22, Tema.alfa(foco ? 0xFF1C2742 : Tema.CAMPO, a), Tema.alfa(foco ? 0xFF7F9BE6 : Tema.CAMPO_BORDE, a));

		List<PlayerInfo> lista = jugadores();
		int filas = lista.size() + (conTodos ? 1 : 0);
		int listaY = py + 88;
		int listaH = H - 88 - 52;
		int paso = 42;
		scroll = Math.max(0, Math.min(scroll, Math.max(0, filas * paso - 6 - listaH)));
		g.enableScissor(px + 1, listaY, px + W - 1, listaY + listaH);

		for (int i = 0; i < filas; i++) {
			boolean todos = conTodos && i == 0;
			PlayerInfo info = todos ? null : lista.get(i - (conTodos ? 1 : 0));
			String nombre = todos ? "" : info.getProfile().name();
			int y = listaY + i * paso - Math.round(scroll);

			if (y + 36 < listaY || y > listaY + listaH) {
				continue;
			}

			boolean sel = nombre.equals(elegido);
			boolean dentro = ratonY >= listaY && ratonY < listaY + listaH;
			boolean hv = dentro && encima(px + 16, y, W - 32, 36);
			float h = anim("fila." + nombre, hv ? 1 : 0, 14);
			int fondo = sel ? Tema.SELECCION : Tema.mezclar(Tema.TARJETA, 0xFF222938, h);
			int borde = sel ? 0xFF6C8FE8 : Tema.mezclar(Tema.TARJETA_BORDE, Tema.TARJETA_BORDE_HOVER, h);
			Tema.caja(g, px + 16, y, W - 32, 36, Tema.alfa(fondo, a), Tema.alfa(borde, a));

			if (todos) {
				Tema.caja(g, px + 25, y + 6, 24, 24, Tema.alfa(0xFF6B5636, a), Tema.alfa(0xFF8A7048, a));
				g.text(this.font, "*", px + 35, y + 13, Tema.alfa(0xFFFFD27A, a), false);
				g.text(this.font, Tema.negrita("Todos"), px + 60, y + 14, Tema.alfa(Tema.BLANCO, a), false);
			} else {
				Cabezas.dibujar(g, info, px + 25, y + 6, 24, a);
				g.text(this.font, Tema.negrita(nombre), px + 60, y + 14, Tema.alfa(Tema.BLANCO, a), false);
			}

			if (y + 36 > listaY && y < listaY + listaH) {
				zona(px + 16, Math.max(y, listaY), W - 32, Math.min(y + 36, listaY + listaH) - Math.max(y, listaY), () -> {
					alElegir.accept(nombre);
					onClose();
				});
			}
		}

		g.disableScissor();

		if (lista.isEmpty() && !conTodos) {
			Tema.textoCentrado(g, this.font, Component.literal("No hay jugadores que coincidan."), px + W / 2, listaY + 40, Tema.alfa(Tema.GRIS, a), false);
		}

		boton(g, "volver", px + W - 180, py + H - 44, 164, 30, Tema.negrita("Volver"), Estilo.OSCURO, null, this::onClose, a);
	}

	@Override
	protected boolean desplazar(double mx, double my, double cantidad) {
		scroll -= (float) cantidad * 42;
		return true;
	}
}
