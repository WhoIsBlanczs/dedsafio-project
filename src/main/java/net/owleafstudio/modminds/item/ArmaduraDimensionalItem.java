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
import net.owleafstudio.modminds.client.model.Modelspacearmorfix;

public abstract class ArmaduraDimensionalItem extends ArmorItem {
   private static final Holder<ArmorMaterial> MATERIAL = Holder.direct(
      new ArmorMaterial(
         Map.of(net.minecraft.world.item.equipment.ArmorType.HELMET, 5, net.minecraft.world.item.equipment.ArmorType.CHESTPLATE, 10, net.minecraft.world.item.equipment.ArmorType.LEGGINGS, 8, net.minecraft.world.item.equipment.ArmorType.BOOTS, 5),
         15,
         Holder.direct(SoundEvents.EMPTY),
         () -> Ingredient.of(),
         List.of(),
         3.5F,
         0.1F
      )
   );

   public ArmaduraDimensionalItem(net.minecraft.world.item.equipment.ArmorType type, Item.Properties properties) {
      super(MATERIAL, type, properties.durability(type.getDurability(45)));
   }

   public static class Boots extends ArmaduraDimensionalItem {
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
                           (new Modelspacearmorfix(Minecraft.getInstance().getEntityModels().bakeLayer(Modelspacearmorfix.LAYER_LOCATION))).BootLeft,
                           "right_leg",
                           (new Modelspacearmorfix(Minecraft.getInstance().getEntityModels().bakeLayer(Modelspacearmorfix.LAYER_LOCATION))).BootRight,
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
         return "modminds:textures/entities/spacearmorfix.png";
      }
   }

   public static class Chestplate extends ArmaduraDimensionalItem {
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
                           (new Modelspacearmorfix(Minecraft.getInstance().getEntityModels().bakeLayer(Modelspacearmorfix.LAYER_LOCATION))).Body,
                           "left_arm",
                           (new Modelspacearmorfix(Minecraft.getInstance().getEntityModels().bakeLayer(Modelspacearmorfix.LAYER_LOCATION))).LeftArm,
                           "right_arm",
                           (new Modelspacearmorfix(Minecraft.getInstance().getEntityModels().bakeLayer(Modelspacearmorfix.LAYER_LOCATION))).RightArm,
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
         return "modminds:textures/entities/spacearmorfix.png";
      }
   }

   public static class Helmet extends ArmaduraDimensionalItem {
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
                           (new Modelspacearmorfix(Minecraft.getInstance().getEntityModels().bakeLayer(Modelspacearmorfix.LAYER_LOCATION))).Head,
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
         return "modminds:textures/entities/spacearmorfix.png";
      }
   }

   public static class Leggings extends ArmaduraDimensionalItem {
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
                           (new Modelspacearmorfix(Minecraft.getInstance().getEntityModels().bakeLayer(Modelspacearmorfix.LAYER_LOCATION))).LeftLeg,
                           "right_leg",
                           (new Modelspacearmorfix(Minecraft.getInstance().getEntityModels().bakeLayer(Modelspacearmorfix.LAYER_LOCATION))).RightLeg,
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
         return "modminds:textures/entities/spacearmorfix.png";
      }
   }
}
