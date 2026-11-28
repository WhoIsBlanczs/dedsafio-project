package net.owleafstudio.modminds.item;

import java.util.List;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.init.ModmindsModEnchantments;

public class EspadaInfernalCurativaItem extends SwordItem {
   private static final int FIRE_ASPECT_LEVEL = 2;
   private static final int ASPECTO_DE_SANGUIJUELA_LEVEL = 2;

   public EspadaInfernalCurativaItem() {
      super(new EspadaInfernalCurativaItem.CustomTier(), new Item.Properties().stacksTo(1).fireResistant());
   }

   public Component getName(ItemStack stack) {
      return Component.literal("§4§lEspada Infernal Curativa");
   }

   public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext level, net.minecraft.world.item.component.TooltipDisplay tooltipDisplay$, java.util.function.Consumer<Component> tooltipAdder$, TooltipFlag flag) {
      java.util.List<Component> tooltip = new java.util.ArrayList<>();
      this.appendHoverText$viejo(stack, level, tooltipDisplay$, tooltip, flag);
      tooltip.forEach(tooltipAdder$);
   }

   private void appendHoverText$viejo(ItemStack stack, net.minecraft.world.item.Item.TooltipContext level, net.minecraft.world.item.component.TooltipDisplay tooltipDisplay$, List<Component> tooltip, TooltipFlag flag) {
      if (!stack.isEnchanted()) {
         tooltip.add(Component.literal("☀ Obtienes una probabilidad de curarte al golpear ☀").withStyle(style -> style.withColor(16734003)));
      }
   }

   public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
      if (!this.hasDefaultEnchantments(stack)) {
         this.addDefaultEnchantments(stack);
      }
   }

   public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
      this.applyFreezingEffect(target);
      return super.hurtEnemy(stack, target, attacker);
   }

   private boolean hasDefaultEnchantments(ItemStack stack) {
      return CompatAccess.enchantmentLevel(stack, Enchantments.FIRE_ASPECT) >= 2
         && CompatAccess.enchantmentLevel(stack, ModmindsModEnchantments.ASPECTO_DE_SANGUIJUELA) >= 2;
   }

   private void addDefaultEnchantments(ItemStack stack) {
      stack.enchant(CompatAccess.enchantment(Enchantments.FIRE_ASPECT), 2);
      stack.enchant(CompatAccess.enchantment(ModmindsModEnchantments.ASPECTO_DE_SANGUIJUELA), 2);
   }

   private void applyFreezingEffect(LivingEntity target) {
      if (!target.level().isClientSide()) {
         target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 10, 3, true, false));
         target.clearFire();
         target.setTicksFrozen(60);
      }
   }

   private static class CustomTier implements Tier {
      public int getUses() {
         return 2031;
      }

      public float getSpeed() {
         return 1.0F;
      }

      public float getAttackDamageBonus() {
         return 5.0F;
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
   }
}
