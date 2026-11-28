package net.owleafstudio.modminds.item;

import net.minecraft.world.InteractionResult;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.Item;
import net.owleafstudio.modminds.procedures.BolaDeHydraCuandoSeHaceClicConElBotonDerechoProcedure;

public class BolaDeHydraItem extends Item {
   public BolaDeHydraItem() {
      super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
   }

   public InteractionResult use(Level world, Player entity, InteractionHand hand) {
      InteractionResult ar = super.use(world, entity, hand);
      BolaDeHydraCuandoSeHaceClicConElBotonDerechoProcedure.execute(entity);
      return ar;
   }
}
