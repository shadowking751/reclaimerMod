package com.example.reclaimer;

import com.example.reclaimer.entity.ReclaimerEndermanEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Registry;

public class ModEntities {

    public static final EntityType<ReclaimerEndermanEntity> RECLAIMER_ENDERMAN =
            Registry.register(
                    Registries.ENTITY_TYPE,
                    new ResourceLocation(ReclaimerMod.MOD_ID, "reclaimer_enderman"),
                    FabricEntityTypeBuilder.createMob()
                            .entityFactory(ReclaimerEndermanEntity::new)
                            .spawnGroup(MobCategory.MONSTER)
                            .dimensions(EntityDimensions.scalable(0.6f, 2.9f))
                            .build()
            );

    public static void register() {
        // static initializer handles registration
    }
}
