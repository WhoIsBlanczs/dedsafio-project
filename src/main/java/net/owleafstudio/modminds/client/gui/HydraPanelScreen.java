package net.owleafstudio.modminds.client.gui;

import java.util.HashMap;
import java.util.List;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.network.HydraPanelButtonMessage;
import net.owleafstudio.modminds.world.inventory.HydraPanelMenu;

public class HydraPanelScreen extends BossPanelScreen<HydraPanelMenu> {
   private static final int ACCENT = -49957;

   public HydraPanelScreen(HydraPanelMenu container, Inventory inventory, Component text) {
      super(container, inventory, text);
   }

   @Override
   protected Component title() {
      return Component.translatable("gui.modminds.hydra_panel.label_panel_limbo_titan");
   }

   @Override
   protected int accent() {
      return -49957;
   }

   @Override
   protected String panelKey() {
      return "hydra_panel";
   }

   @Override
   protected HashMap<String, Object> guistate() {
      return HydraPanelMenu.guistate;
   }

   @Override
   protected BossPanelScreen.Portrait portrait() {
      return new BossPanelScreen.Portrait(Identifier.parse("modminds:textures/screens/hydraiconsmall.png"), 98, 93);
   }

   @Override
   protected List<BossPanelScreen.Section> sections() {
      String p = "gui.modminds.hydra_panel.";
      String h = "gui.modminds.panel.help.";
      return List.of(
         new BossPanelScreen.Section(
            "gui.modminds.panel.section.stage",
            3,
            new BossPanelScreen.Ability(1, p + "button_ligthing", h + "lighting"),
            new BossPanelScreen.Ability(2, p + "button_pantalla_gris", h + "gray_screen"),
            new BossPanelScreen.Ability(4, p + "button_quite_effects", h + "clear_screen"),
            new BossPanelScreen.Ability(5, p + "button_nausea", h + "nausea")
         ),
         new BossPanelScreen.Section(
            "gui.modminds.panel.section.reinforcements",
            3,
            "SummonHydra",
            new BossPanelScreen.Ability(6, p + "button_fantasma_rojo", h + "ghost"),
            new BossPanelScreen.Ability(8, p + "button_change_target1", h + "nereids"),
            new BossPanelScreen.Ability(10, p + "button_control_camera", h + "mount_boss")
         ),
         new BossPanelScreen.Section(
            "gui.modminds.panel.section.attacks",
            3,
            new BossPanelScreen.Ability(0, p + "button_scream", h + "scream"),
            new BossPanelScreen.Ability(3, p + "button_beam", h + "hydra_beam"),
            new BossPanelScreen.Ability(7, p + "button_area_attac", h + "area_attack"),
            new BossPanelScreen.Ability(9, p + "button_water_charge", h + "water_charge"),
            new BossPanelScreen.Ability(11, p + "button_marea", h + "marea"),
            new BossPanelScreen.Ability(12, p + "button_mordida", h + "mordida"),
            new BossPanelScreen.Ability(13, p + "button_abismo", h + "abismo"),
            new BossPanelScreen.Ability(14, p + "button_chorro", h + "chorro")
         )
      );
   }

   @Override
   protected void trigger(int id) {
      HydraPanelMenu menu = (HydraPanelMenu)this.getMenu();
      ModmindsMod.PACKET_HANDLER.sendToServer(new HydraPanelButtonMessage(id, menu.x, menu.y, menu.z));
      HydraPanelButtonMessage.handleButtonAction(menu.entity, id, menu.x, menu.y, menu.z);
   }
}
