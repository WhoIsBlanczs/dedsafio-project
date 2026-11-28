package net.owleafstudio.modminds.compat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.ToggleKeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public final class KeyBindingBridge {
   private static final Logger LOGGER = LoggerFactory.getLogger("modminds-compat");
   private static final int RESCAN_TICKS = 20;
   private static final List<KeyMapping> polled = new ArrayList<>();
   private static int ticksSinceRescan = 20;
   private static boolean firstScan = true;

   private KeyBindingBridge() {
   }

   public static void tick(Minecraft client) {
      if (client.options != null && client.getWindow() != null) {
         if (client.level != null) {
            if (ticksSinceRescan++ >= 20) {
               ticksSinceRescan = 0;
               rescan(client);
            }

            long window = client.getWindow().getWindow();

            for (KeyMapping mapping : List.copyOf(polled)) {
               InputConstants.Key bound = KeyBindingHelper.getBoundKeyOf(mapping);
               if (bound.getType() == InputConstants.Type.KEYSYM && bound.getValue() >= 0) {
                  mapping.setDown(InputConstants.isKeyDown(bound.getValue()));
               }
            }
         }
      }
   }

   private static void rescan(Minecraft client) {
      Map<InputConstants.Key, List<KeyMapping>> byKey = new HashMap<>();

      for (KeyMapping mapping : client.options.keyMappings) {
         if (mapping != null) {
            byKey.computeIfAbsent(KeyBindingHelper.getBoundKeyOf(mapping), key -> new ArrayList<>()).add(mapping);
         }
      }

      List<KeyMapping> shared = new ArrayList<>();

      for (Entry<InputConstants.Key, List<KeyMapping>> group : byKey.entrySet()) {
         if (group.getValue().size() > 1) {
            group.getValue().stream().filter(mappingx -> !(mappingx instanceof ToggleKeyMapping)).forEach(shared::add);
            if (firstScan) {
               LOGGER.info(
                  "Key {} is shared by {} bindings, all kept active: {}",
                  new Object[]{group.getKey().getName(), group.getValue().size(), group.getValue().stream().map(KeyMapping::getName).toList()}
               );
            } else {
               LOGGER.debug("Key {} is shared by {} bindings", group.getKey().getName(), group.getValue().size());
            }
         }
      }

      firstScan = false;
      polled.clear();
      polled.addAll(shared);
   }
}
