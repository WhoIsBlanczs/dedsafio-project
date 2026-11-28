package net.owleafstudio.modminds.potion;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.effect.MobEffectCategory;

public class ProteccionContraLasBombasMobEffect extends MobEffect {
   public ProteccionContraLasBombasMobEffect() {
      super(MobEffectCategory.NEUTRAL, -1);
   }

   public List<ItemStack> getCurativeItems() {
      ArrayList<ItemStack> cures = new ArrayList<>();
      cures.add(new ItemStack(Items.TOTEM_OF_UNDYING));
      return cures;
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }
}
