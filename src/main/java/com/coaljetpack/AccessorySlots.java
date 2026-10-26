package com.coaljetpack;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class AccessorySlots {
	private static final String[][] PROVIDERS = new String[][]{
		{"accessories", "io.wispforest.accessories.api.AccessoriesCapability", "get", "getEquipped"},
		{"trinkets", "eu.pb4.trinkets.api.TrinketsApi", "getAttachment", "findFirst"},
		{"trinkets", "dev.emi.trinkets.api.TrinketsApi", "getTrinketComponent", "getEquipped"}
	};
	private static boolean warned = false;

	private AccessorySlots() {
	}

	public static void logDetected() {
		for (String[] p : PROVIDERS) {
			if (FabricLoader.getInstance().isModLoaded(p[0]) && classExists(p[1])) {
				CoalJetpack.LOGGER.info("Jetpack: {} detected ({}), jetpack can be worn in its slots", p[0], p[1]);
			}
		}
	}

	private static boolean classExists(String name) {
		try {
			Class.forName(name, false, AccessorySlots.class.getClassLoader());
			return true;
		} catch (ClassNotFoundException | LinkageError e) {
			return false;
		}
	}

	public static ItemStack findWornJetpack(Player player) {
		for (String[] p : PROVIDERS) {
			if (!FabricLoader.getInstance().isModLoaded(p[0]) || !classExists(p[1])) {
				continue;
			}

			try {
				ItemStack found = lookup(player, p[1], p[2], p[3]);
				if (found != null) {
					return found;
				}
			} catch (Throwable t) {
				if (!warned) {
					warned = true;
					CoalJetpack.LOGGER.warn("Jetpack: couldn't read {} slots ({}); chest slot still works", p[0], t.toString());
				}
			}
		}

		ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
		return chest.is(CoalJetpack.JETPACK) ? chest : null;
	}

	private static ItemStack lookup(Player player, String className, String getter, String equippedMethod) throws Exception {
		Class<?> api = Class.forName(className);
		Method get = findMethod(api, getter, true, LivingEntity.class);
		if (get == null) {
			return null;
		}

		Object holder = unwrap(get.invoke(null, player));
		if (holder == null) {
			return null;
		}

		Method equipped = findMethod(holder.getClass(), equippedMethod, false, Item.class);
		if (equipped == null) {
			return null;
		}

		equipped.setAccessible(true);
		return firstJetpack(equipped.invoke(holder, CoalJetpack.JETPACK), 0);
	}

	private static Method findMethod(Class<?> type, String name, boolean isStatic, Class<?> argType) {
		for (Method m : type.getMethods()) {
			if (m.getName().equals(name)
				&& Modifier.isStatic(m.getModifiers()) == isStatic
				&& m.getParameterCount() == 1
				&& m.getParameterTypes()[0].isAssignableFrom(argType)) {
				return m;
			}
		}

		return null;
	}

	private static Object unwrap(Object o) {
		return o instanceof Optional<?> opt ? opt.orElse(null) : o;
	}

	private static ItemStack firstJetpack(Object o, int depth) {
		o = unwrap(o);
		if (o == null || depth > 3) {
			return null;
		}

		if (o instanceof ItemStack stack) {
			return stack.is(CoalJetpack.JETPACK) ? stack : null;
		}

		if (o instanceof Collection<?> collection) {
			for (Object e : collection) {
				ItemStack s = firstJetpack(e, depth + 1);
				if (s != null) {
					return s;
				}
			}

			return null;
		}

		Method[] methods = o.getClass().getMethods();
		Arrays.sort(methods, Comparator.comparingInt(mx -> {
			return switch (mx.getName()) {
				case "stack", "getStack", "getB", "get" -> 0;
				default -> mx.getName().toLowerCase().contains("copy") ? 2 : 1;
			};
		}));

		for (Method m : methods) {
			if (m.getName().toLowerCase().contains("copy")) {
				continue;
			}

			if (m.getParameterCount() == 0
				&& (ItemStack.class.isAssignableFrom(m.getReturnType()) || m.getReturnType() == Object.class)) {
				try {
					ItemStack s = firstJetpack(m.invoke(o), depth + 1);
					if (s != null) {
						return s;
					}
				} catch (Exception ignored) {
				}
			}
		}

		return null;
	}
}
