package com.example.reclaimer.entity;

import com.example.reclaimer.ReclaimerMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ServerWorldAccess;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Random;

public class ReclaimerEndermanEntity extends EndermanEntity {

    private final Queue<BlockPos> cleanupQueue = new ArrayDeque<>();
    private int scanCooldown = 0;
    private int angle = 0;

    private int recentUnnaturalPlaced = 0;
    private int reactivityCooldown = 0;

    private static final TagKey<Block> UNNATURAL_BLOCKS =
            BlockTags.create(new Identifier("reclaimer", "unnatural_blocks"));
    private static final TagKey<Block> NATURAL_BLOCKS =
            BlockTags.create(new Identifier("reclaimer", "natural_blocks"));
    private static final TagKey<Block> PLAYER_STRUCTURES =
            BlockTags.create(new Identifier("reclaimer", "player_structures"));
    private static final TagKey<Block> STRUCTURE_PROTECTED =
            BlockTags.create(new Identifier("reclaimer", "structure_protected"));

    public ReclaimerEndermanEntity(EntityType<? extends EndermanEntity> type, World world) {
        super(type, world);
    }

    public static DefaultAttributeContainer.Builder createReclaimerAttributes() {
        return EndermanEntity.createEndermanAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3);
    }

    public static boolean canSpawn(EntityType<ReclaimerEndermanEntity> type, ServerWorldAccess world,
                                   SpawnReason reason, BlockPos pos, Random random) {
        return world.getLightLevel(pos) <= 7 &&
                world.getBlockState(pos.down()).isOpaque();
    }

    @Override
    protected void initGoals() {
        this.goalSelector.clear();
        this.targetSelector.clear();
        this.goalSelector.add(0, new RestoreWorldGoal(this));
    }

    @Override
    public boolean isPlayerStaring(PlayerEntity player) {
        return false;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        boolean result = super.damage(source, amount);
        if (!this.getWorld().isClient && result) {
            for (int i = 0; i < 8; i++) {
                if (this.teleportRandomly()) break;
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
        public boolean canStart() {
            return true;
        }

        @Override
        public void tick() {
            if (!(mob.getWorld() instanceof ServerWorld world)) return;

            BlockPos below = mob.getBlockPos().down();
            BlockState belowState = world.getBlockState(below);
            if (belowState.isOf(Blocks.WATER) || belowState.isOf(Blocks.LAVA)) {
                mob.teleportRandomly();
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

            if (mob.age % 40 == 0) {
                moveTowardCorruption(world);
            }
        }

        private void enqueueScan(ServerWorld world) {
            int radius = ReclaimerMod.CONFIG.scanRadius;

            double rad = Math.toRadians(angle);
            angle = (angle + 30) % 360;

            BlockPos center = mob.getBlockPos();
            BlockPos target = center.add(
                    (int) (Math.cos(rad) * radius),
                    0,
                    (int) (Math.sin(rad) * radius)
            );

            for (int dx = -2; dx <= 2; dx++) {
                for (int dz = -2; dz <= 2; dz++) {
                    BlockPos pos = target.add(dx, 0, dz);
                    if (!world.isChunkLoaded(pos)) continue;

                    BlockState state = world.getBlockState(pos);
                    if (isUnnatural(state.getBlock())) {
                        cleanupQueue.add(pos.toImmutable());
                    }
                }
            }
        }

        private void processCleanup(ServerWorld world) {
            int limit = ReclaimerMod.CONFIG.maxBlocksPerTick;

            for (int i = 0; i < limit && !cleanupQueue.isEmpty(); i++) {
                BlockPos pos = cleanupQueue.poll();
                BlockState state = world.getBlockState(pos);

                if (isUnnatural(state.getBlock())) {
                    world.spawnParticles(ParticleTypes.ASH,
                            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            6, 0.2, 0.2, 0.2, 0.01);

                    world.playSound(null, pos, SoundEvents.BLOCK_GRASS_PLACE,
                            SoundCategory.BLOCKS, 0.4f, 0.8f + world.random.nextFloat() * 0.4f);

                    world.setBlockState(pos, getNaturalReplacement(world, pos));
                }
            }

            if (mob.age % 200 == 0 && !cleanupQueue.isEmpty()) {
                BlockPos center = mob.getBlockPos();
                world.playSound(null, center, SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE,
                        SoundCategory.AMBIENT, 0.8f, 0.6f);
            }
        }

        private void moveTowardCorruption(ServerWorld world) {
            int radius = ReclaimerMod.CONFIG.scanRadius * 2;
            BlockPos origin = mob.getBlockPos();

            BlockPos bestPos = null;
            int bestScore = 0;

            for (int i = 0; i < 16; i++) {
                int dx = world.random.nextInt(radius * 2 + 1) - radius;
                int dz = world.random.nextInt(radius * 2 + 1) - radius;
                BlockPos pos = origin.add(dx, 0, dz);

                if (!world.isChunkLoaded(pos)) continue;

                int score = countUnnaturalAround(world, pos, 3);
                if (score > bestScore) {
                    bestScore = score;
                    bestPos = pos;
                }
            }

            if (bestPos != null && bestScore > 0) {
                mob.getNavigation().startMovingTo(
                        bestPos.getX() + 0.5,
                        bestPos.getY(),
                        bestPos.getZ() + 0.5,
                        1.0
                );
            }
        }

        private int countUnnaturalAround(ServerWorld world, BlockPos center, int r) {
            int count = 0;
            BlockPos.Mutable pos = new BlockPos.Mutable();

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
            return block.getDefaultState().isIn(UNNATURAL_BLOCKS);
        }

        private BlockState getNaturalReplacement(ServerWorld world, BlockPos pos) {
            BlockState below = world.getBlockState(pos.down());

            if (below.getBlock().getDefaultState().isIn(NATURAL_BLOCKS)) {
                return below.getBlock().getDefaultState();
            }

            return Blocks.DIRT.getDefaultState();
        }

        // -----------------------------
        // CATASTROPHIC STRUCTURAL REJECTION
        // -----------------------------
        private void applyStructuralRejection(ServerWorld world) {
            int radius = ReclaimerMod.CONFIG.structureRadius;
            int maxChanges = ReclaimerMod.CONFIG.maxStructureChangesPerTick;

            BlockPos origin = mob.getBlockPos();
            int changes = 0;

            BlockPos.Mutable pos = new BlockPos.Mutable();

            for (int dx = -radius; dx <= radius && changes < maxChanges; dx++) {
                for (int dy = -2; dy <= 4 && changes < maxChanges; dy++) {
                    for (int dz = -radius; dz <= radius && changes < maxChanges; dz++) {
                        pos.set(origin.getX() + dx, origin.getY() + dy, origin.getZ() + dz);
                        if (!world.isChunkLoaded(pos)) continue;

                        BlockState state = world.getBlockState(pos);
                        Block block = state.getBlock();

                        if (!state.isAir() &&
                                block.getDefaultState().isIn(PLAYER_STRUCTURES) &&
                                !block.getDefaultState().isIn(STRUCTURE_PROTECTED)) {

                            BlockState replacement = pickStructureReplacement(world, pos, state);
                            world.setBlockState(pos, replacement, 3);

                            world.spawnParticles(ParticleTypes.ASH,
                                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                                    8, 0.3, 0.3, 0.3, 0.02);

                            world.playSound(null, pos, SoundEvents.BLOCK_ROOTED_DIRT_PLACE,
                                    SoundCategory.BLOCKS, 0.6f, 0.6f + world.random.nextFloat() * 0.4f);

                            changes++;
                        }
                    }
                }
            }
        }

        private BlockState pickStructureReplacement(ServerWorld world, BlockPos pos, BlockState original) {
            Block below = world.getBlockState(pos.down()).getBlock();

            if (below == Blocks.GRASS_BLOCK || below == Blocks.DIRT || below == Blocks.COARSE_DIRT) {
                switch (world.random.nextInt(4)) {
                    case 0: return Blocks.GRASS_BLOCK.getDefaultState();
                    case 1: return Blocks.DIRT.getDefaultState();
                    case 2: return Blocks.COARSE_DIRT.getDefaultState();
                    default: return Blocks.MOSS_BLOCK.getDefaultState();
                }
            }

            if (below == Blocks.STONE || below == Blocks.COBBLESTONE || below == Blocks.ANDESITE) {
                switch (world.random.nextInt(3)) {
                    case 0: return Blocks.MOSSY_COBBLESTONE.getDefaultState();
                    case 1: return Blocks.STONE.getDefaultState();
                    default: return Blocks.DIRT.getDefaultState();
                }
            }

            if (original.isOf(Blocks.GLASS) || original.isOf(Blocks.GLASS_PANE)) {
                return Blocks.AIR.getDefaultState();
            }

            if (original.isOf(Blocks.TORCH) || original.isOf(Blocks.LANTERN) || original.isOf(Blocks.SOUL_LANTERN)) {
                return Blocks.AIR.getDefaultState();
            }

            if (original.isOf(Blocks.FARMLAND)) {
                return Blocks.DIRT.getDefaultState();
            }

            return Blocks.DIRT.getDefaultState();
        }

        // -----------------------------
        // CHEST CORRUPTION (CATACLYSMIC)
        // -----------------------------
        private void corruptChests(ServerWorld world) {
            int radius = ReclaimerMod.CONFIG.chestRadius;
            BlockPos origin = mob.getBlockPos();

            for (BlockPos pos : BlockPos.iterate(
                    origin.add(-radius, -1, -radius),
                    origin.add(radius, 2, radius))) {

                if (!world.isChunkLoaded(pos)) continue;

                BlockEntity be = world.getBlockEntity(pos);
                if (be instanceof ChestBlockEntity chest) {
                    corruptChestInventory(chest);
                }
            }
        }

        private void corruptChestInventory(Inventory inv) {
            int size = inv.size();

            for (int i = 0; i < size; i++) {
                ItemStack stack = inv.getStack(i);

                if (stack.isEmpty()) {
                    inv.setStack(i, new ItemStack(Items.DIRT, 64));
                    continue;
                }

                if (stack.getItem() instanceof BlockItem blockItem) {
                    Block block = blockItem.getBlock();
                    if (block.getDefaultState().isIn(UNNATURAL_BLOCKS)) {
                        inv.setStack(i, new ItemStack(Items.DIRT, 64));
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

    public void forceScan(ServerWorld world) {
        cleanupQueue.clear();
        scanCooldown = 0;
    }
}
