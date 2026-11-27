package net.ruleta.compat;

import java.util.function.Consumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;

/** Puente para el guardado de entidades con ValueInput/ValueOutput (26.x). */
public final class Nbt {
   private Nbt() {
   }

   public static void editar(Creeper c, Consumer<CompoundTag> cambios) {
      TagValueOutput out = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, c.level().registryAccess());
      c.addAdditionalSaveData(out);
      CompoundTag tag = out.buildResult();
      cambios.accept(tag);
      c.readAdditionalSaveData(TagValueInput.create(ProblemReporter.DISCARDING, c.level().registryAccess(), tag));
   }
}
