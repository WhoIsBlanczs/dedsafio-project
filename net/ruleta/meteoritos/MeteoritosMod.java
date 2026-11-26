package net.ruleta.meteoritos;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;
import net.ruleta.Reg;

public final class MeteoritosMod {
   public static final Reg<EntityType<MeteoritoEntity>> METEORITO = Reg.entidadFabric(
      "ruleta",
      "meteorito",
      FabricEntityTypeBuilder.create(MobCategory.MISC, MeteoritoEntity::new)
         .dimensions(EntityDimensions.fixed(2.0F, 2.0F))
         .trackRangeChunks(28)
         .trackedUpdateRate(1)
         .forceTrackedVelocityUpdates(true)
         .fireImmune()
         .disableSaving()
   );
   public static final Reg<Item> PALO = Reg.item(
      "ruleta", "palo_meteorito", () -> new PaloMeteorito(new Properties().stacksTo(1).rarity(Rarity.EPIC).fireResistant())
   );

   private MeteoritosMod() {
   }

   public static void init() {
   }
}
