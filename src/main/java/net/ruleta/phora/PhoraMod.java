package net.ruleta.phora;

import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;
import net.ruleta.Reg;

public final class PhoraMod {
   public static final Reg<EntityType<MisilEntity>> MISIL = Reg.entidad(
      "ruletafanproject",
      "misil",
      () -> EntityType.Builder.<MisilEntity>of(MisilEntity::new, MobCategory.MISC).sized(2.0F, 2.0F).clientTrackingRange(20).updateInterval(1).fireImmune().noSave().build(net.ruleta.compat.Ids.entidad("misil"))
   );
   public static final Reg<Item> PALO = Reg.item(
      "ruletafanproject", "palo_phora", () -> new PaloPhora(new Properties().stacksTo(1).rarity(Rarity.EPIC).fireResistant())
   );

   private PhoraMod() {
   }

   public static void init() {
   }
}
