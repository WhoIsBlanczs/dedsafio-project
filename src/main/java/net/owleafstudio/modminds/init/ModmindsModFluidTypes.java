package net.owleafstudio.modminds.init;

import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.owleafstudio.modminds.fluid.types.AguaDelLimboFluidType;

public class ModmindsModFluidTypes {
   public static final DeferredRegister<FluidType> REGISTRY = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, "modminds");
   public static final RegistryObject<FluidType> AGUA_DEL_LIMBO_TYPE = REGISTRY.register("agua_del_limbo", () -> new AguaDelLimboFluidType());
}
