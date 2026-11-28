package com.watonstudio.creepermetalico;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@Environment(EnvType.CLIENT)
public class NautilusOseoRenderer extends GeoEntityRenderer<NautilusOseoEntity> {
   public NautilusOseoRenderer(Context context) {
      super(context, new NautilusOseoModel());
      this.shadowRadius = 0.6F;
   }
}
