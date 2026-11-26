package ar.hombrosafio.ruleta.client;

import ar.hombrosafio.ruleta.RouletteColor;
import ar.hombrosafio.ruleta.RoulettePayload;
import ar.hombrosafio.ruleta.RuletaMod;
import java.util.ArrayList;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

final class RouletteSounds {
	private static RoulettePayload state = RoulettePayload.NONE;
	private static final ArrayList<SoundInstance> VOICES = new ArrayList<>();
	private static int last = -1;

	static Identifier id(String name) {
		return RuletaMod.id("roulette." + name);
	}

	static void register() {
		ClientTickEvents.END_CLIENT_TICK.register(mc -> {
			if (mc.level != null && state.active()) {
				advance(mc, (int) Math.floor(RouletteHud.elapsed()));
			}
		});
	}

	static void receive(RoulettePayload payload, Minecraft mc) {
		if (!payload.active()) {
			if (state.active() && RouletteHud.elapsed() < 252.0) {
				stop(mc);
			}
			VOICES.clear();
			state = payload;
		} else {
			if (!state.active() || payload.sequence() != state.sequence()) {
				stop(mc);
				last = payload.elapsed() - 1;
			}
			state = payload;
		}
	}

	static void reset(Minecraft mc) {
		stop(mc);
		state = RoulettePayload.NONE;
		last = -1;
	}

	private static void stop(Minecraft mc) {
		for (SoundInstance sound : VOICES) {
			mc.getSoundManager().stop(sound);
		}
		VOICES.clear();
	}

	private static void play(Minecraft mc, String cue) {
		SimpleSoundInstance sound = new SimpleSoundInstance(id(cue), SoundSource.PLAYERS, 1.0F, 1.0F, RandomSource.create(), false, 0,
			SoundInstance.Attenuation.NONE, 0.0, 0.0, 0.0, true);
		VOICES.removeIf(s -> !mc.getSoundManager().isActive(s));
		VOICES.add(sound);
		mc.getSoundManager().play(sound);
	}

	private static void advance(Minecraft mc, int elapsed) {
		elapsed = Math.min(277, elapsed);
		if (elapsed > last) {
			if (elapsed - last > 4) {
				last = elapsed - 1;
			}
			for (int t = last + 1; t <= elapsed; t++) {
				if (t == 0) {
					play(mc, "spin");
				}
				if (t == RouletteColor.SPIN && state.chosen() == RouletteColor.PINK) {
					play(mc, "nutria");
				}
			}
			last = elapsed;
		}
	}

	private RouletteSounds() {
	}
}
