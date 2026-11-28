package com.ghostsafio.mitomod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.Vec3;

public class ModPhora {
   public static final List<Block> BLOCKS = new ArrayList<>();
   private static final List<Item> ITEMS = new ArrayList<>();
   public static final Block PANEL = register("phora_panel", 0);
   public static final Block TILE = register("phora_tile", 0);
   public static final Block ORANGE = register("phora_orange", 9);
   public static final Block CIRCUIT_ORANGE = register("phora_circuit_orange", 0);
   public static final Block YELLOW = register("phora_yellow", 8);
   public static final Block TEAL = register("phora_teal", 11);
   public static final Block CIRCUIT_TEAL = register("phora_circuit_teal", 0);
   public static final Block RED = register("phora_red", 8);
   public static final Block VERDE = register("phora_verde", 15, MapColor.COLOR_GREEN);
   public static final Block IMPULSOR = registerImpulsor("phora_impulsor");
   public static final Identifier MUSICA_ID = Identifier.fromNamespaceAndPath("ghostsafio_cubo", "musica");
   public static final SoundEvent MUSICA = (SoundEvent)Registry.register(
      BuiltInRegistries.SOUND_EVENT, MUSICA_ID, SoundEvent.createVariableRangeEvent(MUSICA_ID)
   );

   private static Block register(String id, int light) {
      return register(id, light, MapColor.COLOR_GRAY);
   }

   private static Block register(String id, int light, MapColor color) {
      Properties p = Properties.of().mapColor(color).strength(3.0F, 40.0F).sound(SoundType.DEEPSLATE_TILES);
      if (light > 0) {
         p = p.lightLevel(state -> light);
      }

      Block block = (Block)Registry.register(BuiltInRegistries.BLOCK, Ghostsafio.id(id), new Block(p.setId(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.BLOCK, Ghostsafio.id(id)))));
      Item item = (Item)Registry.register(BuiltInRegistries.ITEM, com.ghostsafio.mitomod.compat.Ids.item(Ghostsafio.id(id)), new BlockItem(block, new net.minecraft.world.item.Item.Properties()));
      BLOCKS.add(block);
      ITEMS.add(item);
      return block;
   }

   private static Block registerImpulsor(String id) {
      Properties p = Properties.of().mapColor(MapColor.COLOR_ORANGE).strength(3.0F, 40.0F).sound(SoundType.DEEPSLATE_TILES).lightLevel(state -> 7);
      Block block = (Block)Registry.register(BuiltInRegistries.BLOCK, Ghostsafio.id(id), new ModPhora.ImpulsorBlock(p.setId(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.BLOCK, Ghostsafio.id(id)))));
      Item item = (Item)Registry.register(BuiltInRegistries.ITEM, com.ghostsafio.mitomod.compat.Ids.item(Ghostsafio.id(id)), new BlockItem(block, new net.minecraft.world.item.Item.Properties()));
      BLOCKS.add(block);
      ITEMS.add(item);
      return block;
   }

   public static void register() {
      CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.BUILDING_BLOCKS).register(entries -> ITEMS.forEach(entries::accept));
      CuboPhora.register();
   }

   public static final class ImpulsorBlock extends Block {
      private static final double LAUNCH = 2.05;
      private static final int COOLDOWN = 60;
      private static final Map<UUID, Long> LAST_LAUNCH = new HashMap<>();

      public ImpulsorBlock(Properties properties) {
         super(properties);
      }

      public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
         if (!level.isClientSide() && entity instanceof Player player) {
            long now = level.getGameTime();
            Long last = LAST_LAUNCH.get(player.getUUID());
            if (last == null || now - last >= 60L) {
               LAST_LAUNCH.put(player.getUUID(), now);
               Vec3 v = player.getDeltaMovement();
               player.setDeltaMovement(v.x, 2.05, v.z);
               player.needsSync = true;
               level.playSound(null, pos, SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.BLOCKS, 0.6F, 1.2F);
            }
         }

         super.stepOn(level, pos, state, entity);
      }
   }
}
