package main.com.pyratron.pugmatt.bedrockconnect.utils;

import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;
import org.cloudburstmc.protocol.bedrock.codec.v544.Bedrock_v544;
import org.cloudburstmc.protocol.bedrock.codec.v545.Bedrock_v545;
import org.cloudburstmc.protocol.bedrock.codec.v554.Bedrock_v554;
import org.cloudburstmc.protocol.bedrock.codec.v557.Bedrock_v557;
import org.cloudburstmc.protocol.bedrock.codec.v560.Bedrock_v560;
import org.cloudburstmc.protocol.bedrock.codec.v567.Bedrock_v567;

import java.util.ArrayList;
import java.util.List;

// Referenced from: https://github.com/GeyserMC/Geyser/blob/master/connector/src/main/java/org/geysermc/connector/network/BedrockProtocol.java

public class BedrockProtocol {
    /**
     * Latest available version
     */
    public static final BedrockCodec DEFAULT_BEDROCK_CODEC = Bedrock_v567.CODEC;

    /**
     * A list of all supported Bedrock versions that can join BedrockConnect
     */
    public static final List<BedrockCodec> SUPPORTED_BEDROCK_CODECS = new ArrayList<>();

    static {
        SUPPORTED_BEDROCK_CODECS.add(Bedrock_v544.CODEC);
        SUPPORTED_BEDROCK_CODECS.add(Bedrock_v545.CODEC.toBuilder()
                .minecraftVersion("1.19.21/1.19.22")
                .build());
        SUPPORTED_BEDROCK_CODECS.add(Bedrock_v554.CODEC.toBuilder()
                .minecraftVersion("1.19.30/1.19.31")
                .build());
        SUPPORTED_BEDROCK_CODECS.add(Bedrock_v557.CODEC.toBuilder()
                .minecraftVersion("1.19.40/1.19.41")
                .build());
        SUPPORTED_BEDROCK_CODECS.add(Bedrock_v560.CODEC.toBuilder()
                .minecraftVersion("1.19.50/1.19.51")
                .build());
        SUPPORTED_BEDROCK_CODECS.add(DEFAULT_BEDROCK_CODEC);
    }

    /**
     * Gets the {@link BedrockCodec} of the given protocol version.
     * @param protocolVersion The protocol version to attempt to find
     * @return The packet codec, or null if the client's protocol is unsupported
     */
    public static BedrockCodec getBedrockCodec(int protocolVersion) {
        for (BedrockCodec packetCodec : SUPPORTED_BEDROCK_CODECS) {
            if (packetCodec.getProtocolVersion() == protocolVersion) {
                return packetCodec;
            }
        }
        return null;
    }
}
