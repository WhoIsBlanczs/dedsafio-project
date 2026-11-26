package net.ruleta.client;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.ruleta.Cambios;
import net.ruleta.DesactivarPayload;
import net.ruleta.client.ui.Ui;
import net.ruleta.client.ui.UiBoton;

public class PantallaCambiosActivos extends PantallaTarjetas {
   private UiBoton desactivar;
   private String elegido;

   public PantallaCambiosActivos() {
      super("Cambios activos", null);
   }

   protected void init() {
      this.panel(900, 540);
      this.cabecera(true);
      this.subtitulo("Elige uno y pulsa Desactivar para quitarlo");
      this.zona(44);
      int y = this.py + this.ph - 14 - 30;
      this.desactivar = (UiBoton)this.addRenderableWidget(new UiBoton(this.px + this.pw - 16 - 150, y, 150, 30, "Desactivar", UiBoton.Estilo.PELIGRO, () -> {
         if (this.elegido != null) {
            RedCliente.enviar(new DesactivarPayload(this.elegido));
            this.elegido = null;
         }
      }));
   }

   @Override
   protected int colorElegida() {
      return 16735340;
   }

   @Override
   protected String clave() {
      return String.join(",", CambiosCliente.activos());
   }

   @Override
   protected List<PantallaTarjetas.Carta> cartas() {
      List<PantallaTarjetas.Carta> r = new ArrayList<>();

      for (List<Cambios.Entrada> pagina : Cambios.PAGINAS) {
         for (Cambios.Entrada e : pagina) {
            if (CambiosCliente.activo(e.id())) {
               r.add(new PantallaTarjetas.Carta(e.id(), e.titulo(), e.texto()));
            }
         }
      }

      for (List<Cambios.Entrada> pagina : Cambios.PAGINAS_MOBS) {
         for (Cambios.Entrada ex : pagina) {
            if (CambiosCliente.activo(ex.id())) {
               r.add(new PantallaTarjetas.Carta(ex.id(), ex.titulo(), ex.texto()));
            }
         }
      }

      for (String id : CambiosCliente.activos()) {
         if (Cambios.porId(id) == null) {
            r.add(new PantallaTarjetas.Carta(id, id, ""));
         }
      }

      return r;
   }

   @Override
   protected PantallaTarjetas.Estado estado(PantallaTarjetas.Carta c) {
      return c.id().equals(this.elegido) ? PantallaTarjetas.Estado.ELEGIDA : PantallaTarjetas.Estado.NORMAL;
   }

   @Override
   protected String etiqueta(PantallaTarjetas.Carta c) {
      return c.id().equals(this.elegido) ? "SELECCIONADO" : "ACTIVO";
   }

   @Override
   protected void pulsar(PantallaTarjetas.Carta c) {
      this.elegido = c.id().equals(this.elegido) ? null : c.id();
   }

   @Override
   protected void alDibujar() {
      if (this.elegido != null && !CambiosCliente.activo(this.elegido)) {
         this.elegido = null;
      }

      this.desactivar.visible = this.elegido != null;
   }

   @Override
   protected void encima(GuiGraphics g, int mx, int my, float pt) {
      if (this.cartas().isEmpty()) {
         Ui.textoCentro(g, this.font, "No hay cambios de dificultad activos.", this.px + this.pw / 2, this.py + this.ph / 2 - 4, 9344683, 1.0F, false);
      }
   }
}
