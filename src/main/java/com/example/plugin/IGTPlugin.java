package com.example.plugin;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.io.adapter.PacketAdapters;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.concurrent.atomic.AtomicBoolean;


public class IGTPlugin extends JavaPlugin {
    public static AtomicBoolean checkForInteract = new AtomicBoolean();
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public IGTPlugin(@NonNullDecl JavaPluginInit init) {
        super(init);
    }
    @Override
    protected void setup() {
        var registry = getEntityStoreRegistry();
        var timerType = registry.registerComponent(
                TimerComponent.class,
                "IGT_Timer",
                TimerComponent.CODEC
        );
        TimerComponent.setComponentType(timerType);

        PacketAdapters.registerInbound(new interactionPacketListener());

        this.getEventRegistry().registerGlobal(PlayerReadyEvent.class, IGTPlugin::onPlayerReady);
        this.getEntityStoreRegistry().registerSystem(new PlayerTickSystem());
        this.getEntityStoreRegistry().registerSystem(new PlayerRemoved());
    }

    public static void onPlayerReady(PlayerReadyEvent event){
        World world = event.getPlayer().getWorld();
        assert world != null;
        world.execute(() -> {
            Ref<EntityStore> ref = event.getPlayer().getReference();
            assert ref != null;
            Store<EntityStore> store = event.getPlayerRef().getStore();
            PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
            assert playerRef != null;
            IGTUIBuilder igtUI = new IGTUIBuilder(playerRef, "00:00:00:00");
            event.getPlayer().getHudManager().setCustomHud(playerRef, igtUI);
            world.setPaused(true);
            IGTPlugin.checkForInteract.set(true);
            var timerType = TimerComponent.getComponentType();
            if (store.getComponent(ref, timerType) == null){
                store.addComponent(ref, timerType);
            }
            TimerComponent timer = store.getComponent(ref, timerType);
            assert timer != null;
            LOGGER.atInfo().log("Timer init");
            timer.setTimerRunning(true);
        });
    }

    public static void unpauseWorld() {
        for (World world : Universe.get().getWorlds().values()) {
            if (world.isPaused() && world.getPlayerCount() > 0) {
                world.execute(() -> world.setPaused(false));
            }
        }
    }
}
