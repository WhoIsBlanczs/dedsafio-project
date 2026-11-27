package net.ruleta.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;

/**
 * Base de todas las pantallas del menú.
 *
 * <p>Todo se dibuja sobre un lienzo fijo de 1280×720 que se escala para llenar la
 * ventana (como hace el original), así el menú se ve igual con cualquier escala de
 * interfaz. Se dibuja en modo inmediato: cada fotograma se registran las zonas
 * clicables y al hacer clic se busca la zona bajo el ratón.
 */
public abstract class PantallaBase extends Screen {
	public static final int LIENZO_W = 1280;
	public static final int LIENZO_H = 720;

	public enum Estilo { OSCURO, AZUL, ROJO, TEXTO, SELECCIONADO, PESTANA, PESTANA_ACTIVA }

	protected record Zona(int x, int y, int w, int h, Runnable accion, boolean sonido) {
		boolean dentro(double mx, double my) {
			return mx >= x && my >= y && mx < x + w && my < y + h;
		}
	}

	private record Campo(AbstractWidget widget, int x, int y, int w, int h) {
		boolean dentro(double mx, double my) {
			return widget.visible && mx >= x && my >= y && mx < x + w && my < y + h;
		}
	}

	protected final Screen anterior;
	private final List<Zona> zonas = new ArrayList<>();
	private final List<Zona> zonasDibujo = new ArrayList<>();
	private final List<Campo> campos = new ArrayList<>();
	private final Map<String, Float> animaciones = new HashMap<>();
	private List<FormattedCharSequence> tooltipPendiente;
	private long ultimoNs = System.nanoTime();
	private final long abierto = System.nanoTime();
	protected float dt;
	/** Posición del ratón en coordenadas del lienzo. */
	protected int ratonX;
	protected int ratonY;
	private float escala = 1;
	private float origenX;
	private float origenY;

	protected PantallaBase(Component titulo, Screen anterior) {
		super(titulo);
		this.anterior = anterior;
	}

	// ------------------------------------------------------------------ lienzo

	private void calcularEscala() {
		escala = Math.min(this.width / (float) LIENZO_W, this.height / (float) LIENZO_H);
		origenX = (this.width - LIENZO_W * escala) / 2f;
		origenY = (this.height - LIENZO_H * escala) / 2f;
	}

	protected double lienzoX(double pantallaX) {
		return (pantallaX - origenX) / escala;
	}

	protected double lienzoY(double pantallaY) {
		return (pantallaY - origenY) / escala;
	}

	@Override
	protected final void init() {
		calcularEscala();
		campos.clear();
		iniciar();
	}

	/** Equivale a init(), pero con coordenadas del lienzo. */
	protected void iniciar() {
	}

	/** Añade un widget de Minecraft (campo de texto) colocado en coordenadas del lienzo. */
	protected <T extends AbstractWidget> T campoTexto(T widget, int x, int y, int w, int h) {
		addRenderableWidget(widget);
		campos.add(new Campo(widget, x, y, w, h));
		return widget;
	}

	// ------------------------------------------------------------------ utilidades

	/** Segundos desde que se abrió la pantalla. */
	protected float tiempo() {
		return (System.nanoTime() - abierto) / 1.0e9f;
	}

	/** Opacidad de entrada (fundido de 0,18 s). */
	protected float entrada() {
		float t = Math.min(1, tiempo() / 0.18f);
		return 1 - (1 - t) * (1 - t);
	}

	protected float anim(String clave, float objetivo, float velocidad) {
		float v = animaciones.getOrDefault(clave, objetivo);
		v += (objetivo - v) * (1 - (float) Math.exp(-velocidad * dt));

		if (Math.abs(objetivo - v) < 0.001f) {
			v = objetivo;
		}

		animaciones.put(clave, v);
		return v;
	}

	protected void fijarAnim(String clave, float valor) {
		animaciones.put(clave, valor);
	}

	protected boolean encima(int x, int y, int w, int h) {
		return ratonX >= x && ratonY >= y && ratonX < x + w && ratonY < y + h;
	}

	protected void zona(int x, int y, int w, int h, Runnable accion) {
		if (w > 0 && h > 0) {
			zonasDibujo.add(new Zona(x, y, w, h, accion, true));
		}
	}

	protected void zonaSilenciosa(int x, int y, int w, int h, Runnable accion) {
		if (w > 0 && h > 0) {
			zonasDibujo.add(new Zona(x, y, w, h, accion, false));
		}
	}

	protected void tooltip(String texto) {
		List<String> lineas = new ArrayList<>();

		for (String parte : texto.split("\n")) {
			lineas.addAll(Tema.lineas(this.font, parte, 200, 20));
		}

		tooltipPendiente = Tema.secuencias(lineas);
	}

	protected void sonidoClic() {
		this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
	}

	// ------------------------------------------------------------------ botones

	protected void boton(GuiGraphicsExtractor g, String id, int x, int y, int w, int h, Component texto, Estilo estilo, String ayuda, Runnable accion, float a) {
		boton(g, id, x, y, w, h, Texto.de(texto), estilo, ayuda, accion, a);
	}

	/** Botón dibujado a mano con la animación de subrayado del original. */
	protected void boton(GuiGraphicsExtractor g, String id, int x, int y, int w, int h, Texto texto, Estilo estilo, String ayuda, Runnable accion, float a) {
		boolean hover = encima(x, y, w, h);
		float hv = anim("btn." + id, hover ? 1 : 0, 14);
		int fondo;
		int borde;
		int color;

		switch (estilo) {
			case AZUL -> {
				fondo = Tema.mezclar(Tema.AZUL, 0xFF7393E0, hv);
				borde = Tema.mezclar(0xFF6C8AD8, 0xFFDCE6FF, hv);
				color = 0xFFFFFFFF;
			}
			case ROJO -> {
				fondo = Tema.mezclar(Tema.ROJO_FONDO, 0xFF683349, hv);
				borde = Tema.mezclar(0xFF6E3448, 0xFFB5506E, hv);
				color = Tema.ROJO_TEXTO;
			}
			case TEXTO -> {
				fondo = 0;
				borde = 0;
				color = Tema.mezclar(Tema.GRIS, Tema.BLANCO, hv);
			}
			case SELECCIONADO -> {
				fondo = Tema.mezclar(0xFF4B66B0, 0xFF5874C2, hv);
				borde = Tema.mezclar(0xFF7F9BE6, 0xFFA9C0FF, hv);
				color = 0xFFFFFFFF;
			}
			case PESTANA -> {
				fondo = Tema.alfa(Tema.CONTROL, hv * 0.6f);
				borde = 0;
				color = Tema.mezclar(Tema.GRIS, Tema.BLANCO, hv);
			}
			case PESTANA_ACTIVA -> {
				fondo = 0xFF3A4D80;
				borde = 0xFF5D78BF;
				color = 0xFFFFFFFF;
			}
			default -> {
				fondo = Tema.mezclar(0xFF1C2333, 0xFF222A3D, hv);
				borde = Tema.mezclar(0xFF2C3448, 0xFF5C7BC8, hv);
				color = Tema.mezclar(Tema.TEXTO, Tema.BLANCO, hv);
			}
		}

		if (estilo != Estilo.TEXTO && (fondo >>> 24) > 0) {
			if ((borde >>> 24) > 0) {
				Tema.caja(g, x, y, w, h, Tema.alfa(fondo, a), Tema.alfa(borde, a));
			} else {
				Tema.rect(g, x, y, w, h, Tema.alfa(fondo, a));
			}

			if (estilo == Estilo.AZUL) {
				// Brillo suave en la mitad de arriba, como los botones azules del original.
				Tema.rect(g, x + 1, y + 1, w - 2, (h - 2) / 2, Tema.alfa(0x14FFFFFF, a));
			}
		}

		int tw = texto.ancho(this.font);
		int tx = x + (w - tw) / 2;
		int ty = y + (h - 8) / 2;
		texto.dibujar(g, this.font, tx, ty, Tema.alfa(color, a), false);

		if (estilo == Estilo.OSCURO || estilo == Estilo.SELECCIONADO || estilo == Estilo.ROJO) {
			int lw = (int) (Math.min(tw, w - 8) * 0.7f * hv);

			if (lw > 0) {
				int lc = estilo == Estilo.ROJO ? Tema.ROJO_TEXTO : Tema.AZUL_CLARO;
				Tema.rect(g, x + w / 2 - lw / 2, y + h - 3, lw, 1, Tema.alfa(lc, a * hv));
			}
		}

		if (hover && ayuda != null) {
			tooltip(ayuda);
		}

		if (accion != null) {
			zona(x, y, w, h, accion);
		}
	}

	/** Botón × para cerrar, arriba a la derecha de los paneles. */
	protected void botonCerrar(GuiGraphicsExtractor g, int x, int y, float a) {
		boolean hover = encima(x - 3, y - 3, 12, 13);
		int c = hover ? Tema.BLANCO : Tema.GRIS;
		g.text(this.font, "×", x, y, Tema.alfa(c, a), false);
		zona(x - 3, y - 3, 12, 13, this::onClose);
	}

	// ------------------------------------------------------------------ dibujo

	protected abstract void dibujar(GuiGraphicsExtractor g, float a);

	/** Se dibuja por encima de los campos de texto. */
	protected void encima(GuiGraphicsExtractor g, float a) {
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
		long ahora = System.nanoTime();
		dt = Math.min(0.1f, (ahora - ultimoNs) / 1.0e9f);
		ultimoNs = ahora;
		calcularEscala();
		ratonX = (int) Math.floor(lienzoX(mouseX));
		ratonY = (int) Math.floor(lienzoY(mouseY));
		zonasDibujo.clear();
		tooltipPendiente = null;

		g.pose().pushMatrix();
		g.pose().translate(origenX, origenY);
		g.pose().scale(escala, escala);

		float a = entrada();
		dibujar(g, a);
		super.extractRenderState(g, ratonX, ratonY, delta);
		encima(g, a);

		if (tooltipPendiente != null) {
			Tema.tooltip(g, this.font, tooltipPendiente, ratonX, ratonY, LIENZO_W, LIENZO_H);
		}

		g.pose().popMatrix();

		zonas.clear();
		zonas.addAll(zonasDibujo);
	}

	// ------------------------------------------------------------------ ratón

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		double mx = lienzoX(event.x());
		double my = lienzoY(event.y());

		for (Campo c : campos) {
			if (c.dentro(mx, my)) {
				this.setFocused(c.widget());
				return true;
			}
		}

		this.setFocused(null);

		if (event.button() == InputConstants.MOUSE_BUTTON_LEFT) {
			for (int i = zonas.size() - 1; i >= 0; i--) {
				Zona z = zonas.get(i);

				if (z.dentro(mx, my)) {
					if (z.sonido()) {
						sonidoClic();
					}

					z.accion().run();
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public boolean mouseScrolled(double x, double y, double sx, double sy) {
		double mx = lienzoX(x);
		double my = lienzoY(y);

		for (Campo c : campos) {
			if (c.dentro(mx, my)) {
				return c.widget().mouseScrolled(mx, my, sx, sy);
			}
		}

		return desplazar(mx, my, sy);
	}

	/** Rueda del ratón fuera de los campos de texto. */
	protected boolean desplazar(double mx, double my, double cantidad) {
		return false;
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public void onClose() {
		this.minecraft.gui.setScreen(this.anterior);
	}

	protected void abrir(Screen s) {
		this.minecraft.gui.setScreen(s);
	}
}
