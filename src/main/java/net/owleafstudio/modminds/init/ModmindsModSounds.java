package net.owleafstudio.modminds.init;

import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModmindsModSounds {
   public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, "modminds");
   public static final RegistryObject<SoundEvent> ARMA_CORTA = REGISTRY.register(
      "arma_corta", () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("modminds", "arma_corta"))
   );
   public static final RegistryObject<SoundEvent> NAVEDISPARO = REGISTRY.register(
      "navedisparo", () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("modminds", "navedisparo"))
   );
   public static final RegistryObject<SoundEvent> HAMMER_1 = REGISTRY.register(
      "hammer_1", () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("modminds", "hammer_1"))
   );
   public static final RegistryObject<SoundEvent> HAMMER_2 = REGISTRY.register(
      "hammer_2", () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("modminds", "hammer_2"))
   );
   public static final RegistryObject<SoundEvent> HAMMER_3 = REGISTRY.register(
      "hammer_3", () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("modminds", "hammer_3"))
   );
   public static final RegistryObject<SoundEvent> FINISH = REGISTRY.register(
      "finish", () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("modminds", "finish"))
   );
   public static final RegistryObject<SoundEvent> ENTRADOALLIMBO = REGISTRY.register(
      "entradoallimbo", () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("modminds", "entradoallimbo"))
   );
   public static final RegistryObject<SoundEvent> BLOQUEADOR = REGISTRY.register(
      "bloqueador", () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("modminds", "bloqueador"))
   );
   public static final RegistryObject<SoundEvent> GHOSTHIT = REGISTRY.register(
      "ghosthit", () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("modminds", "ghosthit"))
   );
   public static final RegistryObject<SoundEvent> REVIIL_LINE_1 = REGISTRY.register(
      "reviil_line_1", () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("modminds", "reviil_line_1"))
   );
   public static final RegistryObject<SoundEvent> REVIIL_LINE_2 = REGISTRY.register(
      "reviil_line_2", () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("modminds", "reviil_line_2"))
   );
   public static final RegistryObject<SoundEvent> REVIIL_LINE_3 = REGISTRY.register(
      "reviil_line_3", () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("modminds", "reviil_line_3"))
   );
   public static final RegistryObject<SoundEvent> REVIIL_LINE_4 = REGISTRY.register(
      "reviil_line_4", () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("modminds", "reviil_line_4"))
   );
   public static final RegistryObject<SoundEvent> REVIIL_LINE_5 = REGISTRY.register(
      "reviil_line_5", () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("modminds", "reviil_line_5"))
   );
   public static final RegistryObject<SoundEvent> REVIIL_LINE_6 = REGISTRY.register(
      "reviil_line_6", () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("modminds", "reviil_line_6"))
   );
   public static final RegistryObject<SoundEvent> REVIIL_LINE_7 = REGISTRY.register(
      "reviil_line_7", () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("modminds", "reviil_line_7"))
   );
   public static final RegistryObject<SoundEvent> REVIIL_LINE_8 = REGISTRY.register(
      "reviil_line_8", () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("modminds", "reviil_line_8"))
   );
   public static final RegistryObject<SoundEvent> REVIIL_LINE_9 = REGISTRY.register(
      "reviil_line_9", () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("modminds", "reviil_line_9"))
   );
   public static final RegistryObject<SoundEvent> REVIIL_BURLA_1 = REGISTRY.register(
      "reviil_burla_1", () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("modminds", "reviil_burla_1"))
   );
   public static final RegistryObject<SoundEvent> REVIIL_BURLA_2 = REGISTRY.register(
      "reviil_burla_2", () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("modminds", "reviil_burla_2"))
   );
   public static final RegistryObject<SoundEvent> REVIIL_BURLA_3 = REGISTRY.register(
      "reviil_burla_3", () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("modminds", "reviil_burla_3"))
   );
   public static final RegistryObject<SoundEvent> REVIIL_BURLA_4 = REGISTRY.register(
      "reviil_burla_4", () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("modminds", "reviil_burla_4"))
   );
   public static final RegistryObject<SoundEvent> REVIIL_BURLA_5 = REGISTRY.register(
      "reviil_burla_5", () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("modminds", "reviil_burla_5"))
   );
   public static final RegistryObject<SoundEvent> REVIIL_BURLA_6 = REGISTRY.register(
      "reviil_burla_6", () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("modminds", "reviil_burla_6"))
   );
   public static final RegistryObject<SoundEvent> REVIIL_BURLA_7 = REGISTRY.register(
      "reviil_burla_7", () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("modminds", "reviil_burla_7"))
   );
   public static final RegistryObject<SoundEvent> TIKI_LINE_1 = REGISTRY.register(
      "tiki_line_1", () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("modminds", "tiki_line_1"))
   );
   public static final RegistryObject<SoundEvent> TIKI_LINE_2 = REGISTRY.register(
      "tiki_line_2", () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("modminds", "tiki_line_2"))
   );
   public static final RegistryObject<SoundEvent> APLAUSO = REGISTRY.register(
      "aplauso", () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("modminds", "aplauso"))
   );
   public static final RegistryObject<SoundEvent> ZUMBIDO = REGISTRY.register(
      "zumbido", () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("modminds", "zumbido"))
   );
}
