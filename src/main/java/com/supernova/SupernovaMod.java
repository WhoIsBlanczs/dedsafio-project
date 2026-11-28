package com.supernova;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;
import net.ruleta.Reg;

public class SupernovaMod {
   public static final String MODID = "supernova";
   public static final Reg<EntityType<SupernovaEntity>> SUPERNOVA = Reg.entidad(
      "supernova",
      "supernova",
      () -> Builder.of(SupernovaEntity::new, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .clientTrackingRange(64)
         .updateInterval(20)
         .fireImmune()
         .noSave()
         .build(net.ruleta.compat.Ids.entidad("supernova:supernova"))
   );
   public static final Reg<SoundEvent> SND_IDLE = sound("idle_hum");
   public static final Reg<SoundEvent> SND_COLAPSO = sound("colapso");
   public static final Reg<SoundEvent> SND_WAVE = sound("wave");
   public static final Reg<SoundEvent> SND_MUSIC = sound("music");
   public static final ResourceKey<DamageType> SUPERNOVA_DAMAGE = ResourceKey.create(
      Registries.DAMAGE_TYPE, Identifier.fromNamespaceAndPath("supernova", "supernova")
   );

   private static Reg<SoundEvent> sound(String name) {
      return Reg.sonido("supernova", name, () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("supernova", name)));
   }

   public static void init() {
      SupernovaNet.init();
   }
}
