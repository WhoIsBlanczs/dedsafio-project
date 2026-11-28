package net.ruleta;

import java.util.List;

/** Cambios de dificultad (de Hombrosafío) y mobs nuevos (del DEDsafío 4 Ruleta Fan Project, CC BY-NC-SA 4.0). */
public final class Datos {
	public record Entrada(String id, String titulo, String texto) { }

	private Datos() {
	}

	/** Los cambios de dificultad de Hombrosafío (ar.hombrosafio.RouletteDifficultyChange). */
	public static final List<List<Entrada>> CAMBIOS = List.of(
		List.of(
			new Entrada("regeneracion", "Regeneración natural desactivada", "El Momento Mico consumió la vitalidad natural: la comida ya no regenera vida. Solo curan las pociones o efectos directos."),
			new Entrada("perlas_10", "Las Ender Pearls hacen 10 de daño", "El Momento Mico corrompió el End: las Ender Pearls ahora causan 10 de daño base (5 corazones) al teletransportarse."),
			new Entrada("perlas_ignoran_armadura", "El daño de Ender Pearls ignora la armadura", "El Momento Mico desgarró las defensas del vacío: el daño de las Ender Pearls ahora ignora por completo la armadura."),
			new Entrada("fuego_x3", "El fuego y la lava hacen el triple de daño", "El Momento Mico avivó las llamas infernales: el fuego, la lava y el magma ahora infligen el triple de daño."),
			new Entrada("fuego_x10", "El fuego y la lava hacen 10 veces más daño", "El Momento Mico desató un calor abrasador: el fuego, la lava y el magma hacen 10 veces más daño. El contacto es mortal."),
			new Entrada("caida_x2", "El daño por caída se duplica", "El Momento Mico alteró la gravedad: el daño por caída se duplica en todo el mundo. Midan cada salto."),
			new Entrada("aranas_fuerza_3", "Las arañas tienen Fuerza III", "El Momento Mico mutó a los arácnidos: todas las arañas obtienen Fuerza III permanente."),
			new Entrada("aranas_fuerza_4", "Las arañas tienen Fuerza IV", "El Momento Mico desató la furia de las arañas: ahora poseen Fuerza IV permanente. Un golpe puede ser mortal."),
			new Entrada("oxigeno_x5", "El oxígeno se consume cinco veces más rápido", "El Momento Mico volvió sofocantes las aguas: el oxígeno se consume 5 veces más rápido al sumergirse.")
		),
		List.of(
			new Entrada("ahogamiento_x10", "El ahogamiento hace 10 veces más daño", "El Momento Mico volvió letales las profundidades: el daño por ahogamiento se multiplica por 10. No se queden sin aire."),
			new Entrada("leche_desactivada", "La leche deja de quitar efectos", "El Momento Mico anuló los efectos lácteos: beber leche ya no quitará ningún efecto ni veneno."),
			new Entrada("golems_desactivados", "No se pueden invocar Gólems de Hierro ni de Nieve", "El Momento Mico expulsó a los protectores: no se pueden crear gólems de hierro ni de nieve con bloques."),
			new Entrada("sin_brotes", "Los árboles ya no dejan brotes", "El Momento Mico marchitó los bosques: talar árboles ya no soltará brotes. Cuiden la madera que tienen."),
			new Entrada("vacas_sin_leche", "No se puede obtener leche de las vacas", "El Momento Mico secó al ganado: las vacas y champivacas ya no permiten ordeñar leche con un cubo."),
			new Entrada("puertas_sin_aire", "Puertas y trampillas ya no permiten respirar bajo el agua", "El Momento Mico inundó todo resquicio: puertas y trampillas ya no crean bolsas de aire para respirar bajo el agua."),
			new Entrada("magma_instakill", "Los bloques de magma matan al instante", "El Momento Mico sobrecalentó las piedras del Nether: pisar un bloque de magma mata al instante ignorando armadura."),
			new Entrada("aranas_velocidad_1", "Las arañas tienen Velocidad I permanente", "El Momento Mico agilizó a las arañas: obtienen Velocidad I permanente para perseguir a sus presas."),
			new Entrada("creepers_nucleares", "Los creepers normales son ahora Creepers Nucleares", "El Momento Mico irradió a los creepers: todo creeper que aparezca solo será un Creeper Nuclear, con una explosión que mata a 40 bloques. Escudo y distancia.")
		),
		List.of(
			new Entrada("creepers_nucleares_10", "Uno de cada diez creepers es Nuclear", "El Momento Mico dejó radiación suelta: uno de cada diez creepers que aparezcan solos será un Creeper Nuclear. Nunca sabés cuál."),
			new Entrada("creepers_jurasicos", "Todos los creepers son Creepers Jurásicos", "El Momento Mico trajo al pasado: todo creeper que aparezca solo será un Creeper Jurásico, en cualquier dimensión."),
			new Entrada("zombis_trepadores", "Los zombis trepan, no se queman y llevan espada", "El Momento Mico fortaleció a los zombis: trepan paredes como arañas, no se queman con el sol y llevan una espada que quita 3 corazones a una armadura del Limbo completa."),
			new Entrada("zombis_rapidos", "Los zombis tienen Velocidad II permanente", "El Momento Mico aceleró a los muertos: todos los zombis corren con Velocidad II permanente. Ya no alcanza con caminar para escaparles."),
			new Entrada("zombis_diamante", "Los zombis llevan armadura de diamante Protección IV", "El Momento Mico acorazó a los muertos: todos los zombis llevan armadura completa de diamante con Protección IV. No la sueltan al morir."),
			new Entrada("phantoms_tnt", "Los phantoms llevan TNT y explotan al golpearte", "El Momento Mico armó a los phantoms: llevan una TNT encima y, en vez de morderte, explotan como un creeper eléctrico y mueren."),
			new Entrada("zombis_cualquier_luz", "Los zombis aparecen sin importar la luz", "El Momento Mico despertó a los muertos: los zombis aparecen en cualquier lugar, incluso a plena luz del día."),
			new Entrada("creepers_cualquier_luz", "Los creepers aparecen sin importar la luz", "El Momento Mico sacó a los creepers de las sombras: aparecen en cualquier lugar, incluso a plena luz del día y junto a las antorchas."),
			new Entrada("luna_mortal", "Mirar la Luna mata al instante", "El Momento Mico maldijo la Luna: quien la mire de frente, de noche y a cielo abierto, muere al instante, incluso con tótem.")
		),
		List.of(
			new Entrada("flashbang_overworld", "Los Flashbang aparecen en el Overworld", "El Momento Mico dejó escapar a los Flashbang del Limbo: aparecen en todo el Overworld, de día y de noche, y ciegan al explotar."),
			new Entrada("zombificacion", "Los zombis contagian la Zombificación", "El Momento Mico volvió contagiosos a los zombis: su golpe te infecta para siempre con 4 horas de Zombificación imborrable que al terminar mata o salta un tótem. Cada píldora suma 4 h."),
			new Entrada("luna_limbo", "Mirar la Luna te manda al Limbo", "El Momento Mico abrió un portal en la Luna: quien la mire de frente, de noche y a cielo abierto, es desterrado al Limbo al instante."),
			new Entrada("corazones_limbo_una_barra", "Los Corazones del Limbo curan solo una barra de vida", "El Momento Mico debilitó los Corazones del Limbo: cada uno devuelve y cura como mucho una barra de vida (10 corazones), no toda.")
		)
	);

	public static final List<List<Entrada>> MOBS = List.of(
		List.of(
			new Entrada("mob-creeper-nuclear-nieve", "Nuevo mob — El Creeper Nuclear", "Se ha visto una nueva criatura enviada por Reviil! El Creeper Nuclear. Una criatura que provoca grandes explosiones. La ÚNICA manera de sobrevivir a su explosión es utilizando un Escudo. Sólo sale en biomas de Nieve. IMPORTANTE: Es totalmente real. Si estás incluso a 40 bloques y no te cubres con el escudo, podría matarte. PD: El escudo se romperá si te cubres para evitar el daño de la explosión."),
			new Entrada("mob-creeper-pasto", "Nuevo Mob — Creeper de Pasto", "Se ha visto una nueva criatura enviada por ReviiL: El Creeper de Pasto. Tiene probabilidad de salir al romperse: Bloques de Pasto, Pasto Alto, Pasto Bajo, Arbustos, entre otras hierbas. Son muy veloces y explotan al instante."),
			new Entrada("mob-creeper-rojo", "Nuevo Mob — Creeper Rojo con Efectos Desconocidos", "Creeper Rojo con Efectos Desconocidos. Al explotar aplica un efecto desconocido.")
		)
	);

	public static String tituloCambio(String id) {
		Entrada e = porId(id);
		return e != null ? e.titulo() : id;
	}

	public static boolean esCambio(String id) {
		for (List<Entrada> pagina : CAMBIOS) {
			for (Entrada e : pagina) {
				if (e.id().equals(id)) {
					return true;
				}
			}
		}

		return false;
	}

	public static Entrada porId(String id) {
		for (List<List<Entrada>> lista : List.of(CAMBIOS, MOBS)) {
			for (List<Entrada> pagina : lista) {
				for (Entrada e : pagina) {
					if (e.id().equals(id)) {
						return e;
					}
				}
			}
		}

		return null;
	}
}
