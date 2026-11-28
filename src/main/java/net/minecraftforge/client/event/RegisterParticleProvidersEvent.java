package net.minecraftforge.client.event;

import java.util.function.Function;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraftforge.eventbus.api.Event;

public class RegisterParticleProvidersEvent extends Event {
   public <T extends ParticleOptions> void registerSpriteSet(ParticleType<T> type, Function<SpriteSet, ParticleProvider<T>> factory) {
      ParticleProviderRegistry.getInstance().register(type, sprites -> factory.apply(sprites));
   }

   public <T extends ParticleOptions> void register(ParticleType<T> type, ParticleProvider<T> provider) {
      ParticleProviderRegistry.getInstance().register(type, provider);
   }
}
