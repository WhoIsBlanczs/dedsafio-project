package net.ruleta.client;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;

import net.ruleta.client.ui.PantallaBase;
import net.ruleta.client.ui.Tema;

/** Formulario de cada casilla: "Rellena las secciones de izquierda a derecha y pulsa LANZAR". */
public class PantallaLanzar extends PantallaBase {
	public static final List<String> MOMENTOS_REVIIL = List.of(
			"BUNKER: RAYO MORTAL", "GAS TÓXICO", "CEGUERA Y CREEPERS", "AGUJEROS NEGROS", "TORNADOS",
			"PHORA: MISILES", "METEORITOS", "SUPERNOVA", "ECLIPSE");
	public static final List<String> MOBS = List.of(
			"Creeper Eléctrico", "Creeper Nuclear", "Creeper Rojo", "Bomba Warden", "Herobrine",
			"Zombie Inteligente", "Araña de Cueva", "Blaze del Nether");
	public static final List<String> CAMBIOS = List.of(
			"Deep Dark peligroso", "Bombas Warden", "Blazes en el Nether", "Araña de cueva en telarañas",
			"Endermite letales", "Enderman letales", "Mis Ojos!", "Creeper Nuclear con perla",
			"Zombie Inteligente", "Herobrine", "Creeper de hierba", "Creeper rojo");

	private static final int W = 600;
	private static final int H = 339;

	private final Casilla casilla;
	private EditBox titulo;
	private MultiLineEditBox mensaje;
	private EditBox temporizador;
	private EditBox minutosBan;

	String para = "";
	private boolean mini;
	private boolean nutria = true;
	private int momento;
	private String castigo = "ninguno";
	final Set<String> extras = new LinkedHashSet<>();

	// Para conservar lo escrito al volver de las subpantallas.
	private String tituloGuardado = "";
	private String mensajeGuardado = "";
	private String tempGuardado = "";
	private String banGuardado = "60";

	public PantallaLanzar(Casilla casilla, PantallaRuleta anterior) {
		super(Component.literal(casilla.nombre), anterior);
		this.casilla = casilla;
	}

	private int px() {
		return (LIENZO_W - W) / 2;
	}

	private int py() {
		return (LIENZO_H - H) / 2;
	}

	private boolean conTemporizador() {
		return casilla == Casilla.REVIIL || casilla == Casilla.NUTRIA || casilla == Casilla.ANUNCIO;
	}

	@Override
	protected void iniciar() {
		int px = px();
		int py = py();
		int lx = px + 12;
		int rx = px + 272;

		titulo = new EditBox(this.font, lx + 5, py + 58, 238, 10, Component.literal("Título"));
		titulo.setBordered(false);
		titulo.setMaxLength(80);
		titulo.setValue(tituloGuardado);
		titulo.setResponder(s -> tituloGuardado = s);
		campoTexto(titulo, lx, py + 53, 248, 19);

		mensaje = MultiLineEditBox.builder()
				.setX(lx)
				.setY(py + 94)
				.setPlaceholder(Component.literal("Escribe el mensaje..."))
				.build(this.font, 248, 191, Component.literal("Mensaje"));
		mensaje.setCharacterLimit(1000);
		mensaje.setValue(mensajeGuardado);
		mensaje.setValueListener(s -> mensajeGuardado = s);
		campoTexto(mensaje, lx, py + 94, 248, 191);

		temporizador = new EditBox(this.font, rx + 5, py + 140, 109, 10, Component.literal("Temporizador"));
		temporizador.setBordered(false);
		temporizador.setMaxLength(8);
		temporizador.setHint(Component.literal("Sin temporizador").withColor(0xC9CFDB));
		temporizador.setValue(tempGuardado);
		temporizador.setResponder(s -> tempGuardado = s);
		temporizador.visible = conTemporizador();
		campoTexto(temporizador, rx, py + 135, 119, 19);

		minutosBan = new EditBox(this.font, rx + 5, py + 194, 60, 10, Component.literal("Minutos"));
		minutosBan.setBordered(false);
		minutosBan.setMaxLength(6);
		minutosBan.setValue(banGuardado);
		minutosBan.setResponder(s -> banGuardado = s);
		minutosBan.visible = casilla == Casilla.SANCION && castigo.equals("ban");
		campoTexto(minutosBan, rx, py + 189, 70, 19);

		setInitialFocus(titulo);
	}

	/** Segundos del temporizador, -1 si está vacío, -2 si está mal escrito. */
	private int segundosTemporizador() {
		String s = tempGuardado.trim();

		if (s.isEmpty()) {
			return -1;
		}

		try {
			String[] p = s.split(":");
			int total = 0;

			if (p.length > 3) {
				return -2;
			}

			for (String parte : p) {
				int n = Integer.parseInt(parte.trim());

				if (n < 0) {
					return -2;
				}

				total = total * 60 + n;
			}

			return total > 0 ? total : -2;
		} catch (NumberFormatException e) {
			return -2;
		}
	}

	private String queSale() {
		return switch (casilla) {
			case NUTRIA -> nutria ? " con Momento Nutria" : "";
			case REVIIL -> " con " + capitalizar(MOMENTOS_REVIIL.get(momento));
			case NUEVO_MOB, CAMBIO -> extras.isEmpty() ? "" : " con " + String.join(", ", extras);
			default -> "";
		};
	}

	private static String capitalizar(String s) {
		String l = s.toLowerCase(Locale.ROOT);
		return Character.toUpperCase(l.charAt(0)) + l.substring(1);
	}

	private String resumen() {
		StringBuilder sb = new StringBuilder("Sale: ");
		sb.append(casilla.nombre).append(mini ? " mini" : " normal").append(queSale());
		int seg = conTemporizador() ? segundosTemporizador() : -1;

		if (seg > 0) {
			sb.append(" · temporizador ").append(formatoTiempo(seg));
		}

		if (casilla == Casilla.SANCION && !castigo.equals("ninguno") && !para.isEmpty()) {
			sb.append(" · ").append(castigo.equals("ban") ? "BAN" : "KICK");
		}

		sb.append(" · ").append(para.isEmpty() ? "para todos" : "para " + para);
		return sb.toString();
	}

	public static String formatoTiempo(int seg) {
		int h = seg / 3600;
		int m = (seg % 3600) / 60;
		int s = seg % 60;
		return h > 0 ? String.format(Locale.ROOT, "%d:%02d:%02d", h, m, s) : String.format(Locale.ROOT, "%d:%02d", m, s);
	}

	private void etiqueta(GuiGraphicsExtractor g, String texto, int x, int y, float a) {
		Tema.titulo(texto, Tema.GRIS).dibujar(g, this.font, x, y, Tema.alfa(0xFFFFFFFF, a), false);
	}

	private void campo(GuiGraphicsExtractor g, EditBox box, int x, int y, int w, int h, boolean error, float a) {
		boolean foco = box.isFocused();
		int fondo = foco ? 0xFF1C2742 : Tema.CAMPO;
		int borde = error ? 0xFFC0485F : foco ? 0xFF7F9BE6 : encima(x, y, w, h) ? 0xFF46557A : Tema.CAMPO_BORDE;
		Tema.caja(g, x, y, w, h, Tema.alfa(fondo, a), Tema.alfa(borde, a));

		if (foco) {
			Tema.rect(g, x + 1, y + h - 2, w - 2, 1, Tema.alfa(Tema.AZUL_CLARO, a));
		}
	}

	@Override
	protected void dibujar(GuiGraphicsExtractor g, float a) {
		int px = px();
		int py = py();
		boolean widgets = a > 0.6f;
		titulo.visible = widgets;
		mensaje.visible = widgets;
		temporizador.visible = widgets && conTemporizador();
		minutosBan.visible = widgets && casilla == Casilla.SANCION && castigo.equals("ban");

		Tema.panel(g, px, py, W, H, 35, a);

		// Cabecera: icono de la casilla, nombre en su color y subtítulo.
		g.blit(RenderPipelines.GUI_TEXTURED, casilla.icono, px + 13, py + 8, 0, 0, 20, 20, 256, 256, 256, 256, Tema.alfa(0xFFFFFFFF, a));
		casilla.rotulo().dibujar(g, this.font, px + 41, py + 9, Tema.alfa(0xFFFFFFFF, a), false);
		g.text(this.font, "Rellena las secciones de izquierda a derecha y pulsa LANZAR", px + 41, py + 21, Tema.alfa(Tema.TEXTO, a), false);
		Tema.rect(g, px + 33, py + 35, 37, 1, Tema.alfa(0xFF000000 | casilla.rgb, a));

		// Botón volver.
		int bx = px + 570;
		int by = py + 9;
		boolean hv = encima(bx, by, 20, 19);
		Tema.caja(g, bx, by, 20, 19, Tema.alfa(hv ? 0xFF2A3346 : 0xFF1C2333, a), Tema.alfa(hv ? 0xFF6C8FE8 : 0xFF3A4258, a));
		g.text(this.font, "↩", bx + 6, by + 6, Tema.alfa(hv ? 0xFFFFFFFF : Tema.TEXTO, a), false);

		if (hv) {
			tooltip("Volver a la ruleta");
		}

		zona(bx, by, 20, 19, this::onClose);

		// Columna izquierda.
		int lx = px + 12;
		etiqueta(g, "1 · TÍTULO", lx, py + 41, a);
		campo(g, titulo, lx, py + 53, 248, 19, false, a);
		etiqueta(g, "2 · MENSAJE", lx, py + 82, a);

		Tema.rect(g, px + 267, py + 40, 1, H - 50, Tema.alfa(Tema.SEPARADOR, a));

		// Columna derecha.
		int rx = px + 272;
		int rw = 316;
		etiqueta(g, "3 · ¿PARA QUIÉN?", rx, py + 41, a);
		String quien = para.isEmpty() ? "TODOS LOS JUGADORES" : para.toUpperCase(Locale.ROOT);
		boton(g, "para", rx, py + 53, rw, 19, Tema.titulo(quien, 0xFFFFFF), Estilo.OSCURO, null,
				() -> abrir(new PantallaJugadores(this)), a);

		etiqueta(g, "4 · TAMAÑO", rx, py + 82, a);
		boton(g, "mini", rx, py + 93, 155, 20, Tema.titulo("MINI", 0xFFFFFF), mini ? Estilo.SELECCIONADO : Estilo.OSCURO, null, () -> mini = true, a);
		boton(g, "normal", rx + 161, py + 93, 155, 20, Tema.titulo("NORMAL", 0xFFFFFF), mini ? Estilo.OSCURO : Estilo.SELECCIONADO, null, () -> mini = false, a);

		switch (casilla) {
			case REVIIL, NUTRIA, ANUNCIO -> {
				etiqueta(g, "5 · TEMPORIZADOR", rx, py + 123, a);
				campo(g, temporizador, rx, py + 135, 119, 19, segundosTemporizador() == -2, a);
				g.text(this.font, "mm:ss o hh:mm:ss (vacío = sin temporizador)", rx, py + 159, Tema.alfa(Tema.GRIS, a), false);

				if (casilla == Casilla.NUTRIA) {
					etiqueta(g, "6 · QUÉ SALE", rx, py + 178, a);
					opcion(g, "nutria", rx, py + 189, rw, 20, "NUTRIA: ENCENDIDO", nutria, () -> nutria = !nutria, a);
				} else if (casilla == Casilla.REVIIL) {
					etiqueta(g, "6 · QUÉ SALE", rx, py + 178, a);
					int ow = (rw - 6) / 2;

					for (int i = 0; i < MOMENTOS_REVIIL.size(); i++) {
						int col = i % 2;
						int fila = i / 2;
						int idx = i;
						opcion(g, "mom" + i, rx + col * (ow + 6), py + 189 + fila * 18, ow, 16, MOMENTOS_REVIIL.get(i), momento == i, () -> momento = idx, a);
					}
				}
			}
			case SANCION -> {
				etiqueta(g, "5 · CASTIGO", rx, py + 123, a);
				String txt = "CASTIGO: " + switch (castigo) {
					case "kick" -> "KICK";
					case "ban" -> "BAN";
					default -> "NINGUNO";
				};
				boton(g, "castigo", rx, py + 135, rw, 19, Tema.titulo(txt, 0xFFFFFF), Estilo.OSCURO,
						"Castigo al jugador elegido, 10 segundos después del anuncio: BAN por el tiempo indicado o KICK (\"GG\")",
						() -> castigo = switch (castigo) {
							case "ninguno" -> "kick";
							case "kick" -> "ban";
							default -> "ninguno";
						}, a);

				if (castigo.equals("ban")) {
					etiqueta(g, "6 · MINUTOS DE BAN", rx, py + 178, a);
					campo(g, minutosBan, rx, py + 189, 70, 19, minutosBan() <= 0, a);
				}

				if (!castigo.equals("ninguno") && para.isEmpty()) {
					g.text(this.font, "Elige un jugador en «¿Para quién?» para aplicar el castigo.", rx, py + 216, Tema.alfa(0xFFE0A04A, a), false);
				}
			}
			case NUEVO_MOB -> {
				etiqueta(g, "5 · EXTRAS", rx, py + 123, a);
				String txt = extras.isEmpty() ? "MOBS NUEVOS" : extras.size() + (extras.size() == 1 ? " MOB NUEVO" : " MOBS NUEVOS");
				boton(g, "extras", rx, py + 135, rw, 19, Tema.titulo(txt, 0xFFFFFF), Estilo.OSCURO, null,
						() -> abrir(new PantallaElegir(this, "Mobs nuevos", "Elige los mobs que salen con el anuncio", MOBS, extras)), a);
			}
			case CAMBIO -> {
				etiqueta(g, "5 · EXTRAS", rx, py + 123, a);
				String txt = extras.isEmpty() ? "CAMBIOS" : extras.size() + (extras.size() == 1 ? " CAMBIO" : " CAMBIOS");
				boton(g, "extras", rx, py + 135, rw, 19, Tema.titulo(txt, 0xFFFFFF), Estilo.OSCURO, null,
						() -> abrir(new PantallaElegir(this, "Cambios", "Elige los cambios de dificultad que se activan", CAMBIOS, extras)), a);
			}
			default -> {
			}
		}

		// Pie: resumen y LANZAR.
		Tema.rect(g, rx, py + 284, rw, 1, Tema.alfa(Tema.SEPARADOR, a));
		String res = resumen();

		while (this.font.width(res) > rw && res.length() > 4) {
			res = res.substring(0, res.length() - 4) + "...";
		}

		g.text(this.font, res, rx, py + 290, Tema.alfa(Tema.TEXTO, a), false);
		String error = error();
		boton(g, "lanzar", rx, py + 305, rw, 22, Tema.titulo("LANZAR", 0xFFFFFF), Estilo.AZUL, error, error == null ? this::lanzar : null, a);
	}

	private void opcion(GuiGraphicsExtractor g, String id, int x, int y, int w, int h0, String texto, boolean sel, Runnable accion, float a) {
		boolean hv = encima(x, y, w, h0);
		float h = anim("op." + id, hv ? 1 : 0, 14);
		int fondo = sel ? Tema.mezclar(0xFF3A4D80, 0xFF4560A0, h) : Tema.mezclar(0xFF1C2333, 0xFF222A3D, h);
		int borde = sel ? 0xFF7F9BE6 : Tema.mezclar(0xFF2C3448, 0xFF5C7BC8, h);
		Tema.caja(g, x, y, w, h0, Tema.alfa(fondo, a), Tema.alfa(borde, a));
		int tx = x + 7;
		int ty = y + (h0 - 8) / 2;

		if (sel) {
			Tema.rect(g, x + 7, ty + 2, 4, 4, Tema.alfa(0xFF9DB6FF, a));
			tx = x + 16;
		}

		Tema.titulo(texto, sel ? 0xFFFFFF : 0xC9CFDB).dibujar(g, this.font, tx, ty, Tema.alfa(0xFFFFFFFF, a), false);
		zona(x, y, w, h0, accion);
	}

	private int minutosBan() {
		try {
			return Integer.parseInt(banGuardado.trim());
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	private String error() {
		if (conTemporizador() && segundosTemporizador() == -2) {
			return "El temporizador tiene que ser mm:ss o hh:mm:ss";
		}

		if (casilla == Casilla.SANCION && castigo.equals("ban") && minutosBan() <= 0) {
			return "Escribe cuántos minutos dura el BAN";
		}

		return null;
	}

	private void lanzar() {
		JsonObject o = new JsonObject();
		o.addProperty("tipo", casilla.id);
		o.addProperty("nombre", casilla.nombre);
		o.addProperty("titulo", tituloGuardado.trim());
		o.addProperty("mensaje", mensajeGuardado.trim());
		o.addProperty("para", para);
		o.addProperty("tamano", mini ? "mini" : "normal");
		o.addProperty("segundos", conTemporizador() ? Math.max(-1, segundosTemporizador()) : -1);

		switch (casilla) {
			case NUTRIA -> o.addProperty("nutria", nutria);
			case REVIIL -> o.addProperty("momento", capitalizar(MOMENTOS_REVIIL.get(momento)));
			case SANCION -> {
				o.addProperty("castigo", castigo);
				o.addProperty("minutos", Math.max(0, minutosBan()));
			}
			case NUEVO_MOB -> {
				JsonArray arr = new JsonArray();
				extras.forEach(arr::add);
				o.add("mobs", arr);
			}
			case CAMBIO -> {
				JsonArray arr = new JsonArray();
				extras.forEach(arr::add);
				o.add("cambios", arr);
			}
			default -> {
			}
		}

		ClienteEstado.accion("lanzar", o);
		this.minecraft.gui.setScreen(null);
	}

	List<String> jugadoresConectados() {
		List<String> r = new ArrayList<>();

		if (this.minecraft.getConnection() != null) {
			this.minecraft.getConnection().getOnlinePlayers().forEach(info -> r.add(info.getProfile().name()));
		}

		r.sort(String.CASE_INSENSITIVE_ORDER);
		return r;
	}
}
