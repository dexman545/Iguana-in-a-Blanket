package dex.iguanablanket.helpers;

import dex.iguanablanket.IguanaBlanket;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.PacketByteBuf;

import java.util.HashMap;

public abstract class SyncHelper {
    public static void syncData(PlayerEntity playerEntity, HashMap<String, HashMap> syncData) {
        PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());
        PacketByteBuf stacks = new PacketByteBuf(Unpooled.buffer());

        CompoundTag x = new CompoundTag();
        CompoundTag y = new CompoundTag();

        ((HashMap<String, Float>)(syncData.get("weights"))).forEach(x::putFloat);

        ((HashMap<String, Integer>)(syncData.get("stacksizes"))).forEach(y::putInt);


        data.writeCompoundTag(x);
        stacks.writeCompoundTag(y);

        ServerSidePacketRegistry.INSTANCE.sendToPlayer(playerEntity, IguanaBlanket.IGUANA_CONFIG_PACKET_ID_WEIGHTS, data);
        ServerSidePacketRegistry.INSTANCE.sendToPlayer(playerEntity, IguanaBlanket.IGUANA_CONFIG_PACKET_ID_STACKS, stacks);
    }
}
