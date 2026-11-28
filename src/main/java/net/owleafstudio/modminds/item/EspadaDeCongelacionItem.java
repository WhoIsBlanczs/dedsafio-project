package net.owleafstudio.modminds.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.owleafstudio.modminds.init.ModmindsModItems;

public class EspadaDeCongelacionItem extends Item {
   private static final net.minecraft.world.item.ToolMaterial TIER = new net.minecraft.world.item.ToolMaterial(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 2031, 0.0F, 4.0F, 15, net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.Identifier.fromNamespaceAndPath("modminds", "sin_reparacion")));

   public EspadaDeCongelacionItem() {
      super(new Item.Properties().sword(TIER, 3, -3.0F));
   }

   public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
      if (attacker.getMainHandItem().getItem() == ModmindsModItems.ESPADA_DE_CONGELACION.get() && !attacker.level().isClientSide()) {
         target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 10, 10, true, false));
         target.clearFire();
         target.setTicksFrozen(60);
      }

      super.hurtEnemy(stack, target, attacker);
   }
}
