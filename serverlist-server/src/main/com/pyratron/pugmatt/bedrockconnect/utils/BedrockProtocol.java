package main.com.pyratron.pugmatt.bedrockconnect.utils;

import com.nukkitx.protocol.bedrock.BedrockPacketCodec;
import com.nukkitx.protocol.bedrock.v419.Bedrock_v419;
import com.nukkitx.protocol.bedrock.v422.Bedrock_v422;
import com.nukkitx.protocol.bedrock.v428.Bedrock_v428;
import com.nukkitx.protocol.bedrock.v431.Bedrock_v431;
import com.nukkitx.protocol.bedrock.v440.Bedrock_v440;

import java.util.ArrayList;
import java.util.List;

// Referenced from: https://github.com/GeyserMC/Geyser/blob/master/connector/src/main/java/org/geysermc/connector/network/BedrockProtocol.java

public class BedrockProtocol {
    /**
     * Latest available version
     */
    public static final BedrockPacketCodec DEFAULT_BEDROCK_CODEC = Bedrock_v431.V431_CODEC;

    /**
     * A list of all supported Bedrock versions that can join BedrockConnect
     */
    public static final List<BedrockPacketCodec> SUPPORTED_BEDROCK_CODECS = new ArrayList<>();

    static {
        SUPPORTED_BEDROCK_CODECS.add(Bedrock_v419.V419_CODEC.toBuilder()
                .minecraftVersion("1.16.100/1.16.101")
                .build());
        SUPPORTED_BEDROCK_CODECS.add(Bedrock_v422.V422_CODEC.toBuilder()
                .minecraftVersion("1.16.200/1.16.201")
                .build());
        SUPPORTED_BEDROCK_CODECS.add(Bedrock_v428.V428_CODEC);
        SUPPORTED_BEDROCK_CODECS.add(DEFAULT_BEDROCK_CODEC);
        SUPPORTED_BEDROCK_CODECS.add(Bedrock_v440.V440_CODEC);
    }

    /**
     * Gets the {@link BedrockPacketCodec} of the given protocol version.
     * @param protocolVersion The protocol version to attempt to find
     * @return The packet codec, or null if the client's protocol is unsupported
     */
    public static BedrockPacketCodec getBedrockCodec(int protocolVersion) {
        for (BedrockPacketCodec packetCodec : SUPPORTED_BEDROCK_CODECS) {
            if (packetCodec.getProtocolVersion() == protocolVersion) {
                return packetCodec;
            }
        }
        return null;
    }
}
