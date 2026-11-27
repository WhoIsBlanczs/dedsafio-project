package net.ruleta.arboles;


public final class ArbolesCliente {
   private ArbolesCliente() {
   }

   public static void registrar() {
      BlockRenderLayerMap.INSTANCE.putBlock(Arboles.BROTE_COSTA.get(), RenderType.cutout());
      BlockRenderLayerMap.INSTANCE.putBlock(Arboles.BROTE_PALMERA.get(), RenderType.cutout());
   }
}
