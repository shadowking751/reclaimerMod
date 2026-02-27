package com.example.reclaimer.client.model;

import com.example.reclaimer.entity.ReclaimerEndermanEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EndermanEntityModel;

public class ReclaimerEndermanModel extends EndermanEntityModel<ReclaimerEndermanEntity> {

    public ReclaimerEndermanModel(ModelPart root) {
        super(root);
    }

    public static TexturedModelData getTexturedModelData() {
        return EndermanEntityModel.getTexturedModelData();
    }
}
