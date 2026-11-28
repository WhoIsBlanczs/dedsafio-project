package net.minecraftforge.client.extensions.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.LocalPlayer;

public interface IClientItemExtensions {
   Map<Item, IClientItemExtensions> EXTENSIONS = new ConcurrentHashMap<>();

   static IClientItemExtensions of(ItemStack stack) {
      return stack == null ? null : EXTENSIONS.get(stack.getItem());
   }

   default HumanoidModel getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel defaultModel) {
      return null;
   }

   default boolean applyForgeHandTransform(
      PoseStack poseStack, LocalPlayer player, HumanoidArm arm, ItemStack itemInHand, float partialTick, float equipProcess, float swingProcess
   ) {
      return false;
   }

   default boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
      return true;
   }
}
