package ar.hombrosafio.ruleta;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Punto de entrada común (servidor y cliente). */
public final class RuletaMod implements ModInitializer {
	public static final String ID = "ruleta";
	public static final Logger LOG = LoggerFactory.getLogger(ID);

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(ID, path);
	}

	@Override
	public void onInitialize() {
		RuletaNet.register();
		RuletaConfig.register();
		DifficultyChanges.register();
		RouletteManager.register();
		ar.hombrosafio.ruleta.casino.Casino.register();
	}
}
