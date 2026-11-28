package com.watonstudio.creepermetalico;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import com.geckolib.renderer.GeoEntityRenderer;

@Environment(EnvType.CLIENT)
public class CreeperNuclearRenderer extends GeoEntityRenderer<CreeperNuclearEntity, net.minecraft.client.renderer.entity.state.LivingEntityRenderState> {
   public CreeperNuclearRenderer(Context context) {
      super(context, new CreeperNuclearModel());
      this.shadowRadius = 0.5F;
   }
}
