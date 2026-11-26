package net.ruleta.cajero.client;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.ruleta.cajero.CajeroRed;
import net.ruleta.cajero.CajeroServidor;

@Environment(EnvType.CLIENT)
public class PantallaCajero extends Screen {
   private static final ResourceLocation MARCO = CajeroCliente.tex("marco");
   private static final ResourceLocation BOTON = CajeroCliente.tex("boton");
   private static final ResourceLocation FICHAS = CajeroCliente.tex("fichas");
   private static final ResourceLocation FLECHA_ARRIBA = CajeroCliente.tex("flecha_arriba");
   private static final ResourceLocation FLECHA_ABAJO = CajeroCliente.tex("flecha_abajo");
   private static final int W = 176;
   private static final int H = 176;
   private static final int BTN_W = 61;
   private static final int BTN_H = 16;
   private static final int FLECHA_W = 12;
   private static final int FLECHA_H = 10;
   private static final int[] COLX = new int[]{51, 87, 124};
   private static final int FLECHA_ARRIBA_Y = 56;
   private static final int FILA_Y = 70;
   private static final int FLECHA_ABAJO_Y = 87;
   private static final int ORO = -19456;
   private final BlockPos pos;
   private PantallaCajero.Modo modo = PantallaCajero.Modo.MENU;
   private final int[] cant = new int[3];
   private String mensaje = "";
   private boolean mensajeOk;
   private long mensajeHasta;
   private long bloqueadoHasta;
   private int mantenerCol = -1;
   private int mantenerDir;
   private int mantenerTicks;
   private double gs = 1.0;
   private int k = 1;
   private int px0;
   private int py0;
   private static final int BTN_MENU_Y = 132;
   private static final int BTN_OK_X = 57;
   private static final int BTN_OK_Y = 140;

   public PantallaCajero(BlockPos pos) {
      super(Component.literal("Cajero"));
      this.pos = pos;
   }

   public boolean isPauseScreen() {
      return false;
   }

   private void medir() {
      Window ventana = Minecraft.getInstance().getWindow();
      this.gs = ventana.getGuiScale();
      int kAlto = (int)Math.floor(0.8 * ventana.getHeight() / 176.0);
      int kAncho = (int)Math.floor(0.9 * ventana.getWidth() / 176.0);
      this.k = Math.max(1, Math.min(kAlto, kAncho));
      this.px0 = (ventana.getWidth() - 176 * this.k) / 2;
      this.py0 = (ventana.getHeight() - 176 * this.k) / 2;
   }

   private double localX(double mx) {
      return (mx * this.gs - this.px0) / this.k;
   }

   private double localY(double my) {
      return (my * this.gs - this.py0) / this.k;
   }

   private static boolean dentro(double x, double y, int rx, int ry, int rw, int rh) {
      return x >= rx && x < rx + rw && y >= ry && y < ry + rh;
   }

   private int btnIngresarX() {
      return 22;
   }

   private int btnSacarX() {
      return 92;
   }

   private int contarEnInventario(int i) {
      Player jugador = Minecraft.getInstance().player;
      if (jugador == null) {
         return 0;
      } else {
         Item item = CajeroServidor.fichas()[i];
         int n = 0;

         for (ItemStack s : jugador.getInventory().items) {
            if (s.is(item)) {
               n += s.getCount();
            }
         }

         for (ItemStack sx : jugador.getInventory().offhand) {
            if (sx.is(item)) {
               n += sx.getCount();
            }
         }

         return n;
      }
   }

   private int max(int i) {
      if (this.modo == PantallaCajero.Modo.SACAR) {
         long usado = 0L;

         for (int j = 0; j < 3; j++) {
            if (j != i) {
               usado += this.cant[j] * CajeroCliente.VALOR[j];
            }
         }

         long libre = CajeroCliente.saldo - usado;
         return libre <= 0L ? 0 : (int)Math.min(99L, libre / CajeroCliente.VALOR[i]);
      } else {
         return Math.min(99, this.contarEnInventario(i));
      }
   }

   private boolean cambiar(int col, int delta) {
      int antes = this.cant[col];
      this.cant[col] = Math.max(0, Math.min(this.max(col), antes + delta));
      return this.cant[col] != antes;
   }

   private void sonido(SoundEvent s, float tono) {
      Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(s, tono));
   }

   private void click() {
      Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI((SoundEvent)SoundEvents.UI_BUTTON_CLICK.value(), 1.0F));
   }

   private void ir(PantallaCajero.Modo nuevo) {
      this.modo = nuevo;
      Arrays.fill(this.cant, 0);
      this.mensaje = "";
      this.mantenerCol = -1;
   }

   private void confirmar() {
      if (System.currentTimeMillis() >= this.bloqueadoHasta) {
         if (this.cant[0] + this.cant[1] + this.cant[2] <= 0) {
            this.poner(false, "Elige una cantidad");
            this.sonido(SoundEvents.VILLAGER_NO, 1.0F);
         } else {
            this.bloqueadoHasta = System.currentTimeMillis() + 400L;
            ClientPlayNetworking.send(
               new CajeroRed.AccionPayload(this.pos, this.modo == PantallaCajero.Modo.INGRESAR, this.cant[0], this.cant[1], this.cant[2])
            );
         }
      }
   }

   private void poner(boolean ok, String texto) {
      this.mensaje = texto;
      this.mensajeOk = ok;
      this.mensajeHasta = System.currentTimeMillis() + 2500L;
   }

   public void recibirRespuesta(boolean ok, String clave) {
      String t = switch (clave) {
         case "retirado" -> "Retiro completado";
         case "ingresado" -> "Ingreso completado";
         case "saldo" -> "Saldo insuficiente";
         case "fichas" -> "No tienes esas deditas";
         case "vacio" -> "Elige una cantidad";
         case "lejos" -> "El cajero está muy lejos";
         case "lleno" -> "El saldo ya no puede crecer más";
         default -> clave;
      };
      this.poner(ok, t);
      if (ok) {
         Arrays.fill(this.cant, 0);
         this.sonido(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F);
      } else {
         this.sonido(SoundEvents.VILLAGER_NO, 1.0F);
      }
   }

   public void renderBackground(GuiGraphics g, int mouseX, int mouseY, float parcial) {
   }

   public void render(GuiGraphics g, int mx, int my, float pt) {
      g.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
      this.medir();
      double lx = this.localX(mx);
      double ly = this.localY(my);
      g.pose().pushPose();
      g.pose().translate((float)(this.px0 / this.gs), (float)(this.py0 / this.gs), 0.0F);
      float esc = (float)(this.k / this.gs);
      g.pose().scale(esc, esc, 1.0F);
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      g.blit(MARCO, 0, 0, 0.0F, 0.0F, 176, 176, 176, 176);

      String titulo = switch (this.modo) {
         case MENU -> "Cajero";
         case SACAR -> "Sacar Dinero";
         case INGRESAR -> "Ingresar Dinero";
      };
      this.texto(g, titulo, 88.0F, 7.0F, -1, 54.0F, 0.9F);
      if (this.modo == PantallaCajero.Modo.MENU) {
         g.blit(FICHAS, 46, 36, 84, 64, 0.0F, 0.0F, 42, 32, 42, 32);
         this.boton(g, this.btnIngresarX(), 132, "Ingresar Dinero", lx, ly);
         this.boton(g, this.btnSacarX(), 132, "Sacar Dinero", lx, ly);
      } else {
         this.dibujarSaldo(g);

         for (int i = 0; i < 3; i++) {
            this.columna(g, i, lx, ly);
         }

         g.fill(43, 108, 133, 130, -2013265920);
         if (!this.mensaje.isEmpty() && System.currentTimeMillis() < this.mensajeHasta) {
            this.texto(g, this.mensaje, 88.0F, 119.0F, this.mensajeOk ? -11141291 : -43691, 84.0F, 0.8F);
         }

         this.boton(g, 57, 140, this.modo == PantallaCajero.Modo.SACAR ? "Sacar" : "Ingresar", lx, ly);
      }

      g.pose().popPose();
   }

   private void texto(GuiGraphics g, String s, float cx, float cy, int color, float maxW, float escMax) {
      float w = this.font.width(s);
      float sc = Math.min(escMax, maxW / Math.max(1.0F, w));
      g.pose().pushPose();
      g.pose().translate(cx - w * sc / 2.0F, cy - 4.0F * sc, 0.0F);
      g.pose().scale(sc, sc, 1.0F);
      g.drawString(this.font, s, 0, 0, color, true);
      g.pose().popPose();
   }

   private void boton(GuiGraphics g, int x, int y, String texto, double lx, double ly) {
      boolean hover = dentro(lx, ly, x, y, 61, 16);
      g.blit(BOTON, x, y, 0.0F, hover ? 16.0F : 0.0F, 61, 16, 61, 32);
      this.texto(g, texto, x + 30.5F, y + 8.0F + 0.5F, hover ? -96 : -1, 50.0F, 0.8F);
   }

   private void dibujarSaldo(GuiGraphics g) {
      long[] p = CajeroCliente.partes(CajeroCliente.saldo);
      boolean[] ver = new boolean[]{p[0] > 0L, p[1] > 0L, p[2] > 0L};
      if (!ver[0] && !ver[1] && !ver[2]) {
         ver[2] = true;
      }

      float sc = 0.85F;
      String etiqueta = "Saldo:";
      float total = this.font.width(etiqueta) * sc + 4.0F;

      for (int i = 0; i < 3; i++) {
         if (ver[i]) {
            total += this.font.width(Long.toString(p[i])) * sc + 2.0F + 12.0F + 4.0F;
         }
      }

      total -= 4.0F;
      float x = 88.0F - total / 2.0F;
      float cy = 44.0F;
      this.textoIzq(g, etiqueta, x, cy, -19456, sc);
      x += this.font.width(etiqueta) * sc + 4.0F;

      for (int ix = 0; ix < 3; ix++) {
         if (ver[ix]) {
            String t = Long.toString(p[ix]);
            this.textoIzq(g, t, x, cy, -1, sc);
            x += this.font.width(t) * sc + 2.0F;
            g.blit(CajeroCliente.icono(ix), Math.round(x), Math.round(cy - 6.5F), 0.0F, 0.0F, 12, 13, 12, 13);
            x += 16.0F;
         }
      }
   }

   private void textoIzq(GuiGraphics g, String s, float x, float cy, int color, float sc) {
      g.pose().pushPose();
      g.pose().translate(x, cy - 4.0F * sc, 0.0F);
      g.pose().scale(sc, sc, 1.0F);
      g.drawString(this.font, s, 0, 0, color, true);
      g.pose().popPose();
   }

   private void columna(GuiGraphics g, int i, double lx, double ly) {
      int cx = COLX[i];
      boolean puedeSubir = this.cant[i] < this.max(i);
      boolean puedeBajar = this.cant[i] > 0;
      this.flecha(g, FLECHA_ARRIBA, cx - 6, 56, puedeSubir, lx, ly);
      this.flecha(g, FLECHA_ABAJO, cx - 6, 87, puedeBajar, lx, ly);
      g.blit(CajeroCliente.icono(i), cx - 12 - 1, 70, 0.0F, 0.0F, 12, 13, 12, 13);
      this.textoIzq(g, Integer.toString(this.cant[i]), cx + 3.0F, 77.0F, -1, 1.0F);
   }

   private void flecha(GuiGraphics g, ResourceLocation tex, int x, int y, boolean activa, double lx, double ly) {
      boolean hover = activa && dentro(lx, ly, x, y, 12, 10);
      float a = !activa ? 0.3F : (hover ? 1.0F : 0.85F);
      RenderSystem.enableBlend();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, a);
      g.blit(tex, x, y - (hover ? 1 : 0), 0.0F, 0.0F, 12, 10, 12, 10);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private int[] flechaBajo(double lx, double ly) {
      for (int i = 0; i < 3; i++) {
         int x = COLX[i] - 6;
         if (dentro(lx, ly, x, 56, 12, 10)) {
            return new int[]{i, 1};
         }

         if (dentro(lx, ly, x, 87, 12, 10)) {
            return new int[]{i, -1};
         }
      }

      return null;
   }

   public boolean mouseClicked(double mx, double my, int boton) {
      if (boton != 0) {
         return super.mouseClicked(mx, my, boton);
      } else {
         this.medir();
         double lx = this.localX(mx);
         double ly = this.localY(my);
         if (this.modo == PantallaCajero.Modo.MENU) {
            if (dentro(lx, ly, this.btnIngresarX(), 132, 61, 16)) {
               this.click();
               this.ir(PantallaCajero.Modo.INGRESAR);
               return true;
            } else if (dentro(lx, ly, this.btnSacarX(), 132, 61, 16)) {
               this.click();
               this.ir(PantallaCajero.Modo.SACAR);
               return true;
            } else {
               return super.mouseClicked(mx, my, boton);
            }
         } else {
            int[] f = this.flechaBajo(lx, ly);
            if (f != null) {
               if (this.cambiar(f[0], f[1] * (hasShiftDown() ? 10 : 1))) {
                  this.click();
               }

               this.mantenerCol = f[0];
               this.mantenerDir = f[1];
               this.mantenerTicks = 0;
               return true;
            } else if (dentro(lx, ly, 57, 140, 61, 16)) {
               this.click();
               this.confirmar();
               return true;
            } else {
               return super.mouseClicked(mx, my, boton);
            }
         }
      }
   }

   public boolean mouseReleased(double mx, double my, int boton) {
      this.mantenerCol = -1;
      return super.mouseReleased(mx, my, boton);
   }

   public boolean mouseScrolled(double mx, double my, double deltaX, double delta) {
      if (this.modo != PantallaCajero.Modo.MENU) {
         this.medir();
         double lx = this.localX(mx);
         double ly = this.localY(my);

         for (int i = 0; i < 3; i++) {
            if (dentro(lx, ly, COLX[i] - 17, 56, 34, 41)) {
               int paso = hasShiftDown() ? 10 : 1;
               this.cambiar(i, delta > 0.0 ? paso : -paso);
               return true;
            }
         }
      }

      return super.mouseScrolled(mx, my, deltaX, delta);
   }

   public void tick() {
      for (int i = 0; i < 3; i++) {
         if (this.cant[i] > this.max(i)) {
            this.cant[i] = this.max(i);
         }
      }

      if (this.mantenerCol >= 0) {
         this.mantenerTicks++;
         if (this.mantenerTicks > 8 && this.mantenerTicks % 2 == 0) {
            this.cambiar(this.mantenerCol, this.mantenerDir * (hasShiftDown() ? 10 : 1));
         }
      }
   }

   public boolean keyPressed(int tecla, int escaneo, int mods) {
      if (tecla == 256 && this.modo != PantallaCajero.Modo.MENU) {
         this.ir(PantallaCajero.Modo.MENU);
         return true;
      } else {
         return super.keyPressed(tecla, escaneo, mods);
      }
   }

   private static enum Modo {
      MENU,
      SACAR,
      INGRESAR;
   }
}
