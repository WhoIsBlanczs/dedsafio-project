package net.owleafstudio.modminds.potion;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;
import net.owleafstudio.modminds.procedures.EfectoQuemaduraAlEstarActivoElEfectoProcedure;

public class EfectoQuemaduraMobEffect extends MobEffect {
   public EfectoQuemaduraMobEffect() {
      super(MobEffectCategory.HARMFUL, -7143424);
   }

   public List<ItemStack> getCurativeItems() {
      return new ArrayList<>();
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      EfectoQuemaduraAlEstarActivoElEfectoProcedure.execute(entity);
      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public void initializeClient(Consumer<IClientMobEffectExtensions> consumer) {
      consumer.accept(new IClientMobEffectExtensions() {
         @Override
         public boolean isVisibleInInventory(MobEffectInstance effect) {
            return false;
         }

         @Override
         public boolean renderInventoryText(MobEffectInstance instance, EffectRenderingInventoryScreen<?> screen, GuiGraphicsExtractor guiGraphics, int x, int y, int blitOffset) {
            return false;
         }

         @Override
         public boolean isVisibleInGui(MobEffectInstance effect) {
            return false;
         }
      });
   }
}
