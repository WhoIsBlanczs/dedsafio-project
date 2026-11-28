package com.example.dedsafioimitationmod.item;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ToolMaterial;

public final class ModToolMaterial {
   // Durabilidad 2031, velocidad 9, daño extra 5, encantabilidad 15, se repara con netherita.
   public static final ToolMaterial QUMARA = new ToolMaterial(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 2031, 9.0F, 5.0F, 15, ItemTags.NETHERITE_TOOL_MATERIALS);

   private ModToolMaterial() {
   }
}
