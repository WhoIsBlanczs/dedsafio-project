package com.example.dedsafioimitationmod.client;

import com.example.dedsafioimitationmod.DedsafioImitationMod;
import com.example.dedsafioimitationmod.entity.NautilusOseoPEntity;
import com.geckolib.model.DefaultedEntityGeoModel;
import com.geckolib.renderer.GeoEntityRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

@Environment(EnvType.CLIENT)
public class NautilusOseoPRenderer extends GeoEntityRenderer<NautilusOseoPEntity, LivingEntityRenderState> {
   public NautilusOseoPRenderer(Context context) {
      super(context, new DefaultedEntityGeoModel<>(DedsafioImitationMod.id("nautilus_oseo_p")));
   }
}
