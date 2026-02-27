package com.example.reclaimer.client;

import com.example.reclaimer.ModEntities;
import com.example.reclaimer.client.model.ReclaimerEndermanModel;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class ReclaimerModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        // Model layer for the Reclaimer Enderman
        EntityModelLayerRegistry.registerModelLayer(
                ModModelLayers.RECLAIMER_ENDERMAN,
                ReclaimerEndermanModel::getTexturedModelData
        );

        // Renderer for the Reclaimer Enderman
        EntityRendererRegistry.register(
                ModEntities.RECLAIMER_ENDERMAN,
                ReclaimerEndermanRenderer::new
        );
    }
}
