package com.example.reclaimer.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.world.World;

public class ReclaimerEndermanEntity extends EndermanEntity {
    public ReclaimerEndermanEntity(EntityType<? extends EndermanEntity> type, World world) {
        super(type, world);
    }

    public static DefaultAttributeContainer.Builder createEndermanAttributes() {
        return EndermanEntity.createEndermanAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 10.0D);
    }
}
