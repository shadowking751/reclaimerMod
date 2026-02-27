package com.example.reclaimer.client;

import com.example.reclaimer.ReclaimerMod;
import com.example.reclaimer.entity.ReclaimerEndermanEntity;
import net.minecraft.client.render.entity.EndermanEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

public class ReclaimerEndermanRenderer extends EndermanEntityRenderer<ReclaimerEndermanEntity> {

    private static final Identifier TEXTURE =
            new Identifier(ReclaimerMod.MOD_ID, "textures/entity/reclaimer_enderman.png");

    public ReclaimerEndermanRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public Identifier getTexture(ReclaimerEndermanEntity entity) {
        return TEXTURE;
    }
}
