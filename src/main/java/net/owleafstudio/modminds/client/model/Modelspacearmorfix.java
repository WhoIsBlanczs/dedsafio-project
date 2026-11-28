package net.owleafstudio.modminds.client.model;

import net.minecraft.world.entity.Entity;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;

public class Modelspacearmorfix<T extends net.minecraft.client.renderer.entity.state.EntityRenderState> extends EntityModel<T> {
   public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Identifier.fromNamespaceAndPath("modminds", "modelspacearmorfix"), "main");
   public final ModelPart Head;
   public final ModelPart Body;
   public final ModelPart RightArm;
   public final ModelPart LeftArm;
   public final ModelPart RightLeg;
   public final ModelPart LeftLeg;
   public final ModelPart BootRight;
   public final ModelPart BootLeft;

   public Modelspacearmorfix(ModelPart root) {
      super(root);
      this.Head = root.getChild("Head");
      this.Body = root.getChild("Body");
      this.RightArm = root.getChild("RightArm");
      this.LeftArm = root.getChild("LeftArm");
      this.RightLeg = root.getChild("RightLeg");
      this.LeftLeg = root.getChild("LeftLeg");
      this.BootRight = root.getChild("BootRight");
      this.BootLeft = root.getChild("BootLeft");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition Head = partdefinition.addOrReplaceChild(
         "Head",
         CubeListBuilder.create().texOffs(3, 1).addBox(-5.0F, -9.0F, -5.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition Body = partdefinition.addOrReplaceChild(
         "Body",
         CubeListBuilder.create().texOffs(0, 21).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition wheel_r1 = Body.addOrReplaceChild(
         "wheel_r1",
         CubeListBuilder.create().texOffs(14, 39).addBox(-7.0F, -3.25F, -3.7F, 14.0F, 14.0F, 11.0F, new CubeDeformation(-5.0F)),
         PartPose.offsetAndRotation(0.0F, 0.25F, 0.0F, 0.0F, 3.1416F, 0.0F)
      );
      PartDefinition RightArm = partdefinition.addOrReplaceChild(
         "RightArm",
         CubeListBuilder.create().texOffs(24, 21).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
         PartPose.offset(-5.0F, 2.0F, 0.0F)
      );
      PartDefinition LeftArm = partdefinition.addOrReplaceChild(
         "LeftArm",
         CubeListBuilder.create().texOffs(0, 37).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
         PartPose.offset(5.0F, 2.0F, 0.0F)
      );
      PartDefinition RightLeg = partdefinition.addOrReplaceChild(
         "RightLeg",
         CubeListBuilder.create().texOffs(16, 37).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
         PartPose.offset(-1.9F, 12.0F, 0.0F)
      );
      PartDefinition LeftLeg = partdefinition.addOrReplaceChild(
         "LeftLeg",
         CubeListBuilder.create()
            .texOffs(16, 37)
            .mirror()
            .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F))
            .mirror(false),
         PartPose.offset(1.9F, 12.0F, 0.0F)
      );
      PartDefinition BootRight = partdefinition.addOrReplaceChild(
         "BootRight",
         CubeListBuilder.create().texOffs(44, 0).addBox(-2.35F, 3.9F, -2.5F, 5.0F, 8.0F, 5.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-2.0F, 12.5F, 0.0F)
      );
      PartDefinition BootLeft = partdefinition.addOrReplaceChild(
         "BootLeft",
         CubeListBuilder.create()
            .texOffs(44, 0)
            .mirror()
            .addBox(-2.65F, 3.9F, -2.5F, 5.0F, 8.0F, 5.0F, new CubeDeformation(0.0F))
            .mirror(false),
         PartPose.offset(2.0F, 12.5F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 64, 64);
   }

   

   public void setupAnim(T estado$) {
      super.setupAnim(estado$);
      float ageInTicks = estado$.ageInTicks;
      float limbSwing = 0.0F, limbSwingAmount = 0.0F, netHeadYaw = 0.0F, headPitch = 0.0F;
      if (estado$ instanceof net.minecraft.client.renderer.entity.state.LivingEntityRenderState vivo$) {
         limbSwing = vivo$.walkAnimationPos;
         limbSwingAmount = vivo$.walkAnimationSpeed;
         netHeadYaw = vivo$.yRot;
         headPitch = vivo$.xRot;
      }
      this.RightArm.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * limbSwingAmount;
      this.LeftLeg.xRot = Mth.cos(limbSwing * 1.0F) * -1.0F * limbSwingAmount;
      this.LeftArm.xRot = Mth.cos(limbSwing * 0.6662F) * limbSwingAmount;
      this.RightLeg.xRot = Mth.cos(limbSwing * 1.0F) * 1.0F * limbSwingAmount;
      this.BootLeft.xRot = Mth.cos(limbSwing * 1.0F) * -1.0F * limbSwingAmount;
      this.BootRight.xRot = Mth.cos(limbSwing * 1.0F) * 1.0F * limbSwingAmount;
   }
}
