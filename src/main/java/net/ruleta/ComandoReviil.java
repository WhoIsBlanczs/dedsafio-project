package net.ruleta;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;

public final class ComandoReviil {
   private static final String[] PIEZAS = new String[]{"cielo", "grieta", "musica", "tinte", "temblor", "cabezazo", "parar"};

   private ComandoReviil() {
   }

   public static void alRegistrar(CommandDispatcher<CommandSourceStack> dispatcher) {
      LiteralCommandNode<CommandSourceStack> raiz = dispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("reviil").requires(s -> s.hasPermission(2)))
            .executes(
               ctx -> {
                  ((CommandSourceStack)ctx.getSource())
                     .sendSuccess(
                        () -> Component.literal(
                           "/reviil [cielo] [grieta] [musica] [mensaje \"texto\"] [tinte] [temblor] [cabezazo] [parar] (combínalas como quieras)"
                        ),
                        false
                     );
                  return 1;
               }
            )
      );
      String[] todas = new String[PIEZAS.length + 1];
      System.arraycopy(PIEZAS, 0, todas, 0, PIEZAS.length);
      todas[PIEZAS.length] = "mensaje";
      int n = todas.length;
      LiteralCommandNode<CommandSourceStack>[] nodos = new LiteralCommandNode[1 << n];

      for (int usadas = (1 << n) - 1; usadas >= 1; usadas--) {
         LiteralCommandNode<CommandSourceStack> nodo = Commands.literal("_" + usadas).build();

         for (int k = 0; k < n; k++) {
            if ((usadas & 1 << k) == 0) {
               nodo.addChild(pieza(todas[k], nodos[usadas | 1 << k]));
            }
         }

         nodos[usadas] = nodo;
      }

      for (int k = 0; k < n; k++) {
         raiz.addChild(pieza(todas[k], nodos[1 << k]));
      }
   }

   private static CommandNode<CommandSourceStack> pieza(String nombre, LiteralCommandNode<CommandSourceStack> siguiente) {
      return nombre.equals("mensaje")
         ? ((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("mensaje").requires(s -> s.hasPermission(2)))
               .then(((RequiredArgumentBuilder)Commands.argument("texto", StringArgumentType.string()).executes(ComandoReviil::ejecutar)).redirect(siguiente)))
            .build()
         : ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal(nombre).requires(s -> s.hasPermission(2)))
                  .executes(ComandoReviil::ejecutar))
               .redirect(siguiente))
            .build();
   }

   private static List<String> palabras(String entrada) {
      List<String> r = new ArrayList<>();
      int i = 0;
      int n = entrada.length();

      while (i < n) {
         char c = entrada.charAt(i);
         if (Character.isWhitespace(c)) {
            i++;
         } else if (c != '"' && c != '\'') {
            int j = i;

            while (j < n && !Character.isWhitespace(entrada.charAt(j))) {
               j++;
            }

            r.add(entrada.substring(i, j));
            i = j;
         } else {
            StringBuilder b = new StringBuilder();
            i++;

            while (i < n && entrada.charAt(i) != c) {
               if (entrada.charAt(i) == '\\' && i + 1 < n) {
                  i++;
               }

               b.append(entrada.charAt(i));
               i++;
            }

            i++;
            r.add("\u0000" + b);
         }
      }

      return r;
   }

   private static int ejecutar(CommandContext<CommandSourceStack> ctx) {
      MinecraftServer server = ((CommandSourceStack)ctx.getSource()).getServer();
      Set<String> piezas = new LinkedHashSet<>();
      String texto = null;
      List<String> p = palabras(ctx.getInput());

      for (int i = 1; i < p.size(); i++) {
         String w = p.get(i);
         if (w.equals("mensaje")) {
            piezas.add("mensaje");
            if (i + 1 < p.size() && p.get(i + 1).startsWith("\u0000")) {
               texto = p.get(i + 1).substring(1).trim();
               i++;
            }
         } else if (!w.startsWith("\u0000")) {
            piezas.add(w.toLowerCase());
         }
      }

      if (piezas.contains("parar")) {
         MomentoReviil.terminar(server);
         Red.enviarATodos(server, new ReviilPayload("parar", 0, Component.empty(), Component.empty()));
         Mensajes.quitarEfectosReviil(server);
         ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.literal("Momento Reviil detenido: cielo, grieta, música, tinte y temblor."), true);
         return 1;
      }

      if (!piezas.contains("mensaje") || texto != null && !texto.isEmpty()) {
         List<String> hecho = new ArrayList<>();
         if (piezas.contains("cielo")) {
            if (MomentoReviil.piezaCielo(server)) {
               hecho.add("cielo");
            } else {
               ((CommandSourceStack)ctx.getSource()).sendFailure(Component.literal("El cielo de Reviil ya está puesto (/reviil parar para quitarlo)."));
            }
         }

         if (piezas.contains("grieta")) {
            if (MomentoReviil.piezaGrieta(server)) {
               hecho.add("grieta");
            } else {
               ((CommandSourceStack)ctx.getSource()).sendFailure(Component.literal("La grieta de Reviil ya está abierta (/reviil parar para quitarla)."));
            }
         }

         boolean musica = piezas.contains("musica");
         if (musica) {
            Red.enviarATodos(server, new ReviilPayload("tema_momento", 1, Component.empty(), Component.empty()));
            hecho.add("musica");
         }

         if (piezas.contains("mensaje")) {
            Mensajes.anunciarReviil(server, texto);
            hecho.add("mensaje");
         }

         boolean tinte = piezas.contains("tinte");
         boolean temblor = piezas.contains("temblor");
         if (tinte || temblor) {
            Mensajes.efectosReviil(server, tinte, temblor);
            if (tinte) {
               hecho.add("tinte");
            }

            if (temblor) {
               hecho.add("temblor");
            }
         }

         if (piezas.contains("cabezazo")) {
            if (MomentoReviil.grietaAbierta()) {
               Red.enviarATodos(server, new ReviilPayload("cabezazo", 1, Component.empty(), Component.empty()));
               hecho.add("cabezazo");
            } else {
               ((CommandSourceStack)ctx.getSource()).sendFailure(Component.literal("El cabezazo necesita la grieta abierta: ponla antes con /reviil grieta."));
            }
         }

         if (hecho.isEmpty()) {
            return 0;
         }

         String lista = String.join(", ", hecho);
         ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.literal("Reviil: " + lista + " (/reviil parar para acabarlo)."), true);
         return 1;
      } else {
         ((CommandSourceStack)ctx.getSource()).sendFailure(Component.literal("Escribe el mensaje entre comillas: /reviil mensaje \"texto\""));
         return 0;
      }
   }
}
