package ar.hombrosafio.ruleta.casino;

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
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.BlockHitResult;

/** Tragamonedas de tres bloques de alto; la entidad vive en el bloque del medio. */
public class TragamonedasBloque extends Block implements EntityBlock {
	public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final EnumProperty<ParteTragamonedas> PARTE = EnumProperty.create("part", ParteTragamonedas.class);

	public TragamonedasBloque(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(PARTE, ParteTragamonedas.LOWER));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, PARTE);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return state.getValue(PARTE) == ParteTragamonedas.MIDDLE ? new TragamonedasBE(pos, state) : null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		if (!level.isClientSide() && state.getValue(PARTE) == ParteTragamonedas.MIDDLE && type == Casino.BE) {
			return (l, p, s, be) -> TragamonedasBE.tickServidor(l, p, s, (TragamonedasBE) be);
		}
		return null;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		BlockPos pos = ctx.getClickedPos();
		Level level = ctx.getLevel();
		if (pos.getY() > level.getMaxY() - 3) {
			return null;
		}
		for (int i = 1; i <= 2; i++) {
			if (!level.getBlockState(pos.above(i)).canBeReplaced(ctx)) {
				return null;
			}
		}
		return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite()).setValue(PARTE, ParteTragamonedas.LOWER);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		level.setBlock(pos.above(1), state.setValue(PARTE, ParteTragamonedas.MIDDLE), 3);
		level.setBlock(pos.above(2), state.setValue(PARTE, ParteTragamonedas.UPPER), 3);
	}

	@Override
	protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		ConfigTragamonedas cfg = ConfigTragamonedas.get();
		Identifier id = Identifier.tryParse(cfg.pago_item);
		Item pago = id == null || !BuiltInRegistries.ITEM.containsKey(id) ? Casino.DEDITA : BuiltInRegistries.ITEM.getValue(id);
		if (!stack.is(pago)) {
			return InteractionResult.TRY_WITH_EMPTY_HAND;
		}
		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}
		if (stack.getCount() < cfg.pago_cantidad && !player.getAbilities().instabuild) {
			return InteractionResult.CONSUME;
		}
		BlockPos medio = pos.below(state.getValue(PARTE).altura()).above(1);
		if (level.getBlockEntity(medio) instanceof TragamonedasBE be && be.girar((ServerLevel) level)) {
			if (!player.getAbilities().instabuild) {
				stack.shrink(cfg.pago_cantidad);
			}
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.CONSUME;
	}

	@Override
	protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction direction,
		BlockPos neighborPos, BlockState neighbor, RandomSource random) {
		ParteTragamonedas parte = state.getValue(PARTE);
		if (direction == Direction.UP && parte != ParteTragamonedas.UPPER) {
			if (!neighbor.is(this) || neighbor.getValue(PARTE).altura() != parte.altura() + 1) {
				return Blocks.AIR.defaultBlockState();
			}
		} else if (direction == Direction.DOWN && parte != ParteTragamonedas.LOWER
			&& (!neighbor.is(this) || neighbor.getValue(PARTE).altura() != parte.altura() - 1)) {
			return Blocks.AIR.defaultBlockState();
		}
		return super.updateShape(state, level, ticks, pos, direction, neighborPos, neighbor, random);
	}

	@Override
	public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		if (!level.isClientSide()) {
			BlockPos base = pos.below(state.getValue(PARTE).altura());
			if (!player.isCreative()) {
				popResource(level, base, new ItemStack(Casino.ITEM));
			}
			for (int i = 0; i < 3; i++) {
				BlockPos p = base.above(i);
				if (!p.equals(pos) && level.getBlockState(p).is(this)) {
					level.setBlock(p, Blocks.AIR.defaultBlockState(), 35);
				}
			}
		}
		return super.playerWillDestroy(level, pos, state, player);
	}

	@Override
	protected BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
	protected BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}
}
