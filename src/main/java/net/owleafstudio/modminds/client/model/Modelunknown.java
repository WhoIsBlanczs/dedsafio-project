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

public class Modelunknown<T extends net.minecraft.client.renderer.entity.state.EntityRenderState> extends EntityModel<T> {
   public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Identifier.fromNamespaceAndPath("modminds", "modelunknown"), "main");
   public final ModelPart head;
   public final ModelPart bone6;
   public final ModelPart bone2;
   public final ModelPart bone;
   public final ModelPart bone5;
   public final ModelPart AlaIzq;
   public final ModelPart AlaDer;

   public Modelunknown(ModelPart root) {
      super(root);
      this.head = root.getChild("head");
      this.bone6 = this.head.getChild("bone6");
      this.bone2 = this.bone6.getChild("bone2");
      this.bone = this.bone6.getChild("bone");
      this.bone5 = this.bone.getChild("bone5");
      this.AlaIzq = this.head.getChild("AlaIzq");
      this.AlaDer = this.head.getChild("AlaDer");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, 18.0F, -1.0F));
      PartDefinition bone6 = head.addOrReplaceChild(
         "bone6",
         CubeListBuilder.create().texOffs(26, 16).addBox(-1.6259F, 0.2666F, -1.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-0.3741F, -4.2666F, 0.0F)
      );
      PartDefinition bone2 = bone6.addOrReplaceChild(
         "bone2",
         CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -6.0F, -3.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.3741F, 8.2666F, 0.0F)
      );
      PartDefinition bone = bone6.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offset(0.3741F, 0.2666F, 1.0F));
      PartDefinition bone_r1 = bone.addOrReplaceChild(
         "bone_r1",
         CubeListBuilder.create().texOffs(26, 22).addBox(-1.0F, -2.5F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-2.25F, -3.85F, 0.0F, -1.0036F, -1.5708F, 0.2618F)
      );
      PartDefinition bone_r2 = bone.addOrReplaceChild(
         "bone_r2",
         CubeListBuilder.create().texOffs(26, 28).addBox(-1.0F, -1.5F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.01F)),
         PartPose.offsetAndRotation(-0.75F, -2.75F, 0.0F, 0.1309F, 0.0F, -1.1781F)
      );
      PartDefinition bone_r3 = bone.addOrReplaceChild(
         "bone_r3",
         CubeListBuilder.create().texOffs(26, 28).addBox(-1.0F, -11.0F, 0.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, 8.0F, -1.75F, -0.0873F, 0.0F, 0.0F)
      );
      PartDefinition bone5 = bone.addOrReplaceChild("bone5", CubeListBuilder.create(), PartPose.offsetAndRotation(-4.225F, -5.1F, 0.0F, -0.3491F, 0.0F, 0.0F));
      PartDefinition bone5_r1 = bone5.addOrReplaceChild(
         "bone5_r1",
         CubeListBuilder.create().texOffs(5, 30).addBox(-2.0F, -2.5F, 0.0F, 3.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(2.1F, -1.25F, 0.6F, -3.1416F, 0.7418F, -3.098F)
      );
      PartDefinition bone5_r2 = bone5.addOrReplaceChild(
         "bone5_r2",
         CubeListBuilder.create().texOffs(0, 31).addBox(-2.0F, -4.5F, 0.0F, 3.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.3491F, 0.0436F)
      );
      PartDefinition AlaIzq = head.addOrReplaceChild("AlaIzq", CubeListBuilder.create(), PartPose.offset(4.0F, 2.0F, 1.0F));
      PartDefinition AlaDer = head.addOrReplaceChild("AlaDer", CubeListBuilder.create(), PartPose.offsetAndRotation(-4.0F, 1.75F, 1.0F, 0.0F, 0.1309F, 0.0F));
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
