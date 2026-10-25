package com.coaljetpack;

import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class JetpackItem extends Item {
	public JetpackItem(Item.Properties properties) {
		super(properties);
	}

	public static int getFuel(ItemStack stack) {
		return stack.getOrDefault(CoalJetpack.FUEL, 0);
	}

	public static void setFuel(ItemStack stack, int fuel) {
		stack.set(CoalJetpack.FUEL, Mth.clamp(fuel, 0, JetpackConfig.MAX_FUEL));
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return true;
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		return Math.round(13.0F * getFuel(stack) / JetpackConfig.MAX_FUEL);
	}

	@Override
	public int getBarColor(ItemStack stack) {
		return 0xFF8A1E;
	}
}
