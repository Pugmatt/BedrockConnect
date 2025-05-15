package main.com.pyratron.pugmatt.bedrockconnect.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import main.com.pyratron.pugmatt.bedrockconnect.BedrockConnect;
import org.cloudburstmc.nbt.NBTInputStream;
import org.cloudburstmc.nbt.NbtList;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtUtils;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData;
import org.cloudburstmc.protocol.bedrock.data.inventory.CreativeItemData;
import org.cloudburstmc.protocol.bedrock.data.inventory.CreativeItemGroup;
import org.cloudburstmc.protocol.bedrock.data.inventory.CreativeItemCategory;

import java.io.*;
import java.nio.ByteOrder;
import java.util.*;

// https://github.com/DragonetMC/DragonProxy/blob/rewrite/proxy/src/main/java/org/dragonet/proxy/util/PaletteManager.java
// Author: lukeeey

public class PaletteManager {

    private ArrayList<RuntimeEntry> entries;

    private ByteBuf cachedPalette;

    public NbtList<NbtMap> CACHED_PALLETE;

    public static final NbtMap BIOMES;

    static {
        InputStream stream = BedrockConnect.class.getClassLoader().getResourceAsStream("tables/biome_definitions.dat");

        NbtMap biomesTag;

        try (NBTInputStream biomenbtInputStream = NbtUtils.createNetworkReader(stream)){
            biomesTag = (NbtMap) biomenbtInputStream.readTag();
            BIOMES = biomesTag;
        } catch (Exception ex) {
            throw new AssertionError(ex);
        }
    }

    public static final NbtMap ENTITY_IDENTIFIERS;

    static {
        InputStream stream = BedrockConnect.class.getClassLoader().getResourceAsStream("tables/entity_identifiers.dat");

        NbtMap entityTag;

        try (NBTInputStream entitynbtInputStream = NbtUtils.createNetworkReader(stream)){
            entityTag = (NbtMap) entitynbtInputStream.readTag();
            ENTITY_IDENTIFIERS = entityTag;
        } catch (Exception ex) {
            throw new AssertionError(ex);
        }
    }

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    public static final List<ItemData> CREATIVE_ITEMS;

    public static final List<CreativeItemData> CREATIVE_ITEM_DATA;
    public static final List<CreativeItemGroup> CREATIVE_ITEM_GROUPS;

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
        List<CreativeItemData> creativeItemData = new ArrayList<>();
        for (JsonNode itemNode : creativeItemEntries) {
            int damage = 0;
            int count = 1;
            int groupId = 0;
            NbtMap tag = null;

            if (itemNode.has("damage")) {
                damage = itemNode.get("damage").asInt();
            }
            if (itemNode.has("count")) {
                damage = itemNode.get("count").asInt();
            }
            if (itemNode.has("groupId")) {
                groupId = itemNode.get("groupId").asInt();
            }
            if (itemNode.has("nbt_b64")) {
                byte[] bytes = Base64.getDecoder().decode(itemNode.get("nbt_b64").asText());
                ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                try {
                    tag = (NbtMap) NbtUtils.createReaderLE(bais).readTag();
                } catch (IOException e) {
                    BedrockConnect.logger.error("Error loading creative items file", e);
                }
            }

            ItemData item = ItemData.builder()
                    .netId(itemNode.get("id").asInt())
                    .damage(damage)
                    .count(count)
                    .tag(tag).build();
            // Change to something like this?
            // ItemData.builder()
            //                .definition(definition)
            //                .damage(damage)
            //                .count(count)
            //                .tag(tag)
            //                .blockDefinition(blockDefinition);
            creativeItems.add(item);
            creativeItemData.add(new CreativeItemData(item, item.getNetId(), groupId));
        }

        CREATIVE_ITEMS = creativeItems;
        CREATIVE_ITEM_DATA = creativeItemData;

        InputStream groupStream = BedrockConnect.class.getClassLoader().getResourceAsStream("tables/creative_items.json");

        JsonNode creativeItemGroupEntries;
        try {
            creativeItemGroupEntries = JSON_MAPPER.readTree(groupStream).get("groups");
        } catch (Exception e) {
            throw new AssertionError("Unable to load creative item groups", e);
        }

        List<CreativeItemGroup> creativeItemGroups = new ArrayList<>();
        for (JsonNode creativeItemEntry : creativeItemGroupEntries) {
            CreativeItemCategory category = CreativeItemCategory.valueOf(creativeItemEntry.get("category").asText().toUpperCase(Locale.ROOT));
            String name = creativeItemEntry.get("name").asText();

            JsonNode icon = creativeItemEntry.get("icon");
            String identifier = icon.get("id").asText();

            creativeItemGroups.add(new CreativeItemGroup(category, name, ItemData.AIR));
        }

        CREATIVE_ITEM_GROUPS = creativeItemGroups;
    }



    public PaletteManager() {
        InputStream stream = BedrockConnect.class.getClassLoader().getResourceAsStream("tables/runtime_block_states.dat");
        if (stream == null) {
            throw new AssertionError("Unable to locate block state nbt");
        }

        Map<String, Integer> blockIdToIdentifier = new HashMap<>();
        NbtList<NbtMap> tag;

        NBTInputStream nbtInputStream = NbtUtils.createNetworkReader(stream);

        NbtList<NbtMap> blocksTag;
        try {
            tag = (NbtList<NbtMap>) nbtInputStream.readTag();
            nbtInputStream.close();
        } catch (Exception ex) {
            BedrockConnect.logger.error("Failed to receive blocks palette");
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
