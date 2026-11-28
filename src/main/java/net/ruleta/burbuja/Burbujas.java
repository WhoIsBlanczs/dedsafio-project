package net.ruleta.burbuja;

import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.ruleta.Reg;

public final class Burbujas {
   public static final Reg<EntityType<Burbuja>> BURBUJA = Reg.entidad(
      "ruletafanproject",
      "burbuja",
      () -> Builder.of(Burbuja::new, MobCategory.MISC)
         .sized(2.2F, 2.2F)
         .clientTrackingRange(10)
         .updateInterval(2)
         .fireImmune()
         .build("ruletafanproject:burbuja")
   );
   public static final Reg<Item> ITEM = Reg.item("ruletafanproject", "burbuja", () -> new ItemBurbuja(new Properties().stacksTo(1).rarity(Rarity.EPIC)));

   private Burbujas() {
   }

   public static void init() {
   }

   public static void quitarDe(MinecraftServer server, UUID propietario) {
      for (ServerLevel nivel : server.getAllLevels()) {
         for (Burbuja b : nivel.getEntities(EntityTypeTest.forClass(Burbuja.class), x -> propietario.equals(x.getPropietario()))) {
            b.ejectPassengers();
            b.discard();
         }
      }
   }
}
