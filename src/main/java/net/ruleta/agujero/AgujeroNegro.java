package net.ruleta.agujero;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;
import net.ruleta.Reg;

public final class AgujeroNegro {
   public static final Reg<EntityType<AgujeroNegroEntity>> TIPO = Reg.entidad(
      "ruletafanproject",
      "agujero_negro",
      () -> Builder.of(AgujeroNegroEntity::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(32)
         .updateInterval(20)
         .fireImmune()
         .build(net.ruleta.compat.Ids.entidad("agujero_negro"))
   );

   private AgujeroNegro() {
   }

   public static void init() {
   }
}
