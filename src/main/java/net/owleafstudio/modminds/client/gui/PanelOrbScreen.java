package net.owleafstudio.modminds.client.gui;

import java.util.HashMap;
import java.util.List;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.network.PanelOrbButtonMessage;
import net.owleafstudio.modminds.world.inventory.PanelOrbMenu;

public class PanelOrbScreen extends BossPanelScreen<PanelOrbMenu> {
   private static final int ACCENT = -8402689;

   public PanelOrbScreen(PanelOrbMenu container, Inventory inventory, Component text) {
      super(container, inventory, text);
   }

   @Override
   protected Component title() {
      return Component.translatable("gui.modminds.panel_orb.title");
   }

   @Override
   protected int accent() {
      return -8402689;
   }

   @Override
   protected String panelKey() {
      return "panel_orb";
   }

   @Override
   protected HashMap<String, Object> guistate() {
      return PanelOrbMenu.guistate;
   }

   @Override
   protected List<BossPanelScreen.Section> sections() {
      String p = "gui.modminds.panel_orb.";
      String h = "gui.modminds.panel.help.";
      return List.of(
         new BossPanelScreen.Section(
            p + "label_paredes_barreras",
            2,
            new BossPanelScreen.Ability(0, p + "button_pared_x", h + "pared_x"),
            new BossPanelScreen.Ability(1, p + "button_pared_z", h + "pared_z"),
            new BossPanelScreen.Ability(2, p + "button_quitar_paredes", h + "quitar_paredes")
         ),
         new BossPanelScreen.Section(p + "label_summon", 1, "orbsummon", new BossPanelScreen.Ability(3, p + "button_summon", h + "summon_orbs"))
      );
   }

   @Override
   protected void trigger(int id) {
      PanelOrbMenu menu = (PanelOrbMenu)this.getMenu();
      ModmindsMod.PACKET_HANDLER.sendToServer(new PanelOrbButtonMessage(id, menu.x, menu.y, menu.z));
      PanelOrbButtonMessage.handleButtonAction(menu.entity, id, menu.x, menu.y, menu.z);
   }
}
