J=$(ls minecraft-clientOnly-*.jar); unzip -Z1 $J | grep -E 'spawn_egg|template_' | grep -vE 'textures/item/[a-z_]+_spawn_egg.png' | head -20
unzip -Z1 $J | grep -c 'textures/item/.*_spawn_egg.png'
unzip -p $J assets/minecraft/items/zombie_spawn_egg.json; echo
unzip -p $J assets/minecraft/models/item/zombie_spawn_egg.json; echo
unzip -p $J assets/minecraft/items/bow.json; echo
