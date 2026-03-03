package com.example.reclaimer.client.model;

import com.example.reclaimer.entity.ReclaimerEndermanEntity;
import net.minecraft.client.model.EnderManModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;

public class ReclaimerEndermanModel extends EnderManModel<ReclaimerEndermanEntity> {

    public ReclaimerEndermanModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        return EnderManModel.createBodyLayer();
    }
}
