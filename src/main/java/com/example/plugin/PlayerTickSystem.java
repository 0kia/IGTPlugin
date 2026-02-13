package com.example.plugin;

import com.hypixel.hytale.builtin.adventure.memories.MemoriesPlugin;
import com.hypixel.hytale.builtin.adventure.memories.component.PlayerMemories;
import com.hypixel.hytale.builtin.adventure.memories.memories.npc.NPCMemory;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.entity.EntityUtils;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;

public class PlayerTickSystem extends EntityTickingSystem<EntityStore> {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    @Nonnull
    private final Query<EntityStore> query;
    public IGTUIBuilder igtUI = null;
    public static TimerComponent timercomponent = null;
    private boolean timerRunning = false;

    public PlayerTickSystem() {
        this.query = Query.and(Player.getComponentType());
    }

    @Override
    public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        final Holder<EntityStore> holder = EntityUtils.toHolder(index, archetypeChunk);
        final PlayerRef playerRef = holder.getComponent(PlayerRef.getComponentType());
        final Player player = holder.getComponent(Player.getComponentType());
        assert player != null;
        assert playerRef != null;

        if (timercomponent == null && !timerRunning){
            assert player.getReference() != null;
            timercomponent = store.getComponent(player.getReference(), TimerComponent.getComponentType());
            return;
        }

        assert timercomponent != null;

        PlayerMemories playerMemories = (PlayerMemories)store.getComponent(player.getReference(), PlayerMemories.getComponentType());
        if (playerMemories != null){
            if (playerMemories.getRecordedMemories().stream().anyMatch(mem -> ((NPCMemory) mem).getNpcRole().contains("Dragon_Frost"))){
                LOGGER.atInfo().log("Timer Finished");
                timercomponent.setFinished(true);
            }
        }

        timerRunning =  timercomponent.getIsTimerRunning();

        if (timerRunning) {
            timercomponent.addTime(dt);
        }

        float elapsedTime = timercomponent.getTime();
        String timeString = formatElapsedTime(elapsedTime);

        hudBuild(playerRef, timeString);
        player.getHudManager().setCustomHud(playerRef, igtUI);

        igtUI.updateTime(timeString);

    }

    @NonNullDecl
    @Override
    public Query<EntityStore> getQuery() {
        return query;
    }

    private void hudBuild(PlayerRef playerRef, String timeString){
        if (igtUI == null){
            LOGGER.atInfo().log("Hud Is Null, Setting...");
            igtUI = new IGTUIBuilder(playerRef, timeString);
        }
    }

    private String formatElapsedTime(float elapsedTime) {
        int totalSeconds = (int) elapsedTime;
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        int milliseconds = (int) ((elapsedTime - totalSeconds) * 1000);

        return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, milliseconds);
    }

}