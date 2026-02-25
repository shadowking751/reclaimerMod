package com.example.reclaimer.client;

import com.example.reclaimer.ReclaimerMod;
import com.example.reclaimer.client.model.ReclaimerModel;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;

public class ReclaimerModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        // Register your model layer
        EntityModelLayerRegistry.registerModelLayer(
                ReclaimerModel.LAYER_LOCATION,
                ReclaimerModel::getTexturedModelData
        );

        // Add any other client-side initialization here
    }
}
