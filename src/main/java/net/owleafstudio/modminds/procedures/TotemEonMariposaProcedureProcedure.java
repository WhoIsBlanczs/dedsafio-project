package net.owleafstudio.modminds.procedures;

import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.owleafstudio.modminds.init.ModmindsModItems;
import net.owleafstudio.modminds.network.ModmindsModVariables;

@Mod.EventBusSubscriber
public class TotemEonMariposaProcedureProcedure {
   @SubscribeEvent
   public static void onEntityDeath(LivingDeathEvent event) {
      if (event != null && event.getEntity() != null) {
         execute(
            event,
            event.getEntity().level(),
            event.getEntity().getX(),
            event.getEntity().getY(),
            event.getEntity().getZ(),
            event.getEntity()
         );
      }
   }

   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      execute(null, world, x, y, z, entity);
   }

   private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null
         && (
            (entity instanceof LivingEntity _livEntx ? _livEntx.getMainHandItem() : ItemStack.EMPTY).getItem() == ModmindsModItems.TOTEM_EON_MARIPOSA.get()
               || (entity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY).getItem()
                  == ModmindsModItems.TOTEM_EON_MARIPOSA.get()
         )) {
         boolean _setval = true;
         ModmindsModVariables.getVariables(entity).ifPresent(capability -> {
            capability.VariableTotemEonMariposa = _setval;
            capability.syncPlayerVariables(entity);
         });
         if ((entity instanceof LivingEntity _livEntxx ? _livEntxx.getOffhandItem() : ItemStack.EMPTY).getItem()
            == ModmindsModItems.TOTEM_EON_MARIPOSA.get()) {
            (entity instanceof LivingEntity _livEntxxx ? _livEntxxx.getOffhandItem() : ItemStack.EMPTY).shrink(1);
         } else {
            (entity instanceof LivingEntity _livEntxx ? _livEntxx.getMainHandItem() : ItemStack.EMPTY).shrink(1);
         }

         if (entity instanceof LivingEntity _entity) {
            _entity.setHealth(4.0F);
         }

         if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 600, 1));
         }

         if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 800, 1));
         }

         if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
         }

         if (world instanceof Level _level) {
            if (!_level.isClientSide()) {
               _level.playSound(
                  null,
                  BlockPos.containing(x, y, z),
                  ForgeRegistries.SOUND_EVENTS.getValue(Identifier.parse("item.totem.use")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F
               );
            } else {
               _level.playLocalSound(
                  x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(Identifier.parse("item.totem.use")), SoundSource.NEUTRAL, 1.0F, 1.0F, false
               );
            }
         }

         if (world instanceof ServerLevel _levelx) {
            _levelx.sendParticles(ParticleTypes.TOTEM_OF_UNDYING, x, y, z, 128, 1.0, 2.0, 1.0, 0.0);
            String playerName = entity.getDisplayName().getString();
            Component message = Component.literal("")
               .append(Component.literal("۞ ").withStyle(ChatFormatting.YELLOW))
               .append(Component.literal(playerName).withStyle(new ChatFormatting[]{ChatFormatting.BOLD, ChatFormatting.YELLOW}))
               .append(Component.literal(" ha consumido un Tótem Eon Mariposa").withStyle(ChatFormatting.YELLOW));
            _levelx.getServer().getPlayerList().broadcastSystemMessage(message, false);
         }

         if (event != null && event.isCancelable()) {
            event.setCanceled(true);
         } else if (event != null && event.hasResult()) {
            event.setResult(Event.Result.DENY);
         }
      }
   }
}
