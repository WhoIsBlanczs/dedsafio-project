gamemode creative @a
time set noon
weather clear
execute as @p at @s run tp @s ~ ~20 ~
execute as @p at @s run fill ~-30 ~-1 ~-5 ~30 ~-1 ~60 minecraft:smooth_stone
execute as @p at @s run fill ~-30 ~ ~-5 ~30 ~15 ~60 minecraft:air
execute as @p at @s run summon minecraft:marker ~ ~ ~ {Tags:["o"]}
execute as @p at @s run summon nexo:cerearana ~-22 ~ ~12 {NoAI:1b,PersistenceRequired:1b,Invulnerable:1b}
execute as @p at @s run summon nexo:creeper_dorado ~-17 ~ ~12 {NoAI:1b,PersistenceRequired:1b,Invulnerable:1b}
execute as @p at @s run summon nexo:creeper_floral ~-12 ~ ~12 {NoAI:1b,PersistenceRequired:1b,Invulnerable:1b}
execute as @p at @s run summon nexo:creeper_metalico ~-7 ~ ~12 {NoAI:1b,PersistenceRequired:1b,Invulnerable:1b}
execute as @p at @s run summon nexo:creeper_nuclear ~-2 ~ ~12 {NoAI:1b,PersistenceRequired:1b,Invulnerable:1b}
execute as @p at @s run summon nexo:creeper_tronco ~3 ~ ~12 {NoAI:1b,PersistenceRequired:1b,Invulnerable:1b}
execute as @p at @s run summon nexo:fantasma_amarillo ~8 ~ ~12 {NoAI:1b,PersistenceRequired:1b,Invulnerable:1b}
execute as @p at @s run summon nexo:fantasma_blanco ~13 ~ ~12 {NoAI:1b,PersistenceRequired:1b,Invulnerable:1b}
execute as @p at @s run summon nexo:fantasma_rojo ~18 ~ ~12 {NoAI:1b,PersistenceRequired:1b,Invulnerable:1b}
execute as @p at @s run summon nexo:gream_reaper_pequena ~23 ~ ~12 {NoAI:1b,PersistenceRequired:1b,Invulnerable:1b}
execute as @p at @s run summon nexo:grim_reaper ~-22 ~ ~18 {NoAI:1b,PersistenceRequired:1b,Invulnerable:1b}
execute as @p at @s run summon nexo:gusano_glebanoide ~-17 ~ ~18 {NoAI:1b,PersistenceRequired:1b,Invulnerable:1b}
execute as @p at @s run summon nexo:nautilus_glebanoide ~-12 ~ ~18 {NoAI:1b,PersistenceRequired:1b,Invulnerable:1b}
execute as @p at @s run summon nexo:nave_dilitio ~-7 ~ ~18 {NoAI:1b,PersistenceRequired:1b,Invulnerable:1b}
execute as @p at @s run summon nexo:pterodactilo ~-2 ~ ~18 {NoAI:1b,PersistenceRequired:1b,Invulnerable:1b}
execute as @p at @s run summon nexo:raptor ~3 ~ ~18 {NoAI:1b,PersistenceRequired:1b,Invulnerable:1b}
execute as @p at @s run summon nexo:sauropodo_morado ~8 ~ ~18 {NoAI:1b,PersistenceRequired:1b,Invulnerable:1b}
execute as @p at @s run summon nexo:t_rex ~13 ~ ~18 {NoAI:1b,PersistenceRequired:1b,Invulnerable:1b}
say MOBS_LISTOS
schedule function prueba:vista1 100t
