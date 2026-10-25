package se.koda.netherroof.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(NaturalSpawner.class)
public abstract class NaturalSpawnerMixin {
    // Preserve vanilla's random draws, inclusive upper bound, and every spawn validity check.
    // WrapOperation chains with other mods wrapping the same call, unlike Redirect.
    @WrapOperation(method = "getRandomPosWithin", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/world/level/chunk/LevelChunk;getHeight(Lnet/minecraft/world/level/levelgen/Heightmap$Types;II)I"))
    private static int netherRoof$heightBelowRoof(LevelChunk chunk, Heightmap.Types type, int x, int z,
                                                Operation<Integer> original) {
        int height = original.call(chunk, type, x, z);
        Level level = chunk.getLevel();
        if (!level.dimension().equals(Level.NETHER)) return height;
        return se.koda.netherroof.SpawningHeight.find(chunk, x, z, height, level.getMinY(), level.getMaxY());
    }
}
