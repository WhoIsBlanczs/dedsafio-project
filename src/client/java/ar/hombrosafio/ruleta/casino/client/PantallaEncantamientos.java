package ar.hombrosafio.ruleta.casino.client;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.enchantment.Enchantment;
import ar.hombrosafio.ruleta.client.ui.Ui;
import ar.hombrosafio.ruleta.client.ui.UiBoton;
import ar.hombrosafio.ruleta.client.ui.UiCampo;
import ar.hombrosafio.ruleta.client.ui.UiFila;
import ar.hombrosafio.ruleta.client.ui.UiLista;
import ar.hombrosafio.ruleta.client.ui.UiPantalla;
import ar.hombrosafio.ruleta.casino.ConfigTragamonedas;

public class PantallaEncantamientos extends UiPantalla {
   private final ConfigTragamonedas.Recompensa recompensa;
   private final Runnable alCambiar;
   private final List<PantallaEncantamientos.Info> todos = new ArrayList<>();
   private final UiLista lista = new UiLista();
   private UiCampo buscador;
   private String texto = "";

   public PantallaEncantamientos(Screen padre, ConfigTragamonedas.Recompensa recompensa, Runnable alCambiar) {
      super("Encantamientos del libro", padre);
      this.recompensa = recompensa;
      this.alCambiar = alCambiar;
      ClientLevel nivel = Minecraft.getInstance().level;
      if (nivel != null) {
         var reg = nivel.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);

         reg.listElements().forEach(h -> {
            Enchantment e = h.value();
            this.todos.add(new PantallaEncantamientos.Info(h.key().identifier().toString(), e.description().getString(), e.getMaxLevel()));
         });
      }

      this.todos.sort(Comparator.comparing(PantallaEncantamientos.Info::nombre, String.CASE_INSENSITIVE_ORDER));
   }

   protected void init() {
      this.panel(640, 470);
      this.cabecera(true);
      this.subtitulo("Marca los que pueden salir. Sin marcar ninguno: sale cualquiera al azar.");
      int y = this.py + 48;
      this.buscador = (UiCampo)this.addRenderableWidget(new UiCampo(this.font, this.px + 14, y, 300, 24, Component.literal("Buscar")));
      this.buscador.setHint(Component.literal("Buscar encantamiento..."));
      this.buscador.setMaxLength(60);
      this.buscador.setValue(this.texto);
      this.buscador.setResponder(v -> {
         this.texto = v;
         this.reconstruir();
      });
      this.addRenderableWidget(new UiBoton(this.px + 320, y, 150, 24, "Marcar todos", () -> {
         this.recompensa.encantamientos.clear();

         for (PantallaEncantamientos.Info i : this.todos) {
            this.recompensa.encantamientos.add(new ConfigTragamonedas.Encantamiento(i.id(), 1, i.nivelMaximo()));
         }

         this.alCambiar.run();
      }).ayuda("Marca todos con su rango de nivel completo (1 hasta el máximo normal)"));
      this.addRenderableWidget(new UiBoton(this.px + 476, y, 150, 24, "Quitar todos", UiBoton.Estilo.PELIGRO, () -> {
         this.recompensa.encantamientos.clear();
         this.alCambiar.run();
      }).ayuda("Desmarca todos: saldrá cualquier encantamiento al azar"));
      int yPie = this.py + this.ph - 36;
      this.addRenderableWidget(new UiBoton(this.px + this.pw - 14 - 100, yPie, 100, 26, "Listo", UiBoton.Estilo.PRIMARIO, this::onClose));
      this.lista.x = this.px + 14;
      this.lista.y = y + 34;
      this.lista.w = this.pw - 28;
      this.lista.h = yPie - 30 - this.lista.y;
      this.reconstruir();
   }

   private void reconstruir() {
      String q = this.texto.trim().toLowerCase(Locale.ROOT);
      List<UiFila> filas = new ArrayList<>();

      for (PantallaEncantamientos.Info i : this.todos) {
         if (q.isEmpty() || i.nombre().toLowerCase(Locale.ROOT).contains(q) || i.id().contains(q)) {
            filas.add(new PantallaEncantamientos.FilaEnc(i));
         }
      }

      this.lista.poner(filas, false);
   }

   private ConfigTragamonedas.Encantamiento buscar(String id) {
      for (ConfigTragamonedas.Encantamiento e : this.recompensa.encantamientos) {
         if (id.equals(e.id)) {
            return e;
         }
      }

      return null;
   }

   @Override
   protected void contenido(GuiGraphicsExtractor g, int mx, int my, float pt) {
      Font f = this.font;
      this.lista.dibujar(g, f, mx, my);
      int yPie = this.py + this.ph - 36;
      int n = this.recompensa.encantamientos.size();
      Ui.texto(
         g,
         f,
         n == 0 ? "Ninguno marcado: sale cualquier encantamiento al azar" : n + " marcados: sale uno de ellos al azar",
         this.px + 14,
         yPie + 9,
         7113983,
         1.0F
      );
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

   private final class FilaEnc extends FilaZonas {
      private final PantallaEncantamientos.Info info;

      FilaEnc(PantallaEncantamientos.Info info) {
         this.info = info;
         this.alto = 34;
      }

      @Override
      public void dibujar(GuiGraphicsExtractor g, Font f, int x, int y, int w, int mx, int my, boolean hover) {
         this.empezar(x, y);
         PantallaEncantamientos pe = PantallaEncantamientos.this;
         ConfigTragamonedas.Encantamiento e = pe.buscar(this.info.id());
         Ui.cajaBorde(
            g,
            x,
            y,
            w,
            this.alto,
            6,
            Ui.c(e != null ? Ui.mezcla(1711656, 16762967, 0.14F) : 1711656, 0.95F),
            Ui.c(e != null ? 16762967 : 3028037, e != null ? 0.7F : 1.0F)
         );
         this.boton(g, f, 8, 7, 20, 20, e != null ? "✔" : "", mx, my, 16762967, e != null, b -> this.alternar());
         Ui.textoN(g, f, Ui.recortar(f, this.info.nombre(), 200), x + 38, y + 8, 15330549, 1.0F);
         Ui.texto(g, f, Ui.recortar(f, this.info.id(), 215), x + 38, y + 20, 9344683, 0.85F);
         if (e != null) {
            this.texto(g, f, "Nivel desde", 262, 13, 9344683);
            this.paso(g, f, 330, 8, String.valueOf(e.nivelMin), mx, my, d -> {
               e.nivelMin = Mth.clamp(e.nivelMin + d, 1, 255);
               e.nivelMax = Math.max(e.nivelMax, e.nivelMin);
               pe.alCambiar.run();
            });
            this.texto(g, f, "hasta", 432, 13, 9344683);
            this.paso(g, f, 470, 8, String.valueOf(e.nivelMax), mx, my, d -> {
               e.nivelMax = Mth.clamp(e.nivelMax + d, e.nivelMin, 255);
               pe.alCambiar.run();
            });
         } else {
            this.texto(g, f, "nivel máximo normal: " + this.info.nivelMaximo(), 262, 13, 9344683);
         }
      }

      private void alternar() {
         PantallaEncantamientos pe = PantallaEncantamientos.this;
         ConfigTragamonedas.Encantamiento e = pe.buscar(this.info.id());
         if (e != null) {
            pe.recompensa.encantamientos.remove(e);
         } else {
            pe.recompensa.encantamientos.add(new ConfigTragamonedas.Encantamiento(this.info.id(), 1, this.info.nivelMaximo()));
         }

         pe.alCambiar.run();
      }
   }

   private record Info(String id, String nombre, int nivelMaximo) {
   }
}
