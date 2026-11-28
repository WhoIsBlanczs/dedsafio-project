package net.ruleta;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;

public final class Persistente {
   private static final AttachmentType<CompoundTag> DATOS = AttachmentRegistry.builder()
      .persistent(CompoundTag.CODEC)
      .copyOnDeath()
      .initializer(CompoundTag::new)
      .buildAndRegister(Reg.id("ruletafanproject", "datos"));

   private Persistente() {
   }

   public static CompoundTag de(Entity entidad) {
      return (CompoundTag)entidad.getAttachedOrCreate(DATOS);
   }
}
