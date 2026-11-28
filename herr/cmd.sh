J=$(ls minecraft-common-*.jar); mkdir -p /tmp/x && cd /tmp/x && unzip -q -o $OLDPWD/$J 'net/minecraft/world/level/levelgen/*' 2>/dev/null
echo "== DF"; strings -n 3 net/minecraft/world/level/levelgen/DensityFunctions.class | grep -E '^[a-z_]+$' | tr '\n' ' '; echo
echo "== PM"; strings -n 3 net/minecraft/world/level/levelgen/placement/PlacementModifierType.class | grep -E '^[a-z_]+$' | tr '\n' ' '; echo
ls net/minecraft/world/level/levelgen/placement/ | tr '\n' ' '; echo
echo "== OFFSET"; javap -p -cp . net.minecraft.world.level.levelgen.placement.OffsetPlacement 2>/dev/null | head; 
echo "== NOISE"; strings -n 3 net/minecraft/world/level/levelgen/synth/NormalNoise\$NoiseParameters.class | grep -E '^[a-z_A-Z]+$' | tr '\n' ' '
