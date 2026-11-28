package net.owleafstudio.modminds.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.owleafstudio.modminds.procedures.AlternarAlmaTestProcedure;

@Mod.EventBusSubscriber
public class ToggleAlmaCommand {
   @SubscribeEvent
   public static void registerCommand(RegisterCommandsEvent event) {
      event.getDispatcher()
         .register(
            (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("toggle_alma").requires(s -> net.owleafstudio.modminds.compat.Compat.permiso(s, 4)))
               .then(Commands.argument("alma", EntityArgument.entities()).executes(arguments -> {
                  Level world = ((CommandSourceStack)arguments.getSource()).getLevel();
                  double x = ((CommandSourceStack)arguments.getSource()).getPosition().x();
                  double y = ((CommandSourceStack)arguments.getSource()).getPosition().y();
                  double z = ((CommandSourceStack)arguments.getSource()).getPosition().z();
                  Entity entity = ((CommandSourceStack)arguments.getSource()).getEntity();
                  if (entity == null && world instanceof ServerLevel _servLevel) {
                     entity = FakePlayerFactory.getMinecraft(_servLevel);
                  }

                  Direction direction = Direction.DOWN;
                  if (entity != null) {
                     direction = entity.getDirection();
                  }

                  AlternarAlmaTestProcedure.execute(arguments, entity);
                  return 0;
               }))
         );
   }
}
