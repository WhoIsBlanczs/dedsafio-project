package com.example.dedsafioimitationmod.client;

import com.example.dedsafioimitationmod.entity.CerebroAmarilloEntity;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.RenderPassInfo;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.player.Player;

@Environment(EnvType.CLIENT)
public class CerebroAmarilloRenderer extends GeoEntityRenderer<CerebroAmarilloEntity, LivingEntityRenderState> {
   private static final double ALTURA = -0.5;
   private static final DataTicket<Boolean> SOBRE_JUGADOR = DataTicket.create("dedsafioimitationmod_cerebro_amarillo_sobre_jugador", Boolean.class);

   public CerebroAmarilloRenderer(Context context) {
      super(context, new com.geckolib.model.DefaultedEntityGeoModel<>(com.example.dedsafioimitationmod.DedsafioImitationMod.id("cerebro_amarillo")));
   }

   @Override
   public boolean shouldRender(CerebroAmarilloEntity entity, Frustum frustum, double x, double y, double z, float partialTick) {
      return entity.isPassenger() ? true : super.shouldRender(entity, frustum, x, y, z, partialTick);
   }

   @Override
   public void captureDefaultRenderState(CerebroAmarilloEntity entity, Void relatedObject, LivingEntityRenderState state, float partialTick) {
      super.captureDefaultRenderState(entity, relatedObject, state, partialTick);
      state.addGeckolibData(SOBRE_JUGADOR, entity.getVehicle() instanceof Player);
   }

   @Override
   public void adjustRenderPose(RenderPassInfo<LivingEntityRenderState> renderPassInfo) {
      super.adjustRenderPose(renderPassInfo);
      // Cuando va pegado a la cabeza de un jugador se baja medio bloque, igual que antes.
      if (renderPassInfo.getOrDefaultGeckolibData(SOBRE_JUGADOR, false)) {
         renderPassInfo.poseStack().translate(0.0, ALTURA, 0.0);
      }
   }
}
