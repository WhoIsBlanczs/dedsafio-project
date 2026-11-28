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

public class Modeltridentcustom<T extends net.minecraft.client.renderer.entity.state.EntityRenderState> extends EntityModel<T> {
   public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Identifier.fromNamespaceAndPath("modminds", "modeltridentcustom"), "main");
   public final ModelPart body;
   public final ModelPart base;
   public final ModelPart left_spike;
   public final ModelPart middle_spike;
   public final ModelPart right_spike;

   public Modeltridentcustom(ModelPart root) {
      super(root);
      this.body = root.getChild("body");
      this.base = root.getChild("base");
      this.left_spike = root.getChild("left_spike");
      this.middle_spike = root.getChild("middle_spike");
      this.right_spike = root.getChild("right_spike");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition body = partdefinition.addOrReplaceChild(
         "body",
         CubeListBuilder.create().texOffs(0, 6).addBox(-0.5F, 2.0F, -0.5F, 1.0F, 25.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, -3.0F, 0.0F)
      );
      PartDefinition base = partdefinition.addOrReplaceChild(
         "base",
         CubeListBuilder.create().texOffs(4, 0).addBox(-1.5F, 0.0F, -0.5F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, -3.0F, 0.0F)
      );
      PartDefinition left_spike = partdefinition.addOrReplaceChild(
         "left_spike",
         CubeListBuilder.create().texOffs(4, 3).addBox(-2.5F, -3.0F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, -3.0F, 0.0F)
      );
      PartDefinition middle_spike = partdefinition.addOrReplaceChild(
         "middle_spike",
         CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -4.0F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, -3.0F, 0.0F)
      );
      PartDefinition right_spike = partdefinition.addOrReplaceChild(
         "right_spike",
         CubeListBuilder.create()
            .texOffs(4, 3)
            .mirror()
            .addBox(1.5F, -3.0F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
            .mirror(false),
         PartPose.offset(0.0F, -3.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 32, 32);
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
