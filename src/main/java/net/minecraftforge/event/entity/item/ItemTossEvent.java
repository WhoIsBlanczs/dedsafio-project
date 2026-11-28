package net.minecraftforge.event.entity.item;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityEvent;

public class ItemTossEvent extends EntityEvent {
   private final Player player;

   public ItemTossEvent(ItemEntity entity, Player player) {
      super(entity);
      this.player = player;
   }

   public ItemEntity getEntity() {
      return (ItemEntity)super.getEntity();
   }

   public Player getPlayer() {
      return this.player;
   }
}
