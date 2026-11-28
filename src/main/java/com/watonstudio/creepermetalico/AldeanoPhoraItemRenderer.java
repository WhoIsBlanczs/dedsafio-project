package com.watonstudio.creepermetalico;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.geckolib.renderer.GeoItemRenderer;

@Environment(EnvType.CLIENT)
public class AldeanoPhoraItemRenderer extends GeoItemRenderer<AldeanoPhoraItem> {
   public AldeanoPhoraItemRenderer() {
      super(new AldeanoPhoraItemModel());
   }
}
