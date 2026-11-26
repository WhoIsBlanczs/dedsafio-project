package ar.hombrosafio.ruleta.client;

import ar.hombrosafio.ruleta.ActionPayload;
import ar.hombrosafio.ruleta.RouletteColor;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

/** Selector de ruleta: la rueda con sus 8 secciones, la sección bajo el cursor se resalta. */
public final class PickerScreen extends Screen {
	private int hovered = -1;

	public PickerScreen() {
		super(Component.literal("Ruleta"));
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	private int size() {
		return Math.min(this.height - 110, this.width - 40);
	}

	private int cx() {
		return this.width / 2;
	}

	private int cy() {
		return 30 + size() / 2;
	}

	@Override
	protected void init() {
		super.init();
		int w = 100;
		int gap = 6;
		String[] labels = {"Ajustes", "Cambios activos", "Bunkers", "Panel", "Detener ruleta"};
		int[] widths = {80, 110, 80, 80, 110};
		int total = gap * (labels.length - 1);
		for (int v : widths) {
			total += v;
		}
		int x = (this.width - total) / 2;
		int y = this.height - 28;
		for (int i = 0; i < labels.length; i++) {
			final int idx = i;
			addRenderableWidget(new Ui.Btn(x, y, widths[i], 20, labels[i], false, b -> action(idx)));
			x += widths[i] + gap;
		}
	}

	private void action(int index) {
		switch (index) {
			case 0 -> this.minecraft.gui.setScreen(new SettingsScreen(this));
			case 1 -> this.minecraft.gui.setScreen(new ActiveChangesScreen(this));
			case 2 -> this.minecraft.gui.setScreen(new PanelScreen.Notice(this, "Bunkers",
				"Los bunkers pertenecen al mod Hombrosafío. Esta versión de la ruleta no los incluye."));
			case 3 -> this.minecraft.gui.setScreen(new PanelScreen(this));
			default -> {
				ClientPlayNetworking.send(new ActionPayload("stop", ""));
				onClose();
			}
		}
	}

	private int wedgeAt(double mx, double my) {
		double dx = mx - cx();
		double dy = my - cy();
		double r = Math.sqrt(dx * dx + dy * dy);
		double radius = size() / 2.0 * 0.92;
		if (r > radius || r < size() * 0.08) {
			return -1;
		}
		double deg = Math.toDegrees(Math.atan2(dx, -dy));
		return Math.floorMod((int) Math.floor((deg + 22.5) / 45.0), 8);
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		if (super.mouseClicked(event, doubleClick)) {
			return true;
		}
		int wedge = wedgeAt(event.x(), event.y());
		if (wedge >= 0) {
			RouletteColor color = RouletteColor.values()[wedge];
			this.minecraft.gui.setScreen(color == RouletteColor.YELLOW ? new ChangeListScreen(this) : new MessageFormScreen(this, color));
			return true;
		}
		return false;
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
	public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float partial) {
		this.hovered = wedgeAt(mouseX, mouseY);
		int size = size();
		Identifier wheel = ar.hombrosafio.ruleta.RuletaMod.id("textures/gui/roulette/wheel.png");
		g.blit(RenderPipelines.GUI_TEXTURED, wheel, cx() - size / 2, cy() - size / 2, 0.0F, 0.0F, size, size, size, size, 0xFFFFFFFF);
		if (hovered >= 0) {
			RouletteColor c = RouletteColor.values()[hovered];
			Identifier result = ar.hombrosafio.ruleta.RuletaMod.id("textures/gui/roulette/result_" + c.command + ".png");
			g.blit(RenderPipelines.GUI_TEXTURED, result, cx() - size / 2, cy() - size / 2, 0.0F, 0.0F, size, size, size, size, 0x99FFFFFF);
		}
		g.blit(RenderPipelines.GUI_TEXTURED, ar.hombrosafio.ruleta.RuletaMod.id("textures/gui/roulette/rim.png"),
			cx() - size / 2, cy() - size / 2, 0.0F, 0.0F, size, size, size, size, 0xFFFFFFFF);
		g.blit(RenderPipelines.GUI_TEXTURED, ar.hombrosafio.ruleta.RuletaMod.id("textures/gui/roulette/hub.png"),
			cx() - size / 2, cy() - size / 2, 0.0F, 0.0F, size, size, size, size, 0xFFFFFFFF);
		g.centeredText(this.font, "Elegí una sección para lanzar la ruleta", cx(), 12, Ui.TEXT);
		String label = hovered >= 0 ? RouletteColor.values()[hovered].label : "";
		if (hovered >= 0) {
			g.centeredText(this.font, label, cx(), 30 + size + 8, RouletteColor.values()[hovered].argb);
		}
		super.extractRenderState(g, mouseX, mouseY, partial);
	}
}
