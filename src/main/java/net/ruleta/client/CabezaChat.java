package net.ruleta.client;

import com.mojang.blaze3d.platform.NativeImage;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.Identifier;
import org.lwjgl.opengl.GL11;

public final class CabezaChat {
   private static final Identifier FUENTE = Identifier.fromNamespaceAndPath("ruleta", "cabeza");
   private static final Map<Identifier, int[]> CACHE = new HashMap<>();

   private CabezaChat() {
   }

   public static Component para(Component mensaje) {
      if (mensaje != null && mensaje.getContents() instanceof TranslatableContents tr && tr.getKey().startsWith("death.")) {
         Object[] args = tr.getArgs();
         if (args.length == 0) {
            return null;
         }

         String texto = args[0] instanceof Component c ? c.getString() : String.valueOf(args[0]);
         ClientPacketListener con = Minecraft.getInstance().getConnection();
         if (con == null) {
            return null;
         }

         PlayerInfo info = null;
         int mejor = 0;

         for (PlayerInfo pi : con.getOnlinePlayers()) {
            String n = pi.getProfile().name();
            if (texto.contains(n) && n.length() > mejor) {
               info = pi;
               mejor = n.length();
            }
         }

         if (info == null) {
            return null;
         }

         int[] cara = caraDe(info.getSkin().texture());
         if (cara == null) {
            return null;
         }

         MutableComponent r = Component.empty();

         for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
               String s = "" + (char)(57600 + y) + (char)(y == 7 ? 57617 : 57616);
               r.append(Component.literal(s).setStyle(Style.EMPTY.withFont(new net.minecraft.network.chat.FontDescription.Resource(FUENTE)).withColor(TextColor.fromRgb(cara[y * 8 + x]))));
            }
         }

         return r.append(Component.literal(" "));
      } else {
         return null;
      }
   }

   private static int[] caraDe(Identifier skin) {
      int[] guardada = CACHE.get(skin);
      if (guardada != null) {
         return guardada;
      }

      try {
         AbstractTexture tex = Minecraft.getInstance().getTextureManager().getTexture(skin);
         tex.bind();
         int w = GL11.glGetTexLevelParameteri(3553, 0, 4096);
         int h = GL11.glGetTexLevelParameteri(3553, 0, 4097);
         if (w >= 64 && h >= 64 && w <= 4096 && h <= 4096) {
            int[] r = new int[64];
            NativeImage img = new NativeImage(w, h, false);

            try {
               img.downloadTexture(0, false);
               double u = w / 64.0;
               double v = h / 64.0;

               for (int j = 0; j < 8; j++) {
                  for (int i = 0; i < 8; i++) {
                     int base = img.getPixel((int)((8 + i + 0.5) * u), (int)((8 + j + 0.5) * v));
                     int sombrero = img.getPixel((int)((40 + i + 0.5) * u), (int)((8 + j + 0.5) * v));
                     int p = sombrero >>> 24 >= 128 ? sombrero : base;
                     r[j * 8 + i] = (p & 0xFF) << 16 | (p >> 8 & 0xFF) << 8 | p >> 16 & 0xFF;
                  }
               }
            } catch (Throwable var17) {
               try {
                  img.close();
               } catch (Throwable var16) {
                  var17.addSuppressed(var16);
               }

               throw var17;
            }

            img.close();
            CACHE.put(skin, r);
            return r;
         } else {
            return null;
         }
      } catch (Throwable t) {
         return null;
      }
   }
}
