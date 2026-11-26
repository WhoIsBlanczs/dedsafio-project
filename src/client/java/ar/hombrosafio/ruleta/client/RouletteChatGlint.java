package ar.hombrosafio.ruleta.client;

import ar.hombrosafio.ruleta.RouletteAnnouncement;
import net.minecraft.util.FormattedCharSequence;

/** Brillo animado que recorre el texto de los anuncios de la ruleta en el chat. */
public final class RouletteChatGlint {
	public static FormattedCharSequence wrap(FormattedCharSequence original) {
		double seconds = System.nanoTime() / 1.0E9;
		return sink -> original.accept((index, style, codePoint) -> {
			if (!style.getFont().equals(RouletteAnnouncement.FONT)) {
				return sink.accept(index, style, codePoint);
			}
			int rgb = style.getColor() == null ? 0xFFFFFF : style.getColor().getValue();
			double shine = 0.7 * Math.pow(Math.max(0.0, Math.cos((seconds * 0.65 - index * 0.045) * Math.PI * 2.0)), 18.0);
			int r = (int) ((rgb >> 16 & 0xFF) * (1.0 - shine) + 255 * shine);
			int g = (int) ((rgb >> 8 & 0xFF) * (1.0 - shine) + 255 * shine);
			int b = (int) ((rgb & 0xFF) * (1.0 - shine) + 255 * shine);
			return sink.accept(index, style.withColor(r << 16 | g << 8 | b), codePoint);
		});
	}

	private RouletteChatGlint() {
	}
}
