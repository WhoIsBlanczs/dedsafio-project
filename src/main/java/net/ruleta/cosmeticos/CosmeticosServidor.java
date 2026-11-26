package net.ruleta.cosmeticos;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.ruleta.Persistente;
import net.ruleta.Red;
import net.ruleta.compat.PlayerEvent;
import net.ruleta.compat.SubscribeEvent;

public final class CosmeticosServidor {
   private static final Map<UUID, EstadoPayload> ULTIMO = new HashMap<>();

   private CosmeticosServidor() {
   }

   private static EstadoPayload estado(ServerPlayer p) {
      return new EstadoPayload(p.getUUID(), Cosmeticos.aura(p));
   }

   public static void alClonar(ServerPlayer original, ServerPlayer renacido, boolean murio) {
      CompoundTag viejo = Persistente.de(original);
      CompoundTag nuevo = Persistente.de(renacido);
      if (viejo.contains("ruleta_aura")) {
         nuevo.putString("ruleta_aura", viejo.getStringOr("ruleta_aura", ""));
      }
   }

   public static void alTick(ServerPlayer p) {
      if (p.tickCount % 10 == 0) {
         Cosmeticos.sincronizarEfectos(p);
         EstadoPayload nuevo = estado(p);
         EstadoPayload viejo = ULTIMO.get(p.getUUID());
         if (viejo == null && nuevo.vacio()) {
            ULTIMO.put(p.getUUID(), nuevo);
         } else {
            if (!nuevo.equals(viejo)) {
               ULTIMO.put(p.getUUID(), nuevo);
               Red.enviarSeguidores(p, nuevo);
            }
         }
      }
   }

   @SubscribeEvent
   public static void alVer(PlayerEvent.StartTracking e) {
      if (e.getTarget() instanceof ServerPlayer objetivo && e.getEntity() instanceof ServerPlayer quien) {
         EstadoPayload s = estado(objetivo);
         if (!s.vacio()) {
            Red.enviar(quien, s);
         }
      }
   }

   public static void alSalir(ServerPlayer jugador) {
      ULTIMO.remove(jugador.getUUID());
   }

   public static void alRegistrarComandos(CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register(
         (LiteralArgumentBuilder)Commands.literal("ruleta")
            .then(
               ((LiteralArgumentBuilder)Commands.literal("aura").requires(s -> s.permissions().hasPermission(net.minecraft.server.permissions.Permissions.COMMANDS_GAMEMASTER)))
                  .then(
                     Commands.argument("jugador", EntityArgument.player())
                        .then(
                           Commands.argument("pais", StringArgumentType.greedyString())
                              .suggests((ctx, b) -> {
                                 List<String> lista = new ArrayList<>();

                                 for (String[] p : Cosmeticos.PAISES) {
                                    lista.add(p[0]);
                                 }

                                 lista.add("quitar");
                                 return SharedSuggestionProvider.suggest(lista, b);
                              })
                              .executes(
                                 ctx -> {
                                    ServerPlayer j = EntityArgument.getPlayer(ctx, "jugador");
                                    String pedido = Cosmeticos.normalizar(StringArgumentType.getString(ctx, "pais"));
                                    if (pedido.equals("quitar")) {
                                       Cosmeticos.ponerAura(j, "");
                                       ((CommandSourceStack)ctx.getSource())
                                          .sendSuccess(() -> Component.literal("Aura quitada a " + j.getGameProfile().getName()), true);
                                       return 1;
                                    }

                                    boolean existe = false;

                                    for (String[] pa : Cosmeticos.PAISES) {
                                       existe |= pa[0].equals(pedido);
                                    }

                                    if (!existe) {
                                       ((CommandSourceStack)ctx.getSource())
                                          .sendFailure(Component.literal("País desconocido: " + pedido + ". Usa un país de Latinoamérica o España."));
                                       return 0;
                                    } else {
                                       Cosmeticos.ponerAura(j, pedido);
                                       ((CommandSourceStack)ctx.getSource())
                                          .sendSuccess(() -> Component.literal("Aura de " + pedido + " para " + j.getGameProfile().name()), true);
                                       return 1;
                                    }
                                 }
                              )
                        )
                  )
            )
      );
   }
}
