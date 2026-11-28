package net.owleafstudio.modminds.init;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.owleafstudio.modminds.client.model.ModelArmadura_Infernal;
import net.owleafstudio.modminds.client.model.ModelArmadura_de_Dilitio;
import net.owleafstudio.modminds.client.model.ModelDardo;
import net.owleafstudio.modminds.client.model.Modeldisparo;
import net.owleafstudio.modminds.client.model.Modelnave_enemiga_proyectil;
import net.owleafstudio.modminds.client.model.Modelreviil_shoot;
import net.owleafstudio.modminds.client.model.Modelspacearmorfix;
import net.owleafstudio.modminds.client.model.Modeltridentcustom;
import net.owleafstudio.modminds.client.model.Modelunknown;

@Mod.EventBusSubscriber(
   bus = Mod.EventBusSubscriber.Bus.MOD,
   value = {Dist.CLIENT}
)
public class ModmindsModModels {
   @SubscribeEvent
   public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
      event.registerLayerDefinition(Modelunknown.LAYER_LOCATION, Modelunknown::createBodyLayer);
      event.registerLayerDefinition(Modeltridentcustom.LAYER_LOCATION, Modeltridentcustom::createBodyLayer);
      event.registerLayerDefinition(ModelArmadura_Infernal.LAYER_LOCATION, ModelArmadura_Infernal::createBodyLayer);
      event.registerLayerDefinition(Modelreviil_shoot.LAYER_LOCATION, Modelreviil_shoot::createBodyLayer);
      event.registerLayerDefinition(ModelArmadura_de_Dilitio.LAYER_LOCATION, ModelArmadura_de_Dilitio::createBodyLayer);
      event.registerLayerDefinition(ModelDardo.LAYER_LOCATION, ModelDardo::createBodyLayer);
      event.registerLayerDefinition(Modelspacearmorfix.LAYER_LOCATION, Modelspacearmorfix::createBodyLayer);
      event.registerLayerDefinition(Modeldisparo.LAYER_LOCATION, Modeldisparo::createBodyLayer);
      event.registerLayerDefinition(Modelnave_enemiga_proyectil.LAYER_LOCATION, Modelnave_enemiga_proyectil::createBodyLayer);
   }
}
