package com.example.reclaimer.client;

import com.example.reclaimer.ModEntities;
import com.example.reclaimer.client.model.ReclaimerEndermanModel;
import com.example.reclaimer.client.render.ReclaimerEndermanRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class ReclaimerModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        // Register model layer for the Reclaimer Enderman
        EntityModelLayerRegistry.registerModelLayer(
                ModModelLayers.RECLAIMER_ENDERMAN,
                ReclaimerEndermanModel::createBodyLayer
        );

        // Register renderer for the Reclaimer Enderman
        EntityRendererRegistry.register(
                ModEntities.RECLAIMER_ENDERMAN,
                ReclaimerEndermanRenderer::new
        );
    }
}
