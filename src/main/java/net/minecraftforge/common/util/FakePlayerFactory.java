package net.minecraftforge.common.util;

import com.mojang.authlib.GameProfile;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public final class FakePlayerFactory {
   private static final GameProfile MINECRAFT_PROFILE = new GameProfile(UUID.fromString("8b9a5d24-2b71-4a4b-9b1c-0f3b2a6d1e5f"), "[minecraft]");
   private static final Map<ServerLevel, FakePlayer> CACHE = Collections.synchronizedMap(new WeakHashMap<>());

   private FakePlayerFactory() {
   }

   public static synchronized FakePlayer getMinecraft(ServerLevel level) {
      return CACHE.computeIfAbsent(level, l -> new FakePlayer(l, BlockPos.ZERO, MINECRAFT_PROFILE));
   }

   public static FakePlayer get(ServerLevel level, GameProfile profile) {
      return new FakePlayer(level, BlockPos.ZERO, profile);
   }
}
