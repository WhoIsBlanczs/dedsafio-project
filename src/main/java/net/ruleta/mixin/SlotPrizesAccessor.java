package net.ruleta.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.resources.Identifier;

/** Permite cambiar la lista de premios y la ficha de la tragamonedas de CasinoHS. */
@Mixin(targets = "com.casinohs.SlotPrizes", remap = false)
public interface SlotPrizesAccessor {
	@Accessor(value = "PRIZES", remap = false)
	@Mutable
	static void ruleta$setPremios(List<?> premios) {
		throw new AssertionError();
	}

	@Accessor(value = "COIN", remap = false)
	@Mutable
	static void ruleta$setFicha(Identifier ficha) {
		throw new AssertionError();
	}
}
