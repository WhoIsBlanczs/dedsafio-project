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
import net.owleafstudio.modminds.entity.DardoTikiProyectilEntity;
import net.owleafstudio.modminds.init.ModmindsModEntities;
import net.owleafstudio.modminds.init.ModmindsModItems;
import net.owleafstudio.modminds.item.CerbatanaDeGoblinItem;

@Mod.EventBusSubscriber({Dist.CLIENT})
public class DisparoProcedure {
   @SubscribeEvent
   public static void onLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
      ModmindsMod.PACKET_HANDLER.sendToServer(new DisparoProcedure.DisparoMessage());
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
                           .getCooldownPercent((entity instanceof LivingEntity _livEntx ? _livEntx.getMainHandItem() : ItemStack.EMPTY).getItem(), 0.0F)
                        * 100.0F
                     : 0.0F
               )
               > 2.0F
         )
         && (entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == ModmindsModItems.CERBATANA_DE_GOBLIN.get()) {
         if ((entity instanceof LivingEntity _livEntxx ? _livEntxx.getMainHandItem() : ItemStack.EMPTY).getItem() instanceof CerbatanaDeGoblinItem) {
            CompatAccess.itemTag(entity instanceof LivingEntity _livEntxxx ? _livEntxxx.getMainHandItem() : ItemStack.EMPTY)
               .putString("geckoAnim", "animation.cerbatana.shoot");
         }

         if ((entity instanceof LivingEntity _livEntxx ? _livEntxx.getMainHandItem() : ItemStack.EMPTY).getDamageValue() >= 11) {
            ItemStack _ist = entity instanceof LivingEntity _livEntxxx ? _livEntxxx.getMainHandItem() : ItemStack.EMPTY;
            if (ItemCompat.hurt(_ist, -1)) {
               _ist.shrink(1);
               _ist.setDamageValue(0);
            }
         } else if ((entity instanceof LivingEntity _livEntxxx ? _livEntxxx.getMainHandItem() : ItemStack.EMPTY).getDamageValue() < 10) {
            Level projectileLevel = entity.level();
            if (!projectileLevel.isClientSide()) {
               Projectile _entityToSpawn = (new Object() {
                  public Projectile getArrow(Level level, Entity shooter, float damage, int knockback) {
                     AbstractArrow entityToSpawn = new DardoTikiProyectilEntity(ModmindsModEntities.DARDO_TIKI_PROYECTIL.get(), level);
                     entityToSpawn.setOwner(shooter);
                     entityToSpawn.setBaseDamage(damage);
                     ArrowCompat.setKnockback(entityToSpawn, knockback);
                     entityToSpawn.setSilent(true);
                     return entityToSpawn;
                  }
               }).getArrow(projectileLevel, entity, 1.0F, 0);
               _entityToSpawn.setPos(entity.getX(), entity.getEyeY() - 0.1, entity.getZ());
               _entityToSpawn.shoot(entity.getLookAngle().x, entity.getLookAngle().y, entity.getLookAngle().z, 5.0F, 1.0F);
               projectileLevel.addFreshEntity(_entityToSpawn);
            }

            ItemStack _ist = entity instanceof LivingEntity _livEntxxxx ? _livEntxxxx.getMainHandItem() : ItemStack.EMPTY;
            if (ItemCompat.hurt(_ist, 1)) {
               _ist.shrink(1);
               _ist.setDamageValue(0);
            }

            if (entity instanceof Player _player) {
               _player.getCooldowns()
                  .addCooldown((entity instanceof LivingEntity _livEntxxxxx ? _livEntxxxxx.getMainHandItem() : ItemStack.EMPTY).getItem(), 6);
            }
         } else if (entity instanceof Player _player && !_player.level().isClientSide()) {
            net.owleafstudio.modminds.compat.Compat.mensaje(_player, Component.literal("§4Recarga"), true);
         }
      }
   }

   @Mod.EventBusSubscriber(
      bus = Mod.EventBusSubscriber.Bus.MOD
   )
   public static class DisparoMessage {
      public DisparoMessage() {
      }

      public DisparoMessage(FriendlyByteBuf buffer) {
      }

      public static void buffer(DisparoProcedure.DisparoMessage message, FriendlyByteBuf buffer) {
      }

      public static void handler(DisparoProcedure.DisparoMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
         NetworkEvent.Context context = contextSupplier.get();
         context.enqueueWork(() -> {
            if (context.getSender().level().hasChunkAt(context.getSender().blockPosition())) {
               DisparoProcedure.execute(context.getSender().level(), context.getSender());
            }
         });
         context.setPacketHandled(true);
      }

      @SubscribeEvent
      public static void registerMessage(FMLCommonSetupEvent event) {
         ModmindsMod.addNetworkMessage(
            DisparoProcedure.DisparoMessage.class,
            DisparoProcedure.DisparoMessage::buffer,
            DisparoProcedure.DisparoMessage::new,
            DisparoProcedure.DisparoMessage::handler
         );
      }
   }
}
