package net.ruleta.client;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.ruleta.Ajustes;
import net.ruleta.MomentoPayload;
import net.ruleta.Momentos;
import net.ruleta.Tiempo;
import net.ruleta.client.eclipse.EclipseCliente;
import net.ruleta.client.ui.Suave;
import net.ruleta.client.ui.Ui;
import net.ruleta.client.ui.UiBoton;
import net.ruleta.client.ui.UiCampo;
import net.ruleta.client.ui.UiPantalla;

public class MomentosPantalla extends UiPantalla {
   private static final int ESPACIO = 10;
   private static final int COLUMNAS = 3;
   private static final int ENCABEZADO = 86;
   private static final ResourceLocation CARA = ResourceLocation.fromNamespaceAndPath("ruleta", "textures/gui/reviil_momentos.png");
   private final List<MomentosPantalla.Tarjeta> tarjetas = new ArrayList<>();
   private final Map<String, Suave[]> anim = new HashMap<>();
   private int cardX;
   private int cardW;
   private int top;
   private int bottom;
   private int altoTotal;
   private final Suave scroll = new Suave();
   private float objetivo;

   public MomentosPantalla(Screen padre) {
      super("Momentos Reviil", padre);
   }

   protected void init() {
      this.panel(900, 520);
      this.cabecera(true);
      this.subtitulo("Elige los que quieras: se activan cuando sale el mensaje de la ruleta roja");
      this.cardX = this.px + 16;
      this.cardW = this.pw - 32;
      this.top = this.py + 52 + 86;
      this.bottom = this.py + this.ph - 14;
      this.tarjetas.clear();
      this.altoTotal = 0;
      int col = (this.cardW - 20 - 6) / 3;
      int interior = col - 28;
      List<Momentos.Momento> todos = new ArrayList<>(Momentos.TODOS);
      int y = 0;

      for (int fila = 0; fila * 3 < todos.size(); fila++) {
         int altoFila = 0;
         List<List<String>> textos = new ArrayList<>();
         int n = Math.min(3, todos.size() - fila * 3);

         for (int c = 0; c < n; c++) {
            Momentos.Momento m = todos.get(fila * 3 + c);
            List<String> texto = Ui.lineas(this.font, m.texto().replace("{bunker}", "las coordenadas del bunker"), interior, 8);
            textos.add(texto);
            altoFila = Math.max(altoFila, 42 + texto.size() * 10 + 12);
         }

         for (int c = 0; c < n; c++) {
            this.tarjetas.add(new MomentosPantalla.Tarjeta(todos.get(fila * 3 + c), textos.get(c), this.cardX + c * (col + 10), y, col, altoFila));
         }

         y += altoFila + 10;
      }

      this.altoTotal = Math.max(0, y - 10);
      this.objetivo = Math.max(0.0F, Math.min(this.objetivo, (float)this.maxScroll()));
   }

   private int maxScroll() {
      return Math.max(0, this.altoTotal - (this.bottom - this.top));
   }

   private static boolean bloqueado(Momentos.Momento m) {
      if (lunaFija(m)) {
         return true;
      } else if (MomentosCliente.activado(m.id())) {
         return false;
      } else if (!eclipseBloqueado(m) && !eclipseSolo(m)) {
         if ("creepers_negativas".equals(m.id()) && !MomentosCliente.enEfecto("gas_abajo")) {
            return true;
         } else {
            for (String otro : MomentosCliente.armados()) {
               if (Momentos.chocan(otro, m.id())) {
                  return true;
               }
            }

            return false;
         }
      } else {
         return true;
      }
   }

   private static boolean eclipseSolo(Momentos.Momento m) {
      return "eclipse".equals(m.id()) ? !MomentosCliente.armados().isEmpty() : MomentosCliente.activado("eclipse") || EclipseCliente.activo();
   }

   private static boolean eclipseBloqueado(Momentos.Momento m) {
      return "eclipse".equals(m.id()) && !CambiosCliente.activo("herobrine");
   }

   private static boolean lunaFija(Momentos.Momento m) {
      return "luna".equals(m.id()) && MomentosCliente.enEfecto("luna");
   }

   private static boolean lanzado(Momentos.Momento m) {
      return MomentosCliente.activado(m.id()) && MomentosCliente.enEfecto(m.id());
   }

   public boolean mouseScrolled(double mx, double my, double deltaX, double delta) {
      this.objetivo = Math.max(0.0F, Math.min(this.objetivo - (float)delta * 34.0F, (float)this.maxScroll()));
      return true;
   }

   public boolean mouseClicked(double mx, double my, int boton) {
      if (super.mouseClicked(mx, my, boton)) {
         return true;
      } else {
         if (boton == 0 && my >= this.top && my < this.bottom && mx >= this.cardX && mx < this.cardX + this.cardW) {
            for (MomentosPantalla.Tarjeta t : this.tarjetas) {
               int y = this.top - Math.round(this.scroll.v) + t.y();
               if (my >= y && my < y + t.alto() && mx >= t.x() && mx < t.x() + t.ancho()) {
                  Momentos.Momento m = t.momento();
                  if (!lunaFija(m) && !lanzado(m)) {
                     if (MomentosCliente.activado(m.id())) {
                        RedCliente.enviar(new MomentoPayload(m.id(), false, 0));
                     } else if (!bloqueado(m)) {
                        if ("eclipse".equals(m.id())) {
                           Minecraft.getInstance().setScreen(new PantallaEclipse(this));
                        } else if (m.temporizador()) {
                           Minecraft.getInstance().setScreen(new MomentosPantalla.Temporizador(this, m));
                        } else {
                           RedCliente.enviar(new MomentoPayload(m.id(), true, 0));
                        }
                     }

                     Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI((SoundEvent)SoundEvents.UI_BUTTON_CLICK.value(), 1.2F, 0.5F));
                     return true;
                  }

                  return true;
               }
            }
         }

         return false;
      }
   }

   @Override
   protected void contenido(GuiGraphics g, int mx, int my, float pt) {
      float sc = this.scroll.hacia(this.objetivo, 16.0F);
      this.encabezado(g);
      g.enableScissor(this.cardX - 4, this.top, this.cardX + this.cardW + 4, this.bottom);

      for (MomentosPantalla.Tarjeta t : this.tarjetas) {
         int y = this.top - Math.round(sc) + t.y();
         if (y + t.alto() >= this.top && y <= this.bottom) {
            this.dibujar(g, t, y, mx, my);
         }
      }

      g.disableScissor();
      int visible = this.bottom - this.top;
      if (this.altoTotal > visible) {
         int barra = Math.max(16, visible * visible / this.altoTotal);
         int by = this.top + Math.round((visible - barra) * (sc / this.maxScroll()));
         Ui.caja(g, this.cardX + this.cardW + 2, this.top, 2, visible, 1, Ui.c(2369846, 0.5F));
         Ui.caja(g, this.cardX + this.cardW + 2, by, 2, barra, 1, Ui.c(9344683));
      }
   }

   private void encabezado(GuiGraphics g) {
      int y0 = this.py + 46;
      int ih = 78;
      int iw = 84;
      RenderSystem.enableBlend();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, Ui.alfa);
      g.blit(CARA, this.px + 24, y0, iw, ih, 0.0F, 0.0F, 96, 104, 96, 104);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      String t = "MOMENTOS REVIIL";
      int xt = this.px + 24 + iw + 16;
      float escala = Math.min(3.0F, (float)(this.pw - (xt - this.px) - 40) / Math.max(1, Ui.anchoN(this.font, t)));
      Ui.titulo3D(g, this.font, t, xt, y0 + (ih - Math.round(9.0F * escala)) / 2 - 2, escala, 14687774, 16741480, 7211016, 2359296);
   }

   private void dibujar(GuiGraphics g, MomentosPantalla.Tarjeta t, int y, int mx, int my) {
      Momentos.Momento m = t.momento();
      boolean activo = MomentosCliente.activado(m.id());
      boolean bloq = bloqueado(m);
      int x = t.x();
      int w = t.ancho();
      Suave[] a = this.anim.computeIfAbsent(m.id(), k -> new Suave[]{new Suave(), new Suave(), new Suave()});
      boolean encima = !bloq && my >= this.top && my < this.bottom && mx >= x && mx < x + w && my >= y && my < y + t.alto();
      float hv = a[0].hacia(encima ? 1.0F : 0.0F, 14.0F);
      float ac = a[1].hacia(activo ? 1.0F : 0.0F, 10.0F);
      float bl = a[2].hacia(bloq ? 1.0F : 0.0F, 10.0F);
      int sube = Math.round(hv * 2.0F);
      y -= sube;
      float opaco = 1.0F - bl * 0.55F;
      Ui.sombra(g, x, y, w, t.alto(), 8, 0.5F + hv * 0.6F);
      int borde = Ui.mezcla(Ui.mezcla(3028037, 4906624, ac), 7113983, hv * (1.0F - ac) * 0.8F);
      int relleno = Ui.mezcla(Ui.mezcla(1711656, 2369846, hv * 0.8F), Ui.mezcla(4906624, 1711656, 0.9F), ac * 0.7F);
      Ui.cajaBorde(g, x, y, w, t.alto(), 8, Ui.c(relleno, opaco), Ui.c(borde, opaco));
      int tx = x + 14;
      int ty = y + 12;
      String estado = lunaFija(m)
         ? "LANZADO · se libera cuando alguien mire la Luna"
         : (
            bloq && "creepers_negativas".equals(m.id()) && !MomentosCliente.enEfecto("gas_abajo")
               ? "BLOQUEADO · necesita el Gas tóxico (bajar) en curso"
               : null
         );
      if (estado == null && eclipseBloqueado(m)) {
         estado = "BLOQUEADO · falta el cambio Herobrine";
      } else if (estado == null && !activo && eclipseSolo(m)) {
         estado = "BLOQUEADO · el Eclipse va solo";
      }

      if (estado == null && lanzado(m)) {
         estado = "eclipse".equals(m.id()) ? "ACTIVO · hasta que acaben sus 4 Momentos Reviil" : "SELECCIONADO · ya lanzado";
      }

      estado = estado != null
         ? estado
         : (bloq ? "NO COMPATIBLE" : (activo ? (MomentosCliente.enEfecto(m.id()) ? "ACTIVADO" : "ACTIVADO · empieza con el mensaje") : "DESACTIVADO"));
      int colorEstado = bloq ? 9344683 : (activo ? 4906624 : 16735340);
      Ui.caja(g, tx, ty + 1, 6, 6, 3, Ui.c(colorEstado, opaco));
      Ui.textoN(g, this.font, Ui.recortarN(this.font, estado, w - 44 - 6), tx + 12, ty, colorEstado, opaco);
      ty += 16;
      String titulo = Ui.recortarN(this.font, m.titulo().replace("Momento Reviil — ", "").replace("Momento ReviiL — ", ""), w - 28);
      Ui.textoN(g, this.font, titulo, tx, ty, bloq ? 9344683 : 16762967, opaco);
      ty += 14;

      for (String linea : t.texto()) {
         Ui.texto(g, this.font, linea, tx, ty, bloq ? 9344683 : 15330549, opaco * 0.92F);
         ty += 10;
      }
   }

   private static String hhmm(int segundos) {
      return Ui.mmss(segundos);
   }

   private record Tarjeta(Momentos.Momento momento, List<String> texto, int x, int y, int ancho, int alto) {
   }

   static class Temporizador extends UiPantalla {
      private final MomentosPantalla padreM;
      private final Momentos.Momento momento;
      private final boolean bunker;
      private UiCampo tiempo;
      private UiCampo cerrada;
      private UiBoton aceptar;
      private UiBoton botonBunkers;
      private String bunkerElegido = "";
      private String guardadoT = null;
      private String guardadoC = null;

      Temporizador(MomentosPantalla padre, Momentos.Momento momento) {
         super("Temporizador", padre);
         this.padreM = padre;
         this.momento = momento;
         this.bunker = Momentos.delBunker(momento.id());
      }

      private int porDefectoT() {
         return this.bunker ? Ajustes.entero("bunker.contador") : Ajustes.entero("gas.contador");
      }

      private int porDefectoC() {
         return Ajustes.entero("bunker.cerrado");
      }

      private UiCampo caja(int x, int y, int w, String inicial) {
         UiCampo e = new UiCampo(this.font, x, y, w, 24, Component.literal("Temporizador"));
         e.setMaxLength(8);
         e.setFilter(t -> t.matches("[0-9:]*"));
         e.setValue(inicial);
         e.malo(() -> {
            String v = e.getValue().trim();
            return !v.isEmpty() && !LanzadorFormulario.tiempoValido(v);
         });
         return (UiCampo)this.addRenderableWidget(e);
      }

      protected void init() {
         this.panel(460, this.bunker ? 330 : 250);
         this.cabecera(true);
         this.subtitulo(this.momento.titulo().replace("Momento Reviil — ", "").replace("Momento ReviiL — ", ""));
         if (this.tiempo != null) {
            this.guardadoT = this.tiempo.getValue();
         }

         if (this.cerrada != null) {
            this.guardadoC = this.cerrada.getValue();
         }

         int x = this.px + 24;
         int w = this.pw - 48;
         int y = this.py + 76;
         this.tiempo = this.caja(x, y, 150, this.guardadoT != null ? this.guardadoT : MomentosPantalla.hhmm(this.porDefectoT()));
         if (this.bunker) {
            y += 66;
            this.cerrada = this.caja(x, y, 150, this.guardadoC != null ? this.guardadoC : MomentosPantalla.hhmm(this.porDefectoC()));
            y += 56;
            this.botonBunkers = (UiBoton)this.addRenderableWidget(
               new UiBoton(
                  x,
                  y,
                  w,
                  26,
                  PantallaBunkers.texto(this.bunkerElegido),
                  UiBoton.Estilo.NORMAL,
                  () -> Minecraft.getInstance().setScreen(new PantallaBunkers(this, this.bunkerElegido, k -> this.bunkerElegido = k))
               )
            );
         }

         int yb = this.py + this.ph - 14 - 30;
         this.aceptar = (UiBoton)this.addRenderableWidget(
            new UiBoton(this.px + this.pw - 24 - 130, yb, 130, 30, "Activar", UiBoton.Estilo.PRIMARIO, this::activar)
         );
         this.addRenderableWidget(new UiBoton(this.px + this.pw - 24 - 130 - 10 - 100, yb, 100, 30, "Cancelar", UiBoton.Estilo.NORMAL, this::onClose));
         this.setInitialFocus(this.tiempo);
      }

      private static boolean campoOk(String v) {
         String t = v.trim();
         return t.isEmpty() || LanzadorFormulario.tiempoValido(t);
      }

      private boolean valido() {
         return campoOk(this.tiempo.getValue()) && (!this.bunker || campoOk(this.cerrada.getValue()));
      }

      private void activar() {
         if (this.valido()) {
            String vt = this.tiempo.getValue().trim();
            int t = vt.isEmpty() ? this.porDefectoT() : Tiempo.leer(vt);
            String vc = this.bunker ? this.cerrada.getValue().trim() : "";
            int c = !this.bunker ? 0 : (vc.isEmpty() ? this.porDefectoC() : Tiempo.leer(vc));
            RedCliente.enviar(new MomentoPayload(this.momento.id(), true, t, c, this.bunker ? this.bunkerElegido : ""));
            this.onClose();
         }
      }

      @Override
      protected void contenido(GuiGraphics g, int mx, int my, float pt) {
         this.aceptar.active = this.valido();
         if (this.botonBunkers != null) {
            this.botonBunkers.setTexto(Ui.recortar(this.font, PantallaBunkers.texto(this.bunkerElegido), this.pw - 48 - 10));
         }

         int x = this.px + 24;
         int y = this.py + 54;
         if (this.bunker) {
            Ui.textoN(g, this.font, "Tiempo para llegar al bunker", x, y, 15330549, 1.0F);
            Ui.texto(g, this.font, "Cuando acaba, la puerta se cierra y empieza el daño fuera.", x, y + 11, 9344683, 1.0F);
            Ui.textoN(g, this.font, "Tiempo con la puerta cerrada", x, y + 66, 15330549, 1.0F);
            Ui.texto(g, this.font, "Cuando acaba, la puerta vuelve a abrirse.", x, y + 77, 9344683, 1.0F);
            Ui.textoN(g, this.font, "¿En qué bunker?", x, y + 122, 15330549, 1.0F);
            Ui.texto(g, this.font, "por defecto " + MomentosPantalla.hhmm(this.porDefectoT()), x + 162, y + 28 + 8, 9344683, 1.0F);
            Ui.texto(g, this.font, "por defecto " + MomentosPantalla.hhmm(this.porDefectoC()), x + 162, y + 66 + 28 + 8, 9344683, 1.0F);
         } else {
            Ui.textoN(g, this.font, "Tiempo para llegar a su capa", x, y, 15330549, 1.0F);
            Ui.texto(g, this.font, "Cuando acaba, empieza el daño del gas.", x, y + 11, 9344683, 1.0F);
            Ui.texto(g, this.font, "por defecto " + MomentosPantalla.hhmm(this.porDefectoT()), x + 162, y + 28 + 8, 9344683, 1.0F);
         }

         Ui.texto(
            g, this.font, "Formato mm:ss o hh:mm:ss. Vacío = el valor por defecto (se cambia en Ajustes).", x, this.py + this.ph - 14 - 30 - 16, 9344683, 0.9F
         );
      }

      @Override
      public void onClose() {
         Minecraft.getInstance().setScreen(this.padreM);
      }
   }
}
