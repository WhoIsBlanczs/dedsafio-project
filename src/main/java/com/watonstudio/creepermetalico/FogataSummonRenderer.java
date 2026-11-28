package com.watonstudio.creepermetalico;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import com.geckolib.renderer.GeoEntityRenderer;

@Environment(EnvType.CLIENT)
public final class FogataSummonRenderer extends GeoEntityRenderer<FogataSummonEntity, net.minecraft.client.renderer.entity.state.EntityRenderState> {
   public FogataSummonRenderer(Context context) {
      super(context, new FogataSummonModel());
      this.shadowRadius = 0.0F;
      this.withScale(0.65F);
   }
}
