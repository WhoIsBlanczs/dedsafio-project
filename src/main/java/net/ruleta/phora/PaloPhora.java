package net.ruleta.phora;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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

   public InteractionResult use(Level nivel, Player jugador, InteractionHand mano) { return null; }

   public boolean isFoil(ItemStack s) {
      return true;
   }

   public void appendHoverText(ItemStack s, TooltipContext contexto, List<Component> lineas, TooltipFlag f) {
      lineas.add(Component.literal("Clic derecho: un misil de los Phora cae donde miras").withStyle(ChatFormatting.GOLD));
   }
}
