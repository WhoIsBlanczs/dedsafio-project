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

   public static void dibujar(GuiGraphicsExtractor g) {
      Minecraft mc = Minecraft.getInstance();
      LocalPlayer jugador = mc.player;
      if (jugador != null && !jugador.getActiveEffects().isEmpty()) {
         if (!(mc.screen instanceof EffectRenderingInventoryScreen<?> inv && inv.canSeeEffects())) {
            List<MobEffectInstance> buenos = new ArrayList<>();
            List<MobEffectInstance> malos = new ArrayList<>();

            for (MobEffectInstance e : jugador.getActiveEffects()) {
               if (e.showIcon()) {
                  (((MobEffect)e.getEffect().value()).isBeneficial() ? buenos : malos).add(e);
               }
            }

            List<MobEffectInstance> todos = new ArrayList<>(buenos);
            todos.addAll(malos);
            if (!todos.isEmpty()) {
               int ancho = mc.getWindow().getGuiScaledWidth();
               int alto = mc.getWindow().getGuiScaledHeight();
               int limite = ancho / 2 - 91 - 6;
               int porFila = Math.max(1, (limite - 2) / 25);

               for (int i = 0; i < todos.size(); i++) {
                  MobEffectInstance e = todos.get(i);
                  int x = 2 + i % porFila * 25;
                  int y = alto - 24 - 2 - i / porFila * 25;
                  float alfa = 1.0F;
                  if (!e.isInfiniteDuration() && e.endsWithin(200)) {
                     int d = e.getDuration();
                     int k = 10 - d / 20;
                     alfa = Mth.clamp(d / 10.0F / 5.0F * 0.5F, 0.0F, 0.5F) + Mth.cos(d * (float) Math.PI / 5.0F) * Mth.clamp(k / 10.0F * 0.25F, 0.0F, 0.25F);
                     alfa = Mth.clamp(alfa, 0.0F, 1.0F);
                  }

                  g.setColor(1.0F, 1.0F, 1.0F, alfa);
                  g.blitSprite(e.isAmbient() ? FONDO_AMBIENTE : FONDO, x, y, 24, 24);
                  TextureAtlasSprite sprite = mc.getMobEffectTextures().get(e.getEffect());
                  g.blit(x + 3, y + 3, 0, 18, 18, sprite);
                  g.setColor(1.0F, 1.0F, 1.0F, 1.0F);
               }
            }
         }
      }
   }
}
