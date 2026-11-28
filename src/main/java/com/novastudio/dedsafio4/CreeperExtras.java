package com.novastudio.dedsafio4;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.Registry;
import net.minecraft.world.phys.AABB;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;

public final class CreeperExtras {
   static final int RADIUS = 6;
   public static Holder<MobEffect> BLANCO_Y_NEGRO;
   public static Holder<MobEffect> CONTROLES_INVERTIDOS;
   public static Holder<MobEffect> COLORES_INVERTIDOS;

   private CreeperExtras() {
   }

   public static void register() {
      BLANCO_Y_NEGRO = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, Identifier.fromNamespaceAndPath("nexo", "blanco_y_negro"), new CreeperExtras.Effect(9079434));
      CONTROLES_INVERTIDOS = Registry.registerForHolder(
         BuiltInRegistries.MOB_EFFECT, Identifier.fromNamespaceAndPath("nexo", "controles_invertidos"), new CreeperExtras.Effect(14729248)
      );
      COLORES_INVERTIDOS = Registry.registerForHolder(
         BuiltInRegistries.MOB_EFFECT, Identifier.fromNamespaceAndPath("nexo", "colores_invertidos"), new CreeperExtras.Effect(13122740)
      );
   }

   public static int fuse(int var0, String var1) {
      return !"tronco".equals(var1) && !"floral".equals(var1) ? var0 : Math.max(1, var0 / 2);
   }

   public static void onExplode(Object var0) {
      if (var0 instanceof Entity var1 && !var1.level().isClientSide()) {
         String var2 = BuiltInRegistries.ENTITY_TYPE.getKey(var1.getType()).getPath();

         for (Player var4 : var1.level()
            .getEntitiesOfClass(Player.class, new AABB(var1.blockPosition()).inflate(6.0), var0x -> var0x.isAlive() && !var0x.isSpectator())) {
            apply(var4, var2);
         }
      }
   }

   static void apply(Player var0, String var1) {
      switch (var1) {
         case "creeper_tronco":
            var0.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 2400, 0));
            var0.addEffect(new MobEffectInstance(BLANCO_Y_NEGRO, 3600, 0));
            break;
         case "creeper_dorado":
            var0.addEffect(new MobEffectInstance(CONTROLES_INVERTIDOS, 3600, 0));
            break;
         case "creeper_floral":
            var0.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 600, 0));
            var0.addEffect(new MobEffectInstance(MobEffects.POISON, 700, 1));
            var0.addEffect(new MobEffectInstance(COLORES_INVERTIDOS, 1800, 0));
      }
   }

   private static final class Effect extends MobEffect {
      Effect(int var1) {
         super(MobEffectCategory.HARMFUL, var1);
      }
   }
}
