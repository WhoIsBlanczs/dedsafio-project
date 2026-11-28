package com.novastudio.dedsafio4;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.resources.Identifier;
import net.minecraft.client.Minecraft;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.model.DefaultedEntityGeoModel;
import com.geckolib.renderer.GeoEntityRenderer;

@Environment(EnvType.CLIENT)
public final class GlebanoidesClientMod implements ClientModInitializer {
   public void onInitializeClient() {
      NexoMobs.JUMPING = player -> player == Minecraft.getInstance().player && Minecraft.getInstance().options.keyJump.isDown();
      geo(NexoMobs.NAVE, "nave_dilitio");
      geo(NexoMobs.GUSANO, "gusano_glebanoide");
      geo(NexoMobs.NAUTILUS, "nautilus_glebanoide");
      geo(NexoMobs.CEREARANA, "cerearana");
   }

   private static <T extends Entity & GeoAnimatable> void geo(EntityType<T> type, String id) {
      float scale = id.equals("nautilus_glebanoide") ? 1.5F : 1.0F;
      EntityRendererRegistry.register(
         type, ctx -> new GeoEntityRenderer(ctx, new DefaultedEntityGeoModel(Identifier.fromNamespaceAndPath("nexo", id))).withScale(scale)
      );
   }
}
