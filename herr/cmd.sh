J=$(ls *.jar | tr "\n" ":")
javap -p -cp $J net.minecraft.world.entity.monster.Creeper | grep -iE 'explode|ignite'
javap -p -cp $J net.minecraft.world.entity.LivingEntity | grep -iE 'totem|hurtServer'
