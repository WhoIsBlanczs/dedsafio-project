package net.owleafstudio.modminds.compat;

import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

/**
 * Sustituto de ArmorItem (eliminado en 26.x). Las armaduras de 26.3 son ítems normales con
 * el componente "equippable" + atributos, que es lo que pone Item.Properties#humanoidArmor.
 */
public class LegacyArmorItem extends Item {
   private final ArmorMaterial material;
   private final ArmorType type;

   public LegacyArmorItem(ArmorMaterial material, ArmorType type, Item.Properties properties) {
      super(properties.humanoidArmor(material, type));
      this.material = material;
      this.type = type;
   }

   public ArmorMaterial getMaterial() {
      return this.material;
   }

   public ArmorType getType() {
      return this.type;
   }

   /** Material con reparación por tag "modminds:repairs/<nombre>" y asset "modminds:<nombre>". */
   @SuppressWarnings("unchecked")
   public static ArmorMaterial material(
      String name, int durability, Map<ArmorType, Integer> defense, int enchantment, Holder<SoundEvent> sound, float toughness, float knockback
   ) {
      TagKey<Item> repair = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("modminds", "repairs/" + name));
      ResourceKey<EquipmentAsset> asset = ResourceKey.create(
         (ResourceKey<? extends net.minecraft.core.Registry<EquipmentAsset>>)EquipmentAssets.ROOT_ID, Identifier.fromNamespaceAndPath("modminds", name)
      );
      return new ArmorMaterial(durability, defense, enchantment, sound, toughness, knockback, repair, asset);
   }
}
