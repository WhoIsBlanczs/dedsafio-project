package net.ruleta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Definición de todos los ajustes del menú "Ajustes" (tecla G), con los textos,
 * valores por defecto y unidades tal como aparecen en el mod original.
 */
public final class Ajustes {
	public enum Tipo { NUMERO, INTERRUPTOR }

	public enum Unidad {
		NADA(""), BLOQUES(" bloques"), MINUTOS(" min"), SEGUNDOS(" s"), CORAZONES(" corazones"),
		PORCENTAJE(" %"), CAPA(" capa"), X_RADIO(" x radio"), X(" x"), X_ALTURA(" x altura"),
		BLOQUES_TICK(" bloques/tick"), BLOQUES_S(" bloques/s"), TICKS(" ticks");

		public final String sufijo;

		Unidad(String sufijo) {
			this.sufijo = sufijo;
		}
	}

	public record Def(String id, String categoria, String nombre, String descripcion, Tipo tipo,
			double min, double max, double paso, double porDefecto, Unidad unidad) {
		public String formatear(double v) {
			if (tipo == Tipo.INTERRUPTOR) {
				return v >= 0.5 ? "Activado" : "Desactivado";
			}

			if (unidad == Unidad.MINUTOS) {
				int total = (int) Math.round(v);
				return (total / 60) + ":" + String.format(Locale.ROOT, "%02d", total % 60) + unidad.sufijo;
			}

			return numero(v) + unidad.sufijo;
		}

		public double ajustar(double v) {
			if (tipo == Tipo.INTERRUPTOR) {
				return v >= 0.5 ? 1 : 0;
			}

			double r = Math.round((v - min) / paso) * paso + min;
			r = Math.max(min, Math.min(max, r));
			return Math.round(r * 1000.0) / 1000.0;
		}
	}

	public record Categoria(String nombre, List<Def> ajustes) { }

	private static final List<Categoria> CATEGORIAS = new ArrayList<>();
	private static final Map<String, Def> POR_ID = new LinkedHashMap<>();
	private static List<Def> actual;
	private static String catActual;

	private static void cat(String nombre) {
		actual = new ArrayList<>();
		catActual = nombre;
		CATEGORIAS.add(new Categoria(nombre, actual));
	}

	private static void num(String id, String nombre, String desc, double min, double max, double paso, double def, Unidad u) {
		Def d = new Def(id, catActual, nombre, desc, Tipo.NUMERO, min, max, paso, def, u);
		actual.add(d);
		POR_ID.put(id, d);
	}

	private static void bool(String id, String nombre, String desc, boolean def) {
		Def d = new Def(id, catActual, nombre, desc, Tipo.INTERRUPTOR, 0, 1, 1, def ? 1 : 0, Unidad.NADA);
		actual.add(d);
		POR_ID.put(id, d);
	}

	static {
		cat("Bunker");
		num("bunker.distancia", "Distancia mínima entre bunkers", "No se puede construir un bunker a menos de esta distancia de otro (en la misma dimensión). 0 = sin límite.", 0, 20000, 50, 1500, Unidad.BLOQUES);
		num("bunker.llegar", "Tiempo para llegar al bunker", "Valor por defecto del contador antes de que se cierre la puerta (si al activar el momento no se escribe otro).", 0, 7200, 15, 300, Unidad.MINUTOS);
		num("bunker.cerrada", "Tiempo con la puerta cerrada", "Valor por defecto de lo que dura la puerta cerrada (luego se abre).", 0, 7200, 15, 150, Unidad.MINUTOS);
		num("bunker.abierta_final", "Puerta abierta al terminar", "Cuánto se queda abierta la puerta del momento cuando termina, antes de cerrarse sola y sacar a quien siga dentro.", 0, 3600, 15, 60, Unidad.MINUTOS);
		num("bunker.cierre_auto", "Cierre automático de la puerta", "Si abres a mano la puerta del bunker (fuera del contador), se vuelve a cerrar sola pasado este tiempo. Si la cierras tú antes, no pasa nada.", 1, 120, 1, 5, Unidad.SEGUNDOS);
		num("bunker.zona_sin_impactos", "Zona sin impactos", "Los meteoritos y los misiles de los Phora nunca caen a menos de estos bloques de cualquier bunker (ni encima). 0 = sin protección.", 0, 300, 5, 80, Unidad.BLOQUES);
		num("bunker.reapertura", "Reapertura de la puerta", "Si cierran a mano la puerta del bunker mientras el contador aún corre (el servidor la quiere abierta), se vuelve a abrir pasado este tiempo.", 1, 120, 1, 5, Unidad.SEGUNDOS);
		bool("bunker.cualquiera_abre", "Cualquiera puede abrir la puerta", "Desactivado: solo administradores y jugadores en creativo pueden abrir la puerta del bunker a mano.", false);
		num("bunker.nivel_construir", "Nivel para construir dentro", "Nivel de permisos mínimo para colocar bloques, cubos, cofres... dentro de un bunker (los jugadores en creativo siempre pueden).", 0, 4, 1, 2, Unidad.NADA);
		bool("bunker.sacar", "Sacar a los jugadores fuera de un momento", "Si no hay un Momento Reviil del bunker en marcha, quien esté dentro de un bunker (sin creativo) es sacado afuera.", true);
		num("bunker.aviso", "Duración del aviso de construcción", "Cuánto dura en pantalla el texto «No puedes colocar bloques en esta zona».", 1, 10, 1, 2, Unidad.SEGUNDOS);

		cat("Daño y rayos");
		num("dano.fuera_bunker", "Daño fuera del bunker", "Daño del Rayo mortal y de los Agujeros Negros a quien no está dentro del bunker, cada vez que golpea.", 0, 20, 0.5, 1, Unidad.CORAZONES);
		num("dano.cada", "Cada cuánto hace daño", "Segundos entre cada golpe de daño fuera del bunker.", 1, 60, 1, 1, Unidad.SEGUNDOS);
		bool("dano.rayos", "Caen rayos en el Rayo mortal", "Rayos visuales (sin fuego ni daño de rayo) alrededor de quien está fuera del bunker.", true);
		num("dano.prob_rayo", "Probabilidad de rayo", "Probabilidad de que cada golpe de daño venga con un rayo.", 0, 100, 1, 35, Unidad.PORCENTAJE);
		num("dano.dispersion", "Dispersión de los rayos", "A qué distancia máxima del jugador puede caer cada rayo.", 0, 64, 1, 16, Unidad.BLOQUES);
		num("dano.gas", "Daño del gas tóxico", "Daño por segundo del Gas tóxico de Reviil a quien no esté en su capa.", 0, 20, 0.5, 1, Unidad.CORAZONES);
		num("dano.herobrine", "Daño de Herobrine", "Daño de cada golpe de Herobrine (ignora la armadura).", 0, 20, 0.5, 2, Unidad.CORAZONES);

		cat("Momentos Reviil");
		num("reviil.contador_gas", "Contador del gas tóxico", "Valor por defecto del temporizador del gas (tiempo para llegar a la capa pedida).", 0, 7200, 15, 300, Unidad.MINUTOS);
		num("reviil.capa_subir", "Capa del gas (subir)", "Quien esté por debajo de esta altura recibe daño.", -64, 320, 1, 250, Unidad.CAPA);
		num("reviil.capa_bajar", "Capa del gas (bajar)", "Quien esté en esta altura o por encima recibe daño.", -320, 320, 1, 0, Unidad.CAPA);
		num("reviil.creeper_electrico", "Creeper Eléctrico cada...", "De media, cada cuántos segundos le cae un Creeper Eléctrico a cada jugador.", 5, 600, 5, 75, Unidad.MINUTOS);
		num("reviil.ceguera", "Duración de la ceguera", "Segundos de ceguera del momento «Ceguera y Creepers Eléctricos».", 1, 120, 1, 15, Unidad.SEGUNDOS);
		num("reviil.eclipse_1_2", "Eclipse: 1ª → 2ª ruleta", "Valor por defecto del tiempo entre la 1ª y la 2ª ruleta del Eclipse.", 0, 3600, 15, 60, Unidad.MINUTOS);
		num("reviil.eclipse_2_3", "Eclipse: 2ª → 3ª ruleta", "Valor por defecto del tiempo entre la 2ª y la 3ª ruleta del Eclipse.", 0, 3600, 15, 60, Unidad.MINUTOS);
		num("reviil.eclipse_3_4", "Eclipse: 3ª → 4ª ruleta", "Valor por defecto del tiempo entre la 3ª y la 4ª ruleta del Eclipse.", 0, 3600, 15, 60, Unidad.MINUTOS);
		num("reviil.creeper_hierba", "Creeper de hierba: probabilidad", "Probabilidad de que salga un creeper al romper hierba (con ese cambio activo).", 0, 100, 1, 8, Unidad.PORCENTAJE);
		num("reviil.creeper_rojo", "Creeper rojo: probabilidad", "Probabilidad de que un creeper nuevo sea rojo (con ese cambio activo).", 0, 100, 1, 25, Unidad.PORCENTAJE);

		cat("Agujeros negros");
		num("agujeros.min", "Mínimo de agujeros", "Cuántos agujeros negros hay a la vez como mínimo.", 1, 50, 1, 3, Unidad.NADA);
		num("agujeros.max", "Máximo de agujeros", "Cuántos agujeros negros hay a la vez como máximo.", 1, 50, 1, 12, Unidad.NADA);
		num("agujeros.por_jugador", "Agujeros por jugador", "Cuántos agujeros salen por cada jugador conectado (entre el mínimo y el máximo).", 0, 20, 1, 2, Unidad.NADA);
		num("agujeros.separacion", "Separación entre agujeros", "Distancia mínima entre dos agujeros negros.", 0, 1000, 10, 150, Unidad.BLOQUES);
		num("agujeros.dist_bunker", "Distancia mínima al bunker", "Los agujeros nunca salen más cerca del bunker que esto.", 0, 10000, 10, 200, Unidad.BLOQUES);
		num("agujeros.reponer", "Reponer cada", "Cada cuántos segundos el momento revisa los agujeros: quita los que quedaron lejos de todos y pone nuevos cerca de los jugadores.", 1, 600, 1, 10, Unidad.SEGUNDOS);
		num("agujeros.dist_quitar", "Distancia para quitarlos", "Un agujero a más de esta distancia de todos los jugadores se quita (y sale otro cerca de alguien).", 0, 10000, 10, 700, Unidad.BLOQUES);
		num("agujeros.tam_mediano", "Tamaño mediano (x radio)", "Los agujeros medianos miden el radio de atracción multiplicado por esto.", 1, 6, 0.1, 2, Unidad.NADA);
		num("agujeros.tam_gigante", "Tamaño gigante (x radio)", "Los agujeros gigantes miden el radio de atracción multiplicado por esto.", 1, 10, 0.1, 3.5, Unidad.NADA);
		num("agujeros.prob_mediano", "Probabilidad de mediano", "De cada 100 agujeros nuevos, cuántos salen medianos.", 0, 100, 1, 30, Unidad.PORCENTAJE);
		num("agujeros.prob_gigante", "Probabilidad de gigante", "De cada 100 agujeros nuevos, cuántos salen gigantes (el resto salen normales).", 0, 100, 1, 15, Unidad.PORCENTAJE);
		num("agujeros.capa_min", "Capa mínima", "Los agujeros aparecen de esta altura para arriba.", 0, 320, 1, 100, Unidad.CAPA);
		num("agujeros.dano", "Daño dentro del agujero", "Daño de cada golpe a quien queda atrapado en el centro de un Agujero Negro.", 0, 50, 0.5, 2.5, Unidad.CORAZONES);
		num("agujeros.cada", "Cada cuánto daña", "Segundos entre dos golpes del centro del agujero.", 1, 60, 1, 1, Unidad.SEGUNDOS);
		num("agujeros.radio", "Radio de atracción", "Radio de los agujeros que crea el momento (los del comando /agujero llevan el suyo).", 1, 64, 1, 15, Unidad.BLOQUES);
		num("agujeros.velocidad", "Velocidad máxima de arrastre", "Lo rápido que puede llegar a tirar de ti un agujero (bloques por tick).", 0, 4, 0.1, 1.4, Unidad.NADA);

		cat("Explosiones");
		num("explosion.misil_potencia", "Misil: potencia", "Potencia de la explosión del misil de los Phora (un TNT son 4).", 0, 40, 1, 10, Unidad.NADA);
		num("explosion.misil_radio", "Misil: radio de daño", "Hasta dónde llega el daño del impacto (crece con el tamaño del misil).", 0, 100, 1, 26, Unidad.BLOQUES);
		num("explosion.misil_dano", "Misil: daño máximo", "Daño en el centro del impacto; baja con la distancia.", 0, 100, 0.5, 15, Unidad.CORAZONES);
		num("explosion.nuclear_dano", "Creeper nuclear: daño", "Daño a todo ser vivo dentro del radio de la explosión nuclear.", 0, 100, 0.5, 15, Unidad.CORAZONES);
		num("explosion.nuclear_radio", "Creeper nuclear: radio de daño", "Radio del daño de la explosión nuclear.", 0, 128, 1, 40, Unidad.BLOQUES);
		num("explosion.pantalla_azul", "Creeper rojo: radio de la pantalla azul", "Radio en el que la explosión del creeper rojo pone la pantalla azul.", 0, 100, 1, 12, Unidad.BLOQUES);

		cat("Supernova");
		num("supernova.radio", "Radio de la explosión", "Radio máximo de las supernovas nuevas (si no pones otro con /supernova tamaño).", 10, 2000, 10, 150, Unidad.BLOQUES);
		num("supernova.alcance", "Alcance de la onda expansiva", "La onda expansiva llega a este número de veces el radio de la explosión.", 1, 8, 0.5, 4, Unidad.X_RADIO);
		num("supernova.tiempo", "Tiempo hasta que explota", "Segundos de cuenta atrás desde que se activa hasta que la estrella explota (mínimo 30).", 30, 3600, 5, 30, Unidad.SEGUNDOS);
		num("supernova.duracion", "Duración de la explosión", "Segundos que dura la explosión tras estallar (la esfera crece y la onda avanza dentro de este tiempo). Con /supernova duración se cambia solo para una.", 1, 600, 1, 24, Unidad.SEGUNDOS);
		num("supernova.fuego_dentro", "Fuego dentro de la explosión", "Segundos en llamas de quien sobrevive dentro de la esfera.", 0, 120, 1, 15, Unidad.SEGUNDOS);
		num("supernova.fuego_onda", "Fuego de la onda expansiva", "Segundos en llamas de quien alcanza la onda fuera de la esfera.", 0, 120, 1, 5, Unidad.SEGUNDOS);
		num("supernova.desgaste", "Desgaste de armadura por segundo", "Porcentaje de durabilidad que pierde cada pieza de armadura por segundo dentro de la onda.", 0, 50, 0.5, 2.5, Unidad.PORCENTAJE);

		cat("Eclipse");
		num("eclipse.totalidad", "Totalidad por defecto", "Segundos de totalidad de /eclipse cuando no se indica otro valor.", 1, 600, 1, 30, Unidad.SEGUNDOS);
		num("eclipse.oscuridad", "Oscuridad de la totalidad", "Cuánta luz se apaga en la totalidad del eclipse: 0 % = no se oscurece nada, 65 % = se ve algo, 100 % = negro total (solo las luces alumbran).", 0, 100, 1, 65, Unidad.PORCENTAJE);

		cat("Phora y misiles");
		num("phora.entre_min", "Entre misiles: mínimo", "Mínimo de segundos entre un misil y el siguiente en el Momento Phora.", 1, 300, 1, 6, Unidad.SEGUNDOS);
		num("phora.entre_max", "Entre misiles: máximo", "Máximo de segundos entre un misil y el siguiente.", 1, 600, 1, 16, Unidad.SEGUNDOS);
		num("phora.separacion", "Separación entre misiles", "Distancia mínima entre dos impactos seguidos.", 0, 1000, 10, 110, Unidad.BLOQUES);
		num("phora.vida", "Vida máxima del misil", "Segundos que como máximo puede estar cayendo antes de desaparecer.", 1, 300, 1, 30, Unidad.SEGUNDOS);
		num("phora.desgaste", "Desgaste de armadura", "Fracción de la durabilidad de cada pieza que gasta el impacto en su centro (0,45 = 45 %).", 0, 1, 0.05, 0.5, Unidad.NADA);

		cat("Meteoritos");
		num("meteorito.potencia", "Meteorito: potencia", "Potencia de la explosión del meteorito (un TNT son 4).", 0, 40, 1, 8, Unidad.NADA);
		num("meteorito.radio", "Meteorito: radio de daño", "Hasta dónde llega el daño del impacto (crece con el tamaño del meteorito).", 0, 100, 1, 22, Unidad.BLOQUES);
		num("meteorito.dano", "Meteorito: daño máximo", "Daño en el centro del impacto; baja con la distancia.", 0, 100, 0.5, 12, Unidad.CORAZONES);
		num("meteorito.desgaste", "Meteorito: desgaste de armadura", "Fracción de la durabilidad de cada pieza que gasta el impacto en su centro (0,35 = 35 %).", 0, 1, 0.05, 0.4, Unidad.NADA);
		num("meteorito.vida", "Meteorito: vida máxima", "Segundos que como máximo puede estar cayendo antes de desaparecer.", 1, 300, 1, 30, Unidad.SEGUNDOS);
		num("meteorito.ancho", "Meteorito: ancho del hueco", "Radio del cráter que deja un meteorito de tamaño normal (los más grandes lo dejan mayor).", 0, 100, 1, 28, Unidad.BLOQUES);
		num("meteorito.profundidad", "Meteorito: profundidad del hueco", "Profundidad del cráter que deja un meteorito de tamaño normal (los más grandes lo dejan más hondo).", 0, 50, 1, 7, Unidad.BLOQUES);

		cat("Cambios de dificultad");
		num("cambios.deep_dark", "Deep Dark: cada cuánto aparece algo", "Segundos entre intentos de aparición en el Deep Dark (Wardens, bombas...).", 1, 120, 1, 5, Unidad.SEGUNDOS);
		num("cambios.bombas_max", "Bombas Warden: máximo cerca", "Máximo de bombas voladoras a la vez cerca de un jugador.", 0, 40, 1, 4, Unidad.NADA);
		num("cambios.bomba_dano", "Bomba Warden: daño", "Corazones que quita la explosión a cada jugador que esté cerca (ignora la armadura; el Tótem sí salva).", 0, 50, 0.5, 5, Unidad.CORAZONES);
		num("cambios.bomba_velocidad", "Bomba Warden: velocidad", "Lo rápido que vuela hacia ti cuando te detecta, en bloques por tick (0,45 = más rápido que un jugador corriendo).", 0, 1.5, 0.05, 0.5, Unidad.BLOQUES_TICK);
		num("cambios.bomba_radio", "Bomba Warden: radio que destruye", "Radio (en bloques) de la esfera de bloques que destruye al explotar (respeta mobGriefing).", 0, 12, 1, 4, Unidad.BLOQUES);
		num("cambios.bomba_deteccion", "Bomba Warden: distancia de detección", "A qué distancia detecta a un jugador y sale volando a por él.", 0, 80, 1, 28, Unidad.BLOQUES);
		num("cambios.blazes_max", "Blazes en el Nether: máximo cerca", "Máximo de Blazes extra a la vez cerca de un jugador.", 0, 32, 1, 5, Unidad.NADA);
		num("cambios.arana", "Araña de cueva al romper telarañas", "Probabilidad de que salga una Araña de Cueva al romper una telaraña.", 0, 100, 1, 30, Unidad.PORCENTAJE);
		num("cambios.dano_endermite", "Daño de las Endermite", "Daño de cada golpe con el cambio de Endermite activo.", 0, 400, 1, 50, Unidad.CORAZONES);
		num("cambios.dano_enderman", "Daño de los Enderman", "Daño de cada golpe con el cambio de Enderman activo.", 0, 400, 1, 50, Unidad.CORAZONES);
		num("cambios.mis_ojos", "Mis Ojos!: duración", "Segundos de Miopía y Daltonismo tras mirar al Sol.", 0, 3600, 15, 180, Unidad.MINUTOS);
		num("cambios.creeper_perla", "Creeper Nuclear con perla", "Probabilidad de que salga un Creeper Nuclear al aterrizar con una Ender Pearl.", 0, 100, 1, 1, Unidad.PORCENTAJE);
		num("cambios.zombie_altura", "Zombie Inteligente: altura máxima", "Los Zombies Inteligentes aparecen de esta altura (Y) hacia abajo.", -64, 320, 1, 40, Unidad.CAPA);
		num("cambios.zombie_dureza", "Zombie Inteligente: dureza máxima", "Dureza máxima de los bloques que rompe (piedra y menas sí; obsidiana no).", 0, 50, 0.5, 5, Unidad.NADA);
		num("cambios.zombie_ticks", "Zombie Inteligente: ticks por golpe", "Cada cuántos ticks rompe un bloque (más bajo = más rápido).", 1, 20, 1, 3, Unidad.TICKS);

		cat("Tornado");
		num("tornado.tamano", "Tamaño por defecto", "Radio del tornado arriba del todo (donde se abre en la nube). Abajo mide algo más de una quinta parte.", 0, 120, 1, 55, Unidad.BLOQUES);
		num("tornado.altura", "Altura del embudo", "Hasta dónde sube el embudo desde el suelo.", 10, 450, 5, 170, Unidad.BLOQUES);
		num("tornado.avance", "Velocidad de avance", "Cuánto camina el tornado por el mapa (0 = se queda quieto).", 0, 10, 0.1, 0.5, Unidad.BLOQUES_S);
		num("tornado.succion", "Fuerza de succión", "Cuánto tira y hace girar a jugadores, mobs y objetos (1 = normal).", 0, 5, 0.1, 1, Unidad.X);
		num("tornado.dano_centro", "Daño en el centro", "Daño de cada golpe a quien está dentro del núcleo del tornado.", 0, 50, 0.5, 2, Unidad.CORAZONES);
		num("tornado.cada", "Cada cuánto daña", "Medios segundos entre dos golpes (2 = cada segundo).", 1, 20, 1, 2, Unidad.NADA);
		num("tornado.duracion", "Duración", "Segundos que dura cada tornado antes de deshacerse (0 = hasta que lo pares con /tornado stop).", 0, 3600, 5, 0, Unidad.SEGUNDOS);
		bool("tornado.lluvia", "Lluvia y tormenta", "Con el cielo de tormenta (o al crear un tornado) llueve con tormenta eléctrica mientras dure.", true);
		bool("tornado.atrapa", "Atrapa, sube y traga", "El tornado atrapa a quien se acerca al embudo: lo hace girar a su alrededor, lo sube y al llegar arriba lo traga.", true);
		num("tornado.subida", "Velocidad de subida", "Lo rápido que sube a quien tiene atrapado (1 = normal).", 0, 5, 0.1, 1, Unidad.X);
		num("tornado.dano_giro", "Daño mientras giras", "Daño de cada golpe a quien está girando dentro del tornado (ignora la armadura; el Tótem sí salva). Cada cuánto: «Cada cuánto daña».", 0, 20, 0.5, 0.5, Unidad.CORAZONES);
		num("tornado.altura_traga", "Altura a la que te traga", "A qué fracción de la altura del embudo te traga (0,6 = al 60 %).", 0.1, 1, 0.05, 0.6, Unidad.X_ALTURA);
		bool("tornado.traga", "Te traga al llegar arriba", "Apagado: al llegar arriba te suelta (con caída lenta) en vez de tragarte.", true);
		num("tornado.reviil_cantidad", "Momento Reviil: cantidad de tornados", "Cuántos tornados salen en el Momento Reviil de los tornados (repartidos por los chunks cargados, a la separación mínima).", 1, 500, 1, 12, Unidad.NADA);
		num("tornado.reviil_reponer", "Momento Reviil: reponer cada", "Cada cuántos segundos se quitan los tornados que quedaron lejos de todos y se ponen nuevos cerca de los jugadores.", 1, 600, 1, 10, Unidad.SEGUNDOS);
		num("tornado.reviil_separacion", "Momento Reviil: separación mínima", "Distancia mínima entre dos tornados de ese Momento Reviil.", 0, 2000, 10, 400, Unidad.BLOQUES);

		cat("Herobrine y Linterna");
		num("herobrine.vision", "Distancia de visión de Herobrine", "Hasta qué distancia puede detectar que alguien lo mira a los ojos.", 0, 128, 1, 64, Unidad.BLOQUES);
		num("herobrine.tp_min", "Teletransporte: mínimo", "Mínimo de segundos entre dos teletransportes de Herobrine cuando está quieto.", 1, 600, 1, 15, Unidad.SEGUNDOS);
		num("herobrine.tp_max", "Teletransporte: máximo", "Máximo de segundos entre dos teletransportes de Herobrine cuando está quieto.", 1, 600, 1, 45, Unidad.SEGUNDOS);
		num("herobrine.dado", "Aparición: cada cuánto se tira el dado", "Con el cambio de dificultad Herobrine activo, cada cuántos segundos se decide si aparece uno cerca de cada jugador.", 1, 3600, 5, 60, Unidad.MINUTOS);
		num("herobrine.prob", "Aparición: probabilidad", "Probabilidad de que aparezca en cada tirada.", 0, 100, 1, 25, Unidad.PORCENTAJE);
		num("linterna.alcance", "Alcance de la Linterna", "Hasta dónde llega la luz de la Linterna (y lo que hace desaparecer a Herobrine). Mínimo y valor por defecto: 10.", 10, 64, 1, 10, Unidad.BLOQUES);
		num("linterna.intensidad", "Intensidad de la Linterna", "Qué tan fuerte ilumina la Linterna (1 = suave, 2 = fuerte, 4 = muy fuerte).", 0.5, 5, 0.1, 2.2, Unidad.X);
		num("linterna.carga", "Carga máxima de la Linterna", "Unidades de carga de una Linterna llena (y lo que recupera una Batería).", 1, 3600, 1, 150, Unidad.NADA);
		num("linterna.prender", "Tiempo para prender o apagar", "Cuánto hay que mantener el clic derecho para prender o apagar la Linterna.", 0, 10, 0.5, 3, Unidad.SEGUNDOS);
		num("linterna.segundos_unidad", "Segundos por unidad de carga", "Cada cuántos segundos gasta 1 de carga la Linterna prendida (más alto = dura más).", 1, 60, 1, 1, Unidad.SEGUNDOS);
	}

	private Ajustes() {
	}

	public static List<Categoria> categorias() {
		return Collections.unmodifiableList(CATEGORIAS);
	}

	public static Def get(String id) {
		return POR_ID.get(id);
	}

	public static Map<String, Def> todos() {
		return Collections.unmodifiableMap(POR_ID);
	}

	public static Map<String, Double> porDefecto() {
		Map<String, Double> m = new LinkedHashMap<>();

		for (Def d : POR_ID.values()) {
			m.put(d.id(), d.porDefecto());
		}

		return m;
	}

	public static String numero(double v) {
		if (Math.abs(v - Math.rint(v)) < 1e-6) {
			return Long.toString(Math.round(v));
		}

		String s = String.format(Locale.ROOT, "%.2f", v);

		while (s.endsWith("0")) {
			s = s.substring(0, s.length() - 1);
		}

		return s.endsWith(".") ? s.substring(0, s.length() - 1) : s;
	}
}
