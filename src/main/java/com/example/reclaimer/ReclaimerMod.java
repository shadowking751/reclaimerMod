package com.example.reclaimer;

import net.fabricmc.api.ModInitializer;

public class ReclaimerMod implements ModInitializer {
    public static final String MOD_ID = "reclaimer";

    @Override
    public void onInitialize() {
        ModEntities.registerAttributes();
        System.out.println("Reclaimer Mod initialized");
    }
}
