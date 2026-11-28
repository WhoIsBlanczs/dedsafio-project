package net.minecraftforge.common.util;

import java.util.function.Function;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;

public interface ITeleporter {
   Entity placeEntity(Entity var1, ServerLevel var2, ServerLevel var3, float var4, Function<Boolean, Entity> var5);
}
