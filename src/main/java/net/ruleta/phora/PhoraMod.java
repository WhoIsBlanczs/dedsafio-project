package net.ruleta.phora;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;
import net.ruleta.Reg;

public final class PhoraMod {
   public static final Reg<EntityType<MisilEntity>> MISIL = Reg.entidadFabric(
      "ruletafanproject",
      "misil",
      FabricEntityTypeBuilder.create(MobCategory.MISC, MisilEntity::new)
         .dimensions(EntityDimensions.fixed(2.0F, 2.0F))
         .trackRangeChunks(20)
         .trackedUpdateRate(1)
         .forceTrackedVelocityUpdates(true)
         .fireImmune()
         .disableSaving()
   );
   public static final Reg<Item> PALO = Reg.item(
      "ruletafanproject", "palo_phora", () -> new PaloPhora(new Properties().stacksTo(1).rarity(Rarity.EPIC).fireResistant())
   );

   private PhoraMod() {
   }

   public static void init() {
   }
}
