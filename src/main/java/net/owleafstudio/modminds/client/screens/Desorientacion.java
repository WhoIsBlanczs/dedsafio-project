package net.owleafstudio.modminds.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.sounds.SoundSource;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.owleafstudio.modminds.init.ModmindsModSounds;

@Mod.EventBusSubscriber({Dist.CLIENT})
public final class Desorientacion {
   private static final double OIDO_TAPADO = 0.3;
   private static final int ZUMBIDO_CICLO_TICKS = 96;
   private static volatile long blancoInicio = 0L;
   private static volatile long blancoHasta = 0L;
   private static volatile long zumbidoDesde = 0L;
   private static volatile long zumbidoHasta = 0L;
   private static volatile Double volumenPrestado = null;
   private static long ultimoZumbido = 0L;

   private Desorientacion() {
   }

   public static void golpeDeLuz(int whiteTicks, int ringDelayTicks, int ringTicks) {
      long ahora = System.currentTimeMillis();
      blancoInicio = ahora;
      blancoHasta = Math.max(blancoHasta, ahora + whiteTicks * 50L);
      long desde = ahora + Math.max(0, ringDelayTicks) * 50L;
      zumbidoDesde = Math.max(zumbidoDesde, desde);
      zumbidoHasta = Math.max(zumbidoHasta, desde + ringTicks * 50L);
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public static void onRenderOverlay(RenderGuiEvent.Pre event) {
      long ahora = System.currentTimeMillis();
      if (ahora < blancoHasta) {
         Minecraft mc = Minecraft.getInstance();
         if (mc.player != null && mc.level != null) {
            double restante = (double)(blancoHasta - ahora) / Math.max(1L, blancoHasta - blancoInicio);
            int alpha = (int)(255.0 * Math.pow(Math.min(1.0, restante), 0.45));
            GuiGraphicsExtractor gui = event.getGuiGraphics();
            int w = event.getWindow().getGuiScaledWidth();
            int h = event.getWindow().getGuiScaledHeight();
            gui.fill(0, 0, w, h, alpha << 24 | 16054527);
         }
      }
   }

   @SubscribeEvent
   public static void onClientTick(TickEvent.ClientTickEvent event) {
      if (event.phase == TickEvent.Phase.END) {
         Minecraft mc = Minecraft.getInstance();
         long ahora = System.currentTimeMillis();
         if (volumenPrestado == null) {
            if (ahora >= zumbidoDesde && ahora < zumbidoHasta && mc.level != null) {
               tapar(mc);
            }
         } else if (ahora < zumbidoHasta && mc.level != null) {
            if (ahora - ultimoZumbido >= 4800L) {
               zumbido(mc);
            }
         } else {
            destapar(mc);
         }
      }
   }

   private static void tapar(Minecraft mc) {
      OptionInstance<Double> master = master(mc);
      if (master != null) {
         volumenPrestado = (Double)master.get();
         master.set(volumenPrestado * 0.3);
         ultimoZumbido = System.currentTimeMillis();
         zumbido(mc);
      }
   }

   private static void destapar(Minecraft mc) {
      Double prestado = volumenPrestado;
      volumenPrestado = null;
      OptionInstance<Double> master = master(mc);
      if (master != null && prestado != null) {
         if (Math.abs((Double)master.get() - prestado * 0.3) < 1.0E-6) {
            master.set(prestado);
         }
      }
   }

   private static void zumbido(Minecraft mc) {
      mc.getSoundManager().play(SimpleSoundInstance.forUI(ModmindsModSounds.ZUMBIDO.get(), 1.25F, 1.0F));
   }

   private static OptionInstance<Double> master(Minecraft mc) {
      return mc.options.getSoundSourceOptionInstance(SoundSource.MASTER);
   }
}
