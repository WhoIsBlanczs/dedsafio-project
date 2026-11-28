package net.owleafstudio.modminds.init;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;

/** En 26.3 las reglas de juego llevan id con espacio de nombres y en minúsculas (/gamerule modminds:...). */
public class ModmindsModGameRules {
   public static final GameRule<Boolean> BLOQUEADOR_COMANDO = GameRuleBuilder.forBoolean(true).category(GameRuleCategory.PLAYER).buildAndRegister(Identifier.fromNamespaceAndPath("modminds", "bloqueador_comando"));
   public static final GameRule<Integer> ZOMBIFICACION_COMANDO = GameRuleBuilder.forInteger(0).category(GameRuleCategory.PLAYER).buildAndRegister(Identifier.fromNamespaceAndPath("modminds", "zombificacion_comando"));
   public static final GameRule<Boolean> SAPITO_PANTANO_COMANDO = GameRuleBuilder.forBoolean(true).category(GameRuleCategory.MOBS).buildAndRegister(Identifier.fromNamespaceAndPath("modminds", "sapito_pantano_comando"));
   public static final GameRule<Boolean> TORTUGA_NETHERITA_COMANDO = GameRuleBuilder.forBoolean(true).category(GameRuleCategory.MOBS).buildAndRegister(Identifier.fromNamespaceAndPath("modminds", "tortuga_netherita_comando"));

   public static void load() {
   }
}
