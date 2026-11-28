package net.ruleta.client;

import com.mojang.blaze3d.platform.InputConstants.Type;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Arrays;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.ruleta.BunkerAccionPayload;
import net.ruleta.LanzarPayload;
import net.ruleta.Mensajes;
import net.ruleta.RuletaColor;
import net.ruleta.client.ruleta.RuletaHud;
import net.ruleta.client.ui.EscalaGui;
import net.ruleta.client.ui.Suave;
import net.ruleta.client.ui.Ui;
import net.ruleta.client.ui.UiBoton;
import net.ruleta.tragamonedas.client.PantallaPanel;

public class LanzadorRuleta extends Screen {
   public static final KeyMapping ABRIR = new KeyMapping("key.ruletafanproject.lanzador", Type.KEYSYM, 71, "key.categories.ruletafanproject");
   private static final float RADIO = 214.0F;
   private static final float CRECE = 1.12F;
   private static final int Y_BOTON = 50;
   private final float[] escala = new float[8];
   private final float[] luz = new float[8];
   private long ultimo = Util.getMillis();
   private float cx;
   private float cy;
   private float s = 1.0F;
   private int hover = -1;
   private final Suave aparece = new Suave();

   public LanzadorRuleta() {
      super(Component.literal("Lanzar ruleta"));
      Arrays.fill(this.escala, 1.0F);
   }

   public static void tecla(Minecraft mc) {
      while (ABRIR.consumeClick()) {
         if (mc.player != null && mc.screen == null) {
            if (!mc.player.hasPermissions(2)) {
               mc.player.displayClientMessage(Component.literal("El lanzador de la ruleta es sólo para operadores."), true);
            } else if (!servidorConMod()) {
               mc.player.displayClientMessage(Component.literal("Este servidor no tiene el mod de la ruleta."), true);
            } else {
               mc.setScreen(new LanzadorRuleta());
            }
         }
      }
   }

   private static boolean servidorConMod() {
      Minecraft mc = Minecraft.getInstance();
      return mc.getConnection() != null && RedCliente.servidorConMod();
   }

   protected void init() {
      EscalaGui.ajustar(this, 640, 340);
      Fondo.poner();
      int bw = Math.min(118, (this.width - 40 - 32) / 5);
      int total = bw * 5 + 32;
      int x0 = (this.width - total) / 2;
      int yb = this.height - 50;
      this.addRenderableWidget(
         new UiBoton(x0, yb, bw, 22, "Ajustes", UiBoton.Estilo.NORMAL, () -> Minecraft.getInstance().setScreen(new PantallaAjustes(this)))
            .ayuda("Daños, tiempos, rayos, distancias... todo lo configurable")
      );
      this.addRenderableWidget(
         new UiBoton(x0 + bw + 8, yb, bw, 22, "Cambios activos", UiBoton.Estilo.NORMAL, () -> Minecraft.getInstance().setScreen(new PantallaCambiosActivos()))
            .ayuda("Los cambios de dificultad que hay ahora en el mundo")
      );
      this.addRenderableWidget(
         new UiBoton(x0 + (bw + 8) * 2, yb, bw, 22, "Bunkers", UiBoton.Estilo.NORMAL, () -> RedCliente.enviar(new BunkerAccionPayload("abrir", "")))
            .ayuda("Ver los bunkers y demolerlos")
      );
      this.addRenderableWidget(
         new UiBoton(x0 + (bw + 8) * 3, yb, bw, 22, "Panel", UiBoton.Estilo.NORMAL, () -> Minecraft.getInstance().setScreen(new PantallaPanel(this)))
            .ayuda("Opciones para configurar el casino y lo que vaya añadiendo el mod")
      );
      this.addRenderableWidget(new UiBoton(x0 + (bw + 8) * 4, yb, bw, 22, "Detener ruleta", UiBoton.Estilo.PELIGRO, () -> {
         RedCliente.enviar(new LanzarPayload(-1, 1, false, false, "", "", "", ""));
         this.onClose();
      }));
   }

   public void removed() {
      EscalaGui.restaurar();
      Fondo.quitar();
      Ui.alfa = 1.0F;
   }

   public void renderBackground(GuiGraphics g, int mouseX, int mouseY, float parcialFondo) {
   }

   private void fondoRuleta(GuiGraphics g) {
      float a = this.aparece.v;
      g.fillGradient(0, 0, this.width, this.height, Ui.c(329484, 0.45F * a), Ui.c(329484, 0.7F * a));
   }

   private void calcular() {
      float libre = Math.max(60.0F, this.height - 50 - 13.0F - 3.0F - 26.0F);
      float radio = Math.min(libre / 2.0F, this.width * 0.34F * 1.12F);
      this.s = radio / 1.12F / 214.0F;
      this.cx = this.width / 2.0F;
      this.cy = 26.0F + radio;
   }

   private int elegir(double mx, double my) {
      double dx = mx - this.cx;
      double dy = my - this.cy;
      double d = Math.hypot(dx, dy);
      if (!(d < 4.0) && !(d > 214.0F * this.s * 1.12F)) {
         double a = Math.toDegrees(Math.atan2(dx, -dy));
         return Math.floorMod((int)Math.floor((a + 22.5) / 45.0), 8);
      } else {
         return -1;
      }
   }

   public void render(GuiGraphics ctx, int mx, int my, float pt) {
      float aparicion = this.aparece.hacia(1.0F, 11.0F);
      this.fondoRuleta(ctx);
      this.calcular();
      this.hover = this.elegir(mx, my);
      long ahora = Util.getMillis();
      float dt = Math.min(0.1F, (float)(ahora - this.ultimo) / 1000.0F);
      this.ultimo = ahora;

      for (int i = 0; i < 8; i++) {
         float objetivo = i == this.hover ? 1.12F : 1.0F;
         this.escala[i] = this.escala[i] + (objetivo - this.escala[i]) * Math.min(1.0F, dt * 16.0F);
         float brillo = i == this.hover ? 1.0F : 0.0F;
         this.luz[i] = this.luz[i] + (brillo - this.luz[i]) * Math.min(1.0F, dt * 12.0F);
      }

      PoseStack pose = ctx.pose();
      BufferSource vcp = ctx.bufferSource();

      for (int pase = 0; pase < 2; pase++) {
         for (int i = 0; i < 8; i++) {
            if (!(this.escala[i] > 1.02F) && !(this.luz[i] > 0.01F) ? pase == 0 : pase == 1) {
               pose.pushPose();
               pose.translate(this.cx, this.cy, 0.0F);
               pose.scale(this.s, this.s, 1.0F);
               pose.mulPose(Axis.ZP.rotationDegrees(i * 45.0F));
               pose.scale(this.escala[i], this.escala[i], 1.0F);
               RuletaHud.casillaLanzador(pose.last().pose(), i, this.luz[i]);
               pose.popPose();
            }
         }
      }

      ctx.flush();
      Ui.alfa = aparicion;
      Ui.textoCentro(ctx, this.font, "Elige una ruleta", this.width / 2, 12, 15330549, 1.0F, true);
      if (this.hover >= 0) {
         RuletaColor c = RuletaColor.byIndex(this.hover);
         String nombre = Mensajes.encabezado(c);
         nombre = nombre.endsWith(":") ? nombre.substring(0, nombre.length() - 1) : nombre;
         Ui.textoCentro(ctx, this.font, nombre, this.width / 2, this.height - 50 - 13, c.rgb, 1.0F, true);
      }

      super.render(ctx, mx, my, pt);
      Ui.alfa = 1.0F;
   }

   public boolean mouseClicked(double mx, double my, int boton) {
      if (super.mouseClicked(mx, my, boton)) {
         return true;
      } else {
         this.calcular();
         int i = this.elegir(mx, my);
         if (boton == 0 && i >= 0) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI((SoundEvent)SoundEvents.UI_BUTTON_CLICK.value(), 1.0F));
            Minecraft.getInstance().setScreen(new LanzadorFormulario(RuletaColor.byIndex(i)));
            return true;
         } else {
            return false;
         }
      }
   }

   public boolean keyPressed(int key, int scan, int mods) {
      if (ABRIR.matches(key, scan)) {
         this.onClose();
         return true;
      } else {
         return super.keyPressed(key, scan, mods);
      }
   }

   public boolean isPauseScreen() {
      return false;
   }
}
