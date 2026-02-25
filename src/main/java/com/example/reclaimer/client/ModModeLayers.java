package com.example.reclaimer.client;

import com.example.reclaimer.ReclaimerMod;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class ModModelLayers {
    public static final EntityModelLayer RECLAIMER_ENDERMAN =
            new EntityModelLayer(new Identifier(ReclaimerMod.MOD_ID, "reclaimer_enderman"), "main");
}
