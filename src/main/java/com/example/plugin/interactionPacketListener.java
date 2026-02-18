package com.example.plugin;

import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.packets.interaction.SyncInteractionChain;
import com.hypixel.hytale.protocol.packets.interaction.SyncInteractionChains;
import com.hypixel.hytale.server.core.io.PacketHandler;
import com.hypixel.hytale.server.core.io.adapter.PacketWatcher;

public class interactionPacketListener implements PacketWatcher {
    @Override
    public void accept(PacketHandler packetHandler, Packet packet) {
        if (packet.getId() != 290 || !IGTPlugin.checkForInteract.get()) {
            return;
        }
        SyncInteractionChains interactionChains = (SyncInteractionChains) packet;
        SyncInteractionChain[] updates = interactionChains.updates;

        for (SyncInteractionChain item : updates) {
            InteractionType interactionType = item.interactionType;
            if(interactionType == InteractionType.Use) {
                IGTPlugin.unpauseWorld();
                IGTPlugin.checkForInteract.set(false);
            }
        }
    }
}
