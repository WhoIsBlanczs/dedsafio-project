package ar.hombrosafio.ruleta.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

/** Panel de accesos rápidos. Las entradas que dependen del mod Hombrosafío avisan que requieren el addon. */
public final class PanelScreen extends Screen {
	private record Entry(String name, String tip) {
	}

	private static final Entry[] ENTRIES = {
		new Entry("Casino", "Abre el casino de Hombrosafío (requiere el addon)."),
		new Entry("Dulce azar", "Minijuego de azar de Hombrosafío (requiere el addon)."),
		new Entry("Nutria Pedestal", "Coloca el pedestal de la nutria (requiere el addon).")
	};
	private final Screen parent;
	private String notice = "";

	public PanelScreen(Screen parent) {
		super(Component.literal("Panel"));
		this.parent = parent;
	}

	private int px() {
		return (this.width - 280) / 2;
	}

	@Override
	protected void init() {
		super.init();
		addRenderableWidget(new Ui.Btn(this.width / 2 - 50, this.height / 2 + 70, 100, 20, "Volver", false, b -> onClose()));
	}

	@Override
	public void onClose() {
		this.minecraft.gui.setScreen(parent);
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		if (event.isEscape()) {
			onClose();
			return true;
		}
		return super.keyPressed(event);
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		if (super.mouseClicked(event, doubleClick)) {
			return true;
		}
		for (int i = 0; i < ENTRIES.length; i++) {
			if (Ui.in(event.x(), event.y(), px() + 12, this.height / 2 - 70 + i * 30, 256, 26)) {
				notice = ENTRIES[i].name + " requiere el addon de Hombrosafío.";
				return true;
			}
		}
		return false;
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float partial) {
		int top = this.height / 2 - 100;
		Ui.panel(g, px(), top, 280, 200);
		g.centeredText(this.font, "Panel", this.width / 2, top + 10, Ui.GOLD);
		String tip = "";
		for (int i = 0; i < ENTRIES.length; i++) {
			int y = this.height / 2 - 70 + i * 30;
			boolean hover = Ui.in(mouseX, mouseY, px() + 12, y, 256, 26);
			Ui.box(g, px() + 12, y, 256, 26, hover ? 0xFF2A3560 : Ui.PANEL_LIGHT, hover ? Ui.ACCENT : Ui.BORDER);
			g.centeredText(this.font, ENTRIES[i].name, this.width / 2, y + 9, Ui.TEXT);
			if (hover) {
				tip = ENTRIES[i].tip;
			}
		}
		g.centeredText(this.font, "Más opciones próximamente...", this.width / 2, this.height / 2 + 28, Ui.MUTED);
		g.centeredText(this.font, !notice.isEmpty() ? notice : tip, this.width / 2, this.height / 2 + 48, Ui.GOLD);
		super.extractRenderState(g, mouseX, mouseY, partial);
	}

	/** Pantalla de aviso simple. */
	static final class Notice extends Screen {
		private final Screen parent;
		private final String text;

		Notice(Screen parent, String title, String text) {
			super(Component.literal(title));
			this.parent = parent;
			this.text = text;
		}

		@Override
		protected void init() {
			super.init();
			addRenderableWidget(new Ui.Btn(this.width / 2 - 50, this.height / 2 + 24, 100, 20, "Volver", false, b -> onClose()));
		}

		@Override
		public void onClose() {
			this.minecraft.gui.setScreen(parent);
		}

		@Override
		public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float partial) {
			Ui.panel(g, this.width / 2 - 150, this.height / 2 - 50, 300, 100);
			g.centeredText(this.font, this.title, this.width / 2, this.height / 2 - 40, Ui.GOLD);
			var lines = this.font.split(Component.literal(text), 270);
			for (int i = 0; i < lines.size(); i++) {
				g.centeredText(this.font, lines.get(i), this.width / 2, this.height / 2 - 18 + i * 11, Ui.TEXT);
			}
			super.extractRenderState(g, mouseX, mouseY, partial);
		}
	}
}
