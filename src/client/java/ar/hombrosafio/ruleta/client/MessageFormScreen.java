package ar.hombrosafio.ruleta.client;

import ar.hombrosafio.ruleta.LaunchPayload;
import ar.hombrosafio.ruleta.RouletteColor;
import ar.hombrosafio.ruleta.RouletteManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;

/** Formulario de anuncio/sanción: título, mensaje, destinatario, tamaño, castigo y temporizador. */
public final class MessageFormScreen extends Screen {
	private final Screen parent;
	private final RouletteColor color;
	private final boolean yellowText;
	private EditBox title;
	private EditBox message;
	private EditBox target;
	private EditBox timer;
	private boolean mini;
	private int punishment;
	private Ui.Btn sizeButton;
	private Ui.Btn punishButton;

	public MessageFormScreen(Screen parent, RouletteColor color) {
		this(parent, color, false);
	}

	MessageFormScreen(Screen parent, RouletteColor color, boolean yellowText) {
		super(Component.literal(color.label));
		this.parent = parent;
		this.color = color;
		this.yellowText = yellowText;
	}

	private boolean sanction() {
		return color == RouletteColor.PURPLE || color == RouletteColor.RED;
	}

	@Override
	protected void init() {
		super.init();
		int w = 300;
		int x = (this.width - w) / 2;
		int y = Math.max(8, (this.height - 230) / 2);
		title = new EditBox(this.font, x + 12, y + 40, w - 24, 18, Component.literal("Título"));
		title.setMaxLength(80);
		message = new EditBox(this.font, x + 12, y + 76, w - 24, 18, Component.literal("Mensaje"));
		message.setMaxLength(1000);
		target = new EditBox(this.font, x + 12, y + 112, w - 24, 18, Component.literal("¿Para quién?"));
		target.setMaxLength(64);
		target.setValue("*");
		timer = new EditBox(this.font, x + 12, y + 148, 110, 18, Component.literal("Temporizador"));
		timer.setMaxLength(8);
		timer.setHint(Component.literal("mm:ss"));
		if (!yellowText) {
			addRenderableWidget(title);
		}
		addRenderableWidget(message);
		if (!yellowText) {
			addRenderableWidget(target);
			sizeButton = addRenderableWidget(new Ui.Btn(x + 12, y + 172, 130, 20, "", false, b -> {
				mini = !mini;
				refreshLabels();
			}));
			if (sanction()) {
				punishButton = addRenderableWidget(new Ui.Btn(x + 150, y + 172, 138, 20, "", false, b -> {
					punishment = (punishment + 1) % RouletteManager.PUNISHMENTS.size();
					refreshLabels();
				}));
			} else {
				addRenderableWidget(timer);
			}
		}
		addRenderableWidget(new Ui.Btn(x + 12, y + 200, 100, 20, "Cancelar", false, b -> onClose()));
		addRenderableWidget(new Ui.Btn(x + w - 112, y + 200, 100, 20, "LANZAR", true, b -> launch()));
		setInitialFocus(yellowText ? message : title);
		refreshLabels();
	}

	private void refreshLabels() {
		if (sizeButton != null) {
			sizeButton.setMessage(Component.literal("Tamaño: " + (mini ? "MINI" : "NORMAL")));
		}
		if (punishButton != null) {
			String p = RouletteManager.PUNISHMENTS.get(punishment);
			punishButton.setMessage(Component.literal("Castigo: " + switch (p) {
				case "lightning" -> "RAYO";
				case "hurt" -> "EFECTOS";
				case "blind" -> "CEGUERA";
				case "kill" -> "MATAR";
				default -> "NINGUNO";
			}));
		}
	}

	private void launch() {
		String msg = message.getValue().strip();
		if (msg.isEmpty() && title.getValue().isBlank()) {
			return;
		}
		String t = target.getValue().isBlank() ? "*" : target.getValue().strip();
		ClientPlayNetworking.send(new LaunchPayload(color.ordinal(), yellowText ? "" : title.getValue().strip(), msg, t, mini,
			sanction() ? RouletteManager.PUNISHMENTS.get(punishment) : "none", timer.getValue(), yellowText ? "texto" : ""));
		this.minecraft.gui.setScreen(null);
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
	public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float partial) {
		int w = 300;
		int x = (this.width - w) / 2;
		int y = Math.max(8, (this.height - 230) / 2);
		Ui.panel(g, x, y, w, 230);
		g.fill(x, y, x + w, y + 3, color.argb);
		g.centeredText(this.font, color == RouletteColor.GREEN ? "Anuncio" : sanction() ? "Sanción" : color.label, x + w / 2, y + 12, color.argb);
		if (!yellowText) {
			g.text(this.font, "Título", x + 12, y + 29, Ui.MUTED);
		}
		g.text(this.font, "Mensaje  " + message.getValue().length() + "/1000", x + 12, y + 65, Ui.MUTED);
		if (!yellowText) {
			g.text(this.font, "¿Para quién? (* = todos)", x + 12, y + 101, Ui.MUTED);
			if (!sanction()) {
				g.text(this.font, "Temporizador (mm:ss, opcional)", x + 12, y + 137, Ui.MUTED);
			}
		}
		g.centeredText(this.font, "Sale: " + color.label, x + w / 2, y + 222 - 1, Ui.MUTED);
		super.extractRenderState(g, mouseX, mouseY, partial);
	}
}
