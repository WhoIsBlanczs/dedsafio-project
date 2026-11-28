package net.minecraftforge.network;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.resources.Identifier;
import net.minecraftforge.network.simple.SimpleChannel;

public final class NetworkRegistry {
   private static final List<SimpleChannel> CHANNELS = new CopyOnWriteArrayList<>();

   private NetworkRegistry() {
   }

   public static SimpleChannel newSimpleChannel(
      Identifier name, Supplier<String> versionCheck, Predicate<String> acceptedVersions, Predicate<String> generatedVersions
   ) {
      SimpleChannel channel = new SimpleChannel(name);
      CHANNELS.add(channel);
      return channel;
   }

   public static List<SimpleChannel> getChannels() {
      return CHANNELS;
   }
}
