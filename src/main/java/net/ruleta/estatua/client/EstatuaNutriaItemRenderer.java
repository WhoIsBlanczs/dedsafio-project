package net.ruleta.estatua.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry.DynamicItemRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class EstatuaNutriaItemRenderer implements DynamicItemRenderer {
   public void render(ItemStack stack, ItemDisplayContext modo, PoseStack ps, MultiBufferSource buf, int luz, int overlay) {
      ps.pushPose();
      ps.translate(0.5, 0.0, 0.5);
      EstatuaNutriaRenderer.dibujar(ps, buf, luz, overlay);
      ps.popPose();
   }
}
