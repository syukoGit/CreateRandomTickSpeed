// package fr.syukodev.createrandomtickspeed.mixin;

// import fr.syukodev.createrandomtickspeed.Config;
// import fr.syukodev.createrandomtickspeed.StatsLogger;
// import net.minecraft.world.level.Level;
// import net.minecraft.world.level.block.entity.BlockEntity;

// import org.spongepowered.asm.mixin.Mixin;
// import org.spongepowered.asm.mixin.Unique;
// import org.spongepowered.asm.mixin.injection.At;
// import org.spongepowered.asm.mixin.injection.Inject;
// import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;

// // Intentionally avoid importing Create's DeployerBlockEntity to prevent
// pulling in
// // optional Ponder classes at compile time. We target it by name in @Mixin.

// /**
// * Inject at the start of SmartBlockEntity#tickServer to probabilistically
// * skip
// * heavy logic
// * while letting client animation sync happen naturally via Create's systems.
// */
// @Mixin(value = SmartBlockEntity.class, remap = false)
// public abstract class SmartBlockEntityMixin {
// @Unique
// private int createrandomtickspeed$tickCounter = 0;
// @Unique
// private int createrandomtickspeed$lazyTickCounter = 0;

// @Inject(method = "tick", at = @At("HEAD"), cancellable = true, remap = false,
// require = 0)
// private void createrandomtickspeed$maybeSkipTick(CallbackInfo ci) {
// BlockEntity self = (BlockEntity) (Object) this;
// Level level = self.getLevel();
// if (level != null && level.isClientSide())
// return;

// StatsLogger.ATTEMPTED_TICK.increment();

// createrandomtickspeed$tickCounter++;
// if (createrandomtickspeed$tickCounter % Config.TICK_SKIP_EVERY_N.get() == 0)
// {
// StatsLogger.SKIPPED_TICK.increment();
// ci.cancel();
// }
// }

// /**
// * Cancel SmartBlockEntity#lazyTick every Nth call based on config.
// */
// @Inject(method = "lazyTick", at = @At("HEAD"), cancellable = true, remap =
// false, require = 0)
// private void createrandomtickspeed$maybeSkipLazyTick(CallbackInfo ci) {
// BlockEntity self = (BlockEntity) (Object) this;
// Level level = self.getLevel();
// if (level != null && level.isClientSide())
// return;

// StatsLogger.ATTEMPTED_LAZY_TICK.increment();

// createrandomtickspeed$lazyTickCounter++;
// if (createrandomtickspeed$lazyTickCounter % Config.LAZY_SKIP_EVERY_N.get() ==
// 0) {
// StatsLogger.SKIPPED_LAZY_TICK.increment();
// ci.cancel();
// }
// }
// }
