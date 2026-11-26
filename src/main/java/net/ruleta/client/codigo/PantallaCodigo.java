package net.ruleta.client.codigo;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.ruleta.candado.CodigoEnviarPayload;
import net.ruleta.client.RedCliente;

public class PantallaCodigo extends Screen {
   private static final ResourceLocation FONDO = ResourceLocation.fromNamespaceAndPath("ruleta", "textures/gui/codigo.png");
   private static final int W = 194;
   private static final int H = 229;
   private static final float ESCALA_MAX = 1.55F;
   private final int modo;
   private final BlockPos pos;
   private final StringBuilder codigo = new StringBuilder();
   private boolean ver;
   private String aviso = "";
   private long avisoHasta;
   private long bloqueadoHasta;
   private static final int[] COL = new int[]{45, 82, 120};
   private static final int[] FILA = new int[]{69, 105, 143, 180};
   private static final int KW = 30;
   private static final int KH = 30;

   public PantallaCodigo(int modo, BlockPos pos) {
      super(Component.literal("Introduce tu código"));
      this.modo = modo;
      this.pos = pos;
   }

   public static void abrir(int modo, BlockPos pos) {
      Minecraft.getInstance().setScreen(new PantallaCodigo(modo, pos));
   }

   public static void respuesta(boolean ok) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.screen instanceof PantallaCodigo p) {
         if (ok) {
            mc.setScreen(null);
         } else {
            p.codigo.setLength(0);
            p.aviso = "Código incorrecto";
            p.avisoHasta = System.currentTimeMillis() + 1500L;
            p.bloqueadoHasta = System.currentTimeMillis() + 1000L;
            mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.VILLAGER_NO, 1.0F));
         }
      }
   }

   public boolean isPauseScreen() {
      return false;
   }

   private float escala() {
      return Math.max(0.5F, Math.min(1.55F, Math.min(this.height * 0.94F / 229.0F, this.width * 0.94F / 194.0F)));
   }

   private int x0() {
      return Math.round((this.width / this.escala() - 194.0F) / 2.0F);
   }

   private int y0() {
      return Math.round((this.height / this.escala() - 229.0F) / 2.0F);
   }

   private int teclaX(int col) {
      return this.x0() + COL[col];
   }

   private int teclaY(int fila) {
      return this.y0() + FILA[fila];
   }

   private int ojoX() {
      return this.x0() + 154;
   }

   private int ojoY() {
      return this.y0() + 36;
   }

   private void click() {
      Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
   }

   private boolean libre() {
      return System.currentTimeMillis() >= this.bloqueadoHasta;
   }

   private void numero(int n) {
      if (this.libre() && this.codigo.length() < 8) {
         this.codigo.append((char)(48 + n));
         this.click();
      }
   }

   private void borrar() {
      if (this.codigo.length() > 0) {
         this.codigo.setLength(this.codigo.length() - 1);
         this.click();
      }
   }

   private void confirmar() {
      if (this.libre() && this.codigo.length() != 0) {
         this.click();
         RedCliente.enviar(new CodigoEnviarPayload(this.modo, this.pos, this.codigo.toString()));
         this.bloqueadoHasta = System.currentTimeMillis() + 1000L;
      }
   }

   public boolean keyPressed(int tecla, int scan, int mods) {
      if (tecla >= 48 && tecla <= 57) {
         this.numero(tecla - 48);
         return true;
      }

      if (tecla >= 320 && tecla <= 329) {
         this.numero(tecla - 320);
         return true;
      }

      if (tecla == 259) {
         this.borrar();
         return true;
      }

      if (tecla != 257 && tecla != 335) {
         return super.keyPressed(tecla, scan, mods);
      }

      this.confirmar();
      return true;
   }

   private static boolean dentro(double mx, double my, int x, int y, int w, int h) {
      return mx >= x && mx < x + w && my >= y && my < y + h;
   }

   public boolean mouseClicked(double mxReal, double myReal, int boton) {
      double mx = mxReal / this.escala();
      double my = myReal / this.escala();
      if (boton == 0) {
         if (dentro(mx, my, this.ojoX(), this.ojoY(), 17, 16)) {
            this.ver = !this.ver;
            this.click();
            return true;
         }

         for (int f = 0; f < 4; f++) {
            for (int c = 0; c < 3; c++) {
               if (dentro(mx, my, this.teclaX(c), this.teclaY(f), 30, 30)) {
                  if (f < 3) {
                     this.numero(f * 3 + c + 1);
                  } else if (c == 0) {
                     this.borrar();
                  } else if (c == 1) {
                     this.numero(0);
                  } else {
                     this.confirmar();
                  }

                  return true;
               }
            }
         }
      }

      return super.mouseClicked(mxReal, myReal, boton);
   }

   public void renderBackground(GuiGraphics g, int mouseX, int mouseY, float parcialFondo) {
   }

   public void render(GuiGraphics g, int mxReal, int myReal, float parcial) {
      g.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
      int mx = (int)Math.floor(mxReal / this.escala());
      int my = (int)Math.floor(myReal / this.escala());
      g.pose().pushPose();
      g.pose().scale(this.escala(), this.escala(), 1.0F);
      int x0 = this.x0();
      int y0 = this.y0();
      RenderSystem.enableBlend();
      g.blit(FONDO, x0, y0, 194, 229, 0.0F, 0.0F, 776, 916, 776, 916);
      String texto = this.ver ? this.codigo.toString() : "*".repeat(this.codigo.length());
      g.drawString(this.font, texto, x0 + 48, y0 + 38, -11141291, false);

      for (int f = 0; f < 3; f++) {
         for (int c = 0; c < 3; c++) {
            this.numeroTecla(g, mx, my, c, f, String.valueOf(f * 3 + c + 1));
         }
      }

      this.numeroTecla(g, mx, my, 1, 3, "0");
      this.marco(g, mx, my, this.teclaX(0), this.teclaY(3));
      this.marco(g, mx, my, this.teclaX(2), this.teclaY(3));
      this.resalte(g, mx, my, this.ojoX(), this.ojoY(), 17, 16);
      if (this.ver) {
         g.fill(this.ojoX(), this.ojoY(), this.ojoX() + 17, this.ojoY() + 16, 822083583);
      }

      if (System.currentTimeMillis() < this.avisoHasta) {
         g.drawCenteredString(this.font, this.aviso, x0 + 97, y0 + 229 + 6, 16733525);
      }

      g.pose().popPose();
      super.render(g, mxReal, myReal, parcial);
   }

   private void numeroTecla(GuiGraphics g, int mx, int my, int c, int f, String t) {
      int x = this.teclaX(c);
      int y = this.teclaY(f);
      this.resalte(g, mx, my, x, y, 30, 30);
      int tx = x + 15 - this.font.width(t) / 2;
      g.drawString(this.font, t, tx + 1, y + 15 - 3, -12829116, false);
      g.drawString(this.font, t, tx, y + 15 - 4, -2959909, false);
   }

   private void marco(GuiGraphics g, int mx, int my, int x, int y) {
      if (dentro(mx, my, x, y, 30, 30) && this.libre()) {
         int c = -2035998;
         g.fill(x, y, x + 31, y + 1, c);
         g.fill(x, y + 30, x + 31, y + 31, c);
         g.fill(x, y, x + 1, y + 31, c);
         g.fill(x + 30, y, x + 31, y + 31, c);
      }
   }

   private void resalte(GuiGraphics g, int mx, int my, int x, int y, int w, int h) {
      if (dentro(mx, my, x, y, w, h) && this.libre()) {
         g.fill(x, y, x + w, y + h, 687865855);
      }
   }
}
