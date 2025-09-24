package fr.syukodev.createrandomtickspeed.network;

public final class ClientSkipTickApplier {
  private ClientSkipTickApplier() {
  }

  public static void apply(SkipTickPayload payload) {
    ClientSkipTracker.mark(payload.pos(), payload.isLazyTick());
  }
}
