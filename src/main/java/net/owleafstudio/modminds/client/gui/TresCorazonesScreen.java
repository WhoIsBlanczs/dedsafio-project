package net.owleafstudio.modminds.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.HashMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.owleafstudio.modminds.world.inventory.TresCorazonesMenu;

public class TresCorazonesScreen extends AbstractContainerScreen<TresCorazonesMenu> {
   private static final HashMap<String, Object> guistate = TresCorazonesMenu.guistate;
   private final Level world;
   private final int x;
   private final int y;
   private final int z;
   private final Player entity;
   private static final Identifier texture = Identifier.parse("modminds:textures/screens/ganadores.png");

   public TresCorazonesScreen(TresCorazonesMenu container, Inventory inventory, Component text) {
      super(container, inventory, text);
      this.world = container.world;
      this.x = container.x;
      this.y = container.y;
      this.z = container.z;
      this.entity = container.entity;
      this.imageWidth = 176;
      this.imageHeight = 166;
   }

   public void render(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
      this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
      super.render(guiGraphics, mouseX, mouseY, partialTicks);
      this.renderTooltip(guiGraphics, mouseX, mouseY);
   }

   protected void renderBg(GuiGraphicsExtractor guiGraphics, float partialTicks, int gx, int gy) {
      guiGraphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, texture, (int)(this.leftPos), (int)(this.topPos), (float)(0.0F), (float)(0.0F), this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
   }

   public boolean keyPressed(int key, int b, int c) {
      if (key == 256) {
         this.minecraft.player.closeContainer();
         return true;
      } else {
         return super.keyPressed(key, b, c);
      }
   }

   protected void renderLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
   }

   public void init() {
      super.init();
   }
}
