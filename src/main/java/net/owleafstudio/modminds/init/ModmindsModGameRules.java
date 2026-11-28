package net.owleafstudio.modminds.init;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
   bus = Mod.EventBusSubscriber.Bus.MOD
)
public class ModmindsModGameRules {
   public static final GameRules.Key<GameRules.BooleanValue> BLOQUEADOR_COMANDO = GameRuleRegistry.register(
      "bloqueadorComando", GameRules.Category.PLAYER, GameRuleFactory.createBooleanRule(true)
   );
   public static final GameRules.Key<GameRules.IntegerValue> ZOMBIFICACION_COMANDO = GameRuleRegistry.register(
      "zombificacionComando", GameRules.Category.PLAYER, GameRuleFactory.createIntRule(0)
   );
   public static final GameRules.Key<GameRules.BooleanValue> SAPITO_PANTANO_COMANDO = GameRuleRegistry.register(
      "sapitoPantanoComando", GameRules.Category.MOBS, GameRuleFactory.createBooleanRule(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> TORTUGA_NETHERITA_COMANDO = GameRuleRegistry.register(
      "tortugaNetheritaComando", GameRules.Category.MOBS, GameRuleFactory.createBooleanRule(true)
   );
}
