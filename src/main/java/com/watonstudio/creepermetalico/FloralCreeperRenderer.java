package com.watonstudio.creepermetalico;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import com.geckolib.renderer.GeoEntityRenderer;

@Environment(EnvType.CLIENT)
public class FloralCreeperRenderer extends GeoEntityRenderer<FloralCreeperEntity, net.minecraft.client.renderer.entity.state.LivingEntityRenderState> {
   public FloralCreeperRenderer(Context context) {
      super(context, new FloralCreeperModel());
   }
}
