package main.com.pyratron.pugmatt.bedrockconnect.listeners;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.nukkitx.protocol.bedrock.BedrockClientSession;
import com.nukkitx.protocol.bedrock.packet.*;

import com.nukkitx.protocol.bedrock.handler.BedrockPacketHandler;
import com.nukkitx.protocol.bedrock.packet.LoginPacket;
import main.com.pyratron.pugmatt.bedrockconnect.AuthData;
import main.com.pyratron.pugmatt.bedrockconnect.PipePlayer;
import net.minidev.json.JSONObject;

// Heavily referenced from https://github.com/NukkitX/ProxyPass/blob/master/src/main/java/com/nukkitx/proxypass/network/bedrock/session/UpstreamPacketHandler.java

public class ClientPacketHandler implements BedrockPacketHandler {

    private static final int PIXEL_SIZE = 4;

    public static final int SINGLE_SKIN_SIZE = 64 * 32 * PIXEL_SIZE;
    public static final int DOUBLE_SKIN_SIZE = 64 * 64 * PIXEL_SIZE;
    public static final int SKIN_128_64_SIZE = 128 * 64 * PIXEL_SIZE;
    public static final int SKIN_128_128_SIZE = 128 * 128 * PIXEL_SIZE;

    private BedrockClientSession session;
    private PipePlayer player;

    private JSONObject skinData;
    private JSONObject extraData;
    private ArrayNode chainData;
    private AuthData authData;

    @Override
    public boolean handle(AdventureSettingsPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(AnimatePacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(BlockEntityDataPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(BlockPickRequestPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(BookEditPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(ClientToServerHandshakePacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(CommandBlockUpdatePacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(CommandRequestPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(ContainerClosePacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(CraftingEventPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(EntityEventPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(EntityFallPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(EntityPickRequestPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(EventPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(InteractPacket packet) {
        System.out.println(packet.toString());
        session.sendPacketImmediately(packet);
        return false;
    }

    @Override
    public boolean handle(InventoryContentPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(InventorySlotPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(InventoryTransactionPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(ItemFrameDropItemPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(LabTablePacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(LecternUpdatePacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(LevelSoundEventPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(LevelSoundEvent3Packet packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(MapInfoRequestPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(MobArmorEquipmentPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(MobEquipmentPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(ModalFormResponsePacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(MoveEntityAbsolutePacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(MovePlayerPacket packet) {
        System.out.println(packet.toString());
        session.sendPacketImmediately(packet);
        return false;
    }

    @Override
    public boolean handle(NetworkStackLatencyPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(PhotoTransferPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(PlayerActionPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(PlayerHotbarPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(PlayerInputPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(PlayerSkinPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(PurchaseReceiptPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(RequestChunkRadiusPacket packet) {
        System.out.println(packet.toString());
        ChunkRadiusUpdatedPacket chunk = new ChunkRadiusUpdatedPacket();
        chunk.setRadius(Math.max(3, Math.min(packet.getRadius(), 5)));
        //PacketHeader ph = new PacketHeader();
        // ph.set
        //chunk.setHeader();
        session.sendPacketImmediately(chunk);

        int chunkPositionX = 0;
        int chunkPositionZ = 0;
        int chunkRadius = 5;
        for (int x = -chunkRadius; x < chunkRadius; x++) {
            for (int z = -chunkRadius; z < chunkRadius; z++) {
                FullChunkDataPacket fullChunk = new FullChunkDataPacket();
                fullChunk.setChunkX(chunkPositionX + x);
                fullChunk.setChunkZ(chunkPositionZ + z);
                fullChunk.setData(new byte[0]);
                session.sendPacket(fullChunk);
            }
        }

        PlayStatusPacket play = new PlayStatusPacket();
        play.setStatus(PlayStatusPacket.Status.PLAYER_SPAWN);
        session.sendPacketImmediately(play);

        SetTimePacket st = new SetTimePacket();
        st.setTime(0);
        session.sendPacketImmediately(st);
        return false;
    }

    @Override
    public boolean handle(RiderJumpPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(ServerSettingsRequestPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(SetDefaultGameTypePacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(SetLocalPlayerAsInitializedPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(SetPlayerGameTypePacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(SubClientLoginPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(TextPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(AddBehaviorTreePacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(AddEntityPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(AddHangingEntityPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(AddItemEntityPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(AddPaintingPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(AddPlayerPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(AvailableCommandsPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(BlockEventPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(BossEventPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(CameraPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(ChangeDimensionPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(ChunkRadiusUpdatedPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(ClientboundMapItemDataPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(CommandOutputPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(ContainerOpenPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(ContainerSetDataPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(CraftingDataPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(ExplodePacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(FullChunkDataPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(GameRulesChangedPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(GuiDataPickItemPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(HurtArmorPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(AutomationClientConnectPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(LevelEventPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(MapCreateLockedCopyPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(MobEffectPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(ModalFormRequestPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(MoveEntityDeltaPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(NpcRequestPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(OnScreenTextureAnimationPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(PlayerListPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(PlaySoundPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(PlayStatusPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(RemoveEntityPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(RemoveObjectivePacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(ResourcePackChunkDataPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(ResourcePackDataInfoPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(ResourcePacksInfoPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(ResourcePackStackPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(RespawnPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(ScriptCustomEventPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(ServerSettingsResponsePacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(ServerToClientHandshakePacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(SetCommandsEnabledPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(SetDifficultyPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(SetDisplayObjectivePacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(SetEntityDataPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(SetEntityLinkPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(SetEntityMotionPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(SetHealthPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(SetLastHurtByPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(SetScoreboardIdentityPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(SetScorePacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(SetSpawnPositionPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(SetTimePacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(SetTitlePacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(ShowCreditsPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(ShowProfilePacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(ShowStoreOfferPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(SimpleEventPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(SpawnExperienceOrbPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(StartGamePacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(StopSoundPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(StructureBlockUpdatePacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(TakeItemEntityPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(TransferPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(UpdateAttributesPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(UpdateBlockPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(UpdateBlockSyncedPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(UpdateEquipPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(UpdateSoftEnumPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(UpdateTradePacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(AvailableEntityIdentifiersPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(BiomeDefinitionListPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(LevelSoundEvent2Packet packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(NetworkChunkPublisherUpdatePacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(SpawnParticleEffectPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    @Override
    public boolean handle(VideoStreamConnectPacket packet) {
        System.out.println(packet.toString());
        return false;
    }

    public ClientPacketHandler(BedrockClientSession session) {
        this.session = session;

        session.addDisconnectHandler((Reason) -> disconnect());
    }

    public void disconnect() {
        //player.getClient().getSession().disconnect("Left Game");
        //server.removePlayer(player);
    }

    @Override
    public boolean handle(DisconnectPacket packet) {
        System.out.println(packet.toString());
        return true;
    }

    @Override
    public boolean handle(ResourcePackChunkRequestPacket packet) {
        System.out.println(packet.toString());
        return true;
    }

    @Override
    public boolean handle(ResourcePackClientResponsePacket packet) {
        System.out.println(packet.toString());
        return true;
    }

    @Override
    public boolean handle(LoginPacket packet) {
        System.out.println(packet.toString());
        return true;
    }

}