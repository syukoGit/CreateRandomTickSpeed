package fr.syukodev.createrandomtickspeed.network;

import fr.syukodev.createrandomtickspeed.CreateRandomTickSpeed;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class NetworkHandler {
  private NetworkHandler() {
  }

  @SubscribeEvent
  public static void register(final RegisterPayloadHandlersEvent event) {
    var registrar = event.registrar(CreateRandomTickSpeed.MODID).versioned("1");

    registrar.playToClient(
        SkipTickPayload.TYPE,
        SkipTickPayload.STREAM_CODEC,
        (payload, ctx) -> ClientSkipTickApplier.apply(payload));
  }

  public static void sendSkipTick(Level level, BlockPos pos, boolean isLazyTick) {
    if (!(level instanceof ServerLevel serverLevel))
      return;
    SkipTickPayload payload = new SkipTickPayload(pos.immutable(), isLazyTick);
    PacketDistributor.sendToPlayersTrackingChunk(serverLevel, new ChunkPos(pos), payload);
  }
}
