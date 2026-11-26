package net.ruleta.candado;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.ruleta.Red;
import net.ruleta.Reg;
import net.ruleta.Vanilla;

public final class Candados {
   public static final Reg<Block> PUERTA = Reg.block("ruleta", "puerta_candado", () -> new PuertaCandadoBloque(Properties.ofFullCopy(Blocks.IRON_DOOR)));
   public static final Reg<Item> CHIP_PHORA = Reg.item("ruleta", "chip_phora", () -> new Item(new net.minecraft.world.item.Item.Properties()));
   public static final Reg<Item> CANDADO = Reg.item("ruleta", "candado", () -> new Item(new net.minecraft.world.item.Item.Properties().rarity(Rarity.UNCOMMON)));
   public static final int MODO_FIJAR = 0;
   public static final int MODO_ABRIR = 1;
   public static final int MAX_DIGITOS = 8;
   private static final long ESPERA_FALLO = 20L;
   private static final Map<UUID, Long> ULTIMO_FALLO = new HashMap<>();

   private Candados() {
   }

   public static void init() {
   }

   public static BlockPos clave(Level nivel, BlockPos pos) {
      BlockState e = nivel.getBlockState(pos);
      if (esCofre(e) && e.hasProperty(ChestBlock.TYPE) && e.getValue(ChestBlock.TYPE) != ChestType.SINGLE) {
         BlockPos otra = pos.relative(ChestBlock.getConnectedDirection(e));
         return otra.asLong() < pos.asLong() ? otra : pos;
      } else if (esPuerta(e) && e.hasProperty(DoorBlock.HALF)) {
         return e.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos;
      } else {
         return null;
      }
   }

   public static DatosCandados.Candado obtener(Level nivel, BlockPos pos) {
      if (nivel instanceof ServerLevel sl) {
         BlockPos k = clave(nivel, pos);
         if (k == null) {
            DatosCandados.de(sl).quitar(pos);
            return null;
         } else {
            return DatosCandados.de(sl).obtener(k);
         }
      } else {
         return null;
      }
   }

   public static boolean estaBloqueado(Level nivel, BlockPos pos) {
      if (nivel != null && !nivel.isClientSide) {
         BlockState e = nivel.getBlockState(pos);
         return !esCofre(e) && !esPuerta(e) ? false : obtener(nivel, pos) != null;
      } else {
         return false;
      }
   }

   public static boolean esCofre(BlockState e) {
      return e.getBlock() instanceof ChestBlock && Vanilla.es(e.getBlock());
   }

   public static boolean esPuerta(BlockState e) {
      return e.is(Blocks.IRON_DOOR) || e.is(PUERTA.get());
   }

   public static boolean esCandado(ItemStack s) {
      return !s.isEmpty() && s.is(CANDADO.get());
   }

   public static void quitar(Level nivel, BlockPos pos) {
      if (nivel instanceof ServerLevel sl) {
         BlockPos k = clave(nivel, pos);
         DatosCandados d = DatosCandados.de(sl);
         d.quitar(pos);
         if (k != null) {
            d.quitar(k);
         }

         BlockState e = nivel.getBlockState(pos);
         if (esCofre(e) && e.hasProperty(ChestBlock.TYPE) && e.getValue(ChestBlock.TYPE) != ChestType.SINGLE) {
            BlockPos otra = pos.relative(ChestBlock.getConnectedDirection(e));
            BlockState eo = nivel.getBlockState(otra);
            if (esCofre(eo) && eo.hasProperty(Propiedades.LOCKED)) {
               nivel.setBlock(otra, (BlockState)eo.setValue(Propiedades.LOCKED, false), 18);
            }
         }
      }
   }

   private static void marcarConCandado(ServerLevel nivel, BlockPos pos, BlockPos k) {
      BlockState e = nivel.getBlockState(pos);
      if (esCofre(e) && e.hasProperty(ChestBlock.TYPE) && e.getValue(ChestBlock.TYPE) != ChestType.SINGLE) {
         BlockPos otra = pos.relative(ChestBlock.getConnectedDirection(e));

         for (BlockPos p : new BlockPos[]{pos, otra}) {
            BlockState s = nivel.getBlockState(p);
            if (esCofre(s) && s.hasProperty(Propiedades.LOCKED)) {
               nivel.setBlock(p, (BlockState)s.setValue(Propiedades.LOCKED, true), 18);
            }
         }
      } else if (esPuerta(e)) {
         for (BlockPos p : new BlockPos[]{k, k.above()}) {
            BlockState s = nivel.getBlockState(p);
            if (esPuerta(s) && !s.is(PUERTA.get())) {
               nivel.setBlock(p, copiarPropiedades(PUERTA.get().defaultBlockState(), s), 18);
            }
         }
      }
   }

   private static BlockState copiarPropiedades(BlockState destino, BlockState origen) {
      for (Property<?> p : origen.getProperties()) {
         destino = copiar(destino, origen, p);
      }

      return destino;
   }

   private static <T extends Comparable<T>> BlockState copiar(BlockState destino, BlockState origen, Property<T> p) {
      return destino.hasProperty(p) ? (BlockState)destino.setValue(p, origen.getValue(p)) : destino;
   }

   public static void pedirCodigo(ServerPlayer jugador, int modo, BlockPos pos) {
      Red.enviar(jugador, new CodigoAbrirPayload(modo, pos));
   }

   public static void recibirCodigo(ServerPlayer jugador, CodigoEnviarPayload m) {
      if (jugador != null && jugador.level() instanceof ServerLevel nivel) {
         BlockPos var11 = m.pos();
         if (!(jugador.distanceToSqr(var11.getX() + 0.5, var11.getY() + 0.5, var11.getZ() + 0.5) > 64.0) && nivel.isLoaded(var11)) {
            long ahora = nivel.getGameTime();
            Long fallo = ULTIMO_FALLO.get(jugador.getUUID());
            if (fallo != null && ahora - fallo < 20L) {
               Red.enviar(jugador, new CodigoRespuestaPayload(false));
            } else {
               String codigo = m.codigo();
               if (!codigo.isEmpty() && codigo.length() <= 8 && codigo.chars().allMatch(cx -> cx >= 48 && cx <= 57)) {
                  BlockPos k = clave(nivel, var11);
                  if (k == null) {
                     Red.enviar(jugador, new CodigoRespuestaPayload(false));
                  } else {
                     DatosCandados datos = DatosCandados.de(nivel);
                     if (m.modo() == 0) {
                        ItemStack candado = jugador.getMainHandItem();
                        if (!esCandado(candado)) {
                           candado = jugador.getOffhandItem();
                        }

                        if (!esCandado(candado) || datos.obtener(k) != null) {
                           Red.enviar(jugador, new CodigoRespuestaPayload(false));
                           return;
                        }

                        datos.poner(k, jugador, codigo);
                        marcarConCandado(nivel, var11, k);
                        if (!jugador.getAbilities().instabuild) {
                           candado.shrink(1);
                        }

                        nivel.playSound(null, var11, SoundEvents.ANVIL_PLACE, SoundSource.BLOCKS, 0.5F, 1.4F);
                        Red.enviar(jugador, new CodigoRespuestaPayload(true));
                     } else {
                        DatosCandados.Candado c = datos.obtener(k);
                        if (c == null) {
                           Red.enviar(jugador, new CodigoRespuestaPayload(false));
                           return;
                        }

                        if (!c.codigo.equals(codigo)) {
                           ULTIMO_FALLO.put(jugador.getUUID(), ahora);
                           Red.enviar(jugador, new CodigoRespuestaPayload(false));
                           return;
                        }

                        c.abiertos.add(jugador.getUUID());
                        datos.setDirty();
                        Red.enviar(jugador, new CodigoRespuestaPayload(true));
                        abrir(jugador, nivel, var11);
                     }
                  }
               } else {
                  Red.enviar(jugador, new CodigoRespuestaPayload(false));
               }
            }
         }
      }
   }

   public static void abrir(ServerPlayer jugador, ServerLevel nivel, BlockPos pos) {
      BlockState e = nivel.getBlockState(pos);
      if (esCofre(e)) {
         MenuProvider menu = e.getMenuProvider(nivel, pos);
         if (menu != null) {
            jugador.openMenu(menu);
         }
      } else if (esPuerta(e)) {
         ponerAbierta(jugador, nivel, pos, true);
      }
   }

   public static void ponerAbierta(Entity quien, Level nivel, BlockPos pos, boolean abierta) {
      BlockPos k = clave(nivel, pos);
      if (k != null) {
         BlockState e = nivel.getBlockState(k);
         if (esPuerta(e) && e.hasProperty(DoorBlock.OPEN) && (Boolean)e.getValue(DoorBlock.OPEN) != abierta) {
            nivel.setBlock(k, (BlockState)e.setValue(DoorBlock.OPEN, abierta), 3);
            BlockPos arriba = k.above();
            BlockState ea = nivel.getBlockState(arriba);
            if (ea.getBlock() == e.getBlock() && ea.hasProperty(DoorBlock.OPEN)) {
               nivel.setBlock(arriba, (BlockState)ea.setValue(DoorBlock.OPEN, abierta), 3);
            }

            nivel.playSound(
               null,
               k,
               abierta ? SoundEvents.IRON_DOOR_OPEN : SoundEvents.IRON_DOOR_CLOSE,
               SoundSource.BLOCKS,
               1.0F,
               nivel.getRandom().nextFloat() * 0.1F + 0.9F
            );
            nivel.gameEvent(quien, abierta ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, k);
         }
      }
   }
}
