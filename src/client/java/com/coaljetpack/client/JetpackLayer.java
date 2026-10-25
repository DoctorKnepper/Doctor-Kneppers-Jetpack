package com.coaljetpack.client;

import com.coaljetpack.CoalJetpack;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

/** Draws the jetpack on the back of players wearing one (chest slot, Accessories or Trinkets). */
public class JetpackLayer extends RenderLayer<AvatarRenderState, PlayerModel> {
	/** Set by {@link com.coaljetpack.client.mixin.AvatarRendererMixin} while extracting the render state. */
	public static final RenderStateDataKey<Boolean> WORN = RenderStateDataKey.create(() -> "coaljetpack:jetpack_worn");
	private static final Identifier TEXTURE = CoalJetpack.id("textures/entity/jetpack.png");
	private final ModelPart model;

	public JetpackLayer(RenderLayerParent<AvatarRenderState, PlayerModel> parent, ModelPart model) {
		super(parent);
		this.model = model;
	}

	@Override
	public void submit(PoseStack poseStack, SubmitNodeCollector collector, int lightCoords, AvatarRenderState state, float yRot, float xRot) {
		if (state.isInvisible || !state.getDataOrDefault(WORN, false)) {
			return;
		}

		poseStack.pushPose();
		this.getParentModel().root().translateAndRotate(poseStack);
		this.getParentModel().body.translateAndRotate(poseStack);
		if (!state.chestEquipment.isEmpty()) {
			// Worn in an accessory slot over a chestplate: sit on top of the armor.
			poseStack.translate(0.0F, 0.0F, 1.0F / 16.0F);
		}

		collector.submitModelPart(this.model, poseStack, RenderTypes.entityCutout(TEXTURE), lightCoords, OverlayTexture.NO_OVERLAY, null);
		poseStack.popPose();
	}
}
