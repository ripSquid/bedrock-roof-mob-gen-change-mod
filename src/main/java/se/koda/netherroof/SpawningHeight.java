package se.koda.netherroof;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;

public final class SpawningHeight {
    // Matches the roof material rule's vertical gradient: bedrock is possible from maxY - 4 to maxY.
    public static final int ROOF_DEPTH = 5;

    private SpawningHeight() {}

    /**
     * Returns the highest non-air block that a player can reach from below. Everything at or above the
     * lowest roof bedrock in this column is ignored, because pockets inside the roof cannot be cleared.
     */
    public static int find(BlockGetter blocks, int x, int z, int height, int minY, int maxY) {
        var pos = new BlockPos.MutableBlockPos(x, 0, z);
        int top = height;
        for (int y = Math.max(maxY - ROOF_DEPTH, minY); y <= Math.min(height, maxY); y++) {
            if (blocks.getBlockState(pos.setY(y)).is(Blocks.BEDROCK)) {
                top = y - 1;
                break;
            }
        }
        for (int y = top; y >= minY; y--) {
            if (!blocks.getBlockState(pos.setY(y)).isAir()) return y;
        }
        return minY - 1;
    }
}
