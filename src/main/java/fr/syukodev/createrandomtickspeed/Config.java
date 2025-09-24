package fr.syukodev.createrandomtickspeed;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
@EventBusSubscriber(modid = CreateRandomTickSpeed.MODID)
public class Config {
        private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

        // Skip 1 server tick out of every N for Create SmartBlockEntities (server-side
        // only).
        // 0 disables skipping, 1 skips all, 5 ≈ 20% skip rate (previous default).
        public static final ModConfigSpec.IntValue TICK_SKIP_EVERY_N = BUILDER
                        .comment(
                                        "Skip 1 call to SmartBlockEntity.tick every N ticks (server-side only).",
                                        "0 = disabled, 1 = skip all, 5 = skip 1 out of 5 (~20%).")
                        .defineInRange("tickSkipEveryN", 5, 0, Integer.MAX_VALUE);

        // Skip 1 lazyTick out of every N for Create SmartBlockEntities (server-side
        // only).
        // 0 disables skipping, 1 skips all. Default 0 to match prior behavior (no lazy
        // skip).
        public static final ModConfigSpec.IntValue LAZY_SKIP_EVERY_N = BUILDER
                        .comment(
                                        "Skip 1 call to SmartBlockEntity.lazyTick every N lazy ticks (server-side only).",
                                        "0 = disabled, 1 = skip all.")
                        .defineInRange("lazySkipEveryN", 0, 0, Integer.MAX_VALUE);

        static final ModConfigSpec SPEC = BUILDER.build();

        @SubscribeEvent
        static void onLoad(final ModConfigEvent event) {
        }
}
