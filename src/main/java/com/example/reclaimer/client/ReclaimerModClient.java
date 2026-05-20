package com.example.reclaimer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

import com.example.reclaimer.client.ModModelLayers;
import com.example.reclaimer.client.ReclaimerEndermanModel;
import com.example.reclaimer.client.ReclaimerEndermanRenderer;

public class ReclaimerModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(
                ModEntities.RECLAIMER_ENDERMAN,
                ReclaimerEndermanRenderer::new
        );

        EntityModelLayerRegistry.registerModelLayer(
                ModModelLayers.RECLAIMER_ENDERMAN_LAYER,
                ReclaimerEndermanModel::getTexturedModelData
        );
    }
}
