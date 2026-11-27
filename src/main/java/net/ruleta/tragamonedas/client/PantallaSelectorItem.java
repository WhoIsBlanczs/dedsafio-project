package net.ruleta.tragamonedas.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.TreeSet;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.ruleta.client.ui.Ui;
import net.ruleta.client.ui.UiBoton;
import net.ruleta.client.ui.UiCampo;
import net.ruleta.client.ui.UiFila;
import net.ruleta.client.ui.UiLista;
import net.ruleta.client.ui.UiPantalla;

public class PantallaSelectorItem extends UiPantalla {
   private static final int CELDA = 36;
   private final Consumer<String> alElegir;
   private final List<PantallaSelectorItem.ItemEntrada> todos = new ArrayList<>();
   private final List<String> mods = new ArrayList<>();
   private final UiLista lista = new UiLista();
   private UiCampo buscador;
   private UiBoton botonMod;
   private int modActual;
   private String texto = "";
   private String hoverId;
   private ItemStack hoverPila = ItemStack.EMPTY;
   private int cantidad;

   public PantallaSelectorItem(Screen padre, Consumer<String> alElegir) {
      this(padre, alElegir, null);
   }

   public PantallaSelectorItem(Screen padre, Consumer<String> alElegir, List<String> soloEstos) {
      super("Elegir item", padre);
      this.alElegir = alElegir;
      TreeSet<String> namespaces = new TreeSet<>();

      for (Item item : BuiltInRegistries.ITEM) {
         if (item != Items.AIR) {
            Identifier rl = BuiltInRegistries.ITEM.getKey(item);
            if (soloEstos == null || soloEstos.contains(rl.toString())) {
               ItemStack pila = new ItemStack(item);
               String nombre = pila.getHoverName().getString();
               this.todos.add(new PantallaSelectorItem.ItemEntrada(rl.toString(), rl.getNamespace(), nombre, nombre.toLowerCase(Locale.ROOT), pila));
               namespaces.add(rl.getNamespace());
            }
         }
      }

      this.todos.sort((a, b) -> {
         boolean ma = a.namespace().equals("minecraft");
         boolean mb = b.namespace().equals("minecraft");
         if (ma != mb) {
            return ma ? -1 : 1;
         }

         int c = a.namespace().compareTo(b.namespace());
         return c != 0 ? c : a.id().compareTo(b.id());
      });
      this.mods.add("");
      if (namespaces.remove("minecraft")) {
         this.mods.add("minecraft");
      }

      this.mods.addAll(namespaces);
   }

   protected void init() {
      this.panel(640, 470);
      this.cabecera(true);
      this.subtitulo("Todos los items del juego, vanilla y de mods. Clic en uno para elegirlo.");
      int y = this.py + 48;
      this.buscador = (UiCampo)this.addRenderableWidget(new UiCampo(this.font, this.px + 14, y, this.pw - 28 - 190, 24, Component.literal("Buscar")));
      this.buscador.setHint(Component.literal("Buscar por nombre o id..."));
      this.buscador.setMaxLength(60);
      this.buscador.setValue(this.texto);
      this.buscador.setResponder(v -> {
         this.texto = v;
         this.rehacer();
      });
      this.botonMod = (UiBoton)this.addRenderableWidget(new UiBoton(this.px + this.pw - 14 - 180, y, 180, 24, this.textoMod(), () -> {
         this.modActual = (this.modActual + 1) % this.mods.size();
         this.botonMod.setTexto(this.textoMod());
         this.rehacer();
      }).ayuda("Filtra por mod (clic para cambiar)"));
      this.setInitialFocus(this.buscador);
      this.lista.x = this.px + 14;
      this.lista.y = y + 34;
      this.lista.w = this.pw - 28;
      this.lista.h = this.py + this.ph - 30 - this.lista.y;
      this.rehacer();
   }

   private String textoMod() {
      String m = this.mods.get(this.modActual);
      return "Mod: " + (m.isEmpty() ? "todos" : Ui.recortar(this.font, m, 130));
   }

   private void rehacer() {
      String q = this.texto.trim().toLowerCase(Locale.ROOT);
      String mod = this.mods.get(this.modActual);
      List<PantallaSelectorItem.ItemEntrada> filtrados = new ArrayList<>();

      for (PantallaSelectorItem.ItemEntrada e : this.todos) {
         if ((mod.isEmpty() || e.namespace().equals(mod)) && (q.isEmpty() || e.id().contains(q) || e.nombreBajo().contains(q))) {
            filtrados.add(e);
         }
      }

      int porFila = Math.max(1, (this.lista.w - 12) / 36);
      List<UiFila> filas = new ArrayList<>();

      for (int i = 0; i < filtrados.size(); i += porFila) {
         filas.add(new PantallaSelectorItem.FilaItems(filtrados.subList(i, Math.min(filtrados.size(), i + porFila))));
      }

      this.cantidad = filtrados.size();
      this.lista.poner(filas, false);
      this.lista.volverArriba();
   }

   @Override
   protected void contenido(GuiGraphicsExtractor g, int mx, int my, float pt) {
      Font f = this.font;
      this.hoverId = null;
      this.lista.dibujar(g, f, mx, my);
      if (this.cantidad == 0) {
         Ui.textoCentro(g, f, "No hay items con ese filtro", this.lista.x + this.lista.w / 2, this.lista.y + 30, 9344683, 1.0F, false);
      }

      Ui.texto(g, f, this.cantidad + " items", this.px + 14, this.lista.y + this.lista.h + 8, 9344683, 0.95F);
   }

   @Override
   protected void encima(GuiGraphicsExtractor g, int mx, int my, float pt) { }

   public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent ev_, boolean dbl_) {
      double mx = ev_.x(); double my = ev_.y(); int boton = ev_.button();
      return super.mouseClicked(ev_, dbl_) ? true : this.lista.mouseClicked(mx, my, boton);
   }

   public boolean mouseReleased(net.minecraft.client.input.MouseButtonEvent ev_) {
      double mx = ev_.x(); double my = ev_.y(); int boton = ev_.button();
      return this.lista.mouseReleased() || super.mouseReleased(ev_);
   }

   public boolean mouseScrolled(double mx, double my, double dx, double dy) {
      return this.lista.mouseScrolled(mx, my, dx, dy) || super.mouseScrolled(mx, my, dx, dy);
   }

   private void elegir(String id) {
      this.minecraft.setScreenAndShow(this.padre);
      this.alElegir.accept(id);
   }

   private final class FilaItems extends UiFila {
      private final List<PantallaSelectorItem.ItemEntrada> items;

      FilaItems(List<PantallaSelectorItem.ItemEntrada> items) {
         this.items = items;
         this.alto = 36;
      }

      @Override
      public void dibujar(GuiGraphicsExtractor g, Font f, int x, int y, int w, int mx, int my, boolean hover) {
         for (int k = 0; k < this.items.size(); k++) {
            PantallaSelectorItem.ItemEntrada e = this.items.get(k);
            int cx = x + k * 36;
            boolean h = Ui.dentro(mx, my, cx, y, 34, 34)
               && my >= PantallaSelectorItem.this.lista.y
               && my < PantallaSelectorItem.this.lista.y + PantallaSelectorItem.this.lista.h;
            Ui.cajaBorde(g, cx, y, 34, 34, 4, Ui.c(h ? 2369846 : 1711656), Ui.c(h ? 7113983 : 3028037));
            g.item(e.pila(), cx + 9, y + 9);
            if (h) {
               PantallaSelectorItem.this.hoverId = e.id();
               PantallaSelectorItem.this.hoverPila = e.pila();
            }
         }
      }

      @Override
      public boolean clic(double mx, double my, int boton, int x, int y, int w) {
         int k = (int)((mx - x) / 36.0);
         if (mx >= x && k >= 0 && k < this.items.size() && boton == 0) {
            PantallaSelectorItem.this.elegir(this.items.get(k).id());
            return true;
         } else {
            return false;
         }
      }
   }

   private record ItemEntrada(String id, String namespace, String nombre, String nombreBajo, ItemStack pila) {
   }
}
