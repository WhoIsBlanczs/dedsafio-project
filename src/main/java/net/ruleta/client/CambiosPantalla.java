package net.ruleta.client;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.ruleta.Cambios;
import net.ruleta.client.ui.Ui;
import net.ruleta.client.ui.UiBoton;

public class CambiosPantalla extends PantallaTarjetas {
   private final LanzadorFormulario padreForm;
   private UiBoton anterior;
   private UiBoton siguiente;
   private UiBoton botonRuleta;
   private final List<List<Cambios.Entrada>> paginas;
   private final String rotulo;

   public CambiosPantalla(LanzadorFormulario padre) {
      this(padre, Cambios.PAGINAS, "Cambios de dificultad");
   }

   public CambiosPantalla(LanzadorFormulario padre, List<List<Cambios.Entrada>> paginas, String encabezado) {
      super(encabezado.charAt(0) + encabezado.substring(1).toLowerCase(), padre);
      this.padreForm = padre;
      this.paginas = paginas;
      this.rotulo = encabezado.toUpperCase();
   }

   protected void init() {
      this.panel(900, 540);
      this.cabecera(true);
      this.subtitulo("Elige uno · los marcados como ACTIVO ya están en marcha");
      this.padreForm.elegidos.removeIf(CambiosCliente::activo);
      this.padreForm.paginaCambios = Math.max(0, Math.min(this.padreForm.paginaCambios, this.paginas.size() - 1));
      this.zona(44);
      int y = this.py + this.ph - 14 - 30;
      this.anterior = (UiBoton)this.addRenderableWidget(
         new UiBoton(this.px + this.pw / 2 - 90, y, 40, 30, "‹", UiBoton.Estilo.NORMAL, () -> this.cambiarPagina(-1))
      );
      this.siguiente = (UiBoton)this.addRenderableWidget(
         new UiBoton(this.px + this.pw / 2 + 50, y, 40, 30, "›", UiBoton.Estilo.NORMAL, () -> this.cambiarPagina(1))
      );
      this.addRenderableWidget(new UiBoton(this.px + this.pw - 16 - 110, y, 110, 30, "Listo", UiBoton.Estilo.PRIMARIO, this::onClose));
      this.botonRuleta = (UiBoton)this.addRenderableWidget(
         new UiBoton(this.px + 16, y, 150, 30, this.textoRuleta(), UiBoton.Estilo.NORMAL, () -> this.padreForm.conRuleta = !this.padreForm.conRuleta)
      );
      this.reiniciarScroll();
   }

   private String textoRuleta() {
      return this.padreForm.conRuleta ? "RULETA: SÍ" : "RULETA: NO";
   }

   @Override
   protected int altoEncabezado() {
      return 44;
   }

   @Override
   protected void encabezado(GuiGraphics g) {
      String t = this.rotulo;
      float escala = Math.min(2.6F, (float)(this.pw - 80) / Math.max(1, Ui.anchoN(this.font, t)));
      int w = Math.round(Ui.anchoN(this.font, t) * escala);
      Ui.titulo3D(g, this.font, t, this.px + (this.pw - w) / 2 - 2, this.py + 50, escala, 16747028, 16761963, 11553280, 2757120);
   }

   private void cambiarPagina(int delta) {
      int nueva = Math.max(0, Math.min(this.padreForm.paginaCambios + delta, this.paginas.size() - 1));
      if (nueva != this.padreForm.paginaCambios) {
         this.padreForm.paginaCambios = nueva;
         this.reiniciarScroll();
      }
   }

   @Override
   protected String clave() {
      return "p" + this.padreForm.paginaCambios;
   }

   @Override
   protected List<PantallaTarjetas.Carta> cartas() {
      List<PantallaTarjetas.Carta> r = new ArrayList<>();

      for (Cambios.Entrada e : this.paginas.get(Math.min(this.padreForm.paginaCambios, this.paginas.size() - 1))) {
         r.add(new PantallaTarjetas.Carta(e.id(), e.titulo(), e.texto()));
      }

      return r;
   }

   @Override
   protected PantallaTarjetas.Estado estado(PantallaTarjetas.Carta c) {
      if (CambiosCliente.activo(c.id())) {
         return PantallaTarjetas.Estado.ACTIVA;
      } else {
         return this.padreForm.elegidos.contains(c.id()) ? PantallaTarjetas.Estado.ELEGIDA : PantallaTarjetas.Estado.NORMAL;
      }
   }

   @Override
   protected String etiqueta(PantallaTarjetas.Carta c) {
      return switch (this.estado(c)) {
         case ACTIVA -> "ACTIVO";
         case ELEGIDA -> "ELEGIDO";
         default -> null;
      };
   }

   @Override
   protected void pulsar(PantallaTarjetas.Carta c) {
      boolean estabaElegido = this.padreForm.elegidos.contains(c.id());
      this.padreForm.elegidos.clear();
      if (!estabaElegido) {
         this.padreForm.elegidos.add(c.id());
      }
   }

   @Override
   protected void alDibujar() {
      this.padreForm.elegidos.removeIf(CambiosCliente::activo);
      this.botonRuleta.setTexto(this.textoRuleta());
      this.anterior.active = this.padreForm.paginaCambios > 0;
      this.siguiente.active = this.padreForm.paginaCambios < this.paginas.size() - 1;
   }

   @Override
   protected void encima(GuiGraphics g, int mx, int my, float pt) {
      Ui.textoCentro(
         g,
         this.font,
         "Página " + (this.padreForm.paginaCambios + 1) + " de " + this.paginas.size(),
         this.px + this.pw / 2,
         this.py + this.ph - 14 - 19,
         15330549,
         1.0F,
         false
      );
      Ui.texto(
         g,
         this.font,
         this.padreForm.conRuleta ? "Gira la ruleta y luego se activa" : "Sin ruletafanproject: solo anuncio y cambio",
         this.px + 16,
         this.py + this.ph - 14 - 40,
         9344683,
         0.9F
      );
   }
}
