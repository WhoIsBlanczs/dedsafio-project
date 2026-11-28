package net.owleafstudio.modminds.procedures;

import io.netty.buffer.Unpooled;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraftforge.network.NetworkHooks;
import net.owleafstudio.modminds.entity.ReactorHydraEntity;
import net.owleafstudio.modminds.init.ModmindsModEntities;
import net.owleafstudio.modminds.world.inventory.ReactorHydraPanelMenu;

public class ReactorHydraPanelAbrirProcedure {
   public static Entity execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity == null) {
         return null;
      } else {
         if (entity instanceof ServerPlayer _ent) {
            final BlockPos _bpos = BlockPos.containing(x, y, z);
            NetworkHooks.openScreen(_ent, new MenuProvider() {
               public Component getDisplayName() {
                  return Component.literal("ReactorHydraPanel");
               }

               public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
                  return new ReactorHydraPanelMenu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(_bpos));
               }
            }, _bpos);
         }

         return world instanceof Level _level ? new ReactorHydraEntity(ModmindsModEntities.REACTOR_HYDRA.get(), _level) : null;
      }
   }
}
