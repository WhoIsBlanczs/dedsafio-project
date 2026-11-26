package net.ruleta.estatua;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.ruleta.cajero.ParteCajero;
import org.jetbrains.annotations.Nullable;

public class EstatuaNutriaBloque extends BaseEntityBlock {
   public static final MapCodec<EstatuaNutriaBloque> CODEC = simpleCodec(EstatuaNutriaBloque::new);
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final EnumProperty<ParteCajero> PARTE = EnumProperty.create("part", ParteCajero.class);
   private static final VoxelShape FORMA_ABAJO = Shapes.block();
   private static final VoxelShape FORMA_ARRIBA = Shapes.box(0.05, 0.0, 0.05, 0.95, 1.0, 0.95);

   public EstatuaNutriaBloque(Properties propiedades) {
      super(propiedades);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(PARTE, ParteCajero.LOWER)
      );
   }

   protected MapCodec<? extends BaseEntityBlock> codec() {
      return CODEC;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, PARTE});
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState estado) {
      return estado.getValue(PARTE) == ParteCajero.LOWER ? new EstatuaNutriaBE(pos, estado) : null;
   }

   protected VoxelShape getShape(BlockState estado, BlockGetter nivel, BlockPos pos, CollisionContext ctx) {
      return estado.getValue(PARTE) == ParteCajero.LOWER ? FORMA_ABAJO : FORMA_ARRIBA;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext ctx) {
      BlockPos pos = ctx.getClickedPos();
      Level nivel = ctx.getLevel();
      if (pos.getY() > nivel.getMaxY() - 2) {
         return null;
      } else {
         return !nivel.getBlockState(pos.above()).canBeReplaced(ctx)
            ? null
            : (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite()))
               .setValue(PARTE, ParteCajero.LOWER);
      }
   }

   public void setPlacedBy(Level nivel, BlockPos pos, BlockState estado, @Nullable LivingEntity quien, ItemStack item) {
      nivel.setBlock(pos.above(), (BlockState)estado.setValue(PARTE, ParteCajero.UPPER), 3);
   }

   protected InteractionResult useWithoutItem(BlockState estado, Level nivel, BlockPos pos, Player jugador, BlockHitResult golpe) {
      if (nivel.isClientSide()) {
         return InteractionResult.SUCCESS;
      }

      if (jugador instanceof ServerPlayer sp) {
         ComercioEstatua.abrir(sp);
      }

      return InteractionResult.CONSUME;
   }

   protected BlockState updateShape(BlockState estado, Direction direccion, BlockState vecino, LevelAccessor nivel, BlockPos pos, BlockPos posVecino) {
      ParteCajero parte = (ParteCajero)estado.getValue(PARTE);
      if (direccion == Direction.UP && parte == ParteCajero.LOWER) {
         if (!vecino.is(this) || vecino.getValue(PARTE) != ParteCajero.UPPER) {
            return Blocks.AIR.defaultBlockState();
         }
      } else if (direccion == Direction.DOWN && parte == ParteCajero.UPPER && (!vecino.is(this) || vecino.getValue(PARTE) != ParteCajero.LOWER)) {
         return Blocks.AIR.defaultBlockState();
      }

      return super.updateShape(estado, direccion, vecino, nivel, pos, posVecino);
   }

   public BlockState playerWillDestroy(Level nivel, BlockPos pos, BlockState estado, Player jugador) {
      if (!nivel.isClientSide()) {
         BlockPos base = pos.below(((ParteCajero)estado.getValue(PARTE)).altura());

         for (int i = 0; i < 2; i++) {
            BlockPos p = base.above(i);
            if (!p.equals(pos) && nivel.getBlockState(p).is(this)) {
               nivel.setBlock(p, Blocks.AIR.defaultBlockState(), 35);
            }
         }
      }

      return super.playerWillDestroy(nivel, pos, estado, jugador);
   }

   protected BlockState rotate(BlockState estado, Rotation rotacion) {
      return (BlockState)estado.setValue(FACING, rotacion.rotate((Direction)estado.getValue(FACING)));
   }

   protected BlockState mirror(BlockState estado, Mirror espejo) {
      return estado.rotate(espejo.getRotation((Direction)estado.getValue(FACING)));
   }
}
