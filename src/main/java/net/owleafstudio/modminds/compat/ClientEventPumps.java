package net.owleafstudio.modminds.compat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.StartTick;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.resources.Identifier;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;
import net.minecraftforge.eventbus.api.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public final class ClientEventPumps {
   private static final Logger LOGGER = LoggerFactory.getLogger("modminds-compat");
   private static final Map<Class<?>, Optional<Method>> INITIALIZE_CLIENT = new ConcurrentHashMap<>();
   private static final Map<Item, Optional<Method>> ARMOR_TEXTURE = new ConcurrentHashMap<>();
   private static final Map<Class<?>, Boolean> ARMOR_MODEL_OVERRIDES = new ConcurrentHashMap<>();
   private static Field visibleField;
   private static Field showIconField;
   private static boolean effectFieldsResolved;
   private static boolean installed;

   private ClientEventPumps() {
   }

   public static void installClient() {
      if (!installed) {
         installed = true;
         ClientTickEvents.START_CLIENT_TICK.register((StartTick)client -> {
            if (client.level != null) {
               CompatAccess.bindRegistries(client.level.registryAccess(), false);
            }
         });
         fireEvent("RegisterKeyMappingsEvent", RegisterKeyMappingsEvent::new);
         fireEvent("EntityRenderersEvent.RegisterLayerDefinitions", EntityRenderersEvent.RegisterLayerDefinitions::new);
         fireEvent("EntityRenderersEvent.RegisterRenderers", EntityRenderersEvent.RegisterRenderers::new);
         fireEvent("RegisterParticleProvidersEvent", RegisterParticleProvidersEvent::new);
         fireEvent("RegisterDimensionSpecialEffectsEvent", RegisterDimensionSpecialEffectsEvent::new);
         runStep("client item extensions", ClientEventPumps::installItemExtensions);
         runStep("client mob effect extensions", ClientEventPumps::installMobEffectExtensions);
         runStep("hud overlays", ClientEventPumps::installHudOverlays);
      }
   }

   private static void fireEvent(String label, Supplier<? extends Event> factory) {
      try {
         ForgeEventBus.postToBoth(factory.get());
      } catch (Throwable var3) {
         LOGGER.error("Client event {} failed", label, var3);
      }
   }

   private static void runStep(String label, Runnable work) {
      try {
         work.run();
      } catch (Throwable var3) {
         LOGGER.error("Client compat step {} failed", label, var3);
      }
   }

   private static void installItemExtensions() {
      for (Item item : BuiltInRegistries.ITEM.stream().toList()) {
         IClientItemExtensions extensions = collectInitializeClient(item, IClientItemExtensions.class);
         if (extensions != null) {
            IClientItemExtensions.EXTENSIONS.put(item, extensions);
            BlockEntityWithoutLevelRenderer customRenderer = extensions.getCustomRenderer();
            if (customRenderer != null) {
               BuiltinItemRendererRegistry.INSTANCE.register(item, (stack, mode, poseStack, buffers, light, overlay) -> {
                  poseStack.pushPose();
                  applyHandTransform(extensions, mode, stack, poseStack);
                  customRenderer.renderByItem(stack, mode, poseStack, buffers, light, overlay);
                  poseStack.popPose();
               });
            }

            if (item instanceof ArmorItem && overridesArmorModel(extensions.getClass())) {
               registerArmorRenderer(item, extensions);
            }
         }
      }
   }

   private static void applyHandTransform(IClientItemExtensions extensions, ItemDisplayContext mode, ItemStack stack, PoseStack poseStack) {
      boolean leftHand = mode == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || mode == ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
      boolean rightHand = mode == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || mode == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
      LocalPlayer player = Minecraft.getInstance().player;
      if (player != null && (leftHand || rightHand)) {
         extensions.applyForgeHandTransform(poseStack, player, leftHand ? player.getMainArm().getOpposite() : player.getMainArm(), stack, 0.0F, 0.0F, 0.0F);
      }
   }

   private static boolean overridesArmorModel(Class<?> extensionsType) {
      return ARMOR_MODEL_OVERRIDES.computeIfAbsent(extensionsType, type -> {
         try {
            type.getDeclaredMethod("getHumanoidArmorModel", LivingEntity.class, ItemStack.class, EquipmentSlot.class, HumanoidModel.class);
            return true;
         } catch (NoSuchMethodException var2) {
            return false;
         }
      });
   }

   private static void registerArmorRenderer(Item item, IClientItemExtensions extensions) {
      ArmorRenderer.register((poseStack, buffers, stack, entity, slot, light, contextModel) -> {
         Identifier texture = armorTexture(item, stack, entity, slot);
         if (texture != null) {
            HumanoidModel armorModel = extensions.getHumanoidArmorModel(entity, stack, slot, contextModel);
            if (armorModel != null) {
               contextModel.copyPropertiesTo(armorModel);
               setPartVisibility(armorModel, slot);
               VertexConsumer buffer = ItemRenderer.getArmorFoilBuffer(buffers, net.minecraft.client.renderer.rendertype.RenderTypes.armorCutoutNoCull(texture), stack.hasFoil());
               armorModel.renderToBuffer(poseStack, buffer, light, OverlayTexture.NO_OVERLAY, -1);
            }
         }
      }, new ItemLike[]{item});
   }

   private static void setPartVisibility(HumanoidModel model, EquipmentSlot slot) {
      model.setAllVisible(false);
      switch (slot) {
         case HEAD:
            model.head.visible = true;
            model.hat.visible = true;
            break;
         case CHEST:
            model.body.visible = true;
            model.rightArm.visible = true;
            model.leftArm.visible = true;
            break;
         case LEGS:
            model.body.visible = true;
            model.rightLeg.visible = true;
            model.leftLeg.visible = true;
            break;
         case FEET:
            model.rightLeg.visible = true;
            model.leftLeg.visible = true;
      }
   }

   private static Identifier armorTexture(Item item, ItemStack stack, LivingEntity entity, EquipmentSlot slot) {
      Optional<Method> legacy = ARMOR_TEXTURE.computeIfAbsent(item, key -> {
         try {
            return Optional.of(key.getClass().getMethod("getArmorTexture", ItemStack.class, Entity.class, EquipmentSlot.class, String.class));
         } catch (NoSuchMethodException var2) {
            return Optional.empty();
         }
      });
      if (legacy.isPresent()) {
         try {
            if (legacy.get().invoke(item, stack, entity, slot, null) instanceof String path) {
               return Identifier.parse(path);
            }
         } catch (Throwable var9) {
            LOGGER.warn("{}.getArmorTexture failed", item.getClass().getSimpleName(), var9);
         }
      }

      if (item instanceof ArmorItem armorItem) {
         try {
            ArmorMaterial material = (ArmorMaterial)armorItem.getMaterial().value();
            List<ArmorMaterial.Layer> layers = material.layers();
            ArmorMaterial.Layer layer = layers.get(slot == EquipmentSlot.LEGS && layers.size() > 1 ? 1 : 0);
            return layer.texture(layer.dyeable());
         } catch (Throwable var10) {
            return null;
         }
      } else {
         return null;
      }
   }

   private static void installMobEffectExtensions() {
      for (MobEffect effect : BuiltInRegistries.MOB_EFFECT.stream().toList()) {
         IClientMobEffectExtensions extensions = collectInitializeClient(effect, IClientMobEffectExtensions.class);
         if (extensions != null) {
            IClientMobEffectExtensions.EXTENSIONS.put(effect, extensions);
         }
      }

      ClientTickEvents.END_CLIENT_TICK
         .register(
            (EndTick)client -> {
               if (client.player != null) {
                  Collection<MobEffectInstance> effects = client.player.getActiveEffects();
                  if (!effects.isEmpty()) {
                     for (MobEffectInstance instance : effects) {
                        IClientMobEffectExtensions extensionsx = IClientMobEffectExtensions.of((MobEffect)instance.getEffect().value());
                        if (extensionsx != null
                           && (!extensionsx.isVisibleInGui(instance) || !extensionsx.isVisibleInHud(instance) || !extensionsx.isVisibleInInventory(instance))) {
                           hideEffectInstance(instance);
                        }
                     }
                  }
               }
            }
         );
   }

   private static void hideEffectInstance(MobEffectInstance instance) {
      if (!effectFieldsResolved) {
         visibleField = booleanField("visible", 1);
         showIconField = booleanField("showIcon", 2);
         effectFieldsResolved = true;
         if (visibleField == null || showIconField == null) {
            LOGGER.warn("MobEffectInstance visibility fields unreachable, hidden effects will still render");
         }
      }

      setFalse(visibleField, instance);
      setFalse(showIconField, instance);
   }

   private static Field booleanField(String name, int declarationIndex) {
      try {
         Field field = MobEffectInstance.class.getDeclaredField(name);
         field.setAccessible(true);
         return field;
      } catch (NoSuchFieldException var8) {
         int seen = 0;

         for (Field fieldx : MobEffectInstance.class.getDeclaredFields()) {
            if (fieldx.getType() == boolean.class && !Modifier.isStatic(fieldx.getModifiers()) && seen++ == declarationIndex) {
               fieldx.setAccessible(true);
               return fieldx;
            }
         }

         return null;
      }
   }

   private static void setFalse(Field field, Object target) {
      if (field != null) {
         try {
            field.setBoolean(target, false);
         } catch (Throwable var3) {
         }
      }
   }

   private static void installHudOverlays() {
      net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry.addLast(net.minecraft.resources.Identifier.fromNamespaceAndPath("modminds", "hud_79039_12496"), (guiGraphics, deltaTracker) -> {
         Minecraft client = Minecraft.getInstance();
         if (client.level != null && client.options != null && !client.options.hideGui) {
            try {
               ForgeEventBus.postToBoth(new RenderGuiEvent.Pre(guiGraphics));
            } catch (Throwable var4) {
               LOGGER.error("RenderGuiEvent.Pre failed", var4);
            }
         }
      });
   }

   private static <T> T collectInitializeClient(Object target, Class<T> extensionType) {
      Optional<Method> method = INITIALIZE_CLIENT.computeIfAbsent(target.getClass(), type -> {
         try {
            return Optional.of(type.getMethod("initializeClient", Consumer.class));
         } catch (NoSuchMethodException var2x) {
            return Optional.empty();
         }
      });
      if (method.isEmpty()) {
         return null;
      } else {
         Object[] captured = new Object[1];

         try {
            method.get().invoke(target, (Consumer<Object>)value -> {
               if (extensionType.isInstance(value)) {
                  captured[0] = value;
               }
            });
         } catch (Throwable var5) {
            LOGGER.warn("{}.initializeClient failed", target.getClass().getSimpleName(), var5);
            return null;
         }

         return extensionType.isInstance(captured[0]) ? extensionType.cast(captured[0]) : null;
      }
   }
}
