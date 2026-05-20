package com.example.reclaimer;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;

import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import com.example.reclaimer.entity.ReclaimerEndermanEntity;

public class ModEntities {
    public static final EntityType<ReclaimerEndermanEntity> RECLAIMER_ENDERMAN =
            Registry.register(
                    Registries.ENTITY_TYPE,
                    new Identifier(ReclaimerMod.MOD_ID, "reclaimer_enderman"),
                    FabricEntityTypeBuilder
                            .create(SpawnGroup.MONSTER, ReclaimerEndermanEntity::new)
                            .dimensions(EntityDimensions.fixed(0.6F, 2.9F))
                            .build()
            );

    public static void registerAttributes() {
        FabricDefaultAttributeRegistry.register(
                RECLAIMER_ENDERMAN,
                ReclaimerEndermanEntity.createEndermanAttributes()
        );
    }
}
