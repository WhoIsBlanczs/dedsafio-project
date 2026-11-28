package net.owleafstudio.modminds.compat;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.resources.Identifier;

public final class PersistShim {
   private static final AttachmentType<CompoundTag> ENTITY = AttachmentRegistry.<CompoundTag>builder()
      .persistent(CompoundTag.CODEC)
      .copyOnDeath()
      .initializer(CompoundTag::new)
      .buildAndRegister(Identifier.fromNamespaceAndPath("modminds", "forge_persistent_data"));
   private static final AttachmentType<CompoundTag> LEVEL = AttachmentRegistry.<CompoundTag>builder()
      .persistent(CompoundTag.CODEC)
      .initializer(CompoundTag::new)
      .buildAndRegister(Identifier.fromNamespaceAndPath("modminds", "forge_level_persistent_data"));
   private static final AttachmentType<CompoundTag> BLOCK_ENTITY = AttachmentRegistry.<CompoundTag>builder()
      .persistent(CompoundTag.CODEC)
      .initializer(CompoundTag::new)
      .buildAndRegister(Identifier.fromNamespaceAndPath("modminds", "forge_blockentity_persistent_data"));

   private PersistShim() {
   }

   public static CompoundTag data(Entity entity) {
      return (CompoundTag)entity.getAttachedOrCreate(ENTITY);
   }

   public static CompoundTag data(Level level) {
      return (CompoundTag)level.getAttachedOrCreate(LEVEL);
   }

   public static CompoundTag data(BlockEntity blockEntity) {
      return (CompoundTag)blockEntity.getAttachedOrCreate(BLOCK_ENTITY);
   }
}
