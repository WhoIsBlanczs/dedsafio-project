package net.ruleta.client;

import com.google.gson.JsonObject;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import net.ruleta.RuletaMod;
import net.ruleta.client.ui.PantallaBase;
import net.ruleta.client.ui.Tema;

/** La ruleta que se abre con la G: 8 casillas y la barra de botones de abajo. */
public class PantallaRuleta extends PantallaBase {
	private static final Identifier DISCO = RuletaMod.id("textures/ruleta/disco.png");
	private static final int TEX_W = 688;
	private static final int TEX_H = 888;
	private static final int TEX_RADIO = 880;

	private Casilla hover;

	public PantallaRuleta() {
		super(Component.literal("Elige una ruleta"), null);
	}

	private int centroX() {
		return LIENZO_W / 2;
	}

	private int centroY() {
		return 340;
	}

	private float radio() {
		return 284 * (0.9f + 0.1f * entrada());
	}

	private Casilla casillaEn(double mx, double my) {
		double dx = mx - centroX();
		double dy = my - centroY();
		double d = Math.sqrt(dx * dx + dy * dy);
		float r = radio();

		if (d > r * 1.04 || d < r * 0.06) {
			return null;
		}

		double ang = Math.toDegrees(Math.atan2(dx, -dy));

		if (ang < 0) {
			ang += 360;
		}

		int i = (int) Math.floor((ang + 22.5) / 45.0) % 8;
		return Casilla.values()[i];
	}

	@Override
	protected void dibujar(GuiGraphicsExtractor g, float a) {
		hover = casillaEn(ratonX, ratonY);

		Tema.textoCentrado(g, this.font, this.title, LIENZO_W / 2, 11, Tema.alfa(0xFFD8DCE4, a), true);

		int cx = centroX();
		int cy = centroY();
		float r = radio();
		float escala = r / TEX_RADIO;

		for (Casilla c : Casilla.values()) {
			if (c != hover) {
				dibujarCasilla(g, c, cx, cy, r, escala, a);
			}
		}

		if (hover != null) {
			dibujarCasilla(g, hover, cx, cy, r, escala, a);
		}

		// Centro oscuro donde se juntan los separadores.
		int rc = Math.max(3, Math.round(r * 0.055f));
		g.blit(RenderPipelines.GUI_TEXTURED, DISCO, cx - rc, cy - rc, 0, 0, rc * 2, rc * 2, 1536, 1536, 1536, 1536, Tema.alfa(0xFF231A10, a));
		int rc2 = Math.max(2, rc - 2);
		g.blit(RenderPipelines.GUI_TEXTURED, DISCO, cx - rc2, cy - rc2, 0, 0, rc2 * 2, rc2 * 2, 1536, 1536, 1536, 1536, Tema.alfa(0xFF203A22, a));

		if (hover != null) {
			zona((int) (cx - r), (int) (cy - r), (int) (r * 2), (int) (r * 2), () -> {
				Casilla c = casillaEn(ratonX, ratonY);

				if (c != null) {
					abrir(new PantallaLanzar(c, this));
				}
			});
		}

		dibujarBarra(g, a);
	}

	private void dibujarCasilla(GuiGraphicsExtractor g, Casilla c, int cx, int cy, float r, float escala, float a) {
		float hv = anim("casilla." + c.id, c == hover ? 1 : 0, 16);
		float sale = r * 0.09f * hv;
		float ang = (float) Math.toRadians(c.ordinal() * 45.0);
		int color = Tema.alfa(Tema.mezclar(0xFFF2F2F2, 0xFFFFFFFF, hv), a);

		g.pose().pushMatrix();
		g.pose().translate(cx, cy);
		g.pose().rotate(ang);
		g.pose().translate(0, -sale);
		g.pose().scale(escala, escala);
		g.blit(RenderPipelines.GUI_TEXTURED, c.textura, -TEX_W / 2, -TEX_RADIO, 0, 0, TEX_W, TEX_H, TEX_W, TEX_H, color);

		float brillo = 0.55f + 0.45f * (float) Math.sin(tiempo() * 2.2f + c.ordinal() * 0.9f);
		brillo = Math.min(1, brillo + hv * 0.4f);
		g.blit(RenderPipelines.GUI_TEXTURED, c.destellos, -TEX_W / 2, -TEX_RADIO, 0, 0, TEX_W, TEX_H, TEX_W, TEX_H, Tema.alfa(0xFFFFFFFF, a * brillo));
		g.pose().popMatrix();
	}

	private void dibujarBarra(GuiGraphicsExtractor g, float a) {
		int w = 117;
		int h = 21;
		int sep = 9;
		int total = w * 5 + sep * 4;
		int x = (LIENZO_W - total) / 2;
		int y = 671;

		if (hover != null) {
			Tema.centrado(g, this.font, hover.rotulo(), LIENZO_W / 2, 656, Tema.alfa(0xFFFFFFFF, a), true);
		}

		botonBarra(g, "ajustes", x, y, w, h, "Ajustes", false, "Daños, tiempos, rayos, distancias... todo lo configurable",
				() -> abrir(new PantallaAjustes(this)), a);
		botonBarra(g, "cambios", x + (w + sep), y, w, h, "Cambios activos", false, "Los cambios de dificultad que hay ahora en el mundo",
				() -> abrir(new PantallaCambiosActivos(this)), a);
		botonBarra(g, "bunkers", x + (w + sep) * 2, y, w, h, "Bunkers", false, "Ver los bunkers y demolerlos",
				() -> abrir(new PantallaBunkers(this)), a);
		botonBarra(g, "panel", x + (w + sep) * 3, y, w, h, "Panel", false, "Opciones para configurar el casino y lo que vaya añadiendo el mod",
				() -> abrir(new PantallaPanel(this)), a);
		botonBarra(g, "detener", x + (w + sep) * 4, y, w, h, "Detener ruleta", true, "Detiene la ruleta en marcha y quita los avisos de la pantalla",
				() -> ClienteEstado.accion("detener", new JsonObject()), a);
	}

	private void botonBarra(GuiGraphicsExtractor g, String id, int x, int y, int w, int h, String texto, boolean rojo, String ayuda, Runnable accion, float a) {
		boolean on = encima(x, y, w, h);
		float hv = anim("barra." + id, on ? 1 : 0, 14);
		int fondo = rojo ? Tema.mezclar(0xE04A2433, 0xF0602C40, hv) : Tema.mezclar(Tema.BARRA, 0xF02A3346, hv);
		int borde = rojo ? Tema.mezclar(0xFF6A3245, 0xFFB5506E, hv) : Tema.mezclar(Tema.BARRA_BORDE, 0xFF6C8FE8, hv);
		int color = rojo ? Tema.ROJO_TEXTO : Tema.mezclar(0xFFDCE0EA, 0xFFFFFFFF, hv);

		Tema.caja(g, x, y, w, h, Tema.alfa(fondo, a), Tema.alfa(borde, a));
		Tema.rect(g, x + 1, y + h - 2, w - 2, 1, Tema.alfa(0x40000000, a));
		Component c = Tema.negrita(texto);
		int tw = this.font.width(c);
		g.text(this.font, c, x + (w - tw) / 2, y + (h - 8) / 2 + 1, Tema.alfa(color, a), false);

		int lw = (int) (tw * 0.75f * hv);

		if (lw > 0) {
			Tema.rect(g, x + w / 2 - lw / 2, y + h - 3, lw, 1, Tema.alfa(rojo ? Tema.ROJO_TEXTO : Tema.AZUL_CLARO, a * hv));
		}

		if (on) {
			tooltip(ayuda);
		}

		zona(x, y, w, h, accion);
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		if (RuletaClient.TECLA.matches(event)) {
			this.onClose();
			return true;
		}

		return super.keyPressed(event);
	}
}
