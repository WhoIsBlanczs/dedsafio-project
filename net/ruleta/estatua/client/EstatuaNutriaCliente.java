package net.ruleta.estatua.client;

import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.level.ItemLike;
import net.ruleta.estatua.EstatuaNutria;

public final class EstatuaNutriaCliente {
   private EstatuaNutriaCliente() {
   }

   public static void registrar() {
      BlockEntityRenderers.register(EstatuaNutria.BE.get(), EstatuaNutriaRenderer::new);
      BuiltinItemRendererRegistry.INSTANCE.register((ItemLike)EstatuaNutria.ITEM.get(), new EstatuaNutriaItemRenderer());
   }
}
