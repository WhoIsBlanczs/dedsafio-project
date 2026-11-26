package net.ruleta.tragamonedas;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class TragamonedasBloque extends BaseEntityBlock {
   public static final MapCodec<TragamonedasBloque> CODEC = simpleCodec(TragamonedasBloque::new);
   public static final EnumProperty<net.minecraft.core.Direction> FACING = HorizontalDirectionalBlock.FACING;
   public static final EnumProperty<ParteTragamonedas> PARTE = EnumProperty.create("part", ParteTragamonedas.class);

   public TragamonedasBloque(Properties propiedades) {
      super(propiedades);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(PARTE, ParteTragamonedas.LOWER)
      );
   }

   protected MapCodec<? extends BaseEntityBlock> codec() {
      return CODEC;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, PARTE});
   }

   protected RenderShape getRenderShape(BlockState estado) {
      return RenderShape.MODEL;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState estado) {
      return estado.getValue(PARTE) == ParteTragamonedas.MIDDLE ? new TragamonedasBE(pos, estado) : null;
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level nivel, BlockState estado, BlockEntityType<T> tipo) {
      return !nivel.isClientSide() && estado.getValue(PARTE) == ParteTragamonedas.MIDDLE
         ? createTickerHelper(tipo, Tragamonedas.BE.get(), TragamonedasBE::tickServidor)
         : null;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext ctx) {
      BlockPos pos = ctx.getClickedPos();
      Level nivel = ctx.getLevel();
      if (pos.getY() > nivel.getMaxY() - 3) {
         return null;
      }

      for (int i = 1; i <= 2; i++) {
         if (!nivel.getBlockState(pos.above(i)).canBeReplaced(ctx)) {
            return null;
         }
      }

      return (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite()))
         .setValue(PARTE, ParteTragamonedas.LOWER);
   }

   public void setPlacedBy(Level nivel, BlockPos pos, BlockState estado, @Nullable LivingEntity quien, ItemStack item) {
      nivel.setBlock(pos.above(1), (BlockState)estado.setValue(PARTE, ParteTragamonedas.MIDDLE), 3);
      nivel.setBlock(pos.above(2), (BlockState)estado.setValue(PARTE, ParteTragamonedas.UPPER), 3);
   }

   protected InteractionResult useItemOn(
      ItemStack pila, BlockState estado, Level nivel, BlockPos pos, Player jugador, InteractionHand mano, BlockHitResult golpe
   ) {
      ConfigTragamonedas cfg = ConfigTragamonedas.get();
      Identifier id = Identifier.tryParse(cfg.pago_item);
      Item pago = id == null ? Tragamonedas.DEDITA.get() : BuiltInRegistries.ITEM.getOptional(id).orElse(Tragamonedas.DEDITA.get());
      if (!pila.is(pago)) {
         return InteractionResult.TRY_WITH_EMPTY_HAND;
      }

      if (nivel.isClientSide()) {
         return InteractionResult.SUCCESS;
      }

      if (pila.getCount() < cfg.pago_cantidad && !jugador.getAbilities().instabuild) {
         return InteractionResult.CONSUME;
      }

      BlockPos medio = pos.below(((ParteTragamonedas)estado.getValue(PARTE)).altura()).above(1);
      if (nivel.getBlockEntity(medio) instanceof TragamonedasBE be && be.girar((ServerLevel)nivel)) {
         if (!jugador.getAbilities().instabuild) {
            pila.shrink(cfg.pago_cantidad);
         }

         return InteractionResult.SUCCESS;
      } else {
         return InteractionResult.CONSUME;
      }
   }

   protected BlockState updateShape(BlockState estado, Direction direccion, BlockState vecino, LevelAccessor nivel, BlockPos pos, BlockPos posVecino) {
      ParteTragamonedas parte = (ParteTragamonedas)estado.getValue(PARTE);
      if (direccion == Direction.UP && parte != ParteTragamonedas.UPPER) {
         if (!vecino.is(this) || ((ParteTragamonedas)vecino.getValue(PARTE)).altura() != parte.altura() + 1) {
            return Blocks.AIR.defaultBlockState();
         }
      } else if (direccion == Direction.DOWN
         && parte != ParteTragamonedas.LOWER
         && (!vecino.is(this) || ((ParteTragamonedas)vecino.getValue(PARTE)).altura() != parte.altura() - 1)) {
         return Blocks.AIR.defaultBlockState();
      }

      return super.updateShape(estado, direccion, vecino, nivel, pos, posVecino);
   }

   public BlockState playerWillDestroy(Level nivel, BlockPos pos, BlockState estado, Player jugador) {
      if (!nivel.isClientSide()) {
         BlockPos base = pos.below(((ParteTragamonedas)estado.getValue(PARTE)).altura());
         if (!jugador.isCreative() && estado.getValue(PARTE) != ParteTragamonedas.LOWER) {
            dropResources((BlockState)estado.setValue(PARTE, ParteTragamonedas.LOWER), nivel, base, null, jugador, jugador.getMainHandItem());
         }

         for (int i = 0; i < 3; i++) {
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
