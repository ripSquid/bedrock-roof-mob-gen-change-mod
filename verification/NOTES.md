# Verification notes — 2026-10-25

## Sources inspected

* Fabric's official 26.3 example: https://github.com/FabricMC/fabric-example-mod/tree/26.3
* Fabric versions API: https://meta.fabricmc.net/v2/versions/game
* Amplified Nether versions API: https://api.modrinth.com/v2/project/amplified-nether/version
* Exact Amplified Nether mod release: https://modrinth.com/mod/amplified-nether/version/IYaFP7y8
* Local Minecraft 26.3 bytecode and Loom-generated sources (`./gradlew genSources`). Decompiled Minecraft files and downloaded third-party jars are local inspection artifacts, excluded from git and the release jar.

## Confirmed

Amplified Nether 1.3.0+26.3 sets noise min_y=0, height=256, legacy_random_source=false. In `data/minecraft/worldgen/noise_settings/nether.json`, its roof material rule inverts a vertical gradient from below_top=5 to below_top=0, with random_name=minecraft:bedrock_roof. A subsequent rule fills the upper band with netherrack where bedrock wasn't selected.

Minecraft's `NaturalSpawner.getRandomPosWithin(Level, LevelChunk)` draws X/Z within the chunk, calls `chunk.getHeight(WORLD_SURFACE, x, z) + 1`, and draws Y inclusively from the dimension minimum through that value. The redirect changes only the height result, only in the Nether. It does not change mob caps, biome selection, pack attempts, light, distance, collision, or supporting-block checks.

The roof rule makes bedrock possible only from below_top 4 (Y=251) to below_top 0 (Y=255); Y=250 has probability zero. The spawning height therefore looks for the lowest bedrock between maxY-5 and maxY. That block and everything above it are ignored, since blocks sealed between roof bedrock in that column cannot be removed from below. Laterally reachable pockets are also ignored. Mobs could rarely spawn in them anyway.

World generation is not modified.

## Remaining world-level validation

* Remove ceiling terrain below the roof in a column and confirm the spawning upper bound follows the highest remaining reachable block.
* Measure server tick cost and actual spawning behavior in a cleared test area.

## Executed checks

* `./gradlew build`: compilation, packaging, and six JUnit tests passed.
* `./gradlew -ProofSmoke runServer`: Minecraft 26.3 / Fabric Loader 0.20.1 loaded with Amplified Nether 1.3.0+26.3. `NaturalSpawner` transformed successfully and emitted `NETHER_ROOF_SMOKE_OK`. The server then exited because no EULA was accepted in the isolated smoke directory; no world was generated.
* The reference jar's SHA-512 matched the Modrinth release metadata.
