package ar.hombrosafio.ruleta;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;

/** Formato del mensaje de chat que sale al terminar la ruleta (fuente con brillo animado en el cliente). */
public final class RouletteAnnouncement {
	public static final FontDescription.Resource FONT = new FontDescription.Resource(RuletaMod.id("roulette_chat"));

	public static Component message(RouletteColor color, String text) {
		if (color != RouletteColor.YELLOW && color != RouletteColor.RED) {
			return Component.literal(text).withStyle(style -> style.withColor(color.argb & 0xFFFFFF).withBold(true).withFont(FONT));
		}
		return Component.empty()
			.append(Component.literal(color == RouletteColor.YELLOW ? "✹ Nuevo cambio de dificultad:" : "✹ Nuevo Momento ReviiL:")
				.withStyle(style -> style.withColor(color == RouletteColor.YELLOW ? 0xED9F2D : 0xEC4B5B).withBold(true).withFont(FONT)))
			.append(Component.literal("\n\n"))
			.append(Component.literal(text).withStyle(style -> style.withColor(0xE8E0D9).withBold(false)));
	}

	private RouletteAnnouncement() {
	}
}
