package com.example.plugin;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;


public class TimerComponent implements Component<EntityStore> {

    private static ComponentType<EntityStore, TimerComponent> TYPE;

    public static void setComponentType(ComponentType<EntityStore, TimerComponent> type) {
        TYPE = type;
    }

    public static ComponentType<EntityStore, TimerComponent> getComponentType() {
        return TYPE;
    }

    public static final BuilderCodec<TimerComponent> CODEC = BuilderCodec
            .builder(TimerComponent.class, TimerComponent::new)
            .append(
                    new KeyedCodec<>("Timer", Codec.FLOAT),
                    (component, value) -> component.time = value,
                    component -> component.time
            ).add()
            .build();

    float time;
    private boolean isTimerRunning;
    private boolean finished = false;
    public TimerComponent() {

    }

    public float getTime() {
        return time;
    }

    public boolean getIsTimerRunning() {
        return isTimerRunning;
    }

    public void addTime(float elapsedTime) {
        time += elapsedTime;
    }

    public void setTimerRunning(boolean status) {
        if (!finished)
            isTimerRunning = status;
    }

    public void setFinished(boolean finished){
        this.finished = finished;
        isTimerRunning = false;
    }

    public boolean isFinished(){
        return this.finished;
    }

    @NullableDecl
    @Override
    public TimerComponent clone() {
        return new TimerComponent();
    }
}
