package net.owleafstudio.modminds.init;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.owleafstudio.modminds.world.inventory.BossPanelMenu;
import net.owleafstudio.modminds.world.inventory.HydraPanelMenu;
import net.owleafstudio.modminds.world.inventory.InterfazEligeAlGanadorMenu;
import net.owleafstudio.modminds.world.inventory.InventarioMenu;
import net.owleafstudio.modminds.world.inventory.LimboTitanPanelMenu;
import net.owleafstudio.modminds.world.inventory.PanelOrbMenu;
import net.owleafstudio.modminds.world.inventory.ReactorHydraPanelMenu;
import net.owleafstudio.modminds.world.inventory.ReviilPanelMenu;
import net.owleafstudio.modminds.world.inventory.TresCorazonesMenu;

public class ModmindsModMenus {
   public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.MENU_TYPES, "modminds");
   public static final RegistryObject<MenuType<InventarioMenu>> INVENTARIO = REGISTRY.register("inventario", () -> IForgeMenuType.create(InventarioMenu::new));
   public static final RegistryObject<MenuType<InterfazEligeAlGanadorMenu>> INTERFAZ_ELIGE_AL_GANADOR = REGISTRY.register(
      "interfaz_elige_al_ganador", () -> IForgeMenuType.create(InterfazEligeAlGanadorMenu::new)
   );
   public static final RegistryObject<MenuType<TresCorazonesMenu>> GANADORES = REGISTRY.register(
      "ganadores", () -> IForgeMenuType.create(TresCorazonesMenu::new)
   );
   public static final RegistryObject<MenuType<HydraPanelMenu>> HYDRA_PANEL = REGISTRY.register(
      "hydra_panel", () -> IForgeMenuType.create(HydraPanelMenu::new)
   );
   public static final RegistryObject<MenuType<LimboTitanPanelMenu>> LIMBO_TITAN_PANEL = REGISTRY.register(
      "limbo_titan_panel", () -> IForgeMenuType.create(LimboTitanPanelMenu::new)
   );
   public static final RegistryObject<MenuType<ReviilPanelMenu>> REVIIL_PANEL = REGISTRY.register(
      "reviil_panel", () -> IForgeMenuType.create(ReviilPanelMenu::new)
   );
   public static final RegistryObject<MenuType<PanelOrbMenu>> PANEL_ORB = REGISTRY.register("panel_orb", () -> IForgeMenuType.create(PanelOrbMenu::new));
   public static final RegistryObject<MenuType<ReactorHydraPanelMenu>> REACTOR_HYDRA_PANEL = REGISTRY.register(
      "reactor_hydra_panel", () -> IForgeMenuType.create(ReactorHydraPanelMenu::new)
   );
   public static final RegistryObject<MenuType<BossPanelMenu>> BOSS_PANEL = REGISTRY.register("boss_panel", () -> IForgeMenuType.create(BossPanelMenu::new));
}
