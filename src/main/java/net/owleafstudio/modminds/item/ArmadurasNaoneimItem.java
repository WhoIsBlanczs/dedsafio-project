package net.owleafstudio.modminds.item;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.owleafstudio.modminds.client.model.ModelArmadura_de_Dilitio;

public abstract class ArmadurasNaoneimItem extends net.owleafstudio.modminds.compat.LegacyArmorItem {
   private static final ArmorMaterial MATERIAL = net.owleafstudio.modminds.compat.LegacyArmorItem.material(
      "armaduras_naoneim", 37, Map.of(net.minecraft.world.item.equipment.ArmorType.HELMET, 4, net.minecraft.world.item.equipment.ArmorType.CHESTPLATE, 9, net.minecraft.world.item.equipment.ArmorType.LEGGINGS, 7, net.minecraft.world.item.equipment.ArmorType.BOOTS, 4), 15, SoundEvents.ARMOR_EQUIP_GENERIC, 3.0F, 0.1F
   );

   public ArmadurasNaoneimItem(net.minecraft.world.item.equipment.ArmorType type, Item.Properties properties) {
      super(MATERIAL, type, properties);
   }

   public static class Boots extends ArmadurasNaoneimItem {
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
                           (new ModelArmadura_de_Dilitio(Minecraft.getInstance().getEntityModels().bakeLayer(ModelArmadura_de_Dilitio.LAYER_LOCATION))).LeftBoot,
                           "right_leg",
                           (new ModelArmadura_de_Dilitio(Minecraft.getInstance().getEntityModels().bakeLayer(ModelArmadura_de_Dilitio.LAYER_LOCATION))).RightBoot,
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
                  return armorModel;
               }
            }
         );
      }

      public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
         return "modminds:textures/entities/armadura_de_dilitio.png";
      }
   }

   public static class Chestplate extends ArmadurasNaoneimItem {
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
                           (new ModelArmadura_de_Dilitio(Minecraft.getInstance().getEntityModels().bakeLayer(ModelArmadura_de_Dilitio.LAYER_LOCATION))).Body,
                           "left_arm",
                           (new ModelArmadura_de_Dilitio(Minecraft.getInstance().getEntityModels().bakeLayer(ModelArmadura_de_Dilitio.LAYER_LOCATION))).LeftArm,
                           "right_arm",
                           (new ModelArmadura_de_Dilitio(Minecraft.getInstance().getEntityModels().bakeLayer(ModelArmadura_de_Dilitio.LAYER_LOCATION))).RightArm,
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
                  return armorModel;
               }
            }
         );
      }

      public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
         return "modminds:textures/entities/armadura_de_dilitio.png";
      }
   }

   public static class Helmet extends ArmadurasNaoneimItem {
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
                           (new ModelArmadura_de_Dilitio(Minecraft.getInstance().getEntityModels().bakeLayer(ModelArmadura_de_Dilitio.LAYER_LOCATION))).Head,
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
                  return armorModel;
               }
            }
         );
      }

      public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
         return "modminds:textures/entities/armadura_de_dilitio.png";
      }
   }

   public static class Leggings extends ArmadurasNaoneimItem {
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
                           (new ModelArmadura_de_Dilitio(Minecraft.getInstance().getEntityModels().bakeLayer(ModelArmadura_de_Dilitio.LAYER_LOCATION))).LeftLeg,
                           "right_leg",
                           (new ModelArmadura_de_Dilitio(Minecraft.getInstance().getEntityModels().bakeLayer(ModelArmadura_de_Dilitio.LAYER_LOCATION))).RightLeg,
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
                  return armorModel;
               }
            }
         );
      }

      public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
         return "modminds:textures/entities/armadura_de_dilitio.png";
      }
   }
}
