J=$(ls minecraft-common-*-26.3.jar)
unzip -p $J data/minecraft/dimension_type/the_nether.json | tr -d ' \n'; echo
unzip -p $J data/minecraft/dimension_type/the_end.json | tr -d ' \n'; echo
unzip -p $J data/minecraft/worldgen/biome/basalt_deltas.json | python3 -c "import json,sys;d=json.load(sys.stdin);print(json.dumps({k:v for k,v in d.items() if k not in ('features','carvers','spawners','spawn_costs')}))"
CP=$(ls *.jar | tr '\n' ':'); javap -cp "$CP" net.minecraft.world.attribute.EnvironmentAttributes 2>&1 | grep -iE 'fog|cloud|sky' | head -30
