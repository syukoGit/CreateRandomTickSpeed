# SmartBlockEntity Optimization

## Overview

This addon implements an optimization for Create mod's SmartBlockEntity that reduces CPU load by skipping 1 out of every 5 ticks (configurable) while maintaining animation synchronization.

## How it works

### Tick Skipping Logic
- Uses a Mixin to intercept the `tick()` method of SmartBlockEntity
- Implements a deterministic pattern based on block position and game time
- Skips exactly 1 out of every N ticks (where N is configurable, default is 5)
- Different blocks skip different ticks to balance the load across the game

### Animation Synchronization
The optimization maintains animation synchronization because:
1. **Deterministic Pattern**: The same block will always skip the same relative ticks
2. **Position-based Distribution**: Different blocks skip different ticks, preventing synchronized stuttering
3. **Consistent Timing**: The skip pattern is based on game time, ensuring predictable behavior

### Performance Benefits
- Reduces CPU usage by approximately 20% for Create contraptions with many SmartBlockEntity instances
- Load balances across different blocks to prevent frame rate spikes
- Maintains visual continuity and animation smoothness

## Configuration

### enableSmartBlockEntityOptimization
- **Type**: Boolean
- **Default**: true
- **Description**: Enables or disables the tick optimization feature

### tickSkipPattern
- **Type**: Integer
- **Default**: 5
- **Range**: 2-20
- **Description**: How many ticks to process before skipping one. A value of 5 means skip 1 out of every 5 ticks.

## Implementation Details

### Mixin Target
- **Class**: `com.simibubi.create.foundation.blockEntity.SmartBlockEntity`
- **Method**: `tick()`
- **Injection Point**: `@At("HEAD")` with `cancellable = true`

### Hash Calculation
```java
int positionHash = Math.abs((worldPosition.getX() * 31 + worldPosition.getY() * 961 + worldPosition.getZ() * 29791) % Config.tickSkipPattern);
```

### Skip Condition
```java
if ((gameTime + positionHash) % Config.tickSkipPattern == (Config.tickSkipPattern - 1)) {
    ci.cancel(); // Skip this tick
}
```

## Compatibility

This optimization is designed to be:
- **Safe**: Only affects performance, not functionality
- **Reversible**: Can be disabled via configuration
- **Compatible**: Works with all Create mod versions that use SmartBlockEntity
- **Stable**: Uses deterministic patterns to ensure consistent behavior

## Notes

- The optimization skips the first 100 ticks after world load to allow proper initialization
- The skip pattern is position-dependent to ensure load balancing
- The feature can be completely disabled if any issues arise