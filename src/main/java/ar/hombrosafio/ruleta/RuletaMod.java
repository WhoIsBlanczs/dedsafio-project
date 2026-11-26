package ar.hombrosafio.ruleta;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;

/** Punto de entrada común (servidor y cliente). */
public final class RuletaMod implements ModInitializer {
	public static final String ID = "ruleta";

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(ID, path);
	}

	@Override
	public void onInitialize() {
		RuletaNet.register();
		RuletaConfig.register();
		DifficultyChanges.register();
		RouletteManager.register();
	}
}
