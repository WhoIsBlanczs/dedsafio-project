package net.ruleta.puerta;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.ruleta.Ajustes;
import net.ruleta.MomentosBunker;
import net.ruleta.bunker.Bunkers;
import org.jetbrains.annotations.Nullable;

public class PuertaBunkerBloque extends BaseEntityBlock {
   public static final int ANCHO = 8;
   public static final int ALTO = 11;
   public static final EnumProperty<net.minecraft.core.Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
   public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
   public static final IntegerProperty DX = IntegerProperty.create("dx", 0, 7);
   public static final IntegerProperty DY = IntegerProperty.create("dy", 0, 10);
   public static final boolean ABRE_HACIA_ATRAS = true;
   private static final int LARGO_ABIERTA = 3;
   private static final VoxelShape LOSA_Z = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);
   private static final VoxelShape LOSA_X = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);
   private static boolean desmontando = false;
   private final boolean grande;
   public static final MapCodec<PuertaBunkerBloque> CODEC = simpleCodec(props -> new PuertaBunkerBloque(props, false));


   public PuertaBunkerBloque(Properties props, boolean grande) {
      super(props);
      this.grande = grande;
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH))
                  .setValue(OPEN, Boolean.FALSE))
               .setValue(DX, 0))
            .setValue(DY, 0)
      );
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, OPEN, DX, DY});
   }

   public static Direction derecha(Direction frente) {
      return frente.getCounterClockWise();
   }

   public static BlockPos origen(BlockPos pos, BlockState s) {
      return pos.relative(derecha((Direction)s.getValue(FACING)).getOpposite(), (Integer)s.getValue(DX)).below((Integer)s.getValue(DY));
   }

   public static BlockPos celda(BlockPos origen, Direction frente, int dx, int dy) {
      return origen.relative(derecha(frente), dx).above(dy);
   }

   private boolean esDeEstaPuerta(BlockState s, Direction frente, int dx, int dy) {
      return s.is(this) && s.getValue(FACING) == frente && (Integer)s.getValue(DX) == dx && (Integer)s.getValue(DY) == dy;
   }

   private static boolean esHueco(BlockState s) {
      int dx = (Integer)s.getValue(DX);
      return (Boolean)s.getValue(OPEN) && dx >= 1 && dx <= 6;
   }

   public static Direction haciaAbierta(Direction frente) {
      return frente.getOpposite();
   }

   private boolean colocarHojas(Level level, BlockPos o, Direction frente) {
      Direction hacia = haciaAbierta(frente);
      BlockState hoja = PuertaBunker.BLOQUE_HOJA.get().defaultBlockState();
      boolean pendiente = false;

      for (int dx = 0; dx < 8; dx += 7) {
         for (int k = 1; k <= 3; k++) {
            for (int dy = 0; dy < 11; dy++) {
               BlockPos p = celda(o, frente, dx, dy).relative(hacia, k);
               if (level.isLoaded(p) && level.getBlockState(p).canBeReplaced()) {
                  if (!level.isUnobstructed(hoja, p, CollisionContext.empty())) {
                     pendiente = true;
                  } else {
                     level.setBlock(p, hoja, 2);
                  }
               }
            }
         }
      }

      return pendiente;
   }

   private void quitarHojas(Level level, BlockPos o, Direction frente) {
      Direction hacia = haciaAbierta(frente);

      for (int dx = 0; dx < 8; dx += 7) {
         for (int k = 1; k <= 3; k++) {
            for (int dy = 0; dy < 11; dy++) {
               BlockPos p = celda(o, frente, dx, dy).relative(hacia, k);
               if (level.isLoaded(p) && level.getBlockState(p).is(PuertaBunker.BLOQUE_HOJA.get())) {
                  level.setBlock(p, Blocks.AIR.defaultBlockState(), 2);
               }
            }
         }
      }
   }

   public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      if ((Integer)state.getValue(DX) == 2 && (Integer)state.getValue(DY) == 0) {
         this.reaperturaAutomatica(state, level, pos);
      } else if ((Integer)state.getValue(DX) == 1 && (Integer)state.getValue(DY) == 0) {
         this.cierreAutomatico(state, level, pos);
      } else {
         if ((Boolean)state.getValue(OPEN)
            && (Integer)state.getValue(DX) == 0
            && (Integer)state.getValue(DY) == 0
            && this.colocarHojas(level, pos, (Direction)state.getValue(FACING))) {
            level.scheduleTick(pos, this, 10);
         }
      }
   }

   private void cierreAutomatico(BlockState state, ServerLevel level, BlockPos pos) {
      BlockPos o = origen(pos, state);
      if (level.getBlockEntity(o) instanceof PuertaBunkerBE be) {
         long cuando = be.cierraEn();
         if (cuando >= 0L) {
            if (!(Boolean)state.getValue(OPEN)) {
               be.setCierraEn(-1L);
            } else {
               long ahora = level.getGameTime();
               if (ahora < cuando) {
                  level.scheduleTick(pos, this, (int)Math.min(2147483647L, cuando - ahora));
               } else {
                  be.setCierraEn(-1L);
                  if (!MomentosBunker.gestionaPuerta(level, o)) {
                     this.poner(level, o, (Direction)state.getValue(FACING), false);
                  }
               }
            }
         }
      }
   }

   private void reaperturaAutomatica(BlockState state, ServerLevel level, BlockPos pos) {
      BlockPos o = origen(pos, state);
      if (level.getBlockEntity(o) instanceof PuertaBunkerBE be) {
         long cuando = be.abreEn();
         if (cuando >= 0L) {
            if ((Boolean)state.getValue(OPEN)) {
               be.setAbreEn(-1L);
            } else {
               long ahora = level.getGameTime();
               if (ahora < cuando) {
                  level.scheduleTick(pos, this, (int)Math.min(2147483647L, cuando - ahora));
               } else {
                  be.setAbreEn(-1L);
                  if (MomentosBunker.gestionaPuerta(level, o)) {
                     this.poner(level, o, (Direction)state.getValue(FACING), true);
                  }
               }
            }
         }
      }
   }

   private void programarReapertura(Level level, BlockPos o, BlockState cualquiera) {
      if (!this.grande && level instanceof ServerLevel sl && level.getBlockEntity(o) instanceof PuertaBunkerBE be) {
         if (MomentosBunker.gestionaPuerta(level, o)) {
            int ticks = Math.max(1, Ajustes.entero("bunker.puerta_reabrir")) * 20;
            be.setAbreEn(level.getGameTime() + ticks);
            sl.scheduleTick(celda(o, (Direction)cualquiera.getValue(FACING), 2, 0), this, ticks);
         }
      }
   }

   private void programarCierre(Level level, BlockPos o, BlockState cualquiera) {
      if (!this.grande && level instanceof ServerLevel sl && level.getBlockEntity(o) instanceof PuertaBunkerBE be) {
         if (MomentosBunker.gestionaPuerta(level, o)) {
            be.setCierraEn(-1L);
         } else {
            int ticks = Math.max(1, Ajustes.entero("bunker.puerta_auto")) * 20;
            be.setCierraEn(level.getGameTime() + ticks);
            sl.scheduleTick(celda(o, (Direction)cualquiera.getValue(FACING), 1, 0), this, ticks);
         }
      }
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext ctx) {
      Direction frente = ctx.getHorizontalDirection().getOpposite();
      BlockPos o = ctx.getClickedPos();
      Level level = ctx.getLevel();
      if (o.getY() + 11 > level.getMaxY()) {
         return null;
      }

      for (int dx = 0; dx < 8; dx++) {
         for (int dy = 0; dy < 11; dy++) {
            if (dx != 0 || dy != 0) {
               BlockPos p = celda(o, frente, dx, dy);
               if (!level.isInWorldBounds(p) || !level.getBlockState(p).canBeReplaced(ctx)) {
                  return null;
               }
            }
         }
      }

      return (BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(FACING, frente)).setValue(OPEN, Boolean.FALSE))
            .setValue(DX, 0))
         .setValue(DY, 0);
   }

   public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity quien, ItemStack stack) {
      if (!level.isClientSide()) {
         Direction frente = (Direction)state.getValue(FACING);

         for (int dx = 0; dx < 8; dx++) {
            for (int dy = 0; dy < 11; dy++) {
               if (dx != 0 || dy != 0) {
                  level.setBlock(celda(pos, frente, dx, dy), (BlockState)((BlockState)state.setValue(DX, dx)).setValue(DY, dy), 2);
               }
            }
         }

         if (level.getBlockEntity(pos) instanceof PuertaBunkerBE be && this.hayRedstone(level, pos, frente)) {
            be.setAlimentada(true);
            this.poner(level, pos, frente, true);
         }
      }
   }

   public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
      if (!level.isClientSide() && !player.isCreative()) {
         Block.popResource(level, pos, new ItemStack(this.grande ? (ItemLike)PuertaBunker.ITEM_GRANDE.get() : (ItemLike)PuertaBunker.ITEM.get()));
      }

      return super.playerWillDestroy(level, pos, state, player);
   }

   public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) { }

   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
      boolean todos = !this.grande && Ajustes.bool("bunker.puerta_todos");
      if (!todos && !player.isCreative() && !player.permissions().hasPermission(net.minecraft.server.permissions.Permissions.COMMANDS_GAMEMASTER)) {
         return InteractionResult.PASS;
      }

      if (!level.isClientSide()) {
         BlockPos o = origen(pos, state);
         boolean abrir = !(Boolean)state.getValue(OPEN);
         this.poner(level, o, (Direction)state.getValue(FACING), abrir);
         if (abrir) {
            this.programarCierre(level, o, state);
         } else {
            this.programarReapertura(level, o, state);
         }
      }

      return InteractionResult.SUCCESS;
   }

   public void neighborChanged(BlockState state, Level level, BlockPos pos, Block bloque, BlockPos desde, boolean moviendo) {
      if (!level.isClientSide() && !desmontando) {
         BlockPos o = origen(pos, state);
         Direction frente = (Direction)state.getValue(FACING);
         if (level.getBlockEntity(o) instanceof PuertaBunkerBE be) {
            if (this.grande || !Bunkers.enCaja(level, o)) {
               boolean alimentada = this.hayRedstone(level, o, frente);
               if (alimentada != be.estaAlimentada()) {
                  be.setAlimentada(alimentada);
                  this.poner(level, o, frente, alimentada);
               }
            }
         }
      }
   }

   private boolean hayRedstone(Level level, BlockPos o, Direction frente) {
      for (int dx = 0; dx < 8; dx++) {
         for (int dy = 0; dy < 11; dy++) {
            if (level.hasNeighborSignal(celda(o, frente, dx, dy))) {
               return true;
            }
         }
      }

      return false;
   }

   public static boolean ponerPuerta(Level level, BlockPos origen, boolean abierta) {
      BlockState s = level.getBlockState(origen);
      if (s.getBlock() instanceof PuertaBunkerBloque b) {
         b.poner(level, origen(origen, s), (Direction)s.getValue(FACING), abierta);
         return true;
      } else {
         return false;
      }
   }

   private void poner(Level level, BlockPos o, Direction frente, boolean abierta) {
      if (level.getBlockEntity(o) instanceof PuertaBunkerBE be0) {
         be0.setCierraEn(-1L);
         be0.setAbreEn(-1L);
      }

      boolean cambio = false;

      for (int dx = 0; dx < 8; dx++) {
         for (int dy = 0; dy < 11; dy++) {
            BlockPos p = celda(o, frente, dx, dy);
            BlockState s = level.getBlockState(p);
            if (this.esDeEstaPuerta(s, frente, dx, dy) && (Boolean)s.getValue(OPEN) != abierta) {
               level.setBlock(p, (BlockState)s.setValue(OPEN, abierta), 2);
               cambio = true;
            }
         }
      }

      if (cambio) {
         if (abierta) {
            level.scheduleTick(o, this, 60);
         } else {
            this.quitarHojas(level, o, frente);
         }

         BlockPos centro = celda(o, frente, 4, 5);
         level.playSound(null, centro, abierta ? PuertaBunker.SONIDO_ABRIR.get() : PuertaBunker.SONIDO_CERRAR.get(), SoundSource.BLOCKS, 3.0F, 1.0F);
         level.gameEvent(null, abierta ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, centro);
      }
   }

   private static VoxelShape losa(BlockState s) {
      return ((Direction)s.getValue(FACING)).getAxis() == Axis.Z ? LOSA_Z : LOSA_X;
   }

   public VoxelShape getShape(BlockState s, BlockGetter level, BlockPos pos, CollisionContext ctx) {
      return esHueco(s) ? Shapes.empty() : losa(s);
   }

   public VoxelShape getCollisionShape(BlockState s, BlockGetter level, BlockPos pos, CollisionContext ctx) {
      return esHueco(s) ? Shapes.empty() : losa(s);
   }

   public int getLightBlock(BlockState s, BlockGetter level, BlockPos pos) {
      return esHueco(s) ? 0 : 15;
   }

   public boolean propagatesSkylightDown(BlockState s, BlockGetter level, BlockPos pos) {
      return esHueco(s);
   }

   public RenderShape getRenderShape(BlockState s) {
      return RenderShape.INVISIBLE;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return state.getValue(DX) == 0 && state.getValue(DY) == 0 ? new PuertaBunkerBE(pos, state) : null;
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
      return level.isClientSide() ? createTickerHelper(type, PuertaBunker.BE.get(), PuertaBunkerBE::tickCliente) : null;
   }
}
