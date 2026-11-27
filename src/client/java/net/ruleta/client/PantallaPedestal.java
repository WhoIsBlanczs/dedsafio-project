package net.ruleta.client;

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

/** "Nutria Pedestal": comercios «das esto y recibes esto». */
public class PantallaPedestal extends PantallaBase {
	private static final int W = 778;
	private static final int H = 468;

	private JsonObject datos;
	private float scroll;
	private boolean shift;

	public PantallaPedestal(Screen anterior) {
		super(Component.literal("Nutria Pedestal"), anterior);
		this.datos = ClienteEstado.pedestal.deepCopy();

		if (!datos.has("comercios")) {
			datos.add("comercios", new JsonArray());
		}
	}

	private JsonArray comercios() {
		return datos.getAsJsonArray("comercios");
	}

	private static String texto(JsonObject o, String k) {
		return o.has(k) ? o.get(k).getAsString() : "";
	}

	private static int entero(JsonObject o, String k) {
		return o.has(k) ? o.get(k).getAsInt() : 1;
	}

	@Override
	protected void dibujar(GuiGraphicsExtractor g, float a) {
		int px = (LIENZO_W - W) / 2;
		int py = (LIENZO_H - H) / 2;
		Tema.panel(g, px, py, W, H, 39, a);
		g.text(this.font, Tema.negrita("Nutria Pedestal"), px + 17, py + 11, Tema.alfa(Tema.BLANCO, a), false);
		g.text(this.font, "Se guarda en el servidor: config/ruleta_pedestal.json", px + 17, py + 24, Tema.alfa(Tema.TEXTO, a), false);
		botonCerrar(g, px + W - 22, py + 15, a);

		boton(g, "nuevo", px + 14, py + 48, 169, 23, Tema.negrita("+ Nuevo comercio"), Estilo.AZUL, null, () -> {
			JsonObject o = new JsonObject();
			o.addProperty("das", "minecraft:emerald");
			o.addProperty("das_nombre", "Emerald");
			o.addProperty("das_n", 1);
			o.addProperty("recibes", "minecraft:diamond");
			o.addProperty("recibes_nombre", "Diamond");
			o.addProperty("recibes_n", 1);
			comercios().add(o);
		}, a);

		int y0 = py + 81;

		for (String l : Tema.lineas(this.font, "Cada comercio es «das esto y recibes esto». Clic en un cuadro de item para cambiarlo; con − y + cambias la cantidad (Mayús = de 10 en 10). El jugador ve estos comercios al hacer clic derecho en el pedestal.", W - 32, 2)) {
			g.text(this.font, l, px + 13, y0, Tema.alfa(Tema.TEXTO, a), false);
			y0 += 11;
		}

		JsonArray arr = comercios();
		int listaY = py + 107;
		int listaH = 320;
		int alto = arr.size() * 74;
		scroll = Math.max(0, Math.min(scroll, Math.max(0, alto - listaH)));
		g.enableScissor(px + 1, listaY, px + W - 1, listaY + listaH);

		for (int i = 0; i < arr.size(); i++) {
			JsonObject o = arr.get(i).getAsJsonObject();
			int x = px + 14;
			int y = listaY + i * 74 - (int) scroll;
			int w = 743;

			if (y + 66 < listaY || y > listaY + listaH) {
				continue;
			}

			int fila = i;
			boolean visible = y >= listaY && y + 66 <= listaY + listaH;
			Tema.caja(g, x, y, w, 66, Tema.alfa(Tema.TARJETA, a), Tema.alfa(Tema.TARJETA_BORDE, a));
			lado(g, "das", "DAS", o, x + 11, y, fila, visible, a);
			g.text(this.font, "→", x + 283, y + 29, Tema.alfa(Tema.GRIS, a), false);
			lado(g, "recibes", "RECIBES", o, x + 319, y, fila, visible, a);

			boolean hv = encima(x + 700, y + 8, 20, 20);
			float h = anim("x" + i, hv ? 1 : 0, 16);
			Tema.caja(g, x + 700, y + 8, 20, 20, Tema.alfa(Tema.mezclar(0xFF1C2230, 0xFF4A2433, h), a), Tema.alfa(Tema.mezclar(0xFF2D3344, 0xFFB5506E, h), a));
			g.text(this.font, "×", x + 707, y + 14, Tema.alfa(Tema.mezclar(Tema.TEXTO, Tema.ROJO_TEXTO, h), a), false);

			if (visible) {
				zona(x + 700, y + 8, 20, 20, () -> comercios().remove(fila));
			}
		}

		g.disableScissor();

		if (arr.isEmpty()) {
			Tema.textoCentrado(g, this.font, Component.literal("No hay comercios. Pulsa «+ Nuevo comercio»."), px + W / 2, listaY + 40, Tema.alfa(Tema.GRIS, a), false);
		}

		boton(g, "orig", px + 527, py + 434, 130, 25, Tema.negrita("Valores originales"), Estilo.ROJO,
				"Vuelve a poner los comercios originales (no se guarda hasta pulsar Guardar)", () -> datos = Servidor.pedestalPorDefecto(), a);
		boton(g, "guardar", px + 666, py + 434, 98, 25, Tema.negrita("Guardar"), Estilo.AZUL, null, () -> {
			ClienteEstado.accion("guardar_pedestal", datos);
			ClienteEstado.pedestal = datos.deepCopy();
			onClose();
		}, a);
	}

	private void lado(GuiGraphicsExtractor g, String clave, String etiqueta, JsonObject o, int x, int y, int fila, boolean visible, float a) {
		g.text(this.font, etiqueta, x, y + 4, Tema.alfa(Tema.GRIS_OSCURO, a), false);
		int bx = x;
		int by = y + 21;
		boolean hv = encima(bx, by, 36, 35);
		float h = anim(clave + fila, hv ? 1 : 0, 14);
		Tema.caja(g, bx, by, 36, 35, Tema.alfa(Tema.mezclar(0xFF141A24, 0xFF1E2840, h), a), Tema.alfa(Tema.mezclar(Tema.TARJETA_BORDE, Tema.AZUL_BORDE, h), a));
		String id = texto(o, clave);
		ItemStack s = Objetos.stack(id);

		if (!s.isEmpty()) {
			g.item(s, bx + 10, by + 10);
		} else {
			g.text(this.font, "?", bx + 16, by + 14, Tema.alfa(0xFFEEA632, a), false);
		}

		if (hv) {
			tooltip("Clic para cambiar el item");
		}

		if (visible) {
			zona(bx, by, 36, 35, () -> abrir(new PantallaSelectorItem(this, item -> {
				JsonObject e = comercios().get(fila).getAsJsonObject();
				e.addProperty(clave, Objetos.id(item));
				e.addProperty(clave + "_nombre", new ItemStack(item).getHoverName().getString());
			})));
		}

		g.text(this.font, Objetos.nombre(id, texto(o, clave + "_nombre")), x + 46, y + 22, Tema.alfa(Tema.BLANCO, a), false);
		int n = entero(o, clave + "_n");
		int my = y + 37;
		mini(g, clave + "m" + fila, x + 47, my, "−", visible, () -> o.addProperty(clave + "_n", Math.max(1, n - (shift ? 10 : 1))), a);
		Tema.textoCentrado(g, this.font, Component.literal("×" + n), x + 91, my + 5, Tema.alfa(Tema.TEXTO, a), false);
		mini(g, clave + "p" + fila, x + 119, my, "+", visible, () -> o.addProperty(clave + "_n", Math.min(6400, n + (shift ? 10 : 1))), a);
	}

	private void mini(GuiGraphicsExtractor g, String clave, int x, int y, String txt, boolean visible, Runnable accion, float a) {
		boolean hv = encima(x, y, 19, 18);
		float h = anim(clave, hv ? 1 : 0, 16);
		Tema.caja(g, x, y, 19, 18, Tema.alfa(Tema.mezclar(Tema.CONTROL, 0xFF34405A, h), a), Tema.alfa(Tema.mezclar(0xFF2D3344, 0xFF5C7BC8, h), a));
		g.text(this.font, txt, x + 10 - this.font.width(txt) / 2, y + 5, Tema.alfa(Tema.mezclar(Tema.TEXTO, 0xFFFFFFFF, h), a), false);

		if (visible) {
			zona(x, y, 19, 18, accion);
		}
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		shift = event.hasShiftDown();
		return super.mouseClicked(event, doubleClick);
	}

	@Override
	protected boolean desplazar(double mx, double my, double cantidad) {
		scroll -= (float) cantidad * 40;
		return true;
	}
}
