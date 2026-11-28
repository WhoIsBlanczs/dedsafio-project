J=$(ls minecraft-common-*-26.3.jar)
for f in overworld/temperature overworld/vegetation overworld/erosion overworld/continents overworld/ore_vein/toggle overworld/caves/spaghetti_2d_thickness_modulator; do echo "== $f"; unzip -p $J data/minecraft/worldgen/density_function/$f.json | tr -d ' \n'; echo; done
unzip -p $J data/minecraft/worldgen/noise_settings/nether.json | python3 -c "
import json,sys;d=json.load(sys.stdin);print(json.dumps(d['noise_router'])[:1500])"
