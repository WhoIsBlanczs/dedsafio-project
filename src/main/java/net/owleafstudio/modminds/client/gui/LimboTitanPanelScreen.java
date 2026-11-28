package net.owleafstudio.modminds.client.gui;

import java.util.HashMap;
import java.util.List;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.network.LimboTitanPanelButtonMessage;
import net.owleafstudio.modminds.world.inventory.LimboTitanPanelMenu;

public class LimboTitanPanelScreen extends BossPanelScreen<LimboTitanPanelMenu> {
   private static final int ACCENT = -30165;

   public LimboTitanPanelScreen(LimboTitanPanelMenu container, Inventory inventory, Component text) {
      super(container, inventory, text);
   }

   @Override
   protected Component title() {
      return Component.translatable("gui.modminds.limbo_titan_panel.label_limbo_titan_panel");
   }

   @Override
   protected int accent() {
      return -30165;
   }

   @Override
   protected String panelKey() {
      return "limbo_titan_panel";
   }

   @Override
   protected HashMap<String, Object> guistate() {
      return LimboTitanPanelMenu.guistate;
   }

   @Override
   protected BossPanelScreen.Portrait portrait() {
      return new BossPanelScreen.Portrait(Identifier.parse("modminds:textures/screens/titan_del_limbo_1234.png"), 100, 100);
   }

   @Override
   protected List<BossPanelScreen.Section> sections() {
      String p = "gui.modminds.limbo_titan_panel.";
      String h = "gui.modminds.panel.help.";
      return List.of(
         new BossPanelScreen.Section(
            "gui.modminds.panel.section.stage",
            3,
            new BossPanelScreen.Ability(0, p + "button_lighting", h + "lighting"),
            new BossPanelScreen.Ability(1, p + "button_gray_screen", h + "red_screen"),
            new BossPanelScreen.Ability(2, p + "button_nausea", h + "nausea"),
            new BossPanelScreen.Ability(3, p + "button_quite_effects", h + "clear_screen")
         ),
         new BossPanelScreen.Section(
            "gui.modminds.panel.section.reinforcements",
            3,
            "SummonHydra",
            new BossPanelScreen.Ability(8, p + "button_summon_ghost", h + "ghost"),
            new BossPanelScreen.Ability(9, p + "button_summon_piglins", h + "piglins"),
            new BossPanelScreen.Ability(10, p + "button_mount", h + "mount_boss")
         ),
         new BossPanelScreen.Section(
            "gui.modminds.panel.section.attacks",
            3,
            new BossPanelScreen.Ability(4, p + "button_terra_punch", h + "terra_punch"),
            new BossPanelScreen.Ability(5, p + "button_ultimate_jump", h + "ultimate_jump"),
            new BossPanelScreen.Ability(6, p + "button_roll", h + "roll"),
            new BossPanelScreen.Ability(7, p + "button_punch", h + "titan_punch"),
            new BossPanelScreen.Ability(11, p + "button_quiebra", h + "quiebra"),
            new BossPanelScreen.Ability(12, p + "button_corral", h + "corral"),
            new BossPanelScreen.Ability(13, p + "button_muro", h + "muro"),
            new BossPanelScreen.Ability(14, p + "button_suelo", h + "suelo"),
            new BossPanelScreen.Ability(15, p + "button_bola_fuego", h + "bola_fuego"),
            new BossPanelScreen.Ability(16, p + "button_sierra", h + "sierra"),
            new BossPanelScreen.Ability(17, p + "button_meteoro", h + "meteoro")
         )
      );
   }

   @Override
   protected void trigger(int id) {
      LimboTitanPanelMenu menu = (LimboTitanPanelMenu)this.getMenu();
      ModmindsMod.PACKET_HANDLER.sendToServer(new LimboTitanPanelButtonMessage(id, menu.x, menu.y, menu.z));
      LimboTitanPanelButtonMessage.handleButtonAction(menu.entity, id, menu.x, menu.y, menu.z);
   }
}
