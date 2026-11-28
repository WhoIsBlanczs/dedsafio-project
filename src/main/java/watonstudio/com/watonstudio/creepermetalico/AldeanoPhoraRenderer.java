package com.watonstudio.creepermetalico;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@Environment(EnvType.CLIENT)
public class AldeanoPhoraRenderer extends GeoEntityRenderer<AldeanoPhoraEntity> {
   public AldeanoPhoraRenderer(Context context) {
      super(context, new AldeanoPhoraModel());
      this.withScale(0.8F, 0.8F);
      this.shadowRadius = 0.0F;
   }
}
