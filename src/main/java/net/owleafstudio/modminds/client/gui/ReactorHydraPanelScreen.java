package net.owleafstudio.modminds.client.gui;

import java.util.HashMap;
import java.util.List;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.compat.ClientCompat;
import net.owleafstudio.modminds.network.ReactorHydraPanelButtonMessage;
import net.owleafstudio.modminds.procedures.ReactorHydraPanelAbrirProcedure;
import net.owleafstudio.modminds.world.inventory.ReactorHydraPanelMenu;

public class ReactorHydraPanelScreen extends BossPanelScreen<ReactorHydraPanelMenu> {
   private static final int ACCENT = -7542724;
   private static final int PREVIEW = 66;
   private final LivingEntity preview;

   public ReactorHydraPanelScreen(ReactorHydraPanelMenu container, Inventory inventory, Component text) {
      super(container, inventory, text);
      this.preview = ReactorHydraPanelAbrirProcedure.execute(container.world, container.x, container.y, container.z, container.entity) instanceof LivingEntity livingEntity
         ? livingEntity
         : null;
   }

   @Override
   protected Component title() {
      return Component.translatable("gui.modminds.reactor_hydra_panel.label_reactor_hydra_summon_panel");
   }

   @Override
   protected int accent() {
      return -7542724;
   }

   @Override
   protected String panelKey() {
      return "reactor_hydra_panel";
   }

   @Override
   protected HashMap<String, Object> guistate() {
      return ReactorHydraPanelMenu.guistate;
   }

   @Override
   protected List<BossPanelScreen.Section> sections() {
      String p = "gui.modminds.reactor_hydra_panel.";
      String h = "gui.modminds.panel.help.reactor_summon";
      return List.of(
         new BossPanelScreen.Section(
            "gui.modminds.panel.section.reinforcements",
            3,
            new BossPanelScreen.Ability(0, p + "button_fantasmas", h),
            new BossPanelScreen.Ability(1, p + "button_bone_spider", h),
            new BossPanelScreen.Ability(2, p + "button_creeper_cake", h),
            new BossPanelScreen.Ability(3, p + "button_nereidas", h),
            new BossPanelScreen.Ability(4, p + "button_esqueletos", h),
            new BossPanelScreen.Ability(5, p + "button_zombies", h),
            new BossPanelScreen.Ability(6, p + "button_sapo", h),
            new BossPanelScreen.Ability(7, p + "button_fantasma_amarillo", h),
            new BossPanelScreen.Ability(8, p + "button_fantasma_gris", h)
         )
      );
   }

   @Override
   protected int tail() {
      return 66;
   }

   @Override
   protected void renderTail(GuiGraphicsExtractor gui, int y) {
      if (this.preview != null) {
         ClientCompat.renderEntityInInventoryFollowsAngle(gui, this.leftPos + this.imageWidth / 2, y + 66 - 8, 30, 0.0F, 0.0F, this.preview);
      }
   }

   @Override
   protected void trigger(int id) {
      ReactorHydraPanelMenu menu = (ReactorHydraPanelMenu)this.getMenu();
      ModmindsMod.PACKET_HANDLER.sendToServer(new ReactorHydraPanelButtonMessage(id, menu.x, menu.y, menu.z));
      ReactorHydraPanelButtonMessage.handleButtonAction(menu.entity, id, menu.x, menu.y, menu.z);
   }
}
