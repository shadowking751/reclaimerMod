package com.example.reclaimer;

import com.example.reclaimer.client.ReclaimerEndermanRenderer;
import com.example.reclaimer.client.ModModelLayers;
import com.example.reclaimer.client.model.ReclaimerEndermanModel;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;

public class ReclaimerModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.RECLAIMER_ENDERMAN, ReclaimerEndermanRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(
            ModModelLayers.RECLAIMER_ENDERMAN,
            ReclaimerEndermanModel::getTexturedModelData
        );
    }
}
EntityModelLayerRegistry.registerModelLayer(
    ModModelLayers.RECLAIMER_ENDERMAN,
    ReclaimerEndermanModel::getTexturedModelData
);
