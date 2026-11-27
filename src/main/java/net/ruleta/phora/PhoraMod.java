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
      k -> net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType.Builder.create(MisilEntity::new, MobCategory.MISC).sized(2.0F, 2.0F).trackRangeChunks(20).trackedUpdateRate(1).forceTrackedVelocityUpdates(true).fireImmune().noSave().build(k)
   );
   public static final Reg<Item> PALO = Reg.item("ruleta", "palo_phora", () -> new PaloPhora(net.ruleta.Reg.props().stacksTo(1).rarity(Rarity.EPIC).fireResistant()));

   private PhoraMod() {
   }

   public static void init() {
   }
}
