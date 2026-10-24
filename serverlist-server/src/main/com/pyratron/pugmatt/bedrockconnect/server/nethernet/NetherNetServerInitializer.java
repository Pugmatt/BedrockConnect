/*
 * Adapted from GeyserMC/Geyser (core/src/main/java/org/geysermc/geyser/network/bedrock/nethernet/):
 * NetherNetChannelInitialiser, NetherNetPeer, NetherNetFrameCodec and NetherNetPacketEncoder.
 *
 * Copyright (c) 2026 GeyserMC. http://geysermc.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package main.com.pyratron.pugmatt.bedrockconnect.server.nethernet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.MessageToMessageEncoder;
import main.com.pyratron.pugmatt.bedrockconnect.server.PacketHandler;
import org.cloudburstmc.protocol.bedrock.BedrockPeer;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.BedrockSessionFactory;
import org.cloudburstmc.protocol.bedrock.PacketDirection;
import org.cloudburstmc.protocol.bedrock.netty.BedrockBatchWrapper;
import org.cloudburstmc.protocol.bedrock.netty.BedrockPacketWrapper;
import org.cloudburstmc.protocol.bedrock.netty.codec.batch.BedrockBatchEncoder;
import org.cloudburstmc.protocol.bedrock.netty.codec.compression.CompressionCodec;
import org.cloudburstmc.protocol.bedrock.netty.codec.compression.CompressionStrategy;
import org.cloudburstmc.protocol.bedrock.netty.codec.compression.NoopCompression;
import org.cloudburstmc.protocol.bedrock.netty.codec.compression.SimpleCompressionStrategy;
import org.cloudburstmc.protocol.bedrock.netty.codec.packet.BedrockPacketCodec;
import org.cloudburstmc.protocol.bedrock.netty.codec.packet.BedrockPacketCodec_v3;
import org.cloudburstmc.protocol.bedrock.netty.initializer.BedrockServerInitializer;
import org.cloudburstmc.protocol.common.util.VarInts;

import javax.crypto.SecretKey;
import java.util.List;

/**
 * Builds the Bedrock pipeline for a NetherNet (WebRTC) channel and hands each session to the same
 * {@link PacketHandler} RakNet players get. Only the transport-specific framing differs.
 */
public class NetherNetServerInitializer extends BedrockServerInitializer {
    private static final CompressionStrategy NOOP_STRATEGY = new SimpleCompressionStrategy(new NoopCompression());

    @Override
    protected void preInitChannel(Channel channel) {
        // Not calling super: it adds the RakNet frame codec and reads the RakNet protocol version
        channel.attr(PacketDirection.ATTRIBUTE).set(PacketDirection.CLIENT_BOUND);

        // Nothing is compressed until the client sets compression after RequestNetworkSettings
        channel.pipeline()
                .addLast(FrameCodec.NAME, FrameCodec.INSTANCE)
                .addLast(CompressionCodec.NAME, new CompressionCodec(NOOP_STRATEGY, false));
    }

    @Override
    protected void initPacketCodec(Channel channel) {
        // The parent picks this by RakNet protocol version, which a NetherNet channel does not have
        channel.pipeline().addLast(BedrockPacketCodec.NAME, new BedrockPacketCodec_v3());
    }

    @Override
    protected void postInitChannel(Channel channel) {
        // Send one packet per message, as Geyser does, rather than RakNet-style batches
        channel.pipeline().replace(BedrockBatchEncoder.NAME, PacketEncoder.NAME, PacketEncoder.INSTANCE);
    }

    @Override
    protected BedrockPeer createPeer(Channel channel) {
        return new NetherNetPeer(channel, this::createSession);
    }

    @Override
    protected void initSession(BedrockServerSession session) {
        session.setPacketHandler(new PacketHandler(session, false));
    }

    public static class NetherNetPeer extends BedrockPeer {
        /**
         * NetherNet compression behaves like RakNet protocol 11: none until network settings, prefixed afterwards.
         */
        private static final int EQUIVALENT_RAK_VERSION = 11;

        public NetherNetPeer(Channel channel, BedrockSessionFactory sessionFactory) {
            super(channel, sessionFactory);
        }

        @Override
        public void enableEncryption(SecretKey secretKey) {
            // No-op: DTLS already encrypts the data channel
        }

        @Override
        public int getRakVersion() {
            return EQUIVALENT_RAK_VERSION;
        }
    }

    /**
     * A NetherNet message carries a Bedrock batch as-is, without RakNet's 0xFE frame ID.
     */
    @ChannelHandler.Sharable
    static class FrameCodec extends MessageToMessageCodec<ByteBuf, BedrockBatchWrapper> {
        static final String NAME = "nethernet-frame-codec";
        static final FrameCodec INSTANCE = new FrameCodec();

        @Override
        protected void encode(ChannelHandlerContext ctx, BedrockBatchWrapper msg, List<Object> out) {
            if (msg.getCompressed() == null) {
                throw new IllegalStateException("Bedrock batch was not compressed");
            }
            // Slice so the channel, which fragments by absolute index, always sees a reader index of 0
            out.add(msg.getCompressed().retainedSlice());
        }

        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) {
            if (!msg.isReadable()) {
                return;
            }
            out.add(BedrockBatchWrapper.newInstance(msg.readRetainedSlice(msg.readableBytes()), null));
        }
    }

    /**
     * Wraps every packet in a batch of its own, in place of {@link BedrockBatchEncoder}.
     */
    @ChannelHandler.Sharable
    static class PacketEncoder extends MessageToMessageEncoder<BedrockPacketWrapper> {
        static final String NAME = "nethernet-encoder";
        static final PacketEncoder INSTANCE = new PacketEncoder();

        @Override
        protected void encode(ChannelHandlerContext ctx, BedrockPacketWrapper packet, List<Object> out) {
            ByteBuf message = packet.getPacketBuffer();
            if (message == null) {
                throw new IllegalArgumentException("BedrockPacket is not encoded");
            }

            CompositeByteBuf buf = ctx.alloc().compositeDirectBuffer(2);
            BedrockBatchWrapper batch = BedrockBatchWrapper.newInstance();
            try {
                ByteBuf header = ctx.alloc().ioBuffer(5);
                VarInts.writeUnsignedInt(header, message.readableBytes());
                buf.addComponent(true, header);
                buf.addComponent(true, message.retain());
                // MessageToMessageEncoder releases the packet once we return
                batch.addPacket(packet.retain());

                batch.setUncompressed(buf.retain());
                out.add(batch.retain());
            } finally {
                buf.release();
                batch.release();
            }
        }
    }
}
