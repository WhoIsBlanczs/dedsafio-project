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

   public boolean keyPressed(int tecla, int scan, int mods) {
      return true;
   }

   public boolean mouseClicked(double mx, double my, int boton) {
      return true;
   }

   public void render(GuiGraphicsExtractor g, int mx, int my, float parcial) {
      int w = this.width;
      int h = this.height;
      g.fill(0, 0, w, h, -16746281);
      float u = Math.min(h, w * 9.0F / 16.0F);
      float x = 0.137F * u;
      float tam = 0.033F * u;
      this.texto(g, FUENTE_CARA, 40.0F, ":(", x, 0.085F * u, 0.11F * u);
      this.texto(g, FUENTE, 11.0F, "Tu dispositivo se ha encontrado con un problema y necesita reiniciarse. Solo estamos", x, 0.326F * u, tam);
      this.texto(g, FUENTE, 11.0F, "recopilando información del error, y luego reiniciaremos por ti.", x, 0.366F * u, tam);
      int pct = Math.min(100, (int)(progreso() * 100.0F));
      this.texto(g, FUENTE, 11.0F, pct + "% completado", x, 0.435F * u, tam);
      this.texto(g, FUENTE, 11.0F, "Para más información sobre este problema y posibles soluciones, visita", x, 0.505F * u, tam);
      this.texto(g, FUENTE, 11.0F, "https://www.windows.com/stopcode", x, 0.546F * u, tam);
      int lado = Math.round(0.19F * u);
      g.blit(QR, Math.round(x), Math.round(0.61F * u), lado, lado, 0.0F, 0.0F, 31, 31, 31, 31);
      float xSoporte = x + lado + 0.036F * u;
      this.texto(g, FUENTE, 11.0F, "Si llamas a soporte, dales esta información:", xSoporte, 0.635F * u, tam);
      this.texto(g, FUENTE, 11.0F, "Código de detención: CRITICAL_PROCESS_DIED", xSoporte, 0.68F * u, tam);
   }

   private void texto(GuiGraphicsExtractor g, Identifier fuente, float tamFuente, String linea, float x, float y, float alto) {
      float escala = alto / tamFuente;
      g.pose().pushPose();
      g.pose().translate(x, y, 0.0F);
      g.pose().scale(escala, escala, 1.0F);
      g.drawString(this.font, Component.literal(linea).withStyle(Style.EMPTY.withFont(fuente)), 0, 0, -1, false);
      g.pose().popPose();
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

      private static void cortarCreeper(Minecraft mc) {
         mc.getSoundManager().stop(((SoundEvent)SoundEvents.GENERIC_EXPLODE.value()).getLocation(), null);
         mc.getSoundManager().stop(SoundEvents.CREEPER_PRIMED.getLocation(), null);
      }

      private static boolean esCreeper(SoundEvent e, SoundInstance s) {
         return s.getLocation().equals(((SoundEvent)SoundEvents.GENERIC_EXPLODE.value()).getLocation())
            || s.getLocation().equals(SoundEvents.CREEPER_PRIMED.getLocation());
      }

      @SubscribeEvent
      public static void alTerminarTick(TickEvent.ClientTickEvent evento) {
         if (evento.phase == TickEvent.Phase.END) {
            Minecraft mc = Minecraft.getInstance();
            if (sinExplosion > 0) {
               sinExplosion--;
               cortarCreeper(mc);
            }

            if (mc.level != null) {
               long ahora = mc.level.getGameTime();
               ROJOS.removeIf(r -> ahora - (long)r[3] > 600L);
               Map<Integer, double[]> ahoraVistos = new HashMap<>();

               for (Entity en : mc.level.entitiesForRendering()) {
                  if (en instanceof CreeperRojoEntity c) {
                     double[] pos = new double[]{c.getX(), c.getY(), c.getZ(), ahora};
                     ahoraVistos.put(c.getId(), pos);
                     if (c.getSwelling(1.0F) > 0.2F) {
                        ROJOS.add(pos);
                     }
                  }
               }

               for (Entry<Integer, double[]> v : VISTOS.entrySet()) {
                  if (!ahoraVistos.containsKey(v.getKey())) {
                     double[] pos = v.getValue();
                     ROJOS.add(new double[]{pos[0], pos[1], pos[2], ahora});
                  }
               }

               VISTOS.clear();
               VISTOS.putAll(ahoraVistos);
            }

            LocalPlayer jugador = mc.player;
            boolean activa = jugador != null && jugador.isAlive() && jugador.hasEffect(Efectos.PANTALLA_AZUL.get());
            if (activa) {
               if (!silenciado) {
                  cortarCreeper(mc);
                  mc.getSoundManager().pause();
                  silenciado = true;
                  mc.getSoundManager().play(SimpleSoundInstance.forUI(PANTALLAZO, 1.0F, 1.0F));
               }

               if (!(mc.screen instanceof PantallaAzul)) {
                  mc.setScreenAndShow(new PantallaAzul());
               }
            } else {
               if (mc.screen instanceof PantallaAzul) {
                  mc.setScreenAndShow(null);
               }

               if (silenciado) {
                  silenciado = false;
                  cortarCreeper(mc);
                  mc.getSoundManager().resume();
                  cortarCreeper(mc);
                  sinExplosion = 200;
               }
            }
         }
      }

      @SubscribeEvent
      public static void alSonar(PlaySoundEvent evento) {
         if (evento.getSound() != null) {
            if (silenciado) {
               if (!evento.getSound().getLocation().equals(PANTALLAZO.getLocation())) {
                  evento.setSound(null);
               }
            } else {
               boolean explosion = esCreeper(null, evento.getSound());
               if (explosion && (sinExplosion > 0 || esDeRojo(evento.getSound()))) {
                  evento.setSound(null);
               }
            }
         }
      }
   }
}
