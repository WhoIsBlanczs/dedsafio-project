package net.ruleta.objetos;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.ruleta.Ajustes;
import net.ruleta.herobrine.HerobrineEntity;

public class Linterna extends Item {
   private static final String CARGA = "Carga";
   private static final String PRENDIDA = "Prendida";

   public static int cargaMax() {
      return Math.max(1, Ajustes.entero("linterna.carga"));
   }

   public static int ticksPrender() {
      return Math.max(10, Math.round(Ajustes.decimal("linterna.prender") * 20.0F));
   }

   public Linterna(Properties p) {
      super(p);
   }

   private static CompoundTag datos(ItemStack s) {
      return ((CustomData)s.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
   }

   public static int carga(ItemStack s) {
      CompoundTag t = datos(s);
      return !t.contains("Carga") ? cargaMax() : t.getIntOr("Carga", 0);
   }

   public static boolean prendida(ItemStack s) {
      return datos(s).getBooleanOr("Prendida", false) && carga(s) > 0;
   }

   public static float intensidad(ItemStack s) {
      if (!prendida(s)) {
         return 0.0F;
      }

      float k = (float)carga(s) / cargaMax();
      return k >= 0.5F ? 1.0F : 0.25F + 0.75F * (k / 0.5F);
   }

   private static void poner(ItemStack s, int carga, boolean prendida) {
      CustomData.update(DataComponents.CUSTOM_DATA, s, t -> {
         t.putInt("Carga", Mth.clamp(carga, 0, cargaMax()));
         t.putBoolean("Prendida", prendida && carga > 0);
      });
   }

   public InteractionResult use(Level nivel, Player jugador, InteractionHand mano) {
      ItemStack linterna = jugador.getItemInHand(mano);
      ItemStack otra = jugador.getItemInHand(mano == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
      if (mano == InteractionHand.MAIN_HAND && otra.is(Objetos.BATERIA.get())) {
         if (carga(linterna) >= cargaMax()) {
            return InteractionResult.FAIL;
         }

         if (!nivel.isClientSide()) {
            poner(linterna, cargaMax(), prendida(linterna));
            if (!jugador.getAbilities().instabuild) {
               otra.shrink(1);
            }

            nivel.playSound(
               null, jugador.getX(), jugador.getY(), jugador.getZ(), (SoundEvent)SoundEvents.ARMOR_EQUIP_IRON.value(), SoundSource.PLAYERS, 1.0F, 1.4F
            );
         }

         return InteractionResult.SUCCESS;
      } else {
         if (prendida(linterna)) {
            jugador.startUsingItem(mano);
            return InteractionResult.CONSUME;
         }

         if (carga(linterna) <= 0) {
            return InteractionResult.FAIL;
         }

         jugador.startUsingItem(mano);
         return InteractionResult.CONSUME;
      }
   }

   public void onUseTick(Level nivel, LivingEntity quien, ItemStack linterna, int quedan) {
      int usados = this.getUseDuration(linterna, quien) - quedan;
      if (usados >= ticksPrender()) {
         if (!nivel.isClientSide()) {
            boolean prender = !prendida(linterna);
            poner(linterna, carga(linterna), prender);
            nivel.playSound(null, quien.getX(), quien.getY(), quien.getZ(), SoundEvents.LEVER_CLICK, SoundSource.PLAYERS, 0.6F, prender ? 1.3F : 0.8F);
         }

         quien.releaseUsingItem();
      }
   }

   public int getUseDuration(ItemStack s, LivingEntity quien) {
      return 72000;
   }

   public ItemUseAnimation getUseAnimation(ItemStack s) {
      return ItemUseAnimation.NONE;
   }

   public void inventoryTick(ItemStack s, Level nivel, Entity quien, int hueco, boolean elegida) {
      if (!nivel.isClientSide() && prendida(s)) {
         if (nivel.getGameTime() % (Math.max(1, Ajustes.entero("linterna.consumo")) * 20L) == 0L) {
            int c = carga(s) - 1;
            poner(s, c, c > 0);
            if (c <= 0) {
               nivel.playSound(null, quien.getX(), quien.getY(), quien.getZ(), SoundEvents.LEVER_CLICK, SoundSource.PLAYERS, 0.6F, 0.6F);
            }
         }

         if (quien instanceof ServerPlayer p && enMano(p, s) && nivel.getGameTime() % 4L == 0L) {
            apuntarHerobrine((ServerLevel)nivel, p);
         }
      }
   }

   private static boolean enMano(Player p, ItemStack s) {
      return p.getMainHandItem() == s || p.getOffhandItem() == s;
   }

   private static void apuntarHerobrine(ServerLevel nivel, ServerPlayer p) {
      Vec3 ojos = p.getEyePosition();
      Vec3 vista = p.getViewVector(1.0F).normalize();
      double alcance = Ajustes.entero("linterna.alcance");
      AABB caja = new AABB(ojos, ojos).inflate(alcance);

      for (HerobrineEntity h : nivel.getEntitiesOfClass(HerobrineEntity.class, caja)) {
         Vec3 hacia = h.position().add(0.0, h.getBbHeight() * 0.6, 0.0).subtract(ojos);
         double d = hacia.length();
         if (!(d > alcance) && !(d < 0.001) && vista.dot(hacia.scale(1.0 / d)) > 0.96 && p.hasLineOfSight(h)) {
            h.desaparecer();
         }
      }
   }

   public boolean isBarVisible(ItemStack s) {
      return carga(s) < cargaMax();
   }

   public int getBarWidth(ItemStack s) {
      return Math.round(13.0F * carga(s) / cargaMax());
   }

   public int getBarColor(ItemStack s) {
      float k = (float)carga(s) / cargaMax();
      return Mth.hsvToRgb(k / 3.0F, 1.0F, 1.0F);
   }

   public boolean allowComponentsUpdateAnimation(Player jugador, InteractionHand mano, ItemStack antes, ItemStack ahora) {
      return false;
   }

   public void appendHoverText(ItemStack s, TooltipContext contexto, List<Component> lineas, TooltipFlag f) {
      lineas.add(Component.literal("Carga: " + carga(s) + " / " + cargaMax() + (prendida(s) ? "  (prendida)" : "")).withStyle(ChatFormatting.GRAY));
      lineas.add(Component.literal("Mantén clic derecho 3 s para prenderla; clic para apagarla.").withStyle(ChatFormatting.DARK_GRAY));
      lineas.add(Component.literal("Batería en la otra mano + clic derecho: recarga.").withStyle(ChatFormatting.DARK_GRAY));
   }
}
