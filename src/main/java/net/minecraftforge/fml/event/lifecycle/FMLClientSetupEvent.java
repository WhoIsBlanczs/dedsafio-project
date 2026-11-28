package net.minecraftforge.fml.event.lifecycle;

import net.minecraftforge.eventbus.api.Event;
import net.owleafstudio.modminds.compat.WorkQueue;

public class FMLClientSetupEvent extends Event {
   public void enqueueWork(Runnable work) {
      WorkQueue.runNowOrQueue(work);
   }
}
