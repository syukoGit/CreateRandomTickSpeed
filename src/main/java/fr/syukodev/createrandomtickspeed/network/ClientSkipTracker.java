package fr.syukodev.createrandomtickspeed.network;

import net.minecraft.core.BlockPos;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Tracks how many upcoming ticks or lazyTicks should be skipped for a given
 * BlockEntity position.
 * Each consume method decrements the counter and returns true if a skip was
 * applied.
 */
public final class ClientSkipTracker {
  private static final ConcurrentMap<BlockPos, Integer> TICK_SKIPS = new ConcurrentHashMap<>();
  private static final ConcurrentMap<BlockPos, Integer> LAZY_SKIPS = new ConcurrentHashMap<>();

  private ClientSkipTracker() {
  }

  public static void mark(BlockPos pos, boolean isLazy) {
    BlockPos key = pos.immutable();
    if (isLazy)
      LAZY_SKIPS.merge(key, 1, Integer::sum);
    else
      TICK_SKIPS.merge(key, 1, Integer::sum);
  }

  public static boolean consumeTick(BlockPos pos) {
    return consumeOne(TICK_SKIPS, pos);
  }

  public static boolean consumeLazy(BlockPos pos) {
    return consumeOne(LAZY_SKIPS, pos);
  }

  private static boolean consumeOne(ConcurrentMap<BlockPos, Integer> map, BlockPos pos) {
    BlockPos key = pos.immutable();
    while (true) {
      Integer current = map.get(key);
      if (current == null || current <= 0)
        return false;
      int next = current - 1;
      if (next == 0) {
        if (map.remove(key, current))
          return true;
      } else {
        if (map.replace(key, current, next))
          return true;
      }
      // If CAS failed, retry
    }
  }
}
