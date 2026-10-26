package com.coaljetpack;

import com.mojang.serialization.Codec;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoalJetpack implements ModInitializer {
	public static final String MOD_ID = "coaljetpack";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final DataComponentType<Integer> FUEL = Registry.register(
		BuiltInRegistries.DATA_COMPONENT_TYPE,
		id("fuel"),
		DataComponentType.<Integer>builder().persistent(Codec.intRange(0, Integer.MAX_VALUE)).networkSynchronized(ByteBufCodecs.VAR_INT).build()
	);
	public static final ResourceKey<Item> JETPACK_KEY = ResourceKey.create(Registries.ITEM, id("jetpack"));
	public static final Item JETPACK = Registry.register(
		BuiltInRegistries.ITEM, JETPACK_KEY, new JetpackItem(new Properties().setId(JETPACK_KEY).stacksTo(1).equippable(EquipmentSlot.CHEST).component(FUEL, 0))
	);

	@Override
	public void onInitialize() {
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(tab -> tab.accept(JETPACK));
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (ServerPlayer player : server.getPlayerList().getPlayers()) {
				JetpackFlight.tick(player);
			}
		});
		AccessorySlots.logDetected();
		LOGGER.info("Jetpack loaded");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
