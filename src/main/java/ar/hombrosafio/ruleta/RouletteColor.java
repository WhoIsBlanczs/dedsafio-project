package ar.hombrosafio.ruleta;

/** Las 8 secciones de la ruleta, en el sentido de las agujas del reloj empezando por arriba. */
public enum RouletteColor {
	RED("rojo", -1761990, "MOMENTO REVIIL"),
	PINK("rosa", -1235045, "NUTRIA"),
	PURPLE("morado", -5949463, "SANCIÓN"),
	BLUE("azul", -12294929, "AVISO"),
	CYAN("cian", -14033953, "MISIÓN DEL DÍA"),
	GREEN("verde", -10952893, "ANUNCIO"),
	YELLOW("amarillo", -728254, "CAMBIO DE DIFICULTAD"),
	ORANGE("naranja", -681431, "SORPRESA");

	public final String command;
	public final int argb;
	public final String label;
	public static final int SPIN = 218;
	public static final int HOLD = 60;
	public static final int TOTAL = 278;

	RouletteColor(String command, int argb, String label) {
		this.command = command;
		this.argb = argb;
		this.label = label;
	}

	public static RouletteColor byCommand(String name) {
		for (RouletteColor c : values()) {
			if (c.command.equals(name)) {
				return c;
			}
		}
		return null;
	}

	public double finalAngle() {
		return Math.PI * 20 - this.ordinal() * Math.PI / 4.0;
	}

	public double angle(float elapsed, int spinTicks) {
		double t = Math.clamp(elapsed / spinTicks, 0.0F, 1.0F);
		double a = 0.06;
		double b = 0.36;
		double distance = t < a
			? 0.5 * (t - a / Math.PI * Math.sin(Math.PI * t / a))
			: (t < b ? t - a / 2.0 : b - a / 2.0 + 0.5 * (t - b + (1.0 - b) / Math.PI * Math.sin(Math.PI * (t - b) / (1.0 - b))));
		return this.finalAngle() * distance / (b - a / 2.0 + (1.0 - b) / 2.0);
	}
}
