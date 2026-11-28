package net.owleafstudio.modminds.item.renderer;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.item.ItemDisplayContext;
import net.owleafstudio.modminds.item.AtomizadorDeAstrosItem;
import net.owleafstudio.modminds.item.model.AtomizadorDeAstrosItemModel;
import net.owleafstudio.modminds.utils.AnimUtils;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.renderer.GeoItemRenderer;
import com.geckolib.util.RenderUtil;

public class AtomizadorDeAstrosItemRenderer extends GeoItemRenderer<AtomizadorDeAstrosItem> {
   private static final float SCALE_RECIPROCAL = 0.0625F;
   protected boolean renderArms = false;
   protected MultiBufferSource currentBuffer;
   protected RenderType renderType;
   public ItemDisplayContext transformType;
   protected AtomizadorDeAstrosItem animatable;
   private final Set<String> hiddenBones = new HashSet<>();
   private final Set<String> suppressedBones = new HashSet<>();

   public AtomizadorDeAstrosItemRenderer() {
      super(new AtomizadorDeAstrosItemModel());
   }

   public RenderType getRenderType(AtomizadorDeAstrosItem animatable, Identifier texture, MultiBufferSource bufferSource, float partialTick) {
      return net.minecraft.client.renderer.rendertype.RenderTypes.entityTranslucent(this.getTextureLocation(animatable));
   }

   public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack matrixStack, MultiBufferSource bufferIn, int combinedLightIn, int p_239207_6_) {
      this.transformType = transformType;
      super.renderByItem(stack, transformType, matrixStack, bufferIn, combinedLightIn, p_239207_6_);
   }

   public void actuallyRender(
      PoseStack matrixStackIn,
      AtomizadorDeAstrosItem animatable,
      BakedGeoModel model,
      RenderType type,
      MultiBufferSource renderTypeBuffer,
      VertexConsumer vertexBuilder,
      boolean isRenderer,
      float partialTicks,
      int packedLightIn,
      int packedOverlayIn,
      int color
   ) {
      this.currentBuffer = renderTypeBuffer;
      this.renderType = type;
      this.animatable = animatable;
      super.actuallyRender(
         matrixStackIn, animatable, model, type, renderTypeBuffer, vertexBuilder, isRenderer, partialTicks, packedLightIn, packedOverlayIn, color
      );
      if (this.renderArms) {
         this.renderArms = false;
      }
   }

   public void renderRecursively(
      PoseStack stack,
      AtomizadorDeAstrosItem animatable,
      GeoBone bone,
      RenderType type,
      MultiBufferSource buffer,
      VertexConsumer bufferIn,
      boolean isReRender,
      float partialTick,
      int packedLightIn,
      int packedOverlayIn,
      int color
   ) {
      Minecraft mc = Minecraft.getInstance();
      String name = bone.getName();
      boolean renderingArms = false;
      if (!name.equals("izquierda") && !name.equals("derecha")) {
         bone.setHidden(this.hiddenBones.contains(name));
      } else {
         bone.setHidden(true);
         renderingArms = true;
      }

      if (!isReRender && this.transformType.firstPerson() && renderingArms) {
         AbstractClientPlayer player = mc.player;
         float armsAlpha = player.isInvisible() ? 0.15F : 1.0F;
         PlayerRenderer playerRenderer = (PlayerRenderer)mc.getEntityRenderDispatcher().getRenderer(player);
         PlayerModel<AbstractClientPlayer> model = (PlayerModel<AbstractClientPlayer>)playerRenderer.getModel();
         stack.pushPose();
         RenderUtil.translateMatrixToBone(stack, bone);
         RenderUtil.translateToPivotPoint(stack, bone);
         RenderUtil.rotateMatrixAroundBone(stack, bone);
         RenderUtil.scaleMatrixForBone(stack, bone);
         RenderUtil.translateAwayFromPivotPoint(stack, bone);
         Identifier loc = player.getSkin().texture();
         if (name.equals("izquierda")) {
            stack.translate(-0.0625F, 0.125F, 0.0F);
            AnimUtils.renderPartOverBone(
               model.leftArm, bone, stack, buffer.getBuffer(net.minecraft.client.renderer.rendertype.RenderTypes.entitySolid(loc)), packedLightIn, OverlayTexture.NO_OVERLAY, armsAlpha
            );
            AnimUtils.renderPartOverBone(
               model.leftSleeve, bone, stack, buffer.getBuffer(net.minecraft.client.renderer.rendertype.RenderTypes.entityTranslucent(loc)), packedLightIn, OverlayTexture.NO_OVERLAY, armsAlpha
            );
         } else if (name.equals("derecha")) {
            stack.translate(0.0625F, 0.125F, 0.0F);
            AnimUtils.renderPartOverBone(
               model.rightArm, bone, stack, buffer.getBuffer(net.minecraft.client.renderer.rendertype.RenderTypes.entitySolid(loc)), packedLightIn, OverlayTexture.NO_OVERLAY, armsAlpha
            );
            AnimUtils.renderPartOverBone(
               model.rightSleeve, bone, stack, buffer.getBuffer(net.minecraft.client.renderer.rendertype.RenderTypes.entityTranslucent(loc)), packedLightIn, OverlayTexture.NO_OVERLAY, armsAlpha
            );
         }

         buffer.getBuffer(net.minecraft.client.renderer.rendertype.RenderTypes.entityTranslucent(this.getTextureLocation(this.animatable)));
         stack.popPose();
      }

      super.renderRecursively(stack, animatable, bone, type, buffer, bufferIn, isReRender, partialTick, packedLightIn, packedOverlayIn, color);
   }

   public Identifier getTextureLocation(AtomizadorDeAstrosItem instance) {
      return super.getTextureLocation(instance);
   }
}
