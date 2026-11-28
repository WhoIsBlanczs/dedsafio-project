package net.owleafstudio.modminds.client.gui;

import java.util.HashMap;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.owleafstudio.modminds.world.inventory.InventarioMenu;

/** Pantalla de las bolsas de shulker (la clase original venía dañada en el .jar; se rehízo con la plantilla de MCreator). */
public class InventarioScreen extends AbstractContainerScreen<InventarioMenu> {
   private static final HashMap<String, Object> guistate = InventarioMenu.guistate;
   private final Level world;
   private final int x;
   private final int y;
   private final int z;
   private final Player entity;
   private static final Identifier texture = Identifier.parse("modminds:textures/screens/inventario.png");

   public InventarioScreen(InventarioMenu container, Inventory inventory, Component text) {
      super(container, inventory, text, 176, 166);
      this.world = container.world;
      this.x = container.x;
      this.y = container.y;
      this.z = container.z;
      this.entity = container.entity;
   }

   @Override
   public void extractBackground(GuiGraphicsExtractor guiGraphics, int gx, int gy, float partialTicks) {
      super.extractBackground(guiGraphics, gx, gy, partialTicks);
      guiGraphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, texture, this.leftPos, this.topPos, 0.0F, 0.0F, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
   }

   @Override
   public boolean keyPressed(net.minecraft.client.input.KeyEvent evento) {
      if (evento.key() == 256) {
         this.minecraft.player.closeContainer();
         return true;
      }
      return super.keyPressed(evento);
   }

   @Override
   protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
   }
}
