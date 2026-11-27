package net.ruleta;

import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public final class Ajustes {
   public static final String C_BUNKER = "Bunker";
   public static final String C_DANO = "Daño y rayos";
   public static final String C_MOMENTOS = "Momentos Reviil";
   public static final String C_AGUJEROS = "Agujeros negros";
   public static final String C_HEROBRINE = "Herobrine y Linterna";
   public static final String C_EXPLOSIONES = "Explosiones";
   public static final String C_SUPERNOVA = "Supernova";
   public static final String C_ECLIPSE = "Eclipse";
   public static final String C_CAMBIOS = "Cambios de dificultad";
   public static final String C_TORNADO = "Tornado";
   public static final String C_PHORA = "Phora y misiles";
   public static final String C_METEORITOS = "Meteoritos";
   private static final Map<String, Ajustes.Def> POR_ID = new LinkedHashMap<>();
   private static final Map<String, Double> VALORES = new ConcurrentHashMap<>();

   private Ajustes() {
   }

   private static void entero(String id, String cat, String nombre, String ayuda, double def, double min, double max, double paso, String unidad) {
      POR_ID.put(id, new Ajustes.Def(id, cat, nombre, ayuda, Ajustes.Tipo.ENTERO, def, min, max, paso, unidad));
   }

   private static void decimal(String id, String cat, String nombre, String ayuda, double def, double min, double max, double paso, String unidad) {
      POR_ID.put(id, new Ajustes.Def(id, cat, nombre, ayuda, Ajustes.Tipo.DECIMAL, def, min, max, paso, unidad));
   }

   private static void bool(String id, String cat, String nombre, String ayuda, boolean def) {
      POR_ID.put(id, new Ajustes.Def(id, cat, nombre, ayuda, Ajustes.Tipo.BOOL, def ? 1.0 : 0.0, 0.0, 1.0, 1.0, ""));
   }

   public static List<Ajustes.Def> todas() {
      return new ArrayList<>(POR_ID.values());
   }

   public static Ajustes.Def def(String id) {
      return POR_ID.get(id);
   }

   public static List<String> categorias() {
      List<String> r = new ArrayList<>();

      for (Ajustes.Def d : POR_ID.values()) {
         if (!r.contains(d.categoria())) {
            r.add(d.categoria());
         }
      }

      return r;
   }

   public static double valor(String id) {
      Double v = VALORES.get(id);
      if (v != null) {
         return v;
      }

      Ajustes.Def d = POR_ID.get(id);
      return d == null ? 0.0 : d.def();
   }

   public static int entero(String id) {
      return (int)Math.round(valor(id));
   }

   public static float decimal(String id) {
      return (float)valor(id);
   }

   public static boolean bool(String id) {
      return valor(id) >= 0.5;
   }

   public static long ticks(String id) {
      return Math.round(valor(id) * 20.0);
   }

   public static Map<String, Double> copia() {
      Map<String, Double> m = new LinkedHashMap<>();

      for (Ajustes.Def d : POR_ID.values()) {
         m.put(d.id(), valor(d.id()));
      }

      return m;
   }

   public static double limpiar(Ajustes.Def d, double v) {
      if (!Double.isNaN(v) && !Double.isInfinite(v)) {
         double r = Math.max(d.min(), Math.min(d.max(), v));
         if (d.tipo() == Ajustes.Tipo.ENTERO) {
            r = Math.round(r);
         } else if (d.tipo() == Ajustes.Tipo.BOOL) {
            r = r >= 0.5 ? 1.0 : 0.0;
         } else {
            r = Math.round(r * 100.0) / 100.0;
         }

         return r;
      } else {
         return d.def();
      }
   }

   public static boolean poner(String id, double v) {
      Ajustes.Def d = POR_ID.get(id);
      if (d == null) {
         return false;
      }

      VALORES.put(id, limpiar(d, v));
      return true;
   }

   public static void restablecer(String categoria) {
      for (Ajustes.Def d : POR_ID.values()) {
         if (categoria == null || categoria.equals(d.categoria())) {
            VALORES.put(d.id(), d.def());
         }
      }
   }

   public static void aplicarDeRed(Map<String, Double> valores) {
      for (Entry<String, Double> e : valores.entrySet()) {
         poner(e.getKey(), e.getValue());
      }
   }

   private static void cargar() {
      VALORES.clear();

      for (Ajustes.Def d : POR_ID.values()) {
         VALORES.put(d.id(), d.def());
      }

      try {
         JsonObject s = ConfigArchivo.seccion("ajustes");
         if (s != null) {
            for (Ajustes.Def d : POR_ID.values()) {
               if (s.has(d.id())) {
                  VALORES.put(d.id(), limpiar(d, s.get(d.id()).getAsDouble()));
               }
            }
         }
      } catch (Exception ex) {
         Ruleta.LOG.warn("[ajustes] no se pudo leer ruleta.json: se usan los valores por defecto", ex);
      }
   }

   public static void guardar() {
      JsonObject s = new JsonObject();

      for (Ajustes.Def d : POR_ID.values()) {
         s.addProperty(d.id(), valor(d.id()));
      }

      try {
         ConfigArchivo.guardar("ajustes", s);
      } catch (IOException ex) {
         Ruleta.LOG.warn("[ajustes] no se pudo guardar ruleta.json", ex);
      }
   }

   public static void alArrancar(MinecraftServer servidor) {
      cargar();
   }

   public static void alEntrar(ServerPlayer p) {
      Red.enviar(p, new AjustesPayload(copia()));
   }

   static void recibir(ServerPlayer jugador, AjustePayload m) {
      if (jugador != null && m != null && jugador.level().getServer() != null) {
         if (jugador.permissions().hasPermission(net.minecraft.server.permissions.Permissions.COMMANDS_GAMEMASTER)) {
            MinecraftServer server = jugador.level().getServer();
            if (m.reiniciar()) {
               restablecer(m.id().isEmpty() ? null : m.id());
            } else if (!poner(m.id(), m.valor())) {
               return;
            }

            guardar();
            Red.enviarATodos(server, new AjustesPayload(copia()));
         }
      }
   }

   static {
      entero(
         "bunker.distancia_min",
         "Bunker",
         "Distancia mínima entre bunkers",
         "No se puede construir un bunker a menos de esta distancia de otro (en la misma dimensión). 0 = sin límite.",
         1500.0,
         0.0,
         20000.0,
         100.0,
         "bloques"
      );
      entero(
         "bunker.contador",
         "Bunker",
         "Tiempo para llegar al bunker",
         "Valor por defecto del contador antes de que se cierre la puerta (si al activar el momento no se escribe otro).",
         300.0,
         10.0,
         86400.0,
         10.0,
         "s"
      );
      entero(
         "bunker.cerrado",
         "Bunker",
         "Tiempo con la puerta cerrada",
         "Valor por defecto de lo que dura la puerta cerrada (luego se abre).",
         150.0,
         10.0,
         86400.0,
         10.0,
         "s"
      );
      entero(
         "bunker.final_abierto",
         "Bunker",
         "Puerta abierta al terminar",
         "Cuánto se queda abierta la puerta del momento cuando termina, antes de cerrarse sola y sacar a quien siga dentro.",
         60.0,
         0.0,
         3600.0,
         5.0,
         "s"
      );
      entero(
         "bunker.puerta_auto",
         "Bunker",
         "Cierre automático de la puerta",
         "Si abres a mano la puerta del bunker (fuera del contador), se vuelve a cerrar sola pasado este tiempo. Si la cierras tú antes, el servidor no interviene.",
         5.0,
         1.0,
         120.0,
         1.0,
         "s"
      );
      entero(
         "bunker.zona_impactos",
         "Bunker",
         "Zona sin impactos",
         "Los meteoritos y los misiles de los Phora nunca caen a menos de estos bloques de cualquier bunker (ni encima). 0 = sin protección.",
         80.0,
         0.0,
         300.0,
         5.0,
         "bloques"
      );
      entero(
         "bunker.puerta_reabrir",
         "Bunker",
         "Reapertura de la puerta",
         "Si cierran a mano la puerta del bunker mientras el contador aún corre (el servidor la quiere abierta), se vuelve a abrir sola pasado este tiempo.",
         5.0,
         1.0,
         120.0,
         1.0,
         "s"
      );
      bool(
         "bunker.puerta_todos",
         "Bunker",
         "Cualquiera puede abrir la puerta",
         "Desactivado: solo administradores y jugadores en creativo pueden abrir la puerta del bunker a mano.",
         false
      );
      entero(
         "bunker.nivel_construir",
         "Bunker",
         "Nivel para construir dentro",
         "Nivel de permisos mínimo para colocar bloques, cubos, cofres... dentro de un bunker (los jugadores en creativo siempre pueden).",
         2.0,
         0.0,
         4.0,
         1.0,
         ""
      );
      bool(
         "bunker.expulsar",
         "Bunker",
         "Sacar a los jugadores fuera de un momento",
         "Si no hay un Momento Reviil del bunker en marcha, quien esté dentro de un bunker (sin creativo) es sacado afuera.",
         true
      );
      decimal(
         "bunker.aviso_seg",
         "Bunker",
         "Duración del aviso de construcción",
         "Cuánto dura en pantalla el texto «No puedes colocar bloques en esta zona.».",
         2.0,
         0.5,
         10.0,
         0.5,
         "s"
      );
      decimal(
         "dano.bunker",
         "Daño y rayos",
         "Daño fuera del bunker",
         "Daño del Rayo mortal y de los Agujeros Negros a quien no está dentro del bunker, cada vez que golpea.",
         1.0,
         0.5,
         20.0,
         0.5,
         "corazones"
      );
      entero("dano.bunker_intervalo", "Daño y rayos", "Cada cuánto hace daño", "Segundos entre cada golpe de daño fuera del bunker.", 1.0, 1.0, 10.0, 1.0, "s");
      bool(
         "rayo.activo",
         "Daño y rayos",
         "Caen rayos en el Rayo mortal",
         "Rayos visuales (sin fuego ni daño de rayo) alrededor de quien está fuera del bunker.",
         true
      );
      entero("rayo.prob", "Daño y rayos", "Probabilidad de rayo", "Probabilidad de que cada golpe de daño venga con un rayo.", 35.0, 0.0, 100.0, 5.0, "%");
      entero(
         "rayo.radio", "Daño y rayos", "Dispersión de los rayos", "A qué distancia máxima del jugador puede caer cada rayo.", 16.0, 2.0, 64.0, 2.0, "bloques"
      );
      decimal(
         "dano.gas",
         "Daño y rayos",
         "Daño del gas tóxico",
         "Daño por segundo del Gas tóxico de Reviil a quien no esté en su capa.",
         1.0,
         0.5,
         20.0,
         0.5,
         "corazones"
      );
      decimal("dano.herobrine", "Daño y rayos", "Daño de Herobrine", "Daño de cada golpe de Herobrine (ignora la armadura).", 2.0, 0.5, 20.0, 0.5, "corazones");
      entero(
         "gas.contador",
         "Momentos Reviil",
         "Contador del gas tóxico",
         "Valor por defecto del temporizador del gas (tiempo para llegar a la capa pedida).",
         300.0,
         10.0,
         86400.0,
         10.0,
         "s"
      );
      entero("gas.capa_arriba", "Momentos Reviil", "Capa del gas (subir)", "Quien esté por debajo de esta altura recibe daño.", 250.0, 0.0, 320.0, 10.0, "capa");
      entero("gas.capa_abajo", "Momentos Reviil", "Capa del gas (bajar)", "Quien esté en esta altura o por encima recibe daño.", 0.0, -64.0, 64.0, 1.0, "capa");
      entero(
         "creepers.cada",
         "Momentos Reviil",
         "Creeper Eléctrico cada...",
         "De media, cada cuántos segundos le cae un Creeper Eléctrico a cada jugador.",
         75.0,
         5.0,
         600.0,
         5.0,
         "s"
      );
      entero(
         "ceguera.duracion",
         "Momentos Reviil",
         "Duración de la ceguera",
         "Segundos de ceguera del momento «Ceguera y Creepers Eléctricos».",
         15.0,
         1.0,
         120.0,
         1.0,
         "s"
      );
      entero(
         "eclipse.espera1",
         "Momentos Reviil",
         "Eclipse: 1ª → 2ª ruleta",
         "Valor por defecto del tiempo entre la 1ª y la 2ª ruleta del Eclipse.",
         60.0,
         5.0,
         3600.0,
         5.0,
         "s"
      );
      entero(
         "eclipse.espera2",
         "Momentos Reviil",
         "Eclipse: 2ª → 3ª ruleta",
         "Valor por defecto del tiempo entre la 2ª y la 3ª ruleta del Eclipse.",
         60.0,
         5.0,
         3600.0,
         5.0,
         "s"
      );
      entero(
         "eclipse.espera3",
         "Momentos Reviil",
         "Eclipse: 3ª → 4ª ruleta",
         "Valor por defecto del tiempo entre la 3ª y la 4ª ruleta del Eclipse.",
         60.0,
         5.0,
         3600.0,
         5.0,
         "s"
      );
      entero("agujeros.min", "Agujeros negros", "Mínimo de agujeros", "Cuántos agujeros negros hay a la vez como mínimo.", 3.0, 1.0, 50.0, 1.0, "");
      entero("agujeros.max", "Agujeros negros", "Máximo de agujeros", "Cuántos agujeros negros hay a la vez como máximo.", 12.0, 1.0, 50.0, 1.0, "");
      entero(
         "agujeros.por_jugador",
         "Agujeros negros",
         "Agujeros por jugador",
         "Cuántos agujeros salen por cada jugador conectado (entre el mínimo y el máximo).",
         2.0,
         1.0,
         10.0,
         1.0,
         ""
      );
      entero(
         "agujeros.separacion",
         "Agujeros negros",
         "Separación entre agujeros",
         "Distancia mínima entre dos agujeros negros.",
         150.0,
         20.0,
         1000.0,
         10.0,
         "bloques"
      );
      entero(
         "agujeros.lejos_bunker",
         "Agujeros negros",
         "Distancia mínima al bunker",
         "Los agujeros nunca salen más cerca del bunker que esto.",
         200.0,
         100.0,
         5000.0,
         50.0,
         "bloques"
      );
      entero(
         "agujeros.reponer",
         "Agujeros negros",
         "Reponer cada",
         "Cada cuántos segundos el momento revisa los agujeros: quita los que quedaron lejos de todos y pone nuevos cerca de los jugadores hasta tener la cantidad de arriba.",
         10.0,
         2.0,
         300.0,
         1.0,
         "s"
      );
      entero(
         "agujeros.olvidar",
         "Agujeros negros",
         "Distancia para quitarlos",
         "Un agujero a más de esta distancia de todos los jugadores se quita (y sale otro cerca de alguien).",
         700.0,
         200.0,
         5000.0,
         50.0,
         "bloques"
      );
      decimal(
         "agujeros.mediano",
         "Agujeros negros",
         "Tamaño mediano (x radio)",
         "Los agujeros medianos miden el radio de atracción multiplicado por esto.",
         2.0,
         1.0,
         6.0,
         0.25,
         ""
      );
      decimal(
         "agujeros.gigante",
         "Agujeros negros",
         "Tamaño gigante (x radio)",
         "Los agujeros gigantes miden el radio de atracción multiplicado por esto.",
         3.5,
         1.0,
         10.0,
         0.25,
         ""
      );
      entero(
         "agujeros.prob_mediano",
         "Agujeros negros",
         "Probabilidad de mediano",
         "De cada 100 agujeros nuevos, cuántos salen medianos.",
         30.0,
         0.0,
         100.0,
         5.0,
         "%"
      );
      entero(
         "agujeros.prob_gigante",
         "Agujeros negros",
         "Probabilidad de gigante",
         "De cada 100 agujeros nuevos, cuántos salen gigantes (el resto salen normales).",
         15.0,
         0.0,
         100.0,
         5.0,
         "%"
      );
      entero("agujeros.capa_min", "Agujeros negros", "Capa mínima", "Los agujeros aparecen de esta altura para arriba.", 100.0, 0.0, 300.0, 5.0, "capa");
      decimal(
         "agujeros.dano",
         "Agujeros negros",
         "Daño dentro del agujero",
         "Daño de cada golpe a quien queda atrapado en el centro de un Agujero Negro.",
         2.5,
         0.5,
         50.0,
         0.5,
         "corazones"
      );
      entero("agujeros.intervalo", "Agujeros negros", "Cada cuánto daña", "Segundos entre dos golpes del centro del agujero.", 1.0, 1.0, 10.0, 1.0, "s");
      decimal(
         "agujeros.radio",
         "Agujeros negros",
         "Radio de atracción",
         "Radio de los agujeros que crea el momento (los del comando /agujero llevan el suyo).",
         15.0,
         2.0,
         64.0,
         1.0,
         "bloques"
      );
      decimal(
         "agujeros.velocidad",
         "Agujeros negros",
         "Velocidad máxima de arrastre",
         "Lo rápido que puede llegar a tirar de ti un agujero (bloques por tick).",
         1.4,
         0.2,
         4.0,
         0.1,
         ""
      );
      decimal("misil.potencia", "Explosiones", "Misil: potencia", "Potencia de la explosión del misil de los Phora (un TNT son 4).", 10.0, 1.0, 40.0, 1.0, "");
      entero(
         "misil.radio",
         "Explosiones",
         "Misil: radio de daño",
         "Hasta dónde llega el daño del impacto (crece con el tamaño del misil).",
         26.0,
         4.0,
         100.0,
         2.0,
         "bloques"
      );
      decimal("misil.dano", "Explosiones", "Misil: daño máximo", "Daño en el centro del impacto; baja con la distancia.", 15.0, 1.0, 100.0, 1.0, "corazones");
      decimal(
         "nuclear.dano",
         "Explosiones",
         "Creeper nuclear: daño",
         "Daño a todo ser vivo dentro del radio de la explosión nuclear.",
         15.0,
         1.0,
         100.0,
         1.0,
         "corazones"
      );
      entero("nuclear.radio", "Explosiones", "Creeper nuclear: radio de daño", "Radio del daño de la explosión nuclear.", 40.0, 4.0, 128.0, 2.0, "bloques");
      entero(
         "creeperrojo.radio",
         "Explosiones",
         "Creeper rojo: radio de la pantalla azul",
         "Radio en el que la explosión del creeper rojo pone la pantalla azul.",
         12.0,
         2.0,
         64.0,
         1.0,
         "bloques"
      );
      decimal(
         "supernova.radio",
         "Supernova",
         "Radio de la explosión",
         "Radio máximo de las supernovas nuevas (si no pones otro con /supernova tamaño).",
         150.0,
         1.0,
         2000.0,
         25.0,
         "bloques"
      );
      decimal(
         "supernova.onda",
         "Supernova",
         "Alcance de la onda expansiva",
         "La onda expansiva llega a este número de veces el radio de la explosión.",
         4.0,
         1.0,
         8.0,
         0.5,
         "x radio"
      );
      entero(
         "supernova.cuenta",
         "Supernova",
         "Tiempo hasta que explota",
         "Segundos de cuenta atrás desde que se activa hasta que la estrella explota (mínimo 30).",
         30.0,
         30.0,
         600.0,
         5.0,
         "s"
      );
      entero(
         "supernova.duracion",
         "Supernova",
         "Duración de la explosión",
         "Segundos que dura la explosión tras estallar (la esfera crece y la onda avanza dentro de este tiempo). Con /supernova tiempo se puede alargar más.",
         24.0,
         5.0,
         600.0,
         1.0,
         "s"
      );
      entero(
         "supernova.fuego",
         "Supernova",
         "Fuego dentro de la explosión",
         "Segundos en llamas de quien sobrevive dentro de la esfera.",
         15.0,
         0.0,
         120.0,
         1.0,
         "s"
      );
      entero(
         "supernova.fuego_onda",
         "Supernova",
         "Fuego de la onda expansiva",
         "Segundos en llamas de quien alcanza la onda fuera de la esfera.",
         5.0,
         0.0,
         120.0,
         1.0,
         "s"
      );
      decimal(
         "supernova.armadura",
         "Supernova",
         "Desgaste de armadura por segundo",
         "Porcentaje de durabilidad que pierde cada pieza de armadura por segundo dentro de la onda.",
         2.5,
         0.0,
         50.0,
         0.5,
         "%"
      );
      entero(
         "eclipse.totalidad",
         "Eclipse",
         "Totalidad por defecto",
         "Segundos de totalidad de /eclipse cuando no se indica otro valor.",
         30.0,
         5.0,
         600.0,
         5.0,
         "s"
      );
      entero(
         "eclipse.oscuridad",
         "Eclipse",
         "Oscuridad de la totalidad",
         "Cuánta luz se apaga en la totalidad del eclipse: 0 % = no se oscurece nada, 65 % = se ve algo, 100 % = negro total (solo la Linterna deja ver).",
         65.0,
         0.0,
         100.0,
         5.0,
         "%"
      );
      entero(
         "phora.espera_min",
         "Phora y misiles",
         "Entre misiles: mínimo",
         "Mínimo de segundos entre un misil y el siguiente en el Momento Phora.",
         6.0,
         1.0,
         600.0,
         1.0,
         "s"
      );
      entero("phora.espera_max", "Phora y misiles", "Entre misiles: máximo", "Máximo de segundos entre un misil y el siguiente.", 16.0, 2.0, 600.0, 1.0, "s");
      entero(
         "phora.separacion",
         "Phora y misiles",
         "Separación entre misiles",
         "Distancia mínima entre dos impactos seguidos.",
         110.0,
         10.0,
         1000.0,
         10.0,
         "bloques"
      );
      entero(
         "misil.vida",
         "Phora y misiles",
         "Vida máxima del misil",
         "Segundos que como máximo puede estar cayendo antes de desaparecer.",
         30.0,
         5.0,
         300.0,
         5.0,
         "s"
      );
      decimal(
         "misil.armadura",
         "Phora y misiles",
         "Desgaste de armadura",
         "Fracción de la durabilidad de cada pieza que gasta el impacto en su centro (0,45 = 45 %).",
         0.45,
         0.0,
         1.0,
         0.05,
         ""
      );
      decimal("meteorito.potencia", "Meteoritos", "Meteorito: potencia", "Potencia de la explosión del meteorito (un TNT son 4).", 8.0, 1.0, 40.0, 1.0, "");
      entero(
         "meteorito.radio",
         "Meteoritos",
         "Meteorito: radio de daño",
         "Hasta dónde llega el daño del impacto (crece con el tamaño del meteorito).",
         22.0,
         4.0,
         100.0,
         2.0,
         "bloques"
      );
      decimal(
         "meteorito.dano", "Meteoritos", "Meteorito: daño máximo", "Daño en el centro del impacto; baja con la distancia.", 12.0, 1.0, 100.0, 1.0, "corazones"
      );
      decimal(
         "meteorito.armadura",
         "Meteoritos",
         "Meteorito: desgaste de armadura",
         "Fracción de la durabilidad de cada pieza que gasta el impacto en su centro (0,35 = 35 %).",
         0.35,
         0.0,
         1.0,
         0.05,
         ""
      );
      entero(
         "meteorito.vida",
         "Meteoritos",
         "Meteorito: vida máxima",
         "Segundos que como máximo puede estar cayendo antes de desaparecer.",
         30.0,
         5.0,
         300.0,
         5.0,
         "s"
      );
      entero(
         "meteorito.hueco",
         "Meteoritos",
         "Meteorito: ancho del hueco",
         "Radio del cráter que deja un meteorito de tamaño normal (los más grandes lo dejan mayor).",
         28.0,
         5.0,
         80.0,
         1.0,
         "bloques"
      );
      entero(
         "meteorito.hondo",
         "Meteoritos",
         "Meteorito: profundidad del hueco",
         "Profundidad del cráter que deja un meteorito de tamaño normal (los más grandes lo dejan más hondo).",
         7.0,
         1.0,
         40.0,
         1.0,
         "bloques"
      );
      entero(
         "cambios.profundo",
         "Cambios de dificultad",
         "Deep Dark: cada cuánto aparece algo",
         "Segundos entre intentos de aparición en el Deep Dark (Wardens, bombas...).",
         5.0,
         1.0,
         120.0,
         1.0,
         "s"
      );
      entero(
         "cambios.max_bombas",
         "Cambios de dificultad",
         "Bombas Warden: máximo cerca",
         "Máximo de bombas voladoras a la vez cerca de un jugador.",
         4.0,
         1.0,
         30.0,
         1.0,
         ""
      );
      decimal(
         "cambios.bomba_dano",
         "Cambios de dificultad",
         "Bomba Warden: daño",
         "Corazones que quita la explosión a cada jugador que esté cerca (ignora la armadura; el Tótem sí salva).",
         5.0,
         0.5,
         50.0,
         0.5,
         "corazones"
      );
      decimal(
         "cambios.bomba_velocidad",
         "Cambios de dificultad",
         "Bomba Warden: velocidad",
         "Lo rápido que vuela hacia ti cuando te detecta, en bloques por tick (0,45 = más rápido que un jugador corriendo).",
         0.45,
         0.1,
         1.2,
         0.05,
         "bloques/tick"
      );
      entero(
         "cambios.bomba_radio",
         "Cambios de dificultad",
         "Bomba Warden: radio que destruye",
         "Radio (en bloques) de la esfera de bloques que destruye al explotar (respeta mobGriefing).",
         4.0,
         0.0,
         12.0,
         1.0,
         "bloques"
      );
      entero(
         "cambios.bomba_deteccion",
         "Cambios de dificultad",
         "Bomba Warden: distancia de detección",
         "A qué distancia detecta a un jugador y sale volando a por él.",
         28.0,
         8.0,
         64.0,
         2.0,
         "bloques"
      );
      entero(
         "cambios.max_blazes",
         "Cambios de dificultad",
         "Blazes en el Nether: máximo cerca",
         "Máximo de Blazes extra a la vez cerca de un jugador.",
         5.0,
         1.0,
         30.0,
         1.0,
         ""
      );
      entero(
         "cambios.prob_arana",
         "Cambios de dificultad",
         "Araña de cueva al romper telarañas",
         "Probabilidad de que salga una Araña de Cueva al romper una telaraña.",
         30.0,
         0.0,
         100.0,
         5.0,
         "%"
      );
      decimal(
         "cambios.dano_endermite",
         "Cambios de dificultad",
         "Daño de las Endermite",
         "Daño de cada golpe con el cambio de Endermite activo.",
         50.0,
         0.5,
         500.0,
         5.0,
         "corazones"
      );
      decimal(
         "cambios.dano_enderman",
         "Cambios de dificultad",
         "Daño de los Enderman",
         "Daño de cada golpe con el cambio de Enderman activo.",
         50.0,
         0.5,
         500.0,
         5.0,
         "corazones"
      );
      entero("cambios.ojos", "Cambios de dificultad", "Mis Ojos!: duración", "Segundos de Miopía y Daltonismo tras mirar al Sol.", 180.0, 5.0, 3600.0, 5.0, "s");
      decimal(
         "cambios.prob_nucleeper",
         "Cambios de dificultad",
         "Creeper Nuclear con perla",
         "Probabilidad de que salga un Creeper Nuclear al aterrizar con una Ender Pearl.",
         1.0,
         0.0,
         100.0,
         0.5,
         "%"
      );
      entero(
         "cambios.capa_inteligentes",
         "Cambios de dificultad",
         "Zombie Inteligente: altura máxima",
         "Los Zombies Inteligentes aparecen de esta altura (Y) hacia abajo.",
         40.0,
         -60.0,
         320.0,
         5.0,
         "capa"
      );
      decimal(
         "cambios.dureza_zombie",
         "Cambios de dificultad",
         "Zombie Inteligente: dureza máxima",
         "Dureza máxima de los bloques que rompe (piedra y menas sí; obsidiana no).",
         5.0,
         0.5,
         50.0,
         0.5,
         ""
      );
      entero(
         "cambios.picar",
         "Cambios de dificultad",
         "Zombie Inteligente: ticks por golpe",
         "Cada cuántos ticks rompe un bloque (más bajo = más rápido).",
         3.0,
         1.0,
         20.0,
         1.0,
         "ticks"
      );
      decimal(
         "tornado.radio",
         "Tornado",
         "Tamaño por defecto",
         "Radio del tornado arriba del todo (donde se abre en la nube). Abajo mide algo más de una quinta parte.",
         55.0,
         4.0,
         120.0,
         2.0,
         "bloques"
      );
      decimal("tornado.alto", "Tornado", "Altura del embudo", "Hasta dónde sube el embudo desde el suelo.", 170.0, 40.0, 400.0, 10.0, "bloques");
      decimal(
         "tornado.velocidad", "Tornado", "Velocidad de avance", "Cuánto camina el tornado por el mapa (0 = se queda quieto).", 0.5, 0.0, 30.0, 0.1, "bloques/s"
      );
      decimal("tornado.fuerza", "Tornado", "Fuerza de succión", "Cuánto tira y hace girar a jugadores, mobs y objetos (1 = normal).", 1.0, 0.0, 5.0, 0.1, "x");
      decimal(
         "tornado.dano", "Tornado", "Daño en el centro", "Daño de cada golpe a quien está dentro del núcleo del tornado.", 2.0, 0.0, 50.0, 0.5, "corazones"
      );
      entero("tornado.dano_cada", "Tornado", "Cada cuánto daña", "Medios segundos entre dos golpes (2 = cada segundo).", 2.0, 1.0, 20.0, 1.0, "");
      entero(
         "tornado.vida",
         "Tornado",
         "Duración",
         "Segundos que dura cada tornado antes de deshacerse (0 = hasta que lo pares con /tornado stop).",
         0.0,
         0.0,
         3600.0,
         30.0,
         "s"
      );
      bool(
         "tornado.lluvia",
         "Tornado",
         "Lluvia y tormenta",
         "Con el cielo de tormenta (o al crear un tornado) llueve con tormenta eléctrica mientras dure.",
         true
      );
      bool(
         "tornado.atrapar",
         "Tornado",
         "Atrapa, sube y traga",
         "El tornado atrapa a quien se acerca al embudo: lo hace girar a su alrededor, lo sube y al llegar arriba lo traga.",
         true
      );
      decimal("tornado.subida", "Tornado", "Velocidad de subida", "Lo rápido que sube a quien tiene atrapado (1 = normal).", 1.0, 0.2, 4.0, 0.1, "x");
      decimal(
         "tornado.dano_giro",
         "Tornado",
         "Daño mientras giras",
         "Daño de cada golpe a quien está girando dentro del tornado (ignora la armadura; el Tótem sí salva). Cada cuánto: «Cada cuánto daña».",
         0.5,
         0.0,
         20.0,
         0.5,
         "corazones"
      );
      decimal(
         "tornado.tragar_altura",
         "Tornado",
         "Altura a la que te traga",
         "A qué fracción de la altura del embudo te traga (0,6 = al 60 %).",
         0.6,
         0.2,
         1.0,
         0.05,
         "x altura"
      );
      bool("tornado.tragar", "Tornado", "Te traga al llegar arriba", "Apagado: al llegar arriba te suelta (con caída lenta) en vez de tragarte.", true);
      entero(
         "tornado.momento_cuantos",
         "Tornado",
         "Momento Reviil: cantidad de tornados",
         "Cuántos tornados salen en el Momento Reviil de los tornados (repartidos por los chunks cargados, a la separación mínima de abajo).",
         12.0,
         12.0,
         40.0,
         1.0,
         ""
      );
      entero(
         "tornado.momento_reponer",
         "Tornado",
         "Momento Reviil: reponer cada",
         "Cada cuántos segundos se quitan los tornados que quedaron lejos de todos y se ponen nuevos cerca de los jugadores.",
         10.0,
         2.0,
         300.0,
         1.0,
         "s"
      );
      entero(
         "tornado.momento_separacion",
         "Tornado",
         "Momento Reviil: separación mínima",
         "Distancia mínima entre dos tornados de ese Momento Reviil.",
         400.0,
         50.0,
         2000.0,
         50.0,
         "bloques"
      );
      entero(
         "herobrine.vision",
         "Herobrine y Linterna",
         "Distancia de visión de Herobrine",
         "Hasta qué distancia puede detectar que alguien lo mira a los ojos.",
         64.0,
         8.0,
         128.0,
         4.0,
         "bloques"
      );
      entero(
         "herobrine.tp_min",
         "Herobrine y Linterna",
         "Teletransporte: mínimo",
         "Mínimo de segundos entre dos teletransportes de Herobrine cuando está quieto.",
         15.0,
         2.0,
         600.0,
         1.0,
         "s"
      );
      entero(
         "herobrine.tp_max",
         "Herobrine y Linterna",
         "Teletransporte: máximo",
         "Máximo de segundos entre dos teletransportes de Herobrine cuando está quieto.",
         45.0,
         2.0,
         600.0,
         1.0,
         "s"
      );
      entero(
         "herobrine.aparicion_seg",
         "Herobrine y Linterna",
         "Aparición: cada cuánto se tira el dado",
         "Con el cambio de dificultad Herobrine activo, cada cuántos segundos se decide si aparece uno cerca de cada jugador.",
         60.0,
         5.0,
         3600.0,
         5.0,
         "s"
      );
      entero(
         "herobrine.aparicion_prob",
         "Herobrine y Linterna",
         "Aparición: probabilidad",
         "Probabilidad de que aparezca en cada tirada.",
         25.0,
         0.0,
         100.0,
         5.0,
         "%"
      );
      entero(
         "linterna.alcance",
         "Herobrine y Linterna",
         "Alcance de la Linterna",
         "Hasta dónde llega la luz de la Linterna (y lo que hace desaparecer a Herobrine). Mínimo y valor por defecto: 10.",
         10.0,
         10.0,
         128.0,
         2.0,
         "bloques"
      );
      decimal(
         "linterna.brillo",
         "Herobrine y Linterna",
         "Intensidad de la Linterna",
         "Qué tan fuerte ilumina la Linterna (1 = suave, 2 = fuerte, 4 = muy fuerte).",
         2.2,
         0.5,
         5.0,
         0.1,
         "x"
      );
      entero(
         "linterna.carga",
         "Herobrine y Linterna",
         "Carga máxima de la Linterna",
         "Unidades de carga de una Linterna llena (y lo que recupera una Batería).",
         150.0,
         10.0,
         3600.0,
         10.0,
         ""
      );
      decimal(
         "linterna.prender",
         "Herobrine y Linterna",
         "Tiempo para prender o apagar",
         "Cuánto hay que mantener el clic derecho para prender o apagar la Linterna.",
         3.0,
         0.5,
         10.0,
         0.5,
         "s"
      );
      entero(
         "mobs.prob_pasto",
         "Momentos Reviil",
         "Creeper de hierba: probabilidad",
         "Probabilidad de que salga un creeper al romper hierba (con ese cambio activo).",
         8.0,
         0.0,
         100.0,
         1.0,
         "%"
      );
      entero(
         "mobs.prob_rojo",
         "Momentos Reviil",
         "Creeper rojo: probabilidad",
         "Probabilidad de que un creeper nuevo sea rojo (con ese cambio activo).",
         25.0,
         0.0,
         100.0,
         1.0,
         "%"
      );
      entero(
         "linterna.consumo",
         "Herobrine y Linterna",
         "Segundos por unidad de carga",
         "Cada cuántos segundos gasta 1 de carga la Linterna prendida (más alto = dura más).",
         1.0,
         1.0,
         20.0,
         1.0,
         "s"
      );
   }

   public record Def(
      String id, String categoria, String nombre, String ayuda, Ajustes.Tipo tipo, double def, double min, double max, double paso, String unidad
   ) {
   }

   public enum Tipo {
      BOOL,
      ENTERO,
      DECIMAL;
   }
}
