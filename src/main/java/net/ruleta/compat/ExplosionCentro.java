package net.ruleta.compat;

import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.Vec3;

public interface ExplosionCentro {
   Vec3 ruleta$centro();

   static Vec3 de(Explosion explosion) {
      return ((ExplosionCentro)explosion).ruleta$centro();
   }
}
