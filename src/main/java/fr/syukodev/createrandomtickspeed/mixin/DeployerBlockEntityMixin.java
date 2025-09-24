package fr.syukodev.createrandomtickspeed.mixin;

import fr.syukodev.createrandomtickspeed.Config;
import fr.syukodev.createrandomtickspeed.StatsLogger;
import fr.syukodev.createrandomtickspeed.network.ClientSkipTracker;
import fr.syukodev.createrandomtickspeed.network.NetworkHandler;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;

// Intentionally avoid importing Create's DeployerBlockEntity to prevent pulling in
// optional Ponder classes at compile time. We target it by name in @Mixin.

/**
 * Inject at the start of DeployerBlockEntity#tickServer to probabilistically
 * skip
 * heavy logic
 * while letting client animation sync happen naturally via Create's systems.
 */
@Mixin(value = DeployerBlockEntity.class, remap = false)
public abstract class DeployerBlockEntityMixin {
  @Unique
  private int createrandomtickspeed$tickCounter = 0;
  @Unique
  private int createrandomtickspeed$lazyTickCounter = 0;

  @Inject(method = "tick", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
  private void createrandomtickspeed$maybeSkipTick(CallbackInfo ci) {
    BlockEntity self = (BlockEntity) (Object) this;
    Level level = self.getLevel();
    if (level != null && level.isClientSide()) {
      // If client was told to skip this pos, cancel here as well
      if (ClientSkipTracker.consumeTick(self.getBlockPos())) {
        ci.cancel();
      }
      return;
    }

    StatsLogger.DEPLOYER_ATTEMPTED_TICK.increment();

    createrandomtickspeed$tickCounter++;
    int n = Config.TICK_SKIP_EVERY_N.get();
    if (n > 0 && createrandomtickspeed$tickCounter % n == 0) {
      StatsLogger.DEPLOYER_SKIPPED_TICK.increment();
      // Inform clients tracking this chunk so they also skip
      NetworkHandler.sendSkipTick(level, self.getBlockPos(), false);
      ci.cancel();
    }
  }

  /**
   * Cancel DeployerBlockEntity#lazyTick every Nth call based on config.
   */
  @Inject(method = "lazyTick", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
  private void createrandomtickspeed$maybeSkipLazyTick(CallbackInfo ci) {
    BlockEntity self = (BlockEntity) (Object) this;
    Level level = self.getLevel();
    if (level != null && level.isClientSide()) {
      if (ClientSkipTracker.consumeLazy(self.getBlockPos())) {
        ci.cancel();
      }
      return;
    }

    StatsLogger.DEPLOYER_ATTEMPTED_LAZY_TICK.increment();

    createrandomtickspeed$lazyTickCounter++;
    int n = Config.LAZY_SKIP_EVERY_N.get();
    if (n > 0 && createrandomtickspeed$lazyTickCounter % n == 0) {
      StatsLogger.DEPLOYER_SKIPPED_LAZY_TICK.increment();
      NetworkHandler.sendSkipTick(level, self.getBlockPos(), true);
      ci.cancel();
    }
  }
}
