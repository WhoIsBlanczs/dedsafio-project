package com.watonstudio.creepermetalico.client;

import com.watonstudio.creepermetalico.net.ReviveC2S;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public class FogataScreen extends Screen {
   static final Identifier LIBRO = Identifier.fromNamespaceAndPath("fogata", "textures/gui/libro.png");
   static final Identifier BARRA = Identifier.fromNamespaceAndPath("fogata", "textures/gui/barra.png");
   static final Identifier BARRA_SEL = Identifier.fromNamespaceAndPath("fogata", "textures/gui/barra_sel.png");
   static final Identifier BOTON = Identifier.fromNamespaceAndPath("fogata", "textures/gui/boton.png");
   static final Identifier BOTON_HOVER = Identifier.fromNamespaceAndPath("fogata", "textures/gui/boton_hover.png");
   private final List<String> names;
   private final BlockPos pos;
   private int selected = -1;
   private int scroll = 0;
   private int bx;
   private int by;
   private int lx;
   private int ly;
   private int lw;
   private int barH;
   private int rowH;
   private int rows;
   private int btnX;
   private int btnY;
   private int btnW;
   private int btnH;

   public FogataScreen(List<String> names, BlockPos pos) {
      super(Component.literal("Fogata"));
      this.names = names;
      this.pos = pos;
   }

   public void extractRenderState(GuiGraphicsExtractor g, int mx, int my, float delta) {
      int bw = Math.min((int)(this.height * 0.82F * 641.0F / 601.0F), this.width - 20);
      int bh = (int)(bw * 601.0F / 641.0F);
      this.bx = (this.width - bw) / 2;
      this.by = (this.height - bh) / 2;
      float s = bw / 641.0F;
      g.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, LIBRO, this.bx, this.by, 0.0F, 0.0F, bw, bh, 641, 601, 641, 601);
      this.lx = this.bx + (int)(80.0F * s);
      this.ly = this.by + (int)(70.0F * s);
      this.lw = (int)(255.0F * s);
      this.barH = (int)(this.lw * 101.0F / 794.0F * 1.28F);
      this.rowH = this.barH + Math.max(1, (int)(4.0F * s));
      this.rows = Math.max(1, (int)(455.0F * s) / this.rowH);
      if (this.names.isEmpty()) {
         g.text(this.font, "Nadie ha muerto", this.lx + 6, this.ly + 6, 3877402, false);
      }

      for (int i = 0; i < this.rows && i + this.scroll < this.names.size(); i++) {
         int y = this.ly + i * this.rowH;
         int idx = i + this.scroll;
         g.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, idx == this.selected ? BARRA_SEL : BARRA, this.lx, y, 0.0F, 0.0F, this.lw, this.barH, 794, 101, 794, 101);
         String n = this.names.get(idx);
         g.text(this.font, n, this.lx + (int)(this.lw * 0.17F), y + (this.barH - 8) / 2, 16777215, true);
      }

      if (this.names.size() > this.rows) {
         int tx = this.bx + (int)(343.0F * s);
         int th = Math.max(8, (int)(40.0F * s));
         int trackH = (int)(440.0F * s);
         int ty = this.ly + (int)((trackH - th) * ((float)this.scroll / Math.max(1, this.names.size() - this.rows)));
         g.fill(tx, this.ly, tx + Math.max(2, (int)(6.0F * s)), this.ly + trackH, 1429940762);
         g.fill(tx, ty, tx + Math.max(2, (int)(6.0F * s)), ty + th, -5207462);
      }

      int sq = (int)(85.0F * s);
      int sx = this.bx + (int)(420.0F * s);
      int sy = this.by + (int)(197.0F * s);
      ItemStack held = this.minecraft != null && this.minecraft.player != null ? this.minecraft.player.getMainHandItem() : ItemStack.EMPTY;
      if (!held.isEmpty()) {
         float k = (sq - 8) / 16.0F;
         g.pose().pushMatrix();
         g.pose().translate((float)(sx + 4), (float)(sy + 4));
         g.pose().scale((float)(k), (float)(k));
         g.item(held, 0, 0);
         g.pose().popMatrix();
      }

      this.btnX = this.bx + (int)(388.0F * s);
      this.btnW = (int)(154.0F * s);
      this.btnH = (int)(this.btnW * 178.0F / 452.0F);
      this.btnY = this.by + (int)(312.0F * s) + ((int)(78.0F * s) - this.btnH) / 2;
      boolean over = this.selected >= 0 && mx >= this.btnX && mx <= this.btnX + this.btnW && my >= this.btnY && my <= this.btnY + this.btnH;
      g.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, over ? BOTON_HOVER : BOTON, this.btnX, this.btnY, 0.0F, 0.0F, this.btnW, this.btnH, 452, 178, 452, 178);
      g.centeredText(this.font, "Revivir", this.btnX + this.btnW / 2, this.btnY + (this.btnH - 8) / 2, this.selected >= 0 ? 16777215 : 8947848);
   }

   public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent evento$, boolean doble$) {
      double mx = evento$.x();
      double my = evento$.y();
      int button = evento$.button();
      for (int i = 0; i < this.rows && i + this.scroll < this.names.size(); i++) {
         int y = this.ly + i * this.rowH;
         if (mx >= this.lx && mx <= this.lx + this.lw && my >= y && my <= y + this.barH) {
            this.selected = i + this.scroll;
            return true;
         }
      }

      if (this.selected >= 0 && mx >= this.btnX && mx <= this.btnX + this.btnW && my >= this.btnY && my <= this.btnY + this.btnH) {
         ClientPlayNetworking.send(new ReviveC2S(this.names.get(this.selected), this.pos));
         this.onClose();
         return true;
      } else {
         return super.mouseClicked(evento$, doble$);
      }
   }

   public boolean mouseScrolled(double mx, double my, double h, double v) {
      this.scroll = Math.max(0, Math.min(Math.max(0, this.names.size() - this.rows), this.scroll - (int)Math.signum(v)));
      return true;
   }

   public boolean isPauseScreen() {
      return false;
   }
}
