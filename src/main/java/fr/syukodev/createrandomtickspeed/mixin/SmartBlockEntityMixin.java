package fr.syukodev.createrandomtickspeed.mixin;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import fr.syukodev.createrandomtickspeed.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to optimize SmartBlockEntity ticking by skipping heavy computation on some ticks
 * while maintaining animation synchronization.
 * 
 * This implementation:
 * - Skips 1 out of every N ticks (configurable, default 5) to reduce CPU load
 * - Uses position-based deterministic patterns to ensure consistent behavior
 * - Maintains animation synchronization by using predictable skip patterns
 * - Allows load balancing across different block entities
 */
@Mixin(SmartBlockEntity.class)
public class SmartBlockEntityMixin {

    @Shadow
    public Level level;
    
    @Shadow
    public BlockPos worldPosition;

    /**
     * Inject at the head of the tick method to potentially cancel execution
     * for performance optimization.
     * 
     * The cancellation logic:
     * 1. Uses a deterministic hash based on block position to ensure consistency
     * 2. Combines with game time to create a rolling pattern
     * 3. Skips exactly 1 out of every N ticks (where N is configurable)
     * 4. Ensures different blocks skip different ticks for load balancing
     */
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onTick(CallbackInfo ci) {
        // Only proceed with optimization if enabled in config
        if (!Config.enableSmartBlockEntityOptimization) {
            return;
        }

        // Make sure we have a valid level and position
        if (level == null || worldPosition == null) {
            return;
        }
        
        // Skip optimization for the first few ticks to allow proper initialization
        long gameTime = level.getGameTime();
        if (gameTime < 100) {
            return;
        }
        
        // Calculate a deterministic hash based on position
        // This ensures the same block will always skip the same relative ticks
        // but different blocks will skip different ticks for load balancing
        int positionHash = Math.abs((worldPosition.getX() * 31 + worldPosition.getY() * 961 + worldPosition.getZ() * 29791) % Config.tickSkipPattern);
        
        // Check if this tick should be skipped for this block entity
        // The pattern ensures exactly 1 out of every N ticks is skipped
        if ((gameTime + positionHash) % Config.tickSkipPattern == (Config.tickSkipPattern - 1)) {
            // Skip this tick by cancelling the method execution
            // This will skip the heavy computations while maintaining synchronization
            // since the skipping pattern is deterministic and position-based
            ci.cancel();
        }
    }
}