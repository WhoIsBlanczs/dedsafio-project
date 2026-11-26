package net.ruleta.dedsafio;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents.Before;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ChorusFruitItem;
import net.minecraft.world.item.EnderpearlItem;
import net.minecraft.world.item.FireChargeItem;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.ruleta.Proteccion;
import net.ruleta.compat.Bus;
import net.ruleta.compat.ExplosionEvent;
import net.ruleta.compat.SubscribeEvent;

public final class ProteccionDedsafio {
   private static final ResourceKey<Level> DIMENSION = ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath("ruleta", "dedsafio"));

   private ProteccionDedsafio() {
   }

   private static boolean aqui(Level n) {
      return n != null && n.dimension().equals(DIMENSION);
   }

   private static boolean prohibido(ItemStack s) {
      return s.getItem() instanceof BucketItem
         || s.getItem() instanceof EnderpearlItem
         || s.getItem() instanceof ChorusFruitItem
         || s.getItem() instanceof BlockItem
         || s.getItem() instanceof BoneMealItem
         || s.getItem() instanceof FlintAndSteelItem
         || s.getItem() instanceof FireChargeItem;
   }

   @SubscribeEvent
   public static void alExplotar(ExplosionEvent.Detonate e) {
      if (aqui(e.getLevel())) {
         e.getAffectedBlocks().clear();
      }
   }

   private static boolean bloqueado(Player p, ItemStack s) {
      return prohibido(s) && (!(s.getItem() instanceof BlockItem) || !Proteccion.puede(p));
   }

   public static void registrar() {
      Bus.registrar(ProteccionDedsafio.class);
      PlayerBlockBreakEvents.BEFORE.register((Before)(nivel, jugador, pos, estado, be) -> !aqui(nivel) || Proteccion.puede(jugador));
      AttackBlockCallback.EVENT
         .register(
            (AttackBlockCallback)(jugador, nivel, mano, pos, cara) -> aqui(nivel) && !Proteccion.puede(jugador)
               ? InteractionResult.FAIL
               : InteractionResult.PASS
         );
      UseBlockCallback.EVENT
         .register(
            (UseBlockCallback)(jugador, nivel, mano, impacto) -> aqui(nivel) && bloqueado(jugador, jugador.getItemInHand(mano))
               ? InteractionResult.FAIL
               : InteractionResult.PASS
         );
      UseItemCallback.EVENT.register((UseItemCallback)(jugador, nivel, mano) -> {
         ItemStack s = jugador.getItemInHand(mano);
         return aqui(nivel) && bloqueado(jugador, s) ? InteractionResultHolder.fail(s) : InteractionResultHolder.pass(s);
      });
   }
}
