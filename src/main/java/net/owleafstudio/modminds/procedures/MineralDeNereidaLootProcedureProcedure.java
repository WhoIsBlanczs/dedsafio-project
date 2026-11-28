package net.owleafstudio.modminds.procedures;

import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.init.ModmindsModBlocks;
import net.owleafstudio.modminds.init.ModmindsModItems;

@Mod.EventBusSubscriber
public class MineralDeNereidaLootProcedureProcedure {
   @SubscribeEvent
   public static void onBlockBreak(BlockEvent.BreakEvent event) {
      execute(event, event.getLevel(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), event.getPlayer());
   }

   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      execute(null, world, x, y, z, entity);
   }

   private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null
         && (new Object() {
               public boolean checkGamemode(Entity _ent) {
                  if (_ent instanceof ServerPlayer _serverPlayer) {
                     return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.SURVIVAL;
                  } else {
                     return _ent.level().isClientSide() && _ent instanceof Player _player
                        ? Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().id()) != null
                           && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().id()).getGameMode() == GameType.SURVIVAL
                        : false;
                  }
               }
            })
            .checkGamemode(entity)
         && world.getBlockState(BlockPos.containing(x, y, z)).getBlock() == ModmindsModBlocks.MINERAL_DE_NEREIDA.get()) {
         if (EnchantmentHelper.getItemEnchantmentLevel(
                  CompatAccess.enchantment(Enchantments.FORTUNE), entity instanceof LivingEntity _livEntx ? _livEntx.getMainHandItem() : ItemStack.EMPTY
               )
               == 0
            && EnchantmentHelper.getItemEnchantmentLevel(
                  CompatAccess.enchantment(Enchantments.SILK_TOUCH), entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY
               )
               == 0) {
            if (world instanceof ServerLevel _level) {
               ItemEntity entityToSpawn = new ItemEntity(
                  _level, x + 0.5, y + 0.5, z + 0.5, new ItemStack((ItemLike)ModmindsModItems.PEPITA_DE_GEMA_DE_NEREIDA.get())
               );
               entityToSpawn.setPickUpDelay(10);
               _level.addFreshEntity(entityToSpawn);
            }
         } else if (CompatAccess.enchantmentLevel(
               entity instanceof LivingEntity _livEntxx ? _livEntxx.getMainHandItem() : ItemStack.EMPTY, Enchantments.FORTUNE
            )
            == 1) {
            for (int index0 = 0; index0 < Mth.nextInt(RandomSource.create(), 1, 2); index0++) {
               if (world instanceof ServerLevel _level) {
                  ItemEntity entityToSpawn = new ItemEntity(
                     _level, x + 0.5, y + 0.5, z + 0.5, new ItemStack((ItemLike)ModmindsModItems.PEPITA_DE_GEMA_DE_NEREIDA.get())
                  );
                  entityToSpawn.setPickUpDelay(10);
                  _level.addFreshEntity(entityToSpawn);
               }
            }
         } else if (CompatAccess.enchantmentLevel(
               entity instanceof LivingEntity _livEntxx ? _livEntxx.getMainHandItem() : ItemStack.EMPTY, Enchantments.FORTUNE
            )
            == 2) {
            for (int index1 = 0; index1 < Mth.nextInt(RandomSource.create(), 2, 3); index1++) {
               if (world instanceof ServerLevel _level) {
                  ItemEntity entityToSpawn = new ItemEntity(
                     _level, x + 0.5, y + 0.5, z + 0.5, new ItemStack((ItemLike)ModmindsModItems.PEPITA_DE_GEMA_DE_NEREIDA.get())
                  );
                  entityToSpawn.setPickUpDelay(10);
                  _level.addFreshEntity(entityToSpawn);
               }
            }
         } else if (CompatAccess.enchantmentLevel(
               entity instanceof LivingEntity _livEntxx ? _livEntxx.getMainHandItem() : ItemStack.EMPTY, Enchantments.FORTUNE
            )
            == 3) {
            for (int index2 = 0; index2 < Mth.nextInt(RandomSource.create(), 3, 4); index2++) {
               if (world instanceof ServerLevel _level) {
                  ItemEntity entityToSpawn = new ItemEntity(
                     _level, x + 0.5, y + 0.5, z + 0.5, new ItemStack((ItemLike)ModmindsModItems.PEPITA_DE_GEMA_DE_NEREIDA.get())
                  );
                  entityToSpawn.setPickUpDelay(10);
                  _level.addFreshEntity(entityToSpawn);
               }
            }
         }

         if (EnchantmentHelper.getItemEnchantmentLevel(
                  CompatAccess.enchantment(Enchantments.SILK_TOUCH), entity instanceof LivingEntity _livEntxx ? _livEntxx.getMainHandItem() : ItemStack.EMPTY
               )
               != 0
            && world instanceof ServerLevel _level) {
            ItemEntity entityToSpawn = new ItemEntity(_level, x + 0.5, y + 0.5, z + 0.5, new ItemStack((ItemLike)ModmindsModBlocks.MINERAL_DE_NEREIDA.get()));
            entityToSpawn.setPickUpDelay(10);
            _level.addFreshEntity(entityToSpawn);
         }
      }
   }
}
