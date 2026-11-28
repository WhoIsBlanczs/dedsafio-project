package net.ruleta.cosmeticos;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.ruleta.Persistente;
import net.ruleta.Reg;

public final class Cosmeticos {
   public static final String[][] PAISES = new String[][]{
      {"argentina", "Argentina"},
      {"bolivia", "Bolivia"},
      {"brasil", "Brasil"},
      {"chile", "Chile"},
      {"colombia", "Colombia"},
      {"costa_rica", "Costa Rica"},
      {"cuba", "Cuba"},
      {"ecuador", "Ecuador"},
      {"el_salvador", "El Salvador"},
      {"espana", "España"},
      {"guatemala", "Guatemala"},
      {"honduras", "Honduras"},
      {"mexico", "México"},
      {"nicaragua", "Nicaragua"},
      {"panama", "Panamá"},
      {"paraguay", "Paraguay"},
      {"peru", "Perú"},
      {"puerto_rico", "Puerto Rico"},
      {"republica_dominicana", "República Dominicana"},
      {"uruguay", "Uruguay"},
      {"venezuela", "Venezuela"}
   };
   public static final String CLAVE_AURA = "ruleta_aura";
   public static final Map<String, Reg<Holder<MobEffect>>> EFECTO_AURA = new HashMap<>();

   public static void sincronizarEfectos(ServerPlayer p) {
      String aura = aura(p);

      for (Entry<String, Reg<Holder<MobEffect>>> e : EFECTO_AURA.entrySet()) {
         efecto(p, e.getValue().get(), e.getKey().equals(aura));
      }
   }

   private static void efecto(ServerPlayer p, Holder<MobEffect> e, boolean quiere) {
      MobEffectInstance actual = p.getEffect(e);
      if (quiere) {
         if (actual == null || !actual.isInfiniteDuration()) {
            p.addEffect(new MobEffectInstance(e, -1, 0, false, false, true));
         }
      } else if (actual != null) {
         p.removeEffect(e);
      }
   }

   public static String aura(Player p) {
      return Persistente.de(p).getString("ruleta_aura");
   }

   public static void ponerAura(Player p, String pais) {
      if (pais.isEmpty()) {
         Persistente.de(p).remove("ruleta_aura");
      } else {
         Persistente.de(p).putString("ruleta_aura", pais);
      }
   }

   private Cosmeticos() {
   }

   public static void init() {
   }

   public static String normalizar(String texto) {
      String s = Normalizer.normalize(texto.trim(), Form.NFD).replaceAll("\\p{M}", "");
      return s.toLowerCase(Locale.ROOT).replace("\"", "").replace('-', '_').replaceAll("\\s+", "_");
   }

   static {
      for (String[] pais : PAISES) {
         EFECTO_AURA.put(pais[0], Reg.efecto("ruletafanproject", "aura_" + pais[0], () -> new EfectoCosmetico(16777215)));
      }
   }
}
