J=$(ls *.jar | tr "\n" ":")
javap -p -cp $J net.minecraft.client.Camera | grep -iE 'zoom|distance'
javap -p -cp $J net.minecraft.server.MinecraftServer | grep -iE 'loadLevel'
javap -p -cp $J net.minecraft.client.multiplayer.ClientLevel | grep -iE 'sky'
javap -p -cp $J net.minecraft.world.entity.projectile.arrow.AbstractArrow | grep -iE 'onHitEntity'
