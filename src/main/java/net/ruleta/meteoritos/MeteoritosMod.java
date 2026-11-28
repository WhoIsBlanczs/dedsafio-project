package net.ruleta.meteoritos;

import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;
import net.ruleta.Reg;

public final class MeteoritosMod {
   public static final Reg<EntityType<MeteoritoEntity>> METEORITO = Reg.entidad(
      "ruletafanproject",
      "meteorito",
      () -> EntityType.Builder.<MeteoritoEntity>of(MeteoritoEntity::new, MobCategory.MISC).sized(2.0F, 2.0F).clientTrackingRange(28).updateInterval(1).fireImmune().noSave().build(net.ruleta.compat.Ids.entidad("meteorito"))
   );
   public static final Reg<Item> PALO = Reg.item(
      "ruletafanproject", "palo_meteorito", () -> new PaloMeteorito(new Properties().stacksTo(1).rarity(Rarity.EPIC).fireResistant())
   );

   private MeteoritosMod() {
   }

   public static void init() {
   }
}
