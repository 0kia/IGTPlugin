package com.example.plugin;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.time.temporal.ChronoField;

public class IGTUIBuilder extends CustomUIHud {
    private final String timeString;

    public IGTUIBuilder(@Nonnull PlayerRef playerref, String timeString) {
        super(playerref);
        this.timeString = timeString;

    }

    @Override
    public void build(@NonNullDecl UICommandBuilder uiCommandBuilder) {
        PlayerRef playerRef = this.getPlayerRef();
        Ref<EntityStore> storeRef = playerRef.getReference();
        if (storeRef != null) {

            Player player = storeRef.getStore().getComponent(storeRef, Player.getComponentType());
            assert player != null;

            World world = player.getWorld();
            String formattedTime = "0";

            uiCommandBuilder.append("Hud/Overlay.ui");
            uiCommandBuilder.set("#Timer.TextSpans", Message.raw(timeString));
        }
    }

    public void updateTime(String timeString){
        UICommandBuilder builder = new UICommandBuilder();
        builder.set("#Timer.TextSpans", Message.raw(timeString));
        update(false, builder);
    }
}
