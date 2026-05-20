package com.example.reclaimer.client;

import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

import com.example.reclaimer.ReclaimerMod;

public class ModModelLayers {
    public static final EntityModelLayer RECLAIMER_ENDERMAN_LAYER =
            new EntityModelLayer(
                    new Identifier(ReclaimerMod.MOD_ID, "reclaimer_enderman"),
                    "main"
            );
}
