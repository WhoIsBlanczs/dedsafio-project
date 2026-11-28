package net.owleafstudio.modminds.procedures;

import java.util.Comparator;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkEvent;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.entity.ReviilEntity;
import net.owleafstudio.modminds.network.ModmindsModVariables;

@Mod.EventBusSubscriber({Dist.CLIENT})
public class RightpunchProcedure {
   @SubscribeEvent
   public static void onLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
      ModmindsMod.PACKET_HANDLER.sendToServer(new RightpunchProcedure.RightpunchMessage());
      execute(event.getLevel(), event.getPos().getY(), event.getEntity());
   }

   @SubscribeEvent
   public static void onRightClick(PlayerInteractEvent.RightClickEmpty event) {
      ModmindsMod.PACKET_HANDLER.sendToServer(new RightpunchProcedure.RightpunchMessage());
      execute(event.getLevel(), event.getPos().getY(), event.getEntity());
   }

   public static void execute(LevelAccessor world, double y, Entity entity) {
      execute(null, world, y, entity);
   }

   private static void execute(@Nullable Event event, LevelAccessor world, double y, Entity entity) {
      if (entity != null) {
         boolean Cooldown = false;
         if (entity.getVehicle() instanceof ReviilEntity mount) {
            if (ModmindsModVariables.getVariables(mount).orElse(new ModmindsModVariables.PlayerVariables()).RevillPunch
               && ModmindsModVariables.MapVariables.get(world).RevilPunchCooldown) {
               mount.setAnimation("emote.attack_right");
               setMountSpeed(mount, 0.0);
               ModmindsModVariables.MapVariables.get(world).RevilPunchCooldown = false;
               ModmindsModVariables.MapVariables.get(world).syncData(world);
               ModmindsMod.queueServerWork(30, () -> {
                  punch(world, y, entity, mount, 20.0F, false);
                  ModmindsMod.queueServerWork(20, () -> restoreAfterPunch(world, mount));
               });
            } else if (!ModmindsModVariables.getVariables(mount).orElse(new ModmindsModVariables.PlayerVariables()).RevillPunch
               && ModmindsModVariables.MapVariables.get(world).RevilPunchCooldown) {
               ModmindsModVariables.MapVariables.get(world).RevilPunchCooldown = false;
               ModmindsModVariables.MapVariables.get(world).syncData(world);
               mount.setAnimation("emote.attack_left");
               setMountSpeed(mount, 0.0);
               ModmindsMod.queueServerWork(30, () -> {
                  punch(world, y, entity, mount, 30.0F, true);
                  ModmindsMod.queueServerWork(20, () -> restoreAfterPunch(world, mount));
               });
            }
         }
      }
   }

   private static void punch(LevelAccessor world, double y, Entity rider, Entity mount, float damage, boolean revillPunch) {
      if (!mount.isRemoved()) {
         Vec3 look = mount.getLookAngle();
         double x = look.x * 12.0 + mount.getX();
         double z = look.z * 12.0 + mount.getZ();
         if (world instanceof Level _level && !_level.isClientSide()) {
            _level.explode(null, x, y, z, 4.0F, Level.ExplosionInteraction.NONE);
         }

         Vec3 _center = new Vec3(x, y, z);

         for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(6.0), e -> true)
            .stream()
            .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
            .toList()) {
            if (entityiterator != rider || !(entityiterator instanceof ReviilEntity)) {
               entityiterator.hurt(
                  new DamageSource(world.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(DamageTypes.GENERIC)), damage
               );
            }
         }

         ModmindsModVariables.getVariables(mount).ifPresent(capability -> {
            capability.RevillPunch = revillPunch;
            capability.syncPlayerVariables(mount);
         });
      }
   }

   private static void restoreAfterPunch(LevelAccessor world, Entity mount) {
      ModmindsModVariables.MapVariables.get(world).RevilPunchCooldown = true;
      ModmindsModVariables.MapVariables.get(world).syncData(world);
      setMountSpeed(mount, 0.2);
   }

   private static void setMountSpeed(Entity mount, double speed) {
      if (mount instanceof LivingEntity _livingEntity && _livingEntity.getAttributes().hasAttribute(Attributes.MOVEMENT_SPEED)) {
         _livingEntity.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(speed);
      }
   }

   @Mod.EventBusSubscriber(
      bus = Mod.EventBusSubscriber.Bus.MOD
   )
   public static class RightpunchMessage {
      public RightpunchMessage() {
      }

      public RightpunchMessage(FriendlyByteBuf buffer) {
      }

      public static void buffer(RightpunchProcedure.RightpunchMessage message, FriendlyByteBuf buffer) {
      }

      public static void handler(RightpunchProcedure.RightpunchMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
         NetworkEvent.Context context = contextSupplier.get();
         context.enqueueWork(() -> {
            if (context.getSender().level().hasChunkAt(context.getSender().blockPosition())) {
               RightpunchProcedure.execute(context.getSender().level(), context.getSender().getY(), context.getSender());
            }
         });
         context.setPacketHandled(true);
      }

      @SubscribeEvent
      public static void registerMessage(FMLCommonSetupEvent event) {
         ModmindsMod.addNetworkMessage(
            RightpunchProcedure.RightpunchMessage.class,
            RightpunchProcedure.RightpunchMessage::buffer,
            RightpunchProcedure.RightpunchMessage::new,
            RightpunchProcedure.RightpunchMessage::handler
         );
      }
   }
}
