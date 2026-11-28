package net.minecraftforge.eventbus.api;

public class Event {
   private boolean canceled;
   private Event.Result result = Event.Result.DEFAULT;

   public boolean isCancelable() {
      return true;
   }

   public boolean isCanceled() {
      return this.canceled;
   }

   public void setCanceled(boolean canceled) {
      this.canceled = canceled;
   }

   public Event.Result getResult() {
      return this.result;
   }

   public boolean hasResult() {
      return true;
   }

   public void setResult(Event.Result result) {
      this.result = result;
   }

   public Event.EventPhase getPhase() {
      return Event.EventPhase.NORMAL;
   }

   public static enum EventPhase {
      PRE,
      NORMAL,
      POST;
   }

   public static enum Result {
      DENY,
      DEFAULT,
      ALLOW;
   }
}
