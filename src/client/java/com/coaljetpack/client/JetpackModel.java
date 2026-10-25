package com.coaljetpack.client;

import com.coaljetpack.CoalJetpack;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/** The jetpack as worn on the back, in the body's coordinates (pixels; +z is the player's back). */
public final class JetpackModel {
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(CoalJetpack.id("jetpack"), "main");

	private JetpackModel() {
	}

	public static LayerDefinition createLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		root.addOrReplaceChild("pack", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, 1.0F, 2.0F, 6.0F, 8.0F, 2.0F), PartPose.ZERO);
		root.addOrReplaceChild(
			"tanks",
			CubeListBuilder.create()
				.texOffs(0, 12).addBox(-5.5F, 0.0F, 2.5F, 3.0F, 10.0F, 3.0F)
				.texOffs(0, 12).mirror().addBox(2.5F, 0.0F, 2.5F, 3.0F, 10.0F, 3.0F),
			PartPose.ZERO
		);
		root.addOrReplaceChild(
			"nozzles",
			CubeListBuilder.create()
				.texOffs(16, 0).addBox(-5.0F, 10.0F, 3.0F, 2.0F, 2.0F, 2.0F)
				.texOffs(16, 0).addBox(3.0F, 10.0F, 3.0F, 2.0F, 2.0F, 2.0F),
			PartPose.ZERO
		);
		return LayerDefinition.create(mesh, 64, 32);
	}
}
