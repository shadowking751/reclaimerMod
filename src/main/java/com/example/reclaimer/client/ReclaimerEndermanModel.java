package com.example.reclaimer.client;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.EndermanEntityModel;
import net.minecraft.client.render.entity.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.ModelPartData;

import com.example.reclaimer.entity.ReclaimerEndermanEntity;

public class ReclaimerEndermanModel extends EndermanEntityModel<ReclaimerEndermanEntity> {
    public ReclaimerEndermanModel(ModelPart root) {
        super(root);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = EndermanEntityModel.getModelData(new Dilation(0.0F));
        ModelPartData root = modelData.getRoot();

        // You can customize parts here if you want

        return TexturedModelData.of(modelData, 64, 32);
    }
}
