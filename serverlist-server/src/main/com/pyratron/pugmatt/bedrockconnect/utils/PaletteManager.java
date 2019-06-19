package main.com.pyratron.pugmatt.bedrockconnect.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.nukkitx.network.VarInts;
import com.nukkitx.protocol.bedrock.v354.BedrockUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import main.com.pyratron.pugmatt.bedrockconnect.BedrockConnect;

import java.io.InputStream;
import java.util.ArrayList;

// https://github.com/DragonetMC/DragonProxy/blob/rewrite/proxy/src/main/java/org/dragonet/proxy/util/PaletteManager.java
// Author: lukeeey

public class PaletteManager {

    private ByteBuf cachedPalette;

    public ByteBuf getCachedPalette() {
        return cachedPalette;
    }

    public PaletteManager() {
        InputStream stream = BedrockConnect.class.getClassLoader().getResourceAsStream("tables/runtimeid_table.json");
        if (stream == null) {
            throw new AssertionError("Static Runtime ID table not found");
        }

        ObjectMapper mapper = new ObjectMapper();
        CollectionType type = mapper.getTypeFactory().constructCollectionType(ArrayList.class, RuntimeEntry.class);

        ArrayList<RuntimeEntry> entries = new ArrayList<>();
        try {
            entries = mapper.readValue(stream, type);
        } catch (Exception e) {
            e.printStackTrace();
        }

        cachedPalette = Unpooled.buffer();

        VarInts.writeInt(cachedPalette, entries.size());

        for (RuntimeEntry entry : entries) {
            BedrockUtils.writeString(cachedPalette, entry.name);
            cachedPalette.writeShortLE(entry.data);
        }

    }

    private static class RuntimeEntry {
        @JsonProperty("name")
        private String name;
        @JsonProperty("id")
        private int id;
        @JsonProperty("data")
        private int data;

        public RuntimeEntry() {}

        public RuntimeEntry(String name, int id, int data) {
            this.id = id;
            this.name = name;
            this.data = data;
        }
    }
}
