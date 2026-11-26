package ar.hombrosafio.ruleta.client;

import ar.hombrosafio.ruleta.client.ui.Ui;
import ar.hombrosafio.ruleta.client.ui.UiBoton;
import ar.hombrosafio.ruleta.client.ui.UiPantalla;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;

/** Aviso para opciones que se conectan en una tanda posterior del port. */
public final class AvisoAddonPantalla extends UiPantalla {
	private final String nombre;

	public AvisoAddonPantalla(Screen padre, String nombre) {
		super(nombre, padre);
		this.nombre = nombre;
	}

	@Override
	protected void init() {
		this.panel(420, 150);
		this.cabecera(true);
		this.addRenderableWidget(new UiBoton(this.px + this.pw / 2 - 45, this.py + this.ph - 38, 90, 26, "Volver", UiBoton.Estilo.FANTASMA, this::onClose));
	}

	@Override
	protected void contenido(GuiGraphicsExtractor g, int mx, int my, float pt) {
		Ui.texto(g, this.font, nombre + " todavía no está disponible en esta versión.", this.px + 20, this.yContenido() + 14, 15330549, 1.0F);
		Ui.texto(g, this.font, "Se conecta con el mod Hombrosafío en una próxima tanda.", this.px + 20, this.yContenido() + 30, 9344683, 1.0F);
	}
}
