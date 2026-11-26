package ar.hombrosafio.ruleta;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.monster.zombie.ZombifiedPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;

/**
 * Cambios de dificultad activos de la ruleta amarilla. Se guardan por mundo
 * ({@code <mundo>/ruleta/difficulty-changes.json}) y se reaplican al arrancar el servidor.
 */
public final class DifficultyChanges {
	private static final Gson JSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Set<DifficultyChange> ACTIVE = EnumSet.noneOf(DifficultyChange.class);
	private static final Map<DifficultyChange, Long> SINCE = new EnumMap<>(DifficultyChange.class);
	private static Path savePath;
	private static MinecraftServer server;
	private static final Map<EquipmentSlot, Item> DIAMOND_ARMOR = Map.of(
		EquipmentSlot.HEAD, Items.DIAMOND_HELMET,
		EquipmentSlot.CHEST, Items.DIAMOND_CHESTPLATE,
		EquipmentSlot.LEGS, Items.DIAMOND_LEGGINGS,
		EquipmentSlot.FEET, Items.DIAMOND_BOOTS);

	public static void register() {
		ServerLifecycleEvents.SERVER_STARTED.register(DifficultyChanges::load);
		ServerLifecycleEvents.SERVER_STOPPING.register(s -> save());
		ServerLifecycleEvents.SERVER_STOPPED.register(s -> {
			server = null;
			ACTIVE.clear();
			SINCE.clear();
		});
		ServerPlayConnectionEvents.JOIN.register((handler, sender, s) -> sync(handler.player));
		ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> {
			if (entity instanceof Spider spider) {
				applySpiderEffects(spider);
			}
			if (isRouletteZombie(entity)) {
				applyZombieSpeed((Zombie) entity);
				applyZombieArmor((Zombie) entity);
			}
		});
		UseItemCallback.EVENT.register((player, level, hand) ->
			!level.isClientSide() && blocksMilk(player.getItemInHand(hand)) ? InteractionResult.FAIL : InteractionResult.PASS);
		UseEntityCallback.EVENT.register((player, level, hand, entity, hit) ->
			!level.isClientSide() && blocksMilking(entity, player.getItemInHand(hand)) ? InteractionResult.FAIL : InteractionResult.PASS);
	}

	// ------------------------------------------------------------------ estado

	public static boolean isActive(DifficultyChange change) {
		return ACTIVE.contains(change);
	}

	public static boolean activate(MinecraftServer server, DifficultyChange change) {
		if (change == DifficultyChange.TEXT || !ACTIVE.add(change)) {
			return false;
		}
		SINCE.put(change, System.currentTimeMillis());
		apply(server, change);
		save();
		syncAll();
		return true;
	}

	public static boolean deactivate(MinecraftServer server, DifficultyChange change) {
		if (!ACTIVE.remove(change)) {
			return false;
		}
		SINCE.remove(change);
		if (change == DifficultyChange.NATURAL_REGEN_OFF) {
			for (ServerLevel level : server.getAllLevels()) {
				level.getGameRules().set(GameRules.NATURAL_HEALTH_REGENERATION, true, server);
			}
		}
		List<Mob> mobs = new ArrayList<>();
		for (ServerLevel level : server.getAllLevels()) {
			for (Entity entity : level.getAllEntities()) {
				if (entity instanceof Spider || isRouletteZombie(entity)) {
					mobs.add((Mob) entity);
				}
			}
		}
		for (Mob mob : mobs) {
			undo(mob, change);
		}
		save();
		syncAll();
		return true;
	}

	public static List<DifficultyChange> active() {
		List<DifficultyChange> list = new ArrayList<>(ACTIVE);
		list.sort(Comparator.<DifficultyChange>comparingLong(c -> SINCE.getOrDefault(c, 0L)).reversed());
		return list;
	}

	private static void sync(ServerPlayer player) {
		if (ServerPlayNetworking.canSend(player, ActiveChangesPayload.TYPE)) {
			List<String> ids = new ArrayList<>();
			List<Long> since = new ArrayList<>();
			for (DifficultyChange c : active()) {
				ids.add(c.id);
				since.add(SINCE.getOrDefault(c, 0L));
			}
			ServerPlayNetworking.send(player, new ActiveChangesPayload(ids, since));
		}
	}

	private static void syncAll() {
		if (server != null) {
			for (ServerPlayer p : server.getPlayerList().getPlayers()) {
				sync(p);
			}
		}
	}

	private static void load(MinecraftServer s) {
		server = s;
		ACTIVE.clear();
		SINCE.clear();
		savePath = s.getWorldPath(LevelResource.ROOT).resolve("ruleta/difficulty-changes.json");
		if (Files.exists(savePath)) {
			try {
				JsonObject saved = JSON.fromJson(Files.readString(savePath), JsonObject.class);
				if (saved != null) {
					for (Map.Entry<String, com.google.gson.JsonElement> e : saved.entrySet()) {
						DifficultyChange c = DifficultyChange.byId(e.getKey());
						if (c != null && c != DifficultyChange.TEXT) {
							ACTIVE.add(c);
							SINCE.put(c, e.getValue().getAsLong());
						}
					}
				}
			} catch (IOException | RuntimeException e) {
				System.err.println("[ruleta] No se pudieron cargar los cambios de dificultad: " + e);
			}
		}
		for (DifficultyChange c : ACTIVE) {
			apply(s, c);
		}
	}

	private static void save() {
		if (savePath == null) {
			return;
		}
		JsonObject json = new JsonObject();
		for (DifficultyChange c : ACTIVE) {
			json.addProperty(c.id, SINCE.getOrDefault(c, System.currentTimeMillis()));
		}
		try {
			Files.createDirectories(savePath.getParent());
			Files.writeString(savePath, JSON.toJson(json));
		} catch (IOException e) {
			System.err.println("[ruleta] No se pudieron guardar los cambios de dificultad: " + e);
		}
	}

	// ------------------------------------------------------------------ aplicar / deshacer

	private static void apply(MinecraftServer server, DifficultyChange change) {
		if (change == DifficultyChange.NATURAL_REGEN_OFF) {
			for (ServerLevel level : server.getAllLevels()) {
				level.getGameRules().set(GameRules.NATURAL_HEALTH_REGENERATION, false, server);
			}
		}
		if (change == DifficultyChange.SPIDERS_STRENGTH_3 || change == DifficultyChange.SPIDERS_STRENGTH_4 || change == DifficultyChange.SPIDERS_SPEED_1) {
			for (ServerLevel level : server.getAllLevels()) {
				for (Entity entity : level.getAllEntities()) {
					if (entity instanceof Spider spider) {
						applySpiderEffects(spider);
					}
				}
			}
		}
		if (change == DifficultyChange.ZOMBIES_FAST || change == DifficultyChange.ZOMBIES_DIAMOND) {
			List<Zombie> zombies = new ArrayList<>();
			for (ServerLevel level : server.getAllLevels()) {
				for (Entity entity : level.getAllEntities()) {
					if (isRouletteZombie(entity)) {
						zombies.add((Zombie) entity);
					}
				}
			}
			for (Zombie z : zombies) {
				if (change == DifficultyChange.ZOMBIES_FAST) {
					applyZombieSpeed(z);
				} else {
					applyZombieArmor(z);
				}
			}
		}
	}

	private static void undo(Mob mob, DifficultyChange change) {
		if (mob instanceof Spider spider) {
			if (change == DifficultyChange.SPIDERS_SPEED_1) {
				removeEndless(spider, MobEffects.SPEED, RuletaConfig.getInt("diff.spider_speed") - 1);
			}
		} else if (mob instanceof Zombie zombie) {
			if (change == DifficultyChange.ZOMBIES_FAST) {
				removeEndless(zombie, MobEffects.SPEED, RuletaConfig.getInt("diff.zombie_speed") - 1);
			}
			if (change == DifficultyChange.ZOMBIES_DIAMOND) {
				for (Map.Entry<EquipmentSlot, Item> piece : DIAMOND_ARMOR.entrySet()) {
					if (zombie.getItemBySlot(piece.getKey()).is(piece.getValue()) && zombie.getDropChances().byEquipment(piece.getKey()) == 0.0F) {
						zombie.setItemSlot(piece.getKey(), ItemStack.EMPTY);
					}
				}
			}
		}
	}

	private static void removeEndless(LivingEntity mob, Holder<MobEffect> effect, int amplifier) {
		MobEffectInstance current = mob.getEffect(effect);
		if (current != null && current.getAmplifier() == amplifier && (current.isInfiniteDuration() || current.getDuration() > 1_000_000)) {
			mob.removeEffect(effect);
		}
	}

	public static boolean isRouletteZombie(Entity entity) {
		return entity instanceof Zombie && !(entity instanceof ZombifiedPiglin);
	}

	private static void applyZombieSpeed(Zombie zombie) {
		if (ACTIVE.contains(DifficultyChange.ZOMBIES_FAST)) {
			int amp = RuletaConfig.getInt("diff.zombie_speed") - 1;
			MobEffectInstance speed = zombie.getEffect(MobEffects.SPEED);
			if (speed == null || speed.getAmplifier() < amp) {
				zombie.addEffect(new MobEffectInstance(MobEffects.SPEED, Integer.MAX_VALUE, amp, true, false));
			}
		}
	}

	private static void applyZombieArmor(Zombie zombie) {
		if (ACTIVE.contains(DifficultyChange.ZOMBIES_DIAMOND)) {
			Holder.Reference<Enchantment> protection = zombie.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.PROTECTION);
			for (Map.Entry<EquipmentSlot, Item> piece : DIAMOND_ARMOR.entrySet()) {
				EquipmentSlot slot = piece.getKey();
				ItemStack worn = zombie.getItemBySlot(slot);
				if ((!zombie.getDropChances().isPreserved(slot) || worn.isEmpty())
					&& (!worn.is(piece.getValue()) || EnchantmentHelper.getItemEnchantmentLevel(protection, worn) < 4
						|| zombie.getDropChances().byEquipment(slot) != 0.0F)) {
					ItemStack stack = new ItemStack((ItemLike) piece.getValue());
					stack.enchant(protection, 4);
					zombie.setItemSlot(slot, stack);
					zombie.setDropChance(slot, 0.0F);
				}
			}
		}
	}

	private static void applySpiderEffects(Spider spider) {
		boolean s3 = ACTIVE.contains(DifficultyChange.SPIDERS_STRENGTH_3);
		boolean s4 = ACTIVE.contains(DifficultyChange.SPIDERS_STRENGTH_4);
		if (s3 || s4) {
			int amp = s4 ? 3 : 2;
			MobEffectInstance current = spider.getEffect(MobEffects.STRENGTH);
			if (current == null || current.getAmplifier() < amp || current.getDuration() < 40) {
				spider.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 60, amp, true, false));
			}
		}
		if (ACTIVE.contains(DifficultyChange.SPIDERS_SPEED_1)) {
			int amp = RuletaConfig.getInt("diff.spider_speed") - 1;
			MobEffectInstance speed = spider.getEffect(MobEffects.SPEED);
			if (speed == null || speed.getAmplifier() < amp || speed.getDuration() < 40) {
				spider.addEffect(new MobEffectInstance(MobEffects.SPEED, Integer.MAX_VALUE, amp, true, false));
			}
		}
	}

	// ------------------------------------------------------------------ hooks que usan los mixins

	public static float modifyDamage(DamageSource source, float amount) {
		if (ACTIVE.isEmpty()) {
			return amount;
		}
		if (source.is(DamageTypes.HOT_FLOOR) && ACTIVE.contains(DifficultyChange.MAGMA_INSTANT_KILL)) {
			return 10000.0F;
		}
		if (source.is(DamageTypes.ENDER_PEARL) && ACTIVE.contains(DifficultyChange.PEARL_DAMAGE_10)) {
			amount = RuletaConfig.getInt("diff.pearl_damage");
		}
		if (source.is(DamageTypes.DROWN) && ACTIVE.contains(DifficultyChange.DROWNING_X10)) {
			amount *= RuletaConfig.getInt("diff.drown_mult");
		}
		if (source.is(DamageTypeTags.IS_FIRE) || source.is(DamageTypes.HOT_FLOOR)) {
			if (ACTIVE.contains(DifficultyChange.FIRE_DAMAGE_X10)) {
				amount *= RuletaConfig.getInt("diff.fire_mult_hard");
			} else if (ACTIVE.contains(DifficultyChange.FIRE_DAMAGE_X3)) {
				amount *= RuletaConfig.getInt("diff.fire_mult_soft");
			}
		}
		if (source.is(DamageTypes.FALL) && ACTIVE.contains(DifficultyChange.FALL_DAMAGE_X2)) {
			amount *= RuletaConfig.getInt("diff.fall_mult");
		}
		return amount;
	}

	public static boolean ignoresArmor(DamageSource source) {
		return ACTIVE.contains(DifficultyChange.PEARL_BYPASS_ARMOR) && source.is(DamageTypes.ENDER_PEARL);
	}

	public static boolean blocksMilk(ItemStack stack) {
		return ACTIVE.contains(DifficultyChange.MILK_DISABLED) && stack.is(Items.MILK_BUCKET);
	}

	public static boolean blocksMilking(Entity entity, ItemStack held) {
		return ACTIVE.contains(DifficultyChange.COWS_NO_MILK)
			&& (entity.getType() == EntityTypes.COW || entity.getType() == EntityTypes.MOOSHROOM) && held.is(Items.BUCKET);
	}

	public static boolean golemsDisabled(Entity entity) {
		return ACTIVE.contains(DifficultyChange.GOLEMS_DISABLED)
			&& (entity.getType() == EntityTypes.IRON_GOLEM || entity.getType() == EntityTypes.SNOW_GOLEM);
	}

	public static int airAfterVanilla(int remainingAir) {
		return ACTIVE.contains(DifficultyChange.AIR_X5) ? remainingAir - (RuletaConfig.getInt("diff.air_mult") - 1) : remainingAir;
	}

	public static boolean zombiesAnyLight() {
		return ACTIVE.contains(DifficultyChange.ZOMBIES_ANY_LIGHT);
	}

	public static boolean creepersAnyLight() {
		return ACTIVE.contains(DifficultyChange.CREEPERS_ANY_LIGHT);
	}

	public static boolean suppressSapling(ItemStack stack) {
		return ACTIVE.contains(DifficultyChange.NO_SAPLINGS) && stack.is(ItemTags.SAPLINGS);
	}

	public static boolean shouldBlockDoorAir(LivingEntity entity) {
		if (ACTIVE.contains(DifficultyChange.NO_DOOR_AIR) && entity instanceof Player && !entity.isEyeInFluid(FluidTags.WATER)) {
			Level level = entity.level();
			BlockPos origin = entity.blockPosition();
			AABB bounds = entity.getBoundingBox();
			boolean nearbyDoor = false;
			boolean bodyTouchesWater = false;
			for (int x = -2; x <= 2; x++) {
				for (int y = 0; y <= 2; y++) {
					for (int z = -2; z <= 2; z++) {
						BlockPos pos = origin.offset(x, y, z);
						BlockState state = level.getBlockState(pos);
						if (state.getBlock() instanceof DoorBlock || state.getBlock() instanceof TrapDoorBlock) {
							nearbyDoor = true;
						}
						if (level.getFluidState(pos).is(FluidTags.WATER)) {
							AABB cell = new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
							if (bounds.intersects(cell)) {
								bodyTouchesWater = true;
							}
						}
					}
				}
			}
			return nearbyDoor && bodyTouchesWater;
		}
		return false;
	}

	private DifficultyChanges() {
	}
}
