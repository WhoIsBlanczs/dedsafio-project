package net.ruleta.client;

import com.mojang.blaze3d.platform.InputConstants.Type;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Arrays;
import net.minecraft.util.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
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
   public static final KeyMapping ABRIR = new KeyMapping("key.ruleta.lanzador", Type.KEYBOARD, 71, net.minecraft.client.KeyMapping.Category.MISC);
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
         if (mc.player != null && mc.gui.screen() == null) {
            if (!mc.player.permissions().hasPermission(net.minecraft.server.permissions.Permissions.COMMANDS_GAMEMASTER)) {
               mc.player.sendOverlayMessage(Component.literal("El lanzador de la ruleta es sólo para operadores."));
            } else if (!servidorConMod()) {
               mc.player.sendOverlayMessage(Component.literal("Este servidor no tiene el mod de la ruleta."));
            } else {
               mc.setScreenAndShow(new LanzadorRuleta());
            }
         }
      }
   }

   private static boolean servidorConMod() {
      Minecraft mc = Minecraft.getInstance();
      return mc.getConnection() != null && RedCliente.servidorConMod();
   }

   protected void init() { }

   public void removed() {
      EscalaGui.restaurar();
      Fondo.quitar();
      Ui.alfa = 1.0F;
   }

   public void extractBackground(GuiGraphicsExtractor g, int mouseX, int mouseY, float parcialFondo) {
   }

   private void fondoRuleta(GuiGraphicsExtractor g) {
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

   public void extractRenderState(GuiGraphicsExtractor ctx, int mx, int my, float pt) { }

   public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent ev_, boolean dbl_) {
      double mx = ev_.x(); double my = ev_.y(); int boton = ev_.button();
      if (super.mouseClicked(ev_, dbl_)) {
         return true;
      } else {
         this.calcular();
         int i = this.elegir(mx, my);
         if (boton == 0 && i >= 0) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI((SoundEvent)SoundEvents.UI_BUTTON_CLICK.value(), 1.0F));
            Minecraft.getInstance().setScreenAndShow(new LanzadorFormulario(RuletaColor.byIndex(i)));
            return true;
         } else {
            return false;
         }
      }
   }

   public boolean keyPressed(net.minecraft.client.input.KeyEvent ev_) { return false; }

   public boolean isPauseScreen() {
      return false;
   }
}
