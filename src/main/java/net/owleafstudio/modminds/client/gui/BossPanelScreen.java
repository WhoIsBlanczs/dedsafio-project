package net.owleafstudio.modminds.client.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.util.Mth;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.Button;

public abstract class BossPanelScreen<M extends AbstractContainerMenu> extends AbstractContainerScreen<M> {
   private static final int WIDTH = 384;
   private static final int PAD = 9;
   private static final int HEAD = 30;
   private static final int GAP = 4;
   private static final int SEC = 16;
   private static final int BTN_H = 18;
   private static final int ROW = 20;
   private static final int H_GAP = 5;
   private static final int RAIL = 112;
   private static final int RAIL_GAP = 8;
   private static final int TAB_H = 18;
   private static final int TAB_STEP = 20;
   private static final int MIN_CELL = 96;
   private static final int READOUT = 66;
   private static final int READOUT_LINES = 4;
   private static final int EDGE = 8;
   private static final int PLATE = -233696743;
   private static final int PLATE_EDGE = -13421750;
   private static final int CELL = -14935002;
   private static final int CELL_HOVER = -13882306;
   private static final int SUNKEN = -15856107;
   private static final int TEXT = -2697504;
   private static final int TEXT_HOT = -1;
   private static final int TEXT_DIM = -7697767;
   private static final Component FUERA_DE_FASE = Component.translatable("gui.modminds.panel.off_phase");
   private final List<BossPanelScreen.Section> layout;
   private final int contentW;
   private final int gridRows;
   private final int fieldSection;
   private final int bodyH;
   private final int tailH;
   private int tab;
   private int lastPhase = -2;
   private EditBox numberField;
   private String savedFieldValue;
   private BossPanelScreen.Ability lectura;
   private final List<BossPanelScreen.Placed> placed = new ArrayList<>();
   private int scroll;
   private int scrollMax;

   protected int phaseKey() {
      return -1;
   }

   protected boolean appliesToPhase(int phase, int abilityId) {
      return true;
   }

   protected Component phaseBadge() {
      return null;
   }

   protected BossPanelScreen(M menu, Inventory inventory, Component title) {
      super(menu, inventory, title);
      this.layout = this.sections();
      this.contentW = 246;
      int rows = 1;
      int field = -1;

      for (int s = 0; s < this.layout.size(); s++) {
         BossPanelScreen.Section section = this.layout.get(s);
         rows = Math.max(rows, ceilRows(section.abilities().length, this.columnas(section)));
         if (field < 0 && section.fieldKey() != null) {
            field = s;
         }
      }

      this.gridRows = rows;
      this.fieldSection = field;
      this.bodyH = Math.max(16 + (rows + (field < 0 ? 0 : 1)) * 20, this.layout.size() * 20);
      this.tailH = this.tail();
      this.imageWidth = 384;
      this.imageHeight = bodyTop() + this.bodyH + 4 + 66 + this.tailH + 9;
   }

   protected abstract Component title();

   protected abstract int accent();

   protected abstract List<BossPanelScreen.Section> sections();

   protected abstract void trigger(int var1);

   protected abstract HashMap<String, Object> guistate();

   protected BossPanelScreen.Portrait portrait() {
      return null;
   }

   protected int tail() {
      return 0;
   }

   protected void renderTail(GuiGraphicsExtractor gui, int y) {
   }

   private static int ceilRows(int count, int columns) {
      return (count + columns - 1) / columns;
   }

   private int columnas(BossPanelScreen.Section section) {
      int queCaben = Math.max(1, (this.contentW + 5) / 101);
      return Math.min(Math.max(1, section.columns()), queCaben);
   }

   private static int bodyTop() {
      return 43;
   }

   private int gridTop() {
      return bodyTop() + 16;
   }

   private int railLeft() {
      return 9;
   }

   private int readoutTop() {
      return bodyTop() + this.bodyH + 4;
   }

   private int tailTop() {
      return this.readoutTop() + 66;
   }

   private int fieldPlateY() {
      return this.gridTop() + ceilRows(this.layout.get(this.fieldSection).abilities().length, this.columnas(this.layout.get(this.fieldSection))) * 20;
   }

   protected void init() {
      super.init();
      this.lastPhase = this.phaseKey();
      int phase = this.lastPhase;
      this.tab = Mth.clamp(this.tab, 0, this.layout.size() - 1);
      this.scrollMax = Math.max(0, this.imageHeight + 16 - this.height);
      this.scroll = Mth.clamp(this.scroll, 0, this.scrollMax);
      if (this.scrollMax > 0) {
         this.topPos = 8 - this.scroll;
      }

      this.placed.clear();

      for (int s = 0; s < this.layout.size(); s++) {
         BossPanelScreen.Section section = this.layout.get(s);
         int cual = s;
         BossPanelScreen.TabButton tabButton = new BossPanelScreen.TabButton(
            this.leftPos + this.railLeft(),
            this.topPos + bodyTop() + s * 20,
            112,
            18,
            Component.translatable(section.titleKey()),
            section.abilities().length,
            this.accent(),
            s == this.tab,
            b -> this.seleccionarTab(cual)
         );
         this.addRenderableWidget(tabButton);
         this.placed.add(new BossPanelScreen.Placed(tabButton, bodyTop() + s * 20));
      }

      BossPanelScreen.Section activa = this.layout.get(this.tab);
      int cols = this.columnas(activa);
      int cellW = (this.contentW - (cols - 1) * 5) / cols;
      int gridLeft = this.leftPos + this.railLeft() + 112 + 8;

      for (int i = 0; i < activa.abilities().length; i++) {
         BossPanelScreen.Ability ability = activa.abilities()[i];
         int plateY = this.gridTop() + i / cols * 20;
         boolean enFase = this.appliesToPhase(phase, ability.id());
         BossPanelScreen.AbilityButton button = new BossPanelScreen.AbilityButton(
            ability, gridLeft + i % cols * (cellW + 5), this.topPos + plateY, cellW, 18, ability.label(), this.accent(), enFase, b -> {
               this.lectura = ability;
               this.trigger(ability.id());
            }
         );
         Component help = ability.help();
         if (help != null) {
            button.setTooltip(Tooltip.create((Component)(enFase ? help : help.copy().append(" · ").append(FUERA_DE_FASE))));
         }

         this.addRenderableWidget(button);
         this.placed.add(new BossPanelScreen.Placed(button, plateY));
      }

      if (this.fieldSection >= 0) {
         BossPanelScreen.Section conCampo = this.layout.get(this.fieldSection);
         final Component hint = Component.translatable("gui.modminds." + this.panelKey() + "." + conCampo.fieldKey());
         int plateY = this.fieldPlateY();
         this.numberField = new EditBox(this.font, gridLeft, this.topPos + plateY, this.contentW, 16, hint) {
            public void insertText(String text) {
               super.insertText(text);
               this.setSuggestion(this.getValue().isEmpty() ? hint.getString() : null);
            }

            public void moveCursorTo(int pos) {
               super.moveCursorTo(pos, false);
               this.setSuggestion(this.getValue().isEmpty() ? hint.getString() : null);
            }
         };
         this.numberField.setSuggestion(hint.getString());
         this.numberField.setMaxLength(32767);
         if (this.savedFieldValue != null) {
            this.numberField.setValue(this.savedFieldValue);
         }

         boolean suPestana = this.fieldSection == this.tab;
         this.numberField.setVisible(suPestana);
         this.numberField.active = suPestana;
         this.guistate().put("text:" + conCampo.fieldKey(), this.numberField);
         this.addRenderableWidget(this.numberField);
         this.placed.add(new BossPanelScreen.Placed(this.numberField, plateY));
      } else {
         this.numberField = null;
      }
   }

   private void seleccionarTab(int index) {
      if (index != this.tab) {
         this.tab = index;
         this.scroll = 0;
         this.lectura = null;
         if (this.numberField != null) {
            this.savedFieldValue = this.numberField.getValue();
         }

         this.init(this.minecraft, this.width, this.height);
      }
   }

   private void refrescarFase() {
      if (this.phaseKey() != this.lastPhase) {
         if (this.numberField != null) {
            this.savedFieldValue = this.numberField.getValue();
         }

         this.init(this.minecraft, this.width, this.height);
      }
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
      if (this.scrollMax <= 0) {
         return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
      } else {
         int target = Mth.clamp(this.scroll - (int)Math.round(scrollY * 20.0), 0, this.scrollMax);
         if (target != this.scroll) {
            this.scroll = target;
            this.topPos = 8 - this.scroll;

            for (BossPanelScreen.Placed widget : this.placed) {
               widget.widget().setY(this.topPos + widget.plateY());
            }
         }

         return true;
      }
   }

   protected abstract String panelKey();

   public void resize(Minecraft minecraft, int width, int height) {
      if (this.numberField != null) {
         this.savedFieldValue = this.numberField.getValue();
      }

      super.resize(minecraft, width, height);
   }

   public boolean keyPressed(net.minecraft.client.input.KeyEvent evento$) {
      int key = evento$.key();
      int modifiers = 0;
      int keyCode = evento$.modifiers();
      if (key == 256) {
         this.minecraft.player.closeContainer();
         return true;
      } else if (this.numberField != null && this.numberField.isFocused()) {
         return this.numberField.keyPressed(key, modifiers, keyCode);
      } else if (key == 266) {
         this.cambiarTab((this.tab - 1 + this.layout.size()) % this.layout.size());
         return true;
      } else if (key == 267) {
         this.cambiarTab((this.tab + 1) % this.layout.size());
         return true;
      } else if (key >= 49 && key <= 57 && key - 49 < this.layout.size()) {
         this.cambiarTab(key - 49);
         return true;
      } else {
         return super.keyPressed(evento$);
      }
   }

   private void cambiarTab(int index) {
      if (this.numberField != null) {
         this.savedFieldValue = this.numberField.getValue();
      }

      this.seleccionarTab(index);
   }

   public void extractBackground(GuiGraphicsExtractor gui, int mouseX, int mouseY, float partialTicks) {
      super.extractBackground(gui, mouseX, mouseY, partialTicks);
      int x = this.leftPos;
      int y = this.topPos;
      int w = this.imageWidth;
      int h = this.imageHeight;
      int accent = this.accent();
      plate(gui, x, y, w, h, 2, -13421750);
      plate(gui, x + 1, y + 1, w - 2, h - 2, 2, -233696743);

      for (int i = 0; i < 30; i++) {
         int alpha = (int)(40.0F * (1.0F - i / 30.0F)) << 24;
         gui.fill(x + 3, y + 2 + i, x + w - 3, y + 3 + i, alpha | accent & 16777215);
      }

      BossPanelScreen.Portrait portrait = this.portrait();
      int textX = x + 9;
      if (portrait != null) {
         int badge = y + 9 - 2;
         plate(gui, x + 9 - 2, badge - 2, 26, 26, 2, fade(accent, 0.5F));
         plate(gui, x + 9 - 1, badge - 1, 24, 24, 2, -233696743);
         gui.blit(portrait.texture(), x + 9, badge, 22, 22, 0.0F, 0.0F, portrait.texW(), portrait.texH(), portrait.texW(), portrait.texH());
         textX = x + 9 + 28;
      }

      gui.text(this.font, this.title(), textX, y + 9 - 1, accent, false);
      gui.text(
         this.font,
         Component.translatable("gui.modminds.panel.hint").withStyle(Style.EMPTY.withItalic(true)),
         textX,
         y + 9 + 10,
         -7697767,
         false
      );
      Component fase = this.phaseBadge();
      if (fase != null) {
         int chipW = this.font.width(fase) + 10;
         int chipX = x + w - 9 - chipW;
         plate(gui, chipX, y + 9 - 1, chipW, 12, 2, fade(accent, 0.3F));
         gui.text(this.font, fase, chipX + 5, y + 9 + 1, tint(accent, -1, 0.55F), false);
      }

      gui.fill(x + 9, y + 9 + 30 - 1, x + w - 9, y + 9 + 30, fade(accent, 0.3F));
      int railRight = x + this.railLeft() + 112;
      gui.fill(railRight + 4, y + bodyTop() - 2, railRight + 4 + 1, y + this.readoutTop() - 2, -13421750);
      BossPanelScreen.Section activa = this.layout.get(this.tab);
      int top = y + bodyTop();
      Component name = Component.translatable(activa.titleKey());
      int gridLeft = x + this.railLeft() + 112 + 8;
      int chipW = this.font.width(name) + 8;
      plate(gui, gridLeft, top + 1, chipW, 12, 2, fade(accent, 0.22F));
      gui.fill(gridLeft, top + 3, gridLeft + 2, top + 11, accent);
      gui.text(this.font, name, gridLeft + 5, top + 3, tint(accent, -1, 0.4F), false);
      gui.fill(gridLeft + chipW + 4, top + 10, x + w - 9, top + 11, -13421750);
      if (this.fieldSection >= 0 && this.fieldSection == this.tab && this.numberField != null) {
         int fieldTop = y + this.fieldPlateY();
         gui.fill(gridLeft - 1, fieldTop - 1, gridLeft + this.contentW + 1, fieldTop, -13421750);
         gui.fill(gridLeft - 1, fieldTop + 16, gridLeft + this.contentW + 1, fieldTop + 17, -13421750);
      }

      this.renderReadout(gui, x + 9, y + this.readoutTop(), w - 18, accent);
      this.renderTail(gui, y + this.tailTop());
      if (this.scrollMax > 0) {
         int trackTop = 8;
         int trackH = this.height - 16;
         int barX = this.leftPos + 384 - 5;
         gui.fill(barX, trackTop, barX + 3, trackTop + trackH, 1711276032);
         int thumbH = Math.max(16, trackH * trackH / this.imageHeight);
         int thumbY = trackTop + (trackH - thumbH) * this.scroll / this.scrollMax;
         gui.fill(barX, thumbY, barX + 3, thumbY + thumbH, fade(accent, 0.9F));
      }
   }

   private void renderReadout(GuiGraphicsExtractor gui, int x, int y, int w, int accent) {
      plate(gui, x, y, w, 64, 2, -13421750);
      plate(gui, x + 1, y + 1, w - 2, 62, 2, -15856107);
      gui.fill(x + 1, y + 3, x + 3, y + 66 - 5, fade(accent, 0.45F));
      int innerX = x + 8;
      int innerW = w - 14;
      BossPanelScreen.Ability ability = this.lectura;
      if (ability == null) {
         gui.text(
            this.font,
            Component.translatable("gui.modminds.panel.readout.empty").withStyle(Style.EMPTY.withItalic(true)),
            innerX,
            y + 6,
            -7697767,
            false
         );
         gui.text(
            this.font,
            Component.translatable("gui.modminds.panel.readout.nav").withStyle(Style.EMPTY.withItalic(true)),
            innerX,
            y + 66 - 15,
            fade(accent, 0.75F),
            false
         );
      } else {
         boolean enFase = this.appliesToPhase(this.lastPhase, ability.id());
         gui.text(this.font, ability.label(), innerX, y + 5, tint(accent, -1, 0.45F), false);
         Component help = ability.help();
         if (help != null) {
            int row = y + 15;
            int lineas = 4;
            if (!enFase) {
               gui.text(
                  this.font,
                  Component.translatable("gui.modminds.panel.off_phase_here").withStyle(Style.EMPTY.withItalic(true)),
                  innerX,
                  row,
                  -2056024,
                  false
               );
               row += 10;
               lineas--;
            }

            List<FormattedCharSequence> cortado = this.font.split(help, innerW);
            int hasta = Math.min(cortado.size(), lineas);

            for (int i = 0; i < hasta; i++) {
               gui.text(this.font, cortado.get(i), innerX, row + i * 10, enFase ? -2697504 : -7697767, false);
            }

            if (cortado.size() > lineas) {
               gui.text(this.font, "…", x + w - 14, row + (hasta - 1) * 10, -7697767, false);
            }
         }
      }
   }

   private static void plate(GuiGraphicsExtractor gui, int x, int y, int w, int h, int r, int color) {
      gui.fill(x + r, y, x + w - r, y + h, color);

      for (int i = 0; i < r; i++) {
         gui.fill(x + i, y + r - i, x + w - i, y + r - i + 1, color);
      }

      for (int i = 0; i < r; i++) {
         gui.fill(x + i, y + h - r + i, x + w - i, y + h - r + i + 1, color);
      }
   }

   protected void extractLabels(GuiGraphicsExtractor gui, int mouseX, int mouseY) {
   }

   public void extractRenderState(GuiGraphicsExtractor gui, int mouseX, int mouseY, float partialTicks) {
      this.refrescarFase();
      this.seguirCursor(mouseX, mouseY);
      super.extractRenderState(gui, mouseX, mouseY, partialTicks);
   }

   private void seguirCursor(int mouseX, int mouseY) {
      BossPanelScreen.Ability apuntado = null;

      for (BossPanelScreen.Placed widget : this.placed) {
         if (widget.widget() instanceof BossPanelScreen.AbilityButton button && button.isMouseOver(mouseX, mouseY)) {
            apuntado = button.ability;
         }
      }

      if (apuntado == null && this.getFocused() instanceof BossPanelScreen.AbilityButton button) {
         apuntado = button.ability;
      }

      if (apuntado != null) {
         this.lectura = apuntado;
      }
   }

   private static int fade(int argb, float keep) {
      int r = (int)((argb >> 16 & 0xFF) * keep) + 14;
      int g = (int)((argb >> 8 & 0xFF) * keep) + 14;
      int b = (int)((argb & 0xFF) * keep) + 18;
      return 0xFF000000 | r << 16 | g << 8 | b;
   }

   private static int tint(int accent, int towards, float keep) {
      int r = (int)((accent >> 16 & 0xFF) * (1.0F - keep) + (towards >> 16 & 0xFF) * keep);
      int g = (int)((accent >> 8 & 0xFF) * (1.0F - keep) + (towards >> 8 & 0xFF) * keep);
      int b = (int)((accent & 0xFF) * (1.0F - keep) + (towards & 0xFF) * keep);
      return 0xFF000000 | r << 16 | g << 8 | b;
   }

   protected record Ability(int id, String labelKey, String helpKey) {
      public Ability(int id, String labelKey) {
         this(id, labelKey, null);
      }

      Component label() {
         return Component.translatable(this.labelKey);
      }

      Component help() {
         return this.helpKey == null ? null : Component.translatable(this.helpKey);
      }
   }

   private static class AbilityButton extends Button {
      private final BossPanelScreen.Ability ability;
      private final int accent;
      private final boolean enFase;

      AbilityButton(BossPanelScreen.Ability ability, int x, int y, int w, int h, Component label, int accent, boolean enFase, Button.OnPress onPress) {
         super(x, y, w, h, label, onPress, DEFAULT_NARRATION);
         this.ability = ability;
         this.accent = accent;
         this.enFase = enFase;
      }

      protected void renderWidget(GuiGraphicsExtractor gui, int mouseX, int mouseY, float partialTicks) {
         int x = this.getX();
         int y = this.getY();
         int w = this.getWidth();
         int h = this.getHeight();
         boolean hovered = this.isHovered() && this.active;
         BossPanelScreen.plate(gui, x, y, w, h, 2, hovered ? this.accent : -13421750);
         BossPanelScreen.plate(gui, x + 1, y + 1, w - 2, h - 2, 2, hovered ? -13882306 : -14935002);
         gui.fill(x + 3, y + 2, x + w - 3, y + 3, hovered ? 536870911 : 285212671);
         gui.fill(
            x + 1,
            y + 3,
            x + 3,
            y + h - 3,
            !this.enFase ? -13421750 : (hovered ? BossPanelScreen.tint(this.accent, -1, 0.35F) : BossPanelScreen.fade(this.accent, 0.5F))
         );
         if (hovered) {
            gui.fill(x + 2, y + h - 2, x + w - 2, y + h - 1, this.accent);
         }

         int color = this.active && this.enFase ? (hovered ? -1 : -2697504) : -7697767;
         Font font = Minecraft.getInstance().font;
         if (font.width(this.getMessage()) <= w - 8) {
            gui.text(font, this.getMessage(), x + (w - font.width(this.getMessage())) / 2, y + (h - 9) / 2, color);
         } else {
            this.renderScrollingString(gui, font, 4, color);
         }
      }
   }

   private record Placed(AbstractWidget widget, int plateY) {
   }

   protected record Portrait(Identifier texture, int texW, int texH) {
   }

   protected record Section(String titleKey, int columns, String fieldKey, BossPanelScreen.Ability... abilities) {
      public Section(String titleKey, int columns, BossPanelScreen.Ability... abilities) {
         this(titleKey, columns, null, abilities);
      }
   }

   private static class TabButton extends Button {
      private final int accent;
      private final boolean selected;
      private final int count;

      TabButton(int x, int y, int w, int h, Component label, int count, int accent, boolean selected, Button.OnPress onPress) {
         super(x, y, w, h, label, onPress, DEFAULT_NARRATION);
         this.accent = accent;
         this.selected = selected;
         this.count = count;
      }

      protected void renderWidget(GuiGraphicsExtractor gui, int mouseX, int mouseY, float partialTicks) {
         int x = this.getX();
         int y = this.getY();
         int w = this.getWidth();
         int h = this.getHeight();
         boolean hovered = this.isHovered();
         if (this.selected) {
            BossPanelScreen.plate(gui, x, y, w, h, 2, this.accent);
            BossPanelScreen.plate(gui, x + 1, y + 1, w - 2, h - 2, 2, BossPanelScreen.fade(this.accent, 0.32F));
         } else if (hovered) {
            BossPanelScreen.plate(gui, x, y, w, h, 2, -13421750);
            BossPanelScreen.plate(gui, x + 1, y + 1, w - 2, h - 2, 2, -13882306);
         }

         Font font = Minecraft.getInstance().font;
         String howMany = Integer.toString(this.count);
         int right = x + w - 7 - font.width(howMany);
         int color = this.selected ? -1 : (hovered ? -2697504 : -7697767);
         Component label = this.getMessage();
         if (font.width(label) <= right - (x + 7)) {
            gui.text(font, label, x + 7, y + (h - 9) / 2, color);
         } else {
            renderScrollingString(gui, font, label, x + 7, y, right, y + h, color);
         }

         gui.text(font, howMany, right + 2, y + (h - 9) / 2, this.selected ? -2697504 : -7697767);
      }
   }
}
