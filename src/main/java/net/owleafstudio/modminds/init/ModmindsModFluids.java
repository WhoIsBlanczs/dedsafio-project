package net.owleafstudio.modminds.init;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.owleafstudio.modminds.fluid.AguaDelLimboFluid;

public class ModmindsModFluids {
   public static final DeferredRegister<Fluid> REGISTRY = DeferredRegister.create(ForgeRegistries.FLUIDS, "modminds");
   public static final RegistryObject<FlowingFluid> AGUA_DEL_LIMBO = REGISTRY.register("agua_del_limbo", () -> new AguaDelLimboFluid.Source());
   public static final RegistryObject<FlowingFluid> FLOWING_AGUA_DEL_LIMBO = REGISTRY.register("flowing_agua_del_limbo", () -> new AguaDelLimboFluid.Flowing());

   @Mod.EventBusSubscriber(
      bus = Mod.EventBusSubscriber.Bus.MOD,
      value = {Dist.CLIENT}
   )
   public static class FluidsClientSideHandler {
      @SubscribeEvent
      public static void clientSetup(FMLClientSetupEvent event) {
      }
   }
}
