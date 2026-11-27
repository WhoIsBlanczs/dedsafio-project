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
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Team;
import net.minecraft.world.scores.Team.Visibility;
import net.ruleta.client.compat.RenderNameTagEvent;
import net.ruleta.client.compat.SubscribeEvent;
import org.joml.Matrix4f;

public final class VidaNombre {
   private static final Style CORAZON = Style.EMPTY.withFont(new net.minecraft.network.chat.FontDescription.Resource(Identifier.fromNamespaceAndPath("ruleta", "vida"))).withColor(16777215);
   private static final int COLOR_TRAS_PARED = 553648127;

   private VidaNombre() {
   }

   @SubscribeEvent
   public static void alDibujarNombre(RenderNameTagEvent e) { }

   private static boolean seVeElNombre(Player jugador, double distancia) { return false; }
}
