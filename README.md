# IGTPlugin

A Plugin made for the Hytale Speedrunning community

# Implementation
This plugin works by using the delta time of the servers entity ticking system. The IGT overlay is updated every tick, and pauses for dimension transition times. The timer will stop when the player obtains the Frost Dragon memory.


On world join, the entire world instance is paused (the same way the regular Esc. pause menu works), allowing chunks to load before playing. when ready, players can use the interact key to unpause. (this does not apply to the Forgotten Temple, or any subsequent overworld loads)
