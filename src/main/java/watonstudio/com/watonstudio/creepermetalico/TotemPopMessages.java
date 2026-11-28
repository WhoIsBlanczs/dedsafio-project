package com.watonstudio.creepermetalico;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class TotemPopMessages {
   private static final Set<UUID> MUTED_PLAYERS = ConcurrentHashMap.newKeySet();
   private static final int GOLD = 16767829;

   private TotemPopMessages() {
   }

   private static Style estiloDorado() {
      return Style.EMPTY.withColor(16767829).withBold(false).withItalic(false);
   }

   public static void registerCommands() {
      CommandRegistrationCallback.EVENT
         .register(
            (CommandRegistrationCallback)(dispatcher, registryAccess, environment) -> dispatcher.register(
               (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("totem").then(Commands.literal("on").executes(context -> {
                  ServerPlayer player = ((CommandSourceStack)context.getSource()).getPlayerOrException();
                  MUTED_PLAYERS.remove(player.getUUID());
                  player.displayClientMessage(Component.literal("Avisos de tótems activados.").setStyle(estiloDorado()), false);
                  return 1;
               }))).then(Commands.literal("off").executes(context -> {
                  ServerPlayer player = ((CommandSourceStack)context.getSource()).getPlayerOrException();
                  MUTED_PLAYERS.add(player.getUUID());
                  player.displayClientMessage(Component.literal("Avisos de tótems desactivados.").setStyle(estiloDorado()), false);
                  return 1;
               }))
            )
         );
   }

   public static boolean isTotemLike(ItemStack stack) {
      if (stack.isEmpty()) {
         return false;
      }

      if (stack.is(CreeperMetalicoMod.TOTEM_EON)) {
         return false;
      }

      if (stack.is(Items.TOTEM_OF_UNDYING)) {
         return true;
      }

      ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
      String path = id.getPath().toLowerCase(Locale.ROOT);
      String name = stack.getHoverName().getString().toLowerCase(Locale.ROOT);
      return path.contains("totem") || name.contains("totem") || name.contains("tótem");
   }

   public static void announcePop(ServerPlayer player, Component totemName) {
      if (player != null && totemName != null) {
         MinecraftServer server = player.getServer();
         if (server != null) {
            Style gold = estiloDorado();
            Component message = Component.empty()
               .append(Component.literal("⌛ ").setStyle(gold))
               .append(player.getDisplayName().copy().setStyle(gold))
               .append(Component.literal(" consumió ").setStyle(gold))
               .append(totemName.copy().setStyle(gold));

            for (ServerPlayer viewer : server.getPlayerList().getPlayers()) {
               if (!MUTED_PLAYERS.contains(viewer.getUUID())) {
                  viewer.displayClientMessage(message, false);
               }
            }
         }
      }
   }
}
