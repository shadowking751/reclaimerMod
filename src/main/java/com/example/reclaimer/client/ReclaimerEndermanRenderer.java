package com.example.reclaimer.client;

import com.example.reclaimer.ReclaimerMod;
import com.example.reclaimer.entity.ReclaimerEndermanEntity;
import net.minecraft.client.renderer.entity.EnderManRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class ReclaimerEndermanRenderer extends EnderManRenderer<ReclaimerEndermanEntity> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(ReclaimerMod.MOD_ID, "textures/entity/reclaimer_enderman.png");

    public ReclaimerEndermanRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public ResourceLocation getTextureLocation(ReclaimerEndermanEntity entity) {
        return TEXTURE;
    }
}
