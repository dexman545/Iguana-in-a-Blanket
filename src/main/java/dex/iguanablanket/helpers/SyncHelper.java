package dex.iguanablanket.helpers;

import dex.iguanablanket.IguanaBlanket;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
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

        if (ServerSidePacketRegistry.INSTANCE.canPlayerReceive(playerEntity, IguanaBlanket.IGUANA_CONFIG_PACKET_ID_STACKS)
                && ServerSidePacketRegistry.INSTANCE.canPlayerReceive(playerEntity, IguanaBlanket.IGUANA_CONFIG_PACKET_ID_WEIGHTS)) {
            ServerSidePacketRegistry.INSTANCE.sendToPlayer(playerEntity, IguanaBlanket.IGUANA_CONFIG_PACKET_ID_STACKS, stacks);
            ServerSidePacketRegistry.INSTANCE.sendToPlayer(playerEntity, IguanaBlanket.IGUANA_CONFIG_PACKET_ID_WEIGHTS, data);
        } else {
            System.out.println(playerEntity.getDisplayName().asString() + " does not accept data from iguana");
            ((ServerPlayerEntity)playerEntity).networkHandler.disconnect(new LiteralText("This server uses Iguana in a Blanket - " +
                    "\n\nDue to desync reasons you are not allowed to connect until you install it").formatted(Formatting.UNDERLINE).formatted(Formatting.RED));
        }

    }
}
