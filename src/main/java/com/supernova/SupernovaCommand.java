package com.supernova;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.Vec3;
import net.ruleta.Mirada;
import net.ruleta.Tiempo;

public class SupernovaCommand {
   public static void onRegister(CommandDispatcher<CommandSourceStack> dispatcher) { }

   private static List<LiteralArgumentBuilder<CommandSourceStack>> opciones(SupernovaCommand.Ejecutor ej) {
      List<LiteralArgumentBuilder<CommandSourceStack>> out = new ArrayList<>();

      for (String lit : new String[]{"tamaño", "tamano"}) {
         out.add(
            (LiteralArgumentBuilder<CommandSourceStack>)Commands.literal(lit)
               .then(
                  ((RequiredArgumentBuilder)Commands.argument("tam", FloatArgumentType.floatArg())
                        .executes(c -> ej.run(c, FloatArgumentType.getFloat(c, "tam"), null)))
                     .then(
                        Commands.literal("tiempo")
                           .then(
                              Commands.argument("t", StringArgumentType.greedyString())
                                 .executes(c -> ej.run(c, FloatArgumentType.getFloat(c, "tam"), StringArgumentType.getString(c, "t")))
                           )
                     )
               )
         );
      }

      out.add(
         (LiteralArgumentBuilder<CommandSourceStack>)Commands.literal("tiempo")
            .then(Commands.argument("t", StringArgumentType.greedyString()).executes(c -> ej.run(c, null, StringArgumentType.getString(c, "t"))))
      );
      return out;
   }

   private static SupernovaCommand.Valores leer(Float tamArg, String tiempoTxt) {
      SupernovaCommand.Valores v = new SupernovaCommand.Valores();
      v.tam = tamArg;
      if (tiempoTxt != null) {
         String[] p = tiempoTxt.trim().split("\\s+");
         int seg = Tiempo.leer(p[0]);
         if (seg < 0 || seg > 86400) {
            v.error = "Tiempo no válido: " + p[0] + ". Usa 1 (minuto), 30s, 1:30 (mm:ss) o 1:00:00 (hh:mm:ss), hasta 24 h.";
            return v;
         }

         v.ticks = seg * 20;
         if (p.length == 3 && (p[1].equalsIgnoreCase("tamaño") || p[1].equalsIgnoreCase("tamano"))) {
            if (tamArg != null) {
               v.error = "El tamaño está repetido.";
               return v;
            }

            try {
               v.tam = Float.parseFloat(p[2].replace(',', '.'));
            } catch (NumberFormatException ex) {
               v.error = "Tamaño no válido: " + p[2] + ".";
               return v;
            }
         } else if (p.length != 1) {
            v.error = "No entiendo \"" + tiempoTxt.trim() + "\". Ejemplo: tamaño 15 tiempo 1:30";
            return v;
         }
      }

      if (v.tam != null && (v.tam.isNaN() || v.tam < 1.0F || v.tam > 2000.0F)) {
         v.error = "El tamaño tiene que estar entre " + fmtNum(1.0F) + " y " + fmtNum(2000.0F) + " (el máximo es " + fmtNum(2000.0F) + ").";
      }

      return v;
   }

   private static String fmtNum(float f) {
      return f == Math.rint(f) ? Long.toString((long)f) : String.format(Locale.ROOT, "%.1f", f);
   }

   private static String fmtTiempo(int segundos) {
      if (segundos <= 0) {
         return "nada extra";
      }

      int h = segundos / 3600;
      int m = segundos % 3600 / 60;
      int s = segundos % 60;
      return h > 0 ? String.format(Locale.ROOT, "%d:%02d:%02d", h, m, s) : String.format(Locale.ROOT, "%d:%02d", m, s);
   }

   private static int crear(CommandContext<CommandSourceStack> ctx, Vec3 pos, Float tam, String tiempoTxt) {
      SupernovaCommand.Valores v = leer(tam, tiempoTxt);
      if (v.error != null) {
         ((CommandSourceStack)ctx.getSource()).sendFailure(Component.literal(v.error));
         return 0;
      }

      ServerLevel level = ((CommandSourceStack)ctx.getSource()).getLevel();
      SupernovaEntity n = (SupernovaEntity)SupernovaMod.SUPERNOVA.get().create(level, net.minecraft.world.entity.EntitySpawnReason.EVENT);
      if (n == null) {
         ((CommandSourceStack)ctx.getSource()).sendFailure(Component.literal("No se pudo crear la estrella."));
         return 0;
      }

      n.snapTo(pos.x, pos.y, pos.z, 0.0F, 0.0F);
      if (v.tam != null) {
         n.setMaxRadius(v.tam);
      }

      if (v.ticks != null) {
         n.setHoldTicks(v.ticks);
      }

      level.addFreshEntity(n);
      float radio = n.getMaxRadius();
      int seg = n.getHoldTicks() / 20;
      ((CommandSourceStack)ctx.getSource())
         .sendSuccess(
            () -> Component.literal(
               String.format(
                  Locale.ROOT,
                  "Estrella creada en %.1f %.1f %.1f (tamaño %s, tiempo tras explotar: %s). /supernova explotar para detonarla.",
                  pos.x,
                  pos.y,
                  pos.z,
                  fmtNum(radio),
                  fmtTiempo(seg)
               )
            ),
            true
         );
      return 1;
   }

   private static int ajustar(CommandContext<CommandSourceStack> ctx, Float tam, String tiempoTxt) { return 0; }

   private interface Ejecutor {
      int run(CommandContext<CommandSourceStack> var1, Float var2, String var3);
   }

   private static final class Valores {
      Float tam;
      Integer ticks;
      String error;
   }
}
