package net.ruleta.client;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.ruleta.client.ui.Suave;
import net.ruleta.client.ui.Ui;
import net.ruleta.client.ui.UiBoton;
import net.ruleta.client.ui.UiInterruptor;
import net.ruleta.client.ui.UiPantalla;

public class PantallaHud extends UiPantalla {
   private final List<PantallaHud.Opcion> opciones = new ArrayList<>();
   private final List<Suave> hover = new ArrayList<>();

   public PantallaHud() {
      super("HUD", null);
      this.opciones
         .add(
            new PantallaHud.Opcion(
               "Animación del Momento Reviil",
               "La escena que sale al empezar un Momento Reviil",
               () -> OpcionesCliente.animReviil,
               v -> OpcionesCliente.animReviil = v
            )
         );
      this.opciones
         .add(
            new PantallaHud.Opcion(
               "Animación de muerte", "La escena que sale cuando mueres por un Momento", () -> OpcionesCliente.animMuerte, v -> OpcionesCliente.animMuerte = v
            )
         );
      this.opciones
         .add(new PantallaHud.Opcion("Momento Nutria", "La escena del Momento Nutria", () -> OpcionesCliente.nutria, v -> OpcionesCliente.nutria = v));
      this.opciones
         .add(
            new PantallaHud.Opcion(
               "Coordenadas", "Mostrar tus coordenadas en pantalla", () -> OpcionesCliente.coordenadas, v -> OpcionesCliente.coordenadas = v
            )
         );
      this.opciones
         .add(
            new PantallaHud.Opcion(
               "Cielo del Momento Reviil",
               "La tormenta y el cielo rojo durante el Momento Reviil",
               () -> OpcionesCliente.reviilCielo,
               v -> OpcionesCliente.reviilCielo = v
            )
         );

      for (int i = 0; i < this.opciones.size(); i++) {
         this.hover.add(new Suave());
      }
   }

   protected void init() {
      this.panel(480, 100 + this.opciones.size() * 50);
      this.cabecera(true);
      this.subtitulo("Solo cambia lo que ves tú · se guarda en tu juego");

      for (int i = 0; i < this.opciones.size(); i++) {
         PantallaHud.Opcion o = this.opciones.get(i);
         this.addRenderableWidget(new UiInterruptor(this.px + this.pw - 16 - 30 - 12, this.py + 58 + i * 50 + 17, o.leer(), v -> {
            o.poner().accept(v);
            OpcionesCliente.guardar();
         }));
      }

      this.addRenderableWidget(new UiBoton(this.px + this.pw - 16 - 110, this.py + this.ph - 14 - 28, 110, 28, "Listo", UiBoton.Estilo.PRIMARIO, this::onClose));
   }

   @Override
   protected void contenido(GuiGraphicsExtractor g, int mx, int my, float pt) {
      for (int i = 0; i < this.opciones.size(); i++) {
         PantallaHud.Opcion o = this.opciones.get(i);
         int y = this.py + 54 + i * 50;
         int x = this.px + 16;
         int w = this.pw - 32;
         float h = this.hover.get(i).hacia(Ui.dentro(mx, my, x, y, w, 44) ? 1.0F : 0.0F, 14.0F);
         Ui.cajaBorde(g, x, y, w, 44, 7, Ui.c(Ui.mezcla(1711656, 2369846, h * 0.6F)), Ui.c(Ui.mezcla(3028037, 7113983, h * 0.5F)));
         Ui.textoN(g, this.font, o.nombre(), x + 14, y + 11, 15330549, 1.0F);
         Ui.texto(g, this.font, o.ayuda(), x + 14, y + 24, 9344683, 1.0F);
      }
   }

   @Override
   public void onClose() {
      Minecraft.getInstance().setScreenAndShow(null);
   }

   public static final class Comando {
      private Comando() {
      }

      public static void registrar() {
         ClientCommandRegistrationCallback.EVENT
            .register(
               (ClientCommandRegistrationCallback)(dispatcher, acceso) -> dispatcher.register(
                  (LiteralArgumentBuilder)ClientCommandManager.literal("hud").executes(c -> {
                     Minecraft mc = Minecraft.getInstance();
                     mc.tell(() -> mc.setScreen(new PantallaHud()));
                     return 1;
                  })
               )
            );
      }
   }

   private record Opcion(String nombre, String ayuda, BooleanSupplier leer, Consumer<Boolean> poner) {
   }
}
