package net.owleafstudio.modminds.item;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.owleafstudio.modminds.client.model.ModelArmadura_Infernal;
import net.owleafstudio.modminds.init.ModmindsModItems;

public abstract class ArmaduraInfernalItem extends ArmorItem {
   private static final Holder<ArmorMaterial> MATERIAL = Holder.direct(
      new ArmorMaterial(
         Map.of(net.minecraft.world.item.equipment.ArmorType.HELMET, 4, net.minecraft.world.item.equipment.ArmorType.CHESTPLATE, 6, net.minecraft.world.item.equipment.ArmorType.LEGGINGS, 7, net.minecraft.world.item.equipment.ArmorType.BOOTS, 4),
         15,
         SoundEvents.ARMOR_EQUIP_GENERIC,
         () -> Ingredient.of(new ItemStack[]{new ItemStack((ItemLike)ModmindsModItems.LINGOTE_DE_ACERO_NEGRO_AUREO.get())}),
         List.of(),
         3.0F,
         0.1F
      )
   );

   public ArmaduraInfernalItem(net.minecraft.world.item.equipment.ArmorType type, Item.Properties properties) {
      super(MATERIAL, type, properties.durability(type.getDurability(37)));
   }

   public static class Boots extends ArmaduraInfernalItem {
      public Boots() {
         super(net.minecraft.world.item.equipment.ArmorType.BOOTS, new Item.Properties().fireResistant());
      }

      public void initializeClient(Consumer<IClientItemExtensions> consumer) {
         consumer.accept(
            new IClientItemExtensions() {
               @OnlyIn(Dist.CLIENT)
               @Override
               public HumanoidModel getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel defaultModel) {
                  HumanoidModel armorModel = new HumanoidModel(
                     new ModelPart(
                        Collections.emptyList(),
                        Map.of(
                           "left_leg",
                           (new ModelArmadura_Infernal(Minecraft.getInstance().getEntityModels().bakeLayer(ModelArmadura_Infernal.LAYER_LOCATION))).LeftBoot,
                           "right_leg",
                           (new ModelArmadura_Infernal(Minecraft.getInstance().getEntityModels().bakeLayer(ModelArmadura_Infernal.LAYER_LOCATION))).RightBoot,
                           "head",
                           new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                           "hat",
                           new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                           "body",
                           new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                           "right_arm",
                           new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                           "left_arm",
                           new ModelPart(Collections.emptyList(), Collections.emptyMap())
                        )
                     )
                  );
                  armorModel.crouching = living.isShiftKeyDown();
                  armorModel.riding = defaultModel.riding;
                  armorModel.young = living.isBaby();
                  return armorModel;
               }
            }
         );
      }

      public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
         return "modminds:textures/entities/casco.png";
      }
   }

   public static class Chestplate extends ArmaduraInfernalItem {
      public Chestplate() {
         super(net.minecraft.world.item.equipment.ArmorType.CHESTPLATE, new Item.Properties().fireResistant());
      }

      public void initializeClient(Consumer<IClientItemExtensions> consumer) {
         consumer.accept(
            new IClientItemExtensions() {
               @OnlyIn(Dist.CLIENT)
               @Override
               public HumanoidModel getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel defaultModel) {
                  HumanoidModel armorModel = new HumanoidModel(
                     new ModelPart(
                        Collections.emptyList(),
                        Map.of(
                           "body",
                           (new ModelArmadura_Infernal(Minecraft.getInstance().getEntityModels().bakeLayer(ModelArmadura_Infernal.LAYER_LOCATION))).Body,
                           "left_arm",
                           (new ModelArmadura_Infernal(Minecraft.getInstance().getEntityModels().bakeLayer(ModelArmadura_Infernal.LAYER_LOCATION))).LeftArm,
                           "right_arm",
                           (new ModelArmadura_Infernal(Minecraft.getInstance().getEntityModels().bakeLayer(ModelArmadura_Infernal.LAYER_LOCATION))).RightArm,
                           "head",
                           new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                           "hat",
                           new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                           "right_leg",
                           new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                           "left_leg",
                           new ModelPart(Collections.emptyList(), Collections.emptyMap())
                        )
                     )
                  );
                  armorModel.crouching = living.isShiftKeyDown();
                  armorModel.riding = defaultModel.riding;
                  armorModel.young = living.isBaby();
                  return armorModel;
               }
            }
         );
      }

      public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
         return "modminds:textures/entities/casco.png";
      }
   }

   public static class Helmet extends ArmaduraInfernalItem {
      public Helmet() {
         super(net.minecraft.world.item.equipment.ArmorType.HELMET, new Item.Properties().fireResistant());
      }

      public void initializeClient(Consumer<IClientItemExtensions> consumer) {
         consumer.accept(
            new IClientItemExtensions() {
               @Override
               public HumanoidModel getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel defaultModel) {
                  HumanoidModel armorModel = new HumanoidModel(
                     new ModelPart(
                        Collections.emptyList(),
                        Map.of(
                           "head",
                           (new ModelArmadura_Infernal(Minecraft.getInstance().getEntityModels().bakeLayer(ModelArmadura_Infernal.LAYER_LOCATION))).Head,
                           "hat",
                           new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                           "body",
                           new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                           "right_arm",
                           new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                           "left_arm",
                           new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                           "right_leg",
                           new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                           "left_leg",
                           new ModelPart(Collections.emptyList(), Collections.emptyMap())
                        )
                     )
                  );
                  armorModel.crouching = living.isShiftKeyDown();
                  armorModel.riding = defaultModel.riding;
                  armorModel.young = living.isBaby();
                  return armorModel;
               }
            }
         );
      }

      public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
         return "modminds:textures/entities/casco.png";
      }
   }

   public static class Leggings extends ArmaduraInfernalItem {
      public Leggings() {
         super(net.minecraft.world.item.equipment.ArmorType.LEGGINGS, new Item.Properties().fireResistant());
      }

      public void initializeClient(Consumer<IClientItemExtensions> consumer) {
         consumer.accept(
            new IClientItemExtensions() {
               @OnlyIn(Dist.CLIENT)
               @Override
               public HumanoidModel getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel defaultModel) {
                  HumanoidModel armorModel = new HumanoidModel(
                     new ModelPart(
                        Collections.emptyList(),
                        Map.of(
                           "left_leg",
                           (new ModelArmadura_Infernal(Minecraft.getInstance().getEntityModels().bakeLayer(ModelArmadura_Infernal.LAYER_LOCATION))).LeftLeg,
                           "right_leg",
                           (new ModelArmadura_Infernal(Minecraft.getInstance().getEntityModels().bakeLayer(ModelArmadura_Infernal.LAYER_LOCATION))).RightLeg,
                           "head",
                           new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                           "hat",
                           new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                           "body",
                           new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                           "right_arm",
                           new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                           "left_arm",
                           new ModelPart(Collections.emptyList(), Collections.emptyMap())
                        )
                     )
                  );
                  armorModel.crouching = living.isShiftKeyDown();
                  armorModel.riding = defaultModel.riding;
                  armorModel.young = living.isBaby();
                  return armorModel;
               }
            }
         );
      }

      public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
         return "modminds:textures/entities/casco.png";
      }
   }
}
