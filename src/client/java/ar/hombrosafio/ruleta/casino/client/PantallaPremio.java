package ar.hombrosafio.ruleta.casino.client;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import ar.hombrosafio.ruleta.client.ui.Ui;
import ar.hombrosafio.ruleta.client.ui.UiBoton;
import ar.hombrosafio.ruleta.client.ui.UiFila;
import ar.hombrosafio.ruleta.client.ui.UiLista;
import ar.hombrosafio.ruleta.client.ui.UiPantalla;
import ar.hombrosafio.ruleta.casino.ConfigTragamonedas;

public class PantallaPremio extends UiPantalla {
   private final ConfigTragamonedas cfg;
   private final ConfigTragamonedas.Premio premio;
   private final Runnable alCambiar;
   private final UiLista lista = new UiLista();

   public PantallaPremio(Screen padre, ConfigTragamonedas cfg, ConfigTragamonedas.Premio premio, Runnable alCambiar) {
      super("Editar premio", padre);
      this.cfg = cfg;
      this.premio = premio;
      this.alCambiar = alCambiar;
   }

   protected void init() {
      this.panel(780, 470);
      this.cabecera(true);
      this.subtitulo("Condición para ganar y lo que suelta la máquina");
      int yPie = this.py + this.ph - 36;
      this.addRenderableWidget(
         new UiBoton(this.px + this.pw - 14 - 100, yPie, 100, 26, "Listo", UiBoton.Estilo.PRIMARIO, this::onClose)
            .ayuda("Vuelve al casino (acuérdate de pulsar Guardar allí)")
      );
      this.lista.x = this.px + 14;
      this.lista.y = this.py + 48;
      this.lista.w = this.pw - 28;
      this.lista.h = yPie - 30 - this.lista.y;
      this.reconstruir();
   }

   private void cambio() {
      this.alCambiar.run();
   }

   private void reconstruir() {
      List<UiFila> filas = new ArrayList<>();
      filas.add(new PantallaPremio.FilaTipo());
      if ("iguales".equals(this.premio.tipo)) {
         filas.add(new PantallaPremio.FilaItemsValidos());
      } else {
         filas.add(new PantallaPremio.FilaCombinacion());
      }

      filas.add(new PantallaPremio.FilaModo());

      for (int i = 0; i < this.premio.recompensas.size(); i++) {
         filas.add(new PantallaPremio.FilaRecompensa(i));
      }

      if (this.premio.recompensas.size() < 6) {
         filas.add(new PantallaPremio.FilaAgregar());
      }

      this.lista.poner(filas, false);
   }

   static ItemStack pila(String id) {
      Identifier rl = Identifier.tryParse(id);
      Item item = rl == null ? null : (Item)BuiltInRegistries.ITEM.getOptional(rl).orElse(null);
      return item == null ? ItemStack.EMPTY : new ItemStack(item);
   }

   static String nombre(String id) {
      ItemStack p = pila(id);
      return p.isEmpty() ? id : p.getHoverName().getString();
   }

   private List<String> simbolos() {
      List<String> l = new ArrayList<>();

      for (ConfigTragamonedas.Simbolo s : this.cfg.simbolos) {
         l.add(s.item);
      }

      return l;
   }

   @Override
   protected void contenido(GuiGraphicsExtractor g, int mx, int my, float pt) {
      Font f = this.font;
      this.lista.dibujar(g, f, mx, my);
      int yPie = this.py + this.ph - 36;
      double p = PantallaCasino.probabilidad(this.cfg, this.premio);
      Ui.texto(g, f, "Probabilidad de ganar este premio: " + PantallaCasino.textoProb(p), this.px + 14, yPie + 9, 7113983, 1.0F);
      Ui.texto(g, f, "Tip: con Mayús los botones − / + cambian de 10 en 10", this.px + 340, yPie + 9, 9344683, 0.9F);
   }

   public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent ev, boolean dbl) {
      double mx = ev.x();
      double my = ev.y();
      int boton = ev.button();
      return super.mouseClicked(ev, dbl) ? true : this.lista.mouseClicked(mx, my, boton);
   }

   public boolean mouseReleased(net.minecraft.client.input.MouseButtonEvent ev) {
      double mx = ev.x();
      double my = ev.y();
      int boton = ev.button();
      return this.lista.mouseReleased() || super.mouseReleased(ev);
   }

   public boolean mouseScrolled(double mx, double my, double dx, double dy) {
      return this.lista.mouseScrolled(mx, my, dx, dy) || super.mouseScrolled(mx, my, dx, dy);
   }

   private final class FilaAgregar extends FilaZonas {
      FilaAgregar() {
         this.alto = 34;
      }

      @Override
      public void dibujar(GuiGraphicsExtractor g, Font f, int x, int y, int w, int mx, int my, boolean hover) {
         this.empezar(x, y);
         this.boton(g, f, 0, 4, 220, 26, "+ Agregar recompensa", mx, my, 4906624, false, b -> {
            PantallaPremio pp = PantallaPremio.this;
            Minecraft.getInstance().gui.setScreen(new PantallaSelectorItem(pp, id -> {
               if (pp.premio.recompensas.size() < 6) {
                  pp.premio.recompensas.add(new ConfigTragamonedas.Recompensa(id, 1));
                  pp.cambio();
                  pp.reconstruir();
               }
            }));
         });
         this.texto(g, f, "Puedes poner hasta 6 recompensas distintas en un premio", 232, 13, 9344683);
      }
   }

   private final class FilaCombinacion extends FilaZonas {
      FilaCombinacion() {
         this.alto = 66;
      }

      @Override
      public void dibujar(GuiGraphicsExtractor g, Font f, int x, int y, int w, int mx, int my, boolean hover) {
         this.empezar(x, y);
         Ui.cajaBorde(g, x, y, w, this.alto, 6, Ui.c(1711656, 0.95F), Ui.c(3028037));
         ConfigTragamonedas.Premio p = PantallaPremio.this.premio;

         for (int k = 0; k < 3; k++) {
            int kk = k;
            String c = p.combinacion.get(k);
            int rx = 12 + k * 46;
            if ("*".equals(c)) {
               this.cuadroItem(g, rx, 12, 40, 40, ItemStack.EMPTY, mx, my, 7113983, b -> this.cambiar(kk, b));
               Ui.textoCentro(g, f, "*", x + rx + 20, y + 12 + 16, 16762967, 1.0F, true);
            } else {
               this.cuadroItem(g, rx, 12, 40, 40, PantallaPremio.pila(c), mx, my, 7113983, b -> this.cambiar(kk, b));
            }
         }

         this.texto(g, f, "Fila del centro de los rodillos (izquierda → derecha).", 160, 14, 15330549);
         this.texto(g, f, "Clic: siguiente símbolo · Clic derecho: anterior · * = cualquier símbolo", 160, 28, 9344683);
         this.texto(g, f, "Se revisan los premios en orden: gana el primero que coincida.", 160, 42, 9344683);
      }

      private void cambiar(int k, int boton) {
         PantallaPremio pp = PantallaPremio.this;
         List<String> o = new ArrayList<>();
         o.add("*");
         o.addAll(pp.simbolos());
         int idx = Math.max(0, o.indexOf(pp.premio.combinacion.get(k)));
         idx = Math.floorMod(idx + (boton == 1 ? -1 : 1), o.size());
         pp.premio.combinacion.set(k, o.get(idx));
         pp.cambio();
      }
   }

   private final class FilaItemsValidos extends FilaZonas {
      FilaItemsValidos() {
         this.alto = 78;
      }

      @Override
      public void dibujar(GuiGraphicsExtractor g, Font f, int x, int y, int w, int mx, int my, boolean hover) {
         this.empezar(x, y);
         Ui.cajaBorde(g, x, y, w, this.alto, 6, Ui.c(1711656, 0.95F), Ui.c(3028037));
         ConfigTragamonedas.Premio p = PantallaPremio.this.premio;
         this.texto(g, f, "Items que valen como el repetido" + (p.items.isEmpty() ? " (ninguno elegido = cualquiera de los símbolos)" : ""), 12, 8, 15330549);

         for (int k = 0; k < p.items.size(); k++) {
            int kk = k;
            this.cuadroItem(g, 12 + k * 40, 26, 36, 36, PantallaPremio.pila(p.items.get(k)), mx, my, 16735340, b -> this.quitar(kk));
         }

         if (p.items.size() < PantallaPremio.this.simbolos().size()) {
            int rx = 12 + p.items.size() * 40;
            this.boton(g, f, rx, 26, 36, 36, "+", mx, my, 4906624, false, b -> this.agregar());
         }

         this.texto(g, f, "Clic en un item para quitarlo. «+» elige entre los símbolos de los rodillos.", 12, 64, 9344683);
      }

      private void quitar(int k) {
         PantallaPremio pp = PantallaPremio.this;
         pp.premio.items.remove(k);
         pp.cambio();
      }

      private void agregar() {
         PantallaPremio pp = PantallaPremio.this;
         List<String> libres = new ArrayList<>(pp.simbolos());
         libres.removeAll(pp.premio.items);
         Minecraft.getInstance().gui.setScreen(new PantallaSelectorItem(pp, id -> {
            if (!pp.premio.items.contains(id)) {
               pp.premio.items.add(id);
               pp.cambio();
            }
         }, libres));
      }
   }

   private final class FilaModo extends FilaZonas {
      FilaModo() {
         this.alto = 78;
      }

      @Override
      public void dibujar(GuiGraphicsExtractor g, Font f, int x, int y, int w, int mx, int my, boolean hover) {
         this.empezar(x, y);
         Ui.cajaBorde(g, x, y, w, this.alto, 6, Ui.c(1711656, 0.95F), Ui.c(3028037));
         ConfigTragamonedas.Premio p = PantallaPremio.this.premio;
         boolean una = "una".equals(p.modo);
         this.texto(g, f, "2) ¿Qué se gana? Primero elige cómo se reparten las recompensas de abajo:", 12, 8, 15330549);
         this.boton(g, f, 12, 26, 250, 22, "Se dan todas las recompensas", mx, my, 7113983, !una, b -> this.poner("todas"));
         this.boton(g, f, 268, 26, 250, 22, "Se da solo UNA, elegida al azar", mx, my, 7113983, una, b -> this.poner("una"));
         this.texto(
            g,
            f,
            una
               ? "Cada vez que se gana, la máquina elige una sola recompensa. «Frecuencia» decide cuál (más alta = sale más seguido)."
               : "Al ganar, la máquina suelta todas las recompensas de abajo.",
            12,
            56,
            9344683
         );
      }

      private void poner(String modo) {
         PantallaPremio pp = PantallaPremio.this;
         pp.premio.modo = modo;
         pp.cambio();
      }
   }

   private final class FilaRecompensa extends FilaZonas {
      private final int i;

      FilaRecompensa(int i) {
         this.i = i;
         this.alto = 100;
      }

      private ConfigTragamonedas.Recompensa r() {
         return PantallaPremio.this.premio.recompensas.get(this.i);
      }

      @Override
      public void dibujar(GuiGraphicsExtractor g, Font f, int x, int y, int w, int mx, int my, boolean hover) {
         this.empezar(x, y);
         PantallaPremio pp = PantallaPremio.this;
         ConfigTragamonedas.Recompensa r = this.r();
         boolean una = "una".equals(pp.premio.modo);
         Ui.cajaBorde(g, x, y, w, this.alto, 6, Ui.c(1711656, 0.95F), Ui.c(3028037));
         this.cuadroItem(g, 12, 8, 32, 32, PantallaPremio.pila(r.item), mx, my, 7113983, b -> this.cambiarItem());
         Ui.textoN(g, f, Ui.recortar(f, PantallaPremio.nombre(r.item), 300), x + 52, y + 11, 15330549, 1.0F);
         Ui.texto(g, f, Ui.recortar(f, r.item + "  · clic en el cuadro para cambiar el item", 330), x + 52, y + 23, 9344683, 1.0F);
         this.boton(g, f, w - 30, 8, 22, 22, "×", mx, my, 16735340, false, b -> this.borrar());
         if (r.esLibro()) {
            String t = r.encantamientos.isEmpty() ? "Encantamientos: cualquiera" : "Encantamientos: " + r.encantamientos.size() + " elegidos";
            this.boton(
               g,
               f,
               w - 30 - 4 - 210,
               8,
               210,
               22,
               t,
               mx,
               my,
               16762967,
               !r.encantamientos.isEmpty(),
               b -> Minecraft.getInstance().gui.setScreen(new PantallaEncantamientos(pp, r, pp.alCambiar))
            );
         }

         int tope = r.esLibro() ? 64 : 2304;
         this.texto(g, f, "Cantidad mínima", 12, 48, 9344683);
         this.paso(g, f, 12, 60, String.valueOf(r.cantidad), mx, my, d -> {
            r.cantidad = Mth.clamp(r.cantidad + d, 1, tope);
            r.maximo = Math.max(r.maximo, r.cantidad);
            pp.cambio();
         });
         this.texto(g, f, "Cantidad máxima", 232, 48, 9344683);
         this.paso(g, f, 232, 60, String.valueOf(Math.max(r.cantidad, r.maximo)), mx, my, d -> {
            r.maximo = Mth.clamp(Math.max(r.cantidad, r.maximo) + d, r.cantidad, tope);
            pp.cambio();
         });
         if (una) {
            int total = 0;

            for (ConfigTragamonedas.Recompensa o : pp.premio.recompensas) {
               total += Math.max(1, o.peso);
            }

            this.texto(g, f, "Frecuencia (más alta = sale más)", 452, 48, 9344683);
            this.paso(g, f, 452, 60, String.valueOf(r.peso), mx, my, d -> {
               r.peso = Mth.clamp(r.peso + d, 1, 1000);
               pp.cambio();
            });
            this.texto(g, f, "= sale en " + Math.round(100.0 * Math.max(1, r.peso) / Math.max(1, total)) + " % de las veces que se gana", 552, 65, 7113983);
         }

         this.texto(
            g,
            f,
            r.cantidad == Math.max(r.cantidad, r.maximo)
               ? "Siempre da " + r.cantidad + (r.cantidad == 1 ? " unidad." : " unidades.")
               : "Da una cantidad al azar entre " + r.cantidad + " y " + Math.max(r.cantidad, r.maximo) + ".",
            12,
            84,
            9344683
         );
      }

      private void cambiarItem() {
         PantallaPremio pp = PantallaPremio.this;
         ConfigTragamonedas.Recompensa r = this.r();
         Minecraft.getInstance().gui.setScreen(new PantallaSelectorItem(pp, id -> {
            r.item = id;
            if (r.esLibro()) {
               r.cantidad = Math.min(r.cantidad, 64);
               r.maximo = Math.min(r.maximo, 64);
            } else {
               r.encantamientos = new ArrayList<>();
            }

            pp.cambio();
         }));
      }

      private void borrar() {
         PantallaPremio pp = PantallaPremio.this;
         if (pp.premio.recompensas.size() > 1) {
            pp.premio.recompensas.remove(this.i);
            pp.cambio();
            pp.reconstruir();
         }
      }
   }

   private final class FilaTipo extends FilaZonas {
      FilaTipo() {
         this.alto = 56;
      }

      @Override
      public void dibujar(GuiGraphicsExtractor g, Font f, int x, int y, int w, int mx, int my, boolean hover) {
         this.empezar(x, y);
         Ui.cajaBorde(g, x, y, w, this.alto, 6, Ui.c(1711656, 0.95F), Ui.c(3028037));
         this.texto(g, f, "1) ¿Cuándo se gana este premio? Elige cómo tienen que salir los rodillos:", 12, 8, 15330549);
         ConfigTragamonedas.Premio p = PantallaPremio.this.premio;
         boolean comb = !"iguales".equals(p.tipo);
         this.boton(g, f, 12, 26, 200, 22, "Combinación exacta", mx, my, 7113983, comb, b -> this.poner("combinacion", 2));
         this.boton(g, f, 218, 26, 200, 22, "2 iguales (cualquier lugar)", mx, my, 7113983, !comb && p.iguales < 3, b -> this.poner("iguales", 2));
         this.boton(g, f, 424, 26, 200, 22, "3 iguales", mx, my, 7113983, !comb && p.iguales >= 3, b -> this.poner("iguales", 3));
      }

      private void poner(String tipo, int iguales) {
         PantallaPremio pp = PantallaPremio.this;
         pp.premio.tipo = tipo;
         pp.premio.iguales = iguales;
         pp.cambio();
         pp.reconstruir();
      }
   }
}
