package com.novastudio.dedsafio4;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

public final class SeedItem extends Item {
   private static final int MAX_FRUITS = 8;
   private final int fruitKind;

   public SeedItem(int var1) {
      super(new Item.Properties());
      this.fruitKind = var1;
   }

   private static Item item(String var0) {
      return (Item)BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath("nexo", var0));
   }

   public InteractionResult useOn(UseOnContext var1) {
      Level var2 = var1.getLevel();
      BlockPos var3 = var1.getClickedPos();
      if (!(var2.getBlockState(var3).getBlock() instanceof DinoTreeTrunkBlock)) {
         return InteractionResult.PASS;
      } else {
         int var4 = plant(var2, var3, this.fruitKind, var2.getRandom(), !var2.isClientSide());
         if (var4 <= 0) {
            if (!var2.isClientSide() && var1.getPlayer() != null) {
               var1.getPlayer()
                  .sendOverlayMessage(
                     Component.literal(var4 < 0 ? "Este tronco ya tiene el máximo de frutas." : "No hay espacio libre junto a este tronco.")
                  );
            }

            return InteractionResult.FAIL;
         } else {
            if (!var2.isClientSide()) {
               var1.getItemInHand().consume(1, var1.getPlayer());
               var2.playSound(null, var3, SoundEvents.CROP_PLANTED, SoundSource.BLOCKS, 1.0F, 1.0F);
            }

            return InteractionResult.SUCCESS;
         }
      }
   }

   static int plant(Level var0, BlockPos var1, int var2, RandomSource var3, boolean var4) {
      List<BlockPos> var5 = DinoTreeGrower.tree(var0, var1);
      if (DinoTreeGrower.fruitCount(var0, var5) >= 8) {
         return -1;
      } else {
         List<BlockPos> var6 = DinoTreeGrower.fruitSpots(var0, DinoTreeGrower.topTrunk(var5));
         int var7 = Math.min(1, var6.size());
         if (var7 > 0 && var4) {
            Collections.shuffle(var6, new Random(var3.nextLong()));
            Block var8 = (Block)BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath("nexo", DinoFruitRender.FRUITS[var2] + "_tree_fruit"));

            for (int var9 = 0; var9 < var7; var9++) {
               var0.setBlock(var6.get(var9), (BlockState)var8.defaultBlockState().setValue(DinoFruitBlock.AGE, 0), 3);
            }

            return var7;
         } else {
            return var7;
         }
      }
   }

   public static void registerEvents() {
      AttackBlockCallback.EVENT
         .register(
            (AttackBlockCallback)(var0, var1, var2, var3, var4) -> {
               ItemStack var5 = var0.getMainHandItem();
               ItemStack var6 = var0.getOffhandItem();
               if (var2 == InteractionHand.MAIN_HAND
                  && !var0.isSpectator()
                  && !var6.isEmpty()
                  && var5.is(net.minecraft.tags.ItemTags.AXES)
                  && var1.getBlockState(var3).getBlock() instanceof DinoTreeTrunkBlock) {
                  for (int var7 = 0; var7 < DinoFruitRender.FRUITS.length; var7++) {
                     if (var6.is(item(DinoFruitRender.FRUITS[var7]))) {
                        if (var0.getCooldowns().isOnCooldown(var6)) {
                           return InteractionResult.FAIL;
                        }

                        if (!var1.isClientSide()) {
                           var0.getCooldowns().addCooldown(var6, 10);
                           Block.popResource(var1, var3.relative(var4), new ItemStack(item("semilla_" + DinoFruitRender.FRUITS[var7])));
                           var6.consume(1, var0);
                           var5.hurtAndBreak(1, var0, EquipmentSlot.MAINHAND);
                           var1.playSound(null, var3, SoundEvents.AXE_STRIP.value(), SoundSource.BLOCKS, 1.0F, 1.0F);
                        }

                        return InteractionResult.SUCCESS;
                     }
                  }

                  return InteractionResult.PASS;
               } else {
                  return InteractionResult.PASS;
               }
            }
         );
   }
}
