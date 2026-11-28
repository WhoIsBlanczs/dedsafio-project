package net.owleafstudio.modminds.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.owleafstudio.modminds.boss.BossPanels;
import net.owleafstudio.modminds.procedures.AbrirGuiReviilProcedure;
import net.owleafstudio.modminds.procedures.AbrirPanelOrbsProcedure;
import net.owleafstudio.modminds.procedures.AbrirPanelProcedure;
import net.owleafstudio.modminds.procedures.LimbotitanpanelabrirProcedure;
import net.owleafstudio.modminds.procedures.ReactorHydraPanelAbrirProcedure;

@Mod.EventBusSubscriber
public class PanelBossCommand {
   private static final String[][] BOSSES = new String[][]{
      {"reviil", "0"},
      {"hydra", "0"},
      {"limbo_titan", "0"},
      {"orbs", "0"},
      {"reactor_hydra", "0"},
      {"tiki", "1"},
      {"coloso", "2"},
      {"gorgona", "3"},
      {"bomba_negra", "4"},
      {"warden", "5"}
   };

   @SubscribeEvent
   public static void registerCommand(RegisterCommandsEvent event) {
      LiteralArgumentBuilder<CommandSourceStack> root = (LiteralArgumentBuilder<CommandSourceStack>)Commands.literal("panel").requires(source -> net.owleafstudio.modminds.compat.Compat.permiso(source, 2));

      for (String[] boss : BOSSES) {
         String name = boss[0];
         int id = Integer.parseInt(boss[1]);
         root.then(Commands.literal(name).executes(arguments -> {
            CommandSourceStack source = (CommandSourceStack)arguments.getSource();
            Level world = source.getLevel();
            double x = source.getPosition().x();
            double y = source.getPosition().y();
            double z = source.getPosition().z();
            Entity entity = source.getEntity();
            if (entity == null && world instanceof ServerLevel _servLevel) {
               entity = FakePlayerFactory.getMinecraft(_servLevel);
            }

            open(world, x, y, z, entity, name, id);
            return 1;
         }));
      }

      event.getDispatcher().register(root);
   }

   private static void open(Level world, double x, double y, double z, Entity entity, String name, int boss) {
      switch (name) {
         case "reviil":
            AbrirGuiReviilProcedure.execute(world, x, y, z, entity);
            break;
         case "hydra":
            AbrirPanelProcedure.execute(world, x, y, z, entity);
            break;
         case "limbo_titan":
            LimbotitanpanelabrirProcedure.execute(world, x, y, z, entity);
            break;
         case "orbs":
            AbrirPanelOrbsProcedure.execute(world, x, y, z, entity);
            break;
         case "reactor_hydra":
            ReactorHydraPanelAbrirProcedure.execute(world, x, y, z, entity);
            break;
         default:
            BossPanels.open(world, x, y, z, entity, boss);
      }
   }

   private PanelBossCommand() {
   }
}
