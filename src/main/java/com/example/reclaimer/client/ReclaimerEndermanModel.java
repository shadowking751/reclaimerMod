package com.example.reclaimer.client;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.EndermanEntityModel;
import net.minecraft.client.render.entity.model.TexturedModelData;

public class ReclaimerEndermanModel extends EndermanEntityModel<ReclaimerEndermanEntity> {

    public ReclaimerEndermanModel(ModelPart root) {
        super(root);
    }

    public static TexturedModelData getTexturedModelData() {
        return EndermanEntityModel.getTexturedModelData();
    }
}
