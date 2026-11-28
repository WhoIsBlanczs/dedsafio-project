package net.ruleta.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import net.fabricmc.loader.api.FabricLoader;

import net.ruleta.CasinoDatos;
import net.ruleta.client.ui.PantallaBase;
import net.ruleta.client.ui.Tema;

/** "Casino": los premios de la tragamonedas de CasinoHS. Muestra los que tiene y deja cambiarlos. */
public class PantallaCasino extends PantallaBase {
	private static final int W = 778;
	private static final int H = 468;
	private static final int FILA = 36;
	private static final String[][] ENCANTAMIENTOS = {
			{"lunge", "Embestida", "3"}, {"sharpness", "Filo", "5"}, {"power", "Poder", "5"}, {"efficiency", "Eficiencia", "5"},
			{"vanishing_curse", "Maldición de desaparición", "1"}, {"protection", "Protección", "4"}, {"unbreaking", "Irrompibilidad", "3"},
			{"mending", "Reparación", "1"}, {"fortune", "Fortuna", "3"}, {"looting", "Botín", "3"}, {"silk_touch", "Toque de seda", "1"},
			{"feather_falling", "Caída de pluma", "4"}, {"infinity", "Infinidad", "1"}};

	private final List<CasinoDatos.Premio> premios = new ArrayList<>();
	private String ficha;
	private boolean shift;
	private float scroll;

	public PantallaCasino(Screen anterior) {
		super(Component.literal("Casino"), anterior);
		cargar(ClienteEstado.casino);
	}

	private void cargar(com.google.gson.JsonObject json) {
		premios.clear();
		premios.addAll(CasinoDatos.leer(json));
		ficha = CasinoDatos.ficha(json);
	}

	private double pesoTotal() {
		double t = 0;

		for (CasinoDatos.Premio p : premios) {
			if (!Objetos.stack(p.item()).isEmpty()) {
				t += p.peso();
			}
		}

		return t;
	}

	private double paso() {
		return shift ? 10 : 1;
	}

	private void cambiar(int i, CasinoDatos.Premio nuevo) {
		if (i >= 0 && i < premios.size()) {
			premios.set(i, nuevo);
		}
	}

	private static String nombreEncantamiento(String id) {
		for (String[] e : ENCANTAMIENTOS) {
			if (e[0].equals(id)) {
				return e[1];
			}
		}

		return id;
	}

	private static int maxNivel(String id) {
		for (String[] e : ENCANTAMIENTOS) {
			if (e[0].equals(id)) {
				return Integer.parseInt(e[2]);
			}
		}

		return 5;
	}

	private static String romano(int n) {
		return switch (n) {
			case 1 -> "I";
			case 2 -> "II";
			case 3 -> "III";
			case 4 -> "IV";
			case 5 -> "V";
			default -> Integer.toString(n);
		};
	}

	@Override
	protected void dibujar(GuiGraphicsExtractor g, float a) {
		int px = (LIENZO_W - W) / 2;
		int py = (LIENZO_H - H) / 2;
		Tema.panel(g, px, py, W, H, 39, a);
		g.text(this.font, Tema.negrita("Casino"), px + 17, py + 11, Tema.alfa(Tema.BLANCO, a), false);
		boolean hayCasino = FabricLoader.getInstance().isModLoaded("casinohs");
		g.text(this.font, hayCasino ? "Los premios de la tragamonedas de CasinoHS · al guardar cambian en la máquina"
				: "CasinoHS no está instalado: se guarda pero no hay tragamonedas que lo use", px + 17, py + 24, Tema.alfa(hayCasino ? Tema.TEXTO : 0xFFE0A04A, a), false);
		botonCerrar(g, px + W - 22, py + 15, a);

		// Ficha con la que se juega.
		int fy = py + 48;
		g.text(this.font, Tema.negrita("Se juega con"), px + 17, fy + 9, Tema.alfa(Tema.GRIS, a), false);
		cajaItem(g, "ficha", px + 95, fy, ficha, () -> abrir(new PantallaSelectorItem(this, item -> ficha = Objetos.id(item))), a);
		g.text(this.font, Tema.negrita(Objetos.nombre(ficha, "Ficha naranja")), px + 131, fy + 4, Tema.alfa(Tema.BLANCO, a), false);
		g.text(this.font, "1 por tirada · gana el 67,1 % de las veces", px + 131, fy + 15, Tema.alfa(Tema.GRIS, a), false);

		boolean lleno = premios.size() >= CasinoDatos.MAXIMO;
		boton(g, "agregar", px + W - 176, fy + 2, 162, 24, Tema.negrita("+ Agregar premio"), Estilo.AZUL,
				lleno ? "La tragamonedas admite como máximo " + CasinoDatos.MAXIMO + " premios" : null,
				lleno ? null : () -> abrir(new PantallaSelectorItem(this, item -> premios.add(new CasinoDatos.Premio(Objetos.id(item), 1, 10, false, "", 0)))),
				lleno ? a * 0.6f : a);

		// Cabecera de columnas.
		int x = px + 14;
		int w = W - 28;
		int hy = py + 84;
		g.text(this.font, "PREMIO", x + 44, hy, Tema.alfa(Tema.GRIS_OSCURO, a), false);
		g.text(this.font, "PROBABILIDAD", x + 330, hy, Tema.alfa(Tema.GRIS_OSCURO, a), false);
		g.text(this.font, "PESO", x + 466, hy, Tema.alfa(Tema.GRIS_OSCURO, a), false);
		g.text(this.font, "CANTIDAD", x + 568, hy, Tema.alfa(Tema.GRIS_OSCURO, a), false);
		g.text(this.font, "JACKPOT", x + 655, hy, Tema.alfa(Tema.GRIS_OSCURO, a), false);

		int listaY = py + 96;
		int listaH = H - 96 - 46;
		int alto = premios.size() * FILA;
		scroll = Math.max(0, Math.min(scroll, Math.max(0, alto - listaH)));
		double total = pesoTotal();
		g.enableScissor(px + 1, listaY, px + W - 1, listaY + listaH);

		for (int i = 0; i < premios.size(); i++) {
			int y = listaY + i * FILA - Math.round(scroll);

			if (y + FILA < listaY || y > listaY + listaH) {
				continue;
			}

			fila(g, i, x, y, w, listaY, listaH, total, a);
		}

		g.disableScissor();

		if (alto > listaH) {
			int barra = Math.max(18, listaH * listaH / alto);
			int by = listaY + Math.round((listaH - barra) * (scroll / (alto - listaH)));
			Tema.rect(g, px + W - 8, listaY, 2, listaH, Tema.alfa(0xFF1C202D, a));
			Tema.rect(g, px + W - 8, by, 2, barra, Tema.alfa(0xFF8993A3, a));
		}

		if (premios.isEmpty()) {
			Tema.textoCentrado(g, this.font, Component.literal("Sin premios: la tragamonedas usará los originales."), px + W / 2, listaY + 40, Tema.alfa(Tema.GRIS, a), false);
		}

		g.text(this.font, premios.size() + " de " + CasinoDatos.MAXIMO + " premios · Mayús = de 10 en 10", px + 17, py + H - 27, Tema.alfa(Tema.GRIS, a), false);
		boton(g, "orig", px + 527, py + 434, 130, 25, Tema.negrita("Valores originales"), Estilo.ROJO,
				"Vuelve a poner los premios que trae CasinoHS (no se guarda hasta pulsar Guardar)", () -> cargar(CasinoDatos.jsonPorDefecto()), a);
		boton(g, "guardar", px + 666, py + 434, 98, 25, Tema.negrita("Guardar"), Estilo.AZUL, null, () -> {
			com.google.gson.JsonObject json = CasinoDatos.aJson(premios, ficha);
			ClienteEstado.accion("guardar_casino", json);
			ClienteEstado.casino = json.deepCopy();
			onClose();
		}, a);
	}

	private void fila(GuiGraphicsExtractor g, int i, int x, int y, int w, int listaY, int listaH, double total, float a) {
		CasinoDatos.Premio p = premios.get(i);
		boolean visible = y >= listaY && y + FILA - 4 <= listaY + listaH;
		boolean hv = encima(x, y, w, FILA - 4) && ratonY >= listaY && ratonY < listaY + listaH;
		float h = anim("f" + i, hv ? 1 : 0, 14);
		Tema.caja(g, x, y, w, FILA - 4, Tema.alfa(Tema.TARJETA, a), Tema.alfa(Tema.mezclar(p.jackpot() ? 0xFF6A5420 : Tema.TARJETA_BORDE, Tema.TARJETA_BORDE_HOVER, h), a));

		int fila = i;
		cajaItem(g, "i" + i, x + 6, y + 2, p.item(), visible ? () -> abrir(new PantallaSelectorItem(this, item -> {
			String id = Objetos.id(item);
			CasinoDatos.Premio viejo = premios.get(fila);
			boolean libro = id.equals("minecraft:enchanted_book");
			cambiar(fila, new CasinoDatos.Premio(id, viejo.cantidad(), viejo.peso(), viejo.jackpot(), libro ? "sharpness" : "", libro ? 1 : 0));
		})) : null, a);

		String nombre = Objetos.nombre(p.item(), p.item());
		g.text(this.font, Tema.negrita(nombre), x + 44, y + 5, Tema.alfa(Objetos.stack(p.item()).isEmpty() ? 0xFFE0A04A : Tema.BLANCO, a), false);

		if (p.esLibro()) {
			String ench = nombreEncantamiento(p.encantamiento()) + " " + romano(p.nivel());
			boolean ehv = encima(x + 44, y + 16, 200, 11);
			g.text(this.font, "Libro: " + ench + "  ▸", x + 44, y + 17, Tema.alfa(ehv ? 0xFFB8CCFF : Tema.AZUL_CLARO, a), false);

			if (ehv) {
				tooltip("Clic: cambia el encantamiento · Mayús+clic: cambia el nivel");
			}

			if (visible) {
				zona(x + 44, y + 16, 200, 11, () -> {
					CasinoDatos.Premio v = premios.get(fila);

					if (shift) {
						int n = v.nivel() >= maxNivel(v.encantamiento()) ? 1 : v.nivel() + 1;
						cambiar(fila, new CasinoDatos.Premio(v.item(), v.cantidad(), v.peso(), v.jackpot(), v.encantamiento(), n));
					} else {
						int idx = 0;

						for (int k = 0; k < ENCANTAMIENTOS.length; k++) {
							if (ENCANTAMIENTOS[k][0].equals(v.encantamiento())) {
								idx = k;
							}
						}

						String sig = ENCANTAMIENTOS[(idx + 1) % ENCANTAMIENTOS.length][0];
						cambiar(fila, new CasinoDatos.Premio(v.item(), v.cantidad(), v.peso(), v.jackpot(), sig, Math.min(v.nivel(), maxNivel(sig))));
					}
				});
			}
		} else {
			g.text(this.font, p.item(), x + 44, y + 17, Tema.alfa(Tema.GRIS, a), false);
		}

		double pct = total > 0 && !Objetos.stack(p.item()).isEmpty() ? p.peso() * 67.1 / total : 0;
		g.text(this.font, String.format(Locale.ROOT, "%.2f %%", pct), x + 330, y + 11, Tema.alfa(Tema.AZUL_CLARO, a), false);

		if (encima(x + 330, y + 4, 90, 20)) {
			tooltip("De cada 100 tiradas, cuántas dan este premio");
		}

		mini(g, "pm" + i, x + 440, y + 6, "−", false, visible, () -> {
			CasinoDatos.Premio v = premios.get(fila);
			cambiar(fila, new CasinoDatos.Premio(v.item(), v.cantidad(), Math.max(0.1, redondear(v.peso() - paso())), v.jackpot(), v.encantamiento(), v.nivel()));
		}, a);
		Tema.textoCentrado(g, this.font, Component.literal(net.ruleta.Ajustes.numero(p.peso())), x + 482, y + 11, Tema.alfa(Tema.BLANCO, a), false);
		mini(g, "pp" + i, x + 508, y + 6, "+", false, visible, () -> {
			CasinoDatos.Premio v = premios.get(fila);
			cambiar(fila, new CasinoDatos.Premio(v.item(), v.cantidad(), Math.min(10000, redondear(v.peso() + paso())), v.jackpot(), v.encantamiento(), v.nivel()));
		}, a);

		mini(g, "cm" + i, x + 554, y + 6, "−", false, visible && !p.esLibro(), () -> {
			CasinoDatos.Premio v = premios.get(fila);
			cambiar(fila, new CasinoDatos.Premio(v.item(), Math.max(1, v.cantidad() - (int) paso()), v.peso(), v.jackpot(), v.encantamiento(), v.nivel()));
		}, p.esLibro() ? a * 0.4f : a);
		Tema.textoCentrado(g, this.font, Component.literal("×" + p.cantidad()), x + 594, y + 11, Tema.alfa(Tema.BLANCO, a), false);
		mini(g, "cp" + i, x + 614, y + 6, "+", false, visible && !p.esLibro(), () -> {
			CasinoDatos.Premio v = premios.get(fila);
			cambiar(fila, new CasinoDatos.Premio(v.item(), Math.min(64, v.cantidad() + (int) paso()), v.peso(), v.jackpot(), v.encantamiento(), v.nivel()));
		}, p.esLibro() ? a * 0.4f : a);

		// Jackpot (★) y quitar.
		boolean jhv = encima(x + 662, y + 6, 20, 20);
		Tema.caja(g, x + 662, y + 6, 20, 20, Tema.alfa(p.jackpot() ? 0xFF5A4318 : Tema.CONTROL, a), Tema.alfa(p.jackpot() ? 0xFFF5C242 : jhv ? 0xFF5C7BC8 : 0xFF2D3344, a));
		g.text(this.font, "★", x + 668, y + 12, Tema.alfa(p.jackpot() ? 0xFFF5C242 : 0xFF6C7488, a), false);

		if (jhv) {
			tooltip("Jackpot: celebración especial al ganarlo");
		}

		if (visible) {
			zona(x + 662, y + 6, 20, 20, () -> {
				CasinoDatos.Premio v = premios.get(fila);
				cambiar(fila, new CasinoDatos.Premio(v.item(), v.cantidad(), v.peso(), !v.jackpot(), v.encantamiento(), v.nivel()));
			});
		}

		mini(g, "x" + i, x + 712, y + 6, "×", true, visible, () -> premios.remove(fila), a);
	}

	private static double redondear(double v) {
		return Math.round(v * 1000.0) / 1000.0;
	}

	private void icono(GuiGraphicsExtractor g, String id, int x, int y, float a) {
		ItemStack s = Objetos.stack(id);

		if (!s.isEmpty()) {
			g.item(s, x, y);
		} else {
			Tema.caja(g, x, y, 16, 16, Tema.alfa(0xFF3A2A20, a), Tema.alfa(0xFFEEA632, a));
			g.text(this.font, "?", x + 6, y + 4, Tema.alfa(0xFFEEA632, a), false);
		}
	}

	private void cajaItem(GuiGraphicsExtractor g, String clave, int x, int y, String id, Runnable cambiar, float a) {
		boolean hv = encima(x, y, 28, 28);
		float h = anim(clave, hv ? 1 : 0, 14);
		Tema.caja(g, x, y, 28, 28, Tema.alfa(Tema.mezclar(0xFF141A24, 0xFF1E2840, h), a), Tema.alfa(Tema.mezclar(Tema.TARJETA_BORDE, Tema.AZUL_BORDE, h), a));
		icono(g, id, x + 6, y + 6, a);

		if (hv) {
			tooltip(Objetos.nombre(id, id) + "\n" + id + "\nClic para cambiar el item");
		}

		if (cambiar != null) {
			zona(x, y, 28, 28, cambiar);
		}
	}

	private void mini(GuiGraphicsExtractor g, String clave, int x, int y, String txt, boolean rojo, boolean activo, Runnable accion, float a) {
		boolean hv = activo && encima(x, y, 20, 20);
		float h = anim(clave, hv ? 1 : 0, 16);
		int fondo = rojo ? Tema.mezclar(0xFF2A1C26, 0xFF4A2433, h) : Tema.mezclar(Tema.CONTROL, 0xFF34405A, h);
		int borde = rojo ? Tema.mezclar(0xFF4A2B38, 0xFFB5506E, h) : Tema.mezclar(0xFF2D3344, 0xFF5C7BC8, h);
		Tema.caja(g, x, y, 20, 20, Tema.alfa(fondo, a), Tema.alfa(borde, a));
		int c = rojo ? Tema.ROJO_TEXTO : Tema.mezclar(Tema.TEXTO, 0xFFFFFFFF, h);
		g.text(this.font, txt, x + 10 - this.font.width(txt) / 2, y + 6, Tema.alfa(c, a), false);

		if (activo) {
			zona(x, y, 20, 20, accion);
		}
	}

	private void mini(GuiGraphicsExtractor g, String clave, int x, int y, String txt, boolean rojo, boolean activo, Runnable accion) {
		mini(g, clave, x, y, txt, rojo, activo, accion, 1);
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		shift = event.hasShiftDown();
		return super.mouseClicked(event, doubleClick);
	}

	@Override
	protected boolean desplazar(double mx, double my, double cantidad) {
		scroll -= (float) cantidad * FILA;
		return true;
	}
}
