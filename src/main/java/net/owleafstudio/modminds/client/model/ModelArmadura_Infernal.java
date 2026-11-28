package net.owleafstudio.modminds.client.model;

import net.minecraft.world.entity.Entity;
import net.minecraft.resources.Identifier;
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

public class ModelArmadura_Infernal<T extends net.minecraft.client.renderer.entity.state.EntityRenderState> extends EntityModel<T> {
   public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Identifier.fromNamespaceAndPath("modminds", "model_armadura_infernal"), "main");
   public final ModelPart Head;
   public final ModelPart Body;
   public final ModelPart RightArm;
   public final ModelPart LeftArm;
   public final ModelPart RightLeg;
   public final ModelPart LeftLeg;
   public final ModelPart RightBoot;
   public final ModelPart LeftBoot;

   public ModelArmadura_Infernal(ModelPart root) {
      super(root);
      this.Head = root.getChild("Head");
      this.Body = root.getChild("Body");
      this.RightArm = root.getChild("RightArm");
      this.LeftArm = root.getChild("LeftArm");
      this.RightLeg = root.getChild("RightLeg");
      this.LeftLeg = root.getChild("LeftLeg");
      this.RightBoot = root.getChild("RightBoot");
      this.LeftBoot = root.getChild("LeftBoot");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition Head = partdefinition.addOrReplaceChild(
         "Head",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F))
            .texOffs(32, 41)
            .addBox(0.0F, -12.0F, -3.5F, 0.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(32, 41)
            .addBox(0.0F, -12.0F, 0.5F, 0.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(28, 43)
            .addBox(0.0F, -9.0F, 5.0F, 0.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(24, 43)
            .addBox(0.0F, -6.0F, 5.0F, 0.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition CuernoBaseSuperiorDerecho_r1 = Head.addOrReplaceChild(
         "CuernoBaseSuperiorDerecho_r1",
         CubeListBuilder.create()
            .texOffs(25, 32)
            .mirror()
            .addBox(-2.0F, -2.0F, 0.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
            .mirror(false),
         PartPose.offsetAndRotation(4.6F, -6.5F, -2.0F, -3.137F, 0.7955F, 2.3087F)
      );
      PartDefinition CuernoLargoSuperiorDerecho_r1 = Head.addOrReplaceChild(
         "CuernoLargoSuperiorDerecho_r1",
         CubeListBuilder.create()
            .texOffs(25, 37)
            .mirror()
            .addBox(0.0F, -3.0F, 0.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
            .mirror(false),
         PartPose.offsetAndRotation(4.8F, -8.9F, -2.5F, 1.0145F, 0.0131F, 0.7295F)
      );
      PartDefinition CuernoBaseSuperiorIzquierdo_r1 = Head.addOrReplaceChild(
         "CuernoBaseSuperiorIzquierdo_r1",
         CubeListBuilder.create().texOffs(25, 32).addBox(0.0F, -2.0F, 0.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-4.6F, -6.5F, -2.0F, -3.137F, -0.7955F, -2.3087F)
      );
      PartDefinition CuernoLargoSuperiorIzquierdo_r1 = Head.addOrReplaceChild(
         "CuernoLargoSuperiorIzquierdo_r1",
         CubeListBuilder.create().texOffs(25, 37).addBox(-1.0F, -3.0F, 0.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-4.8F, -8.9F, -2.5F, 1.0145F, -0.0131F, -0.7295F)
      );
      PartDefinition Body = partdefinition.addOrReplaceChild(
         "Body",
         CubeListBuilder.create()
            .texOffs(16, 16)
            .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
            .texOffs(0, 32)
            .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.75F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition CuernoBaseSuperiorIzquierdo_r2 = Body.addOrReplaceChild(
         "CuernoBaseSuperiorIzquierdo_r2",
         CubeListBuilder.create()
            .texOffs(25, 32)
            .mirror()
            .addBox(-2.0F, -2.0F, 0.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
            .mirror(false),
         PartPose.offsetAndRotation(3.6F, -0.4F, 1.8F, -0.3937F, 0.1608F, -1.7127F)
      );
      PartDefinition CuernoLargoSuperiorIzquierdo_r2 = Body.addOrReplaceChild(
         "CuernoLargoSuperiorIzquierdo_r2",
         CubeListBuilder.create()
            .texOffs(25, 37)
            .mirror()
            .addBox(0.0F, -3.0F, 0.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
            .mirror(false),
         PartPose.offsetAndRotation(3.1F, 0.0F, 4.3F, -1.1598F, 0.3785F, -0.0811F)
      );
      PartDefinition CuernoLargoSuperiorDerecho_r2 = Body.addOrReplaceChild(
         "CuernoLargoSuperiorDerecho_r2",
         CubeListBuilder.create().texOffs(25, 37).addBox(-1.0F, -3.0F, 0.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-3.1F, 0.0F, 4.3F, -1.1598F, -0.3785F, 0.0811F)
      );
      PartDefinition CuernoBaseSuperiorDerecho_r2 = Body.addOrReplaceChild(
         "CuernoBaseSuperiorDerecho_r2",
         CubeListBuilder.create().texOffs(25, 32).addBox(0.0F, -2.0F, 0.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-3.6F, -0.4F, 1.8F, -0.3937F, -0.1608F, 1.7127F)
      );
      PartDefinition RightArm = partdefinition.addOrReplaceChild(
         "RightArm",
         CubeListBuilder.create().texOffs(40, 16).addBox(-3.1F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
         PartPose.offset(-1.9F, 12.0F, 0.0F)
      );
      PartDefinition LeftArm = partdefinition.addOrReplaceChild(
         "LeftArm",
         CubeListBuilder.create()
            .texOffs(40, 16)
            .mirror()
            .addBox(-0.9F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
            .mirror(false),
         PartPose.offset(1.9F, 12.0F, 0.0F)
      );
      PartDefinition RightLeg = partdefinition.addOrReplaceChild(
         "RightLeg",
         CubeListBuilder.create().texOffs(0, 16).addBox(-1.8F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)),
         PartPose.offset(1.9F, 12.0F, 0.0F)
      );
      PartDefinition LeftLeg = partdefinition.addOrReplaceChild(
         "LeftLeg",
         CubeListBuilder.create()
            .texOffs(0, 16)
            .mirror()
            .addBox(-2.2F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F))
            .mirror(false),
         PartPose.offset(-1.9F, 12.0F, 0.0F)
      );
      PartDefinition RightBoot = partdefinition.addOrReplaceChild(
         "RightBoot",
         CubeListBuilder.create().texOffs(0, 48).addBox(-1.9F, -0.1F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
         PartPose.offset(0.0F, 24.0F, 0.0F)
      );
      PartDefinition LeftBoot = partdefinition.addOrReplaceChild(
         "LeftBoot",
         CubeListBuilder.create()
            .texOffs(0, 48)
            .mirror()
            .addBox(-2.1F, -0.1F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
            .mirror(false),
         PartPose.offset(0.0F, 24.0F, 0.0F)
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
   }
}
