package net.owleafstudio.modminds.procedures;

import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.client.Minecraft;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.init.ModmindsModItems;
import net.owleafstudio.modminds.network.ModmindsModVariables;

@Mod.EventBusSubscriber
public class AnimacionTotemRaroProcedure {
   @SubscribeEvent
   public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
      if (event.phase == TickEvent.Phase.END) {
         execute(event, event.player.level(), event.player);
      }
   }

   public static void execute(LevelAccessor world, Entity entity) {
      execute(null, world, entity);
   }

   private static void execute(@Nullable Event event, LevelAccessor world, Entity entity) {
      if (entity != null && ModmindsModVariables.getVariables(entity).orElse(new ModmindsModVariables.PlayerVariables()).VariableTotemRaro) {
         if (world.isClientSide()) {
            Minecraft.getInstance().player.displayItemActivation(new ItemStack((ItemLike)ModmindsModItems.TOTEM_ESPACIAL.get()));
         }

         ModmindsMod.queueServerWork(10, () -> {
            boolean _setval = false;
            ModmindsModVariables.getVariables(entity).ifPresent(capability -> {
               capability.VariableTotemRaro = _setval;
               capability.syncPlayerVariables(entity);
            });
         });
      }
   }
}
