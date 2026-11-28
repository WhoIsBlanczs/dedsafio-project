package net.minecraftforge.client.event;

import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.minecraft.resources.Identifier;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraftforge.eventbus.api.Event;

public class RegisterDimensionSpecialEffectsEvent extends Event {
   public void register(Identifier id, DimensionSpecialEffects effects) {
      DimensionRenderingRegistry.registerDimensionEffects(id, effects);
   }
}
