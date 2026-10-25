package com.coaljetpack;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class JetpackFlight {
	private JetpackFlight() {
	}

	public static void tick(Player player) {
		if (player.isCreative() || player.isSpectator()) {
			return;
		}

		Abilities abilities = player.getAbilities();
		ItemStack jetpack = AccessorySlots.findWornJetpack(player);
		boolean changed = false;
		boolean powered = jetpack != null && (JetpackItem.getFuel(jetpack) > 0 || refuelFromInventory(player, jetpack));
		if (powered) {
			if (!abilities.mayfly) {
				abilities.mayfly = true;
				changed = true;
			}

			if (abilities.flying && !player.onGround()) {
				burn(player, jetpack);
			}
		} else if (abilities.mayfly) {
			boolean wasFlying = abilities.flying;
			abilities.mayfly = false;
			abilities.flying = false;
			changed = true;
			if (wasFlying) {
				player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 0.8F, 1.0F);
				if (jetpack != null && player instanceof ServerPlayer serverPlayer) {
					serverPlayer.sendSystemMessage(Component.literal("Jetpack out of coal!").withStyle(ChatFormatting.RED), true);
				}
			}
		}

		if (changed) {
			player.onUpdateAbilities();
		}
	}

	private static void burn(Player player, ItemStack jetpack) {
		boolean moving = player.getDeltaMovement().horizontalDistanceSqr() > 1.0E-4 || Math.abs(player.getDeltaMovement().y) > 0.01;
		int cost = moving ? JetpackConfig.MOVING_COST : JetpackConfig.HOVER_COST;
		int fuel = JetpackItem.getFuel(jetpack) - cost;
		JetpackItem.setFuel(jetpack, fuel);
		if (fuel <= 0) {
			refuelFromInventory(player, jetpack);
		}

		if (player.tickCount % 2 == 0 && player.level() instanceof ServerLevel level) {
			double yaw = Math.toRadians(player.getYRot());
			double x = player.getX() + Math.sin(yaw) * 0.35;
			double z = player.getZ() - Math.cos(yaw) * 0.35;
			double y = player.getY() + 0.7;
			level.sendParticles(ParticleTypes.SMOKE, x, y, z, 2, 0.08, 0.05, 0.08, 0.01);
			if (moving) {
				level.sendParticles(ParticleTypes.FLAME, x, y - 0.1, z, 1, 0.04, 0.02, 0.04, 0.005);
			}
		}
	}

	private static boolean refuelFromInventory(Player player, ItemStack jetpack) {
		Inventory inventory = player.getInventory();

		for (int i = 0; i < inventory.getContainerSize(); i++) {
			ItemStack stack = inventory.getItem(i);
			if (stack.isEmpty() || stack == jetpack) {
				continue;
			}

			int value = 0;
			if (stack.is(ItemTags.COALS)) {
				value = JetpackConfig.TICKS_PER_COAL;
			} else if (stack.is(Items.COAL_BLOCK)) {
				value = JetpackConfig.TICKS_PER_COAL_BLOCK;
			}

			if (value > 0) {
				stack.shrink(1);
				JetpackItem.setFuel(jetpack, JetpackItem.getFuel(jetpack) + value);
				inventory.setChanged();
				return true;
			}
		}

		return false;
	}
}
