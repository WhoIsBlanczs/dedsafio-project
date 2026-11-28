package net.owleafstudio.modminds.client.renderer;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.owleafstudio.modminds.client.model.Modelunknown;
import net.owleafstudio.modminds.entity.BombaAmarillaSinAlasProyectilEntity;

public class BombaAmarillaSinAlasProyectilRenderer extends EntityRenderer<BombaAmarillaSinAlasProyectilEntity, net.owleafstudio.modminds.compat.Compat.EstadoEntidad> {
   private static final Identifier texture = Identifier.parse("modminds:textures/entities/bombaamarilla.png");
   private final Modelunknown model;

   public BombaAmarillaSinAlasProyectilRenderer(EntityRendererProvider.Context context) {
      super(context);
      this.model = new Modelunknown(context.bakeLayer(Modelunknown.LAYER_LOCATION));
   }

   public void render(
      BombaAmarillaSinAlasProyectilEntity entityIn, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn
   ) {
      VertexConsumer vb = bufferIn.getBuffer(net.minecraft.client.renderer.rendertype.RenderTypes.entityCutout(this.getTextureLocation(entityIn)));
      poseStack.pushPose();
      poseStack.rotate(
         Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot()) - 90.0F)
      );
      poseStack.rotate(
         Axis.ZP.rotationDegrees(90.0F + Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot()))
      );
      this.model.renderToBuffer(poseStack, vb, packedLightIn, OverlayTexture.NO_OVERLAY, -1);
      poseStack.popPose();
      super.render(entityIn, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
   }

   public Identifier getTextureLocation(BombaAmarillaSinAlasProyectilEntity entity) {
      return texture;
   }
}
