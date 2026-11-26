package net.ruleta.creeperrojo;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.ruleta.Ajustes;
import net.ruleta.Efectos;
import net.ruleta.Reg;
import net.ruleta.compat.ExplosionCentro;
import net.ruleta.compat.ExplosionEvent;
import net.ruleta.compat.SubscribeEvent;

public final class CreeperRojo {
   public static final double RADIO_EFECTO = 12.0;
   public static final Reg<EntityType<CreeperRojoEntity>> TIPO = Reg.entidad(
      "ruleta", "creeper_rojo", () -> Builder.of(CreeperRojoEntity::new, MobCategory.MONSTER).sized(0.6F, 1.0F).clientTrackingRange(8).build("creeper_rojo")
   );
   public static final Reg<Item> HUEVO = Reg.item("ruleta", "creeper_rojo_spawn_egg", () -> new SpawnEggItem(TIPO.get(), 14687764, 5900805, new Properties()));

   private static double radioEfecto() {
      return Ajustes.entero("creeperrojo.radio");
   }

   private CreeperRojo() {
   }

   public static void init() {
      atributos();
   }

   private static void atributos() {
      FabricDefaultAttributeRegistry.register(TIPO.get(), Creeper.createAttributes().build());
   }

   public static final class Eventos {
      private Eventos() {
      }

      @SubscribeEvent
      public static void alDetonar(ExplosionEvent.Detonate evento) {
         Explosion explosion = evento.getExplosion();
         if (explosion.getDirectSourceEntity() instanceof CreeperRojoEntity) {
            evento.getAffectedBlocks().clear();
            evento.getAffectedEntities().clear();
            Vec3 centro = ExplosionCentro.de(explosion);
            AABB zona = new AABB(
               centro.x - CreeperRojo.radioEfecto(),
               centro.y - CreeperRojo.radioEfecto(),
               centro.z - CreeperRojo.radioEfecto(),
               centro.x + CreeperRojo.radioEfecto(),
               centro.y + CreeperRojo.radioEfecto(),
               centro.z + CreeperRojo.radioEfecto()
            );

            for (Player jugador : evento.getLevel().getEntitiesOfClass(Player.class, zona)) {
               jugador.addEffect(new MobEffectInstance(Efectos.PANTALLA_AZUL.get(), 400, 0, false, false, false));
            }
         }
      }
   }
}
