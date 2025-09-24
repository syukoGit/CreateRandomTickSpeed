package fr.syukodev.createrandomtickspeed;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.slf4j.Logger;

import java.util.concurrent.atomic.LongAdder;

@EventBusSubscriber(modid = CreateRandomTickSpeed.MODID)
public final class StatsLogger {
  private static final Logger LOGGER = LogUtils.getLogger();

  // Counters incremented by mixins
  // Attempted = number of times the method was entered on server side
  // Skipped = number of times we cancelled the execution
  public static final LongAdder DEPLOYER_ATTEMPTED_TICK = new LongAdder();
  public static final LongAdder DEPLOYER_SKIPPED_TICK = new LongAdder();
  public static final LongAdder DEPLOYER_ATTEMPTED_LAZY_TICK = new LongAdder();
  public static final LongAdder DEPLOYER_SKIPPED_LAZY_TICK = new LongAdder();

  // Counters incremented by mixins
  // Attempted = number of times the method was entered on server side
  // Skipped = number of times we cancelled the execution
  public static final LongAdder NOZZLE_ATTEMPTED_TICK = new LongAdder();
  public static final LongAdder NOZZLE_SKIPPED_TICK = new LongAdder();
  public static final LongAdder NOZZLE_ATTEMPTED_LAZY_TICK = new LongAdder();
  public static final LongAdder NOZZLE_SKIPPED_LAZY_TICK = new LongAdder();

  private static int secondTicker = 0; // counts server ticks (20 = ~1s)

  private StatsLogger() {
  }

  @SubscribeEvent
  public static void onServerTick(ServerTickEvent.Post event) {
    // Server runs ~20 TPS; print once per second
    secondTicker++;
    if (secondTicker >= 20) {
      long deployerTickAttempted = DEPLOYER_ATTEMPTED_TICK.sumThenReset();
      long deployerTickSkipped = DEPLOYER_SKIPPED_TICK.sumThenReset();
      long deployerTickPassed = Math.max(0, deployerTickAttempted - deployerTickSkipped);
      int deployerTickPercent = deployerTickAttempted == 0 ? 0
          : (int) Math.round((deployerTickSkipped * 100.0) / (deployerTickAttempted));

      long deployerLazyAttempted = DEPLOYER_ATTEMPTED_LAZY_TICK.sumThenReset();
      long deployerLazySkipped = DEPLOYER_SKIPPED_LAZY_TICK.sumThenReset();
      long deployerLazyPassed = Math.max(0, deployerLazyAttempted - deployerLazySkipped);
      int deployerLazyPercent = deployerLazyAttempted == 0 ? 0
          : (int) Math.round((deployerLazySkipped * 100.0) / (deployerLazyAttempted));

      long nozzleTickAttempted = NOZZLE_ATTEMPTED_TICK.sumThenReset();
      long nozzleTickSkipped = NOZZLE_SKIPPED_TICK.sumThenReset();
      long nozzleTickPassed = Math.max(0, nozzleTickAttempted - nozzleTickSkipped);
      int nozzleTickPercent = nozzleTickAttempted == 0 ? 0
          : (int) Math.round((nozzleTickSkipped * 100.0) / (nozzleTickAttempted));

      long nozzleLazyAttempted = NOZZLE_ATTEMPTED_LAZY_TICK.sumThenReset();
      long nozzleLazySkipped = NOZZLE_SKIPPED_LAZY_TICK.sumThenReset();
      long nozzleLazyPassed = Math.max(0, nozzleLazyAttempted - nozzleLazySkipped);
      int nozzleLazyPercent = nozzleLazyAttempted == 0 ? 0
          : (int) Math.round((nozzleLazySkipped * 100.0) / (nozzleLazyAttempted));

      // Format: X/M (P%) where X = skipped, M = passed, P = percentage skipped
      LOGGER.info("[CreateRandomTickSpeed] DEPLOYER: tick: {}/{} ({}%) | lazyTick: {}/{} ({}%)",
          deployerTickSkipped, deployerTickPassed, deployerTickPercent,
          deployerLazySkipped, deployerLazyPassed, deployerLazyPercent);

      LOGGER.info("[CreateRandomTickSpeed] NOZZLE: tick: {}/{} ({}%) | lazyTick: {}/{} ({}%)",
          nozzleTickSkipped, nozzleTickPassed, nozzleTickPercent,
          nozzleLazySkipped, nozzleLazyPassed, nozzleLazyPercent);

      secondTicker = 0;
    }
  }
}
