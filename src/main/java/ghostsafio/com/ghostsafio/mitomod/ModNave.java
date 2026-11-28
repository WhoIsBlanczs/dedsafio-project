package com.ghostsafio.mitomod;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStarted;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents.ModifyEntries;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;

public class ModNave {
   public static final ResourceKey<Level> SPACE = ResourceKey.create(Registries.DIMENSION, Ghostsafio.id("espacio"));
   public static final ResourceKey<Level> RED_PLANET = ResourceKey.create(Registries.DIMENSION, Ghostsafio.id("gleba"));
   public static final float SCALE = 0.7F;
   public static final float PIVOT_HEIGHT = 3.3F;
   public static final float COCKPIT_FLOOR = 3.9F;
   public static final EntityType<NaveEntity> NAVE = (EntityType<NaveEntity>)Registry.register(
      BuiltInRegistries.ENTITY_TYPE,
      Ghostsafio.id("nave"),
      Builder.of(NaveEntity::new, MobCategory.MISC)
         .sized(1.54F, 4.6899996F)
         .attach(EntityAttachment.PASSENGER, 0.0F, 3.33F, 0.0F)
         .clientTrackingRange(16)
         .updateInterval(1)
         .build("nave")
   );
   public static final Item NAVE_ITEM = (Item)Registry.register(
      BuiltInRegistries.ITEM,
      Ghostsafio.id("nave"),
      new NaveItem(new Properties().stacksTo(1).component(DataComponents.ITEM_NAME, Component.literal("Nave espacial")))
   );

   public static void register() {
      PayloadTypeRegistry.playC2S().register(NaveInputPayload.TYPE, NaveInputPayload.CODEC);
      ServerPlayNetworking.registerGlobalReceiver(NaveInputPayload.TYPE, (payload, context) -> {
         if (context.player().getVehicle() instanceof NaveEntity nave) {
            nave.setInput(payload.flags());
         }
      });
      ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register((ModifyEntries)entries -> entries.accept(NAVE_ITEM));
      CommandRegistrationCallback.EVENT
         .register(
            (CommandRegistrationCallback)(dispatcher, registryAccess, environment) -> dispatcher.register(
               (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("gleba").requires(src -> src.hasPermission(2)))
                  .executes(
                     ctx -> {
                        ServerPlayer player = ((CommandSourceStack)ctx.getSource()).getPlayerOrException();
                        ServerLevel gleba = ((CommandSourceStack)ctx.getSource()).getServer().getLevel(RED_PLANET);
                        if (gleba == null) {
                           ((CommandSourceStack)ctx.getSource())
                              .sendFailure(
                                 Component.literal(
                                    "La dimensión ghostsafio:gleba NO está cargada. Revisa data/ghostsafio/dimension/gleba.json y dimension_type/gleba.json, y mira el log (busca 'ghostsafio:gleba')."
                                 )
                              );
                           return 0;
                        } else {
                           int y = GlebaPad.ensure(gleba);
                           player.teleportTo(gleba, 0.5, y + 1.0, 0.5, player.getYRot(), player.getXRot());
                           return 1;
                        }
                     }
                  )
            )
         );
      ServerLifecycleEvents.SERVER_STARTED
         .register(
            (ServerStarted)server -> Ghostsafio.LOGGER
               .info(
                  "[ghostsafio] dimensión espacio: {} · dimensión gleba: {}",
                  server.getLevel(SPACE) != null ? "OK" : "NO CARGADA",
                  server.getLevel(RED_PLANET) != null ? "OK" : "NO CARGADA"
               )
         );
      ServerTickEvents.END_SERVER_TICK.register(ModNave::spaceSafety);
   }

   private static void spaceSafety(MinecraftServer server) {
      if (server.getTickCount() % 20 == 0) {
         ServerLevel space = server.getLevel(SPACE);
         if (space != null) {
            for (ServerPlayer player : space.players()) {
               if (!player.isCreative()
                  && !player.isSpectator()
                  && !(player.getVehicle() instanceof NaveEntity)
                  && space.getEntitiesOfClass(NaveEntity.class, player.getBoundingBox().inflate(40.0)).isEmpty()) {
                  ServerLevel overworld = server.overworld();
                  player.teleportTo(overworld, player.getX(), 400.0, player.getZ(), player.getYRot(), player.getXRot());
                  player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 1200, 0, false, false, false));
                  player.fallDistance = 0.0F;
               }
            }
         }
      }
   }
}
