package net.ruleta;

import java.util.List;

/** Cambios de dificultad y mobs nuevos del DEDsafío 4 Ruleta Fan Project (CC BY-NC-SA 4.0). */
public final class Datos {
	public record Entrada(String id, String titulo, String texto) { }

	private Datos() {
	}

	public static final List<List<Entrada>> CAMBIOS = List.of(
		List.of(
			new Entrada("sin-brotes", "Cambio de dificultad", "Los árboles dejan de soltar brotes, por lo que replantar ya no es una opción."),
			new Entrada("warden-deep-dark", "Cambio de dificultad — Wardens en el Deep Dark", "Reviil altera la genética de los Wardens. A partir de ahora pueden aparecer naturalmente en el Deep Dark."),
			new Entrada("bombas-warden", "Cambio de dificultad — Bombas Warden Voladoras", "Reviil crea una nueva criatura: las Bombas Warden Voladoras, que ahora pueden aparecer naturalmente en el Deep Dark."),
			new Entrada("sin-leche", "Cambio de dificultad — Ya no se puede sacar leche a las vacas", "Reviil ordeña a todas las vacas y, a partir de ahora, los jugadores ya no pueden obtener leche de ellas."),
			new Entrada("sin-golems", "Cambio de dificultad — Sin Gólems de Hierro ni de Nieve", "Reviil lanzó una maldición que impide que las calabazas cobren vida. Los Gólems de Hierro y de Nieve ya no pueden ser invocados."),
			new Entrada("respirar-bajo-agua", "Cambio de dificultad — Respirar bajo el agua", "Reviil alteró las corrientes de agua. Las puertas y trampillas ya no pueden utilizarse para respirar bajo el agua."),
			new Entrada("warden-sculk-shrieker", "Cambio de dificultad — Wardens ahora dejan Sculk Shrieker al morir", "Reviil corrompió el alma de los Wardens. A partir de ahora, al morir dejan un Sculk Shrieker."),
			new Entrada("perla-ignora-armadura", "Ruleta naranja — Cambio de dificultad", "Reviil corrompió la composición de las Ender Pearls. A partir de ahora, su daño ignora la armadura."),
			new Entrada("creepers-electricos", "Cambio de dificultad — Creepers Eléctricos", "Reviil ha electrificado a los Creepers! Los Creepers Normales han sido modificados por Creepers Eléctricos. Con el equipamiento que tenemos, sólo podemos sobrevivir a su explosión utilizando un escudo.")
		),
		List.of(
			new Entrada("zombies-mejorados", "Cambio de dificultad — Zombies mejorados", "Reviil está experimentando con los Zombies. Ahora todos pueden escalar, no se queman con el sol y spawnean con una Espada de Hierro con Filo V."),
			new Entrada("aranas-telaranas", "Cambio de dificultad — Arañas de Cueva al romper telarañas", "Al romper una Tela de Araña existe la probabilidad de que salga una Araña de Cueva."),
			new Entrada("magma", "Cambio de dificultad — Bloques de Magma letales", "El calor de las capas bajas es insoportable. Ahora los Bloques de Magma hacen siete mil millones de cuatrillones de daño. El daño también se aplica al tocar cualquiera de las caras del bloque vertical u horizontalmente."),
			new Entrada("perla", "Cambio de dificultad — Ender Pearls más dañinas", "Reviil ha utilizado el poder de la Ruleta para corromper la composición de las Ender Pearls. Ahora su daño aumentó a 10."),
			new Entrada("blazes-nether", "Cambio de dificultad — Blazes en el Nether", "El Inframundo no descansa. Ahora los Blaze aparecen naturalmente en el Nether."),
			new Entrada("zombies-trepadores", "Cambio de dificultad — Zombies trepadores", "Cuando los zombies aparecen hay un 50% de probabilidad de que puedan escalar paredes como las Arañas y sean inmunes al sol.")
		),
		List.of(
			new Entrada("herobrine", "Cambio de dificultad — Herobrine", "Added Herobrine. Si lo ves, utiliza la Linterna para hacerlo desaparecer. NO lo mires a los ojos."),
			new Entrada("wither-skeleton-nether", "Cambio de dificultad — Wither Skeleton en el Nether", "Los Wither Skeleton ahora aparecen naturalmente en el Nether."),
			new Entrada("dano-lava-fuego", "Cambio de dificultad — Daño de Lava y Fuego", "La Lava y el Fuego han triplicado su daño."),
			new Entrada("extincion-animales", "Cambio de dificultad — Extinción de los animales", "Reviil ha lanzado un gas tóxico que afecta a los animales. Los animales fueron extintos. Ningún animal volverá a aparecer."),
			new Entrada("stray-overworld", "Cambio de dificultad — Esqueletos Stray en el Overworld", "A partir de este momento los Esqueletos Stray pueden salir durante la noche en el Overworld."),
			new Entrada("bogged-noche", "Cambio de dificultad — Esqueletos Bogged durante la noche", "A partir de este momento los Esqueletos Bogged (veneno) aparecen durante la noche."),
			new Entrada("zombies-nether", "Cambio de dificultad — Zombies en el Nether", "Ahora los Zombies aparecen en el Nether, sin importar la luz.")
		),
		List.of(
			new Entrada("aranas-telarana", "Cambio de dificultad — Arañas dejan Tela de Araña", "Ahora las Arañas y Arañas de Cueva dejan una Tela de Araña al golpearte y al Morir."),
			new Entrada("criaturas-detectan-invisibles", "Cambio de dificultad — Criaturas detectan entidades invisibles", "Reviil ha hecho que todas las Criaturas desarrollen su olfato. Ahora pueden detectar la presencia de entidades invisibles. La invisibilidad ya no sirve para esconderse de los Mobs."),
			new Entrada("nucleepers-techo-nether", "Cambio de dificultad — Creepers Nucleares en el Techo del Nether", "Los Creepers Nucleares ahora aparecen en el Techo del Nether. Además, pueden ver a través de las paredes y romper bloques para ir hacia ti."),
			new Entrada("aranas-fuerza", "Cambio de dificultad — Arañas con Fuerza III", "Ahora las Arañas y Arañas de Cueva tienen Fuerza III."),
			new Entrada("dano-caida", "Cambio de dificultad — Daño de Caída", "“El Daño de Caída ha sido multiplicado x2.”"),
			new Entrada("aranas-cueva-overworld", "Cambio de dificultad — Arañas de Cueva en el Overworld", "Ahora las Arañas de Cueva aparecen en el Overworld durante la Noche. Además, todas las arañas tienen Velocidad I infinita.")
		),
		List.of(
			new Entrada("dano-placa", "Cambio de dificultad — Daño de Placa de Presión", "Interactuar con una Placa de Presión ahora te hace 1 billón daño."),
			new Entrada("dano-boton", "Cambio de dificultad — Daño de Botón", "Interactuar con un Botón ahora te hace 4 mil millones de millones de millones de millones de daño."),
			new Entrada("dano-fuego-lava-x10", "Cambio de dificultad — Daño de Fuego y Lava multiplicado", "El daño del Fuego y de la Lava han sido multiplicados x10."),
			new Entrada("fuego-infinito-nether", "Cambio de dificultad — Fuego Infinito al Quemarte en el Nether", "Si te quemas en el Nether el Fuego es Infinito."),
			new Entrada("sin-regeneracion", "Cambio de dificultad — Regeneración Natural desactivada", "La Regeneración Natural ha sido desactivada permanentemente."),
			new Entrada("fuego-sin-armadura-nether", "Cambio de dificultad — Fuego Infinito en el Nether sin Armadura", "Estar en el Nether sin CUALQUIERA de las Piezas de la Armadura te aplica Fuego Infinito.")
		),
		List.of(
			new Entrada("endermite-100-dano", "Cambio de dificultad — Endermite con 100 de daño", "Ahora los Endermite hacen 100 de daño al golpearte."),
			new Entrada("zombies-pico-diamante", "Cambio de dificultad — Zombies con Pico de Diamante", "Ahora los Zombies aparecen con Pico de Diamante... ¿Se estarán preparando para algo?"),
			new Entrada("dano-puertas", "Cambio de dificultad — Daño al interactuar con puertas", "Interactuar con puertas te hace un CHINGO de daño (o sea te mata). Importante: Las Puertas de Hierro y las Puertas de Hierro con Candado (las del código) NO cuentan para este cambio."),
			new Entrada("ahogamiento-aumentado", "Cambio de dificultad — Ahogamiento aumentado", "Ahora pierdes la respiración 5 veces más rápido cuando te sumerges en agua y el daño por ahogamiento ha sido multiplicado x10"),
			new Entrada("sin-absorcion", "Cambio de dificultad — Sin Absorción", "La manzana de oro y la manzana encantada ya no dan el efecto de Absorción")
		),
		List.of(
			new Entrada("zombies-inteligentes", "Cambio de dificultad — Zombies Inteligentes en la Capa 40", "Los Zombies inteligentes, que pueden romper y poner bloques, aparecen de Capa 40 hacia abajo naturalmente... ¿Estarán preparándose para algo?"),
			new Entrada("perla-creeper-nuclear", "Cambio de dificultad — Creeper Nuclear al usar Ender Pearl", "Al aterrizar una Ender Pearl existe una probabilidad del 1% de spawnear un Creeper Nuclear."),
			new Entrada("aranas-fuerza-iv", "Cambio de Dificultad — Fuerza Arácnida", "Ahora todas las Arañas ahora tienen Fuerza IV y las Arañas de Cueva ahora aparecen en el hielo sin importar la Luz"),
			new Entrada("wither-skeleton-noche", "Cambio de Dificultad — Noches de Withers", "Ahora los Wither Skeleton aparecen naturalmente en la noche del Overworld"),
			new Entrada("mis-ojos", "Cambio de Dificultad — Mis Ojos!", "Desde ahora, si miras al Sol tendrás Miopía y Daltonismo durante 3 minutos. Además no podrás ver tu vida durante ese tiempo."),
			new Entrada("enderman-100-dano", "Cambio de dificultad — Enderman con 100 de daño", "Ahora los Enderman hacen 100 de daño al golpearte.")
		)
	);

	public static final List<List<Entrada>> MOBS = List.of(
		List.of(
			new Entrada("mob-creeper-nuclear-nieve", "Nuevo mob — El Creeper Nuclear", "Se ha visto una nueva criatura enviada por Reviil! El Creeper Nuclear. Una criatura que provoca grandes explosiones. La ÚNICA manera de sobrevivir a su explosión es utilizando un Escudo. Sólo sale en biomas de Nieve. IMPORTANTE: Es totalmente real. Si estás incluso a 40 bloques y no te cubres con el escudo, podría matarte. PD: El escudo se romperá si te cubres para evitar el daño de la explosión."),
			new Entrada("mob-creeper-pasto", "Nuevo Mob — Creeper de Pasto", "Se ha visto una nueva criatura enviada por ReviiL: El Creeper de Pasto. Tiene probabilidad de salir al romperse: Bloques de Pasto, Pasto Alto, Pasto Bajo, Arbustos, entre otras hierbas. Son muy veloces y explotan al instante."),
			new Entrada("mob-creeper-rojo", "Nuevo Mob — Creeper Rojo con Efectos Desconocidos", "Creeper Rojo con Efectos Desconocidos. Al explotar aplica un efecto desconocido.")
		)
	);

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
