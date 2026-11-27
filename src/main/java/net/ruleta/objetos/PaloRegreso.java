package net.ruleta.objetos;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;

public class PaloRegreso extends Item {
   public PaloRegreso(Properties p) {
      super(p);
   }

   public InteractionResult interactLivingEntity(ItemStack palo, Player quien, LivingEntity objetivo, InteractionHand mano) { return null; }

   public static void regresar(ServerPlayer p) { }

   private static void despues(ServerPlayer p, String texto) {
      p.fallDistance = 0.0F;
      p.sendOverlayMessage(Component.literal(texto).withStyle(ChatFormatting.WHITE));
      ServerLevel nivel = p.level();
      nivel.playSound(null, p.getX(), p.getY(), p.getZ(), Objetos.SONIDO_REGRESO.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
   }

   static void humo(ServerLevel nivel, Vec3 pos) {
      nivel.sendParticles(ParticleTypes.LARGE_SMOKE, pos.x, pos.y + 1.0, pos.z, 30, 0.3, 0.8, 0.3, 0.02);
      nivel.playSound(null, pos.x, pos.y, pos.z, Objetos.SONIDO_REGRESO.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
   }

   public void appendHoverText(ItemStack s, TooltipContext contexto, List<Component> lineas, TooltipFlag f) {
      lineas.add(Component.literal("Clic derecho sobre un jugador: lo manda a su cama (o al spawn del mundo)").withStyle(ChatFormatting.GRAY));
   }
}
