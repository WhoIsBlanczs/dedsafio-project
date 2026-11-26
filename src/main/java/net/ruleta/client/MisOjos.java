package net.ruleta.client;

import com.mojang.blaze3d.shaders.Object;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.material.FogType;
import net.ruleta.Efectos;
import net.ruleta.client.compat.ClientPlayerNetworkEvent;
import net.ruleta.client.compat.RenderGuiEvent;
import net.ruleta.client.compat.SubscribeEvent;
import net.ruleta.client.compat.ViewportEvent;
import net.ruleta.client.escenas.MomentoNutria;

public final class MisOjos {
   private static final Identifier FILTRO = Identifier.fromNamespaceAndPath("ruleta", "shaders/post/daltonismo.json");
   private static final float VISTA_MIOPE = 12.0F;
   private static final Fundido niebla = new Fundido(1.0F, 3.0F);
   private static float nieblaK;
   private static final FiltroPantalla FILTRO_DALTONICO = new FiltroPantalla(FILTRO);
   private static int ancho;
   private static int alto;
   private static boolean fallo;

   private MisOjos() {
   }

   private static boolean tiene(Holder<MobEffect> efecto) {
      LocalPlayer jugador = Minecraft.getInstance().player;
      return jugador != null && jugador.hasEffect(efecto);
   }

   private static boolean miope() {
      return tiene(Efectos.MIOPIA.get());
   }

   private static boolean daltonico() {
      return tiene(Efectos.DALTONISMO.get());
   }

   @SubscribeEvent
   public static void alPonerNiebla(ViewportEvent.RenderFog e) { }

   @SubscribeEvent
   public static void alDibujarGui(RenderGuiEvent.Pre e) {
      Minecraft mc = Minecraft.getInstance();
      MomentoNutria.filtro(e.getPartialTick());
      nieblaK = niebla.avanzar(mc.level != null && miope());
      if (mc.level != null && daltonico()) {
         if (FILTRO_DALTONICO.preparar()) {
            FILTRO_DALTONICO.fijar("Tiempo", (float)(System.nanoTime() / 1.0E9 % 3600.0));
            FILTRO_DALTONICO.fijar("Fuerza", 1.0F);
            FILTRO_DALTONICO.pasar(e.getPartialTick());
         }
      } else {
         cerrar();
      }
   }

   public static boolean ocultarCorazones() {
      return miope() || daltonico() || MomentosCliente.vidaOculta();
   }

   @SubscribeEvent
   public static void alSalir(ClientPlayerNetworkEvent.LoggingOut e) {
      niebla.cero();
      nieblaK = 0.0F;
      FILTRO_DALTONICO.reiniciar();
   }

   private static void cerrar() {
      FILTRO_DALTONICO.cerrar();
   }
}
