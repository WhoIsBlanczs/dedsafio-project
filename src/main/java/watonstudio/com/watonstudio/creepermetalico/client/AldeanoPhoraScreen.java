package com.watonstudio.creepermetalico.client;

import com.watonstudio.creepermetalico.AldeanoPhoraEntity;
import com.watonstudio.creepermetalico.net.TradePhoraC2S;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

@Environment(EnvType.CLIENT)
public final class AldeanoPhoraScreen extends Screen {
   private static final int W = 1240;
   private static final int H = 770;
   private static final ResourceLocation BG = id("fondo.png");
   private static final ResourceLocation DARK = id("fondo_oscuro.png");
   private static final ResourceLocation ROW_WHITE = id("fila_blanca.png");
   private static final ResourceLocation ROW_RED = id("fila_roja.png");
   private static final ResourceLocation LEVEL_FULL = id("barra_nivel_llena.png");
   private static final ResourceLocation LEVEL_EMPTY = id("barra_nivel_vacia.png");
   private static final ResourceLocation GREEN_FULL = id("barra_verde_llena.png");
   private static final ResourceLocation GREEN_TRACK = id("barra_verde_pista.png");
   private static final ResourceLocation PERCENT = id("caja_porcentaje.png");
   private final int entityId;
   private final List<AldeanoPhoraScreen.OfferView> offers = new ArrayList<>();
   private int selected;
   private float scale = 1.0F;
   private int guiX;
   private int guiY;

   private static ResourceLocation id(String name) {
      return ResourceLocation.fromNamespaceAndPath("watonstudio", "textures/gui/phora/" + name);
   }

   public AldeanoPhoraScreen(int entityId, List<String> encodedOffers) {
      super(Component.literal("Aldeano Phora de Recursos"));
      this.entityId = entityId;

      for (String encoded : encodedOffers) {
         try {
            String[] p = encoded.split("\\|", -1);
            if (p.length >= 7) {
               ItemStack first = decodeStack(p[0], p[1]);
               ItemStack second = decodeStack(p[2], p[3]);
               ItemStack result = decodeStack(p[4], p[5]);
               boolean disabled = "1".equals(p[6]) || Boolean.parseBoolean(p[6]);
               this.offers.add(new AldeanoPhoraScreen.OfferView(first, second, result, disabled));
            }
         } catch (Exception var10) {
         }
      }
   }

   private static ItemStack decodeStack(String idText, String countText) {
      try {
         ResourceLocation itemId = ResourceLocation.tryParse(idText);
         int count = Integer.parseInt(countText);
         return itemId != null && count > 0 && BuiltInRegistries.ITEM.containsKey(itemId)
            ? new ItemStack((ItemLike)BuiltInRegistries.ITEM.get(itemId), count)
            : ItemStack.EMPTY;
      } catch (Exception ignored) {
         return ItemStack.EMPTY;
      }
   }

   private int sx(int x) {
      return this.guiX + Math.round(x * this.scale);
   }

   private int sy(int y) {
      return this.guiY + Math.round(y * this.scale);
   }

   private void image(GuiGraphics context, ResourceLocation texture, int x, int y, int width, int height, int textureWidth, int textureHeight) {
      context.blit(
         texture,
         this.sx(x),
         this.sy(y),
         0.0F,
         0.0F,
         Math.max(1, Math.round(width * this.scale)),
         Math.max(1, Math.round(height * this.scale)),
         textureWidth,
         textureHeight
      );
   }

   private void drawTextAt(GuiGraphics context, Component text, int x, int y, int color) {
      context.pose().pushPose();
      context.pose().translate(this.sx(x), this.sy(y), 0.0F);
      context.pose().scale(this.scale, this.scale, 1.0F);
      context.drawString(this.font, text, 0, 0, color);
      context.pose().popPose();
   }

   private void drawCentered(GuiGraphics context, Component text, int x, int y, int color) {
      context.pose().pushPose();
      context.pose().translate(this.sx(x), this.sy(y), 0.0F);
      context.pose().scale(this.scale, this.scale, 1.0F);
      context.drawCenteredString(this.font, text, 0, 0, color);
      context.pose().popPose();
   }

   private void drawItemAt(GuiGraphics context, ItemStack stack, int x, int y) {
      if (stack != null && !stack.isEmpty()) {
         context.pose().pushPose();
         context.pose().translate(this.sx(x), this.sy(y), 0.0F);
         context.pose().scale(this.scale, this.scale, 1.0F);
         context.renderItem(stack, 0, 0);
         context.pose().popPose();
      }
   }

   private void drawTradeSlot(GuiGraphics context, int x, int y, ItemStack stack) {
      int bx = this.sx(x);
      int by = this.sy(y);
      int size = Math.max(1, Math.round(58.0F * this.scale));
      context.fill(bx, by, bx + size, by + size, -1440472028);
      context.renderOutline(bx, by, size, size, -8947849);
      this.image(context, DARK, x + 20, y + 20, 16, 16, 16, 16);
      this.drawItemAt(context, stack, x + 20, y + 20);
   }

   private int getPhoraLevel() {
      return this.minecraft != null && this.minecraft.level != null && this.minecraft.level.getEntity(this.entityId) instanceof AldeanoPhoraEntity phora
         ? Math.max(1, Math.min(5, phora.getVillagerData().getLevel()))
         : 1;
   }

   public boolean isPauseScreen() {
      return false;
   }

   public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
      this.scale = Math.min((this.width - 12.0F) / 1240.0F, (this.height - 12.0F) / 770.0F);
      this.scale = Math.max(0.2F, this.scale);
      int dw = Math.round(1240.0F * this.scale);
      int dh = Math.round(770.0F * this.scale);
      this.guiX = (this.width - dw) / 2;
      this.guiY = (this.height - dh) / 2;
      this.image(context, BG, 0, 0, 1240, 770, 1240, 770);
      this.drawTextAt(context, Component.literal("Aldeano Phora de Recursos"), 42, 8, -1517400);
      int available = 0;

      for (AldeanoPhoraScreen.OfferView offer : this.offers) {
         if (!offer.disabled()) {
            available++;
         }
      }

      float availableProgress = this.offers.isEmpty() ? 0.0F : (float)available / this.offers.size();
      this.image(context, GREEN_TRACK, 8, 37, 356, 24, 356, 24);
      int greenRight = this.sx(8) + Math.round(370.0F * this.scale * availableProgress);
      context.enableScissor(this.sx(0), this.sy(28), greenRight, this.sy(64));
      this.image(context, GREEN_FULL, 0, 28, 370, 36, 370, 36);
      context.disableScissor();

      for (int i = 0; i < this.offers.size() && i < 7; i++) {
         AldeanoPhoraScreen.OfferView offer = this.offers.get(i);
         int rowY = 82 + i * 87;
         if (i == this.selected) {
            this.image(context, ROW_RED, 8, rowY, 356, 84, 356, 84);
         } else {
            this.image(context, ROW_WHITE, 8, rowY, 356, 80, 356, 80);
         }

         this.drawItemAt(context, offer.first(), 25, rowY + 27);
         if (!offer.first().isEmpty()) {
            this.drawTextAt(context, Component.literal(String.valueOf(offer.first().getCount())), 47, rowY + 39, -1);
         }

         this.drawTextAt(context, Component.literal("›"), 185, rowY + 31, -1517400);
         this.drawItemAt(context, offer.result(), 235, rowY + 27);
         if (!offer.result().isEmpty()) {
            this.drawTextAt(context, Component.literal(String.valueOf(offer.result().getCount())), 258, rowY + 39, -8329062);
         }

         if (offer.disabled()) {
            this.drawTextAt(context, Component.literal("AGOTADO"), 270, rowY + 57, -43691);
         }
      }

      int level = this.getPhoraLevel();
      float levelProgress = level / 5.0F;
      this.drawCentered(context, Component.literal("Nivel " + level), 865, 32, -1517400);
      this.image(context, LEVEL_EMPTY, 680, 57, 372, 32, 372, 32);
      int levelRight = this.sx(680) + Math.round(372.0F * this.scale * levelProgress);
      context.enableScissor(this.sx(680), this.sy(57), levelRight, this.sy(89));
      this.image(context, LEVEL_FULL, 680, 57, 372, 32, 372, 32);
      context.disableScissor();
      this.image(context, PERCENT, 1120, 25, 84, 84, 84, 84);
      this.drawCentered(context, Component.literal((int)(levelProgress * 100.0F) + "%"), 1162, 60, -1);
      this.drawTextAt(context, Component.literal("Intercambio"), 470, 115, -1517400);
      if (!this.offers.isEmpty() && this.selected >= 0 && this.selected < this.offers.size()) {
         AldeanoPhoraScreen.OfferView current = this.offers.get(this.selected);
         this.drawTradeSlot(context, 650, 174, current.first());
         this.drawTradeSlot(context, 760, 174, current.second());
         this.drawCentered(context, Component.literal("›"), 870, 192, -1517400);
         this.drawTradeSlot(context, 990, 174, current.result());
         this.drawTextAt(context, Component.literal(current.first().isEmpty() ? "" : current.first().getHoverName().getString()), 620, 239, -1);
         this.drawTextAt(context, Component.literal(current.second().isEmpty() ? "" : current.second().getHoverName().getString()), 735, 239, -1);
         this.drawTextAt(context, Component.literal(current.result().isEmpty() ? "" : current.result().getHoverName().getString()), 965, 239, -8329062);
         this.drawTextAt(
            context,
            Component.literal(current.disabled() ? "Oferta agotada" : "Pulsa el objeto para intercambiar"),
            650,
            305,
            current.disabled() ? -43691 : -2236963
         );
      }

      this.drawTextAt(context, Component.literal("Inventario"), 465, 382, -1517400);
      if (this.minecraft != null && this.minecraft.player != null) {
         for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
               ItemStack stack = this.minecraft.player.getInventory().getItem(9 + row * 9 + col);
               this.drawItemAt(context, stack, 464 + col * 81 + 32, 408 + row * 79 + 30);
            }
         }

         for (int col = 0; col < 9; col++) {
            ItemStack stack = this.minecraft.player.getInventory().getItem(col);
            this.drawItemAt(context, stack, 464 + col * 81 + 32, 665);
         }
      }

      super.render(context, mouseX, mouseY, delta);
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (button == 0 && !(this.scale <= 0.0F)) {
         double x = (mouseX - this.guiX) / this.scale;
         double y = (mouseY - this.guiY) / this.scale;
         if (x >= 8.0 && x <= 364.0 && y >= 82.0 && y < 691.0) {
            int row = (int)((y - 82.0) / 87.0);
            if (row >= 0 && row < this.offers.size() && row < 7) {
               this.selected = row;
               return true;
            }
         }

         if (x >= 990.0 && x <= 1048.0 && y >= 174.0 && y <= 232.0 && this.selected >= 0 && this.selected < this.offers.size()) {
            AldeanoPhoraScreen.OfferView offer = this.offers.get(this.selected);
            if (!offer.disabled()) {
               ClientPlayNetworking.send(new TradePhoraC2S(this.entityId, this.selected));
            }

            return true;
         } else {
            return super.mouseClicked(mouseX, mouseY, button);
         }
      } else {
         return super.mouseClicked(mouseX, mouseY, button);
      }
   }

   @Environment(EnvType.CLIENT)
   private record OfferView(ItemStack first, ItemStack second, ItemStack result, boolean disabled) {
   }
}
