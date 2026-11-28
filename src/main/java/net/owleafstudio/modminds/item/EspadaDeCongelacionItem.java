package net.owleafstudio.modminds.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.owleafstudio.modminds.init.ModmindsModItems;

public class EspadaDeCongelacionItem extends SwordItem {
   private static final Tier TIER = new Tier() {
      public int getUses() {
         return 2031;
      }

      public float getSpeed() {
         return 0.0F;
      }

      public float getAttackDamageBonus() {
         return 4.0F;
      }

      public TagKey<Block> getIncorrectBlocksForDrops() {
         return BlockTags.INCORRECT_FOR_DIAMOND_TOOL;
      }

      public int getEnchantmentValue() {
         return 15;
      }

      public Ingredient getRepairIngredient() {
         return Ingredient.of();
      }
   };

   public EspadaDeCongelacionItem() {
      super(TIER, new Item.Properties().attributes(createAttributes(TIER, 3, -3.0F)));
   }

   public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
      if (attacker.getMainHandItem().getItem() == ModmindsModItems.ESPADA_DE_CONGELACION.get() && !attacker.level().isClientSide()) {
         target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 10, 10, true, false));
         target.clearFire();
         target.setTicksFrozen(60);
      }

      return super.hurtEnemy(stack, target, attacker);
   }
}
