package net.ruleta.client;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;

import net.ruleta.Datos;
import net.ruleta.MomentosHS;
import net.ruleta.client.ui.PantallaBase;
import net.ruleta.client.ui.Tema;

/** Formulario de cada casilla: "Rellena las secciones de izquierda a derecha y pulsa LANZAR". */
public class PantallaLanzar extends PantallaBase {
	private static final int W = 600;
	private static final int H = 339;
	private static final int RX = 272;
	private static final int RW = 316;

	private final Casilla casilla;
	private EditBox titulo;
	private MultiLineEditBox mensaje;
	private EditBox temporizador;
	private EditBox duracionBan;

	private String para = "";
	private boolean mini;
	private boolean nutria = true;
	private String castigo = "ninguno";
	private String castigado = "";
	private boolean reviil;
	private boolean verMensaje = true;
	private boolean verAnimacion = true;
	private boolean verCielo = true;
	private final PantallaTarjetas.Estado tarjeta = new PantallaTarjetas.Estado();
	private Map<String, PantallaMomentos.Eleccion> momentos = new LinkedHashMap<>();

	// Para conservar lo escrito al volver de las subpantallas.
	private String tituloGuardado = "";
	private String mensajeGuardado = "";
	private String tempGuardado = "";
	private String banGuardado = "";
	/** Texto que se añadió solo al mensaje (descripción del cambio o de los momentos). */
	private String textoAuto = "";

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
		return casilla == Casilla.NUTRIA || casilla == Casilla.ANUNCIO;
	}

	/** Si se puede elegir a quién le sale (con castigo, cambios o mobs sale para todos). */
	private boolean paraQuienLibre() {
		return switch (casilla) {
			case CAMBIO, NUEVO_MOB -> false;
			case SANCION -> castigo.equals("ninguno");
			default -> true;
		};
	}

	@Override
	protected void iniciar() {
		int px = px();
		int py = py();
		int lx = px + 12;
		int rx = px + RX;

		titulo = new EditBox(this.font, lx + 5, py + 58, 238, 10, Component.literal("Título"));
		titulo.setBordered(false);
		titulo.setMaxLength(80);
		titulo.setHint(Component.literal("Título (opcional)").withColor(0xC9CFDB));
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
		campoTexto(temporizador, rx, py + 135, 119, 19);

		duracionBan = new EditBox(this.font, rx + 167, py + 166, 144, 10, Component.literal("Duración"));
		duracionBan.setBordered(false);
		duracionBan.setMaxLength(12);
		duracionBan.setHint(Component.literal("DURACIÓN (30m, 2h)").withColor(0xC9CFDB));
		duracionBan.setValue(banGuardado);
		duracionBan.setResponder(s -> banGuardado = s);
		campoTexto(duracionBan, rx + 161, py + 161, 155, 20);

		setInitialFocus(titulo);
	}

	// ------------------------------------------------------------------ tiempos

	/** Lee mm:ss o hh:mm:ss. Devuelve -1 si está mal escrito. */
	public static int leerTiempo(String s) {
		try {
			String[] p = s.trim().split(":");

			if (p.length > 3) {
				return -1;
			}

			int total = 0;

			for (String parte : p) {
				int n = Integer.parseInt(parte.trim());

				if (n < 0) {
					return -1;
				}

				total = total * 60 + n;
			}

			return total > 0 ? total : -1;
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	/** Segundos del temporizador, -1 si está vacío, -2 si está mal escrito. */
	private int segundosTemporizador() {
		String s = tempGuardado.trim();

		if (s.isEmpty()) {
			return -1;
		}

		int t = leerTiempo(s);
		return t > 0 ? t : -2;
	}

	/** Minutos de BAN a partir de «30m», «2h», «1h30m» o un número (minutos). -1 si está mal. */
	private int minutosBan() {
		String s = banGuardado.trim().toLowerCase(Locale.ROOT).replace(" ", "");

		if (s.isEmpty()) {
			return -1;
		}

		if (s.matches("\\d+")) {
			return Integer.parseInt(s);
		}

		java.util.regex.Matcher m = java.util.regex.Pattern.compile("(\\d+)([dhms])").matcher(s);
		int total = 0;
		int fin = 0;

		while (m.find()) {
			if (m.start() != fin) {
				return -1;
			}

			fin = m.end();
			int n = Integer.parseInt(m.group(1));
			total += switch (m.group(2)) {
				case "d" -> n * 1440;
				case "h" -> n * 60;
				case "s" -> Math.max(1, n / 60);
				default -> n;
			};
		}

		return fin == s.length() && total > 0 ? total : -1;
	}

	public static String formatoTiempo(int seg) {
		int h = seg / 3600;
		int m = (seg % 3600) / 60;
		int s = seg % 60;
		return h > 0 ? String.format(Locale.ROOT, "%d:%02d:%02d", h, m, s) : String.format(Locale.ROOT, "%02d:%02d", m, s);
	}

	// ------------------------------------------------------------------ texto automático del mensaje

	/** Cambia el texto que la selección añadió al mensaje, sin tocar lo que escribió el admin. */
	private void ponerTextoAuto(String nuevo) {
		String actual = mensajeGuardado;

		if (!textoAuto.isEmpty() && actual.contains(textoAuto)) {
			actual = actual.replace(textoAuto, "").strip();
		}

		String r = nuevo.isEmpty() ? actual : (actual.isEmpty() ? nuevo : actual + " " + nuevo);

		if (r.length() > 1000) {
			r = r.substring(0, 1000);
		}

		textoAuto = nuevo;
		mensajeGuardado = r;

		if (mensaje != null) {
			mensaje.setValue(r);
		}
	}

	private void actualizarMomentos(Map<String, PantallaMomentos.Eleccion> elegidos) {
		momentos = new LinkedHashMap<>(elegidos);
		StringBuilder sb = new StringBuilder();

		for (Map.Entry<String, PantallaMomentos.Eleccion> e : momentos.entrySet()) {
			MomentosHS.Momento m = MomentosHS.porId(e.getKey());

			if (m != null) {
				if (!sb.isEmpty()) {
					sb.append(' ');
				}

				sb.append(MomentosHS.texto(m, e.getValue().segundos(), e.getValue().num(), e.getValue().texto()));
			}
		}

		if (!momentos.isEmpty()) {
			reviil = true;

			if (tituloGuardado.isBlank()) {
				tituloGuardado = "Momento Reviil";

				if (titulo != null) {
					titulo.setValue(tituloGuardado);
				}
			}
		}

		ponerTextoAuto(sb.toString());
	}

	// ------------------------------------------------------------------ resumen

	private String nombreSale() {
		return tituloGuardado.isBlank() ? casilla.nombre + (mini ? " mini" : " normal") : tituloGuardado.trim();
	}

	private String resumen() {
		StringBuilder sb = new StringBuilder("Sale: ").append(nombreSale());

		switch (casilla) {
			case NUTRIA -> sb.append(nutria ? " con Momento Nutria" : "");
			case REVIIL -> {
				List<String> partes = new ArrayList<>();

				if (verMensaje) {
					partes.add("mensaje");
				}

				if (verAnimacion) {
					partes.add("animación");
				}

				if (verCielo) {
					partes.add("cielo");
				}

				sb.append(partes.isEmpty() ? " sin nada" : " con " + String.join(", ", partes));
			}
			default -> {
			}
		}

		int seg = conTemporizador() ? segundosTemporizador() : -1;

		if (seg > 0) {
			sb.append(" · temporizador ").append(formatoTiempo(seg));
		}

		if (casilla == Casilla.SANCION && !castigo.equals("ninguno")) {
			sb.append(" · ").append(castigo.equals("ban") ? "BAN" : "KICK").append(castigado.isEmpty() ? "" : " a " + castigado);
		}

		sb.append(" · ").append(para.isEmpty() || !paraQuienLibre() ? "para todos" : "para " + para);
		return sb.toString();
	}

	// ------------------------------------------------------------------ dibujo

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
		duracionBan.visible = widgets && casilla == Casilla.SANCION && castigo.equals("ban");

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
			tooltip("Volver");
		}

		zona(bx, by, 20, 19, this::onClose);

		// Columna izquierda.
		int lx = px + 12;
		etiqueta(g, "1 · TÍTULO", lx, py + 41, a);
		campo(g, titulo, lx, py + 53, 248, 19, false, a);
		etiqueta(g, "2 · MENSAJE", lx, py + 82, a);
		Tema.textoDerecha(g, this.font, Component.literal(mensajeGuardado.length() + "/1000"), lx + 248, py + 290, Tema.alfa(Tema.GRIS, a));

		Tema.rect(g, px + 267, py + 40, 1, H - 50, Tema.alfa(Tema.SEPARADOR, a));

		// Columna derecha.
		int rx = px + RX;
		etiqueta(g, "3 · ¿PARA QUIÉN?", rx, py + 41, a);
		boolean libre = paraQuienLibre();
		String quien = para.isEmpty() || !libre ? "TODOS LOS JUGADORES" : para.toUpperCase(Locale.ROOT);
		boton(g, "para", rx, py + 53, RW, 19, Tema.titulo(quien, libre ? 0xFFFFFF : 0x8E95A6), Estilo.OSCURO,
				libre ? "Elige un jugador: solo a él le sale esta ruleta y su mensaje" : null,
				libre ? () -> abrir(new PantallaJugadores(this, para, true, n -> para = n)) : null, libre ? a : a * 0.7f);

		etiqueta(g, "4 · TAMAÑO", rx, py + 82, a);
		boton(g, "mini", rx, py + 93, 155, 20, Tema.titulo("MINI", 0xFFFFFF), mini ? Estilo.SELECCIONADO : Estilo.OSCURO, null, () -> mini = true, a);
		boton(g, "normal", rx + 161, py + 93, 155, 20, Tema.titulo("NORMAL", 0xFFFFFF), mini ? Estilo.OSCURO : Estilo.SELECCIONADO, null, () -> mini = false, a);

		switch (casilla) {
			case NUTRIA, ANUNCIO -> {
				etiqueta(g, "5 · TEMPORIZADOR", rx, py + 123, a);
				campo(g, temporizador, rx, py + 135, 119, 19, segundosTemporizador() == -2, a);
				g.text(this.font, "mm:ss o hh:mm:ss (vacío = sin temporizador)", rx, py + 159, Tema.alfa(Tema.GRIS, a), false);

				if (casilla == Casilla.NUTRIA) {
					etiqueta(g, "6 · QUÉ SALE", rx, py + 178, a);
					opcion(g, "nutria", rx, py + 189, RW, 20, "NUTRIA: " + (nutria ? "ENCENDIDO" : "APAGADO"), nutria, () -> nutria = !nutria, a);
				}
			}
			case REVIIL -> {
				etiqueta(g, "5 · EXTRAS", rx, py + 123, a);
				String txt = momentos.isEmpty() ? "MOMENTOS REVIIL" : "MOMENTOS REVIIL (" + momentos.size() + ")";
				boton(g, "momentos", rx, py + 135, RW, 20, Tema.titulo(txt, 0xFFFFFF), Estilo.OSCURO, "Los Momentos Revil de Hombrosafío que se activan al salir el mensaje",
						() -> abrir(new PantallaMomentos(this, new LinkedHashMap<>(momentos), this::actualizarMomentos)), a);

				etiqueta(g, "6 · QUÉ SALE", rx, py + 178, a);
				opcion(g, "reviil", rx, py + 189, RW, 20, "REVIIL: " + (reviil ? "ENCENDIDO" : "APAGADO"), reviil,
						() -> reviil = !reviil, a);
				int sw = (RW - 12) / 3;
				int w3 = RW - 2 * (sw + 6);
				opcion(g, "vermensaje", rx, py + 217, sw, 20, "MENSAJE", verMensaje, () -> verMensaje = !verMensaje, a);
				opcion(g, "veranimacion", rx + sw + 6, py + 217, sw, 20, "ANIMACIÓN", verAnimacion, () -> verAnimacion = !verAnimacion, a);
				opcion(g, "vercielo", rx + 2 * (sw + 6), py + 217, w3, 20, "CIELO", verCielo, () -> verCielo = !verCielo, a);

				if (encima(rx, py + 217, RW, 20)) {
					int cual = (ratonX - rx) / (sw + 6);
					tooltip(switch (Math.min(2, Math.max(0, cual))) {
						case 0 -> "Mensaje: el aviso grande de este menú cuando para la ruleta";
						case 1 -> "Animación: la animación de Revil de Hombrosafío (pantalla roja y sonido)";
						default -> "Cielo: el cielo rojo de Revil con la grieta";
					});
				}

				if (!ClienteEstado.hombrosafio && !momentos.isEmpty()) {
					g.text(this.font, "Hombrosafío no está en el servidor: no se activarán.", rx, py + 246, Tema.alfa(0xFFE0A04A, a), false);
				}
			}
			case SANCION -> {
				etiqueta(g, "5 · CASTIGO", rx, py + 123, a);
				String txt = "CASTIGO: " + switch (castigo) {
					case "kick" -> "KICK (GG)";
					case "ban" -> "BAN";
					default -> "NINGUNO";
				};
				boton(g, "castigo", rx, py + 135, RW, 20, Tema.titulo(txt, 0xFFFFFF), Estilo.OSCURO,
						"Castigo al jugador elegido, 10 segundos después del anuncio: BAN por el tiempo indicado o KICK (\"GG\")",
						() -> castigo = switch (castigo) {
							case "ninguno" -> "ban";
							case "ban" -> "kick";
							default -> "ninguno";
						}, a);

				if (!castigo.equals("ninguno")) {
					String jug = castigado.isEmpty() ? "ELEGIR JUGADOR..." : castigado.toUpperCase(Locale.ROOT);
					boton(g, "castigado", rx, py + 161, 155, 20, Tema.titulo(jug, 0xFFFFFF), Estilo.OSCURO, null,
							() -> abrir(new PantallaJugadores(this, castigado, false, n -> castigado = n)), a);

					if (castigo.equals("ban")) {
						campo(g, duracionBan, rx + 161, py + 161, 155, 20, !banGuardado.isBlank() && minutosBan() <= 0, a);
					}
				}
			}
			case NUEVO_MOB, CAMBIO -> {
				etiqueta(g, "5 · EXTRAS", rx, py + 123, a);
				boolean mobs = casilla == Casilla.NUEVO_MOB;
				String base = mobs ? "MOBS NUEVOS" : "CAMBIOS";
				String txt = tarjeta.elegido.isEmpty() ? base : base + " (1)";
				boton(g, "extras", rx, py + 135, RW, 20, Tema.titulo(txt, 0xFFFFFF), Estilo.OSCURO, null,
						() -> abrir(new PantallaTarjetas(this, mobs ? "Mobs nuevos" : "Cambios de dificultad", mobs ? "MOBS NUEVOS" : "CAMBIOS DE DIFICULTAD",
								mobs ? Datos.MOBS : Datos.CAMBIOS, tarjeta, e -> ponerTextoAuto(e == null ? "" : e.texto()))), a);

				if (!tarjeta.girar) {
					g.text(this.font, "Ruleta: NO · el aviso sale sin girar", rx, py + 160, Tema.alfa(Tema.GRIS, a), false);
				}
			}
			default -> {
			}
		}

		// Pie: resumen y LANZAR.
		Tema.rect(g, rx, py + 284, RW, 1, Tema.alfa(Tema.SEPARADOR, a));
		String res = resumen();

		while (this.font.width(res) > RW && res.length() > 4) {
			res = res.substring(0, res.length() - 4) + "...";
		}

		g.text(this.font, res, rx, py + 290, Tema.alfa(Tema.TEXTO, a), false);
		String error = error();
		boton(g, "lanzar", rx, py + 305, RW, 22, Tema.titulo("LANZAR", 0xFFFFFF), Estilo.AZUL, error, error == null ? this::lanzar : null, error == null ? a : a * 0.6f);
	}

	private void opcion(GuiGraphicsExtractor g, String id, int x, int y, int w, int h0, String texto, boolean sel, Runnable accion, float a) {
		boolean hv = encima(x, y, w, h0);
		float h = anim("op." + id, hv ? 1 : 0, 14);
		int fondo = sel ? Tema.mezclar(0xFF3A4D80, 0xFF4560A0, h) : Tema.mezclar(0xFF1C2333, 0xFF222A3D, h);
		int borde = sel ? 0xFF7F9BE6 : Tema.mezclar(0xFF2C3448, 0xFF5C7BC8, h);
		Tema.caja(g, x, y, w, h0, Tema.alfa(fondo, a), Tema.alfa(borde, a));
		int ty = y + (h0 - 8) / 2;
		Tema.rect(g, x + 7, ty + 2, 4, 4, Tema.alfa(sel ? 0xFF9DB6FF : 0xFF8E95A6, a));
		Tema.titulo(texto, sel ? 0xFFFFFF : 0xC9CFDB).dibujar(g, this.font, x + 16, ty, Tema.alfa(0xFFFFFFFF, a), false);
		zona(x, y, w, h0, accion);
	}

	private String error() {
		if (conTemporizador() && segundosTemporizador() == -2) {
			return "El temporizador tiene que ser mm:ss o hh:mm:ss";
		}

		if (casilla == Casilla.SANCION && !castigo.equals("ninguno")) {
			if (castigado.isEmpty()) {
				return "Elige al jugador que recibe el castigo";
			}

			if (castigo.equals("ban") && minutosBan() <= 0) {
				return "Escribe la duración del BAN, por ejemplo 30m o 2h";
			}
		}

		if (mensajeGuardado.isBlank() && tituloGuardado.isBlank()) {
			return "Escribe un título o un mensaje";
		}

		return null;
	}

	private void lanzar() {
		JsonObject o = new JsonObject();
		o.addProperty("tipo", casilla.id);
		o.addProperty("nombre", casilla.nombre);
		o.addProperty("titulo", tituloGuardado.trim());
		o.addProperty("mensaje", mensajeGuardado.trim());
		o.addProperty("para", paraQuienLibre() ? para : "");
		o.addProperty("tamano", mini ? "mini" : "normal");
		o.addProperty("segundos", conTemporizador() ? Math.max(-1, segundosTemporizador()) : -1);
		o.addProperty("girar", casilla != Casilla.CAMBIO && casilla != Casilla.NUEVO_MOB || tarjeta.girar);

		switch (casilla) {
			case NUTRIA -> o.addProperty("nutria", nutria);
			case REVIIL -> {
				o.addProperty("reviil", reviil);
				o.addProperty("ver_mensaje", verMensaje);
				o.addProperty("ver_animacion", verAnimacion);
				o.addProperty("ver_cielo", verCielo);
				JsonArray arr = new JsonArray();
				StringBuilder nombres = new StringBuilder();

				for (Map.Entry<String, PantallaMomentos.Eleccion> e : momentos.entrySet()) {
					JsonObject m = new JsonObject();
					m.addProperty("id", e.getKey());
					m.addProperty("segundos", e.getValue().segundos());
					m.addProperty("num", e.getValue().num());
					m.addProperty("texto", e.getValue().texto());
					arr.add(m);
					MomentosHS.Momento mo = MomentosHS.porId(e.getKey());

					if (mo != null) {
						nombres.append(nombres.isEmpty() ? "" : " · ").append(mo.titulo());
					}
				}

				o.add("momentos", arr);
				o.addProperty("momento", nombres.toString());
			}
			case SANCION -> {
				o.addProperty("castigo", castigo);
				o.addProperty("castigado", castigado);
				o.addProperty("minutos", Math.max(0, minutosBan()));
			}
			case NUEVO_MOB, CAMBIO -> {
				JsonArray arr = new JsonArray();
				Datos.Entrada e = Datos.porId(tarjeta.elegido);

				if (e != null) {
					arr.add(casilla == Casilla.CAMBIO ? e.id() : e.titulo());
				}

				o.add(casilla == Casilla.CAMBIO ? "cambios" : "mobs", arr);
			}
			default -> {
			}
		}

		ClienteEstado.accion("lanzar", o);
		this.minecraft.gui.setScreen(null);
	}
}
