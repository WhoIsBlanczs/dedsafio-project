package net.owleafstudio.modminds.init;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.network.AtomizadorDeAstrosHatkeyRecargarMessage;
import net.owleafstudio.modminds.network.BombaNegraEspacioMessage;
import net.owleafstudio.modminds.network.EspacioGorgonaMessage;
import net.owleafstudio.modminds.network.EspacioHydraMessage;
import net.owleafstudio.modminds.network.EspacioReviilMessage;
import net.owleafstudio.modminds.network.EspacioSapoMessage;
import net.owleafstudio.modminds.network.PerforadorDelVacioHotkeyRecargarMessage;
import net.owleafstudio.modminds.network.RecargaArmaCortaEspacialMessage;
import net.owleafstudio.modminds.network.RecargarCerbatanaMessage;
import net.owleafstudio.modminds.network.RifleDePulsoBorgRecarharMessage;

@Mod.EventBusSubscriber(
   bus = Mod.EventBusSubscriber.Bus.MOD,
   value = {Dist.CLIENT}
)
public class ModmindsModKeyMappings {
   public static final KeyMapping ESPACIO_GORGONA = new KeyMapping("key.modminds.espacio_gorgona", 32, "key.categories.misc") {
      private boolean isDownOld = false;

      public void setDown(boolean isDown) {
         super.setDown(isDown);
         if (this.isDownOld != isDown && isDown) {
            ModmindsMod.PACKET_HANDLER.sendToServer(new EspacioGorgonaMessage(0, 0));
            EspacioGorgonaMessage.pressAction(Minecraft.getInstance().player, 0, 0);
            ModmindsModKeyMappings.ESPACIO_GORGONA_LASTPRESS = System.currentTimeMillis();
         } else if (this.isDownOld != isDown && !isDown) {
            int dt = (int)(System.currentTimeMillis() - ModmindsModKeyMappings.ESPACIO_GORGONA_LASTPRESS);
            ModmindsMod.PACKET_HANDLER.sendToServer(new EspacioGorgonaMessage(1, dt));
            EspacioGorgonaMessage.pressAction(Minecraft.getInstance().player, 1, dt);
         }

         this.isDownOld = isDown;
      }
   };
   public static final KeyMapping BOMBA_NEGRA_ESPACIO = new KeyMapping("key.modminds.bomba_negra_espacio", 32, "key.categories.misc") {
      private boolean isDownOld = false;

      public void setDown(boolean isDown) {
         super.setDown(isDown);
         if (this.isDownOld != isDown && isDown) {
            ModmindsMod.PACKET_HANDLER.sendToServer(new BombaNegraEspacioMessage(0, 0));
            BombaNegraEspacioMessage.pressAction(Minecraft.getInstance().player, 0, 0);
            ModmindsModKeyMappings.BOMBA_NEGRA_ESPACIO_LASTPRESS = System.currentTimeMillis();
         } else if (this.isDownOld != isDown && !isDown) {
            int dt = (int)(System.currentTimeMillis() - ModmindsModKeyMappings.BOMBA_NEGRA_ESPACIO_LASTPRESS);
            ModmindsMod.PACKET_HANDLER.sendToServer(new BombaNegraEspacioMessage(1, dt));
            BombaNegraEspacioMessage.pressAction(Minecraft.getInstance().player, 1, dt);
         }

         this.isDownOld = isDown;
      }
   };
   public static final KeyMapping RIFLE_DE_PULSO_BORG_RECARHAR = new KeyMapping("key.modminds.rifle_de_pulso_borg_recarhar", 82, "key.categories.misc") {
      private boolean isDownOld = false;

      public void setDown(boolean isDown) {
         super.setDown(isDown);
         if (this.isDownOld != isDown && isDown) {
            ModmindsMod.PACKET_HANDLER.sendToServer(new RifleDePulsoBorgRecarharMessage(0, 0));
            RifleDePulsoBorgRecarharMessage.pressAction(Minecraft.getInstance().player, 0, 0);
         }

         this.isDownOld = isDown;
      }
   };
   public static final KeyMapping RECARGAR_CERBATANA = new KeyMapping("key.modminds.recargar_cerbatana", 82, "key.categories.misc") {
      private boolean isDownOld = false;

      public void setDown(boolean isDown) {
         super.setDown(isDown);
         if (this.isDownOld != isDown && isDown) {
            ModmindsMod.PACKET_HANDLER.sendToServer(new RecargarCerbatanaMessage(0, 0));
            RecargarCerbatanaMessage.pressAction(Minecraft.getInstance().player, 0, 0);
         }

         this.isDownOld = isDown;
      }
   };
   public static final KeyMapping PERFORADOR_DEL_VACIO_HOTKEY_RECARGAR = new KeyMapping(
      "key.modminds.perforador_del_vacio_hotkey_recargar", 82, "key.categories.misc"
   ) {
      private boolean isDownOld = false;

      public void setDown(boolean isDown) {
         super.setDown(isDown);
         if (this.isDownOld != isDown && isDown) {
            ModmindsMod.PACKET_HANDLER.sendToServer(new PerforadorDelVacioHotkeyRecargarMessage(0, 0));
            PerforadorDelVacioHotkeyRecargarMessage.pressAction(Minecraft.getInstance().player, 0, 0);
         }

         this.isDownOld = isDown;
      }
   };
   public static final KeyMapping ATOMIZADOR_DE_ASTROS_HATKEY_RECARGAR = new KeyMapping(
      "key.modminds.atomizador_de_astros_hatkey_recargar", 82, "key.categories.misc"
   ) {
      private boolean isDownOld = false;

      public void setDown(boolean isDown) {
         super.setDown(isDown);
         if (this.isDownOld != isDown && isDown) {
            ModmindsMod.PACKET_HANDLER.sendToServer(new AtomizadorDeAstrosHatkeyRecargarMessage(0, 0));
            AtomizadorDeAstrosHatkeyRecargarMessage.pressAction(Minecraft.getInstance().player, 0, 0);
         }

         this.isDownOld = isDown;
      }
   };
   public static final KeyMapping RECARGA_ARMA_CORTA_ESPACIAL = new KeyMapping("key.modminds.recarga_arma_corta_espacial", 82, "key.categories.misc") {
      private boolean isDownOld = false;

      public void setDown(boolean isDown) {
         super.setDown(isDown);
         if (this.isDownOld != isDown && isDown) {
            ModmindsMod.PACKET_HANDLER.sendToServer(new RecargaArmaCortaEspacialMessage(0, 0));
            RecargaArmaCortaEspacialMessage.pressAction(Minecraft.getInstance().player, 0, 0);
         }

         this.isDownOld = isDown;
      }
   };
   public static final KeyMapping ESPACIO_SAPO = new KeyMapping("key.modminds.espacio_sapo", 32, "key.categories.misc") {
      private boolean isDownOld = false;

      public void setDown(boolean isDown) {
         super.setDown(isDown);
         if (this.isDownOld != isDown && isDown) {
            ModmindsMod.PACKET_HANDLER.sendToServer(new EspacioSapoMessage(0, 0));
            EspacioSapoMessage.pressAction(Minecraft.getInstance().player, 0, 0);
            ModmindsModKeyMappings.ESPACIO_SAPO_LASTPRESS = System.currentTimeMillis();
         } else if (this.isDownOld != isDown && !isDown) {
            int dt = (int)(System.currentTimeMillis() - ModmindsModKeyMappings.ESPACIO_SAPO_LASTPRESS);
            ModmindsMod.PACKET_HANDLER.sendToServer(new EspacioSapoMessage(1, dt));
            EspacioSapoMessage.pressAction(Minecraft.getInstance().player, 1, dt);
         }

         this.isDownOld = isDown;
      }
   };
   public static final KeyMapping ESPACIO_HYDRA = new KeyMapping("key.modminds.espacio_hydra", 32, "key.categories.misc") {
      private boolean isDownOld = false;

      public void setDown(boolean isDown) {
         super.setDown(isDown);
         if (this.isDownOld != isDown && isDown) {
            ModmindsMod.PACKET_HANDLER.sendToServer(new EspacioHydraMessage(0, 0));
            EspacioHydraMessage.pressAction(Minecraft.getInstance().player, 0, 0);
            ModmindsModKeyMappings.ESPACIO_HYDRA_LASTPRESS = System.currentTimeMillis();
         } else if (this.isDownOld != isDown && !isDown) {
            int dt = (int)(System.currentTimeMillis() - ModmindsModKeyMappings.ESPACIO_HYDRA_LASTPRESS);
            ModmindsMod.PACKET_HANDLER.sendToServer(new EspacioHydraMessage(1, dt));
            EspacioHydraMessage.pressAction(Minecraft.getInstance().player, 1, dt);
         }

         this.isDownOld = isDown;
      }
   };
   private static long ESPACIO_GORGONA_LASTPRESS = 0L;
   private static long BOMBA_NEGRA_ESPACIO_LASTPRESS = 0L;
   private static long ESPACIO_SAPO_LASTPRESS = 0L;
   private static long ESPACIO_HYDRA_LASTPRESS = 0L;
   public static final KeyMapping ESPACIO_REVIIL = new KeyMapping("key.modminds.espacio_reviil", 32, "key.categories.misc") {
      private boolean isDownOld = false;

      public void setDown(boolean isDown) {
         super.setDown(isDown);
         if (this.isDownOld != isDown && isDown) {
            ModmindsMod.PACKET_HANDLER.sendToServer(new EspacioReviilMessage(0, 0));
            EspacioReviilMessage.pressAction(Minecraft.getInstance().player, 0, 0);
         }

         this.isDownOld = isDown;
      }
   };

   @SubscribeEvent
   public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
      event.register(ESPACIO_GORGONA);
      event.register(BOMBA_NEGRA_ESPACIO);
      event.register(RIFLE_DE_PULSO_BORG_RECARHAR);
      event.register(RECARGAR_CERBATANA);
      event.register(PERFORADOR_DEL_VACIO_HOTKEY_RECARGAR);
      event.register(ATOMIZADOR_DE_ASTROS_HATKEY_RECARGAR);
      event.register(RECARGA_ARMA_CORTA_ESPACIAL);
      event.register(ESPACIO_SAPO);
      event.register(ESPACIO_HYDRA);
      event.register(ESPACIO_REVIIL);
   }

   @Mod.EventBusSubscriber({Dist.CLIENT})
   public static class KeyEventListener {
      @SubscribeEvent
      public static void onClientTick(TickEvent.ClientTickEvent event) {
         if (Minecraft.getInstance().gui.screen() == null) {
            ModmindsModKeyMappings.ESPACIO_GORGONA.consumeClick();
            ModmindsModKeyMappings.BOMBA_NEGRA_ESPACIO.consumeClick();
            ModmindsModKeyMappings.RIFLE_DE_PULSO_BORG_RECARHAR.consumeClick();
            ModmindsModKeyMappings.RECARGAR_CERBATANA.consumeClick();
            ModmindsModKeyMappings.PERFORADOR_DEL_VACIO_HOTKEY_RECARGAR.consumeClick();
            ModmindsModKeyMappings.ATOMIZADOR_DE_ASTROS_HATKEY_RECARGAR.consumeClick();
            ModmindsModKeyMappings.RECARGA_ARMA_CORTA_ESPACIAL.consumeClick();
            ModmindsModKeyMappings.ESPACIO_SAPO.consumeClick();
            ModmindsModKeyMappings.ESPACIO_HYDRA.consumeClick();
            ModmindsModKeyMappings.ESPACIO_REVIIL.consumeClick();
         }
      }
   }
}
