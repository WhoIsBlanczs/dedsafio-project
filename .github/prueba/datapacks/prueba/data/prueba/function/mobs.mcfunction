gamemode creative @a
time set noon
weather clear
execute as @p at @s run tp @s ~ ~20 ~
execute as @p at @s run fill ~-30 ~-1 ~-5 ~30 ~-1 ~60 minecraft:smooth_stone
execute as @p at @s run fill ~-30 ~ ~-5 ~30 ~15 ~60 minecraft:air
execute as @p at @s run summon minecraft:marker ~ ~ ~ {Tags:["o"]}
say MOBS_LISTOS
schedule function prueba:vista1 100t
