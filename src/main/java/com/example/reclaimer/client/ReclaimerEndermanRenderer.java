package com.example.reclaimer.client;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EndermanEntityModel;
import net.minecraft.util.Identifier;

public class ReclaimerEndermanRenderer
        extends MobEntityRenderer<ReclaimerEndermanEntity, EndermanEntityModel<ReclaimerEndermanEntity>> {

    private static final Identifier TEXTURE =
            new Identifier("reclaimer", "textures/entity/reclaimer_enderman.png");

    public ReclaimerEndermanRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new EndermanEntityModel<>(ctx.getPart(ReclaimerModClient.RECLAIMER_ENDERMAN_LAYER)), 0.5f);
    }

    @Override
    public Identifier getTexture(ReclaimerEndermanEntity entity) {
        return TEXTURE;
    }
}
