J=$(ls minecraft-common-*.jar); mkdir -p /tmp/m && cd /tmp/m && unzip -q -o $OLDPWD/$J 'data/*' 2>/dev/null; cd data/minecraft
python3 - <<'P'
import json
def load(p): return json.load(open(p))
b=load('worldgen/biome/plains.json'); b.pop('features',None); print('PLAINS', json.dumps(b)[0:200], '...KEYS', list(b.keys()), json.dumps({k:v for k,v in b.items() if k not in ('attributes',)}))
for n in ['basalt_deltas','lush_caves','swamp','crimson_forest']:
    b=load(f'worldgen/biome/{n}.json'); b.pop('features',None); b.pop('carvers',None)
    a=b.get('attributes',{}); 
    a.pop('minecraft:gameplay/natural_mob_spawns',None)
    print(n.upper(), json.dumps(b))
print('NETHER_DT', json.dumps(load('dimension_type/the_nether.json')))
n=load('worldgen/noise_settings/overworld.json'); print('NS keys', list(n.keys())); print('noise', n.get('noise')); print('router keys', list(n.get('noise_router',{}).keys()) if 'noise_router' in n else None)
for k,v in n.items():
    if k not in ('surface_rule','aquifers','debug_functions'): print('NS', k, json.dumps(v)[:600])
c=load('worldgen/noise_settings/caves.json'); print('CAVES keys', list(c.keys()))
import os
print('DF', os.listdir('worldgen/density_function/overworld')[:40])
print('WP', json.dumps(load('worldgen/world_preset/normal.json'))[:1500])
P
