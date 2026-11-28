package com.watonstudio.creepermetalico;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@Environment(EnvType.CLIENT)
public class CreeperNuclearRenderer extends GeoEntityRenderer<CreeperNuclearEntity> {
   public CreeperNuclearRenderer(Context context) {
      super(context, new CreeperNuclearModel());
      this.shadowRadius = 0.5F;
   }
}
