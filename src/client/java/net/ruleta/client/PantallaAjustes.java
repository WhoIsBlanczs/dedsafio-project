package net.ruleta.client;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.gson.JsonObject;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import net.ruleta.Ajustes;
import net.ruleta.client.ui.PantallaBase;
import net.ruleta.client.ui.Tema;

/** Pantalla "Ajustes": categorías a la izquierda, tarjetas con deslizadores e interruptores a la derecha. */
public class PantallaAjustes extends PantallaBase {
	private static final int W = 738;
	private static final int H = 438;
	private static final int TARJETA_H = 47;
	private static final int PASO = 56;

	private int categoria;
	private EditBox buscar;
	private String busqueda = "";
	private float scroll;
	private float scrollVisual;
	private final Map<String, Double> locales = new HashMap<>();
	private Ajustes.Def arrastrando;
	private int pistaX;
	private int pistaW;

	public PantallaAjustes(Screen anterior) {
		super(Component.literal("Ajustes"), anterior);
	}

	private int px() {
		return (LIENZO_W - W) / 2;
	}

	private int py() {
		return (LIENZO_H - H) / 2;
	}

	@Override
	protected void iniciar() {
		buscar = new EditBox(this.font, px() + 185, py() + 56, 534, 10, Component.literal("Buscar"));
		buscar.setBordered(false);
		buscar.setMaxLength(60);
		buscar.setHint(Component.literal("Buscar un ajuste...").withColor(0xD4D8E0));
		buscar.setValue(busqueda);
		buscar.setResponder(s -> {
			busqueda = s;
			scroll = 0;
			cambioContenido();
		});
		campoTexto(buscar, px() + 179, py() + 50, 546, 20);
		fijarAnim("seleccion", categoria);
	}

	private void cambioContenido() {
		fijarAnim("contenido", 0);
	}

	private static String normal(String s) {
		return Normalizer.normalize(s.toLowerCase(Locale.ROOT), Normalizer.Form.NFD).replaceAll("\\p{M}", "");
	}

	private List<Ajustes.Def> visibles() {
		if (busqueda.isBlank()) {
			return Ajustes.categorias().get(categoria).ajustes();
		}

		String q = normal(busqueda.trim());
		List<Ajustes.Def> r = new ArrayList<>();

		for (Ajustes.Def d : Ajustes.todos().values()) {
			if (normal(d.nombre()).contains(q) || normal(d.descripcion()).contains(q) || normal(d.categoria()).contains(q)) {
				r.add(d);
			}
		}

		return r;
	}

	private double valor(Ajustes.Def d) {
		Double l = locales.get(d.id());
		return l != null ? l : ClienteEstado.valor(d.id());
	}

	private void enviar(Ajustes.Def d, double v) {
		v = d.ajustar(v);
		locales.put(d.id(), v);
		JsonObject o = new JsonObject();
		o.addProperty("id", d.id());
		o.addProperty("valor", v);
		ClienteEstado.accion("ajuste", o);
	}

	@Override
	protected void dibujar(GuiGraphicsExtractor g, float a) {
		int px = px();
		int py = py();
		buscar.visible = a > 0.6f;

		// Limpia valores locales que el servidor ya confirmó.
		locales.entrySet().removeIf(e -> arrastrando == null && Math.abs(ClienteEstado.valor(e.getKey()) - e.getValue()) < 1e-6);

		Tema.panel(g, px, py, W, H, 40, a);

		// Cabecera.
		Tema.caja(g, px + 14, py + 8, 23, 23, Tema.alfa(0xFF405078, a), Tema.alfa(0xFF5A6C9C, a));
		g.text(this.font, "⚙", px + 22, py + 16, Tema.alfa(0xFFFFFFFF, a), false);
		g.text(this.font, Tema.negrita("Ajustes"), px + 44, py + 11, Tema.alfa(Tema.BLANCO, a), false);
		g.text(this.font, "Los cambios se aplican al instante y se guardan en el servidor", px + 44, py + 24, Tema.alfa(Tema.TEXTO, a), false);
		botonCerrar(g, px + W - 22, py + 15, a);

		// Barra lateral.
		List<Ajustes.Categoria> cats = Ajustes.categorias();
		float sel = anim("seleccion", categoria, 18);
		int selY = py + 52 + Math.round(sel * 30);
		float alfaSel = busqueda.isBlank() ? 1 : 0.35f;
		Tema.rect(g, px + 14, selY, 149, 23, Tema.alfa(Tema.SELECCION, a * alfaSel));
		Tema.rect(g, px + 14, selY + 1, 2, 21, Tema.alfa(Tema.AZUL_CLARO, a * alfaSel));

		for (int i = 0; i < cats.size(); i++) {
			int y = py + 52 + i * 30;
			boolean activa = i == categoria && busqueda.isBlank();
			boolean hv = encima(px + 14, y, 149, 23);
			float h = anim("cat." + i, hv && !activa ? 1 : 0, 14);

			if (h > 0) {
				Tema.rect(g, px + 14, y, 149, 23, Tema.alfa(0xFF1A1F29, a * h));
			}

			Component nombre = activa ? Tema.negrita(cats.get(i).nombre()) : Component.literal(cats.get(i).nombre());
			g.text(this.font, nombre, px + 24, y + 8, Tema.alfa(activa ? Tema.BLANCO : Tema.mezclar(Tema.GRIS, Tema.TEXTO, h), a), false);
			int idx = i;
			zona(px + 14, y, 149, 23, () -> {
				if (categoria != idx || !busqueda.isBlank()) {
					categoria = idx;
					scroll = 0;
					scrollVisual = 0;
					buscar.setValue("");
					busqueda = "";
					cambioContenido();
				}
			});
		}

		Tema.rect(g, px + 170, py + 46, 1, H - 56, Tema.alfa(Tema.SEPARADOR, a));

		// Buscador.
		int cx = px + 179;
		int cw = 546;
		boolean foco = buscar.isFocused();
		Tema.caja(g, cx, py + 50, cw, 20, Tema.alfa(foco ? 0xFF1C2742 : Tema.CAMPO, a), Tema.alfa(foco ? 0xFF7F9BE6 : Tema.CAMPO_BORDE, a));

		// Tarjetas.
		List<Ajustes.Def> lista = visibles();
		int listaY = py + 76;
		int listaH = 298;
		int alto = lista.size() * PASO - (PASO - TARJETA_H);
		float maxScroll = Math.max(0, alto - listaH);
		scroll = Math.max(0, Math.min(scroll, maxScroll));
		scrollVisual += (scroll - scrollVisual) * (1 - (float) Math.exp(-20 * dt));
		float cont = anim("contenido", 1, 12);
		int desliz = Math.round((1 - cont) * 14);
		float ac = a * cont;

		g.enableScissor(cx, listaY, cx + cw, listaY + listaH);

		for (int i = 0; i < lista.size(); i++) {
			int y = listaY + i * PASO - Math.round(scrollVisual);

			if (y + TARJETA_H < listaY || y > listaY + listaH) {
				continue;
			}

			tarjeta(g, lista.get(i), cx + desliz, y, 537, listaY, listaH, ac);
		}

		if (lista.isEmpty()) {
			Tema.textoCentrado(g, this.font, Component.literal("No hay ajustes que coincidan con la búsqueda."), cx + cw / 2, listaY + 40, Tema.alfa(Tema.GRIS, a), false);
		}

		g.disableScissor();

		// Barra de desplazamiento.
		if (maxScroll > 0) {
			int sbX = cx + 542;
			Tema.rect(g, sbX, listaY, 3, listaH, Tema.alfa(0xFF1C202D, a));
			int th = Math.max(20, Math.round(listaH * (listaH / (float) alto)));
			int ty = listaY + Math.round((listaH - th) * (scrollVisual / maxScroll));
			Tema.rect(g, sbX, ty, 3, th, Tema.alfa(0xFF8993A3, a));
		}

		// Pie.
		g.text(this.font, "Un punto azul = distinto del original", cx, py + 376, Tema.alfa(Tema.GRIS, a), false);
		String cat = cats.get(categoria).nombre();
		boton(g, "seccion", px + 189, py + 400, 128, 24, Tema.negrita("Restablecer sección"), Estilo.TEXTO,
				"Devuelve los ajustes de esta sección a sus valores originales", () -> {
					JsonObject o = new JsonObject();
					o.addProperty("categoria", cat);
					locales.clear();
					ClienteEstado.accion("restablecer", o);
				}, a);
		boton(g, "todo", px + 338, py + 400, 129, 24, Tema.negrita("Restablecer todo"), Estilo.ROJO,
				"Devuelve todos los ajustes a sus valores originales", () -> {
					locales.clear();
					ClienteEstado.accion("restablecer", new JsonObject());
				}, a);
		boton(g, "listo", px + 616, py + 400, 109, 24, Tema.negrita("Listo"), Estilo.AZUL, null, this::onClose, a);
	}

	private void tarjeta(GuiGraphicsExtractor g, Ajustes.Def d, int x, int y, int w, int listaY, int listaH, float a) {
		boolean visibleRaton = ratonY >= listaY && ratonY < listaY + listaH;
		boolean hv = visibleRaton && encima(x, y, w, TARJETA_H) || arrastrando == d;
		float h = anim("tar." + d.id(), hv ? 1 : 0, 14);
		Tema.caja(g, x, y, w, TARJETA_H, Tema.alfa(Tema.TARJETA, a), Tema.alfa(Tema.mezclar(Tema.TARJETA_BORDE, Tema.TARJETA_BORDE_HOVER, h), a));

		double v = valor(d);
		boolean distinto = Math.abs(v - d.porDefecto()) > 1e-6;

		if (distinto) {
			Tema.rect(g, x + 6, y + 9, 4, 4, Tema.alfa(Tema.AZUL_CLARO, a));
		}

		g.text(this.font, Tema.negrita(d.nombre()), x + 13, y + 7, Tema.alfa(Tema.BLANCO, a), false);
		Tema.textoDerecha(g, this.font, Component.literal(d.formatear(v)), x + w - 12, y + 7, Tema.alfa(Tema.BLANCO, a));

		List<String> desc = Tema.lineas(this.font, d.descripcion(), 323, 2);

		for (int i = 0; i < desc.size(); i++) {
			g.text(this.font, desc.get(i), x + 13, y + 19 + i * 11, Tema.alfa(Tema.GRIS, a), false);
		}

		int cy = Math.max(y, listaY);
		int ch = Math.min(y + TARJETA_H, listaY + listaH) - cy;

		if (d.tipo() == Ajustes.Tipo.INTERRUPTOR) {
			boolean on = v >= 0.5;
			float t = anim("tog." + d.id(), on ? 1 : 0, 16);
			int tx = x + w - 41;
			int ty = y + 27;
			Tema.caja(g, tx, ty, 28, 14, Tema.alfa(Tema.mezclar(Tema.CONTROL, Tema.AZUL, t), a), Tema.alfa(Tema.mezclar(Tema.CONTROL_BORDE, Tema.AZUL_BORDE, t), a));
			int kx = tx + 3 + Math.round(t * 14);
			Tema.caja(g, kx, ty + 3, 8, 8, Tema.alfa(Tema.mezclar(0xFF949EB2, 0xFFFFFFFF, t), a), Tema.alfa(Tema.mezclar(0xFF949EB2, 0xFFFFFFFF, t), a));

			if (ch > 0) {
				zona(x, cy, w, ch, () -> enviar(d, on ? 0 : 1));
			}

			return;
		}

		// − deslizador +
		int mx = x + 350;
		int bY = y + 27;
		botonMini(g, "m." + d.id(), mx, bY, "−", a);
		int px0 = x + 373;
		int pw = 128;
		double t = (v - d.min()) / (d.max() - d.min());
		int relleno = (int) Math.round(pw * Math.max(0, Math.min(1, t)));
		Tema.rect(g, px0, bY + 6, pw, 2, Tema.alfa(Tema.PISTA, a));
		Tema.rect(g, px0, bY + 6, relleno, 2, Tema.alfa(Tema.AZUL_CLARO, a));
		int kx = px0 + relleno - 3;
		Tema.caja(g, kx, bY + 3, 7, 8, Tema.alfa(0xFFFCFCFC, a), Tema.alfa(0xFFFCFCFC, a));
		botonMini(g, "p." + d.id(), x + 510, bY, "+", a);

		if (ch > 0 && bY >= listaY && bY + 14 <= listaY + listaH) {
			zona(mx, bY, 14, 14, () -> enviar(d, v - d.paso() * (hasShiftDown() ? 10 : 1)));
			zona(x + 510, bY, 14, 14, () -> enviar(d, v + d.paso() * (hasShiftDown() ? 10 : 1)));
			zonaSilenciosa(px0 - 4, bY, pw + 8, 14, () -> {
				arrastrando = d;
				pistaX = px0;
				pistaW = pw;
				moverDeslizador(ratonX);
			});
		}
	}

	private boolean shift;

	private boolean hasShiftDown() {
		return shift;
	}

	private void botonMini(GuiGraphicsExtractor g, String id, int x, int y, String txt, float a) {
		boolean hv = encima(x, y, 14, 14);
		float h = anim(id, hv ? 1 : 0, 16);
		Tema.caja(g, x, y, 14, 14, Tema.alfa(Tema.mezclar(Tema.CONTROL, 0xFF34405A, h), a), Tema.alfa(Tema.mezclar(0xFF2D3344, 0xFF5C7BC8, h), a));
		g.text(this.font, txt, x + 7 - this.font.width(txt) / 2, y + 3, Tema.alfa(Tema.mezclar(Tema.TEXTO, 0xFFFFFFFF, h), a), false);
	}

	private void moverDeslizador(double mouseX) {
		if (arrastrando == null) {
			return;
		}

		double t = Math.max(0, Math.min(1, (mouseX - pistaX) / pistaW));
		double v = arrastrando.ajustar(arrastrando.min() + t * (arrastrando.max() - arrastrando.min()));
		locales.put(arrastrando.id(), v);
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		shift = event.hasShiftDown();
		return super.mouseClicked(event, doubleClick);
	}

	@Override
	public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
		if (arrastrando != null) {
			moverDeslizador(lienzoX(event.x()));
			return true;
		}

		return super.mouseDragged(event, dx, dy);
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		if (arrastrando != null) {
			Ajustes.Def d = arrastrando;
			arrastrando = null;
			enviar(d, valor(d));
			sonidoClic();
			return true;
		}

		return super.mouseReleased(event);
	}

	@Override
	protected boolean desplazar(double mx, double my, double cantidad) {
		scroll -= (float) cantidad * 40;
		return true;
	}
}
