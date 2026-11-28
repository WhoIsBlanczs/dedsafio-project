package com.hexagonsafio2;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents.Load;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class NaveCraft implements ModInitializer {
   public static final String MOD_ID = "hexagonsafio2";
   public static final Identifier NAVE_ID = Identifier.fromNamespaceAndPath("ghostsafio", "nave");
   public static final Block PLATAFORMA_NAVE = (Block)Registry.register(BuiltInRegistries.BLOCK, com.hexagonsafio2.compat.Ids.bloque(id("plataforma_nave")), new Block(Properties.of().strength(3.0F, 6.0F).sound(SoundType.METAL)));
   public static final Item PLATAFORMA_NAVE_ITEM = (Item)Registry.register(BuiltInRegistries.ITEM, com.hexagonsafio2.compat.Ids.item(id("plataforma_nave")), new BlockItem(PLATAFORMA_NAVE, new net.minecraft.world.item.Item.Properties()));
   public static final Item DILITIO = item("dilitio");
   public static final Item AMBAR_FOSIL = item("ambar_fosil");
   public static final Item BIOXITA = item("bioxita");
   public static final Item FRASCO_DILITIO = item("frasco_dilitio");
   public static final Item CABINA_NAVE = item("cabina_nave");
   public static final Item ALA_IZQUIERDA_NAVE = item("ala_izquierda_nave");
   public static final Item ALA_DERECHA_NAVE = item("ala_derecha_nave");
   public static final Item FUSELAJE_NAVE = item("fuselaje_nave");
   public static final Item CORAZON = corazon("corazon", j -> {
      CorazonItem.masCorazones(j, 1);
      j.heal(2.0F);
   });
   public static final Item CORAZON_LIMBO = corazon("corazon_limbo", j -> {
      CorazonItem.masCorazones(j, 3);
      j.setHealth(j.getMaxHealth());
   });
   public static final Item CORAZON_AMARILLO = corazon("corazon_amarillo", j -> j.setAbsorptionAmount(j.getAbsorptionAmount() + 10.0F));
   public static final Item MOTOR_NAVE = item("motor_nave");
   public static final Item MOTOR_MEJORADO_NAVE = item("motor_mejorado_nave");
   public static final Identifier ARCO_ID = Identifier.fromNamespaceAndPath("ghostsafio", "arco_glebanoide");
   private static final double PODER_EXTRA = 2.25;

   public static Identifier id(String path) {
      return Identifier.fromNamespaceAndPath("hexagonsafio2", path);
   }

   private static Item item(String name) {
      return (Item)Registry.register(BuiltInRegistries.ITEM, com.hexagonsafio2.compat.Ids.item(id(name)), new DescItem(new net.minecraft.world.item.Item.Properties()));
   }

   private static Item corazon(String name, CorazonItem.Efecto efecto) {
      return (Item)Registry.register(BuiltInRegistries.ITEM, com.hexagonsafio2.compat.Ids.item(id(name)), new CorazonItem(new net.minecraft.world.item.Item.Properties().stacksTo(16), efecto));
   }

   public void onInitialize() {
      ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
         if (entity instanceof AbstractArrow flecha && flecha.getOwner() instanceof LivingEntity tirador) {
            ItemStack arco = tirador.getUseItem();
            if (BuiltInRegistries.ITEM.getKey(arco.getItem()).equals(ARCO_ID)) {
               flecha.setBaseDamage(com.hexagonsafio2.compat.Compat.danoBase(flecha) + 2.25);
            }
         }
      });
      UseBlockCallback.EVENT.register((UseBlockCallback)(player, level, hand, hit) -> {
         ItemStack stack = player.getItemInHand(hand);
         if (!BuiltInRegistries.ITEM.getKey(stack.getItem()).equals(NAVE_ID)) {
            return InteractionResult.PASS;
         }

         boolean sobrePlataforma = hit.getDirection() == Direction.UP && level.getBlockState(hit.getBlockPos()).is(PLATAFORMA_NAVE);
         if (sobrePlataforma) {
            return InteractionResult.PASS;
         }

         if (!level.isClientSide()) {
            com.hexagonsafio2.compat.Compat.mensaje(player, Component.translatable("message.hexagonsafio2.necesita_plataforma"), true);
         }

         return InteractionResult.FAIL;
      });
      Registry.register(
         BuiltInRegistries.CREATIVE_MODE_TAB,
         id("nave"),
         FabricCreativeModeTab.builder()
            .title(Component.translatable("itemGroup.hexagonsafio2.nave"))
            .icon(() -> new ItemStack(MOTOR_NAVE))
            .displayItems((params, out) -> {
               out.accept(PLATAFORMA_NAVE_ITEM);
               out.accept(DILITIO);
               out.accept(AMBAR_FOSIL);
               out.accept(BIOXITA);
               out.accept(FRASCO_DILITIO);
               out.accept(CABINA_NAVE);
               out.accept(ALA_IZQUIERDA_NAVE);
               out.accept(ALA_DERECHA_NAVE);
               out.accept(FUSELAJE_NAVE);
               out.accept(MOTOR_NAVE);
               out.accept(MOTOR_MEJORADO_NAVE);
               out.accept(CORAZON);
               out.accept(CORAZON_LIMBO);
               out.accept(CORAZON_AMARILLO);
               Item nave = (Item)BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath("ghostsafio", "nave"));
               if (nave != Items.AIR) {
                  out.accept(nave);
               }
            })
            .build()
      );
   }
}
