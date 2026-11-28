package net.ruleta.compat;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.portal.TeleportTransition;
import org.jetbrains.annotations.Nullable;

public final class Lanzador {
   private Lanzador() {
   }

   private static boolean cliente(Entity e) {
      return e.level().isClientSide();
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

   public static boolean viaje(Entity e, TeleportTransition destino) {
      return !cliente(e) && destino != null && destino.newLevel() != e.level() && Bus.hayOyentes(EntityTravelToDimensionEvent.class)
         ? Bus.post(new EntityTravelToDimensionEvent(e, destino.newLevel().dimension()))
         : false;
   }

   /** En 26.3 targetConditions es final: se cambia por reflexión para que el objetivo no necesite verse. */
   public static void sinLineaDeVision(net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal<?> meta) {
      try {
         java.lang.reflect.Field f = net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal.class.getDeclaredField("targetConditions");
         f.setAccessible(true);
         f.set(meta, ((net.minecraft.world.entity.ai.targeting.TargetingConditions)f.get(meta)).ignoreLineOfSight());
      } catch (ReflectiveOperationException e) {
         // sin cambios: el objetivo seguirá necesitando línea de visión
      }
   }

   public static java.util.UUID uuidDe(net.minecraft.nbt.Tag tag) {
      return tag instanceof net.minecraft.nbt.IntArrayTag a ? net.minecraft.core.UUIDUtil.uuidFromIntArray(a.getAsIntArray()) : null;
   }

   public static net.minecraft.nbt.Tag uuidATag(java.util.UUID u) {
      return new net.minecraft.nbt.IntArrayTag(net.minecraft.core.UUIDUtil.uuidToIntArray(u));
   }

   public static java.util.List<net.minecraft.world.item.ItemStack> armadura(net.minecraft.world.entity.LivingEntity e) {
      java.util.List<net.minecraft.world.item.ItemStack> l = new java.util.ArrayList<>();
      for (net.minecraft.world.entity.EquipmentSlot s : new net.minecraft.world.entity.EquipmentSlot[]{
         net.minecraft.world.entity.EquipmentSlot.FEET, net.minecraft.world.entity.EquipmentSlot.LEGS,
         net.minecraft.world.entity.EquipmentSlot.CHEST, net.minecraft.world.entity.EquipmentSlot.HEAD}) {
         l.add(e.getItemBySlot(s));
      }
      return l;
   }
}
