package net.ruleta;

import java.util.List;

/**
 * Los Momentos Revil de Hombrosafío (ar.hombrosafio.RouletteRevilMoment), con sus
 * textos originales. El campo "enumHS" es el nombre de la constante en Hombrosafío.
 */
public final class MomentosHS {
	/** Qué datos hay que pedir antes de activar el momento. */
	public enum Parametro { NINGUNO, MINUTOS, REFUGIO, BIOMA, VOTACION }

	public record Momento(String id, String enumHS, String titulo, String descripcion, Parametro parametro, int minutosPorDefecto, String grupo) { }

	public static final List<Momento> TODOS = List.of(
			new Momento("refugio", "REFUGE", "Refugio",
					"El Momento Mico desató una catástrofe en todo el mundo: ¡tenemos que ir al refugio antes de que se agote el tiempo o morirán, incluso con tótem! Sigan las coordenadas de la barra.",
					Parametro.REFUGIO, 5, "lugar"),
			new Momento("intercambio_posiciones", "POSITION_SWAP", "Intercambio aleatorio de posiciones",
					"El Momento Mico distorsiona el espacio: cada minuto los jugadores fuera del refugio intercambian posiciones al azar entre dimensiones. ¡Cuidado dónde pisan!",
					Parametro.NINGUNO, 0, ""),
			new Momento("gas_debajo_250", "TOXIC_GAS_BELOW_250", "Gas tóxico por debajo de Y=250",
					"El Momento Mico invocó un gas tóxico en todo el mundo: suban a Y=250 o más en 3 minutos. Quienes sigan abajo perderán 1 corazón por segundo ignorando armadura.",
					Parametro.NINGUNO, 0, "gas"),
			new Momento("gas_arriba_menos_10", "TOXIC_GAS_ABOVE_MINUS_10", "Gas tóxico por encima de Y=-10",
					"El Momento Mico cubrió la superficie con gas tóxico: bajen a Y=-10 o menos en 3 minutos. Quienes sigan arriba perderán 1 corazón por segundo ignorando armadura.",
					Parametro.NINGUNO, 0, "gas"),
			new Momento("dimension_jurasica", "JURASSIC_EXODUS", "Todos a la dimensión jurásica",
					"El Momento Mico exige entrar a la Selva Ancestral en 3 minutos y quedarse allí. Al vencer el plazo, estar fuera causa 4 corazones/s de daño, sin importar la armadura.",
					Parametro.NINGUNO, 0, "lugar"),
			new Momento("bombas_nucleares", "NUCLEAR_BOMBS", "Tres bombas nucleares",
					"El Momento Mico dejó 3 bombas en Overworld: desactiven en 3 min; errar detona. Radio 1500: 20 corazones + 1/s. Dimensiones cerradas; los que ya estaban fuera se salvan. Coordenadas en chat.",
					Parametro.NINGUNO, 0, ""),
			new Momento("lluvia_acida", "ACID_RAIN", "Lluvia ácida en el Overworld",
					"El Momento Mico desató lluvia ácida en todo el Overworld: sin techo, 1 corazón cada 8 ticks, ignorando armadura. Dimensiones cerradas; quienes ya estaban en otra se salvan.",
					Parametro.NINGUNO, 0, ""),
			new Momento("bioma", "TARGET_BIOME", "Busquen el bioma indicado",
					"El Momento Mico exige llegar al bioma indicado a tiempo y quedarse allí hasta el final. Tras el plazo, estar fuera causa 2 corazones/s de daño ignorando armadura.",
					Parametro.BIOMA, 3, "lugar"),
			new Momento("votacion", "DEATH_VOTE", "Votación obligatoria de muerte",
					"El Momento Mico exige un voto único e irrevocable por otra persona. Al vencer el plazo, los más votados mueren al instante, incluso con tótem. Los empates del último puesto se sortean.",
					Parametro.VOTACION, 1, ""),
			new Momento("creepers_electricos", "ELECTRIC_CREEPERS", "Creepers eléctricos de 1 de vida",
					"El Momento Mico desató una tormenta eléctrica: cada 45 a 120 segundos aparecerá un creeper cargado sobre cada jugador. Tienen solo 1 de vida pero su explosión es letal.",
					Parametro.NINGUNO, 0, ""),
			new Momento("dimension_gleba", "GLEBA_EXODUS", "Todos a Gleba",
					"El Momento Mico exige viajar a Gleba en 3 minutos y quedarse allí. Al vencer el plazo, estar fuera causa 4 corazones/s de daño, sin importar la armadura.",
					Parametro.MINUTOS, 3, "lugar"),
			new Momento("dimension_limbo", "LIMBO_EXODUS", "Todos al Limbo",
					"El Momento Mico exige ir al Limbo en 3 minutos y quedarse allí: rompan una cama y dejen que la Parca los alcance. Al vencer el plazo, estar fuera causa 4 corazones/s de daño, sin importar armadura.",
					Parametro.MINUTOS, 3, "lugar"),
			new Momento("agua_limbo_mortal", "LIMBO_WATER", "El agua del Limbo se volvió mortal",
					"El Momento Mico envenenó el mar del Limbo: su agua quita 3 corazones por segundo, sin importar la armadura, y puede matar. Al terminar, vuelve a su efecto normal y deja de matar.",
					Parametro.NINGUNO, 0, ""),
			new Momento("montana", "MOUNTAIN", "Todos a la montaña",
					"El Momento Mico exige que todos estén en la montaña en 5 minutos.",
					Parametro.MINUTOS, 5, "lugar"));

	private MomentosHS() {
	}

	public static Momento porId(String id) {
		for (Momento m : TODOS) {
			if (m.id().equals(id)) {
				return m;
			}
		}

		return null;
	}

	/** Dos momentos que piden estar en sitios distintos, o los dos gases, no se pueden juntar. */
	public static boolean chocan(String a, String b) {
		if (a.equals(b)) {
			return false;
		}

		Momento ma = porId(a);
		Momento mb = porId(b);
		return ma != null && mb != null && !ma.grupo().isEmpty() && ma.grupo().equals(mb.grupo());
	}

	/** Texto del momento ya con los minutos o el bioma elegidos. */
	public static String texto(Momento m, int segundos, int extraNum, String extraTexto) {
		String d = m.descripcion();

		switch (m.parametro()) {
			case MINUTOS -> {
				int min = Math.max(1, segundos / 60);
				String viejo = m.minutosPorDefecto() + " minutos";
				d = d.replace(viejo, min + (min == 1 ? " minuto" : " minutos"));
			}
			case REFUGIO -> {
				if (segundos > 0 && segundos != 300) {
					String plazo = segundos % 60 == 0 ? (segundos / 60) + " minutos" : segundos + " segundos";
					d = "El Momento Mico desató una catástrofe en todo el mundo: ¡tenemos que ir al refugio en " + plazo
							+ " o morirán, incluso con tótem! Sigan las coordenadas de la barra.";
				}
			}
			case BIOMA -> d = "El Momento Mico exige llegar al bioma " + extraTexto + " en " + segundos
					+ " segundos. Después del plazo, estar fuera causa 2 corazones de daño por segundo ignorando armadura. Permanecé en ese bioma hasta que finalice el cambio.";
			case VOTACION -> d = "El Momento Mico exige votar por otra persona en " + segundos + " segundos. Los " + extraNum
					+ " jugadores más votados morirán instantáneamente, incluso con tótem. Confirmá un único voto desde la lista; no podés votarte a vos mismo. Los empates en el último puesto se resuelven por sorteo.";
			default -> {
			}
		}

		return d;
	}
}
