package net.ruleta.candado;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.ruleta.compat.BlockEvent;
import net.ruleta.compat.ExplosionEvent;
import net.ruleta.compat.PistonEvent;
import net.ruleta.compat.PlayerInteractEvent;
import net.ruleta.compat.SubscribeEvent;

public final class CandadoEventos {
   private CandadoEventos() {
   }

   @SubscribeEvent
   public static void alPulsar(PlayerInteractEvent.RightClickBlock e) {
      Level nivel = e.getLevel();
      if (nivel.isClientSide()) {
         if (e.getHand() == InteractionHand.MAIN_HAND && nivel.getBlockState(e.getPos()).is(Candados.PUERTA.get())) {
            Player yo = e.getEntity();
            boolean algoEnMano = !yo.getMainHandItem().isEmpty() || !yo.getOffhandItem().isEmpty();
            if (!yo.isSecondaryUseActive() || !algoEnMano || Candados.esCandado(yo.getMainHandItem()) || Candados.esCandado(yo.getOffhandItem())) {
               cancelar(e);
            }
         }
      } else if (nivel instanceof ServerLevel sl && e.getEntity() instanceof ServerPlayer j) {
         BlockPos var16 = e.getPos();
         BlockPos k = Candados.clave(nivel, var16);
         if (k != null) {
            boolean principal = e.getHand() == InteractionHand.MAIN_HAND;
            boolean candadoEnMano = Candados.esCandado(j.getMainHandItem()) || Candados.esCandado(j.getOffhandItem());
            DatosCandados.Candado c = DatosCandados.de(sl).obtener(k);
            if (c == null) {
               if (candadoEnMano && principal) {
                  cancelar(e);
                  Candados.pedirCodigo(j, 0, var16);
               }
            } else {
               boolean algoEnMano = !j.getMainHandItem().isEmpty() || !j.getOffhandItem().isEmpty();
               if (!j.isSecondaryUseActive() || !algoEnMano || candadoEnMano) {
                  BlockState estado = nivel.getBlockState(var16);
                  boolean sabe = c.abiertos.contains(j.getUUID());
                  if (Candados.esPuerta(estado)) {
                     cancelar(e);
                     if (principal) {
                        BlockState abajo = nivel.getBlockState(k);
                        boolean abierta = abajo.hasProperty(DoorBlock.OPEN) && (Boolean)abajo.getValue(DoorBlock.OPEN);
                        if (abierta) {
                           Candados.ponerAbierta(j, nivel, k, false);
                        } else if (sabe) {
                           Candados.ponerAbierta(j, nivel, k, true);
                        } else {
                           Candados.pedirCodigo(j, 1, var16);
                        }
                     }
                  } else {
                     if (!sabe) {
                        cancelar(e);
                        if (principal) {
                           Candados.pedirCodigo(j, 1, var16);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static void cancelar(PlayerInteractEvent.RightClickBlock e) {
      e.setCanceled(true);
      e.setCancellationResult(InteractionResult.SUCCESS);
   }

   @SubscribeEvent
   public static void alRomper(BlockEvent.BreakEvent e) {
      if (e.getLevel() instanceof Level nivel && !nivel.isClientSide()) {
         BlockPos pos = e.getPos();
         if (Candados.estaBloqueado(nivel, pos)) {
            DatosCandados.Candado c = Candados.obtener(nivel, pos);
            Player p = e.getPlayer();
            boolean permitido = p.getUUID().equals(c.propietario) || net.ruleta.compat.Compat.permiso(p, 2);
            if (!permitido) {
               e.setCanceled(true);
               net.ruleta.compat.Compat.mensaje(p, Component.literal("Tiene un candado: solo su propietario puede romperlo."), true);
            } else {
               Candados.quitar(nivel, pos);
            }
         }
      }
   }

   @SubscribeEvent
   public static void alExplotar(ExplosionEvent.Detonate e) {
      Level nivel = e.getLevel();
      if (!nivel.isClientSide()) {
         for (BlockPos p : e.getAffectedBlocks()) {
            if (Candados.estaBloqueado(nivel, p)) {
               Candados.quitar(nivel, p);
            }
         }
      }
   }

   @SubscribeEvent
   public static void alMoverPiston(PistonEvent.Pre e) {
      if (e.getLevel() instanceof Level nivel && !nivel.isClientSide()) {
         PistonStructureResolver r = e.getStructureHelper();
         if (r != null && r.resolve()) {
            for (BlockPos p : r.getToPush()) {
               if (Candados.estaBloqueado(nivel, p)) {
                  e.setCanceled(true);
                  return;
               }
            }

            for (BlockPos p : r.getToDestroy()) {
               if (Candados.estaBloqueado(nivel, p)) {
                  e.setCanceled(true);
                  return;
               }
            }
         }
      }
   }

   public static void alRegistrarComandos(CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register(
         (LiteralArgumentBuilder)Commands.literal("ruletafanproject")
            .then(((LiteralArgumentBuilder)Commands.literal("candado").requires(s -> net.ruleta.compat.Compat.permiso(s, 2))).executes(ctx -> {
               ServerPlayer p = ((CommandSourceStack)ctx.getSource()).getPlayerOrException();
               HitResult h = p.pick(8.0, 1.0F, false);
               if (h instanceof BlockHitResult b && h.getType() == Type.BLOCK) {
                  DatosCandados.Candado c = Candados.obtener(p.level(), b.getBlockPos());
                  if (c != null) {
                     ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.literal("Candado de " + c.nombre + ": código " + c.codigo), false);
                     return 1;
                  }
               }

               ((CommandSourceStack)ctx.getSource()).sendFailure(Component.literal("No estás mirando un cofre o una puerta con candado."));
               return 0;
            }))
      );
   }
}
