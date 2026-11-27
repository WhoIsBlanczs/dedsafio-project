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

   public static Component para(Component mensaje) { return null; }

   private static int[] caraDe(Identifier skin) { return null; }
}
