package ar.hombrosafio.ruleta;

/** Cambios de dificultad de la ruleta amarilla que funcionan solo con Minecraft vanilla. */
public enum DifficultyChange {
	NATURAL_REGEN_OFF("regeneracion", "Regeneración natural desactivada",
		"El Momento Mico consumió la vitalidad natural: la comida ya no regenera vida. Solo curan las pociones o efectos directos."),
	PEARL_DAMAGE_10("perlas_10", "Las Ender Pearls hacen 10 de daño",
		"El Momento Mico corrompió el End: las Ender Pearls ahora causan 10 de daño base (5 corazones) al teletransportarse."),
	PEARL_BYPASS_ARMOR("perlas_ignoran_armadura", "El daño de Ender Pearls ignora la armadura",
		"El Momento Mico desgarró las defensas del vacío: el daño de las Ender Pearls ahora ignora por completo la armadura."),
	FIRE_DAMAGE_X3("fuego_x3", "El fuego y la lava hacen el triple de daño",
		"El Momento Mico avivó las llamas infernales: el fuego, la lava y el magma ahora infligen el triple de daño."),
	FIRE_DAMAGE_X10("fuego_x10", "El fuego y la lava hacen 10 veces más daño",
		"El Momento Mico desató un calor abrasador: el fuego, la lava y el magma hacen 10 veces más daño. El contacto es mortal."),
	FALL_DAMAGE_X2("caida_x2", "El daño por caída se duplica",
		"El Momento Mico alteró la gravedad: el daño por caída se duplica en todo el mundo. Midan cada salto."),
	SPIDERS_STRENGTH_3("aranas_fuerza_3", "Las arañas tienen Fuerza III",
		"El Momento Mico mutó a los arácnidos: todas las arañas obtienen Fuerza III permanente."),
	SPIDERS_STRENGTH_4("aranas_fuerza_4", "Las arañas tienen Fuerza IV",
		"El Momento Mico desató la furia de las arañas: ahora poseen Fuerza IV permanente. Un golpe puede ser mortal."),
	AIR_X5("oxigeno_x5", "El oxígeno se consume cinco veces más rápido",
		"El Momento Mico volvió sofocantes las aguas: el oxígeno se consume 5 veces más rápido al sumergirse."),
	DROWNING_X10("ahogamiento_x10", "El ahogamiento hace 10 veces más daño",
		"El Momento Mico volvió letales las profundidades: el daño por ahogamiento se multiplica por 10. No se queden sin aire."),
	MILK_DISABLED("leche_desactivada", "La leche deja de quitar efectos",
		"El Momento Mico anuló los efectos lácteos: beber leche ya no quitará ningún efecto ni veneno."),
	GOLEMS_DISABLED("golems_desactivados", "No se pueden invocar Gólems de Hierro ni de Nieve",
		"El Momento Mico expulsó a los protectores: no se pueden crear gólems de hierro ni de nieve con bloques."),
	NO_SAPLINGS("sin_brotes", "Los árboles ya no dejan brotes",
		"El Momento Mico marchitó los bosques: talar árboles ya no soltará brotes. Cuiden la madera que tienen."),
	COWS_NO_MILK("vacas_sin_leche", "No se puede obtener leche de las vacas",
		"El Momento Mico secó al ganado: las vacas y champivacas ya no permiten ordeñar leche con un cubo."),
	NO_DOOR_AIR("puertas_sin_aire", "Puertas y trampillas ya no permiten respirar bajo el agua",
		"El Momento Mico inundó todo resquicio: puertas y trampillas ya no crean bolsas de aire para respirar bajo el agua."),
	MAGMA_INSTANT_KILL("magma_instakill", "Los bloques de magma matan al instante",
		"El Momento Mico sobrecalentó las piedras del Nether: pisar un bloque de magma mata al instante ignorando armadura."),
	SPIDERS_SPEED_1("aranas_velocidad_1", "Las arañas tienen Velocidad I permanente",
		"El Momento Mico agilizó a las arañas: obtienen Velocidad I permanente para perseguir a sus presas."),
	ZOMBIES_FAST("zombis_rapidos", "Los zombis tienen Velocidad II permanente",
		"El Momento Mico aceleró a los muertos: todos los zombis corren con Velocidad II permanente. Ya no alcanza con caminar para escaparles."),
	ZOMBIES_DIAMOND("zombis_diamante", "Los zombis llevan armadura de diamante Protección IV",
		"El Momento Mico acorazó a los muertos: todos los zombis llevan armadura completa de diamante con Protección IV. No la sueltan al morir."),
	ZOMBIES_ANY_LIGHT("zombis_cualquier_luz", "Los zombis aparecen sin importar la luz",
		"El Momento Mico despertó a los muertos: los zombis aparecen en cualquier lugar, incluso a plena luz del día."),
	CREEPERS_ANY_LIGHT("creepers_cualquier_luz", "Los creepers aparecen sin importar la luz",
		"El Momento Mico sacó a los creepers de las sombras: aparecen en cualquier lugar, incluso a plena luz del día y junto a las antorchas."),
	TEXT("texto", "Texto personalizado", "");

	public final String id;
	public final String announcement;
	public final String description;

	DifficultyChange(String id, String announcement, String description) {
		this.id = id;
		this.announcement = announcement;
		this.description = description;
	}

	public static DifficultyChange byId(String id) {
		for (DifficultyChange c : values()) {
			if (c.id.equals(id)) {
				return c;
			}
		}
		return null;
	}
}
