J=$(ls minecraft-common-*-26.3.jar); unzip -p $J data/minecraft/worldgen/noise_settings/overworld.json | python3 -c "
import json,sys;d=json.load(sys.stdin);r=d['noise_router'];print('TEMP',json.dumps(r['temperature']));print('VEG',json.dumps(r['vegetation']));print('CONT',json.dumps(r['continents'])[:400])"
unzip -l $J | grep 'worldgen/density_function/' | head -40
for f in shift_x shift_z; do unzip -p $J data/minecraft/worldgen/density_function/$f.json; echo; done
unzip -p $J data/minecraft/worldgen/density_function/overworld/base_3d_noise.json 2>/dev/null | head -c 400; echo
javap -p -cp "$(ls *.jar | tr '\n' ':')" 'net.minecraft.world.level.levelgen.DensityFunctions' | grep -i 'class\|record' | head -60
