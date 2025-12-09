package main.com.pyratron.pugmatt.bedrockconnect.server;

import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;
import org.cloudburstmc.protocol.bedrock.codec.v649.Bedrock_v649;
import org.cloudburstmc.protocol.bedrock.codec.v662.Bedrock_v662;
import org.cloudburstmc.protocol.bedrock.codec.v671.Bedrock_v671;
import org.cloudburstmc.protocol.bedrock.codec.v685.Bedrock_v685;
import org.cloudburstmc.protocol.bedrock.codec.v686.Bedrock_v686;
import org.cloudburstmc.protocol.bedrock.codec.v712.Bedrock_v712;
import org.cloudburstmc.protocol.bedrock.codec.v729.Bedrock_v729;
import org.cloudburstmc.protocol.bedrock.codec.v748.Bedrock_v748;
import org.cloudburstmc.protocol.bedrock.codec.v766.Bedrock_v766;
import org.cloudburstmc.protocol.bedrock.codec.v776.Bedrock_v776;
import org.cloudburstmc.protocol.bedrock.codec.v786.Bedrock_v786;
import org.cloudburstmc.protocol.bedrock.codec.v800.Bedrock_v800;
import org.cloudburstmc.protocol.bedrock.codec.v818.Bedrock_v818;
import org.cloudburstmc.protocol.bedrock.codec.v819.Bedrock_v819;
import org.cloudburstmc.protocol.bedrock.codec.v827.Bedrock_v827;
import org.cloudburstmc.protocol.bedrock.codec.v844.Bedrock_v844;
import org.cloudburstmc.protocol.bedrock.codec.v859.Bedrock_v859;
import org.cloudburstmc.protocol.bedrock.codec.v860.Bedrock_v860;
import org.cloudburstmc.protocol.bedrock.codec.v898.Bedrock_v898;

import java.util.ArrayList;
import java.util.List;

// Referenced from: https://github.com/GeyserMC/Geyser/blob/master/connector/src/main/java/org/geysermc/connector/network/BedrockProtocol.java

public class BedrockProtocol {

    /**
     * Latest available version
     */
    public static final BedrockCodec DEFAULT_BEDROCK_CODEC = Bedrock_v898.CODEC.toBuilder().protocolVersion(898).build();


    /**
     * A list of all supported Bedrock versions that can join BedrockConnect
     */
    public static final List<BedrockCodec> SUPPORTED_BEDROCK_CODECS = new ArrayList<>();

    static {
        SUPPORTED_BEDROCK_CODECS.add(Bedrock_v649.CODEC);
        SUPPORTED_BEDROCK_CODECS.add(Bedrock_v662.CODEC);
        SUPPORTED_BEDROCK_CODECS.add(Bedrock_v671.CODEC);
        SUPPORTED_BEDROCK_CODECS.add(Bedrock_v685.CODEC);
        SUPPORTED_BEDROCK_CODECS.add(Bedrock_v686.CODEC);
        SUPPORTED_BEDROCK_CODECS.add(Bedrock_v712.CODEC.toBuilder()
                .minecraftVersion("1.21.20 - 1.21.23")
                .build());
        SUPPORTED_BEDROCK_CODECS.add(Bedrock_v729.CODEC.toBuilder()
                .minecraftVersion("1.21.30/1.21.31")
                .build());
        SUPPORTED_BEDROCK_CODECS.add(Bedrock_v748.CODEC.toBuilder()
                .minecraftVersion("1.21.40 - 1.21.44")
                .build());
        SUPPORTED_BEDROCK_CODECS.add(Bedrock_v766.CODEC.toBuilder() 
                .minecraftVersion("1.21.50 - 1.21.51")
                .build());
        SUPPORTED_BEDROCK_CODECS.add(Bedrock_v776.CODEC.toBuilder()
                .minecraftVersion("1.21.60")
                .build());
        SUPPORTED_BEDROCK_CODECS.add(Bedrock_v786.CODEC);
        SUPPORTED_BEDROCK_CODECS.add(Bedrock_v800.CODEC);
        SUPPORTED_BEDROCK_CODECS.add(Bedrock_v818.CODEC);
        SUPPORTED_BEDROCK_CODECS.add(Bedrock_v819.CODEC);
        SUPPORTED_BEDROCK_CODECS.add(Bedrock_v827.CODEC);
        SUPPORTED_BEDROCK_CODECS.add(Bedrock_v844.CODEC);
        SUPPORTED_BEDROCK_CODECS.add(Bedrock_v859.CODEC);
        SUPPORTED_BEDROCK_CODECS.add(Bedrock_v860.CODEC);
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
