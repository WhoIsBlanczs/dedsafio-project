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
   public static void alChat(RenderGuiEvent.Pre e) {
      try {
         ChatComponent chat = Minecraft.getInstance().gui.getChat();
         if (!buscado) {
            buscado = true;

            for (Field f : ChatComponent.class.getDeclaredFields()) {
               if (List.class.isAssignableFrom(f.getType())
                  && f.getGenericType() instanceof ParameterizedType pt
                  && pt.getActualTypeArguments().length == 1
                  && pt.getActualTypeArguments()[0] == Line.class) {
                  f.setAccessible(true);
                  lineas = f;
                  break;
               }
            }
         }

         if (lineas == null) {
            return;
         }

         List<Line> lista = (List<Line>)lineas.get(chat);

         for (int i = 0; i < lista.size(); i++) {
            Line ln = lista.get(i);
            FormattedCharSequence c = ln.content();
            if (!(c instanceof ChatBrillo.Titulo) && !VISTAS.containsKey(c)) {
               VISTAS.put(c, Boolean.TRUE);
               ChatBrillo.Titulo t = ChatBrillo.Titulo.de(c);
               if (t != null) {
                  lista.set(i, new Line(ln.addedTime(), t, ln.tag(), ln.endOfEntry()));
               }
            }
         }
      } catch (Throwable var8) {
         lineas = null;
      }
   }

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

      static ChatBrillo.Titulo de(FormattedCharSequence c) {
         List<int[]> cps = new ArrayList<>();
         List<Style> estilos = new ArrayList<>();
         c.accept((i, estilo, cpx) -> {
            cps.add(new int[]{cpx});
            estilos.add(estilo);
            return true;
         });
         if (!cps.isEmpty() && cps.get(0)[0] == 57344) {
            int base = Mensajes.baseDeFuente(estilos.get(0).getFont());
            if (base < 0) {
               return null;
            } else {
               int[] cp = new int[cps.size()];

               for (int k = 0; k < cp.length; k++) {
                  cp[k] = cps.get(k)[0];
               }

               return new ChatBrillo.Titulo(cp, estilos.toArray(new Style[0]), base, Math.max(1, cp.length - 2));
            }
         } else {
            return null;
         }
      }

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
