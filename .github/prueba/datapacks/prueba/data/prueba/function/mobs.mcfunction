gamemode creative @a
time set noon
weather clear
execute as @p at @s run tp @s ~ ~20 ~
execute as @p at @s run fill ~-30 ~-1 ~-5 ~30 ~-1 ~60 minecraft:smooth_stone
execute as @p at @s run fill ~-30 ~ ~-5 ~30 ~15 ~60 minecraft:air
execute as @p at @s run summon minecraft:marker ~ ~ ~ {Tags:["o"]}
execute as @p at @s run summon ruletafanproject:agujero_negro ~-22 ~ ~12 {NoAI:1b,PersistenceRequired:1b,Invulnerable:1b}
execute as @p at @s run summon ruletafanproject:bomba_warden ~-12 ~ ~12 {NoAI:1b,PersistenceRequired:1b,Invulnerable:1b}
execute as @p at @s run summon ruletafanproject:burbuja ~-7 ~ ~12 {NoAI:1b,PersistenceRequired:1b,Invulnerable:1b}
execute as @p at @s run summon ruletafanproject:creeper_cesped ~-2 ~ ~12 {NoAI:1b,PersistenceRequired:1b,Invulnerable:1b}
execute as @p at @s run summon ruletafanproject:creeper_rojo ~3 ~ ~12 {NoAI:1b,PersistenceRequired:1b,Invulnerable:1b}
execute as @p at @s run summon ruletafanproject:herobrine ~8 ~ ~12 {NoAI:1b,PersistenceRequired:1b,Invulnerable:1b}
execute as @p at @s run summon ruletafanproject:meteorito ~13 ~ ~12 {NoAI:1b,PersistenceRequired:1b,Invulnerable:1b}
execute as @p at @s run summon ruletafanproject:misil ~18 ~ ~12 {NoAI:1b,PersistenceRequired:1b,Invulnerable:1b}
execute as @p at @s run summon nuclearcreeper:nuclear_creeper ~-7 ~ ~24 {NoAI:1b,PersistenceRequired:1b,Invulnerable:1b}
execute as @p at @s run setblock ~-6 ~ ~6 ruletafanproject:tragamonedas
execute as @p at @s run setblock ~-2 ~ ~6 ruletafanproject:estatua_nutria
execute as @p at @s run setblock ~2 ~ ~6 ruletafanproject:cajero
execute as @p at @s run setblock ~6 ~ ~6 ruletafanproject:puerta_bunker
say MOBS_LISTOS
schedule function prueba:vista1 100t
