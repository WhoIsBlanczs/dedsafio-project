package net.ruleta.arboles;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;

public final class ArbolesCliente {
   private ArbolesCliente() {
   }

   public static void registrar() {
      BlockRenderLayerMap.INSTANCE.putBlock(Arboles.BROTE_COSTA.get(), RenderType.cutout());
      BlockRenderLayerMap.INSTANCE.putBlock(Arboles.BROTE_PALMERA.get(), RenderType.cutout());
   }
}
