package net.ruleta.client.ruleta;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.ruleta.Ruleta;
import net.ruleta.RuletaColor;
import net.ruleta.RuletaPayload;
import net.ruleta.client.OpcionesCliente;
import org.joml.Matrix4f;

public final class RuletaHud {
   private static final SoundEvent SONIDO = SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("ruleta", "ruleta"));
   private static final String[] COLORES = new String[]{"rojo", "rosa", "morado", "azul", "cian", "verde", "amarillo", "naranja"};
   private static final float CX0 = -86.0F;
   private static final float CY0 = -216.0F;
   private static final float CX1 = 86.0F;
   private static final float CY1 = 6.0F;
   private static final float PUNTERO_Y = -228.0F;
   private static final float Z = 2000.0F;
   private static final float RESPLANDOR_DESTELLOS = 0.3F;
   private static final float RESPLANDOR_HALO = 0.8F;
   private static final float GEMA_ESCALA = 1.0F;
   private static final boolean DIBUJAR_SEPARADORES = false;
   private static final float GEMA_Y = -8.125F;
   private static Tirada actual;
   private static SoundInstance sonido;
   private static final Map<RuletaColor, Identifier> ICONOS = new EnumMap<>(RuletaColor.class);

   private RuletaHud() {
   }

   public static void shader(Object s) {
      Pintor.shader(s);
   }

   public static void empezar(RuletaPayload p) {
      if (OpcionesCliente.ruleta) {
         parar();
         actual = new Tirada(p);
         sonido = SimpleSoundInstance.forUI(SONIDO, 1.0F, 1.0F);
         Minecraft.getInstance().getSoundManager().play(sonido);
         Ruleta.LOG.info("[ruleta] empieza la tirada ({})", actual.resultado.id);
      }
   }

   public static void parar() {
      actual = null;
      if (sonido != null) {
         Minecraft.getInstance().getSoundManager().stop(sonido);
         sonido = null;
      }
   }

   public static boolean girando() {
      if (actual != null && actual.terminada()) {
         actual = null;
      }

      return actual != null;
   }

   public static void precargar() {
      for (String c : COLORES) {
         Pintor.textura("casilla_" + c);
         Pintor.textura("destellos_" + c);
         Pintor.textura("resplandor_" + c);
      }

      for (String n : new String[]{
         "aro",
         "bombilla",
         "halo",
         "brillo",
         "disco",
         "centro",
         "centro_brillo",
         "puntero",
         "puntero_blanco",
         "gema",
         "gema_brillo",
         "gema_luz",
         "gema_vetas_0",
         "gema_vetas_1",
         "gema_vetas_2",
         "gema_franja_0",
         "gema_franja_1",
         "gema_franja_2",
         "resplandor",
         "linea",
         "separador"
      }) {
         Pintor.textura(n);
      }
   }

   public static void dibujar(GuiGraphicsExtractor ctx) {
      if (girando()) {
         dibujar(ctx, actual);
      }
   }

   static void dibujar(GuiGraphicsExtractor ctx, Tirada tr) { }

   private static void dibujarRuleta(PoseStack pose, Tirada tr, float t, float cx, float cy, float px) { }

   public static void casillaLanzador(Matrix4f m, int i, float luz) {
      Pintor.empezar();

      try {
         String c = COLORES[Math.floorMod(i, 8)];
         Pintor.rect(m, "casilla_" + c, -86.0F, -216.0F, 86.0F, 6.0F, -1);
         Pintor.rect(m, "destellos_" + c, -86.0F, -216.0F, 86.0F, 6.0F, -1);
         if (luz > 0.003F) {
            Pintor.rectSuma(m, "resplandor_" + c, -86.0F, -216.0F, 86.0F, 6.0F, Mates.argb(luz * 0.9F, 16777215));
            Pintor.rectSuma(m, "casilla_" + c, -86.0F, -216.0F, 86.0F, 6.0F, Mates.argb(luz * 0.3F, 16777215));
         }
      } finally {
         Pintor.terminar();
      }
   }

   private static float respiro(float t, int i) {
      return 0.86F + 0.14F * Mth.sin(t * 2.6F + i * 0.8F);
   }

   public static void icono(GuiGraphicsExtractor ctx, int x, int y, int lado, RuletaColor color) { }
}
