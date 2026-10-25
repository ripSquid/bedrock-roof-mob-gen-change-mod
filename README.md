# Nether Roof Spawning

Server-only Fabric mod for Minecraft **26.3**. It works with the vanilla Nether and with height-extending Nether mods such as Amplified Nether (tested with **1.3.0+26.3**), but depends on neither. Requires Java 25 and Fabric Loader 0.20.1 or newer. Fabric API is not required.

## Behavior

* World generation is unchanged. The roof keeps its standard random bedrock in its top five layers (Y=123–127 in the vanilla Nether, Y=251–255 with Amplified Nether).
* Natural spawning in the Nether uses the highest non-air block a player can reach from below as its initial column height. In each column, the mod finds the lowest bedrock block within the top five layers of the world (the roof band). That block and everything above it are ignored, including netherrack or ores sealed between roof bedrock. Below it, every non-air block counts, including fluids and floor bedrock. Vanilla's extra +1 and random-height draw remain unchanged.
* A column with no bedrock in the roof band uses the vanilla height.
* The roof band is derived from the dimension's maximum Y, so it also matches the vanilla Nether roof.

This applies to existing chunks as well; no migration is needed. Other dimensions and other spawning mechanisms are unchanged.

Actual spawn rates and performance still need an in-game check.

## Build and install

```sh
./gradlew build
```

Install `build/libs/nether-roof-spawning-0.1.0.jar` on a test server. Clients do not need this mod.

## Verification

`./gradlew test` exercises height selection: blocks sealed inside the roof, the lowest possible roof bedrock, cleared columns, fluids, floor bedrock, columns without a roof, and empty columns.

An opt-in Fabric launch smoke check loads and transforms the mixin target class. CI runs it on every push and pull request:

```sh
python verification/download_reference.py
./gradlew -ProofSmoke runServer
```

The check uses a development-only helper mod and the pinned Amplified Nether reference jar, to check that the mod loads alongside it. Look for `NETHER_ROOF_SMOKE_OK`. It uses `run-smoke/` and does not accept the Minecraft EULA automatically. With no accepted EULA, the server exits after the mixin check. This does not constitute a full generated-world test.

See [verification/NOTES.md](verification/NOTES.md) for confirmed source details and remaining checks.
