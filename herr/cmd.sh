J=$(ls minecraft-common-*.jar); mkdir -p /tmp/x && cd /tmp/x && unzip -q -o $OLDPWD/$J 'data/minecraft/worldgen/noise/*' 2>/dev/null
for n in continentalness erosion ridge vegetation offset aquifer_barrier temperature gravel surface cave_cheese; do echo "$n $(tr -d ' \n' < data/minecraft/worldgen/noise/$n.json)"; done
