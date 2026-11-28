J=$(ls minecraft-common-*.jar); mkdir -p /tmp/x && cd /tmp/x && unzip -q -o $OLDPWD/$J 'data/minecraft/loot_table/blocks/iron_ore.json' 'data/minecraft/loot_table/blocks/grass_block.json' 2>/dev/null
tr -d ' \n' < data/minecraft/loot_table/blocks/iron_ore.json; echo; tr -d ' \n' < data/minecraft/loot_table/blocks/grass_block.json; echo
