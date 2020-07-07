package main.com.pyratron.pugmatt.bedrockconnect.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nukkitx.nbt.NbtUtils;
import com.nukkitx.nbt.stream.NBTInputStream;
import com.nukkitx.nbt.tag.CompoundTag;
import com.nukkitx.nbt.tag.ListTag;
import com.nukkitx.protocol.bedrock.data.inventory.ItemData;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import main.com.pyratron.pugmatt.bedrockconnect.BedrockConnect;

import java.io.*;
import java.nio.ByteOrder;
import java.util.*;

// https://github.com/DragonetMC/DragonProxy/blob/rewrite/proxy/src/main/java/org/dragonet/proxy/util/PaletteManager.java
// Author: lukeeey

public class PaletteManager {

    private ArrayList<RuntimeEntry> entries;

    private ByteBuf cachedPalette;

    public ListTag<CompoundTag> CACHED_PALLETE;

    public static final CompoundTag BIOMES;

    static {
        InputStream stream = BedrockConnect.class.getClassLoader().getResourceAsStream("tables/biome_definitions.dat");

        CompoundTag biomesTag;

        try (NBTInputStream biomenbtInputStream = NbtUtils.createNetworkReader(stream)){
            biomesTag = (CompoundTag) biomenbtInputStream.readTag();
            BIOMES = biomesTag;
        } catch (Exception ex) {
            throw new AssertionError(ex);
        }
    }

    public static final CompoundTag ENTITY_IDENTIFIERS;

    static {
        InputStream stream = BedrockConnect.class.getClassLoader().getResourceAsStream("tables/entity_identifiers.dat");

        CompoundTag entityTag;

        try (NBTInputStream entitynbtInputStream = NbtUtils.createNetworkReader(stream)){
            entityTag = (CompoundTag) entitynbtInputStream.readTag();
            ENTITY_IDENTIFIERS = entityTag;
        } catch (Exception ex) {
            throw new AssertionError(ex);
        }
    }

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    public static final ItemData[] CREATIVE_ITEMS;

    static {
        /* Load creative items */
        InputStream stream = BedrockConnect.class.getClassLoader().getResourceAsStream("tables/creative_items.json");

        JsonNode creativeItemEntries;
        try {
            creativeItemEntries = JSON_MAPPER.readTree(stream).get("items");
        } catch (Exception e) {
            throw new AssertionError("Unable to load creative items", e);
        }

        List<ItemData> creativeItems = new ArrayList<>();
        for (JsonNode itemNode : creativeItemEntries) {
            short damage = 0;
            if (itemNode.has("damage")) {
                damage = itemNode.get("damage").numberValue().shortValue();
            }
            if (itemNode.has("nbt_b64")) {
                byte[] bytes = Base64.getDecoder().decode(itemNode.get("nbt_b64").asText());
                ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                try {
                    com.nukkitx.nbt.tag.CompoundTag tag = (com.nukkitx.nbt.tag.CompoundTag) NbtUtils.createReaderLE(bais).readTag();
                    creativeItems.add(ItemData.of(itemNode.get("id").asInt(), damage, 1, tag));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                creativeItems.add(ItemData.of(itemNode.get("id").asInt(), damage, 1));
            }
        }
        CREATIVE_ITEMS = creativeItems.toArray(new ItemData[0]);
    }



    public PaletteManager() {
        InputStream stream = BedrockConnect.class.getClassLoader().getResourceAsStream("tables/runtime_block_states.dat");
        if (stream == null) {
            throw new AssertionError("Unable to locate block state nbt");
        }

        Map<String, Integer> blockIdToIdentifier = new HashMap<>();
        ListTag<CompoundTag> tag;

        NBTInputStream nbtInputStream = NbtUtils.createNetworkReader(stream);

        ListTag<CompoundTag> blocksTag;
        try {
            tag = (ListTag<CompoundTag>) nbtInputStream.readTag();
            nbtInputStream.close();
        } catch (Exception ex) {
            System.out.println("Failed to receive blocks palette");
            throw new AssertionError(ex);
        }

        CACHED_PALLETE = tag;
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
