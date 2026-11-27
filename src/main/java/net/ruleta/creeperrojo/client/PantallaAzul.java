package net.ruleta.creeperrojo.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.ruleta.Efectos;
import net.ruleta.client.compat.PlaySoundEvent;
import net.ruleta.client.compat.SubscribeEvent;
import net.ruleta.client.compat.TickEvent;
import net.ruleta.creeperrojo.CreeperRojoEntity;

public class PantallaAzul extends Screen {
   private static final int AZUL = -16746281;
   private static final int BLANCO = -1;
   private static final Identifier FUENTE = Identifier.fromNamespaceAndPath("ruleta", "bsod");
   private static final Identifier FUENTE_CARA = Identifier.fromNamespaceAndPath("ruleta", "bsod_cara");
   private static final float TAM_FUENTE = 11.0F;
   private static final float TAM_FUENTE_CARA = 40.0F;
   private static final Identifier QR = Identifier.fromNamespaceAndPath("ruleta", "textures/gui/bsod_qr.png");
   private static final int LADO_QR = 31;

   public PantallaAzul() {
      super(Component.literal("Error fatal"));
   }

   static float progreso() {
      LocalPlayer jugador = Minecraft.getInstance().player;
      if (jugador == null) {
         return 1.0F;
      }

      MobEffectInstance efecto = jugador.getEffect(Efectos.PANTALLA_AZUL.get());
      return efecto == null ? 1.0F : Mth.clamp(1.0F - efecto.getDuration() / 400.0F, 0.0F, 1.0F);
   }

   public boolean isPauseScreen() {
      return false;
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public boolean keyPressed(net.minecraft.client.input.KeyEvent ev_) {
      int tecla = ev_.key(); int scan = 0; int mods = ev_.modifiers();
      return true;
   }

   public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent ev_, boolean dbl_) {
      double mx = ev_.x(); double my = ev_.y(); int boton = ev_.button();
      return true;
   }

   public void extractRenderState(GuiGraphicsExtractor g, int mx, int my, float parcial) { }

   private void texto(GuiGraphicsExtractor g, Identifier fuente, float tamFuente, String linea, float x, float y, float alto) {
      float escala = alto / tamFuente;
      g.pose().pushMatrix();
      g.pose().translate(x, y);
      g.pose().scale(escala, escala);
      g.text(this.font, Component.literal(linea).withStyle(Style.EMPTY.withFont(new net.minecraft.network.chat.FontDescription.Resource(fuente))), 0, 0, -1, false);
      g.pose().popMatrix();
   }

   public static final class Control {
      private static final SoundEvent PANTALLAZO = SoundEvent.createVariableRangeEvent(
         Identifier.fromNamespaceAndPath("ruleta", "creeper_rojo.pantallazo")
      );
      private static boolean silenciado;
      private static int sinExplosion;
      private static final List<double[]> ROJOS = new ArrayList<>();
      private static final Map<Integer, double[]> VISTOS = new HashMap<>();

      private static boolean esDeRojo(SoundInstance s) {
         for (double[] r : ROJOS) {
            double dx = s.getX() - r[0];
            double dy = s.getY() - r[1];
            double dz = s.getZ() - r[2];
            if (dx * dx + dy * dy + dz * dz < 144.0) {
               return true;
            }
         }

         return false;
      }

      private Control() {
      }

      private static void cortarCreeper(Minecraft mc) { }

      private static boolean esCreeper(SoundEvent e, SoundInstance s) { return false; }

      @SubscribeEvent
      public static void alTerminarTick(TickEvent.ClientTickEvent evento) { }

      @SubscribeEvent
      public static void alSonar(PlaySoundEvent evento) { }
   }
}
