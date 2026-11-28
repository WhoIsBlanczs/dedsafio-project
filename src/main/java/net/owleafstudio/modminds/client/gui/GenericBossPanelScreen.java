package net.owleafstudio.modminds.client.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.network.BossPanelButtonMessage;
import net.owleafstudio.modminds.world.inventory.BossPanelMenu;

public class GenericBossPanelScreen extends BossPanelScreen<BossPanelMenu> {
   public GenericBossPanelScreen(BossPanelMenu container, Inventory inventory, Component text) {
      super(container, inventory, text);
   }

   private String key() {
      switch (((BossPanelMenu)this.getMenu()).boss) {
         case 1:
            return "tiki";
         case 2:
            return "coloso";
         case 3:
            return "gorgona";
         case 4:
            return "bomba_negra";
         default:
            return "warden";
      }
   }

   @Override
   protected String panelKey() {
      return "boss_panel." + this.key();
   }

   @Override
   protected Component title() {
      return Component.translatable("gui.modminds.boss_panel." + this.key() + ".title");
   }

   @Override
   protected int accent() {
      switch (((BossPanelMenu)this.getMenu()).boss) {
         case 1:
            return -10496918;
         case 2:
            return -9459457;
         case 3:
            return -6559544;
         case 4:
            return -4951297;
         default:
            return -12198680;
      }
   }

   @Override
   protected HashMap<String, Object> guistate() {
      return BossPanelMenu.guistate;
   }

   @Override
   protected List<BossPanelScreen.Section> sections() {
      String b = "gui.modminds.boss_panel.";
      String h = "gui.modminds.panel.help.";
      List<BossPanelScreen.Section> sections = new ArrayList<>();
      sections.add(
         new BossPanelScreen.Section(
            "gui.modminds.panel.section.stage",
            3,
            new BossPanelScreen.Ability(0, b + "lighting", h + "lighting"),
            new BossPanelScreen.Ability(1, b + "gray_screen", h + "gray_screen"),
            new BossPanelScreen.Ability(2, b + "red_screen", h + "red_screen"),
            new BossPanelScreen.Ability(3, b + "clear_screen", h + "clear_screen"),
            new BossPanelScreen.Ability(4, b + "nausea", h + "nausea")
         )
      );
      List<BossPanelScreen.Ability> control = new ArrayList<>();
      control.add(new BossPanelScreen.Ability(5, b + "target_me", h + "target_me"));
      control.add(new BossPanelScreen.Ability(6, b + "drop_target", h + "drop_target"));
      if (((BossPanelMenu)this.getMenu()).boss == 1) {
         control.add(new BossPanelScreen.Ability(7, b + "mount", h + "mount_tiki"));
      }

      sections.add(new BossPanelScreen.Section("gui.modminds.panel.section.control", 3, control.toArray(new BossPanelScreen.Ability[0])));
      switch (((BossPanelMenu)this.getMenu()).boss) {
         case 1:
            List<BossPanelScreen.Ability> tiki = new ArrayList<>();
            tiki.add(new BossPanelScreen.Ability(10, b + "embestida", h + "embestida"));
            tiki.add(new BossPanelScreen.Ability(11, b + "espinas", h + "espinas"));
            tiki.add(new BossPanelScreen.Ability(12, b + "furia", h + "furia"));
            tiki.add(new BossPanelScreen.Ability(13, b + "guardianes", h + "guardianes"));
            tiki.add(new BossPanelScreen.Ability(14, b + "escena", h + "tiki_escena"));
            sections.add(new BossPanelScreen.Section("gui.modminds.panel.section.attacks", 2, tiki.toArray(new BossPanelScreen.Ability[0])));
            break;
         case 2:
            sections.add(
               new BossPanelScreen.Section(
                  "gui.modminds.panel.section.attacks",
                  2,
                  new BossPanelScreen.Ability(10, b + "pulso", h + "pulso"),
                  new BossPanelScreen.Ability(11, b + "picado", h + "picado")
               )
            );
            break;
         case 3:
            sections.add(
               new BossPanelScreen.Section("gui.modminds.panel.section.attacks", 1, new BossPanelScreen.Ability(10, b + "petrificar", h + "petrificar"))
            );
            break;
         case 4:
            sections.add(new BossPanelScreen.Section("gui.modminds.panel.section.attacks", 1, new BossPanelScreen.Ability(10, b + "detonar", h + "detonar")));
      }

      return sections;
   }

   @Override
   protected void trigger(int id) {
      BossPanelMenu menu = (BossPanelMenu)this.getMenu();
      ModmindsMod.PACKET_HANDLER.sendToServer(new BossPanelButtonMessage(menu.boss, id, menu.x, menu.y, menu.z));
      BossPanelButtonMessage.handleButtonAction(menu.entity, menu.boss, id, menu.x, menu.y, menu.z);
   }
}
