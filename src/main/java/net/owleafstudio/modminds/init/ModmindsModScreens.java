package net.owleafstudio.modminds.init;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.owleafstudio.modminds.client.gui.GenericBossPanelScreen;
import net.owleafstudio.modminds.client.gui.HydraPanelScreen;
import net.owleafstudio.modminds.client.gui.InterfazEligeAlGanadorScreen;
import net.owleafstudio.modminds.client.gui.LimboTitanPanelScreen;
import net.owleafstudio.modminds.client.gui.PanelOrbScreen;
import net.owleafstudio.modminds.client.gui.ReactorHydraPanelScreen;
import net.owleafstudio.modminds.client.gui.ReviilPanelScreen;
import net.owleafstudio.modminds.client.gui.TresCorazonesScreen;

@Mod.EventBusSubscriber(
   bus = Mod.EventBusSubscriber.Bus.MOD,
   value = {Dist.CLIENT}
)
public class ModmindsModScreens {
   @SubscribeEvent
   public static void clientLoad(FMLClientSetupEvent event) {
      event.enqueueWork(() -> {
         MenuScreens.register(ModmindsModMenus.INTERFAZ_ELIGE_AL_GANADOR.get(), InterfazEligeAlGanadorScreen::new);
         MenuScreens.register(ModmindsModMenus.GANADORES.get(), TresCorazonesScreen::new);
         MenuScreens.register(ModmindsModMenus.HYDRA_PANEL.get(), HydraPanelScreen::new);
         MenuScreens.register(ModmindsModMenus.LIMBO_TITAN_PANEL.get(), LimboTitanPanelScreen::new);
         MenuScreens.register(ModmindsModMenus.REVIIL_PANEL.get(), ReviilPanelScreen::new);
         MenuScreens.register(ModmindsModMenus.PANEL_ORB.get(), PanelOrbScreen::new);
         MenuScreens.register(ModmindsModMenus.REACTOR_HYDRA_PANEL.get(), ReactorHydraPanelScreen::new);
         MenuScreens.register(ModmindsModMenus.BOSS_PANEL.get(), GenericBossPanelScreen::new);
      });
   }
}
