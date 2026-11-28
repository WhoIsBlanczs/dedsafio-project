J=$(ls minecraft-common-*.jar); mkdir -p /tmp/x && cd /tmp/x && unzip -q -o $OLDPWD/$J 'net/minecraft/world/level/levelgen/*' 'data/minecraft/worldgen/noise/*' 2>/dev/null
echo "== DF"; strings -n 3 net/minecraft/world/level/levelgen/densityfunction/DensityFunctions.class | grep -E '^[a-z_0-9]+$' | tr '\n' ' '; echo
echo "== PM"; strings -n 3 net/minecraft/world/level/levelgen/placement/PlacementModifierTypes.class | grep -E '^[a-z_]+$' | tr '\n' ' '; echo
echo "== NOISE json"; cat data/minecraft/worldgen/noise/temperature.json; echo; ls data/minecraft/worldgen/noise | head -5
