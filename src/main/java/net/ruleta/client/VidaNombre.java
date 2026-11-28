package net.ruleta.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Team;
import net.minecraft.world.scores.Team.Visibility;
import net.ruleta.client.compat.RenderNameTagEvent;
import net.ruleta.client.compat.SubscribeEvent;
import org.joml.Matrix4f;

public final class VidaNombre {
   private static final Style CORAZON = Style.EMPTY.withFont(ResourceLocation.fromNamespaceAndPath("ruletafanproject", "vida")).withColor(16777215);
   private static final int COLOR_TRAS_PARED = 553648127;

   private VidaNombre() {
   }

   @SubscribeEvent
   public static void alDibujarNombre(RenderNameTagEvent e) {
      if (e.getEntity() instanceof Player jugador) {
         Minecraft mc = Minecraft.getInstance();
         EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
         double distancia = dispatcher.distanceToSqr(jugador);
         if (seVeElNombre(jugador, distancia)) {
            int vida = Math.max(0, (int)Math.ceil(jugador.getHealth()));
            Component linea = Component.literal(Integer.toString(vida))
               .withStyle(ChatFormatting.WHITE)
               .append(Component.literal(" "))
               .append(Component.literal("\ue000").setStyle(CORAZON));
            boolean hayPuntuacion = distancia < 100.0 && jugador.getScoreboard().getDisplayObjective(DisplaySlot.BELOW_NAME) != null;
            float y = -10.0F - (hayPuntuacion ? 10.349999F : 0.0F);
            PoseStack pose = e.getPoseStack();
            pose.pushPose();
            pose.translate(0.0F, jugador.getBbHeight() + 0.5F, 0.0F);
            pose.mulPose(dispatcher.cameraOrientation());
            pose.scale(-0.025F, -0.025F, 0.025F);
            Matrix4f matriz = pose.last().pose();
            int fondo = (int)(mc.options.getBackgroundOpacity(0.25F) * 255.0F) << 24;
            Font fuente = e.getEntityRenderer().getFont();
            float x = -fuente.width(linea) / 2.0F;
            boolean atravesarParedes = !jugador.isDiscrete();
            fuente.drawInBatch(
               linea,
               x,
               y,
               553648127,
               false,
               matriz,
               e.getMultiBufferSource(),
               atravesarParedes ? DisplayMode.SEE_THROUGH : DisplayMode.NORMAL,
               fondo,
               e.getPackedLight()
            );
            if (atravesarParedes) {
               fuente.drawInBatch(linea, x, y, -1, false, matriz, e.getMultiBufferSource(), DisplayMode.NORMAL, 0, e.getPackedLight());
            }

            pose.popPose();
         }
      }
   }

   private static boolean seVeElNombre(Player jugador, double distancia) {
      Minecraft mc = Minecraft.getInstance();
      LocalPlayer yo = mc.player;
      if (yo == null) {
         return false;
      }

      float maxima = jugador.isDiscrete() ? 32.0F : 64.0F;
      if (distancia >= maxima * maxima) {
         return false;
      }

      boolean noInvisible = !jugador.isInvisibleTo(yo);
      if (jugador != yo) {
         Team suyo = jugador.getTeam();
         Team mio = yo.getTeam();
         if (suyo != null) {
            Visibility vis = suyo.getNameTagVisibility();
            if (vis == Visibility.ALWAYS) {
               return noInvisible;
            }

            if (vis == Visibility.NEVER) {
               return false;
            }

            if (vis == Visibility.HIDE_FOR_OTHER_TEAMS) {
               return mio == null ? noInvisible : suyo.isAlliedTo(mio) && (suyo.canSeeFriendlyInvisibles() || noInvisible);
            }

            if (vis != Visibility.HIDE_FOR_OWN_TEAM) {
               return true;
            }

            return mio == null ? noInvisible : !suyo.isAlliedTo(mio) && noInvisible;
         }
      }

      return Minecraft.renderNames() && jugador != mc.getCameraEntity() && noInvisible && !jugador.isVehicle();
   }
}
