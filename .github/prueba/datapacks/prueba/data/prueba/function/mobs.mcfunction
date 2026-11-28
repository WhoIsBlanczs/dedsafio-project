gamemode creative @a
time set noon
weather clear
execute as @p at @s run tp @s ~ ~20 ~
execute as @p at @s run fill ~-30 ~-1 ~-5 ~30 ~-1 ~60 minecraft:smooth_stone
execute as @p at @s run fill ~-30 ~ ~-5 ~30 ~15 ~60 minecraft:air
execute as @p at @s run summon minecraft:marker ~ ~ ~ {Tags:["o"]}
execute as @p at @s run summon ghostsafio:cerebro_amarillo ~-22 ~ ~12 {NoAI:1b,PersistenceRequired:1b,Invulnerable:1b}
execute as @p at @s run summon ghostsafio:nautilus ~-17 ~ ~12 {NoAI:1b,PersistenceRequired:1b,Invulnerable:1b}
execute as @p at @s run summon ghostsafio:nave ~-12 ~ ~12 {NoAI:1b,PersistenceRequired:1b,Invulnerable:1b}
execute as @p at @s run summon ghostsafio:pod_phora_laser ~-7 ~ ~12 {NoAI:1b,PersistenceRequired:1b,Invulnerable:1b}
execute as @p at @s run summon ghostsafio:pod_phora ~-2 ~ ~12 {NoAI:1b,PersistenceRequired:1b,Invulnerable:1b}
execute as @p at @s run summon ghostsafio:skiff_phora ~3 ~ ~12 {NoAI:1b,PersistenceRequired:1b,Invulnerable:1b}
say MOBS_LISTOS
schedule function prueba:vista1 100t
