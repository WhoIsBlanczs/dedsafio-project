package net.ruleta.phora;

import net.minecraft.world.InteractionResult;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class PaloPhora extends Item {
   private static final double ALCANCE = 160.0;

   public PaloPhora(Properties p) {
      super(p);
   }

   public InteractionResult use(Level nivel, Player jugador, InteractionHand mano) {
      ItemStack palo = jugador.getItemInHand(mano);
      if (!nivel.isClientSide() && nivel instanceof ServerLevel sl) {
         Vec3 ojos = jugador.getEyePosition();
         Vec3 fin = ojos.add(jugador.getViewVector(1.0F).scale(160.0));
         BlockHitResult hit = nivel.clip(new ClipContext(ojos, fin, Block.OUTLINE, Fluid.ANY, jugador));
         Vec3 objetivo = hit.getType() == Type.MISS ? fin : hit.getLocation();
         MisilEntity.lanzar(sl, objetivo, jugador.getRandom(), jugador.position());
         jugador.getCooldowns().addCooldown(this.getDefaultInstance(), 20);
      }

      return InteractionResult.SUCCESS;
   }

   public boolean isFoil(ItemStack s) {
      return true;
   }

   public void appendHoverText(ItemStack s, net.minecraft.world.item.Item.TooltipContext contexto, net.minecraft.world.item.component.TooltipDisplay tooltipDisplay$, java.util.function.Consumer<Component> tooltipAdder$, TooltipFlag f) {
      java.util.List<Component> lineas = new java.util.ArrayList<>();
      this.appendHoverText$viejo(s, contexto, tooltipDisplay$, lineas, f);
      lineas.forEach(tooltipAdder$);
   }

   private void appendHoverText$viejo(ItemStack s, net.minecraft.world.item.Item.TooltipContext contexto, net.minecraft.world.item.component.TooltipDisplay tooltipDisplay$, List<Component> lineas, TooltipFlag f) {
      lineas.add(Component.literal("Clic derecho: un misil de los Phora cae donde miras").withStyle(ChatFormatting.GOLD));
   }
}
