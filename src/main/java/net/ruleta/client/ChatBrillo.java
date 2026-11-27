package net.ruleta.client;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.GuiMessage.Line;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.FormattedCharSink;
import net.ruleta.Mensajes;
import net.ruleta.client.compat.RenderGuiEvent;
import net.ruleta.client.compat.SubscribeEvent;

public final class ChatBrillo {
   private static Field lineas;
   private static boolean buscado;
   private static final Map<FormattedCharSequence, Boolean> VISTAS = new WeakHashMap<>();

   private ChatBrillo() {
   }

   @SubscribeEvent
   public static void alChat(RenderGuiEvent.Pre e) { }

   private static final class Titulo implements FormattedCharSequence {
      private final int[] cps;
      private final Style[] estilos;
      private final int base;
      private final int n;

      private Titulo(int[] cps, Style[] estilos, int base, int n) {
         this.cps = cps;
         this.estilos = estilos;
         this.base = base;
         this.n = n;
      }

      static ChatBrillo.Titulo de(FormattedCharSequence c) { return null; }

      public boolean accept(FormattedCharSink sink) {
         for (int k = 0; k < this.cps.length; k++) {
            Style st = this.estilos[k];
            if (this.cps[k] < 57344 && this.cps[k] != 32) {
               float u = Math.min(1.0F, (float)(k - 2) / this.n);
               st = st.withColor(Mensajes.brillo(this.base, Math.max(0.0F, u)));
            }

            if (!sink.accept(k, st, this.cps[k])) {
               return false;
            }
         }

         return true;
      }
   }
}
