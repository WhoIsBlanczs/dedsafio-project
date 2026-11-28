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
import net.minecraft.world.entity.Entity;
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;
import net.owleafstudio.modminds.procedures.BombaNegraEfectoAlEstarActivoElEfectoProcedure;
import net.owleafstudio.modminds.procedures.BombaNegraEfectoAlFinalizarElEfectoProcedure;

public class BombaNegraEfectoMobEffect extends MobEffect {
   public BombaNegraEfectoMobEffect() {
      super(MobEffectCategory.NEUTRAL, -1);
   }

   public boolean isInstantaneous() {
      return true;
   }

   public List<ItemStack> getCurativeItems() {
      return new ArrayList<>();
   }

   public boolean applyEffectTick(net.minecraft.server.level.ServerLevel nivel$, LivingEntity entity, int amplifier) {
      BombaNegraEfectoAlEstarActivoElEfectoProcedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity);
      return true;
   }

   public void onMobRemoved(net.minecraft.server.level.ServerLevel nivel$, LivingEntity entity, int amplifier, Entity.RemovalReason reason) {
      super.onMobRemoved(nivel$, entity, amplifier, reason);
      BombaNegraEfectoAlFinalizarElEfectoProcedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity);
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
         public boolean renderInventoryText(MobEffectInstance instance, net.minecraft.client.gui.screens.Screen screen, GuiGraphicsExtractor guiGraphics, int x, int y, int blitOffset) {
            return false;
         }

         @Override
         public boolean isVisibleInGui(MobEffectInstance effect) {
            return false;
         }
      });
   }
}
