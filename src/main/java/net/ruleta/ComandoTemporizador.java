package net.ruleta;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public final class ComandoTemporizador {
   private ComandoTemporizador() {
   }

   public static void alRegistrar(CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("temporizador").requires(s -> s.permissions().hasPermission(net.minecraft.server.permissions.Permissions.COMMANDS_GAMEMASTER)))
               .then(Commands.literal("detener").executes(ctx -> {
                  if (Mensajes.detenerTemporizador(((CommandSourceStack)ctx.getSource()).getServer())) {
                     ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.literal("Temporizador detenido."), true);
                     return 1;
                  } else {
                     ((CommandSourceStack)ctx.getSource()).sendFailure(Component.literal("No hay ningún temporizador en marcha."));
                     return 0;
                  }
               })))
            .then(
               Commands.argument("tiempo", StringArgumentType.greedyString())
                  .executes(
                     ctx -> {
                        String txt = StringArgumentType.getString(ctx, "tiempo");
                        int segundos = Tiempo.leer(txt);
                        if (segundos > 0 && segundos <= 86400) {
                           Mensajes.temporizadorComando(((CommandSourceStack)ctx.getSource()).getServer(), segundos);
                           ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.literal("Temporizador puesto: " + txt + "."), true);
                           return 1;
                        } else {
                           ((CommandSourceStack)ctx.getSource())
                              .sendFailure(Component.literal("Tiempo no válido: " + txt + ". Usa 59:59, hasta 24:00:00 o solo minutos (5)."));
                           return 0;
                        }
                     }
                  )
            )
      );
   }
}
