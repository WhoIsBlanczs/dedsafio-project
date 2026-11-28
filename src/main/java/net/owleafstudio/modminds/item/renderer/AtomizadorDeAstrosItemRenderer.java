package net.owleafstudio.modminds.item.renderer;

import com.geckolib.renderer.GeoItemRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.owleafstudio.modminds.item.AtomizadorDeAstrosItem;
import net.owleafstudio.modminds.item.model.AtomizadorDeAstrosItemModel;

/** Arma animada con GeckoLib 5. Los huesos de los brazos del modelo se ocultan. */
public class AtomizadorDeAstrosItemRenderer extends GeoItemRenderer<AtomizadorDeAstrosItem> {
   public AtomizadorDeAstrosItemRenderer() {
      super(new AtomizadorDeAstrosItemModel());
   }

   @Override
   public RenderType getRenderType(GeoRenderState estado, Identifier textura) {
      return RenderTypes.entityTranslucent(textura);
   }

   @Override
   public void adjustModelBonesForRender(RenderPassInfo<GeoRenderState> info, BoneSnapshots snapshots) {
      super.adjustModelBonesForRender(info, snapshots);
      snapshots.ifPresent("derecha", hueso -> hueso.skipRender(true));
      snapshots.ifPresent("izquierda", hueso -> hueso.skipRender(true));
   }
}
