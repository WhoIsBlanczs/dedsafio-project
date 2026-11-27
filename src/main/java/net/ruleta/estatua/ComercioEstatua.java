package net.ruleta.estatua;

import java.util.OptionalInt;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import org.jetbrains.annotations.Nullable;

public final class ComercioEstatua implements Merchant {
   private final MerchantOffers ofertas = new MerchantOffers();
   @Nullable
   private Player jugador;

   private ComercioEstatua() {
      for (ConfigPedestal.Trato t : ConfigPedestal.get().tratos) {
         Item da = ConfigPedestal.item(t.da_item);
         Item recibe = ConfigPedestal.item(t.recibe_item);
         if (da != null && recibe != null) {
            this.ofertas.add(new MerchantOffer(new ItemCost(da, t.da_cantidad), new ItemStack(recibe, t.recibe_cantidad), Integer.MAX_VALUE, 0, 0.0F));
         }
      }
   }

   public static void abrir(ServerPlayer sp) {
      ComercioEstatua comercio = new ComercioEstatua();
      comercio.setTradingPlayer(sp);
      OptionalInt id = sp.openMenu(
         new SimpleMenuProvider((idMenu, inventario, quien) -> new MenuEstatuaNutria(idMenu, inventario, comercio), Component.empty())
      );
      if (id.isPresent()) {
         sp.sendMerchantOffers(id.getAsInt(), comercio.getOffers(), 1, 0, false, false);
      } else {
         comercio.setTradingPlayer(null);
      }
   }

   public void setTradingPlayer(@Nullable Player quien) {
      this.jugador = quien;
   }

   @Nullable
   public Player getTradingPlayer() {
      return this.jugador;
   }

   public MerchantOffers getOffers() {
      return this.ofertas;
   }

   public void overrideOffers(@Nullable MerchantOffers nuevas) {
   }

   public void notifyTrade(MerchantOffer oferta) {
      oferta.increaseUses();
   }

   public void notifyTradeUpdated(ItemStack pila) {
   }

   public int getVillagerXp() {
      return 0;
   }

   public void overrideXp(int xp) {
   }

   public boolean showProgressBar() {
      return false;
   }

   public SoundEvent getNotifyTradeSound() {
      return SoundEvents.VILLAGER_YES;
   }

   public boolean isClientSide() {
      return false;
   }

   public boolean stillValid(net.minecraft.world.entity.player.Player p_) { return true; }
}
