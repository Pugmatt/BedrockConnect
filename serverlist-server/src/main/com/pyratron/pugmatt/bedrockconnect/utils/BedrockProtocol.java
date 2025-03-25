package main.com.pyratron.pugmatt.bedrockconnect.utils;

import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;
import org.cloudburstmc.protocol.bedrock.codec.v544.Bedrock_v544;
import org.cloudburstmc.protocol.bedrock.codec.v545.Bedrock_v545;
import org.cloudburstmc.protocol.bedrock.codec.v554.Bedrock_v554;
import org.cloudburstmc.protocol.bedrock.codec.v557.Bedrock_v557;
import org.cloudburstmc.protocol.bedrock.codec.v560.Bedrock_v560;
import org.cloudburstmc.protocol.bedrock.codec.v567.Bedrock_v567;
import org.cloudburstmc.protocol.bedrock.codec.v568.Bedrock_v568;
import org.cloudburstmc.protocol.bedrock.codec.v575.Bedrock_v575;
import org.cloudburstmc.protocol.bedrock.codec.v582.Bedrock_v582;
import org.cloudburstmc.protocol.bedrock.codec.v589.Bedrock_v589;
import org.cloudburstmc.protocol.bedrock.codec.v594.Bedrock_v594;
import org.cloudburstmc.protocol.bedrock.codec.v618.Bedrock_v618;
import org.cloudburstmc.protocol.bedrock.codec.v622.Bedrock_v622;
import org.cloudburstmc.protocol.bedrock.codec.v630.Bedrock_v630;
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

import java.util.ArrayList;
import java.util.List;

// Referenced from: https://github.com/GeyserMC/Geyser/blob/master/connector/src/main/java/org/geysermc/connector/network/BedrockProtocol.java

public class BedrockProtocol {

    /**
     * Latest available version
     */
    public static final BedrockCodec DEFAULT_BEDROCK_CODEC = Bedrock_v786.CODEC;


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
        SUPPORTED_BEDROCK_CODECS.add(Bedrock_v567.CODEC.toBuilder()
                .minecraftVersion("1.19.52/1.19.62")
                .build());
        SUPPORTED_BEDROCK_CODECS.add(Bedrock_v568.CODEC);
        SUPPORTED_BEDROCK_CODECS.add(Bedrock_v575.CODEC.toBuilder()
                .minecraftVersion("1.19.70/1.19.71/1.19.73")
                .build());
        SUPPORTED_BEDROCK_CODECS.add(Bedrock_v582.CODEC.toBuilder()
                .build());
        SUPPORTED_BEDROCK_CODECS.add(Bedrock_v589.CODEC.toBuilder()
                .build());
        SUPPORTED_BEDROCK_CODECS.add(Bedrock_v594.CODEC.toBuilder()
                .build());
        SUPPORTED_BEDROCK_CODECS.add(Bedrock_v618.CODEC.toBuilder()
                .build());
        SUPPORTED_BEDROCK_CODECS.add(Bedrock_v622.CODEC.toBuilder()
                .build());
        SUPPORTED_BEDROCK_CODECS.add(Bedrock_v630.CODEC.toBuilder()
                .build());
        SUPPORTED_BEDROCK_CODECS.add(Bedrock_v649.CODEC.toBuilder()
                .build());
        SUPPORTED_BEDROCK_CODECS.add(Bedrock_v662.CODEC.toBuilder()
                .build());
        SUPPORTED_BEDROCK_CODECS.add(Bedrock_v671.CODEC.toBuilder()
                .build());
        SUPPORTED_BEDROCK_CODECS.add(Bedrock_v685.CODEC.toBuilder()
                .build());
        SUPPORTED_BEDROCK_CODECS.add(Bedrock_v686.CODEC.toBuilder()
                .minecraftVersion("1.21.2/1.21.3")
                .build());
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
