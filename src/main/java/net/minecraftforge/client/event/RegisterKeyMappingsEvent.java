package net.minecraftforge.client.event;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.eventbus.api.Event;

public class RegisterKeyMappingsEvent extends Event {
   public void register(KeyMapping keyMapping) {
      KeyBindingHelper.registerKeyBinding(keyMapping);
   }
}
