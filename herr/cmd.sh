J=$(ls minecraft-common-*.jar); mkdir -p /tmp/m && cd /tmp/m && unzip -q -o $OLDPWD/$J 'data/*' 2>/dev/null; cd data/minecraft/worldgen
python3 -c "
import json
for f in ['density_function/overworld/chunk_surface_level.json','density_function/overworld/preliminary_surface_level.json']:
    print(f, json.dumps(json.load(open(f)))[:1500])
n=json.load(open('noise_settings/floating_islands.json')); print('FI', json.dumps({k:v for k,v in n.items() if k not in ('material_rule','debug_functions')})[:1500])
"
grep -rl 'interpolated' density_function | head -3
