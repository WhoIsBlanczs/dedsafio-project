package net.ruleta.client;

import java.util.Locale;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import net.ruleta.Servidor;
import net.ruleta.client.ui.PantallaBase;
import net.ruleta.client.ui.Tema;

/** "Casino": rodillos de la tragamonedas (frecuencias), premios y cobro. */
public class PantallaCasino extends PantallaBase {
	private static final int W = 778;
	private static final int H = 468;
	private static final int MAX_ITEMS = 8;

	private JsonObject datos;
	private int pestana;
	private boolean shift;

	public PantallaCasino(Screen anterior) {
		super(Component.literal("Casino"), anterior);
		this.datos = ClienteEstado.casino.deepCopy();
		asegurar();
	}

	private void asegurar() {
		if (!datos.has("rodillos")) {
			datos.add("rodillos", new JsonArray());
		}

		if (!datos.has("premios")) {
			datos.add("premios", new JsonArray());
		}

		if (!datos.has("cobro")) {
			datos.add("cobro", Servidor.casinoPorDefecto().getAsJsonObject("cobro"));
		}
	}

	private JsonArray rodillos() {
		return datos.getAsJsonArray("rodillos");
	}

	private JsonArray premios() {
		return datos.getAsJsonArray("premios");
	}

	private static int entero(JsonObject o, String k, int def) {
		return o.has(k) ? o.get(k).getAsInt() : def;
	}

	private static String texto(JsonObject o, String k) {
		return o.has(k) ? o.get(k).getAsString() : "";
	}

	private int paso() {
		return shift ? 10 : 1;
	}

	@Override
	protected void dibujar(GuiGraphicsExtractor g, float a) {
		int px = (LIENZO_W - W) / 2;
		int py = (LIENZO_H - H) / 2;
		Tema.panel(g, px, py, W, H, 39, a);
		g.text(this.font, Tema.negrita("Casino"), px + 17, py + 11, Tema.alfa(Tema.BLANCO, a), false);
		g.text(this.font, "Se guarda en el servidor: config/ruleta_tragamonedas.json", px + 17, py + 24, Tema.alfa(Tema.TEXTO, a), false);
		botonCerrar(g, px + W - 22, py + 15, a);

		String[] pestanas = {"Rodillos", "Premios", "Cobro"};

		for (int i = 0; i < pestanas.length; i++) {
			int idx = i;
			boton(g, "tab" + i, px + 14 + i * 115, py + 48, 109, 23, Tema.negrita(pestanas[i]), pestana == i ? Estilo.PESTANA_ACTIVA : Estilo.PESTANA, null, () -> pestana = idx, a);
		}

		switch (pestana) {
			case 0 -> rodillosTab(g, px, py, a);
			case 1 -> premiosTab(g, px, py, a);
			default -> cobroTab(g, px, py, a);
		}

		boton(g, "orig", px + 527, py + 434, 130, 25, Tema.negrita("Valores originales"), Estilo.ROJO,
				"Vuelve a poner los rodillos, premios y cobro originales (no se guarda hasta pulsar Guardar)", () -> datos = Servidor.casinoPorDefecto(), a);
		boton(g, "guardar", px + 666, py + 434, 98, 25, Tema.negrita("Guardar"), Estilo.AZUL, null, () -> {
			ClienteEstado.accion("guardar_casino", datos);
			ClienteEstado.casino = datos.deepCopy();
			onClose();
		}, a);
	}

	private void descripcion(GuiGraphicsExtractor g, String texto, int px, int py, float a) {
		int y = py + 81;

		for (String l : Tema.lineas(this.font, texto, W - 32, 2)) {
			g.text(this.font, l, px + 13, y, Tema.alfa(Tema.TEXTO, a), false);
			y += 11;
		}
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
			tooltip("Clic para cambiar el item");
		}

		zona(x, y, 28, 28, cambiar);
	}

	private void cuadroMini(GuiGraphicsExtractor g, String clave, int x, int y, String txt, boolean rojo, Runnable accion, float a) {
		boolean hv = encima(x, y, 20, 20);
		float h = anim(clave, hv ? 1 : 0, 16);
		int fondo = rojo ? Tema.mezclar(0xFF2A1C26, 0xFF4A2433, h) : Tema.mezclar(Tema.CONTROL, 0xFF34405A, h);
		int borde = rojo ? Tema.mezclar(0xFF4A2B38, 0xFFB5506E, h) : Tema.mezclar(0xFF2D3344, 0xFF5C7BC8, h);
		Tema.caja(g, x, y, 20, 20, Tema.alfa(fondo, a), Tema.alfa(borde, a));
		int c = rojo ? Tema.ROJO_TEXTO : Tema.mezclar(Tema.TEXTO, 0xFFFFFFFF, h);
		g.text(this.font, txt, x + 10 - this.font.width(txt) / 2, y + 6, Tema.alfa(c, a), false);
		zona(x, y, 20, 20, accion);
	}

	private void rodillosTab(GuiGraphicsExtractor g, int px, int py, float a) {
		JsonArray rod = rodillos();
		boolean lleno = rod.size() >= MAX_ITEMS;
		boton(g, "agregar", px + 382, py + 48, 148, 23, Tema.negrita("+ Agregar item"), Estilo.AZUL, lleno ? "Máximo 8 items" : null, lleno ? null : () ->
				abrir(new PantallaSelectorItem(this, item -> {
					JsonObject o = new JsonObject();
					o.addProperty("item", Objetos.id(item));
					o.addProperty("nombre", new ItemStack(item).getHoverName().getString());
					o.addProperty("frecuencia", 10);
					rodillos().add(o);
				})), a);
		descripcion(g, "Estos son los items que giran en los rodillos. «Frecuencia» decide qué tan seguido sale cada uno: a más frecuencia, más veces aparece. El porcentaje de la derecha es lo que de verdad le toca. Máximo 8 items.", px, py, a);

		int total = 0;

		for (int i = 0; i < rod.size(); i++) {
			total += Math.max(0, entero(rod.get(i).getAsJsonObject(), "frecuencia", 0));
		}

		for (int i = 0; i < rod.size(); i++) {
			JsonObject o = rod.get(i).getAsJsonObject();
			int x = px + 14;
			int y = py + 116 + i * 46;
			int w = 743;
			boolean hv = encima(x, y, w, 39);
			float h = anim("fila" + i, hv ? 1 : 0, 14);
			Tema.caja(g, x, y, w, 39, Tema.alfa(Tema.TARJETA, a), Tema.alfa(Tema.mezclar(Tema.TARJETA_BORDE, Tema.TARJETA_BORDE_HOVER, h), a));

			String id = texto(o, "item");
			int fila = i;
			boolean iconoHv = encima(x + 9, y + 9, 20, 20);
			icono(g, id, x + 11, y + 11, a);

			if (iconoHv) {
				tooltip("Clic para cambiar el item");
			}

			zona(x + 9, y + 9, 20, 20, () -> abrir(new PantallaSelectorItem(this, item -> {
				JsonObject e = rodillos().get(fila).getAsJsonObject();
				e.addProperty("item", Objetos.id(item));
				e.addProperty("nombre", new ItemStack(item).getHoverName().getString());
			})));

			g.text(this.font, Tema.negrita(Objetos.nombre(id, texto(o, "nombre"))), x + 37, y + 8, Tema.alfa(Tema.BLANCO, a), false);
			g.text(this.font, id, x + 37, y + 20, Tema.alfa(Tema.GRIS, a), false);

			int f = entero(o, "frecuencia", 0);
			double pct = total > 0 ? f * 100.0 / total : 0;
			g.text(this.font, "Sale en " + String.format(Locale.ROOT, "%.1f", pct) + " % de los giros de cada rodillo", x + 259, y + 15, Tema.alfa(Tema.AZUL_CLARO, a), false);
			g.text(this.font, "Frecuencia", x + 542, y + 15, Tema.alfa(Tema.GRIS, a), false);
			cuadroMini(g, "m" + i, x + 600, y + 9, "−", false, () -> o.addProperty("frecuencia", Math.max(1, f - paso())), a);
			Tema.textoCentrado(g, this.font, Component.literal(Integer.toString(f)), x + 651, y + 15, Tema.alfa(Tema.BLANCO, a), false);
			cuadroMini(g, "p" + i, x + 683, y + 9, "+", false, () -> o.addProperty("frecuencia", Math.min(999, f + paso())), a);
			cuadroMini(g, "x" + i, x + 714, y + 9, "×", true, () -> rodillos().remove(fila), a);
		}
	}

	private void premiosTab(GuiGraphicsExtractor g, int px, int py, float a) {
		JsonArray pre = premios();
		boton(g, "agregarp", px + 382, py + 48, 148, 23, Tema.negrita("+ Agregar premio"), Estilo.AZUL, null, () -> {
			JsonObject o = new JsonObject();
			JsonObject primero = rodillos().isEmpty() ? null : rodillos().get(0).getAsJsonObject();
			String sim = primero != null ? texto(primero, "item") : "minecraft:diamond";
			o.addProperty("simbolo", sim);
			o.addProperty("simbolo_nombre", primero != null ? texto(primero, "nombre") : "Diamond");
			o.addProperty("item", "minecraft:diamond");
			o.addProperty("nombre", "Diamond");
			o.addProperty("cantidad", 1);
			pre.add(o);
		}, a);
		descripcion(g, "Tres símbolos iguales en la línea dan el premio de su fila. Clic en un cuadro de item para cambiarlo; con − y + cambias la cantidad (Mayús = de 10 en 10).", px, py, a);

		for (int i = 0; i < pre.size() && i < 6; i++) {
			JsonObject o = pre.get(i).getAsJsonObject();
			int x = px + 14;
			int y = py + 116 + i * 46;
			int w = 743;
			int fila = i;
			Tema.caja(g, x, y, w, 39, Tema.alfa(Tema.TARJETA, a), Tema.alfa(Tema.TARJETA_BORDE, a));
			cajaItem(g, "ps" + i, x + 9, y + 5, texto(o, "simbolo"), () -> abrir(new PantallaSelectorItem(this, item -> {
				JsonObject e = premios().get(fila).getAsJsonObject();
				e.addProperty("simbolo", Objetos.id(item));
				e.addProperty("simbolo_nombre", new ItemStack(item).getHoverName().getString());
			})), a);
			g.text(this.font, Tema.negrita("3 × " + Objetos.nombre(texto(o, "simbolo"), texto(o, "simbolo_nombre"))), x + 47, y + 15, Tema.alfa(Tema.BLANCO, a), false);
			g.text(this.font, "→", x + 320, y + 15, Tema.alfa(Tema.GRIS, a), false);
			cajaItem(g, "pi" + i, x + 352, y + 5, texto(o, "item"), () -> abrir(new PantallaSelectorItem(this, item -> {
				JsonObject e = premios().get(fila).getAsJsonObject();
				e.addProperty("item", Objetos.id(item));
				e.addProperty("nombre", new ItemStack(item).getHoverName().getString());
			})), a);
			g.text(this.font, Objetos.nombre(texto(o, "item"), texto(o, "nombre")), x + 390, y + 15, Tema.alfa(Tema.TEXTO, a), false);
			int n = entero(o, "cantidad", 1);
			cuadroMini(g, "pm" + i, x + 600, y + 9, "−", false, () -> o.addProperty("cantidad", Math.max(1, n - paso())), a);
			Tema.textoCentrado(g, this.font, Component.literal("×" + n), x + 651, y + 15, Tema.alfa(Tema.BLANCO, a), false);
			cuadroMini(g, "pp" + i, x + 683, y + 9, "+", false, () -> o.addProperty("cantidad", Math.min(6400, n + paso())), a);
			cuadroMini(g, "px" + i, x + 714, y + 9, "×", true, () -> premios().remove(fila), a);
		}
	}

	private void cobroTab(GuiGraphicsExtractor g, int px, int py, float a) {
		JsonObject c = datos.getAsJsonObject("cobro");
		descripcion(g, "Lo que cuesta cada tirada de la tragamonedas. Clic en el cuadro de item para cambiarlo; con − y + cambias la cantidad (Mayús = de 10 en 10).", px, py, a);
		int x = px + 14;
		int y = py + 116;
		int w = 743;
		Tema.caja(g, x, y, w, 39, Tema.alfa(Tema.TARJETA, a), Tema.alfa(Tema.TARJETA_BORDE, a));
		Tema.titulo("CADA TIRADA CUESTA", Tema.GRIS).dibujar(g, this.font, x + 13, y + 15, Tema.alfa(0xFFFFFFFF, a), false);
		cajaItem(g, "cobro", x + 200, y + 5, texto(c, "item"), () -> abrir(new PantallaSelectorItem(this, item -> {
			JsonObject e = datos.getAsJsonObject("cobro");
			e.addProperty("item", Objetos.id(item));
			e.addProperty("nombre", new ItemStack(item).getHoverName().getString());
		})), a);
		g.text(this.font, Tema.negrita(Objetos.nombre(texto(c, "item"), texto(c, "nombre"))), x + 238, y + 15, Tema.alfa(Tema.BLANCO, a), false);
		int n = entero(c, "cantidad", 1);
		cuadroMini(g, "cm", x + 600, y + 9, "−", false, () -> c.addProperty("cantidad", Math.max(1, n - paso())), a);
		Tema.textoCentrado(g, this.font, Component.literal("×" + n), x + 651, y + 15, Tema.alfa(Tema.BLANCO, a), false);
		cuadroMini(g, "cp", x + 683, y + 9, "+", false, () -> c.addProperty("cantidad", Math.min(6400, n + paso())), a);
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		shift = event.hasShiftDown();
		return super.mouseClicked(event, doubleClick);
	}
}
