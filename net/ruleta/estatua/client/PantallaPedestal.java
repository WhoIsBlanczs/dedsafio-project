package net.ruleta.estatua.client;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.ruleta.client.RedCliente;
import net.ruleta.client.ui.Ui;
import net.ruleta.client.ui.UiBoton;
import net.ruleta.client.ui.UiFila;
import net.ruleta.client.ui.UiLista;
import net.ruleta.client.ui.UiPantalla;
import net.ruleta.estatua.ConfigPedestal;
import net.ruleta.estatua.PedestalGuardarPayload;
import net.ruleta.tragamonedas.ConfigTragamonedas;
import net.ruleta.tragamonedas.client.PantallaSelectorItem;

public class PantallaPedestal extends UiPantalla {
   private ConfigPedestal cfg;
   private boolean sucio;
   private long avisoHasta;
   private String mensaje = "";
   private final UiLista lista = new UiLista();

   public PantallaPedestal(Screen padre, ConfigPedestal cfg) {
      super("Nutria Pedestal", padre);
      this.cfg = cfg;
   }

   public void alRecibir(ConfigPedestal nueva) {
      this.cfg = nueva;
      this.sucio = false;
      this.mensaje = "";
      this.avisoHasta = System.currentTimeMillis() + 2500L;
      this.reconstruir();
   }

   protected void init() {
      this.panel(780, 470);
      this.cabecera(true);
      this.subtitulo("Se guarda en el servidor: config/ruleta_pedestal.json");
      int yTop = this.py + 48;
      this.addRenderableWidget(
         new UiBoton(this.px + 14, yTop, 170, 24, "+ Nuevo comercio", UiBoton.Estilo.PRIMARIO, this::nuevoComercio)
            .ayuda("Agrega un comercio nuevo al pedestal (elige qué item das y cuál recibes)")
      );
      int yPie = this.py + this.ph - 36;
      int xr = this.px + this.pw - 14;
      this.addRenderableWidget(
         new UiBoton(xr - 100, yPie, 100, 26, "Guardar", UiBoton.Estilo.PRIMARIO, this::guardar).ayuda("Guarda en el servidor y se aplica al instante")
      );
      this.addRenderableWidget(new UiBoton(xr - 100 - 8 - 130, yPie, 130, 26, "Valores originales", UiBoton.Estilo.PELIGRO, () -> {
         this.cfg = ConfigPedestal.copiaDefecto();
         this.sucio = true;
         this.reconstruir();
      }).ayuda("Vuelve al comercio de fábrica: 2 deditas azules = 1 dedita naranja y 2 deditas azules = 1 Dilitio (hay que guardar para aplicarlo)"));
      this.lista.x = this.px + 14;
      this.lista.y = yTop + 34 + 24;
      this.lista.w = this.pw - 28;
      this.lista.h = yPie - 28 - this.lista.y;
      this.reconstruir();
   }

   private void reconstruir() {
      List<UiFila> filas = new ArrayList<>();

      for (int i = 0; i < this.cfg.tratos.size(); i++) {
         filas.add(new PantallaPedestal.FilaTrato(i));
      }

      this.lista.poner(filas, true);
   }

   private void aviso(String m) {
      this.mensaje = m;
      this.avisoHasta = System.currentTimeMillis() + 2500L;
   }

   private void elegirItem(Consumer<String> alElegir) {
      Minecraft.getInstance().setScreen(new PantallaSelectorItem(this, alElegir));
   }

   private void guardar() {
      String json = this.cfg.aJsonCompacto();
      if (json.getBytes(StandardCharsets.UTF_8).length > 29000) {
         this.aviso("Demasiados comercios para enviar: quita alguno");
      } else {
         RedCliente.enviar(new PedestalGuardarPayload(json));
      }
   }

   private void nuevoComercio() {
      if (this.cfg.tratos.size() >= 40) {
         this.aviso("Máximo 40 comercios");
      } else {
         this.elegirItem(da -> Minecraft.getInstance().setScreen(new PantallaSelectorItem(this, recibe -> {
            if (ConfigTragamonedas.itemExiste(da) && ConfigTragamonedas.itemExiste(recibe)) {
               this.cfg.tratos.add(new ConfigPedestal.Trato(da, 1, recibe, 1));
               this.sucio = true;
               this.reconstruir();
            } else {
               this.aviso("Ese item no existe");
            }
         })));
      }
   }

   private static ItemStack pila(String id) {
      Item item = ConfigPedestal.item(id);
      return item == null ? ItemStack.EMPTY : new ItemStack(item);
   }

   private static String nombre(String id) {
      ItemStack p = pila(id);
      return p.isEmpty() ? id : p.getHoverName().getString();
   }

   private static int tope(String id) {
      Item item = ConfigPedestal.item(id);
      return item == null ? 64 : Math.max(1, item.getDefaultMaxStackSize());
   }

   @Override
   protected void contenido(GuiGraphics g, int mx, int my, float pt) {
      Font f = this.font;
      this.lista.dibujar(g, f, mx, my);
      if (this.lista.tamano() == 0) {
         Ui.textoCentro(g, f, "No hay comercios: pulsa «Nuevo comercio»", this.lista.x + this.lista.w / 2, this.lista.y + 30, 9344683, 1.0F, false);
      }

      int yAyuda = this.py + 48 + 34;
      String ayuda = "Cada comercio es «das esto y recibes esto». Clic en un cuadro de item para cambiarlo; con − y + cambias la cantidad (Mayús = de 10 en 10). El jugador ve estos comercios al hacer clic derecho en el pedestal.";

      for (String linea : Ui.lineas(f, ayuda, this.pw - 28, 2)) {
         Ui.texto(g, f, linea, this.px + 14, yAyuda, 15330549, 0.95F);
         yAyuda += 11;
      }

      if (this.sucio) {
         Ui.texto(g, f, "● Hay cambios sin guardar", this.px + 14, this.lista.y + this.lista.h + 16, 16762967, 1.0F);
      } else if (System.currentTimeMillis() < this.avisoHasta && this.mensaje.isEmpty()) {
         Ui.texto(g, f, "✔ Guardado", this.px + 14, this.lista.y + this.lista.h + 16, 4906624, 1.0F);
      }

      if (System.currentTimeMillis() < this.avisoHasta && !this.mensaje.isEmpty()) {
         Ui.texto(g, f, this.mensaje, this.px + 200, this.lista.y + this.lista.h + 16, 16735340, 1.0F);
      } else if (System.currentTimeMillis() >= this.avisoHasta) {
         this.mensaje = "";
      }
   }

   public boolean mouseClicked(double mx, double my, int boton) {
      return super.mouseClicked(mx, my, boton) ? true : this.lista.mouseClicked(mx, my, boton);
   }

   public boolean mouseReleased(double mx, double my, int boton) {
      return this.lista.mouseReleased() || super.mouseReleased(mx, my, boton);
   }

   public boolean mouseScrolled(double mx, double my, double dx, double dy) {
      return this.lista.mouseScrolled(mx, my, dx, dy) || super.mouseScrolled(mx, my, dx, dy);
   }

   private final class FilaTrato extends FilaZonasPedestal {
      private final int i;

      FilaTrato(int i) {
         this.i = i;
         this.alto = 68;
      }

      @Override
      public void dibujar(GuiGraphics g, Font f, int x, int y, int w, int mx, int my, boolean hover) {
         this.empezar(x, y);
         PantallaPedestal pp = PantallaPedestal.this;
         ConfigPedestal.Trato t = pp.cfg.tratos.get(this.i);
         Ui.cajaBorde(g, x, y, w, this.alto, 6, Ui.c(1711656, 0.95F), Ui.c(3028037));
         this.texto(g, f, "DAS", 12, 6, 9344683);
         this.cuadroItem(g, 12, 22, 36, 36, PantallaPedestal.pila(t.da_item), mx, my, 7113983, b -> pp.elegirItem(id -> {
            t.da_item = id;
            t.da_cantidad = Math.min(t.da_cantidad, PantallaPedestal.tope(id));
            pp.sucio = true;
         }));
         this.texto(g, f, Ui.recortar(f, PantallaPedestal.nombre(t.da_item), 190), 58, 24, 15330549);
         this.paso(g, f, 58, 38, "×" + t.da_cantidad, mx, my, d -> {
            t.da_cantidad = Math.max(1, Math.min(PantallaPedestal.tope(t.da_item), t.da_cantidad + d));
            pp.sucio = true;
         });
         Ui.textoCentro(g, f, "→", x + 284, y + 30, 9344683, 1.0F, true);
         this.texto(g, f, "RECIBES", 320, 6, 9344683);
         this.cuadroItem(g, 320, 22, 36, 36, PantallaPedestal.pila(t.recibe_item), mx, my, 4906624, b -> pp.elegirItem(id -> {
            t.recibe_item = id;
            t.recibe_cantidad = Math.min(t.recibe_cantidad, PantallaPedestal.tope(id));
            pp.sucio = true;
         }));
         this.texto(g, f, Ui.recortar(f, PantallaPedestal.nombre(t.recibe_item), 190), 366, 24, 15330549);
         this.paso(g, f, 366, 38, "×" + t.recibe_cantidad, mx, my, d -> {
            t.recibe_cantidad = Math.max(1, Math.min(PantallaPedestal.tope(t.recibe_item), t.recibe_cantidad + d));
            pp.sucio = true;
         });
         this.boton(g, f, w - 30, 8, 22, 22, "×", mx, my, 16735340, false, b -> {
            pp.cfg.tratos.remove(this.i);
            pp.sucio = true;
            pp.reconstruir();
         });
      }
   }
}
