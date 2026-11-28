package com.example.dedsafioimitationmod.item;

import com.example.dedsafioimitationmod.mixin.PersistentProjectileEntityInvoker;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.Item;

public class ArcoGlebanoideItem extends BowItem {
   public ArcoGlebanoideItem(Item.Properties settings) {
      super(settings);
   }

   protected Projectile createProjectile(Level world, LivingEntity shooter, ItemStack weaponStack, ItemStack projectileStack, boolean critical) {
      Projectile projectile = super.createProjectile(world, shooter, weaponStack, projectileStack, critical);
      if (projectile instanceof AbstractArrow arrow) {
         ((PersistentProjectileEntityInvoker)arrow).dedsafio$setPierceLevel((byte)2);
      }

      return projectile;
   }
}
