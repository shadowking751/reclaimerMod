package com.example.reclaimer;

import com.example.reclaimer.entity.ReclaimerEndermanEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {

    public static final EntityType<ReclaimerEndermanEntity> RECLAIMER_ENDERMAN =
            Registry.register(
                    Registries.ENTITY_TYPE,
                    new Identifier(ReclaimerMod.MOD_ID, "reclaimer_enderman"),
                    FabricEntityTypeBuilder.createMob()
                            .entityFactory(ReclaimerEndermanEntity::new)
                            .spawnGroup(SpawnGroup.MONSTER)
                            .dimensions(EntityDimensions.fixed(0.6f, 2.9f))
                            .build()
            );

    public static void register() {
        // nothing needed here — static init triggers registration
    }
}
