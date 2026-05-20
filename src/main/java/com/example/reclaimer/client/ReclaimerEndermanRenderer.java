package com.example.reclaimer.client;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

import com.example.reclaimer.ReclaimerMod;
import com.example.reclaimer.entity.ReclaimerEndermanEntity;

public class ReclaimerEndermanRenderer
        extends MobEntityRenderer<ReclaimerEndermanEntity, ReclaimerEndermanModel> {

    private static final Identifier TEXTURE =
            new Identifier(ReclaimerMod.MOD_ID, "textures/entity/reclaimer_enderman.png");

    public ReclaimerEndermanRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new ReclaimerEndermanModel(
                ctx.getPart(ModModelLayers.RECLAIMER_ENDERMAN_LAYER)
        ), 0.5F);
    }

    @Override
    public Identifier getTexture(ReclaimerEndermanEntity entity) {
        return TEXTURE;
    }
}
