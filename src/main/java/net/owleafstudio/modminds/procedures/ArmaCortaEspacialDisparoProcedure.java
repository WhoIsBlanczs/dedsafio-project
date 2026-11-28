package net.owleafstudio.modminds.procedures;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkEvent;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.compat.ArrowCompat;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.compat.ItemCompat;
import net.owleafstudio.modminds.entity.BalaDeDilitioProyectilEntity;
import net.owleafstudio.modminds.init.ModmindsModEntities;
import net.owleafstudio.modminds.init.ModmindsModItems;
import net.owleafstudio.modminds.item.ArmaCortaEspacialItem;

@Mod.EventBusSubscriber({Dist.CLIENT})
public class ArmaCortaEspacialDisparoProcedure {
   @SubscribeEvent
   public static void onLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
      ModmindsMod.PACKET_HANDLER.sendToServer(new ArmaCortaEspacialDisparoProcedure.ArmaCortaEspacialDisparoMessage());
      execute(event.getLevel(), event.getEntity());
   }

   public static void execute(LevelAccessor world, Entity entity) {
      execute(null, world, entity);
   }

   private static void execute(@Nullable Event event, LevelAccessor world, Entity entity) {
      if (entity != null
         && !(
            (
                  entity instanceof Player _plrCldRem1
                     ? _plrCldRem1.getCooldowns()
                           .getCooldownPercent((entity instanceof LivingEntity _livEntx ? _livEntx.getMainHandItem() : ItemStack.EMPTY), 0.0F)
                        * 100.0F
                     : 0.0F
               )
               > 2.0F
         )
         && (entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == ModmindsModItems.ARMA_CORTA_ESPACIAL.get()) {
         if ((entity instanceof LivingEntity _livEntxx ? _livEntxx.getMainHandItem() : ItemStack.EMPTY).getItem() instanceof ArmaCortaEspacialItem) {
            CompatAccess.itemTag(entity instanceof LivingEntity _livEntxxx ? _livEntxxx.getMainHandItem() : ItemStack.EMPTY)
               .putString("geckoAnim", "animation.armacorta.shoot");
         }

         if ((entity instanceof LivingEntity _livEntxx ? _livEntxx.getMainHandItem() : ItemStack.EMPTY).getDamageValue() >= 33) {
            ItemStack _ist = entity instanceof LivingEntity _livEntxxx ? _livEntxxx.getMainHandItem() : ItemStack.EMPTY;
            if (ItemCompat.hurt(_ist, -1)) {
               _ist.shrink(1);
               _ist.setDamageValue(0);
            }
         } else if ((entity instanceof LivingEntity _livEntxxx ? _livEntxxx.getMainHandItem() : ItemStack.EMPTY).getDamageValue() < 32) {
            Level projectileLevel = entity.level();
            if (!projectileLevel.isClientSide()) {
               Projectile _entityToSpawn = (new Object() {
                  public Projectile getArrow(Level level, Entity shooter, float damage, int knockback) {
                     AbstractArrow entityToSpawn = new BalaDeDilitioProyectilEntity(ModmindsModEntities.BALA_DE_DILITIO_PROYECTIL.get(), level);
                     entityToSpawn.setOwner(shooter);
                     entityToSpawn.setBaseDamage(damage);
                     ArrowCompat.setKnockback(entityToSpawn, knockback);
                     entityToSpawn.setSilent(true);
                     return entityToSpawn;
                  }
               }).getArrow(projectileLevel, entity, 1.0F, 0);
               _entityToSpawn.setPos(entity.getX(), entity.getEyeY() - 0.1, entity.getZ());
               _entityToSpawn.shoot(entity.getLookAngle().x, entity.getLookAngle().y, entity.getLookAngle().z, 10.0F, 1.0F);
               projectileLevel.addFreshEntity(_entityToSpawn);
            }

            ItemStack _ist = entity instanceof LivingEntity _livEntxxxx ? _livEntxxxx.getMainHandItem() : ItemStack.EMPTY;
            if (ItemCompat.hurt(_ist, 1)) {
               _ist.shrink(1);
               _ist.setDamageValue(0);
            }
         } else if (entity instanceof Player _player && !_player.level().isClientSide()) {
            net.owleafstudio.modminds.compat.Compat.mensaje(_player, Component.literal("§4Recarga"), true);
         }
      }
   }

   @Mod.EventBusSubscriber(
      bus = Mod.EventBusSubscriber.Bus.MOD
   )
   public static class ArmaCortaEspacialDisparoMessage {
      public ArmaCortaEspacialDisparoMessage() {
      }

      public ArmaCortaEspacialDisparoMessage(FriendlyByteBuf buffer) {
      }

      public static void buffer(ArmaCortaEspacialDisparoProcedure.ArmaCortaEspacialDisparoMessage message, FriendlyByteBuf buffer) {
      }

      public static void handler(ArmaCortaEspacialDisparoProcedure.ArmaCortaEspacialDisparoMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
         NetworkEvent.Context context = contextSupplier.get();
         context.enqueueWork(() -> {
            if (context.getSender().level().hasChunkAt(context.getSender().blockPosition())) {
               ArmaCortaEspacialDisparoProcedure.execute(context.getSender().level(), context.getSender());
            }
         });
         context.setPacketHandled(true);
      }

      @SubscribeEvent
      public static void registerMessage(FMLCommonSetupEvent event) {
         ModmindsMod.addNetworkMessage(
            ArmaCortaEspacialDisparoProcedure.ArmaCortaEspacialDisparoMessage.class,
            ArmaCortaEspacialDisparoProcedure.ArmaCortaEspacialDisparoMessage::buffer,
            ArmaCortaEspacialDisparoProcedure.ArmaCortaEspacialDisparoMessage::new,
            ArmaCortaEspacialDisparoProcedure.ArmaCortaEspacialDisparoMessage::handler
         );
      }
   }
}
