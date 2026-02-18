package com.HTSR.plugin;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;

public class IGTUIBuilder extends CustomUIHud {
    private final String timeStringIGT;
    private final String timeStringRTA;

    public IGTUIBuilder(@Nonnull PlayerRef playerref, String timeStringIGT, String timeStringRTA) {
        super(playerref);
        this.timeStringIGT = timeStringIGT;
        this.timeStringRTA = timeStringRTA;

    }

    @Override
    public void build(@NonNullDecl UICommandBuilder uiCommandBuilder) {
        PlayerRef playerRef = this.getPlayerRef();
        Ref<EntityStore> storeRef = playerRef.getReference();
        if (storeRef != null) {

            Player player = storeRef.getStore().getComponent(storeRef, Player.getComponentType());
            assert player != null;

            uiCommandBuilder.append("Hud/Overlay.ui");
            uiCommandBuilder.set("#TimerIGT.TextSpans", Message.raw(timeStringIGT));
            uiCommandBuilder.set("#TimerRTA.TextSpans", Message.raw(timeStringRTA));
        }
    }

    public void updateTimeIGT(String timeString){
        UICommandBuilder builder = new UICommandBuilder();
        builder.set("#TimerIGT.TextSpans", Message.raw(timeString));
        update(false, builder);
    }

    public void updateTimeRTA(String timeString){
        UICommandBuilder builder = new UICommandBuilder();
        builder.set("#TimerRTA.TextSpans", Message.raw(timeString));
        update(false, builder);
    }
}
