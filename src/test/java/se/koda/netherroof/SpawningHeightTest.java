package se.koda.netherroof;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SpawningHeightTest {
    private final Map<Integer, BlockState> column = new HashMap<>();
    private BlockGetter blocks;

    @BeforeAll static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @BeforeEach void setUp() {
        column.clear();
        blocks = mock(BlockGetter.class);
        when(blocks.getBlockState(any())).thenAnswer(a -> column.getOrDefault(
            ((BlockPos) a.getArgument(0)).getY(), Blocks.AIR.defaultBlockState()));
    }

    private void put(int y, net.minecraft.world.level.block.Block block) {
        column.put(y, block.defaultBlockState());
    }

    private int find() {
        int height = column.keySet().stream().mapToInt(Integer::intValue).max().orElse(-1);
        return SpawningHeight.find(blocks, -17, 32, height, 0, 255);
    }

    @Test void blocksTrappedInsideTheRoofAreIgnored() {
        put(255, Blocks.BEDROCK);
        put(254, Blocks.NETHERRACK);
        put(253, Blocks.BEDROCK);
        put(252, Blocks.NETHERRACK);
        put(251, Blocks.BEDROCK);
        put(250, Blocks.NETHERRACK);
        put(120, Blocks.STONE);
        assertEquals(250, find());
    }

    @Test void clearedColumnsFallThroughToTerrainAndFluids() {
        put(255, Blocks.BEDROCK);
        put(252, Blocks.BEDROCK);
        put(253, Blocks.NETHERRACK);
        put(80, Blocks.STONE);
        assertEquals(80, find());
        put(90, Blocks.LAVA);
        assertEquals(90, find());
    }

    @Test void lowestRoofBedrockAtTheBandBottomStillWorks() {
        put(255, Blocks.BEDROCK);
        put(251, Blocks.BEDROCK);
        put(250, Blocks.NETHERRACK);
        assertEquals(250, find());
    }

    @Test void bedrockBelowTheRoofBandCountsLikeVanilla() {
        put(255, Blocks.BEDROCK);
        put(249, Blocks.BEDROCK);
        assertEquals(249, find());
        column.clear();
        put(255, Blocks.BEDROCK);
        put(0, Blocks.BEDROCK);
        assertEquals(0, find());
    }

    @Test void columnWithoutRoofUsesVanillaHeight() {
        put(200, Blocks.NETHERRACK);
        assertEquals(200, find());
        put(253, Blocks.NETHERRACK);
        assertEquals(253, find());
    }

    @Test void emptyColumnReturnsBelowMinimum() {
        put(255, Blocks.BEDROCK);
        assertEquals(-1, find());
    }
}
