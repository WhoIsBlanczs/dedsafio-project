package net.owleafstudio.modminds.world.dimension;

import net.minecraft.world.phys.Vec3;
import net.minecraft.resources.Identifier;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class MounstrosityDimensionDimension {
   @Mod.EventBusSubscriber(
      bus = Mod.EventBusSubscriber.Bus.MOD
   )
   public static class MounstrosityDimensionSpecialEffectsHandler {
      @SubscribeEvent
      @OnlyIn(Dist.CLIENT)
      public static void registerDimensionSpecialEffects(RegisterDimensionSpecialEffectsEvent event) {
         DimensionSpecialEffects customEffect = new DimensionSpecialEffects(Float.NaN, true, DimensionSpecialEffects.SkyType.NONE, false, false) {
            public Vec3 getBrightnessDependentFogColor(Vec3 color, float sunHeight) {
               return new Vec3(0.0, 0.0, 0.0);
            }

            public boolean isFoggyAt(int x, int y) {
               return false;
            }
         };
         event.register(Identifier.parse("modminds:mounstrosity_dimension"), customEffect);
      }
   }
}
