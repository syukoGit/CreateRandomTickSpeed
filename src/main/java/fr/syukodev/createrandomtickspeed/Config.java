package fr.syukodev.createrandomtickspeed;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

// Configuration class for CreateRandomTickSpeed
@EventBusSubscriber(modid = CreateRandomTickSpeed.MODID)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // Config option to enable/disable tick optimization for SmartBlockEntity
    public static final ModConfigSpec.BooleanValue ENABLE_SMART_BLOCK_ENTITY_OPTIMIZATION = BUILDER
            .comment("Enable optimization that skips 1 out of 5 ticks for Create SmartBlockEntity to improve performance")
            .define("enableSmartBlockEntityOptimization", true);

    // Config option to set the tick skip pattern (1 out of X ticks will be skipped)
    public static final ModConfigSpec.IntValue TICK_SKIP_PATTERN = BUILDER
            .comment("How many ticks to process before skipping one (5 means skip 1 out of every 5 ticks)")
            .defineInRange("tickSkipPattern", 5, 2, 20);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean enableSmartBlockEntityOptimization;
    public static int tickSkipPattern;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        enableSmartBlockEntityOptimization = ENABLE_SMART_BLOCK_ENTITY_OPTIMIZATION.get();
        tickSkipPattern = TICK_SKIP_PATTERN.get();
    }
}
