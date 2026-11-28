package net.owleafstudio.modminds.potion;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;
import net.owleafstudio.modminds.procedures.HydraEspaciosAlEstarActivoElEfectoProcedure;
import net.owleafstudio.modminds.procedures.HydraEspaciosAlFinalizarElEfectoProcedure;

public class HydraEspaciosMobEffect extends MobEffect {
   public HydraEspaciosMobEffect() {
      super(MobEffectCategory.NEUTRAL, -14461456);
   }

   public List<ItemStack> getCurativeItems() {
      return new ArrayList<>();
   }

   public boolean applyEffectTick(net.minecraft.server.level.ServerLevel nivel$, LivingEntity entity, int amplifier) {
      HydraEspaciosAlEstarActivoElEfectoProcedure.execute(entity);
      return true;
   }

   public void onMobRemoved(net.minecraft.server.level.ServerLevel nivel$, LivingEntity entity, int amplifier, Entity.RemovalReason reason) {
      super.onMobRemoved(nivel$, entity, amplifier, reason);
      HydraEspaciosAlFinalizarElEfectoProcedure.execute(entity);
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
