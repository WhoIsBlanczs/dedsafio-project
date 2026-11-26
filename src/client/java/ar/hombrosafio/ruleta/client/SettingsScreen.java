package ar.hombrosafio.ruleta.client;

import ar.hombrosafio.ruleta.RuletaConfig;
import ar.hombrosafio.ruleta.SettingChangePayload;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

/** Ajustes: categorías a la izquierda, búsqueda arriba, controles −/+ o interruptor por ajuste. */
public final class SettingsScreen extends Screen {
	private static final int ROW = 36;
	private static String lastCategory = RuletaConfig.CATEGORIES.get(0);
	private final Screen parent;
	private EditBox search;
	private int scroll;

	public SettingsScreen(Screen parent) {
		super(Component.literal("Ajustes"));
		this.parent = parent;
	}

	private int px() {
		return Math.max(6, (this.width - 520) / 2);
	}

	private int pw() {
		return Math.min(520, this.width - 12);
	}

	private int listX() {
		return px() + 130;
	}

	private int listW() {
		return pw() - 138;
	}

	private int listTop() {
		return 58;
	}

	private int visible() {
		return Math.max(1, (this.height - listTop() - 40) / ROW);
	}

	private List<RuletaConfig.Setting> shown() {
		String q = search == null ? "" : search.getValue().toLowerCase();
		List<RuletaConfig.Setting> out = new ArrayList<>();
		for (RuletaConfig.Setting s : RuletaConfig.all()) {
			boolean match = q.isEmpty() ? s.category().equals(lastCategory)
				: s.title().toLowerCase().contains(q) || s.description().toLowerCase().contains(q);
			if (match) {
				out.add(s);
			}
		}
		return out;
	}

	@Override
	protected void init() {
		super.init();
		search = new EditBox(this.font, listX(), 30, listW(), 18, Component.literal("Buscar"));
		search.setHint(Component.literal("Buscar ajuste..."));
		search.setResponder(v -> scroll = 0);
		addRenderableWidget(search);
		int by = this.height - 28;
		addRenderableWidget(new Ui.Btn(listX(), by, 110, 20, "Restablecer sección", false,
			b -> send("cat:" + lastCategory)));
		addRenderableWidget(new Ui.Btn(listX() + 116, by, 100, 20, "Restablecer todo", false, b -> send("*")));
		addRenderableWidget(new Ui.Btn(px() + pw() - 78, by, 70, 20, "Listo", true, b -> onClose()));
	}

	private void send(String id) {
		if (!ClientState.canEdit) {
			return;
		}
		ClientPlayNetworking.send(new SettingChangePayload(id, 0));
		if (id.equals("*") || id.startsWith("cat:")) {
			for (RuletaConfig.Setting s : RuletaConfig.all()) {
				if (id.equals("*") || s.category().equals(id.substring(4))) {
					RuletaConfig.replace(Map.of(s.id(), s.def()));
				}
			}
		}
	}

	private void change(RuletaConfig.Setting s, int value) {
		if (!ClientState.canEdit) {
			return;
		}
		int v = Math.clamp(value, s.min(), s.max());
		ClientPlayNetworking.send(new SettingChangePayload(s.id(), v));
		RuletaConfig.replace(Map.of(s.id(), v));
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
		int max = Math.max(0, shown().size() - visible());
		scroll = Math.clamp(scroll - (int) Math.signum(sy), 0, max);
		return true;
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		if (super.mouseClicked(event, doubleClick)) {
			return true;
		}
		double mx = event.x();
		double my = event.y();
		List<String> cats = RuletaConfig.CATEGORIES;
		for (int i = 0; i < cats.size(); i++) {
			if (Ui.in(mx, my, px() + 8, 30 + i * 20, 114, 18)) {
				lastCategory = cats.get(i);
				search.setValue("");
				scroll = 0;
				return true;
			}
		}
		List<RuletaConfig.Setting> list = shown();
		for (int i = 0; i < visible() && scroll + i < list.size(); i++) {
			RuletaConfig.Setting s = list.get(scroll + i);
			int y = listTop() + i * ROW;
			int right = listX() + listW();
			int value = RuletaConfig.getInt(s.id());
			if (s.kind() == RuletaConfig.Kind.BOOL) {
				if (Ui.in(mx, my, right - 50, y + 8, 42, 16)) {
					change(s, value == 0 ? 1 : 0);
					return true;
				}
			} else {
				int step = s.step() * (this.minecraft.hasShiftDown() ? 5 : 1);
				if (Ui.in(mx, my, right - 110, y + 8, 16, 16)) {
					change(s, value - step);
					return true;
				}
				if (Ui.in(mx, my, right - 24, y + 8, 16, 16)) {
					change(s, value + step);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float partial) {
		Ui.panel(g, px(), 6, pw(), this.height - 12);
		g.text(this.font, "Ajustes" + (ClientState.canEdit ? "" : "  (solo lectura)"), px() + 8, 14, Ui.GOLD);
		List<String> cats = RuletaConfig.CATEGORIES;
		for (int i = 0; i < cats.size(); i++) {
			boolean sel = cats.get(i).equals(lastCategory) && search.getValue().isEmpty();
			boolean hover = Ui.in(mouseX, mouseY, px() + 8, 30 + i * 20, 114, 18);
			Ui.box(g, px() + 8, 30 + i * 20, 114, 18, sel ? 0xFF2A3F80 : hover ? 0xFF222B4C : Ui.PANEL_LIGHT, sel ? Ui.ACCENT : Ui.BORDER);
			g.text(this.font, cats.get(i), px() + 13, 35 + i * 20, Ui.TEXT);
		}
		List<RuletaConfig.Setting> list = shown();
		if (list.isEmpty()) {
			g.text(this.font, "Sin ajustes disponibles todavía", listX() + 4, listTop() + 6, Ui.MUTED);
		}
		for (int i = 0; i < visible() && scroll + i < list.size(); i++) {
			RuletaConfig.Setting s = list.get(scroll + i);
			int y = listTop() + i * ROW;
			int right = listX() + listW();
			int value = RuletaConfig.getInt(s.id());
			Ui.box(g, listX(), y, listW(), ROW - 3, Ui.PANEL_LIGHT, Ui.BORDER);
			if (value != s.def()) {
				g.fill(listX() + 5, y + 7, listX() + 9, y + 11, Ui.ACCENT);
			}
			g.text(this.font, s.title(), listX() + 13, y + 5, Ui.TEXT);
			String desc = s.description();
			int maxW = listW() - 130;
			if (this.font.width(desc) > maxW) {
				desc = this.font.plainSubstrByWidth(desc, maxW - 8) + "…";
			}
			g.text(this.font, desc, listX() + 13, y + 18, Ui.MUTED);
			if (s.kind() == RuletaConfig.Kind.BOOL) {
				boolean on = value != 0;
				Ui.box(g, right - 50, y + 8, 42, 16, on ? 0xFF2F7D4A : 0xFF3A3F55, on ? 0xFF6BCB77 : Ui.BORDER);
				g.centeredText(this.font, on ? "ON" : "OFF", right - 29, y + 12, Ui.TEXT);
			} else {
				Ui.box(g, right - 110, y + 8, 16, 16, 0xFF222B4C, Ui.BORDER);
				g.centeredText(this.font, "-", right - 102, y + 12, Ui.TEXT);
				Ui.box(g, right - 24, y + 8, 16, 16, 0xFF222B4C, Ui.BORDER);
				g.centeredText(this.font, "+", right - 16, y + 12, Ui.TEXT);
				g.centeredText(this.font, s.format(value), right - 59, y + 12, Ui.GOLD);
			}
		}
		super.extractRenderState(g, mouseX, mouseY, partial);
	}
}
