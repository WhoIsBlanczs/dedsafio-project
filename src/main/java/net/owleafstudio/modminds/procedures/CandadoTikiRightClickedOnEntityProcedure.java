package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.entity.CandadoTikiEntity;
import net.owleafstudio.modminds.init.ModmindsModItems;

public class CandadoTikiRightClickedOnEntityProcedure {
   public static void execute(LevelAccessor world, Entity entity, Entity sourceentity) {
      if (entity != null && sourceentity != null) {
         if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem()
               == ModmindsModItems.LLAVE_TIKI_VERDE.get()
            && (entity instanceof CandadoTikiEntity animatable ? animatable.getTexture() : "null").equals("green_lock")) {
            (sourceentity instanceof LivingEntity _livEntx ? _livEntx.getMainHandItem() : ItemStack.EMPTY).shrink(1);
            if (entity instanceof CandadoTikiEntity) {
               ((CandadoTikiEntity)entity).setAnimation("introducing");
            }

            ModmindsMod.queueServerWork(200, () -> {
               if (!entity.level().isClientSide()) {
                  entity.discard();
               }
            });
         } else if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem()
               == ModmindsModItems.LLAVE_TIKI_ROSA.get()
            && (entity instanceof CandadoTikiEntity animatable ? animatable.getTexture() : "null").equals("pink_lock")) {
            (sourceentity instanceof LivingEntity _livEntx ? _livEntx.getMainHandItem() : ItemStack.EMPTY).shrink(1);
            if (entity instanceof CandadoTikiEntity) {
               ((CandadoTikiEntity)entity).setAnimation("introducing");
            }

            ModmindsMod.queueServerWork(200, () -> {
               if (!entity.level().isClientSide()) {
                  entity.discard();
               }
            });
         } else if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem()
               == ModmindsModItems.LLAVE_TIKI_ROJA.get()
            && (entity instanceof CandadoTikiEntity animatable ? animatable.getTexture() : "null").equals("red_lock")) {
            (sourceentity instanceof LivingEntity _livEntx ? _livEntx.getMainHandItem() : ItemStack.EMPTY).shrink(1);
            if (entity instanceof CandadoTikiEntity) {
               ((CandadoTikiEntity)entity).setAnimation("introducing");
            }

            ModmindsMod.queueServerWork(200, () -> {
               if (!entity.level().isClientSide()) {
                  entity.discard();
               }
            });
         } else if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem()
               == ModmindsModItems.LLAVE_TIKI_AZUL.get()
            && (entity instanceof CandadoTikiEntity animatable ? animatable.getTexture() : "null").equals("blue_lock")) {
            (sourceentity instanceof LivingEntity _livEntx ? _livEntx.getMainHandItem() : ItemStack.EMPTY).shrink(1);
            if (entity instanceof CandadoTikiEntity) {
               ((CandadoTikiEntity)entity).setAnimation("introducing");
            }

            ModmindsMod.queueServerWork(200, () -> {
               if (!entity.level().isClientSide()) {
                  entity.discard();
               }
            });
         } else if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem()
            == ModmindsModItems.ORBE_ROSA.get()) {
            if (entity instanceof CandadoTikiEntity animatable) {
               animatable.setTexture("pink_lock");
            }
         } else if ((sourceentity instanceof LivingEntity _livEntx ? _livEntx.getMainHandItem() : ItemStack.EMPTY).getItem()
            == ModmindsModItems.ORBE_VERDE.get()) {
            if (entity instanceof CandadoTikiEntity animatable) {
               animatable.setTexture("green_lock");
            }
         } else if ((sourceentity instanceof LivingEntity _livEntxx ? _livEntxx.getMainHandItem() : ItemStack.EMPTY).getItem()
            == ModmindsModItems.ORBE_AZUL.get()) {
            if (entity instanceof CandadoTikiEntity animatable) {
               animatable.setTexture("blue_lock");
            }
         } else if ((sourceentity instanceof LivingEntity _livEntxxx ? _livEntxxx.getMainHandItem() : ItemStack.EMPTY).getItem()
               == ModmindsModItems.ORBE_ROJO.get()
            && entity instanceof CandadoTikiEntity animatable) {
            animatable.setTexture("red_lock");
         }
      }
   }
}
