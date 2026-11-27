package net.ruleta.burbuja;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class ItemBurbuja extends Item {
   public ItemBurbuja(Properties propiedades) {
      super(propiedades);
   }

   public InteractionResult useOn(UseOnContext ctx) {
      Level nivel = ctx.getLevel();
      Player jugador = ctx.getPlayer();
      if (jugador == null) {
         return InteractionResult.PASS;
      }

      if (nivel.isClientSide()) {
         return InteractionResult.SUCCESS;
      }

      float d = Math.max(0.9F, Math.min(6.0F, jugador.getDimensions(Pose.STANDING).height() * 1.25F + 0.45F));
      BlockPos clic = ctx.getClickedPos();
      Direction cara = ctx.getClickedFace();
      BlockPos pos = clic.relative(cara);
      double x = pos.getX() + 0.5;
      double z = pos.getZ() + 0.5;
      double y = cara == Direction.UP ? clic.getY() + 1.0 : (cara == Direction.DOWN ? clic.getY() - d : pos.getY());
      if (cara.getAxis().isHorizontal()) {
         x += cara.getStepX() * (d / 2.0 - 0.5);
         z += cara.getStepZ() * (d / 2.0 - 0.5);
      }

      Burbuja b = new Burbuja(Burbujas.BURBUJA.get(), nivel);
      b.setDiametro(d);
      b.setPropietario(jugador.getUUID());
      b.setPos(x, y, z);

      for (int k = 0; k < 6 && !nivel.noCollision(b, b.getBoundingBox()); k++) {
         b.setPos(x, b.getY() + 0.5, z);
      }

      if (!nivel.noCollision(b, b.getBoundingBox())) {
         jugador.sendOverlayMessage(Component.literal("No cabe la burbuja aquí"));
         return InteractionResult.FAIL;
      } else {
         Burbujas.quitarDe(jugador.level().getServer(), jugador.getUUID());
         nivel.addFreshEntity(b);
         nivel.playSound(null, b.getX(), b.getY(), b.getZ(), SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundSource.PLAYERS, 1.0F, 1.4F);
         jugador.getCooldowns().addCooldown(net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(this), 10);
         return InteractionResult.CONSUME;
      }
   }
}
