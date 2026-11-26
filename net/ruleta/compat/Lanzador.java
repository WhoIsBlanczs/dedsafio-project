package net.ruleta.compat;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.portal.DimensionTransition;
import org.jetbrains.annotations.Nullable;

public final class Lanzador {
   private Lanzador() {
   }

   private static boolean cliente(Entity e) {
      return e.level().isClientSide;
   }

   public static boolean muerte(LivingEntity e, DamageSource fuente) {
      return !cliente(e) && Bus.hayOyentes(LivingDeathEvent.class) ? Bus.post(new LivingDeathEvent(e, fuente)) : false;
   }

   public static float dano(LivingEntity e, @Nullable DamageSource fuente, float cantidad) {
      if (!cliente(e) && fuente != null && Bus.hayOyentes(LivingHurtEvent.class)) {
         LivingHurtEvent ev = new LivingHurtEvent(e, fuente, cantidad);
         return Bus.post(ev) ? 0.0F : ev.getAmount();
      } else {
         return cantidad;
      }
   }

   public static float danoTrasArmadura(LivingEntity e, @Nullable DamageSource fuente, float cantidad) {
      if (!cliente(e) && fuente != null && Bus.hayOyentes(LivingDamageEvent.class)) {
         LivingDamageEvent ev = new LivingDamageEvent(e, fuente, cantidad);
         return Bus.post(ev) ? 0.0F : ev.getAmount();
      } else {
         return cantidad;
      }
   }

   public static double visibilidad(LivingEntity e, @Nullable Entity observador, double base) {
      if (!cliente(e) && Bus.hayOyentes(LivingEvent.LivingVisibilityEvent.class)) {
         LivingEvent.LivingVisibilityEvent ev = new LivingEvent.LivingVisibilityEvent(e, observador, 1.0);
         Bus.post(ev);
         return base * ev.getVisibilityModifier();
      } else {
         return base;
      }
   }

   public static void tick(LivingEntity e) {
      if (!cliente(e) && Bus.hayOyentes(LivingEvent.LivingTickEvent.class)) {
         Bus.post(new LivingEvent.LivingTickEvent(e));
      }
   }

   public static ItemStack terminarDeUsar(LivingEntity e, ItemStack copiaDelObjeto, ItemStack resultado) {
      if (!cliente(e) && Bus.hayOyentes(LivingEntityUseItemEvent.Finish.class)) {
         LivingEntityUseItemEvent.Finish ev = new LivingEntityUseItemEvent.Finish(e, copiaDelObjeto, resultado);
         Bus.post(ev);
         return ev.getResultStack();
      } else {
         return resultado;
      }
   }

   public static void totem(LivingEntity e, DamageSource fuente, ItemStack copiaDelTotem) {
      if (!cliente(e) && Bus.hayOyentes(LivingUseTotemEvent.class)) {
         Bus.post(new LivingUseTotemEvent(e, fuente, copiaDelTotem));
      }
   }

   public static void botin(LivingEntity e, DamageSource fuente) {
      if (!cliente(e) && Bus.hayOyentes(LivingDropsEvent.class)) {
         List<ItemEntity> extra = new ArrayList<>();
         if (!Bus.post(new LivingDropsEvent(e, fuente, extra))) {
            for (ItemEntity item : extra) {
               e.level().addFreshEntity(item);
            }
         }
      }
   }

   public static boolean viaje(Entity e, DimensionTransition destino) {
      return !cliente(e) && destino != null && destino.newLevel() != e.level() && Bus.hayOyentes(EntityTravelToDimensionEvent.class)
         ? Bus.post(new EntityTravelToDimensionEvent(e, destino.newLevel().dimension()))
         : false;
   }
}
