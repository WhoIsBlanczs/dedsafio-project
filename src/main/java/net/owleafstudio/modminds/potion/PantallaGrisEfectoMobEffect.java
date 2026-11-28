package net.owleafstudio.modminds.potion;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;

public class PantallaGrisEfectoMobEffect extends MobEffect {
   public PantallaGrisEfectoMobEffect() {
      super(MobEffectCategory.NEUTRAL, -6710887);
   }

   public List<ItemStack> getCurativeItems() {
      return new ArrayList<>();
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public void initializeClient(Consumer<IClientMobEffectExtensions> consumer) {
      consumer.accept(new IClientMobEffectExtensions() {
         @Override
         public boolean isVisibleInGui(MobEffectInstance effect) {
            return false;
         }
      });
   }
}
