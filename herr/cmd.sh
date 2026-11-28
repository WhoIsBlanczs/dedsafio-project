J=$(ls minecraft-common-*.jar); mkdir -p /tmp/x && cd /tmp/x && unzip -q -o $OLDPWD/$J 'data/minecraft/loot_table/*' 'data/minecraft/predicate/*' 2>/dev/null
for f in predicate/tool/can_silk_touch.json loot_table/entities/zombie.json loot_table/entities/blaze.json loot_table/blocks/oak_leaves.json loot_table/blocks/wheat.json loot_table/blocks/oak_door.json; do echo "== $f"; tr -d ' \n' < data/minecraft/$f | head -c 1800; echo; done
ls data/minecraft/predicate data/minecraft/predicate/* | head -40 | tr '\n' ' '
