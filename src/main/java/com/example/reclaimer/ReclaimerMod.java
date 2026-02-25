package com.example.reclaimer;

import com.example.reclaimer.config.ReclaimerConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

public class ReclaimerMod implements ModInitializer {

    public static final String MOD_ID = "reclaimer";
    public static ReclaimerConfig CONFIG;

    @Override
    public void onInitialize() {

        // Load config
        AutoConfig.register(ReclaimerConfig.class, GsonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(ReclaimerConfig.class).getConfig();

        // Register entities + attributes
        ModEntities.register();
        FabricDefaultAttributeRegistry.register(
                ModEntities.RECLAIMER_ENDERMAN,
                com.example.reclaimer.entity.ReclaimerEndermanEntity.createReclaimerAttributes()
        );
    }
}
