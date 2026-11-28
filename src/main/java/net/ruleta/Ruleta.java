package net.ruleta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Versión reducida de la clase central de la ruleta: solo lo que usan los mobs, ítems y bloques del catálogo.
 * DEDsafio 4 Ruleta Fan Project (c) 2026 DarkPlayer y colaboradores. Licencia CC BY-NC-SA 4.0.
 */
public final class Ruleta {
   public static final String MOD_ID = "ruletafanproject";
   public static final int PERMISSION_LEVEL = 2;
   public static final Logger LOG = LoggerFactory.getLogger("ruletafanproject");
   private static final int COLOR_TOTEM = 16766720;
   private static final Map<UUID, Long> ULTIMO_AVISO_TOTEM = new HashMap<>();

   private Ruleta() {
   }

   public static DamageSource fuenteLetalPublica(ServerPlayer p, String id, String frase) {
      return new DamageSource(p.damageSources().starve().typeHolder()) {
         @Override
         public String getMsgId() {
            return id;
         }

         @Override
         public Component getLocalizedDeathMessage(LivingEntity victima) {
            return Component.empty().append(victima.getDisplayName()).append(Component.literal(frase));
         }
      };
   }

   /** Sin el menú de la ruleta no hay cambios de dificultad activos. */
   public static boolean cambioActivoPublico(String id) {
      return false;
   }

   public static boolean totemGastadoAhora(ServerPlayer p) {
      Long t = ULTIMO_AVISO_TOTEM.get(p.getUUID());
      return t != null && p.level().getGameTime() - t <= 1L;
   }

   public static void avisarTotem(ServerPlayer p, DamageSource fuente, Component nombreTotem) {
      long ahora = p.level().getGameTime();
      Long previo = ULTIMO_AVISO_TOTEM.put(p.getUUID(), ahora);
      if (previo == null || ahora - previo > 1L) {
         Component mensaje = Component.empty()
            .append(Component.literal("\ue000").setStyle(Style.EMPTY.withFont(new net.minecraft.network.chat.FontDescription.Resource(Identifier.fromNamespaceAndPath("ruletafanproject", "totem")))))
            .append(Component.literal(" "))
            .append(p.getName().copy())
            .append(Component.literal(" consumió "))
            .append(nombreTotem.copy().withStyle(estilo -> estilo.withColor(COLOR_TOTEM)))
            .append(Component.literal(" ("))
            .append(razonTotem(fuente, p))
            .append(Component.literal(")"))
            .withStyle(estilo -> estilo.withColor(COLOR_TOTEM));
         p.level().getServer().getPlayerList().broadcastSystemMessage(mensaje, false);
      }
   }

   public static Component nombreTotem(ItemStack stack) {
      return stack.getHoverName().copy();
   }

   private static Component razonTotem(DamageSource fuente, ServerPlayer victima) {
      if ("supernova.onda".equals(fuente.getMsgId())) {
         return Component.literal("Onda Expansiva");
      }

      if ("supernova".equals(fuente.getMsgId())) {
         return Component.literal("Supernova");
      }

      if ("ruleta.herobrine".equals(fuente.getMsgId())) {
         return Component.literal("Herobrine");
      }

      if ("nuclear_explosion".equals(fuente.getMsgId())) {
         return Component.literal("Explosión de Creeper Nuclear");
      }

      String base = switch (fuente.getMsgId()) {
         case "fall" -> "caída";
         case "lava" -> "lava";
         case "inFire", "onFire" -> "fuego";
         case "hotFloor" -> "bloque de magma";
         case "ruleta.boton" -> "botón";
         case "ruleta.placa" -> "placa de presión";
         case "ruleta.puerta" -> "puerta";
         case "ruleta.gas" -> "Gas Tóxico de Reviil";
         case "ruleta.luna" -> "Luna maldita";
         case "ruleta.rayo" -> "Rayo mortal de Reviil";
         case "ruleta.agujero" -> "Agujeros Negros de Reviil";
         case "ruleta.tornado" -> "Tornado";
         case "ruleta.bomba_warden" -> "Bomba Warden";
         case "ruleta.meteorito" -> "Meteorito";
         case "ruleta.meteoritos" -> "Lluvia de Meteoritos de Reviil";
         case "ruleta.meteoritos_tornados" -> "Tornados de Reviil";
         case "drown" -> "ahogamiento";
         case "explosion", "explosion.player" -> "explosión";
         case "arrow" -> "flecha";
         case "trident" -> "tridente";
         case "fireball", "fireworks" -> "bola de fuego";
         case "thrown", "indirectMagic", "magic" -> "magia";
         case "wither", "witherSkull" -> "Wither";
         case "starve" -> "hambre";
         case "cactus" -> "cactus";
         case "sweetBerryBush" -> "arbusto de bayas";
         case "fallingBlock", "anvil" -> "bloque que cae";
         case "fallingStalactite" -> "estalactita";
         case "stalagmite" -> "estalagmita";
         case "flyIntoWall" -> "choque al volar";
         case "freeze" -> "congelación";
         case "lightningBolt" -> "rayo";
         case "dragonBreath" -> "aliento de dragón";
         case "thorns" -> "espinas";
         case "sonic_boom" -> "grito del Warden";
         case "cramming" -> "aplastamiento";
         case "inWall" -> "asfixia en un bloque";
         case "dryout" -> "deshidratación";
         case "mob", "player", "mob_projectile" -> "ataque";
         case "generic" -> "daño";
         default -> fuente.getMsgId();
      };
      Entity autor = fuente.getEntity();
      return autor != null && autor != victima ? Component.literal(base + " de ").append(autor.getName()) : Component.literal(base);
   }
}
