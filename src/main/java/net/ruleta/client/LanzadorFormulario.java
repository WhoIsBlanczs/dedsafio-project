package net.ruleta.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.ruleta.Cambios;
import net.ruleta.LanzarPayload;
import net.ruleta.Mensajes;
import net.ruleta.Momentos;
import net.ruleta.RuletaColor;
import net.ruleta.Sanciones;
import net.ruleta.client.phora.PhoraCliente;
import net.ruleta.client.ruleta.RuletaHud;
import net.ruleta.client.ui.EscalaGui;
import net.ruleta.client.ui.Suave;
import net.ruleta.client.ui.Ui;
import net.ruleta.client.ui.UiCampo;

public class LanzadorFormulario extends Screen {
   private static final int MAX_MENSAJE = 1000;
   private static final String[] TIPOS = new String[]{"MINI", "NORMAL"};
   private static final String[] CONTENIDOS = new String[]{"MENSAJE", "ANIMACIÓN", "CIELO"};
   private final RuletaColor color;
   private final boolean rojo;
   private boolean reviil;
   private boolean[] contenidoGuardado;
   private final boolean[] opciones = new boolean[]{true, false, false};
   private LanzadorFormulario.BotonInterruptor botonReviil;
   private boolean nutria = true;
   private LanzadorFormulario.BotonInterruptor botonNutria;
   private String cambiosVistos;
   private boolean phora;
   private LanzadorFormulario.BotonInterruptor botonPhora;
   private final boolean morado;
   private int sancion;
   private String objetivo = "";
   private EditBox tiempoBan;
   private String tiempoBanGuardado = "";
   private LanzadorFormulario.BotonMC botonSancion;
   private LanzadorFormulario.BotonMC botonObjetivo;
   private String para = "";
   private LanzadorFormulario.BotonMC botonPara;
   private LanzadorFormulario.BotonMC botonMomentos;
   private LanzadorFormulario.BotonMC botonCambios;
   private final boolean conTemporizador;
   private final boolean naranja;
   private final boolean amarillo;
   final Set<String> elegidos = new LinkedHashSet<>();
   int paginaCambios;
   boolean conRuleta = true;
   private EditBox temporizador;
   private String temporizadorGuardado = "";
   private int yTemporizador;
   private int tipo = 1;
   private EditBox titulo;
   private MultiLineEditBox mensaje;
   private LanzadorFormulario.BotonMC lanzar;
   private LanzadorFormulario.BotonMC[] botonesTipo;
   private LanzadorFormulario.BotonMC[] botonesContenido;
   private String momentosVistos;
   private String tituloPuesto = "";
   private String mensajePuesto = "";
   private String tituloGuardado = "";
   private String mensajeGuardado = "";
   private int xPanel;
   private int yPanel;
   private int anchoPanel;
   private int altoPanel;
   private int x0;
   private int ancho;
   private int xR;
   private int anchoR;
   private int yTop;
   private int yAbajo;
   private int yMensaje;
   private int yContador;
   private int ySumario;
   private final List<LanzadorFormulario.Etiqueta> etiquetas = new ArrayList<>();
   private final Suave aparece = new Suave();

   public LanzadorFormulario(RuletaColor color) {
      super(Component.literal("Lanzar ruleta"));
      this.color = color;
      this.rojo = color == RuletaColor.ROJO;
      this.conTemporizador = color == RuletaColor.ROSA || color == RuletaColor.VERDE;
      this.morado = color == RuletaColor.MORADO;
      this.naranja = color == RuletaColor.NARANJA;
      this.amarillo = color == RuletaColor.AMARILLO;
   }

   static boolean tiempoValido(String s) {
      try {
         String[] p = s.trim().split(":", -1);
         if (p.length == 1) {
            return Integer.parseInt(p[0]) > 0;
         }

         int total;
         if (p.length == 2) {
            int mm = Integer.parseInt(p[0]);
            int ss = Integer.parseInt(p[1]);
            if (ss > 59 || mm > 99) {
               return false;
            }

            total = mm * 60 + ss;
         } else {
            if (p.length != 3) {
               return false;
            }

            int hh = Integer.parseInt(p[0]);
            int mm = Integer.parseInt(p[1]);
            int ss = Integer.parseInt(p[2]);
            if (ss > 59 || mm > 59 || hh > 24 || hh == 24 && (mm > 0 || ss > 0)) {
               return false;
            }

            total = hh * 3600 + mm * 60 + ss;
         }

         return total > 0;
      } catch (NumberFormatException ex) {
         return false;
      }
   }

   protected void init() {
      if (this.titulo != null) {
         this.tituloGuardado = this.titulo.getValue();
      }

      if (this.mensaje != null) {
         this.mensajeGuardado = this.mensaje.getValue();
      }

      if (this.temporizador != null) {
         this.temporizadorGuardado = this.temporizador.getValue();
      }

      if (this.tiempoBan != null) {
         this.tiempoBanGuardado = this.tiempoBan.getValue();
      }

      EscalaGui.ajustar(this, 624, 356);
      Fondo.poner();
      this.etiquetas.clear();
      int margen = 12;
      int hueco = 12;
      this.anchoPanel = Math.min(this.width - 24, 600);
      this.xPanel = (this.width - this.anchoPanel) / 2;
      this.altoPanel = Math.min(this.height - 16, 340);
      this.yPanel = (this.height - this.altoPanel) / 2;
      int xCont = this.xPanel + margen;
      int anchoCont = this.anchoPanel - margen * 2;
      this.ancho = (anchoCont - hueco) * 44 / 100;
      this.x0 = xCont;
      this.xR = xCont + this.ancho + hueco;
      this.anchoR = anchoCont - this.ancho - hueco;
      this.yTop = this.yPanel + 42;
      this.yAbajo = this.yPanel + this.altoPanel - margen;
      this.etiquetas.add(new LanzadorFormulario.Etiqueta("1 · TÍTULO", this.x0, this.yTop));
      int yTitulo = this.yTop + 11;
      this.titulo = new UiCampo(this.font, this.x0, yTitulo, this.ancho, 20, Component.literal("Título"));
      this.titulo.setMaxLength(48);
      this.titulo.setHint(Component.literal("Título (opcional)"));
      this.titulo.setValue(this.tituloGuardado);
      this.addRenderableWidget(this.titulo);
      this.etiquetas.add(new LanzadorFormulario.Etiqueta("2 · MENSAJE", this.x0, yTitulo + 30));
      this.yMensaje = yTitulo + 41;
      int altoMensaje = Math.max(40, this.yAbajo - 14 - 28 - this.yMensaje);
      this.yContador = this.yMensaje + altoMensaje + 3;
      this.mensaje = new MultiLineEditBox(
         this.font, this.x0, this.yMensaje, this.ancho, altoMensaje, Component.literal("Escribe el mensaje..."), Component.literal("Mensaje")
      );
      this.mensaje.setValue(this.mensajeGuardado);
      this.addRenderableWidget(this.mensaje);
      int y = this.yTop;
      this.etiquetas.add(new LanzadorFormulario.Etiqueta("3 · ¿PARA QUIÉN?", this.xR, y));
      this.botonPara = (LanzadorFormulario.BotonMC)this.addRenderableWidget(
         conAyuda(
            new LanzadorFormulario.BotonMC(
               this.xR,
               y + 11,
               this.anchoR,
               20,
               this.textoPara(),
               () -> Minecraft.getInstance().setScreenAndShow(new JugadoresPantalla(this, true, nombre -> this.para = nombre))
            ),
            "Elige un jugador: solo él verá esta ruleta y su mensaje"
         )
      );
      y += 41;
      this.etiquetas.add(new LanzadorFormulario.Etiqueta("4 · TAMAÑO", this.xR, y));
      int gap = 6;
      int n = TIPOS.length;
      int bw = (this.anchoR - gap * (n - 1)) / n;
      int yTipo = y + 11;
      this.botonesTipo = new LanzadorFormulario.BotonMC[n];

      for (int i = 0; i < n; i++) {
         int idx = i;
         this.botonesTipo[i] = (LanzadorFormulario.BotonMC)this.addRenderableWidget(
            new LanzadorFormulario.BotonMC(this.xR + i * (bw + gap), yTipo, bw, 20, TIPOS[i], () -> {
               this.tipo = idx;
               this.refrescar();
            })
         );
      }

      y = yTipo + 30;
      if (this.conTemporizador || this.rojo) {
         this.etiquetas.add(new LanzadorFormulario.Etiqueta(this.conTemporizador ? "5 · TEMPORIZADOR" : "5 · EXTRAS", this.xR, y));
         this.yTemporizador = y + 11;
         this.temporizador = new UiCampo(this.font, this.xR, this.yTemporizador, 120, 20, Component.literal("Temporizador"));
         this.temporizador.setMaxLength(8);
         this.temporizador.setFilter(s -> s.matches("[0-9:]*"));
         this.temporizador.setHint(Component.literal("Sin temporizador"));
         this.temporizador.setValue(this.temporizadorGuardado);
         this.addRenderableWidget(this.temporizador);
         if (this.rojo) {
            int mitad = (this.anchoR - 6) / 2;
            this.botonMomentos = (LanzadorFormulario.BotonMC)this.addRenderableWidget(
               conAyuda(
                  new LanzadorFormulario.BotonMC(
                     this.xR, this.yTemporizador, mitad, 20, this.textoMomentos(), () -> Minecraft.getInstance().setScreenAndShow(new MomentosPantalla(this))
                  ),
                  "Activa o desactiva los Momentos Reviil (se pueden tener varios a la vez)"
               )
            );
            this.botonPhora = (LanzadorFormulario.BotonInterruptor)this.addRenderableWidget(
               new LanzadorFormulario.BotonInterruptor(
                  this.xR + mitad + 6,
                  this.yTemporizador,
                  this.anchoR - mitad - 6,
                  20,
                  "MOMENTO PHORA",
                  "MOMENTO PHORA: ACTIVO",
                  "Lluvia de misiles de los Phora (cielo, música y todo). Mientras esté activo, las demás opciones quedan bloqueadas. Con temporizador, al acabar se acaba el momento; sin él, sigue hasta DETENER RULETA.",
                  this.phora,
                  v -> {
                     this.phora = v;
                     this.refrescar();
                  }
               )
            );
            this.botonPhora.colorEncendido = -2061794;
         }

         y = this.yTemporizador + 44;
      }

      if (this.color == RuletaColor.ROSA) {
         this.etiquetas.add(new LanzadorFormulario.Etiqueta("6 · QUÉ SALE", this.xR, y));
         this.botonNutria = (LanzadorFormulario.BotonInterruptor)this.addRenderableWidget(
            new LanzadorFormulario.BotonInterruptor(
               this.xR,
               y + 11,
               this.anchoR,
               20,
               "NUTRIA: APAGADO",
               "NUTRIA: ENCENDIDO",
               "Encendido: la ruleta rosa lanza el Momento Nutria (su animación) y luego el anuncio. Apagado: solo la ruleta rosa con su mensaje.",
               this.nutria,
               v -> {
                  this.nutria = v;
                  this.refrescar();
               }
            )
         );
         y += 41;
      }

      if (this.amarillo) {
         this.etiquetas.add(new LanzadorFormulario.Etiqueta("5 · EXTRAS", this.xR, y));
         this.botonCambios = (LanzadorFormulario.BotonMC)this.addRenderableWidget(
            conAyuda(
               new LanzadorFormulario.BotonMC(
                  this.xR,
                  y + 11,
                  this.anchoR,
                  20,
                  this.textoMobs(),
                  () -> Minecraft.getInstance().setScreen(new CambiosPantalla(this, Cambios.PAGINAS_MOBS, "MOBS NUEVOS"))
               ),
               "Elige el mob nuevo (permanente) que se lanza con esta ruleta. Solo se puede elegir uno"
            )
         );
         y += 41;
      }

      if (this.naranja) {
         this.etiquetas.add(new LanzadorFormulario.Etiqueta("5 · EXTRAS", this.xR, y));
         this.botonCambios = (LanzadorFormulario.BotonMC)this.addRenderableWidget(
            conAyuda(
               new LanzadorFormulario.BotonMC(
                  this.xR, y + 11, this.anchoR, 20, this.textoCambios(), () -> Minecraft.getInstance().setScreenAndShow(new CambiosPantalla(this))
               ),
               "Elige el cambio de dificultad (permanente) que se lanza con esta ruleta. Solo se puede elegir uno"
            )
         );
         y += 41;
      }

      if (this.morado) {
         this.etiquetas.add(new LanzadorFormulario.Etiqueta("5 · CASTIGO", this.xR, y));
         this.yTemporizador = y + 11;
         this.botonSancion = (LanzadorFormulario.BotonMC)this.addRenderableWidget(
            conAyuda(new LanzadorFormulario.BotonMC(this.xR, this.yTemporizador, this.anchoR, 20, this.textoSancion(), () -> {
               this.sancion = (this.sancion + 1) % 3;
               this.botonSancion.setMessage(Component.literal(this.textoSancion()));
               this.refrescar();
            }), "Castigo al jugador elegido, 10 segundos después del anuncio: BAN por el tiempo indicado o KICK (\"GG\")")
         );
         int mitad = (this.anchoR - 6) / 2;
         int yFila2 = this.yTemporizador + 26;
         this.botonObjetivo = (LanzadorFormulario.BotonMC)this.addRenderableWidget(
            new LanzadorFormulario.BotonMC(
               this.xR,
               yFila2,
               mitad,
               20,
               this.textoObjetivo(),
               () -> Minecraft.getInstance().setScreenAndShow(new JugadoresPantalla(this, false, nombre -> this.objetivo = nombre))
            )
         );
         this.tiempoBan = new UiCampo(this.font, this.xR + mitad + 6, yFila2, this.anchoR - mitad - 6, 20, Component.literal("Duración ban"));
         this.tiempoBan.setMaxLength(10);
         this.tiempoBan.setFilter(t -> t.matches("[0-9hmHM ]*"));
         this.tiempoBan.setHint(Component.literal("DURACIÓN (30m, 2h)"));
         this.tiempoBan.setValue(this.tiempoBanGuardado);
         this.addRenderableWidget(this.tiempoBan);
         y = yFila2 + 30;
      }

      if (this.rojo) {
         this.etiquetas.add(new LanzadorFormulario.Etiqueta("6 · QUÉ SALE", this.xR, y));
         int yReviil = y + 11;
         this.botonReviil = (LanzadorFormulario.BotonInterruptor)this.addRenderableWidget(
            new LanzadorFormulario.BotonInterruptor(
               this.xR,
               yReviil,
               this.anchoR,
               20,
               "REVIIL: APAGADO",
               "REVIIL: ENCENDIDO",
               "Encendido: la ruleta roja lanza el Momento Reviil. Apagado: solo la ruleta roja con su mensaje.",
               this.reviil,
               v -> {
                  this.reviil = v;
                  this.refrescar();
               }
            )
         );
         int cw = (this.anchoR - gap * 2) / 3;
         int yContenido = yReviil + 26;
         int nc = CONTENIDOS.length;
         this.botonesContenido = new LanzadorFormulario.BotonMC[nc];

         for (int i = 0; i < nc; i++) {
            int idx = i;
            this.botonesContenido[i] = (LanzadorFormulario.BotonMC)this.addRenderableWidget(
               new LanzadorFormulario.BotonMC(this.xR + i * (cw + gap), yContenido, cw, 20, CONTENIDOS[i], () -> {
                  this.opciones[idx] = !this.opciones[idx];
                  this.refrescar();
               })
            );
         }
      }

      this.ySumario = this.yAbajo - 38;
      this.lanzar = (LanzadorFormulario.BotonMC)this.addRenderableWidget(
         new LanzadorFormulario.BotonMC(this.xR, this.yAbajo - 22, this.anchoR, 22, "LANZAR", () -> this.enviar())
      );
      this.lanzar.verde = true;
      this.addRenderableWidget(
         new LanzadorFormulario.BotonVolver(
            this.xPanel + this.anchoPanel - 30, this.yPanel + 9, 20, 20, () -> Minecraft.getInstance().setScreenAndShow(new LanzadorRuleta())
         )
      );
      this.refrescar();
      this.setInitialFocus(this.titulo);
   }

   private static LanzadorFormulario.BotonMC conAyuda(LanzadorFormulario.BotonMC b, String ayuda) {
      b.setTooltip(Tooltip.create(Component.literal(ayuda)));
      return b;
   }

   private String textoPara() {
      return this.para.isEmpty() ? "TODOS LOS JUGADORES" : "SOLO: " + this.para;
   }

   private void rellenarMomentos() {
      if (this.rojo && this.titulo != null && this.mensaje != null) {
         String firma = String.join(",", MomentosCliente.armados());
         if (!firma.equals(this.momentosVistos)) {
            boolean primera = this.momentosVistos == null;
            this.momentosVistos = firma;
            if (!primera || this.titulo.getValue().isBlank() && this.mensaje.getValue().isBlank()) {
               String t = "";
               StringBuilder x = new StringBuilder();

               for (String id : MomentosCliente.armados()) {
                  Momentos.Momento mo = Momentos.porId(id);
                  if (mo != null) {
                     if (x.length() > 0) {
                        x.append(" ");
                     }

                     x.append(mo.texto());
                     t = "Momento Reviil";
                  }
               }

               if (x.length() != 0 || this.titulo.getValue().equals(this.tituloPuesto) && this.mensaje.getValue().equals(this.mensajePuesto)) {
                  this.titulo.setValue(t);
                  this.mensaje.setValue(x.toString());
                  this.tituloPuesto = this.titulo.getValue();
                  this.mensajePuesto = this.mensaje.getValue();
                  if (x.length() > 0 && this.opciones != null && this.opciones.length > 0) {
                     this.opciones[0] = true;
                  }
               }
            }
         }
      }
   }

   private void rellenarCambios() {
      if ((this.naranja || this.amarillo) && this.titulo != null && this.mensaje != null) {
         String firma = String.join(",", this.elegidos);
         if (!firma.equals(this.cambiosVistos)) {
            boolean primera = this.cambiosVistos == null;
            this.cambiosVistos = firma;
            if (!primera || this.titulo.getValue().isBlank() && this.mensaje.getValue().isBlank()) {
               String t = "";
               String x = "";

               for (String id : this.elegidos) {
                  Cambios.Entrada e = Cambios.porId(id);
                  if (e != null) {
                     x = e.texto();
                     t = this.naranja ? "Cambios de dificultad" : e.titulo();
                     break;
                  }
               }

               if (!x.isEmpty() || this.titulo.getValue().equals(this.tituloPuesto) && this.mensaje.getValue().equals(this.mensajePuesto)) {
                  this.titulo.setValue(t);
                  this.mensaje.setValue(x);
                  this.tituloPuesto = this.titulo.getValue();
                  this.mensajePuesto = this.mensaje.getValue();
               }
            }
         }
      }
   }

   private String textoMomentos() {
      int n = MomentosCliente.armados().size();
      return n == 0 ? "MOMENTOS REVIIL" : "MOMENTOS REVIIL (" + n + ")";
   }

   private String textoSancion() {
      return switch (this.sancion) {
         case 1 -> "CASTIGO: BAN";
         case 2 -> "CASTIGO: KICK (GG)";
         default -> "CASTIGO: NINGUNO";
      };
   }

   private String textoObjetivo() {
      return this.objetivo.isEmpty() ? "ELEGIR JUGADOR..." : this.objetivo;
   }

   private String textoMobs() {
      this.elegidos.removeIf(CambiosCliente::activo);
      return this.elegidos.isEmpty() ? "MOBS NUEVOS" : "MOBS NUEVOS (" + this.elegidos.size() + ")";
   }

   private String textoCambios() {
      this.elegidos.removeIf(CambiosCliente::activo);
      return this.elegidos.isEmpty() ? "CAMBIOS" : "CAMBIOS (" + this.elegidos.size() + ")";
   }

   private void refrescar() {
      for (int i = 0; i < this.botonesTipo.length; i++) {
         this.botonesTipo[i].elegido = i == this.tipo;
      }

      if (this.botonesContenido != null) {
         for (int i = 0; i < this.botonesContenido.length; i++) {
            this.botonesContenido[i].elegido = this.opciones[i];
         }
      }

      if (this.tiempoBan != null) {
         this.tiempoBan.visible = this.sancion == 1;
         this.tiempoBan.active = this.sancion == 1;
      }

      if (this.botonObjetivo != null) {
         this.botonObjetivo.visible = this.sancion != 0;
      }

      if (this.temporizador != null) {
         boolean ver = this.temporizadorVisible();
         this.temporizador.visible = ver;
         this.temporizador.active = ver;
      }

      if (this.rojo && this.botonMomentos != null) {
         this.botonMomentos.visible = !this.phora;
         if (this.temporizador != null) {
            this.temporizador.setWidth(Math.min(120, this.botonMomentos.getWidth()));
         }
      }
   }

   private boolean temporizadorVisible() {
      return this.conTemporizador || this.rojo && this.phora;
   }

   private static String limpiar(String s) {
      return s == null ? "" : s.replace("\r", "").trim();
   }

   private String tiempoElegido() {
      return this.temporizadorVisible() ? limpiar(this.temporizador.getValue()) : "";
   }

   private LanzarPayload construir() {
      boolean conTexto = !this.rojo || this.opciones[0];
      String t = conTexto ? limpiar(this.titulo.getValue()) : "";
      String m = conTexto ? limpiar(this.mensaje.getValue()) : "";
      boolean animacion = this.rojo && this.opciones[1] || this.color == RuletaColor.ROSA && this.nutria;
      boolean cielo = this.rojo && this.opciones[2];
      String cambios = !this.naranja && !this.amarillo ? "" : String.join(",", this.elegidos);
      int sancionElegida = this.morado ? this.sancion : 0;
      String tiempo = this.tiempoBan != null ? limpiar(this.tiempoBan.getValue()) : "";
      return this.rojo && this.phora
         ? new LanzarPayload(this.color.ordinal(), this.tipo, false, false, t, m, this.tiempoElegido(), "", false, "", 0, "", "", true)
         : new LanzarPayload(
            this.color.ordinal(),
            this.tipo,
            animacion,
            this.rojo && this.reviil && this.para.isEmpty(),
            t,
            m,
            this.tiempoElegido(),
            cambios,
            cielo,
            this.para,
            sancionElegida,
            this.objetivo,
            tiempo,
            false,
            (this.naranja || this.amarillo) && !this.conRuleta
         );
   }

   private boolean tiempoOk() {
      String s = this.tiempoElegido();
      return s.isEmpty() || tiempoValido(s);
   }

   private void enviar() {
      if (this.lanzar.active) {
         RedCliente.enviar(this.construir());
         this.onClose();
      }
   }

   public void removed() {
      EscalaGui.restaurar();
      Fondo.quitar();
      Ui.alfa = 1.0F;
   }

   private String nombreRuleta() {
      String nombre = Mensajes.encabezado(this.color);
      return nombre.endsWith(":") ? nombre.substring(0, nombre.length() - 1) : nombre;
   }

   private String resumen() {
      String alcance = this.para.isEmpty() ? "para todos" : "solo para " + this.para;
      if (this.rojo && this.phora) {
         return "Sale: Momento Phora · " + alcance;
      }

      if (!this.rojo || !this.reviil && MomentosCliente.armados().isEmpty()) {
         String cuerpo = this.rojo ? this.resumenRoja() : (this.tipo == 0 ? "mini" : "normal");
         if (this.color == RuletaColor.ROSA) {
            cuerpo = cuerpo + (this.nutria ? " con Momento Nutria" : " (solo la ruleta)");
         }

         return "Sale: " + this.nombreRuleta() + " " + cuerpo + " · " + alcance;
      } else {
         return "Sale: Momento Reviil · " + alcance;
      }
   }

   private String resumenRoja() {
      List<String> l = new ArrayList<>();
      if (this.opciones[0]) {
         l.add("mensaje");
      }

      if (this.opciones[1]) {
         l.add("animación");
      }

      if (this.opciones[2]) {
         l.add("cielo");
      }

      return l.isEmpty() ? "solo la ruleta" : "con " + String.join(" + ", l);
   }

   private void dibujarPanel(GuiGraphicsExtractor g) {
      int m = Math.round((1.0F - Ui.ease(this.aparece.v)) * 8.0F);
      int x = this.xPanel + m;
      int y = this.yPanel + m;
      int w = this.anchoPanel - m * 2;
      int h = this.altoPanel - m * 2;
      Ui.sombra(g, x, y, w, h, 8, 1.0F);
      Ui.cajaBorde(g, x, y, w, h, 8, Ui.c(1119259, 0.97F), Ui.c(3028037));
      Ui.caja(g, x + 1, y + 36, w - 2, 1, 0, Ui.c(3028037, 0.9F));
      Ui.caja(g, x + 14, y + 36, 56, 1, 0, Ui.c(this.color.rgb));
      int sep = this.x0 + this.ancho + 6;
      Ui.caja(g, sep, this.yTop, 1, this.yAbajo - this.yTop, 0, Ui.c(3028037, 0.8F));
   }

   static void caja(GuiGraphicsExtractor g, int x, int y, int w, int h, int cara, int borde) {
      Ui.cajaBorde(g, x, y, w, h, 4, cara, borde);
   }

   public void render(GuiGraphicsExtractor ctx, int mx, int my, float pt) {
      float aparicion = this.aparece.hacia(1.0F, 11.0F);
      Ui.alfa = aparicion;
      this.fondoFormulario(ctx);
      this.dibujarPanel(ctx);
      RuletaHud.icono(ctx, this.x0, this.yPanel + 8, 22, this.color);
      ctx.flush();
      Ui.textoN(ctx, this.font, this.nombreRuleta(), this.x0 + 30, this.yPanel + 10, this.color.rgb, 1.0F);
      Ui.texto(ctx, this.font, "Rellena las secciones de izquierda a derecha y pulsa LANZAR", this.x0 + 30, this.yPanel + 22, 9344683, 1.0F);

      for (LanzadorFormulario.Etiqueta e : this.etiquetas) {
         Ui.textoN(ctx, this.font, e.texto(), e.x(), e.y(), 9344683, 1.0F);
      }

      int largo = limpiar(this.mensaje.getValue()).length();
      boolean pasa = largo > 1000;
      boolean tiempoMal = !this.tiempoOk();
      boolean sancionMal = this.morado
         && this.sancion != 0
         && (this.objetivo.isEmpty() || this.sancion == 1 && Sanciones.leerTiempo(this.tiempoBan.getValue()) <= 0L);
      this.lanzar.active = !pasa && !tiempoMal && !sancionMal;
      boolean conSancion = this.morado && this.sancion != 0;
      if (conSancion && !this.para.isEmpty()) {
         this.para = "";
      }

      boolean siempreTodos = this.color == RuletaColor.ROSA || this.naranja || this.amarillo;
      if (siempreTodos) {
         this.para = "";
      }

      if (this.botonPara != null) {
         this.botonPara.setMessage(Component.literal(this.textoPara()));
         this.botonPara.active = (!this.rojo || !this.phora) && !conSancion && !siempreTodos;
      }

      if (this.botonMomentos != null) {
         this.botonMomentos.setMessage(Component.literal(this.textoMomentos()));
      }

      this.rellenarMomentos();
      this.rellenarCambios();
      boolean paraUno = !this.para.isEmpty();
      if (paraUno) {
         this.elegidos.clear();
      }

      if (this.botonCambios != null) {
         this.botonCambios.active = !paraUno;
         this.botonCambios.setMessage(Component.literal(this.amarillo ? this.textoMobs() : this.textoCambios()));
      }

      if (this.botonMomentos != null) {
         this.botonMomentos.active = !paraUno;
      }

      if (this.botonReviil != null) {
         boolean hayMomentos = !MomentosCliente.armados().isEmpty() && !paraUno;
         if (hayMomentos && !this.reviil) {
            this.reviil = true;
            this.botonReviil.forzar(true);
         }

         this.botonReviil.active = !paraUno && !hayMomentos && !this.phora;
      }

      if (this.botonPhora != null) {
         boolean enCurso = PhoraCliente.enCurso();
         if ((enCurso || paraUno) && this.phora) {
            this.phora = false;
            this.botonPhora.forzar(false);
            this.refrescar();
         }

         this.botonPhora.active = !enCurso && !paraUno;
         this.botonPhora
            .setTooltip(
               Tooltip.create(
                  Component.literal(
                     enCurso
                        ? "Ya hay un Momento Phora en curso: no se puede lanzar otro hasta que se termine."
                        : (
                           paraUno
                              ? "El Momento Phora es para todos: quita el jugador de PARA para poder usarlo."
                              : "Lluvia de misiles de los Phora (cielo, música y todo). Mientras esté activo, las demás opciones quedan bloqueadas. Con temporizador, al acabar se acaba el momento; sin él, sigue hasta DETENER RULETA."
                        )
                  )
               )
            );
      }

      if (this.rojo && this.phora) {
         if (this.botonesContenido != null) {
            for (LanzadorFormulario.BotonMC b : this.botonesContenido) {
               b.active = false;
            }
         }

         if (this.botonMomentos != null) {
            this.botonMomentos.active = false;
         }
      } else if (this.botonesContenido != null) {
         boolean momentos = !MomentosCliente.armados().isEmpty() && !paraUno;
         boolean luna = momentos && MomentosCliente.activado("luna");
         boolean bloquear = (this.reviil || momentos) && !paraUno;
         if (bloquear) {
            boolean[] forzado = new boolean[]{true, true, !luna};
            if (this.contenidoGuardado == null) {
               this.contenidoGuardado = (boolean[])this.opciones.clone();
            }

            if (!Arrays.equals(this.opciones, forzado)) {
               System.arraycopy(forzado, 0, this.opciones, 0, 3);
               this.refrescar();
            }
         } else if (this.contenidoGuardado != null) {
            System.arraycopy(this.contenidoGuardado, 0, this.opciones, 0, 3);
            this.contenidoGuardado = null;
            this.refrescar();
         }

         for (int i = 0; i < this.botonesContenido.length; i++) {
            this.botonesContenido[i].active = !bloquear;
         }
      }

      if (this.botonObjetivo != null) {
         this.botonObjetivo.setMessage(Component.literal(this.textoObjetivo()));
      }

      String contador = largo + "/1000";
      Ui.textoDerecha(ctx, this.font, contador, this.x0 + this.ancho, this.yContador, pasa ? 16735340 : 9344683, 1.0F);
      if (pasa) {
         Ui.texto(ctx, this.font, "Demasiado largo: acorta el texto", this.x0, this.yContador, 16735340, 1.0F);
      }

      if (this.temporizadorVisible()) {
         String vacio = this.rojo && this.phora ? "vacío = hasta DETENER RULETA" : "vacío = sin temporizador";
         String ayuda = tiempoMal ? "Usa 59:59 o hasta 24:00:00" : "mm:ss o hh:mm:ss (" + vacio + ")";
         Ui.texto(ctx, this.font, ayuda, this.xR, this.yTemporizador + 24, tiempoMal ? 16735340 : 9344683, 1.0F);
      }

      Ui.caja(ctx, this.xR, this.ySumario - 6, this.anchoR, 1, 0, Ui.c(3028037, 0.8F));
      String resumen = Ui.recortar(this.font, this.resumen(), this.anchoR);
      Ui.texto(ctx, this.font, resumen, this.xR, this.ySumario, 13160411, 1.0F);
      super.render(ctx, mx, my, pt);
      Ui.alfa = 1.0F;
   }

   public void renderBackground(GuiGraphicsExtractor g, int mouseX, int mouseY, float parcialFondo) {
   }

   private void fondoFormulario(GuiGraphicsExtractor g) {
      float a = this.aparece.v;
      g.fillGradient(0, 0, this.width, this.height, Ui.c(329484, 0.55F * a), Ui.c(329484, 0.78F * a));
   }

   public boolean isPauseScreen() {
      return false;
   }

   static class BotonInterruptor extends AbstractButton {
      private boolean activo;
      private final String textoApagado;
      private final String textoEncendido;
      private final Consumer<Boolean> alCambiar;
      int colorEncendido;
      private final Suave hover = new Suave();
      private final Suave enc = new Suave();

      BotonInterruptor(int x, int y, int w, int h, String textoApagado, String textoEncendido, String ayuda, boolean activo, Consumer<Boolean> alCambiar) {
         super(x, y, w, h, Component.empty());
         this.activo = activo;
         this.textoApagado = textoApagado;
         this.textoEncendido = textoEncendido;
         this.alCambiar = alCambiar;
         this.setTooltip(Tooltip.create(Component.literal(ayuda)));
      }

      public void onPress() {
         this.activo = !this.activo;
         this.alCambiar.accept(this.activo);
      }

      void forzar(boolean v) {
         this.activo = v;
      }

      public void renderWidget(GuiGraphicsExtractor g, int mx, int my, float pt) {
         int x = this.getX();
         int y = this.getY();
         int w = this.width;
         int h = this.height;
         float hv = this.hover.hacia(this.isHoveredOrFocused() && this.active ? 1.0F : 0.0F, 16.0F);
         float e = this.enc.hacia(this.activo ? 1.0F : 0.0F, 14.0F);
         float a = this.active ? 1.0F : 0.4F;
         int tono = this.colorEncendido != 0 ? this.colorEncendido & 16777215 : 7113983;
         int cara = Ui.mezcla(Ui.mezcla(1711656, 2369846, hv), Ui.mezcla(tono, 1711656, 0.6F), e);
         int borde = Ui.mezcla(Ui.mezcla(3028037, tono, hv * 0.8F), tono, e);
         Ui.cajaBorde(g, x, y, w, h, 4, Ui.c(cara, a), Ui.c(borde, a));
         Ui.caja(g, x + 7, y + h / 2 - 2, 5, 5, 2, Ui.c(Ui.mezcla(9344683, tono, e), a));
         Font f = Minecraft.getInstance().font;
         String texto = Ui.recortar(f, this.activo ? this.textoEncendido : this.textoApagado, w - 26);
         Ui.textoN(g, f, texto, x + 17, y + (h - 8) / 2, e > 0.5F ? 16777215 : 15330549, a);
      }

      protected void updateWidgetNarration(NarrationElementOutput out) {
         this.defaultButtonNarrationText(out);
      }
   }

   static class BotonMC extends AbstractButton {
      private final Runnable alPulsar;
      boolean verde;
      boolean elegido;
      private long desde = -1L;
      private final Suave hover = new Suave();
      private final Suave marca = new Suave();

      BotonMC(int x, int y, int w, int h, String texto, Runnable alPulsar) {
         super(x, y, w, h, Component.literal(texto));
         this.alPulsar = alPulsar;
      }

      public void onPress() {
         this.alPulsar.run();
      }

      public void renderWidget(GuiGraphicsExtractor g, int mx, int my, float pt) {
         int x = this.getX();
         int y = this.getY();
         int w = this.width;
         int h = this.height;
         boolean sobre = this.isHoveredOrFocused() && this.active;
         float hv = this.hover.hacia(sobre ? 1.0F : 0.0F, 16.0F);
         float el = this.marca.hacia(this.elegido ? 1.0F : 0.0F, 14.0F);
         float a = this.active ? 1.0F : 0.4F;
         int cara;
         int borde;
         int color;
         if (this.verde) {
            cara = Ui.mezcla(Ui.mezcla(7113983, 1119259, 0.2F), 7113983, hv);
            borde = Ui.mezcla(7113983, 16777215, hv * 0.4F);
            color = 16777215;
         } else {
            cara = Ui.mezcla(Ui.mezcla(1711656, 2369846, hv), Ui.mezcla(7113983, 1711656, 0.55F), el);
            borde = Ui.mezcla(Ui.mezcla(3028037, 7113983, hv * 0.8F), 7113983, el);
            color = el > 0.5F ? 16777215 : 15330549;
         }

         Ui.cajaBorde(g, x, y, w, h, 4, Ui.c(cara, a), Ui.c(borde, a));
         if (!this.verde && hv > 0.05F) {
            int barra = Math.round((w - 12) * hv);
            Ui.caja(g, x + (w - barra) / 2, y + h - 2, barra, 1, 0, Ui.c(7113983, a));
         }

         Font f = Minecraft.getInstance().font;
         String texto = this.getMessage().getString();
         int area = w - 10;
         int tw = Ui.anchoN(f, texto);
         int ty = y + (h - 8) / 2;
         if (tw <= area) {
            Ui.textoN(g, f, texto, x + (w - tw) / 2, ty, color, a);
         } else if (!sobre) {
            this.desde = -1L;
            Ui.textoN(g, f, Ui.recortar(f, texto, area), x + 5, ty, color, a);
         } else {
            long ahora = Util.getMillis();
            if (this.desde < 0L) {
               this.desde = ahora;
            }

            float ciclo = tw + 28;
            float off = (float)(ahora - this.desde) / 1000.0F * 34.0F % ciclo;
            int inicio = x + 5 - (int)off;
            g.enableScissor(x + 5, y, x + w - 5, y + h);
            Ui.textoN(g, f, texto, inicio, ty, color, a);
            Ui.textoN(g, f, texto, inicio + (int)ciclo, ty, color, a);
            g.disableScissor();
         }
      }

      protected void updateWidgetNarration(NarrationElementOutput out) {
         this.defaultButtonNarrationText(out);
      }
   }

   static class BotonVolver extends AbstractButton {
      private static final String[] FLECHA = new String[]{
         "..X......", ".XX......", "XXXXXXX..", ".XX....X.", "..X.....X", "........X", ".......X.", "..XXXXX.."
      };
      private final Runnable alPulsar;
      private final Suave hover = new Suave();

      BotonVolver(int x, int y, int w, int h, Runnable alPulsar) {
         this(x, y, w, h, alPulsar, "Volver a la ruleta");
      }

      BotonVolver(int x, int y, int w, int h, Runnable alPulsar, String ayuda) {
         super(x, y, w, h, Component.empty());
         this.alPulsar = alPulsar;
         this.setTooltip(Tooltip.create(Component.literal(ayuda)));
      }

      public void onPress() {
         this.alPulsar.run();
      }

      public void renderWidget(GuiGraphicsExtractor g, int mx, int my, float pt) {
         float hv = this.hover.hacia(this.isHoveredOrFocused() ? 1.0F : 0.0F, 16.0F);
         Ui.cajaBorde(
            g, this.getX(), this.getY(), this.width, this.height, 4, Ui.c(Ui.mezcla(1711656, 2369846, hv)), Ui.c(Ui.mezcla(3028037, 7113983, hv * 0.8F))
         );
         int cell = 2;
         int ox = this.getX() + (this.width - 9 * cell) / 2;
         int oy = this.getY() + (this.height - FLECHA.length * cell) / 2;
         int color = Ui.c(Ui.mezcla(9344683, 16777215, hv));

         for (int r = 0; r < FLECHA.length; r++) {
            for (int c = 0; c < 9; c++) {
               if (FLECHA[r].charAt(c) == 'X') {
                  g.fill(ox + c * cell, oy + r * cell, ox + (c + 1) * cell, oy + (r + 1) * cell, color);
               }
            }
         }
      }

      protected void updateWidgetNarration(NarrationElementOutput out) {
         this.defaultButtonNarrationText(out);
      }
   }

   private record Etiqueta(String texto, int x, int y) {
   }
}
