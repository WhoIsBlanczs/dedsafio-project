package ar.hombrosafio.ruleta.client;

import ar.hombrosafio.ruleta.ActionPayload;
import ar.hombrosafio.ruleta.DifficultyChange;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

/** Cambios de dificultad activos, con botón para desactivarlos. */
public final class ActiveChangesScreen extends Screen {
	private static final int ROW = 26;
	private final Screen parent;

	public ActiveChangesScreen(Screen parent) {
		super(Component.literal("Cambios activos"));
		this.parent = parent;
	}

	void refresh() {
	}

	private int px() {
		return (this.width - 360) / 2;
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
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		if (super.mouseClicked(event, doubleClick)) {
			return true;
		}
		if (!ClientState.canEdit) {
			return false;
		}
		for (int i = 0; i < ClientState.activeChanges.size(); i++) {
			if (Ui.in(event.x(), event.y(), px() + 360 - 90, 44 + i * ROW + 2, 84, 18)) {
				ClientPlayNetworking.send(new ActionPayload("change_off", ClientState.activeChanges.get(i)));
				return true;
			}
		}
		return false;
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float partial) {
		Ui.panel(g, px() - 8, 6, 376, this.height - 12);
		g.centeredText(this.font, "Cambios activos", this.width / 2, 16, Ui.GOLD);
		if (ClientState.activeChanges.isEmpty()) {
			g.centeredText(this.font, "No hay cambios de dificultad activos.", this.width / 2, 60, Ui.MUTED);
		}
		for (int i = 0; i < ClientState.activeChanges.size() && 44 + i * ROW < this.height - 40; i++) {
			DifficultyChange c = DifficultyChange.byId(ClientState.activeChanges.get(i));
			int y = 44 + i * ROW;
			Ui.box(g, px(), y, 360, ROW - 2, Ui.PANEL_LIGHT, Ui.BORDER);
			String text = c == null ? ClientState.activeChanges.get(i) : c.announcement;
			if (this.font.width(text) > 250) {
				text = this.font.plainSubstrByWidth(text, 242) + "…";
			}
			g.text(this.font, text, px() + 8, y + 8, Ui.TEXT);
			if (ClientState.canEdit) {
				boolean hover = Ui.in(mouseX, mouseY, px() + 360 - 90, y + 2, 84, 18);
				Ui.box(g, px() + 360 - 90, y + 2, 84, 18, hover ? 0xFF6B2A2A : 0xFF3A1A1A, 0xFFB04A4A);
				g.centeredText(this.font, "Desactivar", px() + 360 - 48, y + 7, Ui.TEXT);
			}
		}
		super.extractRenderState(g, mouseX, mouseY, partial);
	}
}
