package net.owleafstudio.modminds.item;

import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.item.Item;
import net.owleafstudio.modminds.init.ModmindsModFluids;

public class AguaDelLimboItem extends BucketItem {
   public AguaDelLimboItem() {
      super(
         (Fluid)ModmindsModFluids.AGUA_DEL_LIMBO.get(),
         new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).rarity(Rarity.COMMON)
      );
   }
}
