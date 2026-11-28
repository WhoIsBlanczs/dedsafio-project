package com.hexagonsafio2;

import net.minecraft.world.InteractionResult;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;

public class CorazonItem extends DescItem {
   private static final Identifier MODIFICADOR = NaveCraft.id("corazones_extra");
   private final CorazonItem.Efecto efecto;

   public CorazonItem(Properties properties, CorazonItem.Efecto efecto) {
      super(properties);
      this.efecto = efecto;
   }

   public static void masCorazones(ServerPlayer jugador, int corazones) {
      AttributeInstance vida = jugador.getAttribute(Attributes.MAX_HEALTH);
      if (vida != null) {
         AttributeModifier anterior = vida.getModifier(MODIFICADOR);
         double total = (anterior == null ? 0.0 : anterior.amount()) + corazones * 2.0;
         vida.addOrReplacePermanentModifier(new AttributeModifier(MODIFICADOR, total, Operation.ADD_VALUE));
      }
   }

   public InteractionResult use(Level level, Player player, InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      if (!level.isClientSide() && player instanceof ServerPlayer jugador) {
         this.efecto.aplicar(jugador);
         level.playSound(null, jugador.getX(), jugador.getY(), jugador.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.8F, 1.3F);
         if (!player.getAbilities().instabuild) {
            stack.shrink(1);
         }
      }

      return InteractionResult.SUCCESS;
   }

   public interface Efecto {
      void aplicar(ServerPlayer var1);
   }
}
