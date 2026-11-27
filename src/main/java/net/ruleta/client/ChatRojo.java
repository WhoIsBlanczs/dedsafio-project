package net.ruleta.client;

import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.Identifier;
import net.ruleta.client.compat.ClientChatReceivedEvent;
import net.ruleta.client.compat.SubscribeEvent;

public final class ChatRojo {
   private static Component esperado;
   private static long hasta;
   private static final Style ICONO = Style.EMPTY.withFont(new net.minecraft.network.chat.FontDescription.Resource(Identifier.fromNamespaceAndPath("ruleta", "icono")));
   private static final int MENTA = 4971418;

   private ChatRojo() {
   }

   public static void esperar(Component mensaje) {
      esperado = mensaje;
      hasta = System.currentTimeMillis() + 3000L;
   }

   private static boolean entradaSalida(ClientChatReceivedEvent e) {
      Component m = e.getMessage();
      if (m != null && m.getContents() instanceof TranslatableContents tr && tr.getKey().startsWith("multiplayer.player.")) {
         String k = tr.getKey();
         if (k.equals("multiplayer.player.joined") || k.equals("multiplayer.player.joined.renamed") || k.equals("multiplayer.player.left")) {
            Component msg = m;
            Object[] args = tr.getArgs();
            if (args.length > 0 && args[0] instanceof Component nombre) {
               Object[] nuevos = (Object[])args.clone();
               nuevos[0] = nombre.copy().setStyle(nombre.getStyle().withColor(TextColor.fromRgb(4971418)));
               MutableComponent c = Component.translatable(k, nuevos).setStyle(m.getStyle());

               for (Component hijo : m.getSiblings()) {
                  c.append(hijo);
               }

               msg = c;
            }

            e.setMessage(Component.empty().append(Component.literal("\ue000").setStyle(ICONO)).append(Component.literal(" ")).append(msg));
            return true;
         }
      }

      return false;
   }

   private static void nombreMenta(ClientChatReceivedEvent e) {
      Component m = e.getMessage();
      if (m != null && m.getContents() instanceof TranslatableContents tr) {
         String k = tr.getKey();
         if (k.equals("chat.type.text")
            || k.equals("chat.type.emote")
            || k.equals("chat.type.announcement")
            || k.equals("chat.type.team.text")
            || k.equals("chat.type.team.sent")) {
            Object[] args = tr.getArgs();
            int pos = k.startsWith("chat.type.team") ? (k.equals("chat.type.team.text") ? 1 : 1) : 0;
            if (args.length > pos && args[pos] instanceof Component nombre) {
               Object[] nuevos = (Object[])args.clone();
               nuevos[pos] = nombre.copy().setStyle(nombre.getStyle().withColor(TextColor.fromRgb(4971418)));
               MutableComponent nuevo = Component.translatable(k, nuevos).setStyle(m.getStyle());

               for (Component hijo : m.getSiblings()) {
                  nuevo.append(hijo);
               }

               e.setMessage(nuevo);
            }
         }
      }
   }

   private static boolean letraNombre(char c) {
      return Character.isLetterOrDigit(c) || c == '_';
   }

   private static void nombreMentaTexto(ClientChatReceivedEvent e) { }

   @SubscribeEvent
   public static void alLlegarChat(ClientChatReceivedEvent e) {
      if (!entradaSalida(e)) {
         nombreMenta(e);
         nombreMentaTexto(e);
         Component original = e.getMessage();
         ponerRojo(e);
         Component cabeza = CabezaChat.para(original);
         if (cabeza != null) {
            e.setMessage(Component.empty().append(cabeza).append(e.getMessage()));
         }
      }
   }

   private static void ponerRojo(ClientChatReceivedEvent e) {
      if (esperado != null && e.isSystem()) {
         if (System.currentTimeMillis() > hasta) {
            esperado = null;
         } else {
            Component m = e.getMessage();
            if (m != null && (m.equals(esperado) || m.getString().equals(esperado.getString()))) {
               e.setMessage(Component.empty().append(m).withStyle(ChatFormatting.RED));
               esperado = null;
            }
         }
      }
   }
}
