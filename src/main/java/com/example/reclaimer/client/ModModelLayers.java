package com.example.reclaimer.client;

import com.example.reclaimer.ReclaimerMod;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public class ModModelLayers {
    public static final ModelLayerLocation RECLAIMER_ENDERMAN =
            new ModelLayerLocation(
                    new ResourceLocation(ReclaimerMod.MOD_ID, "reclaimer_enderman"),
                    "main"
            );
}
