package ar.hombrosafio.ruleta.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

/** Estilo compartido de las pantallas de la ruleta: panel azul oscuro y botones planos. */
final class Ui {
	static final int PANEL = 0xF0101426;
	static final int PANEL_LIGHT = 0xFF1A2038;
	static final int BORDER = 0xFF2E3A66;
	static final int TEXT = 0xFFE8E0D9;
	static final int MUTED = 0xFF8C93AD;
	static final int ACCENT = 0xFF5B8DEF;
	static final int GOLD = 0xFFFFD36E;

	static void panel(GuiGraphicsExtractor g, int x, int y, int w, int h) {
		g.fill(x - 1, y - 1, x + w + 1, y + h + 1, BORDER);
		g.fill(x, y, x + w, y + h, PANEL);
	}

	static void box(GuiGraphicsExtractor g, int x, int y, int w, int h, int fill, int border) {
		g.fill(x, y, x + w, y + h, border);
		g.fill(x + 1, y + 1, x + w - 1, y + h - 1, fill);
	}

	static boolean in(double mx, double my, int x, int y, int w, int h) {
		return mx >= x && mx < x + w && my >= y && my < y + h;
	}

	static final class Btn extends Button {
		private final boolean primary;

		Btn(int x, int y, int w, int h, String label, boolean primary, Button.OnPress onPress) {
			super(x, y, w, h, Component.literal(label), onPress, DEFAULT_NARRATION);
			this.primary = primary;
		}

		@Override
		protected void extractContents(GuiGraphicsExtractor g, int mouseX, int mouseY, float partial) {
			boolean hover = this.isHoveredOrFocused() && this.active;
			int fill = !this.active ? 0xFF14182A : primary ? (hover ? 0xFF4C7BDB : 0xFF3B66C4) : (hover ? 0xFF2A3560 : 0xFF1A2038);
			box(g, getX(), getY(), getWidth(), getHeight(), fill, hover ? ACCENT : BORDER);
			Font font = Minecraft.getInstance().font;
			g.centeredText(font, getMessage(), getX() + getWidth() / 2, getY() + (getHeight() - 8) / 2, this.active ? TEXT : MUTED);
		}
	}

	private Ui() {
	}
}
