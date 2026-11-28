package net.minecraftforge.common;

import net.minecraftforge.eventbus.api.IEventBus;
import net.owleafstudio.modminds.compat.ForgeEventBus;

public final class MinecraftForge {
   public static final IEventBus EVENT_BUS = ForgeEventBus.FORGE;

   private MinecraftForge() {
   }
}
