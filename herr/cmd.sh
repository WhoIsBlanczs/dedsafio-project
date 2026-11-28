J=$(ls *.jar | tr "\n" ":")
javap -c -p -cp $J net.minecraft.world.entity.LivingEntity | sed -n '/public void knockback(double, double, double, net.minecraft.world.damagesource.DamageSource, float);/,/^$/p' | head -60
javap -cp $J net.minecraft.world.InteractionHand
javap -cp $J net.minecraft.server.level.ServerPlayer | grep -i sound
javap -cp $J net.minecraft.world.entity.player.Player | grep -i sound
