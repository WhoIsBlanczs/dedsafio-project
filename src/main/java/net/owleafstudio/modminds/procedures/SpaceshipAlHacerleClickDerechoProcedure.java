package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.registries.ForgeRegistries;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.entity.SpaceshipEntity;
import net.owleafstudio.modminds.init.ModmindsModItems;

public class SpaceshipAlHacerleClickDerechoProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
      if (entity != null && sourceentity != null) {
         if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem()
               == ModmindsModItems.CABINA_FRONTAL_DE_LA_NAVE.get()
            && (entity instanceof SpaceshipEntity animatable ? animatable.getTexture() : "null").equals("1spaceship")) {
            if (entity instanceof SpaceshipEntity animatablex) {
               animatablex.setTexture("2spaceship");
            }

            (sourceentity instanceof LivingEntity _livEntx ? _livEntx.getMainHandItem() : ItemStack.EMPTY).shrink(1);
            if (world instanceof Level _level) {
               if (!_level.isClientSide()) {
                  _level.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     ForgeRegistries.SOUND_EVENTS.getValue(Identifier.parse("modminds:hammer_1")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F
                  );
               } else {
                  _level.playLocalSound(
                     x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(Identifier.parse("modminds:hammer_1")), SoundSource.NEUTRAL, 1.0F, 1.0F, false
                  );
               }
            }
         }

         if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem()
               == ModmindsModItems.ALA_DERECHA_FRONTAL_DE_NAVE.get()
            && (entity instanceof SpaceshipEntity animatable ? animatable.getTexture() : "null").equals("2spaceship")) {
            if (entity instanceof SpaceshipEntity animatablex) {
               animatablex.setTexture("3spaceship");
            }

            (sourceentity instanceof LivingEntity _livEntx ? _livEntx.getMainHandItem() : ItemStack.EMPTY).shrink(1);
            if (world instanceof Level _levelx) {
               if (!_levelx.isClientSide()) {
                  _levelx.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     ForgeRegistries.SOUND_EVENTS.getValue(Identifier.parse("modminds:hammer_2")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F
                  );
               } else {
                  _levelx.playLocalSound(
                     x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(Identifier.parse("modminds:hammer_2")), SoundSource.NEUTRAL, 1.0F, 1.0F, false
                  );
               }
            }
         }

         if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem()
               == ModmindsModItems.ALA_IZQUIERDA_FRONTAL_DE_NAVE.get()
            && (entity instanceof SpaceshipEntity animatable ? animatable.getTexture() : "null").equals("3spaceship")) {
            if (entity instanceof SpaceshipEntity animatablex) {
               animatablex.setTexture("4spaceship");
            }

            (sourceentity instanceof LivingEntity _livEntx ? _livEntx.getMainHandItem() : ItemStack.EMPTY).shrink(1);
            if (world instanceof Level _levelxx) {
               if (!_levelxx.isClientSide()) {
                  _levelxx.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     ForgeRegistries.SOUND_EVENTS.getValue(Identifier.parse("modminds:hammer_3")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F
                  );
               } else {
                  _levelxx.playLocalSound(
                     x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(Identifier.parse("modminds:hammer_3")), SoundSource.NEUTRAL, 1.0F, 1.0F, false
                  );
               }
            }
         }

         if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem()
               == ModmindsModItems.ALA_DERECHA_DE_NAVE.get()
            && (entity instanceof SpaceshipEntity animatable ? animatable.getTexture() : "null").equals("4spaceship")) {
            if (entity instanceof SpaceshipEntity animatablex) {
               animatablex.setTexture("5spaceship");
            }

            (sourceentity instanceof LivingEntity _livEntx ? _livEntx.getMainHandItem() : ItemStack.EMPTY).shrink(1);
            if (world instanceof Level _levelxxx) {
               if (!_levelxxx.isClientSide()) {
                  _levelxxx.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     ForgeRegistries.SOUND_EVENTS.getValue(Identifier.parse("modminds:hammer_1")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F
                  );
               } else {
                  _levelxxx.playLocalSound(
                     x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(Identifier.parse("modminds:hammer_1")), SoundSource.NEUTRAL, 1.0F, 1.0F, false
                  );
               }
            }
         }

         if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem()
               == ModmindsModItems.ALA_IZQUIERDA_DE_NAVE.get()
            && (entity instanceof SpaceshipEntity animatable ? animatable.getTexture() : "null").equals("5spaceship")) {
            if (entity instanceof SpaceshipEntity animatablex) {
               animatablex.setTexture("6spaceship");
            }

            (sourceentity instanceof LivingEntity _livEntx ? _livEntx.getMainHandItem() : ItemStack.EMPTY).shrink(1);
            if (world instanceof Level _levelxxxx) {
               if (!_levelxxxx.isClientSide()) {
                  _levelxxxx.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     ForgeRegistries.SOUND_EVENTS.getValue(Identifier.parse("modminds:hammer_2")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F
                  );
               } else {
                  _levelxxxx.playLocalSound(
                     x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(Identifier.parse("modminds:hammer_2")), SoundSource.NEUTRAL, 1.0F, 1.0F, false
                  );
               }
            }
         }

         if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem()
               == ModmindsModItems.PROPULSOR_DE_DILITIO_DERECHO.get()
            && (entity instanceof SpaceshipEntity animatable ? animatable.getTexture() : "null").equals("6spaceship")) {
            if (entity instanceof SpaceshipEntity animatablex) {
               animatablex.setTexture("7spaceship");
            }

            (sourceentity instanceof LivingEntity _livEntx ? _livEntx.getMainHandItem() : ItemStack.EMPTY).shrink(1);
            if (world instanceof Level _levelxxxxx) {
               if (!_levelxxxxx.isClientSide()) {
                  _levelxxxxx.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     ForgeRegistries.SOUND_EVENTS.getValue(Identifier.parse("modminds:hammer_3")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F
                  );
               } else {
                  _levelxxxxx.playLocalSound(
                     x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(Identifier.parse("modminds:hammer_3")), SoundSource.NEUTRAL, 1.0F, 1.0F, false
                  );
               }
            }
         }

         if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem()
               == ModmindsModItems.PROPULSOR_DE_DILITIO_IZQUIERDA.get()
            && (entity instanceof SpaceshipEntity animatable ? animatable.getTexture() : "null").equals("7spaceship")) {
            if (entity instanceof SpaceshipEntity animatablex) {
               animatablex.setTexture("8spaceship");
            }

            (sourceentity instanceof LivingEntity _livEntx ? _livEntx.getMainHandItem() : ItemStack.EMPTY).shrink(1);
            if (world instanceof Level _levelxxxxxx) {
               if (!_levelxxxxxx.isClientSide()) {
                  _levelxxxxxx.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     ForgeRegistries.SOUND_EVENTS.getValue(Identifier.parse("modminds:finish")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F
                  );
               } else {
                  _levelxxxxxx.playLocalSound(
                     x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(Identifier.parse("modminds:finish")), SoundSource.NEUTRAL, 1.0F, 1.0F, false
                  );
               }
            }

            if (!entity.level().isClientSide() && CompatAccess.server(entity) != null) {
               CompatAccess.server(entity)
                  .getCommands()
                  .performPrefixedCommand(
                     net.owleafstudio.modminds.compat.Compat.fuente(CommandSource.NULL, entity.position(), entity.getRotationVector(), entity.level() instanceof ServerLevel ? (ServerLevel)entity.level() : null, 4, entity.getName().getString(), entity.getDisplayName(), entity.level().getServer(), entity),
                     "summon comicsafio:spaceship"
                  );
            }

            if (!entity.level().isClientSide()) {
               entity.discard();
            }
         }
      }
   }
}
