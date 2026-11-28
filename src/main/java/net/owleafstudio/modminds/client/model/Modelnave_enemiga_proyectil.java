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

public class Modelnave_enemiga_proyectil<T extends net.minecraft.client.renderer.entity.state.EntityRenderState> extends EntityModel<T> {
   public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Identifier.fromNamespaceAndPath("modminds", "modelnave_enemiga_proyectil"), "main");
   public final ModelPart bone;

   public Modelnave_enemiga_proyectil(ModelPart root) {
      super(root);
      this.bone = root.getChild("bone");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
      PartDefinition cube_r1 = bone.addOrReplaceChild(
         "cube_r1",
         CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -0.5F, -40.0F, 1.0F, 1.0F, 80.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.1F, -0.5F, 0.5F, 0.0F, -1.5708F, -1.5708F)
      );
      return LayerDefinition.create(meshdefinition, 256, 256);
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
