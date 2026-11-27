package net.ruleta.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import net.ruleta.MomentosHS;
import net.ruleta.client.ui.PantallaBase;
import net.ruleta.client.ui.Tema;

/** Ventana "Temporizador" que pide el plazo (y el bioma o las muertes) antes de activar un Momento Revil. */
public class PantallaParametros extends PantallaBase {
	private static final int W = 460;

	private final PantallaMomentos padre;
	private final MomentosHS.Momento momento;
	private EditBox tiempo;
	private EditBox extra;
	private String valorTiempo;
	private String valorExtra;

	public PantallaParametros(PantallaMomentos padre, MomentosHS.Momento momento) {
		super(Component.literal("Temporizador"), padre);
		this.padre = padre;
		this.momento = momento;
		this.valorTiempo = PantallaLanzar.formatoTiempo(porDefecto());
		this.valorExtra = switch (momento.parametro()) {
			case BIOMA -> "minecraft:desert";
			case VOTACION -> "1";
			default -> "";
		};
	}

	private boolean conExtra() {
		return momento.parametro() == MomentosHS.Parametro.BIOMA || momento.parametro() == MomentosHS.Parametro.VOTACION;
	}

	private int alto() {
		return conExtra() ? 316 : 250;
	}

	private int px() {
		return (LIENZO_W - W) / 2;
	}

	private int py() {
		return (LIENZO_H - alto()) / 2;
	}

	private int porDefecto() {
		return switch (momento.parametro()) {
			case VOTACION -> 60;
			default -> Math.max(1, momento.minutosPorDefecto()) * 60;
		};
	}

	@Override
	protected void iniciar() {
		int x = px() + 24;
		int y = py() + 76;
		tiempo = new EditBox(this.font, x + 6, y + 7, 140, 10, Component.literal("Tiempo"));
		tiempo.setBordered(false);
		tiempo.setMaxLength(8);
		tiempo.setValue(valorTiempo);
		tiempo.setResponder(s -> valorTiempo = s);
		campoTexto(tiempo, x, y, 150, 24);

		if (conExtra()) {
			int ey = y + 66;
			int ew = momento.parametro() == MomentosHS.Parametro.BIOMA ? W - 48 : 80;
			extra = new EditBox(this.font, x + 6, ey + 7, ew - 12, 10, Component.literal("Extra"));
			extra.setBordered(false);
			extra.setMaxLength(80);
			extra.setValue(valorExtra);
			extra.setResponder(s -> valorExtra = s);
			campoTexto(extra, x, ey, ew, 24);
		}

		setInitialFocus(tiempo);
	}

	private int segundos() {
		String t = valorTiempo.trim();

		if (t.isEmpty()) {
			return porDefecto();
		}

		return PantallaLanzar.leerTiempo(t);
	}

	private int muertes() {
		try {
			return Integer.parseInt(valorExtra.trim());
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	private boolean valido() {
		int s = segundos();

		if (s <= 0) {
			return false;
		}

		return switch (momento.parametro()) {
			case VOTACION -> muertes() >= 1 && muertes() <= 100 && s >= 10;
			case BIOMA -> valorExtra.trim().matches("[a-z0-9_.-]+:[a-z0-9_./-]+") || valorExtra.trim().matches("[a-z0-9_./-]+");
			default -> true;
		};
	}

	private void activar() {
		String bioma = valorExtra.trim();

		if (momento.parametro() == MomentosHS.Parametro.BIOMA && !bioma.contains(":")) {
			bioma = "minecraft:" + bioma;
		}

		padre.elegir(momento, new PantallaMomentos.Eleccion(segundos(), Math.max(1, muertes()), bioma));
		onClose();
	}

	private void campo(GuiGraphicsExtractor g, EditBox box, int x, int y, int w, boolean error, float a) {
		boolean foco = box.isFocused();
		int borde = error ? 0xFFC0485F : foco ? 0xFF7F9BE6 : Tema.CAMPO_BORDE;
		Tema.caja(g, x, y, w, 24, Tema.alfa(foco ? 0xFF1C2742 : Tema.CAMPO, a), Tema.alfa(borde, a));

		if (foco) {
			Tema.rect(g, x + 1, y + 22, w - 2, 1, Tema.alfa(Tema.AZUL_CLARO, a));
		}
	}

	@Override
	protected void dibujar(GuiGraphicsExtractor g, float a) {
		int px = px();
		int py = py();
		int h = alto();
		tiempo.visible = a > 0.6f;

		if (extra != null) {
			extra.visible = a > 0.6f;
		}

		Tema.panel(g, px, py, W, h, 40, a);
		Tema.caja(g, px + 13, py + 9, 24, 24, Tema.alfa(0xFF405078, a), Tema.alfa(0xFF5A6C9C, a));
		g.text(this.font, "⌚", px + 21, py + 17, Tema.alfa(0xFFFFFFFF, a), false);
		g.text(this.font, Tema.negrita("Temporizador"), px + 45, py + 11, Tema.alfa(Tema.BLANCO, a), false);
		g.text(this.font, momento.titulo(), px + 45, py + 24, Tema.alfa(Tema.TEXTO, a), false);
		botonCerrar(g, px + W - 22, py + 16, a);

		int x = px + 24;
		int y = py + 54;
		String titulo;
		String ayuda;

		switch (momento.parametro()) {
			case REFUGIO -> {
				titulo = "Tiempo para llegar al refugio";
				ayuda = "Cuando acaba, el refugio se cierra y quien siga fuera muere.";
			}
			case VOTACION -> {
				titulo = "Tiempo para votar";
				ayuda = "Cuando acaba, mueren los más votados.";
			}
			case BIOMA -> {
				titulo = "Tiempo para llegar al bioma";
				ayuda = "Cuando acaba, estar fuera del bioma causa daño.";
			}
			default -> {
				titulo = "Tiempo para llegar";
				ayuda = "Cuando acaba, estar fuera causa daño. Se redondea a minutos.";
			}
		}

		g.text(this.font, Tema.negrita(titulo), x, y, Tema.alfa(Tema.BLANCO, a), false);
		g.text(this.font, ayuda, x, y + 11, Tema.alfa(Tema.GRIS, a), false);
		campo(g, tiempo, x, y + 22, 150, segundos() <= 0, a);
		g.text(this.font, "por defecto " + PantallaLanzar.formatoTiempo(porDefecto()), x + 162, y + 30, Tema.alfa(Tema.GRIS, a), false);

		if (momento.parametro() == MomentosHS.Parametro.BIOMA) {
			g.text(this.font, Tema.negrita("¿Qué bioma?"), x, y + 66, Tema.alfa(Tema.BLANCO, a), false);
			g.text(this.font, "Su id, por ejemplo minecraft:desert o minecraft:jungle.", x, y + 77, Tema.alfa(Tema.GRIS, a), false);
			campo(g, extra, x, y + 88, W - 48, !valido() && segundos() > 0, a);
		} else if (momento.parametro() == MomentosHS.Parametro.VOTACION) {
			g.text(this.font, Tema.negrita("¿Cuántos mueren?"), x, y + 66, Tema.alfa(Tema.BLANCO, a), false);
			g.text(this.font, "Tiene que ser menos que los jugadores vivos.", x, y + 77, Tema.alfa(Tema.GRIS, a), false);
			campo(g, extra, x, y + 88, 80, muertes() < 1 || muertes() > 100, a);
		}

		g.text(this.font, "Formato mm:ss o hh:mm:ss. Vacío = el valor por defecto.", x, py + h - 60, Tema.alfa(Tema.GRIS, a), false);
		boolean ok = valido();
		boton(g, "cancelar", px + W - 24 - 130 - 10 - 100, py + h - 44, 100, 30, Tema.negrita("Cancelar"), Estilo.OSCURO, null, this::onClose, a);
		boton(g, "activar", px + W - 24 - 130, py + h - 44, 130, 30, Tema.negrita("Activar"), Estilo.AZUL, ok ? null : "Revisa los datos", ok ? this::activar : null, ok ? a : a * 0.55f);
	}
}
