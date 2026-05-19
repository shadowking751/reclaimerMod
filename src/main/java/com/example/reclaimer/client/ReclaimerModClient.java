package com.example.reclaimer.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class ReclaimerModClient implements ClientModInitializer {

    public static final EntityModelLayer RECLAIMER_ENDERMAN_LAYER =
            new EntityModelLayer(new Identifier("reclaimer", "enderman"), "main");

    @Override
    public void onInitializeClient() {

        EntityModelLayerRegistry.registerModelLayer(
                RECLAIMER_ENDERMAN_LAYER,
                ReclaimerEndermanModel::getTexturedModelData
        );

        EntityRendererRegistry.register(
                ReclaimerMod.RECLAIMER_ENDERMAN_ENTITY,
                ReclaimerEndermanRenderer::new
        );
    }
}
