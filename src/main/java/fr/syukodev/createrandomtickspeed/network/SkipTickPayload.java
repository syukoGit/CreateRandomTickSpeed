package fr.syukodev.createrandomtickspeed.network;

import fr.syukodev.createrandomtickspeed.CreateRandomTickSpeed;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * S2C payload indicating that a server-side tick was skipped for a given
 * BlockEntity position.
 * The client should skip the corresponding next tick to stay in sync with the
 * server.
 */
public record SkipTickPayload(BlockPos pos, boolean isLazyTick) implements CustomPacketPayload {
  public static final Type<SkipTickPayload> TYPE = new Type<>(
      ResourceLocation.fromNamespaceAndPath(CreateRandomTickSpeed.MODID, "skip_tick"));

  public static final StreamCodec<net.minecraft.network.FriendlyByteBuf, SkipTickPayload> STREAM_CODEC = StreamCodec
      .composite(
          BlockPos.STREAM_CODEC, SkipTickPayload::pos,
          ByteBufCodecs.BOOL, SkipTickPayload::isLazyTick,
          SkipTickPayload::new);

  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }

  // Handler is registered in NetworkHandler; keep this class as a pure payload.
}
