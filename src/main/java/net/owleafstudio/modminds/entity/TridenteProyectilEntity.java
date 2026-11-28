package net.owleafstudio.modminds.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import net.owleafstudio.modminds.compat.ArrowCompat;
import net.owleafstudio.modminds.init.ModmindsModEntities;

@OnlyIn(
   value = Dist.CLIENT,
   _interface = ItemSupplier.class
)
public class TridenteProyectilEntity extends AbstractArrow implements ItemSupplier {
   public static final ItemStack PROJECTILE_ITEM = new ItemStack(Items.TRIDENT);

   public TridenteProyectilEntity(EntityType<TridenteProyectilEntity> type, Level world) {
      super(type, world);
   }

   public TridenteProyectilEntity(EntityType<TridenteProyectilEntity> type, double x, double y, double z, Level world) {
      super(type, x, y, z, world, ItemStack.EMPTY, ItemStack.EMPTY);
   }

   public TridenteProyectilEntity(EntityType<TridenteProyectilEntity> type, LivingEntity entity, Level world) {
      super(type, entity, world, ItemStack.EMPTY, ItemStack.EMPTY);
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
      return new ClientboundAddEntityPacket(this, serverEntity);
   }

   @OnlyIn(Dist.CLIENT)
   public ItemStack getItem() {
      return PROJECTILE_ITEM;
   }

   protected ItemStack getDefaultPickupItem() {
      return PROJECTILE_ITEM;
   }

   protected void doPostHurtEffects(LivingEntity entity) {
      super.doPostHurtEffects(entity);
      ArrowCompat.apply(this, entity);
      entity.setArrowCount(entity.getArrowCount() - 1);
   }

   public void tick() {
      super.tick();
      if (this.inGround) {
         this.discard();
      }
   }

   public static TridenteProyectilEntity shoot(Level world, LivingEntity entity, RandomSource source) {
      return shoot(world, entity, source, 2.0F, 2.0, 5);
   }

   public static TridenteProyectilEntity shoot(Level world, LivingEntity entity, RandomSource source, float pullingPower) {
      return shoot(world, entity, source, pullingPower * 2.0F, 2.0, 5);
   }

   public static TridenteProyectilEntity shoot(Level world, LivingEntity entity, RandomSource random, float power, double damage, int knockback) {
      TridenteProyectilEntity entityarrow = new TridenteProyectilEntity(ModmindsModEntities.TRIDENTE_PROYECTIL.get(), entity, world);
      entityarrow.shoot(entity.getViewVector(1.0F).x, entity.getViewVector(1.0F).y, entity.getViewVector(1.0F).z, power * 2.0F, 0.0F);
      entityarrow.setSilent(true);
      entityarrow.setCritArrow(false);
      entityarrow.setBaseDamage(damage);
      ArrowCompat.setKnockback(entityarrow, knockback);
      world.addFreshEntity(entityarrow);
      world.playSound(
         null,
         entity.getX(),
         entity.getY(),
         entity.getZ(),
         ForgeRegistries.SOUND_EVENTS.getValue(Identifier.parse("entity.arrow.shoot")),
         SoundSource.PLAYERS,
         1.0F,
         1.0F / (random.nextFloat() * 0.5F + 1.0F) + power / 2.0F
      );
      return entityarrow;
   }

   public static TridenteProyectilEntity shoot(LivingEntity entity, LivingEntity target) {
      TridenteProyectilEntity entityarrow = new TridenteProyectilEntity(ModmindsModEntities.TRIDENTE_PROYECTIL.get(), entity, entity.level());
      double dx = target.getX() - entity.getX();
      double dy = target.getY() + target.getEyeHeight() - 1.1;
      double dz = target.getZ() - entity.getZ();
      entityarrow.shoot(dx, dy - entityarrow.getY() + Math.hypot(dx, dz) * 0.2F, dz, 4.0F, 12.0F);
      entityarrow.setSilent(true);
      entityarrow.setBaseDamage(2.0);
      ArrowCompat.setKnockback(entityarrow, 5);
      entityarrow.setCritArrow(false);
      entity.level().addFreshEntity(entityarrow);
      entity.level()
         .playSound(
            null,
            entity.getX(),
            entity.getY(),
            entity.getZ(),
            ForgeRegistries.SOUND_EVENTS.getValue(Identifier.parse("entity.arrow.shoot")),
            SoundSource.PLAYERS,
            1.0F,
            1.0F / (RandomSource.create().nextFloat() * 0.5F + 1.0F)
         );
      return entityarrow;
   }
}
