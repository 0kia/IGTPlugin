package com.HTSR.plugin;

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
    private Player player;

    public PlayerTickSystem() {
        this.query = Query.and(Player.getComponentType());
    }

    @Override
    public void tick(float dt,
                     int index,
                     @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
                     @Nonnull Store<EntityStore> store,
                     @Nonnull CommandBuffer<EntityStore> commandBuffer) {

        final Holder<EntityStore> holder = EntityUtils.toHolder(index, archetypeChunk);
        this.player = holder.getComponent(Player.getComponentType());
        final PlayerRef playerRef = holder.getComponent(PlayerRef.getComponentType());
        assert player != null && playerRef != null && player.getReference() != null;

        // get timer component
        if (timercomponent == null) {
            timercomponent = store.getComponent(player.getReference(), TimerComponent.getComponentType());
            if (timercomponent == null) return; // Timer not ready yet
        }

        // check for dragon memory
        checkDragonCompletion(store, player);

        // update timer if running
        if (timercomponent.getIsTimerRunning()) {
            timercomponent.addTime(dt);
        }

        // update hud
        updateTimerHUD(playerRef, timercomponent.getTime());
    }

    @NonNullDecl
    @Override
    public Query<EntityStore> getQuery() {
        return query;
    }

    private void checkDragonCompletion(Store<EntityStore> store, Player player) {
        if (timercomponent.isFinished())
            return;

        assert player.getReference() != null;
        PlayerMemories playerMemories = store.getComponent(
                player.getReference(),
                PlayerMemories.getComponentType()
        );

        if (playerMemories != null && hasDragonFrostMemory(playerMemories)) {
            IGTPlugin.finishTime = System.currentTimeMillis();
            timercomponent.setFinished(true);
            LOGGER.atInfo().log("Timer Finished");
        }
    }

    private boolean hasDragonFrostMemory(PlayerMemories playerMemories) {
        return playerMemories.getRecordedMemories().stream()
                .anyMatch(mem -> ((NPCMemory) mem).getNpcRole().contains("Dragon_Frost"));
    }

    private void updateTimerHUD(PlayerRef playerRef, double elapsedTime) {
        String timeStringIGT = IGTPlugin.formatIGT(elapsedTime);
        String timeStringRTA = IGTPlugin.formatRTA((timercomponent.isFinished() ? IGTPlugin.finishTime : System.currentTimeMillis()) - IGTPlugin.startTime);
        hudBuild(playerRef, timeStringIGT, timeStringRTA);
        player.getHudManager().setCustomHud(playerRef, igtUI);
        igtUI.updateTimeIGT(timeStringIGT);
        igtUI.updateTimeRTA(timeStringRTA);

    }

    private void hudBuild(PlayerRef playerRef, String timeStringIGT, String timeStringRTA){
        if (igtUI == null){
            LOGGER.atInfo().log("Hud Is Null, Setting...");
            igtUI = new IGTUIBuilder(playerRef, timeStringIGT, timeStringRTA);
        }
    }

}