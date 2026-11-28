package com.watonstudio.creepermetalico;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@Environment(EnvType.CLIENT)
public class FloralCreeperRenderer extends GeoEntityRenderer<FloralCreeperEntity> {
   public FloralCreeperRenderer(Context context) {
      super(context, new FloralCreeperModel());
   }
}
