package net.ruleta.cajero;

import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
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
import org.jetbrains.annotations.Nullable;

public class CajeroBloque extends Block {
   public static final MapCodec<CajeroBloque> CODEC = simpleCodec(CajeroBloque::new);
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final EnumProperty<ParteCajero> PARTE = EnumProperty.create("part", ParteCajero.class);
   private static final VoxelShape[] FORMAS = new VoxelShape[4];

   private static VoxelShape formaGirada(Direction dir) {
      double x1 = 0.0625;
      double z1 = 0.0625;
      double x2 = 0.8125;
      double z2 = 1.0;
      int pasos = (dir.get2DDataValue() + 2) % 4;

      for (int i = 0; i < pasos; i++) {
         double nx1 = 1.0 - z2;
         double nx2 = 1.0 - z1;
         double nz1 = x1;
         double nz2 = x2;
         x1 = nx1;
         x2 = nx2;
         z1 = nz1;
         z2 = nz2;
      }

      return Shapes.box(x1, 0.0, z1, x2, 1.0, z2);
   }

   public CajeroBloque(Properties propiedades) {
      super(propiedades);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(PARTE, ParteCajero.LOWER)
      );
   }

   protected MapCodec<? extends Block> codec() {
      return CODEC;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, PARTE});
   }

   protected VoxelShape getShape(BlockState estado, BlockGetter nivel, BlockPos pos, CollisionContext ctx) {
      return FORMAS[((Direction)estado.getValue(FACING)).get2DDataValue()];
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext ctx) {
      BlockPos pos = ctx.getClickedPos();
      Level nivel = ctx.getLevel();
      if (pos.getY() > nivel.getMaxBuildHeight() - 2) {
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
      if (nivel.isClientSide) {
         return InteractionResult.SUCCESS;
      }

      if (jugador instanceof ServerPlayer sp) {
         BlockPos base = pos.below(((ParteCajero)estado.getValue(PARTE)).altura());
         long saldo = SaldoDatos.get(sp.server).saldo(sp.getUUID());
         ServerPlayNetworking.send(sp, new CajeroRed.AbrirPayload(base, saldo));
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
      if (!nivel.isClientSide) {
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

   static {
      for (Direction d : Plane.HORIZONTAL) {
         FORMAS[d.get2DDataValue()] = formaGirada(d);
      }
   }
}
