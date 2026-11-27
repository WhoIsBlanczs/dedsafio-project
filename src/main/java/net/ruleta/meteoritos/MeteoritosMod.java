package net.ruleta.meteoritos;

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
      k -> net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType.Builder.create(MeteoritoEntity::new, MobCategory.MISC).sized(2.0F, 2.0F).trackRangeChunks(28).trackedUpdateRate(1).forceTrackedVelocityUpdates(true).fireImmune().noSave().build(k)
   );
   public static final Reg<Item> PALO = Reg.item(
      "ruleta", "palo_meteorito", () -> new PaloMeteorito(net.ruleta.Reg.props().stacksTo(1).rarity(Rarity.EPIC).fireResistant())
   );

   private MeteoritosMod() {
   }

   public static void init() {
   }
}
