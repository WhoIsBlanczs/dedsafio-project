J=$(ls minecraft-common-*.jar); mkdir -p /tmp/m && cd /tmp/m && unzip -q -o $OLDPWD/$J 'data/*' 2>/dev/null; cd data/minecraft/worldgen
python3 - <<'P'
import json,os
n=json.load(open('noise_settings/nether.json')); print('NETHER', json.dumps({k:v for k,v in n.items() if k!='material_rule'})[:3000])
print('MR list', os.listdir('material_rule')[:20], os.listdir('material_condition')[:20])
m=json.load(open('material_rule/'+sorted(os.listdir('material_rule'))[0])); print('MR0', json.dumps(m)[:1500])
c=json.load(open('noise_settings/caves.json')); print('CAVES noise', c['noise'], json.dumps(c['noise_router'])[:800], json.dumps(c['spawn_target'])[:300])
ls=os.listdir('noise_settings'); print('NSLIST', ls)
for n in ['amplified','large_biomes','floating_islands']:
    if n+'.json' in ls: print(n, json.load(open('noise_settings/'+n+'.json'))['noise'])
print('DFNETHER', os.listdir('density_function/nether') if os.path.isdir('density_function/nether') else os.listdir('density_function'))
d=json.load(open('density_function/overworld/final_density.json')); print('FD', json.dumps(d)[:1200])
print('PB', json.dumps(json.load(open('placed_feature/ore_iron_upper.json'))))
print('SELECTOR_PLACED', [f for f in os.listdir('placed_feature') if 'checked' in f][:10])
print('FEAT birch', json.dumps(json.load(open('feature/birch.json')))[:600])
print('FEAT grass', json.dumps(json.load(open('feature/grass.json')))[:800])
P
