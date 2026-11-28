package net.ruleta.client;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.ruleta.Ajustes;
import net.ruleta.MomentoPayload;
import net.ruleta.Momentos;
import net.ruleta.Tiempo;
import net.ruleta.client.ui.Ui;
import net.ruleta.client.ui.UiBoton;
import net.ruleta.client.ui.UiCampo;
import net.ruleta.client.ui.UiPantalla;

public class PantallaEclipse extends UiPantalla {
   private final UiCampo[] cajas = new UiCampo[5];
   private final String[] guardado = new String[5];
   private final String[] mensajes = new String[]{"", "", "", ""};
   private UiBoton aceptar;
   private UiBoton botonBunkers;
   private String bunker = "";
   private int yFila4;

   public PantallaEclipse(Screen padre) {
      super("Eclipse", padre);
   }

   private static int porDefecto(int i) {
      return switch (i) {
         case 0 -> Ajustes.entero("eclipse.espera1");
         case 1 -> Ajustes.entero("eclipse.espera2");
         case 2 -> Ajustes.entero("eclipse.espera3");
         case 3 -> Ajustes.entero("bunker.contador");
         default -> Ajustes.entero("bunker.cerrado");
      };
   }

   private UiCampo caja(int i, int x, int y, int w) {
      UiCampo e = new UiCampo(this.font, x, y, w, 24, Component.literal("Tiempo"));
      e.setMaxLength(8);
      e.setFilter(t -> t.matches("[0-9:]*"));
      e.setValue(this.guardado[i] != null ? this.guardado[i] : Ui.mmss(porDefecto(i)));
      e.malo(() -> {
         String v = e.getValue().trim();
         return !v.isEmpty() && !LanzadorFormulario.tiempoValido(v);
      });
      return (UiCampo)this.addRenderableWidget(e);
   }

   protected void init() {
      for (int i = 0; i < 5; i++) {
         if (this.cajas[i] != null) {
            this.guardado[i] = this.cajas[i].getValue();
         }
      }

      this.panel(780, 450);
      this.cabecera(true);
      this.subtitulo("4 ruletas rojas seguidas · el tiempo entre ellas solo lo ves tú · «Editar» cambia cada mensaje");
      int x = this.px + 16;
      int w = this.pw - 32;
      int y = this.py + 54;
      int colX = x + w - 14 - 190;

      for (int i = 0; i < 3; i++) {
         this.cajas[i] = this.caja(i, colX + 70, y + 19, 120);
         y += 70;
      }

      int yE = this.py + 54;

      for (int i = 0; i < 4; i++) {
         int n = i;
         this.addRenderableWidget(
            new UiBoton(
               colX - 20 - 70,
               yE + 6,
               70,
               18,
               "Editar",
               UiBoton.Estilo.NORMAL,
               () -> Minecraft.getInstance().setScreen(new PantallaMensajeEclipse(this, n, this.mensajes[n], t -> this.mensajes[n] = t))
            )
         );
         yE += (i < 3 ? 62 : 90) + 8;
      }

      this.yFila4 = y;
      this.botonBunkers = (UiBoton)this.addRenderableWidget(
         new UiBoton(
            colX,
            y + 10,
            190,
            24,
            PantallaBunkers.texto(this.bunker),
            UiBoton.Estilo.NORMAL,
            () -> Minecraft.getInstance().setScreen(new PantallaBunkers(this, this.bunker, k -> this.bunker = k))
         )
      );
      this.cajas[3] = this.caja(3, colX, y + 54, 92);
      this.cajas[4] = this.caja(4, colX + 98, y + 54, 92);
      int yb = this.py + this.ph - 14 - 30;
      this.aceptar = (UiBoton)this.addRenderableWidget(
         new UiBoton(this.px + this.pw - 24 - 130, yb, 130, 30, "Activar", UiBoton.Estilo.PRIMARIO, this::activar)
      );
      this.addRenderableWidget(new UiBoton(this.px + this.pw - 24 - 130 - 10 - 100, yb, 100, 30, "Cancelar", UiBoton.Estilo.NORMAL, this::onClose));
   }

   private boolean valido() {
      for (UiCampo e : this.cajas) {
         String v = e.getValue().trim();
         if (!v.isEmpty() && !LanzadorFormulario.tiempoValido(v)) {
            return false;
         }
      }

      return true;
   }

   private int segundos(int i) {
      String v = this.cajas[i].getValue().trim();
      return v.isEmpty() ? porDefecto(i) : Tiempo.leer(v);
   }

   private void activar() {
      if (this.valido()) {
         String extra = this.segundos(0)
            + ";"
            + this.segundos(1)
            + ";"
            + this.segundos(2)
            + ";"
            + this.segundos(3)
            + ";"
            + this.segundos(4)
            + ";"
            + this.bunker;
         if (!this.mensajes[0].isEmpty() || !this.mensajes[1].isEmpty() || !this.mensajes[2].isEmpty() || !this.mensajes[3].isEmpty()) {
            for (String m : this.mensajes) {
               extra = extra + "\u001f" + m.replace("\u001f", " ");
            }
         }

         RedCliente.enviar(new MomentoPayload("eclipse", true, 0, 0, extra));
         this.onClose();
      }
   }

   @Override
   protected void contenido(GuiGraphics g, int mx, int my, float pt) {
      this.aceptar.active = this.valido();
      this.botonBunkers.setTexto(Ui.recortar(this.font, PantallaBunkers.texto(this.bunker), 180));
      int x = this.px + 16;
      int w = this.pw - 32;
      int y = this.py + 54;
      int colX = x + w - 14 - 190;

      for (int i = 0; i < 4; i++) {
         int alto = i < 3 ? 62 : 90;
         Ui.cajaBorde(g, x, y, w, alto, 8, Ui.c(1711656, 0.95F), Ui.c(3028037));
         Ui.caja(g, x + 14, y + 14, 28, 28, 14, Ui.c(i == 3 ? 16735340 : 7113983, 0.2F));
         Ui.textoCentro(g, this.font, String.valueOf(i + 1), x + 28, y + 24, i == 3 ? 16735340 : 7113983, 1.0F, true);
         if (i < 3) {
            Ui.caja(g, x + 27, y + 44, 2, alto - 44 + 8 + 12, 0, Ui.c(3028037, 0.9F));
         }

         boolean editado = !this.mensajes[i].isEmpty();
         String titulo = Momentos.ECLIPSE_TITULOS[i].replace("Momento ReviiL — ", "") + (editado ? " · editado" : "");
         Ui.textoN(g, this.font, titulo, x + 54, y + 12, 16762967, 1.0F);
         String texto = (editado ? this.mensajes[i] : Momentos.ECLIPSE_TEXTOS[i]).replace("{bunker}", "X: ? // Z: ? (las del bunker)");
         List<String> ls = Ui.lineas(this.font, texto, colX - 20 - (x + 54), i < 3 ? 3 : 6);

         for (int k = 0; k < ls.size(); k++) {
            Ui.texto(g, this.font, ls.get(k), x + 54, y + 26 + k * 10, 15330549, 0.92F);
         }

         if (i < 3) {
            Ui.texto(g, this.font, "Siguiente en", colX, y + 27, 9344683, 1.0F);
         }

         y += alto + 8;
      }

      Ui.texto(g, this.font, "Contador", this.cajas[3].xExterior(), this.yFila4 + 42, 9344683, 1.0F);
      Ui.texto(g, this.font, "Cerrado", this.cajas[4].xExterior(), this.yFila4 + 42, 9344683, 1.0F);
      Ui.texto(g, this.font, "mm:ss o hh:mm:ss · vacío = valor por defecto (Ajustes)", this.px + 24, this.py + this.ph - 14 - 20, 9344683, 0.9F);
   }
}
