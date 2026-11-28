package net.owleafstudio.modminds.client.gui;

import java.util.HashMap;
import java.util.List;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.phys.AABB;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.client.Minecraft;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.entity.ReviilEntity;
import net.owleafstudio.modminds.network.ReviilPanelButtonMessage;
import net.owleafstudio.modminds.world.inventory.ReviilPanelMenu;

public class ReviilPanelScreen extends BossPanelScreen<ReviilPanelMenu> {
   private static final int ACCENT = -20406;
   private static final int[] GIGANTE_KIT = new int[]{20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30};
   private static final int[] PEQUENO_KIT = new int[]{6, 13, 15, 17, 31, 33};

   public ReviilPanelScreen(ReviilPanelMenu container, Inventory inventory, Component text) {
      super(container, inventory, text);
   }

   @Override
   protected Component title() {
      return Component.translatable("gui.modminds.reviil_panel.label_panel_limbo_titan");
   }

   @Override
   protected int accent() {
      return -20406;
   }

   @Override
   protected String panelKey() {
      return "reviil_panel";
   }

   private ReviilEntity jefe() {
      Minecraft mc = Minecraft.getInstance();
      if (mc.level != null && mc.player != null) {
         ReviilEntity nearest = null;
         double best = Double.MAX_VALUE;

         for (ReviilEntity candidate : mc.level
            .getEntitiesOfClass(ReviilEntity.class, new AABB(mc.player.position(), mc.player.position()).inflate(128.0))) {
            if (candidate.isAlive()) {
               double d = candidate.distanceToSqr(mc.player);
               if (d < best) {
                  best = d;
                  nearest = candidate;
               }
            }
         }

         return nearest;
      } else {
         return null;
      }
   }

   @Override
   protected int phaseKey() {
      ReviilEntity reviil = this.jefe();
      return reviil == null ? -1 : reviil.phase();
   }

   @Override
   protected boolean appliesToPhase(int phase, int abilityId) {
      if (phase < 0) {
         return true;
      } else {
         return phase == 2 ? !contiene(PEQUENO_KIT, abilityId) : !contiene(GIGANTE_KIT, abilityId);
      }
   }

   private static boolean contiene(int[] ids, int id) {
      for (int candidate : ids) {
         if (candidate == id) {
            return true;
         }
      }

      return false;
   }

   @Override
   protected Component phaseBadge() {
      ReviilEntity reviil = this.jefe();
      if (reviil == null) {
         return Component.translatable("gui.modminds.panel.phase.none");
      } else {
         String key = switch (reviil.phase()) {
            case 1 -> "gui.modminds.panel.phase.fury";
            case 2 -> "gui.modminds.panel.phase.giant";
            default -> "gui.modminds.panel.phase.normal";
         };
         return Component.translatable(key);
      }
   }

   @Override
   protected HashMap<String, Object> guistate() {
      return ReviilPanelMenu.guistate;
   }

   @Override
   protected BossPanelScreen.Portrait portrait() {
      return new BossPanelScreen.Portrait(Identifier.parse("modminds:textures/screens/boss_reviil_mob.png"), 100, 100);
   }

   @Override
   protected List<BossPanelScreen.Section> sections() {
      String p = "gui.modminds.reviil_panel.";
      String h = "gui.modminds.panel.help.";
      return List.of(
         new BossPanelScreen.Section(
            "gui.modminds.panel.section.phase",
            2,
            new BossPanelScreen.Ability(18, p + "button_furia", h + "reviil_furia"),
            new BossPanelScreen.Ability(19, p + "button_gigante", h + "reviil_gigante"),
            new BossPanelScreen.Ability(32, p + "button_encoge", h + "reviil_encoge")
         ),
         new BossPanelScreen.Section(
            "gui.modminds.panel.section.targeted",
            2,
            new BossPanelScreen.Ability(6, p + "button_disparo", h + "reviil_shot"),
            new BossPanelScreen.Ability(8, p + "button_rayo_vacio", h + "rayo_vacio"),
            new BossPanelScreen.Ability(13, p + "button_tormenta", h + "tormenta"),
            new BossPanelScreen.Ability(15, p + "button_agarre", h + "agarre"),
            new BossPanelScreen.Ability(31, p + "button_embestida", h + "reviil_embestida"),
            new BossPanelScreen.Ability(33, p + "button_zarpazo", h + "zarpazo")
         ),
         new BossPanelScreen.Section(
            "gui.modminds.panel.section.aoe",
            2,
            new BossPanelScreen.Ability(5, p + "button_explode", h + "reviil_explode"),
            new BossPanelScreen.Ability(7, p + "button_orbita", h + "orbita"),
            new BossPanelScreen.Ability(9, p + "button_aullido", h + "aullido"),
            new BossPanelScreen.Ability(10, p + "button_lluvia", h + "lluvia"),
            new BossPanelScreen.Ability(11, p + "button_ruleta_letal", h + "ruleta"),
            new BossPanelScreen.Ability(12, p + "button_croupier", h + "croupier"),
            new BossPanelScreen.Ability(14, p + "button_corona", h + "corona"),
            new BossPanelScreen.Ability(17, p + "button_golpe_suelo", h + "golpe_suelo")
         ),
         new BossPanelScreen.Section(
            "gui.modminds.panel.section.gigante",
            2,
            new BossPanelScreen.Ability(21, p + "button_puno_marcado", h + "puno_marcado"),
            new BossPanelScreen.Ability(22, p + "button_ruleta_gigante", h + "ruleta_gigante"),
            new BossPanelScreen.Ability(23, p + "button_arrastrar_mano", h + "arrastrar_mano"),
            new BossPanelScreen.Ability(24, p + "button_aplauso", h + "aplauso"),
            new BossPanelScreen.Ability(25, p + "button_sal", h + "sal"),
            new BossPanelScreen.Ability(26, p + "button_agujero_negro", h + "agujero_negro"),
            new BossPanelScreen.Ability(27, p + "button_circulos_rayos", h + "circulos_rayos"),
            new BossPanelScreen.Ability(28, p + "button_bloques_alzandose", h + "bloques_alzandose"),
            new BossPanelScreen.Ability(29, p + "button_avalancha", h + "avalancha"),
            new BossPanelScreen.Ability(30, p + "button_campo_rojo", h + "campo_rojo")
         ),
         new BossPanelScreen.Section("gui.modminds.panel.section.reinforcements", 1, new BossPanelScreen.Ability(20, p + "button_soldados", h + "soldados")),
         new BossPanelScreen.Section(
            "gui.modminds.panel.section.stage",
            2,
            new BossPanelScreen.Ability(4, p + "button_stunt", h + "stunt"),
            new BossPanelScreen.Ability(0, p + "button_ligthing", h + "lighting"),
            new BossPanelScreen.Ability(1, p + "button_pantalla_gris", h + "gray_screen"),
            new BossPanelScreen.Ability(2, p + "button_quite_effects", h + "clear_screen"),
            new BossPanelScreen.Ability(3, p + "button_nausea", h + "nausea")
         ),
         new BossPanelScreen.Section("gui.modminds.panel.section.scene", 1, new BossPanelScreen.Ability(16, p + "button_escena", h + "escena"))
      );
   }

   @Override
   protected void trigger(int id) {
      ReviilPanelMenu menu = (ReviilPanelMenu)this.getMenu();
      ModmindsMod.PACKET_HANDLER.sendToServer(new ReviilPanelButtonMessage(id, menu.x, menu.y, menu.z));
      ReviilPanelButtonMessage.handleButtonAction(menu.entity, id, menu.x, menu.y, menu.z);
   }
}
