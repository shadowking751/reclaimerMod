package com.example.reclaimer.entity;

import com.example.reclaimer.ReclaimerMod;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.util.RandomSource;

import java.util.ArrayDeque;
import java.util.Queue;

public class ReclaimerEndermanEntity extends EnderMan {

    private final Queue<BlockPos> cleanupQueue = new ArrayDeque<>();
    private int scanCooldown = 0;
    private int angle = 0;

    private int recentUnnaturalPlaced = 0;
    private int reactivityCooldown = 0;

    private static final TagKey<Block> UNNATURAL_BLOCKS =
            BlockTags.create(new ResourceLocation("reclaimer", "unnatural_blocks"));
    private static final TagKey<Block> NATURAL_BLOCKS =
            BlockTags.create(new ResourceLocation("reclaimer", "natural_blocks"));
    private static final TagKey<Block> PLAYER_STRUCTURES =
            BlockTags.create(new ResourceLocation("reclaimer", "player_structures"));
    private static final TagKey<Block> STRUCTURE_PROTECTED =
            BlockTags.create(new ResourceLocation("reclaimer", "structure_protected"));

    public ReclaimerEndermanEntity(EntityType<? extends EnderMan> type, Level world) {
        super(type, world);
    }

    public static AttributeSupplier.Builder createReclaimerAttributes() {
        return EnderMan.createAttributes()
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3);
    }

    public static boolean canSpawn(EntityType<ReclaimerEndermanEntity> type,
                                   ServerLevelAccessor world,
                                   MobSpawnType reason,
                                   BlockPos pos,
                                   RandomSource random) {
        return world.getBrightness(LightLayer.BLOCK, pos) <= 7 &&
                world.getBlockState(pos.below()).isSolid();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.removeAllGoals();
        this.targetSelector.removeAllGoals();
        this.goalSelector.addGoal(0, new RestoreWorldGoal(this));
    }

    @Override
    public boolean isLookingAtMe(Player player) {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean result = super.hurt(source, amount);
        if (!this.level().isClientSide && result) {
            for (int i = 0; i < 8; i++) {
                if (this.teleport()) break;
            }
        }
        return result;
    }

    public void onUnnaturalBlockPlacedNearby() {
        recentUnnaturalPlaced++;
        reactivityCooldown = 200;
    }

    @Override
    public void tick() {
        super.tick();

        if (reactivityCooldown > 0) {
            reactivityCooldown--;
            if (reactivityCooldown == 0) {
                recentUnnaturalPlaced = 0;
            }
        }
    }

    class RestoreWorldGoal extends Goal {

        private final ReclaimerEndermanEntity mob;

        public RestoreWorldGoal(ReclaimerEndermanEntity mob) {
            this.mob = mob;
        }

        @Override
        public boolean canUse() {
            return true;
        }

        @Override
        public void tick() {
            if (!(mob.level() instanceof ServerLevel world)) return;

            BlockPos below = mob.blockPosition().below();
            BlockState belowState = world.getBlockState(below);
            if (belowState.is(Blocks.WATER) || belowState.is(Blocks.LAVA)) {
                mob.teleport();
                return;
            }

            int baseInterval = ReclaimerMod.CONFIG.scanInterval;
            int interval = Math.max(5, baseInterval - recentUnnaturalPlaced * 2);

            if (scanCooldown-- <= 0) {
                scanCooldown = interval;
                enqueueScan(world);
            }

            processCleanup(world);

            if (ReclaimerMod.CONFIG.structureGriefEnabled) {
                applyStructuralRejection(world);
                if (ReclaimerMod.CONFIG.chestCorruptionEnabled) {
                    corruptChests(world);
                }
            }

            if (mob.tickCount % 40 == 0) {
                moveTowardCorruption(world);
            }
        }

        private void enqueueScan(ServerLevel world) {
            int radius = ReclaimerMod.CONFIG.scanRadius;

            double rad = Math.toRadians(angle);
            angle = (angle + 30) % 360;

            BlockPos center = mob.blockPosition();
            BlockPos target = center.offset(
                    (int) (Math.cos(rad) * radius),
                    0,
                    (int) (Math.sin(rad) * radius)
            );

            for (int dx = -2; dx <= 2; dx++) {
                for (int dz = -2; dz <= 2; dz++) {
                    BlockPos pos = target.offset(dx, 0, dz);
                    if (!world.hasChunkAt(pos)) continue;

                    BlockState state = world.getBlockState(pos);
                    if (isUnnatural(state.getBlock())) {
                        cleanupQueue.add(pos.immutable());
                    }
                }
            }
        }

        private void processCleanup(ServerLevel world) {
            int limit = ReclaimerMod.CONFIG.maxBlocksPerTick;

            for (int i = 0; i < limit && !cleanupQueue.isEmpty(); i++) {
                BlockPos pos = cleanupQueue.poll();
                BlockState state = world.getBlockState(pos);

                if (isUnnatural(state.getBlock())) {
                    world.sendParticles(ParticleTypes.ASH,
                            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            6, 0.2, 0.2, 0.2, 0.01);

                    world.playSound(null, pos, SoundEvents.GRASS_PLACE,
                            SoundSource.BLOCKS, 0.4f, 0.8f + world.random.nextFloat() * 0.4f);

                    world.setBlock(pos, getNaturalReplacement(world, pos), 3);
                }
            }

            if (mob.tickCount % 200 == 0 && !cleanupQueue.isEmpty()) {
                BlockPos center = mob.blockPosition();
                world.playSound(null, center, SoundEvents.AMETHYST_BLOCK_RESONATE,
                        SoundSource.AMBIENT, 0.8f, 0.6f);
            }
        }

        private void moveTowardCorruption(ServerLevel world) {
            int radius = ReclaimerMod.CONFIG.scanRadius * 2;
            BlockPos origin = mob.blockPosition();

            BlockPos bestPos = null;
            int bestScore = 0;

            for (int i = 0; i < 16; i++) {
                int dx = world.random.nextInt(radius * 2 + 1) - radius;
                int dz = world.random.nextInt(radius * 2 + 1) - radius;
                BlockPos pos = origin.offset(dx, 0, dz);

                if (!world.hasChunkAt(pos)) continue;

                int score = countUnnaturalAround(world, pos, 3);
                if (score > bestScore) {
                    bestScore = score;
                    bestPos = pos;
                }
            }

            if (bestPos != null && bestScore > 0) {
                mob.getNavigation().moveTo(
                        bestPos.getX() + 0.5,
                        bestPos.getY(),
                        bestPos.getZ() + 0.5,
                        1.0
                );
            }
        }

        private int countUnnaturalAround(ServerLevel world, BlockPos center, int r) {
            int count = 0;
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

            for (int dx = -r; dx <= r; dx++) {
                for (int dz = -r; dz <= r; dz++) {
                    pos.set(center.getX() + dx, center.getY(), center.getZ() + dz);
                    BlockState state = world.getBlockState(pos);
                    if (isUnnatural(state.getBlock())) count++;
                }
            }
            return count;
        }

        private boolean isUnnatural(Block block) {
            return block.defaultBlockState().is(UNNATURAL_BLOCKS);
        }

        private BlockState getNaturalReplacement(ServerLevel world, BlockPos pos) {
            BlockState below = world.getBlockState(pos.below());

            if (below.getBlock().defaultBlockState().is(NATURAL_BLOCKS)) {
                return below.getBlock().defaultBlockState();
            }

            return Blocks.DIRT.defaultBlockState();
        }

        // -----------------------------
        // CATASTROPHIC STRUCTURAL REJECTION
        // -----------------------------
        private void applyStructuralRejection(ServerLevel world) {
            int radius = ReclaimerMod.CONFIG.structureRadius;
            int maxChanges = ReclaimerMod.CONFIG.maxStructureChangesPerTick;

            BlockPos origin = mob.blockPosition();
            int changes = 0;

            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

            for (int dx = -radius; dx <= radius && changes < maxChanges; dx++) {
                for (int dy = -2; dy <= 4 && changes < maxChanges; dy++) {
                    for (int dz = -radius; dz <= radius && changes < maxChanges; dz++) {
                        pos.set(origin.getX() + dx, origin.getY() + dy, origin.getZ() + dz);
                        if (!world.hasChunkAt(pos)) continue;

                        BlockState state = world.getBlockState(pos);
                        Block block = state.getBlock();

                        if (!state.isAir() &&
                                block.defaultBlockState().is(PLAYER_STRUCTURES) &&
                                !block.defaultBlockState().is(STRUCTURE_PROTECTED)) {

                            BlockState replacement = pickStructureReplacement(world, pos, state);
                            world.setBlock(pos, replacement, 3);

                            world.sendParticles(ParticleTypes.ASH,
                                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                                    8, 0.3, 0.3, 0.3, 0.02);

                            world.playSound(null, pos, SoundEvents.ROOTED_DIRT_PLACE,
                                    SoundSource.BLOCKS, 0.6f, 0.6f + world.random.nextFloat() * 0.4f);

                            changes++;
                        }
                    }
                }
            }
        }

        private BlockState pickStructureReplacement(ServerLevel world, BlockPos pos, BlockState original) {
            Block below = world.getBlockState(pos.below()).getBlock();

            if (below == Blocks.GRASS_BLOCK || below == Blocks.DIRT || below == Blocks.COARSE_DIRT) {
                switch (world.random.nextInt(4)) {
                    case 0: return Blocks.GRASS_BLOCK.defaultBlockState();
                    case 1: return Blocks.DIRT.defaultBlockState();
                    case 2: return Blocks.COARSE_DIRT.defaultBlockState();
                    default: return Blocks.MOSS_BLOCK.defaultBlockState();
                }
            }

            if (below == Blocks.STONE || below == Blocks.COBBLESTONE || below == Blocks.ANDESITE) {
                switch (world.random.nextInt(3)) {
                    case 0: return Blocks.MOSSY_COBBLESTONE.defaultBlockState();
                    case 1: return Blocks.STONE.defaultBlockState();
                    default: return Blocks.DIRT.defaultBlockState();
                }
            }

            if (original.is(Blocks.GLASS) || original.is(Blocks.GLASS_PANE)) {
                return Blocks.AIR.defaultBlockState();
            }

            if (original.is(Blocks.TORCH) || original.is(Blocks.LANTERN) || original.is(Blocks.SOUL_LANTERN)) {
                return Blocks.AIR.defaultBlockState();
            }

            if (original.is(Blocks.FARMLAND)) {
                return Blocks.DIRT.defaultBlockState();
            }

            return Blocks.DIRT.defaultBlockState();
        }

        // -----------------------------
        // CHEST CORRUPTION (CATACLYSMIC)
        // -----------------------------
        private void corruptChests(ServerLevel world) {
            int radius = ReclaimerMod.CONFIG.chestRadius;
            BlockPos origin = mob.blockPosition();

            for (BlockPos pos : BlockPos.betweenClosed(
                    origin.offset(-radius, -1, -radius),
                    origin.offset(radius, 2, radius))) {

                if (!world.hasChunkAt(pos)) continue;

                BlockEntity be = world.getBlockEntity(pos);
                if (be instanceof ChestBlockEntity chest) {
                    corruptChestInventory(chest);
                }
            }
        }

        private void corruptChestInventory(Container inv) {
            int size = inv.getContainerSize();

            for (int i = 0; i < size; i++) {
                ItemStack stack = inv.getItem(i);

                if (stack.isEmpty()) {
                    inv.setItem(i, new ItemStack(Items.DIRT, 64));
                    continue;
                }

                if (stack.getItem() instanceof BlockItem blockItem) {
                    Block block = blockItem.getBlock();
                    if (block.defaultBlockState().is(UNNATURAL_BLOCKS)) {
                        inv.setItem(i, new ItemStack(Items.DIRT, 64));
                    }
                }
            }
        }
    }

    // -----------------------------
    // DEBUG HELPERS
    // -----------------------------
    public int getQueueSize() {
        return cleanupQueue.size();
    }

    public int getRecentUnnaturalPlaced() {
        return recentUnnaturalPlaced;
    }

    public int getScanAngle() {
        return angle;
    }

    public void forceScan(ServerLevel world) {
        cleanupQueue.clear();
        scanCooldown = 0;
    }
}
