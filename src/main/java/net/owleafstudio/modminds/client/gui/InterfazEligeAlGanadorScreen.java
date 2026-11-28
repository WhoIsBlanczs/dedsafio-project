package net.owleafstudio.modminds.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.HashMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.WidgetSprites;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.network.InterfazEligeAlGanadorButtonMessage;
import net.owleafstudio.modminds.world.inventory.InterfazEligeAlGanadorMenu;

public class InterfazEligeAlGanadorScreen extends AbstractContainerScreen<InterfazEligeAlGanadorMenu> {
   private static final HashMap<String, Object> guistate = InterfazEligeAlGanadorMenu.guistate;
   private final Level world;
   private final int x;
   private final int y;
   private final int z;
   private final Player entity;
   ImageButton imagebutton_totem;
   ImageButton imagebutton_nutria;
   ImageButton imagebutton_brocoli;
   ImageButton imagebutton_corazon;
   ImageButton imagebutton_cosa_rara;
   ImageButton imagebutton_dedita;
   ImageButton imagebutton_pluma;
   ImageButton imagebutton_oro;
   ImageButton imagebutton_ganador_custom;
   private static final Identifier texture = Identifier.parse("modminds:textures/screens/interfaz_elige_al_ganador.png");

   public InterfazEligeAlGanadorScreen(InterfazEligeAlGanadorMenu container, Inventory inventory, Component text) {
      super(container, inventory, text, 176, 166);
      this.world = container.world;
      this.x = container.x;
      this.y = container.y;
      this.z = container.z;
      this.entity = container.entity;
   }

   public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
      super.extractRenderState(guiGraphics, mouseX, mouseY, partialTicks);
   }

   public void extractBackground(GuiGraphicsExtractor guiGraphics, int gx, int gy, float partialTicks) {
      super.extractBackground(guiGraphics, gx, gy, partialTicks);
      guiGraphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, texture, (int)(this.leftPos), (int)(this.topPos), (float)(0.0F), (float)(0.0F), this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
      guiGraphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, Identifier.parse("modminds:textures/screens/fondo_para_gui_owleafstudio.png"), (int)(this.leftPos + 0), (int)(this.topPos + 0), (float)(0.0F), (float)(0.0F), 176, 166, 176, 166);
   }

   public boolean keyPressed(net.minecraft.client.input.KeyEvent evento$) {
      int key = evento$.key();
      int b = 0;
      int c = evento$.modifiers();
      if (key == 256) {
         this.minecraft.player.closeContainer();
         return true;
      } else {
         return super.keyPressed(evento$);
      }
   }

   protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
   }

   public void init() {
      super.init();
      this.imagebutton_totem = new ImageButton(
         this.leftPos + 70,
         this.topPos + 52,
         35,
         22,
         new WidgetSprites(
            Identifier.parse("modminds:textures/screens/atlas/imagebutton_totem.png"),
            Identifier.parse("modminds:textures/screens/atlas/imagebutton_totem.png")
         ),
         e -> {}
      );
      guistate.put("button:imagebutton_totem", this.imagebutton_totem);
      this.addRenderableWidget(this.imagebutton_totem);
      this.imagebutton_nutria = new ImageButton(
         this.leftPos + 118,
         this.topPos + 52,
         35,
         22,
         new WidgetSprites(
            Identifier.parse("modminds:textures/screens/atlas/imagebutton_nutria.png"),
            Identifier.parse("modminds:textures/screens/atlas/imagebutton_nutria.png")
         ),
         e -> {}
      );
      guistate.put("button:imagebutton_nutria", this.imagebutton_nutria);
      this.addRenderableWidget(this.imagebutton_nutria);
      this.imagebutton_brocoli = new ImageButton(
         this.leftPos + 70,
         this.topPos + 88,
         35,
         22,
         new WidgetSprites(
            Identifier.parse("modminds:textures/screens/atlas/imagebutton_brocoli.png"),
            Identifier.parse("modminds:textures/screens/atlas/imagebutton_brocoli.png")
         ),
         e -> {}
      );
      guistate.put("button:imagebutton_brocoli", this.imagebutton_brocoli);
      this.addRenderableWidget(this.imagebutton_brocoli);
      this.imagebutton_corazon = new ImageButton(
         this.leftPos + 99,
         this.topPos + 124,
         35,
         22,
         new WidgetSprites(
            Identifier.parse("modminds:textures/screens/atlas/imagebutton_corazon.png"),
            Identifier.parse("modminds:textures/screens/atlas/imagebutton_corazon.png")
         ),
         e -> {}
      );
      guistate.put("button:imagebutton_corazon", this.imagebutton_corazon);
      this.addRenderableWidget(this.imagebutton_corazon);
      this.imagebutton_cosa_rara = new ImageButton(
         this.leftPos + 23,
         this.topPos + 52,
         35,
         22,
         new WidgetSprites(
            Identifier.parse("modminds:textures/screens/atlas/imagebutton_cosa_rara.png"),
            Identifier.parse("modminds:textures/screens/atlas/imagebutton_cosa_rara.png")
         ),
         e -> {
            ModmindsMod.PACKET_HANDLER.sendToServer(new InterfazEligeAlGanadorButtonMessage(4, this.x, this.y, this.z));
            InterfazEligeAlGanadorButtonMessage.handleButtonAction(this.entity, 4, this.x, this.y, this.z);
         }
      );
      guistate.put("button:imagebutton_cosa_rara", this.imagebutton_cosa_rara);
      this.addRenderableWidget(this.imagebutton_cosa_rara);
      this.imagebutton_dedita = new ImageButton(
         this.leftPos + 118,
         this.topPos + 88,
         35,
         22,
         new WidgetSprites(
            Identifier.parse("modminds:textures/screens/atlas/imagebutton_dedita.png"),
            Identifier.parse("modminds:textures/screens/atlas/imagebutton_dedita.png")
         ),
         e -> {}
      );
      guistate.put("button:imagebutton_dedita", this.imagebutton_dedita);
      this.addRenderableWidget(this.imagebutton_dedita);
      this.imagebutton_pluma = new ImageButton(
         this.leftPos + 42,
         this.topPos + 124,
         35,
         22,
         new WidgetSprites(
            Identifier.parse("modminds:textures/screens/atlas/imagebutton_pluma.png"),
            Identifier.parse("modminds:textures/screens/atlas/imagebutton_pluma.png")
         ),
         e -> {}
      );
      guistate.put("button:imagebutton_pluma", this.imagebutton_pluma);
      this.addRenderableWidget(this.imagebutton_pluma);
      this.imagebutton_oro = new ImageButton(
         this.leftPos + 23,
         this.topPos + 88,
         35,
         22,
         new WidgetSprites(
            Identifier.parse("modminds:textures/screens/atlas/imagebutton_oro.png"),
            Identifier.parse("modminds:textures/screens/atlas/imagebutton_oro.png")
         ),
         e -> {}
      );
      guistate.put("button:imagebutton_oro", this.imagebutton_oro);
      this.addRenderableWidget(this.imagebutton_oro);
      this.imagebutton_ganador_custom = new ImageButton(
         this.leftPos + 37,
         this.topPos + 16,
         105,
         22,
         new WidgetSprites(
            Identifier.parse("modminds:textures/screens/atlas/imagebutton_ganador_custom.png"),
            Identifier.parse("modminds:textures/screens/atlas/imagebutton_ganador_custom.png")
         ),
         e -> {}
      );
      guistate.put("button:imagebutton_ganador_custom", this.imagebutton_ganador_custom);
      this.addRenderableWidget(this.imagebutton_ganador_custom);
   }
}
