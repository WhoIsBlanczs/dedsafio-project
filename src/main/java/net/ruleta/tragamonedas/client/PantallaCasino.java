package net.ruleta.tragamonedas.client;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.ruleta.client.RedCliente;
import net.ruleta.client.ui.Ui;
import net.ruleta.client.ui.UiBoton;
import net.ruleta.client.ui.UiFila;
import net.ruleta.client.ui.UiLista;
import net.ruleta.client.ui.UiPantalla;
import net.ruleta.tragamonedas.CasinoGuardarPayload;
import net.ruleta.tragamonedas.ConfigTragamonedas;

public class PantallaCasino extends UiPantalla {
   private ConfigTragamonedas cfg;
   private boolean sucio;
   private int tab;
   private long avisoHasta;
   private final UiLista lista = new UiLista();
   private String mensaje = "";

   public PantallaCasino(Screen padre, ConfigTragamonedas cfg) {
      super("Casino", padre);
      this.cfg = cfg;
   }

   public void alRecibir(ConfigTragamonedas nueva) {
      this.cfg = nueva;
      this.sucio = false;
      this.avisoHasta = System.currentTimeMillis() + 2500L;
      this.reconstruir();
   }

   protected void init() {
      this.panel(780, 470);
      this.cabecera(true);
      this.subtitulo("Se guarda en el servidor: config/ruleta_tragamonedas.json");
      int yTabs = this.py + 48;
      UiBoton b0 = (UiBoton)this.addRenderableWidget(new UiBoton(this.px + 14, yTabs, 110, 24, "Rodillos", UiBoton.Estilo.FANTASMA, () -> this.cambiarTab(0)));
      UiBoton b1 = (UiBoton)this.addRenderableWidget(new UiBoton(this.px + 130, yTabs, 110, 24, "Premios", UiBoton.Estilo.FANTASMA, () -> this.cambiarTab(1)));
      UiBoton b2 = (UiBoton)this.addRenderableWidget(new UiBoton(this.px + 246, yTabs, 110, 24, "Cobro", UiBoton.Estilo.FANTASMA, () -> this.cambiarTab(2)));
      b0.setMarcado(this.tab == 0);
      b1.setMarcado(this.tab == 1);
      b2.setMarcado(this.tab == 2);
      if (this.tab == 0) {
         this.addRenderableWidget(
            new UiBoton(this.px + 382, yTabs, 150, 24, "+ Agregar item", UiBoton.Estilo.PRIMARIO, () -> this.elegirItem(this::agregarSimbolo))
               .ayuda("Abre la lista con todos los items (vanilla y de mods) para agregar uno a los rodillos")
         );
      } else if (this.tab == 1) {
         this.addRenderableWidget(
            new UiBoton(this.px + 382, yTabs, 130, 24, "+ Nuevo premio", UiBoton.Estilo.PRIMARIO, this::nuevoPremio)
               .ayuda("Crea un premio nuevo y abre su editor")
         );
      }

      int yPie = this.py + this.ph - 36;
      int xr = this.px + this.pw - 14;
      this.addRenderableWidget(
         new UiBoton(xr - 100, yPie, 100, 26, "Guardar", UiBoton.Estilo.PRIMARIO, this::guardar).ayuda("Guarda en el servidor y se aplica al instante")
      );
      this.addRenderableWidget(new UiBoton(xr - 100 - 8 - 130, yPie, 130, 26, "Valores originales", UiBoton.Estilo.PELIGRO, () -> {
         this.cfg = ConfigTragamonedas.copiaDefecto();
         this.sucio = true;
         this.reconstruir();
      }).ayuda("Vuelve a los símbolos y premios de fábrica (hay que guardar para aplicarlo)"));
      this.lista.x = this.px + 14;
      this.lista.y = yTabs + 34 + 34;
      this.lista.w = this.pw - 28;
      this.lista.h = yPie - 28 - this.lista.y;
      this.reconstruir();
   }

   private void cambiarTab(int t) {
      if (t != this.tab) {
         this.tab = t;
         this.rebuildWidgets();
      }
   }

   private void elegirItem(Consumer<String> alElegir) {
      Minecraft.getInstance().setScreen(new PantallaSelectorItem(this, alElegir));
   }

   private void guardar() {
      String json = this.cfg.aJsonCompacto();
      if (json.getBytes(StandardCharsets.UTF_8).length > 29000) {
         this.aviso("Demasiada configuración para enviar: quita premios o encantamientos");
      } else {
         RedCliente.enviar(new CasinoGuardarPayload(json));
      }
   }

   private void agregarSimbolo(String id) {
      if (!ConfigTragamonedas.itemExiste(id)) {
         this.aviso("Ese item no existe");
      } else if (this.cfg.simbolos.size() >= 8) {
         this.aviso("Máximo 8 símbolos (las caras del rodillo)");
      } else {
         for (ConfigTragamonedas.Simbolo s : this.cfg.simbolos) {
            if (s.item.equals(id)) {
               this.aviso("Ya está en la lista");
               return;
            }
         }

         this.cfg.simbolos.add(new ConfigTragamonedas.Simbolo(id, 10));
         this.sucio = true;
         this.reconstruir();
      }
   }

   private void nuevoPremio() {
      if (this.cfg.premios.size() >= 40) {
         this.aviso("Máximo 40 premios");
      } else {
         String a = this.cfg.simbolos.get(0).item;
         this.cfg.premios.add(new ConfigTragamonedas.Premio(a, a, a, new ConfigTragamonedas.Recompensa(a, 1)));
         this.sucio = true;
         this.reconstruir();
         this.editarPremio(this.cfg.premios.get(this.cfg.premios.size() - 1));
      }
   }

   private void editarPremio(ConfigTragamonedas.Premio p) {
      Minecraft.getInstance().setScreen(new PantallaPremio(this, this.cfg, p, () -> this.sucio = true));
   }

   private void aviso(String m) {
      this.mensaje = m;
      this.avisoHasta = System.currentTimeMillis() + 2500L;
   }

   private void reconstruir() {
      List<UiFila> filas = new ArrayList<>();
      if (this.tab == 0) {
         for (int i = 0; i < this.cfg.simbolos.size(); i++) {
            filas.add(new PantallaCasino.FilaSimbolo(i));
         }
      } else if (this.tab == 1) {
         for (int i = 0; i < this.cfg.premios.size(); i++) {
            filas.add(new PantallaCasino.FilaPremio(i));
         }
      } else {
         filas.add(new PantallaCasino.FilaPago());
      }

      this.lista.poner(filas, true);
   }

   private static ItemStack pila(String id) {
      ResourceLocation rl = ResourceLocation.tryParse(id);
      Item item = rl == null ? null : (Item)BuiltInRegistries.ITEM.getOptional(rl).orElse(null);
      return item == null ? ItemStack.EMPTY : new ItemStack(item);
   }

   private static String nombre(String id) {
      ItemStack p = pila(id);
      return p.isEmpty() ? id : p.getHoverName().getString();
   }

   private static int pesoTotal(ConfigTragamonedas cfg) {
      int t = 0;

      for (ConfigTragamonedas.Simbolo s : cfg.simbolos) {
         t += Math.max(0, s.peso);
      }

      return Math.max(1, t);
   }

   private static double prob(ConfigTragamonedas cfg, String id) {
      for (ConfigTragamonedas.Simbolo s : cfg.simbolos) {
         if (s.item.equals(id)) {
            return (double)s.peso / pesoTotal(cfg);
         }
      }

      return 0.0;
   }

   static double probabilidad(ConfigTragamonedas cfg, ConfigTragamonedas.Premio p) {
      if (!"iguales".equals(p.tipo)) {
         double r = 1.0;

         for (String c : p.combinacion) {
            if (!"*".equals(c)) {
               r *= prob(cfg, c);
            }
         }

         return r;
      } else {
         double total = 0.0;
         List<String> validos = new ArrayList<>(p.items);
         if (validos.isEmpty()) {
            for (ConfigTragamonedas.Simbolo s : cfg.simbolos) {
               validos.add(s.item);
            }
         }

         for (String it : validos) {
            double q = prob(cfg, it);
            total += p.iguales >= 3 ? q * q * q : 3.0 * q * q * (1.0 - q);
         }

         return Math.min(1.0, total);
      }
   }

   static String textoProb(double p) {
      if (p <= 0.0) {
         return "nunca (ese item ya no está en los rodillos)";
      }

      double uno = 1.0 / p;
      return p >= 0.1 ? String.format(Locale.ROOT, "%.1f %% de las tiradas", p * 100.0) : "1 de cada " + Math.round(uno) + " tiradas";
   }

   @Override
   protected void contenido(GuiGraphics g, int mx, int my, float pt) {
      Font f = this.font;
      this.lista.dibujar(g, f, mx, my);
      if (this.lista.tamano() == 0) {
         Ui.textoCentro(
            g,
            f,
            this.tab == 0 ? "No hay símbolos" : "No hay premios: pulsa «Nuevo premio»",
            this.lista.x + this.lista.w / 2,
            this.lista.y + 30,
            9344683,
            1.0F,
            false
         );
      }
      String ayuda = switch (this.tab) {
         case 0 -> "Estos son los items que giran en los rodillos. «Frecuencia» decide qué tan seguido sale cada uno: a más frecuencia, más veces aparece. El porcentaje de la derecha es lo que de verdad le toca. Máximo 8 items.";
         case 1 -> "Un premio es «si sale esto, ganas esto». Se revisan de arriba hacia abajo y gana el primero que coincida con la fila del centro de los rodillos. Pulsa «Editar premio» para cambiar la condición y lo que se gana.";
         default -> "Lo que cuesta jugar: el item (y cuántos) que tienes que llevar en la mano al hacer clic derecho en la máquina.";
      };
      int yAyuda = this.py + 48 + 34;

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

   private static void cuadro(GuiGraphics g, int x, int y, int w, int h, boolean hover, int color) {
      Ui.cajaBorde(g, x, y, w, h, 4, Ui.c(hover ? 2369846 : 1119259), Ui.c(hover ? color : 3028037));
   }

   private final class FilaPago extends FilaZonas {
      FilaPago() {
         this.alto = 70;
      }

      @Override
      public void dibujar(GuiGraphics g, Font f, int x, int y, int w, int mx, int my, boolean hover) {
         this.empezar(x, y);
         PantallaCasino pc = PantallaCasino.this;
         Ui.cajaBorde(g, x, y, w, this.alto, 6, Ui.c(1711656, 0.95F), Ui.c(3028037));
         this.texto(g, f, "Item que se cobra en cada tirada", 12, 8, 15330549);
         this.cuadroItem(g, 12, 24, 36, 36, PantallaCasino.pila(pc.cfg.pago_item), mx, my, 7113983, b -> pc.elegirItem(id -> {
            pc.cfg.pago_item = id;
            pc.sucio = true;
         }));
         Ui.textoN(g, f, Ui.recortar(f, PantallaCasino.nombre(pc.cfg.pago_item), 260), x + 58, y + 28, 15330549, 1.0F);
         Ui.texto(g, f, Ui.recortar(f, pc.cfg.pago_item + "  (clic en el cuadro para cambiar el item)", 330), x + 58, y + 41, 9344683, 1.0F);
         this.texto(g, f, "Cuántos", 440, 36, 9344683);
         this.paso(g, f, 494, 33, String.valueOf(pc.cfg.pago_cantidad), mx, my, d -> {
            pc.cfg.pago_cantidad = Mth.clamp(pc.cfg.pago_cantidad + d, 1, 64);
            pc.sucio = true;
         });
      }
   }

   private final class FilaPremio extends FilaZonas {
      private static final int CASILLA = 30;
      private final int i;

      FilaPremio(int i) {
         this.i = i;
         this.alto = 82;
      }

      private ConfigTragamonedas.Premio p() {
         return PantallaCasino.this.cfg.premios.get(this.i);
      }

      @Override
      public void dibujar(GuiGraphics g, Font f, int x, int y, int w, int mx, int my, boolean hover) {
         this.empezar(x, y);
         PantallaCasino pc = PantallaCasino.this;
         ConfigTragamonedas.Premio p = this.p();
         Ui.cajaBorde(g, x, y, w, this.alto, 6, Ui.c(1711656, 0.95F), Ui.c(3028037));
         int yc = 22;
         int rx = 12;
         Ui.texto(g, f, "SI SALE", x + rx, y + 8, 9344683, 0.9F);
         if ("iguales".equals(p.tipo)) {
            String t = p.iguales >= 3 ? "3 iguales" : "2 iguales";
            Ui.textoN(g, f, t, x + rx, y + yc + 4, 16762967, 1.0F);
            int ancho = Ui.anchoN(f, t) + 8;
            if (p.items.isEmpty()) {
               Ui.texto(g, f, "(de cualquier item)", x + rx + ancho, y + yc + 4, 9344683, 1.0F);
               rx += ancho + 100;
            } else {
               Ui.texto(g, f, "de:", x + rx + ancho, y + yc + 4, 9344683, 1.0F);
               int k = 0;

               for (String id : p.items) {
                  g.renderItem(PantallaCasino.pila(id), x + rx + ancho + 20 + k * 18, y + yc + 7);
                  k++;
               }

               rx += ancho + 20 + p.items.size() * 18 + 6;
            }
         } else {
            for (int k = 0; k < 3; k++) {
               int cx = x + rx + k * 36;
               Ui.cajaBorde(g, cx, y + yc, 30, 30, 4, Ui.c(1119259), Ui.c(3028037));
               String c = p.combinacion.get(k);
               if ("*".equals(c)) {
                  Ui.textoCentro(g, f, "*", cx + 15, y + yc + 11, 16762967, 1.0F, true);
               } else {
                  g.renderItem(PantallaCasino.pila(c), cx + 7, y + yc + 7);
               }
            }

            rx += 108;
         }

         Ui.textoCentro(g, f, "→", x + rx + 8, y + yc + 11, 9344683, 1.0F, true);
         rx += 26;
         boolean una = "una".equals(p.modo);
         Ui.texto(g, f, una ? "GANAS SOLO UNO DE ESTOS" : "GANAS", x + rx, y + 8, 9344683, 0.9F);

         for (ConfigTragamonedas.Recompensa r : p.recompensas) {
            int max = Math.max(r.cantidad, r.maximo);
            String cant = "×" + (max > r.cantidad ? r.cantidad + "-" + max : String.valueOf(r.cantidad));
            if (r.esLibro()) {
               cant = cant + (r.encantamientos.isEmpty() ? " cualquier enc." : " " + r.encantamientos.size() + " enc.");
            }

            int ancho = 22 + Ui.ancho(f, cant) + 8;
            Ui.cajaBorde(g, x + rx, y + yc, ancho, 30, 4, Ui.c(1119259), Ui.c(4906624, 0.6F));
            g.renderItem(PantallaCasino.pila(r.item), x + rx + 4, y + yc + 7);
            Ui.texto(g, f, cant, x + rx + 24, y + yc + 11, 15330549, 1.0F);
            rx += ancho + 4;
         }

         this.boton(g, f, w - 30 - 4 - 100, 8, 100, 22, "Editar premio", mx, my, 7113983, false, b -> pc.editarPremio(p));
         this.boton(g, f, w - 30, 8, 22, 22, "×", mx, my, 16735340, false, b -> {
            pc.cfg.premios.remove(this.i);
            pc.sucio = true;
            pc.reconstruir();
         });
         Ui.texto(g, f, "Probabilidad de ganarlo: " + PantallaCasino.textoProb(PantallaCasino.probabilidad(pc.cfg, p)), x + 12, y + 62, 7113983, 0.95F);
      }
   }

   private final class FilaSimbolo extends UiFila {
      private final int i;

      FilaSimbolo(int i) {
         this.i = i;
         this.alto = 40;
      }

      private ConfigTragamonedas.Simbolo s() {
         return PantallaCasino.this.cfg.simbolos.get(this.i);
      }

      private int xDel(int x, int w) {
         return x + w - 30;
      }

      private int xMas(int x, int w) {
         return this.xDel(x, w) - 30;
      }

      private int xMenos(int x, int w) {
         return this.xMas(x, w) - 4 - 54 - 4 - 22;
      }

      @Override
      public void dibujar(GuiGraphics g, Font f, int x, int y, int w, int mx, int my, boolean hover) {
         ConfigTragamonedas.Simbolo s = this.s();
         Ui.cajaBorde(g, x, y, w, this.alto, 6, Ui.c(1711656, 0.95F), Ui.c(3028037));
         g.renderItem(PantallaCasino.pila(s.item), x + 12, y + 12);
         Ui.textoN(g, f, Ui.recortar(f, PantallaCasino.nombre(s.item), 220), x + 38, y + 9, 15330549, 1.0F);
         Ui.texto(g, f, Ui.recortar(f, s.item, 220), x + 38, y + 21, 9344683, 1.0F);
         double p = PantallaCasino.prob(PantallaCasino.this.cfg, s.item);
         Ui.texto(g, f, "Sale en " + String.format(Locale.ROOT, "%.1f", p * 100.0) + " % de los giros de cada rodillo", x + 260, y + 15, 7113983, 1.0F);
         Ui.texto(g, f, "Frecuencia", this.xMenos(x, w) - 56, y + 16, 9344683, 1.0F);
         int yb = y + 9;
         boolean hm = Ui.dentro(mx, my, this.xMenos(x, w), yb, 22, 22);
         boolean hp = Ui.dentro(mx, my, this.xMas(x, w), yb, 22, 22);
         boolean hd = Ui.dentro(mx, my, this.xDel(x, w), yb, 22, 22);
         PantallaCasino.cuadro(g, this.xMenos(x, w), yb, 22, 22, hm, 7113983);
         PantallaCasino.cuadro(g, this.xMas(x, w), yb, 22, 22, hp, 7113983);
         PantallaCasino.cuadro(g, this.xDel(x, w), yb, 22, 22, hd, 16735340);
         Ui.textoCentro(g, f, "−", this.xMenos(x, w) + 11, yb + 7, 15330549, 1.0F, false);
         Ui.textoCentro(g, f, "+", this.xMas(x, w) + 11, yb + 7, 15330549, 1.0F, false);
         Ui.textoCentro(g, f, "×", this.xDel(x, w) + 11, yb + 7, 16735340, 1.0F, false);
         Ui.textoCentro(g, f, String.valueOf(s.peso), this.xMenos(x, w) + 22 + 4 + 27, yb + 7, 15330549, 1.0F, false);
      }

      @Override
      public boolean clic(double mx, double my, int boton, int x, int y, int w) {
         int yb = y + 9;
         int paso = Screen.hasShiftDown() ? 10 : 1;
         PantallaCasino p = PantallaCasino.this;
         if (Ui.dentro(mx, my, this.xMenos(x, w), yb, 22, 22)) {
            this.s().peso = Mth.clamp(this.s().peso - paso, 1, 1000);
            p.sucio = true;
            return true;
         }

         if (Ui.dentro(mx, my, this.xMas(x, w), yb, 22, 22)) {
            this.s().peso = Mth.clamp(this.s().peso + paso, 1, 1000);
            p.sucio = true;
            return true;
         }

         if (!Ui.dentro(mx, my, this.xDel(x, w), yb, 22, 22)) {
            return false;
         }

         if (p.cfg.simbolos.size() <= 1) {
            p.aviso("Tiene que quedar al menos un símbolo");
            return true;
         }

         String id = this.s().item;
         p.cfg.simbolos.remove(this.i);

         for (ConfigTragamonedas.Premio pr : p.cfg.premios) {
            for (int k = 0; k < pr.combinacion.size(); k++) {
               if (pr.combinacion.get(k).equals(id)) {
                  pr.combinacion.set(k, "*");
               }
            }
         }

         p.sucio = true;
         p.reconstruir();
         return true;
      }
   }
}
