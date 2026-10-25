package se.koda.netherroof.smoke;

import net.fabricmc.api.ModInitializer;

public final class SmokeCheck implements ModInitializer {
    @Override public void onInitialize() {
        try {
            Class.forName("net.minecraft.world.level.NaturalSpawner", false, getClass().getClassLoader());
            System.out.println("NETHER_ROOF_SMOKE_OK: mixin target transformed successfully");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }
}
