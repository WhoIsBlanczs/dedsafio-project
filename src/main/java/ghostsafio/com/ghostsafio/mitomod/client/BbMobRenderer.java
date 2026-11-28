package com.ghostsafio.mitomod.client;

import com.ghostsafio.mitomod.AttackAnimMob;
import com.ghostsafio.mitomod.CerebroEntity;
import com.ghostsafio.mitomod.Ghostsafio;
import com.ghostsafio.mitomod.PodPhoraEntity;
import com.ghostsafio.mitomod.SkiffPhoraEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

@Environment(EnvType.CLIENT)
public class BbMobRenderer<T extends Mob> extends EntityRenderer<T> {
   public static float yawOffset = 0.0F;
   private final ResourceLocation texture;
   private final ResourceLocation modelId;
   private final float scale;
   private final float yShift;
   private final float zShift;
   private final float modelYaw;
   private BbModel model;
   private BbModel.Pose pose;
   private boolean failed = false;

   public BbMobRenderer(Context context, String name, float scale, float yShift, float zShift, float modelYaw) {
      super(context);
      this.texture = Ghostsafio.id("textures/entity/" + name + ".png");
      this.modelId = Ghostsafio.id("bb/" + name + ".json");
      this.scale = scale;
      this.yShift = yShift;
      this.zShift = zShift;
      this.modelYaw = modelYaw;
      this.shadowRadius = 0.4F * scale;
   }

   public ResourceLocation getTextureLocation(T entity) {
      return this.texture;
   }

   public void render(T mob, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
      if (this.model == null && !this.failed) {
         try {
            this.model = BbModel.load(Minecraft.getInstance().getResourceManager(), this.modelId);
            this.pose = new BbModel.Pose(this.model.boneCount());
         } catch (Exception e) {
            this.failed = true;
            Ghostsafio.LOGGER.error("No se pudo cargar el modelo {}", this.modelId, e);
         }
      }

      if (this.model != null) {
         this.pose.reset();
         float time = (mob.tickCount + partialTick) / 20.0F;
         if (mob.deathTime <= 0 && !mob.isDeadOrDying()) {
            if (mob instanceof AttackAnimMob attacking && attacking.getAttackAnim() > 0) {
               float attackTime = (attacking.attackAnimTicks() - attacking.getAttackAnim() + partialTick) / 20.0F;
               this.model.applyClip(this.model.clip("ataca"), attackTime, this.pose);
            } else {
               this.model.applyClip(this.model.clip("idle"), time, this.pose);
               double dx = mob.getX() - mob.xo;
               double dz = mob.getZ() - mob.zo;
               double dy = Math.abs(mob.getY() - mob.yo);
               if (!(mob instanceof CerebroEntity cb0 && cb0.isAttached()) && dx * dx + dz * dz + dy * dy > 4.0E-4) {
                  this.model.applyClip(this.model.clip("walk"), time, this.pose);
               }
            }
         } else {
            this.model.applyClip(this.model.clip("death"), (mob.deathTime + partialTick) / 20.0F, this.pose);
         }

         poseStack.pushPose();
         if (mob instanceof CerebroEntity cb && cb.getHost() != null) {
            Entity host = cb.getHost();
            Vec3 h = host.getPosition(partialTick);
            Vec3 self = mob.getPosition(partialTick);
            poseStack.translate(h.x - self.x, h.y + host.getBbHeight() - 0.1 - self.y, h.z - self.z);
         }

         float body = Mth.rotLerp(partialTick, mob.yBodyRotO, mob.yBodyRot);
         poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - body + this.modelYaw + yawOffset));
         poseStack.translate(0.0, this.yShift * this.scale, this.zShift * this.scale);
         poseStack.scale(this.scale, this.scale, this.scale);
         if (mob instanceof SkiffPhoraEntity || mob instanceof PodPhoraEntity) {
            poseStack.translate(0.0, Math.sin(time * 2.2) * 0.06, 0.0);
         }

         VertexConsumer vc = buffers.getBuffer(RenderType.entityCutoutNoCull(this.texture));
         int overlay = OverlayTexture.pack(OverlayTexture.u(0.0F), OverlayTexture.v(mob.hurtTime > 0 || mob.deathTime > 0));
         this.model.render(poseStack, vc, packedLight, overlay, this.pose, false);
         poseStack.popPose();
         super.render(mob, entityYaw, partialTick, poseStack, buffers, packedLight);
      }
   }
}
