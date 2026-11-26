package ar.hombrosafio.ruleta.client;

import ar.hombrosafio.ruleta.DifficultyChange;
import ar.hombrosafio.ruleta.LaunchPayload;
import ar.hombrosafio.ruleta.RouletteColor;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

/** Lista de cambios de dificultad (sección amarilla). */
public final class ChangeListScreen extends Screen {
	private static final int ROW = 22;
	private final Screen parent;
	private int scroll;

	public ChangeListScreen(Screen parent) {
		super(Component.literal("Cambio de dificultad"));
		this.parent = parent;
	}

	private int px() {
		return (this.width - 360) / 2;
	}

	private int listTop() {
		return 36;
	}

	private int visible() {
		return Math.max(1, (this.height - 36 - 40) / ROW);
	}

	@Override
	protected void init() {
		super.init();
		addRenderableWidget(new Ui.Btn(this.width / 2 - 50, this.height - 28, 100, 20, "Volver", false, b -> onClose()));
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
	public boolean mouseScrolled(double mx, double my, double sx, double sy) {
		int max = Math.max(0, DifficultyChange.values().length - visible());
		scroll = Math.clamp(scroll - (int) Math.signum(sy), 0, max);
		return true;
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		if (super.mouseClicked(event, doubleClick)) {
			return true;
		}
		DifficultyChange[] all = DifficultyChange.values();
		for (int i = 0; i < visible() && scroll + i < all.length; i++) {
			if (Ui.in(event.x(), event.y(), px(), listTop() + i * ROW, 360, ROW - 2)) {
				DifficultyChange change = all[scroll + i];
				if (change == DifficultyChange.TEXT) {
					this.minecraft.gui.setScreen(new MessageFormScreen(this, RouletteColor.YELLOW, true));
				} else if (!ClientState.activeChanges.contains(change.id)) {
					ClientPlayNetworking.send(new LaunchPayload(RouletteColor.YELLOW.ordinal(), "", "", "*", false, "none", "", change.id));
					this.minecraft.gui.setScreen(null);
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float partial) {
		Ui.panel(g, px() - 8, 6, 376, this.height - 12);
		g.centeredText(this.font, "Cambio de dificultad", this.width / 2, 16, RouletteColor.YELLOW.argb);
		DifficultyChange[] all = DifficultyChange.values();
		for (int i = 0; i < visible() && scroll + i < all.length; i++) {
			DifficultyChange c = all[scroll + i];
			int y = listTop() + i * ROW;
			boolean active = ClientState.activeChanges.contains(c.id);
			boolean hover = Ui.in(mouseX, mouseY, px(), y, 360, ROW - 2);
			Ui.box(g, px(), y, 360, ROW - 2, hover ? 0xFF2A3560 : Ui.PANEL_LIGHT, hover ? Ui.ACCENT : Ui.BORDER);
			String text = c.announcement;
			if (this.font.width(text) > 300) {
				text = this.font.plainSubstrByWidth(text, 292) + "…";
			}
			g.text(this.font, text, px() + 8, y + 6, active ? Ui.MUTED : Ui.TEXT);
			if (active) {
				g.text(this.font, "ACTIVO", px() + 360 - 8 - this.font.width("ACTIVO"), y + 6, 0xFF6BCB77);
			}
		}
		super.extractRenderState(g, mouseX, mouseY, partial);
	}
}
