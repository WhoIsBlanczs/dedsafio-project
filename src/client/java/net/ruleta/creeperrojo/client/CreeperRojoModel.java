package net.ruleta.creeperrojo.client;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.state.CreeperRenderState;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class CreeperRojoModel extends EntityModel<CreeperRenderState> {
   public static final ModelLayerLocation CAPA = new ModelLayerLocation(Identifier.fromNamespaceAndPath("ruletafanproject", "creeper_rojo"), "main");
   private final ModelPart raiz;
   private final ModelPart cabeza;
   private final ModelPart pataTraseraDerecha;
   private final ModelPart pataTraseraIzquierda;
   private final ModelPart pataDelanteraDerecha;
   private final ModelPart pataDelanteraIzquierda;

   public CreeperRojoModel(ModelPart raiz) {
      super(raiz);
      this.raiz = raiz;
      this.cabeza = raiz.getChild("cabeza");
      this.pataTraseraDerecha = raiz.getChild("pata_trasera_derecha");
      this.pataTraseraIzquierda = raiz.getChild("pata_trasera_izquierda");
      this.pataDelanteraDerecha = raiz.getChild("pata_delantera_derecha");
      this.pataDelanteraIzquierda = raiz.getChild("pata_delantera_izquierda");
   }

   public static LayerDefinition crearCapa() {
      MeshDefinition malla = new MeshDefinition();
      PartDefinition r = malla.getRoot();
      r.addOrReplaceChild("cabeza", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F), PartPose.offset(0.0F, 18.0F, 0.0F));
      r.addOrReplaceChild(
         "cuerpo",
         CubeListBuilder.create().texOffs(32, 0).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 2.0F, 4.0F).texOffs(32, 10).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 1.0F, 8.0F),
         PartPose.offset(0.0F, 20.0F, 0.0F)
      );
      CubeListBuilder pata = CubeListBuilder.create().texOffs(0, 18).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 3.0F, 3.0F);
      r.addOrReplaceChild("pata_trasera_derecha", pata, PartPose.offset(-3.0F, 21.0F, 3.0F));
      r.addOrReplaceChild("pata_trasera_izquierda", pata, PartPose.offset(3.0F, 21.0F, 3.0F));
      r.addOrReplaceChild("pata_delantera_derecha", pata, PartPose.offset(-3.0F, 21.0F, -3.0F));
      r.addOrReplaceChild("pata_delantera_izquierda", pata, PartPose.offset(3.0F, 21.0F, -3.0F));
      return LayerDefinition.create(malla, 64, 32);
   }

   @Override
   public void setupAnim(CreeperRenderState estado) {
      super.setupAnim(estado);
      float paso = estado.walkAnimationPos;
      float cantidadPaso = estado.walkAnimationSpeed;
      float giroCabeza = estado.yRot;
      this.cabeza.yRot = giroCabeza * (float) (Math.PI / 180.0) * 0.3F;
      float a = Mth.cos(paso * 0.6662F) * 1.4F * cantidadPaso;
      float b = Mth.cos(paso * 0.6662F + (float) Math.PI) * 1.4F * cantidadPaso;
      this.pataTraseraDerecha.xRot = a;
      this.pataTraseraIzquierda.xRot = b;
      this.pataDelanteraDerecha.xRot = b;
      this.pataDelanteraIzquierda.xRot = a;
   }
}
