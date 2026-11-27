package net.ruleta.client;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

public final class EfectosHud {
   private static final Identifier FONDO = Identifier.withDefaultNamespace("hud/effect_background");
   private static final Identifier FONDO_AMBIENTE = Identifier.withDefaultNamespace("hud/effect_background_ambient");
   private static final int CELDA = 24;
   private static final int SEPARACION = 1;
   private static final int MARGEN = 2;

   private EfectosHud() {
   }

   public static void dibujar(GuiGraphicsExtractor g) { }
}
