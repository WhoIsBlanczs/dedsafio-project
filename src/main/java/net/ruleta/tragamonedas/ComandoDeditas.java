package net.ruleta.tragamonedas;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import java.util.Locale;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.ruleta.cajero.CajeroServidor;
import net.ruleta.cajero.SaldoDatos;

public final class ComandoDeditas {
   public static final int MAXIMO = 1000000;
   private static final List<String> COLORES = List.of("rojo", "azul", "verde");

   private ComandoDeditas() {
   }

   public static void registrar() {
      CommandRegistrationCallback.EVENT.register((CommandRegistrationCallback)(dispatcher, registryAccess, entorno) -> alRegistrar(dispatcher));
   }

   private static long valor(String color) {
      return switch (color) {
         case "rojo", "roja" -> CajeroServidor.VALOR[0];
         case "verde" -> CajeroServidor.VALOR[1];
         case "azul" -> CajeroServidor.VALOR[2];
         default -> 0L;
      };
   }

   public static void alRegistrar(CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register(
         (LiteralArgumentBuilder)Commands.literal("ruleta")
            .then(
               ((LiteralArgumentBuilder)Commands.literal("deditas").requires(s -> s.permissions().hasPermission(net.minecraft.server.permissions.Permissions.COMMANDS_GAMEMASTER)))
                  .then(
                     Commands.argument("jugador", EntityArgument.player())
                        .then(
                           Commands.argument("color", StringArgumentType.word())
                              .suggests((ctx, b) -> SharedSuggestionProvider.suggest(COLORES, b))
                              .then(Commands.argument("cantidad", IntegerArgumentType.integer(1, 1000000)).executes(ComandoDeditas::dar))
                        )
                  )
            )
      );
   }

   private static int dar(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      ServerPlayer jugador = EntityArgument.getPlayer(ctx, "jugador");
      String pedido = StringArgumentType.getString(ctx, "color").toLowerCase(Locale.ROOT);
      int cantidad = IntegerArgumentType.getInteger(ctx, "cantidad");
      long unidad = valor(pedido);
      if (unidad == 0L) {
         ((CommandSourceStack)ctx.getSource()).sendFailure(Component.literal("Color desconocido: " + pedido + ". Usa: rojo, azul o verde."));
         return 0;
      } else {
         SaldoDatos datos = SaldoDatos.get(jugador.server);
         long saldo = datos.saldo(jugador.getUUID());
         long nuevo = saldo + cantidad * unidad;
         if (nuevo > 9000000000000L) {
            ((CommandSourceStack)ctx.getSource())
               .sendFailure(Component.literal("El saldo de " + jugador.getGameProfile().name() + " llegaría al tope permitido."));
            return 0;
         } else {
            datos.poner(jugador.getUUID(), nuevo);
            CajeroServidor.enviarSaldo(jugador);
            ((CommandSourceStack)ctx.getSource())
               .sendSuccess(
                  () -> Component.literal(jugador.getGameProfile().getName() + " recibió " + cantidad + " dedita(s) " + pedido + " en su saldo"), true
               );
            return cantidad;
         }
      }
   }
}
