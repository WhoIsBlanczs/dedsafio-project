package net.ruleta.creeperes;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

public class CreeperCespedEntity extends Creeper {
   public CreeperCespedEntity(EntityType<? extends Creeper> tipo, Level nivel) {
      super(tipo, nivel);
   }

   public SpawnGroupData finalizeSpawn(ServerLevelAccessor nivel, DifficultyInstance dificultad, EntitySpawnReason razon, @Nullable SpawnGroupData datos) {
      SpawnGroupData r = super.finalizeSpawn(nivel, dificultad, razon, datos);
      mechaInstantanea(this);
      return r;
   }

   static void mechaInstantanea(Creeper c) {
      net.minecraft.world.level.storage.TagValueOutput salida = net.minecraft.world.level.storage.TagValueOutput.createWithContext(net.minecraft.util.ProblemReporter.DISCARDING, c.registryAccess());
      c.saveWithoutId(salida);
      CompoundTag t = salida.buildResult();
      t.putShort("Fuse", (short)1);
      c.load(net.minecraft.world.level.storage.TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING, c.registryAccess(), t));
   }
}
