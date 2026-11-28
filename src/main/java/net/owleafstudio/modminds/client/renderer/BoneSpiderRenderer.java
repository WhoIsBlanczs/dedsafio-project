package net.owleafstudio.modminds.client.renderer;

import net.minecraft.resources.Identifier;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.monster.spider.SpiderModel;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.owleafstudio.modminds.entity.BoneSpiderEntity;

public class BoneSpiderRenderer extends MobRenderer<BoneSpiderEntity, SpiderModel<BoneSpiderEntity>> {
   public BoneSpiderRenderer(EntityRendererProvider.Context context) {
      super(context, new SpiderModel(context.bakeLayer(ModelLayers.SPIDER)), 0.5F);
   }

   public Identifier getTextureLocation(BoneSpiderEntity entity) {
      return Identifier.parse("modminds:textures/entities/spider2-12886658.png");
   }
}
