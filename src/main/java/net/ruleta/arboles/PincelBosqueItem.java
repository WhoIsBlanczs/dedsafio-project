package net.ruleta.arboles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class PincelBosqueItem extends Item {
   private static final PincelBosqueItem.Modo[] MODOS = new PincelBosqueItem.Modo[]{
      new PincelBosqueItem.Modo("mixto", 6, 0),
      new PincelBosqueItem.Modo("mixto", 12, 0),
      new PincelBosqueItem.Modo("mixto", 24, 0),
      new PincelBosqueItem.Modo("solo árboles de costa", 12, 1),
      new PincelBosqueItem.Modo("solo palmeras", 12, 2)
   };
   private static final Map<UUID, Integer> elegido = new HashMap<>();

   public PincelBosqueItem(Properties p) {
      super(p);
   }

   public Component getName(ItemStack pila) {
      return Component.literal("Pincel de bosque");
   }

   private static PincelBosqueItem.Modo modo(Player p) {
      return MODOS[elegido.getOrDefault(p.getUUID(), 1)];
   }

   private static void cambiar(Player p) {
      int i = (elegido.getOrDefault(p.getUUID(), 1) + 1) % MODOS.length;
      elegido.put(p.getUUID(), i);
      PincelBosqueItem.Modo m = MODOS[i];
      p.sendOverlayMessage(Component.literal("Pincel de bosque: " + m.nombre() + ", radio " + m.radio() + " (agáchate + clic para cambiar)"));
   }

   public InteractionResult use(Level nivel, Player jugador, InteractionHand mano) {
      ItemStack pila = jugador.getItemInHand(mano);
      if (jugador.isShiftKeyDown()) {
         if (!nivel.isClientSide()) {
            cambiar(jugador);
         }

         return InteractionResult.SUCCESS;
      } else {
         return InteractionResult.PASS;
      }
   }

   public InteractionResult useOn(UseOnContext ctx) {
      Player jugador = ctx.getPlayer();
      if (jugador == null) {
         return InteractionResult.PASS;
      }

      if (jugador.isShiftKeyDown()) {
         if (!ctx.getLevel().isClientSide()) {
            cambiar(jugador);
         }

         return InteractionResult.SUCCESS;
      } else if (!(ctx.getLevel() instanceof ServerLevel nivel)) {
         return InteractionResult.SUCCESS;
      } else if (jugador.isCreative() || jugador instanceof ServerPlayer sp && sp.permissions().hasPermission(net.minecraft.server.permissions.Permissions.COMMANDS_GAMEMASTER)) {
         int n = plantar(nivel, ctx.getClickedPos(), modo(jugador));
         jugador.getCooldowns().addCooldown(net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(this), 10);
         jugador.sendOverlayMessage(Component.literal("Árboles plantados: " + n));
         return InteractionResult.CONSUME;
      } else {
         jugador.sendOverlayMessage(Component.literal("El pincel de bosque es solo para creativo u OP."));
         return InteractionResult.FAIL;
      }
   }

   private static boolean suelo(BlockState s) {
      return s.is(BlockTags.SAND) || s.is(BlockTags.DIRT) || s.is(Blocks.GRAVEL);
   }

   private static boolean libre(BlockState s) {
      return s.isAir() || s.canBeReplaced() && s.getFluidState().isEmpty();
   }

   private static int plantar(ServerLevel nivel, BlockPos centro, PincelBosqueItem.Modo modo) {
      RandomSource r = nivel.getRandom();
      int radio = modo.radio();
      int objetivo = (int)(Math.PI * radio * radio / 14.0);
      int intentos = objetivo * 8;
      List<int[]> puestos = new ArrayList<>();
      int hechos = 0;

      for (int i = 0; i < intentos && hechos < objetivo; i++) {
         double a = r.nextDouble() * Math.PI * 2.0;
         double d = Math.sqrt(r.nextDouble()) * radio;
         int x = centro.getX() + (int)Math.round(Math.cos(a) * d);
         int z = centro.getZ() + (int)Math.round(Math.sin(a) * d);
         boolean cerca = false;

         for (int[] q : puestos) {
            int ddx = q[0] - x;
            int ddz = q[1] - z;
            if (ddx * ddx + ddz * ddz < 13) {
               cerca = true;
               break;
            }
         }

         if (!cerca && nivel.hasChunkAt(new BlockPos(x, centro.getY(), z))) {
            BlockPos base = null;

            for (int y = centro.getY() + 12; y >= centro.getY() - 12; y--) {
               BlockPos p = new BlockPos(x, y, z);
               if (suelo(nivel.getBlockState(p)) && libre(nivel.getBlockState(p.above())) && libre(nivel.getBlockState(p.above(2)))) {
                  base = p.above();
                  break;
               }
            }

            if (base != null) {
               boolean arena = nivel.getBlockState(base.below()).is(BlockTags.SAND);
               int tipo;
               if (modo.mezcla() == 1) {
                  tipo = 0;
               } else if (modo.mezcla() == 2) {
                  tipo = 1;
               } else {
                  tipo = (arena ? r.nextInt(100) >= 65 : r.nextInt(100) < 65) ? 0 : 1;
               }

               GeneradorArboles.crecer(nivel, base, r, tipo);
               puestos.add(new int[]{x, z});
               hechos++;
            }
         }
      }

      return hechos;
   }

   private record Modo(String nombre, int radio, int mezcla) {
   }
}
