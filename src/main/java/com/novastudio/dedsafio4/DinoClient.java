package com.novastudio.dedsafio4;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.Identifier;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.model.DefaultedEntityGeoModel;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.GeoEntityRenderer;

@Environment(EnvType.CLIENT)
public final class DinoClient {
   private DinoClient() {
   }

   public static void register() {
      PteroEscapeClient.register();
      DinoContent.BEAST_TYPES
         .forEach((var0, var1) -> EntityRendererRegistry.register(var1, var1x -> renderer(var1x, var0, var0.equals("quetzalcoatlus") ? 0.7F : 1.3F)));
      EntityRendererRegistry.register(DinoContent.RAPTOR, var0 -> renderer(var0, "raptor", 0.9F));
      EntityRendererRegistry.register(DinoContent.PTERODACTILO, var0 -> renderer(var0, "pterodactilo", 0.8F));
   }

   private static <T extends Entity & GeoAnimatable> GeoEntityRenderer<T, net.minecraft.client.renderer.entity.state.LivingEntityRenderState> renderer(EntityRendererProvider.Context var0, String var1, float var2) {
      return new GeoEntityRenderer<T, net.minecraft.client.renderer.entity.state.LivingEntityRenderState>(var0, new DefaultedEntityGeoModel<T>(Identifier.fromNamespaceAndPath("nexo", var1))) {
         {
            this.shadowRadius = var2;
         }
      };
   }
}
