package net.ruleta.phora;

import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;
import net.ruleta.Reg;

public final class PhoraMod {
   public static final Reg<EntityType<MisilEntity>> MISIL = Reg.entidadFabric(
      "ruleta",
      "misil",
      k -> net.minecraft.world.entity.EntityType.Builder.of(MisilEntity::new, MobCategory.MISC).sized(2.0F, 2.0F).clientTrackingRange(20).updateInterval(1).fireImmune().noSave().build(k)
   );
   public static final Reg<Item> PALO = Reg.item("ruleta", "palo_phora", () -> new PaloPhora(net.ruleta.Reg.props().stacksTo(1).rarity(Rarity.EPIC)));

   private PhoraMod() {
   }

   public static void init() {
   }
}
