package net.ruleta.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.item.ItemStack;

/** Para borrar los ítems que la tragamonedas guarda en caché cuando cambian los premios. */
@Mixin(targets = "com.casinohs.client.SlotMachineRenderer", remap = false)
public interface SlotMachineRendererAccessor {
	@Accessor(value = "DISPLAY", remap = false)
	static ItemStack[] ruleta$cache() {
		throw new AssertionError();
	}
}
