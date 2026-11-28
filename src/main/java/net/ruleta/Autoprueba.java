package net.ruleta;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import com.google.gson.JsonParser;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffers;

/** Solo se usa en las pruebas automáticas (con -Druleta.autoprueba=1): comprueba que los mixins funcionan. */
final class Autoprueba {
	private Autoprueba() {
	}

	static void correr(MinecraftServer server) {
		if (System.getProperty("ruleta.autoprueba") == null) {
			return;
		}

		try {
			CasinoDatos.aplicar(JsonParser.parseString("{\"ficha\":\"minecraft:gold_ingot\",\"premios\":[{\"item\":\"minecraft:diamond\",\"cantidad\":5,\"peso\":3,\"jackpot\":true},{\"item\":\"minecraft:emerald\",\"cantidad\":2,\"peso\":1,\"jackpot\":false}]}").getAsJsonObject());
			Class<?> sp = Class.forName("com.casinohs.SlotPrizes");
			Field premios = sp.getField("PRIZES");
			Field ficha = sp.getField("COIN");
			List<?> lista = (List<?>) premios.get(null);
			Method reward = sp.getMethod("reward", int.class);
			ItemStack r0 = (ItemStack) reward.invoke(null, 0);
			boolean inject = false;

			for (Method m : sp.getDeclaredMethods()) {
				inject |= m.getName().contains("ruleta$premio");
			}

			RuletaMod.LOGGER.info("RULETA-AUTOPRUEBA casino premios={} primero={} ficha={} reward0={}x{} inject={}",
					lista.size(), lista.get(0), ficha.get(null), r0.getItem(), r0.getCount(), inject);

			PedestalDatos.aplicar(JsonParser.parseString("{\"comercios\":[{\"das\":\"minecraft:dirt\",\"das_n\":3,\"recibes\":\"minecraft:diamond\",\"recibes_n\":2}]}").getAsJsonObject());
			MerchantOffers o = (MerchantOffers) Class.forName("ar.hombrosafio.CasinoExchange").getMethod("offers").invoke(null);
			RuletaMod.LOGGER.info("RULETA-AUTOPRUEBA pedestal ofertas={} primera={}", o.size(), o.isEmpty() ? "-" : o.get(0).getCostA() + " -> " + o.get(0).getResult());

			boolean ok = lista.size() == 2 && r0.getCount() == 5 && inject && o.size() == 1 && o.get(0).getResult().getCount() == 2;
			RuletaMod.LOGGER.info("RULETA-AUTOPRUEBA {}", ok ? "OK" : "FALLO");
		} catch (Throwable t) {
			RuletaMod.LOGGER.error("RULETA-AUTOPRUEBA FALLO", t);
		}
	}
}
