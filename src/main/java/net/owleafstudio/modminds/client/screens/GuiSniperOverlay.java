package net.owleafstudio.modminds.client.screens;

import com.mojang.renderpearl.backend.opengl.GlStateManager.DestFactor;
import com.mojang.renderpearl.backend.opengl.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.resources.Identifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.owleafstudio.modminds.procedures.SniperClickDerechoProcedure;

@Mod.EventBusSubscriber({Dist.CLIENT})
public class GuiSniperOverlay {
   @SubscribeEvent(
      priority = EventPriority.NORMAL
   )
   public static void eventHandler(RenderGuiEvent.Pre event) {
      int w = event.getWindow().getGuiScaledWidth();
      int h = event.getWindow().getGuiScaledHeight();
      Level world = null;
      double x = 0.0;
      double y = 0.0;
      double z = 0.0;
      Player entity = Minecraft.getInstance().player;
      if (entity != null) {
         world = entity.level();
         x = entity.getX();
         y = entity.getY();
         z = entity.getZ();
      }
      if (SniperClickDerechoProcedure.execute(entity)) {
         event.getGuiGraphics().blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, Identifier.parse("modminds:textures/screens/sniper3.png"), (int)(0), (int)(0), (float)(0.0F), (float)(0.0F), w, h, w, h);
      }
   }
}
