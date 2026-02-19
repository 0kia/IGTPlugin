package com.HTSR.plugin;

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
    public static AtomicBoolean firstJoin = new AtomicBoolean(true);
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    public static long startTime = 0;
    public static long finishTime = 0;
    public static long currentTime = 0;

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

        if (firstJoin.get()){
            startTime = System.currentTimeMillis();
        }
        currentTime = System.currentTimeMillis();

        World world = event.getPlayer().getWorld();
        assert world != null;
        world.execute(() -> {

            // Grab player
            Ref<EntityStore> ref = event.getPlayer().getReference();
            assert ref != null;
            Store<EntityStore> store = event.getPlayerRef().getStore();
            PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
            assert playerRef != null;

            // build timer component
            var timerType = TimerComponent.getComponentType();
            if (store.getComponent(ref, timerType) == null){
                store.addComponent(ref, timerType);
            }
            TimerComponent timer = store.getComponent(ref, timerType);
            assert timer != null;
            LOGGER.atInfo().log("Timer init");
            timer.setTimerRunning(true);

            // Hud build
            IGTUIBuilder igtUI = new IGTUIBuilder(playerRef, formatIGT(timer.getTime()), formatRTA(
                    (timer.isFinished() ? finishTime : currentTime) - startTime));
            event.getPlayer().getHudManager().setCustomHud(playerRef, igtUI);

            // Pause world (if not temple) and start looking for Interact
            if(!world.getName().contains("Forgotten_Temple")){
                //Don't pause if the world is default or first join
                if (!world.getName().equals("default") || firstJoin.get()) {
                    world.setPaused(true);
                }
            }

            IGTPlugin.checkForInteract.set(true);
            firstJoin.set(false);

        });
    }

    public static void unpauseWorld() {
        for (World world : Universe.get().getWorlds().values()) {
            if (world.isPaused() && world.getPlayerCount() > 0) {
                world.execute(() -> world.setPaused(false));
            }
        }
    }

    public static String formatIGT(double elapsedTime) {
        int totalSeconds = (int) elapsedTime;
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        int milliseconds = (int) ((elapsedTime - totalSeconds) * 1000);

        return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, milliseconds);
    }

    public static String formatRTA(long elapsedMillis) {
        long hours = elapsedMillis / 3_600_000; // 1000 * 60 * 60
        long minutes = (elapsedMillis % 3_600_000) / 60_000;
        long seconds = (elapsedMillis % 60_000) / 1_000;
        long milliseconds = elapsedMillis % 1_000;

        return String.format("%02d:%02d:%02d.%03d",
                hours, minutes, seconds, milliseconds);
    }
}
