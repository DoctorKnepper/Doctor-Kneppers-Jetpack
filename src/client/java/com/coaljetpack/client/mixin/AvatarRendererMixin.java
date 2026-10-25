package com.coaljetpack.client.mixin;

import com.coaljetpack.AccessorySlots;
import com.coaljetpack.CoalJetpack;
import com.coaljetpack.client.JetpackLayer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
public abstract class AvatarRendererMixin {
	private static final boolean HAS_ACCESSORY_MODS = FabricLoader.getInstance().isModLoaded("accessories")
		|| FabricLoader.getInstance().isModLoaded("trinkets");

	@Inject(
		method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V",
		at = @At("TAIL")
	)
	private void coaljetpack$markJetpack(Avatar avatar, AvatarRenderState state, float partialTicks, CallbackInfo ci) {
		// Not state.chestEquipment: vanilla only copies chest items that have an armor model.
		boolean worn = avatar.getItemBySlot(EquipmentSlot.CHEST).is(CoalJetpack.JETPACK)
			|| HAS_ACCESSORY_MODS && avatar instanceof Player player && AccessorySlots.findWornJetpack(player) != null;
		state.setData(JetpackLayer.WORN, worn);
	}
}
