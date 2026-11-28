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
import net.owleafstudio.modminds.client.model.ModelDardo;
import net.owleafstudio.modminds.entity.BalaDeDilitioProyectilEntity;

public class BalaDeDilitioProyectilRenderer extends EntityRenderer<BalaDeDilitioProyectilEntity, net.owleafstudio.modminds.compat.Compat.EstadoEntidad> {
   private static final Identifier texture = Identifier.parse("modminds:textures/entities/balaespacial.png");
   private final ModelDardo model;

   public BalaDeDilitioProyectilRenderer(EntityRendererProvider.Context context) {
      super(context);
      this.model = new ModelDardo(context.bakeLayer(ModelDardo.LAYER_LOCATION));
   }

   public net.owleafstudio.modminds.compat.Compat.EstadoEntidad createRenderState() {
      return new net.owleafstudio.modminds.compat.Compat.EstadoEntidad();
   }

   public void extractRenderState(BalaDeDilitioProyectilEntity entidad$, net.owleafstudio.modminds.compat.Compat.EstadoEntidad estado$, float pt$) {
      super.extractRenderState(entidad$, estado$, pt$);
      estado$.entidad = entidad$;
      estado$.pt = pt$;
   }

   public void submit(net.owleafstudio.modminds.compat.Compat.EstadoEntidad estado$, PoseStack poseStack, net.minecraft.client.renderer.SubmitNodeCollector colector$, net.minecraft.client.renderer.state.level.CameraRenderState camara$) {
      BalaDeDilitioProyectilEntity entityIn = (BalaDeDilitioProyectilEntity)estado$.entidad;
      float partialTicks = estado$.pt;
      float entityYaw = entityIn.getYRot();
      int packedLightIn = estado$.lightCoords;
      net.minecraft.client.renderer.rendertype.RenderType vb$tipo = (net.minecraft.client.renderer.rendertype.RenderTypes.entityCutout(this.getTextureLocation(entityIn)));
      poseStack.pushPose();
      poseStack.rotate(
         Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot()) - 90.0F)
      );
      poseStack.rotate(
         Axis.ZP.rotationDegrees(90.0F + Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot()))
      );
      colector$.submitModel(this.model, estado$, poseStack, vb$tipo, packedLightIn, OverlayTexture.NO_OVERLAY, estado$.outlineColor);
      poseStack.popPose();
      super.submit(estado$, poseStack, colector$, camara$);
   }

   public Identifier getTextureLocation(BalaDeDilitioProyectilEntity entity) {
      return texture;
   }
}
