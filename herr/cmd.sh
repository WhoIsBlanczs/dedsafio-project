J=$(ls minecraft-common-*-26.3.jar)
for f in blocks/oak_slab blocks/gilded_blackstone blocks/hay_block; do echo "== $f"; unzip -p $J data/minecraft/loot_table/$f.json | tr -d ' \n'; echo; done
unzip -l $J | grep -c 'data/minecraft/loot_table/'
