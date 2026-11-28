CP=$(ls *.jar | tr '\n' ':')
javap -p -cp "$CP" net.minecraft.world.level.ServerExplosion | grep -v 'static final'
javap -p -c -cp "$CP" net.minecraft.world.level.ServerExplosion | sed -n '/public int explode()/,/^$/p' | grep -E 'invoke|getfield' | head -30
javap -p -c -cp "$CP" net.minecraft.world.level.ServerExplosion | sed -n '/void hurtEntities()/,/^$/p' | grep -E 'invoke' | head -12
