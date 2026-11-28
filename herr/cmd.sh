J=$(ls minecraft-common-*.jar); mkdir -p /tmp/m && cd /tmp/m && unzip -q -o $OLDPWD/$J 'data/*' 'assets/*' 2>/dev/null; ls data/minecraft; ls data/minecraft/worldgen
echo "--- feature files (sample)"; ls data/minecraft/worldgen/feature 2>/dev/null | head -400 | tr '\n' ' '
for f in feature/ore_iron feature/trees_birch_and_oak_leaf_litter feature/patch_grass feature/flower_default feature/patch_tall_grass feature/fancy_oak placed_feature/ore_iron_upper placed_feature/patch_grass_plain placed_feature/trees_plains ; do echo "--- $f"; cat data/minecraft/worldgen/$f.json 2>/dev/null | head -c 2500; echo; done
echo "--- biome plains"; cat data/minecraft/worldgen/biome/plains.json | head -c 4000; echo
echo "--- dimension_type overworld"; cat data/minecraft/dimension_type/overworld.json; echo
echo "--- noise_settings overworld (head)"; head -c 1500 data/minecraft/worldgen/noise_settings/overworld.json; echo
