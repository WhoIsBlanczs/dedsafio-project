package ar.hombrosafio.ruleta.casino;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class DeditaItem extends Item {
	private final ChatFormatting color;

	public DeditaItem(Properties properties) {
		this(properties, ChatFormatting.GOLD);
	}

	public DeditaItem(Properties properties, ChatFormatting color) {
		super(properties);
		this.color = color;
	}

	@Override
	public Component getName(ItemStack stack) {
		return super.getName(stack).copy().withStyle(this.color);
	}
}
