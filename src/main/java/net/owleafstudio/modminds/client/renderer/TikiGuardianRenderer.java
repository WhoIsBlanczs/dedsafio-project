package net.owleafstudio.modminds.client.renderer;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.owleafstudio.modminds.entity.TikiGuardianEntity;
import net.owleafstudio.modminds.entity.model.TikiGuardianModel;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.renderer.GeoEntityRenderer;

public class TikiGuardianRenderer extends GeoEntityRenderer<TikiGuardianEntity, net.minecraft.client.renderer.entity.state.LivingEntityRenderState> {
   private static final String CHAINS = "chanes";

   public TikiGuardianRenderer(EntityRendererProvider.Context renderManager) {
      super(renderManager, new TikiGuardianModel());
      this.shadowRadius = 0.5F;
   }

   public RenderType getRenderType(net.minecraft.client.renderer.entity.state.LivingEntityRenderState estado$, Identifier texture) {
      return net.minecraft.client.renderer.rendertype.RenderTypes.entityTranslucent(texture);
   }

   private static final com.geckolib.constant.dataticket.DataTicket<Boolean> FURIA = com.geckolib.constant.dataticket.DataTicket.create("modminds_tiki_furia", Boolean.class);

   @Override
   public void captureDefaultRenderState(TikiGuardianEntity entity, Void relacionado$, net.minecraft.client.renderer.entity.state.LivingEntityRenderState estado$, float partialTick) {
      super.captureDefaultRenderState(entity, relacionado$, estado$, partialTick);
      ((com.geckolib.renderer.base.GeoRenderState)estado$).addGeckolibData(FURIA, entity.isFuria());
   }

   @Override
   public void adjustModelBonesForRender(com.geckolib.renderer.base.RenderPassInfo<net.minecraft.client.renderer.entity.state.LivingEntityRenderState> info, com.geckolib.renderer.base.BoneSnapshots snapshots) {
      super.adjustModelBonesForRender(info, snapshots);
      boolean furia = info.getOrDefaultGeckolibData(FURIA, false);
      snapshots.ifPresent(CHAINS, bone -> {
         bone.skipRender(furia);
         bone.skipChildrenRender(furia);
      });
   }
}
