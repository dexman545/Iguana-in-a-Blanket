package dex.iguanablanket;

import net.fabricmc.fabric.api.event.Event;
import net.minecraft.server.MinecraftServer;
import net.fabricmc.fabric.api.event.EventFactory;

public interface ServerReloadCallback {

    Event<ServerReloadCallback> EVENT = EventFactory.createArrayBacked(ServerReloadCallback.class, listeners -> server -> {
        for (ServerReloadCallback event : listeners) {
            event.onServerReload(server);
        }
    });

    void onServerReload(MinecraftServer server);
}
