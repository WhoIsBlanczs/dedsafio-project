package com.watonstudio.creepermetalico.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexSorting;
import com.watonstudio.creepermetalico.LinternaItem;
import com.watonstudio.creepermetalico.client.ClientEclipse;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(GameRenderer.class)
public abstract class EclipseDarknessOverlayMixin {
   private static final ResourceLocation MASK = ResourceLocation.fromNamespaceAndPath("watonstudio", "textures/gui/eclipse_flashlight_mask.png");

   @Inject(method = "renderHand", at = @At("HEAD"))
   private void watonstudio$drawEnvironmentDarkness(Camera camera, float tickDelta, Matrix4f projection, CallbackInfo ci) {
      Minecraft client = Minecraft.getInstance();
      if (client.level != null && client.player != null && client.screen == null) {
         float darkness = Math.max(0.0F, Math.min(1.0F, ClientEclipse.ambientDarkness(tickDelta)));
         if (!(darkness <= 0.001F)) {
            ItemStack main = client.player.getMainHandItem();
            ItemStack off = client.player.getOffhandItem();
            boolean flashlightOn = main.getItem() instanceof LinternaItem a && a.isLit() || off.getItem() instanceof LinternaItem b && b.isLit();
            int width = client.getWindow().getGuiScaledWidth();
            int height = client.getWindow().getGuiScaledHeight();
            RenderSystem.backupProjectionMatrix();
            Matrix4fStack modelView = RenderSystem.getModelViewStack();
            modelView.pushMatrix();

            try {
               modelView.identity();
               modelView.translate(0.0F, 0.0F, -11000.0F);
               RenderSystem.applyModelViewMatrix();
               RenderSystem.setProjectionMatrix(new Matrix4f().setOrtho(0.0F, width, height, 0.0F, 1000.0F, 21000.0F), VertexSorting.ORTHOGRAPHIC_Z);
               RenderSystem.enableBlend();
               RenderSystem.defaultBlendFunc();
               RenderSystem.disableDepthTest();
               GuiGraphics draw = new GuiGraphics(client, client.renderBuffers().bufferSource());
               if (flashlightOn) {
                  draw.setColor(1.0F, 1.0F, 1.0F, darkness);
                  draw.blit(MASK, 0, 0, 0.0F, 0.0F, width, height, 512, 512);
               } else {
                  int alpha = Math.round(darkness * 255.0F);
                  draw.fill(0, 0, width, height, alpha << 24);
               }

               draw.flush();
            } finally {
               RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
               RenderSystem.enableDepthTest();
               RenderSystem.disableBlend();
               modelView.popMatrix();
               RenderSystem.applyModelViewMatrix();
               RenderSystem.restoreProjectionMatrix();
            }
         }
      }
   }
}
