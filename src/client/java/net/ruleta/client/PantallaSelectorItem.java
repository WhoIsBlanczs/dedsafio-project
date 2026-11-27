package net.ruleta.client;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import net.ruleta.client.ui.PantallaBase;
import net.ruleta.client.ui.Tema;

/** Rejilla con todos los ítems del juego y un buscador, para elegir uno. */
public class PantallaSelectorItem extends PantallaBase {
	private static final int W = 480;
	private static final int H = 347;
	private static final int CELDA = 24;

	private final Consumer<Item> alElegir;
	private final List<ItemStack> todos = new ArrayList<>();
	private final List<ItemStack> filtrados = new ArrayList<>();
	private EditBox buscar;
	private String busqueda = "";
	private float scroll;

	public PantallaSelectorItem(Screen anterior, Consumer<Item> alElegir) {
		super(Component.literal("Elegir item"), anterior);
		this.alElegir = alElegir;
		BuiltInRegistries.ITEM.stream().forEach(item -> {
			ItemStack s = new ItemStack(item);

			if (!s.isEmpty()) {
				todos.add(s);
			}
		});
		filtrar();
	}

	private static String normal(String s) {
		return Normalizer.normalize(s.toLowerCase(Locale.ROOT), Normalizer.Form.NFD).replaceAll("\\p{M}", "");
	}

	private void filtrar() {
		filtrados.clear();
		String q = normal(busqueda.trim());

		for (ItemStack s : todos) {
			if (q.isEmpty() || normal(s.getHoverName().getString()).contains(q) || Objetos.id(s.getItem()).contains(q)) {
				filtrados.add(s);
			}
		}

		scroll = 0;
	}

	@Override
	protected void iniciar() {
		int px = (LIENZO_W - W) / 2;
		int py = (LIENZO_H - H) / 2;
		buscar = new EditBox(this.font, px + 21, py + 52, W - 42, 10, Component.literal("Buscar"));
		buscar.setBordered(false);
		buscar.setMaxLength(50);
		buscar.setHint(Component.literal("Buscar un item...").withColor(0xD4D8E0));
		buscar.setValue(busqueda);
		buscar.setResponder(s -> {
			busqueda = s;
			filtrar();
		});
		campoTexto(buscar, px + 16, py + 47, W - 32, 20);
		setInitialFocus(buscar);
	}

	@Override
	protected void dibujar(GuiGraphicsExtractor g, float a) {
		int px = (LIENZO_W - W) / 2;
		int py = (LIENZO_H - H) / 2;
		buscar.visible = a > 0.6f;
		Tema.panel(g, px, py, W, H, 39, a);
		g.text(this.font, Tema.negrita("Elegir item"), px + 16, py + 10, Tema.alfa(Tema.BLANCO, a), false);
		g.text(this.font, "Clic en un item para elegirlo", px + 16, py + 23, Tema.alfa(Tema.TEXTO, a), false);
		botonCerrar(g, px + W - 22, py + 14, a);
		Tema.caja(g, px + 16, py + 47, W - 32, 20, Tema.alfa(buscar.isFocused() ? 0xFF1C2742 : Tema.CAMPO, a), Tema.alfa(buscar.isFocused() ? 0xFF7F9BE6 : Tema.CAMPO_BORDE, a));

		int columnas = (W - 32) / CELDA;
		int gx = px + 16 + ((W - 32) - columnas * CELDA) / 2;
		int gy = py + 75;
		int gh = H - 75 - 10;
		int filas = (filtrados.size() + columnas - 1) / columnas;
		scroll = Math.max(0, Math.min(scroll, Math.max(0, filas * CELDA - gh)));
		g.enableScissor(px + 1, gy, px + W - 1, gy + gh);
		ItemStack encimaDe = null;

		for (int i = 0; i < filtrados.size(); i++) {
			int x = gx + (i % columnas) * CELDA;
			int y = gy + (i / columnas) * CELDA - (int) scroll;

			if (y + CELDA < gy || y > gy + gh) {
				continue;
			}

			ItemStack s = filtrados.get(i);
			boolean hv = encima(x, y, CELDA, CELDA) && ratonY >= gy && ratonY < gy + gh;
			Tema.caja(g, x + 1, y + 1, CELDA - 2, CELDA - 2, Tema.alfa(hv ? 0xFF2A3550 : Tema.TARJETA, a), Tema.alfa(hv ? Tema.AZUL_BORDE : Tema.TARJETA_BORDE, a));
			g.item(s, x + 4, y + 4);

			if (hv) {
				encimaDe = s;
			}

			if (y >= gy && y + CELDA <= gy + gh) {
				zona(x, y, CELDA, CELDA, () -> {
					alElegir.accept(s.getItem());
					onClose();
				});
			}
		}

		g.disableScissor();

		if (encimaDe != null) {
			tooltip(encimaDe.getHoverName().getString() + "\n" + Objetos.id(encimaDe.getItem()));
		}

		if (filtrados.isEmpty()) {
			Tema.textoCentrado(g, this.font, Component.literal("No hay items con ese nombre."), px + W / 2, gy + 30, Tema.alfa(Tema.GRIS, a), false);
		}
	}

	@Override
	protected boolean desplazar(double mx, double my, double cantidad) {
		scroll -= (float) cantidad * CELDA * 2;
		return true;
	}
}
